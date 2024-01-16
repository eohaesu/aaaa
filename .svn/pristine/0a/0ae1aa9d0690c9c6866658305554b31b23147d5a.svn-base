package com.deotis.digitalars.security.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.constants.ResultStrConstants;
import com.deotis.digitalars.model.wms.WmsRequestEntity;
import com.deotis.digitalars.model.wms.WmsResponseEntity;
import com.deotis.digitalars.security.model.AuthorizationEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.rest.external.WmsExternalService;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.util.collection.DMap;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description main authorize SID key manager
 */
@Slf4j
public class AuthenticationKeyManager implements AuthenticationManager {
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	@Value("${authorize.session.mode}")
	private String AUTHORIZE_SESSION_MODE;
	
	@Value("${digitalars.default.site}")
	private String DEFAULT_SITE_CODE;
	
	@Autowired
    SessionRegistry sessionRegistry;
	
	@Autowired
	FindByIndexNameSessionRepository<? extends Session> sessionRepository;
	
	@Autowired
	private WmsExternalService wmsExternalService;

    //sid를 wms로부터 유효성 검사
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
    	@SuppressWarnings("unchecked")
		DMap<String, Object> tokenParams = (DMap<String, Object>) authentication.getPrincipal(); //sid

        String sid = tokenParams.getString("sid"); //sid
        String devInfo = tokenParams.getString("devinfo");
        String crid = tokenParams.getString("crid");

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        
        SecretEntity secretEntity = null;

        if(!SYSTEM_TEST_MODE) {

        	try{
        		
        		String aliveStatus = isAliveCheck(sid, devInfo, crid);

        		if("alive".equals(aliveStatus)) {
        			//WASSTART로 ivr 데이터 생성
        			secretEntity = wasStart(sid, tokenParams);

        		}else if("duplicated".equals(aliveStatus)){
        			//authorize session tactics
        			if("after".equals(AUTHORIZE_SESSION_MODE)) {
        				throw new AuthenticationServiceException("AuthenticationService attempt to duplicate. The session will be invalidate (SID:"+sid+")");
        			}
        			 
        			//연결 상태이면 이전 security 정보를 가져온다.
        			AuthorizationEntity beforeEntity = getAuthorizationPrincipal(sid);
                	
                	if(beforeEntity == null) {
                		throw new AuthenticationServiceException("AuthenticationService attempt to duplicate before data empty (SID:"+sid+")");
                	}
                	
                	log.info("Already had the WMS WasStart SID: [{}]", sid);
                	
        			//get ivr secretEntity
        			secretEntity = beforeEntity.getSecret();
        			//set a new DeviceCode
        			secretEntity.setWmsAccessDeviceCode(tokenParams.getString("devicecode"));
        			//set a new browser token
        			secretEntity.setBtoken(tokenParams.getString("btoken"));
        		}else {
        			throw new AuthenticationServiceException("AuthenticationService AliveCheck Fail (SID:"+sid+")");
        		}
        		//TODO :특정 Role에 따라 다른 비지니스를 수행할 경우 로직 추가
        		authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
        		
        	}catch (WmsException e) {
        		log.error("WmsException : ", e.getMessage());
        		throw new AccountExpiredException("AuthenticationService Fail (SID:"+sid+")");
        	}

        //for local test mode	
        }else {

        	String unescapeUserData = null;
			String unescapeDetail = null;
			
			if(StringUtils.hasLength(tokenParams.getString("userdata"))) {
				unescapeUserData = StringEscapeUtils.unescapeHtml4(tokenParams.getString("userdata"));
			}

			if(StringUtils.hasLength(tokenParams.getString("appbinddetails"))) {
				unescapeDetail = StringEscapeUtils.unescapeHtml4(tokenParams.getString("appbinddetails"));
			}

        	secretEntity = new SecretEntity(tokenParams.getString("sitecode"), 
        			tokenParams.getString("dnis"), 
        			tokenParams.getString("ani"), 
        			tokenParams.getString("devicecode"), 
        			unescapeUserData,
        			"test",
        			unescapeDetail,
        			tokenParams.getString("wcseq"),
        			tokenParams.getString("icid"),
        			tokenParams.getString("btoken"),
        			tokenParams.getString("crid")
        			);
        	
        	log.debug("Attempt to Local Test Secret Entity Value : [{}]", tokenParams);

        	authorities.add(new SimpleGrantedAuthority("ROLE_TESTER"));
        }
        
        if(secretEntity == null) {
        	throw new AuthenticationServiceException("AuthenticationService Fail (SID:"+sid+")");
        }
        
        AuthorizationEntity authEntity = new AuthorizationEntity(sid, authorities, null, secretEntity);

        return new UsernamePasswordAuthenticationToken(authEntity,null,authEntity.getAuthorities());
    }
    
    @SuppressWarnings("unchecked")
    private SecretEntity wasStart(String sid, DMap<String, Object> tokenParams) throws WmsException{
    	
    	WmsRequestEntity params = new WmsRequestEntity();
    	
    	params.setSid(sid);
    	params.setWmsAccessDeviceCode(tokenParams.getString("devicecode"));
    	params.setLauncherName(tokenParams.getString("launchername"));
    	
    	log.debug("WAS START PARAMS : {}", params);
    	
    	String devInfo = tokenParams.getString("devinfo");
    	String crid = tokenParams.getString("crid");
    	
    	WmsResponseEntity result = wmsExternalService.setWasStart(params, devInfo, crid);
    	
		Map<String, Object> detailMsg = new ObjectMapper().convertValue(result.getDetailMsg(), Map.class);
    	
    	log.debug("Attempt to WasStart detailMsg Data : [{}]", detailMsg);
    	
    	SecretEntity entity = null;
    	
    	if(result.getResult() != null && "0".equals(result.getResult())) {

    		entity = new SecretEntity(
    				!DEFAULT_SITE_CODE.isEmpty() ? DEFAULT_SITE_CODE : detailMsg.get("siteCode").toString(),
    				detailMsg.get("dnis").toString(),
    				tokenParams.getString("ani"),
    				result.getWmsAccessDeviceCode(),
    				detailMsg.get("userData").toString(),
    				tokenParams.getString("launchername"),
    				detailMsg.get("appBindDetails") != null ? detailMsg.get("appBindDetails").toString() : null,
    				detailMsg.get("wcSeq").toString(),
    				detailMsg.get("icid").toString(),
    				tokenParams.getString("btoken"),
    				crid
    				);
    	}
    	
    	return entity;
    }
    
    @SuppressWarnings("unchecked")
    private String isAliveCheck(String sid, String devInfo, String crid) throws WmsException{
    	
    	WmsResponseEntity result = wmsExternalService.getAliveCheck(sid, devInfo, crid);
    	
		Map<String, Object> detailMsg = new ObjectMapper().convertValue(result.getDetailMsg(), Map.class);
    	
    	if(result.getResult() != null && "0".equals(result.getResult())) {
    		//중복 진입
    		if("BINDED".equals(detailMsg.get("checkResult"))) {
    			return ResultStrConstants.DUPLICATED;
    		//콜이 죽어있음
    		}else if("0".equals(detailMsg.get("checkResult"))) {
    			return ResultStrConstants.DEAD;
    		}else {
    			return ResultStrConstants.ALIVE;
    		}
    	}
    	
    	return ResultStrConstants.DEAD;
    }
    
    //for none spring session
    /*
    private AuthorizationEntity getAuthorizationPrincipal() {

    	final List<Object> list = sessionRegistry.getAllPrincipals();
    	
    	AuthorizationEntity authEntity = null;
    	
    	if (list != null) {
    		
    		for (Object principal : list) {
    			if (principal instanceof AuthorizationEntity) {
    				authEntity = (AuthorizationEntity) principal;
    			}
    		}	
    	}
    	return authEntity;
    }
    */
    //for redis spring session
    private AuthorizationEntity getAuthorizationPrincipal(String sid) {
    	
    	AuthorizationEntity authEntity = null;
    	
    	Map<String, ? extends Session> principal = sessionRepository.findByPrincipalName(sid);
  
    	for (Map.Entry<String, ? extends Session> entry : principal.entrySet()) {
    		SecurityContext securityContext = entry.getValue().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    		
    		if(securityContext != null) {
    			authEntity = (AuthorizationEntity) securityContext.getAuthentication().getPrincipal();
    		}
    	}

    	return authEntity;
    }
}