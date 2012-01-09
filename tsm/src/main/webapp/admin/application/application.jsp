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
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js">
	
</script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/admin/application/js/app.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		var app = new App();
		new JIM.UI.Grid('tableDiv', {
			url : '${ctx}/html/application/?m=index&search_EQI_status=1&search_ALIAS_spL_NEI_inBlack=1&search_ALIAS_spL_EQI_status=1',
			multipleSelection : false,
			buttons : [ {
				name : '下载',
				icon : ctx + '/admin/images/down.png',
				handler : function() {
					if (this.selectIds != '') {
						app.downApp(this.selectIds);
					} else {
						new LightFace.MessageBox().error("请先选择一条记录");
					}
				}
			}],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用名',
				dataName : 'name'
			}, {
				title : '应用AID',
				dataName : 'aid'
			}, {
				title : '所属SP',
				dataName : 'spName'
			}, /* {title : '所属安全域', dataName : 'sd_sdName'},  */{
				title : '应用类型',
				dataName : 'childType_name'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '应用名：',
					name : 'search_LIKES_name',
					type : 'text',
					width : 150
				}, {
					title : '应用AID：',
					name : 'search_LIKES_aid',
					type : 'text',
					width : 150
				}, {
					title : '应用提供商：',
					name : 'search_ALIAS_spL_LIKES_name',
					type : 'text',
					width : 150
				} ]
			},
			headerText : '下载应用'
		});
	});
</script>
</head>

<body>
	<div id="container">
		<%@ include file="/admin/layout/top.jsp"%>
		<div id="main">
			<%@ include file="/admin/layout/menu.jsp"%>
			<div id="right">
				<div class="rightbo">
					<div id="tableDiv" class="rightcont" style="height: 450px;"></div>
					<div id="appInfo" style="display: none;">
						<table style="border: thin;" width="600">
							<tr>
								<td rowspan="5" id="appImg"></td>
								<td width="80px"><b>&nbsp;&nbsp;应用名称 :</b></td>
								<td id="ename" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;最新版本 :</b></td>
								<td id="elastver" width="350px"></td>
							</tr>
							<tr id="hideSelect">
								<td width="80px"><b>&nbsp;&nbsp;选择版本 :</b></td>
								<td id="evesion" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;所属提供商 :</b></td>
								<td id="espname" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;应用描述 :</b></td>
								<td id="edesc" width="350px"></td>
							</tr>
						</table>
						<hr />
						<table style="border: thin;" width="600">
							<tr>
								<td rowspan="4" align="center"><h1>用户信息</h1>
								</td>
								<td width="80px"><b>&nbsp;&nbsp;用户账号 :</b></td>
								<td id="username" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;用户终端 :</b></td>
								<td id="userter" width="350px"></td>
							</tr>
							<tr>
								<td width="80px"><b>&nbsp;&nbsp;手机号码 :</b></td>
								<td id="usermobileno" width="350px"></td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
		<div id="footer" class="clear">
			<p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p>
		</div>
	</div>
</body>
</html>
