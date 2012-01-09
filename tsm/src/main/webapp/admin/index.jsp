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
<script type="text/javascript">
	function redirect(menuId) {
		new Request.JSON( {
			url : ctx + '/html/menu/?m=getMenu',
			onSuccess : function(data) {
				if (data.success) {
					var url = '${ctx}' + data.message.url;
					if ($chk(data.message.topMenu_id)) {
						url += '?menu=' + data.message.topMenu_id;
					}
					self.location = url;
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({'menuId' : menuId});
	}
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<div class="index">
<div class="box" style="width: 49%;margin-left: 12px;"><div class="box1"><div class="boxtitle"><img class="icon16" src="images/icon_12.png" />快速管理通道</div>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">应用提供商管理：</span><br />
<a class="h" href="javascript:redirect(82);">信息管理</a>|<a class="h" href="javascript:redirect(181);">审核</a>|<a class="h" href="javascript:redirect(341);">推荐提供商设置</a>|<a class="h" href="javascript:redirect(126);">黑名单管理</a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">安全域管理：</span><br />
<a class="h" href="javascript:redirect(102);">信息管理</a>|<a class="h" href="javascript:redirect(121);">审核</a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">应用生命周期管理 ：</span><br />
<a class="h" href="javascript:redirect(104);">审核</a>|<a class="h" href="javascript:redirect(128);">测试</a>|<a class="h" href="javascript:redirect(129);">发布 </a>|<a class="h" href="javascript:redirect(130);">归档 </a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">卡批次管理：</span><br />
<a class="h" href="javascript:redirect(401);">信息管理</a>|<a class="h" href="javascript:redirect(362);">关联加载文件 </a>|<a class="h" href="javascript:redirect(381);">关联安全域 </a>|<a class="h" href="javascript:redirect(382);">关联应用 </a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">用户终端业务管理：</span><br />
<a class="h" href="javascript:redirect(125);">黑名单管理</a>|<a class="h" href="javascript:redirect(123);">应用下载 </a>|<a class="h" href="javascript:redirect(142);">应用删除 </a>|<a class="h" href="javascript:redirect(142);">应用锁定 </a>|<a class="h" href="javascript:redirect(142);">应用解锁 </a>|<a class="h" href="javascript:redirect(142);">应用升级</a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">系统设置 ：</span><br />
<a class="h" href="javascript:redirect(84);">数据字典设置</a>|<a class="h" href="javascript:redirect(301);">手机型号设置 </a>|<a class="h" href="javascript:redirect(642);">万号段管理 </a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">权限管理 ：</span><br />
<a class="h" href="javascript:redirect(8);">用户管理</a>|<a class="h" href="javascript:redirect(9);">角色管理 </a>|<a class="h" href="javascript:redirect(4);">权限管理 </a>|<a class="h" href="javascript:redirect(6);">菜单管理 </a>|<a class="h" href="javascript:redirect(7);">URL管理 </a></p>
<p class="list">
<img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">平台日志：</span><br />
<a class="h" href="javascript:redirect(103);">业务操作日志</a>|<a class="h" href="javascript:redirect(441);">指令详细日志 </a></p>
</div>
</div>
<div class="box" style="width: 49%;"><div class="box1"><div class="boxtitle"><img class="icon16" src="images/icon_12.png" />平台简介</div>
<p class="list""><img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">NFC的技术优势</span><br />
NFC信息也是通过频谱中无线频率部分的电磁感应耦合方式传递，但两者之间还是存在很大的区别。<br />
→ 首先，NFC是一种提供轻松、安全、迅速的通信的无线连接技术，具有距离近、带宽高、能耗低等特点。<br /> 
→ 其次，NFC与现有非接触智能卡技术兼容，目前已经成为得到越来越多主要厂商支持的正式标准。<br />
→ 再次，NFC还是一种近距离连接协议，提供设备间轻松、安全、迅速的通信。是一种近距离的私密通信方式。<br />
→ 最后，NFC在门禁、公交、手机支付等领域内发挥着巨大的作用。 
</p>
<p class="list"><img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">系统使用安全注意事项</span><br />
<span>→ 使用Windows XP/Vista/Win7的用户，请打开Windows XP/Vista/Win7自带的防火墙。</span><br />
<span>→ 使用Windows XP/Vista/Win7的用户，请关闭远程功能。</span><br />
<span>→ 定期下载安装最新的操作系统和浏览器安全程序或补丁。 </span><br />
<span>→ 安装反病毒软件和防火墙软件，并及时升级更新。 </span><br />
<span>→ 长时间无人操作电脑时，请退出后台管理系统并锁定您的计算机。 </span><br />
<span>→ 请保证您的密码安全，不要告诉他人，并定期更改您的登录密码。 </span><br />
<span>→ 在进行对卡片操作时，请使用Microsoft IE浏览器。 </span>
</p>
<p class="list" style="margin-bottom: 12px;"><img class="icon16" src="images/icon_11.png" /><span style="color: #0066FF;font-weight: bold;">系统运行信息：</span><br />
<span>→ Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; QQDownload 551; CIBA)</span><br />
<span>→ Java(TM) 2 Runtime Environment, Standard Edition 1.6.0_12</span><br />
<span>→ Java HotSpot(TM) Client VM </span><br />
<span>→ Java Virtual Machine Specification </span><br />
<span>→ Oracle Hardware and Software, Engineered to Work Together</span>
</p>
</div></div>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
</body>
</html>
