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
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/admin/system/js/mobileSection.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var ms = new System.MobileSection();
        	var p = document.getElement('p[class=oper1]');
        	var div = new Element('div', {
        		styles : {
        			display: 'inline-block', 
        			border: 'solid 1px #7FAAFF',
					'background-color': '#C5D9FF',
					margin:'4px 8px 0px 0px',
					padding: '2px' 
        		}
        	}).inject(p);
        	new Element('span', {id : 'spanButtonPlaceholder'}).inject(div);
        	new Element('div', {id : 'divFileProgressContainer'}).inject(p);
        	swfu = new SWFUpload({
    			// Backend Settings
    			upload_url: "${ctx}/html/mobileSection/?m=importFile",
    			post_params: {},

    			// File Upload Settings
    			file_size_limit : "10 MB",	// 2MB
    			file_types : "*.xlsx;*.xls",
    			file_types_description : "Excel表格文件",
    			file_upload_limit : "0",

    			// Event Handler Settings - these functions as defined in Handlers.js
    			//  The handlers are not part of SWFUpload but are part of my website and control how
    			//  my website reacts to the SWFUpload events.
    			//file_queue_error_handler : fileQueueError,
    			file_dialog_complete_handler : fileDialogComplete,
    			//upload_progress_handler : uploadProgress,
    			//upload_error_handler : uploadError,
    			upload_success_handler : uploadSuccess,
    			//upload_complete_handler : uploadComplete,

    			// Button Settings
    			button_image_url : "${ctx}/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
    			button_placeholder_id : "spanButtonPlaceholder",
    			button_width: 80,
    			button_height: 18,
    			button_text : '<span class="button">导入文件...</span>',
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
            var box;
            function fileDialogComplete(numFilesSelected, numFilesQueued) {
            	try {
            		if (numFilesQueued > 0) {
            			box = new LightFace( {
            				draggable : true,
            				initDraw : true,
            				title : '提示',
            				content : '<img width="16" height="16" src="${ctx}/admin/images/ajax-loader.gif" />数据导入中，请耐心等待...',
            				buttons : []
            			}).open();
            			this.startUpload();
            		}
            	} catch (ex) {
            		new LightFace.MessageBox().error('');
            	}
            }
            function uploadSuccess(file, serverData) {
            	try {
            		var result = eval("data = " + serverData);
            		box.close();
            		if (result.success) {
            			new LightFace.MessageBox({onClose : function(){ms.getGrid().load();}}).info('操作成功');
            		} else {
            			new LightFace.MessageBox({
            				width : 600,
            				onClose : function(){
            					ms.getGrid().load();
            				}}).error(result.message);
            		}
            	} catch (ex) {
            		new LightFace.MessageBox().error('');
            	}
            }
       });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;">
</div>
</div></div>
</div>
	<div style="display: inline-block; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
		<span id="spanButtonPlaceholder"></span>
	</div>
	<div id="divFileProgressContainer" style="height: 75px;"></div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="mobileSectionDiv" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext"><font color="red">*</font>省份: </p><p class="left inputs"><select class="validate['required']" name="province"></select></p></dd>
<dd><p class="regtext"><font color="red">*</font>万号段:</p><p class="left inputs"><input class="inputtext validate['required','number_nonnegative','%chckMaxLength']" maxlength="7" name="paragraph" type="text" /></p></dd>
<dd><p class="regtext">城市:</p><p class="left inputs"><input class="inputtext validate['%chckMaxLength']" maxlength="32" name="city" type="text"  /></p></dd>
<dd><p class="regtext">区号:</p><p class="left inputs"><input class="inputtext validate['number_nonnegative','%chckMaxLength']" maxlength="32" name="district" type="text"  /></p></dd>
<dd><p class="regtext">归属SCP号码:</p><p class="left inputs"><input class="inputtext validate['number_nonnegative','%chckMaxLength']" maxlength="8" name="scpNumber" type="text"  /></p></dd>
<dd><p class="regtext">SCP ID:</p><p class="left inputs"><input class="inputtext validate['number_nonnegative','%chckMaxLength']" maxlength="3" name="scpId" type="text"  /></p></dd>
<dd><p class="regtext">归属SCP名称:</p><p class="left inputs"><input class="inputtext validate['%chckMaxLength']" maxlength="64" name="scpName" type="text"  /></p></dd>
<dd><p class="regtext">彩信中心名称:</p><p class="left inputs"><input class="inputtext validate['%chckMaxLength']" maxlength="64" name="mmscenterName" type="text"  /></p></dd>
<dd><p class="regtext">彩信中心ID:</p><p class="left inputs"><input class="inputtext validate['number_nonnegative','%chckMaxLength']" maxlength="6" name="mmscenterId" type="text"  /></p></dd>
<dd><p class="regtext">启用局数据号:</p><p class="left inputs"><input class="inputtext validate['number_nonnegative','%chckMaxLength']" maxlength="3" name="officeData" type="text"  /></p></dd>
</dl>
</form>
</div>
</div>
</body>
</html>
