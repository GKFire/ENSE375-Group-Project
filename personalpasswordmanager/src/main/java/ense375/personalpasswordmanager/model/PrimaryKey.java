package ense375.personalpasswordmanager.model;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class PrimaryKey {
	
	private static final String HASH = "SHA-256";
	private static final String CIPHER_METHOD = "AES";
	private static Decoder decoder = Base64.getDecoder();
	private static Encoder encoder = Base64.getEncoder();
	
	private Key primaryKey;
	private Cipher primary;
	
	public PrimaryKey(String userpassword) {
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH);
			
			digest.update(userpassword.getBytes());
			
			primaryKey = new SecretKeySpec(digest.digest(), CIPHER_METHOD);
			primary = Cipher.getInstance("AES");
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public String encrypt(String input) {
		try {
			primary.init(Cipher.ENCRYPT_MODE, primaryKey);
			byte[] buffer = primary.doFinal(input.getBytes());
			return new String(encoder.encode(buffer));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String decrypt(String input) {
		try {
			primary.init(Cipher.DECRYPT_MODE, primaryKey);
			byte[] buffer = primary.doFinal(decoder.decode(input.getBytes()));
			return new String(buffer);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
