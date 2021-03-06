<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/winGrid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/admin/cardbase/js/cardbaseManager.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var cbm = new CardBaseManager.joinSecurityDomains();
        });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;"></div>
<div class="userinput" style="display: none" id="subForm">
		<form id="appverForm" action="${ctx}/html/cardbasesecurity/?m=doLink">
			<table>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>选择安全域:
						</p></td>
					<td>
						<p >
							<input readonly="readonly" id="selectedSD" style="width: 200px" type="text" />
							<a class="buts m_t_5" id="getSD">选择</a>
						</p>
					</td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>是否预置:
						</p></td>
					<td>
						<select id="preset" name="preset">
							<option value="0">不预置</option>
							<option value="1">预置</option>
						</select>
					</td>
				</tr>
					<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>预置模式:
						</p></td>
					<td>
						<select id="presetMode" name="presetMode" disabled="disabled">
							<option value="2">创建模式</option>
							<option value="4">个人化模式</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>预置密钥版本:
						</p></td>
					<td>
						<p class="left inputs">
							<input disabled="disabled" id="presetKeyVersion" style="width: 200px" class="inputtext validate['required','%checkKeyVersion']" name="presetKeyVersion" type="text" maxlength="2" />
						</p>
					</td>
				</tr>
			</table>
			<input type="hidden" id="cardId" name="cardBaseId"></input>
			<input type="hidden" id="cbSdId" name="cbSdId"></input>
			<input type="hidden" id="sdId" name="sdId"></input>
			<button class="validate['submit']" style="display: none;"></button>
		</form>
</div>
</div>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
</body>
</html>
