package com.deotis.digitalars.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoMd5 {

	public static String MD5Generator(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException { 
		
		MessageDigest mdMD5 = MessageDigest.getInstance("MD5"); 
		
		mdMD5.update(input.getBytes("UTF-8"));
		
		byte[] md5Hash = mdMD5.digest(); 
		
		StringBuilder hexMD5hash = new StringBuilder(); 
		
		for(byte b : md5Hash) { 
			String hexString = String.format("%02x", b);
			hexMD5hash.append(hexString); 
		} 
		return hexMD5hash.toString(); 
	}

}