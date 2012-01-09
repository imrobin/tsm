<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js" ></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/ie6png.js" ></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	    var ctx = '${ctx}';
		var cal = new Customer.Cal();
	    
	    window.addEvent('domready', function() {
	    	var validater = new FormCheck('resetPassForm', {
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
					new Request.JSON({
						url : $('resetPassForm').get('action'),
						onSuccess : function(object) {
							if(object.success) {
								self.location = ctx + '/home/customer/resetPassSuccess.jsp';
							}else{
								self.location = ctx + '/home/customer/resetPassError.jsp';
							}
						},
						onError : function(response) {
						}
					}).post($('resetPassForm').toQueryString());
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
			});
		});
		
	   
</script>
</head>
<body>
<div id="container">
		<%@ include file="/common/header.jsp"%>

<div id="main">
  <div class="psbg">
<div class="passtilte"><img src="${ctx}/images/key.png" />请设置新密码</div>
<form id="resetPassForm" name="resetPassForm" action="${ctx}/html/customer/?m=resetPwd" method="post">
<input type="hidden" name="checkSign" value="${userRP.checkSign}"></input>
<table width="580" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="80">您的用户名：</td>
   <td width="180">
      <input name="email" type="text" value="${userRP.email}" readonly/>
    </td>
    </tr>
  <tr>
    <td>设置新密码：</td>
    <td>
      <input class="validate['required','length[6,32]']" name="password" id="password" type="password" value="" maxlength="32"/>
</td><td><div id="iPwdBack" class="PwdBack">
        <div class="PwdMeter" id="iPwdMeter">&nbsp;</div>
     </div></td>
  </tr>
  <tr>
    <td> 重复新密码：</td>
    <td><input class="validate['confirm:password']" name="rePassword"  id="rePassword" type="password" value="" maxlength="32"/></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><a id="modifyPwd" class="subbutt validate['submit']"><span>保存</span></a></td>
  </tr>
</table>
</form>
</div>
</div>
		<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>