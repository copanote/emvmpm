package com.copanote.emvmpm.data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
	
	@Test
	public void twoDigitTest() {
		//GIVEN
		int t1 = 0;
		String expected1 = "00";

		int t2 = 1;
		String expected2 = "01";

		int t3 = 3;
		String expected3 = "03";

		int t4 = 10;
		String expected4 = "10";

		int t5 = 99;
		String expected5 = "99";

		int t6 = 100;
		String expected6 = "100";
		
		int t7 = 1001;
		String expected7 = "1001";

		
		//WHEN
		String actual1 =  String.format("%02d", t1);
		String actual2 =  String.format("%02d", t2);
		String actual3 =  String.format("%02d", t3);
		String actual4 =  String.format("%02d", t4);
		String actual5 =  String.format("%02d", t5);
		String actual6 =  String.format("%02d", t6);
		String actual7 =  String.format("%02d", t7);
		
		//THEN
		assertThat(actual1, is(expected1));
		assertThat(actual2, is(expected2));
		assertThat(actual3, is(expected3));
		assertThat(actual4, is(expected4));
		assertThat(actual5, is(expected5));
		assertThat(actual6, is(expected6));
		assertThat(actual7, is(expected7));
		
	}
	
	
}
