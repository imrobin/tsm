var TermOpt = TermOpt ? TermOpt : {};

TermOpt = new Class({
	Implements : [ Events, Options ],
	options : {},
	initialize : function(options) {
		var obj = this;
		this.setOptions(options);
		this.ccid = this.getQueryValue("ccid");
		this.getConstants();
	},
	getQueryValue : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return "";
	},
	tipLostTermianl : function(ccid){
		var obj = this;
		new LightFace.MessageBox( {
			onClose : function() {
				if (this.result) {
					obj.lostTermianl(ccid);
				}
			}
		}).confirm("您确认要进行挂失吗？请慎重操作");
	},
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
					new LightFace.MessageBox().error(resp.message);
				}
			}
		}).get();
		return false;
	},
	addTermEvent : function(){
		var obj = this;
		var ccid = this.ccid;
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
	},
	// 注销终端
	tipCancelTermianl : function(ccid){
		var obj = this;
		new LightFace.MessageBox( {
			onClose : function() {
				if (this.result) {
					obj.cancelTermianl(ccid);
				}
			}
		}).confirm("您确认要进行注销吗？请慎重操作");
	},
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
					var status = cardInfo.card_status;
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
							location.href = ctx + '/home/terminal/termList.jsp';
						}
					}).info("注销成功");
				} else {
					new LightFace.MessageBox().error("请重试");
				}
			}
		}).get();
	},
	tipActiveTerminal : function(ccid){
		var obj = this;
		new LightFace.MessageBox( {
			onClose : function() {
				if (this.result) {
					obj.activeTermianl(ccid);
				}
			}
		}).confirm("您确认要激活终端吗？");
	},
	activeTermianl : function(ccid) {
		var box = new LightFace.MessageBox();
		box.loading('系统正在处理，请稍候');
		var obj = this;
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=reSendActive',
			onSuccess : function(data) {
				if (data.success) {
					box.close();
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
					box.close();
					new LightFace.MessageBox().error("重发激活码失败，请重试");
				}
			}
		}).post( {
			'ccId' : ccid,
			'type' : 1
		});
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
	active : function(box, ccid) {
		var activeCode = box.messageBox.getElement("[id=activeInput]").get('value');
		new Request.JSON( {
			url : ctx + '/html/customerCard/?m=activeCard',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							box.close();
							location.reload();
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
	getAllTerminal : function() {
		var obj = this;
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=index",
			async : false,
			onSuccess : function(responseText, responseXML) {
				if (responseText.success) {
					if (responseText.result.length > 0) {
						var liString = "";
						$each(responseText.result, function(item, index) {
							if(item.statusOriginal == 1 || item.statusOriginal == 2 || item.statusOriginal == 3 || item.statusOriginal == 4){
								var phoneName = item.name == '' ? mobileType_brandChs : item.name;
								liString += '<div class="zdlist"  onmouseover="this.className=\'zdlist1\'"  onmouseout="this.className=\'zdlist\'" id="zdlist">';
								liString += '<table>';
								liString += '<tr>';
								liString +=  '<td width="62" rowspan="2" align="center"> ' + '<img width="45" height="42" onerror="javascript:this.src=\'' + ctx + '/images/defTerim.jpg'+ '\'"  src="/tsm/html/mobile/?m=getMobilePic&id=' + item.mobileType_id + '" /></td>';
								liString += '<td width="69" align="center"><strong>名 称</strong></td>';
								liString += '<td width="102" align="center"><strong>号 码</strong></td>';
								liString += '<td width="104" align="center"><strong>状 态</strong></td>';
								liString += '<td width="74" align="center"><strong>品 牌</strong></td>';
								liString += '<td width="87" align="center"><strong>机 型</strong></td>';
								if(item.statusOriginal == 1){
									if(item.inBlackOriginal == 1){
										liString += '<td width="212" rowspan="2" align="left"><a href="' + ctx + '/home/terminal/termInfo.jsp?ccid=' + item.id+'" class="del">查看</a><a href="javascript:termOpt.tipLostTermianl(\'' + item.id+'\');void(0);" class="del">挂失</a></td>';
									}else{
										liString += '<td width="212" rowspan="2" align="left"><a href="' + ctx + '/home/terminal/termInfo.jsp?ccid=' + item.id+'" class="del">查看</a><a href="javascript:termOpt.tipCancelTermianl(\'' + item.id+'\');void(0);" class="del">注销</a><a href="javascript:termOpt.tipLostTermianl(\'' + item.id+'\');void(0);" class="del">挂失</a></td>';
									}
								} else if(item.statusOriginal == 2){
									liString += '<td width="212" rowspan="2" align="left"><a href="' + ctx + '/home/terminal/termInfo.jsp?ccid=' + item.id+'" class="del">查看</a></td>';
								} else if(item.statusOriginal == 3){
									if(item.inBlackOriginal != 1){
										liString += '<td width="212" rowspan="2" align="left"><a href="' + ctx + '/home/terminal/termInfo.jsp?ccid=' + item.id+'" class="del">查看</a><a href="javascript:termOpt.tipActiveTerminal(\'' + item.id+'\');void(0);" class="del">激活</a><a href="javascript:termOpt.tipCancelTermianl(\'' + item.id+'\');void(0);" class="del">注销</a></td>';
									}else{
										liString += '<td width="212" rowspan="2" align="left"><a href="' + ctx + '/home/terminal/termInfo.jsp?ccid=' + item.id+'" class="del">查看</a></td>';
									}
								}
								liString += '</tr>';
								liString += '<tr>';
								liString += '<td align="center"><span title="' + phoneName + '">' + obj.maxText(phoneName, 14) + '</span></td>';
								liString += '<td align="center">' + item.mobileNo + '</td>';
								liString += '<td align="center"> ' +  item.status + (item.inBlackOriginal == 0 ?'':'(在黑名单)') + '</td>';
								liString += '<td align="center">' + item.mobileType_brandChs + '</td>';
								liString += '<td align="center"><span title="' + item.mobileType_type + ' ">'+ obj.maxText(item.mobileType_type, 14) + '</span></td>';
								liString += '</tr>';
								liString += '</table>'; 
								liString += '</div>';
							}
							});
						$("zdlist").set("html", liString);
					} else {
						if (!$chk($("zdlist").get('html'))) {
							$('noinfo').setStyle('display','');
						}
					}
				} else {
					new LightFace.MessageBox().error(responseText.message);
				}
			}
		}).post();
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
	}
});