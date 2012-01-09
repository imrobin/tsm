<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>SP审核信息列表</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>

<script type="text/javascript" src="${ctx }/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var spId = '';
	var map = new Table();
	window.addEvent('domready', function(){
		new ServiceProvider.requistion();
	});
	
</script>

</head>

<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;信息管理&gt;审核信息</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">
	<div id="userinput">
		<div class="minfo">
			<h3>注册资料审核信息</h3>
			<div id="gridSp1"></div>
			<div id="sp1nextpage" align="right"></div>
		</div>
		<div class="minfo">
			<h3>修改资料审核信息</h3>
			<div id="gridSp2"></div>
			<div id="sp2nextpage" align="right"></div>
		</div>
	</div>
</div>

<div style="display: none;" id="form">
<%@ include file="spForm.jsp"%>
</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>

</body>
</html>