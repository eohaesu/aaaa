package com.deotis.digitalars.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.deotis.digitalars.model.changePayment.BillAcntInfo;
import com.deotis.digitalars.model.changePayment.WithdrawInfo;
import com.deotis.digitalars.model.common.CntcSvcInfo;
import com.deotis.digitalars.model.common.LogData;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 
 * @author jongjin
 * @description base session user entity
 */

@Getter
@Setter
@ToString
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 8333114936264245561L;
	private String sid;
	private String siteCode;
	private String wmsAccessDeviceCode;
	private boolean recieve_callend;
	private String wmsEventName;
	private String counselorWaitTime;
	private String counselorWaitCount;
	private Object ivrControlInfo;
	private String customerName;
	private String memberType;
	private String workTime;
	private String timeStatus;                  	// 1:주간, 2:야간, 3:심야공휴일주말
	private String ctiConnId; 

	private Integer wmsConnectFailCount;
	private Integer showArsProgressType;        	// ivr전문처리 대기용 progressPage 처리
	private String progressRedirect;            	// goToEnd 대기 후 redirection 페이지 정보
	private SiteInfo siteInfo;
	
	private LogData logData;                    	// TLO 로그
	
	private Map<String, Object> custCntcInfo;   	// 고객가입정보 (전달받은 가입번호로 조회)
	private ArrayList<CntcSvcInfo> cntcSvcList;		// 고객가입서비스정보 (고객가입정보의 청구계정으로 조회)
	private Map<String, Object> payInfo;        	// 청구요금정보
	private String authNum;							// 발송된 인증번호
	
	private BillAcntInfo billAcntInfo;				// 현재납부방법정보
	private WithdrawInfo withdrawInfo;				// 현재출금일정보
	
	private String createPayLinkDate;				// 청구서링크 생성일자
	private String icid;							//icid
	
	@Builder
	public UserEntity(String sid, 
			String siteCode, 
			String wmsAccessDeviceCode, 
			String customerName, 
			String memberType, 
			String timeStatus,
			String ctiConnId,
			Map<String, String> logInit
			) {
		this.sid = sid;
		this.siteCode = siteCode;
		this.wmsAccessDeviceCode = wmsAccessDeviceCode;
		this.customerName = customerName;
		this.memberType = memberType;
		this.timeStatus = timeStatus;
		this.ctiConnId = ctiConnId;
		this.recieve_callend = false;
		this.showArsProgressType = 0;
		this.wmsConnectFailCount = 0;
		
		if("1".equals(timeStatus)) {//주간
			this.workTime = "090000-180000";
		}else if("2".equals(timeStatus)) {//야간
			this.workTime = "180000-190000";
		}else {//심야
			this.workTime = "190000-090000";
		}
		
		this.logData = LogData.builder()
							  .sid(logInit.get("ani"))
							  .devInfo(logInit.get("devInfo"))
							  .hostName(logInit.get("hostName"))
							  .callId(logInit.get("icid"))
							  .crid(logInit.get("crid"))
							  .custNo(logInit.get("custNo"))
							  .entrNo(logInit.get("entrNo"))
							  .transactionId(sid).build();
	}
	
	
	
}
