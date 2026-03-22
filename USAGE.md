# emvmpm Usage Guide

EMV MPM (Merchant Presented Mode) QR code payload parser and builder for Java.

---

## Table of Contents

1. [Parsing a QR Code String](#1-parsing-a-qr-code-string)
2. [Navigating the Parsed Tree](#2-navigating-the-parsed-tree)
3. [Building a QR Code from Scratch](#3-building-a-qr-code-from-scratch)
4. [Loading a Schema (EmvMpmPackager)](#4-loading-a-schema-emvmpmpackager)
5. [CRC Calculation](#5-crc-calculation)
6. [Data Object Reference](#6-data-object-reference)

---

## 1. Parsing a QR Code String

### With a schema (recommended)

A schema tells the parser which tags contain nested TLV data (templates). Without one, template values are not recursively parsed.

```java
// 1. Load schema
EmvMpmPackager packager = new EmvMpmPackager();
packager.setEmvMpmPackager("emvmpm_bc.xml");
EmvMpmDefinition definition = packager.create();

// 2. Parse
String qrString = "000201010211...6304ABCD";
EmvMpmNode root = EmvMpmParser.parse(qrString, definition);
```

### Without a schema

Only top-level tags are parsed. Template values remain as raw strings.

```java
EmvMpmNode root = EmvMpmParser.parse(qrString);
```

> **Note:** `parse(qrString)` throws `RuntimeException` if the first tag id is `"10"` or greater (ambiguous without a schema).

---

## 2. Navigating the Parsed Tree

After parsing, the result is a tree of `EmvMpmNode`. Each node has an id, length, value, and optional children.

```java
// Check node type
root.isRoot();       // true for the root node
root.isTemplate();   // true if the node has children (non-root)
root.isPrimitive();  // true if the node is a leaf

// Find a direct child by id
Optional<EmvMpmNode> node26 = root.findChild("26");

// Find a node anywhere in the tree by canonical path
Optional<EmvMpmNode> node = root.find("/26/00");

// Get the canonical path of any node
String path = node.get().getCanonicalId(); // e.g. "/26/00"

// Read a node's value
String value = node.get().getData().getValue();

// Serialize back to QR string (round-trip)
String qrString = root.toQrCodeData();

// Serialize to hex-encoded UTF-8
String hex = root.toHexQrCodeData();
```

---

## 3. Building a QR Code from Scratch

### Create a simple QR payload

```java
EmvMpmNode root = EmvMpmNodeFactory.root();

// Predefined nodes
root.add(EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR)); // 000201
root.add(EmvMpmNodeFactory.staticPim());                                   // 010211
// or: EmvMpmNodeFactory.dynamicPim()                                      // 010212

// Primitive node: id + value (length is auto-calculated)
root.add(EmvMpmNodeFactory.createPrimitive("58", "KR")); // Country Code
root.add(EmvMpmNodeFactory.createPrimitive("59", "MERCHANT NAME"));
root.add(EmvMpmNodeFactory.createPrimitive("60", "SEOUL"));
root.add(EmvMpmNodeFactory.createPrimitive("52", "5812")); // MCC
root.add(EmvMpmNodeFactory.createPrimitive("53", "410"));  // Currency (KRW)

// Append CRC (must be called last — computes over everything before it)
root.markCrc();

String qrCode = root.toQrCodeData();
```

### Create a template node (nested TLV)

```java
List<EmvMpmNode> children = new ArrayList<>();
children.add(EmvMpmNodeFactory.createPrimitive("00", "D410000001401000")); // GUID
children.add(EmvMpmNodeFactory.createPrimitive("05", "1000058320"));       // Account

// createTemplate() auto-calculates the template's length and value
EmvMpmNode merchantInfo = EmvMpmNodeFactory.createTemplate("26", children);
root.add(merchantInfo);

// Add more children to a template after creation
merchantInfo.add(EmvMpmNodeFactory.createPrimitive("09", "0999"));
// Length and value of the template are recalculated automatically
```

### Build manually with EmvMpmDataObject

```java
// of(id, value)              — length is auto-calculated
// of(id, intLength, value)   — length is explicit (must be 0–100)
// of(id, stringLength, value)— raw constructor, no validation

EmvMpmDataObject data = EmvMpmDataObject.of("59", "MY STORE");
EmvMpmNode node = EmvMpmNodeFactory.of(data);
root.add(node);
```

---

## 4. Loading a Schema (EmvMpmPackager)

`EmvMpmPackager` supports four input sources:

```java
EmvMpmPackager packager = new EmvMpmPackager();

// From file path (relative to Maven working directory, i.e. project root)
packager.setEmvMpmPackager("emvmpm_bc.xml");

// From File object
packager.setEmvMpmPackager(new File("/path/to/schema.xml"));

// From InputStream (e.g. classpath resource)
InputStream is = getClass().getResourceAsStream("/emvmpm_bc.xml");
packager.setEmvMpmPackager(is);

// From DataObjectDef array (programmatic)
DataObjectDef[] fields = {
    new DataObjectDef("00", "Payload Format Indicator", DataObjectDef.Type.PRIMITIVE),
    new DataObjectDef("26", "Merchant Account Info", DataObjectDef.Type.TEMPLATE,
        Arrays.asList(
            new DataObjectDef("00", "GUID", DataObjectDef.Type.PRIMITIVE),
            new DataObjectDef("05", "Account", DataObjectDef.Type.PRIMITIVE)
        )),
    new DataObjectDef("63", "CRC", DataObjectDef.Type.PRIMITIVE),
};
packager.setEmvMpmPackager(fields);

EmvMpmDefinition definition = packager.create();
```

### XML schema format

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mpmpackager>
    <dataobject id="00" name="Payload Format Indicator" maxlength="02" type="primitive"/>
    <dataobject id="26" name="Merchant Account Information" maxlength="99" type="template">
        <dataobject id="00" name="Globally Unique Identifier" maxlength="32" type="primitive"/>
        <dataobject id="05" name="Payment Network Specific"   maxlength="09" type="primitive"/>
    </dataobject>
    <dataobject id="63" name="CRC" maxlength="04" type="primitive"/>
</mpmpackager>
```

### Querying a definition

```java
// Find a definition by canonical path
Optional<DataObjectDef> def = definition.find("/26/00");
def.get().getCanonicalId(); // "/26/00"
def.get().getDescription(); // "Globally Unique Identifier"
def.get().getMaxlength();   // 32
def.get().isTemplate();     // false

// Check if a path is a template
definition.isTemplate("/26");  // true
definition.isTemplate("/26/00"); // false
definition.isTemplate("/");    // false (root has no definition entry)
```

---

## 5. CRC Calculation

The CRC is CRC-16/CCITT (polynomial `0x1021`, initial value `0xFFFF`). The input must include the empty CRC placeholder `"6304"`.

```java
// Typically called via root.markCrc() — but can be called directly:
String payload = root.toQrCodeData() + "6304"; // append empty CRC tag
String crc = EmvMpmCRC.calculateEmvMpmCrc(payload, StandardCharsets.UTF_8);
// Returns 4-char uppercase hex, e.g. "1A2B"
```

`root.markCrc()` handles this automatically: it appends the `6304` placeholder, computes the CRC, fills in the value, and adds the node to the root.

---

## 6. Data Object Reference

### Predefined constants (`EmvMpmDataObject`)

| Constant | Tag | Value | Meaning |
|---|---|---|---|
| `PAYLOAD_FORMAT_INDICATOR` | `00` | `01` | EMV MPM v1 |
| `POINT_INITATION_METHOD_STATIC` | `01` | `11` | Static QR |
| `POINT_INITATION_METHOD_DYNAMIC` | `01` | `12` | Dynamic QR |

### Common EMV MPM tags (from `emvmpm_bc.xml`)

| Tag | Name | Max Length | Type |
|---|---|---|---|
| `00` | Payload Format Indicator | 2 | Primitive |
| `01` | Point of Initiation Method | 2 | Primitive |
| `26` | Merchant Account Information | 99 | Template |
| `52` | Merchant Category Code | 4 | Primitive |
| `53` | Transaction Currency | 3 | Primitive |
| `54` | Transaction Amount | 13 | Primitive |
| `58` | Country Code | 2 | Primitive |
| `59` | Merchant Name | 25 | Primitive |
| `60` | Merchant City | 15 | Primitive |
| `61` | Postal Code | 10 | Primitive |
| `62` | Additional Data Field Template | 99 | Template |
| `63` | CRC | 4 | Primitive |
| `64` | Merchant Information Language Template | 99 | Template |

### Canonical path format

Paths use `/` as delimiter, always starting with `/` for absolute paths:

```
/26       → top-level tag 26
/26/00    → tag 00 inside template 26
/62/50/00 → tag 00 inside template 50 inside template 62
```

`EmvMpmPaths.getEmvMpmPath(path)` normalizes any path string (trims trailing slash, collapses duplicates).