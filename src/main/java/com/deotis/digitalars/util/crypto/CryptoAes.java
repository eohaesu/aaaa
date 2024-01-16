package com.deotis.digitalars.util.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;

public class CryptoAes {

	public static String Decrypt(String text, String key, boolean urlEnc)
			throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

		SecureRandom random = new SecureRandom();
	    byte[] bytesIV = new byte[16];
	    random.nextBytes(bytesIV); // Random initialization vector

		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
		GCMParameterSpec ivSpec = new GCMParameterSpec(128, bytesIV);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(urlEnc ? java.util.Base64.getUrlDecoder().decode(text) : Base64.getDecoder().decode(text));

		return new String(results, "UTF-8");


	}

	public static String Encrypt(String text, String key, boolean urlEnc)
			throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecureRandom random = new SecureRandom();
	    byte[] bytesIV = new byte[16];
	    random.nextBytes(bytesIV); // Random initialization vector

		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
		GCMParameterSpec ivSpec = new GCMParameterSpec(128, bytesIV);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

		return urlEnc ? new String(Base64.getUrlEncoder().encode(results), "UTF-8") : Base64.getEncoder().encodeToString(results);

	}
	
	//MYSQL aes_encrypt 전용
	public static String EncryptMySql(String text, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{

		Cipher cipher = Cipher.getInstance("AES");
		
		byte[] keyBytes = new byte[16];
		int i = 0;
		
		for(byte b : key.getBytes()) {
			keyBytes[i++%16] ^= b;
		}
		
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));

		return new String(Hex.encode(cipher.doFinal(text.getBytes("UTF-8")))).toUpperCase();
	}
	//MYSQL aes_decrypt 전용
	public static String DecryptMySql(String text, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance("AES");
		
		byte[] keyBytes = new byte[16];
		int i = 0;
		
		for(byte b : key.getBytes()) {
			keyBytes[i++%16] ^= b;
		}
		
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));

		return new String(cipher.doFinal(Hex.decode(text)));
	}

}