package com.copanote.emvmpm.parser;

import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmParser")
class EmvMpmParserJUnit5Test {

    static EmvMpmDefinition definition;

    static final String VALID_QR = "0102110002011531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";

    @BeforeAll
    static void loadDefinition() throws Exception {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager("emvmpm_bc.xml");
        definition = packager.create();
    }

    @Test
    @DisplayName("parse(qr, def) returns non-null root node")
    void parseWithDefinition_returnsRoot() {
        EmvMpmNode node = EmvMpmParser.parse(VALID_QR, definition);
        assertNotNull(node);
        assertTrue(node.isRoot());
    }

    @Test
    @DisplayName("parse(qr, def) - round-trip toQrCodeData() equals original")
    void parseWithDefinition_roundTrip() {
        EmvMpmNode node = EmvMpmParser.parse(VALID_QR, definition);
        assertEquals(VALID_QR, node.toQrCodeData());
    }

    @Test
    @DisplayName("parse(qr, def) - child nodes are accessible by id")
    void parseWithDefinition_childrenAccessible() {
        EmvMpmNode node = EmvMpmParser.parse(VALID_QR, definition);
        assertTrue(node.findChild("00").isPresent(), "node 00 (Payload Format Indicator) should exist");
        assertTrue(node.findChild("26").isPresent(), "node 26 (template) should exist");
    }

    @Test
    @DisplayName("parse(qr, def) - template child has its own children")
    void parseWithDefinition_templateChildHasChildren() {
        EmvMpmNode node = EmvMpmParser.parse(VALID_QR, definition);
        EmvMpmNode template26 = node.findChild("26").orElseThrow();
        assertNotNull(template26.getChildren());
        assertFalse(template26.getChildren().isEmpty());
    }

    @Test
    @DisplayName("parse(qr) without definition - succeeds for valid standalone QR")
    void parseWithoutDefinition_succeeds() {
        String standaloneQr = "030512345010211625603091000058320515MQ2020000047618060800000000070800000001";
        EmvMpmNode node = EmvMpmParser.parse(standaloneQr);
        assertNotNull(node);
        assertTrue(node.isRoot());
    }

    @Test
    @DisplayName("parse(qr) without definition - throws RuntimeException when first tag > 10")
    void parseWithoutDefinition_throwsForInvalidQr() {
        String invalidQr = "1030512345010211625603091000058320515MQ2020000047618060800000000070800000001";
        assertThrows(RuntimeException.class, () -> EmvMpmParser.parse(invalidQr));
    }
}