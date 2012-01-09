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
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js" ></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/formcheck/customerCheck.js"> </script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>
<script type="text/javascript" src="${ctx}/admin/system/js/apduLog.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	new System.ApduLog();
        });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>
<div id="right"><div class="rightbo">
<div id="tableDiv" class="rightcont" style="height: 450px;">
</div>
</div></div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div id="logReqDiv" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<dl id="logReqDl">
<dd><p class="regtext">业务代码:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="commandID" type="text" /></p></dd>
<dd><p class="regtext">承载方式:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="commonType" type="text" /></p></dd>
<dd><p class="regtext">时间戳:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="timeStamp" type="text"  /></p></dd>
<dd><p class="regtext">会话ID:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="sessionID" type="text"  /></p></dd>
<dd><p class="regtext">会话顺序号:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="seqNum" type="text"  /></p></dd>
<dd><p class="regtext">卡号:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="cardNo" type="text"  /></p></dd>
<dd><p class="regtext">当前执行应用（或安全域）AID:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="currentAppAid" type="text"  /></p></dd>
<dd><p class="regtext">执行指令数量:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="cardPOR.apduSum" type="text"  /></p></dd>
<dd><p class="regtext">卡片返回状态字:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="cardPOR.lastAPDUSW" type="text"  /></p></dd>
<dd><p class="regtext">最后一条指令:</p><p class="left inputs"><textarea cols="3" rows="3" class="inputtext" readonly="readonly" name="cardPOR.lastApdu"></textarea></p></dd>
<dd><p class="regtext">最后一条指令返回结果:</p><p class="left inputs"><textarea cols="3" rows="3" class="inputtext" readonly="readonly" name="cardPOR.lastData"></textarea></p></dd>
</dl>
</div>
</div>
<div id="logResDiv" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<dl id="logResDl">
<dd><p class="regtext">业务代码:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="commandID" type="text" /></p></dd>
<dd><p class="regtext">时间戳:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="timeStamp" type="text"  /></p></dd>
<dd><p class="regtext">状态码:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="status.statusCode" type="text"  /></p></dd>
<dd><p class="regtext">状态描述:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="status.statusDescription" type="text"  /></p></dd>
<dd><p class="regtext">会话ID:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="sessionID" type="text"  /></p></dd>
<dd><p class="regtext">会话顺序号:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="seqNum" type="text"  /></p></dd>
<dd><p class="regtext">当前执行应用（或安全域）AID:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="currentAppAid" type="text"  /></p></dd>
<dd><p class="regtext">执行状态:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="progress" type="text"  /></p></dd>
<dd><p class="regtext">完成比例(%):</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="progressPercent" type="text"  /></p></dd>
<dd><p class="regtext">指令名称:</p><p class="left inputs"><input class="inputtext" readonly="readonly" name="apduName" type="text"  /></p></dd>
<dd><p class="regtext">执行指令:</p><p class="left inputs"><textarea cols="3" rows="10" class="inputtext" readonly="readonly" name="apduList"></textarea></p></dd>
</dl>
</div>
</div>
</body>
</html>
