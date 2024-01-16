package com.deotis.digitalars.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class CryptoRSA {

	private PrivateKey privateKey;
	private PublicKey publicKey;
	private RSAPublicKeySpec publicSpec;

	private String publicKeyModulus;
	private String publicKeyExponent;

	//공개키 생성자
	public CryptoRSA() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

		KeyPair keyPair = generator.genKeyPair();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();

		// 공개키를 문자열로 변환
		publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

		publicKeyModulus = publicSpec.getModulus().toString(16);
		publicKeyExponent = publicSpec.getPublicExponent().toString(16);
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public RSAPublicKeySpec getPublicSpec() {
		return publicSpec;
	}

	public String getPublicKeyModulus() {
		return publicKeyModulus;
	}

	public String getPublicKeyExponent() {
		return publicKeyExponent;
	}

	//복호화
	public static String decryptRsa(PrivateKey privateKey, String securedValue)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
		
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] encryptedBytes = hexToByteArray(securedValue);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8"); 
																	
		return decryptedValue;
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}
		return bytes;
	}
}
