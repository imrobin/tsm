PushSms = {};
PushSms = new Class({
	Implements : [Events,Options],
	options : {},
	initialize : function(options){
		this.setOptions(options);
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
	doQuery : function (ccid) {
		var ps = this;
		ps.ccid = ccid;
		$('downDiv').setStyle('display','');
		if(!$chk(ps.downGrid)) {
			ps.downGrid = new JIM.UI.Grid('downApp', {
				url :ctx +  '/html/application/?m=index&search_EQI_status=1&search_ALIAS_spL_NEI_inBlack=1&search_ALIAS_spL_EQI_status=1',
				multipleSelection : false,
				buttons : [ {
					name : '下载',
					icon : ctx + '/admin/images/down.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.getApplictionVer(ps.downGrid.selectIds[0]);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}],
				columnModel : [ {
					dataName : 'id',
					identity : true
				}, {
					title : '应用名',
					dataName : 'name'
				}, {
					title : '应用AID',
					dataName : 'aid'
				}, {
					title : '所属SP',
					dataName : 'spName'
				},  {title : '所属安全域', dataName : 'sd_sdName'},  {
					title : '应用类型',
					dataName : 'childType_name'
				} ],
				searchButton : true,
				searchBar : {
					filters : [ {
						title : '应用名：',
						name : 'search_LIKES_name',
						type : 'text',
						width : 150
					}, {
						title : '应用AID：',
						name : 'search_LIKES_aid',
						type : 'text',
						width : 150
					}, {
						title : '应用提供商：',
						name : 'search_ALIAS_spL_LIKES_name',
						type : 'text',
						width : 150
					} ]
				},
				headerText : '下载应用'
			});
		}
		if(!$chk(ps.optGrid)) {
			ps.optGrid = 	new JIM.UI.Grid('managerApp', {
				url : ctx + '/html/cardApp/?m=searchAppsForAdminByCustomerCardId&ccid=' +ccid ,
				multipleSelection : false,
				order : false,
				buttons : [ {
					name : '删除应用',
					icon : ctx + '/admin/images/delete.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.optCardApplication(ps.optGrid.selectIds[0], ps.transConstant.DELETE_APP);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '锁定应用',
					icon : ctx + '/admin/images/lock.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.optCardApplication(ps.optGrid.selectIds[0], ps.transConstant.LOCK_APP);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '解除锁定应用',
					icon : ctx + '/admin/images/unlock.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.optCardApplication(ps.optGrid.selectIds[0], ps.transConstant.UNLOCK_APP);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '个人化',
					icon : ctx + '/admin/images/down.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.optCardApplication(ps.optGrid.selectIds[0], ps.transConstant.PERSONALIZE_APP);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '升级应用',
					icon : ctx + '/admin/images/update.jpg',
					handler : function() {
						if (this.selectIds != '') {
							ps.optCardApplication(ps.optGrid.selectIds[0], ps.transConstant.UPDATE_APP);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				} ],
				columnModel : [ {
					dataName : 'id',
					identity : true
				}, {
					title : '所属终端',
					dataName : 'cciName'
				}, {
					title : '应用名',
					dataName : 'appName'
				}, {
					title : '已安装版本',
					dataName : 'appver'
				}, {
					title : '终端上的应用状态',
					dataName : 'appStatus'
				} ],
				searchButton : false,
				searchBar : {filters : []},
				headerText : '指定终端应用管理'
			});
		} else {
			this.options.url =  ctx + '/html/cardApp/?m=searchAppsForAdminByCustomerCardId&ccid=' +ccid ;
			ps.optGrid.load();
		}
		$('showDown').addEvent('click',function(event){
			event.stop();
			$('managerApp').setStyle('display', 'none');
			$('downApp').setStyle('display', '');
		});
		$('showOpt').addEvent('click',function(event){
			event.stop();
			$('downApp').setStyle('display', 'none');
			$('managerApp').setStyle('display','');
		});
	},
	optCardApplication : function (caid, operation) {
		var ps = this;
		new Request.JSON({
			url : ctx + '/html/cardApp/?m=optCardApplication',
			onSuccess : function(json) {
				if (json.success) {
					ps.optGrid.load();
					new LightFace.MessageBox().info("短信PUSH已经发送");
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			}
		}).post({
			'ccId' : ps.ccid,
			'caId' : caid,
			'opt' : operation
		});
	},
	downApplication : function(appId,ver,appVerList) {
		var ps = this;
		new Request.JSON({
			url : ctx + '/html/cardApp/?m=downApplication',
			onSuccess : function(json) {
				if (json.success) {
					appVerList.close();
					new LightFace.MessageBox().info("短信PUSH已经发送");
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			}
		}).post({
			'appId' : appId,
			'appVerId' : ver,
			'ccId' : ps.ccid
		});
	},
	getApplictionVer : function (appId) {
		var ps = this;
		new Request.JSON({
			url : ctx + '/html/appVer/?m=getByAppIdWithPublish',
			onSuccess : function(json) {
				if (json.success) {
					var appVerDiv = $('appVerDiv').get('html');
					var appVerList = new LightFace({
						draggable : true,
						initDraw : true,
						title : '选择应用版本',
						content : appVerDiv,
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
					//处理SELECT
					var appVerSel = appVerList.messageBox.getElement('[id="appverSel"]');
					var autoOption = new Option('自动','');
					appVerSel.options.add(autoOption);
					Array.each(json.message,function(item,index){
						var temp = new Option(item.avName,item.avId);
						appVerSel.options.add(temp);
					});
					var appVer = '';
					appVerSel.addEvent('change',function(){
						appVer = this.value;
					});
					//处理确定按钮
					var goDown = appVerList.messageBox.getElement('[id="goDown"]');
					goDown.addEvent('click',function(event){
						event.stop();
						ps.downApplication(appId,appVer,appVerList);
					});
					appVerList.open();
					
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			}
		}).post({
			'appId' : appId
		});
		
	}
});
