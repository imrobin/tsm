<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/home/app/js/star.css" rel="stylesheet"	type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js"	type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"	type="text/javascript"></script>
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/home/app/js/appinfo.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet"	type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js"	type="text/javascript"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript">
	var ctx = '${ctx}';
	var id = '${param.id}';
	var cardAppId = '${param.cardAppId}';
	var obj;
	var info;
	window.addEvent('domready', function() {
		info = new App.Info();
		info.loadApplication();
		info.recentlyDownLoad();
		info.loadComment();
		info.getConstants();
		info.initDownloadClient();
		info.loadAppImg();
		info.initCommentBox();
	});
	function closeBox(){
		obj.appDownBox.close();
	}
</script>
</head>
<body>
<div id="container"><%@ include file="/common/header.jsp"%>
<div class="curPosition">您的位置: 首页&gt;应用中心&gt;应用详情</div>
<div id="main">
<div id="div_left">
<div class="appcenter">
<div class="title720" id="name"></div>
<div class="cont">
<div class="apptext">
<div class="appimg">
<p id="appImg"></p>
<span id="avgInTen"></span></div>
<p class="apptextinfo" id="info"></p>
<p class="donw" id="downloadP"><a href="#" id="downloadApp"><img  style='border:0px'
	src="${ctx}/images/donwapp.gif" width="107" height="33" /></a><a href="#"  id="downloadClient"><img  style='display:none' 
	class="ml" src="${ctx}/images/donwapp1.gif" width="123" height="33" /></a></p>
</div>
<div class="appttitle">应用截图</div>
<div class="apptbox3">
<ul id="appImgUl">
</ul>
</div>

<div class="apptbox2">
<div class="appttitle">最近两周下载用户</div>
<dl id="alluser">
	<!--
	<dd id="user0"></dd>
	<dd id="user1"></dd>
	<dd id="user2"></dd>
	<dd id="user3"></dd>
	<dd id="user4"></dd>
	<dd id="user5"></dd>-->
</dl>
</div>
<div class="apptbox">
<p class="appttitle">用户评论
<div><a class="subbutt2" href="javascript:void(0)" id="letmetalk"><span>我也来说说</span></a></div>
</p>

<div id="commentrecord">
</div>
<div class="nextpage">
<div id="nextPageComment"></div>
</div>

<div id="commentDiv2" style="display: none; top: -330px;"></div>

</div>
</div>
</div>
</div>
<div id="div_right">
<div class="top"><%@include file="/common/topdownload.jsp"%>

</div>
<%@include file="/common/recommendApplication.jsp"%>
</div>
</div>
<%@include file="/common/footer.jsp"%>
<div id="commentFloatDiv" style="display:none;overflow-x: hidden;">
<div class="shop-rating" id="starDiv" style="overflow-x: hidden;"><span class="title">评分：</span>
<ul class="rating-level" id="stars1" >
	<li><a id="star1"  class="one-stars" star:value="1" href="#">1</a></li>
	<li><a id="star2"  class="two-stars" star:value="2" href="#">2</a></li>
	<li><a id="star3"  class="three-stars" star:value="3" href="#">3</a></li>
	<li><a id="star4"  class="four-stars" star:value="4" href="#">4</a></li>
	<li><a id="star5"  class="five-stars" star:value="5" href="#">5</a></li>
</ul>
<span class="result" id="stars1-tips"></span></div>
<form action="" id="commentForm" name="commentForm" method="post">
<input type="hidden" id="applicationId" name="applicationId" value="${param.id}" />
<input type="hidden" id="contentEncode" name="contentEncode" value="" />
<div id="commentDiv" style="overflow-x: hidden;">
<p >
<!--<textarea rows="8" id="content" name="content"
	style="resize: none;">
	</textarea>
	-->
	<textarea  id="content" name="content" style="resize: none;width:400px;height:200px;text-align:left;
">
</textarea> 
	</p>
<input type="hidden" name="id" id="commentId" /> <input type="hidden"
	name="oldGrade" id="oldGrade" /> <input type="hidden"
	id="stars1-input" name="grade" value="" size="2" />
</div>
</form>
</div>
</div>
</body>
<script type="text/javascript">
	var Class = {
		create : function() {
			return function() {
				this.initialize.apply(this, arguments);
			}
		}
	}
	var Extend = function(destination, source) {
		for ( var property in source) {
			destination[property] = source[property];
		}
	}
	function stopDefault(e) {
		if (e && e.preventDefault) {
			e.preventDefault();
		} else {
			window.event.returnValue = false;
		}
		return false;
	}

</script>

</html>