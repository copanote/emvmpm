package com.copanote.emvmpm.data;

import java.nio.charset.Charset;

/**
 * EMV MPM 스펙 §4.7.3에서 정의한 CRC-16/CCITT(다항식 {@code 0x1021}, 초기값 {@code 0xFFFF})를 계산하는
 * 독립적인 유틸리티. {@link EmvMpmNode#markCrc()}가 내부적으로 사용한다.
 */
public class EmvMpmCRC {

    /** 인스턴스화를 막는 private 생성자. 이 클래스의 모든 멤버는 static이다. */
    private EmvMpmCRC() {}

    /**
     *   4.7.3 CRC (ID "63")
     *      4.7.3.1 The checksum shall be calculated according to [ISO/IEC 13239]
     *      using the polynomial '1021' (hex) and initial value 'FFFF' (hex).
     */
    private static final int HEX_POLYNOMIAL = 0x1021;

    private static final int HEX_INITIAL_VALUE = 0xFFFF;

    /**
     * 주어진 문자열을 charset으로 인코딩한 뒤 EMV MPM CRC-16/CCITT 값을 계산한다.
     *
     * @param data CRC를 계산할 원시 문자열 (CRC 태그 자체까지 포함해야 한다)
     * @param charset 문자열을 바이트로 인코딩할 때 사용할 charset
     * @return 네 자리 대문자 16진수 CRC 문자열
     */
    public static String calculateEmvMpmCrc(String data, Charset charset) {

        byte[] ba = data.getBytes(charset);

        int crc16CCITT = crc16CCITT(ba, HEX_POLYNOMIAL, HEX_INITIAL_VALUE);

        return String.format("%04X", crc16CCITT);
    }

    private static int crc16CCITT(byte[] ba, int polynomial, int crc) {
        for (byte b : ba) {
            for (int i = 0; i < 8; i++) {

                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);

                crc <<= 1;

                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }

        crc &= 0xFFFF;
        return crc;
    }
}
