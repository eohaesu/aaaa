package com.deotis.digitalars.system.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.deotis.digitalars.model.SiteInfo;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.service.business.MainService;
import com.deotis.digitalars.util.collection.DMap;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author jongjin
 * @description nonce advise handler for controller
 */

@ControllerAdvice(basePackages = {
		"com.deotis.digitalars.controller.business"
		,"com.deotis.digitalars.controller.common"
		})
@RequiredArgsConstructor
public class ControllerAdviceHandler{
	
		private final MainService mainService;

	    @ModelAttribute("siteInfo")
	    public SiteInfo addSiteInfoToModel(HttpServletRequest request) {
	    	
	    	UserEntity entity = SessionHandler.getSessionEntity();
	    	
	    	if(entity != null && !"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))){
	    		if(entity.getSiteInfo() == null) { //사이트 정보 추가
	    			DMap<String, Object> params = new DMap<String, Object>();

	    			params.put("site_gcode", 1); 
	    			params.put("site_code", entity.getSiteCode());
	    			
	    			entity.setSiteInfo(mainService.getSiteInfo(params));
	    		}
	    		return SessionHandler.getSessionEntity().getSiteInfo();
	    	}else {
	    		return null;
	    	}
	    }

	}