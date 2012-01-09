<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<%@ include file="/lib/carddriver/cardDriver.jsp"%>
<script type="text/javascript">
	var transConstant;
	window.addEvent('domready', function() {
		new Request.JSON({
			url : ctx + "/html/localtransaction/?m=exportConstant",
			onSuccess : function(json) {
				if (json.success) {
					transConstant = json.message;
				}
			}
		}).get();
	});

	function after(cardPOR, response) {
		var template = $('apduListTemplate').clone();

		var apdu = cardPOR.options.lastApdu;
		var apduLength = 60;
		if (apduLength < apdu.length) {
			apdu = apdu.slice(0, apduLength) + "...";
		}
		template.getElement('[title="指令"]').set('title', cardPOR.options.lastApdu).set('html', apdu);
		template.getElement('[title="类型"]').set('html', response.apduName);
		template.getElement('[title="状态码"]').set('html', cardPOR.options.lastAPDUSW);
		template.getElement('td[title="数据"]').set('html', cardPOR.options.lastData);

		template.inject($("apduList"));
	}

	function prepare() {
		$("apduList").empty();
	}

	function exec(operation) {
		var aid = $('aid').get("value");
		if (!$chk(operation)) {
			alert('operation');
		} else {
			new JIM.CardDriver({
				commonType : transConstant.GPC,
				ctl : cardDriver,
				operations : [ {
					aid : aid,
					appVersion : $('versionNo').get("value"),
					operation : operation
				} ],
				msisdn : $('msisdn').get("value"),
				onPrepare : prepare,
				onAfter : after
			}).exec();
		}
	};

	function downloadApp() {
		exec(transConstant.DOWNLOAD_APP);
	};

	function deleteApp() {
		exec(transConstant.DELETE_APP);
	};

	function updateApp() {
		exec(transConstant.UPDATE_APP);
	};

	function persoApp() {
		exec(transConstant.PERSONALIZE_APP);
	};

	function lockApp() {
		exec(transConstant.LOCK_APP);
	};

	function unlockApp() {
		exec(transConstant.UNLOCK_APP);
	};

	function crateSd() {
		exec(transConstant.CREATE_SD);
	};

	function deleteSd() {
		exec(transConstant.DELETE_SD);
	};

	function lockSd() {
		exec(transConstant.LOCK_SD);
	};

	function unlockSd() {
		exec(transConstant.UNLOCK_SD);
	};

	function updateSd() {
		exec(transConstant.UPDATE_KEY);
	};

	function syncSd() {
		exec(transConstant.SYNC_CARD_SD);
	};

	function replaceMobileNo() {
		exec(transConstant.REPLACE_MOBILE_NO);
	}

	function downloadAppThanDeleteApp() {
		var aid = $('aid').get("value");
		if (!$chk(aid)) {
			alert('aid');
		} else {
			new JIM.CardDriver({
				commonType : transConstant.GPC,
				ctl : cardDriver,
				operations : [ {
					aid : aid,
					operation : transConstant.DOWNLOAD_APP
				}, {
					aid : aid,
					operation : transConstant.DELETE_APP
				} ]
			}).exec();
		}
	};

	function downloadAppThanDownloadAppThanDeleteApp() {
		var aid = $('aid').get("value");
		if (!$chk(aid)) {
			alert('aid');
		} else {
			new JIM.CardDriver({
				commonType : transConstant.GPC,
				ctl : cardDriver,
				operations : [ {
					aid : aid,
					operation : transConstant.DOWNLOAD_APP
				}, {
					aid : aid,
					operation : transConstant.DOWNLOAD_APP
				}, {
					aid : aid,
					operation : transConstant.DELETE_APP
				} ]
			}).exec();
		}
	};

	function readNo() {
		var cardNo = new JIM.CardDriver({
			commonType : transConstant.GPC,
			ctl : cardDriver,
			operations : []
		}).readCardNo();
		$('cardNo').set('html', cardNo);
		alert(cardNo);
	};
</script>
</head>
<body>
	AID
	<input type="text" id="aid" /> 版本号
	<input type="text" id="versionNo" />手机号
	<input type="text" id="msisdn" />
	<br />
	<a href="javascript:downloadApp()">下载应用</a>&nbsp;|&nbsp;
	<a href="javascript:deleteApp()">删除应用</a>&nbsp;|&nbsp;
	<a href="javascript:updateApp()">升级应用</a>&nbsp;|&nbsp;
	<a href="javascript:persoApp()">个人化应用</a>&nbsp;|&nbsp;
	<a href="javascript:lockApp()">锁定应用</a>&nbsp;|&nbsp;
	<a href="javascript:unlockApp()">解锁应用</a>&nbsp;|&nbsp;
	<a href="javascript:crateSd()">创建安全域</a>&nbsp;|&nbsp;
	<a href="javascript:deleteSd()">删除安全域</a>&nbsp;|&nbsp;
	<a href="javascript:updateSd()">更新安全域</a>&nbsp;|&nbsp;
	<a href="javascript:lockSd()">锁定安全域</a>&nbsp;|&nbsp;
	<a href="javascript:unlockSd()">解锁安全域</a>&nbsp;|&nbsp;
	<a href="javascript:syncSd()">同步卡片安全域</a>&nbsp;|&nbsp;
	<a href="javascript:downloadAppThanDeleteApp()">先加载，再删除</a>&nbsp;|&nbsp;
	<a href="javascript:downloadAppThanDownloadAppThanDeleteApp()">先加载，再加载，再删除</a>&nbsp;|&nbsp;
	<a href="javascript:replaceMobileNo()">更换手机号</a>&nbsp;|&nbsp;
	<a href="javascript:readNo()">读取卡号</a>
	<span id="cardNo"></span>
	<table>
		<tr>
			<th>名称</th>
			<th>AID</th>
		</tr>
		<tr>
			<td style="width: 5em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">测试应用1</td>
			<td>5061636B616765312E4170703100</td>
		</tr>
		<tr>
			<td style="width: 5em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">个人化测试</td>
			<td>001984102800100101</td>
		</tr>
		<tr>
			<td style="width: 5em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">升级测试</td>
			<td>001984102800010101</td>
		</tr>
		<tr>
			<td style="width: 5em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">东信手机钱包</td>
			<td>D1560001018003800000000100000000</td>
		</tr>
		<tr>
			<td style="width: 5em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">东信一卡通</td>
			<td>D1560001018000000000000100000000</td>
		</tr>
		<tr></tr>
		<tr>
			<td style="width: 5em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">测试用应用大小第三方安全域</td>
			<td>D1560001010001600000000100000001</td>
		</tr>
	</table>
	<table style="width: 1000px">
		<thead>
			<tr>
				<td style="width: 100px; text-align: center;">指令类型</td>
				<td style="width: 550px; text-align: center;">APDU指令</td>
				<td style="width: 50px; text-align: center;">状态码</td>
				<td style="width: 300px; text-align: center;">响应数据</td>
			</tr>
		</thead>
		<tbody id="apduList"></tbody>
	</table>
	<table style="display: none;">
		<tr id='apduListTemplate'>
			<td title="类型"></td>
			<td title="指令"></td>
			<td title="状态码"></td>
			<td title="数据"></td>
		</tr>
	</table>
</body>
</html>