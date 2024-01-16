package com.deotis.digitalars.system.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description Callback onMessage for processing received objects through Redis
 */
@Slf4j
@Component
public class RedisMessageListener implements MessageListener {
	
	@Value("${spring.session.redis.namespace}")
	private String REDIS_NAMESPACE;
	
    @Override
    public void onMessage(Message message, byte[] pattern) {
    	if(message.toString().contains(REDIS_NAMESPACE)){
    		log.debug("##### The Redis keyspace notification event expired session key : [{}] #####",  message.toString());
    	}
    }
}