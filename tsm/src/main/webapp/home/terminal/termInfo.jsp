<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
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
<script type="text/javascript" src="${ctx}/home/terminal/js/termInfo.js"></script>
<script type="text/javascript" src="${ctx}/home/terminal/js/termOpt.js"></script>
<script type="text/javascript">
<!--
EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}
//-->
	function swichTab(index) {
		for ( var i = 0; i <2; i++) {
			$("tab" + (i + 1)).removeClass("titleapp1").addClass("titleapp");
			$("tabC" + (i + 1)).setStyle("display", "none");
		}
		$("tab" + (index)).removeClass("titleapp").addClass("titleapp1");
		$("tabC" + (index)).setStyle("display", "");

	}
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		termInfo = new TermInfo();
		termOpt = new TermOpt();
		termInfo.doinit();
		termOpt.addTermEvent();
	});
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
<div><img src="${ctx}/images/userinfo.png" width="765" height="12" style="display:block;"/></div>
<div class="munebg1">
<div class="userinput2">
 <div class="appall">
<div class="ts1" style="display: none" id="tip"></div>
<div style="clear:both; overflow:auto; margin-top:15px;">
<div class="mobileinfo">
</div>
<div class="mobileram">
<ul>
<li><div class="ram2" id="vsTotal">内存空间：<br />1008.43KByte</div><div class="ram1">
<div class="jdb" id="c1"><p  class="jbimg1"><img  src="${ctx}/images/jd1.png" /></p><p class="jbimg2"><img src="${ctx}/images/jd3.png" /></p></div>
</div><div class="ram3" id="usedVS"><img src="${ctx}/images/img_b.png" width="11" height="11" border="0" />已用空间：</div><div class="ram4" id="existVS"><img src="${ctx}/images/img_h.png" width="11" height="11" border="0" />剩余空间：</div>
</li>
<li style="margin-top:9px;"><div class="ram2" id="nsTotal">存储空间：<br />1008.43KByte</div>
  <div class="ram1"><div class="jdb1"  id="c2">
  <p  class="jbimg1"><img src="${ctx}/images/jd1_1.png" /></p><p class="jbimg2"><img src="${ctx}/images/jd3_1.png" /></p>
  </div></div><div class="ram3" id="usedNS"><img src="${ctx}/images/img_b.png" width="11" height="11" border="0" />已用空间：</div><div class="ram4" id="existNS"><img src="${ctx}/images/img_h.png" width="11" height="11" border="0" />剩余空间：</div></li>
</ul>
</div>
</div>
<div class="zdbdiv"><a href="#" id="cancel" class="zdb" style="display: none">注销<a href="#" class="zdb" id="lost" style="display:none">挂失</a><a href="#" class="zdb" id="active" style="display: none">激活</a><a href="${ctx}/home/terminal/termList.jsp" class="zdb">返回终端列表</a></div>
</div>
<div id="infoDetial" style="display: none">
<div class="appall">
<div class="title14"><div id="tab1" class="titleapp1"><a href="javascript:swichTab(1)">可用的应用</a></div> <div id="tab2" class="titleapp"><a href="javascript:swichTab(2)">不可用的应用</a></div></div>
<div style="clear:both; overflow:auto;">
<div class="mobileapplist" id="tabC1">
</div>
<div class="mobileapplist" id="tabC2" style="display: none">
</div>
</div>
</div>
<div class="appall">
<div class="title14">终端上的安全域</div>
<div class="mobileyu">
</div>
</div>
</div>
</div>
</div>
<div><img src="${ctx}/images/userinfo1.png"></div>
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