var MobileTypeManager = MobileTypeManager ? MobileTypeManager : {};

MobileTypeManager.type = new Class({
	options : {},
	initialize : function() {
		this.drawGrid();
		this.drawMtBox();
	},
	drawMtBox : function() {
		this.mtBox = new LightFace( {
			draggable :true,
			//width:720,
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
			url : ctx + '/html/mobile/?m=getMobileByKeywordForIndex',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.mtBox.options.title = '新增手机型号';
					this.mtBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.mtBox.options.content = $('mtDivAdd').get('html');
					this.mtBox.addEvent('open', this.openNewMt.bind(this));
					this.mtBox.open();
					this.mtBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的手机型号');
						return;
					}
					
					this.mtBox.options.title = '修改手机型号';
					this.mtBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.mtBox.options.content = $('mtDivEdit').get('html');
					this.mtBox.addEvent('open', this.openEditMt.bind(this));
					this.mtBox.open();
					this.mtBox.removeEvents('open');
				}.bind(this)
			 },
			 {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的手机型号');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/mobile/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {
												var page = this.grid.getPage();
												if (page.total <= 1) {
													this.grid.barDiv.getElementById('search_LIKES_brandChs').empty();
													this.grid.barDiv.getElementById('search_LIKES_brandChs').fireEvent('load', ctx + '/html/mobile/?m=getAllBrand');
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
					}).confirm('确认要将该手机型号删除吗？');
				}.bind(this)
			}],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '品牌中文名',
				dataName : 'brandChs'
			}, {
				title : '品牌英文名',
				dataName : 'brandEng'
			}, {
				title : '型号',
				dataName : 'type'
			}, {
				title : '操作系统版本', dataName : 'originalOsKey'
			}, {
				title : 'J2ME版本',
				dataName : 'j2meKey'
			} ],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '品牌中文名：',
					id : 'search_LIKES_brandChs',
					name : 'search_LIKES_brandChs',
					type : 'select',
					remote : true
				}, {
					title : '手机型号：',
					name : 'search_LIKES_type',
					type : 'text'
				} ]
			},
			headerText : '手机型号管理',
			headerImage : ctx + '/images/mobile_icon.png'
		});
		this.grid.barDiv.getElementById('search_LIKES_brandChs').fireEvent('load', ctx + '/html/mobile/?m=getAllBrand');
	},

	openNewMt : function() {
		this.form = this.mtBox.messageBox.getElement('[name="addMobileForm"]');
		this.form.set('action', ctx + '/html/mobile/?m=add');
		this.loadParams("os");
		this.loadParams("j2me");
		this.addBrand();
		this.addFileUploadEvent();
		this.addValidate();
		this.addTypeEvent('blur');
	},
	openEditMt : function() {
		var selectIds = this.grid.selectIds;
		this.form = this.mtBox.messageBox.getElement('[name="editMobileForm"]');
		this.form.set('action', ctx + '/html/mobile/?m=edit');
		this.loadParams("os");
		this.loadParams("j2me");
		this.addBrand();
		this.addFileUploadEvent();
		this.addValidate();
		this.addTypeEvent('change');
		new Request.JSON( {
			url : ctx + '/html/mobile/?m=getMobileType&id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.mtBox.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
							input.set('value', data.message[input.get('name')]);
						});
					var src = ctx + '/html/mobile/?m=getMobilePic&id=' + data.message.id + '&t='+new Date().getTime();
					var dd = this.mtBox.messageBox.getElement('dd[id=thumbnailsDd]');
					var thumbnails = this.mtBox.messageBox.getElement('[id=thumbnails]');
					dd.set('style', '');
					new Element('img', {src : src, alt : data.message.type, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
					var logoFileName = this.mtBox.messageBox.getElement('input[id=logoFileName]');
					logoFileName.set('value', data.message.id + '.jpg');
					this.brand.set('value', data.message.brandEng);
				}
			    else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	loadParams : function(type){
		var os ;
		if(type=='os'){
		os = this.form.getElement('select[name="originalOsKey"]');
		}else if(type='j2me'){
			os = this.form.getElement('select[name="j2meKey"]');
		}
		var request = new Request.JSON({
			url : ctx+'/html/mobile/?m=getParamsByType',
			async : false,
			onSuccess : function(data) {
				var a = data.message;
				    os.options.add(new Option("--请选择--",""));
				if(data.success) {
					Array.each(a, function(item, index){
					os.options.add(new Option(item.key,item.value));	
					});
				}else{
					
				}
			}
		});
		request.post('type='+type);
	},
	addBrand : function() {
		this.brand = this.mtBox.messageBox.getElement('[name="brandSelect"]');
		new Request.JSON( {
			url : ctx + '/html/sysParams/?m=getParamsByType',
			async : false,
			onSuccess : function(data) {
				if (data.success) {
					this.brand.empty();
					new Element('option', {'value' : '', 'text' : '-------请选择-------'}).inject(this.brand);
					$each(data.result, function(result) {
						new Element('option', {'value' : result.value, 'text' : result.key + '(' + result.value + ')'}).inject(this.brand);
					}.bind(this));
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post({type : 'MobileBrand'});
		this.brand.addEvent('change', function() {
			var brandChs = this.mtBox.messageBox.getElement('[name="brandChs"]');
			var brandEng = this.mtBox.messageBox.getElement('[name="brandEng"]');
			var mobileT = this.mtBox.messageBox.getElement('[name="type"]');
			var text = this.brand.options[this.brand.selectedIndex].text;
			brandChs.set('value', text.substring(0, text.lastIndexOf('(')));
			brandEng.set('value', this.brand.get('value'));
			new Request.JSON( {
				url : ctx + '/html/mobile/?m=checkMobileTypeByBrandAndType',
				async : false,
				onSuccess : function(data) {
					if (data.success) {
						
					}
				    else {
				    	mobileT.set('value','');
						new LightFace.MessageBox().error(data.message);
					}
				}.bind(this)
			}).post({brand : brandChs.get('value'), type : mobileT.get('value')});
		}.bind(this));
	},
	addTypeEvent : function(event) {
		var mobileT = this.mtBox.messageBox.getElement('[name="type"]');
		var brandChs = this.mtBox.messageBox.getElement('[name="brandChs"]');
		mobileT.addEvent('blur', function(e){
			new Request.JSON( {
				url : ctx + '/html/mobile/?m=checkMobileTypeByBrandAndType',
				onSuccess : function(data) {
					if (data.success) {
						
					}
				    else {
						new LightFace.MessageBox().error(data.message);
						mobileT.set('value','');
					}
				}.bind(this)
			}).post({brand : brandChs.get('value'), type : mobileT.get('value')});
	    }.bind(this));
	},
	addValidate : function() {
		new FormCheck(this.form, {
			submit : false,
			zIndex : this.mtBox.options.zIndex,
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
			var dd = this.mtBox.messageBox.getElement('dd[id=thumbnailsDd]');
			var logoFileName = this.mtBox.messageBox.getElement('input[id=logoFileName]');
			logoFileName.empty();
			var thumbnails = this.mtBox.messageBox.getElement('[id=thumbnails]');
			thumbnails.empty();
			
			var serverData = JSON.decode(server_data);
			var filename = serverData.message.fileName;
			var tempRalFilePath = '/' + serverData.message.tempRalFilePath;
			var src = ctx + tempRalFilePath;
			
			//thumbnails
			dd.set('style', '');
			new Element('img', {src : src, alt : filename, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
			
			//logoPath
			var logoPath = this.mtBox.messageBox.getElement('input[id=logoPath]');
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
 			button_placeholder : this.mtBox.messageBox.getElement('span[id=spanButtonPlaceholder]'),
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
	submitForm : function() {
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.mtBox.close();
							this.grid.selectIds = [];
							this.grid.barDiv.getElementById('search_LIKES_brandChs').empty();
							this.grid.barDiv.getElementById('search_LIKES_brandChs').fireEvent('load', ctx + '/html/mobile/?m=getAllBrand');
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