
const validate = {
	//email, fax(phone), cellPhone
	checkInputType : (type, value) => {
		
		let result = false;
		let regex;
		
		if(type === "email"){
			regex = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
		}else if(type === "fax"){
			regex = /^(070|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$/;
		}else if(type === "cell"){
			regex = /^(01[016789]{1})-?[0-9]{3,4}-?[0-9]{4}$/;
		}else if(type === "ssn"){
			regex = /\d{2}([0]\d|[1][0-2])([0][1-9]|[1-2]\d|[3][0-1])[1-4]\d{6}$/;
		}else if(type === "birth"){
			regex = /([0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1]))/g;
		}
		
		result = regex.test(value);
		return result;
	},
	//credit card
	checkCardLuhn : (cardNumber) => {
		
		if (!cardNumber.length) return;

		let cardNumberArray = Array.from(cardNumber);

		const lastNumber = Number(cardNumberArray.pop());

		cardNumberArray.reverse();
		cardNumberArray = cardNumberArray.map((num, idx) => idx % 2 === 0 ? Number(num) * 2 : Number(num));
		cardNumberArray = cardNumberArray.map((num) => num > 9 ? num - 9 : num);

		let sum = cardNumberArray.reduce((acc, curr) => acc + curr, 0);

		sum += lastNumber;

		const result = sum % 10;

		return !result;
	},
	//value 빈값 체크
	isEmpty : (value) => {
		if(value == null || value == "" || typeof value == "undefined"){
			return true;
		}else {
			return false;
		}
	},
	/**
	 * input text size 체크
	 * isSpchk : 특수문자 입력 가능 여부
	 */
	inputSizeChk : (obj, size, isSpChk) => {

	let byteCnt = 0;
	let temp;
	let e = window.event;
	const pattern = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\(\=]/gi;

		for(var i=0;i<obj.value.length;i++) {
			temp = obj.value.charAt(i);
			
			escape(temp).length > 4 ? byteCnt += 2 : byteCnt += 1;
			//사이즈 체크
			if(byteCnt > size){
				if(e.keyCode != 8){
					let text = obj.value;
					text = text.substring(0, i);
					obj.value = text;
					e.preventDefault();
		
					return false;
				}
			}
			//특수문자 체크
			if(isSpChk && pattern.test(temp)){
				if(e.keyCode != 8){
					let text = obj.value;
					text = text.substring(0, i);
					obj.value = text;
					e.preventDefault();
	
					return false;
				}	
			}
		} 
	},

	/*
	 * input text 영문,숫자만 입력 가능하도록 체크
	 * 
	 */
	inputTypeCheck : (obj) => {
		const regExp = /[^0-9a-zA-Z]/g;
		if(regExp.test(obj.value)){
			obj.value = obj.value.replace(regExp, '');
		}
	},
	
	/**
	 * input number 숫자만 입력 가능하도록 체크
	 */
	inputNumCheck : (obj) => {
		const regExp = /[^0-9]/g;
		if(regExp.test(obj.value)){
			obj.value = obj.value.replace(regExp, '');
		}
	},
	
	/**
	 * input text 문자만 입력 가능하도록 체크
	 */
	inputTextCheck : (obj) => {
		const regExp = /[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\(\=]/gi;
		if(regExp.test(obj.value)){
			obj.value = obj.value.replace(regExp, '');
		}
	},
	
	/**
	 * input card number 숫자만 입력 가능 및 '-' 추가
	 */
	inputCardNoCheck : (obj) => {
		
		const regExp = /[^0-9]/g;
		if(regExp.test(obj.value)){
			obj.value = obj.value.replace(regExp, '');
		}
		
		let cardNo = obj.value;
		if(cardNo.length <= 4) {
			
		} else if(cardNo.length > 4 && cardNo.length < 9) {
			obj.value = cardNo.substring(0,4)+ "-" + cardNo.substring(4,cardNo.length);
		} else if(cardNo.length < 13) {
			obj.value = cardNo.substring(0,4) + "-" + cardNo.substring(4,8) + "-" + cardNo.substring(8,cardNo.length);
		} else if(cardNo.length < 17) {
			obj.value = cardNo.substring(0,4) + "-" + cardNo.substring(4,8) + "-" + cardNo.substring(8,12) + "-" + cardNo.substring(12, cardNo.length);
		}
		
	},
	
	/**
	 * 금액 천단위 ',' 추가
	 */
	moneyFormatCheck : (obj) => {
		validate.inputNumCheck(obj);
		const regExp = /\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g;
		obj.value = obj.value.replace(regExp, ",");
	},

	/**
	 * 내/외국인, 출생년도에 따라 남/여 '생년월일+성별' 셋팅
	 */
	genderTypeCheck : (birth, gender, type) => {

		if(birth.substring(0,4) > 2000 && gender == "male" && type == "11") {				// 2000~2099년생 내국인 남성
			birth = birth.substring(2) + "3";
		} else if(birth.substring(0,4) > 2000 && gender == "female" && type == "11") {		// 2000~2099년생 내국인 여성
			birth = birth.substring(2) + "4";
		} else if(birth.substring(0,4) > 2000 && gender == "male" && type == "12") {		// 2000~2099년생 외국인 남성
			birth = birth.substring(2) + "7";	
		} else if(birth.substring(0,4) > 2000 && gender == "female" && type == "12") {		// 2000~2099년생 외국인 여성
			birth = birth.substring(2) + "8";
		} else if(birth.substring(0,4) > 1900 && gender == "male" && type == "11") {		// 1900~1999년생 내국인 남성
			birth = birth.substring(2) + "1";
		} else if(birth.substring(0,4) > 1900 && gender == "female" && type == "11") {		// 1900~1999년생 내국인 여성
			birth = birth.substring(2) + "2";
		} else if(birth.substring(0,4) > 1900 && gender == "male" && type == "12") {		// 1900~1999년생 외국인 남성
			birth = birth.substring(2) + "5";
		} else if(birth.substring(0,4) > 1900 && gender == "female" && type == "12") {		// 1900~1999년생 외국인 여성
			birth = birth.substring(2) + "6";
		}

		return birth;
	}
}	

