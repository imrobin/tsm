<%@ page pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
<script src="${ctx}/common/js/recommendApplication.js" type="text/javascript"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var recommend = new Recommend();
		$('recomappall').addEvent('click',function(){
			recommend.loadRecommand();
			$('recomappall').set('class','b');
			$('recomlocal').set('class','s');
		});
		$('recomlocal').addEvent('click',function(){
			recommend.loadRecommandLocal();
			$('recomappall').set('class','s');
			$('recomlocal').set('class','b');
		});
	});
</script>
</head>
<div class="top mt12">
<!--<div class="top_title">推荐应用下载</div>
-->
<div class="appltitle2">
<ul id="recomapp">
<li id="recomappall" class="b" style="cursor: pointer;">全网推荐应用</li>
<li id="recomlocal" class="s" style="cursor: pointer;display:none" >本省推荐应用</li>
</ul>
</div>
<div class="top_cont">
<dl>
<dd id="recomdd0"></dd>
<dd id="recomdd1"></dd>
<dd id="recomdd2"></dd>
<dd id="recomdd3"></dd>
<dd id="recomdd4"></dd>
</dl>
</div>
</div>
