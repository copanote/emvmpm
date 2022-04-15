package com.copanote.emvmpm.definition.packager;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;

public class EmvMpmPackagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPath() throws ParserConfigurationException, SAXException, IOException {

		EmvMpmPackager emp = new EmvMpmPackager();
		emp.setEmvMpmPackager("emvmpm_bc.xml");
//		System.out.println(emp.toString());
		EmvMpmDefinition definition = emp.create();
//		System.out.println(definition.toString());
		
		System.out.println(definition.find("/00"));
		System.out.println(definition.find("/26/01"));
		System.out.println(definition.find("/26/00"));
		System.out.println(definition.find("/62/50/00"));
		System.out.println(definition.find("/64"));
		System.out.println(definition.find("/"));

		
	}

//	@Test
	public void testArray() {

		DataObjectDef[] fields = {
				new DataObjectDef("00", "Payload Format Indicator", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("01", "Point of Initiation Method", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("15", "Payload Format Indicator", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("26", "Payload Format Indicator", DataObjectDef.Type.TEMPLATE,
						Arrays.asList(
								new DataObjectDef("00", "Globally Unique Indentifier", DataObjectDef.Type.PRIMITIVE),
								new DataObjectDef("05", "Payment Network Specific", DataObjectDef.Type.PRIMITIVE))),
				new DataObjectDef("52", "Merchant Category Code", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("53", "Transaction Currency", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("54", "Tip or Convenience Indicator", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("56", "Value of Convenience Fee Fixed", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("57", "Value of Convenience Fee Percentage", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("58", "Country Code", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("59", "Merchant Name", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("60", "Merchant City", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("61", "Postal Code", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("62", "Payload Format Indicator", DataObjectDef.Type.TEMPLATE, Arrays.asList(
						new DataObjectDef("01", "Bill Number", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("02", "Mobile Number", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("03", "Store ID", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("04", "Loyalty Number", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("05", "Reference ID", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("06", "Customer ID", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("07", "Terminal ID", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("08", "Purpose of Transaction", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("09", "Additional Consumer Data Request", DataObjectDef.Type.PRIMITIVE),
						new DataObjectDef("50", "BC Loacl", DataObjectDef.Type.TEMPLATE,
								Arrays.asList(
										new DataObjectDef("00", "Globally Unique Indentifier",DataObjectDef.Type.PRIMITIVE),
										new DataObjectDef("01", "Installment Month", DataObjectDef.Type.PRIMITIVE),
										new DataObjectDef("02", "Membership", DataObjectDef.Type.PRIMITIVE))))),
				new DataObjectDef("63", "CRC", DataObjectDef.Type.PRIMITIVE),
				new DataObjectDef("64", "Merchant Information Language Template", DataObjectDef.Type.TEMPLATE,
						Arrays.asList(new DataObjectDef("00", "Language Preference", DataObjectDef.Type.PRIMITIVE),
								new DataObjectDef("01", "Merchant Name-Alternate Language", DataObjectDef.Type.PRIMITIVE),
								new DataObjectDef("02", "Merchant City-Alternate Language", DataObjectDef.Type.PRIMITIVE))), 
				};
		
		EmvMpmPackager emp = new EmvMpmPackager();
		emp.setEmvMpmPackager(fields);
		System.out.println(emp.toString());
		
	}

}
