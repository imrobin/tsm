<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script src="${ctx}/home/app/js/advanceSearch.js" type="text/javascript"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var search = new Search();
		$('parentType').addEvent('change', function(){
		    search.getChild($(this).value);
		});
		$('search').addEvent('click', function(){
			var types = "&childs="+hasChecked;
			//alert(hasChecked.length);
			//alert($('parentType').get('value') != '-1' && hasChecked.length == 0);
			if ($('parentType').get('value') != '-1' && hasChecked.length == 0){
				types = "&father="+$('parentType').get('value');
			}
			var nameV = $('name').get('value').trim();
			nameV = encodeURIComponent(nameV);
			window.location.href = ctx+'/home/app/appindex.jsp?advance=true&searchName='+nameV+types
			+'&sp='+$('sp').get('value')+'&star='+$('star').get('value');
		});
		
	});
</script>
</head>
<body>
<div id="container">
<%@ include file="/common/header.jsp" %>
<div class="curPosition">您的位置: 首页&gt;高级搜索</div>
<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx}/images/search_icon.png" width="32" height="32" />高级搜索</div>
<div class="usercont">
<div class="ad_s">
<dl>
<dt>关键字：</dt>
<dd><input class="intext" name="name"  type="text"  id="name"/></dd>
</dl>
</div>
<div class="ad_s2">
<table>
<tr>
<td>应用分类：</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select id="parentType" name="parentType">
</select></td>
</tr>
<tr>
<td>应用类别：</td>
<td  id="childType">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无类别信息</td>
</tr>
<tr>
<td align="right">提供商：</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select id="sp" name="sp">
</select></td>
</tr>
<tr>
<td align="right">星 级：</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name="star" id="star">
  <option value="">请选择...</option>
  <option value="5">5星</option>
  <option value="4">4星</option>
  <option value="3">3星</option>
  <option value="2">2星</option>
  <option value="1">1星</option>
  <option value="0">0星</option>
</select></td>
</tr>

</table>
<div class="ad_s_b"><button class="jh" id="search">搜索</button></div>
</div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
</div>
</body>
</html>