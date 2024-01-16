package com.deotis.digitalars.system.filter;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.util.common.EscapeUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description XSS prevent requestFilter
 */
@Slf4j
public class XssRequestFilter implements Filter {
	
	private final Integer filterLevel;
	private final String charEnc;
	
	public XssRequestFilter(Integer level, String enc) {
		this.filterLevel = level;
        this.charEnc = enc;
    }
	
	@Override 
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if(filterLevel > 0){
			
			XssRequestWrapper wrappedRequest = new XssRequestWrapper((HttpServletRequest) request, filterLevel, charEnc);

			log.trace("ParameterInfo:{}", wrappedRequest.getParameterMap());
			
			if(filterLevel == (filterLevel | 2)){
				
				String body = IOUtils.toString(wrappedRequest.getReader());

			    if(StringUtils.hasLength(body) && isValidJson(body)){
			    	
			        JSONObject oldJsonObject = new JSONObject(body);
			        JSONObject newJsonObject = new JSONObject();
			       
			        Iterator<?> keys = oldJsonObject.keys();
				       
			        while(keys.hasNext() ) {
			        	String key = (String)keys.next();
			        	newJsonObject.put(key, EscapeUtil.escapeOrigin(oldJsonObject.get(key).toString()));
			        }
			        
			        wrappedRequest.resetInputStream(newJsonObject.toString().getBytes());
			    }
			}
			chain.doFilter(wrappedRequest, response);
		}else {
			chain.doFilter(request, response);
		}

    }
	
	private boolean isValidJson(String jsonVal) {
		
		try {
			new JSONObject(jsonVal);
		}catch(JSONException e) {
			return false;
		}
		return true;
	}
}