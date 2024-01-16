package com.deotis.digitalars.model.changePayment;

import lombok.Data;

/**
 * 납부방법변경
 * 
 * @author hyunjung
 */
@Data
public class ChgMethodReqEntity {
	
	public String custBasKdCd;		// 고객기본유형코드 (10: 개인, 20: 개인사업자)
	public String custDetlKdCd;		// 고객상세유형코드 (개인인 경우 11: 내국인, 12: 외국인 / 개인사업자인경우 21: 내국인,외국인)
	public String billAcntId;		// 청구계정ID
	public String paymMthdCd;		// 납부방법코드 (CM: 계좌이체, CC: 신용카드, GR: 지로)
	public String acctCardOwnrNm;	// 계좌카드소유자명 (base64 인코딩)
	public String crypRnno;			// 계좌소유주실명번호 (개인 6자리 or 13자리, 사업자 10자리, base64 인코딩)
	public String bankCd;			// 은행코드 (납부방법 CM일 경우 필수)
	public String cdcoCd;			// 카드사코드 (납부방법코드 CC일 경우 필수)
	public String cardValdEndYymm;	// 카드유효종료년월 (납부방법코드 CC일 경우 필수, base64 인코딩)
	public String eano;				// 은행계좌번호 (납부방법 CM일 경우 필수, base64 인코딩)
	public String ecno;				// 암호화카드번호 (납부방법코드 CC일 경우 필수, base64 인코딩)
	public String frstWdrwDtDivsCd;	// 최초출금일자구분코드 (CC: 1~4차, CM: 15, 18, 22, 26, 99(법인고객-말일))
	
}
