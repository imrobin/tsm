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
<script type="text/javascript" src="${ctx}/admin/cardbase/js/cardbaseManager.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	new CardBaseManager.list();
        });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>

	<div id="right">
		<div class="rightbo">
		
			<div id="tableDiv" class="rightcont" style="height: 450px;">
				<!-- grid -->
			</div>
		
		</div>
	</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>

<!-- add -->
<div id="cardBaseDivAdd" style="display: none;">
<div class="regcont" style="overflow-x: hidden;">
<form method="post">
<input name="id" type="hidden" />
<button class="validate['submit']" style="display: none;"></button>
<dl>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>名称:</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%chckMaxLength']" maxlength="16" id="name" name="name" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>批次编号:</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%chkMaxLengthWordAndNumber']" maxlength="16" id="batchNo" name="batchNo" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>SE类型:</p>
		<p class="left inputs">
			<select name="type"><option value="1">贴片</option><option value="2">NFC终端</option ><option value="3">SIM卡</option ></select>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>起始SEID:</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%chkOx']" maxlength="19" id="startNo" name="startNo" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>终止SEID:</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%chkOx']" maxlength="19" id="endNo" name="endNo" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>主安全域密钥版本:</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%checkKeyVersion']" maxlength="2" id="cardKeyVersion" name="cardKeyVersion" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>SE卡商</p>
		<p class="left inputs">
			<select name="osImplementor" id="osImplementor">
				<option value="00">AXALTO</option>
				<option value="01">GEMPLUS</option >
				<option value="02">武汉天喻</option >
				<option value="03">江西捷德</option>
				<option value="04">珠海东信和平</option >
				<option value="05">大唐微电子</option>
				<option value="06">航天九洲通</option>
				<option value="07">北京握奇</option>
				<option value="08">东方英卡</option>
				<option value="09">北京华虹</option>
				<option value="10">上海柯斯</option>
				<option value="11">航天智通</option> 
			</select>
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>初始内存空间(Byte):</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%chkNumber','%chckMaxLength']" maxlength="9" id="totalRamSize" name="totalRamSize" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext"><span style='color: red;'>*</span>初始存储空间(Byte):</p>
		<p class="left inputs">
			<input class="inputtext validate['required','%chkNumber','%chckMaxLength']" maxlength="9" id="totalRomSize" name="totalRomSize" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">芯片类型:</p>
		<p class="left inputs">
			<input class="inputtext validate['chkMaxLengthWordAndNumberAndDot']" maxlength="16" id="coreType" name="coreType" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">Java版本:</p>
		<p class="left inputs">
			<input class="inputtext validate['chkMaxLengthWordAndNumberAndDot']" maxlength="16" id="javaVersion" name="javaVersion" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">cms2ac版本:</p>
		<p class="left inputs">
			<input class="inputtext validate['chkMaxLengthWordAndNumberAndDot']" maxlength="16" id="cms2acVersion" name="cms2acVersion" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">预置内存类型:</p>
		<p class="left inputs">
			<select id="presetRamType" name="presetRamType"><option value="1">只读</option ><option value="2">可擦写</option ></select>
		</p>
	</dd>
	<dd>
		<p class="regtext">预置内存状态:</p>
		<p class="left inputs">
			<input class="inputtext validate['chkMaxLengthWordAndNumberAndDot']" maxlength="16" id="presetRamStatus" name="presetRamStatus" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">生产编号:</p>
		<p class="left inputs">
			<input class="inputtext validate['%chkMaxLengthWordAndNumber']" maxlength="16" id="stockId" name="stockId" type="text" />
		</p>
	</dd>
	<!-- <dd>
		<p class="regtext">平台类型:</p>
		<p class="left inputs">
			<select id="platformType" name="platformType"><option value="1">STK</option><option value="2">JAVA_CARD</option ></select>
		</p>
	</dd>
	<dd>
		<p class="regtext">平台版本:</p>
		<p class="left inputs">
			<input class="inputtext validate['chkMaxLengthWordAndNumberAndDot']" maxlength="16" id="platformVersion" name="platformVersion" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">系统类型:</p>
		<p class="left inputs">
			<select name="osPlatform"><option value="1">测试COS</option><option value="2">厂商COS</option ></select>
		</p>
	</dd>
	<dd>
		<p class="regtext">系统版本:</p>
		<p class="left inputs">
			<input class="inputtext validate['chkMaxLengthWordAndNumberAndDot']" maxlength="16" id="osVersion" name="osVersion" type="text" />
		</p>
	</dd>
	<dd>
		<p class="regtext">自动内存回收:</p>
		<p class="left inputs">
			<select name="garbageCollection"><option value="1">是</option><option value="2">否</option ></select>
		</p>
	</dd> -->
	<dd>
		<p class="regtext">备注:</p>
		<p class="left inputs">
			<textarea class="inputtext validate['%chckMaxLength']"  maxlength="256" id="comments" name="comments"></textarea>
		</p>
	</dd>
</dl>
</form>
</div>
</div>

</body>
</html>
