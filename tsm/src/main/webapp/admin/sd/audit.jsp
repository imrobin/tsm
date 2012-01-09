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
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<script type="text/javascript" src="${ctx}/admin/sd/js/sd.js"></script>

<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		new SecurityDomain.audit();
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
					</div>
				</div>
			</div>
		</div>
		<div id="footer" class="clear">
			<p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p>
		</div>
	</div>
	<!-- hidden form -->
	<div id="messageBox" style="display: none;">
		<div class="regcont">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>
					<dd>
						<textarea rows="5" cols="30"></textarea>
					</dd>
				</dl>
			</form>
		</div>
	</div>
	<div id="sdDiv" style="display: none;">
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
					<!--
					<dd>
						<p class="regtextsd" id="keyProfileENCText">ENC密钥索引:</p>
						<p class="left inputshort">
							<input id="applyId" name="applyId" type="hidden"/>
							<input id="keyProfileENCindex" name="keyProfileENCindex" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">ENC密钥版本:</p>
						<p class="left inputshort">
							<input id="keyProfileENCversion" name="keyProfileENCversion" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileMACText">MAC密钥索引:</p>
						<p class="left inputshort">
							<input id="keyProfileMACindex" name="keyProfileMACindex" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">MAC密钥版本:</p>
						<p class="left inputshort">
							<input id="keyProfileMACversion" name="keyProfileMACversion" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="keyProfileDEKText">DEK密钥索引:</p>
						<p class="left inputshort">
							<input id="keyProfileDEKindex" name="keyProfileDEKindex" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">DEK密钥版本:</p>
						<p class="left inputshort">
							<input id="keyProfileDEKversion" name="keyProfileDEKversion" class="inputtext validate['required','digit[0,255]']" type="text" maxlength="3" />
						</p>
					</dd>
					-->
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
							<input class="inputtext" id="aid" name="aid" type="text" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="sdNameText">名称:</p>
						<p class="left inputshort">
							<input class="inputtext" id="sdName" name="sdName" type="text" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="spSelectText">应用提供商:</p>
						<p class="left inputshort" id="spSelect" style="margin-top: 8px;">
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
							<input id="volatileSpace" name="volatileSpace" class="inputtext"
								readonly="readonly" maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd">安全域自身的存储空间:</p>
						<p class="left inputshort">
							<input id="noneVolatileSpace" name="noneVolatileSpace"
								class="inputtext" readonly="readonly" maxlength="5" />byte
						</p>
					</dd>
					<dd>
						<p class="regtextsd" id="privilegeText">权限:</p>
						<p class="left">
							<!-- 
			<input disabled="disabled" id="dap" name="dap" type="checkbox" value="true"/>DAP验证<br/>
			<input disabled="disabled" id="dapForce" name="dapForce" type="checkbox" value="true"/>强制要求验证DAP<br/>
			-->
							<input disabled="disabled" id="token" name="token"
								type="checkbox" value="true" />委托管理<br /> <input
								disabled="disabled" id="lockCard" name="lockCard"
								type="checkbox" value="true" />锁定卡<br /> <!-- <input
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
						<p class="regtextsd">安全域密钥版本号:</p>
						<p class="left inputs">
							<input id="currentKeyVersion" name="currentKeyVersion"
								class="inputtext" type="text" maxlength="2" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">ENC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileENC" name="keyProfileENC" class="inputtext"
								type="text" maxlength="32" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">MAC密钥:</p>
						<p class="left inputs">
							<input id="keyProfileMAC" name="keyProfileMAC" class="inputtext"
								type="text" maxlength="32" readonly="readonly" />
						</p>
					</dd>
					<dd>
						<p class="regtextsd">DEK密钥:</p>
						<p class="left inputs">
							<input id="keyProfileDEK" name="keyProfileDEK" class="inputtext"
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
								class="inputtext" name="managedVolatileSpace" type="text"
								maxlength="5" />byte
						</p>
					</dd>
					<dd id="sphidden2">
						<p class="regtextsd">安全域管理的存储空间:</p>
						<p class="left inputshort">
							<input readonly="readonly" id="managedNoneVolatileSpace"
								class="inputtext" name="managedNoneVolatileSpace" type="text"
								maxlength="10" />byte
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
					</dd>

				</dl>
			</form>
		</div>
	</div>

</body>
</html>