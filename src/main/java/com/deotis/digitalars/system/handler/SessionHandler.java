package com.deotis.digitalars.system.handler;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.security.model.AuthorizationEntity;
import com.deotis.digitalars.security.model.SecretEntity;

/**
 * 
 * @author jongjin
 * @description authorization/session info handler
 */

public class SessionHandler{
	
		/**
		 * get session model
		 * @return UserEntity
		 */
		public static UserEntity getSessionEntity() {
			return RequestContextHolder.getRequestAttributes().getAttribute(CommonConstants.SESSION_NAME, RequestAttributes.SCOPE_SESSION) != null ? 
					(UserEntity) RequestContextHolder.getRequestAttributes().getAttribute(CommonConstants.SESSION_NAME, RequestAttributes.SCOPE_SESSION) : null;
		}
		
		/**
		 * set session model
		 * @param SessionEntity
		 * @return void
		 */
		public static void setSessionEntity(UserEntity sessionEntity) {
			RequestContextHolder.getRequestAttributes().setAttribute(CommonConstants.SESSION_NAME, sessionEntity, RequestAttributes.SCOPE_SESSION);
		}
		
		/**
		 * get sessionId
		 * @return String
		 */
		public static String getSessionId() {
			return RequestContextHolder.getRequestAttributes().getSessionId();
		}
		
		/**
		 * remove session model
		 * @param SessionEntity
		 * @return void
		 */
		public static void removeSessionEntity() {
			RequestContextHolder.getRequestAttributes().removeAttribute(CommonConstants.SESSION_NAME, RequestAttributes.SCOPE_SESSION);
		}
		
		
		/**
		 * security secret entity 정보 get
		 * @param 
		 * @return SecretEntity
		 */
	    public static SecretEntity getSecretEntity(){
	    	Authentication authentication = getAuthentication();
			AuthorizationEntity entity = (AuthorizationEntity) authentication.getPrincipal();
			return entity.getSecret();
	    }
		
		/**
		 * security secret entity 정보 set
		 * @param SecretEntity
		 * @return void
		 */
	    public static void setSecretEntity(SecretEntity secretEntity){
	    	Authentication authentication = getAuthentication();
			AuthorizationEntity entity = (AuthorizationEntity) authentication.getPrincipal();
			entity.setSecret(secretEntity);
			setAuthentication(new UsernamePasswordAuthenticationToken(entity,null,entity.getAuthorities()));
	    }
		
		/**
		 * security authentication 정보 set
		 * @param Authentication
		 * @return void
		 */
	    public static void setAuthentication(Authentication authentication){
	    	SecurityContextHolder.getContext().setAuthentication(authentication);
	    }

	    
	    /**
		 * security authentication 정보 조회
		 * @param null
		 * @return Authentication
		 */
	    public static Authentication getAuthentication(){
	        SecurityContext securityContext = SecurityContextHolder.getContext();
	        Authentication authentication = securityContext.getAuthentication();
	        	
	        return authentication;
	    }
	    
}