package com.deotis.digitalars.system.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.deotis.digitalars.util.common.EscapeUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * 
 * @author jongjin
 * @description XSS prevent requestWrapper
 */

public class XssRequestWrapper extends HttpServletRequestWrapper {

	private final Integer filterLevel;
	private final String charEnc;
	private byte[] rawData;
    private final HttpServletRequest request;
    private final ResettableServletInputStream servletStream;

    public XssRequestWrapper(HttpServletRequest request, Integer level, String enc) {
        super(request);
        this.request = request;
        this.servletStream = new ResettableServletInputStream();
        this.filterLevel = level;
        this.charEnc = enc;
    }
    
    @Override
	public String getParameter(String paramName) {
		String value = super.getParameter(paramName);
		return EscapeUtil.escapeOrigin(value);
	}

	@Override
	public String[] getParameterValues(String paramName) {
		String values[] = super.getParameterValues(paramName);
		if (values == null) {
			return values;
		}
		for (int index = 0; index < values.length; index++) {
			values[index] = EscapeUtil.escapeOrigin(values[index]);
		}
		return values;
	}
	
	@Override
	public Enumeration<String> getHeaders(String name) {
		
		if(filterLevel != (filterLevel | 1)) {
			return super.getHeaders(name);
		}
		
	    List<String> result = new ArrayList<>();
	    Enumeration<String> headers = super.getHeaders(name);
	    while (headers.hasMoreElements()) {
	        String header = headers.nextElement();
	        String[] tokens = header.split(",");
	        for (String token : tokens) {
	            result.add(EscapeUtil.escapeOrigin(token));
	        }
	    }
	    return Collections.enumeration(result);
	}

    public void resetInputStream(byte[] data) {
        servletStream.stream = new ByteArrayInputStream(data);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader(), charEnc);
            servletStream.stream = new ByteArrayInputStream(rawData);
        }
        return servletStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader(), charEnc);
            servletStream.stream = new ByteArrayInputStream(rawData);
        }
        return new BufferedReader(new InputStreamReader(servletStream));
    }


    private class ResettableServletInputStream extends ServletInputStream {

        private InputStream stream;

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }

}