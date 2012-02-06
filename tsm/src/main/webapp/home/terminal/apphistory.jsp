<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>历史记录查询</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript">
	//EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		$('menu_history').setStyles({
			'background' : 'url("'+ ctx + '/images/user_m1.png") repeat scroll 0 0 transparent',
			'color' : '#FFFFFF',
			'font-size' : '14px',
			'font-weight' : 'bold',
			'text-decoration' : 'none'
		});
		var url = ctx + '/html/subscribehistory/?m=listHistoryForCustomer';

		var grid = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['手机名称','应用名称','应用版本','订购时间','退订时间']
		});
		grid.inject($('gridDiv'));
		
		var paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:'nextpage', showNumber: true, showText : false},
			onAfterLoad : function(data) {
				grid.empty();
				if(data.result.length > 0){
					data.result.forEach(function(history, index) {
						var unsubscribeDate = history.unsubscribeDate;
						var subscribeDate = history.subscribeDate;
						if(!$chk(unsubscribeDate)){
							unsubscribeDate = '&nbsp;';
						}
						if(!$chk(subscribeDate)){
							subscribeDate = '&nbsp;';
						}
						grid.push([{content : history.customerCardInfo_name, properties : {align : "center"}}, 
						           {content : history.applicationVersion_application_name, properties : {align : "center"}}, 
						           {content : history.applicationVersion_versionNo,properties : {align : "center"}}, 
						           {content : subscribeDate, properties : {align : "center"}}, 
						           {content : unsubscribeDate, properties : {align : "center"}}
						           ]);
					});
					$('tip').setStyle('display','none');
				}else{
					$('tip').setStyle('display','');
				}
			}
		});
		
		$('sendCheck').addEvent('click',function(){
			var phoneName = $('search_name').get('value');
			var appname = $('search_app').get('value');
			var searchQuery = '&phoneName=' + phoneName + '&appname=' + appname;
			var searchurl = url + searchQuery;
			paging.load(searchurl);
		});
		$('refresh').addEvent('click',function(){
			$('search_name').set('value','');
			$('search_app').set('value','');
			paging.reload(url);
		});
		paging.load();
	});
</script>
</head>

<body>
<div id="container">
<%@include file="/common/header.jsp" %>
<div class="curPosition">您的位置: 首页&gt;个人中心&gt;历史记录查询</div>
<div id="main">
<div class="newuserc">
<%@include file="userMenu.jsp" %>
<div class="muneright">
<div style="clear:both; overflow:auto; margin-bottom:-1px;">
<div class="usertitle" id="tab1">应用订购记录</div>
<div class="usertitle1 m_l_6" id="tab2"><a href="sdhistory.jsp">下载安全域历史</a></div>
</div>
<div><img src="${ctx}/images/userinfo.png" width="765" height="12" style="display:block;"></div>
<div class="munebg1">
<div class="hserch">
<table width="710" height="56" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="80" height="46" align="center">手机名称:</td>
    <td width="170" align="center"><input style="width:150px;" class="inputnew" type="text" id="search_name" name="textfield" /></td>
    <td width="80" align="center">应用名称:</td>
    <td width="170" align="center"><input style="width:150px;" class="inputnew" type="text" id="search_app"  name="textfield" /></td>
    <td width="210" align="center"><a href="javascript:void(0)"  id="sendCheck"  class="seh">搜索</a><a href="javascript:void(0)"  id="refresh"  class="re">重置</a></td>
  </tr>
</table>
</div>
<div class="w720">
<div class="minfo">
		<div id="gridDiv"></div>
		<div id="nextpage" align="right"></div>
		<div id="tip" style="display:none;">无符合条件的结果</div>
</div>
</div>
</div>
<img src="${ctx}/images/userinfo1.png"/>
</div>
</div>
<%@include file="/common/footer.jsp" %>
</div>
</div>
</body>
</html>