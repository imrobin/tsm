var User = User ? User :{};

User.role = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawRoleBox();
		this.drawGrid();
	},
	drawRoleBox : function() {
		this.roleBox = new LightFace( {
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
					if (this.roleBox.options.title == '权限设置') {
						if ($chk(this.authSelect)) {
							var authIds = this.authSelect.getSelectedOption();
							if ($chk(authIds) && authIds.length > 0) {
								this.submitSelectAuths();
							} else {
								new LightFace.MessageBox( {
									onClose : function(result) {
										if (result) {
											this.submitSelectAuths();
										}
									}.bind(this)
								}).confirm('您确定删除该角色所有权限吗？');
							}
						}
					} else {
						this.roleForm.getElement('button').click();
					}
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
			url : ctx + '/html/role/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.roleBox.options.title = '新增角色';
					this.roleBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.roleBox.options.content = $('roleDiv').get('html');
					this.roleBox.addEvent('open', this.openNewUser.bind(this));
					this.roleBox.open();
					this.roleBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的角色');
						return;
					}
					this.roleBox.options.title = '修改角色';
					this.roleBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.roleBox.options.content = $('roleDiv').get('html');
					this.roleBox.addEvent('open', this.openEditUser.bind(this));
					this.roleBox.open();
					this.roleBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的角色');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/role/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									roleId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该角色删除吗？');
				}.bind(this)
			}, {
				name : '权限设置',
				icon : ctx + '/images/application_lightning.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的角色');
						return;
					}
					this.roleBox.options.title = '权限设置';
					this.roleBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.roleBox.options.content = '<div id="authSelectDiv" style="width: 650px;height: 250px"></div>';
					this.roleBox.open();
					this.authSelect = new JIM.UI.MultipleSelect(this.roleBox.messageBox.getElement('div[id=authSelectDiv]'), {
						leftTitle : '未拥有的权限：',
						rightTitle : '已拥有的权限：',
						buttons : {
							up : false,
							down : false,
							top : false,
							bottom : false
						},
						store : {
							remote : true,
							rightUrl : ctx + '/html/role/?m=getAuthsByRole&roleId=' + this.grid.selectIds[0],
							leftUrl : ctx + '/html/role/?m=getNotAuthsByRole&roleId=' +  + this.grid.selectIds[0]
						}
					});
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '角色名',
				dataName : 'roleName'
			}, {
				title : '描述',
				dataName : 'description'
			}, {
				title : '登录成功转向的页面',
				dataName : 'loginSuccessForward'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '角色名：',
					name : 'search_LIKES_roleName',
					type : 'text'
				}, {
					title : '描述：',
					name : 'search_LIKES_description',
					type : 'text'
				} ]
			},
			headerText : '角色管理',
			headerImage : ctx + '/images/user_suit.png'
		});
	},
	openNewUser : function() {
		this.roleForm = this.roleBox.messageBox.getElement('form');
		this.roleForm.set('action', ctx + '/html/role/?m=add');
		this.addValidate();
	},
	openEditUser : function() {
		var selectIds = this.grid.selectIds;
		this.roleForm = this.roleBox.messageBox.getElement('form');
		this.roleForm.set('action', ctx + '/html/role/?m=update');
		this.addValidate();
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/role/?m=getRole',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.roleBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.message[input.get('name')]);
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({t : time, roleId : selectIds[0]});
	},
	addValidate : function() {
		new FormCheck(this.roleForm, {
			submit : false,
			zIndex : this.roleBox.options.zIndex,
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
			url : this.roleForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.roleBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.roleForm.toQueryString());
	},
	submitSelectAuths : function() {
		new Request.JSON( {
			url : ctx + '/html/role/?m=selectAuths',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.roleBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({roleId : this.grid.selectIds[0], authId : this.authSelect.getSelectedOption().toString()});
	}
});
