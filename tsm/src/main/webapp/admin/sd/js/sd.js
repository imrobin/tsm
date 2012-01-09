var SecurityDomain = SecurityDomain ? SecurityDomain : {};
var deleteSelf = -1;
var sdStatus = 1;
SecurityDomain.list = new Class({
	options : {},
	initialize : function() {
		this.keyProfile = '';
		this.gridHsmkeyConfig = {};
		this.encSelectIds = [];
		this.macSelectIds = [];
		this.dekSelectIds = [];
		this.drawGrid();
		this.drawSpBox();
		this.initHsmkeyConfigSelect();
	},
	drawSpBox : function() {
		this.hsmkeyConfigBox = new LightFace({
			width : 500,
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

					if(this.keyProfile == 'enc') {
						this.encSelectIds = this.gridHsmkeyConfig.selectIds;
						this.box.messageBox.getElement('input[name=hsmkeyConfigENC]').set('value', this.encSelectIds.toString());
					} else if(this.keyProfile == 'mac') {
						this.macSelectIds = this.gridHsmkeyConfig.selectIds;
						this.box.messageBox.getElement('input[name=hsmkeyConfigMAC]').set('value', this.macSelectIds.toString());
					} else if(this.keyProfile == 'dek') {
						this.dekSelectIds = this.gridHsmkeyConfig.selectIds;
						this.box.messageBox.getElement('input[name=hsmkeyConfigDEK]').set('value', this.dekSelectIds.toString());
					}
					
					this.hsmkeyConfigBox.close();
				}.bind(this),
				color : 'blue'
			} ]
		
		});
		
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
					if($chk(this.form.getElement('[id=deleteRule]'))) {
						if (deleteSelf == 0){
							if (this.form.getElement('[id=deleteRule]').get('value') == 2){
								new LightFace.MessageBox().error('安全域的删除规则必须与安装参数的配置保持一致');
								return false;
							}
						}else if (deleteSelf == 1){
							if (this.form.getElement('[id=deleteRule]').get('value') != 2){
								new LightFace.MessageBox().error('安全域的删除规则必须与安装参数的配置保持一致');
								return false;
							}
						}
					}
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
		
		this.infoBox = new LightFace( {
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
		
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/securityDomain/?m=list',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.box.options.title = '新增安全域';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
					
					this.box.options.content = $('sdDivAdd').get('html');
					
					this.box.addEvent('open', this.openNewSd.bind(this));
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
					var sdId = selectIds[0];
					var sd = {};
					new Request.JSON({
						async : false,
						url : ctx + '/html/securityDomain/?m=getSd',
						onSuccess : function(result) {
							if(result.success) {
								sd = result.message;
							}
						}.bind(this)
					}).post({
						sdId : sdId, t : new Date().getTime()
					});
					
					if(sd.status == 3) {
						new LightFace.MessageBox().error('当前记录已经归档');
						return;
					}
					
					if(sd.hasLock == 'y') {
						new LightFace.MessageBox().error('当前记录正在审核中，审核员处理完毕后，方可再修改');
						return;
					}
					
					this.box.options.title = '修改安全域';
					this.box.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.box.options.content = $('sdDiv').get('html');
					this.box.addEvent('open', this.openEditSd.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}, 
			{
				name : '归档',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					
					var sdId = selectIds[0];
					
					if(sdId < 0) {
						new LightFace.MessageBox().error('只有发布状态的安全域才能进行归档操作');
					} else {
						
						var sd = {};
						new Request.JSON({
							async : false,
							url : ctx + '/html/securityDomain/?m=getSd',
							onSuccess : function(result) {
								if(result.success) {
									sd = result.message;
								}
							}.bind(this)
						}).post({
							sdId : sdId, t : new Date().getTime()
						});
						
						if(sd.status == 3) {
							new LightFace.MessageBox().error('当前记录已经归档');
							return;
						}
						
						if(sd.hasLock == 'y') {
							new LightFace.MessageBox().error('当前记录正在审核中，审核员处理完毕后，方可再归档');
							return;
						} 
						
						new LightFace.MessageBox( {
							onClose : function(result, msg) {
								if (result) {
									new Request.JSON({
										url : ctx + '/html/securityDomain/?m=archiveSdApplyForAdmin',
										onSuccess : function(result) {
											if(result.success) {
												new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(result.message);
											} else {
												new LightFace.MessageBox().error(result.message);
											}
										}.bind(this)
									}).post({sdId : sdId ,reason : msg});
								}
							}.bind(this)
						}).prompt('请填写安全域归档原因：', 200, true);
						
					}
					
				}.bind(this)
			}, {
				name : '升级密钥版本',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					
					var sdId = selectIds[0];
					
					if(sdId < 0) {
						new LightFace.MessageBox().error('只有发布状态的安全域才能进行升级密钥版本操作');
					} else {
						
						var sd = {};
						new Request.JSON({
							async : false,
							url : ctx + '/html/securityDomain/?m=getSd',
							onSuccess : function(result) {
								if(result.success) {
									sd = result.message;
								}
							}.bind(this)
						}).post({
							sdId : sdId, t : new Date().getTime()
						});
						
						if(sd.status == 3) {
							new LightFace.MessageBox().error('只有发布状态的安全域才能进行升级密钥版本操作');
							return;
						}
						
						this.box.options.title = '升级密钥版本';
						this.box.options.titleImage = ctx + '/admin/images/page_white_edit.png';
						this.box.options.content = $('sdDivKeyVersion').get('html');
						this.box.addEvent('open', this.openEditSdKeyVersion.bind(this));
						this.box.open();
						this.box.removeEvents('open');
					}
					
				}.bind(this)
			}, {
				name : '配置加密机',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					
					var sdId = selectIds[0];
					
					this.box.options.title = '配置加密机';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
					
					this.box.options.content = $('keyProfileConfigDiv').get('html');
					
					this.box.addEvent('open', this.openOldSd.bind(this));
					this.box.open();
					this.box.removeEvents('open');
					
				}.bind(this)
			}, {
				name : '撤销申请',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					var sdId = selectIds[0];
					var url = '';
					if(sdId < 0) {
						url = ctx + '/html/securityDomain/?m=remove';
					} else {
						url = ctx + '/html/securityDomain/?m=cancelSdApply';
					}
					
					if(sdId > 0) {
						var sd = {};
						new Request.JSON({
							async : false,
							url : ctx + '/html/securityDomain/?m=getSd',
							onSuccess : function(result) {
								if(result.success) {
									sd = result.message;
								}
							}.bind(this)
						}).post({
							sdId : sdId, t : new Date().getTime()
						});
						
						if(sd.status == 3) {
							new LightFace.MessageBox().error('当前记录已经归档');
							return;
						}
						
						if(sd.hasLock == 'n') {
							new LightFace.MessageBox().error('当前没有记录正在审核中');
							return;
						}
						sdId = sd.sdApplyId;
					}
					
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
								}).post( {sdApplyId : sdId, t : new Date().getTime()});
							}
						}.bind(this)
					}).confirm('确认要将该记录撤销吗？');
					
				}.bind(this)
			}, {
				name : '查看详情',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					this.infoBox.options.title = '查看安全域信息';
					this.infoBox.options.titleImage = ctx + '/admin/images/test.png';
					this.infoBox.options.content = $('sdDivInfo').get('html');
					this.infoBox.addEvent('open', this.openInfoSd.bind(this));
					this.infoBox.open();
					this.infoBox.removeEvents('open');
				}.bind(this)
			
			}, {
				name : '审核信息',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					
					this.infoBox.options.title = '查看安全域审核信息';
					this.infoBox.options.titleImage = ctx + '/admin/images/test.png';
					this.infoBox.options.content = $('requistionDiv').get('html');
					this.infoBox.addEvent('open', this.openTable.bind(this));
					this.infoBox.open();
					this.infoBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '订购关系',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					var sdid = selectIds[0];
					if(sdid < 0) {
						new LightFace.MessageBox().error('当前安全域还未通过审核，没有订购信息');
						return;
					}
					
					this.infoBox.options.title = '查看安全域订购信息';
					this.infoBox.options.titleImage = ctx + '/admin/images/test.png';
					this.infoBox.options.content = document.getElement('[name="subscribeDiv"]').get('html');
					this.infoBox.addEvent('open', this.openSubscribeTable.bind(this));
					this.infoBox.open();
					this.infoBox.removeEvents('open');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用提供商',
				dataName : 'sp_name', align : 'center', order : false
			}, {
				title : '安全域AID',
				dataName : 'aid', align : 'center'
			}, {
				title : '安全域名称',
				dataName : 'sdName', align : 'center'
			}, {
				title : '权限',
				dataName : 'privilegeZh', align : 'center', order : false
			}, 
			{
				title : '状态',
				dataName : 'status', align : 'center', order : true
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '状态：',
					name : 'search_EQI_status',
					type : 'select',
					data : {
					    //0 : '全部',
						1 : '待审核',
						2 : '已发布',
						3 : '已归档'
					}
				}, {
					title : '应用提供商名称：',
					name : 'search_ALIAS_spL_LIKES_name',
					type : 'text'
				} ]
			},
			headerText : '安全域管理',
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	openNewSd : function() {
		//TODO 新增按钮
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/securityDomain/?m=add');
		
		this.initSpBaseInfoSelect();
		
		//初始化RID
		//var select = this.box.messageBox.getElement('select[id=spSelect]');
		
		//add event
		this.addHsmkeyConfigENC();
		this.addHsmkeyConfigMAC();
		this.addHsmkeyConfigDEK();
		
		this.addInstallParamsEvent();
		this.addValidate();
	},
	openOldSd : function() {
		//TODO 
		var sdId = this.grid.selectIds[0];
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/securityDomain/?m=sdModifyKeyProfile&sdid='+sdId);
		
		var url = '';
		if(sdId < 0) {
			sdId = Math.abs(sdId);
			url = ctx + '/html/securityDomain/?m=sdApplyLoad&sdid=' + sdId;
		} else {
			url = ctx + '/html/securityDomain/?m=sdLoad&status=0&sdid=' + sdId;
		}
		this.box.messageBox.getElement('input[name=id]').set('value', sdId);
		
		new Request.JSON({
			async : false,
			url : url,
			onSuccess : function(data) {
				if(data.success) {
					var sd = data.message;
					this.box.messageBox.getElement('input[id=currentKeyVersion]').set('value', sd.currentKeyVersion);
					this.box.messageBox.getElement('input[id=keyProfileMAC]').set('value', sd.keyProfileMAC);
					this.box.messageBox.getElement('input[id=keyProfileDEK]').set('value', sd.keyProfileDEK);
					this.box.messageBox.getElement('input[id=keyProfileENC]').set('value', sd.keyProfileENC);
					
					this.box.messageBox.getElement('input[name=hsmkeyConfigENC]').set('value', sd.hsmkeyConfigENC);
					this.box.messageBox.getElement('input[name=hsmkeyConfigDEK]').set('value', sd.hsmkeyConfigDEK);
					this.box.messageBox.getElement('input[name=hsmkeyConfigMAC]').set('value', sd.hsmkeyConfigMAC);

					if(sd.hsmkeyConfigENC != "") {
						this.encSelectIds = [];
						var array = sd.hsmkeyConfigENC.split(',');
						for(var index = 0; index < array.length; index++) {
							this.encSelectIds.push(array[index]);
						}
					}
					
					if(sd.hsmkeyConfigDEK != "") {
						this.dekSelectIds = [];
						var array = sd.hsmkeyConfigDEK.split(',');
						for(var index = 0; index < array.length; index++) {
							this.dekSelectIds.push(array[index]);
						}
					}
					
					if(sd.hsmkeyConfigMAC != "") {
						this.macSelectIds = [];
						var array = sd.hsmkeyConfigMAC.split(',');
						for(var index = 0; index < array.length; index++) {
							this.macSelectIds.push(array[index]);
						}
					}
				}
			}.bind(this)
		}).get();
		
		this.addHsmkeyConfigDEK();
		this.addHsmkeyConfigENC();
		this.addHsmkeyConfigMAC();
		this.initHsmkeyConfigSelect();
		
		this.addValidate();
	},
	addHsmkeyConfigENC : function() {
		
		this.box.getBox().getElement('[id=addHsmkeyConfigENC]').addEvent('click', function() {
			this.keyProfile = 'enc';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	addHsmkeyConfigMAC : function() {

		this.box.getBox().getElement('[id=addHsmkeyConfigMAC]').addEvent('click', function() {
			this.keyProfile = 'mac';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	addHsmkeyConfigDEK : function() {
		this.box.getBox().getElement('[id=addHsmkeyConfigDEK]').addEvent('click', function() {
			this.keyProfile = 'dek';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	openHsmkeyConfigBox : function() {
		var url = ctx + '/html/encryptor/?m=list';
		
		this.hsmkeyConfigBox.messageBox.setStyle('overflow-y', 'hidden');
		var tableId = this.hsmkeyConfigBox.messageBox.getElement('div[name=hsmkeyConfigDiv]');
		this.gridHsmkeyConfig = new JIM.UI.Grid(tableId, {
			url : url,
			selection : true,
			multipleSelection : true,
			columnModel : [ 
			    {dataName : 'id', identity : true}, 
			    {title : '类型',dataName : 'model', align : 'center', order : true}, 
			    {title : '厂商',dataName : 'vendor', align : 'center', order : true},
			    {title : '索引',dataName : 'index', align : 'center', order : true},
			    {title : '版本',dataName : 'version', align : 'center', order : true}
			]
			,header : false
			,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
		
		if(this.keyProfile == 'enc') {
			this.gridHsmkeyConfig.selectIds = this.encSelectIds;
		} else if(this.keyProfile == 'mac') {
			this.gridHsmkeyConfig.selectIds = this.macSelectIds;
		} else if(this.keyProfile == 'dek') {
			this.gridHsmkeyConfig.selectIds = this.dekSelectIds;
		}
		
		
	},
	initHsmkeyConfigSelect : function() {
		//查询待审核SD对应的KeyProfile数组
	},	
	openEditSd : function() {
		//TODO 修改按钮
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];
		if(sdId < 0) {
			//sdname,privilege,installparams,spSelect可改
			//待审核状态：可修改、可删除
			
		} else {
			//sdname、installparams可改
			//判断状态： 发布状态可修改、归档状态可查看
			
		}
		
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/securityDomain/?m=sdModify&sdId='+sdId);
		
		this.addValidate();
		this.initSpBaseInfoSelect();
		//alert('sd id is ' + sdId);
		//修改时，表单数据加载
		new Request.JSON( {
			async : false,
			url : ctx + '/html/securityDomain/?m=getSd&sdId=' + sdId,
			onSuccess : function(data) {
				if (data.success) {
					sdStatus = data.message.status;
					if(data.message.status == 3) {
						new LightFace.MessageBox().error('所选的安全域已经归档，不能修改');
					} else {
						var inputs = this.box.messageBox.getElements('input,radio,select');
						
						for(var index = 0; index < inputs.length; index++) {
							var input = inputs[index];
							
							if(input.get('type') == 'radio') {
								if(input.get('value') == data.message[input.get('name')]) {
									input.set('checked', 'checked');
								}
							} else if(input.get('type') == 'checkbox') {
								var checked = data.message[input.get('name')];
								if(checked) {
									input.set('checked', 'checked');
								}
								
								if(sdId > 0) input.set('disabled', 'disabled');
							} else {
								input.set('value', data.message[input.get('name')]);
							}
						}
						
						//给AID赋值
						var originalAidHidden = this.box.messageBox.getElement('input[id=originalAid]');
						if($chk(originalAidHidden)) {
							originalAidHidden.set('value',data.message['aid']);
						}
						
						this.box.messageBox.getElement('select[id="deleteRule"]').set('value',data.message.deleteRule);
						
						if(sdId > 0) {
							this.box.messageBox.getElement('input[id="aid"]').set('readonly','readonly');
							this.box.messageBox.getElement('p[id="aidExplain"]').appendText('只读');
						} else {
							//alert('this is sd apply');
						}
						
						if (sdStatus == 2){
							this.box.messageBox.getElement('select[id="deleteRule"]').set('disabled', true);
							new Element('input', {name : 'deleteRule', type : 'hidden',value:data.message.deleteRule}).inject(this.box.messageBox.getElement('[id="hiddendata"]'));
							this.box.messageBox.getElement('input[id="installParams"]').set('readonly', true);
						}
						
						//前面加红色
						this.box.messageBox.getElement('p[id="sdNameText"]').empty();
						new Element('span', {text : '名称:', style : 'color: red;'}).inject(this.box.messageBox.getElement('p[id="sdNameText"]'));
						
						this.box.messageBox.getElement('p[id="installParamsText"]').empty();
						new Element('span', {text : '安装参数:', style : 'color:red;'}).inject(this.box.messageBox.getElement('p[id="installParamsText"]'));
						
						this.box.messageBox.getElement('p[id="scp02SecurityLevelText"]').empty();
						new Element('span', {text : '安全等级:', style : 'color:red;'}).inject(this.box.messageBox.getElement('p[id="scp02SecurityLevelText"]'));
						
						if(data.message.token) {
							this.box.messageBox.getElement('p[id="businessPlatformUrlText"]').empty();
							new Element('span', {text : '业务平台URL:', style : 'color:red;'}).inject(this.box.messageBox.getElement('p[id="businessPlatformUrlText"]'));
							
							this.box.messageBox.getElement('p[id="serviceNameText"]').empty();
							new Element('span', {text : '业务平台服务名:', style : 'color:red;'}).inject(this.box.messageBox.getElement('p[id="serviceNameText"]'));
						} else {
							this.box.messageBox.getElement('input[id=businessPlatformUrl]').set('readonly','readonly');
							this.box.messageBox.getElement('input[id=serviceName]').set('readonly','readonly');
						}
						
						if(sdId < 0) {
							this.box.messageBox.getElement('p[id="deleteRuleText"]').empty();
							new Element('span', {text : '删除规则:', style : 'color: red;'}).inject(this.box.messageBox.getElement('p[id="deleteRuleText"]'));
							this.box.messageBox.getElement('p[id="volatileSpaceText"]').empty();
							new Element('span', {text : '安全域自身的内存空间:', style : 'color: red;'}).inject(this.box.messageBox.getElement('p[id="volatileSpaceText"]'));
							this.box.messageBox.getElement('p[id="noneVolatileSpaceText"]').empty();
							new Element('span', {text : '安全域自身的存储空间:', style : 'color: red;'}).inject(this.box.messageBox.getElement('p[id="noneVolatileSpaceText"]'));
							this.box.messageBox.getElement('p[id="privilegeText"]').empty();
							new Element('span', {text : '权限:', style : 'color:red;'}).inject(this.box.messageBox.getElement('p[id="privilegeText"]'));
							
							var labels = [{text : '安全域密钥版本号:', id : 'currentKeyVersionText'},
							              {text : 'ENC密钥:', id : 'keyProfileENCText'},
							              {text : 'MAC密钥:', id : 'keyProfileMACText'},
							              {text : 'DEK密钥:', id : 'keyProfileDEKText'},
							              {text : 'AID:', id : 'aidText'},
							              {text : '应用提供商:', id : 'spSelectText'}];
							for(var index = 0; index < labels.length; index++) {
								var id = labels[index].id;
								var text = labels[index].text;
								this.box.messageBox.getElement('p[id="'+id+'"]').empty();
								new Element('span', {text : text, style : 'color:red;'}).inject(this.box.messageBox.getElement('p[id="'+id+'"]'));
							}
						}
						
						//发布状态下的不可修改字段
						var spSelect = this.box.messageBox.getElement('select[id="spSelect"]');
						var options = spSelect.getElements('option');
						options.each(function(e, index) {
							if(e.get('value') == data.message.sp_id) {
								e.set('selected','selected');
							}
						});
						if(sdId > 0) {
							this.box.messageBox.getElement('input[id=volatileSpace]').set('readonly','readonly');
							this.box.messageBox.getElement('input[id=noneVolatileSpace]').set('readonly','readonly');
							var labels = [{text : '安全域密钥版本号:', id : 'currentKeyVersion'},
							              {text : 'ENC密钥:', id : 'keyProfileENC'},
							              {text : 'MAC密钥:', id : 'keyProfileMAC'},
							              {text : 'DEK密钥:', id : 'keyProfileDEK'}];
							for(var index = 0; index < labels.length; index++) {
								var id = labels[index].id;
								this.box.messageBox.getElement('input[id='+id+']').set('readonly','readonly');
							}

							spSelect.set('disabled', 'disabled');
						} else {
							//spSelect.set('disabled', 'disabled');
						}
						
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({t : new Date().getTime()});
		
		//add event
		this.addInstallParamsEvent();
	}, 
	openEditSdKeyVersion : function() {
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/securityDomain/?m=sdModifyKeyVersion&sdId='+sdId);
		
		new Request.JSON( {
			async : false,
			url : ctx + '/html/securityDomain/?m=getSd&sdId=' + sdId,
			onSuccess : function(data) {
				if (data.success) {
					var sd = data.message;
					//alert('currentKeyVersion='+sd.currentKeyVersion);
					this.box.messageBox.getElement('input[id=currentKeyVersion]').set('value', sd.currentKeyVersion);
					this.box.messageBox.getElement('input[id=keyProfileDEKindex]').set('value', sd.keyProfileDEKindex);
					this.box.messageBox.getElement('input[id=keyProfileMACindex]').set('value', sd.keyProfileMACindex);
					this.box.messageBox.getElement('input[id=keyProfileENCindex]').set('value', sd.keyProfileENCindex);
					
					this.box.messageBox.getElement('input[id=keyProfileDEKversion]').set('value', sd.keyProfileDEKversion);
					this.box.messageBox.getElement('input[id=keyProfileMACversion]').set('value', sd.keyProfileMACversion);
					this.box.messageBox.getElement('input[id=keyProfileENCversion]').set('value', sd.keyProfileENCversion);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post();
		
		this.addValidate();
	},
	openInfoSd : function() {
		//TODO 查看详情按钮
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];

		//查看安全域表单，数据加载
		new Request.JSON( {
			async : false,
			url : ctx + '/html/securityDomain/?m=getSd&sdId=' + sdId,
			onSuccess : function(data) {
				if (data.success) {

					var inputs = this.infoBox.messageBox.getElements('input,radio,select');
					var box = this.infoBox.messageBox;
					$each(inputs, function(input, index) {
						if(input.get('type') == 'radio') {
							if(input.get('value') == data.message[input.get('name')]) {
								input.set('checked', 'checked');
							}
						} else if(input.get('type') == 'checkbox') {
							if(data.message[input.get('name')]) {
								input.set('checked', 'checked');
								input.set('disabled', 'disabled');
							}
						} else {
							input.set('value', data.message[input.get('name')]);
						}
					});
					//SP下拉框赋值
					new Request.JSON({
						async : false,
						url : ctx + '/html/spBaseInfo/?m=select',
						onSuccess : function(data2) {
							if (data2.success) {
								$each(data2.message, function(e, index) {
									if (data.message.sp_id == e.id){
										box.getElement('[id=spSelect]').set('html',e.name);
									}
								});
							}
						}.bind(this)
					}).post({status : 1});
					
					//load installparams
					var installParams = data.message['installParams'];
					new Request.JSON({
						async : false,
						url : ctx + "/html/securityDomain/?m=parseInstallParams&installParams="+installParams,
						onSuccess : function(data) {
							var msg = data.message;
							var box = this.infoBox.messageBox;
							if(data.success) {
								box.getElement("[name='transfer']").set('value', msg.transfer);
								box.getElement("[name='deleteApp']").set('value', msg.deleteApp);
								box.getElement("[name='deleteSelf']").set('value', msg.deleteSelf);
								box.getElement("[name='installApp']").set('value', msg.installApp);
								box.getElement("[name='downloadApp']").set('value', msg.downloadApp);
								box.getElement("[name='lockedApp']").set('value', msg.lockedApp);
								
								deleteSelf = msg.deleteSelf;
								
								box.getElement("[name='scp']").set('value',msg.scp);
								box.getElement("[name='maxFailCount']").set('value', msg.maxFailCount);;
								box.getElement("[name='keyVersion']").set('value', msg.keyVersion);
								box.getElement("[name='maxKeyNumber']").set('value', msg.maxKeyNumber);
								if (msg.managedNoneVolatileSpace == '' && msg.managedVolatileSpace == ''){
									box.getElement('[id=spacePatten]').set('html','应用大小模式');
									box.getElement("[id='sphidden1']").setStyle('display','none');
									box.getElement("[id='sphidden2']").setStyle('display','none');
								}else{
									box.getElement("[name='managedNoneVolatileSpace']").set('value',msg.managedNoneVolatileSpace);
									box.getElement("[name='managedVolatileSpace']").set('value', msg.managedVolatileSpace);
									box.getElement("[name='managedNoneVolatileSpace']").set('disabled',true);
									box.getElement("[name='managedVolatileSpace']").set('disabled', true);
								}
								if (sdStatus == 2){
									box.getElement("[name='transfer']").set('disabled', true);
									new Element('input', {name : 'transfer', type : 'hidden',value:msg.transfer}).inject(box.getElement('[id="hiddendata"]'));
									box.getElement("[name='deleteApp']").set('disabled', true);
									new Element('input', {name : 'deleteApp', type : 'hidden',value:msg.deleteApp}).inject(box.getElement('[id="hiddendata"]'));
									box.getElement("[name='deleteSelf']").set('disabled', true);
									new Element('input', {name : 'deleteSelf', type : 'hidden',value:msg.deleteSelf}).inject(box.getElement('[id="hiddendata"]'));
									
									box.getElement("[name='installApp']").set('disabled', true);
									new Element('input', {name : 'installApp', type : 'hidden',value:msg.installApp}).inject(box.getElement('[id="hiddendata"]'));
									
									box.getElement("[name='downloadApp']").set('disabled', true);
									new Element('input', {name : 'downloadApp', type : 'hidden',value:msg.downloadApp}).inject(box.getElement('[id="hiddendata"]'));
									
									box.getElement("[name='lockedApp']").set('disabled', true);
									new Element('input', {name : 'lockedApp', type : 'hidden',value:msg.lockedApp}).inject(box.getElement('[id="hiddendata"]'));
									
									box.getElement("[name='scp']").set('disabled', true);
									new Element('input', {name : 'scp', type : 'hidden',value:msg.scp}).inject(box.getElement('[id="hiddendata"]'));
									box.getElement("[name='maxFailCount']").set('readonly', true);
									box.getElement("[name='keyVersion']").set('readonly', true);
									box.getElement("[name='maxKeyNumber']").set('readonly', true);
									box.getElement("[id='spacePatten']").set('disabled', true);
									if (msg.managedNoneVolatileSpace == '' && msg.managedVolatileSpace == ''){
										box.getElement('[id=spacePattentd]').set('html','应用大小模式');
										box.getElement("[id='sphidden1']").setStyle('display','none');
										box.getElement("[id='sphidden2']").setStyle('display','none');
									}else{
										box.getElement('[id=spacePatten]').set('checked',true);
										box.getElement("[name='managedNoneVolatileSpace']").set('value',msg.managedNoneVolatileSpace);
										box.getElement("[name='managedVolatileSpace']").set('value', msg.managedVolatileSpace);
										box.getElement("[name='managedNoneVolatileSpace']").set('disabled',false);
										box.getElement("[name='managedVolatileSpace']").set('disabled', false);
									}
								}
							} else {
								new LightFace.MessageBox().error("安装参数解析出错，请检查参数格式");
								box.getElement("[name='transfer']").set('value', 1);
								box.getElement("[name='deleteApp']").set('value', 1);
								box.getElement("[name='deleteSelf']").set('value', 1);
								deleteSelf = 1;
								box.getElement("[name='scp']").set('value','02,15');
								box.getElement("[name='maxFailCount']").set('value', 255);;
								box.getElement("[name='keyVersion']").set('value', 115);
								box.getElement("[name='maxKeyNumber']").set('value', 16);
								box.getElement("[name='managedNoneVolatileSpace']").set('value','');
								box.getElement("[name='managedVolatileSpace']").set('value', '');
								new LightFace.MessageBox().error(data.message);
							}
						}.bind(this)
					}).post();
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({t : new Date().getTime()});
		
	},
	openTable : function() {
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];
		var url = ctx + '/html/requistion/?m=list&search_EQL_originalId='+sdId+'&t=' + new Date().getTime();
		if(sdId < 0) {
			url = ctx + '/html/requistion/?m=list&search_EQL_id='+Math.abs(sdId)+'&t=' + new Date().getTime();
		}
		this.infoBox.messageBox.setStyle('overflow-y', 'hidden');
		var tableId = this.infoBox.messageBox.getElement('div[name=requistionTable]');
		new JIM.UI.Grid(tableId, {
			url : url,
			selection : false,
			multipleSelection : false,
			columnModel : [ 
			    {dataName : 'id', identity : true}, 
			    {title : '审核状态',dataName : 'status', align : 'center', order : true}, 
			    {title : '申请类型',dataName : 'type', align : 'center', order : true}, 
			    {title : '审核时间',dataName : 'reviewDate', align : 'center', order : true},
			    {title : '审核结果',dataName : 'result', align : 'center', order : true},
			    {title : '审核意见',dataName : 'opinion', align : 'center', order : true}
			]
			,header : false
			,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
		
	},
	openSubscribeTable : function() {
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];
		var url = ctx + '/html/securityDomain/?m=listSubscribe&id=' + sdId;
		this.infoBox.messageBox.setStyle('overflow-y', 'hidden');
		var tableId = this.infoBox.messageBox.getElement('div[name=subscribeTable]');
		new JIM.UI.Grid(tableId, {
			url : url,
			selection : false,
			multipleSelection : false,
			columnModel : [ 
			    {dataName : 'id', identity : true}, 
			    {title : '手机号码',dataName : 'mobileNo', align : 'center', order : false}, 
			    {title : '用户姓名',dataName : 'customer_nickName', align : 'center', order : false}, 
			    {title : 'SEID',dataName : 'card_cardNo', align : 'center', order : false}
			]
			,header : false
			,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	initSpBaseInfoSelect : function() {
		new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/?m=select',
			onSuccess : function(data) {
				if (data.success) {
					var select = this.box.messageBox.getElement('select[id=spSelect]');
					select.set('style','width:96%');
					var html = '';
					$each(data.message, function(e, index) {
						var option = '<option value="'+e.id+'">'+e.name+'</option>';
						html += option;
					});
					select.set('html', html);					
				}
			}.bind(this)
		}).post({status : 1});
	},
	addCheckBoxEvent : function() {
		var dap = this.box.getBox().getElement('[id="dap"]');
		var dapForce = this.box.getBox().getElement('[id="dapForce"]');
		var token = this.box.getBox().getElement('[id="token"]');
		
		dap.addEvent('click', function() {
    		var checked = dap.get('checked');
    		if(checked) {
    			dapForce.set('disabled','disabled');
    			dapForce.set('checked','');
    			token.set('disabled','disabled');
    			token.set('checked','');
    		} else {
    			dapForce.set('disabled','');
    			token.set('disabled','');
    		}
    	});
		dapForce.addEvent('click', function() {
    		var checked = dapForce.get('checked');
    		if(checked) {
    			dap.set('disabled','disabled');
    			token.set('disabled','disabled');
    			dap.set('checked','');
    			token.set('checked','');
    		} else {
    			dap.set('disabled','');
    			token.set('disabled','');
    		}
    	});
		token.addEvent('click', function() {
    		var checked = token.get('checked');
    		if(checked) {
    			dapForce.set('disabled','disabled');
    			dap.set('disabled','disabled');
    			dapForce.set('checked','');
    			dap.set('checked','');
    		} else {
    			dapForce.set('disabled','');
    			dap.set('disabled','');
    		}
    	});
	},
	addInstallParamsEvent : function() {
		this.box.getBox().getElement('[id="installParamsButton"]').addEvent('click', function() {
			var installParams = this.box.getBox().getElement('[id="installParams"]').get('value');
			var apply = new SecurityDomain.Apply();
			apply.params = this;
			apply.createInstallParams(installParams);
		}.bind(this));
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
				if(this.dekSelectIds.length == 0) {
					new LightFace.MessageBox().error("DEK密钥未配置加密机");
				} else if(this.encSelectIds.length == 0) {
					new LightFace.MessageBox().error("ENC密钥未配置加密机");
				} else if(this.macSelectIds.length == 0) {
					new LightFace.MessageBox().error("MAC密钥未配置加密机");
				} else {
					this.submitForm();
				}
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

/* --------------------------------------------------------------------------------------------------- */

//TODO 安全域审核 SecurityDomain.audit
SecurityDomain.audit = new Class({
	options : {},
	initialize : function() {
		this.drawGrid();
		this.drawBox();
		
		this.keyProfile = '';
		this.gridHsmkeyConfig = {};
		this.encSelectIds = [];
		this.macSelectIds = [];
		this.dekSelectIds = [];
		
	},
	drawBox : function() {
		this.hsmkeyConfigBox = new LightFace({
			width : 500,
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

					if(this.keyProfile == 'enc') {
						this.encSelectIds = this.gridHsmkeyConfig.selectIds;
						this.box.messageBox.getElement('input[name=hsmkeyConfigENC]').set('value', this.encSelectIds.toString());
					} else if(this.keyProfile == 'mac') {
						this.macSelectIds = this.gridHsmkeyConfig.selectIds;
						this.box.messageBox.getElement('input[name=hsmkeyConfigMAC]').set('value', this.macSelectIds.toString());
					} else if(this.keyProfile == 'dek') {
						this.dekSelectIds = this.gridHsmkeyConfig.selectIds;
						this.box.messageBox.getElement('input[name=hsmkeyConfigDEK]').set('value', this.dekSelectIds.toString());
					}
					
					this.hsmkeyConfigBox.close();
				}.bind(this),
				color : 'blue'
			} ]
		
		});
		
		this.box = new LightFace( {
			width : 700,
			draggable : true,
			initDraw : false,
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
		this.infoBox = new LightFace( {
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/securityDomain/?m=listAudit',
			multipleSelection : false,
			buttons : [ {
				name : '审核通过',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					var sdid = selectIds[0];
					
					//取得审核记录，判断状态，新增申请，需要填密钥index
					var isPublishApply = null;
					new Request.JSON({
						async : false,
						url : ctx + '/html/securityDomain/?m=sdApplyLoad',
						onSuccess : function(data) {
							if(data.success) {
								isPublishApply = data.message.isPublishApply;
							} else {
								new LightFace.MessageBox().error(data.message);
							}
						}.bind(this)
					}).post({sdid : sdid});
					
					if(isPublishApply) {
						//
						this.box.options.title = '新增安全域申请审核';
						this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
						
						this.box.options.content = $('sdDiv').get('html');
						
						this.box.addEvent('open', this.openNewSd.bind(this));
						this.box.open();
						this.box.removeEvents('open');
					} else {
						//普通审核通过
						new LightFace.MessageBox( {
							onClose : function(result) {
								if (result) {
									new Request.JSON( {
										url : ctx + '/html/securityDomain/?m=audit&status=yes',
										onSuccess : function(data) {
											if (data.success) {
												new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
											} else {
												new LightFace.MessageBox().error(data.message);
											}
										}.bind(this)
									}).post( {id : sdid});
								}
							}.bind(this)
						}).confirm('确认审核通过？');
					}
					
				}.bind(this)
			}, {
				name : '审核不通过',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}

					new LightFace.MessageBox( {
						onClose : function(result, opinion) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/securityDomain/?m=audit',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									id : this.grid.selectIds[0],status:'no',opinion:opinion
								});
							}
							
						}.bind(this)
					}).prompt('审核不通过原因', 200, true);
					
				}.bind(this)
			}, {

				name : '查看详情',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					this.infoBox.options.title = '查看安全域申请信息';
					this.infoBox.options.titleImage = ctx + '/admin/images/test.png';
					this.infoBox.options.content = $('sdDivInfo').get('html');
					this.infoBox.addEvent('open', this.openInfoSd.bind(this));
					this.infoBox.open();
					this.infoBox.removeEvents('open');
				}.bind(this)
			
			} ],
			columnModel : [ {
				dataName : 'id', identity : true
			},
			{title : '状态', dataName : 'requistion_status', align : 'center'},
			{title : '申请类型', dataName : 'applyType', align : 'center'},
			{title : '申请时间', dataName : 'requistion_submitDate', align : 'center'},
			{title : '应用提供商', dataName : 'sp_name', align : 'center'}, 
			{title : '安全域AID', dataName : 'aid', align : 'center'}, 
			{title : '安全域名称', dataName : 'sdName', align : 'center'}, 
			{title : '权限', dataName : 'privilegeZh', align : 'center', order : false},
			{title : '申请原因', dataName : 'requistion_reason', align : 'center'}
			],
			searchButton : true,
			searchBar : {
				filters : [ {

					title : '应用提供商名称：',
					name : 'search_LIKES_name',
					type : 'text'
				
				}
				]
			},
			headerText : '安全域审核',
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	openNewSd : function() {
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/securityDomain/?m=audit&status=yes');
		var applyId = this.grid.selectIds[0];
		this.box.messageBox.getElement('input[name=id]').set('value', applyId);
		
		new Request.JSON({
			async : false,
			url : ctx + '/html/securityDomain/?m=sdApplyLoad&sdid=' + applyId,
			onSuccess : function(data) {
				if(data.success) {
					var sd = data.message;
					this.box.messageBox.getElement('input[id=currentKeyVersion]').set('value', sd.currentKeyVersion);
					this.box.messageBox.getElement('input[id=keyProfileMAC]').set('value', sd.keyProfileMAC);
					this.box.messageBox.getElement('input[id=keyProfileDEK]').set('value', sd.keyProfileDEK);
					this.box.messageBox.getElement('input[id=keyProfileENC]').set('value', sd.keyProfileENC);
					
					this.box.messageBox.getElement('input[name=hsmkeyConfigENC]').set('value', sd.hsmkeyConfigENC);
					this.box.messageBox.getElement('input[name=hsmkeyConfigDEK]').set('value', sd.hsmkeyConfigDEK);
					this.box.messageBox.getElement('input[name=hsmkeyConfigMAC]').set('value', sd.hsmkeyConfigMAC);

					if(sd.hsmkeyConfigENC != "") {
						this.encSelectIds = [];
						var array = sd.hsmkeyConfigENC.split(',');
						for(var index = 0; index < array.length; index++) {
							this.encSelectIds.push(array[index]);
						}
					}
					
					if(sd.hsmkeyConfigDEK != "") {
						this.dekSelectIds = [];
						var array = sd.hsmkeyConfigDEK.split(',');
						for(var index = 0; index < array.length; index++) {
							this.dekSelectIds.push(array[index]);
						}
					}
					
					if(sd.hsmkeyConfigMAC != "") {
						this.macSelectIds = [];
						var array = sd.hsmkeyConfigMAC.split(',');
						for(var index = 0; index < array.length; index++) {
							this.macSelectIds.push(array[index]);
						}
					}
				}
			}.bind(this)
		}).get();
		
		this.addHsmkeyConfigDEK();
		this.addHsmkeyConfigENC();
		this.addHsmkeyConfigMAC();
		this.initHsmkeyConfigSelect();
		
		this.addValidate();
	},
	addHsmkeyConfigENC : function() {
		
		this.box.getBox().getElement('[id=addHsmkeyConfigENC]').addEvent('click', function() {
			this.keyProfile = 'enc';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	addHsmkeyConfigMAC : function() {

		this.box.getBox().getElement('[id=addHsmkeyConfigMAC]').addEvent('click', function() {
			this.keyProfile = 'mac';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	addHsmkeyConfigDEK : function() {
		this.box.getBox().getElement('[id=addHsmkeyConfigDEK]').addEvent('click', function() {
			this.keyProfile = 'dek';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	openHsmkeyConfigBox : function() {
		var url = ctx + '/html/encryptor/?m=list';
		
		this.hsmkeyConfigBox.messageBox.setStyle('overflow-y', 'hidden');
		var tableId = this.hsmkeyConfigBox.messageBox.getElement('div[name=hsmkeyConfigDiv]');
		this.gridHsmkeyConfig = new JIM.UI.Grid(tableId, {
			url : url,
			selection : true,
			multipleSelection : true,
			columnModel : [ 
			    {dataName : 'id', identity : true}, 
			    {title : '类型',dataName : 'model', align : 'center', order : true}, 
			    {title : '厂商',dataName : 'vendor', align : 'center', order : true},
			    {title : '索引',dataName : 'index', align : 'center', order : true},
			    {title : '版本',dataName : 'version', align : 'center', order : true}
			]
			,header : false
			,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
		
		if(this.keyProfile == 'enc') {
			this.gridHsmkeyConfig.selectIds = this.encSelectIds;
		} else if(this.keyProfile == 'mac') {
			this.gridHsmkeyConfig.selectIds = this.macSelectIds;
		} else if(this.keyProfile == 'dek') {
			this.gridHsmkeyConfig.selectIds = this.dekSelectIds;
		}
		
		
	},
	initHsmkeyConfigSelect : function() {
		//查询待审核SD对应的KeyProfile数组
	},
	openInfoSd : function() {
		//TODO 查看详情按钮
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];

		//表单数据加载
		new Request.JSON( {
			async : false,
			url : ctx + '/html/securityDomain/?m=getSd&sdId=' + -sdId,
			onSuccess : function(data) {
				if (data.success) {

					var inputs = this.infoBox.messageBox.getElements('input,radio,select');
					var box = this.infoBox.messageBox;
					$each(inputs, function(input, index) {
						if(input.get('type') == 'radio') {
							if(input.get('value') == data.message[input.get('name')]) {
								input.set('checked', 'checked');
							}
						} else if(input.get('type') == 'checkbox') {
							if(data.message[input.get('name')]) {
								input.set('checked', 'checked');
								input.set('disabled', 'disabled');
							}
						} else if(input.get('type') == 'select') {
							input.set('disabled', 'disabled');
						} else {
							input.set('value', data.message[input.get('name')]);
						}
					});
					new Request.JSON({
						async : false,
						url : ctx + '/html/spBaseInfo/?m=select',
						onSuccess : function(data2) {
							if (data2.success) {
								$each(data2.message, function(e, index) {
									if (data.message.sp_id == e.id){
										box.getElement('[id=spSelect]').set('html',e.name);
									}
								});
							}
						}.bind(this)
					}).post({status : 1});
					//load installparams
					var installParams = data.message['installParams'];
					new Request.JSON({
						async : false,
						url : ctx + "/html/securityDomain/?m=parseInstallParams&installParams="+installParams,
						onSuccess : function(data) {
							var msg = data.message;
							var box = this.infoBox.messageBox;
							if(data.success) {
								box.getElement("[name='transfer']").set('value', msg.transfer);
								box.getElement("[name='deleteApp']").set('value', msg.deleteApp);
								box.getElement("[name='deleteSelf']").set('value', msg.deleteSelf);
								box.getElement("[name='installApp']").set('value', msg.installApp);
								box.getElement("[name='downloadApp']").set('value', msg.downloadApp);
								box.getElement("[name='lockedApp']").set('value', msg.lockedApp);
								
								deleteSelf = msg.deleteSelf;
								box.getElement("[name='scp']").set('value',msg.scp);
								box.getElement("[name='maxFailCount']").set('value', msg.maxFailCount);;
								box.getElement("[name='keyVersion']").set('value', msg.keyVersion);
								box.getElement("[name='maxKeyNumber']").set('value', msg.maxKeyNumber);
								if (msg.managedNoneVolatileSpace == '' && msg.managedVolatileSpace == ''){
									this.infoBox.messageBox.getElement('[id=spacePatten]').set('html','应用大小模式');
									box.getElement("[id='sphidden1']").setStyle('display','none');
									box.getElement("[id='sphidden2']").setStyle('display','none');
								}else{
									box.getElement("[name='managedNoneVolatileSpace']").set('value',msg.managedNoneVolatileSpace);
									box.getElement("[name='managedVolatileSpace']").set('value', msg.managedVolatileSpace);
									box.getElement("[name='managedNoneVolatileSpace']").set('disabled',true);
									box.getElement("[name='managedVolatileSpace']").set('disabled', true);
								}
							} else {
								new LightFace.MessageBox().error("安装参数解析出错，请检查参数格式");
								box.getElement("[name='transfer']").set('value', 1);
								box.getElement("[name='deleteApp']").set('value', 1);
								box.getElement("[name='deleteSelf']").set('value', 1);
								deleteSelf = 1;
								box.getElement("[name='scp']").set('value','02,15');
								box.getElement("[name='maxFailCount']").set('value', 255);;
								box.getElement("[name='keyVersion']").set('value', 115);
								box.getElement("[name='maxKeyNumber']").set('value', 16);
								box.getElement("[name='managedNoneVolatileSpace']").set('value','');
								box.getElement("[name='managedVolatileSpace']").set('value', '');
								
							}
						}.bind(this)
					}).post();
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({t : new Date().getTime()});
		
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
				if(this.dekSelectIds.length == 0) {
					new LightFace.MessageBox().error("DEK密钥未配置加密机");
				} else if(this.encSelectIds.length == 0) {
					new LightFace.MessageBox().error("ENC密钥未配置加密机");
				} else if(this.macSelectIds.length == 0) {
					new LightFace.MessageBox().error("MAC密钥未配置加密机");
				} else {
					this.submitForm();
				}
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

var formCheck;

SecurityDomain.Apply = new Class({
	Implements : [ Events, Options ],
	modal : null,
	formId : null,
	options : {},
	initialize: function(options){
		this.setOptions(options);
		this.formId = "form"+new Date().valueOf();
		this.modal = new LightFace({
				title : "安全域安装参数",
				height : 350,
				width : 550,
				zIndex : 9002,
				content : $('installParamsDiv').get("html"),
				resetOnScroll : false,
				buttons : [ {
					title : '保 存',
					event : function() {
					deleteSelf = this.modal.getBox().getElement('[id=deleteSelf]').get('value');
					this.modal.getBox().getElement('button').click();
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
		this.addValidate();
	},
	addValidate:function(){
		var box = this.modal.getBox();
		var form = box.getElement("[title='installParamsClient']");
		box.getElement("[name='managedVolatileSpace']").set('value','');
		box.getElement("[name='managedNoneVolatileSpace']").set('value','');
		box.getElement("[name='maxFailCount']").set('value',255);
		box.getElement("[name='keyVersion']").set('value',115);
		box.getElement("[name='maxKeyNumber']").set('value',16);
		formCheck = new FormCheck(form, {
			submit:false,
			trimValue:false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure:1
			},
			onValidateSuccess : function() {//校验通过执行load()
				this.submit();
			}.bind(this)
	});
	},
	createInstallParams :function(installParams){
		if(installParams!=""){
			new Request.JSON({
				url : ctx + "/html/securityDomain/?m=parseInstallParams&installParams="+installParams,
				onSuccess : this.setParamsValues.bind(this)
			}).post();
			}
			var box = this.modal.getBox();
			box.getElement("[title='installParamsClient']").set("id", this.formId).eliminate("title");
			var token = box.getElement("[id='spacePatten']");
			token.addEvent('click',function(event){
				if(token.get('checked')){
				box.getElement('[name="managedNoneVolatileSpace"]').erase('disabled');
				box.getElement('[name="managedVolatileSpace"]').erase('disabled');
			    } else{
			    	box.getElement('[name="managedNoneVolatileSpace"]').set('disabled','disabled');
			    	box.getElement('[name="managedVolatileSpace"]').set('disabled','disabled');
			    	box.getElement('[name="managedNoneVolatileSpace"]').set('value','');
			    	box.getElement('[name="managedVolatileSpace"]').set('value','');
					formCheck.removeError(box.getElement('[name="managedNoneVolatileSpace"]'));
					formCheck.removeError(box.getElement('[name="managedVolatileSpace"]'));
			    }
			}); 
		   this.modal.open();
		},
	setParamsValues: function(response){
		var msg = response.message;
		var box = this.modal.getBox();
		if(response.success){
			box.getElement("[name='transfer']").set('value', msg.transfer);
			box.getElement("[name='deleteApp']").set('value', msg.deleteApp);
			box.getElement("[name='deleteSelf']").set('value', msg.deleteSelf);
			box.getElement("[name='installApp']").set('value', msg.installApp);
			box.getElement("[name='downloadApp']").set('value', msg.downloadApp);
			box.getElement("[name='lockedApp']").set('value', msg.lockedApp);
			box.getElement("[name='scp']").set('value',msg.scp);
			box.getElement("[name='maxFailCount']").set('value', msg.maxFailCount);;
			box.getElement("[name='keyVersion']").set('value', msg.keyVersion);
			box.getElement("[name='maxKeyNumber']").set('value', msg.maxKeyNumber);
			box.getElement("[name='managedNoneVolatileSpace']").set('value',msg.managedNoneVolatileSpace);
			box.getElement("[name='managedVolatileSpace']").set('value', msg.managedVolatileSpace);
			if (msg.managedNoneVolatileSpace != '' && msg.managedVolatileSpace != ''){
				box.getElement("[name='spacePatten']").set('checked','checked');		
				box.getElement("[name='managedNoneVolatileSpace']").erase('disabled');
				box.getElement("[name='managedVolatileSpace']").erase('disabled');
			} 
			if (sdStatus == 2){
				box.getElement("[name='transfer']").set('disabled', true);
				new Element('input', {name : 'transfer', type : 'hidden',value:msg.transfer}).inject(box.getElement('[id="hiddendata"]'));
				
				box.getElement("[name='deleteApp']").set('disabled', true);
				new Element('input', {name : 'deleteApp', type : 'hidden',value:msg.deleteApp}).inject(box.getElement('[id="hiddendata"]'));
				
				box.getElement("[name='deleteSelf']").set('disabled', true);
				new Element('input', {name : 'deleteSelf', type : 'hidden',value:msg.deleteSelf}).inject(box.getElement('[id="hiddendata"]'));
				
				box.getElement("[name='installApp']").set('disabled', true);
				new Element('input', {name : 'installApp', type : 'hidden',value:msg.installApp}).inject(box.getElement('[id="hiddendata"]'));
				
				box.getElement("[name='downloadApp']").set('disabled', true);
				new Element('input', {name : 'downloadApp', type : 'hidden',value:msg.downloadApp}).inject(box.getElement('[id="hiddendata"]'));
				
				box.getElement("[name='lockedApp']").set('disabled', true);
				new Element('input', {name : 'lockedApp', type : 'hidden',value:msg.lockedApp}).inject(box.getElement('[id="hiddendata"]'));
				
				
				box.getElement("[name='scp']").set('disabled', true);
				new Element('input', {name : 'scp', type : 'hidden',value:msg.scp}).inject(box.getElement('[id="hiddendata"]'));
				
				box.getElement("[name='maxFailCount']").set('readonly', true);
				box.getElement("[name='keyVersion']").set('readonly', true);
				box.getElement("[name='maxKeyNumber']").set('readonly', true);
				box.getElement("[id='spacePatten']").set('disabled', true);
				if (msg.managedNoneVolatileSpace == '' && msg.managedVolatileSpace == ''){
					box.getElement('[id=spacePattentd]').set('html','应用大小模式');
					box.getElement("[id='sphidden1']").setStyle('display','none');
					box.getElement("[id='sphidden2']").setStyle('display','none');
				}else{
					box.getElement('[id=spacePatten]').set('checked',true);
					box.getElement("[name='managedNoneVolatileSpace']").set('value',msg.managedNoneVolatileSpace);
					box.getElement("[name='managedVolatileSpace']").set('value', msg.managedVolatileSpace);
					box.getElement("[name='managedNoneVolatileSpace']").set('disabled',false);
					box.getElement("[name='managedVolatileSpace']").set('disabled', false);
				}
			}
		}else{
			new LightFace.MessageBox().error("安装参数解析出错，请检查参数格式");
			box.getElement("[name='transfer']").set('value', 1);
			box.getElement("[name='deleteApp']").set('value', 1);
			box.getElement("[name='deleteSelf']").set('value', 1);
			box.getElement("[name='scp']").set('value','02,15');
			box.getElement("[name='maxFailCount']").set('value', 255);;
			box.getElement("[name='keyVersion']").set('value', 115);
			box.getElement("[name='maxKeyNumber']").set('value', 16);
			box.getElement("[name='managedNoneVolatileSpace']").set('value','');
			box.getElement("[name='managedVolatileSpace']").set('value','');
		}
	},
	submit : function() {
		new Request.JSON({
			url : ctx + "/html/securityDomain/?m=createInstallParams",
			data : $(this.formId),
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(response) {
		
		if (response.success) {
			this.close();
			this.params.box.getBox().getElement('[id="installParams"]').set('value',response.message);
		} else {
			new LightFace.MessageBox().error(response.message);
		}
	},
	close : function() {
		this.modal.close();
	}
});
//TODO 安全域订购关系列表
SecurityDomain.SubscribeList = new Class({
	Implements : [ Events, Options ],
	options : {},
	sdId : 0,
	initialize: function(options) {
		this.drawBox();
		this.drawGrid();
	},
	drawBox : function() {
		this.box = new LightFace({
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('table[class=lightface]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		var url = ctx + '/html/securityDomain/?m=listBySpAndStatus&page_orderBy=id_desc&t='+new Date().getTime();
		this.grid = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','状态','安全域AID','安全域名称','操作']//,'权限'
		});
		this.grid.inject($('grid'));
		
		var paging = {};
		paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:'nextpage', showNumber: true, showText : false},
			onAfterLoad : function(data) {
				this.grid.empty();
				
				//修改、撤销
				//查询状态为待审核的SD Apply
				data.result.forEach(function(sd, index) {
					var operation = '';
					var detail  = {};//'<a class="b" style="float : none;" href="javascript:detail('+sd.id+');"><span>查看</span></a>';
					detail = new Element('a', {"class" : "b", style : "float : none;"});
					new Element('span', {text : '查看'}).inject(detail);
					detail.addEvent('click', function() {
						this.sdId = sd.id;
						this.box.options.title = '查看安全域订购信息';
						this.box.options.titleImage = ctx + '/admin/images/test.png';
						this.box.options.content = document.getElement('[name="subscribeDiv"]').get('html');
						this.box.addEvent('open', this.openSubscribeTable.bind(this));
						this.box.open();
						this.box.removeEvents('open');
					}.bind(this));
					operation = detail;
					
					//var sdName = addTip('tip-sdName-' + sd.id, sd.sdName);
					var aid = addTip('tip-aid-' + sd.id, sd.aid, '50%');
					
					this.grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : sd.status, properties : {align : "center"}}, 
					           {content : aid,    properties : {align : "center"}}, 
					           {content : sd.sdName, properties : {align : "center"}}, 
					           {content : operation, properties : {align : "center"}}]);
				}.bind(this));
			}.bind(this)
		});
		paging.load();
	},
	openSubscribeTable : function() {
		var url = ctx + '/html/securityDomain/?m=listSubscribe&id=' + this.sdId;
		var table = new HtmlTable({
			properties: {
		        border: 0,
		        //"class" : "minfo",
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','手机号码','用户姓名','SEID']
		});
		var div = new Element('div', {"class" : "minfo"});
		table.inject(div);
		div.inject(this.box.messageBox.getElement('div[id=grid_]'));
		
		var paging = {};
		paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:this.box.messageBox.getElement('div[id=nextpage_]'), showNumber: true, showText : false},
			onAfterLoad : function(data) {
				table.empty();
				
				data.result.forEach(function(info, index) {
					var whitespace = '&nbsp;';
					var mobileNo = info.mobileNo ? info.mobileNo : whitespace;
					var name = info.customer_nickName ? info.customer_nickName : whitespace;
					var seid = info.card_cardNo ? info.card_cardNo : whitespace;
					table.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : mobileNo, properties : {align : "center"}}, 
					           {content : name,    properties : {align : "center"}}, 
					           {content : seid, properties : {align : "center"}}]);
				});
			}
		});
		paging.load();
	}
});

//TODO 应用订购关系列表
var Application = Application ? Application : {};

Application.SubscribeList = new Class({
	Implements : [ Events, Options ],
	options : {},
	appId : 0,
	initialize: function(options) {
		this.drawBox();
		this.drawGrid();
	},
	drawBox : function() {
		this.box = new LightFace({
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('table[class=lightface]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		var url = ctx + '/html/appVer/?m=indexWithSp&status=3&sp=self';
		this.grid = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','应用名称','版本号','状态','已下载终端数','可下载终端数','操作']
		});
		this.grid.inject($('grid'));
		
		var paging = {};
		paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:'nextpage', showNumber: true, showText : false},
			onAfterLoad : function(data) {
				this.grid.empty();
				data.result.forEach(function(app, index) {
					var operation = '';
					var detail  = {};
					detail = new Element('a', {"class" : "b", style : "float : none; cursor: pointer;"});
					new Element('span', {text : '查看'}).inject(detail);
					detail.addEvent('click', function() {
						this.appId = app.id;
						this.box.options.title = '查看应用订购信息';
						this.box.options.titleImage = ctx + '/admin/images/test.png';
						this.box.options.content = document.getElement('[name="subscribeDiv"]').get('html');
						this.box.addEvent('open', this.openSubscribeTable.bind(this));
						this.box.open();
						this.box.removeEvents('open');
					}.bind(this));
					operation = detail;
					
					//var aid = addTip('tip-aid-' + app.id, sd.aid, '50%');
					
					this.grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : app.application_name, properties : {align : "center"}}, 
					           {content : app.versionNo,    properties : {align : "center"}}, 
					           {content : app.status, properties : {align : "center"}}, 
					           {content : ''+app.downloadUserAmount, properties : {align : "center"}},
					           {content : ''+app.undownloadUserAmount, properties : {align : "center"}},
					           {content : operation, properties : {align : "center"}}]);
				}.bind(this));
			}.bind(this)
		});
		paging.load();
	},
	openSubscribeTable : function() {
		var url = ctx + '/html/appVer/?m=listSubscribe&id=' + this.appId;
		var table = new HtmlTable({
			properties: {
		        border: 0,
		        //"class" : "minfo",
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','手机号码','用户姓名','SEID']
		});
		var div = new Element('div', {"class" : "minfo"});
		table.inject(div);
		div.inject(this.box.messageBox.getElement('div[id=grid_]'));
		
		var paging = {};
		paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:this.box.messageBox.getElement('div[id=nextpage_]'), showNumber: true, showText : false},
			onAfterLoad : function(data) {
				table.empty();
				
				data.result.forEach(function(info, index) {
					var whitespace = '&nbsp;';
					var mobileNo = info.mobileNo ? info.mobileNo : whitespace;
					var name = info.customer_nickName ? info.customer_nickName : whitespace;
					var seid = info.card_cardNo ? info.card_cardNo : whitespace;
					table.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : mobileNo, properties : {align : "center"}}, 
					           {content : name,    properties : {align : "center"}}, 
					           {content : seid, properties : {align : "center"}}]);
				});
			}
		});
		paging.load();
	}
});

//------------------------------------- 华丽的分割线 ---------------------------------------------------
function checkAid(el) {
	//取原值
	var originalAid = '';
	if($chk($('originalAid'))) {
		originalAid = $('originalAid').get('value');
	}
	
	var bln = false;
	new Request.JSON({
		async : false,
		url : ctx + '/html/securityDomain/?m=checkAid',
		onSuccess : function(data) {
			bln = data.success;
			if(!bln) el.errors.push('AID:'+el.value+'已经存在');
		}
	}).post({aid : el.value, originalAid : originalAid});
	return bln;
}

function checkAidStartWithRid(el) {
	var rid = $('rid').get('value');
	
	var aid = el.value.toUpperCase();
	aid = aid.substr(0,10);
	//alert('rid:'+rid + '\naid:' + aid + '\n' + (rid == aid));
	
	if(rid != aid) {
		el.errors.push('RID不匹配');
		return false;
	} else {
		return true;
	}
}

function checkHex(el) {
	if(el.value.test(/[^0-9a-fA-F]/)) {
		el.errors.push('必须是十六进制');
		return false;
	} else if(el.value.length % 2 != 0) {
		el.errors.push('必须是偶数');
		return false;
	} else {
		return true;
	}
}