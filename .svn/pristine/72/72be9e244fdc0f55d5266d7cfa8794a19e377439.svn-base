package com.deotis.digitalars.controller.business;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.model.changePayment.VerifyAcctReqEntity;
import com.deotis.digitalars.model.common.BankInfo;
import com.deotis.digitalars.model.payment.CardInfo;
import com.deotis.digitalars.model.payment.PayChrgReqEntity;
import com.deotis.digitalars.model.payment.PayWthdReqEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.PaymentService;
import com.deotis.digitalars.service.rest.external.UPlusApimService;
import com.deotis.digitalars.system.handler.SessionHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/payment")
public class PaymentController {
	
	private final UPlusApimService uplusApimService;
	private final PaymentService paymentService;
	
	/**
	 * 요금납부 - 결제정보입력
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/p0002")
	public String payInfoInput(Model model) {
		
		ArrayList<BankInfo> bankList = uplusApimService.getBankList("B");
		model.addAttribute("bankList", bankList);
		
		log.info("            ::::: [ PAYMENT ] p0002 init :::::            ");
		
		return "contents/business/payment/p0002";
	}
	
	/**
	 * 요금납부 - 납부 완료
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value="/p0003")
	public String payComplete(Model model) {
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();
		
		if(secretEntity.getCurrentSvcNo()+1 <= secretEntity.getTotalSvcNo()) {
			model.addAttribute("continue", true);
		} else {
			model.addAttribute("continue", false);
		}
		
		log.info("            ::::: [ PAYMENT ] p0003 init :::::            ");
		
		return "contents/business/payment/p0003";
	}
	
	/**
	 * 요금납부 - 납부 실패
	 * 
	 * @return
	 */
	@GetMapping(value="/p0004")
	public String payFail() {
		
		log.info("            ::::: [ PAYMENT ] p0004 init :::::            ");
		
		return "contents/business/payment/p0004";
	}

	/**
	 * 요금납부 - 간편결제 완료
	 * 
	 * @param bank
	 * @param model
	 * @return
	 */
	@GetMapping(value="/p0005")
	public String easyPayComplete(String bank, Model model) {
		
		model.addAttribute("bank", bank);
		
		log.info("            ::::: [ PAYMENT ] p0005 init :::::            ");
		
		return "contents/business/payment/p0005";
	}
	
	/**
	 * 카드사 조회
	 * 
	 * @param cardPrefix
	 * @return
	 */
	@GetMapping(value="/getCardComp")
	@ResponseBody
	public Map<String, Object> getCardComp(@RequestParam(value = "cardPrefix", required = true) String cardPrefix) {
		
		String cardNoPrefix = cardPrefix;
		
		log.info("            ::::: [ PAYMENT ] Get card company :::::            ");
		
		Map<String, Object> result = uplusApimService.getCardComp(cardNoPrefix);
		
		return result;
		
	}
	
	/**
	 * 신용카드(본인) 납부
	 * 
	 * @param cardNo
	 * @param validYm
	 * @param allotMonth
	 * @param payAmount
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/myPayCharge")
	@ResponseBody
	public Map<String, Object> myPayCharge(CardInfo params) {
		
		// 카드 인증을 위한 객체 생성
		VerifyAcctReqEntity verify = VerifyAcctReqEntity.builder()
				.custBasKdCd(params.getCustBasKdCd())
				.custDetlKdCd(params.getCustDetlKdCd())
				.brno(params.getBrno())
				.pymCrypRnno(params.getCustRnno())
				.acntOwnrNm(params.getCardOwnNm())
				.cardValdEndYymm(params.getCardValdEndYymm())
				.cardNo(params.getCardNo())
				.cdcoCd(params.getCardCd())
				.paymMthdCd("CC").build();
		
		log.info("            ::::: [ PAYMENT ] Verify Card Information :::::            ");
		
		Map<String, Object> verifyRst = uplusApimService.verifyPayMthd(verify);	// 계좌,카드 및 이름인증 API 호출
		Map<String, String> cmsResult = (Map<String, String>) verifyRst.get("dma_authRslt");
		
		if(cmsResult != null && "00".equals(cmsResult.get("cmsResultCode"))) {	// 카드 인증 성공
			// 신용카드 납부를 위한 객체 생성
			PayChrgReqEntity cardInfo = PayChrgReqEntity.builder()
					.payAmt(params.getPayAmt())
					.cardNo(params.getCardNo())
					.cardCompCd(params.getCardCd())
					.cardValidYm(params.getCardValdEndYymm())
					.cardOwnBirth(params.getCustRnno())
					.cardInstMonth(params.getCardInst()).build();
			
			log.info("            ::::: [ PAYMENT ] Pay with my credit card :::::            ");
			
			Map<String, Object> result = paymentService.payCardChrg(cardInfo);
			
			return result;
		} else {
			return null;
		}
		
	}
	
	/**
	 * 신용카드(타인) 납부
	 * 
	 * @param cardNo
	 * @param validYm
	 * @param allotMonth
	 * @param payAmount
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/othersPayCharge")
	@ResponseBody
	public Map<String, Object> othersPayCharge(CardInfo params) {
		
		// 카드 인증을 위한 객체 생성
		VerifyAcctReqEntity verify = VerifyAcctReqEntity.builder()
				.custBasKdCd(params.getCustBasKdCd())
				.custDetlKdCd(params.getCustDetlKdCd())
				.brno(params.getBrno())
				.pymCrypRnno(params.getCustRnno())
				.acntOwnrNm(params.getCardOwnNm())
				.cardValdEndYymm(params.getCardValdEndYymm())
				.cardNo(params.getCardNo())
				.cdcoCd(params.getCardCd())
				.paymMthdCd("CC").build();
		
		log.info("            ::::: [ PAYMENT ] Verify Card Information :::::            ");
		
		Map<String, Object> verifyRst = uplusApimService.verifyPayMthd(verify);	// 계좌,카드 및 이름인증 API 호출
		Map<String, String> cmsResult = (Map<String, String>) verifyRst.get("dma_authRslt");
		
		if(cmsResult != null && "00".equals(cmsResult.get("cmsResultCode"))) {	// 카드 인증 성공
			// 신용카드 납부를 위한 객체 생성
			PayChrgReqEntity cardInfo = PayChrgReqEntity.builder()
					.payAmt(params.getPayAmt())
					.cardNo(params.getCardNo())
					.cardCompCd(params.getCardCd())
					.cardValidYm(params.getCardValdEndYymm())
					.cardOwnBirth(params.getCustRnno())
					.cardInstMonth(params.getCardInst()).build();
						
			log.info("            ::::: [ PAYMENT ] Pay with others credit card  :::::            ");
			
			Map<String, Object> result = paymentService.payCardChrg(cardInfo);
			
			return result;
		} else {
			return null;
		}
		
	}
	
	/**
	 * 간편결제 청구서링크 (PAYCO, 토스, 카카오페이)
	 * 
	 * @param bank
	 * @param payAmt
	 * @return
	 */
	@PostMapping(value="/getBillLink")
	@ResponseBody
	public String getBillLink(String bank, String payAmt) {
		
		String link = "";
	
		log.info("            ::::: [ PAYMENT ] Create bill Link from {} :::::            ", bank);
		
		if("kakao".equals(bank)) {
			link = paymentService.getKakaoLink(payAmt);
		} else if("toss".equals(bank)) {
			link = paymentService.getTossLink(payAmt);
		} else if("payco".equals(bank)) {
			link = paymentService.getPaycoLink(payAmt);
		}
		
		return link;
		
	}
	
	/**
	 * 간편결제 수납내역 조회 (PAYCO, 토스, 카카오페이)
	 * 
	 * @param bank
	 * @param payAmt
	 * @return
	 */
	@PostMapping(value="/getBillComplete")
	@ResponseBody
	public String getBillComplete(String bank) {
		
		String complete = "";
	
		log.info("            ::::: [ PAYMENT ] Check {} payment completion from :::::            ", bank);
		
		if("kakao".equals(bank)) {
			complete = paymentService.getKakaoPayComplete();
		} else if("toss".equals(bank)) {
			complete = paymentService.getTossPayComplete();
		} else if("payco".equals(bank)) {
			complete = paymentService.getPaycoPayComplete();
		}
		
		return complete;
		
	}
	
	/**
	 * 실시간출금
	 * 
	 * @param params
	 * @return
	 */
	@PostMapping(value="/payWithdraw")
	@ResponseBody
	public Map<String, Object> payWithdraw(PayWthdReqEntity params) {
		
		log.info("            ::::: [ PAYMENT ] Pay for real-time withdrawal :::::            ");
		
		Map<String, Object> result = paymentService.payWithdraw(params);
		
		return result;
		
	}
}
