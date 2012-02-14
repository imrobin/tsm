function checkMobileNo(value, colname) {
	var reg = /^0{0,1}(1)[0-9]{10}$/;
	if(reg.test(value)) {
		return [true,""];
	} else {
		return [false,colname + "：请输入正确的手机号码"];
	}
}

function checkUrl(value, colname) {
	//相对路径
	var reg = /^\/([a-z0-9\*\-\._\?\,\'\/\\\+&amp;%\$#\=~])*$/i;
	if(reg.test(value)) {
		return [true,""];
	} else {
		return [false,colname + "：请输入正确的URL地址"];
	}
}

function checkEnUpcase(value, colname) {
	var reg =  /^[A-Z_]+$/;
	if(reg.test(value)) {
		return [true,""];
	} else {
		return [false,colname + "：请输入正确的大写英文字母或下划线"];
	}
}

function checkNodigit(value, colname) {
	var reg =  /^\d+$/;
	if(reg.test(value)) {
		return [true,""];
	} else {
		return [false,colname + "：请输入正确正整数"];
	}
}


function checkEn_Cn(value, colname) {
	var reg =  /^[a-zA-Z\u4E00-\u9FA5]*$/;
	if(reg.test(value)) {
		return [true,""];
	} else {
		return [false,colname + "：请输入正确的英文字母或中文汉字"];
	}
}

function checkMaxLength(value, colname, colModel) {
	var maxLength = colModel.editoptions.maxlength;
	var length = value.replace(/[^\x00-\xff]/g,"00").length;
	if (maxLength != null) {
		if (maxLength < length) {
			return [false,colname + "输入的字符最大长度为" + maxLength + "(一个汉字占两个字符)"];
		} else {
			return [true,""];
		}
	}
}