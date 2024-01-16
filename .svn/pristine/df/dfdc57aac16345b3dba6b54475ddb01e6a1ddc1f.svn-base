package com.deotis.digitalars.util.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoSha {
	/**
	 * SHA 암호화
	 * @param encryptionName 암호화종류 (SHA-1, SHA-256, SHA-512)
	 * @param beforeEncValue 암호화 하려는 Value
	 * @return
	 */
	public static String Encrypt(String encryptionName, String beforeEncValue) {
		String EncryptValue ="";		//암호화 적용완료한 값

		try{
			MessageDigest sh = MessageDigest.getInstance(encryptionName);
			sh.update(beforeEncValue.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			EncryptValue = sb.toString();

		}catch(NoSuchAlgorithmException e){
			EncryptValue = null;
		}
		return EncryptValue;
	}
}