<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>订购关系列表</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>

<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<script src="${ctx }/home/sp/js/listSd.js" type="text/javascript"></script>
<script src="${ctx }/home/sp/js/sp.js" type="text/javascript"></script>
<script src="${ctx }/admin/sd/js/sd.js" type="text/javascript"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function(){
		
		$('app').addEvent('click', function() {self.location = ctx + '/home/sp/listSubscribeApp.jsp';});
		
		new SecurityDomain.SubscribeList();
	});
	
</script>

</head>

<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;订购关系&gt;安全域</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">
	<div id="userinput">
		<div class="titletab">
			<ul>
				<li class="s1">安全域</li>
				<li class="s2" style="cursor: pointer;" id="app">应用</li>
			</ul>
		</div>
		<div class="minfo">
			<div id="grid"></div>
			<div id="nextpage" align="right"></div>
		</div>
	</div>
</div>

<!--  -->
<!-- subscribe table -->
<div name="subscribeDiv" style="display: none;">
	<div name="subscribeTable"></div>
	<div id="grid_"></div>
	<div id="nextpage_" align="right"></div>
</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>

</body>
</html>