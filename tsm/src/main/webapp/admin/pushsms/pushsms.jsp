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
<script src="${ctx}/admin/pushsms/js/pushsms.js" type="text/javascript"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var ps = new PushSms();
        	var ccid;
        	var termGrid = new JIM.UI.Grid('tableDiv', {
            	url :ctx + '/html/customerCard/?m=findCustoemrCardInfoByMobileNo',
            	multipleSelection: false,
            	order : false,
            	buttons : [{name : '应用操作', 
            		icon : ctx + '/admin/images/in.png'
            		,handler : function(){
            			if(termGrid.selectIds != '') {
            				ccid = termGrid.selectIds[0];
            				ps.doQuery(ccid);
            			} else {
            				new LightFace.MessageBox().error("请先选择列表中文件");
            			}
            	}}],
            	columnModel : [{
    				dataName : 'id',
    				identity : true
    			}, {
    				title : '手机号',
    				dataName : 'mobileNo'
    			}, {
    				title : '终端名',
    				dataName : 'name'
    			}, {
    				title : '黑名单',
    				dataName : 'inBlack'
    			}, {
    				title : '品牌',
    				dataName : 'mobileType_brandChs'
    			}, {
    				title : '型号', dataName : 'mobileType_type'
    			}, {
    				title : '状态',
    				dataName : 'status'
    			}],
            	searchButton : true,
            	searchBar : {filters : [{title : '手机号码：', name : 'moibleNo', type : 'text'}]},
            	headerText : '终端列表'
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
<div id="downDiv" style="display:none">
<div class="opertop">
	<p class="oper1">
		<a class="butt2" href="#" id="showDown">
			<span>下载应用</span>
		</a>
	</p>
	<p class="oper1" >
		<a class="butt2" href="#" id="showOpt">
				<span>管理应用</span>
		</a>
	</p>
</div>
<div id="downApp"></div>
<div id="managerApp" style="display:none"></div>
</div>
</div>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="appVerDiv" style="display:none">
	<a class="butt2" href="#" id="goDown">
		<span>确定</span>
	</a>
	<select id="appverSel">
	</select>
</div>
</body>
</html>
