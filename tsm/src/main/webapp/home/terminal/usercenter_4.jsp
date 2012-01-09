<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>任务管理器</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/home/terminal/js/usercenter_4.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript">
	//EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var uc;
	window.addEvent('domready', function() {
		uc = new App.Index();
		uc.loadTab1();
		uc.loadTab2();
		uc.loadTab3();
		uc.loadTab4(); //第一次加载时通读，读取titile的总条数
		$('tab1').addEvent('click', function(){
			$('tab1').set('class','usertitle');
			$('tab2').set('class','usertitle1 m_l_6');
			$('tab3').set('class','usertitle1 m_l_6');
			$('tab4').set('class','usertitle1 m_l_6');
			$('nextpage1').setStyle("display","");
			$('nextpage2').setStyle("display","none");
			$('nextpage3').setStyle("display","none");
			$('nextpage4').setStyle("display","none");
			uc.loadTab1();
		});
		$('tab2').addEvent('click', function(){
			$('tab2').set('class','usertitle m_l_6');
			$('tab1').set('class','usertitle1 ');
			$('tab3').set('class','usertitle1 m_l_6');
			$('tab4').set('class','usertitle1 m_l_6');
			$('nextpage2').setStyle("display","");
			$('nextpage1').setStyle("display","none");
			$('nextpage3').setStyle("display","none");
			$('nextpage4').setStyle("display","none");
			uc.loadTab2();
		});
		$('tab3').addEvent('click', function(){
			$('tab3').set('class','usertitle m_l_6');
			$('tab1').set('class','usertitle1 ');
			$('tab2').set('class','usertitle1 m_l_6');
			$('tab4').set('class','usertitle1 m_l_6');
			$('nextpage3').setStyle("display","");
			$('nextpage2').setStyle("display","none");
			$('nextpage1').setStyle("display","none");
			$('nextpage4').setStyle("display","none");
			uc.loadTab3();
		});
		$('tab4').addEvent('click', function(){
			$('tab4').set('class','usertitle m_l_6');
			$('tab1').set('class','usertitle1 ');
			$('tab2').set('class','usertitle1 m_l_6');
			$('tab3').set('class','usertitle1 m_l_6');
			$('nextpage4').setStyle("display","");
			$('nextpage2').setStyle("display","none");
			$('nextpage3').setStyle("display","none");
			$('nextpage1').setStyle("display","none");
			uc.loadTab4();
		});
	});
</script>
</head>

<body>
<div id="container">
<%@include file="/common/header.jsp" %>
<div class="curPosition">您的位置: 首页&gt;个人中心&gt;任务管理器</div>
<div id="main">
<div class="newuserc">
<%@include file="userMenu.jsp" %>
<div class="muneright">
<div style="clear:both; overflow:auto; margin-bottom:-1px;">
<div class="usertitle" id="tab1"><a href="javascript:void(0)">待执行<span id="tab1Sum"></span></a></div>
<div class="usertitle1 m_l_6" id="tab2" style="display:none"><a href="javascript:void(0)">已完成<span id="tab2Sum"></span></a></div>
<div class="usertitle1 m_l_6" id="tab3"><a href="javascript:void(0)">已完成<span id="tab3Sum"></span></a></div>
<div class="usertitle1 m_l_6" id="tab4"><a href="javascript:void(0)">未完成<span id="tab4Sum"></span></a></div>
</div>
<div><img src="${ctx}/images/userinfo.png" width="765" height="12" style="display:block;"></div>
<div class="munebg1">
<div class="userinput2">
<div class="uapplist" id="content"> 
</div>
<div class="nextpage"  id="nextpagef1">
<div id="nextpage1"></div>
<div id="nextpage2" style="display:none"></div>
<div id="nextpage3" style="display:none"></div>
<div id="nextpage4" style="display:none"></div>
</div> 
</div>
</div>
<img src="${ctx}/images/userinfo1.png"/>
</div>
</div>
<%@include file="/common/footer.jsp" %>
</div>
</div>
</body>
</html>