var TermInfo = TermInfo ? TermInfo : {};

TermInfo = new Class({
	Implements : [ Events, Options ],
	options : {},
	initialize : function(options) {
		this.setOptions(options);
		this.ccid = this.getQueryValue("ccid");
		this.getConstants();
	},
	doinit : function() {
		var term = this;
		this.getTerminal(term);
		this.getAppList(term);
		this.getSDList(term);
		this.getSpaceCss(term);
	},
	getTerminal : function(term) {
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=getCusstomerCard",
			data : {
				'ccId' : term.ccid
			},
			async : false,
			onSuccess : function(responseText, responseXML) {
				if (responseText.success) {
					term.currtyTerm = responseText.result[0];
					var termDiv = $$(".mobileinfo");
					var termHtml = '';
					termHtml += '<dl>';
					termHtml += '<dt>' + '<img width="113" height="113" onerror="javascript:this.src=\'' + ctx + '/images/defTerim.jpg\''
							+ '\"  src="' + ctx + '/html/mobile/?m=getMobilePic&id=' + term.currtyTerm.mobileType_id + '" />' + '</dt>';
					termHtml += '<dd>名 称：' + term.currtyTerm.name + '<br />';
					termHtml += '号 码： ' + term.currtyTerm.mobileNo + '<br />';
					termHtml += '状 态： ' + term.currtyTerm.status + (term.currtyTerm.inBlackOriginal == 0 ? '' : '(在黑名单)') + '<br />';
					termHtml += '品 牌： ' + term.currtyTerm.mobileType_brandChs + '<br />';
					termHtml += '机 型： ' + term.currtyTerm.mobileType_type + '</dd>';
					termHtml += '</dl>';
					termDiv.set('html', termHtml);

					var status = term.currtyTerm.statusOriginal;
					var inBlack = term.currtyTerm.inBlackOriginal;
					var active = term.currtyTerm.activeOriginal;

					if (status == 1) {
						$("lost").setStyle("display", '');
						$("cancel").setStyle("display", '');
						if (inBlack == 1) {
							$("cancel").setStyle("display", 'none');
							$('tip').setStyle("display", '').set('html', '此终端已被列入黑名单，只能做挂失操作');
						}
						$('infoDetial').setStyle('display', '');
					} else if (status == 2) {
						$('infoDetial').setStyle('display', '');
						if (inBlack == 1) {
							$("cancel").setStyle("display", 'none');
							$('tip').setStyle("display", '').set('html', '此终端已被列入黑名单，不能对终端进行操作');
						}
					} else if (status == 3) {
						$('infoDetial').setStyle('display', '');
						if (inBlack != 1) {
							$("active").setStyle("display", '');
							$("cancel").setStyle("display", '');
						} else {
							$('tip').setStyle("display", '').set('html', '此终端已被列入黑名单，不能对终端进行操作');
						}
					}
				} else {
					new LightFace.MessageBox().error(responseText.message);
				}
			}
		}).get();
	},
	getAppList : function(term) {
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=listCardApp",
			data : {
				'ccId' : term.ccid
			},
			onSuccess : function(resp, responseXML) {
				if (resp.success) {
					var terStatus = term.currtyTerm.statusOriginal;
					var inblack = term.currtyTerm.inBlackOriginal;
					var apps = resp.result;
					var ulGood = new Element('ul');
					var ulBad = new Element('ul');
					$each(apps, function(item, i) {
						if (!item.showRule || (item.showRule && item.show)) {
							var li = new Element('li')
							var table = new Element('table', {
								'width' : "100%",
								'border' : "0"
							});
							li.grab(table);
							// 信息 TR
							var trAppinfo = new Element('tr');
							// 图像TD
							var tdImgApp = new Element('td', {
								'width' : '29%',
								'height' : 110,
								'align' : 'center',
								'vglign' : 'top'
							});
							tdImgApp.inject(trAppinfo);
							var pImgApp = new Element('p', {
								'class' : 'mobileappimg'
							});
							pImgApp.inject(tdImgApp);
							var imgApp = new Element('img', {
								'width' : 70,
								'height' : 70,
								'src' : ctx + '/html/application/?m=getAppPcImg&appId=' + item.appId,
								'onError' : 'javascript:this.src=\'' + ctx + '/images/defApp.jpg'
							});

							imgApp.inject(pImgApp);
							// 信息TD
							var tdInfo = new Element('td', {
								'width' : '71%'
							});
							tdInfo.inject(trAppinfo);
							var appInfoHtml = '';
							appInfoHtml += '名  称：<a href="' + ctx + '/home/app/appinfo.jsp?id=' + item.appId + '&cardAppId='
									+ item.cardAppId + '" class="b" title="' + item.appNames + '">' + term.maxText(item.appNames, 14)
									+ '</a><br />';
							if (item.hasNew) {
								appInfoHtml += '安装版本：<span class="c_bl">' + item.appVer
										+ '&nbsp;&nbsp;<a title="点击更新应用" href="javascript:termInfo.updateNewApp(\'' + item.aid
										+ '\');void(0);" id="newversion' + item.appId + '"><font color="red">有新版本</font></a></span><br />';
							} else {
								appInfoHtml += '安装版本：<span class="c_bl">' + item.appVer + '</span><br />';
							}
							appInfoHtml += '状  态：<span class="c_r">' + item.status + '</span><br />';
							if (item.useRam != -1 && item.useRom != -1) {
								appInfoHtml += '占用内存空间：<span class="c_bl">' + term.formatSize(item.useRam) + '</span><br />';
								appInfoHtml += '占用存储空间：<span class="c_bl">' + term.formatSize(item.useRom) + '</span>';
							}
							tdInfo.set('html', appInfoHtml);
							// 操作TR
							var trAppOpt = new Element('tr');
							var tdOpt1 = new Element('td', {
								'align' : 'center',
								'valign' : 'top'
							}).inject(trAppOpt);
							var tdOpt = new Element('td', {
								'valign' : 'top'
							}).inject(trAppOpt);

							table.grab(trAppinfo);
							table.grab(trAppOpt);
							// 状态控制
							var optHtml = '';
							if (item.statusOriginal != 1) {
								if ((item.statusOrg == 6 && item.presetMode != 3)
										|| (item.statusOrg == 4 && item.presetMode == 1 && item.delRule == 1) || item.statusOrg == 2
										|| item.statusOrg == 3 || item.statusOrg == 5 || item.statusOrg == 7 || item.statusOrg == 8) {
									if (terStatus == 1 || terStatus == 2) {
										optHtml += '<a class="bu4" href="javascript:termInfo.delApp(\'' + item.aid
												+ '\');void(0);">删 除</a>';
										if (item.statusOrg == 7 || item.statusOrg == 8) {
											optHtml += '<a class="bu4" href="javascript:termInfo.updateApp(\'' + item.aid
													+ '\');void(0);">数据更新</a>';
										}
										if (item.statusOrg == 8) {
											optHtml += '<a class="bu4" href="javascript:termInfo.lockApp(\'' + item.aid
													+ '\');void(0);">锁定</a>';
										}
										tdOpt.set('html', optHtml);
									}
									if (item.statusOrg == 7 || item.statusOrg == 8) {
										li.inject(ulGood);
									} else {
										li.inject(ulBad);
									}
								}
							}
						}
					});

					$('tabC1').set('html', '<ul>' + ulGood.get('html') + '</ul>');
					$('tabC2').set('html', '<ul>' + ulBad.get('html') + '</ul>');
					if (!$chk(ulGood.get('html'))) {
						$('tabC1').set('html', '<span>该终端上无可用的应用</span>');
					}
					if (!$chk(ulBad.get('html'))) {
						$('tabC2').set('html', '<span>该终端上无不可用的应用</span>');
					}
				}
			}
		}).post();
	},
	updateNewApp : function(aid) {
		var term = this;
		var rardNo = term.currtyTerm.card_cardNo;
		new LightFace.MessageBox({
			onClose : function() {
				if (this.result) {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (nowCardNo != rardNo) {
						new LightFace.MessageBox().error("操作终端与所选终端不符");
						return;
					}
					new JIM.CardDriver({
						ctl : cardDriver,
						operations : [ {
							aid : aid,
							operation : term.transConstant.UPDATE_APP
						} ],
						onSuccess : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
									location.reload();
								}
							}).info("更新执行成功");
						},
						onFailure : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
									location.reload();
								}
							}).error(response.status.options.statusDescription);
						}
					}).exec();
				}
			}
		}).confirm("您确认要进行升级吗？");
	},
	lockApp : function(aid) {
		var term = this;
		var rardNo = term.currtyTerm.card_cardNo;
		new LightFace({
			content : '请选择锁定方式',
			title : '提示',
			buttons : [ {
				title : '立即执行',
				event : function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (nowCardNo != rardNo) {
						new LightFace.MessageBox().error("操作终端与所选终端不符");
						return;
					}
					new JIM.CardDriver({
						ctl : cardDriver,
						operations : [ {
							aid : aid,
							operation : term.transConstant.LOCK_APP
						} ],
						onSuccess : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
									location.reload();
								}
							}).info("操作成功");
						},
						onFailure : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
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
					term.createOpt(aid, term.transConstant.LOCK_APP);
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
	updateApp : function(aid) {
		var term = this;
		var rardNo = term.currtyTerm.card_cardNo;
		new LightFace({
			content : '请选择数据更新方式',
			title : '提示',
			buttons : [ {
				title : '立即执行',
				event : function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (nowCardNo != rardNo) {
						new LightFace.MessageBox().error("操作终端与所选终端不符");
						return;
					}
					new JIM.CardDriver({
						ctl : cardDriver,
						operations : [ {
							aid : aid,
							operation : term.transConstant.PERSONALIZE_APP
						} ],
						onSuccess : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
									location.reload();
								}
							}).info("操作成功");
						},
						onFailure : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
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
					term.createOpt(aid, term.transConstant.PERSONALIZE_APP);
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
	delApp : function(aid) {
		var term = this;
		var rardNo = term.currtyTerm.card_cardNo;
		new LightFace({
			content : '请选择删除方式',
			title : '提示',
			buttons : [ {
				title : '立即执行',
				event : function() {
					var nowCardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (nowCardNo != rardNo) {
						new LightFace.MessageBox().error("操作终端与所选终端不符");
						return;
					}
					new JIM.CardDriver({
						ctl : cardDriver,
						operations : [ {
							aid : aid,
							operation : term.transConstant.DELETE_APP
						} ],
						onSuccess : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
									location.reload();
								}
							}).info("操作成功");
						},
						onFailure : function(response) {
							this.closeConnection();
							new LightFace.MessageBox({
								onClose : function() {
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
					term.createOpt(aid, term.transConstant.DELETE_APP);
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
	getSDList : function(term) {
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=listCardSD",
			data : {
				'ccId' : term.ccid
			},
			onSuccess : function(resp, responseXML) {
				if (resp.success) {
					var sds = resp.result;
					if (resp.result.length != 0) {
						var sdHtml = '';
						sdHtml += '<ul>';
						$each(sds,
								function(item, i) {
									sdHtml += '<li>';
									sdHtml += '名称：<span class="c_b" title="' + item.sdName + '">' + term.maxText(item.sdName, 26)
											+ '</span><br />';
									sdHtml += '状态：<span class="c_bl">' + item.status + '</span><br />';
									sdHtml += '空间模式：<span class="c_bl">' + item.spaceRule + '</span><br />';
									sdHtml += '删除规则：<span class="c_bl">' + item.deleteRule + '</span><br />';
									sdHtml += '已用内存空间：<span class="c_bl">' + term.formatSize(item.sdUsedRam) + '</span><br />';
									sdHtml += '已用存储空间：<span class="c_bl">' + term.formatSize(item.sdUsedRom) + '</span><br />';
									sdHtml += '剩余内存空间：<span class="c_bl">' + term.formatSize(item.sdAviliableRam) + '</span><br />';
									sdHtml += '剩余存储空间：<span class="c_bl">' + term.formatSize(item.sdAviliableRom) + '</span><br />';
									sdHtml += '</li>';
								});
						sdHtml += '</ul>';
						$$('.mobileyu').set('html', sdHtml);
					} else {
						$$('.mobileyu').set('html', '该终端上无安全域');
					}
				}
			}
		}).post();
	},
	getSpaceCss : function(term) {
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=getCardSzie",
			data : {
				'ccId' : term.ccid
			},
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					term.changeText("vsTotal", '内存空间：<br />' + term.formatSize(resp.result[0].totalSpace.ram));
					term.changeText("usedVS", '<img src="' + ctx + '/images/img_b.png" width="11" height="11" border="0" />' + '已用空间:'
							+ term.formatSize(resp.result[0].usedSpace.ram));
					term.changeText("existVS", '<img src="' + ctx + '/images/img_h.png" width="11" height="11" border="0" />' + '剩余空间:'
							+ term.formatSize(resp.result[0].existSpace.ram));
					term.changeText("nsTotal", '存储空间：<br />' + term.formatSize(resp.result[0].totalSpace.nvm));
					term.changeText("usedNS", '<img src="' + ctx + '/images/img_y.png" width="11" height="11" border="0" />' + '已用空间:'
							+ term.formatSize(resp.result[0].usedSpace.nvm));
					term.changeText("existNS", '<img src="' + ctx + '/images/img_h.png" width="11" height="11" border="0" />' + '剩余空间:'
							+ term.formatSize(resp.result[0].existSpace.nvm));
					if (resp.result[0].vsPercent != '0%') {
						$("c1").setStyle('width', resp.result[0].vsPercent);
					} else {
						$("c1").setStyle('display', 'none');
					}
					if (resp.result[0].nsPercent != '0%') {
						$("c2").setStyle('width', resp.result[0].nsPercent);
					} else {
						$("c2").setStyle('display', 'none');
					}
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
	},
	changeText : function(id, text) {
		var obj = $(id);
		obj.set("html", text);
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
	getQueryValue : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return "";
	},
	createOpt : function(aid, opt) {
		var ccid = this.ccid;
		new Request.JSON({
			url : ctx + '/html/dersireOpt/?m=createDO',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox().info("已创建待执行任务，请到<a style='color:#4682b4; text-decoration:underline;' href='" + ctx
							+ "/home/terminal/usercenter_4.jsp'>任务管理器</a>进行操作");
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post({
			'aid' : aid,
			'opttype' : opt,
			'ccid' : ccid,
			'cardNo' : ''
		});
	},
	maxText : function(str, len) {
		var newLength = 0;
		var newStr = "";
		var hasDot = true;
		var chineseRegex = /[^\x00-\xff]/g;
		var singleChar = "";
		if (!$chk(str)) {
			str = ''
		}
		;
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
	}
});