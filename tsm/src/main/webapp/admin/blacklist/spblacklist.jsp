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
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/admin/blacklist/js/spblacklist.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var blackList = new SpBlackList();
        	new JIM.UI.Grid('tableDiv', {
            	url : '${ctx}/html/spBlackList/?m=listSp&search_EQI_inBlack=0',
            	multipleSelection: true,
            	buttons : [{name : '添加到黑名单', 
            		icon : ctx + '/admin/images/in.png'
            		,handler : function(){
            		blackList.addSpToBlack(this.selectIds);
            	}}],
            	columnModel : [{
    				dataName : 'id',
    				identity : true
    			}, {
    				title : '应用提供商编号',
    				dataName : 'no'
    			}, {
    				title : '应用提供商名称',
    				dataName : 'name'
    			}, {
    				title : '应用提供商简称',
    				dataName : 'shortName'
    			}, {
    				title : '所在地 ', dataName : 'locationNo'
    			}, {
    				title : '地址',
    				dataName : 'address'
    			}, {
    				title : '邮箱地址',
    				dataName : 'sysUser_email'
    			}, {
    				title : '企业性质', dataName : 'firmNature'
    			}, {
    				title : '状态',
    				dataName : 'status'
    			}],
            	searchButton : true,
            	searchBar : {filters : [{title : '应用提供商名：', name : 'search_LIKES_name', type : 'text'}]},
            	headerText : '应用提供商列表'
            });
        	new JIM.UI.Grid('tableDiv2', {
            	url : '${ctx}/html/spBlackList/?m=listSp&search_EQI_inBlack=1',
            	multipleSelection: true,
            	buttons : [{name : '从黑名单移除',  
            		icon : ctx + '/admin/images/out.png',
            		handler : function(){
            		blackList.removeOutBlack(this.selectIds);
            	}}],
            	columnModel : [{
    				dataName : 'id',
    				identity : true
    			}, {
    				title : '应用提供商编号',
    				dataName : 'no'
    			}, {
    				title : '应用提供商名称',
    				dataName : 'name'
    			}, {
    				title : '应用提供商简称',
    				dataName : 'shortName'
    			}, {
    				title : '所在地 ', dataName : 'locationNo'
    			}, {
    				title : '地址',
    				dataName : 'address'
    			}, {
    				title : '邮箱地址',
    				dataName : 'sysUser_email'
    			}, {
    				title : '企业性质', dataName : 'firmNature'
    			}, {
    				title : '状态',
    				dataName : 'status'
    			}],
            	searchButton : true,
            	searchBar : {filters : [{title : '应用提供商名：', name : 'search_LIKES_name', type : 'text'}]},
            	headerText : '应用提供商黑名单列表'
            });
        });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;"></div>
<div id="tableDiv2" class="rightcont" style="height: 450px;"></div>
</div>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
</body>
</html>
