package com.copanote.emvmpm.data;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link EmvMpmDataObject}를 트리(parent/children) 형태로 감싼 런타임 노드.
 *
 * <p>자식이 있으면 template, 없으면 primitive, 부모 없이 {@link EmvMpmDataObject#ROOT} sentinel을
 * 데이터로 가지면 root로 간주한다. template 노드는 {@link #add(EmvMpmNode)}가 호출될 때마다 자신의
 * length/value를 자식들과 재귀적으로 동기화한다.
 *
 * <p>파싱 대신 트리를 프로그래밍 방식으로 구성하는 예시:
 *
 * <pre>{@code
 * EmvMpmNode root = EmvMpmNodeFactory.root();
 * root.add(EmvMpmNodeFactory.createPrimitive("00", "01"));
 * root.markCrc();
 * String qrData = root.toQrCodeData();
 * }</pre>
 */
public class EmvMpmNode implements Comparable<EmvMpmNode> {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private EmvMpmDataObject data;
    private EmvMpmNode parent;
    private List<EmvMpmNode> children;

    /*
     * Constructors and FactoryMethods
     */
    /**
     * 데이터, 부모, 자식 목록을 직접 지정해서 노드를 생성한다. 일반적으로는 {@link EmvMpmNodeFactory}를
     * 통해 생성하는 것을 권장한다.
     *
     * @param data 이 노드가 감싸는 ILV 데이터
     * @param parent 부모 노드 (없으면 null)
     * @param children 자식 노드 목록 (primitive면 null)
     */
    public EmvMpmNode(EmvMpmDataObject data, EmvMpmNode parent, List<EmvMpmNode> children) {
        this.data = data;
        this.parent = parent;
        this.children = children;
    }

    /*
     *  Getters and Setters
     */
    /**
     * 이 노드가 감싸는 ILV 데이터를 반환한다.
     *
     * @return ILV 데이터 객체
     */
    public EmvMpmDataObject getData() {
        return data;
    }

    /**
     * 이 노드가 감싸는 ILV 데이터를 설정한다.
     *
     * @param data ILV 데이터 객체
     */
    public void setData(EmvMpmDataObject data) {
        this.data = data;
    }

    /**
     * 부모 노드를 반환한다.
     *
     * @return 부모 노드, root인 경우 null
     */
    public EmvMpmNode getParent() {
        return parent;
    }

    /**
     * 부모 노드를 설정한다.
     *
     * @param parent 부모 노드
     */
    public void setParent(EmvMpmNode parent) {
        this.parent = parent;
    }

    /**
     * 자식 노드 목록을 반환한다.
     *
     * @return 자식 노드 목록, primitive인 경우 null일 수 있음
     */
    public List<EmvMpmNode> getChildren() {
        return children;
    }

    /**
     * 자식 노드 목록을 설정한다.
     *
     * @param children 자식 노드 목록
     */
    public void setChildren(List<EmvMpmNode> children) {
        this.children = children;
    }

    /*
     *  Defined Methods
     */
    /**
     * 이 노드가 root sentinel 노드인지 판별한다.
     *
     * @return 부모가 없고 데이터 ID가 {@link EmvMpmDataObject#ROOT}의 ID와 같으면 true
     */
    public boolean isRoot() {
        return parent == null && data.getId().equals(EmvMpmDataObject.ROOT.getId());
    }

    /**
     * 이 노드가 template(자식을 가진 노드)인지 판별한다.
     *
     * @return root가 아니면서 자식이 있으면 true
     */
    public boolean isTemplate() {
        return hasChild() && !isRoot();
    }

    /**
     * 이 노드가 primitive(자식이 없는 노드)인지 판별한다.
     *
     * @return root가 아니면서 자식이 없으면 true
     */
    public boolean isPrimitive() {
        return !hasChild() && !isRoot();
    }

    private boolean hasChild() {
        return children != null && !children.isEmpty();
    }

    /**
     * 자식 노드를 추가한다. 이 노드가 template이 되면 자식들의 ILV를 이어 붙여 자신의 length/value를
     * 재계산한다.
     *
     * @param node 추가할 자식 노드
     * @throws IllegalArgumentException 자식들의 ILV 길이 합이 99를 초과해서 두 자리 length로 표현할 수
     *     없는 경우
     */
    public void add(EmvMpmNode node) {

        if (children == null) {
            children = new ArrayList<>();
        }
        node.setParent(this);
        children.add(node);

        if (isTemplate()) {
            // recalculate parent's length and value
            int len =
                    this.children.stream().map(i -> i.getData().getILVLength()).reduce(0, Integer::sum);
            if (len > 99) {
                throw new IllegalArgumentException("length shall have a value 0 to 99");
            }
            String twoDigitLength = String.format("%02d", len);
            String value =
                    this.children.stream().map(i -> i.getData().toEmvMpmData()).reduce("", String::concat);
            getData().setLength(twoDigitLength);
            getData().setValue(value);
        }
    }

    /**
     * 직계 자식 중 id가 일치하는 노드를 찾는다.
     *
     * @param id 찾고자 하는 자식 노드의 태그 ID
     * @return 일치하는 자식 노드, 없으면 {@link Optional#empty()}
     */
    public Optional<EmvMpmNode> findChild(String id) {
        return getChildren().stream()
                .filter(s -> s.getData().getId().equalsIgnoreCase(id))
                .findAny();
    }

    /**
     * canonical path를 따라 이 노드 기준으로 하위 노드를 순회하며 찾는다.
     *
     * @param canonicalId "/"로 구분된 canonical path (예: "/62/50/00")
     * @return 일치하는 노드, 없으면 {@link Optional#empty()}
     */
    public Optional<EmvMpmNode> find(String canonicalId) {

        List<String> idList = EmvMpmPaths.parsePath(canonicalId);
        List<String> list = new ArrayList<>(idList);

        String first = list.remove(0);
        if (!this.getData().getId().equals(first)) {
            return Optional.empty();
        }

        EmvMpmNode emn = this;
        Optional<EmvMpmNode> t = Optional.empty();

        for (String id : list) {
            t = emn.findChild(id);
            if (!t.isPresent()) {
                return t;
            } else {
                emn = t.get();
            }
        }
        return t;
    }

    /**
     * root로부터 이 노드까지의 canonical path를 계산한다.
     *
     * @return "/"로 구분된 canonical path (예: "/26/00")
     */
    public String getCanonicalId() {
        return __canonicalId(this, EmvMpmPaths.getDelimiter());
    }

    private String __canonicalId(EmvMpmNode node, String delimeter) {

        if (node.isRoot()) {
            if (node.getData().getId().equalsIgnoreCase(delimeter)) {
                return "";
            } else {
                return node.getData().getId();
            }
        } else {
            return __canonicalId(node.getParent(), delimeter)
                    + delimeter
                    + node.getData().getId();
        }
    }

    /**
     * 이 노드 이하의 트리를 EMV MPM QR 코드 원시 문자열로 직렬화한다.
     *
     * @return EMV MPM 형식의 원시 문자열
     */
    public String toQrCodeData() {
        if (isPrimitive()) {
            return getData().toEmvMpmData();
        } else {
            StringBuilder result = new StringBuilder();

            if (isTemplate()) {
                result = new StringBuilder(getData().getId() + getData().getLength());
            } else if (isRoot()) {
                result = new StringBuilder();
            }

            for (EmvMpmNode emvMpmNode : getChildren()) {
                result.append(emvMpmNode.toQrCodeData());
            }
            return result.toString();
        }
    }

    /**
     * {@link #toQrCodeData()} 결과를 UTF-8로 인코딩한 뒤 대문자 16진수 문자열로 변환한다.
     *
     * @return 대문자 16진수 문자열
     */
    public String toHexQrCodeData() {
        return toHex(toQrCodeData().getBytes(StandardCharsets.UTF_8));
    }

    private static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 스펙 §4.7.3에 따라 이 트리의 CRC를 계산하고, 태그 "63" CRC 노드를 마지막 자식으로 추가한다.
     */
    public void markCrc() {
        EmvMpmNode emptyCrc = EmvMpmNodeFactory.emptyCrc();
        String data = this.toQrCodeData() + emptyCrc.toQrCodeData();
        emptyCrc.getData().setValue(EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8));
        this.add(emptyCrc);
    }

    /**
     * 자식 노드를 태그 ID의 자연 순서({@link #compareTo(EmvMpmNode)}, 즉 {@link EmvMpmDataObject}의 id
     * 사전순)로 정렬한다. 자식이 없으면 아무 일도 하지 않는다.
     */
    public void sortById() {
        if (children != null) {
            Collections.sort(children);
        }
    }

    @Override
    public int compareTo(EmvMpmNode o) {
        return this.getData().compareTo(o.getData());
    }

    @Override
    public String toString() {
        return "EmvMpmNode [data=" + data + ", children=" + children + "]";
    }
}
