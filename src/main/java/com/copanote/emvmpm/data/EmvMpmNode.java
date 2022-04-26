package com.copanote.emvmpm.data;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmvMpmNode implements Comparable<EmvMpmNode> {
	private EmvMpmDataObject data;
	private EmvMpmNode parent;      
	private List<EmvMpmNode> children;
	
	
	/*
	 * Constructors and FactoryMethods
	 */
	
	public EmvMpmNode(EmvMpmDataObject data, EmvMpmNode parent, List<EmvMpmNode> children) {
		this.data = data;
		this.parent = parent;
		this.children = children;
	}
	
	//replace children List
	public void add(List<EmvMpmNode> items) {
		items.stream().forEach(n -> n.setParent(this));
		setChildren(items);
	}
	
	public void add(EmvMpmNode node) {
		
		if (children == null) {
			children = new ArrayList<>();
		}
		node.setParent(this);
		children.add(node);
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
		if (parent == null && data.getId().equals(EmvMpmDataObject.ROOT.getId()) ) {
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
	
	public String toQrCodeData() {
		if (isRoot()) {
			String r = "";
			for (EmvMpmNode emvMpmNode : getChildren()) {
				r += emvMpmNode.toQrCodeData();
			}
			return r;
		} else {
			if (isPrimitive()) {
				return getData().toEmvMpmData();
			} else {
				String rr = getData().getId() + getData().getLength();
				for (EmvMpmNode emvMpmNode : getChildren()) {
					rr += emvMpmNode.toQrCodeData();
				}
				return rr;
			}
		}
	}
	
	public void markCrc() {
		EmvMpmNode emptyCrc = EmvMpmNodeFactory.emptyCrc();
		String data = this.toQrCodeData() + emptyCrc.toQrCodeData();
		emptyCrc.getData().setValue(EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8));
		this.add(emptyCrc);
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
