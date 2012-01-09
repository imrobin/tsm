var ApplicationService = ApplicationService ? ApplicationService : {};

ApplicationService.manage = new Class({
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
			url : ctx + '/html/applicationService/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.mwBox.options.title = '新增业务接口';
					this.mwBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.mwBox.options.content = $('mobileWalletDivAdd').get('html');
					this.mwBox.addEvent('open', this.openNewMw.bind(this));
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
						new LightFace.MessageBox().error('请先选择列表中的业务接口');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/applicationService/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													this.grid.barDiv.getElementById('appName').empty();
													this.grid.barDiv.getElementById('appName').fireEvent('load', ctx + '/html/applicationService/?m=index');
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
					}).confirm('确认要将该业务接口删除吗？');
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
				title : '业务名称', 
				dataName : 'serviceName'
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
			headerText : '业务接口管理',
			headerImage : ctx + '/images/mobile_icon.png'
		});
	},
	openNewMw : function() {
		this.form = this.mwBox.messageBox.getElement('[name="addClientForm"]');
		this.form.set('action', ctx + '/html/applicationService/?m=add');
		this.addValidate();
		this.initEvent();
		this.getFunCode(1);
		this.addTypeEvent();
		this.addSpEvent();
	},
	initEvent : function() {
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
			var request = new Request.JSON({
				url : ctx+'/html/applicationService/?m=getSpName',
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
	addSpEvent : function(){
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
		var type =  this.mwBox.messageBox.getElements("input[type='radio']");
		spId.addEvent('change', function() {
			if(type[0].checked){
				this.getAppName(1);
			}else if(type[1].checked){
				this.getAppName(2);
			}
			
		}.bind(this));
	},
	addTypeEvent : function() {
		var type =  this.mwBox.messageBox.getElements("input[type='radio']");
		type.addEvent('click',function(){
			if(type[0].checked){
			    this.getAppName(1);
			    this.getFunCode(1);
		    }else if(type[1].checked){
		    	this.getAppName(2);
		    	this.getFunCode(2);
		    }
		}.bind(this));
	},
    getFunCode: function(type){
    	var serviceName = this.mwBox.messageBox.getElement('[name="serviceName"]');
    	var req = new Request.JSON({
			url : ctx+'/html/applicationService/?m=getFunctionNoByType',
			async : false,
			onSuccess : function(data) {
				serviceName.empty();
				var a = data.message;
				if(data.success) {
					Array.each(a, function(item, index){
					serviceName.options.add(new Option(item.value,item.value));	
					});
				}else{
					
				}
			}
		});
		req.post('type='+type);
	},
	getAppName : function(type){
		var spId = this.mwBox.messageBox.getElement('[name="sp_id"]');
		var aid = this.mwBox.messageBox.getElement('[name="aidName"]');
		var req = new Request.JSON({
			url : ctx+'/html/applicationService/?m=getNameBySp',
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
							this.grid.barDiv.getElementById('appName').empty();
							this.grid.barDiv.getElementById('appName').fireEvent('load', ctx + '/html/applicationService/?m=index');
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