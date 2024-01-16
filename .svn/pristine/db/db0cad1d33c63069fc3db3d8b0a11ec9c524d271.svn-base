package com.deotis.digitalars.security.handler;

import java.io.IOException;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.deotis.digitalars.security.config.SecurityConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description authentication Fail handler
 * 
**/

@Slf4j
public class AuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler  {
	
	@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.debug("#### Authentication Fail just redirect ####");
		
		request.getSession().invalidate();
		
		setDefaultFailureUrl(SecurityConstants.CALL_END_URI);

		if(exception instanceof AccountExpiredException){ 
			log.error(exception.getMessage());
			getRedirectStrategy().sendRedirect(request, response, SecurityConstants.URI_EXPIRATION);
        }else if(exception instanceof AuthenticationServiceException){
        	log.error(exception.getMessage());
        	getRedirectStrategy().sendRedirect(request, response, SecurityConstants.CALL_END_URI);
        }else if(exception instanceof InternalAuthenticationServiceException){ 
        	getRedirectStrategy().sendRedirect(request, response, SecurityConstants.CALL_END_URI);
        }else {
            super.onAuthenticationFailure(request,response,exception);
        }

    }

}