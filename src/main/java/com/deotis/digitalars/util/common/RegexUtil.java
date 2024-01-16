package com.deotis.digitalars.util.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author jongjin
 * @description : validate by regex pattern utility
 *
 */

public class RegexUtil{
	
	private final static int[] appArr = {2,3,4,5,6,7,8,9,2,3,4,5};
    
	/**
	 * IMG : 이미지
	 * SSN : 주민번호
	 * FAX : FAX번호
	 * CELL : 휴대폰
	 * EMAIL : 이메일
	 * BIRTH : 생년월일
	 */
	public static enum Patterns{
		
		IMG("([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)"),
		SSN("\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])[1-4]\\d{6}$"),
		FAX("^(070|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$"),
		CELL("^(01[016789]{1})-?[0-9]{3,4}-?[0-9]{4}$"),
		EMAIL("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-z]{2,3}$"),
		BIRTH("^([0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1]))$");
		
		private String pattern;

		Patterns(String pattern) {
			this.pattern = pattern;
		}
		
		public String getPattern() {
			return pattern;
		}
	}

	/**
	 * Validate pattern by regEx
	 * @param str : compare string
	 * @param pt : execute pattern
	 * @see : RegexUtil.patternValidate("jongjin.kim@deotis.co.kr", RegexUtil.Patterns.EMAIL);
	 * @return boolean
	 */
	public static boolean patternValidate(final String str, final Patterns pt){
		
		Pattern pattern = Pattern.compile(pt.getPattern()); 
		Matcher matcher = pattern.matcher(str);
		
		return matcher.matches();
		    	    
	}
	
	/**
	 * Validate credit card by luhn algorithm
	 * @param cardNumber
	 * @return boolean
	 */
	public static boolean creditCardCheck(String cardNumber) {

		cardNumber = cardNumber.indexOf("-") > 0 ? cardNumber.replaceAll("-", "") : cardNumber;
		
		int sum = 0;
		
		for(int i=cardNumber.length()-1; i>=0; i--) {
			
			int digit = Integer.parseInt(cardNumber.substring(i, i+1));
			
			if((cardNumber.length()-i % 2 == 0)) {
				digit = (digit * 2) > 9 ? digit - 9 : digit * 2; 
			}
			
			sum += digit;
		}
		
		return sum > 0 && sum % 10 == 0;	
	}

	/**
	 * Validate ssn validate
	 * @param ssn
	 * @return boolean
	 */
	public static boolean ssnValidateCheck(String ssn) {

		if(ssn.length() != 13) return false;

		int sum = 0;
		
		for(int i=0; i<ssn.length()-1; i++) {
			sum += appArr[i] * Integer.parseInt(ssn.substring(i, i+1));
		}
		
		sum = 11 - sum % 11;
		
		if(sum > 9) sum = sum % 10;

		return sum == Integer.parseInt(ssn.substring(ssn.length()-1)) ? true : false; 
	}
}