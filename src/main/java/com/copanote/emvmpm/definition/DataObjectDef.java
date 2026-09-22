package com.copanote.emvmpm.definition;

import java.util.Collections;
import java.util.List;

/**
 * 하나의 EMV MPM 필드에 대한 스키마 항목.
 *
 * <p>id, description, maxlength, {@link Type}(PRIMITIVE/TEMPLATE), 그리고(template인 경우)
 * 자식 {@link DataObjectDef}들을 갖는다. {@link com.copanote.emvmpm.data.EmvMpmNode}/
 * {@code EmvMpmDataObject}와 형태는 같지만 실제 데이터가 아닌 스키마를 나타낸다.
 */
public class DataObjectDef {

    /** 필드가 primitive인지 template인지 나타낸다. */
    public enum Type {
        /** 자식이 없는 단일 값 필드. */
        PRIMITIVE,
        /** 자식 {@link DataObjectDef}를 갖는 중첩 필드. */
        TEMPLATE
    }

    /*
     * Fields
     */
    private final String id;
    private final String description;
    private final int maxlength;
    private final Type type;
    private DataObjectDef parent;
    private final List<DataObjectDef> children;

    /*
     * Constructors
     */
    // for Template
    /**
     * maxlength를 기본값(99)으로 하는 template 필드 정의를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param description 필드 설명
     * @param type 필드 타입 ({@link Type#TEMPLATE}이어야 한다)
     * @param children 이 template의 자식 필드 정의 목록
     */
    public DataObjectDef(String id, String description, Type type, List<DataObjectDef> children) {
        this(id, description, 99, type, children);
    }

    /**
     * template 필드 정의를 생성하고, 자식들의 parent를 이 인스턴스로 설정한다.
     *
     * @param id 두 자리 태그 ID
     * @param description 필드 설명
     * @param maxLength 값의 최대 길이
     * @param type 필드 타입 ({@link Type#TEMPLATE}이어야 한다)
     * @param children 이 template의 자식 필드 정의 목록
     */
    public DataObjectDef(String id, String description, int maxLength, Type type, List<DataObjectDef> children) {
        this.id = id;
        this.description = description;
        this.maxlength = maxLength;
        this.type = type;
        this.children = children;

        // Set Parent (같은 클래스 내부 필드 직접 대입 - 생성자에서만 발생하는 1회성 연결이며, 공개 setter는 없다)
        for (DataObjectDef dod : children) {
            dod.parent = this;
        }
    }

    // for Primitive
    /**
     * maxlength를 기본값(99)으로 하는 primitive 필드 정의를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param description 필드 설명
     * @param type 필드 타입 ({@link Type#PRIMITIVE}이어야 한다)
     */
    public DataObjectDef(String id, String description, Type type) {
        this(id, description, 99, type);
    }

    /**
     * primitive 필드 정의를 생성한다.
     *
     * @param id 두 자리 태그 ID
     * @param description 필드 설명
     * @param maxLength 값의 최대 길이
     * @param type 필드 타입 ({@link Type#PRIMITIVE}이어야 한다)
     */
    public DataObjectDef(String id, String description, int maxLength, Type type) {
        this.id = id;
        this.description = description;
        this.maxlength = maxLength;
        this.type = type;
        this.children = null;
    }

    /*
     *  Getters
     */
    /**
     * 태그 ID를 반환한다.
     *
     * @return 두 자리 태그 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 필드 설명을 반환한다.
     *
     * @return 필드 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 값의 최대 길이를 반환한다.
     *
     * @return 최대 길이
     */
    public int getMaxlength() {
        return maxlength;
    }

    /**
     * 필드 타입을 반환한다.
     *
     * @return {@link Type#PRIMITIVE} 또는 {@link Type#TEMPLATE}
     */
    public Type getType() {
        return type;
    }

    /**
     * 부모 필드 정의를 반환한다.
     *
     * @return 부모 필드 정의, 최상위 필드인 경우 null
     */
    public DataObjectDef getParent() {
        return parent;
    }

    /**
     * 자식 필드 정의 목록을 반환한다. {@link #DataObjectDef(String, String, int, Type, List)}로 생성된
     * 뒤에는 바꿀 수 없도록, 수정 불가능한 view로 감싸서 반환한다.
     *
     * @return 자식 필드 정의 목록의 읽기 전용 view, primitive인 경우 빈 리스트
     */
    public List<DataObjectDef> getChildren() {
        return children == null ? Collections.emptyList() : Collections.unmodifiableList(children);
    }

    /*
     *  Defined Method
     */
    /**
     * 최상위 필드로부터 이 필드까지의 canonical path를 계산한다.
     *
     * @return "/"로 구분된 canonical path (예: "/26/00")
     */
    public String getCanonicalId() {
        if (isRootDataObject()) {
            return "/" + getId();
        }
        return getParent().getCanonicalId() + "/" + getId();
    }

    /**
     * 이 필드 정의가 최상위(부모가 없는) 필드인지 판별한다.
     *
     * @return 부모가 없으면 true
     */
    public boolean isRootDataObject() {
        return parent == null;
    }

    /**
     * 이 필드가 template인지 판별한다.
     *
     * @return {@link Type#TEMPLATE}이면 true
     */
    public boolean isTemplate() {
        return Type.TEMPLATE == getType();
    }

    @Override
    public String toString() {
        return "DataObjectDef [id=" + getCanonicalId() + ", description=" + description + ", maxlength=" + maxlength
                + ", type=" + type + ", parent=" + ", children=" + children + "]";
    }
}
