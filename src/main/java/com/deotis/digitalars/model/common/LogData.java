package com.deotis.digitalars.model.common;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 물리 로그 파일
 * 
 * @author DEOTIS
 *
 */
@Getter
@Setter
public class LogData implements Serializable {

	private static final long serialVersionUID = 8157550425788639682L;
	private String seqId="";         		// 로그 단위 Unique한 ID
	private String logTime="";       		// 로그를 파일에 Write 시점 시간
	private String logType="SVC";		    // 로그 타입 (SVC로 고정)
	private String sid="";           		// 사용자 아이디
	private String resultCode="";    		// 서비스 상태코드 (성공, 실패, 구간실패)
	private String reqTime="";       		// 서비스 전체 요청 발생 시간
	private String rspTime="";       		// 서비스 전체 응답 완료 시간
	private String clientIp="";      		// 접속 클라이언트 IP
	private String devInfo="";       		// 접속 단말 타입
	private String osInfo="";        		// OS 정보
	private String nwInfo="";        		// 접속 네트워크 정보
	private String svcName="AICALLBOT";   	// 서비스 또는 시스템 명
	private String devModel="";      		// 단말 모델명  
	private String carrierType="L";   		// 통신사 구분
	
	private String hostName="";     		// 호스트 이름
	private String callId="";       		// CALL ID (ICID)
	private String crid="";          		// CRID
	private String custNo="";        		// 고객번호
	private String entrNo="";        		// 가입번호
	private String transactionId=""; 		// Transaction ID (USID)
	private String moduleApi="";     		// WISEPORTAL 사용 Legacy API
	private String moduleRsp="";    		// 모듈 응답코드
	private String errorMsg="";      		// 처리 결과 메시지
	 
	
	@Builder
	public LogData(String sid, String resultCode, String devInfo, String hostName, String callId, String crid, 
			String custNo, String entrNo, String transactionId, String moduleApi, String moduleRsp, String errorMsg)
	{
		this.sid = sid;
		this.resultCode = resultCode;
		this.devInfo = devInfo;
		this.hostName = hostName;
		this.callId = callId;
		this.crid = crid;
		this.custNo = custNo;
		this.entrNo = entrNo;
		this.transactionId = transactionId;
		this.moduleApi = moduleApi;
		this.moduleRsp = moduleRsp;
		this.errorMsg = errorMsg;
	}

	public LogData() {
		// TODO Auto-generated constructor stub
	}

	/** 
	 * @return 물리 로그 파일에 넣을 내용
	 */
	public String toString() {
		return "SEQ_ID="+getSeqId()+"|"+
			   "LOG_TIME="+getLogTime()+"|"+
			   "LOG_TYPE="+getLogType()+"|"+
			   "SID="+getSid()+"|"+
			   "RESULT_CODE="+getResultCode()+"|"+
			   "REQ_TIME="+getReqTime()+"|"+
			   "RSP_TIME="+getRspTime()+"|"+
			   "CLIENT_IP="+getClientIp()+"|"+
			   "DEV_INFO="+getDevInfo()+"|"+
			   "OS_INFO="+getOsInfo()+"|"+
			   "NW_INFO="+getNwInfo()+"|"+
			   "SVC_NAME="+getSvcName()+"|"+
			   "DEV_MODEL="+getDevModel()+"|"+
			   "CARRIER_TYPE="+getCarrierType()+"|"+
			   "HOST_NAME="+getHostName()+"|"+
			   "CALL_ID="+getCallId()+"|"+
			   "CRID="+getCrid()+"|"+
			   "CUST_NO="+getCustNo()+"|"+
			   "ENTR_NO="+getEntrNo()+"|"+
			   "TRANSACTION_ID="+getTransactionId()+"|"+
			   "MODULE_API="+getModuleApi()+"|"+
			   "MODULE_RSP="+getModuleRsp()+"|"+
			   "ERROR_MSG="+getErrorMsg();
	}
}
