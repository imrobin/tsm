var Encryptor = Encryptor ? Encryptor : {};

//管理页面
Encryptor.list = new Class({
	initialize : function() {
		this.headText = '加密机管理';
		this.url = ctx + '/html/encryptor/?m=list';
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
					this.box.options.title = '新增加密机';
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
					
					this.box.options.title = '修改加密机';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
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
					var url = ctx + '/html/encryptor/?m=delete';
					
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
				title : '类型',
				dataName : 'model', align : 'center', order : false
			}, {
				title : '厂商',
				dataName : 'vendor', align : 'center', order : false
			}, {
				title : '版本',
				dataName : 'version', align : 'center', order : false
			}, {
				title : '索引',
				dataName : 'index', align : 'center', order : false
			}],
			searchButton : true,
			searchBar : {
				filters : [{
					title : '类型:',
					name : 'search_EQI_model',
					type : 'select',
					data : {
						'' : '全部',
						1 : '安全域',
						2 : '应用'
					}
				}]
			},
			headerText : this.headText,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	openNewBox : function() {
		this.initVonderSelect();
		var action = ctx + '/html/encryptor/?m=add';
		if(this.formType == 'edit') {
			action = ctx + '/html/encryptor/?m=edit';
			
			new Request.JSON({
				url : ctx + '/html/encryptor/?m=get',
				onSuccess : function(result) {
					if(result.success) {
						var encryptor = result.message;

						this.box.messageBox.getElements('input[name=id]').set('value', encryptor.id);
						//select
						Array.each(this.box.messageBox.getElements('option'), function(item, index) {
							if(item.get('value') == encryptor.vendor) {
								this.box.messageBox.getElement(item).set('selected', 'selected');
							}
						}.bind(this));
						
						//radio
						Array.each(this.box.messageBox.getElements('input[type=radio]'), function(item, index) {
							if(item.get('value') == encryptor.model) {
								this.box.messageBox.getElement(item).set('checked', 'checked');
							}
						}.bind(this));
						
						this.box.messageBox.getElements('input[name=version]').set('value', encryptor.version);
						this.box.messageBox.getElements('input[name=index]').set('value', encryptor.index);
						this.box.messageBox.getElements('input[name=ciphertext]').set('value', encryptor.ciphertext);
					}
				}.bind(this)
			}).get({id : this.recordId});
			
		}
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', action);
		
		this.addValidate();
	},
	initVonderSelect : function() {
		new Request.JSON({
			async : false,
			url : ctx + "/html/commons/?m=exportEnum&enumName=com.justinmobile.tsm.cms2ac.security.scp02.EncryptorVendor&exportMethodName=export",
			onSuccess : function(json) {
				if (json.success) {
					transConstant = json.message;
					var jsonHash = new Hash(transConstant);
					var option = new Element('option').set('value','').set('text','请选择...');
					option.inject(this.box.messageBox.getElement('select[id=vendor]'));
					jsonHash.each(function(value, key) {
						option = new Element('option').set('value',value.value).set('text',value.name);
						option.inject(this.box.messageBox.getElement('select[id=vendor]'));
					}.bind(this));
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