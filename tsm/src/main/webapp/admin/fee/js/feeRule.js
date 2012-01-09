var FeeRule = FeeRule ? FeeRule : {};

FeeRule.manage = new Class({
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
			url : ctx + '/html/feerule/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.mwBox.options.title = '新增计费规则';
					this.mwBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.mwBox.options.content = $('mobileWalletDivAdd').get('html');
					this.mwBox.addEvent('open', this.openNewMw.bind(this));
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
						new LightFace.MessageBox().error('请先选择列表中的功能计费规则');
						return;
					}
					
					this.mwBox.options.title = '修改功能计费规则';
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
						new LightFace.MessageBox().error('请先选择列表中的计费规则');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/feerule/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													this.grid.barDiv.getElementById('spName').empty();
													this.grid.barDiv.getElementById('spName').fireEvent('load', ctx + '/html/feerule/?m=index');
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
					}).confirm('确认要将该计费规则删除吗？');
				}.bind(this)
			}
			
			],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用提供商',
				dataName : 'sp_name'
			}, {
				title : '计费类型',
				dataName : 'type'
			},
			{
				title : '计费模式',
				dataName : 'pattern'
			},
			{
				title : '粒度', 
				dataName : 'granularity'
			},
			{
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
				 } ]
			},
			headerText : '计费规则管理',
			headerImage : ctx + '/images/mobile_icon.png'
		});
	},
	openNewMw : function() {
		this.form = this.mwBox.messageBox.getElement('[name="addClientForm"]');
		this.form.set('action', ctx + '/html/feerule/?m=add');
		this.addValidate();
		this.initEvent();
		this.addTypeEvent();
		this.addPeriodEvent();
		this.addValueChangeEvent();
	},
	openEditMw : function() {
		var selectIds = this.grid.selectIds;
		this.form = this.mwBox.messageBox.getElement('[name="editClientForm"]');
		this.form.set('action', ctx + '/html/feerule/?m=edit');
		this.addValidate();
		this.addValueChangeEvent();
		new Request.JSON( {
			url : ctx + '/html/feerule/?m=getFeeRule&id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.mwBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
							input.set('value', data.message[input.get('name')]);
						});
					var granularity = this.mwBox.messageBox.getElement('[name="granularity"]');
					var typeDesc = this.mwBox.messageBox.getElement('[name="typeDesc"]');
					typeDesc.set('value',data.message['type']);
					var periodDesc = this.mwBox.messageBox.getElement('[name="periodDesc"]');
					periodDesc.set('value',data.message['period']);
					var type = data.message['typeOriginal'];
					var period = data.message['periodOriginal'];
					this.mwBox.messageBox.getElement('[name="type"]').set('value',type);
					this.mwBox.messageBox.getElement('[name="period"]').set('value',period);
					this.mwBox.messageBox.getElement('[name="price"]').set('value',data.message['uiPrice']);
					granularity.options.add(new Option(data.message['granularity'],data.message['granularity']));
					/*if(type==1){
						granularity.options.add(new Option("1K","1"));
					}else if(type==2){
						if(period==2){
							granularity.options.add(new Option("10000","10000"));
				    		granularity.options.add(new Option("20000","20000"));
				    		granularity.options.add(new Option("30000","30000"));
				    		granularity.options.add(new Option("40000","40000"));
				    		granularity.options.add(new Option("50000","50000"));
						}else if(period==1){
							granularity.options.add(new Option("1","1"));
						}
					}*/
				}
			    else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	initEvent : function() {
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
			var request = new Request.JSON({
				url : ctx+'/html/feerule/?m=getSpName',
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
	addPeriodEvent : function() {
		var period = this.mwBox.messageBox.getElement('[name="period"]');
		var granularity = this.mwBox.messageBox.getElement('[name="granularity"]');
		period.addEvent('change',function(){
			granularity.empty();
			if(period.get('value')==1){
				granularity.options.add(new Option("1","1"));
			}else if(period.get('value')==2){
				granularity.options.add(new Option("10000","10000"));
	    		granularity.options.add(new Option("20000","20000"));
	    		granularity.options.add(new Option("30000","30000"));
	    		granularity.options.add(new Option("40000","40000"));
	    		granularity.options.add(new Option("50000","50000"));
			}
		}.bind(this));
	},
	addTypeEvent:function(){
		var type = this.mwBox.messageBox.getElements("input[type='radio']");
		var period = this.mwBox.messageBox.getElement('[name="period"]');
		type.addEvent('click',function(){
			//空间计费
			period.empty();
			if(type[1].checked){
				period.options.add(new Option('包月','2'));
				this.getGranularity('space');
			}//功能计费
			else if(type[0].checked){
				period.options.add(new Option('包月','2'));
				period.options.add(new Option('按次','1'));
				this.getGranularity('fun');
			}
		}.bind(this));
	},
    getGranularity: function(type){
    	var granularity = this.mwBox.messageBox.getElement('[name="granularity"]');
        var period = this.mwBox.messageBox.getElement('[name="period"]');
    	granularity.empty();
    	if(type=='fun'){
    		granularity.options.add(new Option("10000","10000"));
    		granularity.options.add(new Option("20000","20000"));
    		granularity.options.add(new Option("30000","30000"));
    		granularity.options.add(new Option("40000","40000"));
    		granularity.options.add(new Option("50000","50000"));
    	}else if(type=='space'){
    		granularity.options.add(new Option("1","1"));
    	}
					
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
							this.grid.barDiv.getElementById('spName').fireEvent('load', ctx + '/html/feerule/?m=index');
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