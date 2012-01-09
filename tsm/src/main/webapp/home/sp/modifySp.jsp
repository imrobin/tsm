<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用提供商信息修改</title>
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

<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>
<script type="text/javascript" src="${ctx}/admin/sd/js/sd.js"></script>
<script type="text/javascript" src="${ctx}/admin/sp/js/list.js"></script>
<script type="text/javascript">

EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
var ctx = '${ctx}';
var swfu;
window.addEvent('domready', function() {
	
	swfu = new SWFUpload({
		// Backend Settings
		upload_url: "${ctx}/html/commons/?m=upload",
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
	
	//load data...
	new Request.JSON({
		async : false,
		url : ctx + '/html/spBaseInfo/?m=spLoad',
		onSuccess : function(result) {
			var sp = result.message;
			$('spId').set('value', sp.id);
			$('nameOrg').set('value', sp.name);
			$('shortNameOrg').set('value', sp.shortName);
			$('registrationNoOrg').set('value', sp.registrationNo);
			$('certificateNoOrg').set('value', sp.certificateNo);
			$('legalPersonIdNoOrg').set('value', sp.legalPersonIdNo);
			$('contactPersonMobileNoOrg').set('value', sp.contactPersonMobileNo);
			$('emailOrg').set('value', sp.sysUser_email);
			var legalPersonIdType = $$('input[name=legalPersonIdType]');
			$each(legalPersonIdType, function(e , index) {
				if(sp.legalPersonIdType == e.get('value')) {
					e.set('checked','checked');
				}
			});
			
			if(sp.hasLogo == '有') {
				var src = ctx + '/html/spBaseInfo/?m=loadSpFirmLogo';
				var dd = document.getElement('dd[id=thumbnailsDd]');
				var thumbnails = document.getElement('[id=thumbnails]');
				dd.set('style', '');
				new Element('img', {src : src, alt : sp.name, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
				var logoFileName = document.getElement('input[id=logoFileName]');
				logoFileName.set('value', sp.name + '.jpg');
			}
			
			if(sp.status == 0) {
				//待审核、不可用,email可改
				
			} else {
				//正常、可用，email不可改
				$('email').set('readonly','readonly');
				$('emailExplain').appendText('只读');
				
				$('rid').set('readonly','readonly');
				$('ridExplain').appendText('只读');
			}
			addPronvince();
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
						} else {
							e.set('value', value);
						}
					}
				}
			}
			
			
		}
	}).post({spid : -1, t : new Date().getTime()});
	
	//form submit
	new FormCheck('form_spModify', {
		submit : false,
		trimValue:false,
		display : {
			showErrors : 1,
			errorsLocation : 1,
			indicateErrors : 1,
			keepFocusOnError : 0,
			closeTipsButton : 1,
			removeClassErrorOnTipClosure:1
			
		},
		onValidateSuccess : function() {
			/**/
			$('submitBtn').set('disabled', 'disabled');
			new Request.JSON({
				async : false,
				url   : ctx + '/html/spBaseInfo/?m=spModify',
				onSuccess : function(result) {
					if(result.success) {
						new LightFace.MessageBox({
							onClose : function() {
								self.location = ctx + '/home/sp/center.jsp';
							}
						}).info('修改资料的申请提交成功，请尽快提交书面资料');
					} else {
						new LightFace.MessageBox().error(result.message);
					}
					$('submitBtn').set('disabled', '');
				}
			}).post($('form_spModify').toQueryString());
			
		}
	});
	
});

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

	function validateName(el) {
		var orgValue = $('nameOrg').get('value');
		return validateItem(el, 'name', '企业名称', orgValue);
	}

	function validateShortName(el) {
		var orgValue = $('shortNameOrg').get('value');
		return validateItem(el, 'shortName', '企业简称', orgValue);
	}
	
	function validateRegistrationNo(el) {
		var orgValue = $('registrationNoOrg').get('value');
		return validateItem(el, 'registrationNo', '工商注册编号', orgValue);
	}
	
	function validateCertificateNo(el) {
		var orgValue = $('certificateNoOrg').get('value');
		return validateItem(el, 'certificateNo', '经营许可证编号', orgValue);
	}
	
	function validateLegalPersonIdNo(el) {
		var orgValue = $('legalPersonIdNoOrg').get('value');
		return validateItem(el, 'legalPersonIdNo', '证件号码', orgValue);
	}
	
	function validateContactPersonMobileNo(el) {
		var orgValue = $('contactPersonMobileNoOrg').get('value');
		return validateItem(el, 'contactPersonMobileNo', '业务联系人手机号', orgValue);
	}
	
	function validateItem(el, fieldName, fieldNameZh, orgValue) {
		var bln = false;
		new Request.JSON({
			async : false,
			url   : ctx + '/html/spBaseInfo/?m=validateField',
			onSuccess : function(result) {
				bln = result.message;
				if(!bln) {
					el.errors.push(fieldNameZh+el.value+"已经被使用");
				}
			}
		}).post({fieldName : fieldName, newValue : el.value, orgValue : orgValue});
		return bln;
	}
	
	function goBack() {
		//back(-1); 杯具的IE认不到，必须写成下面的形式才认识，还是firefox智能啊
		history.back(-1);
	}
	
	function uploadSuccessEventHandler(file, server_data) {
		var serverData = JSON.decode(server_data);
		serverData = serverData.message;
		//alert(server_data);
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
</script>

</head>
<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;信息修改</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">

<p id="userinput_t" class="userinput_t">信息修改</p>
<div id="userinput">
<form action="${ctx }/html/spBaseInfo/?m=spModify" id="form_spModify" method="post">
	<div>
		<dl>
			<dd>
				<p class="regtext">
					<span style='color: red;'>*</span>RID:
				</p>
				<p class="left inputs">
					<input tabindex="0" class="inputtext validate['required','length[10,10]','%checkHex']" maxlength="10" id="rid" name="rid" type="text"/>
				</p>
				<p class="explain left" id="ridExplain"></p>
			</dd>
			<dd id="dd_01">
				<p class="regtext">
					<span style='color: red;'>*</span>企业邮件地址:
				</p>
				<p class="left inputs">
					<input type="hidden" id="emailOrg" name="emailOrg"/>
					<input tabindex="1" class="inputtext validate['required','email','length[0,32]','%validateEmailWithoutSelf']" maxlength="32" id="email" name="email" type="text" />
				</p>
				<p class="explain left" id="emailExplain"></p>
			</dd>
			<dd id="dd_05">
				<p class="regtext"><span style='color: red;'>*</span>企业名称:</p>
				<p class="left inputs">
					<input tabindex="1" class="inputtext validate['required','length[1,50]','%validateName']" maxlength="50" id="name" name="name" type="text" />
				</p>
				<p class="explain left">
					<input type="hidden" id="nameOrg"/>
				</p>
			</dd>
			<dd id="dd_06">
				<p class="regtext"><span style='color: red;'>*</span>企业简称:</p>
				<p class="left inputs">
					<input tabindex="2" class="inputtext validate['required','length[1,25]','%validateShortName']" maxlength="25" id="shortName" name="shortName" type="text" />
				</p>
				<p class="explain left">
					<input type="hidden" id="shortNameOrg"/>
				</p>
			</dd>
		
			<dd id="dd_07">
				<p class="regtext"><span style='color: red;'>*</span>工商注册编号:</p>
				<p class="left inputs">
					<input tabindex="3" class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%validateRegistrationNo']" maxlength="32" id="registrationNo" name="registrationNo" type="text" />
					
				</p>
				<p class="explain left">
					只能输入数字或字母
					<input type="hidden" name="id" id="spId" />
					<input type="hidden" id="registrationNoOrg"/>
				</p>
			</dd>
			<dd id="dd_08">
				<p class="regtext"><span style='color: red;'>*</span>经营许可证编号:</p>
				<p class="left inputs">
					<input tabindex="4" class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%validateCertificateNo']" maxlength="32" id="certificateNo" name="certificateNo" type="text" />
				</p>
				<p class="explain left">
					只能输入数字或字母
					<input type="hidden" id="certificateNoOrg"/>
				</p>
			</dd>
			<dd id="dd_09">
				<p class="regtext">
					<span style='color: red;'>*</span>所在地:
				</p>
				<p class="left inputs">
					<select name="locationNo" id="locationNo">
						<option value="全网">全网</option>
					</select>
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style='color: red;'>*</span>应用提供商类型:</p>
				<p class="left inputs">
					<select tabindex="6" name="type" id="type">
						<option value="1">全网移动</option>
						<option value="2">本地移动</option>
						<option value="3">全网应用提供商</option>
						<option value="4">本地应用提供商</option>
					</select>
				</p>
			</dd>
			<dd id="dd_10">
				<p class="regtext"><span style='color: red;'>*</span>企业联系地址:</p>
				<p class="left inputs">
					<input tabindex="7" class="inputtext validate['required','length[1,120]']" maxlength="120" id="address"
										name="address" type="text" />
				</p>
			</dd>
			<dd id="dd_11">
				<p class="regtext"><span style='color: red;'>*</span>企业法人姓名:</p>
				<p class="left inputs">
					<input tabindex="8" class="inputtext validate['required','length[1,16]']" maxlength="16"
										id="legalPersonName" name="legalPersonName" type="text" />
				</p>
			</dd>
			<dd id="dd_12">
				<p class="regtext"><span style='color: red;'>*</span>法人证件类型:</p>
				<p class="left inputs">
					<input tabindex="9" class="inputradio" name="legalPersonIdType" type="radio"
										value="身份证" checked="checked" />身份证 <input class="inputradio"
										name="legalPersonIdType" type="radio" value="护照" />护照
				</p>
			</dd>
			<dd id="dd_13">
				<p class="regtext"><span style='color: red;'>*</span>法人证件号码:</p>
				<p class="left inputs">
					<input tabindex="10" class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%validateLegalPersonIdNo']" maxlength="32"
										id="legalPersonIdNo" name="legalPersonIdNo" type="text" />
				</p>
				<p class="explain left">
					只能输入数字或字母
					<input type="hidden" id="legalPersonIdNoOrg"/>
				</p>
			</dd>
			<dd id="dd_14">
				<p class="regtext"><span style='color: red;'>*</span>企业性质:</p>
				<p class="left inputs">
					<select tabindex="11" id="firmNature" name="firmNature">
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
				<p class="regtext"><span style='color: red;'>*</span>企业规模:</p>
				<p class="left inputs">
					<select tabindex="12" id="firmScale" name="firmScale">
						<option value="1">小型(100人以下)</option>
						<option value="2">中型(100-500人)</option>
						<option value="3">大型(500人以上)</option>
					</select>
				</p>
			</dd>
			<dd>
				<p class="regtext"><span style='color: red;'>*</span>企业LOGO:</p>
				<p class="left inputs">
					<input tabindex="16" class="inputtext validate['required']" id="logoFileName" name="logoFileName" type="text" readonly="readonly"/>
					<input type="hidden" name="logoPath" id="logoPath"/>
				</p>
				<p class="explain left">
					<div>
						<span id="spanButtonPlaceholder"></span>
					</div>
					
					<div id="divFileProgressContainer" style="display: none;"></div>
					
				</p>
			</dd>
			<dd id="thumbnailsDd" style="display: none;">
				<p class="regtext"></p>
				<p class="left inputs" id="thumbnails">
				</p>
			</dd>
			<dd id="dd_17">
				<p class="regtext"><span style='color: red;'>*</span>业务联系人姓名:</p>
				<p class="left inputs">
					<input tabindex="13" class="inputtext validate['required','length[1,16]']" maxlength="16"
										id="contactPersonName" name="contactPersonName" type="text" />
				</p>
				
			</dd>
			<dd id="dd_18">
				<p class="regtext"><span style='color: red;'>*</span>联系人手机号:</p>
				<p class="left inputs">
					<input tabindex="14" class="inputtext validate['required','length[11,11]','number','%validateContactPersonMobileNo']" maxlength="11"
							id="contactPersonMobileNo" name="contactPersonMobileNo"
							type="text" />
				</p>
				<p><input type="hidden" id="contactPersonMobileNoOrg"/></p>
			</dd>
			<dd style="">
				<p class="regtext"></p>
				<p class="left inputs">
					<input class="subutton" style="cursor: pointer;" type="submit" id="submitBtn" value="修改"/>
					<input class="subutton" style="cursor: pointer;" type="button" value="返回" onclick="goBack();"/>
				</p>
				<p></p>
				
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