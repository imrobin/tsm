PushSms = {};
PushSms = new Class({
	Implements : [Events,Options],
	options : {},
	initialize : function(options){
		this.setOptions(options);
	},
	doQuery : function (ccid) {
		var ps = this;
		ps.ccid = ccid;
		$('downDiv').setStyle('display','');
		if(!$chk(downGrid)) {
			var downGrid = new JIM.UI.Grid('downApp', {
				url :ctx +  '/html/application/?m=index&search_EQI_status=1&search_ALIAS_spL_NEI_inBlack=1&search_ALIAS_spL_EQI_status=1',
				multipleSelection : false,
				buttons : [ {
					name : '下载',
					icon : ctx + '/admin/images/down.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.downApp(this.selectIds);
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
		var optGrid;
		if(!$chk(optGrid)) {
			optGrid = 	new JIM.UI.Grid('managerApp', {
				url : ctx + '/html/cardApp/?m=searchAppsForAdminByCustomerCardId&ccid=' +ccid ,
				multipleSelection : false,
				order : false,
				buttons : [ {
					name : '删除应用',
					icon : ctx + '/admin/images/delete.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.delApp(this.selectIds, this);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '锁定应用',
					icon : ctx + '/admin/images/lock.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.lockApp(this.selectIds, this);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '解除锁定应用',
					icon : ctx + '/admin/images/unlock.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.unlockApp(this.selectIds, this);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '个人化',
					icon : ctx + '/admin/images/down.png',
					handler : function() {
						if (this.selectIds != '') {
							ps.personalApp(this.selectIds, this);
						} else {
							new LightFace.MessageBox().error("请先选择一条记录");
						}
					}
				}, {
					name : '升级应用',
					icon : ctx + '/admin/images/update.jpg',
					handler : function() {
						if (this.selectIds != '') {
							ps.updateApp(this.selectIds, this);
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
			optGrid.load();
		}
		$('showDown').addEvent('click',function(e){
			e.stop();
			$('managerApp').setStyle('display', 'none');
			$('downApp').setStyle('display', '');
		});
		$('showOpt').addEvent('click',function(e){
			e.stop();
			$('downApp').setStyle('display', 'none');
			$('managerApp').setStyle('display','');
		});
	},
	delApp : function (caId, ps) {
		
	}
});
