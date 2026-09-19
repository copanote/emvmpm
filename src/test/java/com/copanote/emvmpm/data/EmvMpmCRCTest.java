package com.copanote.emvmpm.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmvMpmCRCTest {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {}

    @AfterAll
    public static void tearDownAfterClass() throws Exception {}

    @BeforeEach
    public void setUp() throws Exception {}

    @AfterEach
    public void tearDown() throws Exception {}

    @Test
    public void testCalculateEmvMpmCrc() {

        // GIVEN
        String data1 =
                "0002010102111531260004102600041071195812000000026310014D410000001401005091000054085204729953034105802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202000004811006080000000007080000000164240002ko0104나이스샷0206서울 마포구6304";
        String expectedCrc1 = "1331"; // 6304 1331
        String data2 =
                "0002010102121531260004102600041071195812000000026310014D4100000014010050910000540852047299530341054062569805802KR5910NAISHUSIAT6013SEOUL MAPO-GU610504086625603091000054080515MQ202200012703306081000445207080000000164240002ko0104나이스샷0206서울 마포구6304";
        String expectedCrc2 = "5C76";

        // WHEN
        String actualCrc1 = EmvMpmCRC.calculateEmvMpmCrc(data1, StandardCharsets.UTF_8);
        String actualCrc2 = EmvMpmCRC.calculateEmvMpmCrc(data2, StandardCharsets.UTF_8);

        // THEN
        assertEquals(expectedCrc1, actualCrc1);
        assertEquals(expectedCrc2, actualCrc2);
    }
}
