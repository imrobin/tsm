var App = App ? App : {};

App = new Class(
		{
			options : {},
			nowId : '',
			transConstant : '',
			aid : '',
			initialize : function() {
				this.drawAppBox();
				this.getConstants();
			},
			drawAppBox : function() {
				this.appBox = new LightFace({
					draggable : false,
					initDraw : false,
					width : 700
				});
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
			downApp : function(appId) {
				var template = $("appInfo").clone(true, true);
				var obj = this;
				obj.updateCaId = null;
				this.appBox.options.title = "下载应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '下载',
					event : function() {
						new JIM.CardDriver({
							ctl : cardDriver,
							operations : [ {
								aid : obj.aid,
								appVersion : obj.ver,
								operation : obj.transConstant.DOWNLOAD_APP
							} ],
							onSuccess : function(response) {
								this.closeConnection();
								obj.appBox.close();
								new LightFace.MessageBox().info("操作成功");
							}
						}).exec();
					},
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(appId, this.appBox, true);
			},
			personalApp : function(appId, grid) {
				var template = $("appInfo").clone(true, true);
				var obj = this;
				this.appBox.options.title = "个人化应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '个人化',
					event : function() {
						new JIM.CardDriver({
							ctl : cardDriver,
							operations : [ {
								aid : obj.aid,
								operation : obj.transConstant.PERSONALIZE_APP
							} ],
							onSuccess : function(response) {
								this.closeConnection();
								obj.appBox.close();
								grid.load();
								new LightFace.MessageBox().info("操作成功");
							}
						}).exec();
					},
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(appId, this.appBox);
			},
			delApp : function(appId, grid) {
				var cardApp = this;
				var template = $("appInfo").clone(true, true);
				this.appBox.options.title = "删除应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '删除',
					event : function() {
						new JIM.CardDriver({
							ctl : cardDriver,
							operations : [ {
								aid : cardApp.aid,
								operation : cardApp.transConstant.DELETE_APP
							} ],
							onSuccess : function(response) {
								this.closeConnection();
								cardApp.appBox.close();
								grid.load();
								new LightFace.MessageBox().info("操作成功");
							}
						}).exec();
					}.bind(this),
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(appId, this.appBox);
			},
			lockApp : function(appId, grid) {
				var cardApp = this;
				var template = $("appInfo").clone(true, true);
				this.appBox.options.title = "锁定应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '锁定',
					event : function() {
						new JIM.CardDriver({
							ctl : cardDriver,
							operations : [ {
								aid : cardApp.aid,
								operation : cardApp.transConstant.LOCK_APP
							} ],
							onSuccess : function(response) {
								this.closeConnection();
								cardApp.appBox.close();
								grid.load();
								new LightFace.MessageBox().info("操作成功");
							}
						}).exec();
					}.bind(this),
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(appId, this.appBox);
			},
			unlockApp : function(appId, grid) {
				var cardApp = this;
				var template = $("appInfo").clone(true, true);
				this.appBox.options.title = "解锁应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '解锁',
					event : function() {
						new JIM.CardDriver({
							ctl : cardDriver,
							operations : [ {
								aid : cardApp.aid,
								operation : cardApp.transConstant.UNLOCK_APP
							} ],
							onSuccess : function(response) {
								this.closeConnection();
								cardApp.appBox.close();
								grid.load();
								new LightFace.MessageBox().info("操作成功");
							}
						}).exec();
					}.bind(this),
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(appId, this.appBox);
			},
			updateApp : function(appId, grid) {
				this.updateCaId = appId;
				new Request.JSON({
					async : false,
					url : ctx + "/html/cardApp/?m=getCardApplicaiton",
					data : {
						caId : appId[0]
					},
					onSuccess : function(json) {
						if (json.success) {
							id = json.message.appId;
						} else {
							new LightFace.MessageBox().error(json.message);
						}
					}
				}).get();
				var template = $("appInfo").clone(true, true);
				var obj = this;
				this.appBox.options.title = "升级应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '升级',
					event : function() {
						new JIM.CardDriver({
							ctl : cardDriver,
							operations : [ {
								aid : obj.aid,
								appVersion : obj.ver,
								operation : obj.transConstant.UPDATE_APP
							} ],
							onSuccess : function(response) {
								obj.appBox.close();
								grid.load();
								this.closeConnection();
								new LightFace.MessageBox().info("操作成功");
							}
						}).exec();

					},
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(id, this.appBox, true);
			},
			getEmmgratedTerminal : function(appId, grid) {
				var cardApp = this;
				var template = $("appInfo").clone(true, true);
				this.appBox.options.title = "迁入应用";
				this.appBox.options.content = template.get('html');
				this.appBox.options.buttons = [ {
					title : '迁入',
					event : function() {
						var driver = new JIM.CardDriver({
							ctl : cardDriver,
							operations : [],
							onSuccess : function(response) {
								new LightFace.MessageBox().info("操作成功");
							}
						});
						new Request.JSON({
							url : ctx + "/html/customerCard/?m=getByApplicationAidAndCurrentCardNoThatEmigrated",
							data : {
								aid : cardApp.aid,
								cardNo : driver.readCardNo()
							},
							onSuccess : function(json) {
								this.getEmmgratedTerminalCallback(json, cardApp.aid, cardApp);
							}.bind(this)
						}).post();
					}.bind(this),
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ];
				this.appBox.draw();
				this.getAppInfo(appId, this.appBox, true);
			},
			getEmmgratedTerminalCallback : function(json, aid, obj) {
				if (json.success) {
					var target = new Element('div');
					if (0 == json.result.length) {
						target.set("html", "您没有迁出该应用的终端");
					} else {
						json.result.each(function(item) {
							var liString = '';
							var phoneName = item.name == '' ? mobileType_brandChs : item.name;
							liString += '<div class="user_m_l_2" id="li' + item.id + '">' + '<p class="user_m_img">'
									+ '<img onerror="javascript:this.src=\'' + ctx + '/images/defTerim.jpg'
									+ '\' " src="/tsm/html/mobile/?m=getMobilePic&id=' + item.mobileType_id + '" />'
									+ '</p>' + '<p class="user_m_text">' + '名称 : ' + this.maxText(phoneName, 14)
									+ '<br />' + '号码 : ' + item.mobileNo + '<br />' + '状态 : ';
							if (item.statusOriginal == 4) {
								liString += '<font color="red">' + item.status + '</font>';
							} else {
								liString += item.status;
							}
							liString += '(' + item.active + ')' + '<br/>' + '品牌: ' + item.mobileType_brandChs
									+ '<br /> ' + '机型 : ' + this.maxText(item.mobileType_type, 14) + '<br /> '
									+ '<a class="buts m_t_5" title="选择' + item.id + '" href="#">选择</a>' + '</p>'
									+ '</div>';

							target.set("html", target.get("html") + liString);
						}.bind(this));
					}

					var html = target.get("html");
					var modal = new LightFace({
						title : "迁入",
						content : html,
						height : 350,
						width : 770,
						resetOnScroll : false,
						buttons : [ {
							title : "取消",
							event : function() {
								this.close();
							}
						} ]
					}).open();

					var box = modal.getBox();
					json.result.each(function(item) {
						var a = box.getElement('[title="选择' + item.id + '"]');
						a.erase("title").addEvent('click', function(event) {
							this.immgrateApp(arguments[0], arguments[1], modal, obj);
						}.bind(this, [ aid, item.cardNo ]));
					}.bind(this));
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},
			immgrateApp : function(aid, orignalCardNo, modal, obj) {
				var driver = new JIM.CardDriver({
					ctl : cardDriver,
					operations : [ {
						aid : aid,
						operation : this.transConstant.IMMIGRATE_APP,
						originalCardNo : orignalCardNo,
						appVersion : obj.ver
					} ],
					onSuccess : function(result) {
						new LightFace.MessageBox().info("操作成功");
						this.closeConnection();
						obj.appBox.close();
					}
				});

				modal.close();

				driver.exec();
			},
			maxText : function(str, len) {
				var newLength = 0;
				var newStr = "";
				var hasDot = true;
				var chineseRegex = /[^\x00-\xff]/g;
				var singleChar = "";
				var strLength = str.replace(chineseRegex, "**").length;
				for ( var i = 0; i < strLength; i++) {
					singleChar = str.charAt(i).toString();
					if (singleChar.match(chineseRegex) != null) {
						newLength += 2;
					} else {
						newLength++;
					}
					if (newLength > len) {
						break;
					}
					newStr += singleChar;
				}

				if (hasDot && strLength > len) {
					newStr += "...";
				}
				return newStr;
			},
			emigrateApp : function(appId, grid) {
				var cardApp = this;
				cardApp.updateCaId = null;
				var template = $("appInfo").clone(true, true);
				this.appBox.options.title = "迁出应用";
				this.appBox.options.content = template.get('html');
				new Request.JSON({
					url : ctx + '/html/cardApp/?m=getCardApplicaiton&caId=' + appId,
					onSuccess : function(caData) {
						var cardNo = caData.message.cardInfo_cardNo;
						cardApp.appBox.options.buttons = [ {
							title : '迁出',
							event : function() {
								new Request.JSON({
									url : ctx + "/html/cardApp/?m=emigrateByCardNo",
									data : {
										aid : cardApp.aid,
										cardNo : cardNo
									},
									onSuccess : function(json) {
										if (json.success) {
											var nowCardNo = new JIM.CardDriver({
												ctl : cardDriver,
												operations : []
											}).readCardNo();
											if (nowCardNo != cardNo) {
												new LightFace.MessageBox().error("操作终端与所选终端不符");
												return;
											}
											new JIM.CardDriver({
												ctl : cardDriver,
												operations : [],
												onSuccess : function(response) {
													this.closeConnection();
													cardApp.appBox.close();
													grid.load();
													new LightFace.MessageBox().info("操作成功");
												}
											}).exec();
										} else {
											new LightFace.MessageBox().error(json.message);
										}
									}
								}).post();
							}.bind(this),
							color : 'blue'
						}, {
							title : '关 闭',
							event : function() {
								this.close();
							}
						} ];
						cardApp.appBox.draw();
						cardApp.getAppInfo(appId, cardApp.appBox, false, true);
					}
				}).get();
			},
			getAppInfo : function(caId, box, isSelect, nocheckNo) {
				var cardApp = this;
				if ($chk(nocheckNo)) {
					new Request.JSON({
						url : ctx + '/html/cardApp/?m=getCardApplicaiton&caId=' + caId,
						onSuccess : function(caData) {
							var appId = caData.message.appId;
							cardApp.aid = caData.message.appAid;
							var cardNo = caData.message.cardInfo_cardNo;
							new Request.JSON({
								url : ctx + '/html/customerCard/?m=getCustomerInfoByCardNo&cardNo=' + cardNo,
								async : false,
								onSuccess : function(data) {
									if (data.success) {
										new Request.JSON({
											url : ctx + '/html/application/?m=getAppById&appId=' + appId,
											async : false,
											onSuccess : function(appdata) {
												if (appdata.success) {
													var e = appdata.message;
													var u = data.message;
													box.open();
													var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId="
															+ e.id;
													box.messageBox.getElement('[id="appImg"]').set(
															'html',
															'<img onerror="javascript:this.src=\'' + ctx
																	+ '/images/defApp.jpg'
																	+ '\' "  width="125" height="125" src="' + iconSrc
																	+ '" />');
													box.messageBox.getElement('[id="ename"]').set('html', e.name);
													if ($chk(isSelect)) {
														cardApp.ver = '0';
														var selectHtml = '<select id="verSel">';
														selectHtml += '<option value="">自动匹配</option>';
														cardApp.ver = "";
														$each(e.vers, function(item, index) {
															selectHtml += "<option value='" + item + "'>" + item
																	+ "</option>";
														});
														selectHtml += "</select>";
														box.messageBox.getElement('[id="evesion"]').set('html',
																selectHtml);
														box.messageBox.getElement('[id="verSel"]').addEvent('change',
																function() {
																	cardApp.ver = this.value;
																});
													} else {
														box.messageBox.getElement('[id="hideSelect"]').setStyle(
																'display', 'none');
													}
													if ($chk(box.messageBox.getElement('[id="elastver"]'))) {
														box.messageBox.getElement('[id="elastver"]').set('html',
																e.lastestVersion);
													}
													box.messageBox.getElement('[id="espname"]').set('html', e.sp_name);
													box.messageBox.getElement('[id="edesc"]')
															.set('html', e.description);
													box.messageBox.getElement('[id="username"]')
															.set('html', u.username);
													box.messageBox.getElement('[id="userter"]').set('html',
															u.mobileType);
													box.messageBox.getElement('[id="usermobileno"]').set('html',
															u.mobileNo);
												} else {
													new LightFace.MessageBox().error(data.message);
												}
											}
										}).get();
									}
								}
							}).get();
						}
					}).get();
				} else {
					var cardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if ($chk(cardNo)) {
						if (isSelect) {
							if (!$chk(cardApp.updateCaId)) {
								new Request.JSON(
										{
											url : ctx + "/html/customerCard/?m=checkMobileNoLocation&cardNo=" + cardNo
													+ "&appId=" + caId,
											onSuccess : function(data) {
												if (!data.success) {
													if (!data.message.force) {
														new LightFace.MessageBox(
																{
																	onClose : function() {
																		if (this.result) {
																			new Request.JSON(
																					{
																						url : ctx
																								+ '/html/customerCard/?m=getCustomerInfoByCardNo&cardNo='
																								+ cardNo,
																						async : false,
																						onSuccess : function(data) {
																							if (data.success) {
																								new Request.JSON(
																										{
																											url : ctx
																													+ '/html/application/?m=getAppById&appId='
																													+ caId,
																											async : false,
																											onSuccess : function(
																													appdata) {
																												if (appdata.success) {
																													var e = appdata.message;
																													var u = data.message;
																													cardApp.aid = e.aid;
																													box
																															.open();
																													var iconSrc = ctx
																															+ "/html/application/?m=getAppPcImg&appId="
																															+ e.id;
																													box.messageBox
																															.getElement(
																																	'[id="appImg"]')
																															.set(
																																	'html',
																																	'<img onerror="javascript:this.src=\''
																																			+ ctx
																																			+ '/images/defApp.jpg'
																																			+ '\' "  width="125" height="125" src="'
																																			+ iconSrc
																																			+ '" />');
																													box.messageBox
																															.getElement(
																																	'[id="ename"]')
																															.set(
																																	'html',
																																	e.name);
																													if ($chk(isSelect)) {
																														cardApp.ver = '0';
																														var selectHtml = '<select id="verSel">';
																														selectHtml += '<option value="">自动匹配</option>';
																														cardApp.ver = "";
																														$each(
																																e.vers,
																																function(
																																		item,
																																		index) {
																																	selectHtml += "<option value='"
																																			+ item
																																			+ "'>"
																																			+ item
																																			+ "</option>";
																																});
																														selectHtml += "</select>";
																														box.messageBox
																																.getElement(
																																		'[id="evesion"]')
																																.set(
																																		'html',
																																		selectHtml);
																														box.messageBox
																																.getElement(
																																		'[id="verSel"]')
																																.addEvent(
																																		'change',
																																		function() {
																																			cardApp.ver = this.value;
																																		});
																													} else {
																														box.messageBox
																																.getElement(
																																		'[id="hideSelect"]')
																																.setStyle(
																																		'display',
																																		'none');
																													}
																													if ($chk(box.messageBox
																															.getElement('[id="elastver"]'))) {
																														box.messageBox
																																.getElement(
																																		'[id="elastver"]')
																																.set(
																																		'html',
																																		e.lastestVersion);
																													}
																													box.messageBox
																															.getElement(
																																	'[id="espname"]')
																															.set(
																																	'html',
																																	e.sp_name);
																													box.messageBox
																															.getElement(
																																	'[id="edesc"]')
																															.set(
																																	'html',
																																	e.description);
																													box.messageBox
																															.getElement(
																																	'[id="username"]')
																															.set(
																																	'html',
																																	u.username);
																													box.messageBox
																															.getElement(
																																	'[id="userter"]')
																															.set(
																																	'html',
																																	u.mobileType);
																													box.messageBox
																															.getElement(
																																	'[id="usermobileno"]')
																															.set(
																																	'html',
																																	u.mobileNo);
																												} else {
																													new LightFace.MessageBox()
																															.error(data.message);
																												}
																											}
																										}).get();
																							}
																						}
																					}).get();
																		} else {
																			box.close();
																			return;
																		}
																	}
																}).confirm(data.message.msg);
													} else {
														new LightFace.MessageBox().error(data.message.msg);
														box.close();
														return;
													}
												} else {
													new Request.JSON(
															{
																url : ctx
																		+ '/html/customerCard/?m=getCustomerInfoByCardNo&cardNo='
																		+ cardNo + "&appId=" + caId,
																async : false,
																onSuccess : function(data) {
																	if (data.success) {
																		new Request.JSON(
																				{
																					url : ctx
																							+ '/html/application/?m=getAppById&appId='
																							+ caId,
																					async : false,
																					onSuccess : function(appdata) {
																						if (appdata.success) {
																							var e = appdata.message;
																							var u = data.message;
																							cardApp.aid = e.aid;
																							box.open();
																							var iconSrc = ctx
																									+ "/html/application/?m=getAppPcImg&appId="
																									+ e.id;
																							box.messageBox
																									.getElement(
																											'[id="appImg"]')
																									.set(
																											'html',
																											'<img onerror="javascript:this.src=\''
																													+ ctx
																													+ '/images/defApp.jpg'
																													+ '\' "  width="125" height="125" src="'
																													+ iconSrc
																													+ '" />');
																							box.messageBox
																									.getElement(
																											'[id="ename"]')
																									.set('html', e.name);
																							if ($chk(isSelect)) {
																								cardApp.ver = '0';
																								var selectHtml = '<select id="verSel">';
																								selectHtml += '<option value="">自动匹配</option>';
																								cardApp.ver = "";
																								$each(
																										e.vers,
																										function(item,
																												index) {
																											selectHtml += "<option value='"
																													+ item
																													+ "'>"
																													+ item
																													+ "</option>";
																										});
																								selectHtml += "</select>";
																								box.messageBox
																										.getElement(
																												'[id="evesion"]')
																										.set('html',
																												selectHtml);
																								box.messageBox
																										.getElement(
																												'[id="verSel"]')
																										.addEvent(
																												'change',
																												function() {
																													cardApp.ver = this.value;
																												});
																							} else {
																								box.messageBox
																										.getElement(
																												'[id="hideSelect"]')
																										.setStyle(
																												'display',
																												'none');
																							}
																							if ($chk(box.messageBox
																									.getElement('[id="elastver"]'))) {
																								box.messageBox
																										.getElement(
																												'[id="elastver"]')
																										.set(
																												'html',
																												e.lastestVersion);
																							}
																							box.messageBox.getElement(
																									'[id="espname"]')
																									.set('html',
																											e.sp_name);
																							box.messageBox
																									.getElement(
																											'[id="edesc"]')
																									.set(
																											'html',
																											e.description);
																							box.messageBox.getElement(
																									'[id="username"]')
																									.set('html',
																											u.username);
																							box.messageBox
																									.getElement(
																											'[id="userter"]')
																									.set(
																											'html',
																											u.mobileType);
																							box.messageBox
																									.getElement(
																											'[id="usermobileno"]')
																									.set('html',
																											u.mobileNo);
																						} else {
																							new LightFace.MessageBox()
																									.error(data.message);
																						}
																					}
																				}).get();
																	}
																}
															}).get();
												}
											}
										}).get();
							} else {
								new Request.JSON(
										{
											url : ctx + '/html/cardApp/?m=getCardApplicaiton&caId='
													+ cardApp.updateCaId,
											onSuccess : function(caData) {
												var appId = caData.message.appId;
												if (caData.message.cardInfo_cardNo == cardNo) {
													new Request.JSON(
															{
																url : ctx
																		+ '/html/customerCard/?m=getCustomerInfoByCardNo&cardNo='
																		+ cardNo,
																async : false,
																onSuccess : function(data) {
																	if (data.success) {
																		new Request.JSON(
																				{
																					url : ctx
																							+ '/html/application/?m=getAppById&appId='
																							+ caId,
																					async : false,
																					onSuccess : function(appdata) {
																						if (appdata.success) {
																							var e = appdata.message;
																							var u = data.message;
																							cardApp.aid = e.aid;
																							box.open();
																							var iconSrc = ctx
																									+ "/html/application/?m=getAppPcImg&appId="
																									+ e.id;
																							box.messageBox
																									.getElement(
																											'[id="appImg"]')
																									.set(
																											'html',
																											'<img onerror="javascript:this.src=\''
																													+ ctx
																													+ '/images/defApp.jpg'
																													+ '\' "  width="125" height="125" src="'
																													+ iconSrc
																													+ '" />');
																							box.messageBox
																									.getElement(
																											'[id="ename"]')
																									.set('html', e.name);
																							if ($chk(isSelect)) {
																								cardApp.ver = '0';
																								var selectHtml = '<select id="verSel">';
																								selectHtml += '<option value="">自动匹配</option>';
																								cardApp.ver = "";
																								$each(
																										e.vers,
																										function(item,
																												index) {
																											selectHtml += "<option value='"
																													+ item
																													+ "'>"
																													+ item
																													+ "</option>";
																										});
																								selectHtml += "</select>";
																								box.messageBox
																										.getElement(
																												'[id="evesion"]')
																										.set('html',
																												selectHtml);
																								box.messageBox
																										.getElement(
																												'[id="verSel"]')
																										.addEvent(
																												'change',
																												function() {
																													cardApp.ver = this.value;
																												});
																							} else {
																								box.messageBox
																										.getElement(
																												'[id="hideSelect"]')
																										.setStyle(
																												'display',
																												'none');
																							}
																							if ($chk(box.messageBox
																									.getElement('[id="elastver"]'))) {
																								box.messageBox
																										.getElement(
																												'[id="elastver"]')
																										.set(
																												'html',
																												e.lastestVersion);
																							}
																							box.messageBox.getElement(
																									'[id="espname"]')
																									.set('html',
																											e.sp_name);
																							box.messageBox
																									.getElement(
																											'[id="edesc"]')
																									.set(
																											'html',
																											e.description);
																							box.messageBox.getElement(
																									'[id="username"]')
																									.set('html',
																											u.username);
																							box.messageBox
																									.getElement(
																											'[id="userter"]')
																									.set(
																											'html',
																											u.mobileType);
																							box.messageBox
																									.getElement(
																											'[id="usermobileno"]')
																									.set('html',
																											u.mobileNo);
																						} else {
																							new LightFace.MessageBox()
																									.error(data.message);
																						}
																					}
																				}).get();
																	}
																}
															}).get();
												} else {
													new LightFace.MessageBox().error("当前终端不是所选操作的终端");
												}
											}
										}).get();
							}
						} else {
							new Request.JSON(
									{
										url : ctx + '/html/cardApp/?m=getCardApplicaiton&caId=' + caId,
										onSuccess : function(caData) {
											var appId = caData.message.appId;
											if (caData.message.cardInfo_cardNo == cardNo) {
												cardApp.aid = caData.message.appAid;
												new Request.JSON(
														{
															url : ctx
																	+ '/html/customerCard/?m=getCustomerInfoByCardNo&cardNo='
																	+ cardNo,
															async : false,
															onSuccess : function(data) {
																if (data.success) {
																	new Request.JSON(
																			{
																				url : ctx
																						+ '/html/application/?m=getAppById&appId='
																						+ appId,
																				async : false,
																				onSuccess : function(appdata) {
																					if (appdata.success) {
																						var e = appdata.message;
																						var u = data.message;
																						box.open();
																						var iconSrc = ctx
																								+ "/html/application/?m=getAppPcImg&appId="
																								+ e.id;
																						box.messageBox
																								.getElement(
																										'[id="appImg"]')
																								.set(
																										'html',
																										'<img onerror="javascript:this.src=\''
																												+ ctx
																												+ '/images/defApp.jpg'
																												+ '\' "  width="125" height="125" src="'
																												+ iconSrc
																												+ '" />');
																						box.messageBox.getElement(
																								'[id="ename"]').set(
																								'html', e.name);
																						if ($chk(isSelect)) {
																							cardApp.ver = '0';
																							var selectHtml = '<select id="verSel">';
																							selectHtml += '<option value="">自动匹配</option>';
																							cardApp.ver = "";
																							$each(
																									e.vers,
																									function(item,
																											index) {
																										selectHtml += "<option value='"
																												+ item
																												+ "'>"
																												+ item
																												+ "</option>";
																									});
																							selectHtml += "</select>";
																							box.messageBox.getElement(
																									'[id="evesion"]')
																									.set('html',
																											selectHtml);
																							box.messageBox
																									.getElement(
																											'[id="verSel"]')
																									.addEvent(
																											'change',
																											function() {
																												cardApp.ver = this.value;
																											});
																						} else {
																							box.messageBox
																									.getElement(
																											'[id="hideSelect"]')
																									.setStyle(
																											'display',
																											'none');
																						}
																						if ($chk(box.messageBox
																								.getElement('[id="elastver"]'))) {
																							box.messageBox
																									.getElement(
																											'[id="elastver"]')
																									.set(
																											'html',
																											e.lastestVersion);
																						}
																						box.messageBox.getElement(
																								'[id="espname"]').set(
																								'html', e.sp_name);
																						box.messageBox.getElement(
																								'[id="edesc"]').set(
																								'html', e.description);
																						box.messageBox.getElement(
																								'[id="username"]').set(
																								'html', u.username);
																						box.messageBox.getElement(
																								'[id="userter"]').set(
																								'html', u.mobileType);
																						box.messageBox
																								.getElement(
																										'[id="usermobileno"]')
																								.set('html', u.mobileNo);
																					} else {
																						new LightFace.MessageBox()
																								.error(data.message);
																					}
																				}
																			}).get();
																}
															}
														}).get();
											} else {
												new LightFace.MessageBox().error("当前终端不是所选操作的终端");
											}
										}
									}).get();
						}
					} else {
						new LightFace.MessageBox().error("不支持当前终端或无法读取卡片信息");
					}
				}
			},
			getQueryValue : function(name) {
				var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
				var r = window.location.search.substr(1).match(reg);
				if (r != null) {
					return unescape(r[2]);
				}
				return "";
			}
		});