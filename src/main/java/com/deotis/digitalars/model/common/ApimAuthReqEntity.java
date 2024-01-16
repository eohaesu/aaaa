package com.deotis.digitalars.model.common;

import lombok.Data;

/**
 * APIM TOKEN 발급을 위한 Default Header
 * 
 * @author hyunjung
 */
@Data
public class ApimAuthReqEntity {
	
	private String grantType = "client_credentials";
	private String clientId = "146d0ead-6c92-4a17-bd7b-b06e0d3b2291";
	private String clientSecret = "oB4aP2hG1kL3vX1cO0wO2sT6tO1dK5vS4jF4qU7fS0vG6hW4qT\r\n";
	private String scope = "CM BL EA SR kakaopay tossbank payco";
	
}
