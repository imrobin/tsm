var User = User ? User :{};

User.menu = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawMenuBox();
		this.drawGrid();
	},
	drawMenuBox : function() {
		this.menuBox = new LightFace( {
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
				this.parentMenuId = null;
			}.bind(this),
			buttons : [ {
				title : '保 存',
				event : function() {
					this.menuForm.getElement('button').click();
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
			url : ctx + '/html/menu/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.menuBox.options.title = '新增菜单';
					this.menuBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.menuBox.options.content = $('adminMenuDiv').get('html');
					this.menuBox.addEvent('open', this.openNewMenu.bind(this));
					this.menuBox.open();
					this.menuBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的菜单');
						return;
					}
					this.menuBox.options.title = '修改菜单';
					this.menuBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.menuBox.options.content = $('adminMenuDiv').get('html');
					this.menuBox.addEvent('open', this.openEditMenu.bind(this));
					this.menuBox.open();
					this.menuBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的菜单');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/menu/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									menuId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该菜单删除吗？');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '菜单名',
				dataName : 'menuName'
			}, {
				title : '菜单等级',
				dataName : 'menuLevel'
			}, {
				title : '链接地址',
				dataName : 'url'
			}, {
				title : '显示顺序',
				dataName : 'orderNo'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '菜单名：',
					name : 'search_LIKES_menuName',
					type : 'text'
				} ]
			},
			headerText : '菜单管理',
			headerImage : ctx + '/admin/images/text_padding_left.png'
		});
	},
	addParentMenuSelect : function(level, value) {
		var select = this.menuForm.getElement('select[name=parent_id]');
		var img = new Element('img', {
			'src' : ctx + '/admin/images/ajax-loader.gif',
			'class' : 'icon16',
			width : 16, 
			height : 16
		}).inject(select, 'after');
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/menu/?m=index',
			onSuccess : function(data) {
				select.empty();
				if (data.success) {
					$each(data.result, function(result, i){
						select.options.add(new Option(result.menuName, result.id));
					}.bind(this));
					if ($chk(value)) {
						select.set('value', value);
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
				img.dispose();
			}.bind(this)
		}).get({search_EQI_menuLevel : level, search_NEL_id : this.grid.selectIds[0], page_orderBy : 'orderNo_asc', page_pageSize : 10000000, t : time});
	},
	changeMenuLevel : function(){
		var parentMenu = this.menuForm.getElement('select[name=parent_id]').getParent().getParent();
		var url = this.menuForm.getElement('input[id=menuUrl]');
		var menuUrl = url.getParent().getParent();
		var level = this.menuLevel.get('value');
		if (level == 1) {
			parentMenu.setStyle('display', 'none');
			menuUrl.setStyle('display', '');
			parentMenu.set('value', '');
		} else if (level == 2) {
			parentMenu.setStyle('display', '');
			this.addParentMenuSelect(1, this.parentMenuId);
			menuUrl.setStyle('display', 'none');
		} else if (level == 3) {
			parentMenu.setStyle('display', '');
			this.addParentMenuSelect(2, this.parentMenuId);
			menuUrl.setStyle('display', '');
		} else {
			alert('error');
		}
	},
	openNewMenu : function() {
		this.menuForm = this.menuBox.messageBox.getElement('form');
		this.menuForm.set('action', ctx + '/html/menu/?m=add');
		this.menuLevel = this.menuForm.getElement('select[name=menuLevel]');
		this.menuLevel.addEvent('change', this.changeMenuLevel.bind(this));
		this.addValidate();
	},
	openEditMenu : function() {
		var selectIds = this.grid.selectIds;
		this.menuForm = this.menuBox.messageBox.getElement('form');
		this.menuForm.set('action', ctx + '/html/menu/?m=update');
		this.menuLevel = this.menuForm.getElement('select[name=menuLevel]');
		this.menuLevel.addEvent('change', this.changeMenuLevel.bind(this));
		this.addValidate();
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/menu/?m=getMenu',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.menuBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.message[input.get('name')]);
					});
					//var parentLevel = this.menuLevel.get('value');
					this.parentMenuId = data.message.parent_id;
					this.changeMenuLevel();
					//this.addParentMenuSelect(parentLevel - 1, this.parentMenuId);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({t : time, menuId : selectIds[0]});
	},
	addValidate : function() {
		this.formCheck = new FormCheck(this.menuForm, {
			submit : false,
			zIndex : this.menuBox.options.zIndex,
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
		var url = this.menuForm.getElement('input[id=menuUrl]');
		var level = this.menuLevel.get('value');
		if (level != 2) {
			if (!$chk(url.get('value'))) {
				new LightFace.MessageBox().error("请输入URL地址.");
				return;
			}
		}
		new Request.JSON( {
			url : this.menuForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.menuBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.menuForm.toQueryString());
	}
});
