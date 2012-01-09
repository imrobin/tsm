var User = User ? User :{};

User.logs = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawResBox();
		this.drawGrid();
	},
	drawResBox : function() {
		this.logsBox = new LightFace( {
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
					this.logsForm.getElement('button').click();
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
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/log/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '查看参数',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.openShowLogParam(selectIds);
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '操作名称',
				dataName : 'operateName'
			}, {
				title : '操作时间',
				dataName : 'time'
			}, {
				title : '操作员',
				dataName : 'loginName'
			}  ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '操作名称：',
					name : 'search_LIKES_operateName',
					type : 'text'
				}, {
					title : '操作员：',
					name : 'search_LIKES_loginName',
					type : 'text'
				} ]
			},
			headerText : '操作员日志查询',
			headerImage : ctx + '/admin/images/world_link.png'
		});
	},
	openShowLogParam : function(selectIds) {
		var logId = selectIds[0];
		this.winGrid = new JIM.UI.WinGrid({
			url : ctx + '/html/log/?m=indexParam&search_ALIAS_operateLogI_EQL_id=' + logId,
        	multipleSelection: false,
        	order : false,
        	width : 500,
        	selection : false,
        	height : 380,
        	winButtons : [{
				title: '关闭',
				event: function() { this.close();}
			}],
			buttons : [],
        	drawSearch : false,
        	columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '参数名',
				dataName : 'key'
			}, {
				title : '参数值',
				dataName : 'value'
			} ],
			searchButton : false,
        	searchBar : {filters : []},
        	headerText : '操作日志'
		});
	}
});
