package com.copanote.emvmpm.data;

import java.nio.charset.Charset;

public class EmvMpmCRC {
	
	/**
	 *   4.7.3 CRC (ID "63")
     *      4.7.3.1 The checksum shall be calculated according to [ISO/IEC 13239] 
     *      using the polynomial '1021' (hex) and initial value 'FFFF' (hex). 
	 */
	private static int HEX_POLYNOMIAL = 0x1021;
	private static int HEX_INITIAL_VALUE = 0xFFFF;
	
	public static String calculateEmvMpmCrc(String data, Charset charset) {
		
		byte[] ba = data.getBytes(charset);
		
		int crc16CCITT = crc16CCITT(ba, HEX_POLYNOMIAL, HEX_INITIAL_VALUE);
		
		return String.format("%04X", crc16CCITT);
	}
	
	private static int crc16CCITT(byte[] ba, int polynomial, int crc ) {
		for (byte b : ba) {
			for(int i = 0; i < 8; i++ ) {
				boolean bit = ((b >> ( 7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) ==1 );
				
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
