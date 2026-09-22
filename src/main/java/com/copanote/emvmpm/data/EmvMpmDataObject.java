package com.copanote.emvmpm.data;

import com.copanote.emvmpm.definition.EmvMpmDefinition;

/**
 * EMV MPM 스펙의 원시 ID-Length-Value(ILV) 단위를 나타낸다.
 *
 * <p>스펙에 따라 {@code id}와 {@code length}는 두 자리 숫자 문자열이다. 이 클래스는 트리 구조를 갖지 않는 순수한 데이터
 * 트리플만 표현하며, 부모/자식 관계는 {@link EmvMpmNode}가 담당한다.
 *
 * <p>세 필드 모두 불변(immutable)이다. 값을 바꾸려면 {@link #of(String, String, String)} 등의 팩토리
 * 메서드로 새 인스턴스를 만들어야 한다.
 */
public class EmvMpmDataObject implements Comparable<EmvMpmDataObject>, Cloneable {

    // preDefiend DataObject
    /** ROOT sentinel 데이터 객체. {@link EmvMpmNode#isRoot()} 판별에 사용된다. */
    public static final EmvMpmDataObject ROOT = new EmvMpmDataObject("/", "", "");

    /** 태그 "00" Payload Format Indicator의 잘 알려진 값("01"). */
    public static final EmvMpmDataObject PAYLOAD_FORMAT_INDICATOR = new EmvMpmDataObject("00", "02", "01");

    /** 태그 "01" Point of Initiation Method의 정적(static) QR 값("11"). */
    public static final EmvMpmDataObject POINT_INITATION_METHOD_STATIC = new EmvMpmDataObject("01", "02", "11");

    /** 태그 "01" Point of Initiation Method의 동적(dynamic) QR 값("12"). */
    public static final EmvMpmDataObject POINT_INITATION_METHOD_DYNAMIC = new EmvMpmDataObject("01", "02", "12");

    // An  ID shall be coded as a two-digit numeric value and shall have a value "00" to "99".
    private final String id;
    // Length shall be coded as a two-digit numeric value and shall have a value "01" to "99".
    private final String length;
    private final String value;

    /**
     * value의 실제 길이를 계산해서 length를 채운 {@link EmvMpmDataObject}를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param value 원시 값 문자열
     * @return 생성된 데이터 객체
     */
    public static EmvMpmDataObject of(String id, String value) {
        return EmvMpmDataObject.of(id, value.length(), value);
    }

    /**
     * 정수 길이를 두 자리 문자열로 변환해서 {@link EmvMpmDataObject}를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param length value의 길이(0~99)
     * @param value 원시 값 문자열
     * @return 생성된 데이터 객체
     * @throws IllegalArgumentException length가 0 미만이거나 99를 초과하는 경우
     */
    public static EmvMpmDataObject of(String id, int length, String value) {

        if (length < 0 || length > 99) {
            throw new IllegalArgumentException("length shall have a value 0 to 99");
        }

        String twoDigitLength = String.format("%02d", length);

        return EmvMpmDataObject.of(id, twoDigitLength, value);
    }

    /**
     * id, length, value를 그대로 사용해서 {@link EmvMpmDataObject}를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param length 두 자리 길이 문자열
     * @param value 원시 값 문자열
     * @return 생성된 데이터 객체
     */
    public static EmvMpmDataObject of(String id, String length, String value) {
        // specification validation
        return new EmvMpmDataObject(id, length, value);
    }

    /**
     * id, length, value를 직접 지정해서 데이터 객체를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param length 두 자리 길이 문자열
     * @param value 원시 값 문자열
     */
    public EmvMpmDataObject(String id, String length, String value) {
        this.id = id;
        this.length = length;
        this.value = value;
    }

    /**
     * 태그 ID를 반환한다.
     *
     * @return 두 자리 태그 ID
     */
    public String getId() {
        return id;
    }

    /**
     * value의 길이를 나타내는 두 자리 문자열을 반환한다.
     *
     * @return 두 자리 길이 문자열
     */
    public String getLength() {
        return length;
    }

    /**
     * 원시 값을 반환한다.
     *
     * @return 원시 값 문자열
     */
    public String getValue() {
        return value;
    }

    /**
     * id, length, value 세 필드를 합친 전체 ILV 문자열의 길이를 계산한다.
     *
     * @return id + length + value의 문자열 길이 합
     */
    public int getILVLength() {
        return id.length() + length.length() + value.length();
    }

    /**
     * id, length, value를 이어 붙인 EMV MPM 원시 문자열을 반환한다.
     *
     * @return "{id}{length}{value}" 형태의 문자열
     */
    public String toEmvMpmData() {
        return getId() + getLength() + getValue();
    }

    @Override
    public String toString() {
        return "EmvMpmDataObject [id=" + id + ", length=" + length + ", value=" + value + "]";
    }

    /**
     * definition을 참고해서 이 데이터 객체를 사람이 읽기 좋은 형태로 설명하려는 메서드였으나, 지원하지
     * 않는다.
     *
     * <p>{@link EmvMpmDataObject}는 부모/경로 정보를 갖지 않아서 자신의 두 자리 {@code id}만으로는
     * definition에서 올바른 항목을 조회할 canonical path(예: "/62/50/00")를 만들 수 없다. bare id로
     * 조회하면 같은 id를 쓰는 다른 위치의 필드(예: 여러 template에 등장하는 "00")와 혼동되어 잘못된 설명을
     * 반환할 위험이 있어, 차라리 항상 빈 문자열을 반환한다.
     *
     * @param def 사용되지 않음
     * @return 항상 빈 문자열
     * @deprecated canonical path 정보가 있는 {@link EmvMpmNode#getCanonicalId()}와
     *     {@link EmvMpmDefinition#find(String)}을 노드 단위로 직접 사용하라.
     */
    @Deprecated
    public String toDetailedString(EmvMpmDefinition def) {
        return "";
    }

    @Override
    public int compareTo(EmvMpmDataObject o) {
        return this.getId().compareTo(o.getId());
    }

    @Override
    protected EmvMpmDataObject clone() throws CloneNotSupportedException {
        return EmvMpmDataObject.of(getId(), getLength(), getValue());
    }
}
