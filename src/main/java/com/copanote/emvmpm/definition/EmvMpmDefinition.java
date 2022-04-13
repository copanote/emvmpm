package com.copanote.emvmpm.definition;

import java.util.List;
import java.util.Optional;


public class EmvMpmDefinition {
	//Data Source
	private List<DataObjectDef> definitionList;
	
	public EmvMpmDefinition(List<DataObjectDef> definitionList) {
		this.definitionList = definitionList;
	}

	public static EmvMpmDefinition of(List<DataObjectDef> definitionList) {
		if (definitionList == null || definitionList.isEmpty()) {
			throw new IllegalArgumentException("argument must not be null or empty");
		}
		
		return new EmvMpmDefinition(definitionList);
	}
	
//	public Optional<DataObjectDef> find(String canonicalId) {
//		return definitionList.stream().filter(dod -> dod.getCanonicalId().equalsIgnoreCase(canonicalId)).findAny();
//	}

	public Optional<DataObjectDef> find(String canonicalId) {
		
		//resolve canonicalId
		
		return _find(definitionList, canonicalId);
		
	}
	
	
	private Optional<DataObjectDef> _find(List<DataObjectDef> defs, String canonicalId) {
		Optional<DataObjectDef> result = null;
		
		for (DataObjectDef dataObjectDef : defs) {
			if (dataObjectDef.getCanonicalId().equalsIgnoreCase(canonicalId)) {
				return Optional.of(dataObjectDef);
			} else {
				if (dataObjectDef.isTemplate()) {
					result = _find(dataObjectDef.getChildren(), canonicalId);
					if (result.isPresent()) {
						return result;
					}
				}
			}
		}
		
		return Optional.empty();
	}
	 
	
	public boolean isTemplate(String canonicalId) {
		Optional<DataObjectDef> dod = find(canonicalId);
		if (dod.isPresent()) {
			if (DataObjectDef.Type.TEMPLATE == dod.get().getType()) {
				return true;
			}
		}
		
		return false;
	}
	
//	public String findDescription(String canonicalId) {
//		return "";
//	}
	
	public String printDefinition() {
		return definitionList.toString();
	}

	@Override
	public String toString() {
		return "EmvMpmDefinition [definitionList=" + definitionList + "]";
	}
	
	
	
}
