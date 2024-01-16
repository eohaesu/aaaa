package com.deotis.digitalars.controller.operation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.mapper.rest.WmsInternalRedisMapper;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.wms.WmsInternalRedisEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.common.PollingResultService;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author jongjin
 * @description invoke short, server sent event.
 */

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/poll")
@RestController
public class PollingController {
	
	@Value("${system.polling.thread.timeout}")
	private long POLLING_THREAD_TIMEOUT;
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	@Value("${wms.internal.event.redis.type}")
	private String WMS_INTERNAL_EVENT_REDIS_TYPE;
	
	private final PollingResultService pollingResultService;
	private final WmsInternalRedisMapper redisMapper;
	private final RedisTemplateService redisTemplateService;
	/**
	 * short Polling
	 * 
	 * @return Map<String, Object>
	 */
	@GetMapping(value = "/short")
	public Map<String, Object> shortPoll(HttpServletRequest request) {
		
		WeakHashMap<String, Object> result = new WeakHashMap<String, Object>();
		
		if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {

			String usid = SessionHandler.getSessionEntity().getSid();
			
			if(redisTemplateService.getOptHashSize(usid) > 0) {
				if(redisTemplateService.getOptHashHasKey(usid, CommonConstants.MOS_EVENT_CALLEND)) {
					log.debug("######## Recieve Call End (Sid : {}) ########", usid);
					result.put("isCallEnd", true);
				}else if(redisTemplateService.getOptHashHasKey(usid, CommonConstants.MOS_EVENT_WAITTIME)) {
					JSONObject waitObj = new JSONObject(redisTemplateService.getHashOptValue(usid, CommonConstants.MOS_EVENT_WAITTIME).toString());
					log.debug("######## Recieve Counselor Wait Time (Sid : {}, waitCount : {}, waitTime : {}) ########", usid, waitObj.get("waitCount"), waitObj.get("waitTime"));
					result.put("waittime", waitObj.get("waitTime"));
					result.put("waitcount", waitObj.get("waitCount"));
				}else if(redisTemplateService.getOptHashHasKey(usid, CommonConstants.MOS_EVENT_CONTROL)) {
					WeakHashMap<String, String> control = new WeakHashMap<String,String>();
					control.put("param", (String) redisTemplateService.getHashOptValue(usid, CommonConstants.MOS_EVENT_CONTROL));
					log.debug("######## Recieve IVR Control (Control : {}) ########",control.get("param"));
					result.put("control", control);
					
					// 콜봇에서 재진입한 경우 secretEntity 정보 업데이트
					try {
						SecretEntity secretEntity = SessionHandler.getSecretEntity();
						UserEntity userEntity = SessionHandler.getSessionEntity();
						
						@SuppressWarnings("unchecked")
						Map<String, String> data = new ObjectMapper().readValue(control.get("param"), Map.class);
						secretEntity.setSvcId(data.get("svcId")); 		       // 서비스ID
						secretEntity.setTitleName(data.get("titleName"));      // 제목
						secretEntity.setCustNo(data.get("custNo"));			   // 고객번호
						secretEntity.setEntrNo(data.get("entrNo"));			   // 가입번호
						secretEntity.setCustrnmBday(data.get("custrnmBday"));  // 생년월일
						userEntity.getLogData().setCustNo(data.get("custNo")); 
						userEntity.getLogData().setEntrNo(data.get("entrNo"));
					} catch (JsonProcessingException e) {
						log.warn("Can not parse params. Is params construct with json?");
						return null;
					}
				}
			}

		}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
			
			String usid = SessionHandler.getSessionEntity().getSid();
			Optional<WmsInternalRedisEntity> wmsEntity = redisMapper.findById(usid);
			if(wmsEntity.isPresent()) {
				switch(wmsEntity.get().getEventName()) {
				case CommonConstants.MOS_EVENT_CALLEND:
					log.debug("######## Recieve Call End (Sid : {}) ########", usid);
					result.put("isCallEnd", true);
					break;
				case CommonConstants.MOS_EVENT_WAITTIME:
					JSONObject waitObj = new JSONObject(wmsEntity.get().getData());
					log.debug("######## Recieve Counselor Wait Time (Sid : {}) ########", usid);
					result.put("waittime", waitObj.get("waitTime"));
					result.put("waitcount", waitObj.get("waitCount"));
					break;
				case CommonConstants.MOS_EVENT_CONTROL:
					Map<String, String> control = new HashMap<String,String>();
					control.put("param", wmsEntity.get().getData());
					log.debug("######## Recieve IVR Control (Control : {}) ########", wmsEntity.get().getData());
					result.put("control", control);
					break;
				default:
					break;
				}
			}

		}else {
			UserEntity userEntity = pollingResultService.getRepositoryEntityBySessionId(request.getSession().getId());

			//log.trace("Session attr CallEnd : [{}], SidSync CallEnd: [{}]", userEntity.isRecieve_callend(), SidSyncHandler.isCallEnd(userEntity.getSid()));
			log.trace("Just user Session Entity : [{}]", userEntity.toString());

			if((userEntity == null || userEntity.isRecieve_callend() || SessionHandler.getAuthentication() instanceof AnonymousAuthenticationToken)){
				log.debug("######## Recieve Call End (Sid : {}) ########", userEntity.getSid());
				result.put("isCallEnd", true);
			}
			
			if(userEntity.getWmsEventName() != null && CommonConstants.MOS_EVENT_CONTROL.equals(userEntity.getWmsEventName())) {
				log.debug("######## Recieve IVR Control (Control : {}) ########", userEntity.getIvrControlInfo());
				result.put("control", userEntity.getIvrControlInfo());
			}
			
			if(userEntity.getWmsEventName() != null && CommonConstants.MOS_EVENT_WAITTIME.equals(userEntity.getWmsEventName())) {
				log.debug("######## Recieve Counselor Wait Time (WaitTime : {}, WaitCount : {}) ########", userEntity.getCounselorWaitTime(), userEntity.getCounselorWaitCount());
				result.put("waittime", userEntity.getCounselorWaitTime());
				result.put("waitcount", userEntity.getCounselorWaitCount());
			}
			
		}

		return result;
	}
	
	/**
	 * check shortPolling browser token
	 * 
	 * @return boolean
	 */
	@GetMapping(value="/chktoken")
    public boolean checkBrowserToken (HttpServletRequest request, @RequestParam(value = "btoken", required = true) String btoken) {

		boolean result = btoken.equals(SessionHandler.getSecretEntity().getBtoken()) ? true : false;
		
		if(!result) {

			log.warn("Detected duplicate browser tab. USID:{}", SessionHandler.getSessionEntity().getSid());
		}
		
		return result;
	}
	

	/**
	 * Server Sent Event Polling
	 * 
	 * @return ResponseEntity<SseEmitter>
	 */
	@GetMapping(value="/emitter")
    public ResponseEntity<SseEmitter> sseEmitter (HttpServletRequest request, @RequestParam(value = "btoken", required = false) String btoken) {

		final SseEmitter emitter = new SseEmitter(POLLING_THREAD_TIMEOUT);
		
		final String sid = SessionHandler.getSessionEntity() != null ? SessionHandler.getSessionEntity().getSid() : "";

        emitter.onError(e -> {
        	log.debug("Async emitter cancel. USID:[{}], msg:[{}]", sid, e.getMessage());
        });
        emitter.onCompletion(() -> {
        	log.debug("Async emitter completion. USID:[{}]", sid);
        });
        if(!StringUtils.hasLength(sid)) {
        	
        	try {
				emitter.send(SseEmitter.event()  
				        .name("AUTHENTICATION_FAIL")
				        .data("AUTHENTICATION_FAIL", MediaType.TEXT_PLAIN));
				emitter.complete();
				
			} catch (IOException e) {
				emitter.completeWithError(e);	
			}

        }else {
        	if(btoken != null && btoken.equals(SessionHandler.getSecretEntity().getBtoken())) {

            	ExecutorService executor = Executors.newSingleThreadExecutor();
                
                executor.execute(() -> {

                	for (int i = 0; i < POLLING_THREAD_TIMEOUT/1500; i++) {
                		
                        try {           
                        	
                        	if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {

                    			if(redisTemplateService.getOptHashSize(sid) > 0) {
                    				if(redisTemplateService.getOptHashHasKey(sid, CommonConstants.MOS_EVENT_CALLEND)) {
                    					log.debug("######## Recieve Call End (Sid : {}) ########", sid);
                    					emitter.send(SseEmitter.event()  
                                                .name(CommonConstants.MOS_EVENT_CALLEND)  
                                                .data("", MediaType.TEXT_PLAIN));
                    				}else if(redisTemplateService.getOptHashHasKey(sid, CommonConstants.MOS_EVENT_WAITTIME)) {
                    					JSONObject waitObj = new JSONObject(redisTemplateService.getHashOptValue(sid, CommonConstants.MOS_EVENT_WAITTIME).toString());
                    					log.debug("######## Recieve Counselor Wait Time (Sid : {}) ########", sid);
                    					emitter.send(SseEmitter.event()  
                                                .name(CommonConstants.MOS_EVENT_WAITTIME)  
                                                .data(waitObj.get("waitCount"))); 
                    				}else if(redisTemplateService.getOptHashHasKey(sid, CommonConstants.MOS_EVENT_CONTROL)) {
                    					Map<String, String> control = new HashMap<String,String>();
                    					control.put("param", (String) redisTemplateService.getHashOptValue(sid, CommonConstants.MOS_EVENT_CONTROL));
                    					log.debug("######## Recieve IVR Control (Control : {}) ########",control.get("param"));
                    					emitter.send(SseEmitter.event()  
                                                .name(CommonConstants.MOS_EVENT_CONTROL)  
                                                .data(control)); 
                    				}
                    			}else {
                    				if(i%10 == 0){
                    					emitter.send("ThreadSecond:"+(i*500)/1000F, MediaType.TEXT_PLAIN);
                    				}
                				}

                    		}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {

                    			Optional<WmsInternalRedisEntity> wmsEntity = redisMapper.findById(sid);
                    			if(wmsEntity.isPresent()) {
                    				switch(wmsEntity.get().getEventName()) {
                    				case CommonConstants.MOS_EVENT_CALLEND:
                    					log.debug("######## Recieve Call End (Sid : {}) ########", sid);
                    					emitter.send(SseEmitter.event()  
                                                .name(CommonConstants.MOS_EVENT_CALLEND)  
                                                .data("", MediaType.TEXT_PLAIN));
                    					break;
                    				case CommonConstants.MOS_EVENT_WAITTIME:
                    					JSONObject waitObj = new JSONObject(wmsEntity.get().getData());
                    					log.debug("######## Recieve Counselor Wait Time (Sid : {}) ########", sid);
                    					emitter.send(SseEmitter.event()  
                                                .name(CommonConstants.MOS_EVENT_WAITTIME)  
                                                .data(waitObj.get("waitCount"))); 
                    					break;
                    				case CommonConstants.MOS_EVENT_CONTROL:
                    					Map<String, String> control = new HashMap<String,String>();
                    					control.put("param", wmsEntity.get().getData());
                    					log.debug("######## Recieve IVR Control (Control : {}) ########", wmsEntity.get().getData());
                    					emitter.send(SseEmitter.event()  
                                                .name(CommonConstants.MOS_EVENT_CONTROL)  
                                                .data(control)); 
                    					break;
                    				default:
                    					break;
                    				}
                    			}else {
                    				if(i%10 == 0){
                    					emitter.send("ThreadSecond:"+(i*500)/1000F, MediaType.TEXT_PLAIN);
                    				}
                				}

                    		}else {
                    			
                    			UserEntity userEntity = pollingResultService.getRepositoryEntityBySessionId(request.getSession().getId());
                            	
                            	if(userEntity == null) {
                            		emitter.complete();
                                	return;
                            	}
                    			
                    			log.trace("Async emitter USID:[{}], userEntity:[{}]", sid, userEntity);
                            	
                            	if(userEntity.isRecieve_callend() 
                            			|| CommonConstants.MOS_EVENT_CALLEND.equals(userEntity.getWmsEventName())
                            	){
                            		emitter.send(SseEmitter.event()  
                                            .name(CommonConstants.MOS_EVENT_CALLEND)  
                                            .data("", MediaType.TEXT_PLAIN));
                            	}else if(CommonConstants.MOS_EVENT_CONTROL.equals(userEntity.getWmsEventName()) && userEntity.getIvrControlInfo() != null ) {
                        			emitter.send(SseEmitter.event()  
                                            .name(CommonConstants.MOS_EVENT_CONTROL)  
                                            .data(userEntity.getIvrControlInfo())); 
                        			log.debug("Async emitter get EventName:[{}], USID:[{}], userEntity:[{}]", CommonConstants.MOS_EVENT_CONTROL, sid, userEntity);
                        		}else if(CommonConstants.MOS_EVENT_WAITTIME.equals(userEntity.getWmsEventName()) && (userEntity.getCounselorWaitCount() != null || userEntity.getCounselorWaitTime() != null )) {
                        			emitter.send(SseEmitter.event()  
                                            .name(CommonConstants.MOS_EVENT_WAITTIME)  
                                            .data(userEntity.getCounselorWaitCount())); 
                        			log.debug("Async emitter get EventName:[{}], USID:[{}], userEntity:[{}]", CommonConstants.MOS_EVENT_CONTROL, sid, userEntity);
                        		}else {
                        			if(i%10 == 0){
                    					emitter.send("ThreadSecond:"+(i*500)/1000F, MediaType.TEXT_PLAIN);
                    				}
                        		}
                    		}

                            if(i < POLLING_THREAD_TIMEOUT/1500) {
                            	TimeUnit.MILLISECONDS.sleep(1500);
                            }else {
                            	emitter.complete();
                            	return;
                            }
                            
                        } catch (IOException | NullPointerException | InterruptedException | IllegalStateException e) {
                            emitter.completeWithError(e);
                            return;
                        }
                    }

                });
                executor.shutdown();
                log.trace("SSE Excutor has been shutdown.[{}]", executor.isShutdown());
            }else {
            	
            	try {
    				emitter.send(SseEmitter.event()  
    				        .name("AUTHORIZATION_TOKEN")  
    				        .data("INVALID_TOKEN", MediaType.TEXT_PLAIN));
    				emitter.complete();
    				
    			} catch (IOException e) {
    				emitter.completeWithError(e);	
    			}
            }
        }

        return new ResponseEntity<SseEmitter>(emitter, HttpStatus.OK);
    }

	@PostMapping(value="/clearEvent")
    public boolean clearEvent (HttpServletRequest request, @RequestParam(value = "btoken", required = false) String btoken) {
		
		boolean result = true;
		
		if(btoken != null && btoken.equals(SessionHandler.getSecretEntity().getBtoken())) {
			UserEntity userEntity = SessionHandler.getSessionEntity();
			
			if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				String usid = userEntity.getSid();
				redisTemplateService.deleteOptHash(usid, CommonConstants.MOS_EVENT_CONTROL);
				redisTemplateService.deleteOptHash(usid, CommonConstants.MOS_EVENT_WAITTIME);

			}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				redisMapper.deleteById(userEntity.getSid());
			}

			log.debug("[Polling ClearEvent] Before ShowArsProgressType : [{}]", userEntity.getShowArsProgressType());
			
			userEntity.setWmsEventName(null);
			userEntity.setIvrControlInfo(null);
			userEntity.setShowArsProgressType(0);
			userEntity.setCounselorWaitTime(null);
			userEntity.setCounselorWaitCount(null);

		}

		return result;
	}


	/**
	 * DeffredResult longPolling
	 * 
	 * @return DeferredResult<ResponseEntity<?>>
	 */
	@GetMapping("/getDeferredResult")
	private DeferredResult<Object> getDeferredResult(HttpServletRequest request, @RequestParam(value = "btoken", required = false) String btoken) {
	
	    DeferredResult<Object> deferredResult = new DeferredResult<Object>(POLLING_THREAD_TIMEOUT, "timeout");
	    
	    String sid = SessionHandler.getSessionEntity().getSid();
	    
	    CompletableFuture.runAsync(() -> {
	    	
	    	for (int i = 0; i < POLLING_THREAD_TIMEOUT/500; i++) {
	    		
	            try {
	
	            	UserEntity userEntity = pollingResultService.getRepositoryEntityBySessionId(request.getSession().getId());
	            	
	            	if(userEntity == null) {
	            		deferredResult.setResult("error");
	                	return;
	            	}
	            	
	            	log.debug("async getDeferredResult USID:[{}], userEntity:[{}]", sid, userEntity);
	
	        		if(userEntity.getIvrControlInfo() != null && !deferredResult.isSetOrExpired()) {
	
	        			deferredResult.setResult(userEntity.getIvrControlInfo());
	        			return;
	        		}
	
	                if(i < POLLING_THREAD_TIMEOUT/500) {
	                	TimeUnit.MILLISECONDS.sleep(500);
	                }else {
	                	deferredResult.setResult("complete");
	                	return;
	                }
	                
	            } catch (InterruptedException e) {
	            	deferredResult.setResult("error");
	                log.debug("DeferredResult exception msg:[{}]", e.getMessage());
	                return;
	            }
	        }
	
	    });
	    
	    return deferredResult;
	}
}
