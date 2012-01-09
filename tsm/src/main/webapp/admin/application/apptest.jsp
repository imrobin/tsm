<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script src="${ctx}/lib/formcheck/lang/cn.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/customerCheck.js" type="text/javascript"></script>
<script src="${ctx}/lib/formcheck/formcheck.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/winGrid.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/date/datepicker/datepicker.js"></script>
<script src="${ctx}/lib/uploadManager/ajaxUploadFile.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/swfupload.js"></script>
<script type="text/javascript" src="${ctx}/lib/uploadManager/handlers.js"></script>
<script type="text/javascript" src="${ctx}/home/customer/js/customer.js"></script>
<script type="text/javascript" src="${ctx}/admin/application/js/appManager.js"></script>
<script type="text/javascript">
		var ctx = '${ctx}';
        window.addEvent('domready', function(){
        	var appMa = new AppManager.Test();
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
</div>
</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
<div class="userinput" style="display: none" id="uploadForm">
		<form id="appverForm" action="${ctx}/html/testfile/?m=finishUpload">
			<table>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>文件介绍：
						</p></td>
					<td>
						<p class="left inputs">
							<input id="fileCommons" style="width: 335px" class="inputtext validate['required']" name="comment" type="text" maxlength="200" />
						</p></td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>上传文件：
						</p></td>
					<td>
						<p class="left inputs" id='uploadP'>
							<input name="testFileOrgName" id="tetsFileName" type="text" class="inputtext validate['required']" readonly="readonly" />
							<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;">
								<span id="spanButtonPlaceholder"></span>
							</div>
							<div id="divFileProgressContainer" style="height: 75px; display: none;"></div>
						</p> <input name="appverId" id="appverId" type="hidden" class="inputtext validate['required']" readonly="readonly" /> <input
						name="tempFileName" id="tempFilename" type="hidden" class="inputtext validate['required']" readonly="readonly" />
						<button class="validate['submit']" style="display: none;"></button></td>
				</tr>
			</table>
		</form>
	</div>
<div style="display: none" id="onlineTestDiv">
	<table>
		<tr>
		<td>
				请选择下面的测试功能.
			</td>
		</tr>
		<tr>
			<td>
					<a href="#" class="butt2" id="dwonappBtn"><span>下载应用</span></a>
					<a href="#" class="butt2" id="delappBtn"><span>删除应用</span></a>
					<a href="#" class="butt2" id="lockappBtn"><span>锁定应用</span></a>
					<a href="#" class="butt2" id="unlockappBtn"><span>解锁应用</span></a>
		</tr>
	</table>
<div class="userinput" style="display: none" id="onlineTestSubDiv">
		<form id="appverForm" action="${ctx}/html/appVer/?m=saveReport">
			<table>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>测试时间：
						</p></td>
					<td>&nbsp;&nbsp;
 							<select id="year" name="year">
									<script type="text/javascript">
											var yearbegin = 2010, yearend = 2080;
											for ( var i = yearbegin; i <= yearend; i++) {
												document.write("<option value="+i+">"+ i+ "</option>");
											}
											$('year').set('value',1980);
										</script>
						</select>年 
						<select id="month" name="month" size="1" style="width: 50px">
							<option value="01">1</option>
							<option value="02">2</option>
							<option value="03">3</option>
							<option value="04">4</option>
							<option value="05">5</option>
							<option value="06">6</option>
							<option value="07">7</option>
							<option value="08">8</option>
							<option value="09">9</option>
							<option value="10">10</option>
							<option value="11">11</option>
							<option value="12">12</option>
						</select> 月 
						<select id="day" name="day" size="1">
									<script type="text/javascript">
											var daybegin = 1, dayend = 31;
											for ( var i = daybegin; i <= dayend; i++) {
												var day = '';
												if(i<10){
													day = "0" + i;
												}
												document.write("<option value="+day+">"+ i+ "</option>");
											}
										</script>
						</select> 日
						</p></td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>手机号：
						</p></td>
					<td>
						<p class="left inputs">
							<input id="mobileNo" style="width: 335px" class="inputtext validate['required']" name="mobileNo" type="text" maxlength="200" />
						</p></td>
				</tr>
				<!-- <tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>SEID:
						</p></td>
					<td>
						<p class="left inputs">
							<input id="seId" style="width: 335px" class="inputtext validate['required']" name="seId" type="text" maxlength="200" />
						</p></td>
				</tr> -->
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>SE芯片类型：
						</p></td>
					<td>
						<p class="left inputs">
							<input id="seType" style="width: 335px" class="inputtext validate['required']" name="seType" type="text" maxlength="200" />
						</p></td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>NFC终端型号：
						</p></td>
					<td>
						<p class="left inputs">
							<input id="modelType" style="width: 335px" class="inputtext validate['required']" name="modelType" type="text" maxlength="200" />
						</p></td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>CMS2AC版本：
						</p></td>
					<td>
						<p class="left inputs">
							<input id="cms2acVer" style="width: 335px" class="inputtext validate['required']" name="cms2acVer" type="text" maxlength="200" />
						</p></td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>选择卡批次：
						</p></td>
					<td>
						<p>
							<input readonly="readonly" id="selectedCardBase" style="width: 200px" type="text" />
							<a class="buts m_t_5" id="getCardBase">选择</a>
						</p>
					</td>
				</tr>
				<tr>
					<td>
						<p class="regtext">
							<span style="color: red">*</span>测试结果:
						</p></td>
					<td>
						<select id="result" name="result">
							<option value="1">通过</option>
							<option value="0">不通过</option>
						</select>
					</td>
				</tr>
		 		<tr>
					<td>
						<p class="regtext">
							结果说明：
						</p></td>
					<td>
						<p class="left inputs">
						<textarea rows="4" cols="40" class="inputtext validate['%chckMaxLength']" name="resultComment" id="resultComment" maxlength="250"></textarea>
						</p></td>
				</tr>
				<tr>
					<td>
						<button class="validate['submit']" style="display: none;"></button></td>
					<td>
					<input type="hidden" id="formAppverId" name="appverId"></input>
					<input type="hidden" id="cardBaseInfo" name="cardBaseId"></input>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
</html>
