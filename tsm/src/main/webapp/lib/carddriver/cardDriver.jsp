<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<script src="${ctx }/lib/webservice/webservice.js" type="text/javascript"></script>
<div id="#cardDriverProgressTemplate$" style="display: none">
<div style="text-align: center;">
<div title="progress" style="font-size: 24px;"></div>
<div title="percent" style="font-size: 12px;"></div>
</div>
</div>
<object id="cardDriver" type="application/x-itst-activex" classid="CLSID:F910CBC1-64A8-4DCA-B0A5-247B8FB7CF49"
	codebase="${ctx}/ReaderCtl.cab"></object>
<script type="text/javascript">
	var ctx = "${ctx}";
</script>
<script src="${ctx }/lib/carddriver/cardDriver.js" type="text/javascript"></script>
