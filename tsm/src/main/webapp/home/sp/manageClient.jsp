<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>管理应用客户端</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" />
<script src="${ctx}/lib/formcheck/customerCheck.js" type="text/javascript"></script>
<style type="text/css">
#next {
	float: right;
}

a .subbutt {
	height: 20px;
}

.butt2 {
	height: 20px;
	background: url(../images/butt_bg.gif);
	border: 0px;
	padding-left: 18px;
	float: center;
	display: block;
	font-size: 14px;
	color: #000;
	text-decoration: none;
	cursor: pointer;
}

.myTable {
	table-layout: fixed;
	width: 100%;
	border-collapse: collapse;
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}

.myTable td {
	word-break: break-all;
	word-wrap: break-word;
	border: 1px solid #cccccc;
	text-align: center;
	padding: 3px;
	max-width: 590px;
}

.myTable th {
	border: 1px solid #cccccc;
	text-align: center;
	width: 200px;
	padding: 3px;
	background-color: #F0FFFF;
}
*
/
</style>
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/security/js/login.js" type="text/javascript"></script>
<link href="${ctx}/lib/uploadManager/default.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers.js"></script>
<script src="${ctx}/admin/sd/js/sd.js" type="text/javascript"></script>
<script src="${ctx}/home/sp/js/uploadClient.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript"></script>
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx}";
	var applicationVersionId = "${param.applicationVersionId }";

	window.addEvent("domready", function() {
		var page = new Application.Page({
			applicationVersionId : "${param.applicationVersionId }"
		});

		page.getCilents();

		$("uploadClientButt").addEvent("click", function(event) {
			event.stop();
			new Application.Client({
				applicationVersionId : "${param.applicationVersionId }"
			}).uploadClient(ctx);
		});

		var client = new Application.Client({
			page : page,
			applicationVersionId : "${param.applicationVersionId }"
		});

	});
</script>
</head>

<body>
	<div id="container"><%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理应用客户端</div>

		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%></div>

					<div class="userinput">
						<div class="minfo">
							<div class="opertop">
								<p class="oper1">
									<a class="butt2" href="#" id="uploadClientButt"><span><img src="${ctx }/images/uploadIcon.png" class="icon16" />上传客户端</span> </a>
								</p>
							</div>
							<table class="myTable" style="width: 100%">
								<thead>
									<tr>
										<th colspan="10">已上传客户端</th>
									</tr>
									<tr>
										<th>名称</th>
										<th>版本</th>
										<th>开发版本</th>
										<th>客户端包名</th>
										<th>客户端入口类</th>
										<th>系统类型</th>
										<th>系统版本</th>
										<th>下载地址</th>
										<th>文件大小<br /> (byte)</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody id="client"></tbody>
								<tfoot>
									<tr>
										<td colspan="10"><div id="paging" align="right"></div>
										</td>
									</tr>
								</tfoot>
							</table>

						</div>

						<p id="userinput_t" class="userinput_t"></p>
						<div id="userinput" style=""></div>

					</div>

				</div>

			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>

	<%--应用客户端表单模板开始 --%>
	<div id="clientFormTemlate" style="display: none;">
		<form id="" title="client">
			<input type="text" name="applicationVersionId" style="display: none" /> <input type="text" name="tempFileAbsPath" style="display: none" />
			<input type="text" name="tempDir" style="display: none" />
			<table class="openw">
				<tr>
					<th><span style="color: red">*</span>客户端文件:</th>
					<td><input class="validate['required']" name="fileName" type="text" readonly="readonly" />
						<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
							<span id="spanButtonPlaceholder"></span>
						</div>
						<div id="divFileProgressContainer" style="height: 75px; display: none"></div>
					</td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>名称:</th>
					<td><input class="inputtext validate['required']" name="name" type="text" value="" maxlength="16" /></td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>系统类型:</th>
					<td><select name="sysType">
							<option value="os">基于手机操作系统</option>
							<option value="j2me">基于J2ME</option>
					</select></td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>系统版本:</th>
					<td><select id="sysRequirment" name="sysRequirment">
					</select></td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>版本:</th>
					<td><input class="inputtext validate['required','%checkVersionNo']" name="version" type="text" value="1.0.0" maxlength="8"
						style="width: 100px" /><span class="explain">格式为x.x.x，x代表0-99的数字</span></td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>开发版本:</th>
					<td><input id="versionCode" class="inputtext validate['required','digit[1,99999]']" name="versionCode" type="text" maxlength="5"></input>
					</td>
				</tr>
				<tr>
					<th><span style='color: red;'>*</span>客户端包名:</th>
					<td><textarea id="clientPackageName" class="validate['required','length[1,256]']" cols="60" rows="2" name="clientPackageName"></textarea>
					</td>
				</tr>
				<tr>
					<th><span style='color: red;'>*</span>客户端入口类:</th>
					<td><textarea id="clientClassName" class="validate['required','length[1,256]']" cols="60" rows="2" name="clientClassName"></textarea>
					</td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>图标:</th>
					<td><input class="validate['required']" name="iconName" type="text" readonly="readonly" />
						<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
							<span id="spanIconPlaceholder"></span>
						</div>
						<div id="divIconProgressContainer" style="height: 75px; display: none"></div>
					</td>
				</tr>
				<tr>
					<th></th>
					<th style="display: none"><input name="tempIconAbsPath" type="text"></input><input name="tempIconDir" type="text"></input></th>
					<td><img id="iconImg" width="128px" height="128px" style="display: none"></img></td>
				</tr>
				<tr>
					<td>
						<button id="submitButton" name="submitButton" class="validate['submit']" type="submit" style="display: none">保存</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<%--应用客户端表单模板结束 --%>
	<%--显示应用客户端模板开始 --%>
	<table class="myTable" style="width: 100%">
		<tbody>
			<tr id="clientInfoTemplate" style="display: none;">
				<td title="名称" style="width: 15%; text-align: center;"><span class="texthidden">&nbsp;</span></td>
				<td title="版本" style="width: 5%; text-align: center;">&nbsp;</td>
				<td title="开发版本" style="width: 5%; text-align: center;">&nbsp;</td>
				<td title="客户端包名" style="width: 10%; text-align: center;">&nbsp;</td>
				<td title="客户端入口类" style="width: 10%; text-align: center;">&nbsp;</td>
				<td title="系统类型" style="width: 10%; text-align: center;">&nbsp;</td>
				<td title="系统版本" style="width: 12%; text-align: center;">&nbsp;</td>
				<td><a title="下载地址" class="b">&nbsp;</a></td>
				<td title="文件大小" style="text-align: center;">&nbsp;</td>
				<td title="操作" style="width: 5%; text-align: center;"><a title="删除" href="#" class="butt2" style="float: none;"><span>删除</span>
				</a></td>
			</tr>
		</tbody>
	</table>
</body>
<%--显示应用客户端模板开结束 --%>
</html>