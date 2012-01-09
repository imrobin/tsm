var Sp = Sp ? Sp :{};

Sp.audit = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawSpBox();
		this.drawGrid();
	},
	drawSpBox : function() {
		this.spBox = new LightFace( {
			draggable : true,
			initDraw : false,
			height: 150,
			width: 750,
			title: '审核应用提供商',
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '审核通过',
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
		
		this.spBoxInfo = new LightFace( {
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
			url : ctx + '/html/spBaseInfo/?m=indexAudit',
        	multipleSelection: false,
        	buttons : [{
        		name : '审核通过',
        		icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的应用提供商');
						return;
					}
					var id = selectIds[0];
					//取得一行的数据
					var tr = this.grid.tableDiv.getElement('input[value='+id+']').getParent('td').getSiblings('td');
					var type = tr[1].get('text');
					if(type == 'SP注册申请') {
						//文件上传弹出框
						this.spBox.options.content = $('attachmentForm').get('html');
						this.spBox.addEvent('open', this.openDialog.bind(this));
						this.spBox.open();
						this.spBox.removeEvents('open');
					} else {
						new LightFace.MessageBox({
							onClose : function(result) {
								if (result) {
									new Request.JSON( {
										url : ctx + '/html/spBaseInfo/?m=audit&status=yes',
										onSuccess : function(data) {
											if (data.success) {
												new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
											} else {
												new LightFace.MessageBox().error(data.message);
											}
										}.bind(this)
									}).post({ id:this.grid.selectIds[0] });
								}
							}.bind(this)
						}).confirm('确认要审核通过吗？');
					}
					/*
					 */
					
				}.bind(this)
			}, 
			{name : '审核不通过', 
			 icon : ctx + '/admin/images/delete.png',	
			handler : function(){
			var selectIds = this.grid.selectIds;
			if (!$chk(selectIds) || selectIds.length == 0) {
				new LightFace.MessageBox().error('请先选择列表中的应用提供商');
				return;
			}
			
			new LightFace.MessageBox( {
				onClose : function(result, opinion) {
					if (result) {
						new Request.JSON( {
							url : ctx + '/html/spBaseInfo/?m=audit&status=no',
							onSuccess : function(data) {
								if (data.success) {
									new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
								} else {
									new LightFace.MessageBox().error(data.message);
								}
							}.bind(this)
						}).post( { id : this.grid.selectIds[0] ,opinion: opinion });
					}
				}.bind(this)
			}).prompt('请填写审核不通过原因：', 200, true);
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
				
				this.spBoxInfo.options.title = '查看应用提供商申请信息';
				this.spBoxInfo.options.titleImage = ctx + '/admin/images/test.png';
				this.spBoxInfo.options.content = $('spDivEdit').get('html');
				this.spBoxInfo.addEvent('open', this.openInfoSp.bind(this));
				this.spBoxInfo.open();
				this.spBoxInfo.removeEvents('open');
			}.bind(this)
	   }
        	],
        	columnModel : [ {
				dataName : 'id',
				identity : true
			},
			{title : '状态', dataName : 'requistion_status'},
			{title : '申请类型', dataName : 'applyType'},
			{title : '申请时间', dataName : 'requistion_submitDate'},
			{
				title : '应用提供商编号',
				dataName : 'no'
			}, {
				title : '应用提供商名称',
				dataName : 'name'
			}, {
				title : '所在地 ', dataName : 'locationNo'
			}, {
				title : '地址',
				dataName : 'address'
			}, {
				title : '企业性质', dataName : 'firmNature'
			}],
        	searchButton : true,
        	searchBar : {
				filters : [
				           {
					title : '应用提供商名称：',
					name : 'search_LIKES_name',
					type : 'text'
				}//, {title : '应用提供商编号：', name : 'search_LIKES_no', type : 'text'}
				]
			},
        	headerText : '审核应用提供商',
        	headerImage : ctx + '/images/user_icon_32.png'
        });
	},
	openDialog : function() {
		this.form = this.spBox.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/spBaseInfo/?m=audit&status=yes&id='+this.grid.selectIds[0]);

		this.addFileUploadEvent();
		this.addValidate();
	},
	addFileUploadEvent : function() {
		var uploadSuccessEventHandler = function (file, server_data) {
			//var dd = this.spBox.messageBox.getElement('dd[id=thumbnailsDd]');
			var fileName = this.spBox.messageBox.getElement('input[id=fileName]');
			fileName.empty();
			var thumbnails = this.spBox.messageBox.getElement('[id=thumbnails]');
			thumbnails.empty();
			
			var serverData = JSON.decode(server_data);
			var filename = serverData.message.fileName;
			var tempRalFilePath = '/' + serverData.message.tempRalFilePath;
			//var src = ctx + tempRalFilePath;
			//alert(src);
			//thumbnails
			//dd.set('style', '');
			//new Element('img', {src : src, alt : filename, style : 'height: 90px;width: 90px;'}).inject(thumbnails);
			
			//logoPath
			var filePath = this.spBox.messageBox.getElement('input[id=filePath]');
			if($chk(filePath)) filePath.set('value', tempRalFilePath);
			
			fileName.set('value', filename);
		}.bind(this);
		
		swfu = new SWFUpload({
 			// Backend Settings
 			upload_url: ctx + "/html/commons/?m=upload",
 			post_params: {},

 			// File Upload Settings
 			file_size_limit : "2 MB",	// 2MB
 			//file_types : "*.jpg;*.png;*.rar;*.zip",
 			//file_types_description : "JPG Images",
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
 			button_text : '<span class="button">上传文件<span class="buttonSmall">(2 MB 最大)</span></span>',
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
			onValidateSuccess : function() {
				//校验通过执行load()
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
		
	},
	openInfoSp : function() {

		var selectIds = this.grid.selectIds;
		//load SP data
		new Request.JSON( {
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getSpApply&spId=' + selectIds[0],
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
						input.set('readonly','readonly');
					});
					var hasLogo = data.message.hasLogo;
					if(hasLogo) {
						this.spBoxInfo.messageBox.getElement('[id="pcIconImg"]').set('src', '');
						var src = ctx + '/html/spBaseInfo/?m=loadSpApplyFirmLogo&id=' + data.message.id + '&t='+new Date().getTime();
						this.spBoxInfo.messageBox.getElement('[id="pcIconImg"]').set('src', src);
						this.spBoxInfo.messageBox.getElement('[id="pcIconImg"]').set("styles", {width: '85px',height: '85px'});
					}else{
						this.spBoxInfo.messageBox.getElement('[id=imgP]').set('html','&nbsp;&nbsp;无');
						this.spBoxInfo.messageBox.getElement('[id=imgP]').setStyle('text-align','left');
						this.spBoxInfo.messageBox.getElement('[id=imgP]').set('class','regtext');
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	
	}
});