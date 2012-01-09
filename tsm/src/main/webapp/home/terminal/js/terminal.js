LTerminal = {};

Terminal = new Class( {
	Implements : [ Events, Options ],
	options : {},
	checked : 1, // 1未检测 2检测成功 3检测失败
	initialize : function(options) {
		var Tclass = this;
		this.setOptions(options);
		this.ccid = this.getQueryValue("ccid");
		this.getConstants();
		// 绑定挂失事件
	},
	getConstants : function() {
		new Request.JSON( {
			url : ctx + "/html/localtransaction/?m=exportConstant",
			onSuccess : function(json) {
				if (json.success) {
					this.transConstant = json.message;
				}
			}.bind(this)
		}).get();
	},
	doInit : function(ccid) {
		var obj = this;
		this.getAppList(ccid);
		this.getSDList(ccid);
		this.getSpaceCss(ccid);
		this.getTerminal(ccid);
		var lost = $("lost");
		lost.addEvent("click", function(e) {
			e.stop();
			new LightFace.MessageBox( {
				onClose : function() {
					if (this.result) {
						obj.lostTermianl(ccid);
					}
				}
			}).confirm("您确认要进行挂失吗？请慎重操作");
		});
		// 绑定注销事件
		var lost = $("cancel");
		lost.addEvent("click", function(e) {
			e.stop();
			new LightFace.MessageBox( {
				onClose : function() {
					if (this.result) {
						obj.cancelTermianl(ccid);
					}
				}
			}).confirm("您确认要进行注销吗？请慎重操作");
		});
		// 添加激活事件
		var active = $("active");
		active.addEvent("click", function(e) {
			e.stop();
			new LightFace.MessageBox( {
				onClose : function() {
					if (this.result) {
						obj.activeTermianl(ccid);
					}
				}
			}).confirm("您确认要激活终端吗？");
		});
		// 添加更换手机号事件
		var active = $("changeNumber");
		active.addEvent("click", function(e) {
			e.stop();
			new LightFace.MessageBox( {
				onClose : function() {
					if (this.result) {
						obj.changeNumber(ccid);
					}
				}
			}).confirm("您确认要更换此终端的手机号码吗？");
		});
		
		$('blankTip').setStyle('display', 'none');
		$('li' + ccid).set('class', 'user_m_l_3');
	},
	// 获取用户的所有终端
	getCustomerTerminal : function() {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=index",
			async : false,
			onSuccess : function(responseText, responseXML) {
				if (responseText.success) {
					if (responseText.result.length > 0) {
						var firstId = responseText.result[0].id;
						var liString = "";
						$each(responseText.result, function(item, index) {
							var phoneName = item.name == '' ? mobileType_brandChs : item.name;
							liString += '<div class="user_m_l_2" id="li' + item.id + '">' + '<p class="user_m_img">'
									+ '<img onerror="javascript:this.src=\'' + ctx + '/images/defTerim.jpg'
									+ '\' "  src="/tsm/html/mobile/?m=getMobilePic&id=' + item.mobileType_id + '" />'
									+ '</p>' + '<p class="user_m_text">' + '名称  : ' + obj.maxText(phoneName, 14)
									+ '<br />' + '号码  : ' + item.mobileNo + '<br />' + '状态 : ';
							if (item.statusOriginal == 4) {
								liString += '<font color="red">' + item.status + '</font>';
							} else {
								liString += item.status;
							}
							liString += '(' + item.active + ')' + '<br/>' + '品牌: ' + item.mobileType_brandChs
									+ '<br /> ' + '机型 : ' + obj.maxText(item.mobileType_type, 14) + '<br /> '
									+ '<a class="buts m_t_5" href="' + ctx + '/home/terminal/termCenter.jsp?ccid='
									+ item.id + '#001">查看</a>' + '</p>' + '</div>';
						});
						$("terminals").set("html", liString);
						var ccid = obj.getQueryValue("ccid");
						if (ccid) {
							obj.doInit(ccid);
						} else {
							if (firstId) {
								obj.doInit(firstId);
							}
						}
					} else {
						obj.setDisplayNone();
					}
				} else {
					new LightFace.MessageBox().error(responseText.message);
				}
			}
		}).send();
	},
	// 获取选择的指定终端
	getTerminal : function(ccid) {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getCusstomerCard",
			data : {
				'ccId' : ccid
			},
			async : false,
			onSuccess : function(responseText, responseXML) {
				if (responseText.success) {
					obj.currtyTerm = responseText.result;
					var status = responseText.result[0].statusOriginal;
					var inBlack = responseText.result[0].inBlackOriginal;
					var active = responseText.result[0].activeOriginal;
					if (status == 1) {
						$("lost").setStyle("display", '');
						$("cancel").setStyle("display", '');
						if (inBlack == 1) {
							$("cancel").setStyle("display", 'none');
						}
						$('haveApp').setStyle('display', '');
						$('haveSd').setStyle('display', '');
						$('appspace').setStyle('display', '');
						if(inBlack == 0){
//						    $("changeNumber").setStyle("display", '');
						}
					} else if (status == 2) {
						$('haveApp').setStyle('display', '');
						$('haveSd').setStyle('display', '');
						$('appspace').setStyle('display', '');
					} else if (status == 3) {
						if(inBlack != 1){
							$("active").setStyle("display", '');
							$("cancel").setStyle("display", '');
							$('activeTip').setStyle('display', '');
						}
					} else if (status == 5) {
						$('haveApp').setStyle('display', '');
						$('haveSd').setStyle('display', '');
						$('appspace').setStyle('display', '');
					}
					if (inBlack == 1) {
							$("blackTip").setStyle("display", '');
					}
				} else {
					new LightFace.MessageBox().error(responseText.message);
				}
			}
		}).get();
		this.curryCcid = ccid;
	},
	// 获取选择终端的应用信息
	getAppList : function(ccid) {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=listCardApp",
			data : {
				'ccId' : ccid
			},
			onSuccess : function(resp, responseXML) {
				if (resp.success) {
					var apps = resp.result;
					// var eUl = $("appUl");
					if (apps.length == 0) {
						$("appUl").set('html', '<b>尚未安装</b>');
					} else {
						$each(apps, function(item, i) {
						    	if(item.statusOrg != 1 ){
						    		if((item.statusOrg  == 6 &&  item.presetMode != 3) || (item.statusOrg == 4 && item.presetMode == 1 && item.delRule == 1) || item.statusOrg == 2 || item.statusOrg == 3 || item.statusOrg == 5 || item.statusOrg == 7 || item.statusOrg == 8){
						    			var appUl = $("appUl");
										var div = new Element('div', {'class' : 'user_m_l_4', styles : {'margin-top':'0px'}});
										// 第一个P
										var eP = new Element("p", {
											'class' : 'user_m_img'
										}).inject(div);
										var eImage = new Element("img", {
											'src' : ctx + '/html/application/?m=getAppPcImg&appId=' + item.appId
										});
										eImage.addEvent("error", function() {
											this.set("src", ctx + "/images/defApp.jpg");
										});
			                                                         eImage.setStyle("width", "67px");
			                                                         eImage.setStyle("height", "67px");
										eImage.inject(eP);

										// 第二个P
										var eP2 = new Element("p", {
											'class' : 'user_m_text'
										});
										var eA = new Element("a", {
											"class" : "b",
											"href" : ctx + '/home/app/appinfo.jsp?id=' + item.appId + '&cardAppId='
													+ item.cardAppId,
											"title" : item.appNames
										});
										eA.set("text", '名称:' + obj.maxText(item.appNames, 14));
										eA.inject(eP2);
										var eBr = new Element("br");
										eBr.inject(eP2);

										var eA3 = new Element("font");
										eA3.set("text", '安装版本:' + item.appVer);
										eA3.inject(eP2);
										var eBr2 = new Element("br");
										eBr2.inject(eP2);

										var ef = new Element("font");
										ef.set("text", '状态:' + item.status);
										if (item.hasNew) {
											ef.set("html", '状态:' + item.status+ '&nbsp;&nbsp;<a title="点击更新应用" href="javascript:void(0)" id="newversion'
													+item.appId+'"><font color="red">有新版本</font></a>');
										}
										ef.inject(eP2);
										var eBr3 = new Element("br");
										eBr3.inject(eP2);
										
										if(item.useRam!=-1 && item.useRom!=-1){
											var eA7 = new Element("font");
											eA7.set("text", '占用内存空间:' + obj.formatSize(item.useRam));
											eA7.inject(eP2);
											var eBr7 = new Element("br");
											eBr7.inject(eP2);
											var eA8 = new Element("font");
											eA8.set("text", '占用存储空间:' + obj.formatSize(item.useRom));
											eA8.inject(eP2);
											var eBr8 = new Element("br");
											eBr8.inject(eP2);
											/*var eA2 = new Element("a", {
												"class" : "b",
												"href" : ctx + '/home/sp/spinfo.jsp?id=' + item.spId,
												"title" : item.spName
											});
											eA2.set("text", '提供商:' + obj.maxText(item.spName, 12));
											eA2.inject(eP2);
											var eBr9 = new Element("br");
											eBr9.inject(eP2);*/
											var option = new Element('a', {html : '操作', 'class' : 'buts m_t_5', styles : {cursor : 'pointer'}}).inject(eP2);
											option.addEvent('click', function(e) {
												e.stop();
												var template = $("appInfo").clone(true, true);
												var appInfoWindow = new LightFace( {
													draggable : true,
													initDraw : false,
													content : template.get('html')
												});
												obj.setButtons(item.appId, item.statusOrg, appInfoWindow,item);
											});
											eP2.inject(div);
											div.inject(appUl);
										}else{
											/*var eA2 = new Element("a", {
												"class" : "b",
												"href" : ctx + '/home/sp/spinfo.jsp?id=' + item.spId,
												"title" : item.spName
											});
											eA2.set("text", '提供商:' + obj.maxText(item.spName, 12));
											eA2.inject(eP2);
											var eBr9 = new Element("br");
											eBr9.inject(eP2);*/
											
											var option = new Element('a', {html : '操作', 'class' : 'buts m_t_5', styles : {cursor : 'pointer'}}).inject(eP2);
											option.addEvent('click', function(e) {
												e.stop();
												var template = $("appInfo").clone(true, true);
												var appInfoWindow = new LightFace( {
													draggable : true,
													initDraw : false,
													content : template.get('html')
												});
												obj.setButtons(item.appId, item.statusOrg, appInfoWindow,item);
											});
											
											eP2.inject(div);
											div.inject(appUl);
										}
										if (item.hasNew) {
											$('newversion'+item.appId).addEvent("click", function(event) {
						        				new LightFace.MessageBox({
						        					onClose : function(){
						        						if(this.result){
						        							obj.updateApp(item.aid);
						        						}
						        					}
						        				}).confirm("您确认要进行升级吗？");
											}.bind(this));
										}
						    		}
						    	}
						});
					}
				} else {
					new LightFace.MessageBox().error(resp.message);
				}
			}.bind(this)
		}).send();
	},
	getSDList : function(ccid) {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=listCardSD",
			data : {
				'ccId' : ccid
			},
			onSuccess : function(resp, responseXML) {
				if (resp.success) {
					var sds = resp.result;
						$each(sds, function(item, i) {
						    	if(item.statusOrg != 1 ){
						    			var sdUl = $("sdUl");
										var div = new Element('div', {'class' : 'user_m_l_5', styles : {'margin-top':'0px'}});
										// 第二个P
										var eP2 = new Element("p", {
											'class' : 'user_m_text'
										});
										var eA2 = new Element("a", {
											"class" : "b",
											"href" : 'javascript:void(0);',
											"title" : item.sdName
										});
										eA2.set("text", '名称:' + obj.maxText(item.sdName, 14));
										eA2.inject(eP2);
										var eBr = new Element("br");
										eBr.inject(eP2);
										
										var eA3 = new Element("font");
										eA3.set("text", '状态 : ' + item.status);
										eA3.inject(eP2);
										var eBr2 = new Element("br");
										eBr2.inject(eP2);
										
										var eA4 = new Element("font");
										eA4.set("text", '空间模式 : ' + item.spaceRule);
										eA4.inject(eP2);
										var eBr3 = new Element("br");
										eBr3.inject(eP2);
										
										var eA5 = new Element("font");
										eA5.set("text", '删除规则 : ' + item.deleteRule);
										eA5.inject(eP2);
										var eBr4 = new Element("br");
										eBr4.inject(eP2);
										

										var sdUsedRam = new Element("font");
										sdUsedRam.set("text", '已用内存空间 : ' + obj.formatSize(item.sdUsedRam));
										sdUsedRam.inject(eP2);
										var eBr5 = new Element("br");
										eBr5.inject(eP2);
										

										var sdUsedRom = new Element("font");
										sdUsedRom.set("text", '已用存储空间 : ' + obj.formatSize(item.sdUsedRom));
										sdUsedRom.inject(eP2);
										var eBr6 = new Element("br");
										eBr6.inject(eP2);
										
										var sdAviliableRam = new Element("font");
										sdAviliableRam.set("text", '剩余内存空间 : ' + obj.formatSize(item.sdAviliableRam));
										sdAviliableRam.inject(eP2);
										var eBr7 = new Element("br");
										eBr7.inject(eP2);
										

										var sdAviliableRom = new Element("font");
										sdAviliableRom.set("text", '剩余存储空间 : ' + obj.formatSize(item.sdAviliableRom));
										sdAviliableRom.inject(eP2);
										var eBr8 = new Element("br");
										eBr8.inject(eP2);
										
										eP2.inject(div);
										div.inject(sdUl);
						    	}
						});
				} else {
					new LightFace.MessageBox().error(resp.message);
				}
			}.bind(this)
		}).send();
	},
	updateApp : function(aid) {
		var obj = this;
		var rardNo = obj.currtyTerm[0].card_cardNo;
// alert(rardNo);
		var nowCardNo = new JIM.CardDriver( {
			ctl : cardDriver,
			operations : []
		}).readCardNo();
// alert(nowCardNo);
		if (nowCardNo != rardNo) {
			new LightFace.MessageBox().error("操作终端与所选终端不符");
			return;
		}
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : obj.transConstant.UPDATE_APP
			} ],
			onSuccess : function(response) {
			this.closeConnection();
			new LightFace.MessageBox({
			    onClose : function(){
					location.reload();
			    }
			}).info("更新执行成功");
		},
		onFailure : function(response){
			this.closeConnection();
			new LightFace.MessageBox({
			onClose : function(){
				location.reload();
				}
			}).error(response.status.options.statusDescription);
		}
		}).exec();
	}
	,
	setButtons : function(appId, status, win, item) {
		var cardApp = this;
		var rardNo = cardApp.currtyTerm[0].card_cardNo;
		var terStatus = cardApp.currtyTerm[0].statusOriginal;
		var inblack = cardApp.currtyTerm[0].inBlackOriginal;
		var prestMode = item.presetMode;
		var delRule = item.delRule;
		if (terStatus == 1) {
			if (status == 5 || status == 3 || status == 2 || status == 4) {
				win.options.buttons = [ {
					title : '删除',
					event : function() {
						new LightFace( {
							content : '请选择删除方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
								new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.DELETE_APP
										} ],
										onSuccess : function(response) {
										    this.closeConnection();
								    			new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
								},
								onFailure : function(response){
								    this.closeConnection();
								    new LightFace.MessageBox({
									 onClose : function(){
						    				win.close();
										location.reload();
						    			    }
								    }).error(response.status.options.statusDescription);
								    return;
								}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.DELETE_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			}
			else if (status == 6) {
				win.options.buttons = [ {
					title : '删除',
					event : function() {
						new LightFace( {
							content : '请选择删除方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
								new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.DELETE_APP
										} ],
										onSuccess : function(response) {
										    this.closeConnection();
								    			new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
								},
								onFailure : function(response){
								    this.closeConnection();
								    new LightFace.MessageBox({
									 onClose : function(){
						    				win.close();
										location.reload();
						    			    }
								    }).error(response.status.options.statusDescription );
								    return;
								}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.DELETE_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				},{
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			} else if (status == 7) {
				win.options.buttons = [{
					title : '删除',
					event : function() {
						new LightFace( {
							content : '请选择删除方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
									 new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.DELETE_APP
										} ],
										onSuccess : function(response) {
										    this.closeConnection();	
										    new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
										},
										onFailure : function(response){
										    this.closeConnection();
										    new LightFace.MessageBox({
											 onClose : function(){
								    				win.close();
												location.reload();
								    			    }
										    }).error(response.status.options.statusDescription);
										    return;
										}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.DELETE_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				}, {
					title : '数据更新',
					event : function() {
						new LightFace( {
							content : '请选择数据更新方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
									new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.PERSONALIZE_APP
										} ],
										onSuccess : function(response) {
								    			this.closeConnection();	
								    			new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
								},
								onFailure : function(response){
								    this.closeConnection();
								    new LightFace.MessageBox({
									 onClose : function(){
						    				win.close();
										location.reload();
						    			    }
								    }).error(response.status.options.statusDescription);
								    return;
								}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.PERSONALIZE_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				},{
					title : '关 闭',
					event : function() {
						win.close();
					}
				}];
			} else if ( status == 8) {
				win.options.buttons = [{
					title : '删除',
					event : function() {
						new LightFace( {
							content : '请选择删除方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
									 new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.DELETE_APP
										} ],
										onSuccess : function(response) {
										    this.closeConnection();	
										    new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
										},
										onFailure : function(response){
										    this.closeConnection();
										    new LightFace.MessageBox({
											 onClose : function(){
								    				win.close();
												location.reload();
								    			    }
										    }).error(response.status.options.statusDescription);
										    return;
										}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.DELETE_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				}, {
					title : '数据更新',
					event : function() {
						new LightFace( {
							content : '请选择数据更新方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
									new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.PERSONALIZE_APP
										} ],
										onSuccess : function(response) {
								    			this.closeConnection();	
								    			new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
								},
								onFailure : function(response){
								    this.closeConnection();
								    new LightFace.MessageBox({
									 onClose : function(){
						    				win.close();
										location.reload();
						    			    }
								    }).error(response.status.options.statusDescription);
								    return;
								}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.PERSONALIZE_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				}, {
					title : '锁定',
					event : function() {
						new LightFace( {
							content : '请选择锁定方式',
							title : '提示',
							buttons : [ {
								title : '立即执行',
								event : function() {
									var nowCardNo = new JIM.CardDriver( {
										ctl : cardDriver,
										operations : []
									}).readCardNo();
									if (nowCardNo != rardNo) {
										new LightFace.MessageBox().error("操作终端与所选终端不符");
										return;
									}
									new JIM.CardDriver( {
										ctl : cardDriver,
										operations : [ {
											aid : cardApp.aid,
											operation : cardApp.transConstant.LOCK_APP
										} ],
										onSuccess : function(response) {
										    this.closeConnection();	
										    new LightFace.MessageBox({
								    			    onClose : function(){
								    				win.close();
												location.reload();
								    			    }
								    			}).info("操作成功");
										},
										onFailure : function(response){
										    this.closeConnection();
										    new LightFace.MessageBox({
											 onClose : function(){
								    				win.close();
												location.reload();
								    			    }
										    }).error(response.status.options.statusDescription);
										    return;
										}
									}).exec();
									this.close();
								},
								color : 'blue'
							}, {
								title : '创建任务',
								event : function() {
									cardApp.createOpt(cardApp.aid, cardApp.transConstant.LOCK_APP);
									this.close();
								},
								color : 'blue'
							}, {
								title : '关闭',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					},
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			} else {
				win.options.buttons = [ {
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			}
			if(inblack == 1){
				win.options.buttons = [ {
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			}
		} else if(terStatus == 2){
			if(status == 7 || status == 8) {
				win.options.buttons = [ {
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			}
			else {
				win.options.buttons = [ {
					title : '关 闭',
					event : function() {
						win.close();
					}
				} ];
			}
		}else{
			win.options.buttons = [ {
				title : '关 闭',
				event : function() {
					win.close();
				}
			} ];
		}
		win.draw();
		this.getAppInfo(appId, win);
	},
	getAppInfo : function(appId, box) {
		var cardApp = this;
		new Request.JSON( {
			url : ctx + '/html/application/?m=getAppById&appId=' + appId,
			async : false,
			onSuccess : function(data) {
				if (data.success) {
					box.open();
					var e = data.message;
					cardApp.aid = e.aid;
					var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId=" + e.id;
					box.messageBox.getElement('[id="appImg"]').set(
							'html',
							'<img onerror="javascript:this.src=\'' + ctx + '/images/defApp.jpg'
									+ '\' "  width="125" height="125" src="' + iconSrc + '" />');
					box.messageBox.getElement('[id="ename"]').set('html', e.name);
					box.messageBox.getElement('[id="espname"]').set('html', e.sp_name);
					box.messageBox.getElement('[id="edesc"]').set('html', e.description);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).get();
	},
	// 获取卡片空间大小信息相关
	getSpaceCss : function(ccid) {
		var Tclass = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getCardSzie",
			data : {
				'ccId' : ccid
			},
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					Tclass.changeText("vsTotal", Tclass.formatSize(resp.result[0].totalSpace.ram));
					Tclass.changeText("usedVS", Tclass.formatSize(resp.result[0].usedSpace.ram));
					Tclass.changeText("existVS", Tclass.formatSize(resp.result[0].existSpace.ram));
					Tclass.changeText("nsTotal", Tclass.formatSize(resp.result[0].totalSpace.nvm));
					Tclass.changeText("usedNS", Tclass.formatSize(resp.result[0].usedSpace.nvm));
					Tclass.changeText("existNS", Tclass.formatSize(resp.result[0].existSpace.nvm));
					$("c1").setStyle('width', resp.result[0].vsPercent);
					$("c2").setStyle('width', resp.result[0].nsPercent);
				} else {
					new LightFace.MessageBox().error(resp.message);
				}
			}
		}).send();
	},
	formatSize : function(total) {
		var totalFolat = total.toFloat();
		if (totalFolat < 1024) {
			return totalFolat + "Byte";
		} else if (totalFolat >= 1024 && totalFolat < 1048576) {
			var i = totalFolat / 1024;
			return i.toFixed(2) + "KByte";
		} else if (totalFolat >= 1048576) {
			var i = totalFolat / 1024 / 1024;
			return i.toFixed(2) + 'MByte';
		}
	},// 挂失终端
	lostTermianl : function(ccid) {
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=customerCardLost&ccId=" + ccid,
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							location.reload();
						}
					}).info("操作成功");
				} else {
					new LightFace.MessageBox().error("请重试");
				}
			}
		}).get();
		return false;
	},// 注销终端
	cancelTermianl : function(ccid) {
		var card = this;
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=getCusstomerCard&ccId=" + ccid,
			onSuccess : function(data) {
				if(data.success){
					var cardInfo =  data.result[0];
					var rardNo = cardInfo.card_cardNo;
					if((cardInfo.statusOriginal != 1 && cardInfo.statusOriginal != 3) || cardInfo.inBlackOriginal == 1){
					    new LightFace.MessageBox().error("终端状态异常或者在黑名单中");
						return;
					}
					var nowCardNo = new JIM.CardDriver( {
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (nowCardNo != rardNo) {
						new LightFace.MessageBox().error("操作终端与所选终端不符");
						return;
					}	
					var status = card.currtyTerm[0].card_status;
					if(status == '无效'){
					    new LightFace.MessageBox().error("卡不可用，请到移动营业厅恢复卡片");
						return;
					}
					new Request.JSON( {
						url : ctx + "/html/customerCard/?m=checkCancelTermCardApp&ccId=" + ccid,
						onSuccess : function(resp, responseXML) {
							if (resp.success) {
								card.finishCancel(ccid); 
							} else {
								new LightFace.MessageBox().error("终端上还有未删除的应用，请先手动删除后再进行操作");
							}
						}
					}).get();
				}
			}
		}).post();
	},
	checkCardOptFinish : function(sessionId, ccid) {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/localtransaction/?m=checkCardOptFinish&sessionId=" + sessionId,
			onSuccess : function(resp, responseXML) {
				if (resp.success) {
					obj.finishCancel(ccid);
				} else {
					new LightFace.MessageBox().error("终端数据清除失败，注销不成功");
				}
			}
		}).get();
	},
	finishCancel : function(ccid) {
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=finishCancel&ccId=" + ccid,
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							location.reload();
						}
					}).info("注销成功");
				} else {
					new LightFace.MessageBox().error("请重试");
				}
			}
		}).get();
	},
	changeNumber : function(ccid){
					var obj = this;
					var activeContent = $('changeNumberDiv').get('html');
					var box = new LightFace( {
						width :400,
						draggable : false,
						initDraw : true,
						title : '更换手机号码',
						content : activeContent,
						buttons : [ {
							title : '更换',
							event : function() {
								if($chk(box.activeCode)){
									var checkCode = box.messageBox.getElement('[id="checkInput"]').get('value');
									if($chk(checkCode)){
										if(box.activeCode == checkCode){
											new Request.JSON({
												url : ctx + '/html/customerCard/?m=changeMobileNo',
												onSuccess : function(data) {
													if (data.success) {
														new LightFace.MessageBox({
															onClose : function(){
																location.reload();
															}
														}).info("更换手机号码成功");
													} else {
														new LightFace.MessageBox().error(data.message);
													}
												}
											}).post({
												'mobileNo' : box.mobileNo,
												'ccId': ccid
											});
										}else{
											new LightFace.MessageBox().error("您输入的验证码有错，请重新输入或者重新获取新的验证码");
										}
									}else{
										new LightFace.MessageBox().error("请输入您收到的验证码");
									}
								}else{
									new LightFace.MessageBox().error("请先发送验证码");
								}
							}
						},  {
							title : '退出',
							event : function() {
								this.close();
							}
						} ]
					});
					var butt = box.messageBox.getElement('[id="sendCheck"]');
					butt.addEvent('click',function(){
						var newMobileNo = box.messageBox.getElement('[id="newMobieNo"]').get('value');
						if($chk(newMobileNo)){
							var tmp = /^1[0-9]{10}$/;
							if(tmp.test(newMobileNo)){
								new Request.JSON({
									url : ctx + '/html/customerCard/?m=checkSend',
									onSuccess : function(data) {
										if (data.success) {
											box.activeCode = data.message;
											box.mobileNo = newMobileNo;
											new LightFace.MessageBox().info("验证码已发送，请查收");
										} else {
											new LightFace.MessageBox().error2("验证码发送失败，请重试");
										}
									}
								}).post({
									'mobileNo' : newMobileNo
								});
							}else{
								new LightFace.MessageBox().error("请输入一个有效的电话(1开头的11位数字)");
								return	
							}
						}else{
							new LightFace.MessageBox().error("请输入手机号");
							return
						}
					});
					box.open();
	},
	activeTermianl : function(ccid) {
		var obj = this;
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=reSendActive',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info("激活码已成功发送，点击确定后请输入激活码");
					var activeContent = $('doactive').get('html');
					new LightFace( {
						width : '400',
						draggable : false,
						initDraw : true,
						title : '激活终端',
						content : activeContent,
						buttons : [ {
							title : '激活',
							event : function() {
								obj.active(this, ccid);
							}
						}, {
							title : '再次发送激活码',
							event : function() {
								obj.reSend(this, ccid);
							}
						}, {
							title : '退出',
							event : function() {
								this.close();
							}
						} ]
					}).open();
				} else {
					new LightFace.MessageBox().error("重发激活码失败，请重试");
				}
			}
		}).post( {
			'ccId' : ccid,
			'type' : 1
		});
	},
	// 获取能够被更换终端
	getCanChange : function() {
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getCanChange",
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					if (resp.result.length > 0) {
						$('blankTip').setStyle('display', 'none');
						$("canChangeSelect").options.add(new Option('请选择', 0));
						Array.each(resp.result, function(item, index) {
							$("canChangeSelect").options
									.add(new Option(item.name + '(状态:' + item.status + ')', item.id));
						});
					} else {
						$("changeContent").setStyle("display", "none");
					}
					$('canChangeSelect').addEvent('change', function() {
						var select = this;
						Array.each(resp.result, function(item, index) {
							if (item.id == select.value) {
								$('changeNo').set('html', item.mobileNo);
							}
							if (select.value == 0) {
								$('changeNo').set('html', "请选择终端");
							}
						});
					});
				} else {
					$("changeContent").setStyle("display", "none");
				}
			},
			onError : function(text, error) {
				$("changeContent").setStyle("display", "none");
			}
		}).send();
		return false;
	},
	getOneInfoToOption : function(oldId) {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getCusstomerCard&ccId=" + oldId,
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					if (resp.result.length > 0) {
						$('blankTip').setStyle('display', 'none');
						Array.each(resp.result, function(item, index) {
							$("reverSelect").options.add(new Option((index + 1) + '.' + item.name + '(状态:'
									+ item.status + ')', item.id));
						});
						$('appTable').setStyle('display', 'none');
						$('doRevert').setStyle('display', 'none');
						$('checkBind').setStyle('display', 'none');
						obj.getReverAppList(oldId);
					} else {
						$("changeContent").setStyle("display", "none");
					}

				} else {
					$("changeContent").setStyle("display", "none");
				}
			},
			onError : function(text, error) {
				$("changeContent").setStyle("display", "none");
			}
		}).post();
	},
	getReverAppList : function(ccid) {
		if (ccid == 0) {
			$('appTable').setStyle('display', 'none');
			$('doRevert').setStyle('display', 'none');
			$('checkBind').setStyle('display', 'none');
		} else {
			new Request.JSON( {
				url : ctx + "/html/customerCard/?m=listRevertApps&ccId=" + ccid,
				onSuccess : function(resp, responseXML) {
					if (!$chk(resp)) {
						resp = JSON.encode(responseXML);
					}
					if (resp.success) {
						if (resp.result.length == 0) {
							$('appTable').setStyle('display', 'none');
							$('doRevert').setStyle('display', 'none');
							$('checkBind').setStyle('display', 'none');
							$('appTip').setStyle('display', '');
						} else {
							$('appTip').setStyle('display', 'none');
							$('appTable').setStyle('display', '');
							$('doRevert').setStyle('display', '');
							$('checkBind').setStyle('display', '');
						}
						var tbody = $('appTable').getElement('tbody');
						tbody.set('html', '');
						Array.each(resp.result, function(item, index) {
							var tr = new Element("tr");
							
							var td1 = new Element("td");
							td1.set("html",item.name);
							td1.inject(tr);

							var td2 = new Element("td");
							td2.set("html", item.status);
							td2.inject(tr);

							var td3 = new Element("td");
							td3.set("html", item.sp_name);
							td3.inject(tr);

							tr.inject(tbody);
						});
					} else {
						new LightFace.MessageBox.error("查询应用错误");
					}
				}
			}).post();
		}
	},
	// 获取能够被更换终端
	getCanRevert : function() {
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getCanRevert",
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					if (resp.result.length > 0) {
						$('blankTip').setStyle('display', 'none');
						$("reverSelect").options.add(new Option("请选择", 0));
						Array.each(resp.result, function(item, index) {
							$("reverSelect").options.add(new Option(item.name + '(状态:' + item.status + ')', item.id));
						});
						$('appTable').setStyle('display', 'none');
						$('doRevert').setStyle('display', 'none');
						$('checkBind').setStyle('display', 'none');
					} else {
						$("changeContent").setStyle("display", "none");
					}
				} else {
					$("changeContent").setStyle("display", "none");
				}
			},
			onError : function(text, error) {
				$("changeContent").setStyle("display", "none");
			}
		}).send();
	},
	revert : function() {
		var obj = this;
		var OldId = $("reverSelect").get('value');
		var cardNo = this.jsReadCard();
		if ($chk(cardNo)) {
			new Request.JSON( {
				url : ctx + "/html/customerCard/?m=revertApp",
				onSuccess : function(resp, responseXML) {
					if (!$chk(resp)) {
						resp = JSON.encode(responseXML);
					}
					if (resp.success) {
						var aidList = resp.message.aidList;
						var ccid = resp.message.customerCardId;
						var options = [];
						Array.each(aidList, function(item, index) {
							var tempOpt = {
								aid : item,
								operation : obj.transConstant.DOWNLOAD_APP
							};
							options.push(tempOpt);
						});
						if (options.length == 0) {
							new LightFace.MessageBox().error("您的终端已安装待恢复的应用，无需恢复");
						} else {
							new JIM.CardDriver( {
								ctl : cardDriver,
								operations : options,
								showMsg : false,
								onSuccess : function(response) {
									var sessionId = response.sessionId;
									obj.tipRevert(sessionId);
								},
								onFailure : function(response){
								    var sessionId = response.sessionId;
								    obj.tipRevert(sessionId);
								}
							}).exec();
						}
					} else {
						new LightFace.MessageBox().error(resp.message);
					}
				},
				onError : function(text, error) {
					new LightFace.MessageBox().error("");
				}
			}).post( {
				'oldId' : OldId,
				'cardNo' : cardNo
			});
		} else {
			new LightFace.MessageBox().error("终端还未准备好");
		}
	},
	tipRevert : function(sessionId) {
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=tipRevert',
			onSuccess : function(resp, responseXml) {
				if (resp.success) {
					if(resp.message.doSuccess){
						new LightFace.MessageBox().info("恢复成功");
					}else{
						new LightFace.MessageBox({
							onClose :  function(){
								var contents = '<div>' + '<h3>恢复详情:</h3><br/>';
								if(resp.message.successSize > 0){
									var successS = '';
									Array.each(resp.message.successList, function(item, index) {
										successS += '<p>   "' + item + '"</P>';
									});
									contents += '<hr/><p>恢复成功' + resp.message.successSize + '个,应用名称分别是:</p>' + successS ;
								}
								if(resp.message.failSize > 0){
									var failS = '';
									Array.each(resp.message.failList, function(item, index) {
										failS += '<p>   "' + item + '"</P>';
									});
									contents += '<hr/><p>恢复失败' + resp.message.failSize + '个,应用名称分别是:</p>' + failS;
								}
								if(resp.message.noexeSize > 0){
									var noexeS = '';
									Array.each(resp.message.noexeList, function(item, index) {
										noexeS += '<p>   "' + item + '"</P>';
									});
									noexeS += '"';
									contents += '<hr/><p>未恢复' + resp.message.noexeSize + '个,应用名称分别是:</p>' + noexeS;
								}
								contents += '</div>';
							new LightFace( {
								title : '提示',
								content : contents,
								buttons : [{
									title: '关闭',
									event: function() { this.close(); }
								}]
							}).open();
							}
						}).error("点击确定后查看详情");
					}
				} else {
					new LightFace.MessageBox().error(resp.message);
				}
			}
		}).post( {
			'sessionId' : sessionId
		});
	},
	respRevert : function(ccid) {
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=checkRevertFinish',
			onSuccess : function(resp, responseXml) {
				if (resp.success) {
					new LightFace.MessageBox().info("恢复成功");
					// window.location.href=ctx +
					// "/home/terminal/termCenter.jsp?ccid=" +
					// resp.message.ccid;
				} else {
					new LightFace.MessageBox().error(resp.message);
				}
			}
		}).post( {
			'ccid' : ccid
		});
	},
	jsReadCard : function() {
		var cardNo = new JIM.CardDriver( {
			ctl : cardDriver,
			operations : []
		}).readCardNo();
		return cardNo;
	},
	// 获取URL的值
	getQueryValue : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return "";
	},
	// 改变某个具体的文字
	changeText : function(id, text) {
		var obj = $(id);
		obj.set("text", text);
	},
	setDisplayNone : function() {
		$('blankTip').setStyle('display', '');
	},
	bindCheckInfo : function() {
		var card = this;
		$("checkBind").addEvent("click", function(e) {
			e.stop();
			new LightFace( {
				width : 300,
				title : '<img id="image" src="' + ctx + '/lib/lightface/assets/information.png" />&nbsp;提示',
				content : '请确定是否已将终端连接至读卡器，详细操作请参考<a  style="color:#4682b4; text-decoration:underline;"  href="' + ctx + '/help.jsp" target="_blank">帮助中心</a>',
				draggable : true,
				initDraw : false,
				onClose : function() {
					// this.close();
				},
				buttons : [ {
					title : '确定',
					event : function() {
						this.close();
						try{
							var cardNo = new JIM.CardDriver( {
								ctl : cardDriver,
								operations : []
							}).readCardNo();
							
							if ($chk(cardNo)) {
								new LightFace.MessageBox().info("检测成功，可以执行操作");
								$('checkFlag').set("src",ctx + '/images/checkyes.png');
								$('checkFlag').setStyle('display','');
								card.checked = 2;
							}
						} catch (e){
							$('checkFlag').set("src",ctx + '/images/checkerror.png');
							$('checkFlag').setStyle('display','');
							card.checked = 3;
						}
					},
					color : 'blue'
				}, {
					title : '关 闭',
					event : function() {
						this.close();
					}
				} ]
			}).open();
		});
		return false;
	},
	bindCheck : function() {
		var card = this;
		var cardNo = new JIM.CardDriver( {
			ctl : cardDriver,
			operations : []
		}).readCardNo();
		if ($chk(cardNo)) {
			return true;
		}
		return false;
	},
	subBind : function(cardNo, formString) {
		var card = this;
		var mobileTypeId = $('mobileType').get('value');
		if (!mobileTypeId) {
			new LightFace.MessageBox().error("必须选择您的机型");
			return;
		}
		if (this.bindCheck()) {
			var box = new LightFace.MessageBox();
			box.loading('系统正在处理，请稍候');
			var activeContent = $('doactive').get('html');
			var obj = this;
			cardNo = new JIM.CardDriver( {
				ctl : cardDriver,
				operations : []
			}).readCardNo();
			new Request.JSON( {
				url : ctx + '/html/customerCard/?m=bindCard&cardNo=' + cardNo + '&mobileTypeId=' + mobileTypeId,
				onSuccess : function(data) {
					if (data.success) {
						box.close();
						new LightFace.MessageBox().info("激活码已成功发送，点击确定后请输入激活码");
						new LightFace( {
							width : '400',
							draggable : false,
							initDraw : true,
							title : '绑定激活',
							content : activeContent,
							buttons : [ {
								title : '激活',
								event : function() {
									obj.active(this, data.message.customerCardId);
								}
							}, {
								title : '再次发送激活码',
								event : function() {
									obj.reSend(this, data.message.customerCardId);
								}
							}, {
								title : '退出',
								event : function() {
									this.close();
								}
							} ]
						}).open();
					} else {
						box.close();
						new LightFace.MessageBox().error(data.message);
					}
				}
			}).post(formString);
		} else {
			new LightFace.MessageBox().error("请确认手机已准备好");
		}
	},
	changeBind : function(cardNo, formString) {
		var mobileTypeId = $('mobileType').get('value');
		if (!mobileTypeId) {
			new LightFace.MessageBox().error("必须选择您的机型");
			return;
		}
		var activeContent = $('doactive').get('html');
		var obj = this;
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=changeBind&cardNo=' + cardNo + '&mobileTypeId=' + mobileTypeId,
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info("操作成功，确认后请输入手机激活码");
					new LightFace( {
						width : '400',
						draggable : false,
						initDraw : true,
						title : '绑定激活',
						content : activeContent,
						buttons : [ {
							title : '激活',
							event : function() {
								obj.changeActive(this, data.message.customerCardId, data.message.oldCardId);
							}
						}, {
							title : '再次发送激活码',
							event : function() {
								obj.reSend(this, data.message.customerCardId);
							}
						}, {
							title : '退出',
							event : function() {
								this.close();
							}
						} ]
					}).open();
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post(formString);
	},
	readCard : function(type) {
		if(this.checked == 1){
			new LightFace.MessageBox().error("请先检测您的终端"); 
		}else{
		   if(this.checked == 2){
				var cardNo = this.jsReadCard();
				var formString = $("bindForm").toQueryString();
				if (type == 1) {
					this.subBind(cardNo, formString);
				} else if (type == 2) {
					this.changeBind(cardNo, formString);
				}
		   }else{
			   new LightFace.MessageBox().error("您的终端尚未准备好"); 
		   }
		}
	},
	active : function(box, ccid) {
		var activeCode = box.messageBox.getElement("[id=activeInput]").get('value');
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=activeCard',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							box.close();
							window.location.href = ctx + "/home/terminal/termInfo.jsp?ccid=" + ccid;
						}
					}).info("绑定激活成功");
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post( {
			'avtiveCode' : activeCode,
			'ccId' : ccid
		});
	},
	changeActive : function(box, ccid, oldId) {
		var obj = this;
		var activeCode = box.messageBox.getElement("[id=activeInput]").get('value');
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=changeActive',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							box.close();
							var aidList = data.message.aidList;
							var options = [];
							Array.each(aidList, function(item, index) {
								var tempOpt = {
									aid : item,
									operation : obj.transConstant.DOWNLOAD_APP
								};
								options.push(tempOpt);
							});
							if (options.length == 0) {
								obj.respChange(data.message.odlId, data.message.actveId);
							} else {
								new JIM.CardDriver( {
									ctl : cardDriver,
									operations : options,
									onSuccess : function(response) {
												obj.respChange(data.message.odlId, data.message.actveId);
									}
								}).exec();
							}
						}
					}).info("更换激活成功");
				} else {
					new LightFace.MessageBox().error2("更换终端失败，请重试");
				}
			}
		}).post( {
			'avtiveCode' : activeCode,
			'ccId' : ccid,
			'oldId' : oldId
		});
	},
	respChange : function(oldId, newId) {
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=checkChangeFinish',
			onSuccess : function(resp, responseXml) {
				if (resp.success) {
					new LightFace.MessageBox().info("更换成功");
					window.location.href = ctx + "/home/terminal/termCenter.jsp?ccid=" + newId;
				} else {
					new LightFace.MessageBox().error("");
				}
			}
		}).post( {
			'oldId' : oldId,
			'ccid' : newId
		});
	},
	reSend : function(box, ccid) {
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=reSendActive',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info("激活码已发送，请重新激活");
				} else {
					new LightFace.MessageBox().error2("重发激活码失败，请重试");
				}
			}
		}).post( {
			'ccId' : ccid,
			'type' : 1
		});
	},
	maxText : function(str, len) {
		var newLength = 0;
		var newStr = "";
		var hasDot = true;
		var chineseRegex = /[^\x00-\xff]/g;
		var singleChar = "";
		if(!$chk(str)){str=''};
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
	createOpt : function(aid, opt) {
		var ccid = this.curryCcid;
		new Request.JSON( {
			url : ctx + '/html/dersireOpt/?m=createDO',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info("已创建待执行任务，请到<a style='color:#4682b4; text-decoration:underline;' href='"+ctx+"/home/terminal/usercenter_4.jsp'>任务管理器</a>进行操作");
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post( {
			'aid' : aid,
			'opttype' : opt,
			'ccid' : ccid,
			'cardNo' : ''
		});
	},
	/*
	 * obj:有常量的的对象, sysCardNo : 系统存储的卡号, aid:操作的AID opt:操作的类型
	 */
	exeCardDriver : function(obj,sysCardNo,aid,opt){//
		var nowCardNo = new JIM.CardDriver( {
			ctl : cardDriver,
			operations : []
		}).readCardNo();
		if (nowCardNo != sysCardNo) {
			new LightFace.MessageBox().error("操作终端与所选终端不符");
			return;
		}
		new JIM.CardDriver( {
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation :opt
			} ],
			onSuccess : function(response) {
						win.close();
						location.reload();
			}
		}).exec();
	}
});
