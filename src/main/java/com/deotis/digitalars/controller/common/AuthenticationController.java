package com.deotis.digitalars.controller.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.system.handler.SessionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(value = "/auth")
@Controller
public class AuthenticationController {
	
	@Value("${digitalars.default.number}")
	private String[] DEFAULT_NUMBER_LIST;
	
	/**
	 * Session check
	 * 
	 * @return boolean
	 */
	@GetMapping(value = "/chksid")
	@ResponseBody
	public boolean checkSessionId(HttpServletRequest request) {

		boolean result = SessionHandler.getSessionEntity() != null ? true : false;

		log.debug("Check session entity. remoteAddr:[{}], session alive is {}", request.getRemoteAddr(), result);
		
		return result;
	}

	/**
	 * 접근 불가
	 * 
	 * @return String
	 */
	@GetMapping(value = "/accessDenied")
	public String accessDenied(HttpServletRequest request) {
		
		log.debug("Access Denied session Id : [{}], remoteAddr:[{}]", request.getSession().getId(), request.getRemoteAddr());
		
		return "contents/common/systemError";
	}
	
	/**
	 * 서버 오류
	 * 
	 * @return String
	 */
	@GetMapping(value = "/serverError")
	public String serverError(HttpServletRequest request) {
		
		log.debug("serverError session Id : [{}], remoteAddr:[{}]", request.getSession().getId(), request.getRemoteAddr());
		
		return "contents/common/systemError";
	}
	
	
	/**
	 * wms uri expiration
	 * 
	 * @return String
	 */
	@GetMapping(value = "/uriExpiration")
	public String uriExpiration(ModelMap model, HttpServletRequest request) {
		
		log.debug("Expired URI access session Id : [{}], remoteAddr:[{}]", request.getSession().getId(), request.getRemoteAddr());
		
		model.addAttribute("arsnumlist", DEFAULT_NUMBER_LIST);
		
		return "contents/common/uriExpiration";
	}
}
