
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
	}
}	

