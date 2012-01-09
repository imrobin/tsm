<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" ></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript"	src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/publishappcheck.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/publishappcheck2.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
		var app;
		var spreadIcon = ctx + "/lib/menu/icon_7.png";
		var hiddenIcon = ctx + "/lib/menu/icon_6.png";
		function swichTab(index) {
		//	if (index == 2){
			//	alert(box2.getElement("[id='tabC2']").get('html'));
			//}
			for ( var i = 0; i < 6; i++) {
				box2.getElement("[id='tab"+(i + 1)+"']").removeClass("s1").addClass("s2");
				box2.getElement("[id='tabC"+(i + 1)+"']").setStyle("display", "none");
			}
			box2.getElement("[id='tab"+(index)+"']").removeClass("s2").addClass("s1");
			box2.getElement("[id='tabC"+(index)+"']").setStyle("display", "");
		}
        window.addEvent('domready', function(el){
        	app = new Appcheck();
        });
        function checkValue(el){
            if(app.appForm.getElement('select[name=statusOriginal]').get('value') == 3){
                return true;
            }else{
                if (app.appForm.getElement('input[name=opinion]').get('value') == ''){
    	         	   el.errors.push("审核不通过时，请填写审核意见。");
    		            return false;
                    }
				return true;
            }
        }
</script>
<style type="text/css">

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
.titletab{background:url(${ctx}/images/line_s.gif) left bottom repeat-x;}

.titletab ul{ overflow:hidden; clear:both;}

.titletab ul li{float:left; padding:4px 10px; font-size:14px; z-index:99; position:relative; margin-right:10px;}

.titletab ul li.s1{ background:#FFFFFF; border:#CCCCCC 1px solid; border-bottom:#FFFFFF 1px solid; color:#027ce0;}

.titletab ul li.s2{ border:#CCCCCC 1px solid; background:url(../images/s2_bg.gif) left bottom repeat-x; }

.titletab ul li.s1 a{ display:block; color:#027ce0; text-decoration:none;}

.titletab ul li.s2 a{ display:block; text-decoration:none;}

.titletab ul li span{ color:#FF3300;}
.minfo{ background:url(${ctx}/images/line_x.gif) left top repeat-x; padding-top:10px;}

.minfo img{ vertical-align:top; float:left;}

.minfo p{ float:left; color:#999999; width:400px;}

.minfo table{border-top:#CCCCCC 1px solid; border-right:#CCCCCC 1px solid; width:500px;}

.minfo table th,.minfo table td{ border-bottom:#CCCCCC 1px solid; border-left:#CCCCCC 1px solid; padding:3px; line-height:18px;}

.minfo table th{ background:url(${ctx}/images/th_bg.gif) left bottom; line-height:22px;:}

.minfo table td.zt{text-align:center; color:#999999;}
</style>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;">
</div>
</div></div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="requistionDiv"  style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form id="appForm"  name="appForm"  method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" id="submitBtn" style="display: none;"></button>
<dl>
<dd><p class="regtext">应用名称:</p><p class="left inputs">
<input id="appName" class="inputtext validate['required']" name="appName" type="text"  readonly="readonly"/></p></dd>
<dd><p class="regtext">版本号:</p><p class="left inputs">
<input id="versionNo" class="inputtext" name="versionNo" type="text"  readonly="readonly"/></p></dd>
<dd><p class="regtextleft">加载文件列表:</p></dd>
<dd>
<table width="100%" border="1"  id="files" name="files"> 
</table> 
</dd>
<dd><p class="regtext">申请理由:</p><p class="left inputs">
<input id="reason" class="inputtext" name="reason" value=""   readonly="readonly"/></p></dd>
<dd><p class="regtext">提交时间:</p><p class="left inputs">
<input id="submitDate" class="inputtext" name="submitDate" type="text"   readonly="readonly"/></p></dd>
<dd><p class="regtext">审核意见:</p><p class="left inputs">
<input id="opinion" class="inputtext" maxlength="100"  name="opinion" type="text" /></p>
</dd>
<dd id="typeTktd"><p class="regtext">传输密钥:</p><p class="left inputs">
<input id="typeTk" class="inputtext validate['required','length[32,32]','%checkHex']" maxlength="32"  name="typeTk" type="text" /></p>
<p>
	<a id="addHsmkeyConfigTK" href="#">配置</a> 
</p>
</dd>
<!-- <dd id="tkVersiontd"><p class="regtext">传输密钥版本:</p><p class="left inputs">
<input id="tkVersion" class="inputtext  validate['digit']" maxlength="100"  name="tkVersion" type="text" /></p>
</dd>
<dd id="tkVendortd"><p class="regtext">传输密钥加密机厂商:</p><p class="left inputs">
<select id="tkVendor"  name="tkVendor" ></select></p>
</dd> -->

<dd  id="typeKektd"><p class="regtext">敏感数据加密密钥:</p><p class="left inputs">
<input id="typeKek"class="inputtext validate['required','length[32,32]','%checkHex']" maxlength="32"  name="typeKek" type="text" /></p>
<p>
	<a id="addHsmkeyConfigKEK" href="#">配置</a>
</p>
</dd>
<dd id="tkAlgorithmtd"><p class="regtext">个人化指令传输密钥(TK)的安全算法:</p><p class="left inputs">
<input id="persoCmdTransferSecureAlgorithm" class="inputtext" readonly="readonly" name="persoCmdTransferSecureAlgorithm" type="text" /></p>
</dd>
<dd id="kekAlgorithmtd"><p class="regtext">个人化指令敏感数据密钥(KEK)的安全算法:</p><p class="left inputs">
<input id="persoCmdSensitiveDataSecureAlgorithm" class="inputtext" 
readonly="readonly" name="persoCmdSensitiveDataSecureAlgorithm" type="text" /></p>
</dd>
<dd>
	<input type="hidden" id="typeOriginal" name="typeOriginal" value=""/>
	<input type="hidden" name="hsmkeyConfigTK" value=""/>
<input type="hidden" name="hsmkeyConfigKEK" value=""/>
<input type="hidden" id="originalId" name="originalId" value=""/>
<input type="hidden" id="sdidStr" name="sdidStr" value=""/>
	<p class="regtext">操作:</p>
	<p class="left inputs"><select name="statusOriginal" id="statusOriginal" class="validate['required']" >
	<option value="" >请选择...</option>
		<option value="3" >审核通过</option>
		<option value="4">审核不通过</option>
	</select></p>
	</dd>
</dl>
</form>
</div>
</div>
<div id="appVerInfoDiv"  style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
				<div class="usercont">
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
							<div id="uploadedCapsInfo" style="width: 100%;">
							</div>
						</div>
						<!-- 结构结束  -->
						<!-- 客户端信息开始 -->
						<div id="tabC3" class="minfo" style="display: none">
							<table style="width: 100%">
								<thead>
									<tr>
										<th colspan="6">客户端信息</th>
									</tr>
									<tr>
										<th>名称</th>
										<th>版本</th>
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
						<div id="tabC6" class="minfo" style="display: none">
							<div id="testReport"></div>
							<div id="trNextPage" align="right"></div>
							<div id="trInfo" align="right"></div>
						</div>
					</div>
				</div>
</div>
</div>
	<%--应用测试报告模板开始 --%>
	<div style="display:none">
		<table id="testReportTemplate" class="myTable">
			<tr>
				<th>测试时间:</th>
				<td><span title="测试时间"></span></td>
			</tr>
			<tr>
				<th>测试手机号:</th>
				<td><span title="测试手机号"></span></td>
			</tr>
			<tr>
				<th>SEID:</th>
				<td><span title="SEID"></span></td>
			</tr>
			<tr>
				<th>NFC终端型号	:</th>
				<td style="text-align: left"><span title="NFC终端型号"></span></td>
			</tr>
			<tr>
				<th>SE芯片类型:</th>
				<td><span title="SE芯片类型"></span></td>
			</tr>
			<tr>
				<th>CMS2AC版本:</th>
				<td><span title="CMS2AC版本"></span></td>
			</tr>
			<tr>
				<th>测试结果:</th>
				<td><span title="测试结果"></span>
				</td>
			</tr>
		</table>
	</div>
	<%--应用测试报告模板开始结束 --%>
<div id="testFileDiv"  style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form id="appForm"  name="appForm"  method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" id="submitBtn" style="display: none;"></button>
<dl id="testFile">
<!--<dd><p class="regtextleft">111.bat</p><input type="button" id="down"  value="下载查看"></input>
</dd>
--></dl>
</form>
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
				<td><span title="个人化指令传输加密算法"></span>
				</td>
			</tr>
			<tr>
				<th>个人化指令敏感数据加密算法:</th>
				<td><span title="个人化指令敏感数据加密算法"></span>
				</td>
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
				<td width="500"><span title="应用截图"></span>
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
			<img title="icon"></img> <span title="ID" style="display: none"></span> <span title="名称"></span>(<span title="AID"></span>)
		</p>
		<div id="loadFileDetailsAndModules">
			<table class="myTable">
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;</span> <img title="detailsIcon"></img> <span>详细信息</span>
						<div title="loadFileDetails">
							<table class="myTable">
								<tr>
									<th width="150"><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <span>版本号：</span>
									</th>
									<td><span title="版本号"></span></td>
								</tr>
								<tr>
									<th><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <span>备注：</span>
									</th>
									<td><span title="备注"></span></td>
								</tr>
								<tr>
									<th><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <span>加载参数：</span>
									</th>
									<td><span title="加载参数"></span></td>
								</tr>
								<tr>
									<th><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <span>文件大小(byte)：</span>
									</th>
									<td><span title="文件大小"></span></td>
								</tr>
							</table>
						</div></td>
				</tr>
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;</span> <img title="modulesIcon"></img> <span>模块信息</span>
						<div title="loadModules"></div></td>
				</tr>
			</table>
		</div>
	</div>
	<%--加载文件信息显示模板结束 --%>
	<%--模块信息显示模板结束 --%>
	<div id="loadModuleTemplateDiv" style="display: none">
		<p id="loadModuleTemplateP">
			<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <img title="icon"></img> <span title="ID" style="display: none"></span> <span
				title="名称"></span>(<span title="AID"></span>)
		</p>
		<div id="loadModuleDetailsAndApplets">
			<table class="myTable">
				<tr>
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <img title="detailsIcon"></img> <span>详细信息</span>
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
					<td><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <img title="appletsIcon"></img> <span>实例信息</span>
						<div title="applets"></div></td>
				</tr>
			</table>
		</div>
	</div>
	<%--模块信息显示模板结束 --%>
	<%--实例信息显示模板结束 --%>
	<div id="appletTemplateDiv" style="display: none">
		<p id="appletTemplateP">
			<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <img
				title="icon"></img> <span title="ID" style="display: none"></span> <span title="名称"></span>(<span title="AID"></span>)<span title="内存空间"></span>
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
				<td title="系统类型" style="text-align: center;"></td>
				<td title="系统需求" style="text-align: center;"></td>
				<td><a title="下载地址"></a>
				</td>
				<td title="文件大小" style="text-align: center;"></td>
			</tr>
		</tbody>
	</table>
	<%--显示应用客户端模板开结束 --%>

</body>
</html>
