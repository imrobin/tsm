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

<link rel="stylesheet" type="text/css" href="${ctx}/lib/mooeditable/Assets/MooEditable/MooEditable.css">
<script type="text/javascript" src="${ctx}/lib/mooeditable/Source/MooEditable/MooEditable.js"></script>

<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';

	window.addEvent('domready', function() {
		//$('textarea-1').empty();
		new ServiceProvider.SummaryEdit();
	});
</script>
</head>

<body>

	<div id="container">

		<%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;我的主页&gt;编辑企业简介</div>

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

						<p id="userinput_t" class="userinput_t">编辑企业简介(1000字内)</p>
						<textarea id="textarea-1" name="editable1" style="height: 500px;width: 100%;"></textarea>
						<div id="userinput">

							<form action="" id="form_apply" method="post">
								<div>
									<dl>
										<dd>
											<p class="regtext"></p>
											<p class="left inputs">
												<input type="hidden" id="id" name="id" value="<%=request.getParameter("id") %>" />
												<input class="subutton" style="cursor: pointer;" type="button" value="保存" id="saveBtn"/>
												<input class="subutton" style="cursor: pointer;" type="button" value="返回" onclick="javascript:history.back(-1);"/>
											</p>
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