package com.deotis.digitalars.constants;

import lombok.Getter;

@Getter
public enum APIM {
	
	// 공통
	IF_API_TOKEN("IF-API-TOKEN", "", "/oauth2/token"),
	IF_API_026805("IF-API-026805", "pv", "/pv/cm/ca/cp/custCmpx/v1/entrCntc"),				// 가입계약
	IF_API_028501("IF-API-028501", "pv", "/pv/cm/ca/ca/hphnPrsnAutn/v1/autnNoCnfm"),		// 인증번호확인
	IF_API_028502("IF-API-028502", "pv", "/pv/cm/ca/ca/hphnPrsnAutn/v1/autnNo"),			// 인증번호요청
	IF_API_048403("IF-API-048403", "pb", "/pb/cm/cc/cm/intgEntrInfo/v1/cntcSvcEntrInfo"),	// 서비스가입정보
	IF_API_028701("IF-API-028701", "pv", "/pv/cm/ca/ca/paymAutn/v1/ccrd"),					// 신용카드인증
	IF_API_028703("IF-API-028703", "pv", "/pv/cm/ca/ca/paymAutn/v1/acctCard"),				// 계좌,카드 및 이름 인증
	
	// 요금납부
	IF_API_000601("IF-API-000601", "pv", "/pv/bl/ar/ar/cardInfo/v1/ccrdPfixInfo"),			// 신용카드 Prefix 정보조회
	IF_API_001201("IF-API-001201", "pv", "/pv/bl/ar/ar/upadInfo/v1/arerEtcInfo"),			// 연체및기타정보조회
	IF_API_029701("IF-API-029701", "pv", "/pv/ea/cm/no/insMsg/v1/gnrOnlnMsg"),				// 대내문자일반온라인
	IF_API_005904("IF-API-005904", "pv", "/pv/bl/ar/ar/recpAply/v1/itnt"),					// 인터넷수납
	IF_API_005906("IF-API-005906", "pv", "/pv/bl/ar/ar/recpAply/v1/rtwd"),					// 실시간출금
	IF_API_066801("IF-API-066801", "pv", "/pt/kakaopay/kakaopayPayment/v1/bltxLink"),		// 카카오페이청구서링크생성
	IF_API_066901("IF-API-066901", "pv", "/pt/tossbank/tossbankPayment/v1/bltxLink"),		// 토스뱅크청구서링크생성
	IF_API_010101("IF-API-010101", "pv", "/pt/payco/payment/v1/bltxLink"),					// 페이코청구서링크생성
	IF_API_066803("IF-API-066803", "pv", "/pt/kakaopay/kakaopayPayment/v1/recpDlstItac"),	// 카카오페이수납내역대사
	IF_API_066903("IF-API-066903", "pv", "/pt/tossbank/tossbankPayment/v1/recpDlstItac"),	// 토스뱅크수납내역대사
	IF_API_010103("IF-API-010103", "pv", "/pt/payco/payment/v1/recpDlstItac"),				// 페이코수납내역대사
	
	// 납부방법변경
	IF_API_001112("IF-API-001112", "pv", "/pv/bl/ar/ar/recpInfo/v1/bankCdco"),				// 은행카드사목록조회
	IF_API_031702("IF-API-031702", "pv", "/pv/cm/ca/pa/billAcnt/v1/info"),					// 정보등록변경
	IF_API_031707("IF-API-031707", "pv", "/pv/cm/ca/pa/billAcnt/v1/info"),					// 정보조회
	IF_API_047801("IF-API-047801", "pv", "/pv/cm/ca/pa/frstWdrwDd/v1/info"),				// 자동이체 최초출금일 조회
	IF_API_047802("IF-API-047802", "pv", "/pv/cm/ca/pa/frstWdrwDd/v1/info");				// 자동이체 최초출금일 변경
	
	private final String code;
	private final String baseGw;
	private final String endUrl;
	
	APIM(final String code, final String baseGw, final String endUrl) {
		this.code = code;
		this.baseGw = baseGw;
		this.endUrl = endUrl;
	}
}
