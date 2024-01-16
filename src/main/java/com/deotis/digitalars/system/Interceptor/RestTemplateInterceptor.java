package com.deotis.digitalars.system.Interceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.system.handler.SessionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description intercept API Client 
 */
@Slf4j
@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		ClientHttpResponse response = null;
		
		//prehandle
		logRequest(request, body);

		if(entity == null) {
			response = execution.execute(request, body);
		} else {
			DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("YYYYMMddHHmmssSSS").toFormatter();
			
			// 요청시간
			LocalDateTime reqNow = LocalDateTime.now();
			String requestTime = reqNow.format(formatter);
			entity.getLogData().setReqTime(requestTime);
			
			response = execution.execute(request, body);
			
			// 응답시간
			LocalDateTime resNow = LocalDateTime.now();
			String responseTime = resNow.format(formatter);
			entity.getLogData().setRspTime(responseTime);
		}
		
		//posthandle
		logResponse(response);
		
		return response;
	}
	
	private void logRequest(HttpRequest request, byte[] body) throws IOException 
    {
		log.info("======= External RestTemplate API request begin =======");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, "UTF-8"));
        log.info("======= External RestTemplate API request end =======");
    }
  
    private void logResponse(ClientHttpResponse response) throws IOException 
    {
    	 log.info("======= External RestTemplate API response begin =======");
         log.info("Status code  : {}", response.getStatusCode());
         log.info("Status text  : {}", response.getStatusText());
         log.info("Headers      : {}", response.getHeaders());
         log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
         log.info("======= External RestTemplate API response end =======");
    }
	
}