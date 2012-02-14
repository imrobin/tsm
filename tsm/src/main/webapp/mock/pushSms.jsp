<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>clear data </title>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript">
	var transConstant;
	window.addEvent('domready', function() {
		$('submit').addEvent('click', function(event) {
			event.stop();
			var req = new Request.JSON({
				url : ctx + '/html/test/?m=sendPush',
				async : false,
				onSuccess : function(data) {
					if (data.success) {
						alert(data.message);
					} else {
						alert(data.message);
					}
				}
			});
			req.post($('clientForm').toQueryString());
		});
	});
</script>
</head>
<body>
	<center>
		<form method="post" name="clientForm" id="clientForm">
			<dl>
				<dd>
					<span style='color: red;'>*</span>手机号: <input name="mobileNo" maxlength="20" type="text" />
				输入手机号后，点击下面的发送即可</dd>
				<br />
                
				<a id="submit" href="#">发送</a>


			</dl>
		</form>
	</center>
</body>
</html>