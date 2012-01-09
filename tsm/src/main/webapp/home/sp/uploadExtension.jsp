<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/taglibs.jsp"%><%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal" var="user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>上传应用</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" media="screen" />
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers.js"></script>
<script src="${ctx}/home/sp/js/uploadCap.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/customerCheck.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx}";
	var spId = "${user.id }";
	var fileType = 2;

	window.addEvent("domready", function() {

		var applictionVersionId = "${param.applicationVersionId }";

		var page = new Application.Page({
			applicationVersionId : applictionVersionId
		});
		page.getExclusiveLoadFiles();
		page.getLoadFiles();

		$("requestUpload").addEvent("click", function(event) {
			event.stop();
			new Application.LoadFile({
				applicationVersionId : applictionVersionId,
				page : page
			}).createNewLoadFile();
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
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%></div>

					<div class="userinput">
						<div class="upapp_t">
							<a class="on3" title="修改基本信息可在应用上传完成后进行">输入基本信息</a><a class="on"
								href="${ctx }/home/sp/uploadCap.jsp?applicationVersionId=${param.applicationVersionId }">上传加载文件</a> <a class="on2">上传应用扩展</a> <a
								class="on3">定义模块和实例</a> <a class="on3">配置顺序</a><a class="on3">上传客户端</a>
						</div>
						<div class="upapptext">
							<p class="left">第 3 步: 上传应用扩展</p>
							<p class="right c_h">上传应用扩展或选择已上传的应用扩展</p>
						</div>
						<div class="minfo">
							<div class="opertop">
								<p class="oper1">
									<a class="butt2" href="#" id="requestUpload"><span><img src="${ctx }/images/uploadIcon.png" class="icon16" />上传扩展文件</span> </a>
								</p>
							</div>
							<br />
							<table id="uploadedCapsInfo" style="width: 100%">
								<thead>
									<tr>
										<th colspan="6">已引入的应用扩展</th>
									</tr>
									<tr>
										<th style="">名称</th>
										<th style="">版本号</th>
										<th style="">AID</th>
										<!-- <th>备注</th> -->
										<th style="display: none">加载参数</th>
										<th>文件大小<br /> (byte)</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
							<table style="width: 100%">
								<thead>
									<tr>
										<th colspan="3">可引入的应用扩展</th>
									</tr>
									<tr>
										<th style="width: 15%">名称</th>
										<th style="width: 20%">AID</th>
										<th style="width: 45%; display: none;">备注</th>
										<th style="width: 20%">操作</th>
									</tr>
								</thead>
								<tbody id="exclusiveLoadFiles">

								</tbody>
							</table>
							<div style="width: 100%; text-align: center;">
								<a class="subbutt" href="#" style="float: right;"><span id="jump">下一步</span> </a>
							</div>
						</div>
						<p id="userinput_t" class="userinput_t"></p>
						<div id="userinput" style=""></div>

					</div>

				</div>

			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>

	<form id="jumpForm" action="${ctx }/home/sp/defineModuleAndApplet.jsp" style="display: none">
		<input name="applicationVersionId" value="${param.applicationVersionId }" />
	</form>
	<%--选择安全域模板开始 --%>
	<select style="display: none" id="sdSelectTemplate">
		<option title="disableTemplate" style="width: 300px;">请选择</option>
		<option title="enableTemplate" style="width: 300px; text-align: left;"></option>
	</select>
	<%--选择安全域模板开始结束 --%>
	<%--显示已引入加载文件模板开始 --%>
	<table id="loadFileTemplateDiv" style="display: none">
		<!-- style="display: none" -->
		<tbody>
			<tr id="loadFileTemplateP">
				<td title="名称" style="width: 8%; text-align: center;">&nbsp;</td>
				<td title="版本号" style="width: 10%; text-align: center;">&nbsp;</td>
				<td title="AID" style="width: 33%;">&nbsp;</td>
				<!--<td title="备注">&nbsp;</td>-->
				<td title="加载参数" style="width: 29%; display: none">&nbsp;</td>
				<td title="文件大小" style="width: 8%; text-align: center;">&nbsp;</td>
				<td title="操作" style="width: 12%; text-align: center;"><a title="删除" href="#" class="butt2" style="float: none;"><span>删除</span>
				</a></td>
			</tr>
		</tbody>
	</table>
	<%--显示已引入加载文件模板结束 --%>
	<%--显示已上传加载文件模板开始 --%>
	<table id="loadFileTemplate" style="display: none">
		<tr id="loadFileTemplateP">
			<td title="名称">&nbsp;</td>
			<td title="AID">&nbsp;</td>
			<td style="display: none;"><span title="备注" style="width: 350px;" class="texthidden">&nbsp;</span></td>
			<td title="操作" style="text-align: center;"><a title="引入" href="#" class="butt2" style="float: none;"><span>引入</span> </a><a
				title="升级" href="#" class="butt2" style="float: none;"><span>升级</span> </a>
			</td>
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
						<th style="width: 15%">文件大小(byte)</th>
					</tr>
				</thead>
				<tbody title="existLoadFileVersions">
					<tr>
						<td><input type="text" name="applicationVersionId" style="display: none" /></td>
					</tr>
				</tbody>
			</table>
			<table>
				<tbody>
					<tr title="existLoadFileVersionTemplateP">
						<td><input type="radio" name="loadFileVersionId" style="display: none"></input></td>
						<td title="版本号" style="text-align: center;">&nbsp;</td>
						<td title="Hash"></td>
						<td title="加载参数" style="text-align: center;">&nbsp;</td>
						<td title="文件大小" style="text-align: center;">&nbsp;</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<%--选择已上传加载文件版本表单模板结束 --%>
	<%--加载文件信息输入表单模板开始 --%>
	<div id="uploadDivTemplate" style="display: none">
		<form id="" title="loadFile">
			<input type="text" name="tempFileAbsPath" style="display: none" /> <input type="text" name="tempDir" style="display: none" /><input
				type="text" name="type" value="2" style="display: none" /> <input type="text" name="applicationVersionId"
				value="${param.applicationVersionId }" style="display: none" />
			<table class="openw">
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>名称:</th>
					<td></td>
					<td colspan="2"><input class="inputtext validate['required']" name="name" type="text" maxlength="16" /></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>AID:</th>
					<td></td>
					<td colspan="2"><input class="inputtext validate['required','length[10,32]','%checkHex','%checkLoadFileAid']" name="aid"
						type="text" maxlength="32" /></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>备注:</th>
					<td></td>
					<td colspan="2"><textarea class="inputtext validate['required','length[0,80]']" name="comments"></textarea></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>版本号:</th>
					<td></td>
					<td><input class="inputtext validate['required','%checkVersionNo']" name="versionNo" type="text" value="1.0.0" maxlength="8"
						style="width: 100px" /> <span class="explain">格式为x.x.x，x代表0-99的数字</span></td>
				</tr>
				<tr>
					<th style="width: 100px">HASH值:</th>
					<th style="width: 2em">0x</th>
					<td colspan="2"><input class="inputtext validate['%checkHex']" name="hash" type="text" maxlength="20" /></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>所属安全域模式:</th>
					<td></td>
					<td colspan="2"><select name="sdModel">
							<option value=1>主安全域</option>
							<option value=2>代理安全域</option>
							<option value=4>委托安全域</option>
					</select></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>所属安全域:</th>
					<td></td>
					<td colspan="2"><select id="sdId" name="sdId" style="text-align: center;">
					</select></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>加载参数:</th>
					<th style="width: 2em">0x</th>
					<td><input class="inputtext validate['required','%checkHex']" name="loadParams" type="text" value="EF0CC6025DC9C702000AC8020800"
						maxlength="28" /></td>
					<td><a title="点击输入参数" href="#" class="butt2"><span>配置参数</span> </a></td>
				</tr>
				<tr>
					<th style="width: 100px"><span style="color: red">*</span>CAP文件:</th>
					<td></td>
					<td><input class="validate['required']" name="fileName" type="text" readonly="readonly" />
						<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
							<span id="spanButtonPlaceholder"></span>
						</div>
						<div id="divFileProgressContainer" style="height: 75px; display: none"></div>
					</td>
				</tr>
				<tr>
					<td>
						<button id="submitButton" name="submitButton" class="validate['submit']" type="submit" style="display: none">保存</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<%--加载文件信息输入表单模板结束 --%>
	<%--升级加载文件信息输入表单模板开始 --%>
	<div id="loadFileVersionDivTemplate" style="display: none">
		<form title="loadFileVersion" id="">
			<input type="text" name="tempFileAbsPath" style="display: none" /> <input type="text" name="tempDir" style="display: none" /> <input
				type="text" name="applicationVersionId" value="${param.applicationVersionId }" style="display: none" /> <input type="text"
				name="loadFileId" style="display: none" />
			<table class="openw">
				<tr>
					<th><span style="color: red">*</span>版本号:</th>
					<td></td>
					<td colspan="2"><input class="inputtext validate['required','%checkVersionNo']" name="versionNo" type="text" maxlength="8"
						style="width: 100px" /> <span class="explain">格式为x.x.x，x代表0-99的数字</span></td>
				</tr>
				<tr>
					<th>HASH值:</th>
					<th style="width: 2em">0x</th>
					<td colspan="2"><input class="inputtext validate['%checkHex','length[20,20]']" name="hash" type="text" maxlength="20" /></td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>加载参数:</th>
					<th style="width: 2em">0x</th>
					<td><input class="inputtext validate['required','%checkHex']" name="loadParams" type="text" value="EF0CC6025DC9C702000AC8020800"
						maxlength="28" /></td>
					<td><a title="点击输入参数" href="#" class="butt2"><span>配置参数</span> </a></td>
				</tr>
				<tr id="capUploadTr">
					<th><span style="color: red">*</span>CAP文件:</th>
					<td></td>
					<td><input class="validate['required']" name="fileName" type="text" readonly="readonly" />
						<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
							<span id="spanButtonPlaceholder"></span>
						</div>
						<div id="divFileProgressContainer" style="height: 75px; display: none"></div>
					</td>
				</tr>
				<tr id="capUploadedTr" style="display: none">
					<th>CAP文件:</th>
					<td></td>
				</tr>
				<tr>
					<td>
						<button id="submitButton" name="submitButton" class="validate['submit']" type="submit" style="display: none">保存</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<%--升级加载文件信息输入表单模板结束 --%>
	<%--加载参数输入表单模板开始 --%>
	<div id="loadParamsFormDivTemplate" style="display: none">
		<form title="loadParams" id="">
			<table class="openw">
				<tr>
					<th style="width: 130px;"><span style="color: red">*</span>不可变编码空间(byte):</th>
					<td><input class="inputtext validate['required','%chkNumber','digit[1,65535]']" name="nonVolatileCodeSpace" type="text"
						maxlength="5" /></td>
				</tr>
				<tr>
					<th style="width: 130px;"><span style="color: red">*</span>可变数据空间(byte):</th>
					<td><input class="inputtext validate['required','%chkNumber','digit[0,65535]']" name="volatileDateSpace" type="text" maxlength="5" />
					</td>
				</tr>
				<tr>
					<th style="width: 130px;"><span style="color: red">*</span>不可变数据空间(byte):</th>
					<td><input class="inputtext validate['required','%chkNumber','digit[0,65535]']" name="nonVolatileDateSpace" type="text"
						maxlength="5" /></td>
				</tr>
				<tr>
					<td>
						<button id="submitButton" name="submitButton" class="validate['submit']" type="submit" style="display: none">保存</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<%--加载参数输入表单模板结束 --%>
</body>
</html>