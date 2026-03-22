package com.copanote.emvmpm.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmCRC")
class EmvMpmCRCJUnit5Test {

    @Test
    @DisplayName("CRC of real QR data 1 matches expected 1331")
    void calculateCrc_data1() {
        String data = "0002010102111531260004102600041071195812000000026310014D410000001401005091000054085204729953034105802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202000004811006080000000007080000000164240002ko0104나이스샷0206서울 마포구6304";
        assertEquals("1331", EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("CRC of real QR data 2 matches expected 5C76")
    void calculateCrc_data2() {
        String data = "0002010102121531260004102600041071195812000000026310014D4100000014010050910000540852047299530341054062569805802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202200012703306081000445207080000000164240002ko0104나이스샷0206서울 마포구6304";
        assertEquals("5C76", EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("CRC result is always 4 characters long")
    void calculateCrc_alwaysFourChars() {
        String data = "0002016304";
        String crc = EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8);
        assertEquals(4, crc.length());
    }

    @Test
    @DisplayName("CRC result is uppercase hex")
    void calculateCrc_isUppercaseHex() {
        String data = "0002016304";
        String crc = EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8);
        assertTrue(crc.matches("[0-9A-F]{4}"), "CRC should be 4 uppercase hex chars, got: " + crc);
    }

    @ParameterizedTest(name = "data={0} => crc={1}")
    @CsvSource({
        "0002010102111531260004102600041071195812000000026310014D410000001401005091000054085204729953034105802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202000004811006080000000007080000000164240002ko0104나이스샷0206서울 마포구6304, 1331",
        "0002010102121531260004102600041071195812000000026310014D4100000014010050910000540852047299530341054062569805802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202200012703306081000445207080000000164240002ko0104나이스샷0206서울 마포구6304, 5C76"
    })
    @DisplayName("parameterized CRC calculation")
    void calculateCrc_parameterized(String data, String expectedCrc) {
        assertEquals(expectedCrc, EmvMpmCRC.calculateEmvMpmCrc(data, StandardCharsets.UTF_8));
    }
}