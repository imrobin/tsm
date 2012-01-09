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
<script type="text/javascript" src="${ctx}/admin/application/js/sd.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var optSd = new OptSd();
        	this.grid = new JIM.UI.Grid('tableDiv2', {
    			url : ctx + '/html/securityDomain/?m=index&search_NEI_status=1',
    			multipleSelection : false,
    			buttons : [ {
    				name : '创建安全域',
    				icon : ctx + '/admin/images/icon_4.png',
    				handler : function() {
    					var selectIds = this.grid.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					new Request.JSON({
    						url : ctx + '/html/securityDomain/?m=getSdStatus&sdId=' + selectIds[0],
    						onSuccess : function(data){
    							if(data.success){
    								if(data.message.statusOriginal == 3){
    									new LightFace.MessageBox().error("已归档的安全域不能进行创建");
    								}else{
    									new LightFace.MessageBox({
    										onClose : function() {
    											if (this.result) {
    												optSd.optSd(selectIds[0],'create',grid);
    											}
    										}
    									}).confirm("您要创建安全域吗？");
    								}
    							}else{
    								new LightFace.MessageBox().error(data.message);
    							}
    						}
    					}).get();
    				}.bind(this)
    			}, {
    				name : '删除',
    				icon : ctx + '/admin/images/page_white_edit.png',
    				handler : function() {
    					var selectIds = this.grid.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					new LightFace.MessageBox({
							onClose : function() {
								if (this.result) {
									optSd.optSd(selectIds[0],'del',grid);
								}
							}
						}).confirm("您要删除安全域吗？");
    				}.bind(this)
    			}, {
    				name : '锁定',
    				icon : ctx + '/admin/images/lock.png',
    				handler : function() {
    					var selectIds = this.grid.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					new LightFace.MessageBox({
							onClose : function() {
								if (this.result) {
									optSd.optSd(selectIds[0],'lock',grid);
								}
							}
						}).confirm("您要锁定安全域吗？");
    				}.bind(this)
    			},{
    				name : '解锁',
    				icon : ctx + '/admin/images/unlock.png',
    				handler : function() {
    					var selectIds = this.grid.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					new LightFace.MessageBox({
							onClose : function() {
								if (this.result) {
									optSd.optSd(selectIds[0],'unlock',grid);
								}
							}
						}).confirm("您要解锁安全域吗？");
    				}.bind(this)
    			}, {
    				name : '更新密钥',
    				icon : ctx + '/admin/images/page_white_edit.png',
    				handler : function() {
    					var selectIds = this.grid.selectIds;
    					if (!$chk(selectIds) || selectIds.length == 0) {
    						new LightFace.MessageBox().error('请先选择列表中的记录');
    						return;
    					}
    					new LightFace.MessageBox({
							onClose : function() {
								if (this.result) {
									optSd.optSd(selectIds[0],'update',grid);
								}
							}
						}).confirm("您要更新密钥吗？");
    				}.bind(this)
    			}],
    			columnModel : [ {
    				dataName : 'id',
    				identity : true
    			}, {
    				title : '安全域名称',
    				dataName : 'sdName',
    				order : false
    			}, {
    				title : '安全域AID',
    				dataName : 'aid'
    			},  {
    				title : '应用提供商',
    				dataName : 'sp_name',
    				order : false
    			},{
    				title : '权限',
    				dataName : 'privilegeZh',
    				order : false
    			}, {
    				title : '安装参数',
    				dataName : 'installParams'
    			}, {
    				title : '状态',
    				dataName : 'status'
    			} ],
    			searchButton : true,
    			searchBar : {filters : [{title : '安全域名：', name : 'search_LIKES_sdName', type : 'text', width : 150}, {title : '安全域AID：', name : 'search_LIKES_aid', type : 'text',width : 150},{title : '安全域提供商：', name : 'search_ALIAS_spL_LIKES_name', type : 'text',width : 150}]},
    			headerText : '终端安全域管理',
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
