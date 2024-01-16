package com.deotis.digitalars.system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.service.business.UPlusLogService;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.service.rest.external.UPlusApimService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description cron scheduler
 * 
**/
@Slf4j
@Configuration
@EnableScheduling
public class ScheduleConfig  {
	
	@Autowired
	private RedisTemplateService redisTemplateService;
	
	@Autowired
	private UPlusLogService uplusLogService;
	
	@Autowired
	private UPlusApimService uplusApimService;
	
	@Value("${system.scheduled.logfile.directory}")
	private String LOG_FILE_LOCATION;	

	
	@Scheduled(cron = "${system.scheduled.crondata}")
	public void start() {
		int result = redisTemplateService.cleanKeysWithScan();
		log.info("Repository clean job excute result count : {}", result);
	}
	
	@Scheduled(cron = "${system.scheduled.logfile.crondata}")
	public void createLogFile() {
		uplusLogService.createLogFile();
	}

	@Scheduled(cron = "${system.scheduled.apim.crondata}")
	public void createApimAuthToken() {
		log.info("Start APIM oauth token generation scheduler.");
		
		uplusApimService.createAuthToken("pv");
		uplusApimService.createAuthToken("pb");
	}
	
	
	@Scheduled(cron = "${system.scheduled.apim.fallback.crondata}") 
	public void checkApimGwAlive() { 
		// PB GW Fallback인 경우
		if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PB_FALLBACK_KEY))) {
			uplusApimService.checkApimGw("pb"); 
		} 
		// PV GW Fallback인 경우
		else if("Y".equals(redisTemplateService.getKeyOptValue(CommonConstants.PV_FALLBACK_KEY))){
			uplusApimService.checkApimGw("pv"); 
		}
	}
}