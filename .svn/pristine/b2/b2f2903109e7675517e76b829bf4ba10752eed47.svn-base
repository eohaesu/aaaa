package com.deotis.digitalars.controller.operation;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.deotis.digitalars.model.changePayment.WithdrawInfo;
import com.deotis.digitalars.model.common.BankInfo;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.PaymentService;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.service.rest.external.ExternalSampleService;
import com.deotis.digitalars.service.rest.external.UPlusApimService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.util.collection.DMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/test")
@Controller
public class TestController {
	
	@Value("${digitalars.default.site}")
	private String DEFAULT_SITE_CODE;
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	@Value("${system.test.ivr}")
	private boolean SYSTEM_TEST_IVR;

	@Value("${jasypt.encryptor.password}")
    private String JASYPT_KEY;

	private final ExternalSampleService externalSampleService;
	private final RedisTemplateService redisTemplateService;
	private final UPlusApimService uplusApimService;
	private final PaymentService paymentService;

	/*
	@Autowired
	private StringRedisTemplate redisTemplate;
	*/
	
	@GetMapping(value = "/dataTestPage")
	public String dataTestPage(ModelMap model) {
		
		String str = "{\"svcId\":\"recp_01\",\"custNo\":\"2027777153\",\"titleName\":\"test\",\"entrNo\":\"500091770474\",\"custrnmBday\":\"811124\"}";

		WeakHashMap<String, String> control = new WeakHashMap<String, String>();
		control.put("parma", str);
		
		WeakHashMap<String, Object> result = new WeakHashMap<String, Object>();
		result.put("control", control);
		
		model.addAttribute("data", result);
		
		return "contents/test/apiTestPage";

	}
	
	/**
	 * Local Test
	 * @param model
	 * @return String
	 */
	@GetMapping(value = "/localTestPage")
	public String localTestPage(ModelMap model) {
		
		if(SYSTEM_TEST_MODE) {
			log.debug("Enter local test page");
			
			model.addAttribute("DEFAULT_SITE_CODE", DEFAULT_SITE_CODE);

			return "contents/test/authorizationMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}
	
	/**
	 * Local Api Test Page
	 * @return
	 */
	@GetMapping(value = "/apiTest")
	public String localApiTestPage() {
		if(SYSTEM_TEST_MODE) {
			return "contents/test/apiTestPage";
		} else {
			return "redirect:/auth/accessDenied";
		}
	}
	

	
	/**
	 * IF-API-028502 인증번호요청
	 * @param model
	 * @return
	 */
	/*	@GetMapping(value = "/reqAutnNo")
	@ResponseBody
	public Map<String, Object> reqAutnNo(Model model) {
		if(SYSTEM_TEST_MODE) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("custNm", Base64Utils.encodeToString("염다학".getBytes()));           	// 고객명
			params.put("custFposRnno", Base64Utils.encodeToString("8111241".getBytes()));  	// 생년월일+성별
			params.put("custBday", Base64Utils.encodeToString("8111241".getBytes()));      	// 생년월일+성별  
			params.put("prodNo", Base64Utils.encodeToString("010062618456".getBytes()));    // 상품번호 (전화번호)
			params.put("mblcDivsCd", "3");													// 이동통신구분코드
			params.put("custBasKdCd", "10");
			params.put("prssCd", "1");    
			
			Map<String, Object> response = uplusApimService.reqAutnNo(params);

			return response;
		} else {
			return null;
		}
	}*/
	
	/**
	 * IF-API-026805 가입계약
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/custCntcInfo")
	@ResponseBody
	public Map<String, Object> custCntcInfo(Model model) {
		if(SYSTEM_TEST_MODE) {
			Map<String, Object> response = uplusApimService.getCustContInfo();
	
			return response;
		} else {
			return null;
		}
		
	}
	
	/**
	 * IF-API-001201 연체및기타정보조회
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/billChrg")
	@ResponseBody
	public Map<String, Object> monthlyChrg(Model model) {
		/*if(SYSTEM_TEST_MODE) {
			Map<String, Object> response = paymentService.getArerEtcInfo();
	
			return response;
		} else {
			return null;
		}*/
		return null;
	}

	/**
	 * IF-API-001112 은행카드사목록조회
	 * @return
	 */
	@GetMapping(value = "/getBankList")
	@ResponseBody
	public ArrayList<BankInfo> getBankList() {
		/*if(SYSTEM_TEST_MODE) {
//			ArrayList<BankInfo> response = changePaymentService.getBankList("B");
	
//			return response;
		} else {
			return null;
		}*/
		return null;
	}
	
	/**
	 * IF-API-031707 정보조회
	 * @return
	 */
	@GetMapping(value = "/getPayMethod")
	@ResponseBody
	public  Map<String, Object> getPayMethod() {
		/*if(SYSTEM_TEST_MODE) {
//			Map<String, Object> response = changePaymentService.getPayMethod();
	
//			return response;
		} else {
			return null;
		}*/
		return null;
	}
	
	/**
	 * IF-API-047801 자동이체 최초출금일 조회
	 * @return
	 */
	@GetMapping(value = "/getWithdrawDate")
	@ResponseBody
	public WithdrawInfo getWithdrawDate() {
		/*if(SYSTEM_TEST_MODE) {
			WithdrawInfo response = changePaymentService.getWithdrawDate();
	
			return response;
		} else {
			return null;
		}*/
		return null;
	}
	
	/**
	 * APIM Auth Token Test
	 */
	@GetMapping(value = "/apimTokenPvTest")
	@ResponseBody
	public String apimTokenPvTest() {
		if(SYSTEM_TEST_MODE) {
			log.debug("APIM Auth Token Pv Create");
			
			String result = uplusApimService.createAuthToken("pv");
			
			if("success".equals(result)) {
				return "contents/test/div/index";
			} else {
				return "contents/common/serverError";
			}
		} else {
			return "redirect:/auth/accessDenied";
		}
	}
	
	/**
	 * APIM Auth Token Test
	 */
	@GetMapping(value = "/apimTokenPbTest")
	@ResponseBody
	public String apimTokenPbTest() {

		if(SYSTEM_TEST_MODE) {
			log.debug("APIM Auth Token Pb Create");
			
			String result = uplusApimService.createAuthToken("pb");
			
			if("success".equals(result)) {
				return "contents/test/div/index";
			} else {
				return "contents/common/serverError";
			}
		} else {
			return "redirect:/auth/accessDenied";
		}
	}
	
	/**
	 * RedisTemplate test
	 */
	@GetMapping(value = "/redisGn")
	@ResponseBody
	public void redisTest() {
		if(SYSTEM_TEST_MODE) {

			for(int i= 0; i<5; i++) {
				String key = "index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:"+UUID.randomUUID();
				redisTemplateService.addKeyOptValue(key, "", 60);
			}
			
		}
	}
	
	/**
	 * RedisTemplate test
	 * @param model
	 */
	@GetMapping(value = "/redisClean")
	@ResponseBody
	public Integer redisClean() {
		
		int deleteCount = 0;
		
		if(SYSTEM_TEST_MODE) {

			deleteCount = redisTemplateService.cleanKeysWithScan();

		}
		return deleteCount;
	}

	
	/**
	 * IVR CONTROL REST TEST
	 * @param model
	 */
	@GetMapping(value = "/ivrControlMock")
	public String ivrControlMock(ModelMap model, HttpSession session) {
		if(SYSTEM_TEST_MODE) {

			return "contents/test/ivrControlMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}
	
	/**
	 * ivrTestMock
	 * @param model
	 */
	@GetMapping(value = "/ivrTestMock")
	public String ivrTestMock(ModelMap model, HttpSession session) {
		if(SYSTEM_TEST_IVR && session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
			SecretEntity entity = SessionHandler.getSecretEntity();
			
			model.addAttribute("userData", entity.getUserData());
			model.addAttribute("appBindDetails", entity.getAppBindDetails());
			
			return "contents/test/ivrTestMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}
	
	/**
	 * callend Test page
	 * @param model
	 * @param request
	 * @return String
	 */
	@GetMapping(value = "/callendTestPage")
	public String sessiontest(ModelMap model, HttpServletRequest request) {
	
		if(SYSTEM_TEST_MODE) {
			log.debug("Enter local callend test page");

			return "contents/test/callendMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}
	
	/**
	 * application enc encode/decode generate
	 * @param model
	 * @param request
	 * @return String
	 */
	@GetMapping(value = "/encGenerate")
	public void encGenerate(HttpServletResponse response, 
			@RequestParam(value = "type", required = true, defaultValue = "enc") String type,
			@RequestParam(value = "key", required = true, defaultValue = "") String key
			) {
		
		response.setContentType("text/html");

		PrintWriter out = null;
		
		if(SYSTEM_TEST_MODE) {
			try {

		        String result = "enc".equals(type) ? jasyptEncrypt(key) : jasyptDecryt(key);
		        
				out = response.getWriter();
	
				out.println("<html><body>");
	
				out.println("<h2>" + result + "</h2>");
				out.println("<hr>");
				out.println("TEST Time on the server is: " + new java.util.Date());
	
				out.println("</body></html>");

			} catch (IOException e) {
				log.error("Fail to EncGenerateTest. message:{}", e.getMessage());
			} finally {
				if(out != null){
					out.flush();
				}
			}
		}
	}
	
	/**
	 * application ssn generate
	 * @param model
	 * @param request
	 * @return String
	 */
	@GetMapping(value = "/ssnGenerate")
	public void ssnGenerate(HttpServletResponse response) {
		
		response.setContentType("text/html");

		PrintWriter out = null;
		
		if(SYSTEM_TEST_MODE) {
			
			String result = "";
			
			try {
				
				Random rd = SecureRandom.getInstanceStrong();
				
				Calendar cal = Calendar.getInstance();
				
				cal.setTimeInMillis(rd.nextLong());
				
				String s1 = new SimpleDateFormat("yyMMdd").format(cal.getTime());
				String s2 = null;
				
				while( s2 == null || s2.length() < 6) {
					s2 = Integer.toString(rd.nextInt(299999));
				}
				int sum = 0;
				for (int i = 0; i< s1.length(); i++) {
					sum += Integer.parseInt(String.valueOf(s1.charAt(i))) * (i + 2);
					int j = i < 2 ? i+8 : i;
					sum += Integer.parseInt(String.valueOf(s1.charAt(i))) * j;
				}
				
				int bit = 11 - (sum % 11);
				
				result = s1+"-"+s2+(bit == 10 ? 0 : bit);

				out = response.getWriter();
	
				out.println("<html><body>");
	
				out.println("<h2>" + result + "</h2>");
				out.println("<hr>");
				out.println("TEST Time on the server is: " + new java.util.Date());
	
				out.println("</body></html>");

			} catch (IOException | NoSuchAlgorithmException e) {
				log.error("Fail to EncGenerateTest. message:{}", e.getMessage());
			} finally {
				if(out != null){
					out.flush();
				}
			}
		}
	}
	
	private String jasyptEncrypt(String input) {
       
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(JASYPT_KEY);
        return encryptor.encrypt(input);
    }

    private String jasyptDecryt(String input){

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(JASYPT_KEY);
        return encryptor.decrypt(input);
    }
	
	
	/**
	 * RestTemplate test
	 * 
	 * @return String
	 */
	@PostMapping(value = "/ajax/testRestTemplate")
	@ResponseBody
	public String testRestTemplate() {
		
		if(SYSTEM_TEST_MODE) {
			Map<String, Object> result = externalSampleService.getRestTemplateSample();

			return "{ \"result\" : \""+result+"\" }";
		}else {
			return null;
		}
	}
	
	/**
	 * ReactiveClient test
	 * 
	 * @return String
	 */
	@PostMapping(value = "/ajax/testReactiveClient")
	@ResponseBody
	public String testReactiveClient() {
		
		if(SYSTEM_TEST_MODE) {
			DMap<String, Object> result = externalSampleService.getReactiveClientSample();

			return "{ \"result\" : \""+result+"\" }";
		}else {
			return null;
		}
	}
	
	/**
	 * SSE Emitter longPolling test
	 * 
	 * @return ResponseEntity<SseEmitter>
	 */
	@PostMapping(value="/sse/emitter", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> handleRequest () {

        final SseEmitter emitter = new SseEmitter((long)(1000*60*5));
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                	log.debug("SEND:::::::"+i);
                    emitter.send(i + " - ", MediaType.TEXT_PLAIN);

                    Thread.sleep(500);
                    
                } catch (IOException | NullPointerException | InterruptedException e) {
                    emitter.completeWithError(e);
                    return;
                }
            }
            emitter.complete();
        });
        return new ResponseEntity<SseEmitter>(emitter, HttpStatus.OK);
    }
	
	/**
	 * DeffredResult longPolling test
	 * 
	 * @return DeferredResult<ResponseEntity<?>>
	 */
	@GetMapping("/deffredTest")
	public DeferredResult<ResponseEntity<?>> handleReqDefResult(HttpServletRequest request, @RequestParam(value = "btoken", required = false) String btoken) {
	    log.info("Received async-deferredresult request");
	    DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
	    
	    ForkJoinPool.commonPool().submit(() -> {
	    	log.info("Processing in separate thread");
	        try {
	        	TimeUnit.MILLISECONDS.sleep(500);
	        } catch (InterruptedException e) {
	        	log.info("exception", e.getMessage());
	        }
	        output.setResult(ResponseEntity.ok("ok"));
	    });
	    
	    log.info("servlet thread freed");
	    return output;
	}

	@GetMapping("/templateIndex")
	public String div() {
		return "contents/test/div/index";
	}

	public void templateData(String type, ModelMap model) {
		switch (type) {
			case "1":
				model.addAttribute("top", "custom");
				model.addAttribute("middle", "main");
				model.addAttribute("bottom", "banner");
				break;
			case "2":
				model.addAttribute("top", "custom");
				model.addAttribute("middle", "banner");
				model.addAttribute("bottom", "main");
				break;
			case "3":
				model.addAttribute("top", "main");
				model.addAttribute("middle", "custom");
				model.addAttribute("bottom", "banner");
				break;
			case "4":
				model.addAttribute("top", "main");
				model.addAttribute("middle", "banner");
				model.addAttribute("bottom", "custom");
				break;
			case "5":
				model.addAttribute("top", "banner");
				model.addAttribute("middle", "custom");
				model.addAttribute("bottom", "main");
				break;
			case "6":
				model.addAttribute("top", "banner");
				model.addAttribute("middle", "main");
				model.addAttribute("bottom", "custom");
				break;
			default:
				model.addAttribute("top", "custom");
				model.addAttribute("middle", "main");
				model.addAttribute("bottom", "banner");
				break;
		}
		model.addAttribute("banner", "배너");
		model.addAttribute("main", "메뉴");
		model.addAttribute("custom", "추천 메뉴");
	}

	@GetMapping("/template1")
	public String template1(ModelMap model,
			@RequestParam(value = "type") String type) {
		// String template1 = "{\"template\":\"1\",\"layout\":{\"top\":\"banner\",\"middle\":\"main\",\"bottom\":\"custom\"}}";
		templateData(type, model);
		model.addAttribute("type", type);
		model.addAttribute("gradation", "N");
		model.addAttribute("colorStart", "#e3ffdd");
		model.addAttribute("colorEnd", "#e3ffdd");
		return "contents/test/div/template1";
	}

	@GetMapping("/template2")
	public String template2(ModelMap model,
			@RequestParam(value = "type") String type) {
		templateData(type, model);
		model.addAttribute("type", type);
		model.addAttribute("gradation", "Y");
		model.addAttribute("colorStart", "#ddf6ff");
		model.addAttribute("colorEnd", "#FFFFFF");
		return "contents/test/div/template2";
	}

	@GetMapping("/template3")
	public String template3(ModelMap model,
			@RequestParam(value = "type") String type) {
		templateData(type, model);
		model.addAttribute("type", type);
		model.addAttribute("gradation", "Y");
		model.addAttribute("colorStart", "#FFFFFF");
		model.addAttribute("colorEnd", "#fbddff");
		return "contents/test/div/template3";
	}

}