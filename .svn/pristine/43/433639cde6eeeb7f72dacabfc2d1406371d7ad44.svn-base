package com.deotis.digitalars.security.model;

import java.io.Serializable;

import org.springframework.security.core.SpringSecurityCoreVersion;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @description security call entity from wasstart of wms
 */

@Getter
@Setter
@ToString
public class SecretEntity implements Serializable{
	
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String siteCode;
	private String dnis;
	private String ani;			// 01012345678 형태로 저장됨
	private String wmsAccessDeviceCode;
	private String userData;
	private String launcherName;
	private String appBindDetails;
	private String wcSeq;
	private String icid;
	private String btoken;
	private String svcId;        // 서비스ID
	private String titleName;
	private String custNo;       // 고객번호
	private String entrNo;       // 가입번호
	private String custrnmBday;  // 생년월일
	private String dnisType;	 // 홈,모바일 구분값
	private String crid;
	
	private int totalSvcNo;      // 진행할 전체 서비스 갯수
	private int currentSvcNo;    // 현재 진행중인 서비스 순서
	private String[] svcIdList;  // 서비스ID 리스트
	
	@Builder
	public SecretEntity(
			String siteCode,
			String dnis,
			String ani,
			String wmsAccessDeviceCode,
			String userData,
			String launcherName,
			String appBindDetails,
			String wcSeq,
			String icid,
			String btoken,
			String crid) {	
		this.siteCode = siteCode;
		this.dnis = dnis;
		this.ani = ani;
		this.wmsAccessDeviceCode = wmsAccessDeviceCode;
		this.userData = userData;
		this.launcherName = launcherName;
		this.appBindDetails = appBindDetails;
		this.wcSeq = wcSeq;
		this.icid = icid;
		this.btoken = btoken;
		this.crid = crid;
	}
	
}
