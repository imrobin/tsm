<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>安全域申请</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
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

	window.addEvent('domready', function() {
		var form = document.getElement('form[id=form_apply]');

		var saveBtn = $('saveBtn');
		var rid = null;
		new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getCurrentSp',
			onSuccess : function(data) {
				rid = data.message.rid;
			}
		}).post();
		
		if(rid != null) $('rid').set('value', rid);
		
		new FormCheck(form, {
			submit : false,
			trimValue : false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 1,
				removeClassErrorOnTipClosure : 1

			},
			onValidateSuccess : function() {
				saveBtn.set('disabled', 'disabled');

				new Request.JSON({
					async : false,
					url : ctx + '/html/securityDomain/?m=apply',
					onSuccess : function(result) {
						if (result.success) {
							new LightFace.MessageBox({
								onClose : function() {
									saveBtn.set('disabled', '');
									self.location = ctx + '/home/sp/listSd.jsp';
								}
							}).info('安全域发布申请提交成功，后台审核中');
						} else {
							new LightFace.MessageBox().error(result.message);
							saveBtn.erase('disabled');
						}
					}
				}).post(form.toQueryString());

			}
		});
		$('installParamsButton').addEvent('click', function(event) {
			event.stop();
			var apply = new SecurityDomain.Apply();
			apply.createInstallParams($('installParams').get('value'));
		});
		$('aid').addEvent('blur', function() {
			$('aidTip').set('html', '');
			var aid = $('aid').get('value');
			if (aid != '') {
				var bln = checkAid($('aid'));
			}
		});

	});
</script>
</head>

<body>

	<div id="container">

		<%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;我的主页&gt;申请安全域</div>

		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu" id="usermenu">
						<%@ include file="/home/sp/menu.jsp"%>
					</div>

					<div class="userinput">

						<p id="userinput_t" class="userinput_t">申请安全域</p>
						<div id="userinput">

							<form action="" id="form_apply" method="post">
								<div>
									<dl>
										<dd id="dd_01">
											<p class="regtext">
												<span style="color: red;">*</span>AID:
											</p>
											<p class="left inputs">
												<input type="hidden" name="rid" id="rid" value=""/>
												<input class="inputtext validate['required','length[10,32]','%checkHex','%checkAid','%checkAidStartWithRid']" id="aid" name="aid" type="text" maxlength="32" />
											</p>
											<p class="explain left" id="aidTip"></p>
										</dd>
										<dd id="dd_02">
											<p class="regtext">
												<span style="color: red;">*</span>名称:
											</p>
											<p class="left inputs">
												<input class="inputtext validate['required','length[1,32]']" name="sdName" type="text" maxlength="25" />
											</p>
										</dd>
										<dd>
											<p class="regtext">
												<span style="color : red;">*</span>安全等级:
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
											<p class="regtext">
												<span style="color: red;">*</span>删除规则:
											</p>
											<p class="left inputs">
												<select id="deleteRule" name="deleteRule">
													<option value="0">自动删除</option>
													<option value="1">调用指令删除</option>
													<option value="2">不能删除</option>
												</select>
											</p>
										</dd>
										<dd>
											<p class="regtext"><span style="color: red;">*</span>安全域自身的内存空间:</p>
											<p class="left inputs">
												<input id="volatileSpace" name="volatileSpace" class="inputtext validate['required','digit[1,65535]']" maxlength="5"/>
											</p>
											<p class="explain left">byte</p>
										</dd>
										<dd>
											<p class="regtext"><span style="color: red;">*</span>安全域自身的存储空间:</p>
											<p class="left inputs">
												<input id="noneVolatileSpace" name="noneVolatileSpace" class="inputtext validate['required','digit[1,65535]']" maxlength="5"/>
											</p>
											<p class="explain left">byte</p>
										</dd>
										<dd id="dd_07">
											<p class="regtext">权限:</p>
											<p class="left inputs">
												<!-- 
												<input name="dap" type="checkbox" id="dap" value="true" />DAP验证<br />
												<input id="dapForce" name="dapForce" type="checkbox" value="true" />强制要求验证DAP<br /> 
												 -->
												<input name="token"
													type="checkbox" id="token" value="true" />委托管理<br /> <input
													name="lockCard" type="checkbox" value="true" />锁定卡<br /> <!-- <input
													name="abandonCard" type="checkbox" value="true" />废止卡<br /> -->
												<input name="cvm" type="checkbox" value="true" />管理卡CVM<br />
											</p>
										</dd>
										<dd>
											<p class="regtext">业务平台URL:</p>
											<p class="left inputs"><input class="inputtext" name="businessPlatformUrl" type="text" maxlength="255" />
											</p>
										</dd>
										<dd>
											<p class="regtext">业务平台服务名:</p>
											<p class="left inputs"><input class="inputtext" name="serviceName" type="text" maxlength="32" /></p>
										</dd>
										<dd>
											<p class="regtext">安全域密钥版本号:</p>
											<p class="left inputs">
												<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['digit[1,47]']" type="text" maxlength="2" />
											</p>
										</dd>
										<dd>
											<p class="regtext">ENC密钥:</p>
											<p class="left inputs">
												<input id="keyProfileENC" name="keyProfileENC" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
											</p>
										</dd>
										<dd>
											<p class="regtext">MAC密钥:</p>
											<p class="left inputs">
												<input id="keyProfileMAC" name="keyProfileMAC" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
											</p>
										</dd>
										<dd>
											<p class="regtext">DEK密钥:</p>
											<p class="left inputs">
												<input id="keyProfileDEK" name="keyProfileDEK" class="inputtext validate['length[32,32]','%checkHex']" type="text" maxlength="32" />
											</p>
										</dd>
										<dd>
											<p class="regtext">
												<span style="color: red;">*</span>安装参数:
											</p>
											<p class="left inputs">
												<input id="installParams" class="inputtext validate['required','length[10,512]','%checkHex']" name="installParams" type="text" maxlength="512" />
											</p>
											<p>
												<a id="installParamsButton" href="#">配置</a>
											</p>

										</dd>

										<dd>
											<p class="regtext"></p>
											<p class="left inputs">
												<input id="saveBtn" class="subutton validate['submit']" style="cursor: pointer;" type="submit" value="保存" />
											</p>
										</dd>
									</dl>
								</div>
							</form>
						</div>
					</div>
					<!-- 安全域安装参数 -->
					<div id="installParamsDiv" style="display: none">
						<form id="" title="installParamsClient" method="post">
							<button class="validate['submit']" style="display: none;"></button>
							<table class="openw">
								<tr>
									<td class="td1"><span style='color: red;'>*</span>安全域是否允许删除:&nbsp;</td>
									<td><select id="deleteSelf" name="deleteSelf">
											<option value="0" selected="selected">允许</option>
											<option value="1">不允许</option>
									</select></td>
								</tr>
								<tr>
									<td class="td1"><span style='color: red;'>*</span>安全域是否接受迁移:&nbsp;</td>
									<td><select id="transfer" name="transfer">
											<option value="1" selected="selected">接受</option>
											<option value="0">不接受</option>
									</select></td>
								</tr>
								<tr>
									<td class="td1"><span style='color: red;'>*</span>是否接受主安全域发起的应用删除:&nbsp;</td>
									<td><select id="deleteApp" name="deleteApp">
											<option value="0" selected="selected">接受</option>
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
									<td colspan="3"><input name="spacePatten" title="spacePatten" type="checkbox" id="spacePatten" value="true" />签约空间模式<br /></td>
								</tr>
								<tr>
									<td class="td1">安全域管理的内存空间:&nbsp;</td>
									<td><input id="managedVolatileSpace" value="" class="inputtext validate['required','digit[1,65535]']" name="managedVolatileSpace" type="text" maxlength="5" disabled="disabled" /></td>
									<td>byte</td>
								</tr>
								<tr>
									<td class="td1">安全域管理的存储空间:&nbsp;</td>
									<td colspan="3"><input id="managedNoneVolatileSpace" value="" class="inputtext validate['required','digit[1,4294967295]']" name="managedNoneVolatileSpace" type="text" maxlength="10" disabled="disabled" /></td>
									<td>byte</td>
								</tr>
								<tr>
									<td class="td1">安全通道协议:&nbsp;</td>
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
									<td class="td1">安全通道最大连续鉴权失败次数:&nbsp;</td>
									<td><input id="maxFailCount" value="" class="inputtext validate['digit[1,255]']" name="maxFailCount" type="text" maxlength="3" /></td>
								</tr>
								<tr>
									<td class="td1">密钥版本号:&nbsp;</td>
									<td><input id="keyVersion" value="" class="inputtext validate['digit[1,255]']" name="keyVersion" type="text" maxlength="3" /></td>
								</tr>
								<tr>
									<td class="td1">安全域支持的最大对称密钥个数:&nbsp;</td>
									<td><input id="maxKeyNumber" value="" class="inputtext validate['digit[1,255]']" name="maxKeyNumber" type="text" maxlength="3" /></td>
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