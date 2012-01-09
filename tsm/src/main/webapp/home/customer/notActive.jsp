<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>注册结果</title>
<%@ include file="/common/meta.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript" ></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript" ></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
   window.addEvent('domready', function() {
	   var email = null;
	   var mobile = null;
	   new Request.JSON( {
			url : '${ctx}/html/user/?m=getCurrentUser',
			async : false,
			onSuccess : function(data) {
				if (data.success) {
					email = data.message.email;
					mobile = data.message.mobile;
					$('span_email').set('html', email);
					$('span_mobile').set('html', mobile);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	   $('email').addEvent('click',function(){
		   var url = $('span_email').get('html');
		   url = "http://mail."+url.substr(url.indexOf('@')+1,url.length);
		   window.open(url);
	   });
       $('reSendMail').addEvent('click',function(){
           $('loaderImg').setStyle('display', '');
           this.setStyle('display', 'none');
           $('emailDetail').set('html', '激活邮件发送中');
    	   new Request.JSON( {
				url : '${ctx}/html/customer/?m=sendActiveEmail',
				onSuccess : function(data) {
					$('loaderImg').setStyle('display', 'none');
					this.setStyle('display', '');
					$('emailDetail').set('html', '激活邮件');
					if (data.success) {
						new LightFace.MessageBox().info('操作成功，请访问您的邮箱');
						$('span_email').set('html', data.message);
					} else {
						new LightFace.MessageBox().error(data.message);
					}
				}.bind(this)
			}).post( {
				dns : 'http://' + self.location.host,
				email : email
			});
       });
       $('reSendSms').addEvent('click',function(){
    	   $('loaderImg2').setStyle('display', '');
           this.setStyle('display', 'none');
    	   new Request.JSON( {
    			url : '${ctx}/html/customer/?m=sendActiveSms',
    			onSuccess : function(data) {
    				$('loaderImg2').setStyle('display', 'none');
					this.setStyle('display', '');
    				if (data.success) {
    					new LightFace.MessageBox().info('操作成功，请查看您的手机');
    				} else {
    					new LightFace.MessageBox().error(data.message);
    				}
    			}.bind(this)
    		}).post( {
    			mobile : mobile
    		});
       });
       $('smsActive').addEvent('click',function(){
    	   new Request.JSON( {
    			url : '${ctx}/html/customer/?m=smsActive',
    			onSuccess : function(data) {
    				if (data.success) {
    					self.location = data.message;
    				} else {
    					new LightFace.MessageBox().error(data.message);
    				}
    			}.bind(this)
    		}).post( {
    			mobile : mobile,
    			activeCode : $('activeCode').get('value')
    		});
       });
   });
</script>
</head>

<body>
<div id="container">
<%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt;用户&gt;注册</div>
<div id="main">
<div class="div980 line_c">
<div class="title9801"></div>
<div class="usercont">
<div class="reginfo" style="width: 100%"><span class="regf18"><img src="${ctx}/images/regf.png" />恭喜您注册成功，请您进行帐户激活</span><br /><br />
<img id="loaderImg" src="${ctx}/admin/images/ajax-loader.gif" style="display: none;width: 18px;height: 18px;" /><a id="reSendMail" href="#" style="color: #ff0000;">重新获取</a><span id="emailDetail">激活邮件。</span>
您的邮箱是：<span id="span_email" class="c_r"></span><button id="email" class="jh">打开邮箱</button><br /><br />
<img id="loaderImg2" src="${ctx}/admin/images/ajax-loader.gif" style="display: none;width: 18px;height: 18px;" /><a id="reSendSms" class="r" href="#">重新下发</a> 激活短信。
您的手机号是：<span id="span_mobile" class="c_r"></span><br /><br /><input id="activeCode" name="activeCode" type="text" /><button id="smsActive" class="jh">短信激活</button>
</div>
</div>
</div>

</div>
		<%@ include file="/common/footer.jsp"%>
		</div>
</body>
</html>