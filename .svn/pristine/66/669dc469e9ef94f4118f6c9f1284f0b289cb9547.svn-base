package com.deotis.digitalars.security.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.model.wms.WmsResponseEntity;
import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.service.rest.external.WmsExternalService;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.util.collection.DMap;
import com.deotis.digitalars.util.common.CommonUtil;
import com.deotis.digitalars.util.common.ConvertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description authorize filter get sid from crid
 */
@Slf4j
public class AuthenticationKeyFilter extends AbstractAuthenticationProcessingFilter {
	
	@Autowired
	private WmsExternalService wmsExternalService;
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;

	public AuthenticationKeyFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		String crid = request.getParameter(SecurityConstants.AUTH_KEY);
		String btoken = request.getParameter(SecurityConstants.BROWSER_KEY);
		String sid = null;
		String wmsAccessDeviceCode = null;
		
		DMap<String, Object> tokenParams = new DMap<String, Object>();
		
		if(!SYSTEM_TEST_MODE) {
			//crid에 대한 get sid 인증 절차
			if(StringUtils.hasLength(crid)) {		
				try {
					
					WmsResponseEntity result = wmsExternalService.getUsidFromCrid(request, crid);
					
					if(result.getResult() != null && "0".equals(result.getResult())) {
						@SuppressWarnings("unchecked")
						Map<String, Object> detailMsg = new ObjectMapper().convertValue(result.getDetailMsg(), Map.class);
						
						sid = ConvertUtil.getQueryParamsMap(detailMsg.get("urlParams").toString()).get("usid");
						
						if(!StringUtils.hasLength(sid)) {
							throw new AuthenticationServiceException("AuthenticationService Failed to Get SID From CRID (CRID:"+crid+")");
						}

						wmsAccessDeviceCode = result.getWmsAccessDeviceCode();
						
						log.info("AuthenticationService get USID :[{}] from CRID :[{}]", sid, crid);

						tokenParams.put("crid", crid);
						tokenParams.put("sid", sid);
						tokenParams.put("devicecode", wmsAccessDeviceCode);
						tokenParams.put("ani", detailMsg.get("ani").toString().replace("-", ""));
						tokenParams.put("launchername", detailMsg.get("launcherName")); //인입 런처
						tokenParams.put("btoken", btoken); //탭생성 체크 브라우저 구분 키
						
						String devinfo = CommonUtil.getDeviceInfo(request);  
						tokenParams.put("devinfo", devinfo); // TLO 로그에 필요한 데이터로 wasStart나 aliveCheck에서 사용하기 위해 저장함
						
					}else {
						throw new AuthenticationServiceException("AuthenticationService Failed to Get SID From CRID (CRID:"+crid+")");
					}

				}catch(WmsException e) {
					throw new AccountExpiredException("Crid Access Expiration or Fail Exception [CRID:"+e.getSid()+"], [RemoteIp:"+request.getRemoteAddr()+"]");
				}

			}else {
				throw new AuthenticationServiceException("Need to a CRID. but not supported");
			}
		}else {
			//from Test Page
			tokenParams.put("sid", request.getParameter("autosid") != null ? RandomStringUtils.randomNumeric(10) : request.getParameter("sid"));
			tokenParams.put("sitecode", request.getParameter("siteCode"));
			tokenParams.put("dnis", request.getParameter("dnis"));
			tokenParams.put("ani", request.getParameter("ani").replace("-", ""));
			tokenParams.put("devicecode", "1");//request.getParameter("wmsAccessDeviceCode")
			tokenParams.put("userdata", request.getParameter("userData"));
			tokenParams.put("wcseq", request.getParameter("wcSeq"));
			tokenParams.put("appBindDetails", request.getParameter("appBindDetails"));
			tokenParams.put("btoken", request.getParameter("btoken"));
			tokenParams.put("devinfo", CommonUtil.getDeviceInfo(request));
			tokenParams.put("crid", "xptmxma.");
			
			log.debug("Attempt to Local Test Data Value : [{}]", tokenParams);
		}

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(tokenParams, null);
		
		return getAuthenticationManager().authenticate(token);
	}
}