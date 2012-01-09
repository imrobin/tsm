<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript"	src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/admin/sp/js/recommendSp.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
			new RecommandSp();
        });
        function orderCheck(el){
			if (el.value != ''){
				var no = orderNos.split(",");
				for (var i = 0;i<no.length;i++){
					var str1 = no[i].split(":")[0]; // 序列
					var str2 = no[i].split(":")[1]; // 省份
					if (parseFloat(str1) == parseFloat(el.value,10)
						//	 && str2 == selectProvince
							 ){
						el.errors.push("该推荐排序已经存在");
						return false;
					}
				}
			}
        }
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;">
</div>
</div></div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="requistionDiv" style="display: none;">
<div class="regcont">
<form id="appForm"  name="appForm"  method="post">
<button class="validate['submit']" id="submitBtn" style="display: none;"></button>
<dl>
	<dd>
	<p class="regtext">提供商名称</p>
	<p class="left inputs" ><select name="sp.id" id="sp" class="validate['required']"  style="width:400px">
	</select></p>
	</dd>
<dd><p class="regtext">推荐排序:</p><p class="left inputs">
<input id="orderNo" class="inputtext validate['required','number_nonnegative','%orderCheck']" name="orderNo" value="" /></p></dd>

</dl>
</form>
</div>
</div>
</body>
</html>
