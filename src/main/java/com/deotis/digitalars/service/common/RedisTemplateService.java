package com.deotis.digitalars.service.common;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.service.rest.internal.WmsInternalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description Service for RedisTemplate
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisTemplateService {

	@Autowired
	private StringRedisTemplate redisTemplate;
	
	private final WmsInternalService wmsService;
	
	@Value("${spring.session.redis.namespace}")
	private String REDIS_NAMESPACE;
	
	private final String OPT_EVENT_NAME = ":event:";
	
	public void addKeyOptValue(String key, String value, int minute) {
		
		ValueOperations<String, String> vo = redisTemplate.opsForValue();
		
		vo.set(REDIS_NAMESPACE+OPT_EVENT_NAME+key, value, minute, TimeUnit.MINUTES);
		
		log.debug("====== Redis addKeyOptValue ======");
		log.debug("key    : {}", key);
		log.debug("value  : {}", value);
		log.debug("minute : {}", minute);
		log.debug("==================================");
		
	}
	
	public void addHashOptValue(String key, Object hashkey, Object value, int minute) {
		
		HashOperations<String, Object, Object> ho = redisTemplate.opsForHash();
		
		ho.put(REDIS_NAMESPACE+OPT_EVENT_NAME+key, hashkey, value.toString());
		
		redisTemplate.expire(REDIS_NAMESPACE+OPT_EVENT_NAME+key, minute, TimeUnit.MINUTES);

	}
	
	public Object getKeyOptValue(String key) {
		return redisTemplate.opsForValue().get(REDIS_NAMESPACE+OPT_EVENT_NAME+key);
	}
	
	public Object getHashOptValue(String key, Object hashkey) {
		return redisTemplate.opsForHash().get(REDIS_NAMESPACE+OPT_EVENT_NAME+key, hashkey);
	}
	
	public void deleteOptString(String key) {
		redisTemplate.delete(REDIS_NAMESPACE+OPT_EVENT_NAME+key);
	}
	
	public void deleteOptHash(String key, Object hashkey) {
		redisTemplate.opsForHash().delete(REDIS_NAMESPACE+OPT_EVENT_NAME+key, hashkey);
		
	}
	
	public Long getOptHashSize(String key){
		return redisTemplate.opsForHash().size(REDIS_NAMESPACE+OPT_EVENT_NAME+key);
	}
	
	public boolean getOptValueHasKey(String key) {
		return redisTemplate.hasKey(key);
	}
	
	public boolean getOptHashHasKey(String key, Object hashkey){
		return redisTemplate.opsForHash().hasKey(REDIS_NAMESPACE+OPT_EVENT_NAME+key, hashkey);
	}
	
	public Map<Object, Object> getOptHashEntries(String key){
		return redisTemplate.opsForHash().entries(REDIS_NAMESPACE+OPT_EVENT_NAME+key);
	}
	
	/**
	 * Clear garbage repository set of redis
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Integer cleanKeysWithScan() {
		
		String pattern = REDIS_NAMESPACE+":index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:";

		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		
		ScanOptions options = ScanOptions.scanOptions().match(pattern+"*").build();
		
		Cursor<byte[]> cursor = connection.scan(options);
		
		int result = 0;
		
		while(cursor.hasNext()) {
			
			byte[] next = cursor.next();

			try {
				String key = new String(next, "UTF-8");
				String sid = key.replaceAll(pattern, "");
				
				if(StringUtils.hasLength(sid) && !wmsService.isExistSessionByUSID(sid)) {
					log.debug("Found matched pattern of key. Now delete from Redis. key:{}", key);
					redisTemplate.delete(key);
					result += 1;
				}
			} catch (UnsupportedEncodingException e) {
				log.error("Redis repository clean fail. message:", e.getMessage());
			}
		}
		
		return result;
	}
	
}