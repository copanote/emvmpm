package com.copanote.emvmpm.definition;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Optional;

import com.copanote.emvmpm.definition.packager.EmvMpmPackager;

public class EmvMpmDefinitionTest {
	private static final EmvMpmPackager bcEmvMpm = new EmvMpmPackager();
	private static EmvMpmDefinition bcEmvMpmDefinition = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bcEmvMpm.setEmvMpmPackager("emvmpm_bc.xml");
		bcEmvMpmDefinition = bcEmvMpm.create();
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
		assertThat(actualPath1, is(equalTo(expectedPath1)));
		assertThat(actualPath2, is(equalTo(expectedPath2)));
		assertThat(actualPath3, is(equalTo(expectedPath3)));
		assertThat(actualPath4, is(equalTo(expectedPath4)));
		assertThat(actualPath5, is(equalTo(expectedPath5)));
		assertThat(actualPath6, is(equalTo(expectedPath6)));
		assertThat(actualPath7, is(equalTo(expectedPath7)));
		assertThat(actualPath8, is(equalTo(expectedPath8)));
		
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
		assertThat(actual1, is(equalTo(expectedPath1FullId)));
		assertThat(actual2.get().getCanonicalId(), is(equalTo(expectedPath2FullId)));
		assertThat(actual3.get().getCanonicalId(), is(equalTo(expectedPath3FullId)));
		assertThat(actual4.get().getCanonicalId(), is(equalTo(expectedPath4FullId)));
		assertThat(actual5, is(equalTo(expectedPath5FullId)));
		assertThat(actual6, is(equalTo(expectedPath6FullId)));
		assertThat(actual7.get().getCanonicalId(), is(equalTo(expectedPath7FullId)));
		assertThat(actual8.get().getCanonicalId(), is(equalTo(expectedPath8FullId)));
		assertThat(actual9, is(equalTo(expectedPath9FullId)));

		
		
	}
	
	
	
	

}
