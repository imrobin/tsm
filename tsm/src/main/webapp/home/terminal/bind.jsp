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
		$('menu_termcenter').setStyles({
			'background' : 'url("'+ ctx + '/images/user_m1.png") repeat scroll 0 0 transparent',
			'color' : '#FFFFFF',
			'font-size' : '14px',
			'font-weight' : 'bold',
			'text-decoration' : 'none'
		});
		var t = new Terminal();
		var mt = new Terminal.list();
		 mt.getAllBrand();
		 $('mobileBrand').addEvent('change',function(){
				mt.getMobileByBrand($('mobileBrand').get('value'));
		 });
		/*  $('mobileType').addEvent('change',function(){
			    mt.getMobileByBrandAndType($('mobileBrand').get('value'),$('mobileType').get('value')); 
		 }); */
		t.bindCheckInfo();
		new FormCheck('bindForm', {
			submit : false,
			display : {
				showErrors:0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {//校验通过执行load()
				var bind = 1;
				t.readCard(bind);
			}
		});
	});
</script>
</head>

<body>
<div id="container">
<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置：首页&gt;个人中心&gt;终端管理</div>
<div id="main">
<div class="newuserc">
<%@include file="userMenu.jsp" %>
<div class="muneright">
<div style="clear:both; overflow:auto; margin-bottom:-1px;">
	<div class="usertitle1 "><a href="termList.jsp">我的终端</a></div>
	<div class="usertitle m_l_6">绑定终端</div>
</div>
<div><img src="${ctx}/images/userinfo.png" width="765" height="12" style="display:block;"/></div>
<div class="munebg1">
<form id="bindForm" method="post" action="#">
	<table width="84%" border="0" align="center" style="margin-top:10px;">
	  <tr>
    <td width="8%" height="50" align="center"><img src="${ctx}/images/on1.png" width="35" height="39" border="0" /> 
      <label></label></td>
    <td width="92%" class="STYLE3 STYLE4">选择您的手机品牌及型号</td>
    </tr>
  <tr>
	 <td height="50" align="center"  class="bdbottom"> 
      <label></label></td>
    <td  class="bdbottom">
   	 手机品牌 : <select id="mobileBrand" size="1"></select>
   	 	&nbsp;&nbsp;&nbsp;&nbsp;
     手机型号 : <select id="mobileType" size="1"></select>
      </td>
  </tr>
	<tr>
    <td height="50" align="center"><img src="${ctx}/images/on2.png" width="35" height="39" border="0" /> 
      <label></label></td>
    <td height="29" class=" STYLE3 STYLE4">给您的手机取一个名字</td>
    </tr>
  <tr>
    <td height="60" align="center"  class="bdbottom">&nbsp;</td>
    <td height="29"  class="bdbottom"><input class="inputnew validate['required','length[1,8]'] " name="phoneName" type="text" maxlength="8" /></td>
  </tr>	
	<tr>
    <td height="50" align="center"><img src="${ctx}/images/on3.png" width="35" height="39" border="0" />
      <label></label></td>
    <td height="29"><span class="STYLE5">放好您的手机终端到读卡器上面，点击右边的检测按钮</span>  
      <label></label>
&nbsp;
<label></label>
<label></label></td>
    </tr>
	<tr>
    <td height="60" align="center"  class="bdbottom">&nbsp;</td>
    <td height="3"  class="bdbottom"><a href="#" class="ck" id="checkBind">检测终端</a><img src="" id="checkFlag" height="16px" style="display:none"/></td>
  </tr>
  <tr>
    <td height="60" align="center">&nbsp;</td>
    <td height="24"><a href="#" class="save validate['submit']">开始绑定</a></td>
  </tr>
  </table>
</form>
</div>
<div id="doactive" style="display:none">
	<p class="regtext">请输入激活码</p>
	<p class="inputs2"><input id="activeInput" type="text" /></p>
	<p>如您长时间未收到激活码，请点击重新发送按钮</p>
</div>
<img src="${ctx}/images/userinfo1.png"/>
</div>
</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
</body>
</html>