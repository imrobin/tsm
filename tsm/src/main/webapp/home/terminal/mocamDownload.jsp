<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用管理器</title>
<%@ include file="/common/meta.jsp"%>
<style type="">
#left {
	float: left;
}

#right {
	float: right;
}

a.client {
	text-decoration: under_line;
	color: #232222;
}
</style>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet"
	type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript"
	src="${ctx}/home/terminal/js/mocamDownload.js"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var download = new MocamDownload.download();
		download.getAllBrand();
		$('mobileBrand').addEvent('change', function(event) {
			download.getAllTypeByBrand($('mobileBrand').get('value'));
		});
		$('mobileType').addEvent(
				'change',
				function(event) {
					download.getPicByBrandAndType(
							$('mobileBrand').get('value'), $('mobileType').get(
									'value'));
				});
		$('downloadButton').addEvent('click', function(e) {
			var brand = $('mobileBrand').get('value');
			var type = $('mobileType').get('value');
			if (brand == '-1' || type == '-1') {
				new LightFace.MessageBox().error("请选择手机品牌和手机型号");
				return false;
			}
		});
		$('otherVersion').addEvent('click', function(e) {
			 var brand = $('mobileBrand').get('value');
			var type = $('mobileType').get('value');
			if (brand == '-1' || type == '-1') {
				new LightFace.MessageBox().error("请选择手机品牌和手机型号");
				return false;
			} else { 
				download.getHistoryVersion(brand,type);
			}
		});
	});
</script>
<style>
</style>
</head>
<body>
	<div id="container">
		<%@ include file="/common/header.jsp"%>
		<div class="curPosition">您的位置: 首页&gt;手机客户端</div>
		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx}/images/download.png" width="32" height="32" />下载中心
				</div>
				<div class="usercont">
					<div class="toPC">
						<div class="tabMain">
							<span>您的手机型号：</span> <select id="mobileBrand" name="brand"
								class="brand">
								<option value="-1">选择品牌</option>
							</select> <select id="mobileType" name="type" class="model"
								imageid="deviceImag">

								<option value="-1">选择机型</option>
							</select>
							<div class="phonePic">
								<img id="deviceImage" src="${ctx}/images/noPhone.gif" width="65"
									height="75" />
							</div>
							<br /> <br />
							<div class="getDownUrlDiv">
								<div><span>请点击获取下载链接</span><span><a id="downloadButton"
									class="buttonDown">Download</a>
								</span>
								<div/>
							</div>
							<div class="getDownUrlDiv">
							<span><a class="buttonDown" id="otherVersion">其它版本</a></span>
								<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
							</div>
						</div>
						<div></div>
						<!--  <div class="content choice">
    	<h2 class="white">通过手机平台选择下载到电脑</h2>
         <ul>
        	<li>
            	<a href="#${ctx}/html/applicationClient/?m=downloadCommonAppManager&sysType=os&sysRequirment=Android" class="btn_android">Android</a>
                <p>适用于大部分 Android平台手机</p>
            </li>
        	<li>
            	<a href="#${ctx}/html/applicationClient/?m=downloadCommonAppManager&sysType=os&sysRequirment=Ophone" class="btn_ophone">OPhone</a>
                <p>适用于大部分 OPhone平台手机</p>
            </li>

        	<li>
            	<a href="#${ctx}/html/applicationClient/?m=downloadCommonAppManager&sysType=os&sysRequirment=s60v3" class="btn_s60v3">s60v3</a>
                <p>适用于大部分 s60v3平台手机</p>
            </li>
        	<li>
            	<a href="#${ctx}/html/applicationClient/?m=downloadCommonAppManager&sysType=os&sysRequirment=s60v5" class="btn_s60v5">s60v5</a>
                <p>适用于大部分 s60v5平台手机</p>

            </li>
        	<li>
            	<a href="#${ctx}/html/applicationClient/?m=downloadCommonAppManager&sysType=os&sysRequirment=wm" class="btn_wm">Windows Mobile</a>
                <p>适用于大部分 Windows Mobile平台手机</p>
            </li>
        	<li>
            	<a href="#${ctx}/html/applicationClient/?m=downloadCommonAppManager&sysType=os&sysRequirment=Kjava" class="btn_kjava">KJava</a>

                <p>适用于大部分 KJava平台手机</p>
            </li>
        </ul>
        </div>  -->
					</div>
				</div>
			</div>
		</div>
		<%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>
