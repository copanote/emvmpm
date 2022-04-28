package com.copanote.emvmpm.data;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmvMpmNodeFactoryTest {

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
	public void constructTreeTest() {
		
		EmvMpmNode root = EmvMpmNodeFactory.root();
		
		EmvMpmNode payloadFormatIndicator = EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR);
		EmvMpmNode t2 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("02", "1234"));
		EmvMpmNode t3 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("03", "12345"));
		EmvMpmNode t4 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("04", "123456"));
		EmvMpmNode t5 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("05", "1234567"));
		EmvMpmNode t6 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("06", "12345678"));
		EmvMpmNode t7 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("07", "123456789"));
		EmvMpmNode t8 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("08", "1234567890"));
		
		String tem = "2631"
				+ "0014D4100000014010"
				+ "0509100005832";
		List<EmvMpmNode> micList = new  ArrayList<>();
		micList.add(  EmvMpmNodeFactory.of(EmvMpmDataObject.of("00", "D4100000014010"))  );
		micList.add(  EmvMpmNodeFactory.of(EmvMpmDataObject.of("05", "100005832"))        );
		EmvMpmNode mic = EmvMpmNodeFactory.createTemplateNode("26", micList);
		mic.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("09", "9999"))   );
		
		System.out.println(mic.toString());
		
		
		root.add(payloadFormatIndicator);
		root.add(t2);
		root.add(t3);
		root.add(t4);
		root.add(t5);
		root.add(t6);
		root.add(t7);
		root.add(t8);
		root.add(mic);
		
		EmvMpmNodeFactory.markCrc(root);
		
		System.out.println(root.toQrCodeData());
		
		
		
	}

}
