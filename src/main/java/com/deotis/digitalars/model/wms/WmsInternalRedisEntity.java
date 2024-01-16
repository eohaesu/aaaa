package com.deotis.digitalars.model.wms;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @description response of redis repositories entity from wms request
 */

@Getter
@Setter
@ToString
@RedisHash(value = "dars:deotis:WmsEvent", timeToLive = 300)
public class WmsInternalRedisEntity{

	private String id;
	private String eventName;
	private String data;

	@Builder
	public WmsInternalRedisEntity(String id, String eventName, String data)
	{
		this.id = id;
		this.eventName = eventName;
		this.data = data;
	}
}
