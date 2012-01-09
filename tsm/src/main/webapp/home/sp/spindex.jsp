<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script src="${ctx}/home/app/js/appindex.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>

<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>

<script type="text/javascript">
	//EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var name = '${param.searchName}';
	window.addEvent('domready', function() {
		new ServiceProvider.index();
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/common/header.jsp"%>
		<div class="curPosition">您的位置: 首页&gt;提供商</div>
		<div id="main">
			<div id="div_left">
				<div class="appcenter">
					<div class="title720">
						<img src="${ctx}/images/appcenter_icon_32.png" />应用提供商
					</div>
					<div class="cont">
						<div id="cont"></div>
						
						<div class="applist" id="template" style="display: none;">
							<dl>
								<dt>
									<img src="${ctx}/images/app_img.gif" />
								</dt>
								<dd>
									<div class="applisttitle">
										<h1 class="left"><!-- 简称 --></h1>
										<span class="left"></span>
										<span class="applisttitletj">
											<!-- 应用数量：12345 -->
										</span>
									</div>
									<p class="applistinfo"></p>
									<p class="applistmible">
										<!-- 全称 -->
									</p>
									<p class="applistbutton">
										<a href="#" id="href0"><img src="${ctx}/images/button_1.gif" /></a>
									</p>
								</dd>
							</dl>
						</div>
						
						<div class="nextpage">
							<div id="nextpage"></div>
						</div>
					</div>
				</div>
			</div>
			<div id="div_right">
				<div class="top">
				<%@include file="/common/topdownload.jsp" %>
				</div>
				<%@include file="/common/recommendSp.jsp" %>
			</div>
		</div>
		<%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>