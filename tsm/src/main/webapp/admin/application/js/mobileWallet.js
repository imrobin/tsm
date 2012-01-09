var MobileWallet = MobileWallet ? MobileWallet : {};

MobileWallet.manage = new Class({
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
			url : ctx + '/html/applicationClient/?m=getAllMobileWallet',
			multipleSelection : false,
			buttons : [ {
				name : '上传',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.mwBox.options.title = '上传手机钱包';
					this.mwBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.mwBox.options.content = $('mobileWalletDivAdd').get('html');
					this.mwBox.addEvent('open', this.openNewmw.bind(this));
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
						new LightFace.MessageBox().error('请先选择列表中的手机钱包');
						return;
					}
					new Request.JSON({
						async : false,
						url : ctx + '/html/applicationClient/?m=getAci',
						onSuccess : function(result) {
							if(result.success) {
								status = result.message;
							}
						}.bind(this)
						}).post({
							clientId : this.grid.selectIds[0], t : new Date().getTime()
						});
					if(status == '2') {
						new LightFace.MessageBox().error('该手机钱包已经发布，不能删除');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/applicationClient/?m=removeClient',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													this.grid.barDiv.getElementById('search_LIKES_name').empty();
													this.grid.barDiv.getElementById('search_LIKES_name').fireEvent('load', ctx + '/html/applicationClient/?m=getAllMobileWallet');
												}
												this.grid.load();
											}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									clientId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该手机钱包删除吗？');
				}.bind(this)
			},
			{
				name : '发布',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的手机钱包');
						return;
					}
					var aci = {};
					new Request.JSON({
						async : false,
						url : ctx + '/html/applicationClient/?m=getAci',
						onSuccess : function(result) {
							if(result.success) {
								status = result.message;
							}
						}.bind(this)
						}).post({
							clientId : this.grid.selectIds[0], t : new Date().getTime()
						});
					if(status == '2') {
						new LightFace.MessageBox().error('该手机钱包已经发布');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/applicationClient/?m=release',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													this.grid.barDiv.getElementById('search_LIKES_name').empty();
													this.grid.barDiv.getElementById('search_LIKES_name').fireEvent('load', ctx + '/html/applicationClient/?m=getAllMobileWallet');
												}
												this.grid.load();
											}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									clientId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要发布该手机钱包吗？');
				}.bind(this)
			}
			],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '名称',
				dataName : 'name'
			}, {
				title : '版本',
				dataName : 'version'
			},{
				title : '开发版本',
				dataName : 'versionCode'
			},
			{
				title : '客户端包名',
				dataName : 'clientPackageName'
			},
			{
				title : '客户端入口类',
				dataName : 'clientClassName'
			},
			{
				title : '支持的操作系统及版本',
				dataName : 'sysRequirment'
			}, {
				title : '文件大小(byte)', 
				dataName : 'size'
			}, {
				title : '创建时间', 
				dataName : 'createDate'
			}, {
				title: '状态',
				dataName : 'status',
				align : 'center'
			}
			],
			searchButton : true,
			searchBar : {
				filters : [ 
                 { title : '状态：',
                   name : 'search_EQI_status',
                   type : 'select',
                   data : {
	                 '' : '全部',
	                  1 : '未发布',
	                  2 : '已发布'
                 }
                 },
				 {
					title : '名称：',
					name : 'search_LIKES_name',
					id   : 'search_LIKES_name',
					type : 'text'
				} ]
			},
			headerText : '手机钱包管理',
			headerImage : ctx + '/images/mobile_icon.png'
		});
	},
	openNewmw : function() {
		this.form = this.mwBox.messageBox.getElement('[name="addClientForm"]');
		this.form.set('action', ctx + '/html/applicationClient/?m=add');
		this.addFileUploadEvent();
		this.checkVersionCode();
		this.addValidate();
		this.addTypeEvent();
	},
	addTypeEvent : function() {
		var sysRequirment = this.mwBox.messageBox.getElement('[name="sysRequirment"]');
			var request = new Request.JSON({
				url : ctx+'/html/mobile/?m=getParamsByType',
				async : false,
				onSuccess : function(data) {
					sysRequirment.empty();
					var a = data.message;
					   //sysRequirment.options.add(new Option("--请选择--",""));
					if(data.success) {
						Array.each(a, function(item, index){
						sysRequirment.options.add(new Option(item.key,item.value));	
						});
					}else{
						
					}
				}
			});
			request.post('type=os');
	},
	checkVersionCode:function(){
		var versionCode = this.mwBox.messageBox.getElement('[name="versionCode"]');
		var sysRequirment = this.mwBox.messageBox.getElement('[name="sysRequirment"]');
		versionCode.addEvent("blur",function(){
			if($chk(versionCode.get('value'))){
			var request = new Request.JSON({
				url : ctx + '/html/applicationClient/?m=checkVersionCode',
				async : false,
				onSuccess : function(data) {
					if(!data.success) {
						new LightFace.MessageBox().error("开发版本必须大于等于"+data.message);
						versionCode.set('value','');
					}
				}
		    });
			request.post('sysType=os&sysRequirment='+sysRequirment.get('value')+"&versionCode="+versionCode.get('value'));
			}
		});	
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
      addFileUploadEvent : function() {
		var uploadSuccessEventHandler = function (file, server_data) {
			var dd = this.mwBox.messageBox.getElement('dd[id=thumbnailsDd]');
			var clientFileName = this.mwBox.messageBox.getElement('input[id=clientFileName]');
			clientFileName.empty();
			var thumbnails = this.mwBox.messageBox.getElement('[id=thumbnails]');
			thumbnails.empty();
			
			var serverData = JSON.decode(server_data);
			var filename = serverData.message.fileName;
			var tempRalFilePath = serverData.message.tempRalFilePath;
			var src = ctx + tempRalFilePath;
			
			dd.set('style', '');
			//clientPath
			var fileUrl = this.mwBox.messageBox.getElement('input[id=fileUrl]');
			var size = this.mwBox.messageBox.getElement('input[id=fileSize]'); 
			size.set('value',serverData.message.fileSize);
			if($chk(fileUrl)) fileUrl.set('value', tempRalFilePath);
			clientFileName.set('value', filename);
		}.bind(this);
		
		swfu = new SWFUpload({
 			
 			upload_url: ctx + "/html/commons/?m=upload",
 			post_params: {},

 			// File Upload Settings
 			file_size_limit : "10 MB",	// 2MB
 			file_types : "*.*",
 			file_types_description : "任意格式文件",
 			file_upload_limit : "0",

 			// Event Handler Settings - these functions as defined in Handlers.js
 			//  The handlers are not part of SWFUpload but are part of my website and control how
 			//  my website reacts to the SWFUpload events.
 			file_queue_error_handler : this.fileQueueError,
 			file_dialog_complete_handler : fileDialogComplete,
 			upload_progress_handler : uploadProgress,
 			upload_error_handler : uploadError,
 			upload_success_handler : uploadSuccessEventHandler,
 			upload_complete_handler : uploadComplete,

 			// Button Settings
 			button_image_url : ctx + "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
 			button_placeholder : this.mwBox.messageBox.getElement('span[id=spanButtonPlaceholder]'),
 			button_width: 180,
 			button_height: 18,
 			button_text : '<span class="button">请选择文件<span class="buttonSmall">(10 MB 最大)</span></span>',
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
	submitForm : function() {
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.mwBox.close();
							this.grid.selectIds = [];
							this.grid.barDiv.getElementById('search_LIKES_name').empty();
							this.grid.barDiv.getElementById('search_LIKES_name').fireEvent('load', ctx + '/html/applicationClient/?m=getAllMobileWallet');
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.form.toQueryString());	
	},
    fileQueueError :function(file,errorCode,message){
		//alert(errorCode + '\n' + message);
		if(errorCode == -110) {
			message = '上传文件大小超过限制';
			new LightFace.MessageBox().error(message);
		}
	}

});