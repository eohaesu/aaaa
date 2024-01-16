package com.deotis.digitalars.model.changePayment;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.Base64Utils;

import lombok.Builder;
import lombok.Data;

/**
 * 납부방법정보
 * 
 * @author hyunjung
 *
 */
@Builder
@Data
public class BillAcntInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String billAcntId;		// 청구계정ID
	private String paymMthdCd;		// 납부방법코드 (CM: 계좌이체, CC: 신용카드, GR: 지로)
	private String paymMthdNm;		// 납부방법명
	private String giroPaymDivsCd;	// 지로납부구분코드
	private String bankCd;			// 은행코드
	private String bankNm;			// 은행명
	private String cdcoCd;			// 카드사코드
	private String cdcoNm;			// 카드사명
	private String eano;			// 계좌번호
	private String ecno;			// 카드번호
	private String bltxRcpNo;		// 청구서수신상품번호
	
	public static BillAcntInfo toEntity(Map<String, String> payMethodInfo) {
		return BillAcntInfo.builder()
				.billAcntId(payMethodInfo.get("billAcntId"))
				.paymMthdCd(payMethodInfo.get("paymMthdCd"))
				.paymMthdNm(payMethodInfo.get("paymMthdNm"))
				.giroPaymDivsCd(payMethodInfo.get("giroPaymDivsCd"))
				.bankCd(payMethodInfo.get("bankCd"))
				.bankNm(payMethodInfo.get("bankNm"))
				.cdcoCd(payMethodInfo.get("cdcoCd"))
				.cdcoNm(payMethodInfo.get("cdcoNm"))
				.eano(payMethodInfo.get("eano") != null ? new String(Base64Utils.decodeFromString(payMethodInfo.get("eano"))) : "")
				.ecno(payMethodInfo.get("ecno") != null ? new String(Base64Utils.decodeFromString(payMethodInfo.get("ecno"))) : "")
				.build();
	}

}
