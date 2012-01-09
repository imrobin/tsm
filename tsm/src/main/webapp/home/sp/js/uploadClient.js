Application = {};

Application.Page = new Class({

	Implements : [ Events, Options ],

	applicationVersionId : -1,
	loadFiles : new Array(),
	applets : new Array(),
	downloadOrder : null,
	deleteOrder : null,

	options : {
		applicationVersionId : -1
	},

	initialize : function(options) {
		this.setOptions(options);
		this.applicationVersionId = this.options.applicationVersionId;
		this.ramdon = new Date().valueOf();
	},

	getCilents : function() {
		new JIM.UI.Paging({
			url : ctx + "/html/applicationClient/?m=getByApplicationVersion&applicationVersionId=" + applicationVersionId,
			limit : 15,
			head : {
				el : 'paging',
				showNumber : true,
				showText : false
			},
			onAfterLoad : this.getCilentsCallback.bind(this)
		}).load();
	},

	getCilentsCallback : function(json) {
		if (json.success) {
			$("client").empty();
			json.result.each(function(item) {
				var client = new Application.Client();
				client.showCilent(item);
			});
		}
	}
});

Application.Client = new Class(
		{
			Implements : [ Events, Options ],

			modal : null,
			page : null,
			formId : null,
			tempFileId : null,
			sysRequirmentSelect : null,
			applicationVersionId : -1,

			initialize : function(options) {
				this.setOptions(options);
				this.page = this.options.page;
				this.applicationVersionId = this.options.applicationVersionId;
				this.tempFileId = "file" + new Date().valueOf();
				this.formId = "form" + new Date().valueOf();
				var template = $("clientFormTemlate");
				var html = template.get("html");
				this.modal = new LightFace({
					title : "上传客户端",
					height : 550,
					width : 650,
					content : html,
					resetOnScroll : false,
					buttons : [ {
						title : "确认",
						color : "blue",
						event : function() {
							this.modal.getBox().getElement('button[type="submit"]').click();
						}.bind(this)
					}, {
						title : "取消",
						event : this.close.bind(this)
					} ]
				});
				this.addValidate();
			},
			addValidate : function() {
				var box = this.modal.getBox();
				var form = box.getElement("[title='client']");
				new FormCheck(form, {
					submit : false,
					trimValue : false,
					display : {
						showErrors : 1,
						errorsLocation : 1,
						indicateErrors : 1,
						keepFocusOnError : 0,
						closeTipsButton : 0,
						removeClassErrorOnTipClosure : 1
					},
					onValidateSuccess : function() {// 校验通过执行load()
						this.submit();
					}.bind(this)
				});
			},
			checkVersionCode : function() {
				var box = this.modal.getBox();
				var versionCode = box.getElement('[name="versionCode"]');
				var appVerId = box.getElement("[name='applicationVersionId']")
				var sysRequirment = box.getElement('[name="sysRequirment"]');
				versionCode.addEvent("blur", function() {
					if ($chk(versionCode.get('value'))) {
						var request = new Request.JSON({
							url : ctx + '/html/applicationClient/?m=checkVersionCode',
							async : false,
							onSuccess : function(data) {
								if (!data.success) {
									new LightFace.MessageBox().error("开发版本必须大于等于" + data.message);
									versionCode.set('value', '');
								}
							}
						});
						request.post('sysType=os&sysRequirment=' + sysRequirment.get('value') + "&versionCode=" + versionCode.get('value')
								+ "&applicationVersionId=" + appVerId.get('value'));
					}
				});
			},
			uploadClient : function(ctx) {
				var box = this.modal.getBox();
				box.getElement("[title='client']").set("id", this.formId).erase("title");
				box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);
				box.getElement("[id='spanButtonPlaceholder']").set("id", 'spanButtonPlaceholder' + this.formId);
				box.getElement("[id='spanIconPlaceholder']").set("id", 'spanIconPlaceholder' + this.formId);

				var sysTypeElement = box.getElement("[name='sysType']");
				sysRequirmentSelect = box.getElement("[name='sysRequirment']");
				sysTypeElement.addEvent('change', function(event) {
					new Request.JSON({
						url : ctx + "/html/sysParams/?m=getParamsByType&type=" + sysTypeElement.get('value'),
						onSuccess : function(data) {
							var a = data.result;
							if (data.success) {
								sysRequirmentSelect.empty();
								Array.each(a, function(item, index) {
									sysRequirmentSelect.options.add(new Option(item.value, item.key));
								});
							} else {

							}
						}
					}).post();
				});

				new Request.JSON({
					url : ctx + "/html/sysParams/?m=getParamsByType&type=" + sysTypeElement.get('value'),
					onSuccess : function(data) {
						var a = data.result;
						if (data.success) {
							sysRequirmentSelect.empty();
							Array.each(a, function(item, index) {
								sysRequirmentSelect.options.add(new Option(item.value, item.key));
							});
						} else {

						}
					}
				}).post();

				this.modal
						.addEvent(
								'open',
								function() {
									swfu = new SWFUpload(
											{
												upload_url : ctx + "/html/commons/?m=upload",
												post_params : {},

												file_size_limit : "10 MB", // 2MB
												file_types : "*.*",
												file_types_description : "CAP FILES",
												file_upload_limit : "0",

												file_queue_error_handler : this.cilentOversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : this.uploadCallback.bind(this),
												upload_complete_handler : uploadComplete,

												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder : document.getElementById("spanButtonPlaceholder" + this.formId),
												button_width : 250,
												button_height : 18,
												button_text : '<span class="button">请选择客户端文件<span class="buttonSmall">(10 MB 最大)</span></span>',
												button_text_style : '.button { font-family:"微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,

												flash_url : ctx + "/lib/uploadManager/swfupload.swf",

												custom_settings : {
													upload_target : "divFileProgressContainer"
												},

												debug : false
											});
									new SWFUpload(
											{
												upload_url : ctx + "/html/commons/?m=upload",
												post_params : {},

												file_size_limit : "10 MB", // 2MB
												file_types : "*.jpg",
												file_types_description : "ICON FILES",
												file_upload_limit : "0",

												file_queue_error_handler : this.iconOversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : this.uploadIconCallback.bind(this),
												upload_complete_handler : uploadComplete,

												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder : document.getElementById("spanIconPlaceholder" + this.formId),
												button_width : 250,
												button_height : 20,
												button_text : '<span class="button">请选择图标：<span class="buttonSmall">(10 MB 最大，支持jpg文件)</span></span>',
												button_text_style : '.button { font-family:"微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,

												flash_url : ctx + "/lib/uploadManager/swfupload.swf",

												custom_settings : {
													upload_target : "divIconProgressContainer"
												},

												debug : false
											});
								}.bind(this));

				this.modal.open();
				this.checkVersionCode();
			},

			uploadIconCallback : function(file, result, responseReceived) {
				var json = JSON.decode(result);
				if (json.success) {
					var div = document.getElement('div[class=fc-tbx]');
					if ($chk(div)) {
						div.dispose();
					}
					new LightFace.MessageBox().info("上传成功");
					var box = this.modal.getBox();
					box.getElement("[name='iconName']").set("value", json.message.fileName);
					box.getElement('[id="iconImg"]').set("src", ctx + "/" + decodeURI(json.message.tempRalFilePath))
							.setStyle("display", "");
					box.getElement("[name='tempIconAbsPath']").set("value", json.message.tempFileAbsPath);
					box.getElement("[name='tempIconDir']").set("value", json.message.tempDir);
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},

			close : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
				this.modal.getBox().dispose();
				this.modal.close();
			},

			submit : function() {
				var data = $(this.formId).toQueryString();
				new Request.JSON({
					url : ctx + "/html/applicationClient/?m=uploadNewClient",
					data : data,
					onSuccess : this.submitCallback.bind(this)
				}).post();
			},

			submitCallback : function(json) {
				if (json.success) {
					this.close();
					this.showCilent(json.message);
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},

			showCilent : function(client) {
				var target = $("client");

				var template = $("clientInfoTemplate").clone();

				template.set("id", "client" + client.id).setStyle('display', '');

				if ($chk(client.name)) {
					template.getElement("[title='名称']").set("html", client.name);
				}
				if ($chk(client.version)) {
					template.getElement("[title='版本']").set("html", client.version);
				}
				if ($chk(client.versionCode)) {
					template.getElement("[title='开发版本']").set('html', client.versionCode);
				}
				if ($chk(client.clientPackageName)) {
					var flag = false;
					var text = client.clientPackageName;
					if (!this.checkLength(text, 20)) {
						flag = true;
					}
					text = this.subString(text, 20, true);
					var clientPackageName = template.getElement("[title='客户端包名']");
					clientPackageName.set('html', text);
					if (flag) {
						clientPackageName.addEvent('mouseover', function(event) {
							div = new Element('div', {
								id : 'tooltip',
								styles : {
									position : 'absolute',
									border : '1px solid #A5CBDB',
									background : '#F6F6F6',
									padding : '1px',
									color : '#333',
									top : event.page.y + 'px',
									left : event.page.x + 'px',
									'z-index' : '99999',
									display : 'none',
									'word-break' : 'break-all',
									'word-wrap' : 'break-word',
									'max-width' : '200px'
								},
								html : client.clientPackageName
							});
							div.inject(document.body, 'bottom');
							div.setStyle('display', '');
						});
						clientClassName.addEvent("mouseout", function(event) {
							div.dispose();
						});
					}
					clientPackageName.set('html', text);
				}
				if ($chk(client.clientClassName)) {
					var flag = false;
					var text = client.clientClassName;
					if (!this.checkLength(text, 20)) {
						flag = true;
					}
					text = this.subString(text, 20, true);
					var clientClassName = template.getElement("[title='客户端入口类']");
					clientClassName.set('html', text);
					if (flag) {
						clientClassName.addEvent('mouseover', function(event) {
							div = new Element('div', {
								id : 'tooltip',
								styles : {
									position : 'absolute',
									border : '1px solid #A5CBDB',
									background : '#F6F6F6',
									padding : '1px',
									color : '#333',
									top : event.page.y + 'px',
									left : event.page.x + 'px',
									'z-index' : '99999',
									display : 'none',
									'word-break' : 'break-all',
									'word-wrap' : 'break-word',
									'max-width' : '200px'
								},
								html : client.clientClassName
							});
							div.inject(document.body, 'bottom');
							div.setStyle('display', '');
						});
						clientClassName.addEvent("mouseout", function(event) {
							div.dispose();
						});
					}
					clientClassName.set('html', text);
				}
				if ($chk(client.sysType)) {
					if (client.sysType == 'os') {
						template.getElement("[title='系统类型']").set("html", '基于手机操作系统');
					} else if (client.sysType = 'j2me') {
						template.getElement("[title='系统类型']").set("html", '基于J2ME');
					}
				}
				if ($chk(client.sysRequirment)) {
					template.getElement("[title='系统版本']").set('html', client.sysRequirment);
				}
				if ($chk(client.fileUrl)) {
					template.getElement("[title='下载地址']").set("html", ctx + "/" + client.fileUrl);
					template.getElement("[title='下载地址']").set("href", ctx + "/" + client.fileUrl);
				}
				if ($chk(client.size)) {
					template.getElement("[title='文件大小']").set("html", client.size);
				}

				template.getElement("[title='删除']").addEvent("click", function(event) {
					event.stop();
					this.confirmRemoveClient(client.id);
				}.bind(this));

				template.inject(target);
			},

			confirmRemoveClient : function(clientId) {
				var confirm = null;
				confirm = new LightFace({
					title : "确认删除",
					appletModal : this.modal,
					height : 50,
					width : 300,
					content : "确认删除？",
					resetOnScroll : false,
					buttons : [ {
						title : "确认",
						color : "blue",
						event : function() {
							confirm.close();
							this.removeClient(clientId);
						}.bind(this)
					}, {
						title : "取消",
						event : function() {
							confirm.close();
						}.bind(this)
					} ]
				});
				confirm.open();
			},

			removeClient : function(clientId) {
				new Request.JSON({
					async : false,
					url : ctx + '/html/applicationClient/?m=removeClient',
					data : {
						clientId : clientId
					},
					onSuccess : function(json) {
						if (json.success) {
							new LightFace.MessageBox().info("删除成功");
							var clientElementId = "client" + clientId;
							$(clientElementId).dispose();
						} else {
							new LightFace.MessageBox().error2("删除失败，" + json.message);
						}
					}.bind(this)
				}).post();
			},

			uploadCallback : function(file, result, responseReceived) {
				var json = JSON.decode(result);
				if (json.success) {
					var div = document.getElement('div[class=fc-tbx]');
					if ($chk(div)) {
						div.dispose();
					}
					new LightFace.MessageBox().info("上传成功");
					var box = this.modal.getBox();
					box.getElement("[name='fileName']").set("value", json.message.fileName);
					box.getElement("[name='tempFileAbsPath']").set("value", json.message.tempFileAbsPath);
					box.getElement("[name='tempDir']").set("value", json.message.tempDir);
				} else {
					new LightFace.MessageBox().error2("上传失败，" + json.message);
				}
			},

			cilentOversize : function() {
				new LightFace.MessageBox().error("客户端文件最大10MB");
			},
			
			iconOversize : function() {
				new LightFace.MessageBox().error("图标最大10MB");
			},
			
			subString : function(str, len, hasDot) {
				var newLength = 0;
				var newStr = "";
				var chineseRegex = /[^\x00-\xff]/g;
				var singleChar = "";
				var strLength = str.replace(chineseRegex, "**").length;
				for ( var i = 0; i < strLength; i++) {
					singleChar = str.charAt(i).toString();
					if (singleChar.match(chineseRegex) != null) {
						newLength += 2;
					} else {
						newLength++;
					}
					if (newLength > len) {
						break;
					}
					newStr += singleChar;
				}

				if (hasDot && strLength > len) {
					newStr += "...";
				}
				return newStr;
			},
			checkLength : function(str, len) {
				var chineseRegex = /[^\x00-\xff]/g;
				var strLength = str.replace(chineseRegex, "**").length;
				if (strLength > len) {
					return false;
				} else {
					return true;
				}
			}
		});
