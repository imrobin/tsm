<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/security/js/login.js" type="text/javascript"></script>
<script src="${ctx}/home/app/js/appinfo.js" type="text/javascript"></script>
<script src="${ctx}/home/terminal/js/mocamDownload.js" type="text/javascript"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   var ctx = '${ctx}';
	var category1,category2, category3 ,category4;
   window.addEvent('domready', function(){
	 
	   $('userLoginTag').addEvent('click', function(){
		   $('userLoginTag').addClass('t1');
		   $('userLoginTag').removeClass('t2');
		   $('siteDirectTag').removeClass('t3');
		   $('siteDirectTag').addClass('t4');
		   $('loginUl').fade('in');
		   $('siteDirectUl').fade('out');
		   $('loginUl').setStyle('display', 'block');
		   $('siteDirectUl').setStyle('display', 'none');
		}); 
	    $('siteDirectTag').addEvent('click', function(){
		   $('userLoginTag').removeClass('t1');
		   $('userLoginTag').addClass('t2');
		   $('siteDirectTag').addClass('t3');
		   $('siteDirectTag').removeClass('t4');
		   $('siteDirectUl').fade('in');
		   $('loginUl').fade('out');
		   $('siteDirectUl').setStyle('display', 'block');
		   $('loginUl').setStyle('display', 'none');
		});
	   
		var info = new App.Info();
		info.recentlyDownLoadForIndex();
		//info.loadByAppType();
		info.loadTypeApplication();
		setInterval(function(){
			info.recentlyDownLoadForIndex();
		}, 300000);
		 $('marquee_1').onmouseover=function() {$('marquee_1').stop()}
		 $('marquee_1').onmouseout=function() {$('marquee_1').start()}
		 info.loadRecommandSp();
		 
		 var download = new MocamDownload.download();
			download.getAllBrand();
			$('mobileBrand').addEvent('change', function(event) {
				download.getAllTypeByBrand($('mobileBrand').get('value'));
			});
			$('mobileType').addEvent(
					'change',
					function(event) {
						download.getPicByBrandAndType(
								$('mobileBrand').get('value'), $('mobileType').get(
										'value'));
					});
			$('downloadButton').addEvent('click', function(e) {
				var brand = $('mobileBrand').get('value');
				var type = $('mobileType').get('value');
				if (brand == '-1' || type == '-1') {
					new LightFace.MessageBox().error("请选择手机品牌和手机型号");
					return false;
				}
			});
			$('otherVersion').addEvent('click', function(e) {
				 var brand = $('mobileBrand').get('value');
				var type = $('mobileType').get('value');
				if (brand == '-1' || type == '-1') {
					new LightFace.MessageBox().error("请选择手机品牌和手机型号");
					return false;
				} else { 
					download.getHistoryVersion(brand,type);
				}
			});
	});
</script>
<%-- <sec:authentication property="principal" var="user" />
<c:if  test="${user != 'anonymousUser'}">
<script type="text/javascript">
	window.addEvent('domready', function(){
		$('userLoginTag').setStyle('display', 'block');
		$('siteDirectTag').fireEvent('click');
		$('siteDirectTag').set('class', 't1');
		$('siteDirectTag').removeEvents('click');
	});
</script>
</c:if> --%>
</head>
<body>
<div id="container">
<%@ include file="/common/header.jsp" %>
<div id="main">
<div id="div_left">
<div><img src="${ctx}/images/ad.gif" /></div>
<div class="adtext">NFC全终端手机中的SE芯片具备多应用功能，使得手机支付、世博手机票、企业一卡通应用、VIP身份卡等越来越多的应用可以在NFC全终端上的SE芯片上实现，需要一个平台对运行于各种应用进行管理、对业务平台进行接入，并实现多种应用灵....</div>
<div class="prtitle">应用导航</div>
<div class="mt15 clear" id="appTypeList">
<%-- <div class="prlistinfo">
<div class="infotitle"><img src="${ctx}/images/icon_pay.png" />支付类</div>
<div class="pay">
<dl id="category1">
<dd><p>58*51</p><p class="text1">手机钱包</p></dd>
<dd><p>58*51</p><p class="text1">电子票</p></dd>
<dd><p>58*51</p><p class="text1">一卡通</p></dd>
<dd><p>58*51</p><p class="text1">手机招行</p></dd>
</dl>
</div>
</div>
<div class="prlistinfo ml13"><div class="infotitle"><img src="${ctx}/images/icon_fav.png" />生活类</div>
<div class="pay">
<dl  id="category2">
<!--<dd><p><img src="${ctx}/images/pepsi.gif" /></p><p class="text1">百事可乐五折优惠</p></dd>
<dd><p>58*51</p><p class="text1">肯德基优惠卷</p></dd>
<dd><p>145*25</p><p class="text1">一卡通</p></dd>
--></dl>
</div>
</div>
<div class="prlistinfo ml13"><div class="infotitle"><img src="${ctx}/images/icon_friend.png" />娱乐类</div>
<div class="pay">
<dl id="category3">
</dl>
</div></div>
<div class="prlistinfo ml13"><div class="infotitle"><img src="${ctx}/images/icon_game.png" />软件类</div>
<div class="pay">
<dl id="category4">
</dl>
</div>
</div> --%>
</div>
<div class="mt15 ">
<div class="div354"><div class="title354">活动推荐</div>
<div class="hd"><dl>
<dd><img src="${ctx}/images/hd_img.gif" /><p><a class="r" href="#"><span>下软件 赢大奖!</span></a><br />
苹果笔记本，ipad,iphone等您拿<br />时间：2011.3 - 2011.7<br />地区：全国</p></dd>
<dd><img src="${ctx}/images/hd_img.gif" /><p><span><a class="r" href="#">下软件 赢大奖!</a><br /></span>苹果笔记本，ipad,iphone等您拿<br />时间：2011.3 - 2011.7<br />地区：全国</p></dd>
<dd></dd>
</dl></div>
</div>
<div class="div354 ml14"><div class="title354_1">提供商推荐</div>
<div class="sj">
<ul id="recommendSp">
<!--<li><img src="${ctx}/images/zh.gif" />1234</li>
<li><img src="${ctx}/images/jh.gif" /></li>
<li><img src="${ctx}/images/kfc.gif" /></li>
<li><img src="${ctx}/images/csc.gif" /></li>
<li>163*47</li>
<li></li>
--></ul>
</div>
</div>
</div>
</div>
<div id="div_right">
<div class="loginbox">
<div class="loginboxt">
<ul>
<li id="userLoginTag" class="t1" style="cursor: pointer;">手机终端</li>
<li id="siteDirectTag" class="t4" style="cursor: pointer;">站点导航</li>
</ul>
</div>
<div class="loginboxi">
<table id="loginUl" class="logintab" width="208" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="2"><label>请选择您的手机型号</label></td>
    </tr>
  <tr>
    <td width="72" rowspan="2"><img id="deviceImage" src="images/mobile_c.jpg" width="58" height="74"/></td>
    <td width="136"><div id="macstyle"><select id="mobileBrand" name="brand">
								<option value="-1">选择品牌</option>
							</select></div></td>
  </tr>
  <tr>
    <td height="48">
                            <select id="mobileType" name="type">
                            <option value="-1">选择机型</option>
							</select>
							</td>
  </tr>
  <tr>
    <td colspan="2" align="center"><a id="downloadButton" href="#" class="down">DOWNLOAD</a><a id="otherVersion" href="#" class="other">其它版本</a></td>
    </tr>
</table>

<div id="siteDirectUl" class="qmenu" style="display:none;">
<ul>
<li><span class="q1">特色</span>活动  推荐  下载   评论</li>
<li><span class="q2">新手</span>注册  支付  下载   安装</li>
<li><span class="q3">娱乐</span>无限音乐   手机游戏</li>
<li><span class="q5">沟通</span>飞信  139邮箱  139说客</li>
<li class="bottom"><span class="q6">生活</span>MM  手机冲浪  手机阅读</li>
</ul>
</div>
</div>
</div>
<div class="regbutt mt12">今日更新：<strong>50</strong>个应用 共有：<strong>5000</strong>个应用<br />
  <a href="${ctx}/home/customer/reg.jsp"><img src="${ctx}/images/reg_button.png" width="235" height="71" border="0" /></a></div>
<div class="top mt12">
<%@include file="/common/topdownload.jsp" %>
</div>
<div class="top mt12">
<div class="top_title">看看大家在做什么?</div>
<div class="top_cont hgt">
<marquee id="marquee_1" direction="up" scrollAmount='1' style="width:170;height:125px;word-wrap: break-word; overflow: hidden;" ><ul id="recentlyDown"></ul></marquee>
<!--<li><span class="c_bl">巩佳知</span> 1分钟以前下载过 <a class="b" href="#">手机钱包</a></li>
<li><span class="c_bl">王理强</span> 2分钟以前下载过 <a class="b" href="#">电子现金</a></li>
<li><span class="c_bl">董祺</span> 3分钟以前下载过 <a class="b" href="#">电影票</a></li>
<li><span class="c_bl">代云川</span> 4分钟以前下载过 <a class="b" href="#">一卡通</a></li>
-->
</div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
</div>
</body>
</html>
