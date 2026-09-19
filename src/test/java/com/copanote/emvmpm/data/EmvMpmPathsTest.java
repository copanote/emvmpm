package com.copanote.emvmpm.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmvMpmPathsTest {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {}

    @AfterAll
    public static void tearDownAfterClass() throws Exception {}

    @BeforeEach
    public void setUp() throws Exception {}

    @AfterEach
    public void tearDown() throws Exception {}

    @Test
    public void testGetEmvMpmPath() {
        // GIVEN
        String path1 = "/aa/bb/cc";
        String expectedEmvPath1 = "/aa/bb/cc";
        String path2 = "/aa/bb/";
        String expectedEmvPath2 = "/aa/bb";
        String path3 = "/";
        String expectedEmvPath3 = "/";
        String path4 = "abc/ddd/eee/";
        String expectedEmvPath4 = "abc/ddd/eee";
        String path5 = "///abc//ee//dd///";
        String expectedEmvPath5 = "/abc/ee/dd";

        // WHEN
        String actualEmvPath1 = EmvMpmPaths.getEmvMpmPath(path1);
        String actualEmvPath2 = EmvMpmPaths.getEmvMpmPath(path2);
        String actualEmvPath3 = EmvMpmPaths.getEmvMpmPath(path3);
        String actualEmvPath4 = EmvMpmPaths.getEmvMpmPath(path4);
        String actualEmvPath5 = EmvMpmPaths.getEmvMpmPath(path5);

        // THEN
        assertEquals(expectedEmvPath1, actualEmvPath1);
        assertEquals(expectedEmvPath2, actualEmvPath2);
        assertEquals(expectedEmvPath3, actualEmvPath3);
        assertEquals(expectedEmvPath4, actualEmvPath4);
        assertEquals(expectedEmvPath5, actualEmvPath5);
    }
}
