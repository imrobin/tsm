<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
<script src="${ctx}/common/js/recommendSp.js" type="text/javascript"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var recommend = new Recommend();
	});
</script>
</head>
<div class="top mt12">
<div class="top_title">推荐提供商</div>
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
