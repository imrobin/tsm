<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
	var ctx = '${pageContext.request.contextPath}';
	window.addEvent('domready', function(){
		new JIM.UI.TreeMenu('menuDiv', {
			url : '${ctx}/html/menu/?m=indexShow'
		}).load();
	});
</script>
<div id="left">
	<div class="leftbo">
		<div id="menuDiv" class="leftcont" style="height: 455px;overflow: auto;">
			
		</div>
	</div>
</div>