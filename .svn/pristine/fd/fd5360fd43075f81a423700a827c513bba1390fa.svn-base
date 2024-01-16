package com.deotis.digitalars.system.config;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import com.deotis.digitalars.system.Interceptor.RestTemplateInterceptor;
import com.deotis.digitalars.system.filter.RedisMessageListener;
import com.deotis.digitalars.system.rest.client.DeotisReactiveClient;
import com.deotis.digitalars.system.rest.client.DeotisTemplate;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
/**
 * 
 * @author jongjin
 * @description application bean configuration
 */
@Slf4j
@Configuration
public class ApplicationConfig {
	
	@Value("${system.external.connection.timeout}")
	private int CONNECTION_TIME_OUT;
	@Value("${system.external.read.timeout}")
	private int READ_TIME_OUT;
	@Value("${system.external.write.timeout}")
	private int WRITE_TIME_OUT;
	@Value("${system.external.trust.ssl}")
	private boolean TRUST_SSL;

	private final String REDIS_EVENT_PATTERN = "__keyevent@*__:expired";
	
	//thymeleaf layout
	@Bean
	LayoutDialect layoutDialect() {
	    return new LayoutDialect();
	}

	//3rd party redis configuraion
	@Bean 
	static ConfigureRedisAction configureRedisAction() { 
		return ConfigureRedisAction.NO_OP; 
	}
	 
		
	//RestTemplate(blocking)
	@Bean
	DeotisTemplate deotisTemplate() {
		
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		
		CloseableHttpClient client = null; 
		
		if(TRUST_SSL) {
			log.info("RestTemplate trustCerts SSL enable");

			client = HttpClients.custom()
					.setSSLHostnameVerifier(new NoopHostnameVerifier())
					.setMaxConnTotal(100) // max connection thread pool
					.setMaxConnPerRoute(60) // IP,PORT connection per thread
					.evictIdleConnections(60L, TimeUnit.SECONDS) // keep thread  time on background
					.evictExpiredConnections().setConnectionTimeToLive(10, TimeUnit.SECONDS) // persistant connection live time
			        .build();
		}else {
			client = HttpClients.createDefault();
		}
		
		clientHttpRequestFactory.setHttpClient(client);
		clientHttpRequestFactory.setConnectTimeout(CONNECTION_TIME_OUT);
		clientHttpRequestFactory.setReadTimeout(READ_TIME_OUT);
		
		return new DeotisTemplate(clientHttpRequestFactory, Collections.singletonList(new RestTemplateInterceptor()));
	}
	
	@Bean
	DeotisReactiveClient deotisReactiveClient() {
		
		HttpClient httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIME_OUT)
				.doOnConnected(conn -> conn
						.addHandlerLast(new ReadTimeoutHandler(READ_TIME_OUT/1000))
						.addHandlerLast(new WriteTimeoutHandler(WRITE_TIME_OUT/1000))	
				)
				.wiretap(this.getClass().getCanonicalName(), LogLevel.TRACE, AdvancedByteBufFormat.SIMPLE); // trace log
		
		if(TRUST_SSL) {
			try {
				
				SslContext sslContext = SslContextBuilder
				        .forClient()
				        .trustManager(InsecureTrustManagerFactory.INSTANCE)
				        .build();
				
				httpClient.secure(t -> t.sslContext(sslContext));
				
				log.debug("Reactive WebClient trustCerts SSL enable");
				
			} catch (SSLException e) {
				log.error("Reactive WebClient trustCerts SSLException : {}", e.toString());
			}
		}
		
		return new DeotisReactiveClient(httpClient);
	}
	
	//RedismessageListener
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, RedisMessageListener messageListener) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(messageListener, new PatternTopic(REDIS_EVENT_PATTERN));
        redisMessageListenerContainer.setErrorHandler(e -> log.error("######## There was an error in redis key expiration listener container ########", e));
        return redisMessageListenerContainer;
    }

}