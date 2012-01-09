var FeeRuleSpace = FeeRuleSpace ? FeeRuleSpace : {};

FeeRuleSpace.manage = new Class({
	options : {},
	initialize : function() {
		this.drawGrid();
		this.drawMwBox();
	},
	drawMwBox : function() {
		this.mwBox = new LightFace( {
			draggable :true,
			width:600,
			initDraw : false,
			resetOnScroll: false,
			buttons : [ {
				title : '保 存',
				event : function() {
					this.form.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					var div = document.getElement('div[class=fc-tbx]');
					if ($chk(div)) {
						div.dispose();
					}
					this.close();
				}
			} ]
		});	
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/feerulespace/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.mwBox.options.title = '新增空间计费规则';
					this.mwBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.mwBox.options.content = $('mobileWalletDivAdd').get('html');
					this.mwBox.addEvent('open', this.openNewmw.bind(this));
					this.mwBox.open();
					this.mwBox.removeEvents('open');
				}.bind(this)
			}, 
			{
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的空间计费规则');
						return;
					}
					
					this.mwBox.options.title = '修改空间计费规则';
					this.mwBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.mwBox.options.content = $('mobileWalletDivEdit').get('html');
					this.mwBox.addEvent('open', this.openEditMw.bind(this));
					this.mwBox.open();
					this.mwBox.removeEvents('open');
				}.bind(this)
			 },
			 {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的空间计费规则');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/feerulespace/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													this.grid.barDiv.getElementById('spName').empty();
													this.grid.barDiv.getElementById('spName').fireEvent('load', ctx + '/html/feerulespace/?m=index');
												}
												this.grid.load();
											}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									id : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该空间计费规则删除吗？');
				}.bind(this)
			}
			],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用提供商',
				dataName : 'sp_name'
			},
			{
				title : '类型',
				dataName : 'type'
			},
			{
				title : '名称(应用或安全域)',
				dataName : 'appName'
			},
			{
				title : '计费模式',
				dataName : 'pattern'
			},
			{
				title : '计费单位(byte)',
				dataName : 'granularity'
			}, {
				title : '单价(元)', 
				dataName : 'uiPrice'
			}
			],
			searchButton : true,
			searchBar : {
				filters : [ 
				 {
					title : '应用提供商名称：',
					name : 'spName',
					id   : 'spName',
					type : 'text'
				 },
				 {
					title : '类型：',
					name  : 'type',
					id    : 'type',
					type  : 'select',
					data  : {
			            '' : '全部',
			            '1' : '应用',
			            '2' : '安全域'
		            }	
				 },
				 {
					title : '名称(应用或安全域)：',
					name : 'appName',
					id   : 'appName',
					type : 'text'
				} ]
			},
			headerText : '空间计费规则管理',
			headerImage : ctx + '/images/mobile_icon.png'
		});
	},
	openNewmw : function() {
		this.form = this.mwBox.messageBox.getElement('[name="addClientForm"]');
		this.form.set('action', ctx + '/html/feerulespace/?m=add');
		this.initEvent();
		this.addSpEvent();
		this.addTypeEvent();
		this.addPatternEvent();
		this.addValueChangeEvent();
		this.addValidate();
	},
	openEditMw : function() {
		var selectIds = this.grid.selectIds;
		this.form = this.mwBox.messageBox.getElement('[name="editClientForm"]');
		this.form.set('action', ctx + '/html/feerulespace/?m=edit');
		this.addValidate();
		new Request.JSON( {
			url : ctx + '/html/feerulespace/?m=getFeeRuleSpace&id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.mwBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
							input.set('value', data.message[input.get('name')]);
						});
					var patternDesc = this.mwBox.messageBox.getElement('[name="patternDesc"]');
					patternDesc.set('value',data.message['pattern']);
					var pattern = this.mwBox.messageBox.getElement('[name="pattern"]');
					pattern.set('value',data.message['patternOriginal']);
					var typeDesc = this.mwBox.messageBox.getElement('[name="typeDesc"]');
					typeDesc.set('value',data.message['type']);
					var type = this.mwBox.messageBox.getElement('[name="type"]');
					type.set('value',data.message['typeOriginal']);
					var dd_granularity = this.mwBox.messageBox.getElement('[title="dd_granularity"]');
					var granularity = this.mwBox.messageBox.getElement('[name="granularity"]');
					if(data.message['patternOriginal']==2){
						dd_granularity.setStyle('display','block');
					}else{
						granularity.set('value',1);
					}
				}
			    else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
		this.addValueChangeEvent();
	},
	initEvent : function() {
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
			var request = new Request.JSON({
				url : ctx+'/html/feerulespace/?m=getSpName',
				async : false,
				onSuccess : function(data) {
					spId.empty();
					var a = data.message;
					if(data.success) {
						spId.options.add(new Option("选择提供商",""));
						Array.each(a, function(item, index){
						spId.options.add(new Option(item[1],item[0]));	
						});
					}else{					
					}
				}
			});
			request.post();	
	},
	addPatternEvent : function(){
		var dd_granularity = this.mwBox.messageBox.getElement('[title="dd_granularity"]');
		var pattern = this.mwBox.messageBox.getElements('[name="pattern"]');
		var granularity = this.mwBox.messageBox.getElement('[name="granularity"]');
		pattern.addEvent('click', function() {
			if(pattern[1].checked){
				dd_granularity.setStyle('display','block');
				granularity.set('value','');
			}else if(pattern[0].checked){
				// 直接给应用定价模式
				dd_granularity.setStyle('display','none');
				granularity.set('value',1);
			}
		}.bind(this));
	},
	addSpEvent : function(){
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
		var type =  this.mwBox.messageBox.getElements('[name="type"]');
		spId.addEvent('change', function() {
			if(type[0].checked){
				this.getAppName(1);
			}else if(type[1].checked){
				this.getAppName(2);
			}
			
		}.bind(this));
	},
	addTypeEvent : function() {
		var type =  this.mwBox.messageBox.getElements('[name="type"]');
		type.addEvent('click',function(){
			if(type[0].checked){
			    this.getAppName(1);
		    }else if(type[1].checked){
		    	this.getAppName(2);
		    }
		}.bind(this));
	},
	getAppName : function(type){
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
		var aid = this.mwBox.messageBox.getElement('[name="aidName"]');
		var req = new Request.JSON({
			url : ctx+'/html/feerulespace/?m=getNameBySp',
			async : false,
			onSuccess : function(data) {
				aid.empty();
				var a = data.message;
				if(data.success) {
					aid.options.add(new Option("选择应用或安全域",""));
					Array.each(a, function(item, index){
					aid.options.add(new Option(item[1],item[0]));	
					});
				}else{
					
				}
			}
		});
		req.post('spId='+spId.get('value')+"&type="+type);
	},
	addValueChangeEvent: function(){
		var uiPrice = this.mwBox.messageBox.getElement('[name="uiPrice"]');
		var price = this.mwBox.messageBox.getElement('[name="price"]');
		uiPrice.addEvent('change',function(){
			price.set('value',Math.round(uiPrice.get('value')*100));
		}.bind(this));
		
	},
	addValidate : function() {
		new FormCheck(this.form, {
			submit : false,
			zIndex : this.mwBox.options.zIndex,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure:1
			},
			onValidateSuccess : function() {
				this.submitForm();
			}.bind(this)
		});
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.mwBox.close();
							this.grid.selectIds = [];
							this.grid.barDiv.getElementById('spName').empty();
							this.grid.barDiv.getElementById('spName').fireEvent('load', ctx + '/html/feerulespace/?m=index');
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.form.toQueryString());	
	}
});