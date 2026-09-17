# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`emvmpm` is a Java library implementing the EMV QR Code Specification for Payment Systems — Merchant Presented Mode (MPM). It parses and builds the TLV (tag-length-value, here called ID-Length-Value / "ILV") data structures used in EMV MPM QR codes, using a pluggable XML-based field definition ("packager") to know which tags are templates (nested TLV) vs primitives.

## Build & Test Commands

Maven, Java 8 target.

```bash
mvn compile              # compile
mvn test                 # run all tests
mvn test -Dtest=EmvMpmParserTest             # run a single test class
mvn test -Dtest=EmvMpmParserTest#testParse   # run a single test method
mvn package              # build target/emvmpm-0.1.0.jar
```

Some packager tests (`EmvMpmPackagerTest`) load `emvmpm_bc.xml` via a relative path (`new File("emvmpm_bc.xml")`), so tests must be run with the repo root as the working directory (Maven's default `mvn test` does this correctly).

## Architecture

The code is organized into three packages under `com.copanote.emvmpm`, forming a pipeline: **definition → parse → data tree**.

### `data` — the runtime TLV tree
- `EmvMpmDataObject`: a single ID/Length/Value triple (the raw ILV unit). Ids and lengths are two-digit numeric strings per spec. Has well-known static constants (`ROOT`, `PAYLOAD_FORMAT_INDICATOR`, etc).
- `EmvMpmNode`: wraps a `EmvMpmDataObject` in a tree (parent/children). A node is a **template** if it has children, a **primitive** if it doesn't, and the **root** if it's the special `EmvMpmDataObject.ROOT` sentinel with no parent. Templates are recursively responsible for keeping their own length/value in sync with their children whenever `add()` is called. Key operations: `find`/`findChild` (traverse by canonical path), `getCanonicalId()` (path from root, e.g. `/26/00`), `toQrCodeData()`/`toHexQrCodeData()` (serialize the tree back to the EMV MPM string/hex format), `markCrc()` (computes and appends the trailing CRC ("63") field per spec §4.7.3).
- `EmvMpmNodeFactory`: preferred way to construct nodes/trees (`root()`, `createPrimitive()`, `createTemplate()`, well-known nodes like `dynamicPim()`/`staticPim()`/`emptyCrc()`).
- `EmvMpmPaths`: canonical path parsing/formatting utility (`/`-delimited, e.g. `/62/50/00`) shared by both the data tree and the definition tree.
- `EmvMpmCRC`: standalone CRC-16/CCITT (polynomial `0x1021`, init `0xFFFF`) implementation used by `EmvMpmNode.markCrc()`.

### `definition` — the schema describing what tags mean
- `DataObjectDef`: one field's schema entry — id, description, maxlength, `Type` (`PRIMITIVE`/`TEMPLATE`), and (for templates) child `DataObjectDef`s. Mirrors the shape of `EmvMpmNode`/`EmvMpmDataObject` but for schema instead of data.
- `EmvMpmDefinition`: an immutable, searchable collection of `DataObjectDef`s (built via `EmvMpmDefinition.of(...)`), looked up by canonical path (`find("/26/00")`).
- `definition.packager.EmvMpmPackager`: builds an `EmvMpmDefinition` from XML (`<mpmpackager>` root, nested `<dataobject id maxlength type>` elements — see `emvmpm_bc.xml` at repo root for an example schema for a specific card scheme). Accepts a `String` path, `File`, or `InputStream`, or a programmatic `DataObjectDef[]`/`List<DataObjectDef>`.

### `parser`
- `EmvMpmParser.parse(data, definition)`: parses a raw EMV MPM data string into an `EmvMpmNode` tree, consulting the `EmvMpmDefinition` at each level to decide whether a given tag's value should be recursively parsed as a template or kept as a primitive's raw value.
- `EmvMpmParser.parse(data)`: parses without a definition (everything is treated as flat/primitive — no recursion into templates).

### Data flow

1. Load a schema: `new EmvMpmPackager().setEmvMpmPackager(xmlFileOrStream)` → `.create()` → `EmvMpmDefinition`.
2. Parse a QR payload string against that definition: `EmvMpmParser.parse(rawData, definition)` → `EmvMpmNode` tree.
3. Traverse/query the tree with `EmvMpmNode.find("/canonical/path")`, or serialize it back out with `toQrCodeData()`/`toHexQrCodeData()`.
4. To build a tree programmatically instead of parsing, use `EmvMpmNodeFactory` (`createPrimitive`/`createTemplate`/`root`), attach children with `node.add(child)` (which recalculates the parent template's length/value), and finish with `node.markCrc()`.

Note the parallel structure: `EmvMpmDataObject`/`EmvMpmNode` (runtime data) vs `DataObjectDef` (schema) — both use the same canonical-path addressing (`EmvMpmPaths`) but are otherwise independent object graphs.
