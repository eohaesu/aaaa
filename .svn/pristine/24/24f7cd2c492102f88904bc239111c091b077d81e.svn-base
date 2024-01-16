package com.deotis.digitalars.util.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.common.LogData;
import com.deotis.digitalars.system.handler.SessionHandler;

public class CommonUtil {

	// TLO 로그 데이터 셋팅
	public static LogData setTloLog(String apiCd, String reqTime, String rspTime, String resultCd, String apiRsp, String errMsg) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		LogData result = null;
		
		if(entity != null) {
			entity.getLogData().setModuleApi(apiCd);
			if(!"".equals(reqTime)) { entity.getLogData().setReqTime(reqTime); }
			if(!"".equals(rspTime)) { entity.getLogData().setRspTime(rspTime); }
			entity.getLogData().setResultCode(resultCd);
			entity.getLogData().setModuleRsp(apiRsp);
			entity.getLogData().setErrorMsg(errMsg);
			
			result = entity.getLogData();
		} else {
			LogData logData = new LogData();
			logData.setModuleApi(apiCd);
			logData.setReqTime(reqTime);
			logData.setRspTime(rspTime);
			logData.setResultCode(resultCd);
			logData.setModuleRsp(apiRsp);
			logData.setErrorMsg(errMsg);
			try {
				logData.setHostName(InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				// HostName null
			}
			result = logData;
		}
		
		return result;
	}
	
	// 단말 타입 조회
	public static String getDeviceInfo(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent").toUpperCase();
		
		// 접속단말타입
	    if(userAgent.indexOf("MOBI") > -1) {
	         return "MOBILE";
	    } else {
	    	return "PC";
	    }
	}
	
	// 휴대폰번호 UPLUS APIM에서 쓰이는 형태로 변경 ex) 010012345678
	public static String phoneUplusFormat(String phoneNo) {
		
		String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";

		Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
		if (matcher.find()) {

			String[] phoneArr = new String[3];
			phoneArr[0] = matcher.group(1);  // ex) 010
			phoneArr[1] = matcher.group(2);  // ex) 1234
			phoneArr[2] = matcher.group(3);  // ex) 1234
			
			phoneNo = phoneArr[0] + "0" + phoneArr[1] + phoneArr[2];
			
			return phoneNo;

		}
		
		return phoneNo;
	}
	
	// 휴대폰번호 마스킹 (국번 4자리 마스킹, 010-****-5678)
	public static String phonePartlyMasking(String phoneNo, String type) {

		// 휴대폰번호가 010012345678 형태인 경우 type에 uplus 지정 필요
		if("uplus".equals(type)) {	
			phoneNo = phoneNo.substring(0,3) + phoneNo.substring(4);
		}
		
		String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";

		Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
		if (matcher.find()) {

			String[] phoneArr = new String[3];
			phoneArr[0] = matcher.group(1);  // ex) 010
			phoneArr[1] = matcher.group(2);  // ex) 1234
			phoneArr[2] = matcher.group(3);  // ex) 1234
			
			char[] phoneCh = phoneArr[1].toCharArray();
			
			for(int c=0; c<phoneCh.length; c++) {
				phoneCh[c] = '*';
			}
			
			phoneNo = phoneArr[0] + "-" + String.valueOf(phoneCh) + "-" + phoneArr[2];
			
			return phoneNo;

		}

		return phoneNo;

	}
	
	// 가입번호 마스킹 (뒤에 4자리 제외 모두 마스킹, ********1234)
	public static String entrIdMasking(String entrId) {
		
		String nonMask = entrId.substring(entrId.length()-4);  // 마지막 4자리
		
		String[] maskArr = new String[entrId.length()-4];
		Arrays.setAll(maskArr, i -> "*");
		
		String masked = "";
		for(String str : maskArr) {
			masked += str;
		}
		
		String maskEntrId = masked + nonMask;
		
		return maskEntrId; 
		
	}
	
	// 카드번호,계좌번호 마스킹 (앞 3자리, 뒤 4자리 제외 모두 마스킹, 123*-****-****-1234 or 123*****1234)
	public static String payMthdNumMasking(String payMethodNum, String type) {
		
		String[] nonMask = new String[2];
		nonMask[0] = payMethodNum.substring(0,3);
		nonMask[1] = payMethodNum.substring(payMethodNum.length()-4, payMethodNum.length());
		
		String[] maskArr = new String[payMethodNum.length()-7];
		Arrays.setAll(maskArr, i -> "*");
		
		String masked= "";
		for(String str : maskArr) {
			masked += str;
		}
		
		String maskPayMethodNum = nonMask[0] + masked + nonMask[1];
		
		if("card".equals(type)) {
			maskPayMethodNum = maskPayMethodNum.substring(0,4) + "-" + maskPayMethodNum.substring(4,8) + "-" + maskPayMethodNum.substring(8,12) + "-" + maskPayMethodNum.substring(12,maskPayMethodNum.length());
		}
		
		return maskPayMethodNum;
	}
	
	// 이름 마스킹 (성 뒤 이름의 첫자리 마스킹, 영문인 경우 앞뒤 각2자리 제외 마스킹)
	public static String nameMasking(String name) {
		
		String maskedName = "";
		
		if(Pattern.matches("^[가-힣]*$", name)) {	 // 한글인 경우
			String lastNm = name.substring(0,1);	// 성
			String firstNm = name.substring(1);		// 이름
			
			if(firstNm.length() > 1) {
				maskedName = lastNm + "*" + firstNm.substring(1);
			} else {
				maskedName = lastNm + "*";	// 외자인 경우
			}
		} else {	// 영어인 경우 ex)
			String[] nameArr = name.split(" ");
			
			// 마스킹 처리
			for(int i=0; i<nameArr.length; i++) {

				char[] nameCh = nameArr[i].toCharArray();
				
				if(i==0) { // 첫번째 문자그룹인 경우 앞 2자리 제외 마스킹
					for(int c=nameCh.length-1; c>1; c--) {
						nameCh[c] = '*';
					}
				} else if(i == nameArr.length-1) {	// 마지막 문자그룹인 경우 뒤 2자리 제외 마스킹
					for(int c=0; c<nameCh.length-2; c++) {
						nameCh[c] = '*';
					}
				} else {
					for(char c : nameCh) {
						c = '*';
					}
				}
				
				nameArr[i] = String.valueOf(nameCh);
			}
			
			// 이름 합치지
			for(int s=0; s<nameArr.length; s++) {
				if(s==nameArr.length-1) {
					maskedName += nameArr[s];
				} else {
					maskedName += nameArr[s] + " ";
				}
			}
		}
		
		return maskedName;
	}
	
	// 금액 천단위 콤마 표기 ex) 1,234,567
	public static String moneyFormat(String money) {
		
		String result = "";
		
		int data = Integer.parseInt(money);
		DecimalFormat df = new DecimalFormat("###,###");
		result = df.format(data);
		
		return result;
		
	}
}
