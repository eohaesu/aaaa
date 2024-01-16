package com.deotis.digitalars.model.payment;

import lombok.Data;

@Data
public class PayWthdReqEntity {
	public String rtwdAprvRqstAmt;		// 금액
	public String uuid = "1";			// 순번 (거래 unique key)
	public String rowCount = "1";		// row 건수 (복수개의 총 count)
	public String totalAmt;				// 총금액
	public String indvCoDivsCd = "0";	// 개인 or 법인 구분코드 (개인:0, 사업자:1)
	public String svcbRecpYn = "N";		// 서비스별수납여부 (서비스별: Y, 청구계정별: N)
	public String bankAcctNo;			// 계좌번호 (base64 인코딩)
	public String custBankCd;			// 은행코드
	public String acctOwnerPersNo;		// 계좌소유주주민번호 (base64 인코딩)
	public String paymCustNm;			// 납부자명 (필수 아님)
}
