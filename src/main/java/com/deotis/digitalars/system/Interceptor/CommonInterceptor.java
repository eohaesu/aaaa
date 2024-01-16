package com.deotis.digitalars.system.Interceptor;
import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.system.handler.SessionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description common interceptor
 */
@Slf4j
@Component
public class CommonInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException{
		
		if (DispatcherType.REQUEST.equals(request.getDispatcherType())) {
			log.debug("=== Common Interceptor === [ Request Url : {} ]", request.getRequestURI());
        }

		UserEntity userEntity = SessionHandler.getSessionEntity();

		//check session model and call end status
		if(userEntity == null || userEntity.isRecieve_callend()) {
			if(StringUtils.hasText(request.getHeader("X-Requested-With")) && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}else {
				if(userEntity == null) {
					response.sendRedirect(request.getContextPath()+SecurityConstants.CALL_END_URI);
				}else {
					response.sendRedirect(request.getContextPath()+SecurityConstants.SESSION_END_URI);
				}
			}
			return false;
		}else {

			String uriPath = new UrlPathHelper().getPathWithinApplication(request);

			if(!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
					&& userEntity.getShowArsProgressType() > 0
					&& !uriPath.contains(CommonConstants.IVR_PROGRESS_CHECK)
					&& !uriPath.contains("/common/duplicateNotAllow")
			) {
				log.debug("IVR still in progress. just redirect wait page. save redirection URL:{}", uriPath);
				userEntity.setProgressRedirect(uriPath);
				response.sendRedirect(request.getContextPath()+CommonConstants.IVR_PROGRESS_CHECK);
				return false;
			}
		}
        
		return true;
	}
	
	@Override	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView){		

	}
	
	@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){

    }

	@Override
    public void afterConcurrentHandlingStarted (HttpServletRequest request, HttpServletResponse response, Object handler){

		log.debug("===== Asynchronous Process Start thread : [{}] =====", Thread.currentThread().getName());

    }
}