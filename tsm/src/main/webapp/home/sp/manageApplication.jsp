<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal" var="user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用管理</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css"
	rel="stylesheet" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />	
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/customerCheck.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/admin/sd/js/sd.js" type="text/javascript"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>	
<script src="${ctx}/home/sp/js/manageApplication.js"
	type="text/javascript"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var spId = '${user.id }';
	var application = null;
	window.addEvent("domready", function() {
		application = new Application();
		application.getApplications();
	});
	function list() {
		application.getApplications();
	}
</script>
</head>

<body>
	<div id="container"><%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;我的主页&gt;应用管理</div>

		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%>
					</div>

					<div class="userinput">
						<div class="minfo">
							<table style="width: 100%;">
								<thead>
									<tr>
										<th colspan="4" align="left"  style="border-bottom: 0px;">应用名称：<input type="text" style="border-width: thin;" name="appName"></input>&nbsp;&nbsp;<a id="search_button" href="JavaScript:list();" class="buts">查询</a></th>
									</tr>
								</thead>
							</table>
							<div id="applicationList"></div>
						</div>

					</div>

				</div>
		        <div id="sdnextpage" align="right"></div>

			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>
	<div id="applicationListTemplate" style="display: none">
		<table id="applicationTableHeader" style="width: 100%">
			<thead>
				<tr>
					<th>应用名称</th>
					<th width="250px">应用AID</th>
					<th width="80px">状态</th>
					<th width="150px">操作</th>
				</tr>
			</thead>
			<tbody title="applicationList"></tbody>
		</table>
		<table id="applicationTableBody">
			<tbody>
				<tr id="applicationTableRow">
					<td title="名称" style="text-align: center;word-break: break-all;word-wrap: break-word;max-width:180px;"></td>
					<td title="AID" style="text-align: center;word-break: break-all;word-wrap: break-word;max-width:180px;"></td>
					<td title="状态" style="text-align: center;"></td>
					<td title="操作" style="text-align: center;"><a
						title="查看" href="#" class="butt2" style="float: none;"><span>查看</span>
					</a> <a title="修改" href="#" class="butt2" style="float: none;"><span>修改</span>
					</a> <a id='create' title="创建" href="#" class="butt2"
						style="float: none;"><span>创建新版本</span>
					</a><a id='create' title="删除" href="#" class="butt2"
						style="float: none;"><span>删除</span>
					</a>
					</td>
				</tr>
			</tbody>
		</table>
		<div id="createVersionDiv" style="display: none">
			<form id="" title="createVersionForm" method="post">
				<button class="validate['submit']" style="display: none;"></button>
				<table class="openw">
					<tr>
						<td class="td1"><span style='color: red;'>*</span>请输入版本号:&nbsp;</td>
						<td><input id="version"
							class="inputtext validate['required','%checkVersionNo']"
							name="version" type="text" value="1.0.0" maxlength="8" /><span
							class="explain">格式为x.x.x，x代表0-99的数字</span>
						</td>
					</tr>
					<tr>
						<td><input type="hidden" id="applicationId"
							name="applicationId" value=""></input></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>

</html>