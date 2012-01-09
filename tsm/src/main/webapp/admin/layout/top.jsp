<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script src="${ctx}/admin/layout/js/top.js" type="text/javascript"></script>
<sec:authentication property="principal" var="user" />
<c:if  test="${user != 'anonymousUser'}">
<script type="text/javascript">
	window.addEvent('domready', function(){
		$('userNameSpan').set('html', '${user.username}');
	});
</script>
</c:if>
<script type="text/javascript">
	var ctx = '${pageContext.request.contextPath}';
	window.addEvent('domready', function(){
		new JIM.UI.TopMenu('nav', {
			url : '${ctx}/html/menu/?m=indexShow'
		}).load();
		setInterval(function(){
			var myDate = new Date();
			$('dateSpan').set('html', myDate.toLocaleDateString());
			$('timeSpan').set('html', myDate.toLocaleTimeString());
		}, 1000);
		new JIM.Top();
		
	});
</script>
<div id="header">
<div class="head1">
	<p class="logo"><img src="${ctx}/webhtml/admin/images/logo.png"></img></p>
	<p class="welcome">管理员：<span id="userNameSpan"></span>&nbsp;欢迎您登录多应用管理后台! 
		<a id="accountSet" class="w" href="#"><img class="icon16" src="${ctx}/webhtml/admin/images/icon_9.png" />帐户设置</a>
		<a id="passwordSet" class="w" href="#"><img class="icon16" src="${ctx}/images/reg_icon_16.png" />修改密码</a>
		<a class="w" href="${ctx}/j_spring_security_logout"><img class="icon16" src="${ctx}/webhtml/admin/images/icon_10.png" />退出系统</a>
	</p>
</div>
</div>
<div id="nav">
	<p class="datetime right">
		<img class="icon16" src="${ctx}/webhtml/admin/images/icon_1.png" />当前日期：<span id="dateSpan"></span>
		<img class="icon16" src="${ctx}/webhtml/admin/images/icon_2.png" />当前时间：<span id="timeSpan"></span>
	</p>
</div>
<div id="accountDiv" style="display: none;">
<div class="regcont">
<form action="${ctx}/html/user/?m=updateSelf">
<input type="hidden" name="roleName" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext">真实姓名:</p><p class="left inputs"><input id="realName" class="inputtext validate['%chckMaxLength']" maxlength="32" name="realName" type="text" /></p></dd>
<dd><p class="regtext">手机:</p><p class="left inputs"><input class="inputtext validate['required','number','length[11,11]','%chckMaxLength']" maxlength="11" name="mobile" type="text"  /></p></dd>
<dd><p class="regtext">电子邮件: </p><p class="left inputs"><input class="inputtext validate['required','email','%chckMaxLength']" maxlength="100" name="email" type="text" /></p></dd>
</dl>
</form>
</div>
</div>
<div id="passwordDiv" style="display: none;">
<div class="regcont">
<form id="passwordForm" action="${ctx}/html/user/?m=modifyPassword">
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext">当前密码:</p><p class="left inputs"><input class="inputtext validate['required','length[6,32]','%chckMaxLength']" maxlength="32" name="oldPassword" type="password" /></p></dd>
<dd><p class="regtext">新密码:</p><p class="left inputs"><input class="inputtext validate['required','length[6,32]','%chckMaxLength']" maxlength="32" id="newPassword" name="newPassword" type="password" /></p></dd>
<dd><p class="regtext">确认新密码: </p><p class="left inputs"><input class="inputtext validate['required','length[6,32]','%chckMaxLength','confirm:newPassword']" maxlength="32" name="reNewPassword" type="password" /></p></dd>
</dl>
</form>
</div>
</div>