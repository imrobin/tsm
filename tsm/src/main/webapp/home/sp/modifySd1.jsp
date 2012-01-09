<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>安全域修改</title>
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
<script type="text/javascript" src="${ctx}/home/sp/js/sdApply.js"></script>

<script type="text/javascript">
EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
var ctx = '${ctx}';
var sdid = <%=request.getParameter("id")%>;
var status = <%=request.getParameter("status") %>
window.addEvent('domready', function() {
	
	new FormCheck('form_apply', {
		submit : false,
		onValidateSuccess : function() {
			//submit to sd apply
			new Request.JSON({
				url : ctx + '/html/securityDomain/?m=sdApplyModify',
				onSuccess : function(result) {
					if(result.success) {
						new LightFace.MessageBox({
							onClose : function() {
								self.location = ctx + '/home/sp/listSd.jsp';
							}
						}).info('安全域发布申请修改成功');
					} else {
						new LightFace.MessageBox().error(result.message);
					}
				}
			}).post($('form_apply').toQueryString());
		}
	});
	
	//load data...
	new Request({
		url : '${ctx}/html/securityDomain/',
		onSuccess : function(responseText) {
			var object = JSON.decode(responseText);
			var sp = object.message;
			$('originalAid').set('value', sp.aid);
			$('spId').set('value', sp.sp_id);
			$('applyId').set('value', sdid);
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
							if(!value) {
								if(e.get('id') == 'dap') {
									e.set('disabled','disabled');
								}
								if(e.get('id') == 'dapForce') {
									e.set('disabled','disabled');
								}
								if(e.get('id') == 'token') {
									e.set('disabled','disabled');
								}
							}
						} else {
							e.set('value', value);
						}
						
					}
				}
			}
			
			if(!sp.token) {
				//$('dap').set('disabled','');
				//$('dapForce').set('disabled','');
				$('token').set('disabled','');
			}
		}
	}).post('m=sdApplyLoad&sdid='+sdid);
	
	 $('installParamsButton').addEvent('click',function(event){
		 event.stop();
		 var apply = new SecurityDomain.Apply();
		 apply.createInstallParams($('installParams').get('value'));
	 });
	
});
</script>

</head>
<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理安全域&gt;修改</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">

<p id="userinput_t" class="userinput_t">安全域修改</p>
<div id="userinput">
<!-- form -->
<%
	String status = request.getParameter("status");
%>
<form action="${ctx }/html/securityDomain/?m=sdApplyModify" id="form_apply" method="post">
	<div class="regcont" >
		<dl>
			<dd id="dd_01">
				<p class="regtext"><span style="color: red;">AID:</span></p>
				<p class="left inputs">
					<input type="hidden" value="" name="applyId" id="applyId"/>
					<input type="hidden" value="" name="spId" id="spId"/>
					<input type="hidden" value="" name="originalAid" id="originalAid" />
					<input class="inputtext validate['required','length[10,32]','%checkHex','%checkAid']" maxlength="32" id="aid" name="aid" type="text" />
				</p>
				<p class="explain left"></p>
			</dd>
			<dd id="dd_02">
				<p class="regtext"><span style="color: red;">名称:</span></p>
				<p class="left inputs">
					<input class="inputtext validate['required','length[1,32]']" maxlength="32" id="sdName" name="sdName" type="text" />
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtext">
					<span style="color : red;">安全等级:</span>
				</p>
				<p class="left inputs">
					<select id="scp02SecurityLevel" name="scp02SecurityLevel">
						<option value="0">安全等级0</option>
						<option value="1" selected="selected">安全等级1</option>
						<option value="3">安全等级3</option>
					</select>
				</p>
			</dd>
			<dd>
			<p class="regtext"><span style="color: red;">删除规则:</span></p>
			<p class="left inputs"><select id="deleteRule" name="deleteRule">
				<option value="0">自动删除</option>
				<option value="1">调用指令删除</option>
				<option value="2">不能删除</option>
			</select></p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">安全域自身的内存空间:</span></p>
				<p class="left inputs">
					<input id="volatileSpace" name="volatileSpace" class="inputtext validate['required','digit[1,65535]']" maxlength="5"/>
				</p>
				<p class="explain left">byte</p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">安全域自身的存储空间:</span></p>
				<p class="left inputs">
					<input id="noneVolatileSpace" name="noneVolatileSpace" class="inputtext validate['required','digit[1,65535]']" maxlength="5"/>
				</p>
				<p class="explain left">byte</p>
			</dd>
			<dd id="dd_15">
				<p class="regtext"><span style="color: red;">权限:</span></p>
				<p class="left inputs">
					<input id="token" name="token" type="checkbox" value="true"/>委托管理<br/>
					<input id="lockCard" name="lockCard" type="checkbox" value="true" />锁定卡<br/>
					<!-- <input id="abandonCard" name="abandonCard" type="checkbox" value="true" />废止卡<br/> -->
					<input id="cvm" name="cvm" type="checkbox" value="true" />管理卡CVM<br/>
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red">业务平台URL:</span></p>
				<p class="left inputs"><input class="inputtext" id="businessPlatformUrl" name="businessPlatformUrl" type="text" maxlength="255" />
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red">业务平台服务名:</span></p>
				<p class="left inputs"><input class="inputtext" id="serviceName" name="serviceName" type="text" maxlength="32" /></p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">安全域密钥版本号:</span></p>
				<p class="left inputs">
					<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['digit[1,47]']" type="text" maxlength="2" />
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">ENC密钥:</span></p>
				<p class="left inputs">
					<input id="keyProfileENC" name="keyProfileENC" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">MAC密钥:</span></p>
				<p class="left inputs">
					<input id="keyProfileMAC" name="keyProfileMAC" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">DEK密钥:</span></p>
				<p class="left inputs">
					<input id="keyProfileDEK" name="keyProfileDEK" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style="color: red;">安装参数:</span></p>
				<p class="left inputs">
					<input class="inputtext validate['required','length[10,512]','%checkHex']" maxlength="512" id="installParams" name="installParams" type="text" />
				</p>
				<p><a id="installParamsButton" href="#" >配置</a></p>
			</dd>
			
			<dd>
				<p class="regtext"></p>
				<p class="left inputs">
					<input type="hidden" id="id" name="id" value="<%=request.getParameter("id") %>" />
					<input class="subutton" style="cursor: pointer;" type="submit" value="保存"/>
					<input class="subutton" style="cursor: pointer;" type="button" value="返回" onclick="javascript:history.back(-1);"/>
				</p>
			</dd>
		</dl>
	</div>
</form>

</div>

</div>
<div id="installParamsDiv" style="display: none">
<form id=""  title="installParamsClient" method="post">
<button class="validate['submit']" style="display: none;"></button>
					<table class="openw">
		     <tr>
				<td class="td1"><span style='color: red;'>*</span>安全域是否允许删除:&nbsp;</td>
				<td>
					<select id="deleteSelf" name="deleteSelf">
						<option value="0" selected >允许</option>
						<option value="1">不允许</option>
					</select>
				</td>
			</tr>	
			<tr>	
			    <td class="td1"><span style='color: red;'>*</span>安全域是否接受迁移:&nbsp;</td>
			    <td>
					<select id="transfer" name="transfer">
						<option value="1" selected>接受</option>
						<option value="0">不接受</option>
					</select>
			    </td>
			</tr>
			<tr>	
			    <td class="td1"><span style='color: red;'>*</span>是否接受主安全域发起的应用删除:&nbsp;</td>
			    <td>
					<select id="deleteApp" name="deleteApp">
						<option value="0" selected>接受</option>
						<option value="1">不接受</option>
					</select>
			    </td>
			</tr>
			
			<tr>
				<td class="td1"><span style='color: red;'>*</span>是否允许从主安全域发起的应用安装:&nbsp;</td>
				<td><select id="installApp" name="installApp">
						<option value="0" selected="selected">允许</option>
						<option value="1">不允许</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="td1"><span style='color: red;'>*</span>是否允许从其他安全域发起的应用下载:&nbsp;</td>
				<td><select id="downloadApp" name="downloadApp">
						<option value="0" selected="selected">允许</option>
						<option value="1">不允许</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="td1"><span style='color: red;'>*</span>是否允许从主安全域发起的应用锁定或解锁:&nbsp;</td>
				<td><select id="lockedApp" name="lockedApp">
						<option value="0" selected="selected">允许</option>
						<option value="1">不允许</option>
					</select>
				</td>
			</tr>
			  
			<tr>
			    <td class="td1">空间管理模式:&nbsp;</td>
			    <td colspan="3">
			        <input name="spacePatten" title="spacePatten" type="checkbox" id="spacePatten" value="true"/>签约空间模式<br/>
			    </td>
			</tr>    
			<tr>
				<td class="td1">安全域管理的内存空间:&nbsp;</td>
				<td>
					<input id="managedVolatileSpace" value="" class="inputtext validate['required','digit[1,65535]']" name="managedVolatileSpace" type="text" maxlength="5" disabled/>
				</td><td>byte</td>
			</tr>
			<tr>
				<td class="td1">安全域管理的存储空间:&nbsp;</td>
				<td colspan="3">
					<input id="managedNoneVolatileSpace" value="" class="inputtext validate['required','digit[1,4294967295]']" name="managedNoneVolatileSpace" type="text" maxlength="10" disabled/>
				</td><td>byte</td>
			</tr>
			<tr>
				<td class="td1">安全通道协议:&nbsp;</td>
				<td>
					<select id="scp" name="scp">
					    <option value="-1">选择通道协议</option>
						<option value="10,01">SCP10 0x01</option>
						<option value="02,15" selected="selected">SCP02 0x15</option>
						<option value="10,02">SCP10 0x02</option>
						<option value="02,05">SCP02 0x05</option>
						<option value="02,45">SCP02 0x45</option>
						<option value="02,55">SCP02 0x55</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="td1">安全通道最大连续鉴权失败次数:&nbsp;</td>
				<td>
					<input id="maxFailCount" value="" class="inputtext validate['digit[1,255]']" name="maxFailCount" type="text" maxlength="3"/>
				</td>
			</tr>
			<tr>
				<td class="td1">密钥版本号:&nbsp;</td>
				<td>
					<input id="keyVersion" value="" class="inputtext validate['digit[1,255]']" name="keyVersion" type="text" maxlength="3"/>
				</td>
			</tr>
			<tr>
				<td class="td1">安全域支持的最大对称密钥个数:&nbsp;</td>
				<td>
					<input id="maxKeyNumber" value="" class="inputtext validate['digit[1,255]']" name="maxKeyNumber" type="text" maxlength="3"/>
				</td>
			</tr>
			</table>
			</form>
		    </div>
</div>
</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>

</body>
</html>