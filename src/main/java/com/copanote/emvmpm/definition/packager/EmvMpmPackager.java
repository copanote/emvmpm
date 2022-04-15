package com.copanote.emvmpm.definition.packager;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;

public class EmvMpmPackager {

	private List<DataObjectDef> FIELDS = new ArrayList<DataObjectDef>();
	
	public EmvMpmDefinition create() {
		
		return EmvMpmDefinition.of(this.FIELDS);
	}
	
	
	public void setEmvMpmPackager(DataObjectDef[] fields) {
		FIELDS = Arrays.asList(fields);
	}

	public void setEmvMpmPackager(List<DataObjectDef> fields) {
		FIELDS.addAll(fields);
	}
	
	/**
	 * @param path for emvmpm xml file 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void setEmvMpmPackager(String path) throws ParserConfigurationException, SAXException, IOException {
		parseAndConfigure(path);
	}
	
	//TODO
	public void setEmvMpmPackager(File file) {
	}

	//TODO
	public void setEmvMpmPackager(Reader reader) {
	}

	
	private void parseAndConfigure(String path) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(path);
		NodeList rootNodeList = doc.getElementsByTagName("mpmpackager");

		if (rootNodeList.getLength() < 1) {
			throw new IllegalArgumentException("There is no mpmpackager element");
		}
		
		Node mpmpackager = rootNodeList.item(0);
		FIELDS = configure(mpmpackager);

	}

	private List<DataObjectDef> configure(Node mpmpackager) {
		List<DataObjectDef> result = new ArrayList<DataObjectDef>();
		NodeList dataObject = mpmpackager.getChildNodes();
		
		for (int i = 0; i < dataObject.getLength(); i++) {
			Node element = dataObject.item(i);
			if (element.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			NamedNodeMap node = element.getAttributes();
			
			String id   = node.getNamedItem("id").getNodeValue();
			String name = node.getNamedItem("name").getNodeValue();
			String type = node.getNamedItem("type").getNodeValue();

			if (DataObjectDef.Type.TEMPLATE.toString().equalsIgnoreCase(type)) {
				List<DataObjectDef> children = configure(element);
				result.add(new DataObjectDef(id, name, DataObjectDef.Type.TEMPLATE, children));
			} else {
				result.add(new DataObjectDef(id, name, DataObjectDef.Type.PRIMITIVE));
			}

		}
		return result;
	}



	@Override
	public String toString() {
		return "EmvMpmPackager [FIELDS=" + FIELDS + "]";
	}
	
	

}