package com.deotis.digitalars.system.rest.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.deotis.digitalars.system.exception.InternalRestEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description Exception class for api entity wrap
 */

@Slf4j
@Setter
@Getter
@ToString
public class ResponseExceptionWrapper {

    private final int status;
    private final String code;
    private final String message;
    private final List<FieldError> errors;
   

    private ResponseExceptionWrapper(final InternalRestEnum code, final List<FieldError> errors) {
      
        this.status = code.getHttpStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = errors;
        
    }

    private ResponseExceptionWrapper(final InternalRestEnum code) {
    	
    	this.status = code.getHttpStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = new ArrayList<>();
        
    }

    public static ResponseExceptionWrapper of(final InternalRestEnum code, final BindingResult bindingResult) {
    	log.debug("InternalRestEnum code: [{}]", code);
        return new ResponseExceptionWrapper(code, FieldError.of(bindingResult));
    }

    public static ResponseExceptionWrapper of(final InternalRestEnum code) {
    	log.debug("InternalRestEnum code: [{}]", code);
        return new ResponseExceptionWrapper(code);
    }

    public static ResponseExceptionWrapper of(final InternalRestEnum code, final List<FieldError> errors) {
    	log.debug("InternalRestEnum code: [{}]", code);
        return new ResponseExceptionWrapper(code, errors);
    }

    public static ResponseExceptionWrapper of(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        log.debug("MethodArgumentTypeMismatchException: [{}], value: [{}]", e.getName(), value);
        final List<ResponseExceptionWrapper.FieldError> errors = ResponseExceptionWrapper.FieldError.of(e.getName(), value, e.getErrorCode());
        return new ResponseExceptionWrapper(InternalRestEnum.INVALID_PARAMETER, errors);
    }
    
    public static ResponseExceptionWrapper of(MissingServletRequestParameterException e) {
    	log.debug("MissingServletRequestParameterException: [{}], Message: [{}]", e.getParameterName(), e.getMessage());
        final List<ResponseExceptionWrapper.FieldError> errors = ResponseExceptionWrapper.FieldError.of(e.getParameterName(), e.getParameterType(), e.getMessage());
        return new ResponseExceptionWrapper(InternalRestEnum.INVALID_PARAMETER, errors);
    }

    @Getter
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}