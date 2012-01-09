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

	importLoadFileVersion : function(loadFileVersionId) {
		var existLoadFileVersions;
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getByCriteria",
			data : {
				search_ALIAS_loadFileL_EQL_id : loadFileVersionId
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
	 * 获取已上传的共享加载文件
	 */
	getSharedLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFile/?m=getSharedLoadFiles",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getSharedLoadFilesCallback.bind(this)
		}).get();
	},

	getSharedLoadFilesCallback : function(json) {
		if (json.success) {
			var template = $("loadFileTemplate");
			var target = $("exclusiveLoadFiles").empty();
			json.result.each(function(item) {
				var row = template.getElement("[id='loadFileTemplateP']").clone(true, true);

				if ($chk(item.aid)) {
					row.getElement("[title='AID']").set("html", item.aid);
				}
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

	/**
	 * 获取当前应用版本已使用的加载文件
	 */
	getLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getBySharedByApplicationVersion",
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
			title : "选择加载文件",
			height : 350,
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
		var box = this.modal.getBox();
		box.dispose();
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
			this.page.getSharedLoadFiles();
		} else {
			new LightFace.MessageBox().error(json.message);
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
		if ($chk(loadFile.comments)) {
			row.getElement("[title='备注']").set("html", loadFile.comments);
		}
		if ($chk(loadFile.loadParams)) {
			row.getElement("[title='加载参数']").set("html", loadFile.loadParams);
		}
		if ($chk(loadFile.fileSize)) {
			row.getElement("[title='文件大小']").set("html", loadFile.fileSize);
		}

		row.getElement("[title='解除引用']").addEvent("click", function(event) {
			event.stop();
			this.modal = new LightFace({
				title : "确认删除",
				appletModal : this.modal,
				height : 50,
				width : 300,
				content : "确认删除引入？",
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
					}
				} ]
			}).open();
		}.bind(this));

		// 显示在页面上
		row.set("id", loadFileDivId).inject(target);
	},

	/**
	 * 弹出输入加载文件信息的界面
	 */
	createNewLoadFile : function() {
		this.tempFileId = (new Date()).valueOf() + "File";

		this.modal = new LightFace({
			title : "上传加载文件",
			height : 350,
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
		box.getElement("id='file'").set("id", this.tempFileId);

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

			this.page.getSharedLoadFiles();
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
			new LightFace.MessageBox().info("上传成功");
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
			height : 350,
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
			height : 350,
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
			new LightFace.MessageBox().info(json.message);
		}
	}
});
