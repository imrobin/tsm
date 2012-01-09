<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">
<!--
window.addEvent('domready', function(){
	new Request({
		url : ctx + '/html/spBaseInfo/',
		onSuccess : function(responseText) {
			var object = JSON.decode(responseText);
			if(object.success) {
				var sp = object.message;
				if(sp.status == '0') {
					$('usermenu').getElements('a').each(function(e, index) {
						if(index != 0 && index != 1) e.dispose();
					});
				}
			} else {
				self.location = ctx + '/html/login/';
			}
		}
	}).post('m=getCurrentSp');
});
//-->
</script>

<a href="${ctx}/home/sp/modifyPwd.jsp">修改登录密码</a>
<a href="${ctx}/home/sp/center.jsp">信息管理</a>
<a href="${ctx}/home/sp/inputAppBaseInfo.jsp">上传应用</a>
<a href="${ctx}/home/sp/manageApplication.jsp">管理应用</a>
<a href="${ctx}/home/sp/applySd.jsp">申请安全域</a>
<a href="${ctx}/home/sp/listSd.jsp">管理安全域</a>
<a href="${ctx}/home/sp/listSubscribe.jsp">订购关系查询</a>
<a href="${ctx}/home/sp/listSubscribeHistory.jsp">订购历史查询</a>
