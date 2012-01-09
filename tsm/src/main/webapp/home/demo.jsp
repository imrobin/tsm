<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Demo</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link  href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>

<link rel="stylesheet" type="text/css" href="${ctx}/lib/mooeditable/Assets/MooEditable/MooEditable.css">
<script type="text/javascript" src="${ctx}/lib/mooeditable/Source/MooEditable/MooEditable.js"></script>

<script type="text/javascript" src="${ctx }/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		$('btn').addEvent('click', function(event) {
			var box = new LightFace.MessageBox({
				title : 'demo',
				width : 500,
				height : 500,
				buttons : [ {
					title : '保存',
					event : function() {
						var textarea = box.messageBox.getElement('[id=textarea-1]');
						alert('内容：[\n' + textarea.get('html')+'\n]');
						this.close();
					}
				}, {
					title : '关闭', 
					event : function() {
						this.close();
					}
				}]
			});
			box.options.content = $('divHidden').get('html');
			box.addEvent('open', function(event) {
				var textarea = box.messageBox.getElement('[id=textarea-1]');
				var rate = 0.93;
				var width = box.options.width * rate;
				var height = box.options.height * 0.92;
				//console.log(width);
				//console.log(height);
				textarea.setStyles({
					width : width,
					height : height
				});
				textarea.mooEditable();
			});
			box.open();
		});
	});
</script>
</head>

<body>
<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;Demo</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />Demo</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
</div>

<div class="userinput">
	
	<a class="b" href="#" id="btn">click</a>
	<!-- 
	 -->
	<div id="divHidden" style="display: none;">
		<textarea id="textarea-1" name="editable1">demo</textarea>
	</div>
</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
</body>

</html>