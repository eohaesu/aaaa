package com.deotis.digitalars.service.business;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.deotis.digitalars.constants.APIM;
import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.constants.ResultStrConstants;
import com.deotis.digitalars.constants.TLO;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.payment.PayChrgReqEntity;
import com.deotis.digitalars.model.payment.PayWthdReqEntity;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.service.rest.external.UPlusApimService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.system.rest.client.DeotisTemplate;
import com.deotis.digitalars.util.common.CommonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Payment API Service
 * @author hyunjung
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {

	private final DeotisTemplate deotisTemplate;
	private final RedisTemplateService redisTemplateService;
	private final UPlusApimService uplusApimService;
	private final UPlusLogService uplusLogService;
	
	@Value("${pay.kakao.key}")
	public String kakaoKey;
	
	@Value("${pay.toss.token}")
	public String tossToken;
	
	@Value("${pay.payco.token}")
	public String paycoToken;
	
	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("YYYYMMddHHmmssSSS").toFormatter();
	
	/**
	 * IF-API-001201 : 연체및기타정보조회
	 * @return
	 */
	public Map<String, Object> getArerEtcInfo(String mode, String entrId) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_001201.getBaseGw();
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_001201.getEndUrl()).queryParam("mode", mode).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("X-USER-ID", "1412399185");
		if("A".equals(mode)) {
			headers.set("billAcntId", entity.getCustCntcInfo().get("billAcntId").toString());
		} else if("E".equals(mode)) {
			headers.set("entrId", entrId);
		}
		
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.GET,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();

			log.debug("IF-API-001201 [response] : {}", result);
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_001201.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			// 결과
			if(result.get("billAcntId") == null && result.get("billAcntId") == "") {
				result.put("result", ResultStrConstants.FAIL);
			}
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-001201 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_001201.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				result = getArerEtcInfo(mode, entrId);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-001201 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_001201.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
			result.put("result", ResultStrConstants.FAIL);
			
		}
		
		return result;
	}
	
	/**
	 * IF-API-005904 : 인터넷수납
	 * @param params
	 * @return
	 */
	public Map<String, Object> payCardChrg(PayChrgReqEntity params) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_005904.getBaseGw();
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_005904.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("X-USER-ID", "1412399185");

		params.setProdNo(entity.getCustCntcInfo().get("prodNo").toString());							// 상품번호
		params.setCardNo(Base64Utils.encodeToString(params.getCardNo().getBytes()));					// 카드번호
		params.setCcrdOwnrPersNo(Base64Utils.encodeToString(params.getCcrdOwnrPersNo().getBytes()));	// 생년월일
		
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_005904.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			log.debug("IF-API-005904 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-005904 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_005904.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				result = payCardChrg(params);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-005904 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_005904.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return result;
	}

	/**
	 * IF-API-066801 : 카카오청구서링크생성
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getKakaoLink(String payAmt) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_066801.getBaseGw();
		String link = "";
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_066801.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("X-Partner-Token", "PARTNER_KEY "+kakaoKey);

		// 만료일시
		LocalDateTime nowDtm = LocalDateTime.now();
		String today = nowDtm.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
		String expire = today+"235959";
		entity.setCreatePayLinkDate(today);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("biller_user_key2", new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("entrId").toString())));
		param.put("overwrite_amount", payAmt);
		param.put("channel", "AP");
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("biller_user_key", new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("billAcntId").toString())));
		data.put("expire_at", expire);
		data.put("parameters", param);
		
		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("data", data);
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066801.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			if("OK".equals(result.get("res_code"))) {
				Map<String, String> dataLink = (Map<String, String>) result.get("data");
				link = dataLink.get("url");
			}
			
			log.debug("IF-API-066801 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-066801 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066801.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				link = getKakaoLink(payAmt);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-066801 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066801.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return link;
	}
	
	/**
	 * IF-API-066803 : 카카오수납내역대사
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getKakaoPayComplete() {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		String billAcntId = entity.getCustCntcInfo().get("billAcntId").toString();
		
		Map<String, Object> response = new HashMap<String, Object>();
		String gw = APIM.IF_API_066803.getBaseGw();
		String payComplete = ResultStrConstants.FAIL;
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_066803.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("X-Partner-Token", "PARTNER_KEY D52600D8B120646778D763A24C615C3F9ED1647B");

		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("page", 1);
		bodyData.put("page_size", 1000);
		bodyData.put("paid_date", entity.getCreatePayLinkDate());
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			response = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066803.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			Map<String, Object> result = (Map<String, Object>) response.get("data");
			ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) result.get("result");
			
			for(Map<String, String> info : list) {
				if(billAcntId.equals(info.get("biller_user_key"))){
					payComplete = ResultStrConstants.SUCCESS;
					return payComplete;
				}
			}
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-066803 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066803.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				payComplete = getKakaoPayComplete();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-066803 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066803.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return payComplete;
	}
	
	/**
	 * IF-API-066901 : 토스뱅크청구서링크생성
	 * @param params
	 * @return
	 */
	public String getTossLink(String payAmt) {
		
		UserEntity entity = SessionHandler.getSessionEntity();

		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_066901.getBaseGw();
		String link = "";
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_066901.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		
		// 만료일시
		LocalDateTime nowDtm = LocalDateTime.now();
		String today = nowDtm.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
		String expire = today+"235959";
		entity.setCreatePayLinkDate(today);
				
		Map<String, Object> billInfo = new HashMap<String, Object>();
		billInfo.put("bill_acnt_no", new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("billAcntId").toString())));
		billInfo.put("ace_no", new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("entrId").toString())));
		billInfo.put("pym_amt", payAmt);
		
		List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
		billList.add(billInfo);
		
		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("bill_list", billList);
		bodyData.put("expire_at", expire);
		bodyData.put("channel_type", "AP");
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066901.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			if("SUCCESS".equals(result.get("message")) && result.get("url") != null) {
				link = result.get("url").toString();
			}
			
			log.debug("IF-API-066901 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-066901 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066901.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				link = getTossLink(payAmt);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-066901 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066901.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return link;
	}
	
	/**
	 * IF-API-066903 : 토스뱅크수납내역대사
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getTossPayComplete() {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		String billAcntId = entity.getCustCntcInfo().get("billAcntId").toString();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_066903.getBaseGw();
		String payComplete = ResultStrConstants.FAIL;
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_066903.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		
		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("currentPage", 1);						// 페이지 번호
		bodyData.put("rowsPerPage", 1000);					// 페이지크기
		bodyData.put("serviceCode", "SB_PAY_D");			// 데이터 타입
		bodyData.put("mrcCode", "TOSS_LGU_BILL");			// 가맹점코드
		bodyData.put("ymd", entity.getCreatePayLinkDate());	// 결제일자
		bodyData.put("token", tossToken);					// 접속토큰
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066903.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			Map<String, Object> res = (Map<String, Object>) result.get("result");
			ArrayList<Map<String, String>> setlList = (ArrayList<Map<String, String>>) res.get("setlList");
			
			for(Map<String, String> payHistory : setlList) {
				String title = payHistory.get("orderTitle");
		        String patternString = "\\(([^)]+)\\)";

		        Pattern pattern = Pattern.compile(patternString);
		        Matcher matcher = pattern.matcher(title);

		        if(matcher.find()) {
		        	String extractedString = matcher.group(1);
		        	if(billAcntId.equals(extractedString) && "승인".equals(payHistory.get("payTpCdName"))) {
		        		payComplete = ResultStrConstants.SUCCESS;
		        		return payComplete;
		        	}
		        }
			}
			
			log.debug("IF-API-066903 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-066903 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066903.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				payComplete = getTossPayComplete();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-066903 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_066903.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return payComplete;
	}
	
	/**
	 * IF-API-010101 : 페이코청구서링크생성
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getPaycoLink(String payAmt) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_010101.getBaseGw();
		String link = "";
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_010101.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		
		// 만료일시
		LocalDateTime nowDtm = LocalDateTime.now();
		String today = nowDtm.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
		String expire = today+"235959";
		entity.setCreatePayLinkDate(today);
				
		Map<String, Object> billInfo = new HashMap<String, Object>();
		billInfo.put("bill_acnt_no", entity.getCustCntcInfo().get("billAcntId").toString());
		billInfo.put("ace_no", entity.getCustCntcInfo().get("entrId").toString());
		billInfo.put("pym_amt", payAmt);
		
		List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
		billList.add(billInfo);
		
		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("bill_list", billList);
		bodyData.put("expire_at", expire);
		bodyData.put("channel_type", "AP");
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_010101.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			if("OK".equals(result.get("res_code"))) {
				Map<String, String> dataLink = (Map<String, String>) result.get("data");
				link = dataLink.get("url");
			}
			
			log.debug("IF-API-010101 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-010101 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_010101.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				link = getPaycoLink(payAmt);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-010101 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_010101.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return link;
	}
	
	/**
	 * IF-API-010103 : 페이코수납내역대사
	 * @param params
	 * @return
	 */
	public String getPaycoPayComplete() {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_010103.getBaseGw();
		String payComplete = ResultStrConstants.FAIL;
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_010103.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		
		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("currentPage", 1);						// 페이지 번호
		bodyData.put("rowsPerPage", 1000);					// 페이지크기
		bodyData.put("serviceCode", "SB_PAY_D");			// 데이터 타입
		bodyData.put("mrcCode", "PAYCO_LGU_BILL");			// 가맹점코드
		bodyData.put("ymd", entity.getCreatePayLinkDate());	// 결제일자
		bodyData.put("token", paycoToken);					// 접속토큰
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_010103.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			// TODO : 수납여부 확인 로직 추가 필요
			
			log.debug("IF-API-010103 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-010103 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_010103.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				payComplete = getPaycoPayComplete();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-010103 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_010103.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return payComplete;
	}
	
	/**
	 * IF-API-005906 : 실시간출금 수납
	 * @param params
	 * @return
	 */
	public Map<String, Object> payWithdraw(PayWthdReqEntity params) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_005906.getBaseGw();
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = uplusApimService.getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = uplusApimService.getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			uplusApimService.createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_005906.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("X-USER-ID", "1412399185");
		
		
		Map<String, Object> simpleInfo = new HashMap<String, Object>();
		simpleInfo.put("prodNo", entity.getCustCntcInfo().get("prodNo").toString());		
		simpleInfo.put("rtwdAprvRqstAmt", params.getRtwdAprvRqstAmt());
		simpleInfo.put("etlsAmt", "0");	// 잡손실금액
		
		List<Map<String, Object>> apiRtwdRecpEncnReqListDTO = new ArrayList<Map<String, Object>>();
		apiRtwdRecpEncnReqListDTO.add(simpleInfo);
		
		Map<String, Object> apiRtwdWdrwReqDTO = new HashMap<String, Object>();
		apiRtwdWdrwReqDTO.put("billAcntId", entity.getCustCntcInfo().get("billAcntId").toString());		
		apiRtwdWdrwReqDTO.put("uuid", params.getUuid());
		apiRtwdWdrwReqDTO.put("rowCount", params.getRowCount());
		apiRtwdWdrwReqDTO.put("totalAmt", params.getTotalAmt());
		apiRtwdWdrwReqDTO.put("indvCoDivsCd", params.getIndvCoDivsCd());
		apiRtwdWdrwReqDTO.put("svcbRecpYn", params.getSvcbRecpYn());
		apiRtwdWdrwReqDTO.put("bankAcctNo", Base64Utils.encodeToString(params.getBankAcctNo().getBytes()));
		apiRtwdWdrwReqDTO.put("custBankCd", params.getCustBankCd());
		apiRtwdWdrwReqDTO.put("acctOwnerPersNo", entity.getCustCntcInfo().get("custCrypRnno").toString());
		
		Map<String, Object> bodyData = new HashMap<String, Object>();
		bodyData.put("apiRtwdRecpEncnReqListDTO", apiRtwdRecpEncnReqListDTO);
		bodyData.put("apiRtwdWdrwReqDTO", apiRtwdWdrwReqDTO);
		
		HttpEntity<?> httpEntity = new HttpEntity<>(bodyData, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();

			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_005906.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));

			log.debug("IF-API-005906 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-005906 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_005906.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				result = payWithdraw(params);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-005906 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_005906.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return result;
	}
}
