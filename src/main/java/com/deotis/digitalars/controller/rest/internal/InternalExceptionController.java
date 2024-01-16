package com.deotis.digitalars.controller.rest.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deotis.digitalars.system.exception.InternalRestEnum;
import com.deotis.digitalars.system.exception.InternalRestException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description internal rest error handling controller
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/exception/internal")
public class InternalExceptionController{
	
	/**
	 * internal rest exception error handling
	 * @param InternalRestEnum
	 * @return void
	 * @throws InternalRestException 
	 */
	@GetMapping(value = "/{internalRestEnum}")
	public void restError(@PathVariable("internalRestEnum") InternalRestEnum internalRestEnum) throws InternalRestException{
		log.debug("throw Authorization Exception");
		throw new InternalRestException(internalRestEnum);
		
	}


}
