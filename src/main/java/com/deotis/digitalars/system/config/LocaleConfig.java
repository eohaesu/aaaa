package com.deotis.digitalars.system.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * 
 * @author jongjin
 * @description locale configuration for multiple language
 * 
**/

@Configuration
public class LocaleConfig implements WebMvcConfigurer {
	
	@Value("${locale.default}")
	private String LOCALE_DEFAULT;

    @Bean
    MessageSource messageSource() {

    	ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    	messageSource.setBasename("classpath:/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        
        return messageSource;
    }

    @Bean
    LocaleResolver localeResolver() {
    	
    	CookieLocaleResolver localeResolver = new CookieLocaleResolver();
    	
        localeResolver.setDefaultLocale(new Locale(LOCALE_DEFAULT));
        localeResolver.setCookieName("clientLanguage");
        localeResolver.setCookieMaxAge(315360000);	
        localeResolver.setCookiePath("/");
        
        return localeResolver;
    }
    
    @Bean
    LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("clientLanguage");
        
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

}