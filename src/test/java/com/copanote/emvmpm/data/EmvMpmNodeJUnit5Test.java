package com.copanote.emvmpm.data;

import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import com.copanote.emvmpm.parser.EmvMpmParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmNode")
class EmvMpmNodeJUnit5Test {

    static final String QR_STR = "0102110002011531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";

    static EmvMpmNode root;

    @BeforeAll
    static void setup() throws Exception {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager("emvmpm_bc.xml");
        EmvMpmDefinition def = packager.create();
        root = EmvMpmParser.parse(QR_STR, def);
    }

    // ── isRoot / isTemplate / isPrimitive ──────────────────────────────────

    @Test
    @DisplayName("parsed root node isRoot() == true")
    void parsedRoot_isRoot() {
        assertTrue(root.isRoot());
    }

    @Test
    @DisplayName("parsed root node isTemplate() == false")
    void parsedRoot_isNotTemplate() {
        assertFalse(root.isTemplate());
    }

    @Test
    @DisplayName("parsed root node isPrimitive() == false")
    void parsedRoot_isNotPrimitive() {
        assertFalse(root.isPrimitive());
    }

    @Test
    @DisplayName("template child isTemplate() == true")
    void templateChild_isTemplate() {
        Optional<EmvMpmNode> child26 = root.findChild("26");
        assertTrue(child26.isPresent(), "node 26 should exist");
        assertTrue(child26.get().isTemplate());
        assertFalse(child26.get().isPrimitive());
    }

    @Test
    @DisplayName("primitive child isPrimitive() == true")
    void primitiveChild_isPrimitive() {
        Optional<EmvMpmNode> child00 = root.findChild("00");
        assertTrue(child00.isPresent(), "node 00 should exist");
        assertTrue(child00.get().isPrimitive());
        assertFalse(child00.get().isTemplate());
    }

    // ── findChild / find ──────────────────────────────────────────────────

    @Test
    @DisplayName("findChild() returns present for existing id")
    void findChild_existingId() {
        assertTrue(root.findChild("00").isPresent());
        assertTrue(root.findChild("26").isPresent());
    }

    @Test
    @DisplayName("findChild() returns empty for non-existing id")
    void findChild_missingId() {
        assertFalse(root.findChild("99").isPresent());
    }

    @Test
    @DisplayName("find() by canonical path '/26/00' returns correct node")
    void find_byCanonicalPath() {
        Optional<EmvMpmNode> node = root.find("/26/00");
        assertTrue(node.isPresent());
        assertEquals("00", node.get().getData().getId());
    }

    @Test
    @DisplayName("find() by non-existing path returns empty")
    void find_nonExistingPath() {
        assertFalse(root.find("/99/00").isPresent());
    }

    // ── getCanonicalId ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getCanonicalId() of root is empty string")
    void canonicalId_root() {
        assertEquals("", root.getCanonicalId());
    }

    @Test
    @DisplayName("getCanonicalId() of top-level child is '/id'")
    void canonicalId_topLevelChild() {
        Optional<EmvMpmNode> child = root.findChild("00");
        assertTrue(child.isPresent());
        assertEquals("/00", child.get().getCanonicalId());
    }

    @Test
    @DisplayName("getCanonicalId() of nested child includes full path")
    void canonicalId_nestedChild() {
        Optional<EmvMpmNode> node = root.find("/26/00");
        assertTrue(node.isPresent());
        assertEquals("/26/00", node.get().getCanonicalId());
    }

    // ── toQrCodeData ──────────────────────────────────────────────────────

    @Test
    @DisplayName("toQrCodeData() round-trips back to original QR string")
    void toQrCodeData_roundTrip() {
        assertEquals(QR_STR, root.toQrCodeData());
    }

    // ── toHexQrCodeData ───────────────────────────────────────────────────

    @Test
    @DisplayName("toHexQrCodeData() returns non-empty uppercase hex")
    void toHexQrCodeData_nonEmpty() {
        String hex = root.toHexQrCodeData();
        assertNotNull(hex);
        assertFalse(hex.isEmpty());
        assertTrue(hex.matches("[0-9A-F]+"), "Should be uppercase hex, got: " + hex);
    }

    // ── add() / markCrc() ─────────────────────────────────────────────────

    @Test
    @DisplayName("add() to root increases children count")
    void add_increasesChildCount() {
        EmvMpmNode newRoot = EmvMpmNodeFactory.root();
        newRoot.add(EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR));
        assertEquals(1, newRoot.getChildren().size());
    }

    @Test
    @DisplayName("markCrc() appends CRC node with id '63'")
    void markCrc_appendsCrcNode() {
        EmvMpmNode newRoot = EmvMpmNodeFactory.root();
        newRoot.add(EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR));
        newRoot.markCrc();

        Optional<EmvMpmNode> crc = newRoot.findChild("63");
        assertTrue(crc.isPresent());
        assertEquals(4, crc.get().getData().getValue().length(), "CRC value should be 4 chars");
    }

    @Test
    @DisplayName("add() to template recalculates parent length and value")
    void add_toTemplate_recalculatesLengthAndValue() {
        List<EmvMpmNode> children = new ArrayList<>(Arrays.asList(
            EmvMpmNodeFactory.of(EmvMpmDataObject.of("00", "ABCD"))
        ));
        EmvMpmNode template = EmvMpmNodeFactory.createTemplate("26", children);
        int lengthBefore = Integer.parseInt(template.getData().getLength());

        template.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("01", "EF")));
        int lengthAfter = Integer.parseInt(template.getData().getLength());

        assertTrue(lengthAfter > lengthBefore, "Template length should increase after adding a child");
    }
}