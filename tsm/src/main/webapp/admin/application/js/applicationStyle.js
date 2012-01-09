var ApplicationStyle = ApplicationStyle ? ApplicationStyle : {};

//管理页面
ApplicationStyle.list = new Class({
	initialize : function() {
		this.headText = '应用样式管理';
		this.url = ctx + '/html/applicationStyle/?m=list';
		this.formType = '';
		this.recordId = '';
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
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.box.options.title = '新增应用样式';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.formType = 'add';
					this.box.options.content = $('add').get('html');
					this.box.addEvent('open', this.openNewBox.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					this.recordId = selectIds[0];
					
					this.box.options.title = '修改应用样式';
					this.box.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.formType = 'edit';
					this.box.options.content = $('add').get('html');
					this.box.addEvent('open', this.openNewBox.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					var id = selectIds[0];
					var url = ctx + '/html/applicationStyle/?m=delete';
					
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : url,
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {id : id});
							}
						}.bind(this)
					}).confirm('确认要将该记录删除吗？');
					
				}.bind(this)
			}],
			columnModel : [{
				dataName : 'id',
				identity : true
			}, {
				title : '应用名称',
				dataName : 'application_name', align : 'center', order : false
			}, {
				title : '样式地址',
				dataName : 'styleUrl', align : 'center', order : false
			}],
			searchButton : true,
			searchBar : {
				filters : [{
					title : '应用名称 : ',
					name : 'search_ALIAS_applicationL_LIKES_name',
					type : 'text'
				}]
			},
			headerText : this.headText,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	openNewBox : function() {
		this.initApplicationSelect();
		var action = ctx + '/html/applicationStyle/?m=add';
		
		if(this.formType == 'edit') {
			action = ctx + '/html/applicationStyle/?m=edit';
			
			new Request.JSON({
				url : ctx + '/html/applicationStyle/?m=get',
				onSuccess : function(result) {
					if(result.success) {
						var app = result.message;
						//select
						Array.each(this.box.messageBox.getElements('option'), function(item, index) {
							if(item.get('value') == app.application_id) {
								this.box.messageBox.getElement(item).set('selected', 'selected');
							}
						}.bind(this));
						this.box.messageBox.getElements('input[name=styleUrl]').set('value', app.styleUrl);
						this.box.messageBox.getElements('input[name=id]').set('value', app.id);
					}
				}.bind(this)
			}).get({id : this.recordId});
			
		}
		
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', action);
		
		this.addValidate();
	},
	initApplicationSelect : function() {
		new Request.JSON({
			async : false,
			url : ctx + "/html/application/?m=select",
			onSuccess : function(json) {
				if (json.success) {
					var option = new Element('option').set('value','').set('text','请选择...');
					option.inject(this.box.messageBox.getElement('select[id=applicationId]'));
					var apps = json.result;
					for(var index = 0; index < apps.length; index++) {
						var app = apps[index];
						option = new Element('option').set('value',app.id).set('text',app.name);
						option.inject(this.box.messageBox.getElement('select[id=applicationId]'));
					}
					
				}
			}.bind(this)
		}).get();
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
		}).post(this.form.toQueryString());
	}
});