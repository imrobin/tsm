<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>恢复应用</title>
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
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
   window.addEvent('domready', function() {
		var t = new Terminal();
		var oldId = t.getQueryValue("oldId");
		t.bindCheckInfo();
		$('reverSelect').addEvent('change',function(){
			var ccid = $('reverSelect').get('value');
			t.getReverAppList(ccid);
		 });
		$("doRevert").addEvent('click',function(){
			if(t.checked == 1){
				new LightFace.MessageBox().error("操作前请先检测您的终端"); 
			}else{
			   if(t.checked == 2){
				   new LightFace.MessageBox( {
						onClose : function() {
							if (this.result) {
								   t.revert();
							}
						}
					}).confirm("您确认恢复应用吗？");
			   }else{
				   new LightFace.MessageBox().error("您的终端尚未准备好"); 
			   }
			}
		});
		if(oldId){
		    t.getOneInfoToOption(oldId);
		}else{
			t.getCanRevert();//获取能够更换的列表	
		}
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
<div class="userinput">
<div class="titletab"><ul><li class="s2"><a href="${ctx}/home/terminal/termCenter.jsp">终端显示</a></li><%-- <li class="s2"><a href="${ctx }/home/terminal/change.jsp">更换终端</a></li> --%><li class="s1">恢复应用</li><li class="s2"><a href="${ctx }/home/terminal/bind.jsp">绑定终端</a></li></ul></div>
<div id="blankTip">
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<h1>您还没有符合恢复条件的终端</h1>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
						</div>
<div id="changeContent">
<dl>
<dd><p class="regtext">选择要恢复的终端: </p><p class="left inputs_1">
<select id="reverSelect">
</select></p></dd>
</dl>
<div id="appTip" style="display:none">
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<h2>此终端无可恢复应用信息</h2>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
</div>
<div class="minfo">
<table width="100%" border="0" cellpadding="0" cellspacing="0" id="appTable">
 <thead>
  <tr>
    <th width="300">应用名称</th>
    <th width="80">状态</th>
    <th width="200">提供商</th>
  </tr>
  </thead>
  <tbody>
  </tbody>
</table>
</div>
<div align="left">
	<br/>
	<a class="buts" href="#" style="text-align: center" id="checkBind">检测</a> <img src="" id="checkFlag" height="16px" style="display:none"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="buts" href="#" style="text-align: center" id="doRevert">恢复</a>
	<br/>
</div>
</div>
</div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp"%>
</div>
</body>
</html>