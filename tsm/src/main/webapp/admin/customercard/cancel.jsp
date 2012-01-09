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
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/admin/customercard/js/cancel.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var admin = new AdminCancelTerm();
        	var gird = new JIM.UI.Grid('tableDiv2', {
    			url : ctx + '/html/customerCard/?m=listLost&search_EQI_status=1',
    			multipleSelection : false,
    			buttons : [ {
    				name : '注销',
    				icon : ctx + '/admin/images/define.png',
    				handler : function() {
    					var selectIds = gird.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					admin.cancelTermianl(selectIds[0],gird);
    				}.bind(this)
    			} ],
    			columnModel : [ {
    				dataName : 'id',
    				identity : true
    			}, {
    				title : '用户名',
    				dataName : 'userName',
    				order : false
    			} ,
    			{
    				title : '终端名',
    				dataName : 'name'
    			} ,
    			{
    				title : '终端状态',
    				dataName : 'status'
    			} ,
    			{
    				title : '终端品牌',
    				dataName : 'mobileType_brandChs',
    				order : false
    			} ,{
    				title : '终端型号',
    				dataName : 'mobileType_type',
    				order : false
    			},{
    				title : '手机号',
    				dataName : 'mobileNo',
    				order : false
    			}],
    			searchButton : true,
    			searchBar : {filters : [{title : '手机号：', name : 'search_LIKES_mobileNo', type : 'text',width : 150}]},
    			headerText : '终端挂失',
    			image : {
    				header : ctx + '/images/user_icon_32.png'
    			}
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
<div id="tableDiv2" class="rightcont" style="height: 450px;"></div>
</div>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
</body>
</html>
