/**
 * 应用管理测试类
 */
var AppManager = AppManager ? AppManager : {};
// 应用测试
AppManager.Test = new Class(
		{
			Implements : [ Events, Options ],
			options : {

			},
			getConstants : function() {
				new Request.JSON({
					url : ctx + "/html/localtransaction/?m=exportConstant",
					onSuccess : function(json) {
						if (json.success) {
							this.transConstant = json.message;
						}
					}.bind(this)
				}).get();
			},
			initialize : function(options) {
				var appM = this;
				this.getConstants();
				var cal = new Customer.Cal();
				appM.grid = new JIM.UI.Grid(
						'tableDiv',
						{
							url : ctx + '/html/appVer/?m=index&search_EQI_status=5&local=true',
							multipleSelection : false,
							buttons : [
									{
										name : '在线测试',
										icon : ctx + '/admin/images/test.png',
										handler : function() {
											if (this.selectIds != '') {
												var appverId = this.selectIds[0];
												new Request.JSON({
													url : ctx + '/html/appVer/?m=getAidAndVerByAppverId&appverId=' + appverId,
													onSuccess : function(json) {
														if (json.success) {
															var upWin = $('onlineTestDiv').get('html');
															var appInfoWindow = new LightFace({
																draggable : true,
																initDraw : true,
																title : '在线测试',
																content : upWin,
																onClose : function() {
																},
																buttons : [ {
																	title : '关 闭',
																	color : 'blue',
																	event : function() {
																		this.close();
																	}
																} ]
															});
															appM.setEventForOnlineTest(appInfoWindow, json.message.aid, json.message.ver);
														}
													}
												}).get();
											} else {
												new LightFace.MessageBox().error("请先选择列表中的应用版本");
											}
										}
									},
									{
										name : '离线测试',
										icon : ctx + '/admin/images/test.png',
										handler : function() {
											if (this.selectIds != '') {
												var appverId = this.selectIds[0];
												var fileGird = new JIM.UI.WinGrid(
														{
															url : ctx + "/html/testfile/?m=index&search_EQL_appVer.id=" + appverId,
															multipleSelection : false,
															height : 330,
															width : 800,
															onClose : function() {
															},
															winButtons : [
																	{
																		title : '上传新文件',
																		color : 'blue',
																		event : function() {
																			fileGird.hideFoot();
																			var upForm = $('uploadForm').get('html');
																			var formWin = new LightFace({
																				titile : '上传新文件',
																				content : upForm,
																				mask : true,
																				draggable : false,
																				onClose : function() {
																					fileGird.showFoot();
																					var div = document.getElement('div[class=fc-tbx]');
																					if ($chk(div)) {
																						div.dispose();
																					}
																				},
																				buttons : [
																						{
																							title : '保存',
																							event : function() {
																								formWin.messageBox.getElement('form')
																										.getElement('button').click();
																							}.bind(this),
																							color : 'blue'
																						}, {
																							title : '退出',
																							event : function() {
																								this.close();
																							}
																						} ]
																			});
																			formWin.messageBox.getElement('[id="appverId"]').set('value',
																					appverId);
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
																								fileGird.showFoot();
																								fileGird.load();
																							} else {
																								new LightFace.MessageBox()
																										.error(result.message);
																							}
																						},
																						onError : function(result) {
																						}
																					}).post(form.toQueryString());
																				}
																			});

																			formWin.messageBox.getElement('[id="spanButtonPlaceholder"]')
																					.set("id", 'winUploadHolder');
																			formWin.messageBox
																					.getElement('[id="divFileProgressContainer"]').set(
																							"id", 'winProgressContainer');
																			formWin.messageBox.getElement('[id="tetsFileName"]').set("id",
																					'winTetsFileName');
																			formWin.messageBox.getElement('[id="tempFilename"]').set("id",
																					'winTempFilename');
																			var swfu = new SWFUpload(
																					{
																						upload_url : ctx + '/html/testfile/?m=upload',
																						post_params : {},
																						file_size_limit : "5 MB", // 2MB
																						file_upload_limit : "0",
																						file_queue_error_handler : function() {
																							new LightFace.MessageBox().info('文件容量不能超过5MB');
																						},
																						file_dialog_complete_handler : fileDialogComplete,
																						upload_progress_handler : uploadProgress,
																						upload_error_handler : uploadError,
																						upload_success_handler : function(file, result,
																								responseReceived) {
																							result = JSON.decode(result);
																							if (result.success) {
																								formWin.messageBox
																										.getElement(
																												'[id="winTetsFileName"]')
																										.set(
																												"value",
																												decodeURI(result.message.oldFileName));
																								formWin.messageBox.getElement(
																										'[id="winTempFilename"]').set(
																										"value",
																										decodeURI(result.message.filename));
																							} else {
																								new LightFace.MessageBox().error(result.message);
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
																						flash_url : ctx
																								+ "/lib/uploadManager/swfupload.swf",
																						custom_settings : {
																							upload_target : "winProgressContainer"
																						},
																						debug : false
																					});
																			formWin.open();
																		}
																	},
																	{
																		title : '删除文件',
																		color : 'blue',
																		event : function() {
																			var selectIds = fileGird.selectIds;
																			if (this.selectIds != '') {
																				new LightFace.MessageBox(
																						{
																							onClose : function() {
																								if (this.result) {
																									new Request.JSON(
																											{
																												url : ctx
																														+ "/html/testfile/?m=delTestFile&tfId="
																														+ selectIds[0],
																												onSuccess : function(result) {
																													if (result.success) {
																														new LightFace.MessageBox()
																																.info("操作成功");
																														fileGird.load();
																													} else {
																														new LightFace.MessageBox()
																																.error(result.message);
																													}
																												}
																											}).get();
																								}
																							}
																						}).confirm("您确认要删除此测试文件吗？");
																			} else {
																				new LightFace.MessageBox().error("请先选择列表中文件");
																			}
																		}
																	},
																	{
																		title : '下载文件',
																		color : 'blue',
																		event : function() {
																			var selectIds = fileGird.selectIds;
																			if (fileGird.selectIds != '') {
																				window.location.href = ctx
																						+ '/html/testfile/?m=downFile&tfId=' + selectIds[0];
																			} else {
																				new LightFace.MessageBox().error("请先选择列表中文件");
																			}
																		}
																	}, {
																		title : '退出',
																		event : function() {
																			this.close();
																		}
																	} ],
															drawButtons : false,
															drawSearch : false,
															columnModel : [ {
																dataName : 'id',
																identity : true
															}, {
																title : '文件名',
																dataName : 'originalName'
															}, {
																title : '文件说明',
																dataName : 'comments'
															}, {
																title : '上传时间',
																dataName : 'uploadDate'
															} ],
															searchButton : false,
															searchBar : {
																filters : []
															},
															headerText : '离线测试'
														});
											} else {
												new LightFace.MessageBox().error("请先选择列表中的应用版本");
											}
										}

									},
									{
										name : '提交测试结果',
										icon : ctx + '/admin/images/test.png',
										handler : function() {
											if (this.selectIds != '') {
												var appverId = this.selectIds[0];
												var grid2 = new JIM.UI.WinGrid({
													url : ctx + "/html/appverTest/?m=index&search_EQL_appVer.id=" + appverId,
													multipleSelection : false,
													height : 330,
													width : 800,
													winButtons : [
															{
																title : '增加测试结果',
																color : 'blue',
																event : function() {
																	appM.submitResult(appverId,grid2);
																}
															},
															{
																title : '删除测试结果',
																color : 'blue',
																event : function() {
																	var selectIds = grid2.selectIds;
																	if (this.selectIds != '') {
																		new LightFace.MessageBox({
																			onClose : function() {
																				if (this.result) {
																					new Request.JSON({
																						url : ctx + "/html/appverTest/?m=remove&tfId="
																								+ selectIds[0],
																						onSuccess : function(result) {
																							if (result.success) {
																								new LightFace.MessageBox().info("操作成功");
																								grid2.load();
																							} else {
																								new LightFace.MessageBox()
																										.error(result.message);
																							}
																						}
																					}).get();
																				}
																			}
																		}).confirm("您确认要删除此结果吗？");
																	} else {
																		new LightFace.MessageBox().error("请先选择列表中结果");
																	}
																}
															}, {
																title : '退出',
																event : function() {
																	this.close();
																}
															} ],
													drawButtons : false,
													drawSearch : false,
													columnModel : [ {
														dataName : 'id',
														identity : true
													}, {
														title : '测试日期',
														dataName : 'testDate'
													}, {
														title : '测试手机号',
														dataName : 'mobileNo'
													}, {
														title : 'NFC终端型号',
														dataName : 'modelType'
													}, {
														title : 'SE芯片类型',
														dataName : 'seType'
													}, {
														title : '卡批次',
														dataName : 'cardBaseInfo_name'
													}, {
														title : '测试结果',
														dataName : 'result'
													}, {
														title : '说明',
														dataName : 'resultComment'
													}, {
														title : '提交者',
														dataName : 'author'
													} ],
													searchButton : false,
													searchBar : {
														filters : []
													},
													headerText : '测试结果'
												});
											} else {
												new LightFace.MessageBox().error("请先选择列表中的应用版本");
											}
										}
									} ,{
										name : '完成应用测试',
										icon : ctx + '/admin/images/test.png',
										handler : function() {
											if (this.selectIds != '') {
												var appverId = this.selectIds[0];
												new LightFace.MessageBox( {
													onClose : function() {
														if (this.result) {
															appM.finishTest(appverId,appM.grid);
														}
													}
												}).confirm("您确认要完成该应用版本的测试吗？");
											}else{
												new LightFace.MessageBox().error("请先选择列表中的应用版本");
											}
										}
									}],
							columnModel : [ {
								dataName : 'id',
								identity : true
							}, {
								title : '所属应用名称',
								dataName : 'application_name',
								order : false
							}, {
								title : '版本号',
								dataName : 'versionNo'
							}, {
								title : '状态',
								dataName : 'status'
							}, {
								title : '占用内存空间(Byte)',
								dataName : 'volatileSpace'
							}, {
								title : '占用存储空间(Byte)',
								dataName : 'nonVolatileSpace'
							} ],
							searchButton : true,
							searchBar : {
								filters : [ {
									title : '应用名称：',
									name : 'search_ALIAS_applicationL_LIKES_name',
									type : 'text',
									width : 150
								} ]
							},
							headerText : '测试结果 '
						});
			},
			finishTest : function(appVerId, grid) {
				var appM = this;
				new Request.JSON({
					url : ctx + "/html/appVer/?m=finishTest",
					onSuccess : function(result) {
						if (result.success) {
							new LightFace.MessageBox({
								onClose : function() {
									appM.grid.load();
								}
							}).info("操作成功");
						} else {
							new LightFace.MessageBox().error(result.message);
						}
					}
				}).post({
					'appVerId' : appVerId
				});
			},
			submitResult : function(appverId, grid2) {
				var appM = this;
				var upForm = $('onlineTestSubDiv').get('html');
				var formWin = new LightFace({
					width : 600,
					height : 400,
					content : upForm,
					title : '提交测试结果',
					draggable : false,
					initDraw : true,
					onClose : function() {
						grid2.showFoot();
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
				var getSdA = formWin.messageBox.getElement('[id="getCardBase"]');
				getSdA.addEvent('click', function() {
					var winGrid = new JIM.UI.WinGrid({
						url : ctx + '/html/cardbaseinfo/?m=index',
						multipleSelection : false,
						order : false,
						width : 850,
						height : 330,
						winButtons : [ {
							title : '选择',
							color : 'blue',
							event : function() {
								if (this.selectIds != '') {
									var cbiId = this.selectIds[0];
									new Request.JSON({
										url : ctx + '/html/cardbaseinfo/?m=getCardBase&cbiId=' + cbiId,
										onSuccess : function(data) {
											if (data.success) {
												formWin.messageBox.getElement('[id="selectedCardBase"]').set('value', data.message.name);
												formWin.messageBox.getElement('[id="cardBaseInfo"]').set('value', cbiId);
												winGrid.close();
											} else {
												new LightFace.MessageBox().error(data.message);
											}
										},
										onError : function(data) {
										}
									}).post();
								} else {
									new LightFace.MessageBox().error("您还未选择卡批次");
								}
								;
							}
						}, {
							title : '取消',
							event : function() {
								this.close();
							}
						} ],
						drawButtons : false,
						drawSearch : false,
						columnModel : [ {
							dataName : 'id',
							identity : true
						}, {
							title : '批次编号',
							dataName : 'batchNo'
						}, {
							title : '批次名称',
							dataName : 'name'
						}, {
							title : 'SE卡商',
							dataName : 'osImplementor'
						}, {
							title : 'SE类型',
							dataName : 'type'
						}, {
							title : '芯片类型',
							dataName : 'coreType'
						}, {
							title : 'JAVA版本',
							dataName : 'javaVersion'
						}, {
							title : 'cms2ac版本',
							dataName : 'cms2acVersion'
						}, {
							title : '初始内存空间(Byte)',
							dataName : 'totalRamSize'
						}, {
							title : '初始存储空间(Byte)',
							dataName : 'totalRomSize'
						} ],
						searchButton : false,
						searchBar : {
							filters : []
						},
						headerText : '选择批次 '
					});
				});
				var today = new Date();
				var month = today.getMonth() + 1;
				if (month < 10) {
					month = '0' + month;
				}
				var formMonth = formWin.messageBox.getElement('[id="month"]');
				var formYear = formWin.messageBox.getElement('[id="year"]');
				var formDay = formWin.messageBox.getElement('[id="day"]');
				formMonth.addEvent('change', function(event) {
					appM.adjustDay(formMonth.get('value'), formYear, formDay);
				});
				formYear.addEvent('change', function(event) {
					appM.adjustDay(formMonth.get('value'), formYear, formDay);
				});
				formWin.messageBox.getElement('[id="year"]').set('value', today.getFullYear());
				formWin.messageBox.getElement('[id="month"]').set('value', month);
				formWin.messageBox.getElement('[id="month"]').fireEvent('change');
				formWin.messageBox.getElement('[id="day"]').set('value', today.getDate());

				formWin.messageBox.getElement('[id="formAppverId"]').set('value', appverId);
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
						var cbiId = formWin.messageBox.getElement('[id="cardBaseInfo"]').get('value');
						if (!$chk(cbiId)) {
							new LightFace.MessageBox().error("请选择一个卡批次");
							return false;
						}
						new Request.JSON({
							url : form.get('action'),
							onSuccess : function(result) {
								if (result.success) {
									new LightFace.MessageBox({
										onClose : function() {
											grid2.load();
										}
									}).info("操作成功");
									formWin.close();
								} else {
									new LightFace.MessageBox().error(result.message);
								}
							},
							onError : function(result) {
							}
						}).post(form.toQueryString());
					}
				});
				grid2.hideFoot();
				formWin.open();
			},
			setEventForOnlineTest : function(box, aid, ver) {
				var appm = this;
				box.messageBox.getElement('[id="dwonappBtn"]').addEvent('click', function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (!$chk(nowCardNo)) {
						new LightFace.MessageBox().error("在线测试需要插入测试卡片");
						return;
					}
					return;
					new Request.JSON({
						url : ctx + '/html/cardinfo/?m=getCardInfoByCardNo&cardNo=' + nowCardNo,
						onSuccess : function(json) {
							if (json.success) {
								if (json.message.cardType == 1) {
									new JIM.CardDriver({
										ctl : cardDriver,
										operations : [ {
											aid : aid,
											appVersion : ver,
											operation : appm.transConstant.DOWNLOAD_APP
										} ],
										onSuccess : function(response) {
											this.closeConnection();
											new LightFace.MessageBox({
												onClose : function() {
												}
											}).info("操作成功");
										}
									}).exec();
								} else {
									new LightFace.MessageBox().error("在线测试必须使用测试卡");
								}
							} else {
								new LightFace.MessageBox().error(json.message);
							}
						}
					}).post();
				});
				box.messageBox.getElement('[id="delappBtn"]').addEvent('click', function() {

					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (!$chk(nowCardNo)) {
						new LightFace.MessageBox().error("在线测试需要插入测试卡片");
						return;
					}

					new Request.JSON({
						url : ctx + '/html/cardinfo/?m=getCardInfoByCardNo&cardNo=' + nowCardNo,
						onSuccess : function(json) {
							if (json.success) {
								if (json.message.cardType == 1) {

									new JIM.CardDriver({
										ctl : cardDriver,
										operations : [ {
											aid : aid,
											appVersion : ver,
											operation : appm.transConstant.DELETE_APP
										} ],
										onSuccess : function(response) {
											this.closeConnection();
											new LightFace.MessageBox({
												onClose : function() {
												}
											}).info("操作成功");
										}
									}).exec();
								} else {
									new LightFace.MessageBox().error("在线测试必须使用测试卡");
								}
							} else {
								new LightFace.MessageBox().error(json.message);
							}
						}
					}).post();

				});
				box.messageBox.getElement('[id="lockappBtn"]').addEvent('click', function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (!$chk(nowCardNo)) {
						new LightFace.MessageBox().error("在线测试需要插入测试卡片");
						return;
					}
					new Request.JSON({
						url : ctx + '/html/cardinfo/?m=getCardInfoByCardNo&cardNo=' + nowCardNo,
						onSuccess : function(json) {
							if (json.success) {
								if (json.message.cardType == 1) {
									new JIM.CardDriver({
										ctl : cardDriver,
										operations : [ {
											aid : aid,
											appVersion : ver,
											operation : appm.transConstant.LOCK_APP
										} ],
										onSuccess : function(response) {
											this.closeConnection();
											new LightFace.MessageBox({
												onClose : function() {
												}
											}).info("操作成功");
										}
									}).exec();
								} else {
									new LightFace.MessageBox().error("在线测试必须使用测试卡");
								}
							} else {
								new LightFace.MessageBox().error(json.message);
							}
						}
					}).post();
				});
				box.messageBox.getElement('[id="unlockappBtn"]').addEvent('click', function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (!$chk(nowCardNo)) {
						new LightFace.MessageBox().error("在线测试需要插入测试卡片");
						return;
					}
					new Request.JSON({
						url : ctx + '/html/cardinfo/?m=getCardInfoByCardNo&cardNo=' + nowCardNo,
						onSuccess : function(json) {
							if (json.success) {
								if (json.message.cardType == 1) {
									new JIM.CardDriver({
										ctl : cardDriver,
										operations : [ {
											aid : aid,
											appVersion : ver,
											operation : appm.transConstant.UNLOCK_APP
										} ],
										onSuccess : function(response) {
											this.closeConnection();
											new LightFace.MessageBox({
												onClose : function() {
												}
											}).info("操作成功");
										}
									}).exec();
								} else {
									new LightFace.MessageBox().error("在线测试必须使用测试卡");
								}
							} else {
								new LightFace.MessageBox().error(json.message);
							}
						}
					}).post();
				});
				box.messageBox.getElement('[id="dwonappBtn"]').addEvent('click', function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (!$chk(nowCardNo)) {
						new LightFace.MessageBox().error("在线测试需要插入测试卡片");
						return;
					}
					new Request.JSON({
						url : ctx + '/html/cardinfo/?m=getCardInfoByCardNo&cardNo=' + nowCardNo,
						onSuccess : function(json) {
							if (json.success) {
								if (json.message.cardType == 1) {
									new JIM.CardDriver({
										ctl : cardDriver,
										operations : [ {
											aid : aid,
											appVersion : ver,
											operation : appm.transConstant.DOWNLOAD_APP
										} ],
										onSuccess : function(response) {
											this.closeConnection();
											new LightFace.MessageBox({
												onClose : function() {
												}
											}).info("操作成功");
										}
									}).exec();
								} else {
									new LightFace.MessageBox().error("在线测试必须使用测试卡");
								}
							} else {
								new LightFace.MessageBox().error(json.message);
							}
						}
					}).post();
				});
				box.open();
			},
			adjustDay : function(month, formYear, formDay) {
				var day = formDay.get('value');
				formDay.empty();
				for ( var i = 0; i < 28; i++) {
					formDay.options.add(new Option(i + 1, i + 1));
				}
				switch (month) {
				case "1":
				case "3":
				case "5":
				case "7":
				case "8":
				case "10":
				case "12": {
					formDay.options.add(new Option(29, 29));
					formDay.options.add(new Option(30, 30));
					formDay.options.add(new Option(31, 31));
				}
					break;
				case "2": {
					var nYear = formYear.get('value');
					if (nYear % 400 == 0 || nYear % 4 == 0 && nYear % 100 != 0)
						formDay.options.add(new Option(29, 29));
				}
					break;
				default: {
					formDay.options.add(new Option(29, 29));
					formDay.options.add(new Option(30, 30));
				}
				}
				if (day == 31) {
					switch (month) {
					case "1":
					case "3":
					case "5":
					case "7":
					case "8":
					case "10":
					case "12": {
						formDay.set('value', day);
					}
						break;
					case "2": {
						var nYear = formYear.get('value');
						if (nYear % 400 == 0 || nYear % 4 == 0 && nYear % 100 != 0) {
							formDay.set('value', 29);
						} else {
							formDay.set('value', 28);
						}
					}
						break;
					default: {
						formDay.set('value', 30);
					}
					}
				} else if (day == 29 || day == 30) {
					switch (month) {
					case "2": {
						var nYear = formYear.get('value');
						if (nYear % 400 == 0 || nYear % 4 == 0 && nYear % 100 != 0) {
							formDay.set('value', 29);
						} else {
							formDay.set('value', 28);
						}
					}
						break;
					default: {
						formDay.set('value', day);
					}
					}
				} else {
					formDay.set('value', day);
				}
			},
			submit : function(ids, grid) {
				var idsS = ids.join(",");
				new Request.JSON({
					url : ctx + '/html/appVer/?m=testAppVersion',
					onSuccess : function(data) {
						if (data.success) {
							new LightFace.MessageBox().info(data.message);
							grid.load();
						} else {
							new LightFace.MessageBox().error(data.message);
						}
					}
				}).post({
					'appVerIds' : idsS
				});
			}
		});

// 应用发布
var publishAppVerId = '';
var cardBaseInfoId = '';
var box3;
AppManager.Publish = new Class({
	Implements : [ Events, Options ],
	options : {

	},
	initialize : function(options) {
		var ids;
		var grid;
		this.drawUserBox3();
		this.userBox = new LightFace({
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '完成发布',
				event : function() {
					this.userBox.messageBox.getElement('form').set('action', ctx + '/html/appVer/?m=publishAppVersion&appVerIds=' + ids);
					this.appForm.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '取消发布',
				event : function() {
					this.close();
				}
			} ]
		});
		this.userBox2 = new LightFace({
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '需要',
				event : function() {
					this.userBox.messageBox.getElement('form').set('action', ctx + '/html/appVer/?m=publishAppVersion&appVerIds=' + ids);
					this.appForm.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '不需要',
				event : function() {
					this.close();
				}
			} ]
		});
		var appM = this;
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/appVer/?m=index&search_EQI_status=2&local=true',
			multipleSelection : false,
			buttons : [ {
				name : '应用发布',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					ids = this.grid.selectIds;
					publishAppVerId = ids;
					grid = this.grid;
					if (!$chk(ids) || ids.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.createCardBaseGridForPublish(ids[0]);
				}.bind(this)
			}, {
				name : '修改应用信息',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.userBox3.options.title = '修改应用信息';
					this.userBox3.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.userBox3.options.content = $('editAppInfoDiv').get('html');
					this.userBox3.addEvent('open', this.editAppInfo.bind(this));
					this.userBox3.open();
					this.userBox3.removeEvents('open');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '所属应用名称',
				dataName : 'application_name',
				order : false
			}, {
				title : '版本号',
				dataName : 'versionNo'
			}, {
				title : '状态',
				dataName : 'status'
			}, {
				title : '占用内存空间(Byte)',
				dataName : 'volatileSpace'
			}, {
				title : '占用存储空间(Byte)',
				dataName : 'nonVolatileSpace'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '应用名称：',
					name : 'search_ALIAS_applicationL_LIKES_name',
					type : 'text',
					width : 150
				} ]
			},
			headerText : '应用发布 '
		});
	},
	editAppInfo : function() {
		var selectIds = this.grid.selectIds;
		this.appForm = this.userBox3.messageBox.getElement('form');
		box3 = this.userBox3.messageBox;
		// this.userBox.messageBox.getElement('form').set('action', ctx
		// + '/html/application/?m=modifyDeleteRuleAndPersonalType');
		new Request.JSON({
			url : ctx + '/html/appVer/?m=index&search_EQL_id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var deleteRule;
					if (data.result[0]['application_deleteRule'] == '不能删除') {
						deleteRule = 0;
					} else if (data.result[0]['application_deleteRule'] == '删除整个应用程序') {
						deleteRule = 1;
					} else if (data.result[0]['application_deleteRule'] == '只删除个人化数据') {
						deleteRule = 2;
					}
					var personalType;
					if (data.result[0]['application_personalType'] == '指令透传') {
						personalType = 1;
					} else if (data.result[0]['application_personalType'] == '应用访问安全域') {
						personalType = 2;
					} else if (data.result[0]['application_personalType'] == '安全域访问应用') {
						personalType = 3;
					} else if (data.result[0]['application_personalType'] == '不需要个人化') {
						personalType = 0;
					}
					box3.getElement('[id=deleteRule]').set('value', deleteRule);
					box3.getElement('[id=appName]').set('value', data.result[0]['application_name']);
					box3.getElement('[id=appId]').set('value', data.result[0]['application_id']);
					box3.getElement('[id=personalType]').set('value', personalType);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	drawUserBox3 : function() {
		this.userBox3 = new LightFace({
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '保 存',
				event : function() {
					new Request.JSON({
						url : ctx + '/html/application/?m=modifyDeleteRuleAndPersonalType',
						data : {
							deleteRule : box3.getElement('[id=deleteRule]').get('value'),
							applicationId : box3.getElement('[id=appId]').get('value'),
							personalType : box3.getElement('[id=personalType]').get('value')
						},
						onSuccess : function(data) {
							if (data.success) {
								new LightFace.MessageBox({
									onClose : function() {
										location.reload();
									}.bind(this)
								}).info("修改成功");
							} else {
								new LightFace.MessageBox().error(data.message);
							}
						}.bind(this)
					}).get();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	addValidate : function() {
		formCheck = new FormCheck(this.appForm, {
			submit : false,
			zIndex : this.userBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {// 校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	submitForm : function() {
		new Request.JSON({
			url : this.appForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox({
						onClose : function() {
							this.userBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.appForm.toQueryString());
	},
	openEditApp : function() {
		thirdSd = "";
		this.appForm = this.userBox.messageBox.getElement('form');
		var box = this.userBox.messageBox;
		box.getElement('input[id=cardBaseInfoId]').set('value', cardBaseInfoId);
		this.addValidate();
	},
	createCardBaseGridForPublish : function(appverId) {
		var appM = this;
		// this.appForm = this.userBox.messageBox.getElement('form');
		var grid2 = new JIM.UI.WinGrid({
			url : ctx + '/html/cardbaseinfo/?m=findTestedCardBase&appVerId=' + appverId,
			multipleSelection : true,
			height : 330,
			width : 800,
			winButtons : [ {
				title : '选择',
				color : 'blue',
				event : function() {
					var selectIds = grid2.selectIds;
					var idS = selectIds.join(",");
					// alert(idS);
					if (idS != '') {
						cardBaseInfoId = idS;
						new LightFace.MessageBox({
							onClose : function() {
								if (this.result == 'need') {
									appM.userBox.options.title = '限定手机号设置';
									appM.userBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
									appM.userBox.options.content = $('publishDiv').get('html');
									appM.userBox.addEvent('open', appM.openEditApp.bind(appM));
									appM.userBox.open();
									appM.userBox.removeEvents('open');
									grid2.close();
								} else if (this.result == 'noNeed'){
									appM.submit(publishAppVerId, cardBaseInfoId);
								}
							}
						}).confirm("您需要进行限定使用手机号设置吗？");
					} else {
						new LightFace.MessageBox().error('请先选择列表中的卡批次');
					}
				}
			}, {
				title : '取消',
				event : function() {
					this.close();
				}
			} ],
			drawButtons : false,
			drawSearch : false,
			order : false,
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '批次编号',
				dataName : 'batchNo'
			}, {
				title : '批次名称',
				dataName : 'name'
			}, {
				title : 'SE卡商',
				dataName : 'osImplementor'
			}, {
				title : 'SE类型',
				dataName : 'type'
			}, {
				title : '芯片类型',
				dataName : 'coreType'
			}, {
				title : 'JAVA版本',
				dataName : 'javaVersion'
			}, {
				title : 'cms2ac版本',
				dataName : 'cms2acVersion'
			}, {
				title : '初始内存空间(Byte)',
				dataName : 'totalRamSize'
			}, {
				title : '初始存储空间(Byte)',
				dataName : 'totalRomSize'
			}, {
				title : "预置模式",
				select : true,
				item : [ {
					key : '空卡模式',
					value : 1
				}, {
					key : '实例创建模式',
					value : 2
				}, {
					key : '个人化模式',
					value : 3
				} ]
			} ],
			searchButton : false,
			searchBar : {
				filters : []
			},
			headerText : '关联卡批次'
		});
	},
	submit : function(publishAppVerId, cardBaseInfoId) {
		new Request.JSON({
			url : ctx + '/html/appVer/?m=publishAppVersion&appVerIds=' + publishAppVerId,
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox({
						onClose : function() {
							window.location.reload();
						}.bind(this)
					}).info(data.message);
					// winGrid.load();
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post({
			'cardBaseInfoId' : cardBaseInfoId
		});
	}
});

// 应用归档
AppManager.Archive = new Class({
	Implements : [ Events, Options ],
	options : {

	},
	initialize : function(options) {
		var appM = this;
		new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/appVer/?m=index&search_EQI_status=3&local=true',
			multipleSelection : false,
			buttons : [ {
				name : '应用归档',
				icon : ctx + '/admin/images/archive.png',
				handler : function() {
					if (this.selectIds != '') {
						appM.submit(this.selectIds, this);
					} else {
						new LightFace.MessageBox().error("请先选择列表中的应用版本");
					}
				}
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '所属应用名称',
				dataName : 'application_name',
				order : false
			}, {
				title : '版本号',
				dataName : 'versionNo'
			}, {
				title : '状态',
				dataName : 'status'
			}, {
				title : '占用内存空间(Byte)',
				dataName : 'volatileSpace'
			}, {
				title : '占用存储空间(Byte)',
				dataName : 'nonVolatileSpace'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '应用名称：',
					name : 'search_ALIAS_applicationL_LIKES_name',
					type : 'text',
					width : 150
				} ]
			},
			headerText : '应用归档'
		});
	},
	submit : function(id, grid) {
		var idS = id.join(",");
		new LightFace.MessageBox({
			onClose : function() {
				if (this.result) {
					var reason = this.resultMessage;
					new Request.JSON({
						url : ctx + '/html/appVer/?m=archiveApp',
						onSuccess : function(data) {
							if (data.success) {
								grid.load();
								new LightFace.MessageBox().info(data.message);
							} else {
								new LightFace.MessageBox().error(data.message);
							}
						}
					}).post({
						'appVerIds' : idS,
						'reason' : reason
					});
				}
			}
		}).confirm("您确认要归档该版本吗？");
	}
});

// 应用定义
AppManager.Define = new Class(
		{
			Implements : [ Events, Options ],
			selectVers : '',
			selectCards : '',
			options : {

			},
			initialize : function(options) {
				var appM = this;
				new JIM.UI.Grid('tableDiv', {
					url : ctx + '/html/appVer/?m=index&search_EQI_status=6',
					multipleSelection : false,
					buttons : [ {
						name : '应用定义',
						icon : ctx + '/admin/images/define.png',
						handler : function() {
							if (this.selectIds != '') {
								appM.createCardBaseGrid(this.selectIds, this);
							} else {
								new LightFace.MessageBox().error("请先选择列表中的应用版本");
							}
						}
					}, {
						name : '修改应用信息',
						icon : ctx + '/admin/images/define.png',
						handler : function() {
							if (this.selectIds != '') {
								appM.changeType(this.selectIds);
							} else {
								new LightFace.MessageBox().error("请先选择列表中的应用版本");
							}

						}
					} ],
					columnModel : [ {
						dataName : 'id',
						identity : true
					}, {
						title : '所属应用名称',
						dataName : 'application_name',
						order : false
					}, {
						title : '版本号',
						dataName : 'versionNo'
					}, {
						title : '状态',
						dataName : 'status'
					}, {
						title : '占用内存空间(Byte)',
						dataName : 'volatileSpace'
					}, {
						title : '占用存储空间(Byte)',
						dataName : 'nonVolatileSpace'
					} ],
					searchButton : true,
					searchBar : {
						filters : [ {
							title : '应用名称：',
							name : 'search_ALIAS_applicationL_LIKES_name',
							type : 'text',
							width : 150
						} ]
					},
					headerText : '应用定义'
				});
			},
			changeType : function(id) {
				new Request.JSON(
						{
							url : ctx + '/html/appVer/?m=getAppIdByAppverId',
							onSuccess : function(data) {
								if (data.success) {
									var html = "";
									html += '<div class="regcont">';
									html += '<form method="post" action="' + ctx + '/html/application/change">';
									html += '<input name="appId" value="' + data.message.id + '" type="hidden"/>';
									html += '<dl>';
									html += '<dd><p class="regtext">删除规则: </p><p class="left inputs"><select id="deleteRule" name="deleteRule"><option value="0">不能删除</option><option value="1">删除整个应用程序</option><option value="2">只删除个人化数据</option></select></p></dd>';
									html += '<dd><p class="regtext">个人化类型: </p><p class="left inputs"><select id="personalType" name="personalType"><option value="1">指令透传</option><option value="2">应用访问安全域</option><option value="3">安全域访问应用</option></select></p></dd>';
									html += '</dl>';
									html += '</form>';
									html += '</div>';

									var box = new LightFace({
										title : '修改应用信息',
										content : html,
										initDraw : false,
										buttons : [ {
											title : '修改',
											event : function() {
												var form = box.messageBox.getElement('form');
												var data = form.toQueryString();
												new Request.JSON({
													url : ctx + '/html/application/?m=defChange',
													onSuccess : function(data) {
														if (data.success) {
															box.close();
															new LightFace.MessageBox().info(data.message);
														} else {
															box.close();
															new LightFace.MessageBox().error(data.message);
														}
													}
												}).post(data);
											}.bind(this),
											color : 'blue'
										}, {
											title : '关 闭',
											event : function() {
												this.close();
											}
										} ]
									});
									box.open();
									var options = box.messageBox.getElement('[id=deleteRule]').getElements('option');
									options[data.message.deleteRuleOriginal].set('selected', true);
									var options2 = box.messageBox.getElement('[id=personalType]').getElements('option');
									options2[data.message.personalTypeOriginal - 1].set('selected', true);
								} else {
									new LightFace.MessageBox().error(data.message);
								}
							}
						}).post({
					'appVerid' : id
				});
			},
			finishDef : function(ids, grid) {
				var idsS = ids.join(",");
				new Request.JSON({
					url : ctx + '/html/appVer/?m=finishAppVersion',
					onSuccess : function(data) {
						if (data.success) {
							new LightFace.MessageBox().info(data.message);
							grid.load();
						} else {
							new LightFace.MessageBox().error(data.message);
						}
					}
				}).post({
					'appVerIds' : idsS
				});
			},
			createCardBaseGrid : function(id, avGrid) {
				var appM = this;
				new JIM.UI.WinGrid({
					url : ctx + '/html/cardbaseinfo/?m=index',
					multipleSelection : true,
					height : 330,
					width : 800,
					winButtons : [ {
						title : '完成定义	',
						color : 'blue',
						event : function() {
							if (this.selectIds != '') {
								appM.submit(id, this.selectIds, this, avGrid);
							} else {
								new LightFace.MessageBox().error('请先选择列表中的卡批次');
							}
						}
					}, {
						title : '取消',
						event : function() {
							this.close();
						}
					} ],
					drawButtons : false,
					drawSearch : false,
					columnModel : [ {
						dataName : 'id',
						identity : true
					}, {
						title : '批次编号',
						dataName : 'batchNo'
					}, {
						title : '批次名称',
						dataName : 'name'
					}, {
						title : '系统实现厂商',
						dataName : 'osImplementor'
					}, {
						title : '平台类型',
						dataName : 'platformType'
					}, {
						title : '平台版本',
						dataName : 'platformVersion'
					}, {
						title : '系统类型',
						dataName : 'osPlatform'
					}, {
						title : '系统版本',
						dataName : 'osVersion'
					}, {
						title : "是否预置",
						checkBox : true
					} ],
					searchButton : false,
					searchBar : {
						filters : []
					},
					headerText : '关联卡批次'
				});
			},
			submit : function(vaerIds, cardIds, grid, avGrid) {
				var verS = vaerIds.join(",");
				var cardS = cardIds.join(",");
				new Request.JSON({
					url : ctx + '/html/cardbaseapp/?m=doLink',
					onSuccess : function(data) {
						if (data.success) {
							grid.close();
							avGrid.load();
							new LightFace.MessageBox().info(data.message);
						} else {
							new LightFace.MessageBox().error(data.message);
						}
					}
				}).post({
					'appVerIds' : verS,
					'cardBaseIds' : cardS
				});
			}
		});

// 应用订购关系查询
AppManager.ListSubscribe = new Class({
	Implements : [ Events, Options ],
	options : {},
	initialize : function(options) {
		this.drawBox();
		this.drawGrid();
	},
	drawBox : function() {
		this.infoBox = new LightFace({
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/appVer/?m=index&search_EQI_status=3',
			multipleSelection : false,
			buttons : [ {
				name : '查询订购关系',
				icon : ctx + '/admin/images/archive.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的应用版本');
						return;
					}

					this.infoBox.options.title = '查看应用订购信息';
					this.infoBox.options.titleImage = ctx + '/admin/images/test.png';
					this.infoBox.options.content = document.getElement('[name="subscribeDiv"]').get('html');
					this.infoBox.addEvent('open', this.openSubscribeTable.bind(this));
					this.infoBox.open();
					this.infoBox.removeEvents('open');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用名称',
				dataName : 'application_name',
				order : false
			}, {
				title : '版本号',
				dataName : 'versionNo'
			}, {
				title : '状态',
				dataName : 'status'
			}, {
				title : '占用内存空间(Byte)',
				dataName : 'volatileSpace'
			}, {
				title : '占用存储空间(Byte)',
				dataName : 'nonVolatileSpace'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '应用名称：',
					name : 'search_ALIAS_applicationL_LIKES_name',
					type : 'text',
					width : 150
				} ]
			},
			headerText : '订购关系查询 '
		});
	},
	openSubscribeTable : function() {
		var selectIds = this.grid.selectIds;
		var appVerId = selectIds[0];
		var url = ctx + '/html/appVer/?m=listSubscribe&id=' + appVerId;
		this.infoBox.messageBox.setStyle('overflow-y', 'hidden');
		var tableId = this.infoBox.messageBox.getElement('div[name=subscribeTable]');
		new JIM.UI.Grid(tableId, {
			url : url,
			selection : false,
			multipleSelection : false,
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '手机号码',
				dataName : 'mobileNo',
				align : 'center',
				order : false
			}, {
				title : '用户姓名',
				dataName : 'customer_nickName',
				align : 'center',
				order : false
			}, {
				title : 'SEID',
				dataName : 'card_cardNo',
				align : 'center',
				order : false
			} ],
			header : false,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	}
});

// 应用订购历史查询
AppManager.ListSubscribeHistory = new Class({
	Implements : [ Events, Options ],
	options : {},
	headerText : '交易历史查询',

	initialize : function(options) {
		// this.drawBox();
		this.drawGrid();
	},
	drawBox : function() {
		this.infoBox = new LightFace({
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/subscribehistory/?m=list',
			multipleSelection : false,
			// buttons : [],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : 'SEID',
				dataName : 'customerCardInfo_card_cardNo'
			}, {
				title : '用户名称',
				dataName : 'customerCardInfo_customer_nickName'
			}, {
				title : '手机号码',
				dataName : 'customerCardInfo_mobileNo'
			}, {
				title : '应用名称',
				dataName : 'applicationVersion_application_name'
			}, {
				title : '应用版本',
				dataName : 'applicationVersion_versionNo'
			}, {
				title : '订购时间',
				dataName : 'subscribeDate'
			}, {
				title : '退订时间',
				dataName : 'unsubscribeDate'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '用户名称：',
					name : 'customerCardInfo_customer_nickName',
					type : 'text',
					width : 100
				}, {
					title : '手机号码：',
					name : 'customerCardInfo_mobileNo',
					type : 'text',
					width : 100
				}, {
					title : '应用名称：',
					name : 'applicationVersion_application_name',
					type : 'text',
					width : 100
				} ]
			},
			headerText : this.headerText
		});
	}
});

// 下载应用测试文件
AppManager.testInfo = new Class({
	Implements : [ Events, Options ],
	options : {

	},
	initialize : function(options) {
		var appM = this;
		new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/appVer/?m=getDownTestFileAppver',
			multipleSelection : false,
			buttons : [ {
				name : '查看离线测试文件',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					if (this.selectIds != '') {
						var ids = this.selectIds;
						appM.getTestFileGrid(ids[0]);
					} else {
						new LightFace.MessageBox().error("请先选择列表中的应用版本");
					}
				}
			}, {
				name : '查看测试结果',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					if (this.selectIds != '') {
						var ids = this.selectIds;
						var grid2 = new JIM.UI.WinGrid({
							url : ctx + "/html/appverTest/?m=index&search_EQL_appVer.id=" + ids[0],
							multipleSelection : false,
							height : 330,
							width : 800,
							winButtons : [ {
								title : '退出',
								event : function() {
									this.close();
								}
							} ],
							drawButtons : false,
							drawSearch : false,
							columnModel : [ {
								dataName : 'id',
								identity : true
							}, {
								title : '测试日期',
								dataName : 'testDate'
							}, {
								title : '测试手机号',
								dataName : 'mobileNo'
							}/*
								 * ,{ title : '测试SEID', dataName : 'seId' }
								 */, {
								title : 'NFC终端型号',
								dataName : 'modelType'
							}, {
								title : 'SE芯片类型',
								dataName : 'seType'
							}, {
								title : '卡批次',
								dataName : 'cardBaseInfo_name'
							}, {
								title : '测试结果',
								dataName : 'result'
							}, {
								title : '说明',
								dataName : 'resultComment'
							}, {
								title : '提交者',
								dataName : 'author'
							} ],
							searchButton : false,
							searchBar : {
								filters : []
							},
							headerText : '测试结果'
						});
					} else {
						new LightFace.MessageBox().error("请先选择列表中的应用版本");
					}
				}
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '所属应用名称',
				dataName : 'application_name',
				order : false
			}, {
				title : '版本号',
				dataName : 'versionNo'
			}, {
				title : '状态',
				dataName : 'status'
			}, {
				title : '占用内存空间(Byte)',
				dataName : 'volatileSpace'
			}, {
				title : '占用存储空间(Byte)',
				dataName : 'nonVolatileSpace'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '应用名称：',
					name : 'appName',
					type : 'text',
					width : 150
				} ]
			},
			headerText : '测试应用信息'
		});
	},
	getTestFileGrid : function(avId) {
		var appM = this;
		var grid2 = new JIM.UI.WinGrid({
			url : ctx + "/html/testfile/?m=index&search_EQL_appVer.id=" + avId,
			multipleSelection : false,
			height : 330,
			width : 800,
			winButtons : [ {
				title : '下载',
				color : 'blue',
				event : function() {
					if (this.selectIds != '') {
						window.location.href = ctx + '/html/testfile/?m=downFile&tfId=' + this.selectIds[0];
					} else {
						new LightFace.MessageBox().error("请先选择列表中文件");
					}
				}
			}, {
				title : '退出',
				event : function() {
					this.close();
				}
			} ],
			drawButtons : false,
			drawSearch : false,
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '文件名',
				dataName : 'originalName'
			}, {
				title : '文件说明',
				dataName : 'comments'
			}, {
				title : '上传时间',
				dataName : 'uploadDate'
			} ],
			searchButton : false,
			searchBar : {
				filters : []
			},
			headerText : '测试文件列表'
		});
	},
	getTestView : function(appverId) {
		var appM = this;
		new Request.JSON({
			url : ctx + "/html/appverTest/?m=getReportByAppver&appVerId=" + appverId,
			onSuccess : function(json) {
				if (json.success) {
					var upForm = $('onlineTestSubDiv').get('html');
					var formWin = new LightFace({
						width : 600,
						content : upForm,
						title : '测试结果',
						draggable : false,
						initDraw : true,
						onClose : function() {
							var div = document.getElement('div[class=fc-tbx]');
							if ($chk(div)) {
								div.dispose();
							}
						},
						buttons : [ {
							title : '退出',
							event : function() {
								this.close();
							}
						} ]
					});
					var testFile = json.message;
					formWin.messageBox.getElement('[id="testDate"]').set('value', testFile.testDate);
					formWin.messageBox.getElement('[id="mobileNo"]').set('value', testFile.mobileNo);
					// formWin.messageBox.getElement('[id="seId"]').set('value',
					// testFile.seId);
					formWin.messageBox.getElement('[id="seType"]').set('value', testFile.seType);
					formWin.messageBox.getElement('[id="modelType"]').set('value', testFile.modelType);
					formWin.messageBox.getElement('[id="cms2acVer"]').set('value', testFile.cms2acVer);
					formWin.messageBox.getElement('[id="result"]').set('value', testFile.result);
					formWin.open();
				} else {
					new LightFace.MessageBox().info("无测试结果信息");
					return;
				}
			}
		}).get();
	}
});
