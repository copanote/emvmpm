package com.copanote.emvmpm.definition;

import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmvMpmDefinition")
class EmvMpmDefinitionJUnit5Test {

    static EmvMpmDefinition definition;

    @BeforeAll
    static void loadDefinition() throws Exception {
        EmvMpmPackager packager = new EmvMpmPackager();
        packager.setEmvMpmPackager("emvmpm_bc.xml");
        definition = packager.create();
    }

    // ── find() ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("find('/') returns empty - root has no definition entry")
    void find_rootPath_isEmpty() {
        assertTrue(definition.find("/").isEmpty());
    }

    @Test
    @DisplayName("find('/00') returns definition with canonicalId '/00'")
    void find_00() {
        Optional<DataObjectDef> result = definition.find("/00");
        assertTrue(result.isPresent());
        assertEquals("/00", result.get().getCanonicalId());
    }

    @Test
    @DisplayName("find('/26') returns template definition")
    void find_26_isTemplate() {
        Optional<DataObjectDef> result = definition.find("/26");
        assertTrue(result.isPresent());
        assertEquals("/26", result.get().getCanonicalId());
        assertTrue(result.get().isTemplate());
    }

    @Test
    @DisplayName("find('/26/00/') strips trailing slash and returns definition")
    void find_26_00_withTrailingSlash() {
        Optional<DataObjectDef> result = definition.find("/26/00/");
        assertTrue(result.isPresent());
        assertEquals("/26/00", result.get().getCanonicalId());
    }

    @Test
    @DisplayName("find('/26/01') returns empty - non-existent definition")
    void find_26_01_isEmpty() {
        assertTrue(definition.find("/26/01").isEmpty());
    }

    @Test
    @DisplayName("find('/62/50/00') returns deeply nested definition")
    void find_deeplyNested() {
        Optional<DataObjectDef> result = definition.find("/62/50/00");
        assertTrue(result.isPresent());
        assertEquals("/62/50/00", result.get().getCanonicalId());
    }

    @Test
    @DisplayName("find('/64') returns template definition")
    void find_64_isTemplate() {
        Optional<DataObjectDef> result = definition.find("/64");
        assertTrue(result.isPresent());
        assertTrue(result.get().isTemplate());
    }

    @Test
    @DisplayName("find with invalid path prefix returns empty")
    void find_invalidPath_isEmpty() {
        assertTrue(definition.find("123/111").isEmpty());
    }

    // ── isTemplate() ──────────────────────────────────────────────────────

    @ParameterizedTest(name = "path={0} => isTemplate={1}")
    @CsvSource({
        "/,       false",
        "/00,     false",
        "/26,     true",
        "/26/00/, false",
        "/26/01/, false",
        "/26/01,  false",
        "/62/50/00, false",
        "/64,     true"
    })
    @DisplayName("isTemplate() returns correct value for known paths")
    void isTemplate_parameterized(String path, boolean expected) {
        assertEquals(expected, definition.isTemplate(path.trim()));
    }

    // ── printDefinition() ─────────────────────────────────────────────────

    @Test
    @DisplayName("printDefinition() returns non-empty string")
    void printDefinition_nonEmpty() {
        String def = definition.printDefinition();
        assertNotNull(def);
        assertFalse(def.isEmpty());
    }
}