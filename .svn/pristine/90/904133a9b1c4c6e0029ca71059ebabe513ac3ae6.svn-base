package com.deotis.digitalars.model.payment;

import lombok.Builder;
import lombok.Data;

/**
 * 신용카드 납부
 * 
 * @author hyunjung
 */

@Data
public class PayChrgReqEntity {
	
	private String prodNo;					// 상품번호 (base64 인코딩)
	private String ccrdAprvRqstAmt;			// 금액
	private String cardNo;					// 카드번호 (base64 인코딩) 		※ 카드결제시 필수
	private String ccrdCompCd;				// 카드사코드 					※ 카드결제시 필수
	private String ccrdValdEndDt;			// 카드유효종료일				※ 카드결제시 필수
	private String ccrdIndvCoDvCd = "0";	// 개인:0, 법인:1 				※ 카드결제시 필수, 법인카드는 상담사 처리 필요
	private String ccrdOwnrPersNo;			// 고객생년월일 (base64 인코딩)
	private String ccrdStlmInsttMms;		// 카드할부개월수				※ 카드결제시 필수
	private String sourceType = "C";		// 구분코드 (카드결제:C, 계좌이체:R)
	
	@Builder
	public PayChrgReqEntity(
			String payAmt, 
			String cardNo, 
			String cardCompCd,
			String cardValidYm,
			String cardOwnBirth,
			String cardInstMonth) {
		this.ccrdAprvRqstAmt = payAmt;
		this.cardNo = cardNo;
		this.ccrdCompCd = cardCompCd;
		this.ccrdValdEndDt = cardValidYm;
		this.ccrdOwnrPersNo = cardOwnBirth;
		this.ccrdStlmInsttMms = cardInstMonth;
	}
}
