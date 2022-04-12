package com.copanote.emvmpm.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.copanote.emvmpm.data.EmvMpmDataObject;
import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;

public class EmvMpmParser {
	
	private static final int LEN_ID = 2;
	private static final int LEN_LENGTH = 2;
	
	public static EmvMpmNode parse(String data, EmvMpmDefinition def) {
		EmvMpmNode rootNode = EmvMpmNode.root();
		return __parse(rootNode, data, def);
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
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        String qrstr = "0002010102111531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";
       
		EmvMpmPackager emp = new EmvMpmPackager();
		emp.setEmvMpmPackager("emvmpm_bc.xml");
		EmvMpmDefinition emd = emp.create();
        
		
        EmvMpmNode node = parse(qrstr, emd);
        
        System.out.println(node.toString());

		
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
