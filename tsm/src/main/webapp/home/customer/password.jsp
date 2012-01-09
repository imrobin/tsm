<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户首页</title>
<%@ include file="/common/meta.jsp"%>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/crop.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>
<script type="text/javascript" src="${ctx}/lib/commons/CityPicker.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var rImage;
	window.addEvent('domready', function() {
		var cal = new Customer.Cal();
		var validater = new FormCheck('modifyPwdForm', {
			submit : false,
			trimValue : false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure : 1,
				titlesInsteadNames : 1
			},
			onValidateSuccess : function() {
				cal.modifyPwd(ctx);
			}
		});
		var sfEls = document.getElementsByTagName("INPUT");
		for ( var i = 0; i < sfEls.length; i++) {
			sfEls[i].onfocus = function() {
				this.className += " sffocus";
			}
			sfEls[i].onblur = function() {
				this.className = this.className.replace(new RegExp(
						" sffocus\\b"), "");
			}
		}
		$('password').addEvent('keyup', function(event) {
			if ($('password').get('value').length >= 6) {
				cal.passwordStrength(ctx, $('password').get('value'));
			} else {
				$('iPwdMeter').setStyle('background-image', '');
				$('iPwdMeter').set('html', '');
				$('iPwdBack').setStyle('background-image', '');
			}
		});
		$('oldPassword').addEvent('keyup', function(event) {
			var div = document.getElement('div[class=fc-tbx]');
			if ($chk(div)) {
				if ($('oldPassword').get('value') == '') {
					div.setStyle('display', '');
				} else {
					div.setStyle('display', 'none');
				}
			}
		});
		$('password').addEvent(
				'keyup',
				function(event) {
					var div = document.getElement('div[class=fc-tbx]');
					if ($chk(div)) {
						if (($('password').get('value').length < 6 || $(
								'password').get('value') == $('oldPassword')
								.get('value'))) {
							div.setStyle('display', '');
						} else {
							div.setStyle('display', 'none');
						}
					}
				});
		$('rePassword').addEvent(
				'keyup',
				function(event) {
					var div = document.getElement('div[class=fc-tbx]');
					if ($chk(div)) {
						if ($('rePassword').get('value') == $('password').get(
								'value')
								&& $('password').get('value') != $(
										'oldPassword').get('value')) {
							div.setStyle('display', 'none');
						} else {
							div.setStyle('display', '');
						}
					}
				});
	});
</script>
</head>
<body>
	<div id="container">
		<%@ include file="/common/header.jsp"%>
		<div class="curPosition">您的位置: 首页&gt个人中心&gt用户信息管理</div>
		<div id="main">
			<div class="newuserc">
				<%@include file="/home/terminal/userMenu.jsp"%>
				<div class="muneright">
					<div style="clear:both; overflow:auto; margin-bottom:-1px">
						<div class="usertitle1"><a href="${ctx}/home/customer/customerCenter.jsp">个人资料</a></div>
						<div class="m_l_6 usertitle" style="margin-right:6px;">
							更改密码
						</div>
						<div class=" m_1_6 usertitle1">
							<a href="${ctx}/home/customer/iconSet.jsp">修改头像</a>
						</div>
					</div>
					<img style="display: block;" src="${ctx}/images/userinfo.png"><div
							class="munebg1">
							<form id="modifyPwdForm" name="modifyPwdForm"
								action="${ctx}/html/customer/?m=modifyPwd" method="post">
								<table width="100%" border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td height="60" align="right" class="bdbottom"><span style='color: red;'>*</span>当前密码:&nbsp;&nbsp;
											<label></label></td>
										<td align="left" class="bdbottom"><input class="inputnew validate['required']"
												onblur="" name="oldPassword" type="password"
												id="oldPassword" title="当前密码" /></td>
									</tr>
									<tr>
										<td height="60" align="right" class="bdbottom"><span style='color: red;'>*</span>新密码:&nbsp;&nbsp;
											<label></label>
										</td>
										<td align="left" class="bdbottom"><input class="inputnew validate['required','differs:oldPassword','length[6,32]']"
												name="password" maxlength="32" type="password" id="password" />
										</td><td>		
										<div id="iPwdBack" class="PwdBack">
											<div class="PwdMeter" id="iPwdMeter">&nbsp;</div>
										</div>
										</td>
									</tr>
									<tr>
										<td height="60" align="right" class="bdbottom"><span style='color: red;'>*</span>确认新密码:&nbsp;&nbsp;
											<label></label>
										</td>
										<td align="left" class="bdbottom"><input class="inputnew validate['confirm:password']"
												id="rePassword" name="rePassword" type="password"/>
										</td>
									</tr>
									<tr>
                                   <td height="71">&nbsp;&nbsp;&nbsp;</td>
                                   <td align="center"><a id="modifyPwd" href="#" class="save validate['submit']">保存</a></td>
                                   </tr>
                                   
								</table>
									
							</form>
							


					
				</div>
				<img src="${ctx}/images/userinfo1.png">
				</div>
				
			</div>
			<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>