package ense375.personalpasswordmanager.utils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base32;

public class HMACUtil {
	
	private static Base32 base32 = Base32.builder().get();

	private static ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(Long.BYTES);
	private static final int key_length = 64;
	
	public static int HOTP_SHA_1(String key, long offset, int num_digits) {
		byte[] value = HMAC_SHA_1(key, offset);
		byte byte_offset = (byte) (value[19] & 0x0F);
		byte[] truncated = {(byte) (value[byte_offset] & 0x7F), value[byte_offset + 1], value[byte_offset + 2], value[byte_offset + 3]};
		return ByteBuffer.wrap(truncated).getInt() % (int) Math.pow(10, num_digits);
	}

	public static byte[] HMAC_SHA_1(String key, long offset) {
		try {
			byte[] buffer_key = convertStringKey(key);
			byte[] buffer_inside = new byte[key_length];
			byte[] buffer_outside = new byte[key_length];
			for (int i = 0; i < key_length; i++) buffer_inside[i] = (byte) (buffer_key[i] ^ 0x36);
			for (int i = 0; i < key_length; i++) buffer_outside[i] = (byte) (buffer_key[i] ^ 0x5C);
			return MessageDigest.getInstance("SHA-1").digest(
					concatByteArrays(
							buffer_outside, 
							MessageDigest.getInstance("SHA-1").digest(
									concatByteArrays(
											buffer_inside,
											BYTE_BUFFER.putLong(offset).array()
									)
							)
					)
			);
			
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] convertStringKey(String key) throws NoSuchAlgorithmException {
		if (key.length() == key_length) return base32.decode(key);
		if (key.length() > key_length) {
			return Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(base32.decode(key)), key_length);
		}
		else return Arrays.copyOf(base32.decode(key), 64);
	}
	
	public static byte[] concatByteArrays(byte[] first, byte[] second) {
		byte[] output = Arrays.copyOf(first, first.length + second.length);
		for (int i = 0 ; i < second.length; i++)
			output[i + first.length] = second[i];
		return output;
	}
	
}
