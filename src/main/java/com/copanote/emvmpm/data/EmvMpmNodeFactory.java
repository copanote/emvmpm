package com.copanote.emvmpm.data;

import java.util.List;

public class EmvMpmNodeFactory {
	
	public static EmvMpmNode root() {
		return EmvMpmNodeFactory.of(EmvMpmDataObject.ROOT);
	}
	
	public static EmvMpmNode emptyCrc() {
		return new EmvMpmNode(EmvMpmDataObject.of("63","04", ""), null, null);
	}
	
	public static EmvMpmNode dynamicPim() {
		return EmvMpmNodeFactory.of(EmvMpmDataObject.POINT_INITATION_METHOD_DYNAMIC);
	}
	
	public static EmvMpmNode staticPim() {
		return EmvMpmNodeFactory.of(EmvMpmDataObject.POINT_INITATION_METHOD_STATIC);
	}
	
	public static EmvMpmNode of(EmvMpmDataObject data) {
		return new EmvMpmNode(data, null, null);
	}
	
	public static EmvMpmNode of(EmvMpmDataObject data, EmvMpmNode parent) {
		return new EmvMpmNode(data, parent, null);
	}
	
	public static EmvMpmNode of(EmvMpmDataObject data, List<EmvMpmNode> children) {
		return new EmvMpmNode(data, null, children);
	}

	/**
	 * @param id
	 * @param value
	 * @return EmvMpmNode that has no children
	 * @Desc create primitive Node that has no children
	 */
	public static EmvMpmNode createPrimitive(String id, String value) {
		return of(EmvMpmDataObject.of(id, value));
	}

	/**
	 * 
	 * @param id
	 * @param children that children of this id
	 * @return EmvMpmNode that has children 
	 * @Desc   create Template node
	 */
	public static EmvMpmNode createTemplate(String id, List<EmvMpmNode> children) {
		
		int len = children.stream().map(i -> i.getData().getILVLength()).reduce(0, Integer::sum);
		String value = children.stream().map(i -> i.getData().toEmvMpmData()).reduce("", String::concat);
		
		EmvMpmDataObject dataObject = EmvMpmDataObject.of(id, len, value);
		
		return EmvMpmNodeFactory.of(dataObject, children);
	}
	
}
