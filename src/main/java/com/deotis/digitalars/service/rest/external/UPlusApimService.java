package com.deotis.digitalars.service.rest.external;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.deotis.digitalars.constants.APIM;
import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.constants.ResultStrConstants;
import com.deotis.digitalars.constants.TLO;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.changePayment.VerifyAcctReqEntity;
import com.deotis.digitalars.model.common.ApimAuthReqEntity;
import com.deotis.digitalars.model.common.AuthSendReqEntity;
import com.deotis.digitalars.model.common.BankInfo;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.UPlusLogService;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.system.rest.client.DeotisTemplate;
import com.deotis.digitalars.util.common.CommonUtil;

import lombok.extern.slf4j.Slf4j;

/** 
 * APIM API Common Service
 * @author hyunjung
 */
@Slf4j
@Service
public class UPlusApimService {

	private final DeotisTemplate deotisTemplate;
	private final RedisTemplateService redisTemplateService;
	private final UPlusLogService uplusLogService;
	
	public UPlusApimService(DeotisTemplate deotisTemplate, RedisTemplateService redisTemplateService, UPlusLogService uplusLogService) {
		this.deotisTemplate = deotisTemplate;
		this.redisTemplateService = redisTemplateService;
		this.uplusLogService = uplusLogService;
	}
	
	@Value("${spring.profiles.active}")
	public String profile;
	
	@Value("${apim.pv.baseUrl}")
	public String PV_BASE_URL;
	
	@Value("${apim.pb.baseUrl}")
	public String PB_BASE_URL;
	
	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("YYYYMMddHHmmssSSS").toFormatter();
	
	/**
	 * GW 정보 조회
	 * @param gw
	 * @return
	 */
	public Map<String, String> getGwInfo(String gw) {
		
		Map<String, String> gwInfo = new HashMap<String, String>();
		
		if("pb".equals(gw)) {
			gwInfo.put("fallbackKey", CommonConstants.PB_FALLBACK_KEY);
			gwInfo.put("baseUrl", PB_BASE_URL);
			gwInfo.put("tokenKey", CommonConstants.APIM_AUTH_TOKEN_PB_KEY);
		} else {
			gwInfo.put("fallbackKey", CommonConstants.PV_FALLBACK_KEY);
			gwInfo.put("baseUrl", PV_BASE_URL);
			gwInfo.put("tokenKey", CommonConstants.APIM_AUTH_TOKEN_PV_KEY);
		}
		
		return gwInfo;
	}
	
	/**
	 * Fallback 상황인 경우 GW 변경
	 * @param gw
	 * @return
	 */
	public String getSwitchGw(String gw) {
		return "pb".equals(gw) ? "pv" : "pb";
	}
	
	/**
	 * 기본 Header 설정
	 * @param tokenKey
	 * @return
	 */
	public HttpHeaders getDefaultHeader(String tokenKey) {
		
		ApimAuthReqEntity apimAuthReqEntity = new ApimAuthReqEntity();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(redisTemplateService.getKeyOptValue(tokenKey).toString());
		headers.set("X-IBM-Client-ID", apimAuthReqEntity.getClientId());
		headers.set("X-IBM-Client-Secret", apimAuthReqEntity.getClientSecret());
		
		return headers;
		
	}
	
	/**
	 * Fallback Check
	 * @param fallbackKey
	 * @param ex
	 * @return
	 */
	public String checkFallback(String fallbackKey, HttpServerErrorException ex) {
		
		// Fallback 상황일 경우 Fallback Process 진행
		if(HttpStatus.BAD_GATEWAY.equals(ex.getStatusCode()) || HttpStatus.SERVICE_UNAVAILABLE.equals(ex.getStatusCode()) || HttpStatus.GATEWAY_TIMEOUT.equals(ex.getStatusCode()) || ex.getMessage().contains("Connection Timeout")) { // 502, 503, 504, timeout 에러
			
			redisTemplateService.addKeyOptValue(fallbackKey, "Y", 1440);
			
			// PB,PV 모두 Fallback인 경우
			if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PB_FALLBACK_KEY)) && "Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) {
				log.info("            ##### Both PB and PV are in fallback #####            ");
				return ResultStrConstants.GWERROR;
			} else { // PB 또는 PB 한쪽만 Fallback인 경우
				log.info("            ##### {} is in fallback #####            ", fallbackKey);
				return ResultStrConstants.FALLBACK;
			}
			
		} else {
			return ResultStrConstants.FAIL;
		}
	}
	
	/**
	 * APIM GW 서버 체크
	 * @param gw
	 */
	public void checkApimGw(String gw) {
		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		// URI
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_TOKEN.getEndUrl()).build();
		
		// Header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		// Request Body
		ApimAuthReqEntity apimAuthReqEntity = new ApimAuthReqEntity();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", apimAuthReqEntity.getGrantType());
		params.add("client_id", apimAuthReqEntity.getClientId());
		params.add("client_secret", apimAuthReqEntity.getClientSecret());
		params.add("scope", apimAuthReqEntity.getScope());
		
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {
			
			Map<String, Object> tokenResult = deotisTemplate.exchange(
							uriBuilder.toUriString(),
							HttpMethod.POST,
							httpEntity,
							new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			log.info("APIM AUTH TOKEN {} KEY IS ALIVE.", gw.toUpperCase());
			
			redisTemplateService.deleteOptString(fallbackKey);
			redisTemplateService.addKeyOptValue(tokenKey, tokenResult.get("access_token").toString(), 1440);

		} catch(RestClientException e) {
			
			log.info("APIM AUTH TOKEN {} KEY IS NOT ALIVE.", gw.toUpperCase());
			
		}
	}
	
	/**
	 * APIM OAuth2.0 Token 발급 (PB(Public), PV(Private))
	 * @param gw
	 */
	public String createAuthToken(String gw) {
		
		String requestTime = "";
		String responseTime = "";
		
		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		// URI
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_TOKEN.getEndUrl()).build();
		
		// Header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		// Request Body
		ApimAuthReqEntity apimAuthReqEntity = new ApimAuthReqEntity();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", apimAuthReqEntity.getGrantType());
		params.add("client_id", apimAuthReqEntity.getClientId());
		params.add("client_secret", apimAuthReqEntity.getClientSecret());
		params.add("scope", apimAuthReqEntity.getScope());
		
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {
			
			// 요청시간
			LocalDateTime reqNow = LocalDateTime.now();
			requestTime = reqNow.format(formatter);
			
			Map<String, Object> tokenResult = deotisTemplate.exchange(
							uriBuilder.toUriString(),
							HttpMethod.POST,
							httpEntity,
							new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			redisTemplateService.addKeyOptValue(tokenKey, tokenResult.get("access_token").toString(), 1440);
			
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			responseTime = resNow.format(formatter);
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_TOKEN.getCode(), requestTime, responseTime, TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
		} catch(HttpServerErrorException ex) {
			
			log.error("APIM AUTH TOKEN ERROR [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_TOKEN.getCode(), requestTime, responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				return createAuthToken(getSwitchGw(gw));
			}
			
		} catch(RestClientException e) {
			
			log.error("APIM AUTH TOKEN ERROR : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_TOKEN.getCode(), requestTime, responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
			return ResultStrConstants.FAIL;
			
		}
		
		return ResultStrConstants.SUCCESS;
	}
	
	/**
	 * IF-API-000601 : 신용카드Prefix 정보조회
	 * @param cardPrefix
	 * @return
	 */
	public Map<String, Object> getCardComp(String cardPrefix) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_000601.getBaseGw();
		
		if(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY) != null) { // fallback일 경우 gw 변경
			gw = getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_000601.getEndUrl()).build();
		
		HttpHeaders headers = getDefaultHeader(tokenKey);
		headers.set("cardNo", Base64Utils.encodeToString(cardPrefix.getBytes()));
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {

			ResponseEntity<Map<String, Object>> response = deotisTemplate.exchange(
																	uriBuilder.toUriString(),
																	HttpMethod.GET,
																	httpEntity,
																	new ParameterizedTypeReference<Map<String, Object>>() {});
			
			result = response.getBody();
			
			// 결과
			if(!"000".equals(response.getBody().get("retCd"))) {
				uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_000601.getCode(), "", "", TLO.CODE_70200004.getCode(), TLO.CODE_70200004.getApiRsp(), TLO.CODE_70200004.getErrMsg()));
			} else {
				uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_000601.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
				
				log.debug("IF-API-000601 [response] : {}", result);
			}
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-000601 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_000601.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				result = getCustContInfo();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-000601 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_000601.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}

		return result;
	}
	
	/**
	 * IF-API-026805 : 가입계약
	 * @param gw
	 * @return
	 */
	public Map<String, Object> getCustContInfo() {
		
		SecretEntity secret = SessionHandler.getSecretEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_026805.getBaseGw();
		
		if(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY) != null) { // fallback일 경우 gw 변경
			gw = getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_026805.getEndUrl()).queryParam("msMode", "ETC").build();
		
		HttpHeaders headers = getDefaultHeader(tokenKey);
		headers.set("entrId", Base64Utils.encodeToString(secret.getEntrNo().getBytes()));
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {

			ResponseEntity<Map<String, Object>> response = deotisTemplate.exchange(
																	uriBuilder.toUriString(),
																	HttpMethod.GET,
																	httpEntity,
																	new ParameterizedTypeReference<Map<String, Object>>() {});
			
			result = response.getBody();
			
			// 결과
			if(response.getHeaders().get("bizerror") != null && "Y".equals(response.getHeaders().get("bizerror").get(0))) {
				uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_026805.getCode(), "", "", TLO.CODE_70200004.getCode(), TLO.CODE_70200004.getApiRsp(), TLO.CODE_70200004.getErrMsg()));
			} else {
				uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_026805.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
				
				log.debug("IF-API-026805 [response] dma_custCntcInfo : {}", result.get("dma_custCntcInfo"));
			}
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-026805 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_026805.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				result = getCustContInfo();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-026805 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_026805.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}

		return result;
		
	}
	
	/**
	 * IF-API-029701 : 대내문자일반온라인 (LGU+ 고객 인증번호 발송)
	 * @return
	 */
	public Map<String, Object> sendAutnNo(AuthSendReqEntity params) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		SecretEntity secret = SessionHandler.getSecretEntity();
		String authNum = "";
		
		Map<String, Object> result = new HashMap<String, Object>();
		String gw = APIM.IF_API_029701.getBaseGw();
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = getSwitchGw(gw);
		}

		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_029701.getEndUrl()).build();
		
		HttpHeaders headers = getDefaultHeader(tokenKey);
	
		if("01062618456".equals(secret.getAni())) {	// 김동욱 책임님의 경우 테스트번호로 등록되어 있음 (테스트시 인입번호와 고객정보가 일치함. 다른분들은 불일치)
			/* 고객정보 확인 */
			String custNm = new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("custNm").toString()));
			String telNum = new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("tlno").toString()));
			
			log.debug("            ::::: [ AUTHENTICATION ] Entered Customer Info => custNm [{}], telNum [{}] :::::            ", custNm, telNum);
			log.debug("            ::::: [ AUTHENTICATION ] Input Value => custNm [{}], telNum [{}] :::::            ", params.getCustNm(), CommonUtil.phoneUplusFormat(params.getRcpNo()));
			
			if(!custNm.equals(params.getCustNm()) || !telNum.equals(CommonUtil.phoneUplusFormat(params.getRcpNo()))) {
				result.put("sendRslt", "MISMATCH");
				return result;
			}
		}
		
		// 인증번호 6자리 생성R
		authNum = String.valueOf((int)(Math.random() * 899999) + 100000);
		entity.setAuthNum(authNum);
		log.info("            ::::: [ AUTHENTICATION ] Create Authentication Number [{}] :::::            ", authNum);
		
		params.setMsgCntn("[LG유플러스] 본인 확인을 위해 인증번호["+authNum+"]를 입력해주세요.");
		
		// 회신번호
		if("LZP0000001".equals(entity.getCustCntcInfo().get("svcCd"))) { 
			params.setRplyNo(Base64Utils.encodeToString("114".getBytes())); 	// 모바일
		} else {
			params.setRplyNo(Base64Utils.encodeToString("101".getBytes()));		// 홈
		}
		// 수신번호 base64 인코딩
		params.setRcpNo(Base64Utils.encodeToString(params.getRcpNo().getBytes()));
		
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {
			
			result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_029701.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			log.debug("IF-API-029701 [response] : {}", result);
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-029701 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_029701.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				result = sendAutnNo(params);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-029701 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_029701.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}

		return result;
		
	}
	
	/**
	 * IF-API-048403 : 서비스가입정보조회
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, String>> getCntcSvcInfo() {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<Map<String, String>> cntcSvcInfo = new ArrayList<Map<String, String>>();
		
		String gw = APIM.IF_API_048403.getBaseGw();
		
		if(redisTemplateService.getKeyOptValue(CommonConstants.PB_FALLBACK_KEY) != null) { // fallback일 경우 gw 변경
			gw = getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_048403.getEndUrl()).queryParam("mode", "C").build();	// A: 가입ID, B: 결합번호, C: 청구계정ID, D: 상품번호, E: 홈회선ID
		
		HttpHeaders headers = getDefaultHeader(tokenKey);
		headers.set("typeId", entity.getCustCntcInfo().get("billAcntId").toString());
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {

			result = deotisTemplate.exchange(
							uriBuilder.toUriString(),
							HttpMethod.GET,
							httpEntity,
							new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			
			if(result.get("cntcSvcEntrInfoDTO") != null) {
				log.debug("IF-API-048403 [response] cntcSvcEntrInfoDTO : {}", result.get("cntcSvcEntrInfoDTO"));
				
				uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_048403.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
				
				cntcSvcInfo = (ArrayList<Map<String, String>>) result.get("cntcSvcEntrInfoDTO");
			}
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-048403 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_048403.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				cntcSvcInfo = getCntcSvcInfo();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-048403 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_048403.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}

		return cntcSvcInfo;
	}
	
	/**
	 * IF-API-001112 : 은행카드사목록조회
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<BankInfo> getBankList(String type) {
		
		ArrayList<BankInfo> bankList = new ArrayList<BankInfo>();
		
		String gw = APIM.IF_API_001112.getBaseGw();
		
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))) { // fallback일 경우 gw 변경
			gw = getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = getGwInfo("pv");
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");

		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_001112.getEndUrl()).queryParam("requestType", type).build();  // A: 은행목록, B: 실시간출금/인증가능 은행목록, C: 카드사목록
		
		HttpHeaders headers = getDefaultHeader(tokenKey);
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {
			
			Map<String, Object> result = deotisTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.GET,
					httpEntity,
					new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			bankList = (ArrayList<BankInfo>) result.get("list");
			
			// 결과
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_001112.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));

			log.debug("IF-API-001112 [response] : {}", result);
			
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-001112 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_001112.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				return getBankList(type);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-001112 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_001112.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return bankList; 
	}
	
	/**
	 * IF-API-028703 : 계좌,카드 및 이름인증
	 * @param params
	 * @return
	 */
	public Map<String, Object> verifyPayMthd(VerifyAcctReqEntity params) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		SecretEntity secret = SessionHandler.getSecretEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String gw = APIM.IF_API_028703.getBaseGw();
		
		if(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY) != null) { // fallback일 경우 gw 변경
			gw = getSwitchGw(gw);
		}
		
		Map<String, String> gwInfo = getGwInfo(gw);
		String baseUrl = gwInfo.get("baseUrl");
		String tokenKey = gwInfo.get("tokenKey");
		String fallbackKey = gwInfo.get("fallbackKey");
		
		if(redisTemplateService.getKeyOptValue(tokenKey) == null) { // token 발급여부 확인
			createAuthToken(gw);
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_028703.getEndUrl()).build();
		
		HttpHeaders headers = getDefaultHeader(tokenKey);
		
		if("01062618456".equals(secret.getAni())) {		// 김동욱 책임님의 경우 테스트번호로 등록되어 있음 (테스트시 인입번호와 고객정보가 일치함. 다른분들은 불일치)
			/* 고객정보 확인 */
			String custNm = new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("custNm").toString()));	// 고객명
			String custBday = secret.getCustrnmBday();	// 실명번호(생년월일)
			
			log.debug("            ::::: [ AUTHENTICATION ] Entered Customer Info => custNm [{}], custBday [{}] :::::            ", custNm, custBday);

			// 실시간출금의 경우 생년월일 입력칸이 없음. 콜봇에서 인증된 생년월일 셋팅
			if(params.getPymCrypRnno() == null || "".equals(params.getPymCrypRnno())) {	
				params.setPymCrypRnno(custBday);
			}
			
			// 고객명, 생년월일 확인
			if(!custNm.equals(params.getAcntOwnrNm()) || !custBday.equals(params.getPymCrypRnno())) {
				result.put("mismatch", "Y");
				return result;
			} 
		} else {
			String custBday = secret.getCustrnmBday();	// 실명번호(생년월일)
			
			// 실시간출금, 개인사업자의 경우 생년월일 입력칸이 없음. 콜봇에서 인증된 생년월일 셋팅
			if(params.getPymCrypRnno() == null || "".equals(params.getPymCrypRnno())) {	
				params.setPymCrypRnno(custBday);
			}
		}
				
		// 실시간출금의 경우 내,외국인 선택이 없음. 고객정보에서 조회하여 셋팅
		if(params.getCustDetlKdCd() == null || "".equals(params.getCustDetlKdCd())) {	
			params.setCustDetlKdCd(entity.getCustCntcInfo().get("custDetlKdCd").toString());
		}
				
		params.setPymCrypRnno(Base64Utils.encodeToString(params.getPymCrypRnno().getBytes()));	// 실명번호
		params.setAcntOwnrNm(Base64Utils.encodeToString(params.getAcntOwnrNm().getBytes()));	// 소유자명
		
		if("20".equals(params.getCustBasKdCd())) {	// 개인사업자인 경우
			params.setBrno(Base64Utils.encodeToString(params.getBrno().getBytes()));
		}
		
		if("CM".equals(params.getPaymMthdCd())) {
			// CM(계좌인체)인 경우
			params.setBankAcntNo(Base64Utils.encodeToString(params.getBankAcntNo().getBytes()));
		} else if("CC".equals(params.getPaymMthdCd())) {	// CC(신용카드)인 경우
			params.setCardValdEndYymm(Base64Utils.encodeToString(params.getCardValdEndYymm().getBytes()));
			params.setCardNo(Base64Utils.encodeToString(params.getCardNo().getBytes()));
		}
	
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {

			result = deotisTemplate.exchange(
							uriBuilder.toUriString(),
							HttpMethod.POST,
							httpEntity,
							new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_028703.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-028703 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_028703.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == checkFallback(fallbackKey, ex)) {
				result = verifyPayMthd(params);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-028703 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_028703.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}

		return result;
	}
}