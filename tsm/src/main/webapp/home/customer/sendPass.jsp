<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js" ></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js" ></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script src="${ctx}/home/customer/js/forgetPassword.js" type="text/javascript"></script>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/forget-password.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	    var ctx = '${ctx}';
	    var hasClick = false;
	    var fp = new ForgetPassword.Display();
	    window.addEvent('domready', function() {
	    	$('dns').set('value',location.protocol+"//"+location.host);
	    	 $('J_CheckCode').addEvent('click', function(event) {
	    		fp.refreshImage();
			});
	    	$('J_checkcode_trigger').addEvent('click', function(event) {
	    		fp.refreshImage();
			}); 
	    	var item = $('main').getElements("input[type='radio']");
	    	item.addEvent('click',function(event){
	    		if(item[0].checked){
	    			fp.setEmail();
		    	}else if(item[1].checked){
		    		fp.setMobile();
		    	}
	    	});
	    	$('in_email').addEvent('click',function(event){
	    		item[1].set('checked','checked');
	    		fp.setMobile();
	    	});
	    	$('in_mobile').addEvent('click',function(event){
	    		item[0].set('checked','checked');
	    		fp.setEmail();
	    	});
			$('sendPassButton').addEvent('click',function(event){
				event.stop();
				var captcha = $('j_captcha_response');
					if(item[1].checked){
						var reg=/^([a-zA-Z0-9_\.\-\+%])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
						var email = $('in_email');
						if(!reg.test(email.get('value').trim())){
						   email.getParent().getElement('[class="msg"]').setStyle('display','block');
						   email.getParent().getElement('[class="error"]').set('html','请输入正确的邮箱');
						   return false;
						}else if(captcha.get('value').trim().length!=4){
							 captcha.getParent().getElement('[class="msg"]').setStyle('display','block');
							 captcha.getParent().getElement('[class="error"]').set('html','请输入长度为4的验证码');
							 return false;
						}else{
							fp.sendRequest(item);
						}	
					}else if(item[0].checked){
						var reg=/^1[358]\d{9}$/;
						var mobile = $('in_mobile');
						if(!reg.test(mobile.get('value').trim())){
						   mobile.getParent().getElement('[class="msg"]').setStyle('display','block');
						   mobile.getParent().getElement('[class="error"]').set('html','请输入正确的手机号码');
						   return false;
						}else if(captcha.get('value').trim().length!=4){
							 captcha.getParent().getElement('[class="msg"]').setStyle('display','block');
							 captcha.getParent().getElement('[class="error"]').set('html','请输入长度为4的验证码');
							 return false;
						}else{
							fp.sendRequest(item);
						}	
					}
			});
		});
	   
</script>
</head>
<body>
<div id="container">
<%@ include file="/common/header.jsp"%>
<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx}/images/mobile_icon.png" width="32" height="32" />找回密码</div>
<div class="usercont">
<form id="sendPassForm" name="sendPassForm" action="${ctx}/html/customer/?m=findPwdRequest" method="post">
<div class="cntbox">
<h3>请输入以下资料中的一项</h3>

        <div class="fpout"> <div class="fpbox">
            <ul id="fp_choose" class="formitem choose">
                <li id="first" class="first">
                    <label class="lb lb_m"><input name="type" value="mobile" type="radio" />手机：</label>
                    <input tabindex="50" id="in_mobile" name="mobile" value="" type="text" class="text" maxlength="11"/>
                    <div class="msg" style="display:none;" ><p class="error"></p></div>
                </li>
                <li id="last" class="last">
                    <label class="lb lb_e"><input name="type" value="email" type="radio" />邮箱：</label>
                    <input  id="in_email" name="email" value=""  type="text" class="text" maxlength="64"/>
                    <input id="dns" name="dns" value="" type="hidden"></input>
                    <div class="msg"  style="display:none;" ><p class="error"></p></div>
                </li>
            </ul>

        </div></div>

        <h3>请输入图片中的验证码</h3>
        <div class="fpout"> <div class="fpbox">
            <ul class="formitem">	
            	<li class="last">
                    <label class="lb lb_co" for="ck_code_input">验证码：</label>
                    <img id="J_CheckCode" src="${ctx}/j_captcha_get" class="codeimg" title="点击刷新验证码" />
                    <span style="position:relative;left:5px;color:#666;text-decoration:underline;top:-8px;cursor:pointer;" id="J_checkcode_trigger">刷新验证码</span>
					<br>
                    <input tabindex="100" name="j_captcha_response" maxlength="4" id="j_captcha_response" type="text" class="text code" />
                    <div class="msg"  style="display:none;" ><p class="error"></p></div>
                </li>
            </ul>
        </div> </div> 
        <div class="skin-blue">
            <button id="sendPassButton" class="btn submit">提交</button>
        </div>
        </div>
        </form>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
</div>

</div>
</body>
</html>