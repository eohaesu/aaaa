package com.deotis.digitalars.constants;

import lombok.Getter;

@Getter
public enum TLO {
	
	CODE_20000000("20000000", "정상", "SUCCESS", "Success"),
	CODE_70200001("70200001", "APIM API 호출 실패", "RTN_APIM_REQ_FAILED", "Fail"),
	CODE_70200002("70200002", "APIM API 타임아웃", "RTN_APIM_TIMEOUT", "Fail"),
	CODE_70200003("70200003", "APIM API 서버 에러", "RTN_APIM_SERVER_EXCEPTION", "Fail"),
	CODE_70200004("70200004", "APIM API 비즈니스 에러", "RTN_APIM_BIZ_EXCEPTION", "Fail"),
	CODE_70200005("70200005", "APIM API 예외 에러", "RTN_APIM_EXCEPTION", "Fail"),
	CODE_70200006("70200006", "WMS GET USID 실패", "RTN_WMS_GET_USID_FAILED", "Fail"),
	CODE_70200007("70200007", "WMS CRID 만료", "RTN_WMS_CRID_EXPIRED", "Fail"),
	CODE_70200008("70200008", "WMS 콜 중복 또는 끊어짐", "RTN_WMS_CALL_DUPLICATED_OR_DEAD", "Fail"),
	CODE_70200009("70200009", "WMS Callend 실패", "RTN_WMS_CALLEND_FAILED", "Fail"),
	CODE_70200010("70200010", "WMS GoToEnd 실패", "RTN_WMS_GOTOEND_FAILED", "Fail"),
	CODE_70200011("70200011", "WMS 데이터 전송 실패", "RTN_WMS_SEND_FAILED", "Fail"),
	CODE_70200012("70200012", "WMS WAS START 실패", "RTN_WMS_WAS_START_FAILED", "Fail");
	
	private final String code;
	private final String codeMsg;
	private final String errMsg;
	private final String apiRsp;
	
	TLO(final String code, final String codeMsg, final String errMsg, final String apiRsp) {
		this.code = code;
		this.codeMsg = codeMsg;
		this.errMsg = errMsg;
		this.apiRsp = apiRsp;
	}
}
