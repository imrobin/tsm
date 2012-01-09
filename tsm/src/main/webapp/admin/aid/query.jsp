<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>

<%@ include file="script.jsp" %>

<script type="text/javascript" src="${ctx}/admin/aid/js/aid.js"></script>

<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		//TODO aid
		new ApplicationIdentifier.query();
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

</body>
</html>
