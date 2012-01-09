function chckMaxLength(el) {// 中文按两个字符算
	var maxLength = el.get('maxlength');
	var length = el.value.replace(/[^\x00-\xff]/g,"00").length;
	if ($chk(maxLength)) {
		if (maxLength < length) {
			el.errors.push("输入的字符最大长度为" + maxLength + "(一个汉字占两个字符)");
			return false;
		} else {
			return true;
		}
	}
}

function checkKeyVersion(el) {// 中文按两个字符算
	var reStr  = /^[0-9]|[1-3][0-9]|4[0-7]$/;
	var flag = reStr.test(el.value);
	if(flag){
		if(el.value < 1 || el.value > 47){
			el.errors.push("您必须输入1-47的密钥版本号.");
			return false;	
		}
		return true;
	}else{
		el.errors.push("您必须输入1-47的密钥版本号.");
		return false;	
	}
}

function chkMaxLengthWordAndNumber(el){// 判断英文与数字长度限制
	var maxLength = el.get('maxlength');
	var value = el.value;
	eval("var reStr = /^[0-9a-zA-Z]{1," + maxLength + "}$/;");
	var flag = reStr.test(value);
	if(flag){
		return true;
	}else{
		el.errors.push("输入的字符(英文和数字)最大长度为" + maxLength);
		return false;	
	}
}

function chkOx(el){// 判断英文与数字长度限制
	var maxLength = el.get('maxlength');
	var value = el.value;
	if(value.length != maxLength){
		el.errors.push("请输入"+ maxLength +"位长度数据");
		return false;	
	}
	var reStr  = /^\d{19}$/;
	var flag = reStr.test(el.value);
	if(flag){
		return true;
	}else{
		el.errors.push("请输入符合规范的SEID");
		return false;	
	}
}


function chkNumber(el){// 正整数
	var reStr  = /^\d+$/;
	var flag = reStr.test(el.value);
	if(flag){
		return true;
	}else{
		el.errors.push("您必须输入大于等于0的整数");
		return false;	
	}
}


function chkCardNo(el){// 检查20位卡号
	var reStr  = /^\d{1,20}$/;
	var flag = reStr.test(el.value);
	if(flag){
		return true;
	}else{
		el.errors.push("您必须输入少于或等于20位数字卡号.");
		return false;	
	}
}

function chkMaxLengthWordAndNumberAndDot(el){// 判断英文与数字包括.任一支付长度限制
	var maxLength = el.get('maxlength')
	var value = el.value;
	eval("var reStr = /^[0-9a-zA-Z\.]{1," + maxLength + "}$/;");
	var flag = reStr.test(value);
	if(flag){
		return true;
	}else{
		el.errors.push("输入的字符(英文和数字)最大长度为" + maxLength);
		return false;	
	}
}

/**
 * AID重复性验证
 * 
 * @param el
 * @param url
 *            验证AID重复性的url
 * @returns true-AID可用，false-AID重复
 */
function checkAid(el, url, entity) {
	if(!$chk(entity)){
		entity = '';
	}
	if(!$chk(el.orgVaule)){
		el.orgVaule='';
	}
	var bln = false;
	new Request.JSON({
		async : false,
		url : url,
		data : {
			newAid : el.value,
			orgAid : el.orgVaule,
			aid : el.value
		},
		onSuccess : function(data) {
			if(data.success){
				bln = true;
			}else{
				el.errors.push(data.message);
			}
		}
	}).post();
	return bln;
}


function checkLoadFileAid(el) {
	return checkAid(el, ctx + '/html/loadFile/?m=validateAid');
}

function checkLoadModuleAid(el){
	return checkAid(el, ctx+'/html/loadModule/?m=checkAid')
}

function checkHex(el) {
	if(el.value.test(/[^0-9a-fA-F]/)) {
		el.errors.push('必须是十六进制');
		return false;
	} else if(el.value.length % 2 != 0) {
		el.errors.push('必须是偶数');
		return false;
	} else {
		return true;
	}
}

function checkVersionNo(el){
	if(!el.value.test(/^[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{1,2}$/)) {
		el.errors.push('版本号格式错误');
		return false;
	}else{
		return true;
	}
}

function checkCap(el){
	if(!el.value.toLocaleLowerCase().test(/^.*\.cap$/)) {
		el.errors.push('加载文件的扩展名必须是.cap');
		return false;
	}else{
		return true;
	}
}
function checkFloat(el){
	input  = el.value;
	pos = input.indexOf(".");
	if(pos!=-1){
	           strDec = input.substr(pos+1, input.length );  
	            if(strDec.length > 2){   
	               el.errors.push("小数位不能超过2位");   
	               return false;   
	            }else{
	            	return true;
	            }  
	}else{
		return true;
	}
}