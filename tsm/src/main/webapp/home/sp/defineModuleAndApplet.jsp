<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用上传</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js" type="text/javascript"></script>
<script src="${ctx}/home/sp/js/defineModuleAndApplet.js" type="text/javascript"></script>
<script src="${ctx}/admin/sd/js/sd.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx}";
	var spreadIcon = ctx + "/lib/menu/icon_7.png";
	var hiddenIcon = ctx + "/lib/menu/icon_6.png";

	window.addEvent("domready", function() {
		var page = new Application.Page({
			applicationVersionId : "${param.applicationVersionId }"
		});
		page.init();

		$("jump").addEvent("click", function(event) {
			event.stop();
			$("jumpForm").submit();
		});
	});
</script>
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
							<a class="on3" title="修改基本信息可在应用上传完成后进行">输入基本信息</a> <a class="on"
								href="${ctx }/home/sp/uploadCap.jsp?applicationVersionId=${param.applicationVersionId }">上传加载文件</a> <a class="on"
								href="${ctx }/home/sp/uploadExtension.jsp?applicationVersionId=${param.applicationVersionId }">上传应用扩展</a> <a class="on2">定义模块和实例</a>
							<a class="on3">配置顺序</a><a class="on3">上传客户端</a>
						</div>
						<div class="upapptext">
							<p class="left">第 4 步: 定义模块和实例</p>
							<p class="right c_h">定义模块和实例</p>
						</div>
						<div id="uploadedCapsInfo" class="list_sj"></div>
						<div>
							<a class="subbutt" href="#" style="float: right;"><span id="jump">下一步</span> </a>
						</div>
						<p id="userinput_t" class="userinput_t"></p>
						<div id="userinput" style=""></div>
					</div>
				</div>
			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>

	<form id="jumpForm" action="${ctx }/home/sp/setOrders.jsp" style="display: none">
		<input name="applicationVersionId" value="${param.applicationVersionId }" />
	</form>
	<%--加载文件信息显示模板开始 --%>
	<div id="loadFileTemplateDiv" style="display: none">
		<div title="loadFileTemplateDiv" class="list_1">
			<p id="loadFileTemplateP">
				<img title="icon" style="cursor: pointer;"></img> <span title="ID" style="display: none"></span> <span title="名称"></span><span
					title="AID"></span> <span title="操作">
					<button title="创建模块">创建模块</button> </span>
			</p>
			<div id="loadFileDetailsAndModules" class="list_2">
				<table class="myTable">
					<tr>
						<td>
							<p class="list_2">
								<img title="detailsIcon" style="cursor: pointer;"></img> <span>详细信息</span>
							</p>
							<div title="loadFileDetails" class="list_3">
								<table class="myTable">
									<tr>
										<td style="text-align: right;" width="150">版本号：</td>
										<td title="版本号" style="text-indent: 0em"></td>
									</tr>
									<tr>
										<td style="text-align: right;">备注：</td>
										<td title="备注" style="text-indent: 0em"></td>
									</tr>
									<tr>
										<td style="text-align: right;">加载参数：</td>
										<td title="加载参数" style="text-indent: 0em"></td>
									</tr>
									<tr>
										<td style="text-align: right;">文件大小：</td>
										<td style="text-indent: 0em"><span title="文件大小"></span>byte</td>
									</tr>
								</table>
							</div></td>
					</tr>
					<tr>
						<td>
							<p class="list_2">
								<img title="modulesIcon" style="cursor: pointer;"></img> <span>模块信息</span>
							</p>
							<div title="loadModules" class="list_3"></div></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<%--加载文件信息显示模板结束 --%>
	<%--模块信息输入表单模板开始 --%>
	<div id="loadModuleFormTemplateDiv" style="display: none">
		<form title="loadModule">
			<input type="text" name="loadFileVersionId" style="display: none" />
			<table class="openw">
				<tr>
					<th><span style="color: red">*</span>名称:</th>
					<td><input class="inputtext validate['required']" name="name" type="text" maxlength="32" />
					</td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>AID:</th>
					<td><input class="inputtext validate['required','length[10,32]','%checkHex']" name="aid" type="text" maxlength="32" />
					</td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>备注:</th>
					<td><textarea class="inputtext validate['required','length[1,80]']" name="comments"></textarea></td>
				</tr>
				<tr>
					<td>
						<button id="submitButton" name="submitButton" class="validate['submit']" type="submit" style="display: none">保存</button></td>
				</tr>
			</table>
			<button type="submit" style="display: none"></button>
		</form>
	</div>
	<%--模块信息输入表单模板结束 --%>
	<%--模块信息显示模板开始 --%>
	<div id="loadModuleTemplateDiv" style="display: none">
		<p id="loadModuleTemplateP" class="list_3">
			<img title="icon" style="cursor: pointer;"></img> <span title="ID" style="display: none"></span> <span title="名称"></span>(<span
				title="AID"></span>) <br /> <span title="操作">
				<button class="list_button" title="创建实例">创建实例</button>
				<button class="list_button" title="删除模块">删除模块</button> </span>
		</p>
		<div id="loadModuleDetailsAndApplets">
			<table class="myTable">
				<tr>
					<td>
						<p class="list_4">
							<img title="detailsIcon" style="cursor: pointer;"></img> <span>详细信息</span>
						</p>
						<div title="loadModuleDetails" class="list_5">
							<span>备注：</span> <span title="备注"></span>
						</div></td>
				</tr>
				<tr>
					<td>
						<p class="list_4">
							<img title="appletsIcon" style="cursor: pointer;"></img> <span>实例信息</span>
						</p>
						<div title="applets" class="list_5"></div></td>
				</tr>
			</table>
		</div>
	</div>
	<%--模块信息显示模板结束 --%>
	<%--实例信息显示模板结束 --%>
	<div id="appletTemplateDiv" style="display: none">
		<p id="appletTemplateP" class="list_5">
			<img title="icon" style="cursor: pointer;"></img> <span title="ID" style="display: none"></span> <span title="名称"></span>(<span
				title="AID"></span>) <span title="内存空间"></span> <span title="存储空间"></span> <span title="操作">
				<button title="删除实例" style="text-indent: 0em">删除实例</button> </span>
		</p>
		<div id="appletDetails" class="list_6">
			<table class="myTable">
				<tr style="display: none">
					<td style="text-align: right; border: 0px;">权限：</td>
					<td style="border: 0px; text-indent: 0em" title="权限"></td>
				</tr>
				<tr>
					<td style="text-align: right; border: 0px; width: 240px">安装参数：</td>
					<td style="border: 0px; text-indent: 0em"><span title="安装参数" style="width: 400px" class="texthidden"></span>
					</td>
				</tr>
			</table>
		</div>
		<div id="applets"></div>
	</div>
	<%--实例信息显示模板结束 --%>
	<%--实例信息输入表单模板开始 --%>
	<div id="appletFormTemplateDiv" style="display: none">
		<form title="applet">
			<input type="text" name="loadModuleId" style="display: none" /> <input type="text" name="applicationVersionId" style="display: none" />
			<table class="openw">
				<tr>
					<th><span style="color: red">*</span>名称:</th>
					<td>&nbsp;</td>
					<td colspan="2"><input class="inputtext validate['required']" name="name" type="text" maxlength="25" />
					</td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>AID:</th>
					<td>&nbsp;</td>
					<td colspan="2"><input class="inputtext validate['required','length[10,32]','%checkHex']" name="aid" type="text" maxlength="32" />
					</td>
				</tr>
				<tr style="display: none">
					<th><span style="color: red">*</span>权限:</th>
					<th style="width: 2em">0x</th>
					<td><input class="inputtext" name="hexPrivilege" value="00" readonly="readonly" />
					</td>
					<td><a title="点击选择权限" href="#" class="butt2"><span>选择权限</span> </a>
					</td>
				</tr>
				<tr>
					<th><span style="color: red">*</span>安装参数:</th>
					<th style="width: 2em">0x</th>
					<td><input class="inputtext validate['required','%checkHex']" type="text" name="installParams" value="C900EF08C8020100C7020010"
						maxlength="56" />
					</td>
					<td><a title="点击输入参数" href="#" class="butt2"><span>配置参数</span> </a>
					</td>
				</tr>
			</table>
			<button type="submit" style="display: none" class="validate['submit']"></button>
		</form>
	</div>
	<%--实例信息输入表单模板结束 --%>
	<%--实例选项选择表单模板开始 --%>
	<div id="appletSelectPrivilegeFormTemplateDiv" style="display: none">
		<div title="privilege">
			<table class="openw">
				<tr>
					<th><input type="checkbox" name="lockCard" title="锁卡" value="true"></input>
					</th>
					<td>锁卡</td>
				</tr>
				<tr>
					<th><input type="checkbox" name="abandonCard" title="废卡" value="true"></input>
					</th>
					<td>废卡</td>
				</tr>
				<tr style="display: none">
					<th><input type="checkbox" name="defaultSelect" title="缺省选择" value="true"></input>
					</th>
					<td>缺省选择</td>
				</tr>
				<tr>
					<th><input type="checkbox" name="cvm" title="CVM管理" value="true"></input></th>
					<td>CVM管理</td>
				</tr>
			</table>
		</div>
	</div>
	<%--实例选项选择表单模板结束 --%>
	<%--安装参数输入表单模板开始 --%>
	<div id="installParamsFormDivTemplate" style="display: none">
		<form title="installParams">
			<table class="openw">
				<tr>
					<th style="width: 130px">应用自定义参数(C9):</th>
					<th style="width: 2em">0x</th>
					<td><input class="inputtext validate['%checkHex']" name="customerParams" type="text" maxlength="32" />
					</td>
				</tr>
				<tr>
					<th style="width: 130px"><span style="color: red">*</span>可变数据空间(byte):</th>
					<td></td>
					<td><input class="inputtext validate['required','digit[0,65535]']" name="volatileDateSpace" type="text" />
					</td>
				</tr>
				<tr>
					<th style="width: 130px"><span style="color: red">*</span>不可变数据空间(byte):</th>
					<td></td>
					<td><input class="inputtext validate['required','digit[0,65535]']" name="nonVolatileDateSpace" />
					</td>
				</tr>
			</table>
			<button type="submit" style="display: none" class="validate['submit']"></button>
		</form>
	</div>
	<%--安装参数输入表单模板结束 --%>
</body>
</html>