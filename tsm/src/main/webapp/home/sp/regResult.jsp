<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>注册结果</title>
<%@ include file="/common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	window.addEvent('domready', function() {
		
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/common/header.jsp"%>
		<div class="curPosition">您的位置: 首页&gt;提供商&gt;注册</div>
		<div id="main">
			<div class="div980 line_c">
				<div class="title9801"></div>
				<div class="usercont">
					<div class="reginfo">
						<span class="regf18"><img src="${ctx}/images/regf.png">恭喜您注册成功
						</span><br />
						<br /> 请提交注册时所填信息的书面材料，待审核通过后，方可登录。<br />
					</div>
				</div>
			</div>

		</div>
		<%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>