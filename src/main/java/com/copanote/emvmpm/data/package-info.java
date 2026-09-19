/**
 * EMV MPM QR 코드의 런타임 TLV(ID-Length-Value) 트리를 표현하고 조작하는 클래스들.
 *
 * <p>{@link com.copanote.emvmpm.data.EmvMpmDataObject}는 단일 ID/Length/Value 트리플을,
 * {@link com.copanote.emvmpm.data.EmvMpmNode}는 그것을 트리 형태로 감싼 노드를 나타낸다. 노드/트리
 * 생성은 {@link com.copanote.emvmpm.data.EmvMpmNodeFactory}를 통해 하는 것을 권장한다.
 */
package com.copanote.emvmpm.data;
