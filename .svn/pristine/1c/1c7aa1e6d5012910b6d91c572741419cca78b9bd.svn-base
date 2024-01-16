package com.deotis.digitalars.controller.rest.internal;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deotis.digitalars.model.SystemInternalEntity;
import com.deotis.digitalars.system.rest.entity.ResponseEntityWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(value = "/api/system")
@RestController
public class InternalSystemController {
	
	@Value("${system.log.changekey}")
	private String SYSTEM_LOG_CHANGE_KEY;

	private final String[] LevelArray = {"OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"};
	
	@PostMapping( value="/config/getLogLevel" )
    public ResponseEntityWrapper<SystemInternalEntity> getLogLevel(HttpServletRequest request){
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		
		Logger logger = loggerContext.getLogger("com.deotis.digitalars");
		
		Level justLevel = logger.getLevel();

		SystemInternalEntity resultEntity = new SystemInternalEntity(request.getRemoteAddr(), request.getSession().getId());
		
		resultEntity.setLevel(justLevel.toString());
		resultEntity.setProcessMessage("DigitalArs Log Level is "+justLevel);
		resultEntity.setProcessResult(true);
		
		log.debug("request getLogLevel from : {}", request.getRemoteAddr());

		return new ResponseEntityWrapper<SystemInternalEntity>(resultEntity);	

	}

	@PostMapping( value="/config/setLogLevel" )
    public ResponseEntityWrapper<SystemInternalEntity> setLogLevel(HttpServletRequest request, @RequestBody String data) throws MissingServletRequestParameterException{
		
		String message = "Unable change log level. Please check the parameters.";

		SystemInternalEntity resultEntity = new SystemInternalEntity(request.getRemoteAddr(), request.getSession().getId());
		
		resultEntity.setProcessMessage(message);
		
		log.debug("setLogLevel requestBody : {}", data);

		if(StringUtils.hasLength(data)) {
			
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			
			Logger logger = loggerContext.getLogger("com.deotis.digitalars");

			try {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> result = new ObjectMapper().readValue(data, Map.class);
				
				String levelStr = (String) result.get("LEVEL");
				String changeKey = (String) result.get("KEY");

				if(Arrays.stream(LevelArray).anyMatch(levelStr::equalsIgnoreCase) && SYSTEM_LOG_CHANGE_KEY.equals(changeKey)){

					Level oldLevel = logger.getLevel();
			        logger.setLevel(Level.toLevel(levelStr));

			        message = "The Log Level just change from " + oldLevel + " to " + logger.getLevel();
			        resultEntity.setLevel(levelStr);
			        resultEntity.setProcessMessage(message);
			        resultEntity.setProcessResult(true);
				}
				
			} catch (JsonProcessingException e) {
				throw new MissingServletRequestParameterException("Unable parse from data parameter", "String");
			}
		}

		return new ResponseEntityWrapper<SystemInternalEntity>(resultEntity);	
    }

}
