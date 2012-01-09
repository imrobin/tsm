<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>安全域明细</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<script type="text/javascript" src="${ctx}/admin/sd/js/sd.js"></script>

<script type="text/javascript">
EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
var ctx = '${ctx}';
var sdid = <%=request.getParameter("id")%>;
var status = <%=request.getParameter("status") %>
window.addEvent('domready', function() {
	//load data...
	new Request.JSON({
		async : false,
		url : ctx + '/html/securityDomain/?m=sdLoad',
		onSuccess : function(result) {
			var sp = result.message;
			
			//遍历json对象
			for(var attr in sp) {
				if(typeof(sp[attr]) == 'function') {
					sp[attr]();
				} else if(typeof(sp[attr]) == 'object' && sp[attr] != null && sp[attr] != '') {
					//nothing
				} else {
					var value = sp[attr];
					//处理外键字段
					if(attr.indexOf('_') != -1) {
						attr = attr.substring(attr.indexOf('_')+1);
					}
					//alert(attr + ':' + value);
					var e = $(attr);
					if(e) {
						if(e.get('type') == 'radio') {
							e = $$('input[name='+attr+']');
							for(var i = 0; i < e.length; i++) {
								if(value == e[i].get('value')) {
									e[i].set('checked','checked');
									break;
								}
							}
						} else if(e.get('type') == 'checkbox') {
							e.set('checked',value);
						} else {
							e.set('value', value);
						}
					}
				}
			}
			
			//load installparams
			var installParams = sp.installParams;
			var request = new Request.JSON({
				async : false,
				url : ctx + "/html/securityDomain/?m=parseInstallParams&installParams="+installParams,
				onSuccess : function(data) {
					var msg = data.message;
					if(data.success) {
						$('transfer').set('value', msg.transfer);
						$('deleteApp').set('value', msg.deleteApp);
						$('deleteSelf').set('value', msg.deleteSelf);
						$('installApp').set('value', msg.installApp);
						$('downloadApp').set('value', msg.downloadApp);
						$('lockedApp').set('value', msg.lockedApp);
						$('scp').set('value',msg.scp);
						$('maxFailCount').set('value', msg.maxFailCount);
						$('keyVersion').set('value', msg.keyVersion);
						$('maxKeyNumber').set('value', msg.maxKeyNumber);
						if(msg.managedNoneVolatileSpace == "" && msg.managedVolatileSpace == "") {
							$('spacePatten').set('html','应用大小模式');
							$('managedNoneVolatileSpaceDD').set('style','display:none');
							$('managedVolatileSpaceDD').set('style', 'display:none');
						} else {
							$('managedNoneVolatileSpace').set('value',msg.managedNoneVolatileSpace);
							$('managedVolatileSpace').set('value', msg.managedVolatileSpace);
						}
					} else {
						$('transfer').set('value', 1);
						$('deleteApp').set('value', 1);
						$('deleteSelf').set('value', 1);
						$('scp').set('value','02,15');
						$('maxFailCount').set('value', 255);
						$('keyVersion').set('value', 105);
						$('maxKeyNumber').set('value', 16);
						$('managedNoneVolatileSpaceDD').set('style','display:none');
						$('managedVolatileSpaceDD').set('style', 'display:none');
						
					}
				}
			}).post();
		}
	}).post({sdid : sdid, status : status});
	
});
</script>

</head>
<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理安全域&gt;安全域详细信息</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">

<p id="userinput_t" class="userinput_t">安全域详细信息</p>
<div id="userinput">
<!-- form -->
<%
	String status = request.getParameter("status");
%>
<form action="${ctx }/html/securityDomain/?m=sdModify" id="form_apply" method="post">
	<div class="regcont" >
		<dl>
			<dd id="dd_01">
				<p class="regtextsd">AID:</p>
				<p class="left inputshort">
					<input class="inputtext" id="aid" name="aid" type="text" readonly="readonly"/>
				</p>
				<p class="explain left"></p>
			</dd>
			<dd id="dd_02">
				<p class="regtextsd">名称:</p>
				<p class="left inputshort">
					<input readonly="readonly" class="inputtext validate['required','length[1,32]']" maxlength="32" id="sdName" name="sdName" type="text" />
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtextsd">
					安全等级:
				</p>
				<p class="left inputshort">
					<select id="scp02SecurityLevel" name="scp02SecurityLevel" disabled="disabled">
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
					<input readonly="readonly" id="volatileSpace" name="volatileSpace" class="inputtext" maxlength="5"/>byte
				</p>
			</dd>
			<dd>
				<p class="regtextsd">安全域自身的存储空间:</p>
				<p class="left inputshort">
					<input readonly="readonly" id="noneVolatileSpace" name="noneVolatileSpace" class="inputtext" maxlength="5"/>byte
				</p>
			</dd>
			<dd id="dd_15">
				<p class="regtextsd">权限:</p>
				<p class="left">
					<input disabled="disabled" id="token" name="token" type="checkbox" value="true"/>委托管理<br/>
					<input disabled="disabled" id="lockCard" name="lockCard" type="checkbox" value="true" />锁定卡<br/>
					<!-- <input disabled="disabled" id="abandonCard" name="abandonCard" type="checkbox" value="true" />废止卡<br/> -->
					<input disabled="disabled" id="cvm" name="cvm" type="checkbox" value="true" />管理卡CVM<br/>
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtextsd">业务平台URL:</p>
				<p class="left inputshort"><input readonly="readonly" class="inputtext validate['required']" id="businessPlatformUrl" name="businessPlatformUrl" type="text" maxlength="255" />
				</p>
			</dd>
			<dd>
				<p class="regtextsd">业务平台服务名:</p>
				<p class="left inputshort"><input readonly="readonly" class="inputtext validate['required']" id="serviceName" name="serviceName" type="text" maxlength="32" /></p>
			</dd>
			<dd>
				<p class="regtextsd">安全域密钥版本号:</p>
				<p class="left inputshort">
					<input readonly="readonly" id="currentKeyVersion" name="currentKeyVersion" class="inputtext" type="text" maxlength="2" />
				</p>
			</dd>
			<dd>
				<p class="regtextsd">ENC密钥:</p>
				<p class="left inputshort">
					<input readonly="readonly" id="keyProfileENC" name="keyProfileENC" class="inputtext" type="text" maxlength="32" />
				</p>
			</dd>
			<dd>
				<p class="regtextsd">MAC密钥:</p>
				<p class="left inputshort">
					<input readonly="readonly" id="keyProfileMAC" name="keyProfileMAC" class="inputtext" type="text" maxlength="32" />
				</p>
			</dd>
			<dd>
				<p class="regtextsd">DEK密钥:</p>
				<p class="left inputshort">
					<input readonly="readonly" id="keyProfileDEK" name="keyProfileDEK" class="inputtext" type="text" maxlength="32" />
				</p>
			</dd>
			<!-- 安装参数 -->
	<dd>
		<p class="regtextsd">安全域是否允许删除:</p>
		<p class="left inputshort">
			<select id="deleteSelf" name="deleteSelf" disabled="disabled">
				<option value="0" selected >允许</option>
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
		<p class="left inputshort" id="spacePatten" style="margin-top:8px;">签约空间模式
		</p>
	</dd>
	<dd id="managedVolatileSpaceDD">
		<p class="regtextsd" style="float:left;">安全域管理的内存空间:</p>
		<p class="left inputshort" >
			<input readonly="readonly" id="managedVolatileSpace" class="inputtext" name="managedVolatileSpace" type="text" maxlength="5"/>byte
		</p>
		<!-- <p class="explain left">byte</p> -->
	</dd>
	<dd id="managedNoneVolatileSpaceDD">
		<p class="regtextsd">安全域管理的存储空间:</p>
		<p class="left inputshort">
			<input readonly="readonly" id="managedNoneVolatileSpace" class="inputtext" name="managedNoneVolatileSpace" type="text" maxlength="10"/>byte
		</p>
		<!-- <p class="explain left">byte</p> -->
	</dd>
	<dd>
		<p class="regtextsd">安全通道协议:</p>
		<p class="left">
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
			<input readonly="readonly" id="maxFailCount" class="inputtext" name="maxFailCount" type="text" maxlength="3"/>
		</p>
	</dd>
	<dd>
		<p class="regtextsd">密钥版本号:</p>
		<p class="left inputshort">
			<input readonly="readonly" id="keyVersion" value="" class="inputtext" name="keyVersion" type="text" maxlength="3"/>
		</p>
	</dd>
	<dd>
		<p class="regtextsd">安全域支持的最大对称密钥个数:</p>
		<p class="left inputshort">
			<input readonly="readonly" id="maxKeyNumber" value="" class="inputtext" name="maxKeyNumber" type="text" maxlength="3"/>
		</p>
	</dd>
			
			
			<dd>
				<p class="regtext"></p>
				<p class="left inputs">
					<input class="subutton" style="cursor: pointer;" type="button" value="返回" onclick="javascript:history.back(-1);"/>
				</p>
			</dd>
		</dl>
	</div>
</form>

</div>

</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>

</body>
</html>