package com.copanote.emvmpm.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EmvMpmNode {
	private EmvMpmDataObject data;
	private EmvMpmNode parent;      
	private List<EmvMpmNode> children;
	
	private static final EmvMpmNode ROOT_NODE = new EmvMpmNode(EmvMpmDataObject.ROOT, null, null);
	public static EmvMpmNode root() {
		return ROOT_NODE;
	}
	
	public static EmvMpmNode of(EmvMpmDataObject data) {
		return new EmvMpmNode(data, null, null);
	}
	
	public static EmvMpmNode of(EmvMpmDataObject data, EmvMpmNode p) {
		return new EmvMpmNode(data, p, null);
	}
	
	public EmvMpmNode(EmvMpmDataObject data, EmvMpmNode parent, List<EmvMpmNode> children) {
		this.data = data;
		this.parent = parent;
		this.children = children;
	}
	
	/*
	 *  Getters and Setters
	 */
	public EmvMpmDataObject getData() {
		return data;
	}
	public void setData(EmvMpmDataObject data) {
		this.data = data;
	}
	public EmvMpmNode getParent() {
		return parent;
	}
	public void setParent(EmvMpmNode parent) {
		this.parent = parent;
	}
	public List<EmvMpmNode> getChildren() {
		return children;
	}
	public void setChildren(List<EmvMpmNode> children) {
		this.children = children;
	}
	
	/*
	 *  Defined Methods
	 */
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
	
	public Optional<EmvMpmNode> findChild(String id) {
		return getChildren().stream().filter(s -> s.getData().getId().equalsIgnoreCase(id)).findAny();
	}
	
	public Optional<EmvMpmNode> find(String canonicalId) {
		
		List<String> idList = parseId(canonicalId);
		List<String> list = new ArrayList<>();
		list.addAll(idList);

		
		String first = list.remove(0);
		if (! this.getData().getId().equals(first)) {
			return Optional.of(null);
		}
		

		EmvMpmNode emn = this;
		Optional<EmvMpmNode> t = Optional.of(null);
		
		for (String id : list) {
			t = emn.findChild(id);
			if (false == t.isPresent()) {
				return t;
			} else {
				emn = t.get();
			}
		}
		
		return t;
	}
	
	// /ab/cd/ef/gh
	private List<String> parseId(String canonicalId) {
		String[] sa = canonicalId.split("/");
		List<String> sl = Arrays.asList(sa);
		if (canonicalId.startsWith("/")) {
			if (sl.size() == 0 ) {
				return Arrays.asList("/");
			} else {
				sl.set(0, "/");
			}
		}
		return sl;
	}
	
	public String getCanonicalId() {
		EmvMpmNode p = this;
		String id = p.getData().getId();
		
		while(true) {
			if (p.isRoot()) {
				return "/" + id;
			}
			p = p.getParent();
			id = p.getData().getId() + "." + id;
		}
	}
	
	private EmvMpmNode findRoot() {
		EmvMpmNode rootCandidate = this;
		while(true) {
			if (rootCandidate.isRoot()) {
				break;
			} 
			rootCandidate = rootCandidate.getParent();
		}
		return rootCandidate;
	}

	@Override
	public String toString() {
		return "EmvMpmNode [data=" + data +  ", children=" + children + "]";
	}
	
	
	
	
}
