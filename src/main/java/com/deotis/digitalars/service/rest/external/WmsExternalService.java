package com.deotis.digitalars.service.rest.external;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import com.deotis.digitalars.constants.TLO;
import com.deotis.digitalars.constants.WMS;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.common.LogData;
import com.deotis.digitalars.model.wms.WmsProperties;
import com.deotis.digitalars.model.wms.WmsRequestEntity;
import com.deotis.digitalars.model.wms.WmsResponseEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.UPlusLogService;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.system.rest.client.DeotisTemplate;
import com.deotis.digitalars.util.common.CommonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description Base external WMS rest communication service
 */

@Slf4j
@Service
public class WmsExternalService {
	
	private final DeotisTemplate deotisTemplate;
	private final WmsProperties wmsProperties;
	private final UPlusLogService uplusLogService;

	public WmsExternalService(DeotisTemplate deotisTemplate, WmsProperties wmsProperties, UPlusLogService uplusLogService) {
		this.deotisTemplate = deotisTemplate;
		this.wmsProperties = wmsProperties;
		this.uplusLogService = uplusLogService;
	}
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("YYYYMMddHHmmssSSS").toFormatter();

	/**
	 * get usid from crid (crid로부터 usid)
	 * @return WmsResponseEntity
	 * @param String crid
	 * @throws WmsException 
	 */
	public WmsResponseEntity getUsidFromCrid(HttpServletRequest request, String crid) throws WmsException{
		
		ResponseEntity<WmsResponseEntity> response = null;
		
		LogData logData = new LogData();
		String devInfo = CommonUtil.getDeviceInfo(request);
		
		String[] server = wmsProperties.getServer().get("balancer");
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.CRID_QUERY.getEndUrl());
		uriBuilder.port(server[1]);
		
		log.info("request SID from crid:{}, sessionId:{}", crid, RequestContextHolder.getRequestAttributes().getSessionId());
		
		// 요청시간
		LocalDateTime reqNow = LocalDateTime.now();
		String requestTime = reqNow.format(formatter);
		
		try {
			
			JSONObject param = new JSONObject();
	
			param.put("Crid", crid);

			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);
			
			response = deotisTemplate.exchange(
							uriBuilder.build().toUriString(),
							HttpMethod.POST,
							httpEntity,
							new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			
			WmsResponseEntity result =  response.getBody();
			if(result.getResult() != null && "0".equals(result.getResult())) {
				logData = CommonUtil.setTloLog(WMS.CRID_QUERY.name(), requestTime, responseTime, TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg());
			} else {
				logData = CommonUtil.setTloLog(WMS.CRID_QUERY.name(), requestTime, responseTime, TLO.CODE_70200007.getCode(), TLO.CODE_70200007.getApiRsp(), TLO.CODE_70200007.getErrMsg());	
			}

			logData.setCrid(crid);
			logData.setDevInfo(devInfo);
			
			uplusLogService.putLogMsg(logData);

		}catch(RuntimeException e){
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
						
			// TLO 로그
			logData = CommonUtil.setTloLog(WMS.CRID_QUERY.name(), requestTime, responseTime, TLO.CODE_70200006.getCode(), TLO.CODE_70200006.getApiRsp(), TLO.CODE_70200006.getErrMsg());
			logData.setCrid(crid);
			logData.setDevInfo(devInfo);
			
			uplusLogService.putLogMsg(logData);
						
			throw new WmsException(crid);
		}

		return response.getBody();
	}
	
	/**
	 * WMS wasStart (가동 시작)
	 * @return WmsResponseEntity
	 * @param String crid, String deviceCode
	 * @throws WmsException 
	 */
	public WmsResponseEntity setWasStart(WmsRequestEntity requestEntity, String devInfo, String crid) throws WmsException {
		
		WmsResponseEntity result = null;

		LogData logData = new LogData();
		
		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.WAS_START.getEndUrl());
		uriBuilder.port(server[1]);
		
		log.info("request WasStart URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		// 요청시간
		LocalDateTime reqNow = LocalDateTime.now();
		String requestTime = reqNow.format(formatter);
					
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("launcherName", requestEntity.getLauncherName());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			
			// TLO 로그
			logData = CommonUtil.setTloLog(WMS.WAS_START.name(), requestTime, responseTime, TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg());			
			logData.setDevInfo(devInfo);
			logData.setCrid(crid);
			logData.setTransactionId(requestEntity.getSid());
			
			uplusLogService.putLogMsg(logData);
						
			result = response.getBody();
			
		}catch(RuntimeException e){
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
						
			// TLO 로그
			logData = CommonUtil.setTloLog(WMS.WAS_START.name(), requestTime, responseTime, TLO.CODE_70200012.getCode(), TLO.CODE_70200012.getApiRsp(), TLO.CODE_70200012.getErrMsg());
			logData.setDevInfo(devInfo);
			logData.setCrid(crid);
			logData.setTransactionId(requestEntity.getSid());
			
			uplusLogService.putLogMsg(logData);
						
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());
		}	

		return result;
	}
	
	/**
	 * Alive Check (WMS 가동 상태 확인)
	 * @return WmsResponseEntity
	 * @param String usid
	 * @throws WmsException 
	 */
	public WmsResponseEntity getAliveCheck(String usid, String devInfo, String crid) throws WmsException {
		
		ResponseEntity<WmsResponseEntity> response = null;
		
		LogData logData = new LogData();
		
		String[] server = wmsProperties.getServer().get("balancer");
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.ALIVE_CHECK.getEndUrl());
		uriBuilder.port(server[1]);
		
		log.info("request WasStart from USID:{},  sessionId:{}", usid, RequestContextHolder.getRequestAttributes().getSessionId());
		
		// 요청시간
		LocalDateTime reqNow = LocalDateTime.now();
		String requestTime = reqNow.format(formatter);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", usid);
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);
			
			log.debug(uriBuilder.build().toUriString());

			response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			
			// TLO 로그
	    	logData = CommonUtil.setTloLog(WMS.ALIVE_CHECK.name(), requestTime, responseTime, TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg());	
			logData.setDevInfo(devInfo);
			logData.setCrid(crid);
			logData.setTransactionId(usid);
			
			uplusLogService.putLogMsg(logData);
			
		}catch(RuntimeException e){
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
						
			// TLO 로그
			logData = CommonUtil.setTloLog(WMS.ALIVE_CHECK.name(), requestTime, responseTime, TLO.CODE_70200008.getCode(), TLO.CODE_70200008.getApiRsp(), TLO.CODE_70200008.getErrMsg());
			logData.setDevInfo(devInfo);
			logData.setCrid(crid);
			logData.setTransactionId(usid);
			
			uplusLogService.putLogMsg(logData);
			
			throw new WmsException(usid);
		}	
		
		log.debug("response:{}", response);

		return response.getBody();
	}
	
	/**
	 * Call End (통화 종료)
	 * @return WmsResponseEntity
	 * @param String usid
	 * @throws WmsException 
	 */
	public WmsResponseEntity setCallEnd(WmsRequestEntity requestEntity) throws WmsException{
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.UNBIND.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}

		WmsResponseEntity result = null;
		
		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.UNBIND.getEndUrl());
		uriBuilder.port(server[1]);
		
		log.info("request CallEnd URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());

			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});

			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.UNBIND.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
						
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = setCallEnd(requestEntity);
			}else {
				result = response.getBody();
			}
		}catch(ResourceAccessException e){
			
			// 응답시간 및 결과
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.UNBIND.name(), "", responseTime, TLO.CODE_70200009.getCode(), TLO.CODE_70200009.getApiRsp(), TLO.CODE_70200009.getErrMsg()));
			
			log.debug("WMS Connection ResourceAccessException[setCallEnd]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = setCallEnd(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}	
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), e.getMessage());	
		}	

		
		return result;
	}

	/**
	 * CTI Wait Time (상담사 대기시간)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity getCtiWaitTime(WmsRequestEntity requestEntity) throws WmsException {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.CTI_WAIT.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("getCtiWaitTime WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.CTI_WAIT.getEndUrl());
		uriBuilder.port(server[1]);
		
		log.info("request CTI Wait Time URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("QDN", requestEntity.getQdn());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("TimeoutMs", requestEntity.getTimeoutMs());
			
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);		
			
			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.CTI_WAIT.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = getCtiWaitTime(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[getCtiWaitTime]");
			
			// 응답시간 및 결과 (보류, 사용시 TLO 코드 확인 필요)
			//LocalDateTime resNow = LocalDateTime.now();
			//String responseTime = resNow.format(formatter);
			//uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.UNBIND.getCode(), "", responseTime, TLO.CODE_70200008.getCode(), TLO.CODE_70200008.getApiRsp(), TLO.CODE_70200008.getErrMsg()));

						
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = getCtiWaitTime(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * Call Transfer(호전환)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity setCallTranfer(WmsRequestEntity requestEntity) throws WmsException {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.CALL_TRANFER.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("setCallTranfer WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.CALL_TRANFER.getEndUrl());
		uriBuilder.port(server[1]);

		log.info("request Call Transfer URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("VDN", requestEntity.getVdn());
			param.put("QDN", requestEntity.getQdn());
			param.put("UUI", requestEntity.getUui());
			param.put("TimeoutMs", requestEntity.getTimeoutMs());
			param.put("Trid", requestEntity.getTrid());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);
			
			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.CALL_TRANFER.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));	
						
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = setCallTranfer(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[setCallTranfer]");
			
			// 응답시간 및 결과 (보류, 사용시 TLO 코드 확인 필요)
			//LocalDateTime resNow = LocalDateTime.now();
			//String responseTime = resNow.format(formatter);
			//uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.CALL_TRANFER.getCode(), "", responseTime, TLO.CODE_70200008.getCode(), TLO.CODE_70200008.getApiRsp(), TLO.CODE_70200008.getErrMsg()));

			
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = setCallTranfer(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * IVR 메세지 전송
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity goToEnd(WmsRequestEntity requestEntity) throws WmsException {

		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.GO_TO_END.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}

		WmsResponseEntity result = null;
		
		log.debug("goToEnd WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.GO_TO_END.getEndUrl());
		uriBuilder.port(server[1]);

		log.info("request Go To End URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			String message = requestEntity.getMessage();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("Message", isValidJson(message) ? message.replaceAll("\"", "'") : message);
			//param.put("Message", isValidJson(message) ? new JSONObject(message) : message);
			param.put("ResolvedRespCode", requestEntity.getResolvedRespCode());
			
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.GO_TO_END.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));	
						
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = goToEnd(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			
			// 응답시간 및 결과
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.GO_TO_END.name(), "", responseTime, TLO.CODE_70200010.getCode(), TLO.CODE_70200010.getApiRsp(), TLO.CODE_70200010.getErrMsg()));

			
			log.debug("WMS Connection ResourceAccessException[goToEnd]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = goToEnd(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * IVR userData save
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity userDataSave(WmsRequestEntity requestEntity) throws WmsException {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.USER_DATA_SAVE.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("userDataSave WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.USER_DATA_SAVE.getEndUrl());
		uriBuilder.port(server[1]);

		log.info("request userDataSave URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			SecretEntity secret = SessionHandler.getSecretEntity();
			
			param.put("Ani", secret.getAni());
			param.put("USID", requestEntity.getSid());
			param.put("UserData", requestEntity.getUserData());
			param.put("AppBindDetails", requestEntity.getAppBindDetails());
			param.put("TimeoutSec", requestEntity.getTimeoutSec());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.USER_DATA_SAVE.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));	
						
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = userDataSave(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS ResourceAccessException[userDataSave]. Change to the Balancer and recursive call.");
			
			// 응답시간 및 결과 (보류, 사용시 TLO 코드 확인 필요)
			//LocalDateTime resNow = LocalDateTime.now();
			//String responseTime = resNow.format(formatter);
			//uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.USER_DATA_SAVE.name(), "", responseTime, TLO.CODE_70200008.getCode(), TLO.CODE_70200008.getApiRsp(), TLO.CODE_70200008.getErrMsg()));

			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = userDataSave(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * SendVariable(IVR)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity sendVariable(WmsRequestEntity requestEntity) throws WmsException {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.SEND_VARIABLE.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("sendVariable WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.SEND_VARIABLE.getEndUrl());
		uriBuilder.port(server[1]);

		log.info("request sendVariable URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			String message = requestEntity.getMessage();

			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("Type", requestEntity.getType());
			param.put("Message", message);
			//param.put("Message", isValidJson(message) ? new JSONObject(message) : message);
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.SEND_VARIABLE.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));	
						
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = sendVariable(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			
			// 응답시간 및 결과 (보류, 사용시 TLO 코드 확인 필요)
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.SEND_VARIABLE.name(), "", responseTime, TLO.CODE_70200011.getCode(), TLO.CODE_70200011.getApiRsp(), TLO.CODE_70200011.getErrMsg()));
			
			log.debug("WMS ResourceAccessException[sendVariable]. Change to the Balancer and recursive call.");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = sendVariable(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * IVR check Web Live status
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity webLive(WmsRequestEntity requestEntity) throws WmsException {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.getLogData().setModuleApi(WMS.WEB_LIVE.name());
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("webLive WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WMS.WEB_LIVE.getEndUrl());
		uriBuilder.port(server[1]);

		log.info("request webLive URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();

			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);
			
			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			// TLO 로그
			uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.WEB_LIVE.name(), "", "", TLO.CODE_20000000.getCode(), TLO.CODE_20000000.getApiRsp(), TLO.CODE_20000000.getErrMsg()));	
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = webLive(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS ResourceAccessException[webLive]. Change to the Balancer and recursive call.");
			
			// 응답시간 및 결과 (보류, 사용시 TLO 코드 확인 필요)
			//LocalDateTime resNow = LocalDateTime.now();
			//String responseTime = resNow.format(formatter);
			//uplusLogService.putLogMsg(CommonUtil.setTloLog(WMS.WEB_LIVE.name(), "", responseTime, TLO.CODE_70200011.getCode(), TLO.CODE_70200011.getApiRsp(), TLO.CODE_70200011.getErrMsg()));
			
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode("balancer");
				result = webLive(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * Check for deviceCode changed
	 * @return boolean
	 * @param WmsResponseEntity response, WmsRequestEntity requestEntity
	 */
	private boolean checkChangedDevice(WmsResponseEntity response, WmsRequestEntity requestEntity){
		
		//WMSAccessDeviceCode has been changed
		if("2".equals(response.getResult()) && "9510".equals(response.getMessage())) {
			
			String changedDeviceCode = response.getWmsAccessDeviceCode();
			log.info("######## checkChangedDevice ######## new DeviceCodeNumber is {}", changedDeviceCode);
			setSessionDeviceChange(changedDeviceCode);
			
			requestEntity.setWmsAccessDeviceCode(changedDeviceCode);

			return true;

		}else {
			return false;
		}
	}

	/**
	 * Set deviceCode changed to session model
	 * @return void
	 * @param String deviceCode
	 */
	private void setSessionDeviceChange(String deviceCode) {

		UserEntity entity = SessionHandler.getSessionEntity();
		SecretEntity secretEntity = SessionHandler.getSecretEntity();

		log.debug(" ######## setSessionDeviceChange ######## sid:{}", entity.getSid());

		entity.setWmsAccessDeviceCode(deviceCode);
		secretEntity.setWmsAccessDeviceCode(deviceCode);
		
		SessionHandler.setSecretEntity(secretEntity);
	}
	
	/**
	 * Check DeviceCode has been changed
	 * @return void
	 * @param WmsResponseEntity
	 */
	private void checkDeviceCode(WmsResponseEntity response){
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();
		//장비 번호를 -1을 받을 경우는 call end
		if("-1".equals(response.getWmsAccessDeviceCode())) {
			log.info(" ######## response WMSdeviceCode recieve -1. Just set session End ########");
			setSessionEnd();
		}else {
			if(!secretEntity.getWmsAccessDeviceCode().equals(response.getWmsAccessDeviceCode())) {
				log.info("######## DeviceChange before:{}, after:{}", secretEntity.getWmsAccessDeviceCode(), response.getWmsAccessDeviceCode());
				setSessionDeviceChange(response.getWmsAccessDeviceCode());
			}
			
			SessionHandler.getSessionEntity().setWmsConnectFailCount(0); // 정상 연결인 경우 실패 카운트 초기화
		}
	}
	
	/**
	 * Set WMS connection fail count
	 * @return boolean
	 * @throws InterruptedException 
	 */
	private boolean checkConnectionFailCount(){
		UserEntity entity = SessionHandler.getSessionEntity();
		
		if(entity.getWmsConnectFailCount() < 2) {
			
			try {
				log.info(" ######## WMS Connection is fail. Just WmsConnectFailCount+1 ########");
				Thread.sleep(1500); //IVR 재 커넥션 시간
				entity.setWmsConnectFailCount(entity.getWmsConnectFailCount()+1);
			} catch (InterruptedException e) {
				log.error(" ######## InterruptedException {} ########", e.getMessage());
				return true;
			}
			
			return true;
			
		}else {
			log.debug(" ######## WMS Connection fail count is full. Just set session End ########");
			setSessionEnd();
			return false;
		}
	}
	
	/**
	 * Set Session End by WMS Connection
	 * @return void
	 */
	private void setSessionEnd() {
		
		if(!SYSTEM_TEST_MODE) {
			UserEntity entity = SessionHandler.getSessionEntity();
			
			entity.setRecieve_callend(true);
		}
	}
	
	
	private WmsResponseEntity getTestWmsResult(WmsRequestEntity requestEntity) {
		
		WmsResponseEntity result = new WmsResponseEntity();
		
		result.setResult("0");
		result.setUsid(requestEntity.getSid());
		result.setMessage("test message");
		result.setWmsAccessDeviceCode(requestEntity.getWmsAccessDeviceCode());
		result.setDetailMsg(null);

		
		return result;
	}
	
	private boolean isValidJson(String jsonVal) {
		
		try {
			new JSONObject(jsonVal);
		}catch(JSONException e) {
			return false;
		}
		return true;
	}
}