package com.deotis.digitalars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.deotis.digitalars.service.business.UPlusLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jihyun.choi
 * @description Class for App startUp
 */
@Slf4j
@Component
public class AppStartupRunner  implements ApplicationRunner{
	
	@Autowired
	private UPlusLogService uplusLogService;

	@Override
	public void run(ApplicationArguments args) throws Exception {	
		log.info("After start job");
		
		//app 구동 시 로그 파일 최초 생성
		uplusLogService.createLogFile();
	}
}
