package com.copanote.emvmpm.data;

public class EmvMpmDataObject implements Comparable<EmvMpmDataObject> {
	
	public static final EmvMpmDataObject ROOT = new EmvMpmDataObject("/", "", "");
	
	//preDefiend DataObject
	public static final EmvMpmDataObject PAYLOAD_FORMAT_INDICATOR = new EmvMpmDataObject("00", "02", "01");

	
	private String id;
	private String length;
	private String value;
	
	public static EmvMpmDataObject of(String id, String length, String value) {
		//specification validation
		return new EmvMpmDataObject(id, length, value);
	}
	
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
	
	public String toQrCodeData() {
		return getId() + getLength() + getValue();
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
