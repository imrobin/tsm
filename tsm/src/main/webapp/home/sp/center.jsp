<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用提供商首页</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script src="${ctx}/lib/paging/paging.js" type="text/javascript" ></script>

<script type="text/javascript" src="${ctx }/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var div;
	var ctx = '${ctx}';
	var map = new Table();
	window.addEvent('domready', function() {
		
		var spId = '';
		var currentUser = new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getCurrentSp',
			onSuccess : function(result) {
				if(result.success) {
					var sp = result.message;
					spId = sp.id;
					//handle sp base info
					var html = '编号：'+sp.no+'<br/>全称：'+sp.name+'<br/>简称：'+sp.shortName+'<br/>邮箱：'+sp.sysUser_email+'<br/><a class="b" href="'+ctx+'/home/sp/spinfoDetail.jsp">查看注册信息</a>';
					html += ' | <a class="b" href="${ctx}/home/sp/listSpRequistion.jsp">审核信息</a>';
					
					
					if(sp.status == 1) {
						html += ' | <a id="btn" class="b" href="'+ctx+'/home/sp/spSummary.jsp">编辑企业简介</a>';
					}
					
					if(sp.hasLock == 1) {
						html += ' | <a class="b" href="'+ctx+'/home/sp/modifySp.jsp">修改注册信息</a>';
						if(sp.requistion && sp.requistion != null && sp.requistion.status == 4 && sp.requistion.applicantReview == null) {
							map.set(sp.id, sp.requistion);
							html += ' | <a href="javascript:info('+sp.id+')"><span style="color : red;">审核未通过</span></a>';
						}
					} else {
						if(sp.requistion && sp.requistion != null && sp.requistion.status == 1 && sp.requistion.type == 32) {
							html += ' | <a class="b" href="javascript:cancelApply('+sp.requistion.id+')">撤销修改注册信息申请</a>';
						}
					}
					
					//console.log(sp);
					
					$('column_a').set('html', html);
					html = '发布应用：'+sp.avalidApp+'个';
					
					$('column_b').set('html', html);
					
				} else {
					new LightFace.MessageBox({
						onClose : function() {
							self.location = ctx + '/html/login/';
						}
					}).error('用户未登录');
				}
			}
		});
		currentUser.post({t : new Date().getTime()});
		
		
		//SD审核信息
		/*
		*/
		var tableSd = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','申请时间','处理时间','申请类型','状态','审核结果','审核意见','安全域AID','安全域名称']
		});
		tableSd.inject($('gridSd'));
		
		var paging = new JIM.UI.Paging({
			url : ctx + '/html/securityDomain/?m=getApplyList&page_orderBy=requistion_submitDate_desc&t='+new Date().getTime(),
			limit : 5,
			head : {el : 'sdnextpage', showNumber : true, showText : false},
			onAfterLoad: function(result) {
				tableSd.empty();
				var whitespace = '&nbsp;';
        		result.result.forEach(function(e, index) {
        			var reviewDate = $chk(e.requistion_reviewDate) ? e.requistion_reviewDate : whitespace;
					var pass = e.requistion_result != undefined ? e.requistion_result : whitespace;
					var opinion = $chk(e.requistion_opinion) ? e.requistion_opinion : '';
					var tipDivId = 'tip-opinion-' + e.id;
					opinion = addTip(tipDivId, opinion);
					var sdName = addTip('tip-sdName-' + e.id, e.sdName);
					var aid = addTip('tip-aid-' + e.id, e.aid);
					
					tableSd.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : 'center'}},
					              {content : e.requistion_submitDate, properties : {align : 'center'}},
					              {content : reviewDate, properties : {align : 'center'}},
					              {content : e.applyType, properties : {align : 'center', width : '70px'}},
					              {content : e.status, properties : {align : 'center'}},
					              {content : pass, properties : {align : 'center'}},
					              {content : opinion, properties : {align : 'center', style : 'max-width: 80px;word-break: break-all;word-wrap: break-word;'}},
					              {content : aid, properties : {align : 'center'}},
					              {content : sdName, properties : {align : 'center'}}]);
        		});
			}
		});
		//应用审核信息 
		var tableApp = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','申请时间','处理时间','申请类型','状态','审核结果','审核意见','应用AID','应用名称','应用版本']
		});
		tableApp.inject($('gridApp'));
		
		var paging2 = new JIM.UI.Paging({
			url : ctx + '/html/requistion/?m=index&spId='+spId+'&page_orderBy=submitDate_desc&t='+new Date().getTime(),
			limit : 5,
			head : {el : 'appnextpage', showNumber : true, showText : false},
			onAfterLoad: function(result) {
				tableApp.empty();
				var whitespace = '&nbsp;';
        		result.result.forEach(function(e, index) {
        			var reviewDate = $chk(e.reviewDate) ? e.reviewDate : whitespace;
					var pass = e.result != undefined ? e.result : whitespace;
					var opinion = $chk(e.opinion) ? e.opinion : '';
					var tipDivId = 'tip-opinion-' + e.id;
					opinion = addTip(tipDivId, opinion);
					var appName = addTip('tip-sdName-' + e.id, e.appName);
					var aid = addTip('tip-aid-' + e.id, e.appAid);
					
					tableApp.push([{content : (paging2.getPageNo() * paging2.getLimit() + index + 1), properties : {align : 'center'}},
					              {content : e.submitDate, properties : {align : 'center'}},
					              {content : reviewDate, properties : {align : 'center'}},
					              {content : e.type, properties : {align : 'center', width : '70px'}},
					              {content : e.status, properties : {align : 'center'}},
					              {content : pass, properties : {align : 'center'}},
					              {content : opinion, properties : {align : 'center', style : 'max-width: 80px;word-break: break-all;word-wrap: break-word;'}},
					              {content : aid, properties : {align : 'center'}},
					              {content : appName, properties : {align : 'center'}},
					              {content : e.versionNo, properties : {align : 'center'}}]);
        		});
			}
		});
		//应用end
		paging.load();
		paging2.load();
	});
	
	function info(id) {
		var sd = map.get(id);
		var reviewDate = sd.reviewDate;
		var date = new Date();
		date.setTime(sd.reviewDate);
		var opinion = sd.opinion;
		var tr  = '<tr><td align="center" width="60px">审核时间：</td><td align="left">'+date.toLocaleString()+'</td></tr>';
		    tr += '<tr><td align="center">审核意见：</td><td align="left">'+opinion+'</td></tr>';
		    tr += '<tr><td align="center">温馨提示：</td><td align="left">若审核不通过，请重新修改信息后提交</td></tr>';
		var table = '<div class="minfo"><table border="0" cellspacing="0">'+tr+'</table></div>';
		new LightFace.MessageBox({
			title : '审核未通过', 
			width : '100%', 
			onClose : function() {
				new Request.JSON({
					url : ctx + '/html/securityDomain/?m=signApply',
					onSuccess : function() {
						self.location = ctx + '/home/sp/center.jsp';
					}
				}).post({id : sd.id, t : new Date().getTime()});
		}}).info(table);
		
	}
	
	function cancelApply(id) {
		var content = '确定要撤销已提交的修改注册信息申请吗？';
		var title = '撤销修改注册信息申请';
		new LightFace.MessageBox({
			title : title,
			onClose : function(result) {
				if(result) {
					new Request.JSON({
						url : ctx + '/html/spBaseInfo/?m=cancelApply',
						onSuccess : function(data) {
							if(data.success) {
								new LightFace.MessageBox({
									onClose : function() {
										self.location = ctx + '/home/sp/center.jsp';
									}
								}).info(data.message);
							} else {
								new LightFace.MessageBox().error('');
							}
						}
					}).post({id : id, t : new Date().getTime()});
				}
			}
		}).confirm(content);
	}
	
	$('btn').addEvent('click', function(event) {
		var box = new LightFace.MessageBox({
			title : 'demo',
			width : 500,
			height : 500,
			buttons : [ {
				title : '保存',
				event : function() {
					var textarea = box.messageBox.getElement('[id=textarea-1]');
					alert('内容：[\n' + textarea.get('html')+'\n]');
					this.close();
				}
			}, {
				title : '关闭', 
				event : function() {
					this.close();
				}
			}]
		});
		box.options.content = $('divHidden').get('html');
		box.addEvent('open', function(event) {
			var textarea = box.messageBox.getElement('[id=textarea-1]');
			var rate = 0.93;
			var width = box.options.width * rate;
			var height = box.options.height * 0.92;
			//console.log(width);
			//console.log(height);
			textarea.setStyles({
				width : width,
				height : height
			});
			textarea.mooEditable();
		});
		box.open();
	});
</script>
</head>

<body>
<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">
<div class="pinfo">
	<p class="pimg"><img src="${ctx}/html/spBaseInfo/?m=loadSpFirmLogo" style="width: 80px;height: 80px;" alt="firmLogo" /></p>
	<p class="ptext" id="column_a"></p>
	<p class="ptext" id="column_b"></p>
	<p class="ptext" id="column_c"></p>
</div>
<!-- 
 -->
<p></p>
<div id="userinput" style="">
	<div id="divHidden" style="display: none;">
		<textarea id="textarea-1" name="editable1">demo</textarea>
	</div>
	
	<div class="minfo">
		<h3>安全域审核信息</h3>
		<div id="gridSd"></div>
		<div id="sdnextpage" align="right"></div>
	</div>
	<div id="detail">
		<div class="minfo" id="minfo"></div>
	</div>
</div>
<div id="userinput2" style="">
	<div class="minfo">
		<h3>应用审核信息</h3>
		<div id="gridApp"></div>
		<div id="appnextpage" align="right"></div>
	</div>
	<div id="appdetail">
		<div class="minfo" id="appminfo"></div>
	</div>
</div>
</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
</body>

</html>