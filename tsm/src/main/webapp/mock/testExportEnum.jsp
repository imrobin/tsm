<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet"
	type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js"
	type="text/javascript"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript">
	var transConstant;
	window
			.addEvent(
					'domready',
					function() {
						new Request.JSON(
								{
									url : ctx
											+ "/html/commons/?m=exportEnum&enumName=com.justinmobile.tsm.cms2ac.security.scp02.EncryptorVendor&exportMethodName=export",
									onSuccess : function(json) {
										if (json.success) {
											transConstant = json.message;
											var jsonHash = new Hash(transConstant);
											jsonHash.each(function(value, key) {
													alert(value.name);
											});
										}
									}
								}).get();
					});
</script>
</head>
<body>
</body>
</html>