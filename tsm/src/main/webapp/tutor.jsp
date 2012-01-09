<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<title>新用户指南</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/iepng.js" type="text/javascript"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
</script>


<script type="text/javascript">
<!--
<!--//--><![CDATA[//><!--
sfFocus = function() {
var sfEls = document.getElementsByTagName("INPUT");
for (var i=0; i<sfEls.length; i++) {
   sfEls[i].onfocus=function() {
    this.className+=" sffocus";
   }
   sfEls[i].onblur=function() {
    this.className=this.className.replace(new RegExp(" sffocus\\b"), "");
   }
}
}
if (window.attachEvent) window.attachEvent("onload", sfFocus);
//--><!]]>

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
</head>

<body onload="MM_preloadImages('${ctx}/images/up1.png','${ctx}/images/down1.png')">
<div id="container">

<%@ include file="/common/header.jsp" %>


<div id="main">
<div><img src="${ctx}/images/top_780.png" width="980" height="10" /></div>
<div class="userdiv"><div class="userup"><a href="#" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image10','','${ctx}/images/up1.png',1)"><img src="${ctx}/images/up.png" name="Image10" width="64" height="51" border="0" id="Image10" /></a></div><div class="usert"><img src="${ctx}/images/one.png" /></div><div class="usertext"><span class="f26">选择您的手机品牌及型号，下载相应的手机客户端</span><br />手机客户端是运行应用的唯一平台，为确保你下载的应用能成功运行，请您先下载手机客户端。<br /><br />
  <img src="${ctx}/images/photo1.png"></div><div class="userdown"><a href="#" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image11','','${ctx}/images/down1.png',1)"><img src="${ctx}/images/down.png" name="Image11" width="64" height="51" border="0" id="Image11" /></a></div></div>
<div><img src="${ctx}/images/bottom_780.png" width="980" height="10" /></div>
<%@ include file="/common/footer.jsp" %>
</div>
</div>
</body>
</html>