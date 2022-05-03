package com.copanote.emvmpm.definition;

import java.util.List;

public class DataObjectDef {
	
	public enum Type {
		PRIMITIVE,
		TEMPLATE
	}
	
	/*
	 * Fields
	 */
	private String id;
	private String description;
	private int maxlength;
	private Type   type;
	private DataObjectDef parent;
	private List<DataObjectDef> children;
	
	
	/*
	 * Constructors 
	 */
	//for Template
	public DataObjectDef(String id, String description, Type type, List<DataObjectDef> children) {
		this(id, description, 99, type, children);
	}
	
	public DataObjectDef(String id, String description, int maxLength, Type type, List<DataObjectDef> children) {
		this.id = id;
		this.description = description;
		this.maxlength = maxLength;
		this.type = type;
		this.children = children;
		
		//Set Parent
		for (DataObjectDef dod : children) {
			dod.setParent(this);
		}
	}

	//for Primitive
	public DataObjectDef(String id, String description, Type type) {
		this(id, description, 99, type);
	}
	
	public DataObjectDef(String id, String description, int maxLength ,Type type) {
		this.id = id;
		this.description = description;
		this.maxlength = maxLength;
		this.type = type;
	}
	
	/*
	 *  Getters and Setters
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public DataObjectDef getParent() {                                                                                                                   
		return parent;
	}
	public void setParent(DataObjectDef parent) {
		this.parent = parent;
	}
	public List<DataObjectDef> getChildren() {
		return children;
	}
	public void setChildren(List<DataObjectDef> children) {
		this.children = children;
	}

	
	/*
	 *  Defined Method
	 */
	public String getCanonicalId() {
		if (isRootDataObject()) {
			return "/" + getId();
		}
		return getParent().getCanonicalId() + "/" + getId();
	}
	
	public boolean isRootDataObject() {
		if (parent == null) {
			return true;
		}
		
		return false;
	}
	
	public boolean isTemplate() {
		if (Type.TEMPLATE == getType()) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "DataObjectDef [id=" + getCanonicalId() + ", description=" + description +  ", maxlength=" + maxlength + ", type=" + type + ", parent="
				+ ", children=" + children + "]" ;
	}
	
	

}
