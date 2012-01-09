<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>安全域申请列表</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>

<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<script src="${ctx }/home/sp/js/listSd.js" type="text/javascript"></script>
<script src="${ctx }/home/sp/js/sp.js" type="text/javascript"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var status = <%=request.getParameter("status")%>
	if(status == null) status = 0;
	var map = new Table();
	window.addEvent('domready', function(){
		
		$('listSdPublished').addEvent('click', function() {self.location = ctx + '/home/sp/listSdPublished.jsp';});
		
		$('listSdArchived').addEvent('click', function() {self.location = ctx + '/home/sp/listSdArchived.jsp';});
		$('listSdRequistion').addEvent('click', function() {self.location = ctx + '/home/sp/listSdRequistion.jsp';});
		var url = ctx + '/html/securityDomain/?m=listSelf&search_EQI_status=1&page_orderBy=id_desc,status_desc';
		
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
					var detail = '<a class="b" style="float : none;" href="javascript:detail('+sd.id+');"><span>查看</span></a>';
					var cancel = '<a class="b" style="float : none;" href="javascript:cancel('+sd.id+');"><span>撤销</span></a>';
					var modify = '<a class="b" style="float : none;" href="javascript:modify('+sd.id+','+sd.statusOriginal+');"><span>修改</span></a>';
					//var requistion = '<a class="b" style="float : none;" href="javascript:applyInfo('+sd.id+');"><span>申请信息</span></a>';
					operation  = detail + '|' + modify + '|' + cancel;// + '|' + requistion;
					if(sd.requistion_reviewDate) {
						map.set(sd.id, sd);
						var info = '<a class="b" style="float : none;" href="javascript:info('+sd.id+');"><span style="color: red;">'+sd.requistion_status+'</span></a>';
						operation += '|' + info;
					}
					
					operation = '<div>' + operation + '</div>';
					
					var whitespace = '&nbsp;';
					var aid = addTip('tip-aid-' + sd.id, sd.aid, '50%');
					grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : sd.status, properties : {align : "center"}}, 
					           {content : aid,    properties : {align : "center"}}, 
					           {content : sd.sdName, properties : {align : "center"}}, 
					           {content : operation, properties : {align : "center"}}]);
				});
			}
		});
		paging.load();
		
	});
	
	function info(id) {
		var sd = map.get(id);
		var reviewDate = sd.requistion_reviewDate;
		var opinion = sd.requistion_opinion;
		
		var tr  = '<tr><td align="center" width="60px">审核时间：</td><td align="left">'+reviewDate+'</td></tr>';
		    tr += '<tr><td align="center">审核意见：</td><td align="left" style="word-break:break-all;">'+opinion+'</td></tr>';
		    tr += '<tr><td align="center">温馨提示：</td><td align="left">若审核被拒，请修改后重新提交</td></tr>';
		var table = '<div class="minfo"><table border="0" cellspacing="0">'+tr+'</table></div>';
		new LightFace.MessageBox({title : '安全域发布申请', width : '100%'}).info(table);//, height : '50px'
	}
	
	function detail(id) {
		self.location = ctx + '/home/sp/sdinfo.jsp?id='+id+'&status=1';
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
		handleSdApply(id, 'deleteSdApply');
	}
	
	function archive(id) {
		//archiveSdApply
		handleSdApply(id, 'archiveSdApply');
	}
	
	function handleSdApply(id, handleType) {
		var title = '';
		var content = '';
		if(handleType == 'deleteSdApply') {
			content = '确定要撤销已提交的安全域发布申请吗？';
			title = '撤销安全域发布申请';
		} else if(handleType == 'archiveSdApply') {
			content = '确定要提交归档安全域申请吗？';
			title = '归档安全域申请';
		}
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

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理安全域&gt;列表&gt;待审核</div>

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
			<li class="s1">待审核</li>
			<li class="s2" style="cursor: pointer;" id="listSdPublished">已发布</li>
			<li class="s2" style="cursor: pointer;" id="listSdArchived" >已归档</li>
			<li class="s2" style="cursor: pointer;" id="listSdRequistion">申请历史</li>
		</ul>
	</div>
	<div class="minfo">
		<!-- 
		<a class="butt2" href="${ctx}/home/sp/listSd.jsp"><span>全部</span></a><a class="butt2" href="?status=1"><span>待审核</span></a><a class="butt2" href="?status=2"><span>已发布</span></a><a class="butt2" href="?status=3"><span>已归档</span></a>
		<table id="grid" style="width: 100%" border="0" cellpadding="0" cellspacing="0">
		</table>
		 -->
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