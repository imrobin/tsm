<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal" var="user" />
<c:if  test="${user != 'anonymousUser'}">
<script type="text/javascript">
	window.addEvent('domready', function(){
		$('userNameSpan').set('html', '欢迎：${user.username}');
	});
</script>
</c:if>
<div id="header">
<div class="guild"><span id="userNameSpan" style="color: #FFA500; font-weight: bold;"></span>
	<img src="${pageContext.request.contextPath}/images/user_suit.png" class="icon16" /><a href="${pageContext.request.contextPath}/html/login/?m=myCenter">我的主页</a> 
	<c:choose>
		<c:when test="${user != 'anonymousUser'}">
	<img class="icon16" src="${ctx}/webhtml/admin/images/icon_10.png" /><a href="${ctx}/j_spring_security_logout">退出系统</a> 
		</c:when>
		<c:otherwise>
	<img src="${pageContext.request.contextPath}/images/login_icon_16.png" class="icon16" /><a id="loginLink" href="${pageContext.request.contextPath}/security/login.jsp">登录</a> 
	<img src="${pageContext.request.contextPath}/images/reg_icon_16.png" class="icon16" /><a href="${pageContext.request.contextPath}/home/customer/reg.jsp">注册</a>  
		</c:otherwise>
	</c:choose>
	<img src="${pageContext.request.contextPath}/images/help_icon_16.png" class="icon16" /><a href="${pageContext.request.contextPath}/help.jsp">帮助中心</a>
	<img src="${pageContext.request.contextPath}/images/new_user.png" class="icon16" /><a href="${pageContext.request.contextPath}/tutor.jsp">新手须知</a>
</div>

<div class="clear">
	<div class="left logo"><img src="${pageContext.request.contextPath}/images/logo.gif" width="252px" height="59px" /></div>
	<%@include file="/common/search.jsp" %>
</div>
</div>
<div id="nav">
	<a href="${pageContext.request.contextPath}/index.jsp">首页</a> | 
	<c:if  test="${user != 'anonymousUser'}">
		<a href="${pageContext.request.contextPath}/html/login/?m=myCenter">我的主页</a> | 
	</c:if>
	<a href="${pageContext.request.contextPath}/home/app/appindex.jsp">应用中心</a> | 
	<a href="${pageContext.request.contextPath}/home/terminal/mobileCenter.jsp">手机中心</a> | 
	<a href="${pageContext.request.contextPath}/home/sp/spindex.jsp">提供商</a> | 
	<a href="${pageContext.request.contextPath}/home/terminal/mocamDownload.jsp">手机客户端</a> | 
	<a href="${pageContext.request.contextPath}/aboutnfc.jsp">关于NFC</a>
</div>