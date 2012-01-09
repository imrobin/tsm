<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>	
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script src="${ctx}/security/js/login.js" type="text/javascript"></script>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/forget-password.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css"
	rel="stylesheet" type="text/css" />
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
   window.addEvent('domready', function() {
	   var mobile = location.search.substr(8);
	   var hasClick = false; 
	   $('mobile').set('value',mobile);
	   $('validateCode').addEvent('click',function(event){
		   var validateCode = $('validateCode');
		   validateCode.getParent().getElement('[class="msg"]').setStyle('display','none');
		   validateCode.getParent().getElement('[class="error"]').set('html','');
	   });
	   $('sendCodeButton').addEvent('click',function(event){
		        event.stop();
			    var validateCode = $('validateCode');
			    var reg=/^\d{6}$/;
			    if(!reg.test(validateCode.get('value').trim())){
						 validateCode.getParent().getElement('[class="msg"]').setStyle('display','block');
						 validateCode.getParent().getElement('[class="error"]').set('html','请输入6位数字验证码');
						 return false;
				}else{
					if (!hasClick){
						var box = new LightFace.MessageBox();
						box.loading('提交中，请稍候');
							 var request = new Request({
								url : $('sendPassForm').get('action'),
								onSuccess : function(response) {
									var result = JSON.decode(response);
									if(result.success) {
										self.location = ctx + '/html/customer/?m=findPwdByEmail&'+result.message;
									} else {
										if(result.message=='验证码输入错误'){
											new LightFace.MessageBox().error(result.message);
										}else if(result.message=='验证码过期'){
											self.location = ctx +'/home/customer/resetPassError.jsp';
										}
									}
									box.close();
									
								},
								onError : function(response) {
									box.close();
								}
							});
							request.post($('sendPassForm').toQueryString()); 
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
				<div class="title980">
					<img src="${ctx}/images/mobile_icon.png" width="32" height="32" />找回密码
				</div>
				<div class="usercont">
					<form id="sendPassForm" name="sendPassForm"
						action="${ctx}/html/customer/?m=findPwdByMobile" method="post">
						<div class="cntbox">
							<h3>请输入手机验证码</h3>
							<div>
								<ul class="formitem mod J_ValidateMod">
									<li><label for="input_1" class="lb title">
											手机号码： </label> <input type="text" class="text"
											id="mobile" name="mobile" maxlength="11" readonly /></li>
									<li><label for="input_2" class="lb title"> 验证码： </label>
											<input type="text" id="validateCode" name="validateCode"
											maxlength="6" class="text" />
											<div class="msg" style="display: none;">
												<p class="error"></p>
											</div> </li>

								</ul>
							</div>
							<div class="skin-blue">
								<br />
								<button id="sendCodeButton" class="btn submit">提交</button>
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