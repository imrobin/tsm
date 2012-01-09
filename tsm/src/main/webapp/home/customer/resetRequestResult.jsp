<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js" ></script>
<script type="text/javascript" src="${ctx}/lib/ie6png.js" ></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   window.addEvent('domready', function() {
	   var email = location.search.substr(7);
	   $('email').set('html',email);
	   var url = $('email').get('html');
	   url = "http://mail."+url.substr(url.indexOf('@')+1,url.length);
	   $('email').set('href',url);
   });
</script>
<style>
.s_email, a.s_email, a.s_email:link {
    color: #336699;
}
a.s_email:hover {
    color: #3366CC;
}
</style>
</head>
<body>
<div id="container">
		<%@ include file="/common/header.jsp"%>
<div id="main">
  <div class="psbg">
<div class="passtilte"><img src="../../images/key.png" />请设置新密码</div>
<div>密码重置邮件已发送到邮箱：<b><a id="email" class='s_email' href='' target='_blank'></a></b><br/>
请点击邮件中的密码重置链接，即可进行密码重置</div>
<div class="pasmail"><strong>如果半小时内收不到邮件：</strong><br />
请到邮箱的广告邮件、垃圾邮件目录下找找<br />
或联系<a class="b" href="#">在线客服</a>（工作时间为星期一至星期五09:00~18:00）,其它时间请<a class="b" href="#">留言</a><br />
</div>
</div>
</div>
		<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>