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
		cal.setProvince();
		//日期显示控件
		cal.getCustomerInfo();
		cal.loadUserIcon();
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
		$('year').addEvent('change', function(event) {
			cal.adjustDay($('month').get('value'));
		});
		$('month').addEvent('change', function(event) {
			cal.adjustDay($('month').get('value'));
		});
		new FormCheck('modifyCustomerForm', {
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
				cal.modifyCustomer(ctx);
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
						<div class="usertitle">个人资料</div>
						<div class="usertitle1 m_l_6">
							<a href="${ctx}/home/customer/password.jsp">更改密码</a>
						</div>
						<div class="usertitle1 m_l_6">
							<a href="${ctx}/home/customer/iconSet.jsp">修改头像</a>
						</div>
					</div>
					<img style="display: block;" src="${ctx}/images/userinfo.png"><div
							class="munebg1">
							<form id="modifyCustomerForm" name="modifyCustomerForm"
								action="${ctx}/html/customer/?m=modifyCustomer" method="post">
								<table width="100%" border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td width="23%" align="center" valign="top"><br />
											<table width="106" height="150" border="0" cellpadding="0"
												cellspacing="0">
												<tr>
													<td height="105" align="center" background="${ctx}/images/userimg_bg.gif"><img
														id="userlogo" src="" width="90" height="90" style="" />
													</td>
												</tr>
												<tr>
													<td height="44" align="center"><a
														href="${ctx}/home/customer/iconSet.jsp" class="edit">修改头像</a>
													</td>
												</tr>
											</table></td>
										<td width="77%" align="center"><table width="94%"
												border="0">
												<tr>
													<td height="60" align="right" class="bdbottom">昵称:&nbsp;&nbsp;
														<label></label>
													</td>
													<td align="left" class="bdbottom"><input
														class="inputnew" id="nickName" name="nickName" type="text"
														value="" maxlength="8" />
													</td>
												</tr>
												<tr>
													<td height="60" align="right" class="bdbottom">邮编:&nbsp;&nbsp;</td>
													<td align="left" class="bdbottom"><input
														class="inputnew validate['zip']" id="zip" name="zip"
														type="text" value="" maxlength="6" />
													</td>
												</tr>
												<tr>
													<td height="60" align="right" class="bdbottom">详细地址:&nbsp;&nbsp;</td>
													<td align="left" class="bdbottom"><input
														class="inputnew" id="address" name="address" type="text"
														maxlength="128" />
													</td>
												</tr>
												<tr>
													<td height="60" align="right" class="bdbottom">性别:&nbsp;&nbsp;</td>
													<td align="left" class="bdbottom"><label><input
															id="sex-x" type="radio" name="sex" type="radio" value="1" />先生</label>
														<label><input id="sex-o" type="radio" name="sex"
															value="0" />女士</label>
													</td>
												</tr>
												<tr>
													<td height="60" align="right" class="bdbottom">生日:&nbsp;&nbsp;
													</td>
													<td align="left" class="bdbottom"><select id="year"
														name="year">
													</select> <select id="month" name="month">
													</select> <select id="day" name="day">
													</select></td>
												</tr>
												<tr>
													<td height="60" align="right" class="bdbottom">所在地:&nbsp;&nbsp;</td>
													<td align="left" class="bdbottom"><select
														id="location" name="location">
													</select>
													</td>
												</tr>
											</table>
										</td>
									</tr>

									<tr>
										<td height="71">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
										<td align="center"><a id="customerModifyButton" href="#"
											class="save validate['submit']">保存</a>
										</td>
									</tr>
								</table>
							</form>
						</div>
						<img src="${ctx}/images/userinfo1.png">
				</div>
			</div>


		</div><%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>