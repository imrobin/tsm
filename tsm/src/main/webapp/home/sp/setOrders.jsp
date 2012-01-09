<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用上传</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />

<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js" type="text/javascript"></script>
<script src="${ctx}/home/sp/js/setOrders.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx}";
	var applicationVersionId = "${param.applicationVersionId }";
	window.addEvent("domready", function() {
		var page = new Application.Page({
			applicationVersionId : applicationVersionId
		});
		page.init();

		$("submitDownloadOrderButt").addEvent("click", function(event) {
			event.stop();
			page.submitDownloadOrders();
		});

		$("setDownloadOrderButt").addEvent("click", function(event) {
			event.stop();
			page.setDownloadOrders();
		});

		$("submitDeleteOrderButt").addEvent("click", function(event) {
			event.stop();
			page.submitDeleteOrders();
		});

		$("setDeleteOrderButt").addEvent("click", function(event) {
			event.stop();
			page.setDeleteOrders();
		});

		$("submitInstallOrderButt").addEvent("click", function(event) {
			event.stop();
			page.submitInstallOrders();
		});

		$("setInstallOrderButt").addEvent("click", function(event) {
			event.stop();
			page.setInstallOrders();
		});

		$("jump").addEvent("click", function(event) {
			event.stop();
			$("jumpForm").submit();
		});

	});
</script>
</head>

<body>
	<div id="container"><%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;我的主页&gt;上传应用</div>

		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%>
					</div>

					<div class="userinput">
						<div class="upapp_t">
							<a class="on3" title="修改基本信息可在应用上传完成后进行">输入基本信息</a> <a class="on"
								href="${ctx }/home/sp/uploadCap.jsp?applicationVersionId=${param.applicationVersionId }">上传加载文件</a> <a class="on"
								href="${ctx }/home/sp/uploadExtension.jsp?applicationVersionId=${param.applicationVersionId }">上传应用扩展</a> <a class="on"
								href="${ctx }/home/sp/defineModuleAndApplet.jsp?applicationVersionId=${param.applicationVersionId }">定义模块和实例</a> <a class="on2">配置顺序</a><a
								class="on3">上传客户端</a>
						</div>
						<div class="upapptext">
							<p class="left">第 5 步: 配置顺序</p>
							<p class="right c_h">配置加载文件的下载顺序、删除顺序</p>
						</div>
						<!-- 顺序开始 -->
						<div id="orderList" class="minfo">

							<!-- 下载顺序开始 -->
							<div id="downloadOrder">
								<div id="showDownloadOrderDiv">
									<table style="width: 100%">
										<thead>
											<tr>
												<th colspan="3">下载顺序</th>
											</tr>
											<tr>
												<th style="width: 10%">下载顺序</th>
												<th style="width: 40%">加载文件名</th>
												<th>加载文件AID</th>
											</tr>
										</thead>
										<tbody id="showDownloadOrder"></tbody>
										<tfoot>
											<tr style="text-align: center;">
												<td colspan="3"><a href="#" id="setDownloadOrderButt" class="butt2" style="float: none;"><span>设置</span> </a></td>
											</tr>
										</tfoot>
									</table>
								</div>
								<div id="setDownloadOrderDiv" style="display: none">
									<table style="width: 100%">
										<thead>
											<tr>
												<th colspan="3">下载顺序</th>
											</tr>
											<tr>
												<th style="width: 10%">下载顺序</th>
												<th style="width: 40%">加载文件名</th>
												<th>加载文件AID</th>
											</tr>
										</thead>
										<tbody id="setDownloadOrder"></tbody>
										<tfoot>
											<tr>
												<td colspan="3" style="text-align: center;"><a href="#" id="submitDownloadOrderButt" class="butt2" style="float: none;"><span>提交</span>
												</a></td>
											</tr>
										</tfoot>
									</table>
								</div>
							</div>
							<!-- 下载顺序完成 ，删除顺序开始 -->
							<div id="deleteOrder">
								<div id="showDeleteOrderDiv">
									<table style="width: 100%">
										<thead>
											<tr>
												<th colspan="3">删除顺序</th>
											</tr>
											<tr>
												<th style="width: 10%">删除顺序</th>
												<th style="width: 40%">加载文件名</th>
												<th>加载文件AID</th>
											</tr>
										</thead>
										<tbody id="showDeleteOrder"></tbody>
										<tfoot>
											<tr>
												<td colspan="3" style="text-align: center;"><a href="#" id="setDeleteOrderButt" class="butt2" style="float: none;"><span>设置</span>
												</a></td>
											</tr>
										</tfoot>
									</table>
								</div>
								<div id="setDeleteOrderDiv" style="display: none">
									<table style="width: 100%">
										<thead>
											<tr>
												<th colspan="3">删除顺序</th>
											</tr>
											<tr>
												<th style="width: 10%">删除次序</th>
												<th style="width: 40%">加载文件名</th>
												<th>加载文件AID</th>
											</tr>
										</thead>
										<tbody id="setDeleteOrder"></tbody>
										<tfoot>
											<tr>
												<td colspan="3" style="text-align: center;"><a href="#" id="submitDeleteOrderButt" class="butt2" style="float: none;"><span>提交</span>
												</a></td>
											</tr>
										</tfoot>
									</table>
								</div>
							</div>
							<!-- 删除顺序完成 ， 安装顺序开始 -->
							<div id="installOrder" style="display: none">
								<div id="showInstallOrderDiv">
									<table style="width: 100%">
										<thead>
											<tr>
												<th colspan="3">安装顺序</th>
											</tr>
											<tr>
												<th style="width: 10%">安装次序</th>
												<th style="width: 40%">实例名</th>
												<th>实例AID</th>
											</tr>
										</thead>
										<tbody id="showInstallOrder"></tbody>
										<tfoot>
											<tr>
												<td colspan="3" style="text-align: center;"><a href="#" id="setInstallOrderButt" class="butt2" style="float: none;"><span>重新设置</span>
												</a></td>
											</tr>
										</tfoot>
									</table>
								</div>
								<div id="setInstallOrderDiv" style="display: none">
									<table style="width: 100%">
										<thead>
											<tr>
												<th colspan="3">安装顺序</th>
											</tr>
											<tr>
												<th style="width: 10%">安装次序</th>
												<th style="width: 40%">实例名</th>
												<th>实例AID</th>
											</tr>
										</thead>
										<tbody id="setInstallOrder"></tbody>
										<tfoot>
											<tr>
												<td colspan="3" style="text-align: center;"><a href="#" id="submitInstallOrderButt" class="butt2" style="float: none;"><span>提交</span>
												</a></td>
											</tr>
										</tfoot>
									</table>
								</div>
							</div>
							<!-- 安装顺序完成 -->
						</div>
						<!-- 顺序完成 -->
						<div>
							<a class="subbutt" id="jump" href="#" style="float: right;"><span>下一步</span> </a>
						</div>
						<p id="userinput_t" class="userinput_t"></p>
						<div id="userinput" style=""></div>

					</div>

				</div>

			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>
	<%--下载、删除、安装表单模板开始 --%>
	<table style="display: none">
		<tr id="shwoOrderTemplate">
			<td title="次序" style="text-align: center;"></td>
			<td title="名称"></td>
			<td title="AID"></td>
		</tr>
		<tr id="setOrderTemplate">
			<td style="display: none"><input name="applicationVersionId" />
			</td>
			<td style="display: none"><input name="id" /></td>
			<td style="width: 10%"><select name="order" style="width: 100%;">
					<option value=-1 selected="selected">请选择</option>
			</select></td>
			<td title="名称" style="width: 40%"></td>
			<td title="AID"></td>
		</tr>
	</table>
	<%--下载、删除、安装表单模板结束 --%>
	<form id="jumpForm" action="${ctx }/home/sp/uploadClient.jsp" style="display: none">
		<input name="applicationVersionId" value="${param.applicationVersionId }" />
	</form>
</body>
</html>