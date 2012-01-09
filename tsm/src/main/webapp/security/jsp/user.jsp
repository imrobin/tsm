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
<script type="text/javascript" src="${ctx}/security/js/user.js"></script>
<script type="text/javascript" src="${ctx}/lib/multipleselect/multipleSelect.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	new User.user();
        });
        function checkName(el) {
			var email = /^([a-zA-Z0-9_\.\-\+%])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			var name = /^[A-Za-z0-9_]+$/i;
			if(email.exec(el.value) || name.exec(el.value)) {
				return true;
			} else {
				el.errors.push("用户名请使用英文、数字、下划线或者邮箱地址");
				return false;
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
<div id="tableDiv" class="rightcont" style="height: 450px;">
</div>
</div ></div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="userDiv" style="display: none;">
<div class="regcont">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext">登录名:</p><p class="left inputs"><input id="userName" class="inputtext validate['required','%chckMaxLength','%checkName']" maxlength="32" name="userName" type="text" /></p></dd>
<dd><p class="regtext">密码:</p><p class="left inputs"><input id="password" class="inputtext validate['required','length[6,32]','%chckMaxLength']" maxlength="32" name="password" type="password" value="000000" /></p><p class="explain left">默认为000000</p></dd>
<dd><p class="regtext">真实姓名:</p><p class="left inputs"><input id="realName" class="inputtext validate['%chckMaxLength']" maxlength="32" name="realName" type="text" /></p></dd>
<dd><p class="regtext">手机:</p><p class="left inputs"><input class="inputtext validate['number','length[11,11]','%chckMaxLength']" maxlength="11" name="mobile" type="text"  /></p></dd>
<dd><p class="regtext">电子邮件:</p><p class="left inputs"><input class="inputtext validate['required','email','%chckMaxLength']" maxlength="100" name="email" type="text" /></p></dd>
<dd><p class="regtext">所属地区:</p><p class="left inputs"><select name="province"><option value="全网" selected="selected">全网</option></select></p></dd>
<dd><p class="regtext">操作员类型:</p><p class="left inputs"><select name="roleName"><option value="OPERATOR_CUSTOMER_SERVICE">客服操作员</option><option value="OPERATOR_AUDITOR">审核管理员</option><option value="SUPER_OPERATOR">超级管理员</option></select></p></dd>
<dd><p class="regtext">状态:</p><p class="left inputs"><select name="status"><option value="1">有效</option><option value="0">无效</option></select></p></dd>
</dl>
</form>
</div>
</div>
</body>
</html>
