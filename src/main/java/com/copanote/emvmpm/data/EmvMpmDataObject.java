package com.copanote.emvmpm.data;

public class EmvMpmDataObject implements Comparable<EmvMpmDataObject> {
	
	public static final EmvMpmDataObject ROOT = new EmvMpmDataObject("/", "", "");
	
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
	
	public int getILVLength() {
		return id.length() + length.length() + value.length();
	}
	
	@Override
	public String toString() {
		return "EmvMpmDataObject [id=" + id + ", length=" + length + ", value=" + value + "]";
	}
	
	@Override
	public int compareTo(EmvMpmDataObject o) {
		return this.getId().compareTo(o.getId());
	}
	
}
