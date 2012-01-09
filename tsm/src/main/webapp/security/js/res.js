var User = User ? User :{};

User.res = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawResBox();
		this.drawGrid();
	},
	drawResBox : function() {
		this.resBox = new LightFace( {
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
					this.resForm.getElement('button').click();
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
			url : ctx + '/html/res/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.resBox.options.title = '新增链接资源';
					this.resBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.resBox.options.content = $('resDiv').get('html');
					this.resBox.addEvent('open', this.openNewResr.bind(this));
					this.resBox.open();
					this.resBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的链接资源');
						return;
					}
					this.resBox.options.title = '修改链接资源';
					this.resBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.resBox.options.content = $('resDiv').get('html');
					this.resBox.addEvent('open', this.openEditRes.bind(this));
					this.resBox.open();
					this.resBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的链接资源');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/res/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox({
												onClose : function() {
													new LightFace.MessageBox({
														onClose : function(result) {
															if (result) {
																new Request.JSON( {
																	url : ctx + '/html/res/?m=removeAuths',
																	onSuccess : function(data) {
																		if (data.success) {
																			new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
																		} else {
																			new LightFace.MessageBox().error(data.message);
																		}
																	}.bind(this)
																}).post({resId : this.grid.selectIds[0]});
															}
														}.bind(this)
													}).confirm('是否取消被管理的关联关系吗？');
												}.bind(this)
											}).error(data.message);
										}
									}.bind(this)
								}).post( {
									resId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该链接资源删除吗？');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '链接名称',
				dataName : 'resName'
			}, {
				title : '链接地址',
				dataName : 'filterString'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '链接名称：',
					name : 'search_LIKES_resName',
					type : 'text'
				}, {
					title : '链接地址：',
					name : 'search_LIKES_filterString',
					type : 'text'
				} ]
			},
			headerText : '链接资源管理',
			headerImage : ctx + '/admin/images/world_link.png'
		});
	},
	openNewResr : function() {
		this.resForm = this.resBox.messageBox.getElement('form');
		this.resForm.set('action', ctx + '/html/res/?m=add');
		this.addValidate();
	},
	openEditRes : function() {
		var selectIds = this.grid.selectIds;
		this.resForm = this.resBox.messageBox.getElement('form');
		this.resForm.set('action', ctx + '/html/res/?m=update');
		this.addValidate();
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/res/?m=getRes',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.resBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.message[input.get('name')]);
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({t : time, resId : selectIds[0]});
	},
	addValidate : function() {
		this.formCheck = new FormCheck(this.resForm, {
			submit : false,
			zIndex : this.resBox.options.zIndex,
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
			url : this.resForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.resBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.resForm.toQueryString());
	}
});
