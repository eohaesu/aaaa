package com.deotis.digitalars.model.changePayment;

import java.io.Serializable;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * @author hyunjung
 * @description Withdrawal date inquiry response
 */

@Builder
@Data
public class WithdrawInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String frstWdrwRgstDd;  // 최초출금일
	private String bankCd;			// 은행코드
	private String wdrwAgrmtCd;		// 출금동의코드
	
	public static WithdrawInfo toEntity(Map<String, String> wdrawInfo) {
		return WithdrawInfo.builder()
				.frstWdrwRgstDd(wdrawInfo.get("frstWdrwRgstDd"))
				.bankCd(wdrawInfo.get("bankCd"))
				.wdrwAgrmtCd(wdrawInfo.get("wdrwAgrmtCd"))
				.build();
	}
	
}
