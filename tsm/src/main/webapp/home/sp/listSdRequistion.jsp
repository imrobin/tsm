<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>安全域申请列表</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<script type="text/javascript" src="${ctx }/home/sp/js/listSd.js"></script>
<script type="text/javascript" src="${ctx }/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	
	window.addEvent('domready', function(){
		$('listSd').addEvent('click', function() {self.location = ctx + '/home/sp/listSd.jsp';});
		$('listSdArchived').addEvent('click', function() {self.location = ctx + '/home/sp/listSdArchived.jsp';});
		$('listSdPublished').addEvent('click', function() {self.location = ctx + '/home/sp/listSdPublished.jsp';});
		
		var url = ctx + '/html/securityDomain/?m=listSelf&page_orderBy=aid_desc,applyDate_desc,status_desc'

		var grid = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','状态','申请类型','处理结果','安全域AID','安全域名称','申请时间','操作']
		});
		grid.inject($('grid'));
		
		var paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:'nextpage', showNumber: true, showText : false},
			onAfterLoad : function(data) {
				grid.empty();
				data.result.forEach(function(sd, index) {
					var operation = '';
					var detail    = '<a class="b"  style="float : none;"  href="javascript:detail('+sd.id+');"><span>查看</span></a>';
					operation = detail;
					var whitespace = '&nbsp;';
					var privilegeZh = sd.privilegeZh.length == 0 ? whitespace : {content : sd.privilegeZh, properties : {align : "center"}};
					var result = sd.requistion_result ? sd.requistion_result : whitespace;
					var aid = addTip('tip-aid-' + sd.id, sd.aid, '50%');
					var sdName = addTip('tip-aid-' + sd.id, sd.sdName, '50%');
					grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : sd.status, properties : {align : "center"}}, 
					           {content : sd.requistion_type, properties : {align : "center"}}, 
					           {content : result, properties : {align : "center"}}, 
					           {content : aid,    properties : {align : "center"}}, 
					           {content : sdName, properties : {align : "center"}}, 
					           {content : sd.applyDate, properties : {align : "center"}},
					           {content : operation, properties : {align : "center", width : "90px"}}]);
				});
			}
		});
		paging.load();
	});
	
	function detail(id) {
		self.location = ctx + '/home/sp/sdinfo.jsp?id='+id+'&status=1';
	}
</script>

</head>

<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理安全域&gt;列表&gt;申请历史</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">
<div id="userinput">
	<div class="titletab">
		<ul>
			<li class="s2" style="cursor: pointer;" id="listSd">待审核</li>
			<li class="s2" style="cursor: pointer;" id="listSdPublished">已发布</li>
			<li class="s2" style="cursor: pointer;" id="listSdArchived">已归档</li>
			<li class="s1" >申请历史</li>
		</ul>
	</div>
	<div class="minfo">
		<div id="grid"></div>
		<div id="nextpage" align="right"></div>
	</div>
</div>

</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>

</body>
</html>