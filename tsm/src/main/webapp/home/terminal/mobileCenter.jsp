<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>手机中心</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/home/terminal/js/mobileType.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
	window.addEvent('domready', function() {
		 var mt = new MobileType.list();
		 mt.getAllBrand();
		 $('mobileBrand').addEvent('change',function(){
			    $('quick_search').set('value','');
			    $('mobile-paging').set('html','');
				mt.getMobileByBrand($('mobileBrand').get('value'));
		 });
		 $('mobileType').addEvent('change',function(){
			    $('quick_search').set('value','');
			    $('mobile-paging').set('html','');
			    mt.getMobileByBrandAndType($('mobileBrand').get('value'),$('mobileType').get('value')); 
		 });
		 $('keywordButton').addEvent('click',function(){
			   $('mobile-paging').set('html','');
			mt.getMobileByKeyword($('quick_search').get('value')); 
		 });
	});
</script>
</head>
<body>
<div id="container">
		<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt手机中心</div>
<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx}/images/mobile_icon.png" width="32" height="32" />手机中心</div>
<div class="usercont">
<div class="mcenter">
<div class="mst"><img class="left" src="${ctx}/images/mst_l.gif" /><p class="left">看看您的手机是否支持NFC应用！</p><img class="right" src="../../images/mst_r.gif"/></div>
<div class="msearch">选择手机品牌:<select id="mobileBrand" name="mobileBrand" size="1">
<option value="-1">全部品牌</option></select>
 选择手机型号:<select id="mobileType" name="mobileType" size="1">
 <option value="-1">全部型号</option></select>
  输入关键字:<input id="quick_search" name="q" type="text" /><button id="keywordButton">匹配手机</button>
</div>
<img style="display:block;" src="${ctx}/images/mbottom.gif" width="100%" />
</div>
<div class="mcenter">
<div class="mst1">如果您需要下载NFC应用，请选择购买以下手机</div>
<div class="phonelist">
<dl id="mobile-list">
</dl>
<br />
<div id="mobile-paging" style="text-align: right;"></div>
</div>
</div>
</div>
</div>
</div>
		<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>
