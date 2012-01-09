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

<script src="${ctx }/home/sp/js/listSd.js" type="text/javascript"></script>
<script src="${ctx }/home/sp/js/sp.js" type="text/javascript"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var status = <%=request.getParameter("status")%>
	if(status == null) status = 0;
	
	window.addEvent('domready', function(){
		
		$('listSdPublished').addEvent('click', function() {self.location = ctx + '/home/sp/listSdPublished.jsp';});
		$('listSdRequistion').addEvent('click', function() {self.location = ctx + '/home/sp/listSdRequistion.jsp';});
		$('listSd').addEvent('click', function() {self.location = ctx + '/home/sp/listSd.jsp';});
		
		var url = ctx + '/html/securityDomain/?m=listSelfByStatus&search_EQI_status=3&page_orderBy=id_desc,status_desc&t='+new Date().getTime();

		var grid = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','状态','安全域AID','安全域名称','操作']
		});
		grid.inject($('grid'));
		
		var paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:'nextpage', showNumber: true, showText : false},
			onAfterLoad : function(data) {
				grid.empty();
				//修改、撤销
				//查询状态为待审核的SD Apply
				data.result.forEach(function(sd, index) {
					var operation = '';
					var detail    = '<a class="b"  style="float : none;"  href="javascript:detail('+sd.id+');"><span>查看</span></a>';
					operation = detail;
					var whitespace = '&nbsp;';
					var privilegeZh = sd.privilegeZh.length == 0 ? whitespace : {content : sd.privilegeZh, properties : {align : "center"}};
					var aid = addTip('tip-aid-' + sd.id, sd.aid, '50%');
					grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : sd.status, properties : {align : "center"}}, 
					           {content : aid,    properties : {align : "center"}}, 
					           {content : sd.sdName, properties : {align : "center"}}, 
					           {content : operation, properties : {align : "center", width : "90px"}}]);
				});
			}
		});
		paging.load();
		
	});
	
	function detail(id) {
		self.location = ctx + '/home/sp/sdinfo.jsp?id='+id+'&status=3';
	}
	
	function modify(id, status) {
		if(status == 1) {
			self.location = ctx+'/home/sp/modifySd1.jsp?id='+id+'&status='+status;
		} else if(status == 2) {
			self.location = ctx+'/home/sp/modifySd2.jsp?id='+id+'&status='+status;
		}
	}
	
	function cancel(id) {
		//cancelSdApply
		handleSdApply(id, 'cancelSdApply');
	}
	
	function archive(id) {
		//archiveSdApply
		handleSdApply(id, 'archiveSdApply');
	}
	
	function handleSdApply(id, handleType) {
		var title = '';
		var content = '';
		if(handleType == 'cancelSdApply') {
			title = '撤销已提交的安全域发布申请吗？';
		} else if(handleType == 'archiveSdApply') {
			title = '提交归档安全域申请吗？';
		}
		content = '确定要' + title;
		new LightFace.MessageBox({
			title : title,
			//content : content,
			onClose : function(result) {
				if(result) {
					new Request.JSON({
						url : ctx + '/html/securityDomain/?m='+handleType,
						onSuccess : function(data) {
							if(data.success) {
								new LightFace.MessageBox({
									onClose : function() {
										self.location = ctx + '/home/sp/listSd.jsp';
									}
								}).info(data.message);
							} else {
								new LightFace.MessageBox().error('');
							}
						}
					}).post({sdId : id});
				}
			}
		}).confirm(content);
	}
	
</script>

</head>

<body>

<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理安全域&gt;列表&gt;已归档</div>

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
			<li class="s1">已归档</li>
			<li class="s2" style="cursor: pointer;" id="listSdRequistion">申请历史</li>
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