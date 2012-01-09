var AppList = AppList ? AppList : {};

AppList = new Class({
	Implements : [ Events, Options ],
	options : {},
	initialize : function(options) {
		this.setOptions(options);
		this.getConstants();
	},
	getAllApplist : function() {
		var appList = this;
		appList.table = new Table();
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getAllAppListByUser",
			onSuccess : function(resp) {
				if (resp.success) {
					var ul = $('appList');
					if(resp.message.length > 0) {
						ul.set('html','');
						var html = '';
						$each(resp.message, function(item, i) {
							html += '<li onmouseover="this.className=\'bgover\'"  onmouseout="this.className=\'bgout\'">';
							html += '<a title="' + item.name + '" href="cardAppDetail.jsp?appId=' + item.id +'"><img border="0" width="100" height="100" onerror="javascript:this.src=\'' + ctx + '/images/defApp.jpg\'' + '\"  src="' + 	ctx + '/html/application/?m=getAppPcImg&appId=' + item.id + '" />';
							html += '<p>' + appList.maxText(item.name,14) + '</p></a></li>';
							appList.table.set(item.id,item);
						});
						ul.set('html',html);
					}else{
						$('tip1').setStyle('display','none');
						$('tip2').setStyle('display','');
					}
				} else {
					new LightFace.MessageBox().error("请重试");
				}
			}
		}).post();
	},
	queryDetail : function(id){
		var appId = this.getQueryValue("appId");
		new Request.JSON({
			url : ctx + "/html/application/?m=getApplication&appId=" + appId,
			onSuccess : function(appMessage) {
				var appInfo = appMessage.message;
				new Request.JSON( {
					url : ctx + "/html/customerCard/?m=getCardApplicationByUserAndAppId&appId=" + appId,
					onSuccess : function(resp) {
						if (resp.success) {
							//图片
							var appImg = '';
							appImg += '<img width="113" height="113" onerror="javascript:this.src=\'' + ctx + '/images/defApp.jpg\'' + '\"  src="' + 	ctx + '/html/application/?m=getAppPcImg&appId=' + appInfo.id + '" />';
							$('tdImage').set('html', appImg);
							//应用信息
							var appInfoHtml = '';
							appInfoHtml += '应 用 名： ' + appInfo.name  +'<br />';
							appInfoHtml +=  '应用介绍： ' + (appInfo.description == undefined ?'':appInfo.description) + ' <br />';
							appInfoHtml +=  '应用类别： ' + (appInfo.childType_name == undefined ?'无分类': appInfo.childType_name) +'<br />';
							appInfoHtml += '所 在 地： ' + appInfo.location  +'<br />';
							appInfoHtml += '提 供 商： ' + ' <a target="_blank" href="' + ctx + '/home/sp/spinfo.jsp?id=' + appInfo.sp_id + '" class="c_b">' + appInfo.sp_name + '</a> <br />';
							$('infoTd').set('html', appInfoHtml);
							if(resp.message.length > 0){
								var cciInfo = '';
								$each(resp.message,function(item,index){
									cciInfo += '<tr>';
									cciInfo += '<td align="center">' + item.appver + '</td>';
									cciInfo += '<td align="center">' + item.appStatus + '</td>';
									cciInfo += '<td align="center">' + item.cciName + '</td>';
									cciInfo += '<td align="center">' + '<a class="zdb"   href="' + ctx + '/home/terminal/termInfo.jsp?ccid=' + item.cciId + '">进入终端管理</a>' + '</td>';
									cciInfo += '</tr>';
								});
								$('customerCardList').set('html', cciInfo);
								$('cciList').setStyle('display', '');
							}
						} else {
							new LightFace.MessageBox().error("请重试");
						}
					}
				}).post();
			}
		}).post();
	},
	delApp : function(id,aid) {
		var term = this;
		var appInfo = this.table.get(id);
		var rardNo = appInfo.cciCardNo;
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
							aid : aid,
							operation : term.transConstant.DELETE_APP
						} ],
						onSuccess : function(response) {
						    this.closeConnection();
				    			new LightFace.MessageBox({
				    			    onClose : function(){
				    					location.reload();
				    			    }
				    			}).info("操作成功");
				},
				onFailure : function(response){
				    this.closeConnection();
				    new LightFace.MessageBox({
					 onClose : function(){
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
					term.createOpt(id,aid, term.transConstant.DELETE_APP);
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
	lockApp :function(id,aid) {
		var term = this;
		var appInfo = this.table.get(id);
		var rardNo = appInfo.cciCardNo;
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
							aid : aid,
							operation : term.transConstant.LOCK_APP
						} ],
						onSuccess : function(response) {
						    this.closeConnection();	
						    new LightFace.MessageBox({
				    			    onClose : function(){
				    				location.reload();
				    			    }
				    			}).info("操作成功");
						},
						onFailure : function(response){
						    this.closeConnection();
						    new LightFace.MessageBox({
							 onClose : function(){
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
					term.createOpt(id,aid, term.transConstant.LOCK_APP);
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
	updateApp :function(id,aid) {
		var term = this;
		var appInfo = this.table.get(id);
		var rardNo = appInfo.cciCardNo;
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
							aid : aid,
							operation : term.transConstant.PERSONALIZE_APP
						} ],
						onSuccess : function(response) {
				    			this.closeConnection();	
				    			new LightFace.MessageBox({
				    			    onClose : function(){
								location.reload();
				    			    }
				    			}).info("操作成功");
				},
				onFailure : function(response){
				    this.closeConnection();
				    new LightFace.MessageBox({
					 onClose : function(){
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
					term.createOpt(id,aid, term.transConstant.PERSONALIZE_APP);
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
	getQueryValue : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return "";
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
	createOpt : function(id,aid, opt) {
		var appInfo = this.table.get(id);
		var ccid = appInfo.cciId;
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
	}
});