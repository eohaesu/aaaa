package com.deotis.digitalars.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @description response entity from System request
 */

@Getter
@Setter
@ToString
public class SystemInternalEntity{
	
	private String level;
	private String remoteAddr;
	private String sessionId; 
	private String processMessage;
	private boolean processResult;

	@Builder
	public SystemInternalEntity(String remoteAddr, String sessionId)
	{
		this.remoteAddr = remoteAddr;
		this.sessionId = sessionId;
		this.processResult = false;
	}
}
