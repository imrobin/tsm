var ApplicationIdentifier = ApplicationIdentifier ? ApplicationIdentifier : {};

//管理页面
ApplicationIdentifier.list = new Class({
	initialize : function() {
		this.url = ctx + '/html/aid/?m=list&search_EQI_status=1';
		this.aidType = 0;
		this.drawGrid();
		this.drawBox();
	},
	drawBox : function() {
		this.box = new LightFace( {
			width : 700,
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
					this.form.getElement('button').click();
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
			url : this.url,
			multipleSelection : false,
			buttons : [{
				name : '分配安全域AID',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.aidType = 1;
					this.box.options.title = '分配安全域AID';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
					
					this.box.options.content = $('addSd').get('html');
					this.box.addEvent('open', this.openNewBox.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}, {
				name : '分配应用AID',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.aidType = 2;
					this.box.options.title = '分配应用AID';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
					
					this.box.options.content = $('addApp').get('html');
					
					this.box.addEvent('open', this.openNewBox.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}/*
			, {
				name : '作废AID',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					var id = selectIds[0];
					new Request.JSON( {
						async : false,
						url   : ctx + '/html/aid/?m=zuofei',
						onSuccess : function(data) {
							if (data.success) {
								new LightFace.MessageBox( {
									onClose : function() {
										//this.box.close();
										this.grid.load();
										this.grid.selectIds = [];
									}.bind(this)
								}).info(data.message);
							} else {
								new LightFace.MessageBox().error(data.message);
							}
						}.bind(this)
					}).post({id : id});
					
				}.bind(this)
			}*/],
			columnModel : [{
				dataName : 'id',
				identity : true
			}, {
				title : '类型',
				dataName : 'type', align : 'center', order : false
			}, {
				title : '应用提供商',
				dataName : 'sp_name', align : 'center', order : false
			}, {
				title : 'AID',
				dataName : 'aid', align : 'center', order : false, isOmission : false
			}, {
				title : '分配时间',
				dataName : 'assignmentTime', align : 'center', order : false
			}, {
				title : '状态',
				dataName : 'status', align : 'center', order : false
			}],
			searchButton : true,
			searchBar : {
				filters : [/*{
					title : '状态:',
					name : 'search_EQI_status',
					type : 'select',
					data : {
						'' : '全部',
						1 : '正常',
						0 : '作废'
					}
				},*/{
					title : '类型:',
					name : 'search_EQI_type',
					type : 'select',
					data : {
						'' : '全部',
						1 : '安全域',
						2 : '应用'
					}
				}, {
					title : '应用提供商名称:',
					name : 'search_ALIAS_spL_LIKES_name',
					type : 'text'
				}, {
					title : 'AID:',
					name : 'search_EQS_aid',
					type : 'text'
				}]
			},
			headerText : 'AID分配',
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	openNewBox : function() {
		var action = ctx + '/html/aid/?m=generate';
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', action);
		
		this.initSpBaseInfoSelect();
		
		this.addValidate();
	},
	initSpBaseInfoSelect : function() {
		new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/?m=select',
			onSuccess : function(data) {
				if (data.success) {
					var select = this.box.messageBox.getElement('select[id=spSelect]');
					select.set('style','width:96%');
					var html = '';
					$each(data.message, function(e, index) {
						var option = '<option value="'+e.id+'">'+e.name+'</option>';
						html += option;
					});
					select.set('html', html);					
				}
			}.bind(this)
		}).post({status : 1, type : true});
	},
	addValidate : function() {
		new FormCheck(this.form, {
			submit : false,
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
		/**/
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.box.close();
							this.grid.load();
							this.grid.selectIds = [];
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.form.toQueryString()+'&type=' + this.aidType);
	}
});

//查询页面
ApplicationIdentifier.query = new Class({
	initialize : function() {
		this.url = ctx + '/html/aid/?m=list&search_EQI_status=1';
		this.drawGrid();
	},
	//drawBox : function() {},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : this.url,
			multipleSelection : false,
			//buttons : [{}, {}],
			columnModel : [{
				dataName : 'id',
				identity : true
			}, {
				title : '类型',
				dataName : 'type', align : 'center', order : false
			}, {
				title : '应用提供商',
				dataName : 'sp_name', align : 'center', order : false
			}, {
				title : 'AID',
				dataName : 'aid', align : 'center', order : false, isOmission : false
			}, {
				title : '分配时间',
				dataName : 'assignmentTime', align : 'center', order : false
			}, {
				title : '状态',
				dataName : 'status', align : 'center', order : false
			}],
			searchButton : true,
			searchBar : {
				filters : [{
					title : '应用提供商名称:',
					name : 'search_ALIAS_spL_LIKES_name',
					type : 'text'
				}, {
					title : 'AID:',
					name : 'search_EQS_aid',
					type : 'text'
				}]
			},
			headerText : 'AID查询',
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	}
});