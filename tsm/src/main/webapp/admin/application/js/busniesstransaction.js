var Busniess = Busniess ? Busniess :{};

Busniess = new Class({

	options:{
		
	},
	initialize: function(){        
		this.drawUserBox();
		this.drawGrid();
	},
	drawUserBox : function() {
		this.userBox = new LightFace({
			draggable : true,
			initDraw : false,
			resetOnScroll : false,
			width : 600,
			height: parseInt(window.getSize().y)*0.85,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ 
			{
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
		this.logBox = new LightFace( {
			draggable : true,
			initDraw : false,
			width : 600,
			height : 400,
			zIndex : 9005,
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
			url : ctx + '/html/localtransaction/?m=index',
			multipleSelection : false,
			buttons : [ 
			{
				name : '查看详情',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.userBox.options.title = '业务操作日志';
					this.userBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.userBox.options.content = $('requistionDiv').get('html');
					this.userBox.addEvent('open', this.openEditApp.bind(this));
					this.userBox.open();
					this.userBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '查看指令日志',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.openShowCmd(selectIds);
				}.bind(this)
			}
			],
        	columnModel : [
        	{dataName : 'id', identity : true}
        	,{title : '手机号', dataName : 'mobileNo'}
        	,{title : 'SEID', dataName : 'cardNo'}
        	,{title : '应用/安全域/终端 名称', dataName : 'appName',order:false}
        	,{title : '操作类型', dataName : 'procedureName'}
        	,{title : '承载方式', dataName : 'commType'}
        	,{title : '执行开始时间', dataName : 'beginTime'}
        	,{title : '执行结束时间', dataName : 'endTime'}
        	,{title : '执行结果', dataName : 'result'}],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '手机号：',
					name : 'search_LIKES_mobileNo',
					id : 'mobileNo',
					type : 'text',
					validates : [ {
						regexp : 'number',
						message : '请输入正确的手机号码'
					}]
				}, {
					title : '会话ID：',
					name : 'search_LIKES_localSessionId',
					id : 'localSessionId',
					type : 'text',
					validates : [ {
						regexp : 'number',
						message : '请输入正确的会话ID'
					} ]
				} ]
			},
			headerText : '业务操作日志',
			headerImage : ctx + '/images/user_icon_32.png'
		});
	},
	openEditApp : function() {
		var selectIds = this.grid.selectIds;
		this.userForm = this.userBox.messageBox.getElement('form');
		this.userForm.set('action', ctx + '/html/requistion/?m=updatePublish');
		new Request.JSON( {
			url : ctx + '/html/localtransaction/?m=index',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.userBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.result[0][input.get('name')]);
					});
					//alert(data.message['showType']);
//					alert(this.userBox.messageBox.getElement('[id=showtype]'));
					if (data.result[0]['showType'] == 'termial'){
						this.userBox.messageBox.getElement('[id=showtype]').set('html','终端名称:');
					} else if (data.result[0]['showType'] == 'sd'){
						this.userBox.messageBox.getElement('[id=showtype]').set('html','安全域名称:');
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({search_EQL_id : selectIds[0]});
	},
	openShowCmd : function(selectIds) {
		var sessionId = null;
		new Request.JSON( {
			url : ctx + '/html/localtransaction/?m=get',
			async : false,
			onSuccess : function(data) {
				if (data.success) {
					sessionId = data.message.localSessionId;
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({id : selectIds[0]});
		this.winGrid = new JIM.UI.WinGrid({
			url : ctx + '/html/mLog/?m=indexApdus&search_EQS_sessionId=' + sessionId,
        	multipleSelection: false,
        	order : false,
        	width : 1000,
        	height : 380,
        	winButtons : [{
				title: '关闭',
				event: function() { this.close();}
			}],
			buttons : [ {
				name : '下发指令详情',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.winGrid.selectIds;
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
					var selectIds = this.winGrid.selectIds;
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
			}],
        	drawSearch : false,
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
			searchButton : false,
        	searchBar : {filters : []},
        	headerText : '指令日志'
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
		}).post({logId : this.winGrid.selectIds[0]});
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
		}).post({logId : this.winGrid.selectIds[0]});
	},
	addValidate : function() {
		new FormCheck(this.userForm, {
			submit : false,
			zIndex : this.userBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {//校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.userForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.userBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.userForm.toQueryString());
	}
});
