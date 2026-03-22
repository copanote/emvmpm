package com.copanote.emvmpm.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmPaths")
class EmvMpmPathsJUnit5Test {

    @Test
    @DisplayName("getRootId() returns '/'")
    void getRootId() {
        assertEquals("/", EmvMpmPaths.getRootId());
    }

    @Test
    @DisplayName("getDelimiter() returns '/'")
    void getDelimiter() {
        assertEquals("/", EmvMpmPaths.getDelimiter());
    }

    @ParameterizedTest(name = "input={0} => normalized={1}")
    @CsvSource({
        "/aa/bb/cc,  /aa/bb/cc",
        "/aa/bb/,    /aa/bb",
        "/,          /",
        "abc/ddd/eee/, abc/ddd/eee",
        "///abc//ee//dd///, /abc/ee/dd"
    })
    @DisplayName("getEmvMpmPath() normalizes paths correctly")
    void getEmvMpmPath(String input, String expected) {
        assertEquals(expected.trim(), EmvMpmPaths.getEmvMpmPath(input.trim()));
    }

    @Test
    @DisplayName("parsePath('/26/00') returns [/, 26, 00]")
    void parsePath_nested() {
        List<String> segments = EmvMpmPaths.parsePath("/26/00");
        assertEquals(Arrays.asList("/", "26", "00"), segments);
    }

    @Test
    @DisplayName("parsePath('/') returns [/]")
    void parsePath_rootOnly() {
        List<String> segments = EmvMpmPaths.parsePath("/");
        assertEquals(Arrays.asList("/"), segments);
    }

    @Test
    @DisplayName("parsePath('/62/50/00') returns [/, 62, 50, 00]")
    void parsePath_deeplyNested() {
        List<String> segments = EmvMpmPaths.parsePath("/62/50/00");
        assertEquals(Arrays.asList("/", "62", "50", "00"), segments);
    }
}