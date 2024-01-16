package com.deotis.digitalars.controller.business;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import com.deotis.digitalars.constants.ResultStrConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.changePayment.BillAcntInfo;
import com.deotis.digitalars.model.changePayment.WithdrawInfo;
import com.deotis.digitalars.model.common.CntcSvcInfo;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.ChangePaymentService;
import com.deotis.digitalars.service.business.PaymentService;
import com.deotis.digitalars.service.rest.external.UPlusApimService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.util.common.CommonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

	@Value("${system.test.ivr}")
	private boolean SYSTEM_TEST_IVR;
	private final MessageSource messageSource;
	private final UPlusApimService uplusApimService;
	private final PaymentService paymentService;
	private final ChangePaymentService changePaymentService;
	
	/**
	 * main 페이지
	 * @param model
	 * @param session
	 * @param request
	 * @param response
	 * @return String
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping(value = "/main")
	public String arsMain(ModelMap model) {

		SecretEntity secret = SessionHandler.getSecretEntity();
		UserEntity entity = SessionHandler.getSessionEntity();

		if(secret.getAppBindDetails() != null && !"".equals(secret.getAppBindDetails())) {
			model.addAttribute("packageName", new JSONObject(secret.getAppBindDetails()).getString("packageName"));
		}

		log.debug("Attempt to MainPage SOE entity : [{}]", secret);
		
		if(StringUtils.hasLength(secret.getSvcId())) {
			
			String[] svcIdList = secret.getSvcId().split("\\|");
			secret.setSvcIdList(svcIdList);
			secret.setTotalSvcNo(svcIdList.length);
			secret.setCurrentSvcNo(secret.getCurrentSvcNo() != 0 ? secret.getCurrentSvcNo() : 1);  // SMS URL 재인입인 경우 진행중인 서비스로 셋팅
			
			log.debug("            ::::: Current Service [{}/{}] {} :::::            ", secret.getCurrentSvcNo(), secret.getTotalSvcNo(), svcIdList[secret.getCurrentSvcNo()-1]);
			
			// 고객계약정보조회 API - 콜봇으로부터 전달받은 가입번호로 고객 가입 정보 조회하기 위함 (청구계정 조회)
			Map<String, Object> custInfo = uplusApimService.getCustContInfo();
			
			if(custInfo.get("dma_custCntcInfo") == null) {
				model.addAttribute("msg", messageSource.getMessage("payment.getCustInfo.failed", null, LocaleContextHolder.getLocale()));
				return "contents/common/commonError";
			}
			
			Map<String, Object> custCntcInfo = (Map<String, Object>) custInfo.get("dma_custCntcInfo");
			entity.setCustCntcInfo(custCntcInfo);
			
			// 서비스가입정보조회 API - 위에서 조회한 청구계정에 대한 가입서비스 목록을 조회하기 위함
			ArrayList<Map<String, String>> cntcSvcInfo = uplusApimService.getCntcSvcInfo();
			if(cntcSvcInfo == null) {
				model.addAttribute("msg", messageSource.getMessage("payment.getCustInfo.failed", null, LocaleContextHolder.getLocale()));
				return "contents/common/commonError";
			}
			
			ArrayList<CntcSvcInfo> cntcInfoList = new ArrayList<CntcSvcInfo>();
			for(Map<String, String> svcOne : cntcSvcInfo) {
				cntcInfoList.add(CntcSvcInfo.toEntity(svcOne));
			}
			entity.setCntcSvcList(cntcInfoList);
			
			model.addAttribute("cntcSvcList", entity.getCntcSvcList());		// 가입서비스목록
			model.addAttribute("countSvc", entity.getCntcSvcList().size());	// 가입서비스갯수
			
			// 콜봇으로부터 전달받은 가입번호에 대한 가입서비스 상품정보 (상품번호, 가입상품명, 서비스코드)
			byte[] decProdNo = Base64Utils.decodeFromString(custCntcInfo.get("prodNo").toString());
			String prodNo = new String(decProdNo);
			
			if("LZP0000001".equals(custCntcInfo.get("svcCd").toString())) { // 모바일
				prodNo = CommonUtil.phonePartlyMasking(prodNo, "uplus");
			} else { // 홈
				prodNo = CommonUtil.entrIdMasking(prodNo);
			}
			model.addAttribute("prodNo", prodNo);    						// 상품번호
			model.addAttribute("prodNm", custCntcInfo.get("prodNm"));      	// 가입상품명
			model.addAttribute("svcCd", custCntcInfo.get("svcCd"));        	// 서비스코드
			model.addAttribute("btoken", secret.getBtoken());
			
			if(secret.getCurrentSvcNo() <= secret.getTotalSvcNo()) {
				switch(svcIdList[secret.getCurrentSvcNo()-1]) {
				case "recp_01" :  // 요금납부
		
					// 연체및기타정보조회 API
					Map<String, Object> arerEtcInfo = paymentService.getArerEtcInfo("A", null);
					
					if(arerEtcInfo.get("result") != null && arerEtcInfo.get("result").toString() == ResultStrConstants.FAIL) {
						model.addAttribute("msg", messageSource.getMessage("payment.getCharge.failed", null, LocaleContextHolder.getLocale()));
						return "contents/common/commonError";
					} 
					
					entity.setPayInfo(arerEtcInfo);	// 청구계정에 대한 청구요금 정보
					
					// 가입서비스별 청구요금 조회
					for(CntcSvcInfo svcOne  : entity.getCntcSvcList()) {
						Map<String, Object> billInfo = paymentService.getArerEtcInfo("E", svcOne.getEntrId());
						svcOne.setCurrentBill(billInfo.get("curBillAmt").toString());
					}
					model.addAttribute("cntcSvcList", entity.getCntcSvcList());		// 가입서비스목록
					
					log.info("            ::::: [ PAYMENT ] p0001 init :::::            ");
			        
					model.addAttribute("paidAmt", CommonUtil.moneyFormat(entity.getPayInfo().get("arBalance").toString()));	 // 납부금액(당월요금+미납요금)
					model.addAttribute("arrearAmt", CommonUtil.moneyFormat(entity.getPayInfo().get("arerAmt").toString()));	 // 연체금액
					model.addAttribute("curAmt", CommonUtil.moneyFormat(entity.getPayInfo().get("curBillAmt").toString()));  // 당월청구금액
					return "contents/business/payment/p0001";
					
				case "chg_01" : // 납부방법변경
					
					// 납부방법조회
					Map<String, Object> payMethodRst = changePaymentService.getPayMethod();
					
					if(payMethodRst.get("result") != null && payMethodRst.get("result").toString() == ResultStrConstants.FAIL) {
						model.addAttribute("msg", messageSource.getMessage("change.payment.getPayMthd.failed", null, LocaleContextHolder.getLocale()));
						return "contents/common/commonError";
					}
					
					ArrayList payMethodList = (ArrayList) payMethodRst.get("dtl_billAcntInfo");
					Map<String, String> payMethodInfo = (Map<String, String>) payMethodList.get(0);
					BillAcntInfo billAcntInfo = BillAcntInfo.toEntity(payMethodInfo);
					entity.setBillAcntInfo(billAcntInfo);
					
					// 출금일조회
					WithdrawInfo withdrawInfo = null;
					
					if(!"GR".equals(billAcntInfo.getPaymMthdCd())) { // 납부방법이 '지로'가 아닌 경우
						withdrawInfo = changePaymentService.getWithdrawDate();
						entity.setWithdrawInfo(withdrawInfo);
					}
					
					if(!"GR".equals(billAcntInfo.getPaymMthdCd()) && (withdrawInfo.getFrstWdrwRgstDd() == null || "".equals(withdrawInfo.getFrstWdrwRgstDd()))) {
						model.addAttribute("msg", messageSource.getMessage("change.payment.getWithdraw.failed", null, LocaleContextHolder.getLocale()));
						return "contents/common/commonError";
					}
					
					// 마스킹 처리
					if("CM".equals(billAcntInfo.getPaymMthdCd())) { // 계좌이체인 경우
						billAcntInfo.setEano(CommonUtil.payMthdNumMasking(billAcntInfo.getEano(), null));
					} else if("CC".equals(billAcntInfo.getPaymMthdCd())) { // 카드인 경우
						billAcntInfo.setEcno(CommonUtil.payMthdNumMasking(billAcntInfo.getEcno(), "card"));
					}
					
					model.addAttribute("payMethodInfo", billAcntInfo);
					model.addAttribute("withdrawInfo", withdrawInfo);
					
					log.info("            ::::: [ CHANGE PAYMENT ] cp0001 init :::::            ");
					
					return "contents/business/changePayment/cp0001";
				case "counsel_01" :	// 상담예약
					return "contents/business/counselRequest/cr0001";
				default:
					log.info("            ::::: The service you requested is not available. => [{}] :::::            ", svcIdList[secret.getCurrentSvcNo()-1]);
					model.addAttribute("msg", messageSource.getMessage("common.svcid.null", null, LocaleContextHolder.getLocale()));
					return "contents/common/commonError";
				}
			} else {
				log.info("            ::::: The service you requested has been completed. :::::            ");
				model.addAttribute("msg", messageSource.getMessage("common.svcid.null", null, LocaleContextHolder.getLocale()));
				return "contents/common/commonError";
			}
		}else {
			log.info("            ::::: There is no service requested. :::::            ");
			model.addAttribute("msg", messageSource.getMessage("common.svcid.null", null, LocaleContextHolder.getLocale()));
			return "contents/common/commonError";
		}
	}
}
