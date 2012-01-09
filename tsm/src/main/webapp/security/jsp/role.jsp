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
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/security/js/role.js"></script>
<script type="text/javascript" src="${ctx}/lib/multipleselect/multipleSelect.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	new User.role();
        });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;">
</div>
</div></div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="roleDiv" style="display: none;">
<div class="regcont">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext">角色名:</p><p class="left inputs"><input class="inputtext validate['required','%chckMaxLength','nameUpcase']" maxlength="32" name="roleName" type="text" /></p></dd>
<dd><p class="regtext">描述:</p><p class="left inputs"><input class="inputtext validate['required','%chckMaxLength','en_cn']" maxlength="255" name="description" type="text" /></p></dd>
<dd><p class="regtext">登录转向的页面:</p><p class="left inputs"><input class="inputtext validate['required','%chckMaxLength','rel_url']" maxlength="256" name="loginSuccessForward" type="text"  /></p></dd>
</dl>
</form>
</div>
</div>
</body>
</html>
