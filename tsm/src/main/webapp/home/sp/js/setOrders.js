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
		this.getDownloadOrder();
		this.getDeleteOrder();
		this.getInstallOrder();
	},

	/**
	 * 下载顺序
	 */
	getDownloadOrder : function() {
		new Request.JSON({
			url : ctx + "/html/applicationLoadFile/?m=getAllByDownloadOrder",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getDownloadOrderCallback.bind(this)
		}).get();
	},

	getDownloadOrderCallback : function(json) {
		if (json.success) {
			var showDownloadOrderDiv = $("showDownloadOrderDiv");
			showDownloadOrderDiv.setStyle("display", "");

			var setDownloadOrderDiv = $("setDownloadOrderDiv");
			setDownloadOrderDiv.setStyle("display", "none");

			this.downloadOrder = json.result;
			this.showDownloadOrder();
		}
	},

	showDownloadOrder : function() {
		var target = $("showDownloadOrder");
		target.empty();

		this.downloadOrder.each(function(item, index) {
			var template = $("shwoOrderTemplate").clone();

			var order = template.getElement("[title='次序']");
			order.set("html", index + 1);

			var name = template.getElement("[title='名称']");
			name.set("html", item.name);

			var name = template.getElement("[title='AID']");
			name.set("html", item.aid);

			template.inject(target);
		}.bind(this));
	},

	setDownloadOrders : function() {
		var showDownloadOrderDiv = $("showDownloadOrderDiv");
		showDownloadOrderDiv.setStyle("display", "none");

		var setDownloadOrderDiv = $("setDownloadOrderDiv");
		setDownloadOrderDiv.setStyle("display", "");

		var target = $("setDownloadOrder");
		target.empty();

		this.downloadOrder.each(function(item, index) {
			var template = $("setOrderTemplate").clone();

			template.set("title", "orders");

			var applicationVersionIdInput = template.getElement("[name='applicationVersionId']");
			applicationVersionIdInput.set("value", this.applicationVersionId);

			var idInput = template.getElement("[name='id']");
			idInput.set("value", item.loadFileVersionId).set("name", "loadFileVersionId");

			var span = template.getElement("[title='名称']");
			span.set("html", item.name);

			var span = template.getElement("[title='AID']");
			span.set("html", item.aid);

			var select = template.getElement("[name='order']");
			var option = template.getElement("option");
			this.generateOption(select, option, this.downloadOrder.length, index + 1);

			template.inject(target);
		}.bind(this));
	},

	submitDownloadOrders : function() {
		var isSuccess = true;
		var message = "";
		var downloadOrder = $("setDownloadOrder");
		var orders = downloadOrder.getElements("[title='orders']");
		orders.each(function(order) {
			if (isSuccess) {
				var data = order.toQueryString();
				new Request.JSON({
					async : false,
					url : ctx + "/html/applicationLoadFile/?m=setDownloadOrder",
					data : data,
					onSuccess : function(json) {
						isSuccess = json.success;
						message = json.message;
					}
				}).post();
			}
		}.bind(this));

		if (isSuccess) {// 设置成功，从服务器获取下载顺序
			this.getDownloadOrder();
		} else {
			new LightFace.MessageBox().error(message);
		}
	},

	/**
	 * 获取删除顺序
	 */
	getDeleteOrder : function() {
		new Request.JSON({
			url : ctx + "/html/applicationLoadFile/?m=getAllByDeleteOrder",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getDeleteOrderCallback.bind(this)
		}).get();
	},

	getDeleteOrderCallback : function(json) {
		if (json.success) {
			var setDeleteOrderDiv = $("setDeleteOrderDiv");
			setDeleteOrderDiv.setStyle("display", "none");

			var showDeleteOrderDiv = $("showDeleteOrderDiv");
			showDeleteOrderDiv.setStyle("display", "");

			this.deleteOrder = json.result;
			this.showDeleteOrder();
		}
	},

	showDeleteOrder : function() {
		var setDeleteOrderDiv = $("setDeleteOrderDiv");
		setDeleteOrderDiv.setStyle("display", "none");

		var target = $("showDeleteOrder");
		target.empty();

		this.deleteOrder.each(function(item, index) {
			var template = $("shwoOrderTemplate").clone();

			var order = template.getElement("[title='次序']");
			order.set("html", index + 1);

			var name = template.getElement("[title='名称']");
			name.set("html", item.name);

			var name = template.getElement("[title='AID']");
			name.set("html", item.aid);

			template.inject(target);
		}.bind(this));

		var showDeleteOrderDiv = $("showDeleteOrderDiv");
		showDeleteOrderDiv.setStyle("display", "");
	},

	setDeleteOrders : function() {
		var showDeleteOrderDiv = $("showDeleteOrderDiv");
		showDeleteOrderDiv.setStyle("display", "none");

		var target = $("setDeleteOrder");
		target.empty();

		this.deleteOrder.each(function(item, index) {
			var template = $("setOrderTemplate").clone();
			template.setStyle("display", "");

			template.set("title", "orders");

			var applicationVersionIdInput = template.getElement("[name='applicationVersionId']");
			applicationVersionIdInput.set("value", this.applicationVersionId);

			var idInput = template.getElement("[name='id']");
			idInput.set("value", item.loadFileVersionId).set("name", "loadFileVersionId");

			var span = template.getElement("[title='名称']");
			span.set("html", item.name);

			var span = template.getElement("[title='AID']");
			span.set("html", item.aid);

			var select = template.getElement("[name='order']");
			var option = template.getElement("option");
			this.generateOption(select, option, this.downloadOrder.length, index + 1);

			template.inject(target);
			;
		}.bind(this));

		var setDeleteOrderDiv = $("setDeleteOrderDiv");
		setDeleteOrderDiv.setStyle("display", "");
	},

	submitDeleteOrders : function() {
		var isSuccess = true;
		var message = "";
		var deleteOrder = $("setDeleteOrder");
		var orders = deleteOrder.getElements("[title='orders']");
		orders.each(function(order) {
			if (isSuccess) {
				var data = order.toQueryString();
				new Request.JSON({
					async : false,
					url : ctx + "/html/applicationLoadFile/?m=setDeleteOrder",
					data : data,
					onSuccess : function(json) {
						isSuccess = json.success;
						message = json.message;
					}
				}).post();
			}
		}.bind(this));

		if (isSuccess) {// 设置成功，从服务器获取下载顺序
			this.getDeleteOrder();
		} else {
			new LightFace.MessageBox().error(message);
		}
	},

	/**
	 * 安装顺序
	 */
	getInstallOrder : function() {
		new Request.JSON({
			url : ctx + "/html/applet/?m=getInstallOrder",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getInstallOrderCallback.bind(this)
		}).get();
	},

	getInstallOrderCallback : function(json) {
		if (json.success) {
			var showInstallOrderDiv = $("showInstallOrderDiv");
			showInstallOrderDiv.setStyle("display", "");

			var setInstallOrderDiv = $("setInstallOrderDiv");
			setInstallOrderDiv.setStyle("display", "none");

			this.installOrder = json.result;
			this.showInstallOrder();
		}
	},

	showInstallOrder : function() {
		var target = $("showInstallOrder");
		target.empty();

		this.installOrder.each(function(item, index) {
			var template = $("shwoOrderTemplate").clone();

			var order = template.getElement("[title='次序']");
			order.set("html", index + 1);

			var name = template.getElement("[title='名称']");
			name.set("html", item.name);

			var name = template.getElement("[title='AID']");
			name.set("html", item.aid);

			template.inject(target);
		}.bind(this));
	},

	setInstallOrders : function() {
		var showInstallOrderDiv = $("showInstallOrderDiv");
		showInstallOrderDiv.setStyle("display", "none");

		var setInstallOrderDiv = $("setInstallOrderDiv");
		setInstallOrderDiv.setStyle("display", "");

		var target = $("setInstallOrder");
		target.empty();

		this.installOrder.each(function(item, index) {
			var template = $("setOrderTemplate").clone();

			template.set("title", "orders");

			var applicationVersionIdInput = template.getElement("[name='applicationVersionId']");
			applicationVersionIdInput.set("value", this.applicationVersionId);

			var idInput = template.getElement("[name='id']");
			idInput.set("value", item.id).set("name", "appletId");

			var span = template.getElement("[title='名称']");
			span.set("html", item.name);

			var span = template.getElement("[title='AID']");
			span.set("html", item.aid);

			var select = template.getElement("[name='order']");
			var option = template.getElement("option");
			this.generateOption(select, option, this.installOrder.length, index + 1);

			template.inject(target);
		}.bind(this));
	},

	submitInstallOrders : function() {
		var isSuccess = true;
		var message = "";
		var installOrder = $("setInstallOrder");
		var orders = installOrder.getElements("[title='orders']");
		orders.each(function(order) {
			if (isSuccess) {
				var data = order.toQueryString();
				new Request.JSON({
					async : false,
					url : ctx + "/html/applet/?m=setInstallOrder",
					data : data,
					onSuccess : function(json) {
						isSuccess = json.success;
						message = json.message;
					}
				}).post();
			}
		}.bind(this));

		if (isSuccess) {// 设置成功，从服务器获取下载顺序
			this.getInstallOrder();
		} else {
			new LightFace.MessageBox().error(message);
		}
	},

	/**
	 * 生成设置顺序的html option
	 * 
	 * @param target
	 *            生成的html option所在的节点
	 * @param template
	 *            用于生成html option的模板元素
	 * @param count
	 *            生成html option的数目
	 * @param defaultOrder
	 *            默认的html option取值
	 */
	generateOption : function(target, template, count, defaultOrder) {
		target.empty();
		for ( var i = 1; i <= count; i++) {
			var option = template.clone();
			option.erase("selected").set("value", i).set("html", i);
			if (defaultOrder == i) {
				option.set("selected", "selected");
			}
			option.inject(target);
		}
	},

	/**
	 * 获取当前应用版本已使用的加载文件
	 */
	getLoadFiles : function() {
		new Request.JSON({
			url : ctx + "/html/loadFileVersion/?m=getByApplicationVersion",
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
				this.getLoadModules(loadFileJson.id);
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
			url : ctx + "/html/loadModule/?m=getByCriteria",
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