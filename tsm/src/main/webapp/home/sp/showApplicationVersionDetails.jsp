<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http: //www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用上传</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="${ctx}/lib/lightface/assets/LightFace.css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet"
	type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/security/js/login.js" type="text/javascript"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js"
	type="text/javascript"></script>
<script src="${ctx}/home/sp/js/showApplicationVersionDetails.js"
	type="text/javascript"></script>
<script src="${ctx}/admin/sd/js/sd.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx}";
	var spreadIcon = ctx + "/lib/menu/icon_7.png";
	var hiddenIcon = ctx + "/lib/menu/icon_6.png";

	function swichTab(index) {
		for ( var i = 0; i < 6; i++) {
			$("tab" + (i + 1)).removeClass("s1").addClass("s2");
			$("tabC" + (i + 1)).setStyle("display", "none");
		}

		$("tab" + (index)).removeClass("s2").addClass("s1");
		$("tabC" + (index)).setStyle("display", "");

	}

	window.addEvent("domready", function() {
		var page = new Application.Page({
			applicationVersionId : "${param.applicationVersionId }"
		});
		page.init();
	});
</script>
<style type="text/css">
table {
	table-layout: fixed;
	border-collapse: collapse;
}

td {
	word-break: break-all;
	word-wrap: break-word;
}

.myTable {
	width: 100%;
	border-collapse: collapse;
	font-family: "微软雅黑", "宋体", Arial, sans-serif;
	font-size: 12px;
}

.myTable td {
	border: 1px solid #cccccc;
	text-align: left;
	padding: 3px;
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
		<div class="curPosition">您的位置: 上传应用</div>
		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%></div>
					<div class="userinput">
						<div class="titletab">
							<ul>
								<li class="s1" id="tab1"><a href="javascript:swichTab(1)">应用详情</a>
								</li>
								<li class="s2" id="tab2"><a href="javascript:swichTab(2)">应用结构</a>
								</li>
								<li class="s2" id="tab3"><a href="javascript:swichTab(3)">客户端信息</a>
								</li>
								<li class="s2" id="tab4"><a href="javascript:swichTab(4)">顺序信息</a>
								</li>
								<li class="s2" id="tab5"><a href="javascript:swichTab(5)">应用测试文件</a>
								</li>
								<li class="s2" id="tab6"><a href="javascript:swichTab(6)">应用测试结果</a>
								</li>
							</ul>
						</div>
						<!-- 详情开始 -->
						<div id="tabC1">
							<div id="applicationDetails"></div>
						</div>
						<!-- 详情结束  -->
						<!-- 结构开始 -->
						<div id="tabC2" style="display: none">
							<table id="uploadedCapsInfo" style="width: 100%;">
								<tbody>
								</tbody>
							</table>
						</div>
						<!-- 结构结束  -->
						<!-- 客户端信息开始 -->
						<div id="tabC3" class="minfo" style="display: none">
							<table style="width: 100%">
								<thead>
									<tr>
										<th colspan="8">客户端信息</th>
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
										<th>文件大小<br />(byte)</th>
									</tr>
								</thead>
								<tbody id="client"></tbody>
							</table>
						</div>
						<!-- 客户端信息结束 -->
						<!-- 顺序开始 -->
						<div id="tabC4" class="minfo" style="display: none">
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
									</table>
								</div>
							</div>
							<!-- 下载顺序完成  -->
							<!-- 删除顺序开始 -->
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
									</table>
								</div>
							</div>
							<!--安装顺序完成 -->
						</div>
						<!-- 顺序完成 -->
						<!-- 测试文件列表 -->
						<div id="tabC5" class="minfo" style="display: none">
							<div id="grid"></div>
							<div id="nextpage" align="right"></div>
							<div id="tfInfo" align="right"></div>
						</div>
						<!-- 测试文件列表结束 -->
						<!-- 测试报告列表 -->
						<div id="tabC6" class="minfo" style="display: none">
							<div id="testReport"></div>
							<div id="trNextPage" align="right"></div>
							<div id="trInfo" align="right"></div>
						</div>
						<!-- <div id="tabC6" class="minfo" style="display: none">
							<div id="reportGrid"></div>
							<div id="reportnextpage" align="right"></div>
						</div> -->
						<!-- 测试报告结束 -->
					</div>
				</div>
				<%@ include file="/common/footer.jsp"%></div>
		</div>
	</div>

	<%--应用详情显示模板介绍 --%>
	<div style="display: none">
		<table id="applicationDetailsTemplate" class="myTable">
			<tr>
				<th>应用名称:</th>
				<td><span title="应用名称"></span>
				</td>
			</tr>
			<tr>
				<th>应用类型:</th>
				<td><span title="应用类型"></span>
				</td>
			</tr>
			<tr>
				<th>应用AID:</th>
				<td><span title="AID"></span>
				</td>
			</tr>
			<tr>
				<th>应用描述:</th>
				<td style="text-align: left"><span title="应用描述"></span>
				</td>
			</tr>
			<tr>
				<th>业务类型:</th>
				<td><span title="业务类型"></span>
				</td>
			</tr>
			<tr>
				<th>个人化类型:</th>
				<td><span title="个人化类型"></span>
				</td>
			</tr>
			<tr>
				<th>个人化指令传输加密算法:</th>
				<td><span title="个人化指令传输加密算法"></span></td>
			</tr>
			<tr>
				<th>个人化指令敏感数据加密算法:</th>
				<td><span title="个人化指令敏感数据加密算法"></span></td>
			</tr>
			<tr>
				<th>是否需要订购:</th>
				<td><span title="是否需要订购"></span></td>
			</tr>
			<tr>
				<th>预置收费条件:</th>
				<td><span title="预置时收费条件"></span>
				</td>
			</tr>
			<tr>
				<th>所属安全域模式:</th>
				<td><span title="所属安全域模式"></span>
				</td>
			</tr>
			<tr style="diplay: none">
				<th>所属安全域:</th>
				<td><span title="所属安全域"></span>
				</td>
			</tr>
			<tr>
				<th>业务平台URL:</th>
				<td><span title="URL"></span>
				</td>
			</tr>
			<tr>
				<th>业务平台服务名:</th>
				<td><span title="业务平台服务名"></span>
				</td>
			</tr>
			<tr>
				<th>删除规则:</th>
				<td><span title="删除规则"></span>
				</td>
			</tr>
			<tr>
				<th>所在地:</th>
				<td><span title="所在地"></span>
				</td>
			</tr>
			<tr>
				<th>PC版图标:</th>
				<td><img title="PC版图标" style="width: 128px; height: 128px;"></img>
				</td>
			</tr>
			<tr>
				<th>手机版图标:</th>
				<td><img title="手机版图标" style="width: 50px; height: 50px;"></img>
				</td>
			</tr>
			<tr>
				<th>应用截图:</th>
				<td><span title="应用截图"></span>
				</td>
			</tr>
			<tr>
				<th>应用版本:</th>
				<td><span title="应用版本"></span>
				</td>
			</tr>
			<tr>
				<th>内存空间(byte):</th>
				<td><span title="内存空间"></span>
				</td>
			</tr>
			<tr>
				<th>存储空间(byte):</th>
				<td><span title="存储空间"></span>
				</td>
			</tr>
			<tr>
				<th>应用版本状态:</th>
				<td><span title="版本状态"></span>
				</td>
			</tr>
		</table>
	</div>
	<%--应用详情显示模板介绍 --%>
	<%--加载文件信息显示模板开始 --%>
	<div id="loadFileTemplateDiv" style="display: none;">
		<p id="loadFileTemplateP">
			<img title="icon"></img> <span title="ID" style="display: none"></span>
			<span title="名称"></span>(<span title="AID"></span>)
		</p>
		<div id="loadFileDetailsAndModules">
			<table class="myTable">
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;</span> <img
						title="detailsIcon"></img> <span>详细信息</span>
						<div title="loadFileDetails">
							<table class="myTable">
								<tr>
									<th width="150"><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
										<span>版本号：</span>
									</th>
									<td><span title="版本号"></span></td>
								</tr>
								<tr>
									<th><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
										<span>备注：</span>
									</th>
									<td><span title="备注"></span></td>
								</tr>
								<tr>
									<th><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
										<span>加载参数：</span>
									</th>
									<td><span title="加载参数"></span></td>
								</tr>
								<tr>
									<th><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
										<span>文件大小(byte)：</span>
									</th>
									<td><span title="文件大小"></span></td>
								</tr>
							</table>
						</div></td>
				</tr>
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;</span> <img
						title="modulesIcon"></img> <span>模块信息</span>
						<div title="loadModules"></div></td>
				</tr>
			</table>
		</div>
	</div>
	<%--加载文件信息显示模板结束 --%>
	<%--模块信息显示模板结束 --%>
	<div id="loadModuleTemplateDiv" style="display: none">
		<p id="loadModuleTemplateP">
			<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <img
				title="icon"></img> <span title="ID" style="display: none"></span> <span
				title="名称"></span>(<span title="AID"></span>)
		</p>
		<div id="loadModuleDetailsAndApplets">
			<table class="myTable">
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
						<img title="detailsIcon"></img> <span>详细信息</span>
						<div title="loadModuleDetails">
							<table class="myTable">
								<tr>
									<th width="150"><span>备注：</span>
									</th>
									<td><span title="备注"></span></td>
								</tr>
							</table>
						</div></td>
				</tr>
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
						<img title="appletsIcon"></img> <span>实例信息</span>
						<div title="applets"></div></td>
				</tr>
			</table>
		</div>
	</div>
	<%--模块信息显示模板结束 --%>
	<%--实例信息显示模板结束 --%>
	<div id="appletTemplateDiv" style="display: none">
		<p id="appletTemplateP">
			<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<img title="icon"></img> <span title="ID" style="display: none"></span>
			<span title="名称"></span>(<span title="AID"></span>)<span title="内存空间"></span>
			<span title="存储空间"></span>
		</p>
		<div id="appletDetails">
			<table class="myTable">
				<tr>
					<th width="150"><span>权限：</span>
					</th>
					<td><span title="权限"></span>
					</td>
				</tr>
				<tr>
					<th><span>安装参数：</span>
					</th>
					<td><span title="安装参数"></span>
					</td>
				</tr>
			</table>
		</div>
		<div id="applets"></div>
	</div>
	<%--实例信息显示模板结束 --%>
	<%--下载、删除、安装表单模板开始 --%>
	<table style="display: none">
		<tr id="shwoOrderTemplate">
			<td title="次序" style="text-align: center;"></td>
			<td title="名称" style="text-align: center;"></td>
			<td title="AID" style="text-align: center;"></td>
		</tr>
	</table>
	<%--下载、删除、安装表单模板结束 --%>
	<%--显示应用客户端模板开始 --%>
	<table>
		<tbody>
			<tr id="clientInfoTemplate">
				<td title="名称" style="text-align: center;"></td>
				<td title="版本" style="text-align: center;"></td>
				<td title="开发版本" style="text-align: center;"></td>
				<td title="客户端包名" style="text-align: center;"></td>
				<td title="客户端入口类" style="text-align: center;"></td>
				<td title="系统类型" style="text-align: center;"></td>
				<td title="系统需求" style="text-align: center;"></td>
				<td><a title="下载地址"></a>
				</td>
				<td title="文件大小" style="text-align: center;"></td>
			</tr>
		</tbody>
	</table>
	<%--显示应用客户端模板开结束 --%>
	<%--应用测试报告模板开始 --%>
	<div style="display: none">
		<table id="testReportTemplate" class="myTable">
			<tr>
				<th>测试时间:</th>
				<td><span title="测试时间"></span>
				</td>
			</tr>
			<tr>
				<th>测试手机号:</th>
				<td><span title="测试手机号"></span>
				</td>
			</tr>
			<tr>
				<th>SEID:</th>
				<td><span title="SEID"></span>
				</td>
			</tr>
			<tr>
				<th>NFC终端型号 :</th>
				<td style="text-align: left"><span title="NFC终端型号"></span>
				</td>
			</tr>
			<tr>
				<th>SE芯片类型:</th>
				<td><span title="SE芯片类型"></span>
				</td>
			</tr>
			<tr>
				<th>CMS2AC版本:</th>
				<td><span title="CMS2AC版本"></span>
				</td>
			</tr>
			<tr>
				<th>测试结果:</th>
				<td><span title="测试结果"></span></td>
			</tr>
		</table>
	</div>
	<%--应用测试报告模板开始结束 --%>
</body>
</html>