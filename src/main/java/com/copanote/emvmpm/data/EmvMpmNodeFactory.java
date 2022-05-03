package com.copanote.emvmpm.data;

import java.util.List;

public class EmvMpmNodeFactory {
	
	public static EmvMpmNode root() {
		return new EmvMpmNode(EmvMpmDataObject.ROOT, null, null);
	}
	
	public static EmvMpmNode emptyCrc() {
		return new EmvMpmNode(EmvMpmDataObject.of("63","04", ""), null, null);
	}
	
	public static EmvMpmNode of(String id, String value) {
		return of(EmvMpmDataObject.of(id, value));
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

	//create Template Node by Items
	public static EmvMpmNode createTemplateNode(String id, List<EmvMpmNode> items) {
		
		int len = items.stream().map(i -> i.getData().getILVLength()).reduce(0, Integer::sum);
		String value = items.stream().map(i -> i.getData().toEmvMpmData()).reduce("", String::concat);
		
		EmvMpmDataObject dataObject = EmvMpmDataObject.of(id, len, value);
		
		return EmvMpmNodeFactory.of(dataObject, items);
	}

//	public static void markCrc(EmvMpmNode node) {
//		EmvMpmNode emptyCrc = EmvMpmNodeFactory.emptyCrc();
//		String data = node.toQrCodeData() + emptyCrc.toQrCodeData();
//		emptyCrc.getData().setValue(EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8));
//		node.add(emptyCrc);
//	}
	
}
