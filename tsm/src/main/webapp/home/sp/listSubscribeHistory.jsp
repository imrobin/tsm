<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>订购历史列表</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" media="screen" />

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>

<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<script src="${ctx }/home/sp/js/listSd.js" type="text/javascript"></script>
<script src="${ctx }/home/sp/js/sp.js" type="text/javascript"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function(){
		
		new ServiceProvider.SubscribeHistory();
		
	});
	
	function yyyymmCheck(el) {
		if (!el.value.test(/^[A-Z]/)) {
	        el.errors.push("Username should begin with an uppercase letter");
	        return false;
	    } else {
	        return true;
	    }
	}
</script>

</head>

<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;订购历史&gt;列表</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">
	<div id="userinput">
		<form id="queryform">
		用户名称:&nbsp;<input type="text" value="" name="nickName" id="nickName"/>&nbsp;&nbsp;
		手机号码:&nbsp;<input type="text" value="" class="validate['phone']" name="customerCardInfo_mobileNo" id="customerCardInfo_mobileNo" maxlength="11"/>&nbsp;&nbsp;
		订购时间(例如:201109):&nbsp;<input type="text" value="" class="validate['number','length[6,6]']" name="subscribeDate" id="subscribeDate" maxlength="6"/>&nbsp;&nbsp;
		<input type="submit" value="查询" id="submitBtn"/>
		</form>
		<div class="minfo">
			<div id="grid"></div>
			<div id="nextpage" align="right"></div>
		</div>
	</div>
</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>

</body>
</html>