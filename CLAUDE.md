# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=EmvMpmCRCJUnit5Test

# Run a single test method
mvn test -Dtest=EmvMpmCRCJUnit5Test#calculateCrc_data1

# Package JAR
mvn package
```

Tests are run from the project root directory (`emvmpm/`). Tests that load XML packager definitions (e.g. `emvmpm_bc.xml`) use a relative path — Maven's working directory is the project root, so those paths resolve correctly during `mvn test`.

## Architecture

This library parses and builds EMV MPM (Merchant Presented Mode) QR code payloads following the EMVCo specification. The payload is a flat string of TLV (Tag-Length-Value) data objects, where some tags are "templates" that nest further TLV data in their value field.

### Core data model

- **`EmvMpmDataObject`** — a single TLV element with `id` (2-digit), `length` (2-digit, zero-padded), and `value`. `toEmvMpmData()` concatenates them. Factory method `of(id, value)` auto-computes length.
- **`EmvMpmNode`** — wraps an `EmvMpmDataObject` in a tree (parent + children list). A node is either ROOT (no parent, id="/"), TEMPLATE (has children, non-root), or PRIMITIVE (leaf). `toQrCodeData()` serializes the subtree back to the QR string. `markCrc()` appends and calculates the CRC-16/CCITT field (tag "63").
- **`EmvMpmNodeFactory`** — static factory for common nodes (root, crc, pim, primitive, template). `createTemplate()` auto-calculates the template's length and value from its children.

### Schema / definition layer

Before parsing, a schema is loaded to distinguish which tags are templates (requiring recursive parsing of their value).

- **`DataObjectDef`** — schema entry: id, description, maxlength, type (PRIMITIVE/TEMPLATE), children. `getCanonicalId()` returns a slash-delimited path like `/26/00`.
- **`EmvMpmDefinition`** — holds the flat list of root-level `DataObjectDef`s and searches them recursively by canonical path. `find("/26/00")` and `isTemplate("/26")` are the primary query methods.
- **`EmvMpmPackager`** — builds an `EmvMpmDefinition` from one of: XML file path (String), `File`, `InputStream`, `DataObjectDef[]`, or `List<DataObjectDef>`. The XML schema format is `<mpmpackager><dataobject id="..." name="..." maxlength="..." type="primitive|template">...</dataobject></mpmpackager>`. See `emvmpm_bc.xml` for a complete example (BC card network definition).

### Parsing flow

```
EmvMpmPackager → EmvMpmDefinition
EmvMpmParser.parse(qrString, definition) → EmvMpmNode (tree)
```

`EmvMpmParser` walks the flat QR string, splitting on 2-byte id + 2-byte length + N-byte value boundaries. For each node, it checks `EmvMpmDefinition.isTemplate()` via canonical path; if true, it recurses into the value string. `parse(qrString)` (no definition) parses only the top level (no template recursion).

### Path conventions

`EmvMpmPaths` normalizes canonical paths: always starts with `/`, no trailing slash, collapses duplicate separators. Paths uniquely identify nodes in the tree (e.g. `/62/50/00`).

### CRC

`EmvMpmCRC.calculateEmvMpmCrc(data, charset)` — CRC-16/CCITT (polynomial 0x1021, initial value 0xFFFF) over the UTF-8 bytes of the QR string up to and including the empty `6304` placeholder. Returns a 4-char uppercase hex string.

## Test structure

All tests use JUnit 5 (Jupiter). Test class naming: `JUnit5` suffix for classes that mirror a specific component (e.g. `EmvMpmCRCJUnit5Test`), no suffix for classes that are the sole test for a component (e.g. `EmvMpmDataObjectTest`).