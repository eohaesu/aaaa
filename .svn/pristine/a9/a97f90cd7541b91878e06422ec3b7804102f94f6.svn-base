package com.deotis.digitalars.system.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalRestException extends Exception {

	private static final long serialVersionUID = 3573622201419874562L;
	
	private final InternalRestEnum internalRestEnum;

	
	public InternalRestException(String message, InternalRestEnum internalRestEnum) {
        super(message);
        log.error("Internal Rest Exception : [{}]", message);
        this.internalRestEnum = internalRestEnum;
	}

	public InternalRestException(InternalRestEnum internalRestEnum) {
        super(internalRestEnum.getMessage());
        log.error("Internal Rest Exception : [{}]", internalRestEnum.getMessage());
        this.internalRestEnum = internalRestEnum;
	}

}