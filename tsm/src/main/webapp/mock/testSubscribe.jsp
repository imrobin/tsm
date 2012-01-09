<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>模拟业务平台</title>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript">
	var transConstant;
	window.addEvent('domready', function() {
		var spId = $('sp_id')
		var request = new Request.JSON({
			url : ctx + '/html/feerulespace/?m=getSpName',
			async : false,
			onSuccess : function(data) {
				spId.empty();
				var a = data.message;
				if (data.success) {
					spId.options.add(new Option("选择提供商", ""));
					Array.each(a, function(item, index) {
						spId.options.add(new Option(item[1], item[0]));
					});
				} else {
				}
			}
		});
		request.post();
		spId.addEvent('change', function() {
			var aid = $('aidName');
			var req = new Request.JSON({
				url : ctx + '/html/feerulespace/?m=getNameBySp',
				async : false,
				onSuccess : function(data) {
					aid.empty();
					var a = data.message;
					if (data.success) {
						aid.options.add(new Option("选择应用", ""));
						Array.each(a, function(item, index) {
							aid.options.add(new Option(item[1], item[0]));
						});
					} else {

					}
				}
			});
			req.post('spId=' + spId.get('value') + "&type=1");
		});
		$('submit').addEvent('click', function(event) {
			event.stop();
			var req = new Request.JSON({
				url : ctx + '/html/test/?m=subscribe',
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
					<span style='color: red;'>*</span>应用提供商: <select id="sp_id" name="sp_id" size="1">
					</select>
				</dd>
				<br />
				<dd>
					<span style='color: red;'>*</span>应用名称: <select id="aidName" name="aidName" size="1">
						<option value="">选择应用</option>
					</select>
				</dd>
				<br />

				<dd>
					<span style='color: red;'>*</span>卡号: <input name="cardNo" maxlength="20" type="text" />
				</dd>
				<br />
				<dd>
					<span style='color: red;'>*</span>手机号: <input name="mobileNo" maxlength="16" type="text" />
				</dd>
				<br />
				<dd>
					<span style='color: red;'>*</span>业务操作: <select id="eventId" name="eventId" size="1">
						<option value="3">--订购--</option>
						<option value="2">--退订--</option>
					</select>
				</dd>
				<br />

				<a id="submit" href="#">提交请求</a>


			</dl>
		</form>
	</center>
</body>
</html>