package com.deotis.digitalars.model.wms;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;

/**
 * 
 * @author jongjin
 * @description make wms server data list from properties
 */

@ConfigurationProperties(prefix = "wms.external")
@Getter
@Component
@Data
public class WmsProperties{

	private Map<String, String[]> server;
	
}
