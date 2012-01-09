var ServiceProvider = ServiceProvider ? ServiceProvider : {};
var formCheck;
ServiceProvider.sp = new Class({
	options : {},
	initialize : function() {
		this.drawGrid();
		this.drawSpBox();
	},
	drawSpBox : function() {
		this.spBox = new LightFace( {
			width : 800,
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
				title : '保 存',
				event : function() {
					this.form.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.messageBox.dispose();
					this.close();
				}
			} ]
		});
		
		this.spBoxInfo = new LightFace( {
			width : 700,
			height : 300,
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
			url : ctx + '/html/spBaseInfo/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.spBox.options.title = '新增应用提供商信息';
					this.spBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.spBox.options.content = $('spDivAdd').get('html');
					this.spBox.addEvent('open', this.openNewSp.bind(this));
					this.spBox.open();
					this.spBox.removeEvents('open');
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
					
					var bln = false;
					new Request.JSON({
						async : false,
						url : ctx + '/html/spBaseInfo/?m=getSp',
						onSuccess : function(result) {
							if(result.success) {
//								if(result.message.statusOriginal == 0) bln = true;
//								else 
								bln = result.message.hasLock == 1;
							}
						}
					}).post({spId : selectIds[0]});
					
					//alert(bln);
					if(!bln) {
						new LightFace.MessageBox().error('当前记录正在审核中，审核员处理完毕后，方可再修改');
						return;
					}
					/* */
					this.spBox.options.title = '修改应用提供商信息';
					this.spBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.spBox.options.content = $('spDivEdit').get('html');
					this.spBox.addEvent('open', this.openEditSp.bind(this));
					this.spBox.open();
					this.spBox.removeEvents('open');
					
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
					var id = selectIds[0];
					var url = ctx + '/html/spBaseInfo/?m=cancelApplyForAdmin';
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
								}).post( {id : id, t : new Date().getTime()});
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
					
					this.spBoxInfo.options.title = '查看应用提供商信息';
					this.spBoxInfo.options.titleImage = ctx + '/admin/images/test.png';
					this.spBoxInfo.options.content = $('spDivEdit').get('html');
					this.spBoxInfo.addEvent('open', this.openInfoSp.bind(this));
					this.spBoxInfo.open();
					this.spBoxInfo.removeEvents('open');
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
					this.spBoxInfo.options.title = '查看应用提供商审核信息';
					this.spBoxInfo.options.titleImage = ctx + '/admin/images/test.png';
					this.spBoxInfo.options.content = document.getElement('[name="requistionDiv"]').get('html');
					this.spBoxInfo.addEvent('open', this.openTable.bind(this));
					this.spBoxInfo.open();
					this.spBoxInfo.removeEvents('open');
				}.bind(this)
			}, {
				name : '下载附件',
				icon : ctx + '/admin/images/test.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					var id = selectIds[0];
					new Request.JSON({
						url : ctx + '/html/spBaseInfo/?m=getSp',
						async : false,
						onSuccess : function(result) {
							if(result.success) {
								var sp = result.message;
								if($chk(sp.attachmentName)) {
									window.location.href = ctx + '/html/spBaseInfo/?m=downloadAttachment&id=' + selectIds[0];
								} else {
									new LightFace.MessageBox({
										onClose : function() {
											
										}
									}).error('文件不存在');
								}
							}
						}
					}).post({spId : id});
					
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用提供商编号',
				dataName : 'no'
			}, {
				title : '应用提供商名称',
				dataName : 'name'
			}, {
				title : '应用提供商简称',
				dataName : 'shortName'
			}, {
				title : '所在地 ', dataName : 'locationNo'
			}, {
				title : '地址',
				dataName : 'address'
			}, {
				title : '邮箱地址',
				dataName : 'sysUser_email'
			}, {
				title : '企业性质', dataName : 'firmNature'
			},
			{
				title : '状态',
				dataName : 'status'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '状态：',
					name : 'search_EQI_status',
					type : 'select',
					data : {
						'' : '全部',
						1 : '审核通过',
						0 : '待审核'
					}
				}, {
					title : '应用提供商名称：',
					name : 'search_LIKES_name',
					type : 'text'
				}, {title : '应用提供商编号：', name : 'search_LIKES_no', type : 'text'} ]
			},
			headerText : '应用提供商管理',
			headerImage : ctx + '/images/user_icon_32.png'
		});
	},
	addLocationEvent : function() {
		this.spBox.getBox().getElement('[id="location"]').addEvent('click', function() {
			formCheck.removeError(this.spBox.getBox().getElement('[id="location"]'));
			new CityPicker().showCity(this.spBox.getBox().getElement('[id="location"]'));
		}.bind(this));
	},
	addEmailCheck : function() {
		this.spBox.getBox().getElement('[id="email"]').addEvent('blur', function() {
			var email = this.spBox.getBox().getElement('[id="email"]').get('value');
			
			var request = new Request.JSON({
				url : ctx + '/html/customer/?m=checkEmail',
				async : false,
				onSuccess : function(result) {
					if(result.success) {
						new LightFace.MessageBox({
							onClose : function() {
								
							}
						}).error('该邮箱'+email+'已经被注册，请输入其它邮箱帐户');
					}
				}
			});
			request.post({email : email, t : new Date().getTime()});
		}.bind(this));
	},
	addFileUploadEvent : function() {
		
		var uploadSuccessEventHandler = function (file, server_data) {
			var dd = this.spBox.messageBox.getElement('dd[id=thumbnailsDd]');
			var logoFileName = this.spBox.messageBox.getElement('input[id=logoFileName]');
			logoFileName.empty();
			var thumbnails = this.spBox.messageBox.getElement('[id=thumbnails]');
			thumbnails.empty();
			
			var serverData = JSON.decode(server_data);
			var filename = serverData.message.fileName;
			var tempRalFilePath = '/' + serverData.message.tempRalFilePath;
			var src = ctx + tempRalFilePath;
			
			//thumbnails
			dd.set('style', '');
			new Element('img', {src : src, alt : filename, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
			
			//logoPath
			var logoPath = this.spBox.messageBox.getElement('input[id=logoPath]');
			if($chk(logoPath)) logoPath.set('value', tempRalFilePath);
			
			logoFileName.set('value', filename);
		}.bind(this);
		
		swfu = new SWFUpload({
 			// Backend Settings
 			upload_url: ctx + "/html/commons/?m=upload",
 			post_params: {},

 			// File Upload Settings
 			file_size_limit : "2 MB",	// 2MB
 			file_types : "*.jpg;*.png",
 			file_types_description : "JPG Images",
 			file_upload_limit : "0",

 			// Event Handler Settings - these functions as defined in Handlers.js
 			//  The handlers are not part of SWFUpload but are part of my website and control how
 			//  my website reacts to the SWFUpload events.
 			file_queue_error_handler : fileQueueError,
 			file_dialog_complete_handler : fileDialogComplete,
 			upload_progress_handler : uploadProgress,
 			upload_error_handler : uploadError,
 			upload_success_handler : uploadSuccessEventHandler,
 			upload_complete_handler : uploadComplete,

 			// Button Settings
 			button_image_url : ctx + "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
 			button_placeholder : this.spBox.messageBox.getElement('span[id=spanButtonPlaceholder]'),
 			button_width: 180,
 			button_height: 18,
 			button_text : '<span class="button">请选择图片<span class="buttonSmall">(2 MB 最大)</span></span>',
 			button_text_style : '.button { font-family: "微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
 			button_text_top_padding: 0,
 			button_text_left_padding: 18,
 			button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
 			button_cursor: SWFUpload.CURSOR.HAND,
 			
 			// Flash Settings
 			flash_url : ctx + "/lib/uploadManager/swfupload.swf",

 			custom_settings : {
 				upload_target : "divFileProgressContainer"
 			},
 			
 			// Debug Settings
 			debug: false
 		});
		
	},
	openNewSp : function() {
		this.form = this.spBox.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/spBaseInfo/?m=add');

		//add event
		//this.addLocationEvent();
		this.addFileUploadEvent();
		this.addProvice();
		this.addValidate();
	},
	openEditSp : function() {
		var selectIds = this.grid.selectIds;
		
		this.form = this.spBox.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/spBaseInfo/?m=edit');
		//add event
		//this.addLocationEvent();
		this.addProvice();
		this.addFileUploadEvent();
		
		this.addValidate();
		
		//load SP data
		new Request.JSON( {
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getSp&spId=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.spBox.messageBox.getElements('input,select,radio');
					$each(inputs, function(input, i) {
						if(input.get('type') == 'radio') {
							if(input.get('value') == data.message[input.get('name')]) {
								input.set('checked', 'checked');
							}
						} else if(input.get('name') == 'locationNo') {
							input.set('value', data.message[input.get('name')]);
						} else {
							input.set('value', data.message[input.get('name')]);
						}
						
						if(input.get('name') == 'type') {
							//var options = input.getElements('option');
							input.getElements('option').each(function(e, index) {
								if(e.get('value') == data.message['typeOriginal']) {
									e.set('selected', 'selected');
								}
							});
						}
						
						if(input.get('id') == 'no') input.set('value', data.message['no']);
						if(input.get('id') == 'rid') {
							input.set('value', data.message['rid']);
							if(data.message['statusOriginal'] == 1) {
								this.form.rid.set('readonly','readonly');
								this.spBox.messageBox.getElement('[id="_rid"]').appendText('只读');
							}
						}
						if(input.get('id') == 'email') {
							input.set('value', data.message['sysUser_email']);
							if(data.message['statusOriginal'] == 1) {
								this.form.email.set('readonly','readonly');
								this.spBox.messageBox.getElement('[id="_email"]').appendText('只读');
							}
						}
						
					}.bind(this));
					
					new Element('input', {id : 'emailOrg', type : 'hidden', value : data.message['sysUser_email']}).inject(this.spBox.messageBox);
					new Element('input', {id : 'nameOrg', type : 'hidden', value : data.message['name']}).inject(this.spBox.messageBox);
					new Element('input', {id : 'shortNameOrg', type : 'hidden', value : data.message['shortName']}).inject(this.spBox.messageBox);
					new Element('input', {id : 'registrationNoOrg', type : 'hidden', value : data.message['registrationNo']}).inject(this.spBox.messageBox);
					new Element('input', {id : 'certificateNoOrg', type : 'hidden', value : data.message['certificateNo']}).inject(this.spBox.messageBox);
					new Element('input', {id : 'legalPersonIdNoOrg', type : 'hidden', value : data.message['legalPersonIdNo']}).inject(this.spBox.messageBox);
					new Element('input', {id : 'contactPersonMobileNoOrg', type : 'hidden', value : data.message['contactPersonMobileNo']}).inject(this.spBox.messageBox);
					
					var hasLogo = data.message.hasLogo;
					if(hasLogo) {
						var src = ctx + '/html/spBaseInfo/?m=loadSpFirmLogo&id=' + data.message.id + '&t='+new Date().getTime();
						var dd = this.spBox.messageBox.getElement('dd[id=thumbnailsDd]');
						var thumbnails = this.spBox.messageBox.getElement('[id=thumbnails]');
						dd.set('style', '');
						new Element('img', {src : src, alt : data.message.name, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
						var logoFileName = this.spBox.messageBox.getElement('input[id=logoFileName]');
						logoFileName.set('value', data.message.name + '.jpg');
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	openInfoSp : function() {
		this.addProvice('openInfoSp');
		var selectIds = this.grid.selectIds;
		//this.form = 
		var p = this.spBoxInfo.messageBox.getElements('p[class="explain left"]');
		$each(p, function(e, index) {e.empty();});
		
		var span = this.spBoxInfo.messageBox.getElements('span');
		$each(span, function(e, index) {e.empty();});
		
		new Request.JSON( {
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getSp&spId=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.spBoxInfo.messageBox.getElements('input,select,radio');
					$each(inputs, function(input, i) {
						if(input.get('type') == 'radio') {
							if(input.get('value') == data.message[input.get('name')]) {
								input.set('checked', 'checked');
							}
							input.set('disabled','disabled');
						} else if(input.get('name') == 'locationNo') {
							input.set('value', data.message[input.get('name')]);
							input.set('disabled','disabled');
						} else {
							input.set('value', data.message[input.get('name')]);
						}
						
						if(input.get('name') == 'type') {
							input.getElements('option').each(function(e, index) {
								if(e.get('value') == data.message['typeOriginal']) {
									e.set('selected', 'selected');
								}
							});
							input.set('disabled','disabled');
						}
						
						if(input.get('name') == 'firmNature' || input.get('name') == 'firmScale') {
							input.set('disabled','disabled');
						}
						if(input.get('id') == 'no') input.set('value', data.message['no']);
						if(input.get('id') == 'rid') input.set('value', data.message['rid']);
						if(input.get('id') == 'email') input.set('value', data.message['sysUser_email']);
						input.set('readonly','readonly');
					});
					//this.spBoxInfo.messageBox.getElement('[id=pcIconFile]').setStyle('display','none');
					var hasLogo = data.message.hasLogo;
					if(hasLogo) {
						var src = ctx + '/html/spBaseInfo/?m=loadSpFirmLogo&id=' + data.message.id + '&t='+new Date().getTime();
						var dd = this.spBoxInfo.messageBox.getElement('dd[id=thumbnailsDd]');
						var thumbnails = this.spBoxInfo.messageBox.getElement('[id=thumbnails]');
						dd.set('style', '');
						new Element('img', {src : src, alt : data.message.name, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
						var logoFileName = this.spBoxInfo.messageBox.getElement('input[id=logoFileName]');
						logoFileName.set('value', data.message.name + '.jpg');
					}else{
						//无图片情况
						var logoFileName = this.spBoxInfo.messageBox.getElement('input[id=logoFileName]');
						logoFileName.set('value', '无');
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	openTable : function() {
		var selectIds = this.grid.selectIds;
		var sdId = selectIds[0];
		var url = ctx + '/html/requistion/?m=list&page_orderBy=reviewDate_desc&search_EQL_originalId='+sdId+'&t=' + new Date().getTime();
		this.spBoxInfo.messageBox.setStyle('overflow-y', 'hidden');
		var table = this.spBoxInfo.messageBox.getElement('div[name=requistionTable]');
		new JIM.UI.Grid(table, {
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
			],
			header : false,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
		
	},
	addValidate : function() {
		formCheck = new FormCheck(this.form, {
			submit : false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure:1
			},
			onValidateSuccess : function() {//校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	addProvice : function(flag) {
		var pronvince = {};
		if(flag) {
			pronvince = this.spBoxInfo.messageBox.getElement('select[name=locationNo]');
		} else {
			pronvince = this.spBox.messageBox.getElement('select[name=locationNo]');
		}
		
		var userProvince = {};
		new Request.JSON({
			async : false,
			url : ctx + '/html/user/?m=getCurrentUser',
			onSuccess : function(data) {
				if(data.success) {
					userProvince = data.message.province;
				}
			}
		}).post();
		var options = pronvince.options;
		if(userProvince) {
			options.add(new Option(userProvince,userProvince));
		} else {
			options.add(new Option("全网","全网"));
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
		}
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.spBox.messageBox.dispose();
							this.spBox.close();
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

/* ***** formcheck custom function ***** */
function validateEmail(el) {
	var bln = false;
	var email = el.value;
	new Request.JSON({
		async : false,
		url : ctx + '/html/customer/?m=checkEmail',
		onSuccess : function(result) {
			bln = !result.success;
			if(!bln) {
				//该邮箱'+email+'已经被注册，请输入其它邮箱帐户
				el.errors.push('该邮箱'+email+'已经被注册，请输入其它邮箱帐户');
			}
		}
	}).post({email : email, t : new Date().getTime()});
	
	return bln;
}

function validateEmailWithoutSelf(el) {
	var orgVal = $('emailOrg').get('value');
	if(el.value == orgVal) {
		return true;
	} else {
		return validateEmail(el);
	}
	
}