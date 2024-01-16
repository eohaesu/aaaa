package com.deotis.digitalars.model.payment;

import lombok.Data;

/**
 * 요금납부 - 신용카드 정보
 * 
 * @author hyunjung
 *
 */
@Data
public class CardInfo {
	
	private String cardNo;			// 카드번호 (base64 인코딩) 	
	private String cardCd;			// 카드사코드 			
	private String cardValdEndYymm;	// 카드유효종료일
	private String cardInst;		// 카드할부개월수
	private String cardOwnNm;		// 소유자명 (base64 인코딩)
	private String custRnno;		// 실명번호
	private String custBasKdCd;		// 고객기본유형코드 (10: 개인, 20: 개인사업자)
	private String custDetlKdCd;	// 고객상세유형코드 (개인인 경우 11: 내국인, 12: 외국인 / 개인사업자인경우 21: 내국인,외국인)
	private String brno;			// 사업자등록번호 (base64 인코딩)
	private String payAmt;			// 금액
	
}
