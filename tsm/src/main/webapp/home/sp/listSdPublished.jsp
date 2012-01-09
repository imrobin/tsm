<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>安全域申请列表</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more-1.3.2.1.js"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>
<script type="text/javascript" src="${ctx}/lib/grid/grid.js"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
<script type="text/javascript" src="${ctx}/lib/lightface/LightFace.MessageBox.js"></script>

<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>

<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>

<script type="text/javascript" src="${ctx }/home/sp/js/listSd.js"></script>
<script type="text/javascript" src="${ctx }/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button'); //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	var ctx = '${ctx}';
	var status =
<%=request.getParameter("status")%>
	if (status == null)
		status = 0;
	var map = new Table();
	var typeMap = new Table();
	window.addEvent('domready', function() {
		
		$('listSd').addEvent('click', function() { self.location = ctx + '/home/sp/listSd.jsp'; });

		$('listSdArchived').addEvent('click', function() {self.location = ctx + '/home/sp/listSdArchived.jsp'; });
		$('listSdRequistion').addEvent('click', function() {self.location = ctx + '/home/sp/listSdRequistion.jsp';});
		var url = ctx + '/html/securityDomain/?m=listSelfByStatus&search_EQI_status=2&page_orderBy=id_desc,status_desc&t=' + new Date().getTime();
		var grid = new HtmlTable({
							properties : {
								border : 0,
								cellspacing : 0,
								style : 'width: 100%'
							},
							headers : [ '序号', '状态', '安全域AID', '安全域名称', '操作' ]
						});
						grid.inject($('grid'));

						var paging = new JIM.UI.Paging(
								{
									url : url,
									limit : 10,
									head : {
										el : 'nextpage',
										showNumber : true,
										showText : false
									},
									onAfterLoad : function(data) {
										grid.empty();

										//修改、撤销
										//查询状态为待审核的SD Apply
										data.result.forEach(function(sd, index) {
													var operation = '';
													var detail = '<a class="b" style="float : none;" href="javascript:detail(' + sd.id + ');"><span>查看</span></a>';
													var modify = '<a class="b" style="float : none;" href="javascript:modify(' + sd.id + ',' + sd.statusOriginal + ');"><span>修改</span></a>';
													var archive = '<a class="b" style="float : none;" href="javascript:archive(' + sd.id + ');"><span>归档</span></a>';
													var updateKey = '<a class="b" style="float : none;" href="javascript:updatekey(' + sd.id + ','+sd.currentKeyVersion+');"><span>升级密钥版本</span></a>';
													operation = detail + '|' + updateKey;
													if (sd.hasLock == 'y') {
														var requistion = getApply(sd.id);
														var type = getApplyType(sd.id);
														typeMap.set(sd.id, type);
														var cancel = '<a class="b" style="float : none;" href="javascript:cancel(' + requistion.id + ','+requistion.typeOriginal+');"><span>撤销' + type + '申请</span></a>';
														operation += '|' + cancel;
													} else {
														operation += '|' + modify + '|' + archive;

														var requistion = getApply(sd.id);
														if (requistion && !requistion.applicantReview && requistion.statusOriginal == 4) {
															map.set(sd.id, requistion);
															var info = '<a class="b" style="float : none;" href="javascript:info(' + sd.id + ');"><span style="color: red;">' + requistion.status + '</span></a>';
															operation += '|' + info;
														}
													}
													var whitespace = '&nbsp;';

													var sdName = addTip(
															'tip-sdName-'
																	+ sd.id,
															sd.sdName);
													var aid = addTip('tip-aid-'
															+ sd.id, sd.aid,
															'50%');

													grid
															.push([
																	{
																		content : (paging
																				.getPageNo()
																				* paging
																						.getLimit()
																				+ index + 1),
																		properties : {
																			align : "center",
																			width : "30px"
																		}
																	},
																	{
																		content : sd.status,
																		properties : {
																			align : "center"
																		}
																	},
																	{
																		content : aid,
																		properties : {
																			align : "center"
																		}
																	},
																	{
																		content : sd.sdName,
																		properties : {
																			align : "center"
																		}
																	},
																	{
																		content : operation,
																		properties : {
																			align : "center"
																		}
																	} ]);
												});
									}
								});
						paging.load();
					});

	function updatekey(id, currentKeyVersion) {
		currentKeyVersion = currentKeyVersion ? currentKeyVersion : '';
		var table = '<div><form method="post" id="updateForm">';
		table += '<dl><dd>';
		table += '<p class="regtextsd">安全域密钥版本号:</p>';
		table += '<p class="left inputs">';
		table += '<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate[\'required\',\'digit[1,47]\']" type="text" maxlength="2" />';
		table += '</p>';
		table += '</dd></dl>';
		table += '</form></div>';
		
		var div = new Element('div', {html : table});
		var form = new LightFace({
			title : '升级密钥版本',
			width : '100%',
			content : $('sdDivKeyVersion').get('html'),
			onClose : function() {
				
			},
			buttons : [ {
				title : '保 存',
				color : 'blue',
				event : function() {
					this.form = this.messageBox.getElement('form');
					new FormCheck(this.form, {
						submit : false,
						display : {
							showErrors : 0,
							indicateErrors : 1,
							scrollToFirst : false
						},
						onValidateSuccess : function() {
							var keyVersion = this.form.getElement('input[id=currentKeyVersion]').get('value');
							//console.log(keyVersion);
							/**/
							new Request.JSON({
								async : false,
								url : ctx + '/html/securityDomain/?m=sdModifyKeyVersion',
								onSuccess : function(data) {
									if(data.success) {
										new LightFace.MessageBox({
											onClose : function() {self.location = ctx + '/home/sp/listSdPublished.jsp';}
										}).info('操作成功');
									}
								}
							}).post({ sdId : id,  currentKeyVersion : keyVersion});
						}
					});
					this.messageBox.getElement('form').getElement('button').click();
				}
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
		
		form.addEvent('open', function() {
			this.messageBox.getElement('input[id=currentKeyVersion]').set('value', currentKeyVersion);
		});
		form.open();
		form.removeEvents('open');
	}

	function info(id) {
		var requistion = map.get(id);
		var reviewDate = requistion.reviewDate;
		var opinion = requistion.opinion;
		var type = requistion.type;
		var tr = '<tr><td align="center" width="60px">处理时间：</td><td align="left">'
				+ reviewDate + '</td></tr>';
		tr += '<tr><td align="center">处理意见：</td><td align="left" style="word-wrap: break-word;"><div style="word-wrap: break-word;">'
				+ opinion + '</div></td></tr>';
		tr += '<tr><td align="center">温馨提示：</td><td align="left">修改申请若审核被拒，请修改后重新提交；归档申请若审核被拒，可再次点击归档按钮，进行申请。</td></tr>';
		var table = '<div class="minfo"><table border="0" cellspacing="0">'
				+ tr + '</table></div>';
		new LightFace.MessageBox({
			title : type,
			width : '100%',
			onClose : function() {
				new Request.JSON({
					async : false,
					url : ctx + '/html/securityDomain/?m=signApply',
					onSuccess : function() {
						self.location = ctx + '/home/sp/listSdPublished.jsp';
					}
				}).post({
					id : requistion.id
				});
			}
		}).info(table);

	}

	function getApply(id) {
		var requistion = null;
		new Request.JSON({
			async : false,
			url : ctx + '/html/securityDomain/?m=getApply',
			onSuccess : function(result) {
				if (result.success) {
					requistion = result.message;
				}
			}
		}).post({
			id : id
		});
		return requistion;
	}

	function getApplyType(id) {
		var type = '';
		new Request.JSON({
			async : false,
			url : ctx + '/html/securityDomain/?m=getApplyType',
			onSuccess : function(result) {
				if (result.success) {
					type = result.message;
				} else {
					new LightFace.MessageBox().error(result.message);
				}
			}
		}).post({
			id : id
		});
		return type;
	}

	function detail(id) {
		self.location = ctx + '/home/sp/sdinfo.jsp?id=' + id + '&status=2';
	}

	function modify(id, status) {
		if (status == 1) {
			self.location = ctx + '/home/sp/modifySd1.jsp?id=' + id
					+ '&status=' + status;
		} else if (status == 2) {
			self.location = ctx + '/home/sp/modifySd2.jsp?id=' + id
					+ '&status=' + status;
		}
	}

	function cancel(id, type) {
		//cancelSdApply
		handleSdApply(id, type, 'cancelSdApply');
	}

	function archive(id) {
		//archiveSdApply
		handleSdApply(id, 0, 'archiveSdApply');
	}

	function handleSdApply(id, typeId, handleType) {
		var title = '';
		var content = '';
		if (handleType == 'cancelSdApply') {
			if(typeId == 22) {
				title = '撤销安全域归档申请';
				content = '确定要撤销已提交的安全域归档申请吗？';				
			} else if(typeId == 23) {
				title = '撤销安全域修改申请';
				content = '确定要撤销已提交的安全域修改申请吗？';
			}
			
			new LightFace.MessageBox({
				title : title,
				onClose : function(result) {
					if (result) {
						new Request.JSON({
							url : ctx + '/html/securityDomain/?m=' + handleType,
								onSuccess : function(data) {
									if (data.success) {
										new LightFace.MessageBox({
											onClose : function() {
												self.location = ctx + '/home/sp/listSdPublished.jsp';
											}
										}).info(data.message);
									} else {
										new LightFace.MessageBox().error(data.message);
									}
								}
							}
						).post({ sdApplyId : id });
					}
				}
			}).confirm(content);
		} else if (handleType == 'archiveSdApply') {
			title = '安全域归档申请';
			content = '请填写安全域归档原因：';
			new LightFace.MessageBox( {
				title : title,
				onClose : function(result, msg) {
					if (result) {
						new Request.JSON({
							url : ctx + '/html/securityDomain/?m=' + handleType,
							onSuccess : function(data) {
								if (data.success) {
									new LightFace.MessageBox({
										onClose : function() {
											self.location = ctx + '/home/sp/listSdPublished.jsp';
										}
									}).info(data.message);
								} else {
									new LightFace.MessageBox().error(data.message);
								}
							}
						}).post({sdId : id ,reason : msg});
					}
				}
			}).prompt(content, 200, true);
		}
	}
</script>

</head>

<body>

	<div id="container">

		<%@ include file="/common/header.jsp"%>

		<div class="curPosition">您的位置: 首页&gt;我的主页&gt;管理安全域&gt;列表&gt;已发布</div>

		<div id="main">
			<div class="div980 line_c">
				<div class="title980">
					<img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商
				</div>
				<div class="usercont">
					<div class="usermenu" id="usermenu">
						<%@ include file="/home/sp/menu.jsp"%>
					</div>

					<div class="userinput">
						<div id="userinput">
							<div class="titletab">
								<ul>
									<li class="s2" style="cursor: pointer;" id="listSd">待审核</li>
									<li class="s1">已发布</li>
									<li class="s2" style="cursor: pointer;" id="listSdArchived">已归档</li>
									<li class="s2" style="cursor: pointer;" id="listSdRequistion">申请历史</li>
								</ul>
							</div>
							<div class="minfo">
								<div id="grid"></div>
								<div id="nextpage" align="right"></div>
							</div>
						</div>

					</div>

				</div>
				<!--  -->
				<div id="sdDivKeyVersion" style="display: none;">
					<div>
						<form method="post">
							<input name="id" type="hidden" />
							<button class="validate['submit']" style="display: none;"></button>
							<dl>
								<dd>
									<p class="regtextsd">
										安全域密钥版本号:
									</p>
									<p class="left inputs">
										<input id="currentKeyVersion" name="currentKeyVersion" class="inputtext validate['required','digit[1,47]']" type="text" maxlength="2" />
									</p>
								</dd>
							</dl>
						</form>
					</div>
				</div>
			</div>
			<%@ include file="/common/footer.jsp"%>
		</div>
	</div>

</body>
</html>