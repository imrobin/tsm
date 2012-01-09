<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/security/js/login.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var login = new User.Login();
		$('captchaImage').addEvent('click', function(event) {
			login.refreshImage();
		});
		$('loginButton').addEvent('click', function(event) {
			//停止原有的事件，类似return false
			new Event(event).stop();
			login.submitForm(false);
		});
		$('j_username').addEvent('focus',function(event){
			$('j_username').addClass("s1ffocus");
		});
        $('j_username').addEvent('blur',function(event){
        	$('j_username').removeClass("s1ffocus");
		});
        $('j_password').addEvent('focus',function(event){
			$('j_password').addClass("s1ffocus");
		});
        $('j_password').addEvent('blur',function(event){
        	$('j_password').removeClass("s1ffocus");
		});
		$('j_captcha_response').addEvent('focus',function(event){
			$('j_captcha_response').addClass("s2ffocus");
		});
        $('j_captcha_response').addEvent('blur',function(event){
        	$('j_captcha_response').removeClass("s2ffocus");
		});
		
	});
	function refresh() {
		var timestamp = (new Date()).valueOf();
		$('captchaImage').set('src', ctx + '/j_captcha_get?t=' + timestamp);
	}
</script>
</head>

<body>
<div id="container">
<div id="header">
<div class="clear">
<div class="left logo"><img src="${ctx}/images/logo.gif" width="252px" height="59px" /></div>
<div class="guild_login">
<img src="${ctx}/images/web.png" width="16px" height="16px" /><a href="${ctx}/index.jsp">首页</a>  
<img src="${ctx}/images/login_icon_16.png" width="16px" height="16px" /><a href="${ctx}/security/login.jsp" style="cursor: pointer;">登录</a>  
<img src="${ctx}/images/reg_icon_16.png" width="16" height="16" /><a href="${ctx}/home/customer/reg.jsp">用户注册</a>
<img src="${ctx}/images/help_icon_16.png" width="16px" height="16px" /><a href="${ctx}/help.jsp">帮助中心</a>
 <img src="${ctx}/images/new_user.png" width="16px" height="16px" /><a href="${ctx}/tutor.jsp">新手须知</a> 
</div>
</div>
</div>
<div id="main">
<div class="login"><div id="loginInfo" class="logininfo">
<form id="loginForm" action="${ctx}/j_spring_security_check" method="post">
<p class="username">用户名<input type="text" class="userninput" id="j_username" name="j_username" /></p>
<p id="userNamePrompt" class="prompt"></p>
<p class="userpass" style="word-spacing:0.7em;">密 码<input class="userninput" id="j_password" name="j_password" type="password" /></p>
<p id="passwordNamePrompt" class="prompt"></p>
<p class="validate">验证码<input id="j_captcha_response" size="12" class="userninput1" name="j_captcha_response" type="text"  maxlength="5" />
<img id="captchaImage" title="看不清楚?点击图片更换" style="vertical-align:middle;border: none;cursor: pointer;" src="${ctx}/j_captcha_get" /></p>
<p class="prompt" style="text-align: right;"><span id="captchaNamePrompt"></span><a href="javaScript:refresh();">看不清楚?点击更换</a>&nbsp;&nbsp;&nbsp;&nbsp;</p>
<p class="remember"><label><input  name="_spring_security_remember_me" type="checkbox" value="" /><span style="vertical-align: middle;">自动登录</span></label></p>
<p class="sub"><button id="loginButton" class="logbutton">登录</button>&nbsp;&nbsp;&nbsp;&nbsp;<a class="c_b" href="${ctx}/home/customer/sendPass.jsp">找回密码?</a></p>
<input name="from" type="hidden" value="${param.from}" />
</form>
</div>
<div class="hotinfo">手机钱包   电子票优惠卷   公交一卡通    手机招行</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
</div>
</body>
</html>