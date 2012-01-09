<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>更换终端</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/home/terminal/js/terminal.js"></script>
<script type="text/javascript" src="${ctx}/home/terminal/js/bind.js"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var t = new Terminal();
		var mt = new Terminal.list();
		t.getCanChange();//获取能够更换的列表
		 mt.getAllBrand();
		 $('mobileBrand').addEvent('change',function(){
				mt.getMobileByBrand($('mobileBrand').get('value'));
		 });
		 $('mobileType').addEvent('change',function(){
			    mt.getMobileByBrandAndType($('mobileBrand').get('value'),$('mobileType').get('value')); 
		 });
		t.bindCheckInfo();
		new FormCheck('bindForm', {
			submit : false,
			display : {
				showErrors:0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {//校验通过执行load()
				new LightFace.MessageBox({
					onClose : function(){
						var change = 2;
						t.readCard(change);
					}
				}).info("请稍后，正在处理");
				
			}
		});
	});
</script>
</head>

<body>
<div id="container">
<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt;个人中心&gt;终端管理</div>
<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx}/images/user_icon_32.png" width="32" height="32" />个人中心</div>
<div class="usercont">
<%@include file="userMenu.jsp" %>
<div class="userinput" style="overflow:auto;">
<div class="titletab">
<ul>
<li class="s2"><a href="${ctx}/home/terminal/termCenter.jsp">终端显示</a></li>
<li class="s1">更换终端</li><li class="s2"><a href="${ctx }/home/terminal/revertApp.jsp">恢复应用</a></li>
<li class="s2"><a href="${ctx }/home/terminal/bind.jsp">绑定终端</a></li>
</ul>
</div>
<form id="bindForm" method="post" action="#">
<div id="blankTip">
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<h1>您还没有任何可更换手机</h1>
							<h1><a href="${ctx}/home/terminal/bind.jsp"><font color="blue">请点这里</font></a>&nbsp;&nbsp;&nbsp;进行绑定</h1>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
						</div>
<dl id="changeContent">
<dd><p class="regtext2">1.选择已有的终端:</p><p class="left inputs_1"><select id="canChangeSelect" name="oldCardId"></select>
</p>
</dd>
<dd>		
<p class="regtext2">2.选择手机品牌:</p><p class="left inputs_1"><select id="mobileBrand" size="1"></select>
</p>
</dd>
<dd>		
<p class="regtext2">选择手机型号:</p><p class="left inputs_1"><select id="mobileType" size="1">
 </select>
</p>
</dd>
<dd>
<p class="regtext2">3.给您的手机取一个名字：</p><p class="left inputs_1"><input class="inputtext validate['required','length[1,8]'] " name="phoneName" type="text" /></p>
</dd>
<dd>
<p class="regtext2">4.放好您的手机终端到读卡器上面,点击右边的检测按钮</p><p class="left inputs_1">
<a class="buts m_t_5" href="#" id="checkBind">检测</a>
</p></dd>
<dd><p class="regtext2">您的手机号：</p><p id="changeNo" class="left inputs_1">请选择终端</p></dd>
<dd><p class="regtext"></p><p class="left inputs_1"><button class="validate['submit']">开始更换</button></p></dd>
</dl>
</form>
<div class="minfo"><img src="${ctx}/images/mible.gif" /><p>更换手机时,需要先指定一个被更换的终端.然后按照提示输入相关信息,对终端进行更换</p></div>
</div>
<div id="doactive" style="display:none">
	<p class="regtext">请输入激活码</p>
	<p class="inputs2"><input id="activeInput" type="text" /></p>
	<p>如果您长时间没有收到激活码,请点击重新发送按钮</p>
</div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp"%>
</div>
</body>
</html>