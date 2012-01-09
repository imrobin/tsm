Application = {};
Application.Page = new Class({

	Implements : [ Events, Options ],

	applicationVersionId : -1,
	loadFiles : new Array(),
	applets : new Array(),
	downloadOrder : null,
	deleteOrder : null,
	modal : null,

	options : {
		applicationVersionId : -1
	},

	initialize : function(options) {
		this.setOptions(options);
		this.applicationVersionId = this.options.applicationVersionId;
		this.ramdon = new Date().valueOf();
	},

	importLoadFileVersion : function(loadFileVersionId) {
		var existLoadFileVersions = null;
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getByCriteria",
			data : {
				search_ALIAS_loadFileL_EQL_id : loadFileVersionId,
				page_orderBy : "versionNo_asc"
			},
			async : false,
			onSuccess : function(json) {
				if (json.success) {
					existLoadFileVersions = json.result;
				}
			}.bind(this)
		}).get();

		var existLoadFileVersion = new Application.ExistLoadFileVersion({
			applicationVersionId : this.applicationVersionId,
			page : this,
			existLoadFileVersions : existLoadFileVersions
		});
		existLoadFileVersion.selectExistLoadFileVersion();
	},

	/**
	 * 获取已上传的私有加载文件
	 */
	getExclusiveLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFile/?m=getUnusedLoadFiles",
			data : {
				applicationVersionId : this.applicationVersionId,
				fileType : fileType,
				page_orderBy : "aid_asc"
			},
			onSuccess : this.getExclusiveLoadFilesCallback.bind(this)
		}).get();
	},

	getExclusiveLoadFilesCallback : function(json) {
		if (json.success) {
			var template = $("loadFileTemplate");
			var target = $("exclusiveLoadFiles").empty();
			json.result.each(function(item) {
				var row = template.getElement("[id='loadFileTemplateP']").clone(true, true);

				row.set("id", "loadFile" + item.id);

				row.getElement("[title='AID']").set("html", item.aid);

				if ($chk(item.name)) {
					row.getElement("[title='名称']").set("html", item.name);
				}

				if ($chk(item.comments)) {
					row.getElement("[title='备注']").set("html", item.comments);
				}

				row.getElement("[title='引入']").addEvent("click", function(event) {
					event.stop();

					this.importLoadFileVersion(item.id);
				}.bind(this));

				row.getElement("[title='升级']").addEvent("click", function(event) {
					event.stop();
					this.createNewLoadFileVersion(item.id);
				}.bind(this));

				row.inject(target);
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	removeLoadFile : function(loadFileId) {
		new Request.JSON({
			async : false,
			url : ctx + '/html/loadFile/?m=remove',
			data : {
				loadFileId : loadFileId
			},
			onSuccess : function(json) {
				if (json.success) {
					new LightFace.MessageBox().info('删除成功');
					var loadFileElementId = "loadFile" + loadFileId;
					$(loadFileElementId).dispose();
				} else {
					new LightFace.MessageBox().error2('删除失败，' + json.message);
				}
			}.bind(this)
		}).post();
	},

	close : function() {
		this.modal.close();
	},

	/**
	 * 升级已存在的加载文件
	 */
	createNewLoadFileVersion : function(loadFileId) {
		new Application.LoadFile({
			applicationVersionId : this.applicationVersionId,
			page : this
		}).createNewLoadFileVersion(loadFileId);
	},

	/**
	 * 获取当前应用版本已使用的加载文件
	 */
	getLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getThatUsedCms2acFileByApplicationVersion",
			data : {
				applicationVersionId : this.options.applicationVersionId,
				fileType : fileType
			},
			onSuccess : this.getLoadFilesCallback.bind(this)

		}).get();
	},

	getLoadFilesCallback : function(json) {
		if (json.success) {
			$("uploadedCapsInfo").getElement("tbody").empty();
			json.result.each(function(loadFileJson) {
				var loadFile = new Application.LoadFile({
					applicationVersionId : this.applicationVersionId,
					page : this
				});
				loadFile.showLoadFile(loadFileJson);
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	/**
	 * 根据加载文件的ID组装显示加载文件信息DIV的ID
	 */
	getLoadFileVersionDivId : function(loadFileId) {
		return "loadFile" + loadFileId;
	},

	/**
	 * 根据加载文件的ID组装显示属于该加载文件的模块DIV的ID
	 */
	getLoadModulesDivId : function(loadFileId) {
		return "loadFile" + loadFileId + "_loadModules";
	},

	/**
	 * 根据模块的ID组装显示模块信息DIV的ID
	 */
	getLoadModuleDivId : function(loadModuleId) {
		return "loadModule" + loadModuleId;
	},

	/**
	 * 根据模块的ID组装显示属于该模块的实例DIV的ID
	 */
	getAppletsDivId : function(loadModuleId) {
		return "loadModule" + loadModuleId + "_applets";
	},

	/**
	 * 根据实例的ID组装显示实例信息DIV的ID
	 */
	getAppletDivId : function(appletId) {
		return "applet" + appletId;
	}
});

Application.ExistLoadFileVersion = new Class({

	Implements : [ Events, Options ],

	modal : null,
	formId : null,
	page : null,
	applicationVersionId : -1,
	existLoadFileVersions : null,

	options : {
		page : null,
		applicationVersionId : -1,
		existLoadFileVersions : null
	},

	initialize : function(options) {
		this.setOptions(options);
		this.page = this.options.page;
		this.applicationVersionId = this.options.applicationVersionId;
		this.existLoadFileVersions = this.options.existLoadFileVersions;
		this.formId = "form" + new Date().valueOf();

	},
	selectExistLoadFileVersion : function() {
		var template = $("selectExsitLoadFileVersionFormTemlate").clone();
		var html = template.getElement("[title='existLoadFileVersionDiv']").get("html");

		this.modal = new LightFace({
			title : "选择文件",
			height : 350,
			width : 550,
			content : html,
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : this.submit.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});

		var box = this.modal.getBox();
		var target = box.getElement("[title='existLoadFileVersions']");
		target.erase("title").set("id", this.formId);
		target.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);

		this.existLoadFileVersions.each(function(item) {
			var row = template.getElement("[title='existLoadFileVersionTemplateP']").clone();
			row.erase("title").setStyle("display", "");

			row.getElement("input[type='radio']").set("value", item.id);
			row.getElement("input[type='radio']").setStyle("display", "");
			row.getElement("[title='版本号']").set("html", item.versionNo);
			row.getElement("[title='Hash']").set("html", item.hash);
			row.getElement("[title='加载参数']").set("html", item.loadParams);
			row.getElement("[title='文件大小']").set("html", item.fileSize);

			row.inject(target);
		});

		this.modal.open();
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
		var form = $(this.formId);
		var data = form.toQueryString();

		new Request.JSON({
			url : ctx + "/html/applicationLoadFile/?m=buildImportBetweenLoadFileVersionAndApplicationVersion",
			data : data,
			async : false,
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(json) {
		if (json.success) {
			try {
				this.close();
			} catch (e) {
				console.log(e);
			}
			// var loadFile = new Application.LoadFile({
			// applicationVersionId : this.applicationVersionId,
			// page : this.page
			// });
			this.page.getLoadFiles();
			this.page.getExclusiveLoadFiles();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	}
});

Application.LoadFile = new Class(
		{

			Implements : [ Events, Options ],

			page : null,
			modal : null,
			tempFileId : "",
			formId : null,
			applicationVersionId : -1,
			sdList : new Array(5),

			jsonLoadParam : null,

			options : {
				page : null,
				applicationVersionId : -1
			},

			initialize : function(options) {
				this.setOptions(options);
				this.page = this.options.page;
				this.applicationVersionId = this.options.applicationVersionId;
				this.formId = "form" + new Date().valueOf();
			},

			/**
			 * 将以添加的加载文件显示在页面上
			 */
			showLoadFile : function(loadFile) {
				var template = $("loadFileTemplateDiv");
				var target = $("uploadedCapsInfo").getElement("tbody");

				var row = template.getElement("[id='loadFileTemplateP']").clone(true, true);// 用于显示加载文件基本信息
				var loadFileDivId = this.page.getLoadFileVersionDivId(loadFile.id);// 封装基本信息和模块的ID
				row.set("id", loadFileDivId + "_info");

				// 填写加载文件信息
				if ($chk(loadFile.name)) {
					row.getElement("[title='名称']").set("html", loadFile.name);
				}
				if ($chk(loadFile.aid)) {
					row.getElement("[title='AID']").set("html", loadFile.aid);
				}
				if ($chk(loadFile.versionNo)) {
					row.getElement("[title='版本号']").set("html", loadFile.versionNo);
				}
				/*
				 * if ($chk(loadFile.comments)) {
				 * row.getElement("[title='备注']").set("html",
				 * loadFile.comments); }
				 */
				if ($chk(loadFile.loadParams)) {
					row.getElement("[title='加载参数']").set("html", loadFile.loadParams);
				}
				if ($chk(loadFile.fileSize)) {
					row.getElement("[title='文件大小']").set("html", loadFile.fileSize);
				}

				row.getElement("[title='删除']").addEvent("click", function(event) {
					event.stop();
					this.modal = new LightFace({
						title : "确认删除",
						height : 50,
						width : 300,
						content : "确认删除此文件？",
						resetOnScroll : false,
						buttons : [ {
							title : "确认",
							color : "blue",
							event : function() {
								this.close();
								this.remove(loadFile.id);
							}.bind(this)
						}, {
							title : "取消",
							event : function() {
								this.close();
							}.bind(this)
						} ]
					}).open();
				}.bind(this));

				// 显示在页面上
				row.set("id", loadFileDivId).inject(target);
			},

			getConstant : function(element) {
				new Request.JSON({
					async : false,
					url : ctx + "/html/securityDomain/?m=exportConstant",
					onSuccess : function(json) {
						if (json.success) {
							this.sdConstant = json.message;
						}
					}.bind(this)
				}).get();
				this.confSd(element);
			},

			confSd : function(element) {
				// var t = element.getElement("[name='sdModel']");
				var sdModelEle = element.getElement("[name='sdModel']");
				var sdModel = element.getElement("[name='sdModel']").get("value");
				if ((this.sdConstant.MODEL_ISD == sdModel) || (this.sdConstant.MODEL_COMMON == sdModel)) {
					var target = element.getElement("[name='sdId']");
					target.set("disabled", "disabled").empty();

					var template = $("sdSelectTemplate");
					var row = template.getElement("[title='disableTemplate']").clone();
					row.erase("title").inject(target);
				} else {
					this.getSdList(sdModel, element);
				}
			},

			getSdList : function(sdModel, element) {
				if ($chk(this.sdList[sdModel])) {
					this.showSdList(this.sdList[sdModel], element);
				} else {
					new Request.JSON({
						url : ctx + "/html/securityDomain/?m=index",
						data : {
							search_ALIAS_spL_EQL_id : spId,
							search_EQI_status : this.sdConstant.STATUS_PUBLISHED,
							search_EQI_model : sdModel,
							page_pageSize : 100000
						},
						onSuccess : function(json) {
							if (json.success) {
								if (0 == json.totalCount) {
									new LightFace.MessageBox().error('没有指定模式的安全域');

									var select = this.modal.getBox().getElement("[name='sdModel']");
									var sdOptions = select.getElements('option');
									sdOptions.each(function(item) {
										if (this.sdConstant.MODEL_ISD == item.get("value")) {
											item.set("selected", "selected");
										} else {
											item.set("selected", "");
										}
									}.bind(this));
									this.confSd(this.modal.getBox());
								} else {
									this.sdList[sdModel] = json.result;
									this.showSdList(this.sdList[sdModel], element);
								}
							}
						}.bind(this)
					}).get();
				}
			},

			showSdList : function(sdList, element) {
				var target = element.getElement("[name='sdId']");
				target.set("disabled", "").empty();

				var template = $("sdSelectTemplate");
				sdList.each(function(item) {
					var row = template.getElement("[title='enableTemplate']").clone();
					row.set("value", item.id).set("html", item.sdName + "(" + item.aid + ")");
					row.erase("title").inject(target);
				});
			},

			/**
			 * 弹出输入加载文件信息的界面
			 */
			createNewLoadFile : function() {
				this.modal = new LightFace({
					title : "上传文件",
					height : 350,
					width : 650,
					content : $("uploadDivTemplate").get("html"),
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

				this.tempFileId = (new Date()).valueOf() + "File";
				// 填写数据
				var box = this.modal.getBox();
				box.getElement("[title='loadFile']").set("id", this.formId).eliminate("title");
				box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);
				box.getElement("[id='spanButtonPlaceholder']").set("id", "spanButtonPlaceholder" + this.tempFileId);
				box.getElement("[id='divFileProgressContainer']").set("id", "divFileProgressContainer" + this.tempFileId);
				this.getConstant(box);

				// 添加事件
				box.getElement("[title='点击输入参数']").addEvent("click", function(event) {
					event.stop();
					this.inputLoadParams();
				}.bind(this));
				box.getElement("[name='sdModel']").addEvent("change", function(event) {
					event.stop();
					this.confSd(box);
				}.bind(this));

				new FormCheck(box.getElement("[title='loadFile']"), {
					submit : false,
					trimValue : false,
					display : {
						showErrors : 1,
						errorsLocation : 1,
						indicateErrors : 1,
						keepFocusOnError : 0,
						closeTipsButton : 0,
						removeClassErrorOnTipClosure : 1,
						scrollToFirst : false
					},
					onValidateSuccess : function() {// 校验通过执行load()
						this.submit();
					}.bind(this)
				});

				this.modal
						.addEvent(
								'open',
								function() {
									swfu = new SWFUpload(
											{
												// Backend Settings
												upload_url : ctx + "/html/commons/?m=upload",
												post_params : {},

												// File Upload Settings
												file_size_limit : "10 MB", // 2MB
												file_types : "*.cap",
												file_types_description : "CAP FILES",
												file_upload_limit : "0",

												// Event Handler Settings -
												// these functions as defined in
												// Handlers.js
												// The handlers are not part of
												// SWFUpload but are part of my
												// website and control how
												// my website reacts to the
												// SWFUpload events.
												file_queue_error_handler : this.cilentOversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : this.uploadCapCallback.bind(this),
												upload_complete_handler : uploadComplete,

												// Button Settings
												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder : document.getElementById("spanButtonPlaceholder" + this.tempFileId),
												button_width : 200,
												button_height : 18,
												button_text : '<span class="button">请选择文件<span class="buttonSmall">(10 MB 最大)</span></span>',
												button_text_style : '.button { font-family:"微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,

												// Flash Settings
												flash_url : ctx + "/lib/uploadManager/swfupload.swf",

												custom_settings : {
													upload_target : "divFileProgressContainer" + this.tempFileId
												},

												// Debug Settings
												debug : false
											});
								}.bind(this));

				this.modal.open();
			},

			remove : function(loadFileVersionId) {
				new Request.JSON({
					url : ctx + "/html/applicationLoadFile/?m=removeImportBetweenLoadFileVersionAndApplicationVersion",
					data : {
						applicationVersionId : this.applicationVersionId,
						loadFileVersionId : loadFileVersionId
					},
					onSuccess : this.removeCallback.bind(this)
				}).post();
			},

			removeCallback : function(json) {
				if (json.success) {
					var loadFileDivId = this.page.getLoadFileVersionDivId(json.message);
					var loadFileDiv = $(loadFileDivId);
					loadFileDiv.dispose();

					this.page.getExclusiveLoadFiles();
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},

			inputLoadParams : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}

				var loadParams = new Application.LoadFile.LoadParams();

				loadParams.loadFile = this;
				var loadParamsValue = this.modal.getBox().getElement("[name='loadParams']").get("value");
				loadParams.inputLoadParams(loadParamsValue);
			},

			refreshLoadParams : function(params) {
				this.modal.getBox().getElement("[name='loadParams']").set("value", params);
			},

			uploadCapCallback : function(file, result, responseReceived) {
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
					new LightFace.MessageBox().error(json.message);
				}
			},

			/**
			 * 关闭弹出窗口
			 */
			close : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
				this.modal.getBox().dispose();
				this.modal.close();
			},

			submitNewVersion : function() {
				data = $(this.formId).toQueryString();

				new Request.JSON({
					url : ctx + "/html/loadFileVersion/?m=createNewLoadFileVersion",
					data : data,
					onSuccess : this.submitNewVersionCallback.bind(this)
				}).post();
			},

			submitNewVersionCallback : function(json) {
				if (json.success) {
					this.close();
					this.page.getLoadFiles();
					this.page.getExclusiveLoadFiles();
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},

			/**
			 * 提交加载文件信息
			 */
			submit : function() {
				new Request.JSON({
					url : ctx + "/html/loadFile/?m=createNewLoadFileForApplicationVersion",
					data : $(this.formId),
					onSuccess : this.submitCallback.bind(this)
				}).post();
			},

			submitCallback : function(json) {
				if (json.success) {
					this.close();
					this.page.getLoadFiles();
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},

			/**
			 * 升级加载文件
			 */
			createNewLoadFileVersion : function(loadFileId) {
				this.modal = new LightFace({
					title : "升级文件",
					height : 350,
					width : 600,
					content : $("loadFileVersionDivTemplate").get("html"),
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

				this.tempFileId = (new Date()).valueOf() + "File";
				// 填写数据
				var box = this.modal.getBox();
				box.getElement("[title='loadFileVersion']").set("id", this.formId).eliminate("title");
				box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);
				box.getElement("[name='loadFileId']").set("value", loadFileId);
				box.getElement("[id='spanButtonPlaceholder']").set("id", "spanButtonPlaceholder" + this.tempFileId);
				box.getElement("[id='divFileProgressContainer']").set("id", "divFileProgressContainer" + this.tempFileId);

				// 添加事件
				box.getElement("[title='点击输入参数']").addEvent("click", function(event) {
					event.stop();
					this.inputLoadParams();
				}.bind(this));

				new FormCheck(box.getElement("[title='loadFileVersion']"), {
					submit : false,
					trimValue : false,
					display : {
						showErrors : 1,
						errorsLocation : 1,
						indicateErrors : 1,
						keepFocusOnError : 0,
						closeTipsButton : 0,
						removeClassErrorOnTipClosure : 1,
						scrollToFirst : false
					},
					onValidateSuccess : function() {
						// 校验通过执行load()
						this.submitNewVersion();
					}.bind(this)
				});

				this.modal
						.addEvent(
								'open',
								function() {
									swfu = new SWFUpload(
											{
												// Backend Settings
												upload_url : ctx + "/html/commons/?m=upload",
												post_params : {},

												// File Upload Settings
												file_size_limit : "10 MB", // 2MB
												file_types : "*.cap",
												file_types_description : "CAP FILES",
												file_upload_limit : "0",

												// Event Handler Settings -
												// these functions as defined in
												// Handlers.js
												// The handlers are not part of
												// SWFUpload but are part of my
												// website and control how
												// my website reacts to the
												// SWFUpload events.
												file_queue_error_handler : this.capOversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : this.uploadCapCallback.bind(this),
												upload_complete_handler : uploadComplete,

												// Button Settings
												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder : document.getElementById("spanButtonPlaceholder" + this.tempFileId),
												button_width : 200,
												button_height : 18,
												button_text : '<span class="button">请选择文件<span class="buttonSmall">(10 MB 最大)</span></span>',
												button_text_style : '.button { font-family:"微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,

												// Flash Settings
												flash_url : ctx + "/lib/uploadManager/swfupload.swf",

												custom_settings : {
													upload_target : "divFileProgressContainer" + this.tempFileId
												},

												// Debug Settings
												debug : false
											});
								}.bind(this));

				this.modal.open();
			},

			capOversize : function() {
				new LightFace.MessageBox().error("文件最大10MB");
			}
		});

Application.LoadFile.LoadParams = new Class({

	Implements : [ Events, Options ],

	loadFile : null,
	modal : null,
	formId : null,
	jsonLoadParams : {
		nonVolatileCodeSpace : 0,
		volatileDateSpace : 0,
		nonVolatileDateSpace : 0
	},

	initialize : function(options) {
		this.setOptions(options);
		this.formId = "form" + new Date().valueOf();
		// 填写加载参数的弹出窗口
		var template = $("loadParamsFormDivTemplate").clone(true, true);
		var html = template.get("html");
		this.modal = new LightFace({
			title : "输入加载参数",
			loadModule : this,
			height : 350,
			width : 550,
			content : html,
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : function(event) {
					event.stop();
					this.modal.getBox().getElement('button[type="submit"]').click();
				}.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});
		this.addValidate();
	},

	inputLoadParams : function(loadParams) {
		// 向服务器发起请求解析加载参数
		new Request.JSON({
			url : ctx + "/html/loadFile/?m=parseLoadParams",
			data : {
				hexLoadParams : loadParams
			},
			async : false,
			onSuccess : function(json) {
				if (json.success) {
					this.jsonLoadParams = json.message;
				}
			}.bind(this)
		}).get();
		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='loadParams']").set("id", this.formId).eliminate("title");
		box.getElement("[name='nonVolatileCodeSpace']").set("value", this.jsonLoadParams.nonVolatileCodeSpace);
		box.getElement("[name='volatileDateSpace']").set("value", this.jsonLoadParams.volatileDateSpace);
		box.getElement("[name='nonVolatileDateSpace']").set("value", this.jsonLoadParams.nonVolatileDateSpace);

		this.modal.open();
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
			url : ctx + "/html/loadFile/?m=buildLoadParams",
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).post();

	},
	// 加载页面验证
	addValidate : function() {
		var box = this.modal.getBox();
		var form = box.getElement("[title='loadParams']");
		new FormCheck(form, {
			submit : false,
			trimValue : false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				removeClassErrorOnTipClosure : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {// 校验通过执行load()
				this.submit();
			}.bind(this)
		});
	},
	submitCallback : function(json) {
		if (json.success) {
			this.close();
			this.loadFile.refreshLoadParams(json.message.hexLoadParams);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	}
});
