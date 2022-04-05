package com.copanote.emvmpm.data;

import java.util.List;

public class EmvMpm {
	private EmvMpmDataObject data;
	private EmvMpm parent;
	private List<EmvMpm> children;
	
	public EmvMpm(EmvMpmDataObject data, EmvMpm parent, List<EmvMpm> children) {
		this.data = data;
		this.parent = parent;
		this.children = children;
	}
	public EmvMpmDataObject getData() {
		return data;
	}
	public void setData(EmvMpmDataObject data) {
		this.data = data;
	}
	public EmvMpm getParent() {
		return parent;
	}
	public void setParent(EmvMpm parent) {
		this.parent = parent;
	}
	public List<EmvMpm> getChildren() {
		return children;
	}
	public void setChildren(List<EmvMpm> children) {
		this.children = children;
	}
	
	
	public boolean isRoot() {
		if (parent == null) {
			return true;
		}
		return false;
	}
	
	public boolean hasChild() {
		if (children == null || children.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public boolean isTemplate() {
		return hasChild();
	}
	
	public boolean isPrimitive() {
		return !hasChild();
	}
	
	
	public String getCanonicalId() {
		EmvMpm p = this;
		String id = p.getData().getId();
		
		while(true) {
			if (p.isRoot()) {
				return id;
			}
			p = p.getParent();
			id = p.getData().getId() + "." + id;
		}
	}
	
	
	private EmvMpm findRoot() {
		EmvMpm rootCandidate = this;
		while(true) {
			if (rootCandidate.isRoot()) {
				break;
			} 
			rootCandidate = rootCandidate.getParent();
		}
		
		return rootCandidate;
	}
	
}
