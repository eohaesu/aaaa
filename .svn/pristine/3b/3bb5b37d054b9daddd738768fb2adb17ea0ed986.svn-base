package com.deotis.digitalars.service.business;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import com.deotis.digitalars.model.changePayment.ChgMethodReqEntity;
import com.deotis.digitalars.model.changePayment.ChgWithdrawReqEntity;
import com.deotis.digitalars.model.changePayment.WithdrawInfo;
import com.deotis.digitalars.security.model.SecretEntity;
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
public class ChangePaymentService {

	private final DeotisTemplate deotisTemplate;
	private final RedisTemplateService redisTemplateService;
	private final UPlusApimService uplusApimService;
	private final UPlusLogService uplusLogService;
	
	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("YYYYMMddHHmmssSSS").toFormatter();
	
	/**
	 * 서비스 : 납부방법변경
	 * IF-API-031707 : 정보조회
	 * @return
	 */
	public Map<String, Object> getPayMethod() {
		
		SecretEntity secret = SessionHandler.getSecretEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String gw = APIM.IF_API_031707.getBaseGw();
		
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
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_031707.getEndUrl()).queryParam("mode", "E").build();  // B: 청구계정, E: 가입번호, P: 상품번호
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("entrId", Base64Utils.encodeToString(secret.getEntrNo().getBytes()));
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {
			
			ResponseEntity<Map<String, Object>> response = deotisTemplate.exchange(
																uriBuilder.toUriString(),
																HttpMethod.GET,
																httpEntity,
																new ParameterizedTypeReference<Map<String, Object>>() {});
			
			result = response.getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_031707.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));

			if(response.getHeaders().get("bizerror") != null && "Y".equals(response.getHeaders().get("bizerror").toString())) {	 // 비즈니스 에러
				result.put("result", ResultStrConstants.FAIL);
			}
			
			log.debug("IF-API-031707 [response] : {}", result);
			
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-031707 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_031707.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				return getPayMethod();
			} else {
				result.put("result", ResultStrConstants.FAIL);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-031707 Failed. [message] : {}", e.getMessage());
			
			result.put("result", ResultStrConstants.FAIL);
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_031707.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return result;
	}
	
	/**
	 * 서비스 : 납부방법변경
	 * IF-API-047801 : 자동이체 최초출금일 조회
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public WithdrawInfo getWithdrawDate() {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		WithdrawInfo withdrawInfo = null;
		
		String gw = APIM.IF_API_047801.getBaseGw();
		
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
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_047801.getEndUrl()).queryParam("mode", "E").build();  // B: 청구계정, E: 가입번호, P: 상품번호
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("billAcntId", entity.getCustCntcInfo().get("billAcntId").toString());
		
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {
			
			ResponseEntity<Map<String, Object>> response = deotisTemplate.exchange(
																uriBuilder.toUriString(),
																HttpMethod.GET,
																httpEntity,
																new ParameterizedTypeReference<Map<String, Object>>() {});
														
			Map<String, Object> result = response.getBody();
			Map<String, String> wdrwInfo = (Map<String, String>) result.get("dsFrstWdrw");
			withdrawInfo = WithdrawInfo.toEntity(wdrwInfo); // 출금일 정보
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_047801.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			log.debug("IF-API-047801 [response] : {}", result);
			
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-047801 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_047801.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				return getWithdrawDate();
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-047801 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_047801.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return withdrawInfo;
	}
	
	/**
	 * IF-API-031702 : 정보등록변경
	 * @return
	 */
	public String chgPayMethod(ChgMethodReqEntity params) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		SecretEntity secret = SessionHandler.getSecretEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String successYn = ResultStrConstants.FAIL;
		String gw = APIM.IF_API_031702.getBaseGw();
		
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
		
		if("01062618456".equals(secret.getAni())) {	// 김동욱 책임님의 경우 테스트번호로 등록되어 있음 (테스트시 인입번호와 고객정보가 일치함. 다른분들은 불일치)
			/* 고객정보 확인 */
			String custNm = new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("custNm").toString()));	// 고객명
			String custBday = secret.getCustrnmBday();	// 실명번호(생년월일)
			
			if("20".equals(params.getCustBasKdCd())) {	// 개인사업자의 경우 사업자번호 비교
				if(entity.getCustCntcInfo().get("brno") != null) {	
					String busiNum = new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("brno").toString()));		// 사업자번호
					
					log.debug("            ::::: [ AUTHENTICATION ] Entered Customer Info => custNm [{}], custBday [{}], busiNum [{}]:::::            ", custNm, custBday, busiNum);
					
					if(!custNm.equals(params.getAcctCardOwnrNm()) || !busiNum.equals(params.getCrypRnno())) {
						successYn = "mismatch";
						return successYn;
					}
				} else {	// 고객정보에 사업자번호가 없는 경우
					successYn = "mismatch";
					return successYn;
				}
			} else {
				log.debug("            ::::: [ AUTHENTICATION ] Entered Customer Info => custNm [{}], custBday [{}] :::::            ", custNm, custBday);
				
				// 고객명, 생년월일 확인
				if(!custNm.equals(params.getAcctCardOwnrNm()) || !custBday.equals(params.getCrypRnno())) {
					successYn = "mismatch";
					return successYn;
				}
			}
		}
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_031702.getEndUrl()).build();  // B: 청구계정, E: 가입번호, P: 상품번호
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		headers.set("X-USER-ID", "1412399185");
		
		params.setBillAcntId(entity.getBillAcntInfo().getBillAcntId());										// 청구계정ID
		params.setAcctCardOwnrNm(Base64Utils.encodeToString(params.getAcctCardOwnrNm().getBytes()));		// 계좌 또는 카드소유자명
		params.setCrypRnno(Base64Utils.encodeToString(params.getCrypRnno().getBytes()));					// 계좌소유주실명번호
		
		if("CC".equals(params.getPaymMthdCd())) {
			params.setEcno(Base64Utils.encodeToString(params.getEcno().getBytes()));						// 카드번호
			params.setCardValdEndYymm(Base64Utils.encodeToString(params.getCardValdEndYymm().getBytes()));	// 카드유효종료년월
		}
		if("CM".equals(params.getPaymMthdCd())) {
			params.setEano(Base64Utils.encodeToString(params.getEano().getBytes()));						// 은행계좌번호
		}
		
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {
			
			ResponseEntity<Map<String, Object>> response = deotisTemplate.exchange(
																uriBuilder.toUriString(),
																HttpMethod.POST,
																httpEntity,
																new ParameterizedTypeReference<Map<String, Object>>(){});
			
			result = response.getBody();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_031702.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			// 결과
			if(result.get("msgCode") != null && "SUCCESS".equals(result.get("msgCode"))){
				successYn = ResultStrConstants.SUCCESS;	// 성공
			} 
			
			log.debug("IF-API-031702 [response] : {}", result);
			
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-031702 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_031702.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				return chgPayMethod(params);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-031702 Failed. [message] : {}", e.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_031702.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return successYn;
	}
	
	/**
	 * 서비스 : 납부방법변경
	 * IF-API-047802 : 자동이체 최초 출금일 변경
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String chgWithdrawDate(ChgWithdrawReqEntity params) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		Map<String, Object> result = new HashMap<String, Object>();
		String successYn = ResultStrConstants.FAIL;
		String gw = APIM.IF_API_047802.getBaseGw();
		
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
		
		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(baseUrl+APIM.IF_API_047802.getEndUrl()).build();
		
		HttpHeaders headers = uplusApimService.getDefaultHeader(tokenKey);
		
		params.setBillAcntId(entity.getBillAcntInfo().getBillAcntId());
		HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
		
		try {
			
			result = deotisTemplate.exchange(
										uriBuilder.toUriString(),
										HttpMethod.PUT,
										httpEntity,
										new ParameterizedTypeReference<Map<String, Object>>(){}).getBody();

			Map<String, String> resultInfo = (Map<String, String>) result.get("dsRsltInfo");
			successYn = resultInfo.get("rsltCd").toLowerCase();
			
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_047802.getCode(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			log.debug("IF-API-047802 [response] : {}", result);
			
			
		} catch(HttpServerErrorException ex) {
			
			log.error("IF-API-047802 Failed. [status] : {}, [message] : {}", ex.getStatusCode(), ex.getMessage());
			
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_047802.getCode(), "", responseTime, TLO.CODE_70200003.getCode(), TLO.CODE_70200003.getApiRsp(), TLO.CODE_70200003.getErrMsg()));
			
			// Fallback check
			if(ResultStrConstants.FALLBACK == uplusApimService.checkFallback(fallbackKey, ex)) {
				return chgWithdrawDate(params);
			}
			
		} catch(RestClientException e) {
			
			log.error("IF-API-047802 Failed. [message] : {}", e.getMessage());

			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(APIM.IF_API_047802.getCode(), "", responseTime, TLO.CODE_70200005.getCode(), TLO.CODE_70200005.getApiRsp(), TLO.CODE_70200005.getErrMsg()));
			
		}
		
		return successYn;
	}
}
