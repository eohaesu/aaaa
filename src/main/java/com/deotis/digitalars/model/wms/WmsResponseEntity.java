package com.deotis.digitalars.model.wms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @description wms result entity for response
 */

@Getter
@Setter
@ToString
public class WmsResponseEntity{

	private Object detailMsg;
	private String message;
	private String result; 
	private String wmsAccessDeviceCode;
	private String usid;
	
}
