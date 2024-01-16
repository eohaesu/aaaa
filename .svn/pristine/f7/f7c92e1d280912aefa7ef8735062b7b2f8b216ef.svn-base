package com.deotis.digitalars.system.exception;

import lombok.Getter;

/**
 * 
 * @author jongjin
 * @description Internal rest error enumeration
 */

@Getter
public enum InternalRestEnum {
	
	RESULT_SUCCESS(200, "S001", "Request result is success"),
	NOT_FOUND_TRANSACTION(417, "E001", "Can not found transaction key"),
	INVALID_AUTHORIZATION(401, "E002", "Authorization fail"),
	INVALID_PARAMETER(400, "E003", "Invalid parameter error"),
	INTERNAL_SERVER_ERROR(500, "E004", "Internal server error"),
	NOT_ALLOW(401, "E005", "Request ip address is not allow");

	private final int httpStatus;
	private final String code;
	private final String message;

	InternalRestEnum(final int status, final String code, final String message) {
		this.httpStatus = status;
		this.code = code;
		this.message = message;
	}
}