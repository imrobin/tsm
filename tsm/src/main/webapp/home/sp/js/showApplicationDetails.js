Application = {};
Application.Details = new Class(
		{
			applicationVersionConstants : null,

			options : {},

			initialize : function(options) {
			},

			init : function() {
				this.getApplicationDetails();
				this.getApplictionVersions();
			},

			getApplictionVersions : function() {
				var appVersionNo = document.getElement('input[name=appVersionNo]').get('value');
				new Request.JSON({
					url : ctx + "/html/appVer/?m=index",
					data : {
						search_ALIAS_applicationL_EQL_id : applicationId,
						search_EQS_versionNo : appVersionNo
					},
					onSuccess : this.getApplictionVersionsCallback.bind(this)
				}).get();
			},

			getApplictionVersionsCallback : function(json) {
				new Request.JSON({
					async : false,
					url : ctx + "/html/appVer/?m=exportConstant",
					onSuccess : function(json) {
						if (json.success) {
							this.applicationVersionConstants = json.message;
						}
					}.bind(this)
				}).get();

				if (json.success) {
					var target = $('versionsTbody').empty();
					var template = $('versionsInfo');

					json.result.each(function(item) {
						var row = template.getElement('[title="versionsInfoTr"]').clone().erase("title");

						if ($chk(item.versionNo)) {
							row.getElement('[title="版本号"]').set("html", item.versionNo);
						}

						if ($chk(item.volatileSpace)) {
							row.getElement('[title="内存空间"]').set("html", item.volatileSpace);
						}

						if ($chk(item.nonVolatileSpace)) {
							row.getElement('[title="存储空间"]').set("html", item.nonVolatileSpace);
						}

						if ($chk(item.status)) {
							row.getElement('[title="状态"]').set("html", item.status);
						}

						row.getElement('[title="查看"]').set("href",
								ctx + "/home/sp/showApplicationVersionDetails.jsp?applicationVersionId=" + item.id);

						if ((this.applicationVersionConstants.STATUS_INIT == item.statusOriginal)
								|| (this.applicationVersionConstants.STATUS_UPLOADED == item.statusOriginal)) {
							row.getElement('[title="修改"]').set("href", ctx + "/home/sp/uploadCap.jsp?applicationVersionId=" + item.id);
						} else {
							row.getElement('[title="修改"]').addEvent("click", function(event) {
								event.stop();
								new LightFace.MessageBox().error('此版本不是初始化或已上传，不能修改');
							}.bind(this));
						}

						row.getElement('[title="管理客户端"]').addEvent("click", function(event) {
							event.stop();
							window.location.href = ctx + "/home/sp/manageClient.jsp?applicationVersionId=" + item.id;
						}.bind(this));

						row.getElement('[title="删除"]').addEvent("click", function(event) {
							event.stop();
							if (this.applicationVersionConstants.STATUS_INIT == item.statusOriginal) {
								this.confirmRemoveApplicaitonVersion(item.id);
							} else {
								new LightFace.MessageBox().error('此版本不是初始化，不能删除');
							}
						}.bind(this));

						row.getElement('[title="归档"]').addEvent("click", function(event) {
							event.stop();
							if (this.applicationVersionConstants.STATUS_PULISHED == item.statusOriginal) {
								this.confirmArchiveApplicaitonVersion(item.id, this);
							} else {
								new LightFace.MessageBox().error('此版本不是已发布，不能归档');
							}
						}.bind(this));
						// 判定是否已经归档过
						if (this.applicationVersionConstants.STATUS_PULISHED == item.statusOriginal) {
							new Request.JSON({
								async : false,
								url : ctx + "/html/appVer/?m=hasArchiveRequest&appVerId=" + item.id,
								onSuccess : function(json) {
									if (json.success) {
										// alert(json.message);
										if (json.message != 0) {
											row.getElement('[title="归档"]').set('html', '<span>撤销归档申请</span>');
											row.getElement('[title="归档"]').removeEvents('click');
											// row.getElement('[title="归档"]').set('href',ctx+'/html/requistion/?m=remove&requistionId='+json.message);
											row.getElement('[title="归档"]').addEvent("click", function(event) {
												event.stop();
												this.confirmRemoveArchive(json.message);
											}.bind(this));
											row.getElement('[title="归档"]').set('title', '撤销归档');
										}
									}
								}.bind(this)
							}).get();
						}
						/*
						 * if (this.applicationVersionConstants.STATUS_UPLOADED ==
						 * item.statusOriginal) {
						 * row.getElement('[title="测试"]').addEvent("click",
						 * function(event) { event.stop();
						 * this.uploadTest(item.id); }.bind(this));
						 * row.getElement('[title="测试"]').setStyle('display',
						 * ''); }
						 */
						// end
						row.inject(target);
					}.bind(this));

				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},
			uploadTest : function(appVerId) {
				var details = this;
				var tableDiv = '<div id="uploadFileListDiv" class="minfo">';
				tableDiv += '<div id="grid"></div>';
				tableDiv += '<div><a class="subbutt1 mag" href="#" id="upFileA"><span>上传文件</span></a></div><div id="nextpage" align="right"></div>';
				tableDiv += '</div>';
				var box = new LightFace({
					content : tableDiv,
					width : 800,
					height : 300,
					draggable : false,
					buttons : [ {
						title : '完成测试',
						event : function() {
							new Request.JSON({
								url : ctx + "/html/appVer/?m=finishTest&appVerId=" + appVerId,
								onSuccess : function(result) {
									if (result.success) {
										new LightFace.MessageBox({
											onClose : function() {
												box.close();
												location.reload();
											}
										}).info("操作成功");
									} else {
										new LightFace.MessageBox().error(result.message);
									}
								}
							}).get();
						}
					}, {
						title : '退出',
						event : function() {
							this.close();
						}
					} ]
				});
				var page = box.messageBox.getElement('[id="nextpage"]');
				var wgrid = box.messageBox.getElement('[id="grid"]');

				var grid = new HtmlTable({
					properties : {
						border : 0,
						cellspacing : 0,
						style : 'width: 100%'
					},
					headers : [ '文件名', '文件说明', '上传时间', '序列号', '操作' ]
				});
				grid.inject(wgrid);
				function addTip(id, content, width) {
					var span = null;
					if (width == undefined)
						width = '90px';
					span = new Element('span', {
						'class' : 'texthidden',
						style : 'width:' + width
					});
					span.appendText(content);
					span.addEvent('mouseover', function(event) {
						div = new Element('div', {
							id : id,
							styles : {
								position : 'absolute',
								border : '1px solid #A5CBDB',
								background : '#F6F6F6',
								padding : '1px',
								color : '#333',
								top : event.page.y + 'px',
								left : event.page.x + 'px',
								'z-index' : '99999',
								display : 'none',
								'word-break' : 'break-all',
								'word-wrap' : 'break-word',
								'max-width' : '200px'
							},
							html : content
						});
						div.inject(document.body, 'bottom');
						div.setStyle('display', '');
					});
					span.addEvent('mouseout', function(event) {
						if ($chk($(id)))
							$(id).dispose();
					});

					return span;
				}
				var paging = new JIM.UI.Paging({
					url : ctx + "/html/testfile/?m=index&search_EQL_appVer.id=" + appVerId,
					limit : 5,
					head : {
						el : page,
						showNumber : true,
						showText : false
					},
					onAfterLoad : function(json) {
						if (json.success) {
							grid.empty();
							json.result.forEach(function(item, index) {
								var uploadA = new Element('a', {
									'class' : 'b'
								});
								uploadA.setStyle('float', 'none');
								uploadA.set('html', '<span>删除</span>');
								var comments = addTip('tip-aid-' + item.id, item.comments, '250px');
								var name = addTip('tip-aid-' + item.id, item.originalName, '250px');
								grid.push([ {
									content : name,
									properties : {
										align : "center",
										width : "150px"
									}
								}, {
									content : comments,
									properties : {
										'align' : "center",
										width : "150px"
									}
								}, {
									content : item.uploadDate,
									properties : {
										align : "center",
										width : "70px"
									}
								}, {
									content : item.seqNum,
									properties : {
										align : "center",
										width : "50px"
									}
								}, {
									content : uploadA,
									properties : {
										align : "center",
										width : "50px"
									}
								} ]);
								uploadA.addEvent('click', function(event) {
									event.stop();
									new LightFace.MessageBox({
										onClose : function() {
											if (this.result) {
												new Request.JSON({
													url : ctx + "/html/testfile/?m=delTestFile&tfId=" + item.id,
													onSuccess : function(result) {
														if (result.success) {
															new LightFace.MessageBox().info("操作成功");
															paging.load();
														} else {
															new LightFace.MessageBox().error(result.message);
														}
													}
												}).get();
											}
										}
									}).confirm("您确认要进行删除此测试文件吗？");
								});
							}.bind(this));
						} else {
							new LightFace.MessageBox().error(json.message);
						}
					}
				});
				box.messageBox
						.getElement('[id="upFileA"]')
						.addEvent(
								'click',
								function(e) {
									e.stop();
									var upForm = $('uploadForm').get('html');
									var formWin = new LightFace({
										content : upForm,
										draggable : false,
										onClose : function() {
											var div = document.getElement('div[class=fc-tbx]');
											if ($chk(div)) {
												div.dispose();
											}
										},
										buttons : [ {
											title : '保存',
											event : function() {
												formWin.messageBox.getElement('form').getElement('button').click();
											}.bind(this),
											color : 'blue'
										}, {
											title : '退出',
											event : function() {
												this.close();
											}
										} ]
									});
									formWin.messageBox.getElement('[id="appverId"]').set('value', appVerId);
									var form = formWin.messageBox.getElement('form');
									var validater = new FormCheck(form, {
										submit : false,
										trimValue : false,
										display : {
											showErrors : 1,
											errorsLocation : 1,
											indicateErrors : 1,
											keepFocusOnError : 0,
											closeTipsButton : 0,
											scrollToFirst : false,
											removeClassErrorOnTipClosure : 1
										},
										onValidateSuccess : function() {
											new Request.JSON({
												url : form.get('action'),
												onSuccess : function(result) {
													if (result.success) {
														new LightFace.MessageBox().info("操作成功");
														formWin.close();
														paging.load();
													} else {
														new LightFace.MessageBox().error(result.message);
													}
												},
												onError : function(result) {
												}
											}).post(form.toQueryString());
										}
									});

									formWin.messageBox.getElement('[id="spanButtonPlaceholder"]').set("id", 'winUploadHolder');
									formWin.messageBox.getElement('[id="divFileProgressContainer"]').set("id", 'winProgressContainer');
									formWin.messageBox.getElement('[id="tetsFileName"]').set("id", 'winTetsFileName');
									formWin.messageBox.getElement('[id="tempFilename"]').set("id", 'winTempFilename');
									var swfu = new SWFUpload(
											{
												upload_url : ctx + '/html/testfile/?m=upload',
												post_params : {},
												// File Upload Settings
												file_size_limit : "5 MB", // 2MB
												file_upload_limit : "0",
												file_queue_error_handler : details.oversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : function(file, result, responseReceived) {
													result = JSON.decode(result);
													if (result.success) {
														formWin.messageBox.getElement('[id="winTetsFileName"]').set("value",
																decodeURI(result.message.oldFileName));
														formWin.messageBox.getElement('[id="winTempFilename"]').set("value",
																decodeURI(result.message.filename));
													} else {
														new LightFace.MessageBox().error2("上传失败，" + result.message);
													}
												},
												upload_complete_handler : uploadComplete,
												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder_id : "winUploadHolder",
												button_width : 180,
												button_height : 18,
												button_text : '<span class="button">请选择文件<span class="buttonSmall">(5MB 最大)</span></span>',
												button_text_style : '.button { font-family: "微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,
												// Flash Settings
												flash_url : ctx + "/lib/uploadManager/swfupload.swf",
												custom_settings : {
													upload_target : "winProgressContainer"
												},
												// Debug Settings
												debug : false
											});
									formWin.open();
								});
				box.open();
				paging.load();
			},
			oversize : function() {
				new LightFace.MessageBox().error('文件容量不能超过5MB');
			},
			confirmRemoveArchive : function(reqId) {
				var confirm = null;
				confirm = new LightFace({
					title : "确认撤销",
					appletModal : this.modal,
					height : 50,
					width : 300,
					content : "确认撤销？",
					resetOnScroll : false,
					buttons : [ {
						title : "确认",
						color : "blue",
						event : function() {
							confirm.close();
							this.removeArchiveRequest(reqId);
						}.bind(this)
					}, {
						title : "取消",
						event : function() {
							confirm.close();
						}.bind(this)
					} ]
				});
				confirm.open();
			},
			confirmRemoveApplicaitonVersion : function(applicationVersionId) {
				var confirm = null;
				confirm = new LightFace({
					title : "确认删除",
					appletModal : this.modal,
					height : 50,
					width : 300,
					content : "确认删除？",
					resetOnScroll : false,
					buttons : [ {
						title : "确认",
						color : "blue",
						event : function() {
							confirm.close();
							this.removeApplicaitonVersion(applicationVersionId);
						}.bind(this)
					}, {
						title : "取消",
						event : function() {
							confirm.close();
						}.bind(this)
					} ]
				});
				confirm.open();
			},
			removeArchiveRequest : function(reqId) {
				new Request.JSON({
					url : ctx + '/html/requistion/?m=remove',
					data : {
						requistionId : reqId
					},
					onSuccess : this.removeArchiveRequestCallback.bind(this)
				}).post();

			},
			removeApplicaitonVersion : function(applicationVersionId) {
				new Request.JSON({
					url : ctx + '/html/appVer/?m=remove',
					data : {
						applicationVersionId : applicationVersionId
					},
					onSuccess : this.removeApplicaitonVersionCallback.bind(this)
				}).post();

			},
			removeArchiveRequestCallback : function(json) {
				if (json.success) {
					new LightFace.MessageBox().info('撤销成功');
					this.getApplictionVersions();
				} else {
					new LightFace.MessageBox().error2('撤销失败，' + json.message);
				}
			},
			removeApplicaitonVersionCallback : function(json) {
				if (json.success) {
					new LightFace.MessageBox().info('删除成功');
					this.getApplictionVersions();
				} else {
					new LightFace.MessageBox().error2('删除失败，' + json.message);
				}
			},
			confirmArchiveApplicaitonVersion : function(id, grid) {
				new LightFace.MessageBox({
					onClose : function() {
						if (this.result) {
							var reason = this.resultMessage;
							new Request.JSON({
								url : ctx + '/html/appVer/?m=archiveApp',
								onSuccess : function(data) {
									if (data.success) {
										new LightFace.MessageBox({
											onClose : function() {
												window.location.reload();
											}.bind(this)
										}).info(data.message);
									} else {
										new LightFace.MessageBox({
											onClose : function() {
												window.location.reload();
											}.bind(this)
										}).error(data.message);
									}
								}
							}).post({
								'appVerIds' : id,
								'reason' : reason
							});
						}
					}
				}).confirm("您确认要归档该版本吗？");
			},

			getApplicationDetails : function() {
				new Request.JSON({
					url : ctx + "/html/application/?m=getByCriteria",
					data : {
						search_EQL_id : applicationId
					},
					onSuccess : this.getApplicationDetailsCallback.bind(this)
				}).get();
			},

			getApplicationDetailsCallback : function(json) {
				if (json.success) {
					var details = $('applicationDetails');
					var application = json.result[0];

					details.getElement('[title="应用名称"]').set("html", application.name);
					details.getElement('[title="应用类型"]').set("html", application.form);
					details.getElement('[title="AID"]').set("html", application.aid);
					details.getElement('[title="应用描述"]').set("html", application.description);
					if ($chk(application.childType_id)) {
						new Request.JSON({
							url : ctx + "/html/applicationType/?m=getTypeById",
							data : {
								id : application.childType_id
							},
							onSuccess : function(json) {
								if (json.success) {
									details.getElement('[title="业务类型"]').set("html", json.message);
								}
							}
						}).get();
					}
					details.getElement('[title="个人化类型"]').set("html", application.personalType);
					details.getElement('[title="个人化指令传输加密算法"]').set("html", application.persoCmdTransferSecureAlgorithm.name);
					details.getElement('[title="个人化指令敏感数据加密算法"]').set("html", application.persoCmdSensitiveDataSecureAlgorithm.name);
					details.getElement('[title="是否需要订购"]').set("html", application.needSubscribe);
					details.getElement('[title="预置时收费条件"]').set("html", application.presetChargeCondition);
					details.getElement('[title="所属安全域模式"]').set("html", application.sdModel);
					if ($chk(application.sd_id)) {
						new Request.JSON({
							url : ctx + "/html/securityDomain/?m=index",
							data : {
								search_EQL_id : application.sd_id
							},
							onSuccess : function(json) {
								if (json.success) {
									var sd = json.result[0];
									details.getElement('[title="所属安全域"]').set("html", sd.sdName + '(' + sd.aid + ')');
								}
							}
						}).get();

					}
					details.getElement('[title="业务平台URL"]').set("html", application.businessPlatformUrl);
					details.getElement('[title="业务平台服务名"]').set("html", application.serviceName);
					details.getElement('[title="删除规则"]').set("html", application.deleteRule);
					details.getElement('[title="所在地"]').set("html", application.location);
					details.getElement('[title="PC版图标"]').set("src", ctx + "/html/application/?m=getAppPcImg&appId=" + application.id);
					details.getElement('[title="手机版图标"]').set("src", ctx + "/html/application/?m=getAppMobileImg&appId=" + application.id);
					// 获取应用截图
					new Request.JSON({
						url : ctx + "/html/application/?m=getImgIdByAppId",
						data : {
							applicationId : application.id
						},
						onSuccess : function(json) {
							if (json.success && json.message != '') {
								var ids = json.message.split(",");
								for ( var i = 0; i < ids.length; i++) {
									// new Element('img').set('src',ctx
									// +
									// "/html/application/?m=getAppImg&appImgId="
									// + ids[i]).set('width','95')
									// .set('height','140').inject(details.getElement("[title='应用截图']"));
									new Element('span').set(
											'html',
											'<img src="' + ctx + "/html/application/?m=getAppImg&appImgId=" + ids[i]
													+ '" width="95" height="140"/>&nbsp').inject(details.getElement("[title='应用截图']"));

								}
							}
						}
					}).get();
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			}
		});