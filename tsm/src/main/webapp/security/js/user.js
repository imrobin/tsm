var User = User ? User :{};

User.user = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawUserBox();
		this.drawGrid();
	},
	drawUserBox : function() {
		this.userBox = new LightFace( {
			draggable : true,
			initDraw : false,
			width : 630,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '保 存',
				event : function() {
					if (this.userBox.options.title == '权限设置') {
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
								}).confirm('您确定删除该用户所有权限吗？');
							}
						}
					} else {
						this.userForm.getElement('button').click();
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
			url : ctx + '/html/user/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.userBox.options.title = '新增用户';
					this.userBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.userBox.options.content = $('userDiv').get('html');
					this.userBox.addEvent('open', this.openNewUser.bind(this));
					this.userBox.open();
					this.userBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的用户');
						return;
					}
					this.userBox.options.title = '修改用户';
					this.userBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.userBox.options.content = $('userDiv').get('html');
					this.userBox.addEvent('open', this.openEditUser.bind(this));
					this.userBox.open();
					this.userBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的用户');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/user/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									userId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该用户删除吗？');
				}.bind(this)
			}, {
				name : '权限设置',
				icon : ctx + '/images/application_lightning.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的用户');
						return;
					}
					this.userBox.options.title = '权限设置';
					this.userBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.userBox.options.content = '<div id="authSelectDiv" style="width: 650px;height: 250px"></div>';
					this.userBox.open();
					this.authSelect = new JIM.UI.MultipleSelect(this.userBox.messageBox.getElement('div[id=authSelectDiv]'), {
						leftTitle : '未赋予的权限：',
						rightTitle : '已被赋予的权限：',
						buttons : {
							up : false,
							down : false,
							top : false,
							bottom : false
						},
						store : {
							remote : true,
							rightUrl : ctx + '/html/user/?m=getAuthsByUserName&userId=' + this.grid.selectIds[0],
							leftUrl : ctx + '/html/user/?m=getNotAuthsByUserName&userId=' +  + this.grid.selectIds[0]
						}
					});
				}.bind(this)
			}, {
				name : '重置密码',
				icon : ctx + '/admin/images/group_key.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的用户');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/user/?m=resetPassword',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									userId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该用户密码重置吗？');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '用户名',
				dataName : 'userName'
			}, {
				title : '角色',
				dataName : 'sysRole_description'
			}, {
				title : '真实姓名',
				dataName : 'realName'
			}, {
				title : '手机号码',
				dataName : 'mobile'
			}, {
				title : '邮箱地址',
				dataName : 'email'
			}, {
				title : '状态',
				dataName : 'status'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '用户名：',
					name : 'search_LIKES_userName',
					type : 'text'
				}, {
					title : '手机号码：',
					name : 'search_LIKES_mobile',
					type : 'text',
					validates : [ {
						regexp : 'number',
						message : '请输入正确的手机号码'
					} ]
				}, {
					title : '状态：',
					name : 'search_EQI_status',
					type : 'select',
					data : {
						'' : '全部',
						1 : '有效',
						0 : '无效'
					}
				} ]
			},
			headerText : '用户管理',
			headerImage : ctx + '/images/user_icon_32.png'
		});
	},
	openNewUser : function() {
		var height = this.userBox.messageBox.getElement('div').getStyle('height');
		this.userBox.messageBox.getElement('div').getParent().setStyle('height', height);
		var password = this.userBox.messageBox.getElement('input[name=password]');
		password.set('value', '000000');
		password.getParent().getParent().setStyle('display', '');
		var status = this.userBox.messageBox.getElement('select[name=status]');
		status.getParent().getParent().setStyle('display', 'none');
		this.userForm = this.userBox.messageBox.getElement('form');
		this.userForm.set('action', ctx + '/html/user/?m=add');
		this.addValidate();
		this.addProvice();
		var operator = this.userBox.messageBox.getElement('select[name=roleName]');
		operator.addEvent('change', function(){
			var op = operator.get('value');
			var province = this.userBox.messageBox.getElement('select[name=province]');
			if (op == 'SUPER_OPERATOR') {
				province.set('disabled', 'disabled');
				province.getParent().getParent().setStyle('display' , 'none');
			} else {
				province.set('disabled', '');
				province.getParent().getParent().setStyle('display' , '');
			}
		}.bind(this));
	},
	openEditUser : function() {
		var height = this.userBox.messageBox.getElement('div').getStyle('height');
		this.userBox.messageBox.getElement('div').getParent().setStyle('height', height);
		var selectIds = this.grid.selectIds;
		var password = this.userBox.messageBox.getElement('input[name=password]');
		password.getParent().getParent().setStyle('display', 'none');
		var status = this.userBox.messageBox.getElement('select[name=status]');
		status.getParent().getParent().setStyle('display', '');
		this.userForm = this.userBox.messageBox.getElement('form');
		this.userForm.set('action', ctx + '/html/user/?m=edit');
		this.addValidate();
		this.addProvice();
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/role/?m=getRoleType&t=' + time,
			onSuccess : function(data) {
				if (data.success) {
					var roleSelect = this.userBox.messageBox.getElement('select[name=roleName]');
					if ($chk(data.message)) {
						roleSelect.empty();
						for ( var key in data.message) {
							roleSelect.options.add(new Option(data.message[key], key));
						}
					}
					new Request.JSON( {
						url : ctx + '/html/user/?m=getUser',
						onSuccess : function(data) {
							if (data.success) {
								var inputs = this.userBox.messageBox.getElements('input,select');
								$each(inputs, function(input, i) {
									var inputName = input.get('name');
									var value = data.message[inputName];
									if (inputName == 'roleName') {
										if (value == 'OPERATOR_CUSTOMER_SERVICE' || value == 'OPERATOR_AUDITOR') {
											input.empty();
											input.options.add(new Option('客服操作员', 'OPERATOR_CUSTOMER_SERVICE'));
											input.options.add(new Option('审核管理员', 'OPERATOR_AUDITOR'));
											input.options.add(new Option('超级管理员', 'SUPER_OPERATOR'));
										} else if (value == 'SUPER_OPERATOR') {
											input.empty();
											input.options.add(new Option('客服操作员', 'OPERATOR_CUSTOMER_SERVICE'));
											input.options.add(new Option('审核管理员', 'OPERATOR_AUDITOR'));
											input.options.add(new Option('超级管理员', 'SUPER_OPERATOR'));
											var province = this.userBox.messageBox.getElement('select[name=province]');
											province.set('disabled', 'disabled');
											province.getParent().getParent().setStyle('display' , 'none');
										} else {
											input.set('disabled', 'disabled');
											new Element('input', {'type' : 'hidden', 'value' : value, 'name' : input.get('name')}).inject(input, 'after');
											var province = this.userBox.messageBox.getElement('select[name=province]');
											province.set('disabled', 'disabled');
											province.getParent().getParent().setStyle('display' , 'none');
										}
										input.addEvent('change', function(){
											var op = input.get('value');
											var province = this.userBox.messageBox.getElement('select[name=province]');
											if (op == 'SUPER_OPERATOR') {
												province.set('disabled', 'disabled');
												province.getParent().getParent().setStyle('display' , 'none');
											} else {
												province.set('disabled', '');
												province.getParent().getParent().setStyle('display' , '');
												if (!$chk(province.get('value'))) {
													province.set('value', '全网');
												}
											}
										}.bind(this));
									}
									input.set('value', value);
								}.bind(this));
							} else {
								new LightFace.MessageBox().error(data.message);
							}
						}.bind(this)
					}).get({t : time, userId : selectIds[0]});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	addValidate : function() {
		this.formCheck = new FormCheck(this.userForm, {
			submit : false,
			zIndex : this.userBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			alerts : {number : '手机号由数字组成', length_str : '密码长度必须是 %0 - %1之间'},
			onValidateSuccess : function() {//校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	addProvice : function() {
		var pronvince = this.userBox.messageBox.getElement('select[name=province]');
		var options = pronvince.options;
		options.add(new Option("北京","北京"));
		options.add(new Option("天津","天津"));
		options.add(new Option("河北","河北"));
		options.add(new Option("山西","山西"));
		options.add(new Option("内蒙古","内蒙古"));
		options.add(new Option("辽宁","辽宁"));
		options.add(new Option("吉林","吉林"));
		options.add(new Option("黑龙江","黑龙江"));
		options.add(new Option("上海","上海"));
		options.add(new Option("江苏","江苏"));
		options.add(new Option("浙江","浙江"));
		options.add(new Option("安徽","安徽"));
		options.add(new Option("福建","福建"));
		options.add(new Option("江西","江西"));
		options.add(new Option("山东","山东"));
		options.add(new Option("河南","河南"));
		options.add(new Option("湖北","湖北"));
		options.add(new Option("湖南","湖南"));
		options.add(new Option("广东","广东"));
		options.add(new Option("广西","广西"));
		options.add(new Option("海南","海南"));
		options.add(new Option("重庆","重庆"));
		options.add(new Option("四川","四川"));
		options.add(new Option("云南","云南"));
		options.add(new Option("贵州","贵州"));
		options.add(new Option("西藏","西藏"));
		options.add(new Option("陕西","陕西"));
		options.add(new Option("甘肃","甘肃"));
		options.add(new Option("宁夏","宁夏"));
		options.add(new Option("青海","青海"));
		options.add(new Option("新疆","新疆"));
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
	},
	submitSelectAuths : function() {
		new Request.JSON( {
			url : ctx + '/html/user/?m=selectAuths',
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
		}).post({userId : this.grid.selectIds[0], authId : this.authSelect.getSelectedOption().toString()});
	}
});
