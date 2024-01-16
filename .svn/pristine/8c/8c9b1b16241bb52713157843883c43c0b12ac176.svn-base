
const common = {
	// 서비스 구분 코드
	svcType : {
		LZP0000001 : "모바일",
		LZP0000107 : "IOT",
		LZP0000301 : "인터넷전화",
		LZP0000601 : "인터넷",
		LZP0000701 : "TV"
   	},
   	
	setSessionEnd : () => {
		polling.stop();	
		return new Promise((resolve, reject) => {		
			$.get(contextPath + "/auth/sessionEnd", (xhr, status) => {
				status === "success" || xhr.status === 200 ? resolve(true) : reject(false);
			});			
		});	
	},
	
	blockHistoryBack : () => {
		window.history.pushState(null, document.title, location.href);
		window.onpopstate = () => { history.go(0); };
	},

	base64UrlEncode : (p) =>{
		const ENC = {'+':'-','/':'_'};
		return btoa(p).replace(/[+/]/g, (m) => ENC[m]);
	},

	base64UrlDecode : (p) =>{
		const DEC = {'-':'+','_':'/'};
		return atob(p).replace(/[-_]/g, (m) => DEC[m]);
	},
	
	chkIsJSON : (str) =>{
		try{
			return (JSON.parse(str) && !!str);
		}catch (e){
			return false;
		}
	},
	
	blockRefresh : () => {
		window.addEventListener('beforeunload', (event) => {
			event.preventDefault();
			event.returnValue = '';
		});
	},
	
	removeBlockRefresh : () => {
		window.removeEventListener('beforeunload');
	},
	
	replaceAt : (value, index, replacement) => {
		if(index >= value.length){
			return value.valueOf();
		}
		return value.substring(0, index) + replacement + value.substring(index + 1);
	},
	
	isAndroid : () => {
	 	return navigator.userAgent.match(/android/i) ? true : false;
	}
}	

