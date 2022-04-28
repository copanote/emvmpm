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
	
	public boolean isTemplate() {
		return hasChild() && !isRoot() ;
	}
	
	public boolean isPrimitive() {
		return !hasChild() && !isRoot();
	}
	
	private boolean hasChild() {
		if (children == null || children.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public void add(EmvMpmNode node) {
		
		if (children == null) {
			children = new ArrayList<>();
		}
		node.setParent(this);
		children.add(node);
		
		if (isTemplate()) {
			//recalculate EmvMpmDataObject data object
			int len = this.children.stream().map(i -> i.getData().getILVLength()).reduce(0, Integer::sum);
			String twoDigitLength =  String.format("%02d", len);
			String value = this.children.stream().map(i -> i.getData().toEmvMpmData()).reduce("", String::concat);
			getData().setLength(twoDigitLength);
			getData().setValue(value);
		}
		
	}
	
	//Need ?
	public void delete(String id) {
		
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
		if( isPrimitive() ) {
			return getData().toEmvMpmData();
		} else {
			String result = "";
			
			if (isTemplate()) {
				result = getData().getId() + getData().getLength();
			} else if(isRoot()) {
				result  = "";
			}
			
			for (EmvMpmNode emvMpmNode : getChildren()) {
				result += emvMpmNode.toQrCodeData();
			}
			return result;
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
