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
	private Cipher cipher;
	
	public static PrimaryKey derriveKey(String password) {
		if (password == null) return null;
		return new PrimaryKey(password);
	}
	
	private PrimaryKey(String userpassword) {
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH);
			
			digest.update(userpassword.getBytes());
			
			primaryKey = new SecretKeySpec(digest.digest(), CIPHER_METHOD);
			cipher = Cipher.getInstance(CIPHER_METHOD);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public String encrypt(String input) {
		if (input == null) return null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, primaryKey);
			byte[] buffer = encoder.encode(cipher.doFinal(input.getBytes()));
			return new String(buffer);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
		} catch (InvalidKeyException e) {
		}
		
		return null;
	}
	
	public String decrypt(String input) {
		if (input == null) return null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, primaryKey);
			byte[] buffer = cipher.doFinal(decoder.decode(input.getBytes()));
			return new String(buffer);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
		} catch (InvalidKeyException e) {
		}
		
		return null;
	}
}
