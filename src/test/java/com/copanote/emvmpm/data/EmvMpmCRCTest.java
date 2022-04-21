package com.copanote.emvmpm.data;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmvMpmCRCTest {

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
	public void testCalculateEmvMpmCrc() {
		
		//GIVEN
		String data = "0002010102111531260004102600041071195812000000026310014D410000001401005091000054085204729953034105802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202000004811006080000000007080000000164240002ko0104나이스샷0206서울 마포구6304";
		String expectedCrc = "1331";
		//6304 1331
		
		//WHEN
		String actualCrc = EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8);
		
		//THEN
		assertThat(actualCrc, is(expectedCrc));
		
	}

}
