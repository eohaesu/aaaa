package com.deotis.digitalars.model.common;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.Base64Utils;

import com.deotis.digitalars.util.common.CommonUtil;

import lombok.Builder;
import lombok.Data;

/**
 * 서비스가입정보
 */
@Builder
@Data
public class CntcSvcInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String entrId;			// 가입ID
	private String custNm;			// 고객명
	private String mbltlno;			// 모바일전화번호
	private String tlno;			// 전화번호
	private String billAcntId;		// 청구계정ID
	private String encnTlno;		// 가입계약전화번호 (prodNo: 상품번호)
	private String svcCd;			// 서비스코드
	private String svcNm;			// 서비스명
	private String ppCd;			// 요금제코드
	private String ppNm;			// 요금제명
	private String paymMthdCd;		// 납부방법코드
	private String paymMthdNm;		// 납부방법명
	private String currentBill;		// 당월요금
	
	public static CntcSvcInfo toEntity(Map<String, String> cntcSvcList) {
		
		String prodNo = new String(Base64Utils.decodeFromString(cntcSvcList.get("encnTlno")));
		
		if("LZP0000001".equals(cntcSvcList.get("svcCd").toString())) { // 모바일
			prodNo = CommonUtil.phonePartlyMasking(prodNo, "uplus");
		} else { // 홈
			prodNo = CommonUtil.entrIdMasking(prodNo);
		}
		
		return CntcSvcInfo.builder()
				.entrId(cntcSvcList.get("entrId"))
				.custNm(cntcSvcList.get("custNm"))
				.mbltlno(cntcSvcList.get("Mbltlno"))
				.tlno(cntcSvcList.get("tlno"))
				.billAcntId(cntcSvcList.get("billAcntId"))
				.encnTlno(prodNo)
				.svcCd(cntcSvcList.get("svcCd"))
				.svcNm(cntcSvcList.get("svcNm"))
				.ppCd(cntcSvcList.get("ppCd"))
				.ppNm(cntcSvcList.get("ppNm"))
				.paymMthdCd(cntcSvcList.get("paymMthdCd"))
				.paymMthdNm(cntcSvcList.get("paymMthdNm"))
				.build();
	}
}
