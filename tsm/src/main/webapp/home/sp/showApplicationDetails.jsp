<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%><%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ include file="/common/taglibs.jsp"%><%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal" var="user" />
<c:if test="${user != 'anonymousUser'}">
</c:if>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用详情</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/commons/CityPicker.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.Request.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js"
	type="text/javascript"></script>
<link href="${ctx}/lib/uploadManager/default.css" rel="stylesheet"
	type="text/css" />
<script type="text/javascript"
	src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/uploadManager/handlers.js"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script src="${ctx}/home/sp/js/showApplicationDetails.js"
	type="text/javascript"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx }";
	var applicationId = "${param.applicationId}";
	var details = null;
	window.addEvent("domready", function() {
		details = new Application.Details();
		details.init();
	});
	function list() {
		details.getApplictionVersions();
	}
</script>
<style type="text/css">
#applicationDetails td {
	word-break: break-all;
	word-wrap: break-word;
}

.myTable {
	table-layout: fixed;
	width: 740px;
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}

.myTable td {
	border: 1px solid #cccccc;
	text-align: left;
	padding: 3px;
	overflow: auto;
	max-width: 590px;
}

.myTable th {
	border: 1px solid #cccccc;
	text-align: right;
	width: 150px;
	padding: 3px;
	background-color: #F0FFFF;
}
</style>
</head>

<body>
	<div id="container"><%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 应用管理</div>

		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%>
					</div>
					<div class="userinput">
						<div class="upapptext">应用详细信息：</div>
					</div>
					<div class="userinput">
						<table id="applicationDetails" class="myTable">
							<tr>
								<th>应用名称：</th>
								<td><span title="应用名称"></span></td>
							</tr>
							<tr>
								<th>应用类型：</th>
								<td><span title="应用类型"></span>
								</td>
							</tr>
							<tr>
								<th>应用AID：</th>
								<td><span title="AID"></span>
								</td>
							</tr>
							<tr>
								<th>应用描述：</th>
								<td><span title="应用描述"></span>
								</td>
							</tr>
							<tr>
								<th>业务类型：</th>
								<td><span title="业务类型"></span>
								</td>
							</tr>
							<tr>
								<th>个人化类型：</th>
								<td><span title="个人化类型"></span></td>
							</tr>
							<tr>
								<th>个人化指令传输加密算法：</th>
								<td><span title="个人化指令传输加密算法"></span></td>
							</tr>
							<tr>
								<th>个人化指令敏感数据加密算法：</th>
								<td><span title="个人化指令敏感数据加密算法"></span></td>
							</tr>
							<tr>
								<th>是否需要订购：</th>
								<td><span title="是否需要订购"></span>
								</td>
							</tr>
							<tr>
								<th>预置收费条件：</th>
								<td><span title="预置时收费条件"></span>
								</td>
							</tr>
							<tr>
								<th>所属安全域模式：</th>
								<td><span title="所属安全域模式"></span></td>
							</tr>
							<tr style="">
								<th>所属安全域：</th>
								<td><span title="所属安全域"></span></td>
							</tr>
							<tr>
								<th>业务平台URL：</th>
								<td><span title="业务平台URL"></span></td>
							</tr>
							<tr>
								<th>业务平台服务名：</th>
								<td><span title="业务平台服务名"></span></td>
							</tr>
							<tr>
								<th>删除规则：</th>
								<td><span title="删除规则"></span></td>
							</tr>
							<tr>
								<th>所在地：</th>
								<td><span title="所在地"></span></td>
							</tr>
							<tr id="pcIconUpload">
								<th>PC版图标：</th>
								<td><img title="PC版图标" style="width: 128px; height: 128px"
									src=""></img></td>
							</tr>
							<tr id="mobileIconUpload">
								<th>手机版图标：</th>
								<td><img title="手机版图标" style="width: 50px; height: 50px"
									src=""></img></td>
							</tr>
							<tr>
								<th>应用截图：</th>
								<td><span title="应用截图"></span>
								</td>
							</tr>
						</table>
						<div class="minfo">
							<table style="width: 100%">
								<thead>
									<tr>
										<th colspan="5" align="left">版本号：<input type="text"
											style="margin-bottom: 0px; padding: 0px; border-width: thin;"
											name="appVersionNo" />&nbsp;&nbsp;<a id="search_button"
											href="JavaScript:list();" class="buts">查询</a>
										</th>
									</tr>
								</thead>
								<thead>
									<tr>
										<th style="width: 10%">版本号</th>
										<th style="width: 16%">内存空间(byte)</th>
										<th style="width: 16%">存储空间(byte)</th>
										<th style="width: 14%">状态</th>
										<th style="width: 44%">操作</th>
									</tr>
								</thead>
								<tbody id="versionsTbody"></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>

	<select style="display: none" id="sdSelectTemplate">
		<option title="disableTemplate" style="width: 300px;">请选择</option>
		<option title="enableTemplate" style="width: 300px; text-align: left;"></option>
	</select>
	<table id="versionsInfo" style="display: none">
		<tbody>
			<tr title="versionsInfoTr">
				<td title="版本号" style="width: 10%; text-align: center;">&nbsp;</td>
				<td title="内存空间" style="width: 16%; text-align: center;">&nbsp;</td>
				<td title="存储空间" style="width: 16%; text-align: center;">&nbsp;</td>
				<td title="状态" style="width: 14%; text-align: center;">&nbsp;</td>
				<td title="操作" style="width: 44%; text-align: center;"><a
					title="管理客户端" href="#" class="butt2" style="float: none;"><span>管理客户端</span>
				</a><a title="查看" href="#" class="butt2" style="float: none;"><span>查看</span>
				</a><a title="测试" href="#" class="butt2"
					style="float: none; display: none"><span>测试</span> </a><a
					title="修改" href="#" class="butt2" style="float: none;"><span>修改</span>
				</a><a title="删除" href="#" class="butt2" style="float: none;"><span>删除</span>
				</a><a title="归档" href="#" class="butt2" style="float: none;"><span>归档</span>
				</a></td>
			</tr>
		</tbody>
		<tbody id="versionsTbody"></tbody>
	</table>
	<!--
	      此处为上传文件和信息的form
	-->
	<div class="userinput" style="display: none" id="uploadForm">
		<form id="appverForm" action="${ctx}/html/testfile/?m=finishUpload">
			<table>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>文件介绍:
						</p></td>
					<td>
						<p class="left inputs">
							<input id="fileCommons" style="width: 335px"
								class="inputtext validate['required']" name="comment"
								type="text" maxlength="200" />
						</p></td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>上传文件:
						</p></td>
					<td>
						<p class="left inputs" id='uploadP'>
							<input name="testFileOrgName" id="tetsFileName" type="text"
								class="inputtext validate['required']" readonly="readonly" />
							<div
								style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
								<span id="spanButtonPlaceholder"></span>
							</div>
							<div id="divFileProgressContainer"
								style="height: 75px; display: none;"></div>
						</p> <input name="appverId" id="appverId" type="hidden"
						class="inputtext validate['required']" readonly="readonly" /> <input
						name="tempFileName" id="tempFilename" type="hidden"
						class="inputtext validate['required']" readonly="readonly" />
						<button class="validate['submit']" style="display: none;"></button>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>