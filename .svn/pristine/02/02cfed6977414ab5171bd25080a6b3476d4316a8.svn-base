package com.deotis.digitalars.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
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
 * @description entrypoint handler(authorize exception or normally use single sign on entry point) 
 * 
 */
@Slf4j
@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) 
    				throws IOException, ServletException {
    	
    		if(request.getAttribute("javax.servlet.error.exception") != null) {
    			log.error("Authorization Entry Point Handler Exception Request Url: {}", request.getRequestURL());
        		log.error("Authorization Entry Point Handler Exception :", request.getAttribute("javax.servlet.error.exception")); 
    		}

    		if(StringUtils.hasText(request.getHeader("X-Requested-With")) && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {//ajax요청일때
    			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    			response.sendError(HttpServletResponse.SC_FORBIDDEN);
    		}else {
    			
    			log.debug("Authentication EntryPoint redirect : [{}], RequestUrl : [{}]", SecurityConstants.CALL_END_URI, request.getRequestURL());
    			response.sendRedirect(request.getContextPath()+SecurityConstants.CALL_END_URI);
    		}

    }
    
}