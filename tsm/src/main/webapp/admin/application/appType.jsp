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
<script type="text/javascript" src="${ctx}/lib/grid/winGrid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers90.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/ajaxUploadFile.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/appType.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	new App.Type();
        });
        (function() {
        	var rememberTitle = document.title; // 记住原有的窗口标题

        	try { // try-catch 用于兼容不支持attachEvent方法的浏览器
        		document.attachEvent('onpropertychange', function(){
        			if (document.title != rememberTitle) { // 此判断一定要加上，否则会导致递归调用堆栈溢出
        				document.title = rememberTitle;
        			}
        		});
        	} catch (e) {
        		// noop
        	}

        	document.setTitle = function(newTitle) { // 如果需要自定义修改标题，请使用此方法替代“document.title = xxx”语句
        		rememberTitle = newTitle;
        		document.title = newTitle;
        	};
        })();
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
<div id="appTypeDiv" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext"><span style='color: red;'>*</span>应用类型名：</p><p class="left inputs"><input class="inputtext validate['required','%chckMaxLength','en_cn']" maxlength="32" name="name" type="text" /></p></dd>
<dd><p class="regtext"><span style='color: red;'>*</span>类别：</p><p class="left inputs"><input class="inputtext validate['required','%chckMaxLength','number_nonnegative']" maxlength="10" name="classify" type="text" /></p></dd>
<dd><p class="regtext"><span style='color: red;'>*</span>应用类型等级：</p><p class="left inputs"><select id="typeLevel" name="typeLevel"><option value="1">一级类型</option><option value="2">二级类型</option></select></p></dd>
<dd><p class="regtext">父类型：</p><p class="left inputs"><select style="overflow: scroll;" id="parentType_id" name="parentType_id"></select></p></dd>
<dd>
		<p class="regtext">类型LOGO:</p>
		<p class="left inputs">
			<input tabindex="16" class="inputtext validate['citypicker']" id="logoFileName" name="logoFileName" type="text" readonly="readonly"/>
			<input type="hidden" name="logoPath" id="logoPath"/>
		</p>
		<p class="explain left">
			<div>
				<span id="spanButtonPlaceholder"></span>
			</div>
			<div id="divFileProgressContainer" style="display: none;"></div>
		</p>
	</dd>
	<dd id="thumbnailsDd" style="display: none;">
		<p class="regtext"></p>
		<p class="left inputs" id="thumbnails"></p>
	</dd>
</dl>
</form>
</div>
</div>
</body>
</html>
