package com.deotis.digitalars.controller.rest.internal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.mapper.rest.WmsInternalRedisMapper;
import com.deotis.digitalars.model.wms.WmsInternalEntity;
import com.deotis.digitalars.model.wms.WmsInternalRedisEntity;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.service.rest.internal.WmsInternalService;
import com.deotis.digitalars.system.rest.entity.ResponseEntityWrapper;
import com.deotis.digitalars.util.collection.DMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description InBound Event controller from WMS
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/wms/internal")
public class InternalWmsController {
	
	@Value("${wms.internal.event.redis.type}")
	private String WMS_INTERNAL_EVENT_REDIS_TYPE;

	private final WmsInternalService wmsService;
	private final WmsInternalRedisMapper redisMapper;
	private final RedisTemplateService redisTemplateService;
	
	/**
	 * wms pingpong confirm
	 * @param wcseq
	 * @param input_type
	 * @param tracecode
	 */
	@GetMapping(value = "/event/pingpong")
	public ResponseEntityWrapper<WmsInternalEntity> pingpong(HttpServletRequest request){
		
		WmsInternalEntity entity = new WmsInternalEntity(request.getRemoteAddr(), request.getSession().getId());
		log.debug("WMS pingpong confirm event : [{}]", entity);
		return new ResponseEntityWrapper<WmsInternalEntity>(entity);		
	}	
	
	/**
	 * wms 외부 이벤트 처리 통화 종료 (call end)
	 * @param request
	 */
	@GetMapping(value = "/event/callend")
	public ResponseEntityWrapper<WmsInternalEntity> callEndEvent(HttpServletRequest request,
			@RequestParam(value = "event", required = true) String event,
			@RequestParam(value = "usid", required = true) String usid
			){

		log.info("WMS Inbound Call End Event : [{}] sid : [{}]", event, usid);
		
		WmsInternalEntity entity = new WmsInternalEntity(request.getRemoteAddr(), request.getSession().getId());
		
		String icid = wmsService.getIcidByUSID(usid);
		
		//icid SET TLO LOG
		
		if (StringUtils.hasLength(usid) && CommonConstants.MOS_EVENT_CALLEND.equals(event)) {
			if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				
				if(wmsService.isExistSessionByUSID(usid)) {
					redisTemplateService.addHashOptValue(usid, event, true, 2);
				}else {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
			}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				
				if(wmsService.isExistSessionByUSID(usid)) {
					JSONObject obj = new JSONObject();
					
					obj.put("callEnd", "true");
					
					WmsInternalRedisEntity wmsInternalRedisEntity = new WmsInternalRedisEntity(usid, CommonConstants.MOS_EVENT_CALLEND, obj.toString());
					
					redisMapper.save(wmsInternalRedisEntity);
				}else {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
			}else {
				//set session
				boolean result = wmsService.addCallEndSession(usid);
				if(!result) {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
				
			}
			
			wmsService.deleteSessionRepository(usid);
		}
		return new ResponseEntityWrapper<WmsInternalEntity>(entity);		
	}
	
	/**
	 * wms 외부 이벤트 처리 상담사 대기시간 (Wait Time)
	 * @param request
	 */
	@GetMapping(value = "/event/waitTime")
	public ResponseEntityWrapper<WmsInternalEntity> waitTime(HttpServletRequest request,
			@RequestParam(value = "event", required = true) String event,
			@RequestParam(value = "usid", required = true) String usid,
			@RequestParam(value = "waitTime", required = true) String waitTime,
			@RequestParam(value = "waitCount", required = true) String waitCount
			){

		log.info("WMS Inbound Wait Time Event : [{}] sid : [{}]", event, usid);
		
		WmsInternalEntity entity = new WmsInternalEntity(request.getRemoteAddr(), request.getSession().getId());
		
		if (StringUtils.hasLength(usid) && CommonConstants.MOS_EVENT_WAITTIME.equals(event)) {
			
			if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				if(wmsService.isExistSessionByUSID(usid)) {
					JSONObject obj = new JSONObject();
					
					obj.put("waitTime", waitTime);
					obj.put("waitCount", waitCount);
					
					redisTemplateService.addHashOptValue(usid, event, obj.toString(), 2);
				}else {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
			}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				if(wmsService.isExistSessionByUSID(usid)) {
					JSONObject obj = new JSONObject();
					
					obj.put("waitTime", waitTime);
					obj.put("waitCount", waitCount);
					
					WmsInternalRedisEntity wmsInternalRedisEntity = new WmsInternalRedisEntity(usid, CommonConstants.MOS_EVENT_WAITTIME, obj.toString());
					
					redisMapper.save(wmsInternalRedisEntity);
				}else {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
				
			}else {
				//set session
				entity.setProcessResult(wmsService.addWaitTimeSession(usid, waitTime, waitCount) ? true : false);		
			}
			
		}
		return new ResponseEntityWrapper<WmsInternalEntity>(entity);		
	}
	
	/**
	 * wms 외부 이벤트 처리 IVR CONTROL (uri control)
	 * @param request
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@PostMapping(value = "/event/control")
	public ResponseEntityWrapper<WmsInternalEntity> inBoundServiceEvent(HttpServletRequest request, @RequestBody String data ) throws JsonMappingException, JsonProcessingException{

		WmsInternalEntity entity = new WmsInternalEntity(request.getRemoteAddr(), request.getSession().getId());

		if(StringUtils.hasLength(data)) {
			
			if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				
				JSONObject result = new JSONObject(data);
				
				String usid = (String) result.get("usid");
				String event = (String) result.get("event");
				
				log.info("WMS Inbound Control Event : [{}] sid : [{}] uri : [{}], param : [{}]", result.get("event"), result.get("usid"), result.get("uri"), result.get("param"));
				
				if (StringUtils.hasLength(usid) && CommonConstants.MOS_EVENT_CONTROL.equals(event) && wmsService.isExistSessionByUSID(usid)) {
					redisTemplateService.addHashOptValue(usid, event,  result.get("param").toString(), 5);
				}else {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
				
			}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				
				JSONObject result = new JSONObject(data);
				
				String usid = (String) result.get("usid");
				String event = (String) result.get("event");
				
				log.info("WMS Inbound Control Event : [{}] sid : [{}] uri : [{}], param : [{}]", result.get("event"), result.get("usid"), result.get("uri"), result.get("param"));
				
				if (StringUtils.hasLength(usid) && CommonConstants.MOS_EVENT_CONTROL.equals(event) && wmsService.isExistSessionByUSID(usid)) {
					WmsInternalRedisEntity wmsInternalRedisEntity = new WmsInternalRedisEntity(usid, CommonConstants.MOS_EVENT_CONTROL, result.get("param").toString());
					redisMapper.save(wmsInternalRedisEntity);
				}else {
					log.info("Already the session has been invalidated : sid : [{}]", usid);
				}
			}else {
				@SuppressWarnings("unchecked")
				Map<String, Object> result = new ObjectMapper().readValue(data, Map.class);

				String usid = (String) result.get("usid");
				String event = (String) result.get("event");
				
				log.info("WMS Inbound Control Event : [{}] sid : [{}] uri : [{}], param : [{}]", result.get("event"), result.get("usid"), result.get("uri"), result.get("param"));

				if (StringUtils.hasLength(usid) && CommonConstants.MOS_EVENT_CONTROL.equals(event)) {
					
					DMap<String, Object> ivrData = new DMap<String, Object>();

					ivrData.putOrigin("uri", result.get("uri") != null ? result.get("uri") : "");
					ivrData.putOrigin("param", result.get("param") != null ? result.get("param") : "");

					//set session
					entity.setProcessResult(wmsService.addIVRControl(usid, ivrData) ? true : false);
				}
			}
		}

		return new ResponseEntityWrapper<WmsInternalEntity>(entity);		
	}
}
