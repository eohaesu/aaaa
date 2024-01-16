package com.deotis.digitalars.security.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.mapper.rest.WmsInternalRedisMapper;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.system.handler.SessionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description logout additional handler
 * 
**/
@Slf4j
public class AuthenticationLogoutHandler implements LogoutHandler {
	
	@Autowired
	private FindByIndexNameSessionRepository<? extends Session> sessionRepository;
	@Autowired
	private WmsInternalRedisMapper redisMapper;
	@Autowired
	private RedisTemplateService redisTemplateService;
	
	@Value("${authorize.session.mode}")
	private String AUTHORIZE_SESSION_MODE;
	
	@Value("${wms.internal.event.redis.type}")
	private String WMS_INTERNAL_EVENT_REDIS_TYPE;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
		if(authentication != null && SessionHandler.getSessionEntity() != null) {
			
			String sid = SessionHandler.getSessionEntity().getSid();
			
			if("hash".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				redisTemplateService.deleteOptHash(sid, CommonConstants.MOS_EVENT_CALLEND);
				redisTemplateService.deleteOptHash(sid, CommonConstants.MOS_EVENT_CONTROL);
				redisTemplateService.deleteOptHash(sid, CommonConstants.MOS_EVENT_WAITTIME);
			}else if("repo".equals(WMS_INTERNAL_EVENT_REDIS_TYPE)) {
				redisMapper.deleteById(sid);
			}
			log.info("Logout SessionId : [{}], SID : [{}]", request.getSession().getId(), sid);

			//delete sid from concurrent call end map
			SessionHandler.removeSessionEntity();
			
			//authorize session tactics
			if("both".equals(AUTHORIZE_SESSION_MODE)) {
				//delete all session from repository by sid 
				deleteSessionRepository(SessionHandler.getSessionEntity().getSid());
			}
		}
			
	}
	
	private void deleteSessionRepository(String sid) {
		sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid)
		.keySet()
		.forEach(session -> sessionRepository.deleteById(session));
	}

}
