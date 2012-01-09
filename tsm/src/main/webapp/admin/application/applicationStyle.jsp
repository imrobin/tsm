<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>

<%@ include file="/admin/aid/script.jsp" %>
<%-- 
 --%>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/applicationStyle.js"></script>

<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		new ApplicationStyle.list();
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/admin/layout/top.jsp"%>
		<div id="main">
			<%@ include file="/admin/layout/menu.jsp"%>

			<div id="right">
				<div class="rightbo">

					<div id="tableDiv" class="rightcont" style="height: 450px;">
						<!-- grid -->
					</div>

				</div>
			</div>
		</div>
		<div id="footer" class="clear">
			<p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p>
		</div>
	</div>

	<!-- add -->
	<div id="add" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>

					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>应用名称：
						</p>
						<p class="left inputs">
							<select id="applicationId"  name="applicationId" class="validate['required']"></select>
						</p>
						<p class="explain left"></p>
					</dd>
					
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>样式地址：
						</p>
						<p class="left inputs">
							<input type="text" value="" name="styleUrl" class="validate['required']" maxlength="128"/>
						</p>
						<p class="explain left"></p>
					</dd>
					
				</dl>
			</form>
		</div>
	</div>
	
	<!-- edit -->
	
</body>
</html>
