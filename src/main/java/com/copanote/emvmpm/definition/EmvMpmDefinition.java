package com.copanote.emvmpm.definition;

import java.util.List;


public class EmvMpmDefinition {
	//Data Source
	private List<DataObjectDef> definitionList;
	
	public EmvMpmDefinition(List<DataObjectDef> definitionList) {
		this.definitionList = definitionList;
	}

	public static EmvMpmDefinition of(List<DataObjectDef> definitionList) {
		if (definitionList == null || definitionList.isEmpty()) {
			throw new IllegalArgumentException("param must not be null or empty");
		}
		
		return new EmvMpmDefinition(definitionList);
	}
	
	public DataObjectDef find(String canonicalId) {
		
		definitionList.stream().filter(dod -> dod.getCanonicalId().equalsIgnoreCase(canonicalId)).findAny();
		
		
		return null;
	}
	
	public boolean isTemplate(String canonicalId) {
		return false;
	}
	
	public String findDescription(String canonicalId) {
		return "";
	}
	
	public String printDefinition() {
		return "";
	}
	
	private String resolve(String canonicalId) {
		return "";
	}
	
	
}
