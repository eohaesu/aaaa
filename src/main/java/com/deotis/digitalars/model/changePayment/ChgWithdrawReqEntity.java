package com.deotis.digitalars.model.changePayment;

import lombok.Data;

/**
 * 결제일변경
 * 
 * @author hyunjung
 */
@Data
public class ChgWithdrawReqEntity {
	
	public String billAcntId;			// 청구계정ID
	public String frstWdrwRgstDt;		// 최초출금등록일(변경후)
	public String frstWdrwRgstDay;		// 최초출금등록일(변경전)
	public String paymMthdCd;			// 납부방법코드
	
}
