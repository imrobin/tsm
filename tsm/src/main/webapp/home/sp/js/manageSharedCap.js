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

	init : function() {
		this.getLoadFiles();
	},

	/**
	 * 获取已上传的共享加载文件
	 */
	getLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFile/?m=index",
			data : {
				search_ALIAS_spL_EQL_id : spId,
				page_orderBy : "aid_asc",
				search_EQI_shareFlag : 1

			},
			onSuccess : this.getLoadFilesCallback.bind(this)
		}).get();
	},

	getLoadFilesCallback : function(json) {
		if (json.success) {
			json.result.each(function(loadFileJson) {
				var loadFile = new Application.LoadFile({
					applicationVersionId : this.applicationVersionId,
					page : this
				});
				loadFile.showLoadFile(loadFileJson);
				this.getLoadFileVersions(loadFileJson.id);
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	/**
	 * 获取指定加载文件的版本信息
	 * 
	 * @param id
	 *            模块ID
	 */
	getLoadFileVersions : function(id) {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=index",
			data : {
				search_ALIAS_loadFileL_EQL_id : id,
				page_orderBy : "versionNo_desc"
			},
			onSuccess : this.getLoadFileVersionsCallback.bind(this)
		}).get();
	},

	getLoadFileVersionsCallback : function(json) {
		if (json.success) {
			var loadFileVersions = json.result;
			if (0 != loadFileVersions.length) {
				var loadFileId = loadFileVersions[0].loadFile_id;
				var loadFileDivId = this.getLoadFileDivId(loadFileId);// 用于封装加载文件基本信息和所属版本DIV的ID
				var loadFile = $(loadFileDivId);
				var loadFileVersionsDivId = this.getLoadFileVersionsDivId(loadFileId);// 用于显示该加载文件所属版本DIV的ID
				var target = loadFile.getElement("[id='" + loadFileVersionsDivId + "']");

				target.empty();
			}

			json.result.each(function(loadFileVersionJson) {
				var loadFile = new Application.LoadFile({
					page : this
				});
				loadFile.showLoadFileVersion(loadFileVersionJson);
				this.getDependence(loadFileVersionJson.id);
				this.getLoadModules(loadFileVersionJson.id);
			}.bind(this));
		}
	},

	/**
	 * 获取指定版本加载文件已定义的依赖
	 * 
	 * @param id
	 *            加载文件版本ID
	 */
	getDependence : function(id) {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getDependence",
			data : {
				search_ALIAS_childrenL_EQL_id : id
			},
			onSuccess : function(json) {
				json.id = id;
				this.getDependenceCallback(json);
			}.bind(this)
		}).get();
	},

	getDependenceCallback : function(json) {
		if (json.success) {
			json.result.each(function(item) {
				new Application.LoadFile.Dependecy({
					page : this,
					loadFileVersionId : json.id
				}).showDependece(item);
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	/**
	 * 获取指定版本加载文件已定义的模块
	 * 
	 * @param id
	 *            加载文件版本ID
	 */
	getLoadModules : function(id) {
		new Request.JSON({
			url : ctx + "/html/loadModule/?m=index",
			data : {
				search_ALIAS_loadFileVersionL_EQL_id : id
			},
			onSuccess : this.getLoadModulesCallback.bind(this)
		}).get();
	},

	getLoadModulesCallback : function(json) {
		if (json.success) {
			json.result.each(function(loadModuleJson) {
				var loadModule = new Application.LoadModule({
					applicationVersionId : this.applicationVersionId,
					loadFileVersionId : loadModuleJson.loadFileId,
					page : this
				});
				loadModule.showLoadModule(loadModuleJson);
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	/**
	 * 根据加载文件版本的ID组装显示加载文件信息DIV的ID
	 */
	getLoadFileDivId : function(loadFileId) {
		return "loadFile" + loadFileId;
	},

	/**
	 * 根据加载文件版本的ID组装显示加载文件版本详细信息DIV的ID
	 */
	getLoadFileDetailsDivId : function(loadFileId) {
		return this.getLoadFileDivId(loadFileId) + "_details";
	},

	/**
	 * 根据加载文件的ID组装显示属于该加载文件版本的模块DIV的ID
	 */
	getLoadFileVersionsDivId : function(loadFileId) {
		return this.getLoadFileDivId(loadFileId) + "_versions";
	},

	/**
	 * 根据加载文件版本的ID组装显示加载文件信息DIV的ID
	 */
	getLoadFileVersionDivId : function(loadFileId) {
		return "loadFileVersion" + loadFileId;
	},

	/**
	 * 根据加载文件版本的ID组装显示加载文件版本详细信息DIV的ID
	 */
	getLoadFileVersionDetailsDivId : function(loadFileId) {
		return this.getLoadFileVersionDivId(loadFileId) + "_details";
	},

	/**
	 * 根据加载文件的ID组装显示属于该加载文件版本的模块DIV的ID
	 */
	getLoadModulesDivId : function(loadFileId) {
		return this.getLoadFileVersionDivId(loadFileId) + "_loadModules";
	},

	/**
	 * 根据加载文件的ID组装显示该加载文件版本依赖DIV的ID
	 */
	getDependenciesDivId : function(loadFileId) {
		return this.getLoadFileVersionDivId(loadFileId) + "_dependences";
	},

	getDependenceDivId : function(childId, parentId) {
		return childId + "dependent" + parentId;
	},

	/**
	 * 根据模块的ID组装显示模块信息DIV的ID
	 */
	getLoadModuleDivId : function(loadModuleId) {
		return "loadModule" + loadModuleId;
	},

	/**
	 * 根据模块的ID组装显示模块详细信息DIV的ID
	 */
	getLoadModuleDetailsDivId : function(loadModuleId) {
		return this.getLoadModuleDivId(loadModuleId) + "_details";
	},

	/**
	 * 根据模块的ID组装显示属于该模块的实例DIV的ID
	 */
	getAppletsDivId : function(loadModuleId) {
		return this.getLoadModuleDivId(loadModuleId) + "_applets";
	},

	/**
	 * 根据实例的ID组装显示实例信息DIV的ID
	 */
	getAppletDivId : function(appletId) {
		return "applet" + appletId;
	},

	/**
	 * 根据实例的ID组装显示实例详细信息DIV的ID
	 */
	getAppletDetailsDivId : function(appletId) {
		return this.getAppletDivId(appletId) + "_details";
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
				this.tempFileId = (new Date()).valueOf() + "File";
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
								this.sdList[sdModel] = json.result;
								this.showSdList(this.sdList[sdModel], element);
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

			hidden : function(id) {
				var loadFileDivId = this.page.getLoadFileDivId(id);

				$(loadFileDivId + "_loadFileDetailsAndVersions").setStyle("display", "none");

				var icon = $(loadFileDivId + "_icon");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spread(id);
				}.bind(this));

			},

			spread : function(id) {
				var loadFileDivId = this.page.getLoadFileDivId(id);

				$(loadFileDivId + "_loadFileDetailsAndVersions").setStyle("display", "");

				var icon = $(loadFileDivId + "_icon");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hidden(id);
				}.bind(this));

			},

			hiddenDetails : function(id) {
				var detailsDivId = this.page.getLoadFileDetailsDivId(id);

				$(detailsDivId).setStyle("display", "none");

				var icon = $(detailsDivId + "_icon1");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spreadDetails(id);
				}.bind(this));

			},

			spreadDetails : function(id) {
				var detailsDivId = this.page.getLoadFileDetailsDivId(id);

				$(detailsDivId).setStyle("display", "");

				var icon = $(detailsDivId + "_icon1");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenDetails(id);
				}.bind(this));

			},

			hiddenVersions : function(id) {
				var detailsDivId = this.page.getLoadFileVersionsDivId(id);

				$(detailsDivId).setStyle("display", "none");

				var icon = $(detailsDivId + "_icon2");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spreadVersions(id);
				}.bind(this));
			},

			spreadVersions : function(id) {
				var detailsDivId = this.page.getLoadFileVersionsDivId(id);

				$(detailsDivId).setStyle("display", "");

				var icon = $(detailsDivId + "_icon2");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenVersions(id);
				}.bind(this));

			},

			/**
			 * 将以添加的加载文件显示在页面上
			 */
			showLoadFile : function(loadFile) {
				var template = $("loadFileTemplateDiv").getElement("[title='loadFileTemplateDiv']").clone(true, true).erase("title");
				var target = $("uploadedCapsInfo");

				var info = template.getElement("[id='loadFileTemplateP']");// 用于显示加载文件基本信息
				var loadFileDivId = this.page.getLoadFileDivId(loadFile.id);// 封装基本信息和模块的ID
				info.set("id", +"_info");

				// 图标
				var icon = info.getElement("[title='icon']");
				icon.erase('title').set("id", loadFileDivId + "_icon").set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hidden(loadFile.id);
				}.bind(this));

				// 填写加载文件信息
				info.getElement("[title='ID']").set("html", loadFile.id);
				info.getElement("[title='名称']").set("html", loadFile.name);
				info.getElement("[title='AID']").set("html", loadFile.aid);

				// 添加操作事件
				info.getElement("[title='更新']").addEvent("click", function(event) {
					event.stop();
					this.createNewLoadFileVersion(loadFile.id);
				}.bind(this));
				info.getElement("[title='删除']").addEvent("click", function(event) {
					event.stop();
					var confirm = null;
					confirm = new LightFace({
						title : "确认删除",
						appletModal : this.modal,
						height : 50,
						width : 300,
						content : "确认删除文件？只有未被使用的文件可以删除",
						resetOnScroll : false,
						buttons : [ {
							title : "确认",
							color : "blue",
							event : function() {
								confirm.close();
								this.removeLoadFile(loadFile.id);
							}.bind(this)
						}, {
							title : "取消",
							event : function() {
								confirm.close();
							}.bind(this)
						} ]
					});
					confirm.open();
				}.bind(this));

				var loadFileDetailsAndVersions = template.getElement("[id='loadFileDetailsAndVersions']").set("id",
						loadFileDivId + "_loadFileDetailsAndVersions");

				// 详细信息
				var deatilsDivId = this.page.getLoadFileDetailsDivId(loadFile.id);
				var details = loadFileDetailsAndVersions.getElement("[title='loadFileDetails']");
				details.erase("title").set("id", deatilsDivId);
				details.getElement("[title='备注']").set("html", loadFile.comments).set("title", loadFile.comments);
				details.getElement("[title='所属安全域模式']").set("html", loadFile.sdModel);
				if ($chk(loadFile.sd_sdName) && $chk(loadFile.sd_aid)) {
					details.getElement("[title='所属安全域']").set("html", loadFile.sd_sdName + '(' + loadFile.sd_aid + ')');
				}

				// 详细信息展开收拢
				var detailsIcon = loadFileDetailsAndVersions.getElement("[title='detailsIcon']");
				detailsIcon.erase('title').set("id", deatilsDivId + "_icon1").set("src", hiddenIcon);
				detailsIcon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenDetails(loadFile.id);
				}.bind(this));

				// 配置显示模块的Div
				var versionsDiv = loadFileDetailsAndVersions.getElement("[title='versions']");
				var versionsDivId = this.page.getLoadFileVersionsDivId(loadFile.id);
				versionsDiv.erase("title").set("id", versionsDivId);
				// 模块信息展开收拢
				var modulesIcon = loadFileDetailsAndVersions.getElement("[title='modulesIcon']");
				modulesIcon.erase('title').set("id", versionsDivId + "_icon2").set("src", hiddenIcon);
				modulesIcon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenVersions(loadFile.id);
				}.bind(this));

				// 显示在页面上
				var loadFileDiv = template.set("id", loadFileDivId); // 封装基本信息和模块
				loadFileDiv.inject(target);

				this.hidden(loadFile.id);
				this.hiddenDetails(loadFile.id);
				this.hiddenVersions(loadFile.id);
			},

			removeLoadFile : function(loadFileId) {
				new Request.JSON({
					url : ctx + '/html/loadFile/?m=remove',
					data : {
						loadFileId : loadFileId
					},
					onSuccess : function(json) {
						this.removeLoadFileCallback(json, loadFileId);
					}.bind(this)
				}).post();
			},

			removeLoadFileCallback : function(json, loadFileId) {
				if (json.success) {
					new LightFace.MessageBox().info('删除成功');
					var loadFileDivId = this.page.getLoadFileDivId(loadFileId);
					$(loadFileDivId).dispose();
				} else {
					new LightFace.MessageBox().error(json.message);
				}
			},

			/**
			 * 弹出输入加载文件信息的界面
			 */
			createNewLoadFile : function() {
				this.modal = new LightFace({
					title : "上传加载文件",
					height : 350,
					width : 620,
					content : $("uploadDivTemplate").get("html"),
					resetOnScroll : false,
					buttons : [ {
						title : "确认",
						color : "blue",
						event : function() {
							this.modal.getBox().getElement('[type="submit"]').click();
						}.bind(this)
					}, {
						title : "取消",
						event : this.close.bind(this)
					} ]
				});

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

				new FormCheck(box.getElement('form'), {
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
						this.submit();
					}.bind(this)
				});

				this.modal
						.addEvent(
								'open',
								function() {
									swfu = new SWFUpload(
											{
												upload_url : ctx + "/html/commons/?m=upload",
												post_params : {},

												file_size_limit : "20 MB", // 20MB
												file_types : "*.cap",
												file_types_description : "CAP FILES",
												file_upload_limit : "0",

												file_queue_error_handler : this.capOversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : this.uploadCapCallback.bind(this),
												upload_complete_handler : uploadComplete,

												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder : document.getElementById("spanButtonPlaceholder" + this.tempFileId),
												button_width : 200,
												button_height : 18,
												button_text : '<span class="button">请选择加载文件<span class="buttonSmall">(20 MB 最大)</span></span>',
												button_text_style : '.button { font-family:"微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,

												flash_url : ctx + "/lib/uploadManager/swfupload.swf",

												custom_settings : {
													upload_target : "divFileProgressContainer" + this.tempFileId
												},

												debug : false
											});
								}.bind(this));

				this.modal.open();
			},

			inputLoadParams : function() {
				var loadParams = new Application.LoadFile.LoadParams();

				loadParams.loadFile = this;
				var loadParamsValue = this.modal.box.getElement("[name='loadParams']").get("value");
				loadParams.inputLoadParams(loadParamsValue);
			},

			refreshLoadParams : function(params) {
				this.modal.getBox().getElement("[name='loadParams']").set("value", params);
			},

			capOversize : function() {
				new LightFace.MessageBox().error("加载文件不能操作20MB");
			},

			uploadCapCallback : function(file, result, responseReceived) {
				var json = JSON.decode(result);
				if (json.success) {
					div = document.getElement('div[class=fc-tbx]');
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
					url : ctx + "/html/loadFileVersion/?m=createNewSharedLoadFileVersion",
					data : data,
					onSuccess : this.submitNewVesionCallback.bind(this)
				}).post();
			},

			submitNewVesionCallback : function(json) {
				if (json.success) {
					this.close();
					this.page.getLoadFileVersions(json.message.loadFile_id);
				} else {
					new LightFace.MessageBox().error2("上传失败，" + json.message);
				}
			},

			/**
			 * 提交加载文件信息
			 */
			submit : function() {
				new Request.JSON({
					url : ctx + "/html/loadFile/?m=createNewSharedLoadFile",
					data : $(this.formId),
					onSuccess : this.submitCallback.bind(this)
				}).post();
			},

			submitCallback : function(json) {
				if (json.success) {
					this.close();
					this.showLoadFile(json.message);
					this.page.getLoadFileVersions(json.message.id);
				} else {
					new LightFace.MessageBox().error2("上传失败，" + json.message);
				}
			},

			/**
			 * 升级加载文件
			 */
			createNewLoadFileVersion : function(loadFileId) {
				this.modal = new LightFace({
					title : "升级加载文件",
					height : 200,
					width : 620,
					content : $("loadFileVersionDivTemplate").get("html"),
					resetOnScroll : false,
					buttons : [ {
						title : "确认",
						color : "blue",
						event : function() {
							this.modal.getBox().getElement('[type=submit]').click();
						}.bind(this)
					}, {
						title : "取消",
						event : this.close.bind(this)
					} ]
				});

				var box = this.modal.getBox();
				// 填写数据
				box.getElement("[title='loadFileVersion']").set("id", this.formId).eliminate("title");
				box.getElement("[name='loadFileId']").set("value", loadFileId);
				box.getElement("[id='spanButtonPlaceholder']").set("id", "spanButtonPlaceholder" + this.tempFileId);
				box.getElement("[id='divFileProgressContainer']").set("id", "divFileProgressContainer" + this.tempFileId);

				// 添加事件
				box.getElement("[title='点击输入参数']").addEvent("click", function(event) {
					event.stop();
					this.inputLoadParams();
				}.bind(this));

				new FormCheck(box.getElement('form'), {
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
						this.submitNewVersion();
					}.bind(this)
				});

				this.modal
						.addEvent(
								'open',
								function() {
									swfu = new SWFUpload(
											{
												upload_url : ctx + "/html/commons/?m=upload",
												post_params : {},

												file_size_limit : "20 MB", // 20MB
												file_types : "*.cap",
												file_types_description : "CAP FILES",
												file_upload_limit : "0",

												file_queue_error_handler : this.capOversize,
												file_dialog_complete_handler : fileDialogComplete,
												upload_progress_handler : uploadProgress,
												upload_error_handler : uploadError,
												upload_success_handler : this.uploadCapCallback.bind(this),
												upload_complete_handler : uploadComplete,

												button_image_url : ctx
														+ "/lib/uploadManager/images/SmallSpyGlassWithTransperancy_17x18.png",
												button_placeholder : document.getElementById("spanButtonPlaceholder" + this.tempFileId),
												button_width : 200,
												button_height : 18,
												button_text : '<span class="button">请选择加载文件<span class="buttonSmall">(20 MB 最大)</span></span>',
												button_text_style : '.button { font-family:"微软雅黑","宋体",Arial,sans-serif; font-size: 12pt; } .buttonSmall { font-size: 12pt; }',
												button_text_top_padding : 0,
												button_text_left_padding : 18,
												button_window_mode : SWFUpload.WINDOW_MODE.TRANSPARENT,
												button_cursor : SWFUpload.CURSOR.HAND,

												flash_url : ctx + "/lib/uploadManager/swfupload.swf",

												custom_settings : {
													upload_target : "divFileProgressContainer" + this.tempFileId
												},

												debug : false
											});
								}.bind(this));

				this.modal.open();
			},

			spreadVersionDetails : function(id) {
				var detailsDivId = this.page.getLoadFileVersionDetailsDivId(id);

				$(detailsDivId).setStyle("display", "");

				var icon = $(detailsDivId + "_icon1");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenVersionDetails(id);
				}.bind(this));
			},

			hiddenVersionDetails : function(id) {
				var detailsDivId = this.page.getLoadFileVersionDetailsDivId(id);

				$(detailsDivId).setStyle("display", "none");

				var icon = $(detailsDivId + "_icon1");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spreadVersionDetails(id);
				}.bind(this));
			},

			spreadVersion : function(id) {
				var detailsDivId = this.page.getLoadFileVersionDivId(id);

				$(detailsDivId + "_loadFileVersionDetailsAndLoadModuules").setStyle("display", "");

				var icon = $(detailsDivId + "_icon");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenVersion(id);
				}.bind(this));
			},

			hiddenVersion : function(id) {
				var detailsDivId = this.page.getLoadFileVersionDivId(id);

				$(detailsDivId + "_loadFileVersionDetailsAndLoadModuules").setStyle("display", "none");

				var icon = $(detailsDivId + "_icon");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spreadVersion(id);
				}.bind(this));
			},

			spreadLoadModules : function(id) {
				var detailsDivId = this.page.getLoadModulesDivId(id);

				$(detailsDivId).setStyle("display", "");

				var icon = $(detailsDivId + "_icon2");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenLoadModules(id);
				}.bind(this));
			},

			hiddenLoadModules : function(id) {
				var detailsDivId = this.page.getLoadModulesDivId(id);

				$(detailsDivId).setStyle("display", "none");

				var icon = $(detailsDivId + "_icon2");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spreadLoadModules(id);
				}.bind(this));
			},

			spreadLoadFileVersionDependencies : function(id) {
				var detailsDivId = this.page.getDependenciesDivId(id);

				$(detailsDivId).setStyle("display", "");

				var icon = $(detailsDivId + "_icon3");
				icon.set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenLoadFileVersionDependencies(id);
				}.bind(this));
			},

			hiddenLoadFileVersionDependencies : function(id) {
				var detailsDivId = this.page.getDependenciesDivId(id);

				$(detailsDivId).setStyle("display", "none");

				var icon = $(detailsDivId + "_icon3");
				icon.set("src", spreadIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.spreadLoadFileVersionDependencies(id);
				}.bind(this));
			},

			/**
			 * 加载文件版本显示在页面上
			 */
			showLoadFileVersion : function(loadFileVersion) {
				var template = $("loadFileVersionTemplateDiv").clone(true, true).erase("title").setStyle("display", "");

				var loadFileDivId = this.page.getLoadFileDivId(loadFileVersion.loadFile_id);// 用于封装加载文件基本信息和所属版本DIV的ID
				var loadFile = $(loadFileDivId);
				var loadFileVersionsDivId = this.page.getLoadFileVersionsDivId(loadFileVersion.loadFile_id);// 用于显示该加载文件所属版本DIV的ID
				var target = loadFile.getElement("[id='" + loadFileVersionsDivId + "']");

				var info = template.getElement("[id='loadFileVersionTemplateP']");// 用于显示加载文件基本信息
				var loadFileVersionDivId = this.page.getLoadFileVersionDivId(loadFileVersion.id);// 封装基本信息和模块的ID
				info.set("id", +"_info");

				// 图标
				var icon = info.getElement("[title='icon']");
				icon.erase('title').set("id", loadFileVersionDivId + "_icon").set("src", hiddenIcon);
				icon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenVersion(loadFileVersion.id);
				}.bind(this));

				// 填写加载文件信息
				info.getElement("[title='ID']").set("html", loadFileVersion.id);
				info.getElement("[title='版本号']").set("html", loadFileVersion.versionNo);

				// 添加操作事件
				info.getElement("[title='创建模块']").addEvent("click", function(event) {
					event.stop();
					var loadModule = new Application.LoadModule({
						page : this.page,
						applicationVersionId : this.page.applicationVersionId,
						loadFileVersionId : loadFileVersion.id
					});
					loadModule.createNewModule();
				}.bind(this));
				info.getElement("[title='添加依赖']").addEvent("click", function(event) {
					event.stop();
					new Application.LoadFile.Dependecy({
						page : this.page,
						loadFileVersionId : loadFileVersion.id
					}).getUndependencyLoadFiles();
				}.bind(this));

				var loadFileVersionDetailsAndLoadModules = template.getElement("[id='loadFileVersionDetailsAndLoadModuules']").set("id",
						loadFileVersionDivId + "_loadFileVersionDetailsAndLoadModuules");

				// 详细信息
				var deatilsDivId = this.page.getLoadFileVersionDetailsDivId(loadFileVersion.id);
				var details = loadFileVersionDetailsAndLoadModules.getElement("[title='loadFileVersionDetails']");
				details.erase("title").set("id", deatilsDivId);
				details.getElement("[title='hash']").set("html", loadFileVersion.hash);
				details.getElement("[title='加载参数']").set("html", loadFileVersion.loadParams);
				details.getElement("[title='文件大小']").set("html", loadFileVersion.fileSize);

				// 详细信息展开收拢
				var detailsIcon = loadFileVersionDetailsAndLoadModules.getElement("[title='detailsIcon']");
				detailsIcon.erase('title').set("id", deatilsDivId + "_icon1").set("src", hiddenIcon);
				detailsIcon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenVersionDetails(loadFileVersion.id);
				}.bind(this));

				// 配置显示模块的Div
				var versionsDiv = loadFileVersionDetailsAndLoadModules.getElement("[title='loadModules']");
				var versionsDivId = this.page.getLoadModulesDivId(loadFileVersion.id);
				versionsDiv.erase("title").set("id", versionsDivId);
				// 模块信息展开收拢
				var modulesIcon = loadFileVersionDetailsAndLoadModules.getElement("[title='modulesIcon']");
				modulesIcon.erase('title').set("id", versionsDivId + "_icon2").set("src", hiddenIcon);
				modulesIcon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenLoadModules(loadFileVersion.id);
				}.bind(this));

				// 配置显示依赖的Div
				var dependenciesDiv = loadFileVersionDetailsAndLoadModules.getElement("[title='loadFileVersionDependencies']");
				var dependenciesDivId = this.page.getDependenciesDivId(loadFileVersion.id);
				dependenciesDiv.erase("title").set("id", dependenciesDivId);
				// 依赖信息展开收拢
				var dependenciesIcon = loadFileVersionDetailsAndLoadModules.getElement("[title='dependenciesIcon']");
				dependenciesIcon.erase('title').set("id", dependenciesDivId + "_icon3").set("src", hiddenIcon);
				dependenciesIcon.removeEvent("click").addEvent("click", function(event) {
					event.stop();
					this.hiddenLoadFileVersionDependencies(loadFileVersion.id);
				}.bind(this));

				// 显示在页面上
				var loadFileVersionDiv = template.set("id", loadFileVersionDivId); // 封装基本信息和模块
				loadFileVersionDiv.inject(target);

				this.hiddenVersion(loadFileVersion.id);
				this.hiddenVersionDetails(loadFileVersion.id);
				this.hiddenLoadModules(loadFileVersion.id);
				this.hiddenLoadFileVersionDependencies(loadFileVersion.id);
			}
		});

Application.LoadFile.Dependecy = new Class({
	Implements : [ Events, Options ],

	loadFileModal : null,
	loadFileVersionModal : null,
	removeDependenceModal : null,
	formId : null,
	page : null,
	loadFileVersionId : -1,

	options : {
		loadFileVersionId : -1,
		page : null
	},

	initialize : function(options) {
		this.setOptions(options);
		this.loadFileVersionId = this.options.loadFileVersionId;
		this.page = this.options.page;
		this.formId = "form" + new Date().valueOf();
	},

	showDependece : function(dependence) {
		var dependencesDivId = this.page.getDependenciesDivId(this.loadFileVersionId);
		var target = $(dependencesDivId);

		var template = $("loadFileVersionDependenceTemplateDiv");

		var row = template.getElement("[id='loadFileVersionDependenceTemplate']").clone();
		var dependenceDivId = this.page.getDependenceDivId(this.loadFileVersionId, dependence.id);
		row.set("id", dependenceDivId);
		row.getElement("[title='ID']").set("html", dependence.id);
		row.getElement("[title='名称']").set("html", dependence.name);
		row.getElement("[title='AID']").set("html", dependence.aid);
		row.getElement("[title='版本号']").set("html", dependence.versionNo);

		row.getElement("[title ='移除依赖']").addEvent("click", function(event) {
			event.stop();
			this.removeDependenceModal = new LightFace({
				title : "确认移除",
				appletModal : this.modal,
				height : 50,
				width : 300,
				content : "确认移除依赖？",
				resetOnScroll : false,
				buttons : [ {
					title : "确认",
					color : "blue",
					event : function() {
						this.removeDependence(dependence.id);
						this.closeRemoveDependenceModal();
					}.bind(this)
				}, {
					title : "取消",
					event : function() {
						this.close();
					}
				} ]
			}).open();
		}.bind(this));
		row.inject(target);
	},

	removeDependence : function(id) {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=removeDependence",
			data : {
				childLoadFileVersionId : this.loadFileVersionId,
				parentLoadFileVersionId : id
			},
			onSuccess : function(json) {
				this.removeDependenceCallback(json, id);
			}.bind(this)
		}).post();
	},

	removeDependenceCallback : function(json, dependenceId) {
		if (json.success) {
			new LightFace.MessageBox().info("操作成功");
			var dependenceDivId = this.page.getDependenceDivId(this.loadFileVersionId, dependenceId);
			$(dependenceDivId).dispose();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	getUndependencyLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFile/?m=getUndependentLoadFiles",
			data : {
				loadFileVersionId : this.loadFileVersionId
			},
			onSuccess : this.getUndependencyLoadFilesCallback.bind(this)
		}).get();
	},

	getUndependencyLoadFilesCallback : function(json) {
		if (json.success) {
			var template = $("loadFileTemplateTable");
			this.loadFileModal = new LightFace({
				title : "添加依赖",
				height : 450,
				width : 740,
				resetOnScroll : false,
				content : template.get("html"),
				buttons : [ {
					title : "关闭",
					event : this.closeLoadFileModal.bind(this)
				} ]
			});
			var box = this.loadFileModal.getBox();
			json.result.each(function(item) {
				var target = box.getElement("[id='loadFiles']");

				var row = box.getElement("[id='loadFileTemplateTr']").clone().setStyle("display", "");
				row.set("id", "dependence_loadFile" + item.id);
				row.getElement("[title='ID']").set("html", item.id);
				if ($chk(item.name)) {
					row.getElement("[title='名称']").set("html", item.name);
				}
				if ($chk(item.aid)) {
					row.getElement("[title='AID']").set("html", item.aid);
				}

				row.getElement("[title='选择版本']").addEvent("click", function(event) {
					event.stop();
					this.getLoadFileVersions(item.id);
				}.bind(this));
				row.inject(target);
			}.bind(this));

			this.loadFileModal.open();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	closeLoadFileModal : function() {
		this.loadFileModal.getBox().dispose();
		this.loadFileModal.close();
	},

	getLoadFileVersions : function(loadFileVersionId) {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getByCriteria",
			data : {
				search_ALIAS_loadFileL_EQL_id : loadFileVersionId
			},
			onSuccess : this.getLoadFileVersionsCallback.bind(this)
		}).get();
	},

	getLoadFileVersionsCallback : function(json) {
		var template = $("undependenentLoadFileVersionFormTemlate").clone();
		var html = template.getElement("[title='undependenentLoadFileVersionDiv']").get("html");

		if (json.success) {
			this.loadFileVersionModal = new LightFace({
				title : "选择版本",
				width : 480,
				content : html,
				resetOnScroll : false,
				buttons : [ {
					title : "确认",
					resetOnScroll : false,
					color : "blue",
					event : this.addDependence.bind(this)
				}, {
					title : "取消",
					event : this.closeLoadFileVersionModal.bind(this)
				} ]
			});

			var box = this.loadFileVersionModal.getBox();
			var target = box.getElement("[title='undependenentLoadFileVersions']");
			target.erase("title").set("id", this.formId);
			target.getElement("[name='childLoadFileVersionId']").set("value", this.loadFileVersionId);

			json.result.each(function(item) {
				var row = template.getElement("[title='undependenentLoadFileVersionsTemplate']").clone();
				row.erase("title").setStyle("display", "");

				row.getElement("input[type='radio']").set("value", item.id);
				row.getElement("input[type='radio']").setStyle("display", "");
				row.getElement("[title='版本号']").set("html", item.versionNo);
				row.getElement("[title='Hash']").set("html", item.hash);
				row.getElement("[title='加载参数']").set("html", item.loadParams);
				row.getElement("[title='文件大小']").set("html", item.fileSize);

				row.inject(target);
			});

			this.loadFileVersionModal.open();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	addDependence : function() {
		var data = $(this.formId).toQueryString();

		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=addDependence",
			data : data,
			onSuccess : this.addDependenceCallback.bind(this)
		}).post();
	},

	addDependenceCallback : function(json) {
		if (json.success) {
			new LightFace.MessageBox().info("操作成功");
			this.showDependece(json.message);
			this.refreshUndependencyLoadFiles(json.message.loadFile_id);
			this.closeLoadFileVersionModal();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	refreshUndependencyLoadFiles : function(id) {
		var box = this.loadFileModal.getBox();
		box.getElement("[id='dependence_loadFile" + id + "']").dispose();
	},

	closeLoadFileVersionModal : function() {
		this.loadFileVersionModal.getBox().dispose();
		this.loadFileVersionModal.close();
	},
	closeRemoveDependenceModal : function() {
		this.removeDependenceModal.getBox().dispose();
		this.removeDependenceModal.close();
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
	},

	inputLoadParams : function(loadParams) {
		var template = $("loadParamsFormDivTemplate").clone(true, true);
		var html = template.get("html");
		this.modal = new LightFace({
			title : "输入加载参数",
			loadModule : this,
			height : 150,
			width : 400,
			resetOnScroll : false,
			color : "blue",
			content : html,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : function() {
					this.modal.getBox().getElement('[type="submit"]').click();
				}.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});

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

		new FormCheck(box.getElement("form"), {
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
				this.submit();
			}.bind(this)
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
		var data = $(this.formId).toQueryString();
		new Request.JSON({
			url : ctx + "/html/loadFile/?m=buildLoadParams",
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).post();
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

Application.LoadModule = new Class({

	Implements : [ Events, Options ],

	modal : null,
	formId : null,
	page : null,
	loadFileVersionId : -1,

	options : {
		loadFileVersionId : -1,
		page : null
	},

	initialize : function(options) {
		this.setOptions(options);
		this.loadFileVersionId = this.options.loadFileVersionId;
		this.page = this.options.page;
		this.formId = "form" + new Date().valueOf();
	},

	createNewModule : function() {
		var template = $("loadModuleFormTemplateDiv").clone(true, true);
		var html = template.get("html");
		this.modal = new LightFace({
			title : "创建模块",
			height : 200,
			width : 450,
			content : html,
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : function() {
					this.modal.getBox().getElement('[type="submit"]').click();
				}.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='loadModule']").set("id", this.formId).eliminate("title");
		box.getElement("[name='loadFileVersionId']").set("value", this.loadFileVersionId);

		new FormCheck(box.getElement("form"), {
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
			url : ctx + "/html/loadModule/?m=createNewLoadModule",
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(json) {
		this.close();
		if (json.success) {
			var loadModule = json.message;
			loadModule.loadFileVersion_id = loadModule.loadFileId;
			this.showLoadModule(loadModule);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	hidden : function(id) {
		var loadModuleDivId = this.page.getLoadModuleDivId(id);
		$(loadModuleDivId + "_details").setStyle("display", "none");

		var icon = $(loadModuleDivId + "_icon");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spread(id);
		}.bind(this));
	},

	spread : function(id) {
		var loadModuleDivId = this.page.getLoadModuleDivId(id);
		$(loadModuleDivId + "_details").setStyle("display", "");

		var icon = $(loadModuleDivId + "_icon");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hidden(id);
		}.bind(this));
	},

	hiddenDetails : function(id) {
		var detailsDivId = this.page.getLoadModuleDetailsDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spreadDetails(id);
		}.bind(this));

	},

	spreadDetails : function(id) {
		var detailsDivId = this.page.getLoadModuleDetailsDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenDetails(id);
		}.bind(this));

	},

	showLoadModule : function(loadModule) {
		var template = $("loadModuleTemplateDiv").clone(true, true).setStyle("display", "");

		var loadFileVersionDivId = this.page.getLoadFileVersionDivId(loadModule.loadFileVersion_id);// 用于封装加载文件基本信息和所属模块的ID
		var loadFileVersion = $(loadFileVersionDivId);
		var loadModulesDivId = this.page.getLoadModulesDivId(loadModule.loadFileVersion_id);// 用于显示该加载文件所属模块的ID
		var target = loadFileVersion.getElement("[id='" + loadModulesDivId + "']");

		var loadModuleDivId = this.page.getLoadModuleDivId(loadModule.id);// 封装模块基本信息和实例的ID

		var info = template.getElement("[id='loadModuleTemplateP']");
		info.set("id", loadModuleDivId + "_info");

		// 图标
		var icon = info.getElement("[title='icon']");
		icon.erase('title').set("id", loadModuleDivId + "_icon").set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hidden(loadModule.id);
		}.bind(this));

		// 填写数据
		info.getElement("[title='ID']").set("html", loadModule.id);
		info.getElement("[title='名称']").set("html", loadModule.name);
		info.getElement("[title='AID']").set("html", loadModule.aid);

		// 添加事件
		info.getElement("[title='删除']").addEvent("click", function(event) {
			event.stop();
			var confirm = null;
			confirm = new LightFace({
				title : "确认删除",
				appletModal : this.modal,
				height : 50,
				width : 300,
				content : "确认删除模块？",
				resetOnScroll : false,
				buttons : [ {
					title : "确认",
					color : "blue",
					event : function() {
						confirm.close();
						this.remove(loadModule.id);
					}.bind(this)
				}, {
					title : "取消",
					event : function() {
						confirm.close();
					}.bind(this)
				} ]
			});
			confirm.open();
		}.bind(this));

		// 处理详细信息
		var deatilsDivId = this.page.getLoadModuleDetailsDivId(loadModule.id);

		var details = template.getElement("[id='loadModuleDetails']").set("id", loadModuleDivId + "_loadModuleDetails");
		details.erase("title").set("id", deatilsDivId);
		details.getElement("[title='备注']").set("html", loadModule.comments).set("title", loadModule.comments);
		template.set("id", loadModuleDivId).inject(target);
	},

	remove : function(loadModuleId) {
		new Request.JSON({
			url : ctx + "/html/loadModule/?m=removeLoadModule",
			data : {
				loadModuleId : loadModuleId
			},
			onSuccess : this.removeCallback.bind(this)
		}).post();
	},

	removeCallback : function(json) {
		if (json.success) {
			var loadModuleDivId = this.page.getLoadModuleDivId(json.message);
			var loadModuleDiv = $(loadModuleDivId);
			loadModuleDiv.dispose();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	}
});