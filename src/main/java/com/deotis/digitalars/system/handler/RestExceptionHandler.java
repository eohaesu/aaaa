package com.deotis.digitalars.system.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.deotis.digitalars.system.exception.InternalRestEnum;
import com.deotis.digitalars.system.exception.InternalRestException;
import com.deotis.digitalars.system.rest.entity.ResponseExceptionWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description exception advise handler for rest controller api
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.deotis.digitalars.controller.rest.internal"})
public class RestExceptionHandler{
	
	@ExceptionHandler(InternalRestException.class)
	protected ResponseEntity<ResponseExceptionWrapper> handleInternalRestException(final InternalRestException e) {
		final InternalRestEnum restEnum = e.getInternalRestEnum();
		final ResponseExceptionWrapper response = ResponseExceptionWrapper.of(restEnum);

		return new ResponseEntity<>(response, HttpStatus.valueOf(restEnum.getHttpStatus()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ResponseExceptionWrapper> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final ResponseExceptionWrapper response = ResponseExceptionWrapper.of(InternalRestEnum.INVALID_PARAMETER, e.getBindingResult());
        log.error("Message : {}, File : [{}], Line : {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<ResponseExceptionWrapper> handleParameterException(final MissingServletRequestParameterException e) {
		final ResponseExceptionWrapper response = ResponseExceptionWrapper.of(e);
		log.error("Message : {}, File : [{}], Line : {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<ResponseExceptionWrapper> handleDataIntegrityException(final DataIntegrityViolationException e) {
		final ResponseExceptionWrapper response = ResponseExceptionWrapper.of(InternalRestEnum.INVALID_PARAMETER);
		log.error("Message : {}, File : [{}], Line : {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ResponseExceptionWrapper> handleException(final Exception e) {
		log.error("handle-Exception:{}", e.getMessage());
		final ResponseExceptionWrapper response = ResponseExceptionWrapper.of(InternalRestEnum.INTERNAL_SERVER_ERROR);
		log.error("Message : {}, File : [{}], Line : {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

}