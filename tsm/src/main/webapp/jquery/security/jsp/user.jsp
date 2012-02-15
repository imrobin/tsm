<%@ page pageEncoding="utf-8"%>
<%@ include file="/jquery/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/jquery/common/meta.jsp" %>
<link href="${ctx}/jquery/lib/jquery.jqGrid-4.2.0/css/ui.jqgrid.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/jquery/lib/jquery.jqGrid-4.2.0/js/i18n/grid.locale-cn.js" type="text/javascript"></script>
<script src="${ctx}/jquery/lib/jquery.jqGrid-4.2.0/js/jquery.jqGrid.src.js" type="text/javascript"></script>
<script src="${ctx}/jquery/security/js/user.js" type="text/javascript"></script>
<script src="${ctx}/jquery/lib/validateForm.js" type="text/javascript"></script>
<script src="${ctx}/jquery/lib/messageBox.js" type="text/javascript"></script>
</head>

<body>
	<div style="width: 100%;">
		<table id="userGrid"></table>
		<div id="userGridPage"></div>
	</div>
</body>
</html>