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

    // TODO:: implement this method
    /**
     * definition으로 파싱한 결과를 definition 자체와 대조 검증한다 (현재 미구현).
     *
     * @param data 원시 EMV MPM 데이터 문자열
     * @param def 검증에 사용할 definition
     * @return 현재는 항상 null
     */
    public static EmvMpmNode parseAndDefinitionValidation(String data, EmvMpmDefinition def) {
        EmvMpmNode parsedNode = __parse(EmvMpmNodeFactory.root(), data, def);
        return null;
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

        EmvMpmDataObject emdo = null;
        int parsedLength = getTotalLength(children);

        while (data.length() > parsedLength) {
            emdo = parseOneNode(data.substring(parsedLength));
            children.add(emdo);
            parsedLength = getTotalLength(children);
        }

        return children;
    }

    private static int getTotalLength(List<EmvMpmDataObject> list) {
        return list.stream().mapToInt(i -> i.getILVLength()).sum();
    }

    private static EmvMpmDataObject parseOneNode(String data) {
        int cursor = 0;

        String id = data.substring(cursor, cursor + LEN_ID);
        cursor += LEN_ID;

        String sLentgh = data.substring(cursor, cursor + LEN_LENGTH);
        cursor += LEN_LENGTH;
        int iLength = Integer.parseInt(sLentgh);

        if (cursor + iLength <= data.length()) {}

        String value = data.substring(cursor, cursor + iLength);
        cursor += iLength;

        return new EmvMpmDataObject(id, sLentgh, value);
    }
}
