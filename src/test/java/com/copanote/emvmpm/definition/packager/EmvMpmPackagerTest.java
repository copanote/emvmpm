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
    @DisplayName("setEmvMpmPackager(String path) loads definition from file path")
    void loadFromPath_createsDefinition() throws Exception {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager("emvmpm_bc.xml");
        EmvMpmDefinition def = packager.create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("setEmvMpmPackager(File) loads definition from File object")
    void loadFromFile_createsDefinition() throws Exception {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager(new File("emvmpm_bc.xml"));
        EmvMpmDefinition def = packager.create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("setEmvMpmPackager(InputStream) loads definition from InputStream")
    void loadFromInputStream_createsDefinition() throws Exception {
        EmvMpmPackager packager = new EmvMpmPackager();
        File file = new File("emvmpm_bc.xml");
        packager.setEmvMpmPackager(Files.newInputStream(file.toPath()));
        EmvMpmDefinition def = packager.create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("setEmvMpmPackager(array) loads definition from DataObjectDef array")
    void loadFromArray_createsDefinition() {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager(MINIMAL_FIELDS);
        EmvMpmDefinition def = packager.create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("setEmvMpmPackager(list) loads definition from DataObjectDef list")
    void loadFromList_createsDefinition() {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager(Arrays.asList(MINIMAL_FIELDS));
        EmvMpmDefinition def = packager.create();
        assertNotNull(def);
    }

    @Test
    @DisplayName("path and file sources produce equivalent definitions for '/26'")
    void pathAndFile_produceEquivalentDefinitions() throws Exception {
        EmvMpmPackager fromPath = new EmvMpmPackager();
        fromPath.setEmvMpmPackager("emvmpm_bc.xml");
        EmvMpmDefinition defPath = fromPath.create();

        EmvMpmPackager fromFile = new EmvMpmPackager();
        fromFile.setEmvMpmPackager(new File("emvmpm_bc.xml"));
        EmvMpmDefinition defFile = fromFile.create();

        assertEquals(defPath.isTemplate("/26"), defFile.isTemplate("/26"));
        assertEquals(defPath.find("/26/00").isPresent(), defFile.find("/26/00").isPresent());
    }

    @Test
    @DisplayName("array-based definition finds nested path '/26/00'")
    void arrayDefinition_findsNestedPath() {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager(MINIMAL_FIELDS);
        EmvMpmDefinition def = packager.create();

        assertTrue(def.find("/26/00").isPresent());
        assertEquals("/26/00", def.find("/26/00").get().getCanonicalId());
    }

    @Test
    @DisplayName("array-based definition: '/26' is template, '/26/00' is not template")
    void arrayDefinition_templateFlags() {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager(MINIMAL_FIELDS);
        EmvMpmDefinition def = packager.create();

        assertTrue(def.isTemplate("/26"));
        assertFalse(def.isTemplate("/26/00"));
        assertFalse(def.isTemplate("/00"));
    }

    @Test
    @DisplayName(
            "setEmvMpmPackager(InputStream) wraps malformed XML in unchecked EmvMpmException, not raw SAXException")
    void loadFromInputStream_malformedXml_throwsEmvMpmException() {
        EmvMpmPackager packager = new EmvMpmPackager();
        ByteArrayInputStream malformed = new ByteArrayInputStream("<mpmpackager".getBytes(StandardCharsets.UTF_8));

        EmvMpmException e =
                assertThrows(EmvMpmException.class, () -> packager.setEmvMpmPackager(malformed));
        assertInstanceOf(SAXException.class, e.getCause());
    }

    @Test
    @DisplayName("setEmvMpmPackager(String path) wraps a missing file in unchecked EmvMpmException, not raw IOException")
    void loadFromPath_missingFile_throwsEmvMpmException() {
        EmvMpmPackager packager = new EmvMpmPackager();

        EmvMpmException e = assertThrows(
                EmvMpmException.class, () -> packager.setEmvMpmPackager("no-such-file-emvmpm.xml"));
        assertInstanceOf(IOException.class, e.getCause());
    }
}
