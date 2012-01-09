<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>业务接口管理</title>

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

<script type="text/javascript" src="${ctx}/admin/application/js/applicationService.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
    window.addEvent('domready', function(){
    	new ApplicationService.manage();
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
		<p class="regtext"><span style='color: red;'>*</span>应用提供商:</p>
		<p class="left inputs">
			<select id="sp_id" class="validate['required']" name="sp_id" size="1">
			</select>
		</p>	
	</dd>
	<dd>
	   <p class="regtext"><span style='color:red;'>*</span>类型:</p>
	   <p class="left inputs">
		<input class="inputradio" name="type" type="radio" value="1" checked/>应用
		<input class="inputradio" name="type" type="radio" value="2" />安全域
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>名称:</p>
		<p class="left inputs">
			<select id="aidName" class="validate['required']" name="aidName" size="1">
			</select>
		</p>
	</dd>
	<dd>
	    <p class="regtext"><span style='color: red;'>*</span>接口类型:</p>
		<p class="left inputs">
		    <select id="serviceName" name="serviceName" size="1">
		    </select>
		</p>
	</dd>	
</dl>
</form>
</div>
</div>
</body>
</html>