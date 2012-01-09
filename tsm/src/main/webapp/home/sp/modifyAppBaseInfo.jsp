<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%><%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ include file="/common/taglibs.jsp"%><%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal" var="user" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>修改应用基本信息</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet"
	type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/commons/CityPicker.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript"
	src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/uploadManager/handlers.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.Request.js"
	type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js"
	type="text/javascript"></script>
<script src="${ctx}/home/sp/js/modifyAppBaseInfo.js"
	type="text/javascript"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/admin/sd/js/sd.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。

	var ctx = "${ctx }";
	var spId = "${user.id }";
	var applicationId = "${param.applicationId}";
	window
			.addEvent(
					"domready",
					function() {
						var info = new Application.BaseInfo();
						var pronvince = $("location");
						var options = pronvince.options;
						options.add(new Option("全网", "全网"));
						options.add(new Option("北京", "北京"));
						options.add(new Option("天津", "天津"));
						options.add(new Option("河北", "河北"));
						options.add(new Option("山西", "山西"));
						options.add(new Option("内蒙古", "内蒙古"));
						options.add(new Option("辽宁", "辽宁"));
						options.add(new Option("吉林", "吉林"));
						options.add(new Option("黑龙江", "黑龙江"));
						options.add(new Option("上海", "上海"));
						options.add(new Option("江苏", "江苏"));
						options.add(new Option("浙江", "浙江"));
						options.add(new Option("安徽", "安徽"));
						options.add(new Option("福建", "福建"));
						options.add(new Option("江西", "江西"));
						options.add(new Option("山东", "山东"));
						options.add(new Option("河南", "河南"));
						options.add(new Option("湖北", "湖北"));
						options.add(new Option("湖南", "湖南"));
						options.add(new Option("广东", "广东"));
						options.add(new Option("广西", "广西"));
						options.add(new Option("海南", "海南"));
						options.add(new Option("重庆", "重庆"));
						options.add(new Option("四川", "四川"));
						options.add(new Option("云南", "云南"));
						options.add(new Option("贵州", "贵州"));
						options.add(new Option("西藏", "西藏"));
						options.add(new Option("陕西", "陕西"));
						options.add(new Option("甘肃", "甘肃"));
						options.add(new Option("宁夏", "宁夏"));
						options.add(new Option("青海", "青海"));
						options.add(new Option("新疆", "新疆"));

						info.init();
						var validater = new FormCheck('appBaseInfoForm', {
							submit : false,
							trimValue : false,
							display : {
								showErrors : 1,
								errorsLocation : 1,
								indicateErrors : 1,
								keepFocusOnError : 0,
								closeTipsButton : 0,
								removeClassErrorOnTipClosure : 1

							},
							onValidateSuccess : function() {
								/**/
								new Request.JSON({
									url : $('appBaseInfoForm').get('action'),
									onSuccess : function(result) {
										if (result.success) {
											new LightFace.MessageBox().info("操作成功");
											self.location = ctx + "/home/sp/uploadClient.jsp?applicationVersionId=" + result.message;
										} else {
											new LightFace.MessageBox().error(result.message);
										}
									},
									onError : function(result) {
									}
								}).post($('appBaseInfoForm').toQueryString());
							}
						});
						$("submitButton").addEvent("click", function(event) {
							event.stop();
							info.submit();
						});

						$("sdModel").addEvent("change", function(event) {
							event.stop();
							info.confSd();
						});

						new SWFUpload(
								{
									// Backend Settings
									upload_url : "${ctx}/html/application/?m=uploadPcIcon",
									post_params : {},

									// File Upload Settings
									file_size_limit : "2 MB", // 2MB
									file_types : "*.jpg",
									file_types_description : "JPG Images",
									file_upload_limit : "0",

									// Event Handler Settings - these functions as
									// defined in Handlers.js
									// The handlers are not part of SWFUpload but are
									// part of my website and control how
									// my website reacts to the SWFUpload events.
									file_queue_error_handler : info.pcIconOversize,
									file_dialog_complete_handler : fileDialogComplete,
									upload_progress_handler : uploadProgress,
									upload_error_handler : uploadError,
									upload_success_handler : info.uploadPcIconCallback.bind(info),
									upload_complete_handler : uploadComplete,

									// Button Settings
									button_image_url : "${ctx}/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
									button_placeholder_id : "pcIconButtonPlaceholder",
									button_width : 180,
									button_height : 18,
									button_text : '<span class="button">请选择图片<span class="buttonSmall">(2 MB 最大)</span></span>',
									button_text_style : '.button { font-family: "微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
									button_text_top_padding : 0,
									button_text_left_padding : 18,
									button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
									button_cursor : SWFUpload.CURSOR.HAND,

									// Flash Settings
									flash_url : "${ctx}/lib/uploadManager/swfupload.swf",

									custom_settings : {
										upload_target : "pcIconProgressContainer"
									},

									// Debug Settings
									debug : false
								});

						new SWFUpload(
								{
									// Backend Settings
									upload_url : "${ctx}/html/application/?m=uploadMobileIcon",
									post_params : {},

									// File Upload Settings
									file_size_limit : "15 KB", // 2MB
									file_types : "*.jpg",
									file_types_description : "JPG Images",
									file_upload_limit : "0",

									// Event Handler Settings - these functions as
									// defined in Handlers.js
									// The handlers are not part of SWFUpload but are
									// part of my website and control how
									// my website reacts to the SWFUpload events.
									file_queue_error_handler : info.mobileIconOversize,
									file_dialog_complete_handler : fileDialogComplete,
									upload_progress_handler : uploadProgress,
									upload_error_handler : uploadError,
									upload_success_handler : info.uploadMobileIconCallback.bind(info),
									upload_complete_handler : uploadComplete,

									// Button Settings
									button_image_url : "${ctx}/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
									button_placeholder_id : "mobileIconButtonPlaceholder",
									button_width : 180,
									button_height : 18,
									button_text : '<span class="button">请选择图片<span class="buttonSmall">(15 KB 最大)</span></span>',
									button_text_style : '.button { font-family: "微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
									button_text_top_padding : 0,
									button_text_left_padding : 18,
									button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
									button_cursor : SWFUpload.CURSOR.HAND,

									// Flash Settings
									flash_url : "${ctx}/lib/uploadManager/swfupload.swf",

									custom_settings : {
										upload_target : "mobileIconProgressContainer"
									},

									// Debug Settings
									debug : false
								});

						new SWFUpload(
								{
									// Backend Settings
									upload_url : "${ctx}/html/application/?m=uploadAppliationImage",
									post_params : {},

									// File Upload Settings
									file_size_limit : "2 MB", // 2MB
									file_types : "*.jpg",
									file_types_description : "JPG Images",
									file_upload_limit : "0",

									// Event Handler Settings - these functions as
									// defined in Handlers.js
									// The handlers are not part of SWFUpload but are
									// part of my website and control how
									// my website reacts to the SWFUpload events.
									file_queue_error_handler : info.applicationImgOversize,
									file_dialog_complete_handler : fileDialogComplete,
									upload_progress_handler : uploadProgress,
									upload_error_handler : uploadError,
									upload_success_handler : info.uploadAppliationImageCallback.bind(info),
									upload_complete_handler : uploadComplete,

									// Button Settings
									button_image_url : "${ctx}/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
									button_placeholder_id : "applicationImgButtonPlaceholder",
									button_width : 180,
									button_height : 18,
									button_text : '<span class="button">请选择图片<span class="buttonSmall">(2 MB 最大)</span></span>',
									button_text_style : '.button { font-family: "微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
									button_text_top_padding : 0,
									button_text_left_padding : 18,
									button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
									button_cursor : SWFUpload.CURSOR.HAND,

									// Flash Settings
									flash_url : "${ctx}/lib/uploadManager/swfupload.swf",

									custom_settings : {
										upload_target : "applicationImgProgressContainer"
									},

									// Debug Settings
									debug : false
								});
					});
</script>
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
					<div class="usermenu"><%@ include file="/home/sp/menu.jsp"%>
					</div>

					<div class="userinput">
						<form id="appBaseInfoForm"
							action="${ctx}/html/application/?m=modfiyApplication">
							<input name="applicaitonId" value="${param.applicationId }"
								type="hidden" />
							<table>
								<tbody>
									<tr>
										<td class="regtext"><span style="color: red">*</span>应用名称:</td>
										<td class="left inputs"><input
											class="inputtext validate['required']" name="name"
											type="text" maxlength="50" />
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>应用类型:</td>
										<td class="left inputs"><select name="form"><option
													value="1">CMS2AC应用</option>
												<option value="2">MIFARE应用</option>
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>应用AID:</td>
										<td class="left inputs"><input
											class="inputtext validate['required','length[10,32]','%checkHex','%checkAid']"
											name="aid" type="text" maxlength="32" disabled="disabled" />
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>应用描述:</td>
										<td class="left inputs"><textarea
												class="inputtext validate['required','length[0,256]']"
												name="description"></textarea>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>业务类型:</td>
										<td class="left inputs"><select id="fristType"></select><select
											id="secondType" disabled="disabled" name=applicationTypeId></select>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>个人化类型:</td>
										<td class="left inputs"><select name="personalType">
												<option value=0>不需要个人化</option>
												<option value=1>指令透传</option>
												<option value=2>应用访问安全域</option>
												<option value=3>安全域访问应用</option>
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>个人化指令传输加密算法:</td>
										<td class="left inputs"><select name="persoCmdTransferSA">
												<option value=81>3DES-ECB</option>
												<option value=82>3DES-CBC</option>
												<option value=84>DES-CBC</option>
												<option value=88>AES</option>
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>个人化指令敏感数据加密算法:</td>
										<td class="left inputs"><select
											name="persoCmdSensitiveDataSA">
												<option value=81>3DES-ECB</option>
												<option value=82>3DES-CBC</option>
												<option value=84>DES-CBC</option>
												<option value=88>AES</option>
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>是否需要订购:</td>
										<td class="left inputs"><select name="needSubscribe"><option
													value="false">不需要</option>
												<option value="true">需要</option>
										</select></td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>预置收费条件:</td>
										<td class="left inputs"><select
											name="presetChargeCondition"><option value=1>注册后计费</option>
												<option value=2>订购后计费</option>
										</select></td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>所属安全域模式:</td>
										<td class="left inputs"><select id="sdModel"
											name="sdModel">
												<option value=1>主安全域</option>
												<option value=2>代理安全域</option>
												<option value=4>委托安全域</option>
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext">所属安全域:</td>
										<td class="left inputs"><select id="sdId" name="sdId"
											style="text-align: center;">
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>业务平台URL:</td>
										<td class="left inputs"><input
											class="inputtext validate['required']"
											name="businessPlatformUrl" type="text" maxlength="255" /></td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>业务平台服务名:</td>
										<td class="left inputs"><input
											class="inputtext validate['required']" maxlength="32"
											name="serviceName" type="text" />
										</td>
									</tr>
									<tr>
										<td class="regtext"><span style="color: red">*</span>删除规则:</td>
										<td class="left inputs"><select name="deleteRule">
												<option value=0>不能删除</option>
												<option value=1>全部删除</option>
												<option value=2>只删除数据</option>
										</select>
										</td>
									</tr>
									<tr>
										<td class="regtext">所在地:</td>
										<td class="left inputs"><select id="location"
											name="location" />
										</td>
									</tr>
									<tr id="pcIconUpload">
										<td class="regtext">PC版图标:</td>
										<td class="left inputs"><input name="pcIconFileName"
											type="text" class="inputtext" readonly="readonly" /><span
											class="left explain">支持格式.jpg，建议尺寸128*128</span></td>
										<td style="vertical-align: top;">
											<div
												style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
												<span id="pcIconButtonPlaceholder"></span>
											</div>
											<div id="pcIconProgressContainer"
												style="height: 75px; display: none;"></div>
										</td>
									</tr>
									<tr id="pcIconDisplay">
										<td class="regtext"></td>
										<td class="left inputs"><img id="pcIconImg"
											style="width: 128px; height: 128px" /><input
											id="pcIconTempDir" class="inputtext" name="pcIconTempDir"
											type="hidden" /><input class="inputtext"
											name="pcIconTempFileAbsPath" type="hidden" />
										</td>
									</tr>
									<tr id="mobileIconUpload">
										<td class="regtext">手机版图标:</td>
										<td class="left inputs"><input name="mobileIconFileName"
											type="text" class="inputtext" readonly="readonly" /><span
											class="left explain">支持格式.jpg，尺寸必须是50*50</span></td>
										<td style="vertical-align: top;">
											<div
												style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
												<span id="mobileIconButtonPlaceholder"></span>
											</div>
											<div id="mobileIconProgressContainer"
												style="height: 75px; display: none;"></div>
										</td>
									</tr>
									<tr id="mobileIconDisplay">
										<td class="regtext"></td>
										<td class="left inputs"><img id="mobileIconImg"
											style="width: 50px; height: 50px" /><input class="inputtext"
											name="mobileIconTempDir" type="hidden" /><input
											class="inputtext" name="mobileIconTempFileAbsPath"
											type="hidden" />
										</td>
									</tr>
									<tr id="applicationImgUpload">
										<td class="regtext">应用截图:</td>
										<td class="left inputs"><input
											name="applicationImgFileName" type="text" class="inputtext"
											readonly="readonly" /><span class="left explain">支持格式.jpg，建议尺寸190*280（可以上传多张）</span>
										</td>
										<td style="vertical-align: top;">
											<div
												style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
												<span id="applicationImgButtonPlaceholder"></span>
											</div>
											<div id="applicationImgProgressContainer"
												style="height: 75px; display: none;"></div>
										</td>
									</tr>
									<tr id="applicationImgDisplay"
										style="display: none; width: 500">
										<td id="applicationImgTd" colspan="3" style="width: 500px"><input
											class="inputtext" name="applicationImgTempDir" type="hidden" /><input
											class="inputtext" name="applicationImgTempFileAbsPath"
											type="hidden" />
										</td>
									</tr>
								</tbody>
								<tfoot>
									<tr class="s" style="width: 600px;">
										<td colspan="3"><a class="subbutt validate['submit']"
											href="#" style="float: right;"><span id="submitButton">提交</span>
										</a>
										</td>
									</tr>
								</tfoot>
							</table>
						</form>
						<p id="userinput_t" class="userinput_t"></p>
						<div id="userinput" style=""></div>

					</div>

				</div>

			</div>
			<%@ include file="/common/footer.jsp"%></div>
	</div>
	<select style="display: none" id="sdSelectTemplate">
		<option title="disableTemplate" style="width: 300px;">请选择</option>
		<option title="enableTemplate" style="width: 300px; text-align: left;"></option>
	</select>
</body>
</html>