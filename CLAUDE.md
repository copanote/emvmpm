# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 개요

`emvmpm`은 EMV QR Code Specification for Payment Systems — Merchant Presented Mode (MPM)를 구현한 Java 라이브러리입니다. EMV MPM QR 코드에서 사용되는 TLV(tag-length-value, 여기서는 ID-Length-Value / "ILV"라고 부름) 데이터 구조를 파싱하고 생성하며, 어떤 태그가 template(중첩 TLV)이고 어떤 태그가 primitive인지 알기 위해 플러그인 방식의 XML 기반 필드 정의("packager")를 사용합니다.

## 빌드 & 테스트 명령어

Maven, Java 8 타겟.

```bash
mvn compile              # 컴파일
mvn test                 # 전체 테스트 실행
mvn test -Dtest=EmvMpmParserTest             # 단일 테스트 클래스 실행
mvn test -Dtest=EmvMpmParserTest#testParse   # 단일 테스트 메서드 실행
mvn package              # target/emvmpm-0.1.0.jar 빌드
```

일부 packager 테스트(`EmvMpmPackagerTest`)는 상대 경로(`new File("emvmpm_bc.xml")`)를 통해 `emvmpm_bc.xml`을 로드하므로, 반드시 리포 루트를 작업 디렉터리로 하여 테스트를 실행해야 합니다(Maven의 기본 `mvn test`는 이 조건을 만족합니다).

## 아키텍처

코드는 `com.copanote.emvmpm` 아래 세 개의 패키지로 구성되어 있으며, **definition → parse → data tree**로 이어지는 파이프라인을 형성합니다.

### `data` — 런타임 TLV 트리
- `EmvMpmDataObject`: 단일 ID/Length/Value 트리플(원시 ILV 단위). 스펙에 따라 id와 length는 두 자리 숫자 문자열입니다. 잘 알려진 static 상수(`ROOT`, `PAYLOAD_FORMAT_INDICATOR` 등)를 가지고 있습니다.
- `EmvMpmNode`: `EmvMpmDataObject`를 트리(parent/children) 형태로 감쌉니다. 자식이 있으면 **template**, 없으면 **primitive**, 부모 없이 특수한 `EmvMpmDataObject.ROOT` sentinel을 가지면 **root**입니다. template은 `add()`가 호출될 때마다 자신의 length/value를 자식들과 재귀적으로 동기화할 책임이 있습니다. 주요 연산: `find`/`findChild`(canonical path로 순회), `getCanonicalId()`(root로부터의 경로, 예: `/26/00`), `toQrCodeData()`/`toHexQrCodeData()`(트리를 EMV MPM 문자열/hex 포맷으로 직렬화), `markCrc()`(스펙 §4.7.3에 따라 후행 CRC("63") 필드를 계산하여 추가).
- `EmvMpmNodeFactory`: 노드/트리를 생성하는 권장 방법(`root()`, `createPrimitive()`, `createTemplate()`, `dynamicPim()`/`staticPim()`/`emptyCrc()` 같은 잘 알려진 노드들).
- `EmvMpmPaths`: data tree와 definition tree가 공유하는 canonical path 파싱/포맷팅 유틸리티(`/`로 구분, 예: `/62/50/00`).
- `EmvMpmCRC`: `EmvMpmNode.markCrc()`에서 사용하는 독립적인 CRC-16/CCITT(다항식 `0x1021`, 초기값 `0xFFFF`) 구현체.

### `definition` — 태그의 의미를 설명하는 스키마
- `DataObjectDef`: 하나의 필드에 대한 스키마 항목 — id, description, maxlength, `Type`(`PRIMITIVE`/`TEMPLATE`), 그리고(template인 경우) 자식 `DataObjectDef`들. `EmvMpmNode`/`EmvMpmDataObject`와 형태는 같지만 데이터가 아닌 스키마를 나타냅니다.
- `EmvMpmDefinition`: 변경 불가능하고 검색 가능한 `DataObjectDef` 컬렉션(`EmvMpmDefinition.of(...)`로 생성), canonical path(`find("/26/00")`)로 조회합니다.
- `definition.packager.EmvMpmPackager`: XML(`<mpmpackager>` 루트, 중첩된 `<dataobject id maxlength type>` 엘리먼트 — 특정 카드 스킴에 대한 스키마 예시는 리포 루트의 `emvmpm_bc.xml` 참고)로부터 `EmvMpmDefinition`을 생성하는 불변 클래스입니다. `String` 경로, `File`, `InputStream`, 또는 프로그래밍 방식의 `DataObjectDef[]`/`List<DataObjectDef>` 중 하나의 소스를 정적 팩토리 메서드 `of(...)`로 선택해 인스턴스를 생성합니다.

### `parser`
- `EmvMpmParser.parse(data, definition)`: 원시 EMV MPM 데이터 문자열을 `EmvMpmNode` 트리로 파싱하며, 각 레벨에서 `EmvMpmDefinition`을 참조하여 해당 태그의 값을 template으로 재귀 파싱할지 primitive의 원시 값으로 유지할지 판단합니다.
- `EmvMpmParser.parse(data)`: definition 없이 파싱합니다(모든 것을 flat/primitive로 취급 — template으로의 재귀 없음).

### 데이터 흐름

1. 스키마 로드: `EmvMpmPackager.of(xmlFileOrStream)` → `.create()` → `EmvMpmDefinition`.
2. 해당 definition을 기준으로 QR payload 문자열 파싱: `EmvMpmParser.parse(rawData, definition)` → `EmvMpmNode` 트리.
3. `EmvMpmNode.find("/canonical/path")`로 트리를 순회/조회하거나, `toQrCodeData()`/`toHexQrCodeData()`로 다시 직렬화.
4. 파싱 대신 트리를 프로그래밍 방식으로 만들려면 `EmvMpmNodeFactory`(`createPrimitive`/`createTemplate`/`root`)를 사용하고, `node.add(child)`로 자식을 붙인 뒤(부모 template의 length/value가 재계산됨), `node.markCrc()`로 마무리합니다.

참고: `EmvMpmDataObject`/`EmvMpmNode`(런타임 데이터)와 `DataObjectDef`(스키마)는 병렬 구조를 가집니다 — 둘 다 동일한 canonical-path 주소 체계(`EmvMpmPaths`)를 사용하지만, 그 외에는 독립적인 객체 그래프입니다.
