package com.copanote.emvmpm;

/**
 * EMV MPM 데이터 파싱에 실패했을 때 던져지는 unchecked 예외.
 *
 * <p>원시 EMV MPM 데이터가 스펙(ID-Length-Value 구조)에 맞지 않거나, 주어진 {@link
 * com.copanote.emvmpm.definition.EmvMpmDefinition}에 정의되지 않은 태그가 발견되는 등, {@link
 * com.copanote.emvmpm.parser.EmvMpmParser}가 데이터를 파싱하는 과정에서 실패하는 경우에 사용한다.
 */
public class EmvMpmException extends RuntimeException {

    public EmvMpmException(String message) {
        super(message);
    }

    public EmvMpmException(String message, Throwable cause) {
        super(message, cause);
    }
}
