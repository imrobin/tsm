<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>手机钱包管理</title>

<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js" ></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers90.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/ajaxUploadFile.js"></script>

<script type="text/javascript" src="${ctx}/admin/application/js/mobileWallet.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
    window.addEvent('domready', function(){
    	new MobileWallet.manage();
    });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>

	<div id="right">
		<div class="rightbo">
		
			<div id="tableDiv" class="rightcont" style="height: 450px;">
				<!-- grid -->
			</div>
		
		</div>
	</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>

<!-- add -->
<div id="mobileWalletDivAdd" style="display: none;">
<div class="regcont">
<form method="post" name="addClientForm">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>

	<dd>
		<p class="regtext"><span style='color: red;'>*</span>名称：</p>
		<p class="left inputs">
			<input class="inputtext validate['required','length[0,16]']" name="name" maxlength="16" type="text"/>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>版本：</p>
		<p class="left inputs">
			<input id="version"
							class="inputtext validate['required','%checkVersionNo']"
							name="version" type="text" value="1.0.0" maxlength="8" /><span
							class="explain">格式为x.x.x，x代表0-99的数字</span>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>系统版本：</p>
		<p class="left inputs">
			<select id="sysRequirment" name="sysRequirment" size="1">
			
			</select>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>开发版本：</p>
		<p class="left inputs">
			<input id="versionCode" class="inputtext validate['required','digit[1,99999]']" name="versionCode" maxlength="5" type="text" value="" ></input>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>客户端包名：</p>
		<p class="left inputs">
			<input id = "clientPackageName" class="inputtext validate['required']" name="clientPackageName" type="text" value="" maxlength="256"></input>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>客户端入口类：</p>
		<p class="left inputs">
			<input id = "clientClassName" class="inputtext validate['required']" name="clientClassName" type="text" value="" maxlength="256"></input>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>选择文件：</p>
		<p class="left inputs">
			<input tabindex="16" class="inputtext validate['required']" id="clientFileName" name="clientFileName" type="text" readonly="readonly" />
			<input type="hidden" name="fileUrl" id="fileUrl"/>
		</p>
		<p class="regtext"></p>
		<p class="left inputs">
				<span id="spanButtonPlaceholder"></span>
			<div id="divFileProgressContainer" style="display: none;"></div>
		</p>
		<input type="hidden" id="fileSize" name="size"></input>
		<input type="hidden" id="sysType" name="sysType" value="os"></input>
	</dd>
		<dd id="thumbnailsDd" style="display: none;">
		<p class="regtext"></p>
		<p class="left inputs" id="thumbnails">
		</p>
	</dd>
</dl>
</form>
</div>
</div>

</body>
</html>