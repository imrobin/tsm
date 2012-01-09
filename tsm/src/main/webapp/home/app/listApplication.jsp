<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>欢迎页</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/yui.css" type="text/css" rel="stylesheet" />
<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/js/jquery.js" type="text/javascript"></script>
<script src="${ctx}/js/jquery.form.js" type="text/javascript"></script>
<script src="${ctx}/js/page.js" type="text/javascript"></script>
</head>
<body>
<%@ include file="/common/header.jsp"%>
<script type="text/javascript">
function saveComment() {
		if (confirm('是否提交')) {
			//alert($('#comment').html());
			//alert($('#comment').formSerialize());
			$.post('${ctx}/html/application/?m=updateComment', $('#comment').formSerialize(), function(json) {
				alert(json.success);
				if (json.success) {
					self.location = ctx + "/home/listApplication.jsp";
				} else {
					// self.location= ctx + "/home/group/index.jsp";
				}

			}, 'json');
		}
}
$(document).ready(function() {
	//list(null);
	listsingle();
});
var cells2;
var description;
function listsingle(){
	//全部软件
	$.getJSON("${ctx}/html/application/?m=index",{page_orderBy : "id_desc",cardNo : 1},function(json){
		if(json.success) {
			clearRows('applicationListTable');
			$.each(json.result, function(i, item){
				var lastRow = addRow('applicationListTable');
				var cells = lastRow.cells;
				cells[0].innerHTML = item.pcIcon;
				cells[1].innerHTML = item.name;
				cells[2].innerHTML = item.avgCount;
				cells[3].innerHTML = item.description;
			//	alert(item.cardAppStatus);
				if (item.cardAppStatus == 1)
					cells[4].innerHTML = '<a href="JavaScript:downloadToMobile(' + item.id + ')">下载到手机</a>';
				else if (item.cardAppStatus == 4)
					cells[4].innerHTML = '已下载';
				else
					cells[4].innerHTML = '状态异常';
			});
		} else {
			alert(json.message);
			self.location = "${ctx}/security/login.jsp";
		}
		});
}
function list(param) {
	//var groupId = getQueryValue('groupId');
	//$.getJSON("${ctx}/html/applicationlist?method=index", {search_ALIAS_groupI_EQL_id : groupId}, function(json){
	//全部软件
$.getJSON("${ctx}/html/application/?m=index",{page_orderBy : "id_desc",cardNo : 1},function(json){
	if(json.success) {
		clearRows('applicationListTable');
		$.each(json.result, function(i, item){
			var lastRow = addRow('applicationListTable');
			var cells = lastRow.cells;
			cells[0].innerHTML = item.pcIcon;
			cells[1].innerHTML = item.name;
			cells[2].innerHTML = item.avgCount;
			cells[3].innerHTML = item.description;
			cells[4].innerHTML = '<a href="JavaScript:downloadToMobile(' + item.id + ')">下载到手机</a>';
		});
	} else {
		alert(json.message);
		self.location = "${ctx}/security/login.jsp";
	}
	});
//下载排行
	$.getJSON("${ctx}/html/application/?m=index",{page_orderBy : "downloadCount_desc",cardNo : 1},function(json){
		if(json.success) {
			clearRows('applicationTopList');
			$.each(json.result, function(i, item){
				var lastRow = addRow('applicationTopList');
				var cells = lastRow.cells;
				cells[0].innerHTML = item.pcIcon;
				cells[1].innerHTML = item.name;
				cells[2].innerHTML = item.avgCount;
				cells[3].innerHTML = item.downloadCount;
			});
		} else {
			alert(json.message);
			self.location = "${ctx}/security/login.jsp";
		}
		});

	//单个信息
	$.getJSON("${ctx}/html/application/?m=index",{search_EQL_id : 1,cardNo : 1},function(json){
		if(json.success) {
			clearRows('singleInfo');
			$.each(json.result, function(i, item){
				var lastRow = addRow('singleInfo');
				cells2 = lastRow.cells;
				cells2[0].innerHTML = item.pcIcon;
				cells2[1].innerHTML = item.name;
				cells2[2].innerHTML = item.avgCount;
				description = item.description;
				if (item.description.length > 10 ){
					var subDesc = item.description.substring(0,10);
					cells2[3].innerHTML =subDesc+'...   <a href="JavaScript:showMore()">展开详细介绍 &gt;&gt;</a>';
				}else{
					cells2[3].innerHTML = item.description;
				}
				cells2[4].innerHTML = item.publishDate;
				cells2[5].innerHTML = '<a href="JavaScript:downloadToMobile(' + item.id + ')">下载到手机</a>';
			});
		} else {
			alert(json.message);
			self.location = "${ctx}/security/login.jsp";
		}
		});	

	//应用评论加载
	$.getJSON("${ctx}/html/application/?m=loadComment",{search_EQL_applicationId : 1,page_orderBy : "commentTime_desc"},function(json){
		if(json.success) {
			clearRows('commentInfo');
			$.each(json.result, function(i, item){
				var lastRow = addRow('commentInfo');
				cells = lastRow.cells;
				cells[0].innerHTML = item.user_userName;
				cells[1].innerHTML = item.content;
			});
		} else {
			alert(json.message);
			self.location = "${ctx}/security/login.jsp";
		}
		});	
	//最近两周下载用户
	$.getJSON("${ctx}/html/application/?m=recentlyDownLoad",{search_EQL_applicationId : 1},function(json){
		if(json.success) {
			clearRows('recentlyDownInfo');
			$.each(json.result, function(i, item){
				var lastRow = addRow('recentlyDownInfo');
				cells = lastRow.cells;
				cells[0].innerHTML = item.userName;
				cells[1].innerHTML = item.mobile;
			});
		} else {
			alert(json.message);
			self.location = "${ctx}/security/login.jsp";
		}
		});	
}
function downloadToMobile(gId, mId) {
	$.post('${ctx}/application?method=downloadToMobile', {groupId : gId, groupMemberId : mId}, function(data){
		alert(data.message);
		list(null);
		}, 'json');
}
function showMore() {
	cells[3].innerHTML = description;
}
function clearRows(tableId) {
	var table = $('#' + tableId)[0];
	var rows = table.tBodies[0].rows;
	if(rows.length > 2) {
		for(i = rows.length - 1; i > 1; i--) {
			table.deleteRow(i);
		}
	}
}
function addRow(tableId) {
	var temp = $('#' + tableId)[0].tBodies[0];
	var rows = temp.rows;
	return temp.appendChild(rows[rows.length - 1].cloneNode(true));
}
function getQueryValue(name) {  
     var reg = new RegExp("(^|&)"+name+"=([^&]*)(&|$)");  
     var r = window.location.search.substr(1).match(reg);  
     if(r!=null) {
         return unescape(r[2]);
     } 
     return "";  

}
</script>

<table id="applicationListTable">
	<tr>
		<th id="groupTitle" colspan="5"><a
			href="/home/group/listGroup.jsp">返回圈子列表</a></th>
	</tr>
	<tr>
		<td>图标</td>
		<td>名称</td>
		<td>星</td>
		<td>描述</td>
		<td>操作</td>
	</tr>
</table>
<table id="applicationTopList">
	<tr>
		<td>下载排行</td>
	</tr>
	<tr>
		<td>图标</td>
		<td>名称</td>
		<td>星</td>
		<td>下载次数</td>
	</tr>
</table>
<table id="singleInfo">
	<tr>
		<td>应用详情</td>
	</tr>
	<tr>
		<td>图标</td>
		<td>名称</td>
		<td>星</td>
		<td>描述</td>
		<td>发布时间</td>
		<td>操作</td>
	</tr>
</table>
<table id="commentInfo">
	<tr>
		<td>评论详情</td>
	</tr>
	<tr>
		<td>用户名</td>
		<td>描述</td>
	</tr>
</table>
<div id="page"></div>
<table id="recentlyDownInfo">
	<tr>
		<td>最近下载</td>
	</tr>
	<tr>
		<td>用户名</td>
		<td>手机</td>
	</tr>
</table>
<form method="post" id="comment" name="comment">
<table>
	<tr>
		<td><input id="commentInput'" name="commentInput"></input></td>
		<td><input type="button" id="submitForm" value="提交评论"
			onclick="saveComment()"></input></td>
	</tr>
</table>
</form>

</body>
</html>
