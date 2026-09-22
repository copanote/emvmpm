package com.copanote.emvmpm.parser;

import com.copanote.emvmpm.data.EmvMpmDataObject;
import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.data.EmvMpmNodeFactory;
import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 원시 EMV MPM 데이터 문자열을 {@link EmvMpmNode} 트리로 파싱한다.
 *
 * <p>{@link EmvMpmDefinition}이 주어지면 각 레벨에서 definition을 참조해서 해당 태그의 값을 template으로
 * 재귀 파싱할지 primitive의 원시 값으로 유지할지 판단한다. definition이 없으면 모든 것을 flat/primitive로
 * 취급한다(template으로의 재귀 없음).
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * EmvMpmPackager packager = new EmvMpmPackager();
 * packager.setEmvMpmPackager("emvmpm_bc.xml");
 * EmvMpmDefinition definition = packager.create();
 *
 * EmvMpmNode root = EmvMpmParser.parse(rawQrData, definition);
 * String merchantName = root.find("/59").map(EmvMpmNode::getData).map(EmvMpmDataObject::getValue).orElse(null);
 * }</pre>
 */
public class EmvMpmParser {

    private static final int LEN_ID = 2;
    private static final int LEN_LENGTH = 2;

    /** 인스턴스화를 막는 private 생성자. 이 클래스의 모든 멤버는 static이다. */
    private EmvMpmParser() {}

    /**
     * definition을 참조해서 원시 데이터를 파싱한다. template으로 정의된 태그는 재귀적으로 하위 트리를
     * 구성한다.
     *
     * @param data 원시 EMV MPM 데이터 문자열
     * @param def 태그가 template인지 primitive인지 판단할 definition
     * @return 파싱된 root 노드
     */
    public static EmvMpmNode parse(String data, EmvMpmDefinition def) {
        return __parse(EmvMpmNodeFactory.root(), data, def);
    }

    /**
     * definition을 참조해서 파싱한 뒤, 파싱된 트리의 모든 태그가 definition에 정의돼 있는지 검증한다.
     * {@link #parse(String, EmvMpmDefinition)}와 달리, definition에 없는 태그가 하나라도 있으면 결과를
     * 버리지 않고 예외를 던진다.
     *
     * @param data 원시 EMV MPM 데이터 문자열
     * @param def 파싱 및 검증에 사용할 definition
     * @return 검증을 통과한 파싱 결과 root 노드
     * @throws IllegalArgumentException definition에 정의되지 않은 태그가 발견된 경우
     */
    public static EmvMpmNode parseAndDefinitionValidation(String data, EmvMpmDefinition def) {
        EmvMpmNode parsedNode = __parse(EmvMpmNodeFactory.root(), data, def);
        validateAgainstDefinition(parsedNode, def);
        return parsedNode;
    }

    private static void validateAgainstDefinition(EmvMpmNode node, EmvMpmDefinition def) {
        if (!node.isRoot() && !def.find(node.getCanonicalId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Tag \"" + node.getCanonicalId() + "\" is not defined in the given definition");
        }
        for (EmvMpmNode child : node.getChildren()) {
            validateAgainstDefinition(child, def);
        }
    }

    // parse EmvMpm without Definition
    /**
     * definition 없이 원시 데이터를 파싱한다. 모든 태그를 primitive로 취급하며 template으로 재귀
     * 파싱하지 않는다.
     *
     * @param data 원시 EMV MPM 데이터 문자열
     * @return 파싱된 root 노드 (자식은 모두 primitive)
     */
    public static EmvMpmNode parse(String data) {
        return __parseWithoutDef(EmvMpmNodeFactory.root(), data);
    }

    private static EmvMpmNode __parse(EmvMpmNode node, String childData, EmvMpmDefinition def) {
        List<EmvMpmDataObject> children = parseChild(childData);
        List<EmvMpmNode> childrenNode =
                children.stream().map(e -> EmvMpmNodeFactory.of(e, node)).collect(Collectors.toList());
        node.setChildren(childrenNode);

        for (EmvMpmNode emvMpmNode : childrenNode) {
            if (isTemplate(emvMpmNode, def)) {
                __parse(emvMpmNode, emvMpmNode.getData().getValue(), def);
            }
        }
        return node;
    }

    private static EmvMpmNode __parseWithoutDef(EmvMpmNode node, String childData) {
        List<EmvMpmDataObject> children = parseChild(childData);
        List<EmvMpmNode> childrenNode =
                children.stream().map(e -> EmvMpmNodeFactory.of(e, node)).collect(Collectors.toList());
        node.setChildren(childrenNode);
        return node;
    }

    private static boolean isTemplate(EmvMpmNode node, EmvMpmDefinition def) {
        Optional<DataObjectDef> d = def.find(node.getCanonicalId());
        if (d.isPresent()) {
            DataObjectDef ddef = d.get();
            return ddef.isTemplate();
        }
        return false;
    }

    private static List<EmvMpmDataObject> parseChild(String data) {
        List<EmvMpmDataObject> children = new ArrayList<>();

        int cursor = 0;
        while (cursor < data.length()) {
            EmvMpmDataObject emdo = parseOneNode(data.substring(cursor));
            children.add(emdo);
            cursor += emdo.getILVLength();
        }

        return children;
    }

    private static EmvMpmDataObject parseOneNode(String data) {
        int cursor = 0;

        if (data.length() < LEN_ID + LEN_LENGTH) {
            throw new IllegalArgumentException("Malformed EMV MPM data: expected at least " + (LEN_ID + LEN_LENGTH)
                    + " characters for a tag's id/length but only " + data.length() + " remain: \"" + data + "\"");
        }

        String id = data.substring(cursor, cursor + LEN_ID);
        cursor += LEN_ID;

        String sLentgh = data.substring(cursor, cursor + LEN_LENGTH);
        cursor += LEN_LENGTH;
        int iLength;
        try {
            iLength = Integer.parseInt(sLentgh);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Malformed EMV MPM data: tag \"" + id + "\" has a non-numeric length \"" + sLentgh + "\"", e);
        }

        if (cursor + iLength > data.length()) {
            throw new IllegalArgumentException("Malformed EMV MPM data: tag \"" + id + "\" declares length " + iLength
                    + " but only " + (data.length() - cursor) + " characters remain");
        }

        String value = data.substring(cursor, cursor + iLength);
        cursor += iLength;

        return new EmvMpmDataObject(id, sLentgh, value);
    }
}
