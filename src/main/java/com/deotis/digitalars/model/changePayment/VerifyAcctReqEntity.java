package com.deotis.digitalars.model.changePayment;

import lombok.Builder;
import lombok.Data;

/**
 * 계좌, 카드 및 이름인증
 * 
 * @author hyunjung
 *
 */
@Data
public class VerifyAcctReqEntity {
	
	private String custBasKdCd;		// 고객기본유형코드 (10: 개인, 20: 개인사업자)
	private String custDetlKdCd;	// 고객상세유형코드 (개인인 경우 11: 내국인, 12: 외국인 / 개인사업자인경우 21: 내국인,외국인)
	private String mode = "N";		// 모드 (N: 인증시 계좌/카드가 납부자 소유인지 체크, P: 인증시 계좌/카드가 납부자 소유인지 체크 안함)
	private String brno;			// 사업자등록번호 (base64 인코딩)
	private String pymCrypRnno;		// 납부실명번호 (생년월일+성별, base64 인코딩)
	private String acntOwnrNm;		// 소유자명 (base64 인코딩)
	private String bankAcntNo;		// 은행계좌번호 (base64 인코딩) ※ 납부방법 CM일 시 필수
	private String bankCd;			// 은행코드 ※ 납부방법 CM일 시 필수
	private String cardValdEndYymm;	// 카드유효종료년월 (base64 인코딩) ※ 납부방법 CC일 시 필수
	private String cardNo;			// 카드번호 (base64 인코딩) ※ 납부방법 CC일 시 필수
	private String cdcoCd;			// 카드사코드 ※ 납부방법 CC일 시 필수
	private String paymMthdCd;		// 납부방법코드 (CM: 계좌이체, CC: 신용카드, GR: 지로, RW: 계좌출금, DD: 은행자동이체)
	
	@Builder
	public VerifyAcctReqEntity(
			String custBasKdCd, 
			String custDetlKdCd, 
			String brno, 
			String pymCrypRnno,
			String acntOwnrNm,
			String bankAcntNo,
			String bankCd,
			String cardValdEndYymm,
			String cardNo,
			String cdcoCd,
			String paymMthdCd) {
		this.custBasKdCd = custBasKdCd;
		this.custDetlKdCd = custDetlKdCd;
		this.brno = "20".equals(custBasKdCd) ? brno : "";
		this.pymCrypRnno = pymCrypRnno;
		this.acntOwnrNm = acntOwnrNm;
		this.bankAcntNo = "CM".equals(paymMthdCd) ? bankAcntNo : "";
		this.bankCd = "CM".equals(paymMthdCd) ? bankCd : "";
		this.cardValdEndYymm = "CC".equals(paymMthdCd) ? cardValdEndYymm : "";
		this.cardNo = "CC".equals(paymMthdCd) ? cardNo : "";
		this.cdcoCd = "CC".equals(paymMthdCd) ? cdcoCd : "";
		this.paymMthdCd = paymMthdCd;
	}
}
