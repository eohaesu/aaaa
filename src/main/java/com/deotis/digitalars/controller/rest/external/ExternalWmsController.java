package com.deotis.digitalars.controller.rest.external;

import java.util.Collections;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.wms.WmsRequestEntity;
import com.deotis.digitalars.model.wms.WmsResponseEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.rest.external.WmsExternalService;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/wms/external")
public class ExternalWmsController {

	private final WmsExternalService wmsService;
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	/**
	 * wms call end
	 * @return boolean
	 */
	@GetMapping(value = "/callEnd")
	public boolean callEnd(){
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS callEnd");
			return true;
		}
		
		try {
			
			UserEntity entity = SessionHandler.getSessionEntity();
			
			WmsRequestEntity params = new WmsRequestEntity();
	    	
	    	params.setSid(entity.getSid());
	    	params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
	    	
	    	log.debug("WMS ExternalController callEnd parameters : {}", params);

			WmsResponseEntity result = wmsService.setCallEnd(params);
			
			return result.getResult() != null && "0".equals(result.getResult()) ? true : false;

		} catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("callEnd-Exception:{}", e.getMessage());
			}
			log.error("Error : Set Callend Failed USID : {}", e.getSid());
			return false;
		}
	}
	
	/**
	 * wms connectCounselorWaitTime
	 * @param tocData
	 * @return boolean
	 */
	@PostMapping(value = "/connectCounselorWaitTime")
	public Map<String, Object> connectCounselorWaitTime(
			@RequestParam(value = "QDN", defaultValue = "") String qdn,
			@RequestParam(value = "TimeoutMS", defaultValue = "5000") String timeoutMs){
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS connectCounselorWaitTime. QDN:{}, TimeoutMS:{}", qdn, timeoutMs);
			return Collections.emptyMap();
		}

		try {
			
			UserEntity entity = SessionHandler.getSessionEntity();

			WmsRequestEntity params = new WmsRequestEntity();
	    	
	    	params.setSid(entity.getSid());
	    	params.setQdn(qdn);
	    	params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
	    	params.setTimeoutMs(timeoutMs);
	    	
	    	log.debug("WMS ExternalController connectCounselorWaitTime parameters : {}", params);

			WmsResponseEntity result = wmsService.getCtiWaitTime(params);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> detailMsg = new ObjectMapper().convertValue(result.getDetailMsg(), Map.class);
			
			if(result.getResult() != null && "0".equals(result.getResult())){
				return detailMsg;
			}else {
				return Collections.emptyMap();
			}
			
		} catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("connectCounselorWaitTime-Exception:{}", e.getMessage());
			}
			log.error("Error : connectCounselorWaitTime Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			return Collections.emptyMap();
		}

	}

	
	/**
	 * Call Transfer
	 * @param vdn
	 * @param qdn
	 * @param uui
	 * @param trid
	 * @return boolean
	 */
	@PostMapping(value = "/callTransfer")
	public boolean callTransfer(
				@RequestParam(value = "counselorCode", defaultValue = "") String counselorCode,
				@RequestParam(value = "VDN", defaultValue = "") String vdn,
				@RequestParam(value = "QDN", defaultValue = "") String qdn,
				@RequestParam(value = "UUI", defaultValue = "") String uui,
				@RequestParam(value = "TimeoutMS", defaultValue = "5000") String timeoutMs,
				@RequestParam(value = "Trid", defaultValue = "0") String trid
			) {
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS callTransfer. counselorCode:{}, VDN:{}, QDN:{}, UUI:{}",counselorCode, vdn, qdn, uui);
			return true;
		}
		
		try {
			UserEntity entity = SessionHandler.getSessionEntity();
			//SecretEntity secretEntity = SessionHandler.getSecretEntity();
			
			WmsRequestEntity params = new WmsRequestEntity();

			//skill코드 포함 시 제외
			if(counselorCode.contains("|")) {
				counselorCode = counselorCode.split("\\|")[0];
			}
				
	    	params.setVdn(counselorCode);
	    	params.setQdn(counselorCode);
	   	
	    	params.setSid(entity.getSid());
	    	params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
	    	params.setUui(uui);
	    	params.setTimeoutMs(timeoutMs);
	    	params.setTrid(trid);
	    	WmsResponseEntity result = wmsService.setCallTranfer(params);
	    	
			
	    	log.debug("WMS ExternalController callTransfer parameters : {}", params);
			
			return result.getResult() != null && "0".equals(result.getResult()) ? true : false;
			
			
		}catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("callTransfer-Exception:{}", e.getMessage());
			}
			log.error("Error : Call Transfer Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			return false;
		}
	}
	
	/**
	 * Go to End
	 * @param message
	 * @return boolean
	 */
	@PostMapping(value = "/goToEnd")
	public boolean goToEnd(@RequestParam(value = "message", defaultValue = "") String message) {
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS goToEnd. message:{}", message);
			return true;
		}
		
		try {
			UserEntity entity = SessionHandler.getSessionEntity();
			SecretEntity secretEntity = SessionHandler.getSecretEntity();
			WmsRequestEntity params = new WmsRequestEntity();
			
			JSONObject msg = new JSONObject();
			msg.put("svcId", secretEntity.getSvcIdList()[secretEntity.getCurrentSvcNo()-1]); // 서비스ID
			msg.put("message", "");
			msg.put("param1", "");
			
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
			params.setMessage(msg.toString());
			params.setResolvedRespCode(message); // SUCCESS(처리성공), NO_INPUT(미입력,타임아웃), INPUT_FAIL(입력오류 및 API처리실패)
			
			// 서비스 진행순서 정리
			if(secretEntity.getTotalSvcNo() > 1 && secretEntity.getCurrentSvcNo() != 0) {
				secretEntity.setCurrentSvcNo(secretEntity.getCurrentSvcNo()+1);
			} else {
				secretEntity.setSvcId(null);
				secretEntity.setTotalSvcNo(0);
				secretEntity.setCurrentSvcNo(0);
			}
			
			log.debug("WMS External goToEnd parameters : {}", params);
			
			WmsResponseEntity result = wmsService.goToEnd(params);
			
			return result.getResult() != null && "0".equals(result.getResult()) ? true : false;
		}catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("goToEnd-Exception:{}", e.getMessage());
			}
			log.error("Error : Go to End Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			return false;
		}
	}
	
	/**
	 * userDataSave
	 * @param message
	 * @return boolean
	 */
	@PostMapping(value = "/userDataSave")
	public boolean userDataSave(
			@RequestParam(value = "userData", defaultValue = "") String userData,
			@RequestParam(value = "appBindDetails", defaultValue = "") String appBindDetails,
			@RequestParam(value = "TimeoutSec", defaultValue = "5") String timeoutSec
			) {
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS userDataSave. userData:{}, appBindDetails:{}, timeoutSec:{}");
			return true;
		}
		
		try {
			
			UserEntity entity = SessionHandler.getSessionEntity();
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
			params.setUserData(userData);
			params.setAppBindDetails(appBindDetails);
			params.setTimeoutSec(timeoutSec);
			
			log.debug("WMS ExternalController userDataSave parameters : {}", params);
			
			WmsResponseEntity result = wmsService.userDataSave(params);
			
			return result.getResult() != null && "0".equals(result.getResult()) ? true : false;
		}catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("userDataSave-Exception:{}", e.getMessage());
			}
			log.error("Error : userDataSave Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			return false;
		}
	}
	
	/**
	 * Send Variable
	 * @param message
	 * @param type
	 * @return boolean
	 */
	@PostMapping(value = "/sendVariable")
	public boolean sendVariable(
			@RequestParam(value = "Message", defaultValue = "") String message,
			@RequestParam(value = "Type", defaultValue = "2") int type
			) {
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS sendVariable. message:{}, type:{}", message, type);
			return true;
		}
		
		try {
			
			UserEntity entity = SessionHandler.getSessionEntity();
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
			params.setType(type);
			params.setMessage(message);

			log.debug("WMS ExternalController sendVariable parameters : {}", params);
			
			WmsResponseEntity result = wmsService.sendVariable(params);
			
			return result.getResult() != null && "0".equals(result.getResult()) ? true : false;
		}catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("sendVariable-Exception:{}", e.getMessage());
			}
			log.error("Error : sendVariable Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			return false;
		}
	}
	
	/**
	 * Web Live
	 * @return boolean
	 */
	@PostMapping(value = "/webLive")
	public boolean webLive() {
		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS webLive");
			return true;
		}
		
		try {
			
			UserEntity entity = SessionHandler.getSessionEntity();
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());

			log.debug("WMS ExternalController webLive parameters : {}", params);
			
			WmsResponseEntity result = wmsService.webLive(params);
			
			return result.getResult() != null && "0".equals(result.getResult()) ? true : false;
		}catch (WmsException e) {
			if(log.isDebugEnabled()){
				log.error("webLive-Exception:{}", e.getMessage());
			}
			log.error("Error : webLive Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			return false;
		}
	}
}
