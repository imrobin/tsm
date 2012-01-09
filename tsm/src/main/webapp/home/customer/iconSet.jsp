<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户首页</title>
<%@ include file="/common/meta.jsp"%>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/crop.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />	
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/css/password-strength-meter.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />	
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>
<script type="text/javascript" src="${ctx}/lib/commons/CityPicker.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<link href="${ctx}/lib/uploadManager/default.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var rImage;
	var cal = new Customer.Cal();
	function uploadSuccess2(file, serverData) {
		try {
			var result = eval("data = " + serverData);
			cal.afterUpload(result);
		} catch (ex) {
			this.debug(ex);
		}
	}
	window.addEvent('domready', function() {
		var swfu = new SWFUpload({
			// Backend Settings
			upload_url: ctx + '/html/image/?m=upload',
			post_params: {},

			// File Upload Settings
			file_types : "*.jpg",
			file_types_description : "JPG Images",
			file_upload_limit : "0",
			file_dialog_complete_handler : fileDialogComplete,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccess2,
			upload_complete_handler : uploadComplete,
			button_image_url : ctx + "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
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
			flash_url : ctx+"/lib/uploadManager/swfupload.swf",

			custom_settings : {
				upload_target : "divFileProgressContainer"
			},
			
			// Debug Settings
			debug: false
		});

		cal.loadUserIcon();
		$('cutButton').addEvent('click',function(event){
			event.stop();
			cal.cutImage(ctx);
		});
		$('reset').addEvent('click',function(event){
			$("crop").erase('width');
			$("crop").erase('height');
			$("crop").set("src", imgSrc);
			$("crop").onload   =   function(){ 
				$('crop').setStyle('left',127 - $('crop').width/2);
				$('crop').setStyle('top',97 - $('crop').height/2);
			} 
		});
	 	
	});
</script>
<script type="text/javascript">
var rImage;
var imgSrc;
window.addEvent('domready',function() {
	$('imgW').value=$("crop").getWidth();
	$('imgH').value=$("crop").getHeight();
	var initPosition=$("crop").getPosition("dragable-holder"),totalAdded={'x':0,'y':0}, curAdded={}, initPos=$("dragable1").getPosition();
	var myMove = new Drag.Move('dragable1',{
		onStart: function(){
			initPos=$("dragable1").getPosition();
		},
		onSnap: function(el){
			el.addClass('dragging');
		},
		onComplete: function(el){
			el.removeClass('dragging');
		//	alert($('crop').width);
			curAdded = $("dragable1").getPosition();
			totalAdded.x += curAdded.x-initPos.x;
			totalAdded.y += curAdded.y-initPos.y;
			if (totalAdded.x > 82){
				totalAdded.x=	82;
			}
			if (totalAdded.x < ((127 - $('crop').width/2) -($('crop').width/2 - 45))){
				totalAdded.x=	(127 - $('crop').width/2) -($('crop').width/2 - 45);
			}
			if (totalAdded.y > 52){
				totalAdded.y=	52;
			}
			if (totalAdded.y < ((97 - $('crop').height/2) -($('crop').height/2 - 45))){
				totalAdded.y= ((97 - $('crop').height/2) -($('crop').height/2 - 45));
			}
			$('crop').setStyles({
				'position':'absolute',
				'left':totalAdded.x,
				'top':totalAdded.y,
				'z-index':0
			});
			$('dragable1').setStyles({'left':0,'top':0});
			$('imgW').value=$("crop").getWidth();
			var left = 81 - ($("crop").getStyle('left')).toInt(),top = 52 - ($("crop").getStyle('top')).toInt();
			$('imgleft').set('value',left);
			$('imgtop').set('value',top);
			$('imgH').value=$("crop").getHeight();	 
		}
	});
});
</script>
</head>
<body>
<div id="container">
		<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt个人中心&gt用户信息管理</div>
        <div id="main">
			<div class="newuserc">
				<%@include file="/home/terminal/userMenu.jsp"%>
				<div class="muneright">
					<div style="clear:both; overflow:auto; margin-bottom:-1px">
						<div class="usertitle1"><a href="${ctx}/home/customer/customerCenter.jsp">个人资料</a></div>
						<div class="usertitle1 m_l_6">
							<a href="${ctx}/home/customer/password.jsp">更改密码</a>
						</div>
						<div class="usertitle m_l_6">
							修改头像
						</div>
					</div>
			<img style="display: block;" src="${ctx}/images/userinfo.png"><div
							class="munebg1">		
			<div id="alluser">
				<p class='applistinfo'></p><img id="userlogo" src="" width="90" height="90" style=""/></dd>
			</div>
			<div class="changePtohoBox">
				<div class="changePtohoBoxInner">
					<div class="changeImageBox">
						<div id="dragable-holder" style="background:#fff; width: 260px; height: 200px; overflow: hidden;position: relative;" class="none">
							<div style="position: absolute; overflow: hidden; background-color: black; visibility: visible; opacity: 0.2;filter: alpha(opacity = 20); width: 260px; height: 52px;z-index:10"></div>
							<div style="position: absolute; overflow: hidden; background-color: black; visibility: visible; opacity: 0.2;filter: alpha(opacity = 20); height: 92px; width: 81px; top: 52px;z-index:10"></div>
							<div style="position: absolute;overflow:hidden;background-color: black;visibility: visible; opacity: 0.2;filter: alpha(opacity = 20);top:52px;width:87px;right:0px;height:92px;z-index:10"></div>
							<div style="position: absolute;overflow:hidden;background-color: black;visibility: visible; opacity: 0.2;filter: alpha(opacity = 20);bottom:0px;width:260px;height:56px;left:0px;z-index:10"></div>
							<div style="position: absolute; top: 52px; left: 81px; border: 1px solid #eee;width: 90px; height: 90px;z-index:10"></div>
							<div id="dragable1" style="position: absolute;left:0;top:0; z-index: 33; width: 260px; height: 200px;background-color:#fff;cursor:url('../../images/href.cur'),default;*cursor:url('../../images/href.cur');filter: alpha(opacity = 10);opacity: 0.1;"></div>
							<img id="crop" style="position: relative;left:0px;top:0px;" src="/css/images/blank.gif"/>
						</div>
					</div>
					<div class="bigLink none" id="bigLink">
						<a href="#" rel="zoomOut" id="zoomOut">-</a><a href="#" rel="zoomIn" id="zoomIn">+</a><a href="#" rel="reset" id="reset">重置</a>
					</div>
				</div>
				
				

	<div id="divFileProgressContainer" style="height: 75px;display:none;"></div>
	<br/>
				<div class="">
	<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
		<span id="spanButtonPlaceholder"></span>
		<br/><br/><br/>
		<br/>
		<br/>
		<span class="button"  id="cutButton" style="cursor:pointer;">&nbsp;&nbsp;&nbsp;保存图片&nbsp;&nbsp;&nbsp;<span class="buttonSmall"></span></span>
		<input type="hidden" name="callbackUrl" value="" />
	</div>
					 <form action="" id="picForm" method="get">
						<input type="hidden" name="filename" id="filename"  value=""/>
						<input type="hidden" id="imgleft" name="left" value="81"/>
						<input type="hidden" id="imgtop" name="top" value="52"/>
						<input type="hidden" id="imgW" name="imgW" />
						<input type="hidden" id="imgH" name="imgH"/><!--
						 <input id="cutButton" type="submit" class="sbbutton" style="position: absolute;bottom:0px;cursor:pointer;z-index: 99" value=""/>
				    --></form>
				</div>
				</div>
				</div>
				<img src="${ctx}/images/userinfo1.png">
				</div>
				
			</div>
			
			</div>
						<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>