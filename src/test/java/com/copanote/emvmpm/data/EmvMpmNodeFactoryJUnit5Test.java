package com.copanote.emvmpm.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmNodeFactory")
class EmvMpmNodeFactoryJUnit5Test {

    @Test
    @DisplayName("root() creates a root node with no parent and ROOT data")
    void root_isRoot() {
        EmvMpmNode root = EmvMpmNodeFactory.root();
        assertTrue(root.isRoot());
        assertNull(root.getParent());
        assertEquals("/", root.getData().getId());
    }

    @Test
    @DisplayName("createPrimitive() creates a primitive leaf node")
    void createPrimitive_isPrimitive() {
        EmvMpmNode node = EmvMpmNodeFactory.createPrimitive("00", "01");
        assertTrue(node.isPrimitive());
        assertFalse(node.isTemplate());
        assertFalse(node.isRoot());
    }

    @Test
    @DisplayName("createTemplate() creates template with correct ILV serialization")
    void createTemplate_serialization() {
        List<EmvMpmNode> children = Arrays.asList(
            EmvMpmNodeFactory.of(EmvMpmDataObject.of("00", "D4100000014010")),
            EmvMpmNodeFactory.of(EmvMpmDataObject.of("05", "100005832"))
        );
        EmvMpmNode template = EmvMpmNodeFactory.createTemplate("26", children);

        String expected = "2631"
                + "0014D4100000014010"
                + "0509100005832";
        assertEquals(expected, template.toQrCodeData());
    }

    @Test
    @DisplayName("createTemplate() node is recognized as template")
    void createTemplate_isTemplate() {
        List<EmvMpmNode> children = Arrays.asList(
            EmvMpmNodeFactory.createPrimitive("00", "TEST")
        );
        EmvMpmNode template = EmvMpmNodeFactory.createTemplate("26", children);
        assertTrue(template.isTemplate());
        assertFalse(template.isPrimitive());
        assertFalse(template.isRoot());
    }

    @Test
    @DisplayName("emptyCrc() creates CRC node with id '63'")
    void emptyCrc_hasCorrectId() {
        EmvMpmNode crc = EmvMpmNodeFactory.emptyCrc();
        assertEquals("63", crc.getData().getId());
    }

    @Test
    @DisplayName("dynamicPim() has value '12'")
    void dynamicPim_hasValue12() {
        EmvMpmNode pim = EmvMpmNodeFactory.dynamicPim();
        assertEquals("12", pim.getData().getValue());
    }

    @Test
    @DisplayName("staticPim() has value '11'")
    void staticPim_hasValue11() {
        EmvMpmNode pim = EmvMpmNodeFactory.staticPim();
        assertEquals("11", pim.getData().getValue());
    }

    @Test
    @DisplayName("of(data) creates node with null parent and null children")
    void of_data_noParentNoChildren() {
        EmvMpmNode node = EmvMpmNodeFactory.of(EmvMpmDataObject.of("01", "12"));
        assertNull(node.getParent());
        assertNull(node.getChildren());
    }
}