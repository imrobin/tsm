<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>终端管理</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/home/terminal/js/terminal.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var appId = <%=request.getParameter("appId") %>
	window.addEvent('domready', function() {
		
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/common/header.jsp"%>
		<div class="curPosition">您的位置: 首页&gt;个人中心&gt;终端应用管理</div>
		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx}/images/user_icon_32.png" width="32" height="32" />个人中心
				</div>
				<div class="usercont">
					<%@include file="userMenu.jsp"%>
					<div class="userinput">
						<div class="appall" style="margin-top: 0px;">
							<div class="title14">终端应用列表</div>
							<div class="mobilealist">
								<ul id="appList">正在查询请等待...
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%@ include file="/common/footer.jsp"%>
		<div class="appopen" id="appopen" style="display: none">
			<div>
				<img src="${ctx}/images/appopentop.png" width="690" height="10" border="0" />
			</div>
			<div class="appopeninfo">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="24%"><div class="appopenimg" id="tdImage"></div></td>
						<td width="76%" style="word-spacing: 0.2em" id="infoTd"></td>
					</tr>
					<tr>
						<td height="46"></td>
						<td height="46" valign="bottom" id='optTd'></td>
					</tr>
				</table>
			</div>
			<div>
				<img src="${ctx}/images/appopenbottom.png" width="690" height="10" border="0" />
			</div>
		</div>
	</div>
	</div>
</body>
</html>