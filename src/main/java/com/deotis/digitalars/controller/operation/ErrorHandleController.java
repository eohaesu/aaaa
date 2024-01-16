package com.deotis.digitalars.controller.operation;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.deotis.digitalars.util.common.RegexUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description Error handling controller
 */

@Slf4j
@Controller
@AllArgsConstructor
public class ErrorHandleController implements ErrorController{
	
	private final MessageSource messageSource;

	/**
	 * 기본 error handling
	 * @param model
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/error")
	public String getError(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		return errorOperation(request, response);	
	}
	
	@PostMapping(value = "/error")
	public String postError(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		return errorOperation(request, response);	
	}
	
	private String errorOperation(HttpServletRequest request, HttpServletResponse response) {
		int statusCode = 0;
		
		if(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) != null) {
			statusCode = Integer.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
		}

		String request_error = request.getAttribute("javax.servlet.error.request_uri") != null ? 
				request.getAttribute("javax.servlet.error.request_uri").toString() : "";
		
		printErrorInfo(request);

    	if(StringUtils.hasText(request.getHeader("X-Requested-With")) && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
    		try {
    			
    			if(request_error.indexOf("static") > -1 || RegexUtil.patternValidate(request_error, RegexUtil.Patterns.IMG)) {
    				log.info("Occure static request error. please check static link. status code:{}, request uri:{}", statusCode, request_error);
    			}else {
    				if(!request_error.contains("/poll/")) {
    					log.error("Occure Ajax request Error. status code:{}, request uri:{}", statusCode, request_error);
    				}
    			}

    			response.setCharacterEncoding("UTF-8");	
    			
    			if(request_error.contains("/poll/")) {
    				response.setHeader( "poll_denied", "Y");
    			}
    			
    			if(statusCode == HttpStatus.FORBIDDEN.value()) {		
    				response.getWriter().write(messageSource.getMessage("error.page.access.denied", null, LocaleContextHolder.getLocale()));
    			}else {
    				response.getWriter().write(messageSource.getMessage("error.page.server.error", null, LocaleContextHolder.getLocale()));
    			}
				
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
    		
    		return null;    		    		
    		
    	}else {

    		if(request_error.indexOf("static") > -1 || RegexUtil.patternValidate(request_error, RegexUtil.Patterns.IMG)) {
    			log.info("Occure static request error. please check static link. status code:{}, request uri:{}", statusCode, request_error);
    		}else {
    			log.error("Occure request Error. status code:{}, request uri:{}", statusCode, request_error);
    		}
    		
    		if(statusCode == HttpStatus.NOT_FOUND.value()) {
	            return "contents/common/systemError";
	            
	        }else if(statusCode == HttpStatus.FORBIDDEN.value()) {
	        	
	            return "contents/common/systemError";
	        }
    		return "contents/common/systemError";
    	}
	}

	private void printErrorInfo(HttpServletRequest request) { 
		
		if(request.getAttribute("javax.servlet.error.exception") != null) {
			log.error("ERROR_REQUEST_URI: {}", request.getAttribute("javax.servlet.error.request_uri")); 
			log.error("ERROR_SERVLET_NAME: {}", request.getAttribute("javax.servlet.error.servlet_name")); 
			log.error("ERROR_STATUS_CODE: {}", request.getAttribute("javax.servlet.error.status_code")); 
			log.error("ERROR_EXCEPTION: {}", request.getAttribute("javax.servlet.error.exception")); 
			log.error("ERROR_EXCEPTION_TYPE: {}", request.getAttribute("javax.servlet.error.exception_type")); 
			log.error("ERROR_MESSAGE: {}", request.getAttribute("javax.servlet.error.message")); 
			log.error("dispatcherType = {}", request.getDispatcherType()); 
		}
		
	}

}
