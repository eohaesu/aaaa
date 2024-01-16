package com.deotis.digitalars.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.system.Interceptor.CommonInterceptor;
import com.deotis.digitalars.system.Interceptor.InternalRestInterceptor;
import com.deotis.digitalars.system.filter.LogRequestFilter;
import com.deotis.digitalars.system.filter.XssRequestFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description base web mvc configuration
 */

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Value("${spring.thymeleaf.encoding}")
	private String CHAR_ENC;
	
	@Value("${system.xss.filter.level}")
	private Integer FILTER_LEVEL;
	
	@Bean
	InternalRestInterceptor internalRestInterceptor() {
    	return new InternalRestInterceptor ();
	}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	//common interceptor
        registry.addInterceptor(new CommonInterceptor())
                .addPathPatterns("/**")
        		.excludePathPatterns(
        				"/",
        				"/static/**",
        				"/auth/*",
        				"/error/**",
        				"/test/**",
        				"/poll/**",
        				"/api/**",
        				"/actuator/**", //for CI Health check tool
        				SecurityConstants.SESSION_END_WITH_TRACE,
        				SecurityConstants.CALL_END_URI
        		); 
        log.debug("Interceptor 'CommonInterceptor' configured for use");
        //rest interceptor
        registry.addInterceptor(internalRestInterceptor())
        .addPathPatterns("/api/internal/**", "/api/wms/internal/**"); 

        log.debug("Interceptor 'InternalRestInterceptor' configured for use");
    }
    
    //xss prevent filter
  	@Bean
  	FilterRegistrationBean<XssRequestFilter> xssEscapeSeveletFilter(){
  	
  	    FilterRegistrationBean<XssRequestFilter> registrationBean = new FilterRegistrationBean<>();
  	    
  	    registrationBean.setFilter(new XssRequestFilter(FILTER_LEVEL, CHAR_ENC));
  	    registrationBean.setOrder(2);
  	    registrationBean.addUrlPatterns("/*");
  	    
  	    return registrationBean;
  	}
  	
  	//filter regist
  	@Bean
  	FilterRegistrationBean<LogRequestFilter> logFilter() {
  	    FilterRegistrationBean<LogRequestFilter> registrationBean = new FilterRegistrationBean<>();
  	    registrationBean.setFilter(new LogRequestFilter());
  	    registrationBean.setOrder(3);
  	    return registrationBean;
  	}

}