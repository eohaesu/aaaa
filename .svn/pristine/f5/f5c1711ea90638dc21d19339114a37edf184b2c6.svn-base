package com.deotis.digitalars.system.filter;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.deotis.digitalars.constants.CommonConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author jongjin
 * @description expression for logback filter
 */

@Component
public class LogRequestFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
        {

    		String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
    		String usrName = "anonymousUser";
    		
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		
    		if (auth != null) {
    			usrName = auth.getName();
    		}
    		
    		MDC.put(CommonConstants.SESSION_TOKEN_ID, sessionId);
    		MDC.put(CommonConstants.SECURITY_USR_NAME, usrName);

    	    try {
    	    	
    			filterChain.doFilter(request, response);
    			
    			
    		} finally {
    			 MDC.remove(CommonConstants.SESSION_TOKEN_ID);
    	         MDC.remove(CommonConstants.SECURITY_USR_NAME);
    		}
        }
        
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
            String path = request.getRequestURI();
            return path.contains("/static");
        }

    }