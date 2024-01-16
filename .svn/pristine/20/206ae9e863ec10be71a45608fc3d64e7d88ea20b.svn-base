package com.deotis.digitalars.system.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class BusinessException extends Exception {

	private static final long serialVersionUID = 3573622201419874562L;

	public BusinessException(String msg) {
		super(msg);
		log.error("Business Exception : [{}]", msg);
	}
	
}