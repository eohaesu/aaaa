package com.deotis.digitalars.model.common;

import lombok.Data;

/**
 * 요금납부 - 신용카드(타인) 인증번호 발송
 * 
 * @author hyunjung
 */
@Data
public class AuthSendReqEntity {

	private String rcpNo;							// 수신번호 (base64 인코딩)
	private String rplyNo;							// 회신번호 (홈: 101, 모바일: 114)
	private String msgFomId = "MF_NUNO_G000180";	// 메시지양식ID
	private String msgCntn;							// 메시지내용
	private String msgSendJobDivsCd = "NUNO";		// 메시지발송업무구분코드
	private String msgFomSendFormCd = "MMS";		// 메시지양식발송형태코드
	private String ocmpSendYn;						// 타사발송여부
	private String userId = "MTQxMjUxMDc4MQ==";		// 전제사용자ID (base64 인코딩), 값: 1412510781
	private String msgCrteKdCd = "PSVT";			// 메시지생성유형코드 (PSVT: 상담사문자, 인증문자 등 사용자가 UI를 통해 발송 요청한 경우, AUTO: 비즈니스 로직에 의해 발송 요청한 경우)
	private String custNm;							// 고객명 *** API Request 값이 아님 ***
	
}
