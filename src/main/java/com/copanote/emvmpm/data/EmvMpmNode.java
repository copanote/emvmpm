package com.copanote.emvmpm.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmvMpmNode implements Comparable<EmvMpmNode> {
	private EmvMpmDataObject data;
	private EmvMpmNode parent;      
	private List<EmvMpmNode> children;
	
	private static final EmvMpmNode ROOT_NODE = new EmvMpmNode(EmvMpmDataObject.ROOT, null, null);
	public static EmvMpmNode root() {
		return ROOT_NODE;
	}
	
	
	/*
	 * Constructors and FactoryMethods
	 */
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
	
	private boolean hasChild() {
		if (children == null || children.isEmpty()) {
			return false;
		}
		return true;
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
		
		List<String> idList = EmvMpmPaths.parsePath(canonicalId);
		List<String> list = new ArrayList<>();
		list.addAll(idList);

		
		String first = list.remove(0);
		if (! this.getData().getId().equals(first)) {
			return Optional.empty();
		}
		

		EmvMpmNode emn = this;
		Optional<EmvMpmNode> t = Optional.empty();
		
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
			return __canonicalId(node.getParent(), delimeter) + delimeter + node.getData().getId();
		}
	}
	
//	private EmvMpmNode findRoot() {
//		EmvMpmNode rootCandidate = this;
//		while(true) {
//			if (rootCandidate.isRoot()) {
//				break;
//			} 
//			rootCandidate = rootCandidate.getParent();
//		}
//		return rootCandidate;
//	}
	
	
	public String toQrCodeData() {
		
		if (isRoot()) {
//			Collections.sort(getChildren());
			String r = "";
			for (EmvMpmNode emvMpmNode : getChildren()) {
				r += emvMpmNode.toQrCodeData();
			}
			return r;
		} else {
			if (isPrimitive()) {
				return getData().toQrCodeData();
			} else {
				String rr = getData().getId() + getData().getLength();
				for (EmvMpmNode emvMpmNode : getChildren()) {
					rr += emvMpmNode.toQrCodeData();
				}
				return rr;
			}
		}
	}
	
	public void sortById() {
		
	}
	

	@Override
	public String toString() {
		return "EmvMpmNode [data=" + data +  ", children=" + children + "]";
	}

	@Override
	public int compareTo(EmvMpmNode o) {
		return this.getData().compareTo(o.getData());
	}
	
	
	
	
}
