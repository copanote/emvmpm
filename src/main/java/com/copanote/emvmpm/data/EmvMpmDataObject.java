package com.copanote.emvmpm.data;

import com.copanote.emvmpm.definition.EmvMpmDefinition;

/**
 * EMV MPM 스펙의 원시 ID-Length-Value(ILV) 단위를 나타낸다.
 *
 * <p>스펙에 따라 {@code id}와 {@code length}는 두 자리 숫자 문자열이다. 이 클래스는 트리 구조를 갖지 않는 순수한 데이터
 * 트리플만 표현하며, 부모/자식 관계는 {@link EmvMpmNode}가 담당한다.
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
    private String id;
    // Length shall be coded as a two-digit numeric value and shall have a value "01" to "99".
    private String length;
    private String value;

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
     * 태그 ID를 설정한다.
     *
     * @param id 두 자리 태그 ID
     */
    public void setId(String id) {
        this.id = id;
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
     * value의 길이를 나타내는 두 자리 문자열을 설정한다.
     *
     * @param length 두 자리 길이 문자열
     */
    public void setLength(String length) {
        this.length = length;
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
     * 원시 값을 설정한다.
     *
     * @param value 원시 값 문자열
     */
    public void setValue(String value) {
        this.value = value;
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
     * definition을 참고해서 이 데이터 객체를 사람이 읽기 좋은 형태로 설명한다.
     *
     * @param def 태그의 의미를 조회할 definition
     * @return 상세 설명 문자열 (현재 미구현)
     */
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
