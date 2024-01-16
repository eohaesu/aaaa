package com.deotis.digitalars.service.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deotis.digitalars.model.common.LogData;

import lombok.extern.slf4j.Slf4j;

/** 
 * @author jihyun
 * @description service for logFile
 */
@Slf4j
@Service
public class UPlusLogService {
	
	@Value("${system.scheduled.logfile.directory}")
	public String LOG_FILE_LOCATION;	

	public static volatile File lastCreateFile; // 최근 생성된 로그 파일
	
	/**
	 * 물리 로그 파일 생성
	 */
	public void createLogFile() {
		LocalDateTime nowDtm = LocalDateTime.now();		
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMddHHmm");
        
        long fileCreatedPeriod = Long.MIN_VALUE;
		
        File lastFile = null; 	// 최근 생성 또는 수정된 로그 파일
		
		// 재기동시 전역변수 lastCreateFile이 null 일 수 있기 때문에
		// lastCreateFile이 null인 경우 최근 수정된 로그 파일을 가져오도록 함
		if(lastCreateFile != null) {
			lastFile = lastCreateFile;
		} else {
			lastFile =getLastModifyFile();
		}
		
		log.debug("lastFile => {}", lastFile);
		
		String lastFileName = "";
		
		if(lastFile != null){
			int index = lastFile.toString().indexOf("WISEPORTAL_WAS_");
			lastFileName = FilenameUtils.removeExtension(lastFile.toString().substring(index).replace("WISEPORTAL_WAS_", ""));
			
		    LocalDateTime fileDateTime = LocalDateTime.of(
		    		Integer.parseInt(lastFileName.substring(0,4)),
		    		Integer.parseInt(lastFileName.substring(4,6)),
		    		Integer.parseInt(lastFileName.substring(6,8)),
		    		Integer.parseInt(lastFileName.substring(8,10)),
		    		Integer.parseInt(lastFileName.substring(10,12))	    		
		    		);
		    
		    Duration dulation = Duration.between(nowDtm, fileDateTime); 
		    
		    fileCreatedPeriod = dulation.getSeconds();//최근 파일 생성 간격
		}

	    if(fileCreatedPeriod <= -300 || lastFileName == "") { // 최근 파일 생성이 5분 이내이거나, 생성된 파일이 없는 경우
	    	LocalDateTime createNowDtm = LocalDateTime.now();
	    	String time = createNowDtm.format(formatter);
	    	
	    	File dirPath = new File(LOG_FILE_LOCATION+time.substring(0,8)+"/");  // 로그 현재날짜 폴더 경로
	    	File file = new File(LOG_FILE_LOCATION+time.substring(0,8)+"/WISEPORTAL_WAS_"+time+".log");  // 로그 파일 경로

	    	try {
	        	if(!file.exists()) { //로그파일이 존재하지 않는 경우에만 생성
	        		// 로그파일을 생성하고자 하는 날짜의 폴더가 존재하지 않는 경우 생성
	        		if(!dirPath.exists()) { 
	        			if(dirPath.mkdir()) {
	        				log.info("create '{}' Date Folder at '{}'", time.substring(0,8), dirPath.getPath());
	        			} else {
	        				log.info("create '{}' Date Folder failed", time.substring(8,12));
	        			}
	        		} 
	        		
		            if(file.createNewFile()) {
		            	log.info("create '{}' LogFile at '{}'", file.getName(), file.getPath());
		            	lastCreateFile = file;
		            } else {
		            	log.info("create '{}' LogFile failed", file.getName());
		            }
	        	}
	        } catch (IOException e) {
	        	log.error(e.toString());
	        }	    	
	    }else {
	    	log.info("LogFile is already existed");
	    }
	}
		
	/**
	 * 로그 파일에 내용 넣기
	 * @param logData
	 */
	public void putLogMsg(LogData logData) {	
		File lastFile = getLastModifyFile();
		
		if(lastCreateFile != null) {
			lastFile = lastCreateFile;		// 최근 생성된 로그 파일
		} else {
			lastFile =getLastModifyFile();	// 최근 수정된 로그 파일
		}
		
	    try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(lastFile, true));
	    	
	    	LocalDateTime nowDtm = LocalDateTime.now();
	    	
	    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("YYYYMMddHHmmssSSS").toFormatter();
	    	String seqId = nowDtm.format(formatter) + String.valueOf((int)(Math.random() * 8999) + 1000) + String.valueOf((int)(Math.random() * 8999) + 1000);
	    	logData.setSeqId(seqId);
	    	
	        String logTime = nowDtm.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
	    	logData.setLogTime(logTime);
	    	
	    	writer.write(logData.toString());
	        writer.newLine();
	        writer.flush();
	        writer.close();	    	
	    }catch(IOException ioe) {
	    	log.error("LogData Exception : {}", ioe.toString());
	    }

	}
	
	/**
	 * @return 최근에 생성된 로그 파일 
	 */
	public File getLastModifyFile() {
		
		// 현재날짜 로그 폴더
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMddHHmm");
		String today = LocalDateTime.now().format(formatter);
		File lastDir = new File(LOG_FILE_LOCATION+today.substring(0,8)+"/");
		
		// 최근에 생성된 로그 파일
		File fileDirectory = new File(LOG_FILE_LOCATION+"/"+lastDir.getName());
		File[] files = fileDirectory.listFiles(File::isFile);
		
	    long lastModifiedTime = Long.MIN_VALUE;
	    File lastFile = null;

	    if (files != null && files.length != 0){
	        for (File file : files){
	            if (file.lastModified() > lastModifiedTime && file.getName().contains("WISEPORTAL_WAS_")){
	            	lastFile = file;
	                lastModifiedTime = file.lastModified();
	            }
	        }
	    }
		
		return lastFile;
	}
}