<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>模拟业务平台</title>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/mock/js/editPcmd.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
</script>
</head>
<body>
<center>
<form id="pcmdForm" action="" method="post">
<input type="hidden" name="id" id="pcmdId" />
<table border="1" style="width: 400px;">
	<tr>
		<td align="right" style="width: 150px;">应用Aid<font color="red">*</font>：</td>
		<td><input name="appAid" title="应用Aid" type="text" style="width: 250px;" /></td>
	</tr>
	<tr>
		<td align="right" style="width: 150px;">指令类型<font color="red">*</font>：</td>
		<td><select name="type"><option value="1">写数据</option><option value="2">读数据</option><option value="3">删数据</option></select></td>
	</tr>
	<tr>
		<td align="right">批次号<font color="red">*</font>：</td>
		<td><input name="batch"  title="批次号" type="text" style="width: 250px;" /></td>
	</tr>
	<tr>
		<td align="right">执行顺序<font color="red">*</font>：</td>
		<td><input name="cmdIndex"  title="执行顺序" type="text" style="width: 250px;" /></td>
	</tr>
	<tr>
		<td align="right">指令<font color="red">*</font>：</td>
		<td><textarea name="cmd" title="指令" cols="5" rows="5" style="width: 250px;"></textarea></td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<input type="submit" id="saveButton" value="保存"></input>
			<input type="button" id="returnButton" value="返回"></input>
		</td>
	</tr>
</table>
</form>
</center>
</body>
</html>