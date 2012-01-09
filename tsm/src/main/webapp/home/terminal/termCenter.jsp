<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>终端管理</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css"
	rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript"
	src="${ctx}/lib/mootools/mootools-more.js"></script>
	<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/home/terminal/js/terminal.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var t = new Terminal();
		var ccid = t.getQueryValue("ccid");
		t.getCustomerTerminal();
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/common/header.jsp"%>
		<div class="curPosition">您的位置: 首页&gt;个人中心&gt;终端管理</div>
		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx}/images/user_icon_32.png" width="32" height="32" />个人中心
				</div>
				<div class="usercont">
					<%@include file="userMenu.jsp" %>
					<div class="userinput">
						<div id="titletab" class="titletab">
							<ul>
								<li class="s1">终端显示</li>
								<%-- <li class="s2"><a href="${ctx }/home/terminal/change.jsp">更换终端</a></li> --%>
							<%-- 	<li class="s2"><a href="${ctx }/home/terminal/revertApp.jsp">恢复应用</a></li> --%>
								<li class="s2"><a href="${ctx }/home/terminal/bind.jsp">绑定终端</a></li>
							</ul>
						</div>
						<div id="blankTip" style="display:none;">
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<h1>您还没有绑定任何手机</h1>
							<h1><a href="${ctx}/home/terminal/bind.jsp"><font color="blue">请点这里</font></a>&nbsp;&nbsp;&nbsp;进行绑定</h1>
						</div>
						<div id="doactive" style="display:none">
							<p class="regtext">请输入激活码</p>
							<p class="inputs2"><input id="activeInput" type="text" /></p>
							<p>如您长时间未收到激活码，请点击重新发送按钮</p>
						</div>
						<div id="changeNumberDiv" style="display:none">
							<p class="regtext">请输入新的手机号</p>
							<p class="inputs2"><input id="newMobieNo" type="text" /><input type="button" id="sendCheck" value="发送验证码"></input></p>
							<p class="regtext">请输入验证码</p>
							<p class="inputs2"><input id="checkInput" type="text" /></p>
							<p>如您长时间未收到验证码,请再次点击发送验证码按钮重试</p>
						</div>
						<div class="user_m_l_1" id="terminals">
						</div>
						<a name="001" id="001" >&nbsp;</a> 
						<div id="activeTip" style="display:none;">
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<p>&nbsp;&nbsp;&nbsp;</p>
							<h1>此终端还未激活,请先激活或者注销</h1>
						</div>
							<div id="appInfo" style="display: none;">
						<table style="border: thin;" width="600">
							<tr>
								<td rowspan="5" id="appImg"></td>
								<td width="80px"><b>&nbsp;&nbsp;应用名称 :</b>
								</td>
								<td id="ename" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;所属提供商 :</b>
								</td>
								<td id="espname" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;应用描述 :</b>
								</td>
								<td id="edesc" width="350px"></td>
							</tr>
						</table>
					</div>
						<div id="haveApp" class="appall" style="display:none;">
							<div class="title14">已安装的应用</div>
							<div class="user_m_l_1" id="appUl">
							</div>
						</div>
						<div id="haveSd" class="appall" style="display:none;">
							<div class="title14">终端上的安全域</div>
							<div class="user_m_l_1" id="sdUl">
						</div>
						</div>
						<div id="appspace" class="appall" style="display:none;">
							<div class="title14">空间信息</div>
							<div class="appallcont">
								<div class="kjtext">内存空间：</div>
								<div class="kjt">
									<p class="k1">
										容量:<br/><font id="vsTotal"></font>
									</p>
									<p class="k2">
										<span id="c1" class="c1"></span>
									</p>
									<p class="k3">
										<span>已用空间：<font id="usedVS"></font></span> <span>可用空间：<font id="existVS"></font></span>
									</p>
								</div>
							</div>
							<div class="appallcont">
							<div class="kjtext">存储空间：</div>
							<div class="kjt">
								<p class="k1">
									容量:<br/><font id="nsTotal"></font>
								</p>
								<p class="k2">
									<span id="c2" class="c2"></span>
								</p>
								<p class="k3">
									<span>已用空间：<font id="usedNS"></font> </span> <span>可用空间：<font id="existNS"></font> </span>
								</p>
							</div>
							</div>
						</div>
						<div id="blackTip" style="display:none;">
								<h1>此终端已经被列入黑名单</h1>
							</div>
						<div id="butt" class="kbutton">
							<a class="subbutt1 mag" href="#" id="active" style="display:none"><span>激活终端
							</span> </a>
							<a class="subbutt1 mag" href="#" id="cancel" style="display:none"><span>注销终端
							</span> </a>
							<a class="subbutt1 mag" href="#" id="lost" style="display:none"><span>挂失终端
							</span> </a>
							<a class="subbutt1 mag" href="#" id="changeNumber" style="display:none"><span>更换号码</span> </a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>