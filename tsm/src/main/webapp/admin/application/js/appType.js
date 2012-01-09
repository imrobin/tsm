var App = App ? App : {};

App.Type = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawAppTypeBox();
		this.drawGrid();
	},
	drawAppTypeBox : function() {
		this.appTypeBox = new LightFace( {
			draggable : true,
			width : 750,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
				this.parentMenuId = null;
			}.bind(this),
			buttons : [ {
				title : '保 存',
				event : function() {
					this.appTypeForm.getElement('button').click();
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
			url : ctx + '/html/applicationType/?m=getByCriteria',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.appTypeBox.options.title = '新增应用类型';
					this.appTypeBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.appTypeBox.options.content = $('appTypeDiv').get('html');
					this.appTypeBox.addEvent('open', this.openNewAppType.bind(this));
					this.appTypeBox.open();
					this.addFileUploadEvent();
					this.appTypeBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的应用类型');
						return;
					}
					this.appTypeBox.options.title = '修改应用类型';
					this.appTypeBox.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.appTypeBox.options.content = $('appTypeDiv').get('html');
					this.appTypeBox.addEvent('open', this.openEditAppType.bind(this));
					this.appTypeBox.open();
					this.addFileUploadEvent();
					this.appTypeBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的应用类型');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/applicationType/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									typeId : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该应用类型删除吗？<br/>删除一级类型会自动将所属二级类型全部删除');
				}.bind(this)
			} , {
				name : '首页展示设置',
				icon : ctx + '/admin/images/define.png',
				handler : function() {
					var winGrid = new JIM.UI.WinGrid({
						url : ctx + "/html/applicationType/?m=getAllTopLevel",
						multipleSelection : true,
						height : 330,
						width : 800,
						pageMode : false,
						maxSelection : 4,
						callBack : {'name' : 'showIndex' , 'value' : '1'},
						onSelect : function(selcet,value){
							if(selcet.checked && winGrid.selectIds.length == 4) {
								new LightFace.MessageBox().error("首页展示应用类型最多4个");
								return false;
							}
							return true;
						},
						winButtons : [
								{
									title : '完成设置',
									color : 'blue',
									event : function() {
										if (!$chk(winGrid.selectIds) || winGrid.selectIds.length == 0) {
											new LightFace.MessageBox().error('请先选择4个应用类型');
											return;
										}
										if(winGrid.selectIds.length == 4) {
											new Request.JSON( {
												url : ctx + '/html/applicationType/?m=setShowIndex&ids=' + winGrid.selectIds,
												async : false,
												onSuccess : function(data) {
													if(data.success) {
														new LightFace.MessageBox().info('操作成功！');
													} else {
														new LightFace.MessageBox().error(e.message);
													}
												}
											}).post();
										} else {
											new LightFace.MessageBox().error('请选择4个应用类型');
										}
									}
								}, {
									title : '退出',
									event : function() {
										this.close();
									}
								} ],
						drawButtons : false,
						drawSearch : false,
						columnModel : [ {
							dataName : 'id',
							identity : true
						}, {
							title : '名称',
							dataName : 'name'
						} , {
							title : '级别',
							dataName : 'typeLevel'
						}],
						searchButton : false,
						searchBar : {
							filters : []
						},
						headerText : '首页展示设置'
					});
				}
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '应用类型名',
				dataName : 'name'
			}, {
				title : '类别',
				dataName : 'classify'
			}, {
				title : '应用类型等级',
				dataName : 'typeLevel'
			}, {
				title : '父类型',
				dataName : 'parentType_name'
			}],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '应用类型名：',
					name : 'search_LIKES_name',
					type : 'text'
				} ]
			},
			headerText : '应用类型管理',
			headerImage : ctx + '/admin/images/text_padding_left.png'
		});
	},
addFileUploadEvent : function() {
		var uploadSuccessEventHandler = function (file, server_data) {
			var dd = this.appTypeBox.messageBox.getElement('dd[id=thumbnailsDd]');
			var logoFileName = this.appTypeBox.messageBox.getElement('input[id=logoFileName]');
			logoFileName.empty();
			var thumbnails = this.appTypeBox.messageBox.getElement('[id=thumbnails]');
			thumbnails.empty();
			
			var serverData = JSON.decode(server_data);
			var filename = serverData.message.fileName;
			var tempRalFilePath = '/' + serverData.message.tempRalFilePath;
			var src = ctx + tempRalFilePath;
			
			//thumbnails
			dd.set('style', '');
			new Element('img', {src : src, alt : filename, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
			
			//logoPath
			var logoPath = this.appTypeBox.messageBox.getElement('input[id=logoPath]');
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
 			file_queue_error_handler : function(){
 					new LightFace.MessageBox().error('上传图片不能超过2M');
 			},
 			file_dialog_complete_handler : fileDialogComplete,
 			upload_progress_handler : uploadProgress,
 			upload_error_handler : uploadError,
 			upload_success_handler : uploadSuccessEventHandler,
 			upload_complete_handler : uploadComplete,

 			// Button Settings
 			button_image_url : ctx + "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
 			button_placeholder : this.appTypeBox.messageBox.getElement('[id="spanButtonPlaceholder"]'),
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
	addParentAppTypeSelect : function(value) {
		var select = this.appTypeForm.getElement('select[name=parentType_id]');
		var img = new Element('img', {
			'src' : ctx + '/admin/images/ajax-loader.gif',
			'class' : 'icon16',
			width : 16, 
			height : 16
		}).inject(select, 'after');
		new Request.JSON( {
			url : ctx + '/html/applicationType/?m=getByCriteria',
			async : false,
			onSuccess : function(data) {
				select.empty();
				if (data.success) {
					$each(data.result, function(result, i){
						select.options.add(new Option(result.name, result.id));
					}.bind(this));
					if ($chk(value)) {
						select.set('value', value);
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
				img.dispose();
			}.bind(this)
		}).get({search_EQI_typeLevel : 1, page_pageSize : 10000000});
	},
	changeAppTypeLevel : function(){
		var parentType = this.appTypeForm.getElement('select[name=parentType_id]');
		var level = this.appTypeLevel.get('value');
		if (level == 1) {
			new Element('select', {'disabled' : 'disabled'}).inject(parentType, 'after');
			parentType.setStyle('display', 'none');
		} else if (level == 2) {
			if (parentType.options.length == 0) {
				this.addParentAppTypeSelect();
			}
			parentType.setStyle('display', '');
			var showSelect = parentType.getNext('select');
			if ($chk(showSelect)) {
				showSelect.dispose();
			}
			if ($chk(this.originalLevel) && this.originalLevel == 1) {
				var idInput = this.appTypeBox.messageBox.getElement('input[name="id"]');
				for ( var i = 0; i < parentType.options.length; i++) {
					if (parentType.options[i].value == idInput.get('value')) {
						parentType.options.remove(i);
						break;
					}
				}
			}
		} else {
			alert('error');
		}
	},
	openNewAppType : function() {
		this.appTypeForm = this.appTypeBox.messageBox.getElement('form');
		this.appTypeForm.set('action', ctx + '/html/applicationType/?m=add');
		this.appTypeLevel = this.appTypeForm.getElement('select[name=typeLevel]');
		this.appTypeLevel.addEvent('change', this.changeAppTypeLevel.bind(this));
		this.appTypeLevel.fireEvent('change');
		this.addValidate();
	},
	openEditAppType : function() {
		var selectIds = this.grid.selectIds;
		this.appTypeForm = this.appTypeBox.messageBox.getElement('form');
		this.appTypeForm.set('action', ctx + '/html/applicationType/?m=update');
		this.appTypeLevel = this.appTypeForm.getElement('select[name=typeLevel]');
		this.appTypeLevel.addEvent('change', this.changeAppTypeLevel.bind(this));
		this.addValidate();
		new Request.JSON( {
			url : ctx + '/html/applicationType/?m=getType',
			onSuccess : function(data) {
				if (data.success) {
					var idInput = this.appTypeBox.messageBox.getElement('input[name="id"]');
					idInput.set('value', data.message.id);
					var nameInput = this.appTypeBox.messageBox.getElement('input[name="name"]');
					nameInput.set('value', data.message[nameInput.get('name')]);
					var classifyInput = this.appTypeBox.messageBox.getElement('input[name="classify"]');
					classifyInput.set('value', data.message[classifyInput.get('name')]);
					this.appTypeLevel.set('value', data.message[(this.appTypeLevel.get('name') + 'Original')]);
					this.originalLevel = this.appTypeLevel.get('value');
					this.changeAppTypeLevel();
					if (this.appTypeLevel.get('value') == 2) {
						this.addParentAppTypeSelect(data.message.parentType_id);
					}
					var hasLogo = data.message.hasLogo;
					if(hasLogo) {
						var src = ctx + '/html/applicationType/?m=loadTypeLogo&id=' + data.message.id + '&t='+new Date().getTime();
						var dd = this.appTypeBox.messageBox.getElement('dd[id=thumbnailsDd]');
						var thumbnails = this.appTypeBox.messageBox.getElement('[id=thumbnails]');
						dd.set('style', '');
						new Element('img', {src : src, alt : data.message.name, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
						var logoFileName = this.appTypeBox.messageBox.getElement('input[id=logoFileName]');
						logoFileName.set('value', data.message.name + '.jpg');
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get({id : selectIds[0]});
	},
	addValidate : function() {
		this.formCheck = new FormCheck(this.appTypeForm, {
			submit : false,
			zIndex : this.appTypeBox.options.zIndex,
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
		new Request.JSON( {
			url : this.appTypeForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.appTypeBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.appTypeForm.toQueryString());
	}
});
