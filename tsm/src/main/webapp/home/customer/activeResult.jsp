<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/home/app/js/star.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/home/app/js/appinfo.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<sec:authentication property="principal" var="user" />
<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		if ('${message}'== '账户激活成功' && '${user}' == 'anonymousUser') {
			var uid = getQueryValue('uid');
			var userName = null;
			new Request.JSON({
				url : '${ctx}/html/user/?m=getUser',
				async : false,
				onSuccess : function (data) {
					if (data.success) {
						userName = data.message.userName;
					}
				}
			}).get({userId : uid});
			var activeCode = getQueryValue('activeCode');
			var password = activeCode.substring(activeCode.lastIndexOf('@') + 1, activeCode.length);
			new Request.JSON({
				url : '${ctx}/j_spring_security_check',
				onSuccess : function (data) {
					if (data.success) {
						self.location.href = self.location+"&isLogin=y";
					}
				}
			}).post({j_username : userName, j_password : password});
		}
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
<div id="container"><%@ include file="/common/header.jsp"%>
<div id="main">
<div class="div980 line_c">
<div class="title9801"></div>
<div class="usercont">
<c:if test="${message == '激活已经失效，请重新申请激活邮件或短信'}">
	<div class="reginfo"><span class="regf18red"><img src="${ctx}/images/error.png" />尊敬的用户：${message}。</span><br />
	<br />
	您可以做以下事情：<br />
	<br />
	<img class="icon16" src="${ctx}/images/link.png" /><a class="b" href="${ctx}/security/login.jsp">重新登录获取激活链接</a>
	<img class="icon16" src="${ctx}/images/web.png" /><a class="b" href="${ctx}/index.jsp">浏览网站</a></div>
</c:if> 
<c:if test="${message == '该账户未注册,请先注册'}">
	<div class="reginfo"><span class="regf18red"><img src="${ctx}/images/error.png" />尊敬的用户：${message}。</span><br />
	<br />
	您可以做以下事情：<br />
	<br />
	<img class="icon16" src="${ctx}/images/link.png" /><a class="b" href="${ctx}/home/customer/reg.jsp">进行注册</a>
	<img class="icon16" src="${ctx}/images/web.png" /><a class="b" href="${ctx}/index.jsp">浏览网站</a></div>
</c:if> 
<c:if test="${message == '账户激活成功'}">
	<div class="reginfo"><span class="regf18"><img src="${ctx}/images/regf.png" />尊敬的用户：恭喜您帐户已激活。</span><br />
	<br />
	您可以做以下事情：<br />
	<br />
	<img class="icon16" src="${ctx}/images/link.png" /><a class="b" href="${ctx}/home/terminal/bind.jsp">绑定终端</a> <img
		class="icon16" src="${ctx}/images/edit.png" /> <a class="b" href="${ctx}/home/customer/customerCenter.jsp">完善个人资料</a> 
		<img class="icon16" src="${ctx}/images/web.png" /><a class="b" href="${ctx}/index.jsp">浏览网站</a></div>
</c:if>
<c:if test="${message == '账户已登录'}">
	<div class="reginfo"><span class="regf18"><img src="${ctx}/images/regf.png" />尊敬的用户：恭喜您帐户已激活。</span><br />
	<br />
	您可以做以下事情：<br />
	<br />
	<img class="icon16" src="${ctx}/images/link.png" /><a class="b" href="${ctx}/home/terminal/bind.jsp">绑定终端</a> <img
		class="icon16" src="${ctx}/images/edit.png" /> <a class="b" href="${ctx}/home/customer/customerCenter.jsp">完善个人资料</a> 
		<img class="icon16" src="${ctx}/images/web.png" /><a class="b" href="${ctx}/index.jsp">浏览网站</a></div>
</c:if>
<c:if test="${message == '已经激活'}">
	<div class="reginfo"><span class="regf18"><img src="${ctx}/images/regf.png" />尊敬的用户：帐户已激活。</span><br />
	<br />
	</div>
</c:if>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp"%></div>
</body>
</html>