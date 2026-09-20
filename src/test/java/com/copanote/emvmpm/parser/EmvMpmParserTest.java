package com.copanote.emvmpm.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EmvMpmParser")
class EmvMpmParserTest {

    static EmvMpmDefinition definition;

    static final String VALID_QR =
            "0102110002011531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";

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

    // ── parseAndDefinitionValidation() ───────────────────────────────────────

    @Test
    @DisplayName("parseAndDefinitionValidation() returns the parsed root when every tag is defined")
    void parseAndDefinitionValidation_allTagsDefined_returnsRoot() {
        DataObjectDef[] fields = {
            new DataObjectDef("00", "Payload Format Indicator", DataObjectDef.Type.PRIMITIVE),
        };
        EmvMpmDefinition minimalDef = EmvMpmDefinition.of(Arrays.asList(fields));

        EmvMpmNode node = EmvMpmParser.parseAndDefinitionValidation("000201", minimalDef);

        assertNotNull(node);
        assertTrue(node.findChild("00").isPresent());
    }

    @Test
    @DisplayName("parseAndDefinitionValidation() throws when a tag is not in the definition")
    void parseAndDefinitionValidation_undefinedTag_throws() {
        DataObjectDef[] fields = {
            new DataObjectDef("00", "Payload Format Indicator", DataObjectDef.Type.PRIMITIVE),
        };
        EmvMpmDefinition minimalDef = EmvMpmDefinition.of(Arrays.asList(fields));

        // tag "97" is not declared in minimalDef
        String dataWithUndefinedTag = "000201970241";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> EmvMpmParser.parseAndDefinitionValidation(dataWithUndefinedTag, minimalDef));
        assertTrue(ex.getMessage().contains("/97"), "message should mention the offending tag: " + ex.getMessage());
    }

    // ── malformed / truncated data ───────────────────────────────────────────

    @Test
    @DisplayName("parse(qr) throws a clear error when declared length exceeds remaining data")
    void parse_truncatedData_throwsClearError() {
        // tag "00" declares length 05 but only 2 characters ("AB") remain
        String truncated = "0005AB";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> EmvMpmParser.parse(truncated));
        assertTrue(ex.getMessage().contains("00"), "message should mention the offending tag: " + ex.getMessage());
    }
}
