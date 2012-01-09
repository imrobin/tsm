<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>应用提供商注册</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/commons/CityPicker.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/ajaxUploadFile.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/uploadManager/default.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers90.js"></script>

<script type="text/javascript" src="${ctx}/admin/sd/js/sd.js"></script>
<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var swfu;
	var ctx = '${ctx}';
	var hasClick= false;
	var formCheck;
	window.addEvent('domready', function() {
		var cal = new Customer.Cal();
		swfu = new SWFUpload({
			// Backend Settings
			upload_url: ctx + "/html/commons/?m=upload",
			post_params: {},

			// File Upload Settings
			file_size_limit : "2 MB",	// 2MB
			file_types : "*.jpg;*.png",
			file_types_description : "JPG Images",
			file_upload_limit : "0",

			// Event Handler Settings - these functions as defined in Handlers.js
			//  The handlers are not part of SWFUpload but are part of my website and control how
			//  my website reacts to the SWFUpload events.
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccessEventHandler,
			upload_complete_handler : uploadComplete,

			// Button Settings
			button_image_url : "${ctx}/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
			button_placeholder_id : "spanButtonPlaceholder",
			button_width: 180,
			button_height: 18,
			button_text : '<span class="button">请选择图片<span class="buttonSmall">(2 MB 最大)</span></span>',
			button_text_style : '.button { font-family: "微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
			button_text_top_padding: 0,
			button_text_left_padding: 18,
			button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			button_cursor: SWFUpload.CURSOR.HAND,
			
			// Flash Settings
			flash_url : "${ctx}/lib/uploadManager/swfupload.swf",

			custom_settings : {
				upload_target : "divFileProgressContainer"
			},
			
			// Debug Settings
			debug: false
		});		
		
		$('email').addEvent('blur', function(event) {
			var reg=/^([a-zA-Z0-9_\.\-\+%])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			if(reg.test($('email').get('value').trim())){
				cal.checkEmail(ctx, $('email').get('value'));
			}
		});
		$('captcha').addEvent('click',function(event) {
			event.stop();
			var timestamp = (new Date()).valueOf();
			$('captchaImage').set('src',ctx + '/j_captcha_get?t=' + timestamp);
        });
		$('captchaImage').addEvent('click',function(event) {
			var timestamp = (new Date()).valueOf();
			$('captchaImage').set('src',ctx + '/j_captcha_get?t=' + timestamp);
		});
		formCheck = new FormCheck("form_id", {
			submit:false,
			trimValue:false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure:1
				
			},
			onValidateSuccess : function() {
				/**/
				new Request.JSON({
					async : false,
					url : $('form_id').get('action'),
					onSuccess : function(response) {
						var object = response;
						if(object.success) {
							self.location = ctx + '/home/sp/regResult.jsp';
						} else {
							new LightFace.MessageBox().error(object.message);
							cal.refreshImage();
							hasClick = true;
						}
					},
					onError : function(response) {
						
					}
				}).post($('form_id').toQueryString());
				/* if (!hasClick){
					hasClick = true;
				} */
			},
			onValidateFailure : function() {
				cal.refreshImage();
				hasClick = false;
			}
			
		});

		$('password').addEvent('keyup', function(event) {
			if($('password').get('value').length>0){
				cal.passwordStrength($('password').get('value'));
			}else{
				$('passwordDescription').set('html',"");         
				$('passwordStrength').set('class','');
			}
			//$('dns').set('value',location.protocol+"//"+location.host);
		});
		
		$('spReg').addEvent('click', function() {
			self.location=ctx+"/home/customer/reg.jsp";
		});
		
		addPronvince();
	});
	
	function uploadSuccessEventHandler(file, server_data) { 
		var serverData = JSON.decode(server_data); 
		serverData = serverData.message;
		
		var tempRalFile = '/' + serverData.tempRalFilePath;
		var src = ctx + tempRalFile;
		var filename = serverData.fileName;
		$('logoFileName').set('value', '');
		$('logoFileName').set('value', filename);
		$('logoPath').set('value', tempRalFile);
		
		var thumbnails = $('thumbnails');
		thumbnails.empty();
		new Element('img', {src : src, alt : filename, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
		
		var dd = document.getElement('dd[id=thumbnailsDd]');
		dd.set('style', '');
	}
	
	//下拉框
	function addPronvince() {
		var pronvince = $('locationNo');
		var options = pronvince.options;
		options.add(new Option("北京","北京"));
		options.add(new Option("天津","天津"));
		options.add(new Option("河北","河北"));
		options.add(new Option("山西","山西"));
		options.add(new Option("内蒙古","内蒙古"));
		options.add(new Option("辽宁","辽宁"));
		options.add(new Option("吉林","吉林"));
		options.add(new Option("黑龙江","黑龙江"));
		options.add(new Option("上海","上海"));
		options.add(new Option("江苏","江苏"));
		options.add(new Option("浙江","浙江"));
		options.add(new Option("安徽","安徽"));
		options.add(new Option("福建","福建"));
		options.add(new Option("江西","江西"));
		options.add(new Option("山东","山东"));
		options.add(new Option("河南","河南"));
		options.add(new Option("湖北","湖北"));
		options.add(new Option("湖南","湖南"));
		options.add(new Option("广东","广东"));
		options.add(new Option("广西","广西"));
		options.add(new Option("海南","海南"));
		options.add(new Option("重庆","重庆"));
		options.add(new Option("四川","四川"));
		options.add(new Option("云南","云南"));
		options.add(new Option("贵州","贵州"));
		options.add(new Option("西藏","西藏"));
		options.add(new Option("陕西","陕西"));
		options.add(new Option("甘肃","甘肃"));
		options.add(new Option("宁夏","宁夏"));
		options.add(new Option("青海","青海"));
		options.add(new Option("新疆","新疆"));
	}
</script>

</head>
<body>

	<div id="container">

		<%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;提供商&gt;注册</div>

		<div id="main">
			<div class="div980">
				<div class="reg_t_2">
					<ul>
						<li id="spReg" class="t2" style="cursor: pointer;">普通用户注册</li>
						<li class="t1">应用提供商注册</li>
					</ul>
				</div>
				<div class="regcont">

					<form action="${ctx }/html/spBaseInfo/?m=spRegister" id="form_id" method="post">
						<dl>
							<dd id="dd_01">
								<p class="regtext">
									<span style='color: red;'>*</span>企业邮件地址:
								</p>
								<p class="left inputs">
									<input tabindex="1" class="inputtext validate['required','email','length[0,32]']" maxlength="32" id="email" name="email" type="text" />
								</p>
								<div id="email_tip">
									<p class="explain left">作为登录账号，不允许修改。建议使用139邮箱，点击<a href="http://mail.10086.cn/register/" target="_blank">注册</a></p>
								</div>
							</dd>
							<dd id="dd_02">
								<p class="regtext">
									<span style='color: red;'>*</span>登录密码:
								</p>
								<p class="left inputs">
									<input tabindex="2" class="inputtext validate['required','length[6,32]']" maxlength="32" name="password"
										id="password" type="password" />
								</p>
								<p class="explain left">
								    <div id="passwordStrength"></div>
									<div id="passwordDescription"></div>
								</p>
							</dd>
							<dd id="dd_03">
								<p class="regtext">
									<span style='color: red;'>*</span>重复登录密码:
								</p>
								<p class="left inputs">
									<input tabindex="3" class="inputtext validate['confirm:password']" maxlength="32" name="confirm" type="password" />
								</p>
							</dd>
							<dd>
								<p class="regtext">
									<span style='color: red;'>*</span>RID:
								</p>
								<p class="left inputs">
									<input tabindex="4" class="inputtext validate['required','length[10,10]','%checkHex']" maxlength="10" id="rid" name="rid" type="text" />
								</p>
							</dd>
							<dd id="dd_05">
								<p class="regtext">
									<span style='color: red;'>*</span>企业名称:
								</p>
								<p class="left inputs">
									<input tabindex="4" class="inputtext validate['required','length[1,50]','%checkFullName']" maxlength="50" id="name" name="name" type="text" />
								</p>
							</dd>
							<dd id="dd_06">
								<p class="regtext">
									<span style='color: red;'>*</span>企业简称:
								</p>
								<p class="left inputs">
									<input tabindex="5" class="inputtext validate['required','length[1,25]','%checkShortName']" maxlength="25" id="shortName" name="shortName" type="text" />
								</p>
							</dd>
							<dd id="dd_07">
								<p class="regtext">
									<span style='color: red;'>*</span>工商注册编号:
								</p>
								<p class="left inputs">
									<input tabindex="6" class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%checkRegistrationNo']" maxlength="32" id="registrationNo" name="registrationNo" type="text" />
								</p>
								<p class="explain left">只能输入数字或字母</p>
							</dd>
							<dd id="dd_08">
								<p class="regtext">
									<span style='color: red;'>*</span>经营许可证编号:
								</p>
								<p class="left inputs">
									<input tabindex="7" class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%checkCertificateNo']" maxlength="32" id="certificateNo" name="certificateNo" type="text" />
								</p>
								<p class="explain left">只能输入数字或字母</p>
							</dd>
							<dd id="dd_09">
								<p class="regtext">
									<span style='color: red;'>*</span>所在地:
								</p>
								<p class="left inputs">
									<!-- <input tabindex="8" class="inputtext  validate['required','citypicker']"  readonly="readonly"  maxlength="16" name="locationNo" id="location" type="text" />
									<input id="dns" type="hidden" name="dns" value=""></input> -->
									<select name="locationNo" id="locationNo">
										<option value="全网">全网</option>
									</select>
								</p>
							</dd>
							<dd>
								<p class="regtext">应用提供商类型:</p>
								<p class="left inputs">
									<select tabindex="9" name="type">
										<option value="1">全网移动</option>
										<option value="2">本地移动</option>
										<option value="3">全网应用提供商</option>
										<option value="4">本地应用提供商</option>
									</select>
								</p>
							</dd>
							<dd id="dd_10">
								<p class="regtext">
									<span style='color: red;'>*</span>企业联系地址:
								</p>
								<p class="left inputs">
									<input tabindex="10" class="inputtext validate['required','length[1,120]']" maxlength="120" id="address"
										name="address" type="text" />
								</p>
							</dd>
							<dd id="dd_11">
								<p class="regtext">
									<span style='color: red;'>*</span>企业法人姓名:
								</p>
								<p class="left inputs">
									<input tabindex="11" class="inputtext validate['required','length[1,16]']" maxlength="16"
										id="legalPersonName" name="legalPersonName" type="text" />
								</p>
							</dd>
							<dd id="dd_12">
								<p class="regtext">
									<span style='color: red;'>*</span>法人证件类型:
								</p>
								<p class="left inputs">
									<input tabindex="12" class="inputradio" name="legalPersonIdType" type="radio"
										value="身份证" checked="checked" />身份证 <input class="inputradio"
										name="legalPersonIdType" type="radio" value="护照" />护照
								</p>
							</dd>
							<dd id="dd_13">
								<p class="regtext">
									<span style='color: red;'>*</span>法人证件号码:
								</p>
								<p class="left inputs">
									<input tabindex="13" class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%checkLegalPersonIdNo']" maxlength="32"
										id="legalPersonIdNo" name="legalPersonIdNo" type="text" />
								</p>
								<p class="explain left">只能输入数字或字母</p>
							</dd>
							<dd id="dd_14">
								<p class="regtext">
									<span style='color: red;'>*</span>企业性质:
								</p>
								<p class="left inputs">
									<select tabindex="14" name="firmNature" class="validate['required']">
										<option>请选择</option>
										<option value="1">国有</option>
										<option value="2">合作</option>
										<option value="3">合资</option>
										<option value="4">独资</option>
										<option value="5">集体</option>
										<option value="6">私营</option>
										<option value="7">个体工商户</option>
										<option value="8">报关</option>
										<option value="9">其他</option>
									</select>
								</p>
							</dd>
							<dd id="dd_15">
								<p class="regtext">
									<span style='color: red;'>*</span>企业规模:
								</p>
								<p class="left inputs">
									<select tabindex="15" name="firmScale" class="validate['required']">
										<option>请选择</option>
										<option value="1">小型(100人以下)</option>
										<option value="2">中型(100-500人)</option>
										<option value="3">大型(500人以上)</option>
									</select>
								</p>
							</dd>
							<dd>
								<p class="regtext"><span style='color: red;'>*</span>企业LOGO:</p>
								<p class="left inputs">
									<input tabindex="16" class="inputtext validate['required', 'citypicker']" id="logoFileName" name="logoFileName" type="text" readonly="readonly"/>
									<input type="hidden" name="logoPath" id="logoPath"/>
								</p>
								<p class="explain left">
									<div>
										<span id="spanButtonPlaceholder"></span>
										<!-- 
										<span class="button" id="clearBtn" style="cursor:pointer;">取消<span class="buttonSmall"></span></span>
										 -->
									</div>
									
									<div id="divFileProgressContainer" style="display: none;"></div>
									
								</p>
							</dd>
							<dd id="thumbnailsDd" style="display: none;">
								<p class="regtext"></p>
								<p class="left inputs" id="thumbnails">
								</p>
							</dd>
							<!-- file upload begin 
							
							<dd id="pcIconUpload">
							<p class="regtext">企业LOGO:</p>
							<p class="left inputs">
								<input tabindex="16" id="pcIconFile" class="inputtext" name="file" type="file" />
								<a class="subbutt" href="#" style="display: none"><span id="pcIconButton">上传</span></a></p>
							</dd>
							<dd id="pcIconDisplay" style="display: none">
							<p class="regtext"></p>
							<p class="left inputs">
								<img id="pcIconImg"  width="128" height="128" src="" alt="" />
								<input id="pcIconTempDir" class="inputtext" name="tempDir" type="hidden" />
								<input id="pcIconTempFileAbsPath" class="inputtext" name="logoPath" type="hidden" />
							</p>
							</dd>
							-->
							<!-- file upload end -->
							<dd id="dd_17">
								<p class="regtext">
									<span style='color: red;'>*</span>业务联系人姓名:
								</p>
								<p class="left inputs">
									<input tabindex="17" class="inputtext validate['required','length[1,16]']" maxlength="16"
										id="contactPersonName" name="contactPersonName" type="text" />
								</p>
							</dd>
							<dd id="dd_18">
								<p class="regtext">
									<span style='color: red;'>*</span>联系人手机号:
								</p>
								<p class="left inputs">
									<input tabindex="18" class="inputtext validate['required','length[11,11]','number','%checkContactPersonMobileNo']" maxlength="11"
										id="contactPersonMobileNo" name="contactPersonMobileNo"
										type="text" />
								</p>
							</dd>
							<dd id="dd_19">
								<p class="regtext">
									<span style='color: red;'>*</span>验证码:
								</p>
								<p class="left inputs">
									<input tabindex="19" class="inputtext validate['required']" id="j_captcha_response" name="j_captcha_response" type="text" />
								</p>
								<img id="captchaImage"
								title="看不清楚?点击图片更换"
								style="vertical-align: middle; border: none; cursor: pointer;"
								src="${ctx}/j_captcha_get" />
								<p><a id="captcha" href="${ctx}/j_captcha_get?t=1">看不清楚?点击更换</a></p>	
							</dd>
							<dd id="dd_20">
								<p class="regtext"></p>
								<p class="left inputs">
									<label>
										<input tabindex="20" class="inputcheckbox validate['required']" type="checkbox" />已阅读并接受
										<a class="b" href="${ctx }/home/customer/protocol.jsp" target="_blank">注册条款</a>
									</label>
								</p>
							</dd>
							<!-- -->
							<dd id="dd_21" class="s" style="width: 600px;">
								<input class="subutton" type="submit" id="submitBtn" value="创建账户" />
							</dd>

						</dl>
					</form>
				</div>
			</div>
		</div>


		<%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>