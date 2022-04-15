package com.copanote.emvmpm.data;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmvMpmPathsTest {

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
	public void testGetEmvMpmPath() {
		//GIVEN
		String path1 = "/aa/bb/cc";
		String expectedEmvPath1 = "/aa/bb/cc";
		String path2 = "/aa/bb/";
		String expectedEmvPath2 = "/aa/bb";
		String path3 = "/";
		String expectedEmvPath3 = "/";
		String path4 = "abc/ddd/eee/";
		String expectedEmvPath4 = "abc/ddd/eee";
		String path5 ="///abc//ee//dd///";
		String expectedEmvPath5 = "/abc/ee/dd";
		
		
		//WHEN
		String actualEmvPath1 = EmvMpmPaths.getEmvMpmPath(path1);
		String actualEmvPath2 = EmvMpmPaths.getEmvMpmPath(path2);
		String actualEmvPath3 = EmvMpmPaths.getEmvMpmPath(path3);
		String actualEmvPath4 = EmvMpmPaths.getEmvMpmPath(path4);
		String actualEmvPath5 = EmvMpmPaths.getEmvMpmPath(path5);

		
		//THEN
		assertThat(actualEmvPath1, is(equalTo(expectedEmvPath1)));
		assertThat(actualEmvPath2, is(equalTo(expectedEmvPath2)));
		assertThat(actualEmvPath3, is(equalTo(expectedEmvPath3)));
		assertThat(actualEmvPath4, is(equalTo(expectedEmvPath4)));
		assertThat(actualEmvPath5, is(equalTo(expectedEmvPath5)));

		
	}

}
