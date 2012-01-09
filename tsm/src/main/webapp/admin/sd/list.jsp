<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>

<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<script type="text/javascript" src="${ctx}/admin/sd/js/sd.js"></script>

<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		new SecurityDomain.list();
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/admin/layout/top.jsp"%>
		<div id="main">
			<%@ include file="/admin/layout/menu.jsp"%>

			<div id="right">
				<div class="rightbo">

					<div id="tableDiv" class="rightcont" style="height: 450px;">
						<!-- grid -->
					</div>

				</div>
			</div>
		</div>
		<div id="footer" class="clear">
			<p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p>
		</div>
	</div>

	<!-- add -->
	<div id="sdDivAdd" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>

					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>AID:
						</p>
						<p class="left inputs">
							<input type="hidden" value="" name="originalAid" id="originalAid" />
							<input class="inputtext validate['required','length[10,32]','%checkHex','%checkAid']"
								maxlength="32" id="aid" name="aid" type="text" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>名称:
						</p>
						<p class="left inputs">
							<input class="inputtext validate['required','lenght[1,25]']"
								maxlength="25" id="sdName" name="sdName" type="text" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>应用提供商:
						</p>
						<p class="left inputs">
							<select id="spSelect" name="spId">
								<option value="">----- 请选择 -----</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span> 安全等级:
						</p>
						<p class="left inputshort">
							<select id="scp02SecurityLevel" name="scp02SecurityLevel">
								<option value="0">安全等级0</option>
								<option value="1">安全等级1</option>
								<option value="3">安全等级3</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">删除规则:</p>
						<p class="left inputs">
							<select id="deleteRule" name="deleteRule">
								<option value="0" selected="selected">自动删除</option>
								<option value="1">调用指令删除</option>
								<option value="2">不能删除</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>安全域自身的内存空间:
						</p>
						<p class="left inputshort">
							<input id="volatileSpace" name="volatileSpace"
								class="inputtext validate['required','digit[1,65535]']"
								maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>安全域自身的存储空间:
						</p>
						<p class="left inputshort">
							<input id="noneVolatileSpace" name="noneVolatileSpace"
								class="inputtext validate['required','digit[1,65535]']"
								maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd">权限:</p>
						<p class="left inputs">
							<input id="token" name="token" type="checkbox" value="true" />委托管理<br />
							<input id="lockCard" name="lockCard" type="checkbox" value="true" />锁定卡<br />
							<!-- <input id="abandonCard" name="abandonCard" type="checkbox"
								value="true" />废止卡<br /> --> <input id="cvm" name="cvm"
								type="checkbox" value="true" />管理卡CVM<br />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">业务平台URL:</p>
						<p class="left inputs"><input class="inputtext" id="businessPlatformUrl" name="businessPlatformUrl" type="text" maxlength="255" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">业务平台服务名:</p>
						<p class="left inputs"><input class="inputtext" id="serviceName" name="serviceName" type="text" maxlength="32" /></p>
					</dd>
					<dd>
						<p class="regtextsd">安全域密钥版本号:</p>
						<p class="left inputs">
							<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['digit[1,47]']" type="text" maxlength="2" />
							<input type="hidden" name="hsmkeyConfigENC" value=""/>
							<input type="hidden" name="hsmkeyConfigMAC" value=""/>
							<input type="hidden" name="hsmkeyConfigDEK" value=""/>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">ENC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileENC" name="keyProfileENC" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
						</p>
						<p>
							<a id="addHsmkeyConfigENC" href="#">配置</a>
						</p>
					</dd>
					<dd id="mac">
						<p class="regtextsd">MAC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileMAC" name="keyProfileMAC" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
						</p>
						<p>
							<a id="addHsmkeyConfigMAC" href="#">配置</a>
						</p>
					</dd>
					<dd id="dek">
						<p class="regtextsd">DEK密钥:</p>
						<p class="left inputs">
							<input id="keyProfileDEK" name="keyProfileDEK" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
						</p>
						<p>
							<a id="addHsmkeyConfigDEK" href="#">配置</a>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>安装参数:
						</p>
						<p class="left inputs">
							<input
								class="inputtext validate['required','lenght[10,32]','%checkHex']"
								maxlength="32" id="installParams" name="installParams"
								type="text" />
						</p>
						<p>
							<a id="installParamsButton" href="#">配置</a>
						</p>
					</dd>

				</dl>
			</form>
		</div>
	</div>

	<!-- edit -->
	<div id="sdDiv" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>

					<dd>
						<p class="regtextsd" id="aidText">AID:</p>
						<p class="left inputs">
							<input type="hidden" name="applyId" id="applyId"/>
							<input type="hidden" value="" name="originalAid" id="originalAid" />
							<input class="inputtext validate['required','length[10,32]','%checkHex']" maxlength="32" id="aid" name="aid" type="text" />
						</p>
						<p class="explain left" id="aidExplain"></p>
					</dd>
					<dd>
						<p class="regtextsd" id="sdNameText">名称:</p>
						<p class="left inputs">
							<input class="inputtext" id="sdName" name="sdName" type="text" />
						</p>
						<p class="explain left" id="sdNameExplain"></p>
					</dd>
					<dd>
						<p class="regtextsd" id="spSelectText">应用提供商:</p>
						<p class="left inputs">
							<select id="spSelect" name="spId">
								<option value="">----- 请选择 -----</option>
							</select>
						</p>
						<p class="explain left" id="spSelectExplain"></p>
						<dd>
							<p class="regtextsd" id="scp02SecurityLevelText">安全等级:</p>
							<p class="left inputs">
								<select id="scp02SecurityLevel" name="scp02SecurityLevel">
									<option value="0">安全等级0</option>
									<option value="1">安全等级1</option>
									<option value="3">安全等级3</option>
								</select>
							</p>
						</dd>
					</dd>
					<dd>
						<p class="regtextsd" id="deleteRuleText">删除规则:</p>
						<p class="left inputs">
							<select id="deleteRule" name="deleteRule">
								<option value="0">自动删除</option>
								<option value="1">调用指令删除</option>
								<option value="2">不能删除</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="volatileSpaceText">安全域自身的内存空间:</p>
						<p class="left inputshort">
							<input id="volatileSpace" name="volatileSpace"
								class="inputtext validate['required','digit[1,65535]']"
								maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="noneVolatileSpaceText">安全域自身的存储空间:</p>
						<p class="left inputshort">
							<input id="noneVolatileSpace" name="noneVolatileSpace"
								class="inputtext validate['required','digit[1,65535]']"
								maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="privilegeText">权限:</p>
						<p class="left inputs">
							<input id="token" name="token" type="checkbox" value="true" />委托管理<br />
							<input id="lockCard" name="lockCard" type="checkbox" value="true" />锁定卡<br />
							<!-- <input id="abandonCard" name="abandonCard" type="checkbox" value="true" />废止卡<br />  -->
							<input id="cvm" name="cvm" type="checkbox" value="true" />管理卡CVM<br />
						</p>
						<p class="explain left" id="privilegeExplain"></p>
					</dd>
					
					<dd>
						<p class="regtextsd" id="businessPlatformUrlText">业务平台URL:</p>
						<p class="left inputs"><input class="inputtext" id="businessPlatformUrl" name="businessPlatformUrl" type="text" maxlength="255" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="serviceNameText">业务平台服务名:</p>
						<p class="left inputs"><input class="inputtext" id="serviceName" name="serviceName" type="text" maxlength="32" /></p>
					</dd>
					<dd>
						<p class="regtextsd" id="currentKeyVersionText">安全域密钥版本号:</p>
						<p class="left inputs">
							<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['digit[1,47]']"
								type="text" maxlength="2" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileENCText">ENC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileENC" name="keyProfileENC" class="inputtext validate['length[32,32]','%checkHex']"
								type="text" maxlength="32" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileMACText">MAC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileMAC" name="keyProfileMAC" class="inputtext validate['length[32,32]','%checkHex']"
								type="text" maxlength="32" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileDEKText">DEK密钥:</p>
						<p class="left inputs">
							<input id="keyProfileDEK" name="keyProfileDEK" class="inputtext validate['length[32,32]','%checkHex']"
								type="text" maxlength="32" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="installParamsText">安装参数:</p>
						<p class="left inputs">
							<input
								class="inputtext validate['required','lenght[10,32]','%checkHex']"
								maxlength="32" id="installParams" name="installParams"
								type="text" />
						</p>
						<p>
							<a id="installParamsButton" href="#">配置</a>
						</p>
					</dd>
					<div id="hiddendata" />
				</dl>
			</form>
		</div>
	</div>
	<!-- edit version -->
	<div id="sdDivKeyVersion" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>
					<dd>
						<p class="regtextsd">安全域密钥版本号:</p>
						<p class="left inputs">
							<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['required','digit[1,47]']" type="text" maxlength="2" />
						</p>
					</dd>
					<!-- <dd>
						<p class="regtextsd">ENC密钥索引:</p>
						<p class="left inputs">
							<input id="keyProfileENCindex" name="keyProfileENCindex" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">ENC密钥版本:</p>
						<p class="left inputs">
							<input id="keyProfileENCversion" name="keyProfileENCversion" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">MAC密钥索引:</p>
						<p class="left inputs">
							<input id="keyProfileMACindex" name="keyProfileMACindex" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">MAC密钥版本:</p>
						<p class="left inputs">
							<input id="keyProfileMACversion" name="keyProfileMACversion" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">DEK密钥索引:</p>
						<p class="left inputs">
							<input id="keyProfileDEKindex" name="keyProfileDEKindex" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">DEK密钥版本:</p>
						<p class="left inputs">
							<input id="keyProfileDEKversion" name="keyProfileDEKversion" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd> -->
				</dl>
			</form>
		</div>
	</div>
	<!-- requistion table -->
	<div id="requistionDiv" style="display: none;">
		<div name="requistionTable" class="regcont"></div>
	</div>
	<!-- subscribe table -->
	<div name="subscribeDiv" style="display: none;">
		<div name="subscribeTable" class="regcont"></div>
	</div>
	
	<div id="keyProfileConfigDiv" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>
					<dd>
						<p class="regtextsd"><!-- 安全域密钥版本号: --></p>
						<p class="left inputs">
							<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['digit[1,47]']" type="hidden" maxlength="2" readonly="readonly"/>
							<input type="hidden" name="hsmkeyConfigENC" value=""/>
							<input type="hidden" name="hsmkeyConfigMAC" value=""/>
							<input type="hidden" name="hsmkeyConfigDEK" value=""/>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">ENC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileENC" name="keyProfileENC" class="inputtext validate['required','length[32,32]','%checkHex']" type="text" maxlength="32" />
						</p>
						<p>
							<a id="addHsmkeyConfigENC" href="#">配置</a>
						</p>
					</dd>
					<dd id="mac">
						<p class="regtextsd">MAC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileMAC" name="keyProfileMAC" class="inputtext validate['required','length[32,32]','%checkHex']" type="text" maxlength="32" />
						</p>
						<p>
							<a id="addHsmkeyConfigMAC" href="#">配置</a>
						</p>
					</dd>
					<dd id="dek">
						<p class="regtextsd">DEK密钥:</p>
						<p class="left inputs">
							<input id="keyProfileDEK" name="keyProfileDEK" class="inputtext validate['required','length[32,32]','%checkHex']" type="text" maxlength="32" />
						</p>
						<p>
							<a id="addHsmkeyConfigDEK" href="#">配置</a>
						</p>
					</dd>
				</dl>
			</form>
		</div>
	</div>
	
	<!-- info -->
	<div id="sdDivInfo" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>

					<dd>
						<p class="regtextsd">AID:</p>
						<p class="left inputshort">
							<input class="inputtext" id="aid" name="aid" type="text"
								readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="sdNameText">名称:</p>
						<p class="left inputshort">
							<input class="inputtext" id="sdName" name="sdName" type="text"
								readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="spSelectText">应用提供商:</p>
						<p class="left inputshort" id="spSelect" style="margin-top: 7px;">
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="scp02SecurityLevelText">安全等级:</p>
						<p class="left inputshort">
							<select id="scp02SecurityLevel" name="scp02SecurityLevel"
								disabled="disabled">
								<option value="0">安全等级0</option>
								<option value="1">安全等级1</option>
								<option value="3">安全等级3</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">删除规则:</p>
						<p class="left inputshort">
							<select id="deleteRule" name="deleteRule" disabled="disabled">
								<option value="0">自动删除</option>
								<option value="1">调用指令删除</option>
								<option value="2">不能删除</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全域自身的内存空间:</p>
						<p class="left inputshort">
							<input id="volatileSpace" name="volatileSpace"
								class="inputtext validate['required','digit[1,65535]']"
								readonly="readonly" maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全域自身的存储空间:</p>
						<p class="left inputshort">
							<input id="noneVolatileSpace" name="noneVolatileSpace"
								class="inputtext validate['required','digit[1,65535]']"
								readonly="readonly" maxlength="5" />byte
						</p>
					</dd>

					<dd>
						<p class="regtextsd" id="privilegeText">权限:</p>
						<p class="left ">
							<input disabled="disabled" id="token" name="token"
								type="checkbox" value="true" />委托管理<br /> <input
								disabled="disabled" id="lockCard" name="lockCard"
								type="checkbox" value="true" />锁定卡<br /><!--  <input
								disabled="disabled" id="abandonCard" name="abandonCard"
								type="checkbox" value="true" />废止卡<br /> --> <input
								disabled="disabled" id="cvm" name="cvm" type="checkbox"
								value="true" />管理卡CVM<br /> <input class="inputtext"
								maxlength="32" id="installParams" name="installParams"
								type="hidden" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">业务平台URL:</p>
						<p class="left inputs"><input readonly="readonly" class="inputtext validate['required']" id="businessPlatformUrl" name="businessPlatformUrl" type="text" maxlength="255" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">业务平台服务名:</p>
						<p class="left inputs"><input readonly="readonly" class="inputtext validate['required']" id="serviceName" name="serviceName" type="text" maxlength="32" /></p>
					</dd>
					<dd>
						<p class="regtextsd" id="currentKeyVersionText">安全域密钥版本号:</p>
						<p class="left inputs">
							<input id="currentKeyVersion" name="currentKeyVersion"
								class="inputtext validate['required','digit[1,47]']"
								type="text" maxlength="2" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileENCText">ENC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileENC" name="keyProfileENC"
								class="inputtext validate['required','length[32,32]','%checkHex']"
								type="text" maxlength="32" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileMACText">MAC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileMAC" name="keyProfileMAC"
								class="inputtext validate['required','length[32,32]','%checkHex']"
								type="text" maxlength="32" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileDEKText">DEK密钥:</p>
						<p class="left inputs">
							<input id="keyProfileDEK" name="keyProfileDEK"
								class="inputtext validate['required','length[32,32]','%checkHex']"
								type="text" maxlength="32" readonly="readonly" />
						</p>
					</dd>
					<!-- 安装参数 -->
					<dd>
						<p class="regtextsd">安全域是否允许删除:</p>
						<p class="left inputshort">
							<select id="deleteSelf" name="deleteSelf" disabled="disabled">
								<option value="0" selected>允许</option>
								<option value="1">不允许</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全域是否接受迁移:</p>
						<p class="left inputshort">
							<select id="transfer" name="transfer" disabled="disabled">
								<option value="1" selected>接受</option>
								<option value="0">不接受</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">是否接受主安全域发起的应用删除:</p>
						<p class="left inputshort">
							<select id="deleteApp" name="deleteApp" disabled="disabled">
								<option value="0" selected>接受</option>
								<option value="1">不接受</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">是否允许从主安全域发起的应用安装:</p>
						<p class="left inputshort">
							<select id="installApp" name="installApp" disabled="disabled">
								<option value="0" selected="selected">允许</option>
								<option value="1">不允许</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">是否允许从其他安全域发起的应用下载:</p>
						<p class="left inputshort">
							<select id="downloadApp" name="downloadApp" disabled="disabled">
								<option value="0" selected="selected">允许</option>
								<option value="1">不允许</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">是否允许从主安全域发起的应用锁定或解锁:</p>
						<p class="left inputshort">
							<select id="lockedApp" name="lockedApp" disabled="disabled">
								<option value="0" selected="selected">允许</option>
								<option value="1">不允许</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">空间管理模式:</p>
						<p class="left" id="spacePatten" style="margin-top: 6px;">签约空间模式
						</p>
					</dd>
					<dd id="sphidden1">
						<p class="regtextsd">安全域管理的内存空间:</p>
						<p class="left inputshort">
							<input readonly="readonly" id="managedVolatileSpace"
								class="inputtext" name="managedVolatileSpace"
								readonly="readonly" type="text" maxlength="5" />byte
						</p>
					</dd>
					<dd id="sphidden2">
						<p class="regtextsd">安全域管理的存储空间:</p>
						<p class="left inputshort">
							<input readonly="readonly" id="managedNoneVolatileSpace"
								class="inputtext" name="managedNoneVolatileSpace"
								readonly="readonly" type="text" maxlength="10" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全通道协议:</p>
						<p class="left inputshort">
							<select id="scp" name="scp" disabled="disabled">
								<option value="10,01">SCP10 0x01</option>
								<option value="02,15" selected="selected">SCP02 0x15</option>
								<option value="10,02">SCP10 0x02</option>
								<option value="02,05">SCP02 0x05</option>
								<option value="02,45">SCP02 0x45</option>
								<option value="02,55">SCP02 0x55</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全通道最大连续鉴权失败次数:</p>
						<p class="left inputshort">
							<input readonly="readonly" id="maxFailCount" class="inputtext"
								name="maxFailCount" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">密钥版本号:</p>
						<p class="left inputshort">
							<input readonly="readonly" id="keyVersion" value=""
								class="inputtext" name="keyVersion" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全域支持的最大对称密钥个数:</p>
						<p class="left inputshort">
							<input readonly="readonly" id="maxKeyNumber" value=""
								class="inputtext" name="maxKeyNumber" type="text" maxlength="3" />
						</p>
						<div id="hiddendata"></div>
					</dd>
				</dl>
			</form>
		</div>
	</div>

	<!-- InstallParams -->
	<div id="installParamsDiv" style="display: none">
		<form id="" title="installParamsClient" method="post">
			<button class="validate['submit']" style="display: none;"></button>
			<table class="openw">
				<tr>
					<td class="td1" align="right"><span style='color: red;'>*</span>安全域是否允许删除:&nbsp;</td>
					<td><select id="deleteSelf" name="deleteSelf">
							<option value="0" selected>允许</option>
							<option value="1">不允许</option>
					</select></td>
				</tr>
				<tr>
					<td class="td1" align="right"><span style='color: red;'>*</span>安全域是否接受迁移:&nbsp;</td>
					<td><select id="transfer" name="transfer">
							<option value="1" selected>接受</option>
							<option value="0">不接受</option>
					</select></td>
				</tr>
				<tr>
					<td class="td1" align="right"><span style='color: red;'>*</span>是否接受主安全域发起的应用删除:&nbsp;</td>
					<td><select id="deleteApp" name="deleteApp">
							<option value="0" selected>接受</option>
							<option value="1">不接受</option>
					</select></td>
				</tr>
				<tr>
					<td class="td1" align="right"><span style='color: red;'>*</span>是否允许从主安全域发起的应用安装:&nbsp;</td>
					<td><select id="installApp" name="installApp">
							<option value="0" selected="selected">允许</option>
							<option value="1">不允许</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="td1" align="right"><span style='color: red;'>*</span>是否允许从其他安全域发起的应用下载:&nbsp;</td>
					<td><select id="downloadApp" name="downloadApp">
							<option value="0" selected="selected">允许</option>
							<option value="1">不允许</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="td1" align="right"><span style='color: red;'>*</span>是否允许从主安全域发起的应用锁定或解锁:&nbsp;</td>
					<td><select id="lockedApp" name="lockedApp">
							<option value="0" selected="selected">允许</option>
							<option value="1">不允许</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="td1" align="right">空间管理模式:&nbsp;</td>
					<td colspan="3" id="spacePattentd"><input name="spacePatten"
						title="spacePatten" type="checkbox" id="spacePatten" value="true" />签约空间模式<br />
					</td>
				</tr>
				<tr id="sphidden1">
					<td class="td1" align="right">安全域管理的内存空间:&nbsp;</td>
					<td><input id="managedVolatileSpace" value=""
						class="inputtext validate['required','digit[1,65535]']"
						name="managedVolatileSpace" type="text" maxlength="5" disabled />
					</td>
					<td>byte</td>
				</tr>
				<tr id="sphidden2">
					<td class="td1" align="right">安全域管理的存储空间:&nbsp;</td>
					<td colspan="3"><input id="managedNoneVolatileSpace" value=""
						class="inputtext validate['required','digit[1,4294967295]']"
						name="managedNoneVolatileSpace" type="text" maxlength="10"
						disabled /></td>
					<td>byte</td>
				</tr>
				<tr>
					<td class="td1" align="right">安全通道协议:&nbsp;</td>
					<td><select id="scp" name="scp">
							<option value="-1">选择通道协议</option>
							<option value="10,01">SCP10 0x01</option>
							<option value="02,15" selected="selected">SCP02 0x15</option>
							<option value="10,02">SCP10 0x02</option>
							<option value="02,05">SCP02 0x05</option>
							<option value="02,45">SCP02 0x45</option>
							<option value="02,55">SCP02 0x55</option>
					</select></td>
				</tr>
				<tr>
					<td class="td1" align="right">安全通道最大连续鉴权失败次数:&nbsp;</td>
					<td><input id="maxFailCount" value=""
						class="inputtext validate['digit[1,255]']" name="maxFailCount"
						type="text" maxlength="3" /></td>
				</tr>
				<tr>
					<td class="td1" align="right">密钥版本号:&nbsp;</td>
					<td><input id="keyVersion" value=""
						class="inputtext validate['digit[1,255]']" name="keyVersion"
						type="text" maxlength="3" /></td>
				</tr>
				<tr>
					<td class="td1" align="right">安全域支持的最大对称密钥个数:&nbsp;</td>
					<td><input id="maxKeyNumber" value=""
						class="inputtext validate['digit[1,255]']" name="maxKeyNumber"
						type="text" maxlength="3" /></td>
				</tr>
				<tr style="display: none">
					<td><button id="submitButton" name="submitButton"
							class="validate['submit']" type="submit">保存</button>
					</td>
					<td><button id="cancelButton" name="cancelButton">取消</button>
					</td>
				</tr>
				<div id="hiddendata"></div>
			</table>
		</form>
	</div>

</body>
</html>
