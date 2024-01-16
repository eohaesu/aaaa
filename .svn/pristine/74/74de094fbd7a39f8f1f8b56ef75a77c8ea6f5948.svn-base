package com.deotis.digitalars.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import com.deotis.digitalars.security.filter.AuthenticationKeyFilter;
import com.deotis.digitalars.security.handler.AuthenticationDeniedHandler;
import com.deotis.digitalars.security.handler.AuthenticationEntryPointHandler;
import com.deotis.digitalars.security.handler.AuthenticationFailHandler;
import com.deotis.digitalars.security.handler.AuthenticationKeySuccessHandler;
import com.deotis.digitalars.security.handler.AuthenticationLogoutHandler;
import com.deotis.digitalars.security.handler.AuthenticationLogoutSuccessHandler;
import com.deotis.digitalars.security.manager.AuthenticationKeyManager;

/**
 * 
 * @author jongjin
 * @description security module configuration
 * 
**/

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Autowired //Spring session(redis 포함)을 사용하지 않을 시 제거
	private FindByIndexNameSessionRepository<? extends Session> sessionRepository;
	
	@Value("${authorize.session.mode}")
	private String AUTHORIZE_SESSION_MODE;

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    	http
    		// 페이지 권한 설정
    		.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
    				.antMatchers(
	    				"/",
	    				"/error",
	    				"/static/**",
	                    "/auth/**",
	                    "/api/exception/**",
	                    "/api/internal/**",
	                    "/api/wms/internal/**",
	                    "/api/system/**",
	                    "/test/**",
	                    "/actuator/**", //for CI Health check tool	
	                    "/poll/emitter",
	                    SecurityConstants.CALL_END_URI
	    				 ).permitAll() //securityFilter 제외 url
    				.anyRequest().authenticated() //permitAll외 모든 경로는 인증이 필요
			)
    		.formLogin((login) -> login
    				.disable()
    		)		
    		.logout((logout) -> logout
    				.logoutRequestMatcher(new AntPathRequestMatcher(SecurityConstants.SESSION_END_URI))
    				.addLogoutHandler(logoutAddHandler())
    				.logoutSuccessHandler(logoutSuccessHandler())
    				.invalidateHttpSession(true)	
    		)
    		.csrf((csrf) -> csrf
    				.ignoringAntMatchers(SecurityConstants.LOGIN_PROCESS_URI, "/api/**")
    		)
    		.exceptionHandling((exceptionHandling) -> exceptionHandling
    				.accessDeniedHandler(accessDeniedHandler())
    				.authenticationEntryPoint(enrtyPointHandler())
    		)
    		.headers((headers) -> headers
            		.frameOptions((frameOptions) -> frameOptions
            				.sameOrigin()
            				.contentSecurityPolicy((contentSecurityPolicy) -> contentSecurityPolicy
            						//.policyDirectives("default-src 'self'") //if required the full CSP(Contents Security Policy)
            						.policyDirectives("form-action 'self';")
            				)
            		)
            		.xssProtection((xss) -> xss.xssProtectionEnabled(true))
            		.httpStrictTransportSecurity((hsts) -> hsts
            				.includeSubDomains(true)
            				.preload(true)
            				.maxAgeInSeconds(31536000)
            		)
            )	
    		.sessionManagement((management) -> management
    				.maximumSessions(1)
    		        .expiredUrl(SecurityConstants.CALL_END_URI)
    		);

    	return http.addFilterBefore(authenticationKeyFilter(), UsernamePasswordAuthenticationFilter.class).build();
    }
	
	@Bean
    AuthenticationKeyFilter authenticationKeyFilter(){
		AuthenticationKeyFilter filter = new AuthenticationKeyFilter(
				new AntPathRequestMatcher(SecurityConstants.LOGIN_PROCESS_URI)
				);
		
    	filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailHandler());
        filter.setAuthenticationManager(authenticationKeyManager());
        //authorize session tactics
        if("default".equals(AUTHORIZE_SESSION_MODE) || "both".equals(AUTHORIZE_SESSION_MODE)) {
        	filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy(sessionRegistry()));
        }

        return filter;
    }
	
	@Bean
	AuthenticationKeyManager authenticationKeyManager() {
        return new AuthenticationKeyManager();
	}
	
    @Bean
    AuthenticationKeySuccessHandler authenticationSuccessHandler() {
        return new AuthenticationKeySuccessHandler();
    }
    
    @Bean
    AuthenticationLogoutHandler logoutAddHandler() {
        return new AuthenticationLogoutHandler();
    }
	
	@Bean
	AuthenticationLogoutSuccessHandler logoutSuccessHandler() {
        return new AuthenticationLogoutSuccessHandler();
    }
    
    @Bean
    AuthenticationFailHandler authenticationFailHandler() {
        return new AuthenticationFailHandler();
    }
    
    @Bean
    AuthenticationDeniedHandler accessDeniedHandler() {
        return new AuthenticationDeniedHandler();
    }

    @Bean
    AuthenticationEntryPointHandler enrtyPointHandler() {
        return new AuthenticationEntryPointHandler();
    }
    
    //다중화 spring session redis 환경
    @Bean
    SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
    }
    /* spring redis session 미사용시 주석 해제
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl(); 
    }
    */
    @Bean
 	ConcurrentSessionControlAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
 		return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
 	}
    
    @Bean
	ServletListenerRegistrationBean<HttpSessionEventPublisher> getHttpSessionEventPublisher() {
	    return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}

}