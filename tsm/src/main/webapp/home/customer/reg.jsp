<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>
<script type="text/javascript" src="${ctx}/lib/commons/CityPicker.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var hasClick= false;
	window.addEvent('domready', function() {
		var cal = new Customer.Cal();
		cal.setProvince();
		var validater = new FormCheck('regForm', {
			submit:false,
			trimValue:false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure:1
				
			},
			onValidateSuccess : function() {
				/**/
				//cal.regWaiting();
				if (!hasClick){
					hasClick = false;
					var box = new LightFace.MessageBox();
					box.loading('注册中，请稍候');
					 new Request.JSON({
						url : $('regForm').get('action'),
						onSuccess : function(response) {
							var object = response;
							if(object.success) {
								//clearInterval();
								message = object.message;
								self.location = ctx + '/home/customer/regResult.jsp?email=' + message.email + '&mobile=' + message.mobile;
							} else {
								new LightFace.MessageBox().error(object.message);
								cal.refreshImage();
							}
							box.close();
						},
						onError : function(response) {
							box.close();
						}
					}).post($('regForm').toQueryString()); 
				}
			}
		});
		$('captchaImage').addEvent('click',function(event) {
					var timestamp = (new Date()).valueOf();
					$('captchaImage').set('src',ctx + '/j_captcha_get?t=' + timestamp);
		});
		$('month').addEvent('change', function(event) {
			cal.adjustDay($('month').get('value'));
		});
		$('year').addEvent('change', function(event) {
			cal.adjustDay($('month').get('value'));
		});
		$('userMobile').addEvent('blur', function(event) {
			var reg=/^1[358]\d{9}$/;
			if(reg.test($('userMobile').get('value').trim())){
			cal.checkMobile(ctx, $('userMobile').get('value'));
			}
		});
            $('userMobile').addEvent('click', function(event) {
            	$('mobile_tip').set('html','<p class="explain left">可作为登录帐户，不允许修改。</p>');
		});
		$('email').addEvent('blur', function(event) {
			var reg=/^([a-zA-Z0-9_\.\-\+%])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			if(reg.test($('email').get('value').trim())){
			cal.checkEmail(ctx, $('email').get('value'));
			}
		});
		$('password').addEvent('keyup', function(event) {
			if($('password').get('value').length>=6){
			cal.passwordStrength(ctx,$('password').get('value'));
			}else{
					$('iPwdMeter').setStyle('background-image','');
					$('iPwdMeter').set('html','');
					$('iPwdBack').setStyle('background-image','');
			}
			$('dns').set('value',location.protocol+"//"+location.host);
		});
		$('password').addEvent('blur', function(event) {
			if($('password').get('value') == $('repassword').get('value') && $('password').get('value')!= ""){
				$('repasswordDiv').set('html','<img src="'+ctx+'/images/regf.png" style="margin-top:6px;" width=20 height=20/>');
			}else{
				$('repasswordDiv').set('html','');
			}
		});
		$('repassword').addEvent('blur', function(event) {
			if($('password').get('value') == $('repassword').get('value') && $('password').get('value')!= ""){
				$('repasswordDiv').set('html','<img src="'+ctx+'/images/regf.png" style="margin-top:6px;" width=20 height=20/>');
			}else{
				$('repasswordDiv').set('html','');
			}
		});
		$('captcha').addEvent('click',function(event) {
			event.stop();
			var timestamp = (new Date()).valueOf();
			$('captchaImage').set('src',ctx + '/j_captcha_get?t=' + timestamp);
        });
		$('spReg').addEvent('click', function() {
			self.location=ctx+"/home/sp/reg.jsp";
		});
	});
	//下拉框
	function addPronvince() {
		var pronvince = $('location');
		var options = pronvince.options;
		options.add(new Option("北京","北京"));
		options.add(new Option("天津","天津"));
		options.add(new Option("河北","河北"));
		options.add(new Option("山西","山西"));
		options.add(new Option("内蒙古","内蒙古"));
		options.add(new Option("辽宁","辽宁"));
		options.add(new Option("吉林","吉林"));
		options.add(new Option("黑龙江","黑龙江"));
		options.add(new Option("上海","上海"));
		options.add(new Option("江苏","江苏"));
		options.add(new Option("浙江","浙江"));
		options.add(new Option("安徽","安徽"));
		options.add(new Option("福建","福建"));
		options.add(new Option("江西","江西"));
		options.add(new Option("山东","山东"));
		options.add(new Option("河南","河南"));
		options.add(new Option("湖北","湖北"));
		options.add(new Option("湖南","湖南"));
		options.add(new Option("广东","广东"));
		options.add(new Option("广西","广西"));
		options.add(new Option("海南","海南"));
		options.add(new Option("重庆","重庆"));
		options.add(new Option("四川","四川"));
		options.add(new Option("云南","云南"));
		options.add(new Option("贵州","贵州"));
		options.add(new Option("西藏","西藏"));
		options.add(new Option("陕西","陕西"));
		options.add(new Option("甘肃","甘肃"));
		options.add(new Option("宁夏","宁夏"));
		options.add(new Option("青海","青海"));
		options.add(new Option("新疆","新疆"));
	}
</script>
</head>
<body>
<div id="container"><%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt用户&gt注册</div>
<div id="main">
<div class="div980">
<div class="reg_t_2">
<ul>
	<li class="t1">普通用户注册</li>
	<li id="spReg" class="t2" style="cursor: pointer;">应用提供商注册</li>
</ul>
</div>
<div class="regcont">
<form id="regForm" name="regForm" action="${ctx}/html/customer/?m=customerReg" method="post">
<dl>
	<dd>
	<p class="regtext"><span style='color: red;'>*</span>手机号码:</p>
	<p class="left inputs"><input class="inputtext validate['required','phone']"
		name="userMobile" id="userMobile" type="text" maxlength="11" /></p>
	<div id="mobile_tip">
	<p class="explain left">可作为登录帐户，不允许修改。</p>
	</div>
	</dd>
	<dd>
	<p class="regtext"><span style='color: red;'>*</span>登录密码:</p>
	<p class="left inputs"><input class="inputtext validate['required','length[6,32]']" name="password"
		maxlength="32" type="password" id="password" /></p>
    <div id="iPwdBack" class="PwdBack">
        <div class="PwdMeter" id="iPwdMeter">&nbsp;</div>
        </div>
	</dd>
	<dd>
	<p class="regtext"><span style='color: red;'>*</span>重复登录密码:</p>
	<p class="left inputs"><input class="inputtext validate['confirm:password']" name="confirm"
		type="password" id="repassword" />
	<div id="repasswordDiv"></div>
	</p>
	</dd>
	<dd>
	<p class="regtext"><span style='color: red;'>*</span>电子邮件地址:</p>
	<p class="left inputs"><input class="inputtext validate['required','email','length_str[6-32]']"
		name="email" id="email" type="text" maxlength="32" /></p>
	<div id="email_tip">
	<p class="explain left">可作为登录账号，不允许修改。建议使用139邮箱，点击<a href="http://mail.10086.cn/register/" target="_blank">注册</a></p>
	</div>
	</dd>
	<dd>
	<p class="regtext"><span style='color:red;'>*</span>所在地:</p>
	<p class="left inputs">
	<select id="location" class="validate['required']" name="location" size="1" style="width: 75px">
	<option value="">选择省市</option>
	</select>	
		<input id="dns" type="hidden" name="dns" value=""></input></p>
	</dd>
	<dd class="aboutnfc">
	<h3></h3>
	</dd>
	<dd>
	<p class="regtext">真实姓名:</p>
	<p class="left inputs"><input class="inputtext validate['%chckMaxLength']"
		name="realName" id="realName" type="text" maxlength="32" /></p>
	</dd>
	<dd>
	<p class="regtext">性别:</p>
	<p class="left inputs"><input class="inputradio" name="sex" type="radio" value="1" />先生 <input class="inputradio"
		name="sex" type="radio" value="0" />女士</p>
	</dd>
	<dd>
	<p class="regtext">生日:</p>
	<p class="left inputs"><select id="year" name="year">
		<script type="text/javascript">
											var yearbegin = 1930, yearend = 2000;
											for ( var i = yearbegin; i <= yearend; i++) {
												document.write("<option value="+i+">"+ i+ "</option>");
											}
											$('year').set('value',1980);
										</script>
	</select>年 <select id="month" name="month" size="1" style="width: 50px">
		<option value="1">1</option>
		<option value="2">2</option>
		<option value="3">3</option>
		<option value="4">4</option>
		<option value="5">5</option>
		<option value="6">6</option>
		<option value="7">7</option>
		<option value="8">8</option>
		<option value="9">9</option>
		<option value="10">10</option>
		<option value="11">11</option>
		<option value="12">12</option>
	</select> 月 <select id="day" name="day" size="1">
		<script type="text/javascript">
											var daybegin = 1, dayend = 31;
											for ( var i = daybegin; i <= dayend; i++) {
												document.write("<option value="+i+">"+ i+ "</option>");
											}
										</script>
	</select> 日</p>
	</dd>
	<dd>
	<p class="regtext"><span style='color: red;'>*</span>验证码:</p>
	<p class="left inputs"><input id="j_captcha_response" class="inputtext validate['required']"
		name="j_captcha_response" type="text" /></p>
	<p><img id="captchaImage" title="看不清楚?点击图片更换" height="28"
		style="vertical-align: middle; border: none; cursor: pointer;" src="${ctx}/j_captcha_get?t=1'" /></p>
	<p><a id="captcha" href="${ctx}/j_captcha_get?t=1">看不清楚?点击更换</a></p>	
	</dd>
	<dd id="dd_20">
	<p class="regtext"></p>
	<p class="left inputs"><label><input class="inputcheckbox validate['required']" type="checkbox" />已阅读并接受 <a
		class="b" href="protocol.jsp" target="_blank">注册条款</a> </label></p>
	</dd>
	<dd class="s" style="width: 400px;"><a id="reg" class="subbutt validate['submit']"><span>创建账户</span></a></dd>
</dl>
</form>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp"%></div>
</body>
</html>