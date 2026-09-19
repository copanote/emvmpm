package com.copanote.emvmpm.definition;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import com.copanote.emvmpm.definition.packager.EmvMpmPackager;

public class EmvMpmDefinitionTest {
	private static final EmvMpmPackager bcEmvMpm = new EmvMpmPackager();
	private static EmvMpmDefinition bcEmvMpmDefinition = null;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		bcEmvMpm.setEmvMpmPackager("emvmpm_bc.xml");
		bcEmvMpmDefinition = bcEmvMpm.create();
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
	public void isTemplate() {
		//GIVEN
		String path1 = "/";
		boolean expectedPath1 = false;

		String path2 = "/00";
		boolean expectedPath2 = false;

		String path3 = "/26";
		boolean expectedPath3 = true;

		String path4 = "/26/00/";
		boolean expectedPath4 = false;

		String path5 = "/26/01/";
		boolean expectedPath5 = false;

		String path6 = "/26/01";
		boolean expectedPath6 = false;

		String path7 = "/62/50/00";
		boolean expectedPath7 = false;

		String path8 = "/64";
		boolean expectedPath8 = true;


		//WHEN
		boolean actualPath1 = bcEmvMpmDefinition.isTemplate(path1);
		boolean actualPath2 = bcEmvMpmDefinition.isTemplate(path2);
		boolean actualPath3 = bcEmvMpmDefinition.isTemplate(path3);
		boolean actualPath4 = bcEmvMpmDefinition.isTemplate(path4);
		boolean actualPath5 = bcEmvMpmDefinition.isTemplate(path5);
		boolean actualPath6 = bcEmvMpmDefinition.isTemplate(path6);
		boolean actualPath7 = bcEmvMpmDefinition.isTemplate(path7);
		boolean actualPath8 = bcEmvMpmDefinition.isTemplate(path8);

		//THEN
		assertEquals(expectedPath1, actualPath1);
		assertEquals(expectedPath2, actualPath2);
		assertEquals(expectedPath3, actualPath3);
		assertEquals(expectedPath4, actualPath4);
		assertEquals(expectedPath5, actualPath5);
		assertEquals(expectedPath6, actualPath6);
		assertEquals(expectedPath7, actualPath7);
		assertEquals(expectedPath8, actualPath8);

	}

	@Test
	public void testFind() {

		//GIVEN
		String path1 = "/";
		Optional<DataObjectDef> expectedPath1FullId = Optional.empty();

		String path2 = "/00";
		String expectedPath2FullId = "/00";

		String path3 = "/26";
		String expectedPath3FullId = "/26";

		String path4 = "/26/00/";
		String expectedPath4FullId = "/26/00";

		String path5 = "/26/01/";
		Optional<DataObjectDef> expectedPath5FullId = Optional.empty();

		String path6 = "/26/01";
		Optional<DataObjectDef> expectedPath6FullId = Optional.empty();


		String path7 = "/62/50/00";
		String expectedPath7FullId = "/62/50/00";

		String path8 = "/64";
		String expectedPath8FullId = "/64";

		String path9 = "123/111";
		Optional<DataObjectDef> expectedPath9FullId = Optional.empty();

		//WHEN
		Optional<DataObjectDef> actual1 = bcEmvMpmDefinition.find(path1);
		Optional<DataObjectDef> actual2 = bcEmvMpmDefinition.find(path2);
		Optional<DataObjectDef> actual3 = bcEmvMpmDefinition.find(path3);
		Optional<DataObjectDef> actual4 = bcEmvMpmDefinition.find(path4);
		Optional<DataObjectDef> actual5 = bcEmvMpmDefinition.find(path5);
		Optional<DataObjectDef> actual6 = bcEmvMpmDefinition.find(path6);
		Optional<DataObjectDef> actual7 = bcEmvMpmDefinition.find(path7);
		Optional<DataObjectDef> actual8 = bcEmvMpmDefinition.find(path8);
		Optional<DataObjectDef> actual9 = bcEmvMpmDefinition.find(path9);


		//THEN
		assertEquals(expectedPath1FullId, actual1);
		assertEquals(expectedPath2FullId, actual2.get().getCanonicalId());
		assertEquals(expectedPath3FullId, actual3.get().getCanonicalId());
		assertEquals(expectedPath4FullId, actual4.get().getCanonicalId());
		assertEquals(expectedPath5FullId, actual5);
		assertEquals(expectedPath6FullId, actual6);
		assertEquals(expectedPath7FullId, actual7.get().getCanonicalId());
		assertEquals(expectedPath8FullId, actual8.get().getCanonicalId());
		assertEquals(expectedPath9FullId, actual9);



	}





}
