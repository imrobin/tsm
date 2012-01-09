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
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<!-- 
<link href="${ctx}/lib/uploadManager/default.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/commons/CityPicker.js"></script>
 -->
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers90.js"></script>

<script type="text/javascript" src="${ctx}/lib/uploadManager/ajaxUploadFile.js"></script>

<script type="text/javascript" src="${ctx}/admin/sd/js/sd.js"></script>
<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>
<script type="text/javascript" src="${ctx}/admin/sp/js/list.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
	var swfu;
    window.addEvent('domready', function(){
    	 new ServiceProvider.sp();
    	 
    });
    (function() {
    	var rememberTitle = document.title; // 记住原有的窗口标题

    	try { // try-catch 用于兼容不支持attachEvent方法的浏览器
    		document.attachEvent('onpropertychange', function(){
    			if (document.title != rememberTitle) { // 此判断一定要加上，否则会导致递归调用堆栈溢出
    				document.title = rememberTitle;
    			}
    		});
    	} catch (e) {
    		// noop
    	}

    	document.setTitle = function(newTitle) { // 如果需要自定义修改标题，请使用此方法替代“document.title = xxx”语句
    		rememberTitle = newTitle;
    		document.title = newTitle;
    	};
    })();
    
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
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>

<!-- 弹出框 -->
<div id="tableDiv2" style="display: none;"></div>
<!-- requistion table -->
<div name="requistionDiv" style="display: none;">
	<div name="requistionTable"></div>
</div>
<!-- add -->
<div id="spDivAdd" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>

	<dd id="dd_01">
		<p class="regtext">
			<span style='color: red;'>*</span>企业邮件地址:
		</p>
		<p class="left inputs">
			<input tabindex="1" class="inputtext validate['required','email','length[0,32]','%validateEmail']" maxlength="32" id="email" name="email" type="text" />
		</p>
		<p class="explain left">默认密码：000000</p>
	</dd>
	<dd>
		<p class="regtext">
			<span style='color: red;'>*</span>RID:
		</p>
		<p class="left inputs">
			<input tabindex="3" class="inputtext validate['required','length[10,10]','%checkHex']" maxlength="10" id="rid" name="rid" type="text" />
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
			<!-- 
			<input tabindex="8" class="inputtext validate['required','length[1,16]','citypicker']"  readonly="readonly" maxlength="16" name="locationNo" id="location" type="text" />
			<input id="dns" type="hidden" name="dns" value=""></input>
			 -->
			 <select name="locationNo"></select>
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
	<!-- 
	 -->
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>企业LOGO:</p>
		<p class="left inputs">
			<input tabindex="16" class="inputtext validate['required','citypicker']" id="logoFileName" name="logoFileName" type="text" readonly="readonly" />
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
			<input tabindex="18" class="inputtext validate['required','length[11,11]','number','%checkMobileInSp']" maxlength="11"
				id="contactPersonMobileNo" name="contactPersonMobileNo"
				type="text" />
		</p>
	</dd>

</dl>
</form>
</div>
</div>

<!-- edit -->
<div id="spDivEdit" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
	<dd>
		<p class="regtext">
			<span style='color: red;'>*</span>企业编号:
		</p>
		<p class="left inputs">
			<input class="inputtext" type="text" readonly="readonly" id="no"/>
		</p>
		<p class="explain left">只读</p>
	</dd>
	<dd>
		<p class="regtext">
			<span style='color: red;'>*</span>RID:
		</p>
		<p class="left inputs">
			<input class="inputtext validate['required','length[10,10]','%checkHex']" maxlength="10" type="text" id="rid" name="rid"/>
		</p>
		<p class="explain left" id="_rid"></p>
	</dd>
	<dd id="dd_01">
		<p class="regtext">
			<span style='color: red;'>*</span>企业邮件地址:
		</p>
		<p class="left inputs" id="email">
			<input tabindex="1" class="inputtext validate['required','email','length[0,32]','%validateEmailWithoutSelf']" maxlength="32" id="email" name="email" type="text" />
		</p>
		<p class="explain left" id="_email"></p>
	</dd>

	<dd id="dd_05">
		<p class="regtext">
			<span style='color: red;'>*</span>企业名称:
		</p>
		<p class="left inputs">
			<input type="hidden" name="no" id="no"/>
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
			<!-- 
			<input tabindex="8" class="inputtext validate['required','length[1,16]','citypicker']"  readonly="readonly" maxlength="16" name="locationNo" id="location" type="text" />
			<input id="dns" type="hidden" name="dns" value=""></input>
			 --> 
			 <select name="locationNo"></select>
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
			<input tabindex="16" class="inputtext validate['required','citypicker']" id="logoFileName" name="logoFileName" type="text" readonly="readonly"/>
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
		<p class="left inputs" id="thumbnails"></p>
	</dd>
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

</dl>
</form>
</div>
</div>

</body>
</html>
