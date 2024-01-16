
const call = {

	/**
	 *	상담원 연결 시간 체크
	 */
	counselorCheckTime : (csst, csed) => {
		return new Promise((resolve) => {
			$.post(contextPath + "/common/ajax/checkWorkTime", {cs_time_st : csst, cs_time_ed : csed}, (result) => {
				resolve(result);
			});
		});
	},
	
	/**
	 *	상담사 연결
	 *  
	 */
	connectCounselor : (counselorCode, call_end) => {
		
		$.post(contextPath+"/api/wms/external/callTransfer", {counselorCode : counselorCode}, (result) =>{
			if(result && call_end){
				location.replace(contextPath+"/common/sessionEndWithParam?link=true");
			}else {
				alert("상담사 연결을 실패하였습니다. 잠시 후 다시 시도해 주시기 바랍니다.");
				location.replace(contextPath+"/main");
			}
		}).fail(() => {
			alert("상담사 연결을 실패하였습니다. 잠시 후 다시 시도해 주시기 바랍니다.");
			location.replace(contextPath+"/main");
		});

	},
	/**
	 *	음성 플레이
	 *  
	 */
	voicePlay : (isVoiceToggle, voiceData) => {
		let voiceArr = [];
		let voiceObject = new Object;
		
		if (!voiceData) {

			let type 	 = $(".TYPE"),
				src 	 = $(".SRC"),
				ttsClass = $(".TTSCLASS");
				
			
			$(type).each(function(index){
				voiceArr.push({
					"TYPE"  	: type[index].value,
					"SRC"		: src[index].value,
					"TTSCLASS"	: ttsClass[index].value
				});
			});
			
		}else if(typeof voiceData === "string") {

			const voiceTemp = voiceData.split(',');

			if(voiceTemp.length > 1){
				for(let value of voiceTemp){
					voiceArr.push({
						"TYPE": "FILE",
						"SRC": value,
						"TTSCLASS": "" 
					});
				}
			}else {
				voiceArr.push({
					"TYPE": "FILE",
					"SRC": voiceData,
					"TTSCLASS": "" 
				});
			}
		}else {
			
			voiceArr = voiceData;
		}
		
		voiceObject.AUDIOSET = voiceArr;
		
		// 페이지마다 단순 호출일때 재생할 음성이 없으면 return
		if (!isVoiceToggle && voiceArr.length < 1) {
			return;
		}
		
		$.post(contextPath+"/api/wms/external/playStart", {tocData : JSON.stringify(voiceObject), isVoiceToggle : isVoiceToggle}, function(data){
			if(data && isVoiceToggle){
				/* 음성 플레이 이후 UI컨트롤
				$('').show();
				$('').hide();
				*/
			}
		}).fail((data, textStatus, xhr) => {
			//음성 재생 실패 시 추가 처리
		}).done( () => {
			/*
			if(isVoiceToggle){
				// 음소거|음재생 버튼 활성화(한번만 클릭되도록 처리)
				$('').find("button").prop("disabled", false);
			}
			*/
		});

	
	},
	// isVoiceToggle : true  :: 음소거 버튼클릭
	//			     : false :: 페이지 load시 voice stop(기존 음성 stop)
	voiceStop : (isVoiceToggle, voiceData) => {
		
		call.voicePlay(false, voiceData);
		
		/* voice play 전 stop 필요 시 주석해제
		$.post(contextPath+"/api/wms/external/playStop", {isVoiceToggle : isVoiceToggle}, function(data){
			if(data && isVoiceToggle){

			}
		}).fail(function(data, textStatus, xhr){
			//음성 중단 실패 시 추가 처리
		}).done(function(){
			call.voicePlay(false, voiceData);
			//isVoiceToggle ? $('').find("button").prop("disabled", false) : call.voicePlay(false, voiceData);
		});
		*/

	},
	
	unbind : () => {
		return new Promise((resolve, reject) => {
			$.get(contextPath + "/api/wms/external/callEnd", (result) => {
				result ? resolve(result) : reject();
			});
		});
	},

	connectCounselorWaitTime : (qdn) => {
		return new Promise((resolve, reject) => {
			$.post(contextPath + "/api/wms/external/connectCounselorWaitTime", {QDN : qdn}, (result) => {
				result ? resolve(result) : reject();
			});
		});
	},
	
	goToEnd : (message) => {
		return new Promise((resolve, reject) => {
			$.post(contextPath + "/api/wms/external/goToEnd", {message : message}, (result) => {
				result ? resolve(result) : reject();
			});
		});
	},
	
	sendVariable : (message) => {
		return new Promise((resolve, reject) => {
			$.post(contextPath + "/api/wms/external/sendVariable", {message : message, type : "2"}, (result) => {
				result ? resolve(result) : reject();
			});
		});
	},
	
	userDataSave : (userData, appBindDetails, timeoutSec) => {
		return new Promise((resolve, reject) => {
			$.post(contextPath + "/api/wms/external/userDataSave", {userData : userData, appBindDetails : appBindDetails, timeoutSec : timeoutSec}, (result) => {
				result ? resolve(result) : reject();
			});
		});
	},
	
	setProgress : (tp) => {
		return new Promise((resolve, reject) => {
			$.post(contextPath + "/common/set/progress", {tp : tp}, (result) => {
				result ? resolve(result) : reject();
			});
		});
	}

};