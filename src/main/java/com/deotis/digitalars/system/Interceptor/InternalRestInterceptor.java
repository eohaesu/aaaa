package com.deotis.digitalars.system.Interceptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description intercept Internal API Client 
 */
@Slf4j
public class InternalRestInterceptor implements HandlerInterceptor {
	
	@Value("${rest.internal.auth.use}")
	private boolean REST_INTERNAL_AUTH_USE;
	
	private final String LOCALHOST_IPV4 = "127.0.0.1";
	private final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException{
		
		String uriPath = new UrlPathHelper().getPathWithinApplication(request);
		String remoteDiv = uriPath.contains("/wms/") ? "WMS" : "EXTERNAL";
		String clientIp = getClientIp(request);
		
		log.info("======= Internal Rest API request from {} IP:{} =======", remoteDiv, clientIp);

		if(REST_INTERNAL_AUTH_USE) {
			
			if("EXTERNAL".equals(remoteDiv)) {
				
				log.info(" [ Request Menu Url : {} ]", request.getRequestURI());
				log.info(" [ Request Remorte Ip : {} ]", request.getRemoteAddr());

				String authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION);
				
				log.info("internal rest process authorizationToken:[{}]", authorizationToken );
				
			}

		}

		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView){

		
	}
	
	/* rest api authentication
	private boolean verifyBasicToken(String token) {
		
		String[] split = token.split(" ");
        String type = split[0];
        String credential = split[1];

        if ("Basic".equalsIgnoreCase(type)) {

            String decoded = new String(Base64Utils.decodeFromString(credential));
            
            String[] nameValue = decoded.split(":");

            if(CommonConstants.BASIC_AUTH_NAME.equals(nameValue[0]) && CommonConstants.BASIC_AUTH_VALUE.equals(nameValue[1])) {
            	return true;
            }
        }
        
        return false;
	}
	*/
	private String getClientIp(HttpServletRequest request) throws UnknownHostException {
		String ipAddress = request.getHeader("X-Forwarded-For");
		
		if(!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		
		if(!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		
		if(!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
				InetAddress inetAddress = InetAddress.getLocalHost();
				ipAddress = inetAddress.getHostAddress();
			}
		}

		if(StringUtils.hasLength(ipAddress) 
				&& ipAddress.length() > 15
				&& ipAddress.indexOf(",") > 0) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
		}
		
		return ipAddress;
	}
	

}