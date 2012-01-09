<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用提供商首页</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/lib/commons/CityPicker.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/ajaxUploadFile.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>


<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		//alert('modifyPwd.jsp');
		var oldPassword = $('oldPassword').get('value');
		var newPassword = $('newPassword').get('value');
		
		var form = new FormCheck('confirm', {
			submit : false,
			onValidateSuccess : function() {
				new Request({
					url : ctx + '/html/user/',
					//method : 'post',
					onSuccess : function(responseText) {
						var object = JSON.decode(responseText);
						if(object.success) {
							new LightFace.MessageBox().info('操作成功');
						} else {
							new LightFace.MessageBox().error(object.message);
						}
					}
				}).post('m=modifyPassword&oldPassword='+$('oldPassword').get('value')+'&newPassword='+$('newPassword').get('value')+'&reNewPassword='+$('newPassword').get('value'));
			},
			onAjaxRequest : function() {
			},
			onAjaxSuccess : function(response) {
			}
		});
		
	});
	
	function oldPasswordCheck(el) {
		var bln = false;
		var request = new Request({
			async : false,
			url : ctx + '/html/user/',
			onSuccess : function(responseText) {
				var object = JSON.decode(responseText);
				bln = object.success;
				if(!object.success) {
					el.errors.push("密码错误");
				}
			}
		});
		request.post('m=validatePassword&password='+el.value);
		return bln;
	}
	
</script>
</head>

<body>
<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;修改登录密码</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">

	<p id="userinput_t" class="userinput_t">修改登录密码</p>
	<div id="userinput">
	<!-- content block TODO -->
		<div><form id="confirm" name="confirm">
			<dl>
				<dd>
					<p class="regtext">请输入旧密码：</p>
					<p class="left inputs">
						<input type="password" class="validate['required','%oldPasswordCheck'] inputtext" id="oldPassword"/>
					</p>
				</dd>
				<dd>
					<p class="regtext">请输入新密码：</p>
					<p class="left inputs">
						<input type="password" class="validate['required','length[6,32]'] inputtext" maxlength="32" name="password" id="newPassword"/>
					</p>
				</dd>
				<dd>
					<p class="regtext">再输一次新密码：</p>
					<p class="left inputs">
						<input type="password" class="validate['confirm:password'] inputtext" name="confirm" />
					</p>
				</dd>
				<dd class="s" style="width: 600px;">
					<input class="subutton" type="submit" value="修改密码" />
				</dd>
			</dl>
		</form></div>
	</div>

</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
</body>

</html>