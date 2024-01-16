package com.deotis.digitalars.model.wms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @description response entity from wms request
 */

@Getter
@Setter
@ToString
public class WmsInternalEntity{

	private String remoteAddr;
	private String sessionId; 
	private boolean processResult;

	@Builder
	public WmsInternalEntity(String remoteAddr, String sessionId)
	{
		this.remoteAddr = remoteAddr;
		this.sessionId = sessionId;
		this.processResult = true;
	}
}
