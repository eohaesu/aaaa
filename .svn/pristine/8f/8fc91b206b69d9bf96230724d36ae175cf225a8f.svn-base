package com.deotis.digitalars.util.common;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 
 * @author jongjin
 * @description static utility for convert
 */

public class ConvertUtil{

	/**
	 * convert parameter map from URL
	 * @param url
	 * @return Map<String, String>
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> getURLParamsMap(URL url) throws UnsupportedEncodingException {

		if (url == null) {
			 return Collections.emptyMap();
		}

		String queryPart = url.getQuery();

		if (!StringUtils.hasLength(queryPart)) {
			return Collections.emptyMap();
		}

		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = queryPart.split("&");
		for (String pair : pairs) {
			String[] keyValuePair = pair.split("=");

			if(keyValuePair.length == 1) { // 값이 없는 경우 ex) custId=&
				result.put(URLDecoder.decode(keyValuePair[0], StandardCharsets.UTF_8.name()),"");
			} else {
				result.put(URLDecoder.decode(keyValuePair[0], StandardCharsets.UTF_8.name()),URLDecoder.decode(keyValuePair[1], StandardCharsets.UTF_8.name()));
			}
		}
		
		return result;
		
	}
	
	/**
	 * convert parameter map from queryParam
	 * @param queryParam
	 * @return Map<String, String>
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> getQueryParamsMap(String queryParam) throws UnsupportedEncodingException {

		if (!StringUtils.hasLength(queryParam)) {
			 return Collections.emptyMap();
		}

		Map<String, String> result = new HashMap<String, String>();
		
		queryParam = queryParam.replace("?", "");

		String[] pairs = queryParam.split("&");
		for (String pair : pairs) {
			String[] keyValuePair = pair.split("=");

			if(keyValuePair.length == 1) { // 값이 없는 경우 ex) custId=&
				result.put(URLDecoder.decode(keyValuePair[0], StandardCharsets.UTF_8.name()),"");
			} else {
				result.put(URLDecoder.decode(keyValuePair[0], StandardCharsets.UTF_8.name()),URLDecoder.decode(keyValuePair[1], StandardCharsets.UTF_8.name()));
			}
		}
		
		return result;
		
	}

}