package com.copanote.emvmpm.data;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import com.copanote.emvmpm.parser.EmvMpmParser;

public class EmvMpmNodeTest {
	public static EmvMpmNode node = null;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
        String qrstr = "0102110002011531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";
        String qrData = "625603091000058320515MQ2020000047618060800000000070800000001";
        
		EmvMpmPackager emp = new EmvMpmPackager();
		emp.setEmvMpmPackager("emvmpm_bc.xml");
		EmvMpmDefinition emd = emp.create();
		
        node = EmvMpmParser.parse(qrstr, emd);
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
	public void testIsRoot() {
        
	}
	
	@Test 
	public void testIsTemplate() {
        
	}
	
	@Test 
	public void testfindChild() {
        
	}
	
	@Test
	public void testFind() {
		
	}
	
	@Test
	public void testgetCanonicalId() {
		
	}
	
	
	@Test
	public void testToQrCodeData() {
		
	}
	
	

}
