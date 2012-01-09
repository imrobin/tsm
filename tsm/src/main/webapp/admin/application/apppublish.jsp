<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/winGrid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/appManager.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript">
		var ctx = '${ctx}';
		var appMa;
		window.addEvent('domready', function(){
			appMa = new AppManager.Publish();
		});
		function mobileCheck(el){
			//alert(el.value);
			var mobiles = el.value.split(",");
			for (var i = 0 ;i<mobiles.length;i++){
			//	alert(mobiles[i]);
				for (var j = 0 ; j<mobiles.length;j++){
					if (mobiles[i] == mobiles[j] && i != j){
						el.errors.push("号码"+mobiles[i]+"重复输入了,请确认号码");
						return false;
					}
				}
				if (isNaN(mobiles[i])){
					el.errors.push("号码"+mobiles[i]+"错误,必须为以1开头的11位数字");
					return false;
				}
				if (mobiles[i].indexOf(".") != -1){
					el.errors.push("号码"+mobiles[i]+"错误,必须为以1开头的11位数字");
					return false;
				}
				if (mobiles[i].length != 11){
					el.errors.push("号码"+mobiles[i]+"错误,必须为以1开头的11位数字");
					return false;
				}
		//		alert(mobiles[i].charAt(0));
				if (mobiles[i].charAt(0) != '1'){
					el.errors.push("号码"+mobiles[i]+"错误,必须为以1开头的11位数字");
					return false;
				}
			}
			
		}
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;"></div>
</div>
</div>
</div>
<div id="publishDiv"  style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form id="appForm"  name="appForm"  method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" id="submitBtn" style="display: none;"></button>
<dl>
<dd><p style="float:left; font-size:14px; width:170px; text-align:left; margin-right:10px;">
限定使用手机号<br/>(手机号之间请用逗号隔开):</p><p class="left inputs">
<input id="mobiles" class="inputtext validate['required','%mobileCheck']" name="mobiles" type="text" /></p>
<input id="cardBaseInfoId"  name="cardBaseInfoId" type="hidden"/></dd>
<!--<dd><p class="regtext" >终端:</p><p class="left inputs" id="cardP">
<input id="cardBaseInfoIdInput" class="inputtext validate['required']" name="cardBaseInfoIdInput" type="text"  readonly="readonly"/>

</p></dd>
--></dl>
</form>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="editAppInfoDiv"  style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form id="appForm"  name="appForm"  method="post">
<input name="applicationId" id="appId"  type="hidden" />
<button id="submitBtn" style="display: none;"></button>
<dl>
	<dd><p class="regtext">应用名称:</p><p class="left inputs">
	<input id="appName" class="inputtext" name="appName" type="text"  readonly="readonly"/></p></dd>
	<dd>
		<p class="regtext">删除规则:</p>
		<p class="left inputs"><select name="deleteRule" id="deleteRule"  >
			<option value="0">不能删除</option>
			<option value="1">删除整个应用程序</option>
			<option value="2">只删除个人化数据</option>
		</select></p>
	</dd>
	<dd>
		<p class="regtext">个人化类型:</p>
		<p class="left inputs"><select name="personalType" id="personalType">
			<option value="1">指令透传</option>
			<option value="2">应用访问安全域</option>
			<option value="3">安全域访问应用</option>
			<option value="0">不需要个人化</option>
		</select></p>
	</dd>
</dl>
</form>
</div>
</div>
</body>
</html>
