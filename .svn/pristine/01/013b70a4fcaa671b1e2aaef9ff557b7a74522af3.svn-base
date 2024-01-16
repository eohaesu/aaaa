package com.deotis.digitalars.system.rest.entity;

import java.util.List;

import com.deotis.digitalars.system.exception.InternalRestEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @param <T>
 * @description class for api entity wrap
 */

@Setter
@Getter
@ToString
public class ResponseEntityWrapper<T>{
	
	private final int status;
	private final String code;
	private final String message;
	private T entity;
	private List<T> entitys;
	private Integer count;

	@Builder
	public ResponseEntityWrapper(T entity)
	{
		this.status = InternalRestEnum.RESULT_SUCCESS.getHttpStatus();
		this.code = InternalRestEnum.RESULT_SUCCESS.getCode();
		this.message = InternalRestEnum.RESULT_SUCCESS.getMessage();
		this.entity = entity;
	}

	@Builder
	public ResponseEntityWrapper(List<T> entitys)
	{
		this.status = InternalRestEnum.RESULT_SUCCESS.getHttpStatus();
		this.code = InternalRestEnum.RESULT_SUCCESS.getCode();
		this.message = InternalRestEnum.RESULT_SUCCESS.getMessage();
		this.entitys = entitys;
	}

	
	public Integer setCount() {
		return entitys != null && !entitys.isEmpty() ? entitys.size() : 0;
	}

}