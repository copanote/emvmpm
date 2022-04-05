package com.copanote.emvmpm.data;

/**
 * @author dongwookshin
 * 
 * Markable Interface?
 */
public class EmvMpmDataObject {
	
	public static final EmvMpmDataObject ROOT = new EmvMpmDataObject("", "", "");
	
	private String id;
	private String length;
	private String value;
	
	public EmvMpmDataObject(String id, String length, String value) {
		this.id = id;
		this.length = length;
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
