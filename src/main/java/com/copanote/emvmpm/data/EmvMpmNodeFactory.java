package com.copanote.emvmpm.data;

import java.util.List;

/**
 * {@link EmvMpmNode} 트리를 생성하는 팩토리.
 *
 * <p>새 노드/트리를 만들 때는 생성자를 직접 호출하는 대신 이 클래스가 제공하는 정적 메서드를 사용하는 것을
 * 권장한다.
 */
public class EmvMpmNodeFactory {

    /** 인스턴스화를 막는 private 생성자. 이 클래스의 모든 멤버는 static이다. */
    private EmvMpmNodeFactory() {}

    /**
     * root sentinel 노드를 생성한다.
     *
     * @return {@link EmvMpmDataObject#ROOT}를 데이터로 갖는 노드
     */
    public static EmvMpmNode root() {
        return EmvMpmNodeFactory.of(EmvMpmDataObject.ROOT);
    }

    /**
     * value가 비어 있는 태그 "63" CRC 노드를 생성한다. {@link EmvMpmNode#markCrc()}가 실제 CRC 값을
     * 계산해서 채운다.
     *
     * @return 빈 CRC 노드
     */
    public static EmvMpmNode emptyCrc() {
        return new EmvMpmNode(EmvMpmDataObject.of("63", "04", ""), null, null);
    }

    /**
     * Point of Initiation Method(태그 "01")의 동적(dynamic) 값을 갖는 노드를 생성한다.
     *
     * @return 동적 PIM 노드
     */
    public static EmvMpmNode dynamicPim() {
        return EmvMpmNodeFactory.of(EmvMpmDataObject.POINT_INITATION_METHOD_DYNAMIC);
    }

    /**
     * Point of Initiation Method(태그 "01")의 정적(static) 값을 갖는 노드를 생성한다.
     *
     * @return 정적 PIM 노드
     */
    public static EmvMpmNode staticPim() {
        return EmvMpmNodeFactory.of(EmvMpmDataObject.POINT_INITATION_METHOD_STATIC);
    }

    /**
     * 부모/자식이 없는 단독 노드를 생성한다. {@code data}는 노드에 담기 전에 복사되므로, 이후 이 노드의
     * data를 변경해도 호출자가 들고 있는 원본이나 {@link EmvMpmDataObject}의 공유 static 상수(예:
     * {@link EmvMpmDataObject#ROOT})에는 영향을 주지 않는다.
     *
     * @param data 노드가 감쌀 ILV 데이터 (복사되어 사용됨)
     * @return 생성된 노드
     */
    public static EmvMpmNode of(EmvMpmDataObject data) {
        return new EmvMpmNode(copyOf(data), null, null);
    }

    /**
     * 자식 목록이 지정된 노드를 생성한다. {@code data}는 노드에 담기 전에 복사된다.
     *
     * @param data 노드가 감쌀 ILV 데이터 (복사되어 사용됨)
     * @param children 자식 노드 목록
     * @return 생성된 노드
     */
    public static EmvMpmNode of(EmvMpmDataObject data, List<EmvMpmNode> children) {
        return new EmvMpmNode(copyOf(data), null, children);
    }

    /**
     * data를 독립적인 새 인스턴스로 복사한다. {@link EmvMpmDataObject#ROOT}와 같은 공유 static 상수가
     * 노드에 그대로 들어가서 변경(mutation)에 노출되는 것을 막기 위한 방어적 복사다.
     *
     * @param data 복사할 데이터
     * @return 필드 값이 같은 새 인스턴스
     */
    private static EmvMpmDataObject copyOf(EmvMpmDataObject data) {
        return EmvMpmDataObject.of(data.getId(), data.getLength(), data.getValue());
    }

    /**
     * 자식이 없는 primitive 노드를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param value 원시 값 문자열
     * @return 자식이 없는 노드
     */
    public static EmvMpmNode createPrimitive(String id, String value) {
        return of(EmvMpmDataObject.of(id, value));
    }

    /**
     * 주어진 자식들로부터 length/value를 계산해서 template 노드를 생성한다. 생성된 template을 각 자식의
     * parent로 설정하므로, 결과 노드에서 바로 {@link EmvMpmNode#getCanonicalId()}를 사용할 수 있다.
     *
     * @param id 두 자리 태그 ID
     * @param children 이 template의 자식이 될 노드 목록
     * @return 자식을 갖는 template 노드
     */
    public static EmvMpmNode createTemplate(String id, List<EmvMpmNode> children) {

        int len = children.stream().map(i -> i.getData().getILVLength()).reduce(0, Integer::sum);
        String value = children.stream().map(i -> i.getData().toEmvMpmData()).reduce("", String::concat);

        EmvMpmDataObject dataObject = EmvMpmDataObject.of(id, len, value);

        EmvMpmNode template = EmvMpmNodeFactory.of(dataObject, children);
        children.forEach(child -> child.setParent(template));
        return template;
    }
}
