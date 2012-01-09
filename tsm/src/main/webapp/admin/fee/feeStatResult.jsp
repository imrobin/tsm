<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<style>
a {
 text-color:blue;
}
</style>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet"
	type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<title>计费统计结果</title>
<script type="text/javascript">
	var ctx = '${ctx}';
	var spId = '${spId}'
	var start = '${start}';
	var end = '${end}';
	window.addEvent('domready', function() {
		$('excel').set(
				'href',
				ctx + '/html/feestat/?m=genExcel&spId=' + spId + "&start="
						+ start + "&end=" + end);
	});
</script>
</head>
<body bgcolor="#FFFFFF">
	<center>
		<h3>计费统计</h3>
		${name}:${start}--${end} <span><a id="excel"
			href=""><img height="16" width="16" src="${ctx}/images/excel.jpg" alt="导出EXCEL"></img></a>
		</span>
		<br />
		<br />
		空间计费统计
		<table border=0>
			<tr>
				<td width="100">卡号</td>
				<td width="50">手机号</td>
				<td width="100">会话ID</td>
				<td width="50">名称</td>
				<td width="50">版本</td>
				<td width="100">业务名称</td>
				<td width="150">计费时间</td>
				<td width="50">费用</td>
			</tr>
			<c:forEach var="s" items="${space}">
				<tr>
					<td width="100"><c:out value="${s.cardNo}" />
					</td>
					<td width="50"><c:out value="${s.mobileNo}" />
					</td>
					<td width="100"><c:out value="${s.sessionId}" />
					</td>
					<td width="100"><c:out value="${s.appName}" />
					</td>
					<td width="50"><c:out value="${s.version}"/>
					</td>
					<td width="100"><c:out value="${s.operateName}"/>
					</td>
					<td width="150"><fmt:formatDate type="both" dateStyle="medium" timeStyle="medium" value="${s.operateTime}" />
					</td>
					<td width="50"><fmt:formatNumber value="${s.price}" type="number" pattern="￥0.00" />
					</td>
				</tr>
			</c:forEach>
			<tr><td width="100"></td><td width="100"></td><td width="20"></td><td width="20"></td><td width="10"></td><td width="10"></td><td>合计:<fmt:formatNumber value="${spaceTotal}" type="number" pattern="￥0.00" />元</td></tr>
		</table>
		<br/>
		功能计费统计
		<table border=0>
			<tr>
				<td width="100">卡号</td>
				<td width="50">手机号</td>
				<td width="100">会话ID</td>
				<td width="100">名称</td>
				<td width="50">版本</td>
				<td width="100">业务名称</td>
				<td width="150">计费时间</td>
				<td width="50">费用</td>
			</tr>
			<c:forEach var="f" items="${function}">
				<tr>
					<td width="100"><c:out value="${f.cardNo}" />
					</td>
					<td width="50"><c:out value="${f.mobileNo}" />
					</td>
					<td width="100"><c:out value="${f.sessionId}" />
					</td>
					<td width="100"><c:out value="${f.appName}" />
					</td>
					<td width="50"><c:out value="${f.version}"/>
					</td>
					<td width="100"><c:out value="${f.operateName}"/>
					</td>
					<td width="150"><fmt:formatDate type="both" dateStyle="medium" timeStyle="medium" value="${f.operateTime}" />
					</td>
					<td width="50"><fmt:formatNumber value="${f.price}" type="number" pattern="￥0.00" />
					</td>
				</tr>
			</c:forEach>
			    <tr><td width="100"></td><td width="100"></td><td width="20"></td><td width="20"></td><td width="10"></td><td width="10"></td><td>合计:<fmt:formatNumber value="${funTotal}" type="number" pattern="￥0.00" />元</td></tr>
			    <tr></tr>
				<tr><td width="100"></td><td width="20"></td><td width="50"></td><td width="50"></td><td>总计:<fmt:formatNumber value="${total}" type="number" pattern="￥0.00" />元</td></tr>
		</table>
	</center>
</body>
</html>