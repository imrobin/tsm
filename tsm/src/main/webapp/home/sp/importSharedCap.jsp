<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用上传</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css"
	rel="stylesheet" type="text/css" media="screen" />
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />

<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/admin/sd/js/sd.js" type="text/javascript"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/multipleselect/multipleSelect.js"
	type="text/javascript"></script>
<script src="${ctx}/home/sp/js/importSharedCap.js"
	type="text/javascript"></script>
<style type="text/css">
.txtoverflow {
	display: block;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
</style>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx}";
	window.addEvent("domready", function() {

		var applictionVersionId = "${param.applicationVersionId }";

		var page = new Application.Page({
			applicationVersionId : applictionVersionId
		});
		page.getSharedLoadFiles();
		page.getLoadFiles();

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
							<a class="on3" title="修改基本信息可在应用上传完成后进行">输入基本信息</a><a class="on"
								href="${ctx }/home/sp/uploadClient.jsp?applicationVersionId=${param.applicationVersionId }">上传客户端</a>
							<a class="on"
								href="${ctx }/home/sp/uploadCap.jsp?applicationVersionId=${param.applicationVersionId }">上传文件</a>
							<a class="on2">选择共享文件</a> <a class="on3">定义模块和实例</a> <a
								class="on3">配置顺序</a>
						</div>
						<div class="upapptext">
							<p class="left">第 4 步: 选择共享文件</p>
							<p class="right c_h">选择共享的加载文件</p>
						</div>

						<div title="已引入的加载文件" class="minfo">
							<table id="uploadedCapsInfo" style="width: 100%">
								<thead>
									<tr>
										<th colspan="7">已引入的共享加载文件</th>
									</tr>
									<tr>
										<th>名称</th>
										<th>版本号</th>
										<th>AID</th>
										<th style="display: none">备注</th>
										<th style="display: none">加载参数</th>
										<th>文件大小(byte)</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
							<br />
							<table style="width: 100%">
								<thead>
									<tr>
										<th colspan="4">可引入的共享加载文件</th>
									</tr>
									<tr>
										<th style="width: 15%">名称</th>
										<th style="width: 20%">AID</th>
										<th style="width: 50%">备注</th>
										<th style="width: 15%">操作</th>
									</tr>
								</thead>
								<tbody id="exclusiveLoadFiles">

								</tbody>
							</table>
							<div>
								<a class="subbutt" href="#" style="float: right;"><span
									id="jump">下一步</span> </a>
							</div>
						</div>
						<p id="userinput_t" class="userinput_t"></p>
						<div id="userinput" style=""></div>

					</div>

				</div>

			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>
</body>
<form id="jumpForm" action="${ctx }/home/sp/defineModuleAndApplet.jsp"
	style="display: none">
	<input name="applicationVersionId"
		value="${param.applicationVersionId }" />
</form>
<table id="loadFileTemplateDiv" style="display: none">
	<tbody>
		<tr id="loadFileTemplateP">
			<td><span title="名称" class="texthidden" style="width: 150px">&nbsp;</span>
			</td>
			<td title="版本号" style="width: 7%; text-align: center;">&nbsp;</td>
			<td title="AID">&nbsp;</td>
			<td title="备注" style="display: none">&nbsp;</td>
			<td title="加载参数" style="display: none">&nbsp;</td>
			<td title="文件大小" style="width: 10%; text-align: center;">&nbsp;</td>
			<td title="操作" style="text-align: center;"><a title="解除引用"
				href="#" class="butt2" style="float: none"><span>解除引入</span> </a></td>
		</tr>
	</tbody>
</table>
<%--显示已上传加载文件模板开始 --%>
<table id="loadFileTemplate" style="display: none">
	<tr id="loadFileTemplateTr">
		<th title="名称"></th>
		<th title="AID"></th>
		<th title="备注"></th>
		<th title="操作"></th>
	</tr>
	<tr id="loadFileTemplateP">
		<td><span title="名称" style="width: 100px" class="texthidden">&nbsp;</span>
		</td>
		<td title="AID">&nbsp;</td>
		<td><span title="备注" style="width: 325px" class="texthidden">&nbsp;</span>
		</td>
		<td title="操作" style="text-align: center;"><span title="引入"><a
				href="#" class="buts" style="text-align: center;">引入</a> </span> <span>
		</span> <span title="升级" style="display: none"><a href="#">升级</a> </span></td>
	</tr>
</table>
<%--显示已上传加载文件模板结束 --%>
<%--选择已上传加载文件版本表单模板开始 --%>
<div id="selectExsitLoadFileVersionFormTemlate" style="display: none">
	<div title="existLoadFileVersionDiv">
		<table>
			<thead>
				<tr>
					<th style="width: 5%"></th>
					<th style="width: 10%">版本号</th>
					<th style="width: 20%">Hash</th>
					<th style="width: 30%">加载参数</th>
					<th style="width: 15%">文件大小<br />(byte)</th>
				</tr>
			</thead>
			<tbody title="existLoadFileVersions">
				<tr>
					<td><input type="text" name="applicationVersionId"
						style="display: none" /></td>
				</tr>
			</tbody>
		</table>
		<table>
			<tbody>
				<tr title="existLoadFileVersionTemplateP">
					<td><input type="radio" name="loadFileVersionId"
						style="display: none"></input></td>
					<td title="版本号" style="text-align: center;">&nbsp;</td>
					<td title="Hash"></td>
					<td title="加载参数" style="text-align: center">&nbsp;</td>
					<td title="文件大小" style="text-align: center;">&nbsp;</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<%--选择已上传加载文件版本表单模板结束 --%>
<%--加载文件信息输入表单模板开始 --%>
<div id="uploadDivTemplate" style="display: none">
	<div title="loadFile">
		<input type="text" name="tempFileAbsPath" style="display: none" /> <input
			type="text" name="tempDir" style="display: none" /> <input
			type="text" name="applicationVersionId"
			value="${param.applicationVersionId }" style="display: none" />
		<table class="openw">
			<tr>
				<th>名称:</th>
				<td colspan="2"><input class="inputtext validate['required']"
					name="name" type="text" value="测试加载文件" /></td>
			</tr>
			<tr>
				<th>AID:</th>
				<td colspan="2"><input
					class="inputtext validate['required','length[10,32]','%checkHex','%checkAid']"
					name="aid" type="text" value="" maxlength="32" /></td>
			</tr>
			<tr>
				<th>备注：</th>
				<td colspan="2"><textarea
						class="inputtext validate['required','length[0,80]']"
						name="comments">测试</textarea></td>
			</tr>
			<tr>
				<th>版本号:</th>
				<td colspan="2"><input class="inputtext validate['required']"
					name="versionNo" type="text" value="1.0.0" /></td>
			</tr>
			<tr>
				<th>HASH值:</th>
				<td colspan="2"><input class="inputtext" name="hash"
					type="text" value="hash" value="554433221100" /></td>
			</tr>
			<tr>
				<th>所属安全域模式:</th>
				<td colspan="2"><select name="sdModel">
						<option value=1>主安全域</option>
						<option selected="selected" value=2>公共第三方安全域</option>
						<option value=3>DAP模式</option>
				</select></td>
			</tr>
			<tr>
				<th>加载参数: 0x</th>
				<td><input class="inputtext" name="loadParams" type="text"
					value="EF0CC6025DC9C702000AC8020800" /></td>
				<td><span title="点击输入参数"><a href="#">配置参数</a> </span></td>
			</tr>
			<tr id="capUploadTr">
				<th>CAP文件:</th>
				<td><input id="file" name="file" type="file" /> <span
					title="上传"><a href="#">上传</a> </span></td>
			</tr>
			<tr id="capUploadedTr" style="display: none">
				<th>CAP文件:</th>
				<td></td>
			</tr>
		</table>
	</div>
</div>
<%--加载文件信息输入表单模板结束 --%>
<%--升级加载文件信息输入表单模板开始 --%>
<div id="loadFileVersionDivTemplate" style="display: none">
	<div title="loadFileVersion">
		<input type="text" name="tempFileAbsPath" style="display: none" /> <input
			type="text" name="tempDir" style="display: none" /> <input
			type="text" name="applicationVersionId"
			value="${param.applicationVersionId }" style="display: none" /> <input
			type="text" name="loadFileId" style="display: none" />
		<table class="openw">
			<tr>
				<th>版本号:</th>
				<td colspan="2"><input class="inputtext validate['required']"
					name="versionNo" type="text" value="1.0.0" maxlength="48" /></td>
			</tr>
			<tr>
				<th>HASH值:</th>
				<td colspan="2"><input class="inputtext validate['required']"
					name="hash" type="text" value="hash" value="554433221100"
					maxlength="160" /></td>
			</tr>
			<tr>
				<th>加载参数: 0x</th>
				<td><input class="inputtext validate['required']"
					name="loadParams" type="text" value="EF0CC6025DC9C702000AC8020800" />
				</td>
				<td><span title="点击输入参数"><a href="#">配置参数</a> </span></td>
			</tr>
			<tr id="capUploadTr">
				<th>CAP文件:</th>
				<td><input class="validate['required']" id="file" name="file"
					type="file" /> <span title="上传"><a href="#">上传</a> </span></td>
			</tr>
			<tr id="capUploadedTr" style="display: none">
				<th>CAP文件:</th>
				<td></td>
			</tr>
		</table>
	</div>
</div>
<%--升级加载文件信息输入表单模板结束 --%>
<%--加载参数输入表单模板开始 --%>
<div id="loadParamsFormDivTemplate" style="display: none">
	<div title="loadParams">
		<table class="openw">
			<tr>
				<th>不可变编码空间:</th>
				<td><input class="inputtext validate['required','digit']"
					name="nonVolatileCodeSpace" type="text" value="2000" maxlength="10" />
				</td>
			</tr>
			<tr>
				<th>可变数据空间:</th>
				<td><input class="inputtext validate['required','digit']"
					name="volatileDateSpace" type="text" value="0" maxlength="10" /></td>
			</tr>
			<tr>
				<th>不可变数据空间:</th>
				<td><input class="inputtext validate['required','digit']"
					name="nonVolatileDateSpace" type="text" value="0" maxlength="10" />
				</td>
			</tr>
		</table>
	</div>
</div>
<%--加载参数输入表单模板结束 --%>
</html>