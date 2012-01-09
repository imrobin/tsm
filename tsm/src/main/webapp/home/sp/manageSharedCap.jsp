<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal" var="user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>管理共享加载文件</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers.js"></script>
<script src="${ctx}/home/sp/js/manageSharedCap.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/customerCheck.js" type="text/javascript"></script>
<style type="text/css">
.myTable {
	width: 100%;
	border-collapse: collapse;
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 14px;
}

.myTable td {
	border: 1px solid #cccccc;
	text-align: left;
	padding: 3px;
}

.myTable th {
	border: 1px solid #cccccc;
	text-align: right;
	width: 150px;
	padding: 3px;
	background-color: #F0FFFF;
}
</style>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = '${ctx}';
	var spreadIcon = ctx + "/lib/menu/icon_7.png";
	var hiddenIcon = ctx + "/lib/menu/icon_6.png";
	var spId = "${user.id }";

	window.addEvent("domready", function() {
		var page = new Application.Page();

		$("uploadButt").addEvent("click", function(event) {
			event.stop();
			new Application.LoadFile({
				page : page
			}).createNewLoadFile();
		});
		page.init();
	});
</script>
</head>

<body>
<div id="container"><%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理共享文件</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%></div>

<div class="userinput">
<div class="opertop">
<p class="oper1"><a class="butt2" href="#" id="uploadButt"><span><img src="${ctx }/images/uploadIcon.png" class="icon16" />上传共享文件</span>
</a></p>
</div>
<div id="uploadedCapsInfo" class="list_sj"></div>

<div id="userinput" style=""></div>

</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%></div>
</div>

<%--加载文件信息输入表单模板开始 --%>
<div id="uploadDivTemplate" style="display: none">
<form title="loadFile"><input type="text" name="tempFileAbsPath" style="display: none" /> <input type="text" name="tempDir"
	style="display: none" /> <input type="text" name="applicationVersionId" value="${param.applicationVersionId }" style="display: none" />
<table class="openw">
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>名称:</th>
		<td></td>
		<td colspan="2"><input class="inputtext validate['required']" name="name" type="text" maxlength="16" /></td>
	</tr>
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>AID:</th>
		<td></td>
		<td colspan="2"><input class="inputtext validate['required','length[10,32]','%checkHex','%checkLoadFileAid']" name="aid" type="text"
			maxlength="32" /></td>
	</tr>
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>备注:</th>
		<td></td>
		<td colspan="2"><textarea class="inputtext validate['required','length[0,80]']" name="comments"></textarea></td>
	</tr>
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>版本号:</th>
		<td></td>
		<td colspan="2"><input class="inputtext validate['required','%checkVersionNo']" name="versionNo" type="text" value="1.0.0"
			maxlength="8" style="width: 100px" /><span class="explain">格式为x.x.x，x代表0-99的数字</span></td>
	</tr>
	<tr>
		<th style="width: 110px">HASH值:</th>
		<th style="width: 2em">0x</th>
		<td colspan="2"><input class="inputtext" name="hash" type="text" maxlength="20" /></td>
	</tr>
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>所属安全域模式:</th>
		<td></td>
		<td colspan="2"><select name="sdModel">
			<option value=1>主安全域</option>
			<option selected="selected" value=2>公共第三方安全域</option>
			<option value=3>DAP模式</option>
			<option value=4>Token模式</option>
		</select></td>
	</tr>
	<tr>
		<th style="width: 110px">所属安全域:</th>
		<td></td>
		<td colspan="2"><select id="sdId" name="sdId" style="text-align: center;">
		</select></td>
	</tr>
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>加载参数:</th>
		<th style="width: 2em">0x</th>
		<td><input class="inputtext validate['required','%checkHex']" name="loadParams" type="text" value="EF0CC6025DC9C702000AC8020800"
			maxlength="28" /></td>
		<td><a title="点击输入参数" href="#" class="butt2"><span>配置参数</span> </a></td>
	</tr>
	<tr id="capUploadTr">
		<th style="width: 110px"><span style="color: red">*</span>CAP文件:</th>
		<td></td>
		<td><input class="validate['required']" name="fileName" type="text" readonly="readonly" />
		<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;"><span id="spanButtonPlaceholder"></span></div>
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
<form title="loadFileVersion"><input type="text" name="tempFileAbsPath" style="display: none" /> <input type="text" name="tempDir"
	style="display: none" /> <input type="text" name="loadFileId" style="display: none" />
<table class="openw">
	<tr>
		<th><span style="color: red">*</span>版本号:</th>
		<td></td>
		<td colspan="2"><input class="inputtext validate['required','%checkVersionNo']" name="versionNo" type="text" value="1.0.0"
			maxlength="8" style="width: 100px" /><span class="explain">格式为x.x.x，x代表0-99的数字</span></td>
	</tr>
	<tr>
		<th>HASH值:</th>
		<th style="width: 2em">0x</th>
		<td colspan="2"><input class="inputtext" name="hash" type="text" maxlength="20" /></td>
	</tr>
	<tr>
		<th><span style="color: red">*</span>加载参数:</th>
		<th style="width: 2em">0x</th>
		<td><input class="inputtext validate['required','%checkHex']" name="loadParams" type="text" value="EF0CC6025DC9C702000AC8020800"
			maxlength="28" /></td>
		<td><a href="#" class="butt2" title="点击输入参数"><span>配置参数</span> </a></td>
	</tr>
	<tr>
		<th style="width: 110px"><span style="color: red">*</span>CAP文件:</th>
		<td></td>
		<td><input class="validate['required']" name="fileName" type="text" readonly="readonly" />
		<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;"><span id="spanButtonPlaceholder"></span></div>
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
<%--升级加载文件信息输入表单模板结束 --%>
<%--加载参数输入表单模板开始 --%>
<div id="loadParamsFormDivTemplate" style="display: none">
<form title="loadParams">
<table class="openw">
	<tr>
		<th style="width: 130px"><span style="color: red">*</span>不可变编码空间(byte):</th>
		<td><input class="inputtext validate['required','digit[1,65535]']" name="nonVolatileCodeSpace" type="text" maxlength="5" /></td>
	</tr>
	<tr>
		<th style="width: 130px"><span style="color: red">*</span>可变数据空间(byte):</th>
		<td><input class="inputtext validate['required','digit[0,65535]']" name="volatileDateSpace" type="text" maxlength="5" /></td>
	</tr>
	<tr>
		<th style="width: 130px"><span style="color: red">*</span>不可变数据空间(byte):</th>
		<td><input class="inputtext validate['required','digit[0,65535]']" name="nonVolatileDateSpace" type="text" maxlength="5" /></td>
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
<%--加载文件信息显示模板开始 --%>
<div id="loadFileTemplateDiv" style="display: none">
<div title="loadFileTemplateDiv" class="list_1">
<p id="loadFileTemplateP"><img title="icon" style="cursor: pointer;"></img> <span title="ID" style="display: none"></span> <span
	title="名称"></span>(<span title="AID"></span>) <span title="操作">
<button title="更新"><span>&nbsp;更 新&nbsp;</span></button>
<button title="删除"><span>&nbsp;删 除&nbsp;</span></button>
</span></p>
<div id="loadFileDetailsAndVersions" class="list_2">
<table class="myTable">
	<tr>
		<td>
		<p class="list_2"><img title="detailsIcon" style="cursor: pointer;"></img> <span>详细信息</span></p>
		<div title="loadFileDetails" class="list_3">
		<table class="myTable">
			<tr>
				<td style="text-align: right; width: 175px">备注:</td>
				<td style="text-indent: 0em;"><span title="备注" style="width: 200px;" class="texthidden"></span></td>
			</tr>
			<tr>
				<td style="text-align: right; width: 200px">所属安全域模式:</td>
				<td style="text-indent: 0em;"><span title="所属安全域模式"></span></td>
			</tr>
			<tr>
				<td style="text-align: right;">所属安全域:</td>
				<td style="text-indent: 0em;"><span title="所属安全域"><span class="explain">未指定</span> </span></td>
			</tr>
		</table>
		</div>
		<p class="list_2"><img title="modulesIcon" style="cursor: pointer;"></img> <span>版本信息</span></p>
		<div title="versions" class="list_3"></div>
		</td>
	</tr>
</table>
</div>
</div>
</div>
<%--加载文件信息显示模板结束 --%>
<%--加载文件版本信息显示模板开始 --%>
<div id="loadFileVersionTemplateDiv" style="display: none">
<p id="loadFileVersionTemplateP"><img title="icon" style="cursor: pointer;"></img> <span title="ID" style="display: none"></span>版本号:<span
	title="版本号"></span><span title="操作">
<button title="创建模块" style="text-indent: 0em">创建模块</button>
<button title="添加依赖" style="text-indent: 0em">添加依赖</button>
</span></p>
<div id="loadFileVersionDetailsAndLoadModuules">
<table class="myTable">
	<tr>
		<td>
		<p class="list_4"><img title="detailsIcon" style="cursor: pointer;"></img> <span>详细信息</span></p>
		<div title="loadFileVersionDetails">
		<table class="myTable">
			<tr>
				<td style="text-align: right;">Hash值:</td>
				<td style="text-indent: 0em;"><span title="hash"></span></td>
			</tr>
			<tr>
				<td style="text-align: right;">加载参数:</td>
				<td style="text-indent: 0em;"><span title="加载参数"></span></td>
			</tr>
			<tr>
				<td style="text-align: right; width: 175px">文件大小:</td>
				<td style="text-indent: 0em;"><span title="文件大小"></span><span>byte</span></td>
			</tr>
		</table>
		</div>
		<p class="list_4"><img title="modulesIcon" style="cursor: pointer;"></img> <span>模块信息</span></p>
		<div title="loadModules" class="list_5"></div>
		<p class="list_4"><img title="dependenciesIcon" style="cursor: pointer;"></img> <span>依赖信息</span></p>
		<div title="loadFileVersionDependencies" class="list_5"></div>
		</td>
	</tr>
</table>
</div>
</div>
<%--加载文件版本信息显示模板结束 --%>
<%--加载文件版本依赖信息显示模板开始 --%>
<div id="loadFileVersionDependenceTemplateDiv" style="display: none">
<p id="loadFileVersionDependenceTemplate"><span title="ID" style="display: none"></span><span title="名称"></span>(<span title="AID"></span>)
<span title="版本号"></span><span title="操作">
<button title="移除依赖" style="text-indent: 0em">移除依赖</button>
</span></p>
</div>
<%--加载文件版本依赖信息显示模板结束 --%>
<%--模块信息输入表单模板开始 --%>
<div id="loadModuleFormTemplateDiv" style="display: none">
<form id="" title="loadModule"><input type="text" name="loadFileVersionId" style="display: none" />
<table class="openw">
	<tr>
		<th><span style="color: red">*</span>名称:</th>
		<td><input class="inputtext validate['required']" name="name" type="text" maxlength="50" /></td>
	</tr>
	<tr>
		<th><span style="color: red">*</span>AID:</th>
		<td><input class="inputtext validate['required','length[10,32]','%checkHex']" maxlength="32" name="aid" type="text" /></td>
	</tr>
	<tr>
		<th><span style="color: red">*</span>备注:</th>
		<td><textarea class="inputtext validate['required','length[0,80]']" name="comments"></textarea></td>
	</tr>
	<tr>
		<td>
		<button id="submitButton" name="submitButton" class="validate['submit']" type="submit" style="display: none">保存</button>
		</td>
	</tr>
</table>
</form>
</div>
<%--模块信息输入表单模板结束 --%>
<%--模块信息显示模板开始 --%>
<div id="loadModuleTemplateDiv" style="display: none">
<table class="myTable">
	<tr>
		<td>
		<p id="loadModuleTemplateP" class="list_5"><img title="icon"></img> <span title="ID" style="display: none"></span> <span title="名称"></span>(<span
			title="AID"></span>)<span title="操作">
		<button title="删除" style="text-indent: 0em">删除</button>
		</span></p>
		<div id="loadModuleDetails" class="list_6">
		<table class="myTable">
			<tr>
				<td style="text-align: right; width: 175px">备注:</td>
				<td style="text-indent: 0em;"><span title="备注" style="width: 200px;" class="texthidden"></span></td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>
</div>
<%--模块信息显示模板结束 --%>
<%--选择安全域模板开始 --%>
<select style="display: none" id="sdSelectTemplate">
	<option title="disableTemplate" style="width: 300px;">请选择</option>
	<option title="enableTemplate" style="width: 300px; text-align: left;"></option>
</select>
<%--选择安全域模板结束 --%>
<%--显示未依赖加载文件模板开始 --%>
<div id="loadFileTemplateTable" style="display: none">
<table class="openw">
	<thead>
		<tr>
			<th style="display: none">ID</th>
			<th style="width: 35%; text-align: center;">名称</th>
			<th style="width: 35%; text-align: center;">AID</th>
			<!-- <th style="width: 55%; text-align: center;">备注</th> -->
			<th style="width: 30%; text-align: center;">操作</th>
		</tr>
		<tr id="loadFileTemplateTr" style="display: none">
			<td title="ID" style="display: none">&nbsp;</td>
			<td title="名称" style="width: 35%; text-align: center;">&nbsp;</td>
			<td title="AID" style="width: 35%; text-align: center;">&nbsp;</td>
			<!-- <td title="备注" style="width: 35%;text-align: center;">&nbsp;</td> -->
			<td title="操作" style="text-align: center; width: 30%;"><a title="选择版本" href="#" class="butt3"><span>选择版本</span> </a></td>
		</tr>
	</thead>
	<tbody id="loadFiles"></tbody>
</table>
</div>
<%--显示未依赖加载文件模板结束 --%>
<%--选择依赖加载文件版本表单模板开始 --%>
<div id="undependenentLoadFileVersionFormTemlate" style="display: none">
<div title="undependenentLoadFileVersionDiv">
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
	<tbody title="undependenentLoadFileVersions">
		<tr>
			<td><input type="text" name="childLoadFileVersionId" style="display: none" /></td>
		</tr>
	</tbody>
</table>
<table>
	<tbody>
		<tr title="undependenentLoadFileVersionsTemplate">
			<td><input type="radio" name="parentLoadFileVersionId" style="display: none"></input></td>
			<td title="版本号" style="width: 10%; text-align: center;">&nbsp;</td>
			<td title="Hash" style="width: 20%; text-align: center;"></td>
			<td title="加载参数" style="width: 30%; text-align: center;">&nbsp;</td>
			<td title="文件大小" style="width: 15%; text-align: center;">&nbsp;</td>
		</tr>
	</tbody>
</table>
</div>
</div>
<%--选择依赖加载文件版本表单模板结束 --%>
</body>
</html>