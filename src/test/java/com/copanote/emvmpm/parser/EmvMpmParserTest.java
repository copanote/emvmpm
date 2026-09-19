package com.copanote.emvmpm.parser;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;

public class EmvMpmParserTest {

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testParserWithDefinition() throws ParserConfigurationException, SAXException, IOException {

		//GIVEN
		String qrData = "0102110002011531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";
		EmvMpmPackager emp = new EmvMpmPackager();
		emp.setEmvMpmPackager("emvmpm_bc.xml");
		EmvMpmDefinition emd = emp.create();

		//WHEN
        EmvMpmNode actualNode = EmvMpmParser.parse(qrData, emd);

        //THEN
        //todo:: assertion statement
		assertNotNull(actualNode);


	}

	@Test
	public void testFailParserWithOutDefinition() {
		//GIVEN
        String qrData = "1030512345010211625603091000058320515MQ2020000047618060800000000070800000001";
        //WHEN & THEN
        assertThrows(RuntimeException.class, () -> EmvMpmParser.parse(qrData));

	}

	@Test
	public void testParserWithOutDefinition() {
		//GIVEN
        String qrData = "030512345010211625603091000058320515MQ2020000047618060800000000070800000001";

        //WHEN
        EmvMpmNode actualNode = EmvMpmParser.parse(qrData);

        //THEN
        //todo:: assertion statement
		assertNotNull(actualNode);

	}


}
