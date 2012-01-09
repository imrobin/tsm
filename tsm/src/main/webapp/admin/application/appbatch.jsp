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
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/app.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var app = new App();
        	new JIM.UI.Grid('tableDiv', {
            	url : '${ctx}/html/cardbaseinfo/?m=index',
            	multipleSelection: true,
            	buttons : [{name : '关联', handler :function(){
            		app.connectAppCard(this.selectIds);
            	}}],
            	columnModel : [{dataName : 'id', identity : true},{title : '卡片名', dataName : 'name'}, {title : '卡片批次', dataName : 'batchNo'}, {title : '备注', dataName : 'comments'}],
            	searchButton : true,
            	searchBar : {filters : [{title : '卡片名：', name : 'search_LIKES_name', type : 'text',width : 150}, {title : '卡片批次号：', name : 'search_LIKES_batchNo', type : 'text',width : 150}]},
            	headerText : '选择批次'
            });
        });
</script>
</head>

<body>

<div id="tableDiv" class="rightcont" style="height: 450px;"></div>
</div>
</body>
</html>
