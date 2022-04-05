package com.copanote.emvmpm.definition;

import java.nio.file.Path;
import java.util.List;

public class DataObjectDef {
	
	public enum Type {
		PRIMITIVE,
		TEMPLATE
	}
	
	private String id;
	private String description;
	private Type   type;
	private DataObjectDef parent;
	private List<DataObjectDef> children;
	
	
	//for template
	public DataObjectDef(String id, String description, Type type, List<DataObjectDef> children) {
		this.id = id;
		this.description = description;
		this.type = type;
		this.children = children;
		
		//Set Parent
		for (DataObjectDef dod : children) {
			dod.parent = this;
		}
	}
	
	//for primitive
	public DataObjectDef(String id, String description, Type type) {
		this.id = id;
		this.description = description;
		this.type = type;
	}
	
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

	public String getCanonicalId() {
		if (isRootDataObject()) {
			return "/" + getId();
		}
		
		return this.parent.getCanonicalId() + "/" + getId();
	}
	
	private boolean isRootDataObject() {
		if (parent == null) {
			return true;
		}
		
		return false;
	}

}
