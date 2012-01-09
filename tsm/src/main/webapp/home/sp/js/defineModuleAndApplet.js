Application = {};
Application.Page = new Class({

	Implements : [ Events, Options ],

	applicationVersionId : -1,
	loadFiles : new Array(),
	applets : new Array(),
	downloadOrder : null,
	deleteOrder : null,
	loadFileConstants : null,

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
		this.getConstant();
	},

	getConstant : function() {
		new Request.JSON({
			async : false,
			url : ctx + "/html/loadFile/?m=exportConstant",
			onSuccess : function(json) {
				if (json.success) {
					this.loadFileConstants = json.message;
				}
			}.bind(this)
		}).get();
	},

	/**
	 * 获取当前应用版本已使用的加载文件
	 */
	getLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getByApplicationVersionThatTypeCms2acFile",
			data : {
				applicationVersionId : this.options.applicationVersionId
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
				this.getLoadModules(loadFileJson.id, loadFileJson.shareFlag);
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
	getLoadModules : function(id, shareFlag) {
		new Request.JSON({
			url : ctx + "/html/loadModule/?m=getByCriteria",
			data : {
				search_ALIAS_loadFileVersionL_EQL_id : id
			},
			onSuccess : function(json) {
				this.getLoadModulesCallback(json, shareFlag);
			}.bind(this)
		}).get();
	},

	getLoadModulesCallback : function(json, shareFlag) {
		if (json.success) {
			json.result.each(function(loadModuleJson) {
				var loadModule = new Application.LoadModule({
					applicationVersionId : this.applicationVersionId,
					loadFileVersionId : loadModuleJson.loadFileId,
					page : this
				});
				loadModuleJson.shareFlag = shareFlag;
				loadModule.showLoadModule(loadModuleJson);
				this.getApplets(loadModuleJson.id);
			}.bind(this));
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	/**
	 * 获取指定模块已定义且当前应用版本所使用的实例
	 * 
	 * @param id
	 *            模块ID
	 */
	getApplets : function(id) {
		new Request.JSON({
			url : ctx + "/html/applet/?m=getByCriteria",
			data : {
				search_ALIAS_applicationVersionL_EQL_id : this.applicationVersionId,
				search_ALIAS_loadModuleL_EQL_id : id
			},
			onSuccess : this.getAppletsCallback.bind(this)
		}).get();
	},

	getAppletsCallback : function(json) {
		if (json.success) {
			json.result.each(function(appletJson) {
				var applet = new Application.Applet({
					applicationVersionId : this.applicationVersionId,
					loadModuleId : appletJson.loadModuleId,
					page : this
				});
				applet.showApplet(appletJson);
			}.bind(this));
		}
	},

	/**
	 * 根据加载文件的ID组装显示加载文件信息DIV的ID
	 */
	getLoadFileVersionDivId : function(loadFileId) {
		return "loadFile" + loadFileId;
	},

	/**
	 * 根据加载文件的ID组装显示加载文件详细信息DIV的ID
	 */
	getLoadFileVersionDetailsDivId : function(loadFileId) {
		return this.getLoadFileVersionDivId(loadFileId) + "_details";
	},

	/**
	 * 根据加载文件的ID组装显示属于该加载文件的模块DIV的ID
	 */
	getLoadModulesDivId : function(loadFileId) {
		return this.getLoadFileVersionDivId(loadFileId) + "_loadModules";
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
			height : 300,
			width : 500,
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
			row.getElement("[title='版本号']").set("html", item.versionNo);
			row.getElement("[title='Hash']").set("html", item.hash);
			row.getElement("[title='加载参数']").set("html", item.loadParams);
			row.getElement("[title='文件大小']").set("html", item.fileSize);

			row.inject(target);
		});

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

		this.modal.open();
	},

	close : function() {
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
		try {
			this.close();
		} catch (e) {
			console.log(e);
		}

		if (json.success) {
			var loadFile = new Application.LoadFile({
				applicationVersionId : this.applicationVersionId,
				page : this.page
			});
			loadFile.showLoadFile(json.message);
			this.page.getLoadModules(json.message.id);
			this.page.getExclusiveLoadFiles();
		}
	}
});

Application.LoadFile = new Class({

	Implements : [ Events, Options ],

	page : null,
	modal : null,
	tempFileId : "",
	formId : null,
	applicationVersionId : -1,

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

	hidden : function(id) {
		var loadFileDivId = this.page.getLoadFileVersionDivId(id);

		$(loadFileDivId + "_loadFileDetailsAndModules").setStyle("display", "none");

		var icon = $(loadFileDivId + "_icon");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spread(id);
		}.bind(this));

	},

	spread : function(id) {
		var loadFileDivId = this.page.getLoadFileVersionDivId(id);

		$(loadFileDivId + "_loadFileDetailsAndModules").setStyle("display", "");

		var icon = $(loadFileDivId + "_icon");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hidden(id);
		}.bind(this));

	},

	hiddenDetails : function(id) {
		var detailsDivId = this.page.getLoadFileVersionDetailsDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spreadDetails(id);
		}.bind(this));

	},

	spreadDetails : function(id) {
		var detailsDivId = this.page.getLoadFileVersionDetailsDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenDetails(id);
		}.bind(this));

	},

	hiddenModules : function(id) {
		var detailsDivId = this.page.getLoadModulesDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spreadModules(id);
		}.bind(this));

	},

	spreadModules : function(id) {
		var detailsDivId = this.page.getLoadModulesDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenModules(id);
		}.bind(this));

	},

	/**
	 * 将以添加的加载文件显示在页面上
	 */
	showLoadFile : function(loadFile) {
		var template = $("loadFileTemplateDiv").getElement("[title='loadFileTemplateDiv']").clone(true, true).erase("title");
		var target = $("uploadedCapsInfo");

		var info = template.getElement("[id='loadFileTemplateP']");// 用于显示加载文件基本信息
		var loadFileDivId = this.page.getLoadFileVersionDivId(loadFile.id);// 封装基本信息和模块的ID
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
		var loadFileName = info.getElement("[title='名称']").set("html", loadFile.name);
		var loadFileAid = info.getElement("[title='AID']").set("html", '（' + loadFile.aid + '）');
		loadFileName.setStyle('font-weight', 'bold');
		loadFileAid.setStyle('font-weight', 'bold');
		if (loadFile.shareFlag == 0) {
			loadFileName.setStyle('color', 'red');
			loadFileAid.setStyle('color', 'red');
		} else {
			loadFileName.setStyle('color', 'green');
			loadFileAid.setStyle('color', 'green');
		}

		// 添加操作事件
		info.getElement("[title='创建模块']").addEvent("click", function(event) {
			event.stop();
			var loadModule = new Application.LoadModule({
				page : this.page,
				applicationVersionId : this.page.applicationVersionId,
				loadFileVersionId : loadFile.id
			});
			loadModule.createNewModule();
		}.bind(this));
		if (this.page.loadFileConstants.FLAG_SHARED == loadFile.shareFlag) {
			info.getElement("[title='创建模块']").setStyle("display", "none");
		}

		var loadFileDetailsAndModules = template.getElement("[id='loadFileDetailsAndModules']").set("id",
				loadFileDivId + "_loadFileDetailsAndModules");

		// 详细信息
		var deatilsDivId = this.page.getLoadFileVersionDetailsDivId(loadFile.id);
		var details = loadFileDetailsAndModules.getElement("[title='loadFileDetails']");
		details.erase("title").set("id", deatilsDivId);
		details.getElement("[title='版本号']").set("html", loadFile.versionNo);
		details.getElement("[title='备注']").set("html", loadFile.comments);
		details.getElement("[title='加载参数']").set("html", loadFile.loadParams);
		details.getElement("[title='文件大小']").set("html", loadFile.fileSize);
		// 详细信息展开收拢
		var detailsIcon = loadFileDetailsAndModules.getElement("[title='detailsIcon']");
		detailsIcon.erase('title').set("id", deatilsDivId + "_icon1").set("src", hiddenIcon);
		detailsIcon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenDetails(loadFile.id);
		}.bind(this));

		// 配置显示模块的Div
		var loadModulesDiv = loadFileDetailsAndModules.getElement("[title='loadModules']");
		var loadModulesDivId = this.page.getLoadModulesDivId(loadFile.id);
		loadModulesDiv.erase("title").set("id", loadModulesDivId);
		// 模块信息展开收拢
		var modulesIcon = loadFileDetailsAndModules.getElement("[title='modulesIcon']");
		modulesIcon.erase('title').set("id", loadModulesDivId + "_icon2").set("src", hiddenIcon);
		modulesIcon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenModules(loadFile.id);
		}.bind(this));

		// 显示在页面上
		var loadFileDiv = template.set("id", loadFileDivId); // 封装基本信息和模块
		loadFileDiv.inject(target);
	},

	/**
	 * 弹出输入加载文件信息的界面
	 */
	createNewLoadFile : function() {
		this.tempFileId = (new Date()).valueOf() + "File";

		this.modal = new LightFace({
			title : "上传加载文件",
			height : 300,
			width : 500,
			content : $("uploadDivTemplate").get("html"),
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

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='loadFile']").set("id", this.formId).eliminate("title");
		box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);
		box.getElement("[id='file']").set("id", this.tempFileId);

		// 添加事件
		box.getElement("[title='点击输入参数']").addEvent("click", this.inputLoadParams.bind(this));
		box.getElement("[title='上传']").addEvent("click", this.uploadCap.bind(this));

		this.modal.open();
	},

	remove : function(loadFileVersionId) {
		new Request.JSON({
			url : ctx + "/html/applicationLoadFile/?m=removeImportBetweenLoadFileVersionAndApplicationVersion",
			data : {
				applicationVersionId : this.applicationVersionId,
				loadFileVersionId : loadFileVersionId
			},
			onSuccess : this.removeCall.bind(this)
		}).post();
	},

	removeCall : function(json) {
		if (json.success) {
			var loadFileDivId = this.page.getLoadFileVersionDivId(json.message);
			var loadFileDiv = $(loadFileDivId);
			loadFileDiv.dispose();

			this.page.getExlusiveLoadFiles();
			this.page.getDownloadOrder();
			this.page.getDeleteOrder();
			this.page.getInstallOrder();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
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

	/**
	 * 上传CAP文件
	 */
	uploadCap : function() {
		var uploader = new JIM.AjaxUploadFile({
			url : ctx + "/html/commons/?m=upload",
			fileElementId : this.tempFileId,
			onSuccess : this.uploadCapCallback.bind(this)
		});

		uploader.upload();
	},

	uploadCapCallback : function(json) {
		if (json.success) {
			var box = this.modal.getBox();
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
		this.modal.getBox().dispose();
		this.modal.close();
	},

	submitNewVersion : function() {
		data = $(this.formId).toQueryString();

		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=createNewLoadFileVersion",
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	/**
	 * 提交加载文件信息
	 */
	submit : function() {
		var request = new Request.JSON({
			url : ctx + "/html/loadFile/?m=createNewLoadFileForApplicationVersion",
			data : $(this.formId),
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(json) {
		this.close();
		if (json.success) {
			this.page.getDownloadOrder();
			this.page.getDeleteOrder();
			this.showLoadFile(json.message);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	/**
	 * 升级加载文件
	 */
	createNewLoadFileVersion : function(loadFileId) {
		this.tempFileId = (new Date()).valueOf() + "File";

		this.modal = new LightFace({
			title : "升级加载文件",
			height : 300,
			width : 500,
			content : $("loadFileVersionDivTemplate").get("html"),
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : this.submitNewVersion.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		}).open();

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='loadFileVersion']").set("id", this.formId).eliminate("title");
		box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);
		box.getElement("[name='loadFileId']").set("value", loadFileId);
		box.getElement("[id='file']").set("id", this.tempFileId);

		// 添加事件
		box.getElement("[title='点击输入参数']").addEvent("click", this.inputLoadParams.bind(this));
		box.getElement("[title='上传']").addEvent("click", this.uploadCap.bind(this));
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

		// 填写加载参数的弹出窗口
		var template = $("loadParamsFormDivTemplate").clone(true, true);
		var html = template.get("html");

		this.modal = new LightFace({
			title : "输入加载参数",
			loadModule : this,
			height : 300,
			width : 500,
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

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='loadParams']").set("id", this.formId).eliminate("title");
		box.getElement("[name='nonVolatileCodeSpace']").set("value", this.jsonLoadParams.nonVolatileCodeSpace);
		box.getElement("[name='volatileDateSpace']").set("value", this.jsonLoadParams.volatileDateSpace);
		box.getElement("[name='nonVolatileDateSpace']").set("value", this.jsonLoadParams.nonVolatileDateSpace);

		this.modal.open();
	},

	close : function() {
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
		this.close();
		if (json.success) {
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
	// 加载页面验证
	addValidate : function() {
		var box = this.modal.getBox();
		var form = box.getElement("[title='loadModule']");
		var formCheck = new FormCheck(form, {
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
	createNewModule : function() {
		var template = $("loadModuleFormTemplateDiv").clone(true, true);
		var html = template.get("html");
		this.modal = new LightFace({
			title : "创建模块",
			loadModule : this,
			height : 300,
			width : 500,
			content : html,
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : function() {
					this.modal.getBox().getElement('button[type=submit]').click();
				}.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});

		new FormCheck(this.modal.getBox().getElement('form'), {
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
		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='loadModule']").set("id", this.formId).eliminate("title");
		box.getElement("[name='loadFileVersionId']").set("value", this.loadFileVersionId);

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
		if (json.success) {
			this.close();
			this.showLoadModule(json.message);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	hidden : function(id) {
		var loadModuleDivId = this.page.getLoadModuleDivId(id);
		$(loadModuleDivId + "_loadModuleDetailsAndApplets").setStyle("display", "none");

		var icon = $(loadModuleDivId + "_icon");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spread(id);
		}.bind(this));
	},

	spread : function(id) {
		var loadModuleDivId = this.page.getLoadModuleDivId(id);
		$(loadModuleDivId + "_loadModuleDetailsAndApplets").setStyle("display", "");

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

	hiddenApplets : function(id) {
		var detailsDivId = this.page.getAppletsDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spreadApplets(id);
		}.bind(this));

	},

	spreadApplets : function(id) {
		var detailsDivId = this.page.getAppletsDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenApplets(id);
		}.bind(this));

	},

	showLoadModule : function(loadModule) {
		var template = $("loadModuleTemplateDiv");
		var loadFileDivId = this.page.getLoadFileVersionDivId(loadModule.loadFileId);// 用于封装加载文件基本信息和所属模块的ID
		var loadFileDiv = $(loadFileDivId);
		var loadModulesDivId = this.page.getLoadModulesDivId(loadModule.loadFileId);// 用于显示该加载文件所属模块的ID
		var target = loadFileDiv.getElement("[id='" + loadModulesDivId + "']");

		var loadModuleDivId = this.page.getLoadModuleDivId(loadModule.id);// 封装模块基本信息和实例的ID

		var info = template.getElement("[id='loadModuleTemplateP']").clone(true, true);
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
		var page = this.page;
		info.getElement("[title='创建实例']").addEvent("click", function(event) {
			event.stop();
			(new Application.Applet({
				page : page,
				applicationVersionId : page.applicationVersionId,
				loadModuleId : loadModule.id
			})).createNewApplet();
		});
		info.getElement("[title='删除模块']").addEvent("click", function(event) {
			event.stop();
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

		if (this.page.loadFileConstants.FLAG_SHARED == loadModule.shareFlag) {
			info.getElement("[title='删除模块']").setStyle("display", "none");
		}

		var loadModuleDetailsAndApplets = template.getElement("[id='loadModuleDetailsAndApplets']").clone(true, true).set("id",
				loadModuleDivId + "_loadModuleDetailsAndApplets");

		// 处理详细信息
		var deatilsDivId = this.page.getLoadModuleDetailsDivId(loadModule.id);
		var details = loadModuleDetailsAndApplets.getElement("[title='loadModuleDetails']");
		details.erase("title").set("id", deatilsDivId);
		details.getElement("[title='备注']").set("html", loadModule.comments);
		// 详细信息展开收拢
		var detailsIcon = loadModuleDetailsAndApplets.getElement("[title='detailsIcon']");
		detailsIcon.erase('title').set("id", deatilsDivId + "_icon1").set("src", hiddenIcon);
		detailsIcon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenDetails(loadModule.id);
		}.bind(this));

		// 实例
		var appletsDiv = loadModuleDetailsAndApplets.getElement("[title='applets']");
		var appletsDivId = this.page.getAppletsDivId(loadModule.id);
		appletsDiv.set("id", appletsDivId);
		// 实例信息展开收拢
		var appletsIcon = loadModuleDetailsAndApplets.getElement("[title='appletsIcon']");
		appletsIcon.erase('title').set("id", appletsDivId + "_icon2").set("src", hiddenIcon);
		appletsIcon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hiddenApplets(loadModule.id);
		}.bind(this));

		var loadFileDiv = new Element("div").set("id", loadModuleDivId);
		var table = new Element('table', {
			'class' : 'myTable'
		}).inject(loadFileDiv);
		var tbody = table.getElement('tbody');
		if (!$chk(tbody)) {
			tbody = new Element('tbody').inject(table);
		}
		var tr = new Element('tr').inject(tbody);
		var td = new Element('td').inject(tr);
		info.inject(td);
		loadModuleDetailsAndApplets.inject(td);
		loadFileDiv.inject(target);
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

Application.Applet = new Class({

	Implements : [ Events, Options ],

	modal : null,
	formId : null,
	page : null,
	privilegeModal : null,
	applicationVersionId : -1,
	loadModuleId : -1,

	hexPrivilege : "00",
	jsonPrivilege : {
		lockCard : false,
		abandonCard : false,
		defaultSelect : false,
		cvm : false
	},

	options : {
		page : null,
		loadModuleId : -1,
		loadFileVersionId : -1
	},

	initialize : function(options) {
		this.setOptions(options);
		this.page = this.options.page;
		this.loadModuleId = this.options.loadModuleId;
		this.applicationVersionId = this.options.applicationVersionId;
		this.formId = "form" + new Date().valueOf();
	},

	createNewApplet : function() {
		var template = $("appletFormTemplateDiv").clone(true, true);

		var html = template.get("html");

		this.modal = new LightFace({
			title : "创建实例",
			loadModule : this,
			height : 300,
			width : 500,
			content : html,
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : function() {
					this.modal.getBox().getElement('button[type=submit]').click();
				}.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='applet']").set("id", this.formId).eliminate("applet");
		box.getElement("[name='loadModuleId']").set("value", this.loadModuleId);
		box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);

		// 添加事件
		box.getElement("[title='点击选择权限']").addEvent("click", function(event) {
			event.stop();
			this.selectPrivilege();
		}.bind(this));
		box.getElement("[title='点击输入参数']").addEvent("click", function(event) {
			event.stop();
			this.inputInstallParams();
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
			onValidateSuccess : function() {// 校验通过执行load()
				this.submit();
			}.bind(this)
		});

		this.modal.open();
	},

	selectPrivilege : function() {
		var privilege = new Application.Applet.Privilege();
		privilege.applet = this;

		var privilegeValue = this.modal.getBox().getElement("[name='hexPrivilege']").get("value");
		privilege.selectPrivilege(privilegeValue);
	},

	refreshPrivilege : function(privilege) {
		this.modal.getBox().getElement("[name='hexPrivilege']").set("value", privilege);
	},

	inputInstallParams : function() {
		var installParams = new Application.Applet.InstallParams();
		installParams.applet = this;

		var installParamsValue = this.modal.box.getElement("[name='installParams']").get("value");
		installParams.inputInstallParams(installParamsValue);
	},

	refreshInstallParams : function(params) {
		this.modal.getBox().getElement("[name='installParams']").set("value", params);
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
			url : ctx + "/html/applet/?m=createNewApplet",
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(json) {
		if (json.success) {
			this.close();
			this.showApplet(json.message);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	hidden : function(id) {
		var appletDivId = this.page.getAppletDivId(id);
		var appletDiv = $(appletDivId);

		appletDiv.getElement("[id='appletDetails']").setStyle("display", "none");

		var icon = $(appletDivId + "_icon");
		icon.set("src", spreadIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.spread(id);
		}.bind(this));
	},

	spread : function(id) {
		var appletDivId = this.page.getAppletDivId(id);
		var appletDiv = $(appletDivId);

		appletDiv.getElement("[id='appletDetails']").setStyle("display", "");

		var icon = $(appletDivId + "_icon");
		icon.set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hidden(id);
		}.bind(this));
	},

	showApplet : function(applet) {
		var template = $("appletTemplateDiv");
		var loadLoadModuleDivId = this.page.getLoadModuleDivId(applet.loadModuleId);// 用于封装模块基本信息和所属实例的ID
		var loadModule = $(loadLoadModuleDivId);
		var appletsDivId = this.page.getAppletsDivId(applet.loadModuleId);// 用于显示所属实例的ID
		var target = loadModule.getElement("[id='" + appletsDivId + "']");

		var appletDivId = this.page.getAppletDivId(applet.id);// 封装模块基本信息和实例的ID

		var info = template.getElement("[id='appletTemplateP']").clone(true, true);
		info.set("id", appletDivId + "_info");

		// 图标
		var icon = info.getElement("[title='icon']");
		icon.erase('title').set("id", appletDivId + "_icon").set("src", hiddenIcon);
		icon.removeEvent("click").addEvent("click", function(event) {
			event.stop();
			this.hidden(applet.id);
		}.bind(this));

		// 填写数据
		info.getElement("[title='ID']").set("html", applet.id);
		info.getElement("[title='名称']").set("html", applet.name);
		info.getElement("[title='AID']").set("html", applet.aid);

		info.getElement("[title='删除实例']").addEvent("click", function(event) {
			event.stop();
			var confirm = new LightFace({
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
						this.remove(applet.id);
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

		var details = template.getElement("[id='appletDetails']").clone(true, true).set("id", "appletDetails");
		details.getElement("[title='权限']").set("html", applet.privilege);
		details.getElement("[title='安装参数']").set("html", applet.installParams).set("title", applet.installParams);

		var loadFileDiv = new Element("div").set("id", appletDivId);
		var table = new Element('table', {
			'class' : 'myTable'
		}).inject(loadFileDiv);
		var tbody = table.getElement('tbody');
		if (!$chk(tbody)) {
			tbody = new Element('tbody').inject(table);
		}
		var tr = new Element('tr').inject(tbody);
		var td = new Element('td').inject(tr);
		info.inject(td);
		details.inject(td);
		loadFileDiv.inject(target);
	},

	remove : function(appletId) {
		new Request.JSON({
			url : ctx + "/html/applet/?m=removeApplet",
			data : {
				appletId : appletId
			},
			onSuccess : this.removeCallback.bind(this)
		}).post();
	},

	removeCallback : function(json) {
		if (json.success) {
			var appletDivId = this.page.getAppletDivId(json.message);
			var appletDiv = $(appletDivId);
			appletDiv.dispose();

		} else {
			new LightFace.MessageBox().error(json.message);
		}
	}
});

Application.Applet.Privilege = new Class({
	Implements : [ Events, Options ],

	applet : null,
	modal : null,
	formId : null,

	initialize : function(options) {
		this.setOptions(options);
		this.formId = "form" + new Date().valueOf();
	},

	selectPrivilege : function(privilege) {
		// 向服务器发起请求解析权限
		new Request.JSON({
			url : ctx + "/html/commons/?m=parsePrivilege",
			data : {
				hexPrivilege : privilege
			},
			async : false,
			onSuccess : function(json) {
				if (json.success) {
					this.jsonPrivilege = json.message;
				}
			}.bind(this)
		}).get();

		var template = $("appletSelectPrivilegeFormTemplateDiv").clone(true, true);
		var html = template.get("html");

		this.modal = new LightFace({
			title : "选择权限",
			appletModal : this.modal,
			height : 300,
			width : 500,
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

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='privilege']").set("id", this.formId).eliminate("privilege");
		if (this.jsonPrivilege.lockCard) {
			box.getElement("[name='lockCard']").set("checked", "checked");
		}
		if (this.jsonPrivilege.abandonCard) {
			box.getElement("[name='abandonCard']").set("checked", "checked");
		}
		if (this.jsonPrivilege.defaultSelect) {
			box.getElement("[name='defaultSelect']").set("checked", "checked");
		}
		if (this.jsonPrivilege.cvm) {
			box.getElement("[name='cvm']").set("checked", "checked");
		}

		this.modal.open();
	},

	close : function() {
		this.modal.getBox().dispose();
		this.modal.close();
	},

	submit : function() {
		var data = $(this.formId).toQueryString();

		new Request.JSON({
			url : ctx + "/html/commons/?m=buildPrivilege",
			async : false,
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).get();
	},

	submitCallback : function(json) {
		this.modal.getBox().dispose();
		this.close();

		if (json.success) {
			this.applet.refreshPrivilege(json.message.hexPrivilege);
		}
	}
});

Application.Applet.InstallParams = new Class({

	Implements : [ Events, Options ],

	applet : null,
	modal : null,
	formId : null,
	jsonInstallParams : {
		customerParams : "",
		volatileDateSpace : 0,
		nonVolatileDateSpace : 0
	},

	initialize : function(options) {
		this.setOptions(options);
		this.formId = "form" + new Date().valueOf();
	},

	inputInstallParams : function(installParams) {
		// 向服务器发起请求解析安装参数
		new Request.JSON({
			url : ctx + "/html/applet/?m=parseInstallParams",
			data : {
				hexInstallParams : installParams
			},
			async : false,
			onSuccess : function(json) {
				if (json.success) {
					this.jsonInstallParams = json.message;
				}
			}.bind(this)
		}).get();

		// 填写加载参数的弹出窗口
		var template = $("installParamsFormDivTemplate").clone(true, true);
		var html = template.get("html");

		this.modal = new LightFace({
			title : "输入安装参数",
			loadModule : this,
			height : 300,
			width : 500,
			content : html,
			resetOnScroll : false,
			buttons : [ {
				title : "确认",
				color : "blue",
				event : function() {
					this.modal.getBox().getElement('button[type=submit]').click();
				}.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			} ]
		});

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='installParams']").set("id", this.formId).eliminate("title");
		box.getElement("[name='customerParams']").set("value", this.jsonInstallParams.customerParams);
		box.getElement("[name='volatileDateSpace']").set("value", this.jsonInstallParams.volatileDateSpace);
		box.getElement("[name='nonVolatileDateSpace']").set("value", this.jsonInstallParams.nonVolatileDateSpace);

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
			url : ctx + "/html/applet/?m=buildInstallParams",
			data : data,
			onSuccess : this.submitCallback.bind(this)
		}).post();

	},

	submitCallback : function(json) {
		this.close();
		if (json.success) {
			this.applet.refreshInstallParams(json.message.hexInstallParams);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	}
});