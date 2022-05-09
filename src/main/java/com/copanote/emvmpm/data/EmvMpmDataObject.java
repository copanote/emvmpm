package com.copanote.emvmpm.data;

import com.copanote.emvmpm.definition.EmvMpmDefinition;

public class EmvMpmDataObject implements Comparable<EmvMpmDataObject>, Cloneable {
	
	//preDefiend DataObject
	public static final EmvMpmDataObject ROOT = new EmvMpmDataObject("/", "", "");
	public static final EmvMpmDataObject PAYLOAD_FORMAT_INDICATOR = new EmvMpmDataObject("00", "02", "01");
	public static final EmvMpmDataObject POINT_INITATION_METHOD_STATIC  = new EmvMpmDataObject("01", "02", "11");
	public static final EmvMpmDataObject POINT_INITATION_METHOD_DYNAMIC = new EmvMpmDataObject("01", "02", "12");
	
	
	//An  ID shall be coded as a two-digit numeric value and shall have a value "00" to "99".
	private String id;
	//Length shall be coded as a two-digit numeric value and shall have a value "01" to "99".
	private String length;
	private String value;
	
	public static EmvMpmDataObject of(String id, String value) {
		return EmvMpmDataObject.of(id, value.length(), value);
	}
	
	public static EmvMpmDataObject of(String id, int length, String value) {
		
		if (length < 0 || length > 100) {
			throw new IllegalArgumentException("length shall have a vale 0 to 99");
		}
		
		String twoDigitLength =  String.format("%02d", length);
		
		return EmvMpmDataObject.of(id, twoDigitLength, value);
	}
	
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
	
	public String toEmvMpmData() {
		return getId() + getLength() + getValue();
	}
	
	@Override
	public String toString() {
		return "EmvMpmDataObject [id=" + id + ", length=" + length + ", value=" + value + "]";
	}
	
	public String toDetailedString(EmvMpmDefinition def) {
		
		return "";
	}
	
	
	@Override
	public int compareTo(EmvMpmDataObject o) {
		return this.getId().compareTo(o.getId());
	}

	@Override
	protected EmvMpmDataObject clone() throws CloneNotSupportedException {
		return EmvMpmDataObject.of(getId(), getLength(), getValue());
	}
	
	
	
}
