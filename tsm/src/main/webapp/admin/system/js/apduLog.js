var System = System ? System :{};

System.ApduLog = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawLogBox();
		this.drawGrid();
	},
	drawLogBox : function() {
		this.logBox = new LightFace( {
			draggable : true,
			initDraw : false,
			width : 600,
			height : 400,
			buttons : [{
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/mLog/?m=indexApdus',
			multipleSelection : false,
			buttons : [ {
				name : '下发指令详情',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					this.logBox.options.title = '下发指令详情';
					this.logBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.logBox.options.content = $('logResDiv').get('html');
					this.logBox.addEvent('open', this.openResultBox.bind(this));
					this.logBox.open();
					this.logBox.removeEvents('open');
				}.bind(this)
			},  {
				name : '卡片响应详情',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					this.logBox.options.title = '卡片响应详情';
					this.logBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.logBox.options.content = $('logReqDiv').get('html');
					this.logBox.addEvent('open', this.openParamBox.bind(this));
					this.logBox.open();
					this.logBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '清空',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
				new LightFace.MessageBox({onClose : function(result) {
					if (result) {
						var time = new Date().valueOf();
						new Request.JSON( {
							url : ctx + '/html/mLog/?m=remove',
							onSuccess : function(data) {
								if (data.success) {
									new LightFace.MessageBox().info(data.message);
								} else {
									new LightFace.MessageBox().error(data.message);
								}
								this.grid.load();
							}.bind(this)
						}).post( {t : time});
					}
				}.bind(this)}).confirm('您确定要删除所有的日志记录吗？');
				}.bind(this)
			}  ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '会话ID',
				dataName : 'sessionId'
			}, {
				title : '手机号码',
				dataName : 'customerCardInfo_mobileNo'
			}, {
				title : '应用名称',
				dataName : 'application_name'
			}, {
				title : '指令名称',
				dataName : 'apduName'
			}, {
				title : '卡片响应结果',
				dataName : 'cardResult'
			}, {
				title : '开始时间',
				dataName : 'startTime'
			}, {
				title : '结束时间',
				dataName : 'endTime'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '会话ID：',
					name : 'search_LIKES_sessionId',
					type : 'text'
				}]
			},
			headerText : '指令详细日志',
			headerImage : ctx + '/images/user_icon_32.png'
		});
	},
	openResultBox : function() {
		var dl = this.logBox.messageBox.getElement('dl[id=logResDl]');
		new Request.JSON( {
			url : ctx + '/html/mLog/?m=getResult',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = dl.getElements('input,textarea');
					$each(inputs, function(input, i) {
						var inputName = input.get('name');
						var index = inputName.indexOf('.');
						if (index != -1) {
							var parentName = inputName.substring(0, index);
							var fieldName = inputName.substring(index + 1, inputName.length);
							var parent = data.message[parentName];
							input.set('value', parent[fieldName]);
						} else {
							if ("apduList" == inputName) {
								var apdus = data.message.apduList.apdu;
								var buf = '';
								for ( var i = 0; i < apdus.length; i++) {
									var apdu = apdus[i];
									buf += apdu;
									buf += '\n';
								}
								input.set('value', buf);
							} else {
								input.set('value', data.message[inputName]);
							}
						}
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({logId : this.grid.selectIds[0]});
	},
	openParamBox : function() {
		var dl = this.logBox.messageBox.getElement('dl[id=logReqDl]');
		new Request.JSON( {
			url : ctx + '/html/mLog/?m=getParams',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = dl.getElements('input,textarea');
					$each(inputs, function(input, i) {
						var inputName = input.get('name');
						var index = inputName.indexOf('.');
						if (index != -1) {
							var parentName = inputName.substring(0, index);
							var fieldName = inputName.substring(index + 1, inputName.length);
							var parent = data.message[parentName];
							input.set('value', parent[fieldName]);
						} else {
							input.set('value', data.message[inputName]);
						}
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({logId : this.grid.selectIds[0]});
	}
});
