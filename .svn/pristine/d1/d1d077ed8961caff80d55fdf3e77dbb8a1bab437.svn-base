package com.deotis.digitalars.security.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.security.config.SecurityConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description base AccessDenied handler
 * 
**/
@Slf4j
@Component
public class AuthenticationDeniedHandler implements AccessDeniedHandler {

	private String accessDeniedUrl;

	public AuthenticationDeniedHandler() {
		accessDeniedUrl = SecurityConstants.ACCESS_DENIED_URI; 
	}

	public AuthenticationDeniedHandler(String accessDeniedUrl) {
		this.accessDeniedUrl = accessDeniedUrl;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		log.info("request security access denied : "+request.getRequestURI());

		
		if(StringUtils.hasText(request.getHeader("X-Requested-With")) && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {//ajax요청일때
			if(request.getRequestURI().contains("/poll/short")) {
				response.setHeader( "poll_denied", "Y");
			}
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}else {
			response.sendRedirect(request.getContextPath() + accessDeniedUrl);
		}

	}

	public String getAccessDeniedUrl() {
		return accessDeniedUrl;
	}

	public void setAccessDeniedUrl(String accessDeniedUrl) {
		this.accessDeniedUrl = accessDeniedUrl;
	}
}