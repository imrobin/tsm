App = {};
var hasAvailableVer = true;
var aid = "";
var commentBox;
var loginStatus = '';
var clickflag = true;
//var hasSubscribed =true;
App.Info = new Class({
	transConstant : null,
	appAid : '',

	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.options.url = '';
		this.getConstants();
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
	loadTypeApplication : function() {
		new Request.JSON({
			url : ctx + "/html/application/?m=getShowTypeApp" ,
			onSuccess : function(data){
				if(data.success) {
					$('appTypeList').empty();
					var result = data.message;
					var appTypeListHtml = '';
					result.forEach(function(item,index){
						if(index == 0){
							appTypeListHtml += '<div class="prlistinfo">';
						} else {
							appTypeListHtml += '<div class="prlistinfo ml13">';
						}
						appTypeListHtml += '<div class="infotitle"><img width="16" height="16" onerror="javascript:this.src=\'' + ctx + '/images/icon3.png\'' + '\"  src="' +  ctx + '/html/applicationType/?m=loadTypeLogo&id=' + item.typeId + '&t='+ new Date().getTime() +'" /></img><span title="' + item.typeName + '">' + maxText(item.typeName,8) + '</span></div>';
						appTypeListHtml += '<div class="pay"><dl>';
						var appList = item.appList;
						appList.forEach(function(app,appIndex){
							if(appIndex < 4) {
								var name = maxText(app.appName, 8);
								appTypeListHtml += '<dd><p><a title="' +app.appName + '" href="' + ctx + '/home/app/appinfo.jsp?id=' + app.appId +'"><img style="border:0px" width="58" height="58" onerror="javascript:this.src=\'' + ctx + '/images/defApp.jpg\'' + '\" src="' + ctx + '/html/application/?m=getAppPcImg&appId=' + app.appId + '"></img></a></p><p class="text1"><a title="' +app.appName + '" href="' + ctx + '/home/app/appinfo.jsp?id=' + app.appId +'">' + name + '</a></p></dd>';
							}
						});
						appTypeListHtml += '</dl></div>';
						appTypeListHtml += '</div>';
					});
					$('appTypeList').set('html',appTypeListHtml);
				}
			}
		}).post();
	},
	loadApplication : function() {
		var url = ctx + "/html/application/?m=index&search_ALIAS_spL_NEI_inBlack=1&search_EQI_status=1&search_EQL_id=" + id;
		if (cardAppId != null && cardAppId != '') {
			url += "&cardAppId=" + cardAppId;
		}
		var login = this;
		this.request = new Request.JSON({
			url : url,
			async : false,
			onSuccess : this.onComplete.bind(this)
		}).post();

	},
	onComplete : function(result) {
		result.result.forEach(function(e, index) {
			aid = e.aid;
			var name = e.name;
			if (e.name.length > 30) {
				$('name').set('class', 'title2720');
				name = name.substring(0, 30) + "<br>" + name.substring(30);
			}
			$('name').set(
					'html',
					"<span class='titlespan'>" + name + "<img src='" + ctx + "/images/s_" + e.avgCount
							+ ".png' width='96' height='16' /></span>" + "<span class='titlecon'>下载次数：<b>"
							+ (e.downloadCount == undefined ? '0' : e.downloadCount) + "</b></span>");
			var info = '';
			if (e.type == 'oldversion') {
				info = "版本号：" + e.versionNo + " (最新版本:&nbsp;" + e.lastestVersionNo + ")<br/>";
			} else {
				info = "版本号：" + e.versionNo + "<br/>";
			}
			info += "应用介绍：" + (e.description == undefined ? '暂无' : e.description) + " <br />应用类别："
					+ (e.childType_name == null ? '' : e.childType_name) + "<br />所在地：" + e.location +"</br>" +
							"大    小： 内存空间&nbsp;" + e.spaceRam + " 存储空间&nbsp;"
					+ e.spaceNvm + " <br />提 供 商： " + e.spName;
			if (e.type == 'oldversion') {
				info += " <br />" + "发布时间： " + (e.publishDate == null ? '' : e.publishDate) + "<br />";
				$('downloadP').setStyle('display', 'none');
			} else {
				info += " <br />" + "发布时间： " + (e.publishDate == null ? '' : e.publishDate) + "<br />更新时间： "
						+ (e.updateDate == null ? '' : e.updateDate) + "<br />	";
			}
			$('info').set('html', info);
			$('avgInTen').set('html', e.avgCountInTen);
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId=" + e.id;
			if (!e.hasIcon) {
				iconSrc = ctx + "/images/defApp.jpg";
			}
			$('appImg').set('html', "<img width='125' height='125' src='" + iconSrc + "' />");

		//	alert(e.hasAvailableVer);
			hasAvailableVer = e.hasAvailableVer;
			if (!e.hasAvailableVer){
				$('downloadApp').setStyle('display','none');
				$('downloadClient').setStyle('display','none');
				$('downloadP').set('html','无可用的下载版本');
				$('downloadP').set('class','apptextinfo2');
			}else{
				$('downloadApp').addEvent("click", function(event) {
					event.stop();
					var obj = this;
					if (loginStatus != 'login') {
						if (loginStatus == 'notlogin'){
							new LightFace.MessageBox().error2('您还没登录，请先<a style='+
			'"color:#ff0000; text-decoration:underline;" id="loginLink"	href="' + ctx+ '/security/login.jsp?from=/home/app/appinfo.jsp?id=' + 
			id + '">登录</a>');
						}else if (loginStatus == 'notcustomer'){
							new LightFace.MessageBox().error2('您不是普通用户，无法下载');
						}
						event.stop();
						return;
					}
				var cardNo = new JIM.CardDriver({
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					new Request.JSON({
						url : ctx + '/html/application/?m=getLocationMobileStatus',
						data : {
							cardNo : cardNo,
							appLocation : e.location
						},
						async : false,
						onSuccess : function(json) {
							var message = json.message;
						//	alert(json.success);
							if (json.success){
								if(message != ''){
									if (message == 'notInMobileSection'){
									new LightFace.MessageBox().error("该手机所在地未开通本项业务，无法下载");
									return false;
									}else{
	    								obj.appDownBox = new LightFace({
	    									content : "应用属于"+e.location+"，您的手机号属于"+message+"，可能无法使用该应用",
	    									title : '<img id="image" src="'+ctx+'/lib/lightface/assets/information.png" />&nbsp;提示',
	    									buttons : [ {
	    										title : '确认下载',
	    										event : function() {
			    								obj.appDownBox = new LightFace({
			    									content : '请选择下载方式',
			    									title : '<img id="image" src="'+ctx+'/lib/lightface/assets/information.png" />&nbsp;提示',
			    									buttons : [ {
			    										title : '立即执行',
			    										event : function() {
			    											obj.downloadApp(e.aid);
			    											this.close();
			    										},
			    										color : 'blue'
			    									}, {
			    										title : '创建任务',
			    										event : function() {
			    											obj.createOpt(cardNo,e.aid, obj.transConstant.DOWNLOAD_APP);
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
									}
								}else{
									obj.appDownBox = new LightFace({
										content : '请选择下载方式',
										title :'<img id="image" src="'+ctx+'/lib/lightface/assets/information.png" />&nbsp;提示',
										buttons : [ {
											title : '立即执行',
											event : function() {
												obj.downloadApp(e.aid);
//												$('downloadClient').click();
												this.close();
											},
											color : 'blue'
										}, {
											title : '创建任务',
											event : function() {
												obj.createOpt(cardNo,e.aid, obj.transConstant.DOWNLOAD_APP);
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
								}
							}else{
								new LightFace.MessageBox().error(message);
							}
						}.bind(this)
					// this.getEmmgratedTerminalCallback.bind(this, [ aid ])
					}).post();
				}.bind(this));
			
			}
		}.bind(this));
	},
	getEmmgratedTerminal : function(aid) {
		new Request.JSON({
			url : ctx + '/html/customerCard/?m=getByApplicationAidAndCurrentCustomerThatEmigrated',
			data : {
				aid : aid
			},
			onSuccess : function(json) {
				this.getEmmgratedTerminalCallback(json, aid);
			}.bind(this)
		// this.getEmmgratedTerminalCallback.bind(this, [ aid ])
		}).get();
	},
	getEmmgratedTerminalCallback : function(json, aid) {
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
							+ '\' " src="/tsm/html/mobile/?m=getMobilePic&id=' + item.mobileType_id + '" />' + '</p>'
							+ '<p class="user_m_text">' + '名称 : ' + this.maxText(phoneName, 14) + '<br />' + '号码 : ' + item.mobileNo
							+ '<br />' + '状态 : ';
					if (item.statusOriginal == 4) {
						liString += '<font color="red">' + item.status + '</font>';
					} else {
						liString += item.status;
					}
					liString += '(' + item.active + ')' + '<br/>' + '品牌: ' + item.mobileType_brandChs + '<br /> ' + '机型 : '
							+ this.maxText(item.mobileType_type, 14) + '<br /> ' + '<a class="buts m_t_5" title="选择' + item.id
							+ '" href="#">选择</a>' + '</p>' + '</div>';

					target.set("html", target.get("html") + liString);
				}.bind(this));
			}

			var html = target.get("html");
			var modal = new LightFace({
				title : "迁入",
				content : html,
				height : 350,
				width : 765,
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
					this.immgrateApp(arguments[0], arguments[1], modal);
				}.bind(this, [ aid, item.cardNo ]));
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
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
	immgrateApp : function(aid, orignalCardNo, modal) {
		var driver = new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : this.transConstant.IMMIGRATE_APP,
				originalCardNo : orignalCardNo
			} ]
		});
		
		modal.close();
		
		driver.exec();
	},
	createOpt : function(cardNo,aid, opt) {
	    new Request.JSON({
		url : ctx + '/html/customerCard/?m=getCustomerInfoByCardNo',
		onSuccess : function(data) {
			if(data.success){
			    var ccid = data.message.id;
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
        				'ccid' : ccid
        			});
    			}
		}
        	}).post({
        		'cardNo' : cardNo
        	});
	},
	recentlyDownLoad : function() {
		var url = ctx + "/html/application/?m=recentlyDownLoad&id=" + id + "&page_pageSize=6&isRecently=true";
		var login = this;
		this.request = new Request.JSON({
			url : url,
			onSuccess : this.onRecentlyDownLoadComplete.bind(this)
		}).post();
	},
	onRecentlyDownLoadComplete : function(result) {
		if (result.result.length == 0) {
			$('alluser').set('html', " <b>最近两周无人下载该应用!</b>");
		}
		result.result.forEach(function(e, index) {
			var iconSrc = ctx + '/images/defuser.jpg';
			if (e.hasIcon) {
				iconSrc = ctx + '/html/customer/?m=getCustomerPcImg&customerId=' + e.customerId;
			}
			new Element('dd').set(
					'html',
					'<img src="' + iconSrc
							+ '" width="78" height="78"/><br/><b style="width: 85px; word-wrap: break-word; overflow: hidden; ">'
							+ e.userName + '</b>').inject($('alluser'));
		});
	},
	recentlyDownLoadForIndex : function() {
		var url = ctx + "/html/application/?m=recentlyDownLoad&page_pageSize=5";
		var login = this;
		this.request = new Request.JSON({
			url : url,
			onSuccess : this.onRecentlyDownLoadForIndexComplete.bind(this)
		}).post();
	},
	onRecentlyDownLoadForIndexComplete : function(result) {
		result.result.forEach(function(e, index) {
			new Element('li').set(
					'html',
					'<span class="c_bl">' + e.userName + '</span> ' + e.subscribeTime + '订购过 <a class="b" href="' + ctx
							+ '/home/app/appinfo.jsp?id=' + e.appId + '">' + e.appName + '</a>').inject($('recentlyDown'));
		});
	},
	updateComment : function() {
		if (this.checkParams()) {
			var url = ctx + "/html/application/?m=updateComment";
			var login = this;
			this.request = new Request.JSON({
				url : url,
				onSuccess : this.onComment.bind(this)
			}).post(commentBox.messageBox.getElement("form").toQueryString());
		}else {
			clickflag = true;
		}
	},
	onComment : function(result) {
		if (result.success) {
			new LightFace.MessageBox({
				onClose : function() {
					window.location.reload();
				}.bind(this)
			}).info('评论成功');
		} else {
			new LightFace.MessageBox({
				onClose : function() {
					window.location.reload();
				}.bind(this)
			}).info('评论失败');
		}
	},
	loadComment : function() {
		var url = ctx + "/html/application/?m=loadComment&search_EQL_application.id=" + id + "&page_orderBy=commentTime_desc";
		new JIM.UI.Paging({
			url : url,
			limit : 4,
			head : {
				el : 'nextPageComment',
				showNumber : true,
				showText : false
			},
			onAfterLoad : this.onLoadComment.bind(this)
		}).load();
	},
	onLoadComment : function(result) {
		if (result.success){
			$('commentrecord').set('html', "");
			if (result.message == 'notlogin') {
				loginStatus = 'notlogin';
			} else if (result.message == 'notcustomer') {
				loginStatus = 'notcustomer';
			}else{
				loginStatus = 'login';
			}
			
			if (result.result.length == 0) {
				$('commentrecord').set('html', "<b>当前无任何评论!</b>");
			}
			result.result.forEach(function(e, index) {
				var content = e.content;
				if (content == undefined) {
					content = "";
				}
				var iconSrc = ctx + '/images/defuser.jpg';
				if (e.hasIcon) {
					iconSrc = ctx + '/html/customer/?m=getCustomerPcImg&customerId=' + e.customerId;
				}
				var commentHtml = "<div class='ly_img'><img src='" + iconSrc + "' width='40' height='40'/></div>" +
				"<div class='ly_info'><p><span style='float:left; display:block;color:#0066CC'>"+e.user_userName+"&nbsp&nbsp</span>"
				+"<span style='float:left; display:block;'>" +e.commentTime+
		" 说：</span><span style='float:right; display:block;'>"+
		(e.grade != undefined ? "评分：<img src='" + ctx + "/images/s_" + e.grade + ".png' width='60px' height='10px' />": '')
		+"</span></p><p class='ly_text'>"+HTMLEnCode(content)+"</p></div>";
				if (e.isCurrentUser == 'true'){
					commentHtml += "<div><span style='float:right; display:block;'>" +
							"<a href='javascript:void(0)' style='color:#4682b4; text-decoration:underline;' id='editComment" + e.id + "'>修改</a>" +
									"</span></div>";
				}
				new Element('div').set('class','ly').set('html',commentHtml).inject($('commentrecord'));
	
				if (e.isCurrentUser == 'true') {
					$('editComment' + e.id).addEvent('click', function() {
						editComment(e.id, e.content, e.grade);
					});
				}
			});
		} else {
			new LightFace.MessageBox().error(result.message);
		}
	},
	checkParams : function() {
		var result = true;
		if (commentBox.messageBox.getElement("[id='content']").get('value').trim() == '') {
			new LightFace.MessageBox().info('请输入评论');
			result = false;
		}
		if (commentBox.messageBox.getElement("[id='content']").get('value').length > 100) {
			new LightFace.MessageBox().error2('评论内容过长，长度应限制在100以内');
			result = false;
		}
		return result;
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

	downloadApp : function(aid) {
		var obj = this;
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : obj.transConstant.DOWNLOAD_APP
			} ],
			onSuccess : function(response) {
    			this.closeConnection();	
    			new LightFace.MessageBox({
    			    onClose : function(){
    					$('downloadClient').click();
    			    }
    			}).info("操作成功，点击确定后请下载客户端");
			}
		}).exec();
	},
	loadRecommandSp : function() {
		var url = ctx + "/html/spBaseInfo/?m=recommendSp&page_orderBy=orderNo_asc&page_pageSize=6"
				+ "&search_ALIAS_spL_EQI_status=1&search_ALIAS_spL_NEI_inBlack=1";
		var login = this;
		this.request = new Request.JSON({
			url : url,
			onSuccess : this.onRecommandSpComplete.bind(this)
		}).post();
	},
	onRecommandSpComplete : function(result) {
		result.result.forEach(function(e, index) {
			var imgSrc = (e.hasLogo) ? ('/html/spBaseInfo/?m=loadSpFirmLogo&id=' + e.sp_id) : ctx + '/images/defsp.jpg';
			var spDetailUrl = ctx + '/home/sp/spinfo.jsp?id=' + e.sp_id;
			new Element('li').set('html',
					"<a  href='" + spDetailUrl + "' title='" + e.sp_name + "'>" + "<img src='" + ctx + imgSrc + "'/></a>").inject(
					$('recommendSp'));
		});
	},
	loadByAppType : function(id) {
		this.request = new Request.JSON({
			url : ctx + "/html/application/?m=findByAppType&page_pageSize=4&type=-1",
			async : false,
			onSuccess : this.setCategoryId.bind(this)
		}).post();
		this.request = new Request.JSON({
			url : ctx + "/html/application/?m=findByAppType&page_pageSize=4&type=" + category1,
			onSuccess : this.onloadByAppTypeComplete.bind(this)
		}).post();
		this.request = new Request.JSON({
			url : ctx + "/html/application/?m=findByAppType&page_pageSize=4&type=" + category2,
			onSuccess : this.onloadByAppTypeComplete2.bind(this)
		}).post();
		this.request = new Request.JSON({
			url : ctx + "/html/application/?m=findByAppType&page_pageSize=4&type=" + category3,
			onSuccess : this.onloadByAppTypeComplete3.bind(this)
		}).post();
		this.request = new Request.JSON({
			url : ctx + "/html/application/?m=findByAppType&page_pageSize=4&type=" + category4,
			onSuccess : this.onloadByAppTypeComplete4.bind(this)
		}).post();
	},
	onloadByAppTypeComplete : function(result) {
		result.result.forEach(function(e, index) {
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId=" + e.id;
			var href = ctx + '/home/app/appinfo.jsp?id=' + e.id;
			if (!e.hasIcon) {
				iconSrc = ctx + "/images/defApp.jpg";
			}
			var name = DataLength(e.name, 8);
			new Element('dd').set(
					'html',
					"<p><a  href='" + href + "' title='" + e.name + "'><img width='59' style='border:0px' height='51'  src='" + iconSrc
							+ "'/></a></p>" + "<a  href='" + href + "' title='" + e.name + "'><p class='text1'>" + name + "</p></a>")
					.inject($('category1'));
		});
	},
	onloadByAppTypeComplete2 : function(result) {
		result.result.forEach(function(e, index) {
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId=" + e.id;
			var href = ctx + '/home/app/appinfo.jsp?id=' + e.id;
			if (!e.hasIcon) {
				iconSrc = ctx + "/images/defApp.jpg";
			}
			var name = DataLength(e.name, 8);
			new Element('dd').set(
					'html',
					"<p><a  href='" + href + "' title='" + e.name + "'><img width='59' style='border:0px' height='51'  src='" + iconSrc
							+ "'/></a></p>" + "<a  href='" + href + "' title='" + e.name + "'><p class='text1'>" + name + "</p></a>")
					.inject($('category2'));
		});
	},
	onloadByAppTypeComplete3 : function(result) {
		result.result.forEach(function(e, index) {
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId=" + e.id;
			var href = ctx + '/home/app/appinfo.jsp?id=' + e.id;
			if (!e.hasIcon) {
				iconSrc = ctx + "/images/defApp.jpg";
			}
			var name = DataLength(e.name, 8);
			new Element('dd').set(
					'html',
					"<p><a  href='" + href + "' title='" + e.name + "'><img width='59' style='border:0px' height='51'  src='" + iconSrc
							+ "'/></a></p>" + "<a  href='" + href + "' title='" + e.name + "'><p class='text1'>" + name + "</p></a>")
					.inject($('category3'));
		});
	},
	onloadByAppTypeComplete4 : function(result) {
		result.result.forEach(function(e, index) {
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId=" + e.id;
			var href = ctx + '/home/app/appinfo.jsp?id=' + e.id;
			if (!e.hasIcon) {
				iconSrc = ctx + "/images/defApp.jpg";
			}
			var name = DataLength(e.name, 8);
			new Element('dd').set(
					'html',
					"<p><a  href='" + href + "' title='" + e.name + "'><img width='59' style='border:0px' height='51'  src='" + iconSrc
							+ "'/></a></p>" + "<a  href='" + href + "' title='" + e.name + "'><p class='text1'>" + name + "</p></a>")
					.inject($('category4'));
		});
	},
	setCategoryId : function(result) {
		var ids = result.message.split(",");
		category1 = ids[0];
		category2 = ids[1];
		category3 = ids[2];
		category4 = ids[3];
	},
	initDownloadClient : function() {
		if (hasAvailableVer){
			$('downloadClient').addEvent(
					'click',
					function() {
						if (loginStatus != 'login') {
							if (loginStatus == 'notlogin'){
								new LightFace.MessageBox().error2('您还没登录，请先<a style='+
			'"color:#ff0000; text-decoration:underline;" id="loginLink"	href="' + ctx+ '/security/login.jsp?from=/home/app/appinfo.jsp?id=' + 
										id + '">登录</a>');
								return false;
							}else if (loginStatus == 'notcustomer'){
								new LightFace.MessageBox().error2('您不是普通用户，无法下载');
								return false;
							}
						} else {
							obj = this;
							var liString = "无手机终端信息";
							// var liString = '';
							new Request.JSON({
								url : ctx + "/html/customerCard/?m=index&status=1&aid=" + aid,
								async : false,
								onSuccess : function(responseText, responseXML) {
									if (responseText.success) {
										if (responseText.result.length > 0) {
											liString = "";
											var firstId = responseText.result[0].id;
											$each(responseText.result, function(item, index) {
												var phoneName = item.name == '' ? mobileType_brandChs : item.name;
												liString += '<div class="user_m_l_2" id="li' + item.id + '">' + '<p class="user_m_img">'
														+ '<img onerror="javascript:this.src=\'' + ctx + '/images/defTerim.jpg'
														+ '\' "  src="/tsm/html/mobile/?m=getMobilePic&id=' + item.mobileType_id + '" />'
														+ '</p>' + '<p class="user_m_text">' + '名称  : ' + DataLength(phoneName, 13) + '<br />'
														+ '号码  : ' + item.mobileNo + '<br />' + '状态 : ' + item.status + '(' + item.active + ')'
														+ '<br/>' + '品牌 : ' + item.mobileType_brandChs + '<br /> ' + '机型 : '
														+ DataLength(item.mobileType_type, 13) + '<br /> ';
//												''alert(item.clientStatusStr);
												if (item.clientStatusStr != '') {
													liString += ('<br/><b style="color:#ff0000">' + item.clientStatusStr + '</b></p></div>');
												} else {
												//	alert(item.clientAndroidUrl);
													if (item.clientAndroidUrl != '') {
														liString += '<a target="_blank" class="butswide m_t_5" href="' + 
														ctx+'/html/applicationClient/?m=downloadByHref&href='+encodeURIComponent(item.clientAndroidUrl)+'">Android</a>';
													}
													if (item.clientJ2MEUrl != '') {
														liString += '&nbsp;&nbsp;<a target="_blank" class="butswide m_t_5" href="' + 
															ctx+'/html/applicationClient/?m=downloadByHref&href='+encodeURIComponent(item.clientJ2MEUrl)+'">J2ME</a>';
													}
												}
												liString += '</p></div>';
											});
										}
									} else {
										new LightFace.MessageBox().error(responseText.message);
									}
								}
							}).send();
							// alert(liString);
							obj.appDownBox = new LightFace({
								content : '<div class="user_m_l_1" id="terminals" style="width:600px;">' + liString + '</div>',
								title : '选择客户端',
								buttons : [ {
									title : '关闭',
									event : function() {
										this.close();
									}
								} ]
							}).open();
						}
					}.bind(this));
		}

	},
	loadAppImg : function() {
		//获取应用截图
		new Request.JSON({
			url : ctx + "/html/application/?m=getImgIdByAppId",
			data : {
				applicationId : id
			},
			onSuccess : function(json) {
				if (json.success && json.message!='') {
					var ids= json.message.split(",");
					for (var i=0;i<ids.length;i++){
						new Element('li').set('html',
								"<img width='190' height='280' src='"+ctx + "/html/application/?m=getAppImg&appImgId=" + ids[i]+"'>")
								.inject($('appImgUl'));
					}
					$('appImgUl').setStyle('width',ids.length*235);
				}
			}
		}).get();
	},
	initCommentBox : function (){
		commentBox = new LightFace({
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ 			
				{
					title : '提交评论',
					event : function() {
					if (clickflag){
						clickflag = false;
						info.updateComment();
					}
						
						
					},
					color : 'blue'
				},
				{
					title : '关 闭',
					event : function() {
						this.close();
					}
				}
			]
		});
		$('letmetalk').addEvent('click', function(event) {
			
			if (loginStatus == 'notlogin'){
				new LightFace.MessageBox().error2('您还没登录，请先<a style='+
						'"color:#ff0000; text-decoration:underline;" id="loginLink"	href="' + ctx+ '/security/login.jsp?from=/home/app/appinfo.jsp?id=' + 
						id + '">登录</a>');
				return;
			}else if (loginStatus == 'notcustomer'){
				new LightFace.MessageBox().error2('您不是普通用户，无法评论');
				return;
			}
			var isComment = false;
			this.request = new Request.JSON({
				url : ctx + "/html/application/?m=isCommented&id=" + id,
				async : false,
				onSuccess : function(result) {
					if (result.message) {
						isComment = true;
					} 
				}.bind(this)
			}).post();
			if (isComment){
				new LightFace.MessageBox().error2('您已经评论过了，请不要重复评论');
				return;
			}
			commentBox.options.title = '我也说说';
			commentBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
			commentBox.options.content = $('commentFloatDiv').get('html');
			commentBox.addEvent('open', function(event) {
				this.request = new Request.JSON({
					url : ctx + "/html/application/?m=hasSubscribed&appId=" + id,
					async : false,
					onSuccess : function(result) {
						if (!result.message) {
							commentBox.messageBox.getElement("[id='stars1-input']").set('value', '-1');
							commentBox.messageBox.getElement("[id='starDiv']").setStyle('display','none');
						}
					}.bind(this)
				}).post();
			});
			commentBox.open();

			commentBox.removeEvents('open');
			initStar('');
		});
	}
});
function editComment(commentId, content, grade) {
//	$('content').set('value', content);
//	$('commentId').set('value', id);
//	$('oldGrade').set('value', grade);
	commentBox.options.title = '我也说说';
	commentBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
	commentBox.options.content = $('commentFloatDiv').get('html');
	commentBox.addEvent('open', function(event) {
		commentBox.messageBox.getElement("[id='content']").set('value',content);
		commentBox.messageBox.getElement("[id='commentId']").set('value',commentId);
		commentBox.messageBox.getElement("[id='oldGrade']").set('value',grade);
		commentBox.messageBox.getElement("[id='stars1-input']").set('value',grade);
		var gradeStr = '';
		if (grade == '1'){
			gradeStr = 'one';
		}else if (grade == '2'){
			gradeStr = 'two';
		}else if (grade == '3'){
			gradeStr = 'three';
		}else if (grade == '4'){
			gradeStr = 'four';
		}else if (grade == '5'){
			gradeStr = 'five';
		}
		if (grade == undefined){
			initStar('');
			this.request = new Request.JSON({
				url : ctx + "/html/application/?m=hasSubscribed&appId=" + id,
				async : false,
				onSuccess : function(result) {
					if (!result.message) {
						commentBox.messageBox.getElement("[id='stars1-input']").set('value', '-1');
						commentBox.messageBox.getElement("[id='starDiv']").setStyle('display','none');
					}
				}.bind(this)
			}).post();
		}else {
			initStar(grade);
			if (grade != 0){
				commentBox.messageBox.getElement("[id='star"+grade+"']").set('class',gradeStr+'-stars current-rating');
			}
		}

	});
	commentBox.open();
}

function maxText(str, len) {
	var newLength = 0;
	var newStr = "";
	var hasDot = true;
	var chineseRegex = /[^\x00-\xff]/g;
	var singleChar = "";
	if(!$chk(str)){str=''};
	var strLength = str.replace(chineseRegex, "**").length;
	for ( var i = 0; i < strLength; i++) {
		singleChar = str.charAt(i).toString();
		newLength += 2;
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



function HTMLEnCode(str) {
	var s = "";
	if (str.length == 0)
		return "";
	s = str.replace(/&/g, "&gt;");
	s = s.replace(/</g, "&lt;");
	s = s.replace(/>/g, "&gt;");
	s = s.replace(/ /g, "&nbsp;");
	s = s.replace(/\'/g, "&#39;");
	s = s.replace(/\"/g, "&quot;");
	s = s.replace(/\n/g, "<br>");
	return s;
}
function initStar(grade){
	/**
	 * 星星打分组件
	 *
	 * @author	Yunsd
	 * @date	2010-7-5
	 */
	var Stars = Class.create();
	Stars.prototype = {
		initialize : function(star, options) {
			this.SetOptions(options); //默认属性
			var flag = 999; //定义全局指针
			var isIE = (document.all) ? true : false; //IE?
			var starlist = commentBox.messageBox.getElement("[id='stars1']").getElementsByTagName('a'); //星星列表
			var input = commentBox.messageBox.getElement("[id='stars1-input']"); // 输出结果
			var tips = commentBox.messageBox.getElement("[id='stars1-tips']"); // 打印提示
			var nowClass = " " + this.options.nowClass; // 定义选中星星样式名
			var tipsTxt = this.options.tipsTxt; // 定义提示文案
			var len = starlist.length; //星星数量
			for (i = 0; i < len; i++) { // 绑定事件 点击 鼠标滑过
				starlist[i].value = i;
				starlist[i].onclick = function(e) {
					stopDefault(e);
					this.className = this.className + nowClass;
					flag = this.value;
					input.value = this.getAttribute("star:value");
					tips.innerHTML = tipsTxt[this.value];
					commentBox.messageBox.getElement("[id='stars1-input']").set('value',this.getAttribute("star:value"));
				}
				starlist[i].onmouseover = function() {
					if (grade == 1 || grade == 2 || grade == 3|| grade == 4|| grade == 5){
						commentBox.messageBox.getElement("[id='star"+grade+"']").removeClass('current-rating');
					}
					if (flag < 999) {
						var reg = RegExp(nowClass, "g");
						starlist[flag].className = starlist[flag].className.replace(reg, "")
					}
				}
				starlist[i].onmouseout = function() {
					if (input.value == 1 ||input.value == 2 ||input.value == 3||input.value == 4||input.value == 5){
						commentBox.messageBox.getElement("[id='star"+input.value+"']").addClass('current-rating');
					}
				}
			}
			;
			if (isIE) { //FIX IE下样式错误
				var li = commentBox.messageBox.getElement("[id='"+star+"']").getElementsByTagName('li');
				for ( var i = 0, len = li.length; i < len; i++) {
					var c = li[i];
					if (c) {
						c.className = c.getElementsByTagName('a')[0].className;
					}
				}
			}
		},
		//设置默认属性
		SetOptions : function(options) {
			this.options = {//默认值
				Input : "",//设置触保存分数的INPUT
				Tips : "",//设置提示文案容器
				nowClass : "current-rating",//选中的样式名
				tipsTxt : [ "1分-严重不合格", "2分-不合格", "3分-合格", "4分-优秀", "5分-完美" ]
			//提示文案
			};
			Extend(this.options, options || {});
		}
	}
	var Stars1 = new Stars("stars1");
}
