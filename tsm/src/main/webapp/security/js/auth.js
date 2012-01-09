var User = User ? User :{};

User.auth = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawAuthBox();
		this.drawGrid();
	},
	drawAuthBox : function() {
		this.authBox = new LightFace( {
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
					if (this.authBox.options.title == '链接地址管理') {
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
								}).confirm('您确定删除该权限管理的所有链接地址吗？');
							}
						}
					} else if (this.authBox.options.title == '菜单管理') {
						var nodes = this.tree.getChecked();
						var ids = [];
						$each(nodes, function(node, i) {
							ids.push(node.data.id);
						});
						this.submitSelectMenus(ids);
					} else {
						this.authForm.getElement('button').click();
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
			url : ctx + '/html/auth/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.authBox.options.title = '新增权限';
					this.authBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.authBox.options.content = $('authDiv').get('html');
					this.authBox.addEvent('open', this.openNewUser.bind(this));
					this.authBox.open();
					this.authBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的权限');
						return;
					}
					this.authBox.options.title = '修改权限';
					this.authBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.authBox.options.content = $('authDiv').get('html');
					this.authBox.addEvent('open', this.openEditUser.bind(this));
					this.authBox.open();
					this.authBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的权限');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/auth/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									authId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该权限删除吗？');
				}.bind(this)
			}, {
				name : '菜单管理',
				icon : ctx + '/admin/images/text_padding_left.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的权限');
						return;
					}
					this.authBox.options.title = '菜单管理';
					this.authBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.authBox.options.content = '<div id="tree_container" style="width: 450px;height: 350px"></div>';
					this.authBox.addEvent('open', this.openMenuTree.bind(this));
					this.authBox.open();
					this.authBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '链接地址管理',
				icon : ctx + '/admin/images/world_link.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的权限');
						return;
					}
					this.authBox.options.title = '链接地址管理';
					this.authBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.authBox.options.content = '<div id="authSelectDiv" style="width: 650px;height: 250px"></div>';
					this.authBox.open();
					this.authSelect = new JIM.UI.MultipleSelect(this.authBox.messageBox.getElement('div[id=authSelectDiv]'), {
						leftTitle : '未被加入管理的链接地址：',
						rightTitle : '已被管理的链接地址：',
						buttons : {
							up : false,
							down : false,
							top : false,
							bottom : false
						},
						store : {
							remote : true,
							rightUrl : ctx + '/html/auth/?m=getResByAuth&authId=' + this.grid.selectIds[0],
							leftUrl : ctx + '/html/auth/?m=getNotResByAuth&authId=' +  + this.grid.selectIds[0]
						}
					});
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '权限名',
				dataName : 'authName'
			}, {
				title : '权限描述',
				dataName : 'description'
			}, {
				title : '状态',
				dataName : 'status'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '权限名：',
					name : 'search_LIKES_authName',
					type : 'text'
				}, {
					title : '权限描述：',
					name : 'search_LIKES_description',
					type : 'text'
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
			headerText : '权限管理',
			headerImage : ctx + '/images/application_lightning.png'
		});
	},
	openNewUser : function() {
		this.authForm = this.authBox.messageBox.getElement('form');
		this.authForm.set('action', ctx + '/html/auth/?m=add');
		this.addValidate();
	},
	openEditUser : function() {
		var selectIds = this.grid.selectIds;
		this.authForm = this.authBox.messageBox.getElement('form');
		this.authForm.set('action', ctx + '/html/auth/?m=update');
		this.addValidate();
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/auth/?m=getAuth',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.authBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.message[input.get('name')]);
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({t : time, authId : selectIds[0]});
	},
	openMenuTree : function() {
		//IE 第二次根据id得不到对象
		//var container = this.authBox.messageBox.getElementById('tree_container');
		var container = this.authBox.messageBox.getElement('div');
		this.tree = new Mif.Tree( {
			container : container,
			forest : true,
			initialize : function() {
				this.initCheckbox('simple');
				new Mif.Tree.KeyNav(this);
			},
			types : {
				folder : {
					openIcon : 'mif-tree-open-icon',
					closeIcon : 'mif-tree-close-icon'
				},
				file : {
					openIcon : 'mif-tree-file-open-icon',
					closeIcon : 'mif-tree-file-close-icon'
				}
			},
			dfltType : 'folder',
			height : 18,
			onClickCheck : function(node) {
				this.checkChildren(node.getChildren(), 'checked');
				this.checkParent(node, 'checked');
			}.bind(this),
			onClickUnCheck : function(node) {
				this.checkChildren(node.getChildren(), 'unchecked');
				this.checkParent(node, 'unchecked');
			}.bind(this)
		});
		var json = [];
		var timestamp = (new Date()).valueOf();
		new Request.JSON( {
			url : ctx + '/html/menu/?m=getMenuTree',
			onComplete : function(data){
				if (data.success) {
					json.push(data.message);
					this.tree.load( {
						json : json
					});
				}
			}.bind(this)
		}).get({authId : this.grid.selectIds[0], t : timestamp});
	},
	addValidate : function() {
		this.formCheck = new FormCheck(this.authForm, {
			submit : false,
			zIndex : this.authBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {// 校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.authForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.authBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.authForm.toQueryString());
	},
	submitSelectAuths : function() {
		new Request.JSON( {
			url : ctx + '/html/auth/?m=selectRes',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.authBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({authId : this.grid.selectIds[0], resId : this.authSelect.getSelectedOption().toString()});
	},
	submitSelectMenus : function(menuIds) {
		new Request.JSON( {
			url : ctx + '/html/auth/?m=selectMenus',
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.authBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({authId : this.grid.selectIds[0], menuId : menuIds.toString()});
	},
	checkChildren : function(childrenNodes, checked) {
		if ($chk(childrenNodes) && childrenNodes.length > 0) {
			$each(childrenNodes, function(childNode, i){
				childNode.switchCheck(checked);
				this.checkChildren(childNode.getChildren(), checked);
			}.bind(this));
		}
	},
	checkParent : function (node, checked) {
		var parentNode = node.getParent();
		if ($chk(parentNode)) {
			var brothers = parentNode.getChildren();
			var brotherIsCheck = false;
			$each(brothers, function(brother, i){
				if (brother.state.checked == 'checked' && brother != node) {
					brotherIsCheck = true;
				}
			});
			if (!brotherIsCheck) {
				if(parentNode.property.hasCheckbox) {
					parentNode.switchCheck(checked);
				}
				this.checkParent(parentNode, checked);
			}
		}
	}
});
