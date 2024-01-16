package com.deotis.digitalars.controller.common;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.constants.ResultStrConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.changePayment.VerifyAcctReqEntity;
import com.deotis.digitalars.model.common.AuthSendReqEntity;
import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.rest.external.UPlusApimService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.util.common.CommonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CommonController {

	//private final MessageSource messageSource; //support for multiple language
	private final UPlusApimService uplusApimService;
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	/**
	 * 기본 진입 페이지
	 * @param model
	 * @param session
	 * @param request
	 * @param response
	 * @return String
	 */
	@GetMapping(value={"", "/"})
	public String income(ModelMap model, @RequestParam(value = "crid", required = false) String crid) {
		if(!StringUtils.hasLength(crid)){
			return "redirect:/auth/accessDenied"; 
		} else {
			model.addAttribute("crid", crid);
			
			//미리 보기용 페이지로 이동
			return "contents/common/preview";
		}

	}

	/**
	 * 콜 종료 후 키패드 화면 전환
	 * 
	 * @return String
	 * @throws Exception 
	 */
	 
	@PostMapping(value = "/common/sessionEndWithParam")
	public String sessionEndWithTrace(
			@RequestParam(value = "pt", required = false, defaultValue = "") String pt,
			@RequestParam(value = "link", required = false, defaultValue = "false") boolean link
			){

		//종료페이지 변경시 파라미터 처리
		String endPointParam = !"".equals(pt) ? "?pt="+pt.replaceAll("[\\r\\n]", "") : "";
		
		//Call end 이후 딥링크 실행
		if(link) {
			SecretEntity secretEntity = SessionHandler.getSecretEntity();
			
			String packageName = new JSONObject(secretEntity.getAppBindDetails()).getString("packageName").replaceAll("[\\r\\n]", "");		
			
			packageName = Base64.getUrlEncoder().encodeToString(packageName.getBytes());

			endPointParam = !"".equals(endPointParam) ? "&pn="+packageName : "?pn="+packageName;

		}
		
		return "redirect:"+SecurityConstants.SESSION_END_URI+endPointParam;
	}
	
	/**
	 * @return
	 */
	@GetMapping(value="/common/timeGuide")
	public String timeGuide(ModelMap model) {		
		return "contents/business/timeGuide";
	}

	/**
	 * 종료 페이지
	 * @param String
	 * @return String
	 */
	@GetMapping(value = "/common/callEnd")
	public String callEnd(ModelMap model, @RequestParam(value = "pn", required = false, defaultValue = "") String pn) {
		
		if(!"".equals(pn)) {
			
			String packageName = "";
			
			try {
				//파라미터 변조 검사
				packageName = new String(Base64.getUrlDecoder().decode(pn));
				
			}catch(IllegalArgumentException e){
				return "redirect:/auth/accessDenied";
			}
			
			model.addAttribute("packageName", packageName);
		}

		
		model.addAttribute("event", "callend");
		
		return "contents/common/endPage";
	}
	
	/**
	 * 중복 브라우저 탭 오류 페이지
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/duplicateNotAllow")
	public String duplicationNotAllow() {
		
		return "contents/common/duplicateNotAllow";
	}
	
	/**
	 * IVR PROGRESS CHECK
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/prgchk")
	public String ivrProgressCheck(ModelMap model) {
		
		UserEntity u_entity = SessionHandler.getSessionEntity();
		
		int progressType = u_entity.getShowArsProgressType();
		String redirectUri = u_entity.getProgressRedirect();
		
		model.addAttribute("showArsProgressType", progressType);
		model.addAttribute("redirectUri", redirectUri);
		
		return "contents/common/progress";
		//return progressType == 0 && !StringUtils.hasLength(redirectUri) ?  "redirect:/main" : "contents/common/progress";
	}
	
	/**
	 * 상담사 근무 시간 확인
	 * 
	 * @return String
	 * @throws ParseException 
	 * @throws Exception 
	 */
	 
	@PostMapping(value = "/common/ajax/checkWorkTime")
	@ResponseBody
	public boolean checkWorkTime(
			@RequestParam(value = "cs_time_st", required = false, defaultValue = "") String cs_time_st,
			@RequestParam(value = "cs_time_ed", required = false, defaultValue = "") String cs_time_ed
			) throws ParseException{
		
		UserEntity userEntity = SessionHandler.getSessionEntity();
		
		String workTime = "090000-180000";
		
		if(StringUtils.hasLength(cs_time_st) && StringUtils.hasLength(cs_time_ed)) {
			workTime = cs_time_st.replace(":", "")+"00-"+cs_time_ed.replace(":", "")+"00";
		}else if(StringUtils.hasLength(userEntity.getWorkTime())){
			workTime = userEntity.getWorkTime();
		}
		
		String[] timeArr = workTime.split("-");					
		
		String startTime = timeArr[0];
		String endTime = timeArr[1];

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HHmmss");
		
		LocalTime str_time = LocalTime.parse(startTime, dateFormat);
		LocalTime end_time = LocalTime.parse(endTime, dateFormat);
		
		LocalTime current_time = LocalTime.now();

		if(current_time.compareTo(str_time) < 0 || end_time.compareTo(current_time) < 0) {
			log.debug("Request Counselor work time check is fail. SID : {}, start time : [{}], end time : [{}], current time : [{}]", userEntity.getSid(), str_time, end_time, current_time);
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * progress 설정 (보이는 self서비스용 프로그레스 셋)
	 * @param request
	 * @return
	 */
	@PostMapping(value="/common/set/progress")
	@ResponseBody
	public boolean progress(@RequestParam(value = "tp", required = true, defaultValue = "1") Integer tp) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.setShowArsProgressType(tp);
		
		return true;
	}
		

	/**
	 * LGU+ 본인인증 - SMS 인증팝업 요청
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/common/getAuthPopup")
	@ResponseBody
	public Map<String, String> getAuthPopup(Model model) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		SecretEntity secret = SessionHandler.getSecretEntity();
		
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("custNm", CommonUtil.nameMasking(new String(Base64Utils.decodeFromString(entity.getCustCntcInfo().get("custNm").toString()))));
		params.put("telNo", secret.getAni());	// 인입번호
		
		return params;
	}
	
	/**
	 * 인증번호 발송
	 * 
	 * @param cardOwnNm
	 * @param birth
	 * @param gender
	 * @param carrier
	 * @param telNo
	 * @return
	 */
	@PostMapping(value="/common/sendAuthNo")
	@ResponseBody
	public String reqAuthNo(AuthSendReqEntity params) {
		
		String sendResult = "";
		
		log.info("            ::::: [ AUTHENTICATION ] Send for authentication number :::::            ");
		
		Map<String, Object> result = uplusApimService.sendAutnNo(params);
		
		if(result.get("sendRslt") != null) {
			sendResult = result.get("sendRslt").toString();
		} else {
			sendResult = "N";
		}
		
		return sendResult;
		
	}
	
	/**
	 * 인증번호 확인
	 * 
	 * @param authNo
	 * @param birth
	 * @param gender
	 * @param carrier
	 * @param telNo
	 * @return
	 */
	@PostMapping(value="/common/confirmAuthNo")
	@ResponseBody
	public String confirmAuthNo(String authNum) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		
		log.info("            ::::: [ AUTHENTICATION ] Check authentication number :::::            ");
		log.debug("            ::::: [ AUTHENTICATION ] Authentication number value [{}] :::::            ", authNum);

		if(entity.getAuthNum().equals(authNum)) {
			return ResultStrConstants.SUCCESS;
		} else {
			return ResultStrConstants.FAIL;
		}

	}
	
	/**
	 * 실명확인 계좌인증, 카드인증
	 * 
	 * @param params
	 * @return
	 */
	@PostMapping(value="/common/verifyPayMthd")
	@ResponseBody
	public Map<String, Object> verifyPayMthd(VerifyAcctReqEntity params) {
		
		log.info("            ::::: [ COMMON ] Verify Pay Method Infomation :::::            ");
		
		Map<String, Object> result = uplusApimService.verifyPayMthd(params);
		
		return result;
	}
	
	/**
	 * API 통신 오류
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/apiError")
	public String serverError(HttpServletRequest request) {
		
		log.debug("API Error session Id : [{}], remoteAddr:[{}]", request.getSession().getId(), request.getRemoteAddr());
		
		return "contents/common/systemError";
	}
	
	/**
	 * 서비스 종료 페이지 (브라우저가 열려 있는 경우 언제든 눈콜 서비스 제공 가능)
	 * @param String
	 * @return String
	 */
	@GetMapping(value = "/common/svcEnd")
	public String standby(ModelMap model) {
		
		return "contents/common/endPage";
		
	}
}
