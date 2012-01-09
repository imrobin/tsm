var System = System ? System :{};

System.params = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawParamBox();
		this.drawGrid();
	},
	drawParamBox : function() {
		this.paramBox = new LightFace( {
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
				var type = this.grid.barDiv.getElementById('search_LIKES_type');
				type.empty();
				type.fireEvent('load', ctx + '/html/sysParams/?m=getAllType');
			}.bind(this),
			buttons : [ {
				title : '保 存',
				event : function() {
					this.paramForm.getElement('button').click();
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
			url : ctx + '/html/sysParams/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.paramBox.options.title = '新增参数';
					this.paramBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.paramBox.options.content = $('paramDiv').get('html');
					this.paramBox.addEvent('open', this.openNewUser.bind(this));
					this.paramBox.open();
					this.paramBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的参数');
						return;
					}
					this.paramBox.options.title = '修改参数';
					this.paramBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.paramBox.options.content = $('paramDiv').get('html');
					this.paramBox.addEvent('open', this.openEditUser.bind(this));
					this.paramBox.open();
					this.paramBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的参数');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/sysParams/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													var type = this.grid.barDiv.getElementById('search_LIKES_type');
													type.empty();
													type.fireEvent('load', ctx + '/html/sysParams/?m=getAllType');
												}
												this.grid.load();
												}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									paramId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该参数删除吗？');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '参数类型',
				dataName : 'type'
			}, {
				title : '参数名',
				dataName : 'key'
			}, {
				title : '参数值',
				dataName : 'value'
			}, {
				title : '描述',
				dataName : 'description'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '参数类型：',
					id : 'search_LIKES_type',
					name : 'search_LIKES_type',
					type : 'select',
					remote : true
				}, {
					title : '参数名：',
					name : 'search_LIKES_key',
					type : 'text'
				} ]
			},
			headerText : '数据字典管理',
			headerImage : ctx + '/images/user_icon_32.png'
		});
		this.grid.barDiv.getElementById('search_LIKES_type').fireEvent('load', ctx + '/html/sysParams/?m=getAllType');
	},
	openNewUser : function() {
		this.paramForm = this.paramBox.messageBox.getElement('form');
		this.paramForm.set('action', ctx + '/html/sysParams/?m=add');
		this.addAllType();
		this.addValidate();
	},
	openEditUser : function() {
		var selectIds = this.grid.selectIds;
		this.paramForm = this.paramBox.messageBox.getElement('form');
		this.paramForm.set('action', ctx + '/html/sysParams/?m=update');
		this.addValidate();
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/sysParams/?m=getParam',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.paramBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.message[input.get('name')]);
					});
					this.addAllType(data.message.type);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({t : time, paramId : selectIds[0]});
	},
	addValidate : function() {
		this.formcheck = new FormCheck(this.paramForm, {
			submit : false,
			zIndex : this.paramBox.options.zIndex,
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
	addAllType : function(typeValue) {
		var typeSelect = this.paramForm.getElement('select');
		var img = new Element('img', {
			'src' : ctx + '/admin/images/ajax-loader.gif',
			'class' : 'icon16',
			width : 16, 
			height : 16
		}).inject(typeSelect, 'after');
		window.setTimeout(function() {
			var time = new Date().valueOf();
			new Request.JSON( {
				url : ctx + '/html/sysParams/?m=getAllType',
				onSuccess : function(data) {
					if (data.success) {
						typeSelect.options.add(new Option('----请选择----', ''));
						$each(data.message, function(message, i) {
							typeSelect.options.add(new Option(message.value, message.key));
						}.bind(this));
						if ($chk(typeValue)) {
							typeSelect.set('value', typeValue);
						}
						typeSelect.getNext().dispose();
						var param = this;
						typeSelect.addEvent('change', function() {
							if (this.options[this.selectedIndex].text == '新类型') {
								
							} else {
								var newType = this.getParent().getElement('input[name=newType]');
								if ($chk(newType)) {
									newType.dispose();
									param.formcheck.dispose(newType);
								}
								if (this.options[this.options.length - 1].text == '新类型') {
									this.options.remove(this.options.length - 1);
								}
							}
						});
						var newType = new Element('a', {
							'href' : '#',
							'html' : '新类型'
						}).inject(typeSelect, 'after');
						newType.addEvent('click', function() {
							if (!$chk(newType.getParent().getElement('input[name=newType]'))) {
								var newField = new Element('input', {
									'name' : 'newType',
									'class' : "inputtext validate['required','%chckMaxLength']",
									maxlength : 32
								}).inject(newType, 'after');
								this.formcheck.register(newField, 1);
								var options = typeSelect.options;
								var hasNewType = false;
								for ( var i = 0; i < options.length; i++) {
									if (options[i].value == '新类型') {
										hasNewType = true;
									}
								}
								if (!hasNewType) {
									typeSelect.options.add(new Option('新类型', '新类型'));
								}
								typeSelect.set('value', '新类型');
							}
						}.bind(this));
					} else {
						new LightFace.MessageBox().error(data.message);
					}
				}.bind(this)
			}).get({t : time});
		}.bind(this), 1000);
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.paramForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.paramBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.paramForm.toQueryString());
	}
});
