package com.deotis.digitalars.system.rest.client;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author jongjin
 * @description block rest connector extends RestTemplate
 */

public class DeotisTemplate extends RestTemplate{

	public DeotisTemplate(HttpComponentsClientHttpRequestFactory requestFactory, List<ClientHttpRequestInterceptor> interceptors){

		super(new BufferingClientHttpRequestFactory(requestFactory));
		super.setInterceptors(interceptors);

	}
	//default header configuration
	public HttpEntity<?> getHttpEntityDefaultHeader(HttpEntity<?> requestEntity){
		
		if(requestEntity!=null){
			
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.putAll(requestEntity.getHeaders());

			return new HttpEntity<>(requestEntity.getBody(), headers);
		}

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		return httpEntity;
	}
	
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		return super.exchange(url, method, requestEntity, responseType);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);

		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}
}