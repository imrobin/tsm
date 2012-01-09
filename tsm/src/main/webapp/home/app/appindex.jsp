<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/home/app/js/appindex.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript">
	//EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); 
	var ctx = '${ctx}';
	var name= '${param.searchName}';
	var advance= '${param.advance}';
	var star= '${param.star}';
	var sp= '${param.sp}';
	var childs= '${param.childs}';
	var father= '${param.father}';
	var index ;
	window.addEvent('domready', function() {
		index = new App.Index();
	});
</script>
</head>

<body>
<div id="container">

<%@include file="/common/header.jsp" %>

<div class="curPosition">您的位置: 首页&gt;应用中心</div>
<div id="main">
<div id="div_left">
<div><img src="${ctx}/images/app_ad.png" width="720" height="257" border="0" /></div>
<div class="appcenter m_t_12">
<div style="display:none" class="title720"><img src="${ctx}/images/appce	nter_icon_32.png" />应用中心</div>
<div class="appltitle" id="apptitle">
<ul id="type">
<!--<li class="b">所有应用</li>
<li class="s">优惠打折</li>
<li class="s">金融银行</li>
<li class="s">娱乐游戏</li>
<li class="s">生活消费</li>
--></ul>
</div>
<div class="cont">
<div  id="cont"></div>
<div class="nextpage"><div id="nextpage"></div></div></div>
</div>
</div>
<div id="div_right">
<div class="top">
<%@include file="/common/topdownload.jsp" %>
</div>
<%@include file="/common/recommendApplication.jsp" %>
</div>
</div>
<%@include file="/common/footer.jsp" %>
</div>
</body>
</html>