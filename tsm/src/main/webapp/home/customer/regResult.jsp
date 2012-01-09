<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>注册结果</title>
  <%@ include file="/common/meta.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js" ></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js" ></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
   window.addEvent('domready', function() {
	   var email = getQueryValue('email');
	   $('span_email').set('html',email);
   $('email').addEvent('click',function(){
	   var url = $('span_email').get('html');
	   url = "http://mail."+url.substr(url.indexOf('@')+1,url.length);
	   window.open(url);
   });
   $('reSendMail').addEvent('click',function(){
	   new Request.JSON( {
			url : '${ctx}/html/customer/?m=sendActiveEmail',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info('操作成功，请访问您的邮箱');
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post( {
			dns : 'http://' + self.location.host,
			email : email
		});
   });
   $('reSendSms').addEvent('click',function(){
	   new Request.JSON( {
			url : '${ctx}/html/customer/?m=sendActiveSms',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info('操作成功，请查看您的手机');
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post( {
			mobile : getQueryValue('mobile')
		});
   });
   $('smsActive').addEvent('click',function(){
	   new Request.JSON( {
			url : '${ctx}/html/customer/?m=smsActive',
			onSuccess : function(data) {
				if (data.success) {
					self.location = data.message;
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post( {
			mobile : getQueryValue('mobile'),
			activeCode : $('activeCode').get('value')
		});
   });
   });
   function getQueryValue(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return "";
	}
</script>
</head>

<body>
<div id="container">
		<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt用户&gt注册</div>
<div id="main">
<div class="div980 line_c">
<div class="title9801"></div>
<div class="usercont">
<div class="reginfo" style="width: 100%">
<span class="regf18"><img src="../../images/regf.png">恭喜您注册成功，您可以选择以下两种激活方式：</span>
<table width="100%">
	<tr>
		<td width="50%" style="padding: 5px;">
			<span class="c_h">激活邮件已发送到您的邮箱<br />请在您的邮箱中点击激活链接进行激活</span><br /><br />
			如果24小时后还没有收到激活邮件，请 <a id="reSendMail" class="r" href="#">重新下发</a> 激活邮件。<br /><br />
			您的邮箱是：<span id="span_email" class="c_r"></span><button id="email" class="jh">打开邮箱</button>
		</td>
		<td style="padding: 5px;">
		<span class="c_h">请在下面的文本框输入短信中的激活验证码<br />然后点击手机短信激活按钮激活手机</span><br /><br />
						如果1分钟后还没有收到激活短信，请 <a id="reSendSms" class="r" href="#">重新下发</a> 激活短信。<br /><br />
		请输入激活短信中的验证码：
			<input id="activeCode" name="activeCode" type="text" />
			<button id="smsActive" class="jh">激活</button>
		</td>
	</tr>
</table>
</div>
</div>
</div>

</div>
		<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>