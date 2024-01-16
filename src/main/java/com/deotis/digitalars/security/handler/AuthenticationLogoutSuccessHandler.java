package com.deotis.digitalars.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.system.handler.SessionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description logout success handler
 * 
**/
@Slf4j
public class AuthenticationLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) throws IOException, ServletException {
		
		if(SessionHandler.getAuthentication() != null) {
			request.getSession().invalidate();
		}
		
		String packageName = request.getParameter("pn") != null ? request.getParameter("pn").replaceAll("[\\r\\n]", "") : "";

		log.info("Log out Success End");
		
		if(StringUtils.hasText(request.getHeader("X-Requested-With")) && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {//ajax요청일때
			response.setStatus(HttpServletResponse.SC_OK);
		}else{
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect(request.getContextPath()+SecurityConstants.CALL_END_URI+"?pn="+packageName);
		}

	}			
}
