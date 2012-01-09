<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>模拟测试</title>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/mock/js/pcmd.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
</script>
</head>
<body>
应用AID：<input id="appAidInput" type="text" style="width: 250px;" />&nbsp;&nbsp;<button id="searchButton">查询</button><br />
[<a id="newButton" href="#">新增</a>]
<table style="width: 100%" id="pcmdTable" border="1">
	<tr>
		<th>主键</th>
		<th>应用Aid</th>
		<th>指令类型</th>
		<th>批次</th>
		<th>执行顺序</th>
		<th>指令</th>
		<th style="width: 100px;">操作</th>
	</tr>
	<tr>
		<td title="id"></td>
		<td title="appAid"></td>
		<td title="type"></td>
		<td title="batch"></td>
		<td title="cmdIndex"></td>
		<td title="cmd"></td>
		<td title="option" style="text-align: center;" align="center">[<a name="editButton" href="#">修改</a>][<a name="delButton" href="#">删除</a>]</td>
	</tr>
</table>
</body>
</html>