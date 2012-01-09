<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<title>终端管理</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-more.js"></script>
	<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/home/terminal/js/termOpt.js"></script>
<script type="text/javascript">
<!--

EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

var ctx = '${ctx}';
window.addEvent('domready', function() {
	termOpt = new TermOpt();
	termOpt.getAllTerminal();
});
//-->
</script>
</head>

<body>
<div id="container">
<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt;个人中心&gt;终端管理</div>
<div id="main">
<div class="newuserc">
<%@include file="userMenu.jsp" %>
<div class="muneright">
<div style="clear:both; overflow:auto; margin-bottom:-1px;">
<div class="usertitle">我的终端</div>
<div class="usertitle1 m_l_6"><a href="bind.jsp">绑定终端</a></div>
</div>
<div><img src="${ctx}/images/userinfo.png" width="765" height="12" style="display:block;"></div>
<div class="munebg1">
<div class="noinfo" id="noinfo" style="display:none"><img src="${ctx}/images/no.gif" width="36" height="33" />您目前还未绑定手机终端，无法使用手机应用相关功能。请先点击<a href="bind.jsp"><img src="${ctx}/images/button1.gif" width="91" height="28" border="0" /></a></div>
<div id="zdlist"></div>
</div>
<img src="${ctx}/images/userinfo1.png"/>
</div>
</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
<div id="doactive" style="display:none">
							<p class="regtext">请输入激活码</p>
							<p class="inputs2"><input id="activeInput" type="text" /></p>
							<p>如您长时间未收到激活码，请点击重新发送按钮</p>
						</div>
</body>
</html>