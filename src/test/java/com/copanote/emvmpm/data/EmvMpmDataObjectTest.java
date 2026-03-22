package com.copanote.emvmpm.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmDataObject")
class EmvMpmDataObjectTest {

    @Test
    @DisplayName("of(id, value) - length is auto-calculated as two-digit string")
    void of_autoLength() {
        EmvMpmDataObject obj = EmvMpmDataObject.of("26", "HELLO");
        assertEquals("26", obj.getId());
        assertEquals("05", obj.getLength());
        assertEquals("HELLO", obj.getValue());
    }

    @Test
    @DisplayName("of(id, intLength, value) - length is formatted as two-digit string")
    void of_intLength() {
        EmvMpmDataObject obj = EmvMpmDataObject.of("00", 2, "01");
        assertEquals("00", obj.getId());
        assertEquals("02", obj.getLength());
        assertEquals("01", obj.getValue());
    }

    @Test
    @DisplayName("of(id, intLength, value) - throws when length > 100")
    void of_intLength_throwsWhenTooLarge() {
        assertThrows(IllegalArgumentException.class, () -> EmvMpmDataObject.of("00", 101, "X"));
    }

    @Test
    @DisplayName("of(id, intLength, value) - throws when length < 0")
    void of_intLength_throwsWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> EmvMpmDataObject.of("00", -1, "X"));
    }

    @Test
    @DisplayName("toEmvMpmData() - concatenates id + length + value")
    void toEmvMpmData() {
        EmvMpmDataObject obj = EmvMpmDataObject.of("26", "ABCD");
        // id="26", length="04" (auto), value="ABCD"
        assertEquals("2604ABCD", obj.toEmvMpmData());
    }

    @Test
    @DisplayName("getILVLength() - returns total character count of id+length+value")
    void getILVLength() {
        EmvMpmDataObject obj = EmvMpmDataObject.of("26", "HELLO");  // id=2, len=2, value=5
        assertEquals(9, obj.getILVLength());
    }

    @ParameterizedTest(name = "id={0} value={1} => toEmvMpmData={2}")
    @CsvSource({
        "00, 01, 000201",
        "01, 11, 010211",
        "01, 12, 010212"
    })
    @DisplayName("predefined constants produce correct EMV MPM data strings")
    void predefinedConstants(String id, String value, String expected) {
        EmvMpmDataObject obj = EmvMpmDataObject.of(id, value);
        assertEquals(expected, obj.toEmvMpmData());
    }

    @Test
    @DisplayName("PAYLOAD_FORMAT_INDICATOR constant has correct fields")
    void payloadFormatIndicatorConstant() {
        EmvMpmDataObject pfi = EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR;
        assertEquals("00", pfi.getId());
        assertEquals("02", pfi.getLength());
        assertEquals("01", pfi.getValue());
        assertEquals("000201", pfi.toEmvMpmData());
    }

    @Test
    @DisplayName("POINT_INITATION_METHOD_STATIC constant has correct value")
    void pointInitiationMethodStaticConstant() {
        EmvMpmDataObject pim = EmvMpmDataObject.POINT_INITATION_METHOD_STATIC;
        assertEquals("01", pim.getId());
        assertEquals("11", pim.getValue());
    }

    @Test
    @DisplayName("POINT_INITATION_METHOD_DYNAMIC constant has correct value")
    void pointInitiationMethodDynamicConstant() {
        EmvMpmDataObject pim = EmvMpmDataObject.POINT_INITATION_METHOD_DYNAMIC;
        assertEquals("01", pim.getId());
        assertEquals("12", pim.getValue());
    }

    @Test
    @DisplayName("compareTo() - compares by id lexicographically")
    void compareTo() {
        EmvMpmDataObject a = EmvMpmDataObject.of("00", "X");
        EmvMpmDataObject b = EmvMpmDataObject.of("26", "Y");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(EmvMpmDataObject.of("00", "Z")));
    }
}