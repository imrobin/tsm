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
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.Request.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/winGrid.js"></script>
<script type="text/javascript" src="${ctx}/admin/customercard/js/info.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var info = new QueryInfo();
        	var gird = new JIM.UI.Grid('tableDiv2', {
    			url : ctx + '/html/customer/?m=index',
    			multipleSelection : false,
    			buttons : [ {
    				name : '查看用户终端',
    				icon : ctx + '/admin/images/define.png',
    				handler : function() {
    					var selectIds = gird.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					info.createCCIGrid(selectIds[0]);
    				}
    			}],
    			columnModel : [ {
    				dataName : 'id',
    				identity : true
    			}, 
    			{
    				title : '用户账号',
    				dataName : 'sysUser_userName'
    			} ,
    			{
    				title : '用户昵称',
    				dataName : 'nickName'
    			}  ,
    			{
    				title : '手机号',
    				dataName : 'sysUser_mobile'
    			},
    			{
    				title : '性别',
    				dataName : 'sex',
    				order : false
    			} ,{
    				title : '生日',
    				dataName : 'birthday',
    				order : false
    			},
    			{
    				title : '地区',
    				dataName : 'location',
    				order : false
    			},
    			{
    				title : '地址',
    				dataName : 'address',
    				order : false
    			},
    			{
    				title : '注册时间',
    				dataName : 'regDate',
    				order : false
    			},
    			{
    				title : '是否激活',
    				dataName : 'active',
    				order : false
    			},
    			{
    				title : '用户状态',
    				dataName : 'sysUser_status',
    				order : false
    			}],
    			searchButton : true,
    			searchBar : {filters : [{title : '手机号：', name : 'custom_search_LIKES_mobileNo', type : 'text',width : 150},
    			                        {title : '用户账号：', name : 'search_ALIAS_sysUserL_LIKES_userName', type : 'text',width : 150}]},
    			headerText : '信息查询',
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

<div id="bindDiv" style="display: none;">
<div class="regcont">
<form method="post" action="${ctx}/html/customerCard/?m=adminbindcard">
<button class="validate['submit']" style="display: none;"></button>
<dl>
<dd><p class="regtext">终端名称:</p><p class="left inputs"><input id="userName" class="inputtext validate['required','length[1,8]'] "  name="phoneName" type="text" /></p></dd>
<dd><p class="regtext">手机品牌:</p><p class="left inputs"><select id="mobileBrand" name="mobileBrand" size="1"></select></p></dd>
<dd><p class="regtext">手机型号:</p><p class="left inputs"><select id="mobileTypeId" name="mobileTypeId" size="1"  class="validate['required'] "></select></p></dd>
</dl>
</form>
</div>
</div>

</body>
</html>
