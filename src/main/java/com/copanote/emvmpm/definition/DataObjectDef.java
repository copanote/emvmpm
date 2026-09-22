package com.copanote.emvmpm.definition;

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
    private String id;
    private String description;
    private int maxlength;
    private Type type;
    private DataObjectDef parent;
    private List<DataObjectDef> children;

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

        // Set Parent
        for (DataObjectDef dod : children) {
            dod.setParent(this);
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
    }

    /*
     *  Getters and Setters
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
     * 태그 ID를 설정한다.
     *
     * @param id 두 자리 태그 ID
     */
    public void setId(String id) {
        this.id = id;
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
     * 필드 설명을 설정한다.
     *
     * @param description 필드 설명
     */
    public void setDescription(String description) {
        this.description = description;
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
     * 값의 최대 길이를 설정한다.
     *
     * @param maxlength 최대 길이
     */
    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
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
     * 필드 타입을 설정한다.
     *
     * @param type {@link Type#PRIMITIVE} 또는 {@link Type#TEMPLATE}
     */
    public void setType(Type type) {
        this.type = type;
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
     * 부모 필드 정의를 설정한다.
     *
     * @param parent 부모 필드 정의
     */
    public void setParent(DataObjectDef parent) {
        this.parent = parent;
    }

    /**
     * 자식 필드 정의 목록을 반환한다.
     *
     * @return 자식 필드 정의 목록, primitive인 경우 null일 수 있음
     */
    public List<DataObjectDef> getChildren() {
        return children;
    }

    /**
     * 자식 필드 정의 목록을 설정한다.
     *
     * @param children 자식 필드 정의 목록
     */
    public void setChildren(List<DataObjectDef> children) {
        this.children = children;
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
