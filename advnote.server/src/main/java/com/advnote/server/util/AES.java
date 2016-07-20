package com.advnote.server.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AES {
	private static final Logger logger = LoggerFactory.getLogger(AES.class);
	
	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	private static byte[] hexByte(final String encoded) {
	    if ((encoded.length() % 2) != 0)
	        throw new IllegalArgumentException("Input string must contain an even number of characters");

	    final byte result[] = new byte[encoded.length()/2];
	    final char enc[] = encoded.toCharArray();
	    for (int i = 0; i < enc.length; i += 2) {
	        StringBuilder curr = new StringBuilder(2);
	        curr.append(enc[i]).append(enc[i + 1]);
	        result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
	    }
	    return result;
	}

	public String encrypt(String pass, String message) throws Exception {
		try {
			byte[] keyMaterial = hexByte(pass);
			SecretKeySpec key = new SecretKeySpec(keyMaterial, "AES");
			// encrypting
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plaintext = message.getBytes("UTF-8");
			byte[] ciphertext = cipher.doFinal(plaintext);		
			return asHex(ciphertext);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "";
		}
		
	}

	public String decrypt(String pass, String message) throws Exception {
		try {
			byte[] keyMaterial = hexByte(pass);
			SecretKeySpec key = new SecretKeySpec(keyMaterial, "AES");
			// decrypting
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] original = cipher.doFinal(hexByte(message));
			String originalString = new String(original,"UTF-8");		
			return originalString;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "";
		}
		
		
	}
}
