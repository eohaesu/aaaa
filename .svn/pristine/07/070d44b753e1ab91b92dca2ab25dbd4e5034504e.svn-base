package com.deotis.digitalars.controller.business;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.constants.ResultStrConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.changePayment.BillAcntInfo;
import com.deotis.digitalars.model.changePayment.ChgMethodReqEntity;
import com.deotis.digitalars.model.changePayment.ChgWithdrawReqEntity;
import com.deotis.digitalars.model.changePayment.VerifyAcctReqEntity;
import com.deotis.digitalars.model.common.BankInfo;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.ChangePaymentService;
import com.deotis.digitalars.service.rest.external.UPlusApimService;
import com.deotis.digitalars.system.handler.SessionHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/changePayment")
public class ChangePaymentController {

	private final UPlusApimService uplusApimService;
	private final ChangePaymentService changePaymentService;
	
	/**
	 * 납부방법변경 - 납부수단 변경
	 * @param model
	 * @return
	 */
	@GetMapping(value="/cp0002")
	public String payMethodChange(Model model) {
		
		ArrayList<BankInfo> bankList = uplusApimService.getBankList("A");
		model.addAttribute("bankList", bankList);
		
		log.info("            ::::: [ CHANGE PAYMENT ] cp0002 init :::::            ");
		
		return "contents/business/changePayment/cp0002";
		
	}

	/**
	 * 납부방법변경 - 결제일 변경
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/cp0003")
	public String payDateChange(Model model) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		BillAcntInfo billAcntInfo = entity.getBillAcntInfo();
		
		model.addAttribute("billInfo", billAcntInfo);
		model.addAttribute("withdrawDt", entity.getWithdrawInfo().getFrstWdrwRgstDd());
		
		log.info("            ::::: [ CHANGE PAYMENT ] cp0003 init :::::            ");
		
		return "contents/business/changePayment/cp0003";
		
	}
	
	/**
	 * 납부방법변경 - 납부수단 변경 완료
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/cp0004")
	public String chgPayComplete(Model model) {
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();
		if(secretEntity.getCurrentSvcNo()+1 <= secretEntity.getTotalSvcNo()) {
			model.addAttribute("continue", true);
		} else {
			model.addAttribute("continue", false);
		}
		
		log.info("            ::::: [ CHANGE PAYMENT ] cp0004 init :::::            ");
		
		return "contents/business/changePayment/cp0004";
		
	}
	
	/**
	 * 납부방법변경 - 결제일 변경 완료 
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/cp0005")
	public String chgWithdComplete(Model model) {
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();
		if(secretEntity.getCurrentSvcNo()+1 <= secretEntity.getTotalSvcNo()) {
			model.addAttribute("continue", true);
		} else {
			model.addAttribute("continue", false);
		}
		
		log.info("            ::::: [ CHANGE PAYMENT ] cp0005 init :::::            ");
		
		return "contents/business/changePayment/cp0005";
		
	}
	
	/**
	 * 납부방법변경 - 납부수단 변경 실패
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/cp0006")
	public String chgPayFail(Model model) {
		
		log.info("            ::::: [ CHANGE PAYMENT ] cp0006 init :::::            ");
		
		return "contents/business/changePayment/cp0006";
		
	}
	
	/**
	 * 납부방법변경 - 결제일 변경 실패
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/cp0007")
	public String chgWithdFail(Model model) {
		
		log.info("            ::::: [ CHANGE PAYMENT ] cp0007 init :::::            ");
		
		return "contents/business/changePayment/cp0007";
		
	}
	
	/**
	 * 계좌 및 이름인증
	 * @param params
	 * @return
	 */
	@PostMapping(value="/verifyAcct")
	@ResponseBody
	public Map<String, Object> verifyAcct(VerifyAcctReqEntity params) {
		
		log.info("            ::::: [ CHANGE PAYMENT ] Verify Account Infomation :::::            ");
		
		Map<String, Object> result = uplusApimService.verifyPayMthd(params);
		
		return result;
	}
	
	/**
	 * 납부수단 변경
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/chgPayMethod")
	@ResponseBody
	public String chgPayMethod(ChgMethodReqEntity params) {
		
		String result = ResultStrConstants.FAIL;
		
		if("CC".equals(params.getPaymMthdCd())) {	// 카드인 경우
			
			String expPeriod = params.getCardValdEndYymm();
			expPeriod = expPeriod.substring(2,4) + expPeriod.substring(0,2);	// YYMM 형태로 변환
			
			// 카드 인증을 위한 객체 생성
			VerifyAcctReqEntity verify = VerifyAcctReqEntity.builder()
					.custBasKdCd(params.getCustBasKdCd())
					.custDetlKdCd(params.getCustDetlKdCd())
					.brno(params.getCrypRnno())			// 개인사업자인 경우에 셋팅
					.pymCrypRnno(params.getCrypRnno())
					.acntOwnrNm(params.acctCardOwnrNm)
					.cardValdEndYymm(expPeriod)
					.cardNo(params.getEcno())
					.cdcoCd(params.getCdcoCd())
					.paymMthdCd("CC").build();
		
			log.info("            ::::: [ CHANGE PAYMENT ] Verify Card Information :::::            ");
			
			Map<String, Object> verifyRst = uplusApimService.verifyPayMthd(verify);	// 신용카드 인증 API 호출
			Map<String, String> cmsResult = (Map<String, String>) verifyRst.get("dma_authRslt");
			
			if(cmsResult != null && "00".equals(cmsResult.get("cmsResultCode"))) {	// 카드 인증 성공
				log.info("            ::::: [ CHANGE PAYMENT ] Change Payment Method :::::            ");
				
				result = changePaymentService.chgPayMethod(params);
			} else {
				return result;
			}
		} else {
			log.info("            ::::: [ CHANGE PAYMENT ] Change Payment Method :::::            ");
			
			result = changePaymentService.chgPayMethod(params);
		}
		
		return result;
		
	}

	/**
	 * 결제일 변경
	 * @param params
	 * @return
	 */
	@PostMapping(value="/chgPayWithdraw")
	@ResponseBody
	public String chgPayWithdraw(ChgWithdrawReqEntity params) {
		
		log.info("            ::::: [ CHANGE PAYMENT ] Change Payment Method :::::            ");
		
		String result = changePaymentService.chgWithdrawDate(params);
		
		return result;
		
	}

}
