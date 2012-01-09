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
<script type="text/javascript" src="${ctx}/admin/encryptor/js/encryptor.js"></script>

<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		new Encryptor.list();
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
							<span style="color: red;">*</span>加密机厂商：
						</p>
						<p class="left inputs">
							<select id="vendor"  name="vendor" class="validate['required']"></select>
						</p>
						<p class="explain left"></p>
					</dd>
					
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>类型：
						</p>
						<p class="left inputs">
							<input type="radio" value="1" name="model" checked="checked"/> 安全域 <input type="radio" value="2" name="model"/> 应用
						</p>
						<p class="explain left"></p>
					</dd>
					
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>版本：
						</p>
						<p class="left inputs">
							<input id="version" name="version" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
						<p class="explain left"></p>
					</dd>
					
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>索引：
						</p>
						<p class="left inputs">
							<input id="index" name="index" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
						<p class="explain left"></p>
					</dd>
					
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>密文：
						</p>
						<p class="left inputs">
							<input id="ciphertext" name="ciphertext" class="inputtext validate['required','%checkHex','length[32,32]']" type="text" maxlength="32" />
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
