package com.copanote.emvmpm.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.copanote.emvmpm.data.EmvMpmDataObject;
import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;

public class EmvMpmParser {
	
	private static final int LEN_ID = 2;
	private static final int LEN_LENGTH = 2;
	
	public static EmvMpmNode parse(String data, EmvMpmDefinition def) {
		return __parse(EmvMpmNode.root(), data, def);
	}
	
	private static EmvMpmNode __parse(EmvMpmNode node, String childData, EmvMpmDefinition def) {
		
        List<EmvMpmDataObject> children = parseChild(childData);
		List<EmvMpmNode> childrenNode = children.stream().map(e -> EmvMpmNode.of(e, node)).collect(Collectors.toList());
        node.setChildren(childrenNode);
        
        for (EmvMpmNode emvMpmNode : childrenNode) {
        	if (isTemplate(emvMpmNode, def)) {
				__parse(emvMpmNode, emvMpmNode.getData().getValue(), def);
			}
			
		}
		return node;
	}
	
	
	
	private static boolean isTemplate(EmvMpmNode node, EmvMpmDefinition def) {
		System.out.println(node.getCanonicalId());
		Optional<DataObjectDef> d = def.find(node.getCanonicalId());
		if (d.isPresent()) {
			DataObjectDef ddef = d.get();
			if (ddef.isTemplate()) {
				return true;
			}
		}
		return false;
	}
	
	
	private static List<EmvMpmDataObject> parseChild(String data) {
		List<EmvMpmDataObject> children = new ArrayList<>();
		
		EmvMpmDataObject emdo = null;
		int parsedLength = getTotalLength(children);
		
		while (data.length() > parsedLength) {
			emdo = parseOneNode(data.substring(parsedLength));
			children.add(emdo);
			parsedLength = getTotalLength(children);
		}
		
		
		return children;
	}
	
	private static int getTotalLength(List<EmvMpmDataObject> list) {
		return list.stream().mapToInt(i -> i.getILVLength()).sum();
	}
	
	private static EmvMpmDataObject parseOneNode(String data) {
		int cursor = 0;
		
		String id = data.substring(cursor, cursor + LEN_ID);
		cursor += LEN_ID;
		
		String sLentgh = data.substring(cursor, cursor + LEN_LENGTH);
		cursor += LEN_LENGTH;
		int iLength = Integer.parseInt(sLentgh);
		
		if (cursor + iLength <= data.length()) {
			
		}
		
		String value = data.substring(cursor, cursor + iLength);
		cursor += iLength;
		
		return new EmvMpmDataObject(id, sLentgh, value);
	}
	
}
