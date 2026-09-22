package com.copanote.emvmpm.definition.packager;

import static org.junit.jupiter.api.Assertions.*;

import com.copanote.emvmpm.EmvMpmException;
import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

@DisplayName("EmvMpmPackager")
class EmvMpmPackagerTest {

    private static final DataObjectDef[] MINIMAL_FIELDS = {
        new DataObjectDef("00", "Payload Format Indicator", DataObjectDef.Type.PRIMITIVE),
        new DataObjectDef("01", "Point of Initiation Method", DataObjectDef.Type.PRIMITIVE),
        new DataObjectDef(
                "26",
                "Merchant Account Info",
                DataObjectDef.Type.TEMPLATE,
                Arrays.asList(
                        new DataObjectDef("00", "Globally Unique Identifier", DataObjectDef.Type.PRIMITIVE),
                        new DataObjectDef("05", "Payment Network Specific", DataObjectDef.Type.PRIMITIVE))),
        new DataObjectDef("52", "Merchant Category Code", DataObjectDef.Type.PRIMITIVE),
        new DataObjectDef("63", "CRC", DataObjectDef.Type.PRIMITIVE),
    };

    @Test
    @DisplayName("of(String path) loads definition from file path")
    void loadFromPath_createsDefinition() throws Exception {
        EmvMpmDefinition def = EmvMpmPackager.of("emvmpm_bc.xml").create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("of(File) loads definition from File object")
    void loadFromFile_createsDefinition() throws Exception {
        EmvMpmDefinition def = EmvMpmPackager.of(new File("emvmpm_bc.xml")).create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("of(InputStream) loads definition from InputStream")
    void loadFromInputStream_createsDefinition() throws Exception {
        File file = new File("emvmpm_bc.xml");
        EmvMpmDefinition def =
                EmvMpmPackager.of(Files.newInputStream(file.toPath())).create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("of(array) loads definition from DataObjectDef array")
    void loadFromArray_createsDefinition() {
        EmvMpmDefinition def = EmvMpmPackager.of(MINIMAL_FIELDS).create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("of(list) loads definition from DataObjectDef list")
    void loadFromList_createsDefinition() {
        EmvMpmDefinition def = EmvMpmPackager.of(Arrays.asList(MINIMAL_FIELDS)).create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("path and file sources produce equivalent definitions for '/26'")
    void pathAndFile_produceEquivalentDefinitions() throws Exception {
        EmvMpmDefinition defPath = EmvMpmPackager.of("emvmpm_bc.xml").create();
        EmvMpmDefinition defFile =
                EmvMpmPackager.of(new File("emvmpm_bc.xml")).create();

        assertEquals(defPath.isTemplate("/26"), defFile.isTemplate("/26"));
        assertEquals(defPath.find("/26/00").isPresent(), defFile.find("/26/00").isPresent());
    }

    @Test
    @DisplayName("array-based definition finds nested path '/26/00'")
    void arrayDefinition_findsNestedPath() {
        EmvMpmDefinition def = EmvMpmPackager.of(MINIMAL_FIELDS).create();

        assertTrue(def.find("/26/00").isPresent());
        assertEquals("/26/00", def.find("/26/00").get().getCanonicalId());
    }

    @Test
    @DisplayName("array-based definition: '/26' is template, '/26/00' is not template")
    void arrayDefinition_templateFlags() {
        EmvMpmDefinition def = EmvMpmPackager.of(MINIMAL_FIELDS).create();

        assertTrue(def.isTemplate("/26"));
        assertFalse(def.isTemplate("/26/00"));
        assertFalse(def.isTemplate("/00"));
    }

    @Test
    @DisplayName("of(InputStream) wraps malformed XML in unchecked EmvMpmException, not raw SAXException")
    void loadFromInputStream_malformedXml_throwsEmvMpmException() {
        ByteArrayInputStream malformed = new ByteArrayInputStream("<mpmpackager".getBytes(StandardCharsets.UTF_8));

        EmvMpmException e = assertThrows(EmvMpmException.class, () -> EmvMpmPackager.of(malformed));
        assertInstanceOf(SAXException.class, e.getCause());
    }

    @Test
    @DisplayName("of(String path) wraps a missing file in unchecked EmvMpmException, not raw IOException")
    void loadFromPath_missingFile_throwsEmvMpmException() {
        EmvMpmException e =
                assertThrows(EmvMpmException.class, () -> EmvMpmPackager.of("no-such-file-emvmpm.xml"));
        assertInstanceOf(IOException.class, e.getCause());
    }
}
