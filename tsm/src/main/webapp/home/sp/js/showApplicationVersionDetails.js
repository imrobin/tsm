Application = {};
Application.Page = new Class({

	Implements : [ Events, Options ],

	applicationVersionId : -1,
	loadFiles : new Array(),
	applets : new Array(),
	downloadOrder : null,
	deleteOrder : null,
	applicationVersion : null,
	application : null,

	options : {
		applicationVersionId : -1
	},

	initialize : function(options) {
		this.setOptions(options);
		this.applicationVersionId = this.options.applicationVersionId;
		this.ramdon = new Date().valueOf();
	},

	init : function() {
		this.getApplicationVersionDetails();
		this.getLoadFiles();
		this.getCilents();
		this.getDownloadOrder();
		this.getDeleteOrder();
		this.getInstallOrder();
		this.getTestFileList();
		this.getTestReport();
	},
	getTestFileList : function() {
		var url = ctx + "/html/testfile/?m=index&search_EQL_appVer.id=" + this.applicationVersionId;
		var grid = new HtmlTable({
			properties : {
				border : 0,
				cellspacing : 0,
				style : 'width: 100%'
			},
			headers : [ '文件名', '文件说明', '上传时间', '序列号', '操作' ]
		});
		grid.inject($('grid'));

		var paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {
				el : 'nextpage',
				showNumber : true,
				showText : false
			},
			onAfterLoad : function(data) {
				grid.empty();
				data.result.forEach(function(testFile, index) {
					var operation = '';
					var detail = '<a class="b"  style="float : none;"  href="' + ctx + '/html/testfile/?m=downFile&tfId=' + testFile.id
							+ '"><span>下载</span></a>';
					operation = detail;
					grid.push([ {
						content : testFile.originalName,
						properties : {
							align : "center"
						}
					}, {
						content : testFile.comments,
						properties : {
							align : "center"
						}
					}, {
						content : testFile.uploadDate,
						properties : {
							align : "center"
						}
					}, {
						content : testFile.seqNum,
						properties : {
							align : "center"
						}
					}, {
						content : operation,
						properties : {
							align : "center",
							width : "90px"
						}
					} ]);
				});
			}
		});
		paging.load();
	},
	getTestReport : function() {
		var url = ctx + "/html/appverTest/?m=index&search_EQL_appVer.id=" + this.applicationVersionId;
		var grid = new HtmlTable({
			properties : {
				border : 0,
				cellspacing : 0,
				style : 'width: 100%'
			},
			headers : [ '测试日期', '测试手机号', 'NFC终端型号', 'SE芯片类型', '卡批次', '测试结果', '说明', '提交者' ]
		});
		grid.inject($('testReport'));

		var paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {
				el : $('trNextPage'),
				showNumber : true,
				showText : false
			},
			onAfterLoad : function(data) {
				grid.empty();
				data.result.forEach(function(testReport, index) {
					grid.push([ {
						content : testReport.testDate,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.mobileNo,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.modelType,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.seType,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.cardBaseInfo_name,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.result,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.resultComment,
						properties : {
							align : "center"
						}
					}, {
						content : testReport.author,
						properties : {
							align : "center"
						}
					} ]);
				});
				if (data.result.length == 0) {
					$('trInfo').set('html', '<span>无数据记录</span>')
				} else {
					$('trInfo').set('html', '<span></span>')
				}
			}
		});
		paging.load();
	},
	getApplicationVersionDetails : function() {
		new Request.JSON({
			url : ctx + "/html/appVer/?m=index",
			data : {
				async : false,
				search_EQL_id : this.applicationVersionId
			},
			onSuccess : this.getApplicationVersionDetailsCallback.bind(this)
		}).get();
	},

	getApplicationVersionDetailsCallback : function(json) {
		if (json.success) {
			this.applicationVersion = json.result[0];
			this.getApplicationDetails();
		} else {
			alert("获取应用详情失败" + json.message);
		}
	},

	getApplicationDetails : function() {
		new Request.JSON({
			url : ctx + "/html/application/?m=getByCriteria",
			data : {
				async : false,
				search_EQL_id : this.applicationVersion.application_id
			},
			onSuccess : this.getApplicationDetailsCallback.bind(this)
		}).get();
	},

	getApplicationDetailsCallback : function(json) {
		if (json.success) {
			this.application = json.result[0];
			this.showApplicationDetails();
		} else {
			alert("获取应用详情失败" + json.message);
		}
	},

	showApplicationDetails : function() {
		var target = $("applicationDetails").empty();
		var template = $("applicationDetailsTemplate").clone();

		if ($chk(this.application.sd_id)) {
			new Request.JSON({
				url : ctx + "/html/securityDomain/?m=index",
				data : {
					search_EQL_id : this.application.sd_id
				},
				onSuccess : function(json) {
					if (json.success) {
						var sd = json.result[0];
						template.getElement('[title="所属安全域"]').set("html", sd.sdName + '(' + sd.aid + ')');
					}
				}
			}).get();
		}
		if ($chk(this.application.childType_id)) {
			new Request.JSON({
				url : ctx + "/html/applicationType/?m=getTypeById",
				data : {
					id : this.application.childType_id
				},
				onSuccess : function(json) {
					if (json.success) {
						template.getElement('[title="业务类型"]').set("html", json.message);
					}
				}
			}).get();
		}

		template.getElement("[title='应用名称']").set("html", this.application.name);
		template.getElement('[title="应用类型"]').set("html", this.application.form);
		template.getElement("[title='AID']").set("html", this.application.aid);
		template.getElement("[title='应用描述']").set("html", this.application.description);
		template.getElement("[title='业务类型']").set("html", this.application.childType_name);

		template.getElement("[title='个人化类型']").set("html", this.application.personalType);
		template.getElement("[title='个人化指令传输加密算法']").set("html", this.application.persoCmdTransferSecureAlgorithm.name);
		template.getElement("[title='个人化指令敏感数据加密算法']").set("html", this.application.persoCmdSensitiveDataSecureAlgorithm.name);

		template.getElement('[title="是否需要订购"]').set("html", this.application.needSubscribe);
		template.getElement("[title='所属安全域模式']").set("html", this.application.sdModel);
		template.getElement('[title="预置时收费条件"]').set("html", this.application.presetChargeCondition);
		template.getElement("[title='URL']").set("html", this.application.businessPlatformUrl);
		template.getElement('[title="业务平台服务名"]').set("html", this.application.serviceName);
		template.getElement("[title='删除规则']").set("html", this.application.deleteRule);
		template.getElement("[title='所在地']").set("html", this.application.location);
		template.getElement("[title='PC版图标']").set("src", ctx + "/html/application/?m=getAppPcImg&appId=" + this.application.id);
		template.getElement("[title='手机版图标']").set("src", ctx + "/html/application/?m=getAppMobileImg&appId=" + this.application.id);

		// 获取应用截图
		new Request.JSON({
			url : ctx + "/html/application/?m=getImgIdByAppId",
			data : {
				applicationId : this.application.id
			},
			onSuccess : function(json) {
				if (json.success && json.message != '') {
					var ids = json.message.split(",");
					for ( var i = 0; i < ids.length; i++) {
						new Element('span').set(
								'html',
								'<img src="' + ctx + "/html/application/?m=getAppImg&appImgId=" + ids[i]
										+ '" width="95" height="140"/>&nbsp').inject(template.getElement("[title='应用截图']"));
					}
				}
			}
		}).get();
		template.getElement("[title='应用版本']").set("html", this.applicationVersion.versionNo);
		template.getElement("[title='内存空间']").set("html", this.applicationVersion.volatileSpace);
		template.getElement("[title='存储空间']").set("html", this.applicationVersion.nonVolatileSpace);
		template.getElement("[title='版本状态']").set("html", this.applicationVersion.status);

		template.inject(target);
	},

	/**
	 * 下载顺序
	 */
	getDownloadOrder : function() {
		new Request.JSON({
			url : ctx + "/html/applicationLoadFile/?m=getDownloadOrder",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getDownloadOrderCallback.bind(this)
		}).get();
	},

	getDownloadOrderCallback : function(json) {
		if (json.success) {
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

	/**
	 * 获取删除顺序
	 */
	getDeleteOrder : function() {
		new Request.JSON({
			url : ctx + "/html/applicationLoadFile/?m=getDeleteOrder",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getDeleteOrderCallback.bind(this)
		}).get();
	},

	getDeleteOrderCallback : function(json) {
		if (json.success) {
			this.deleteOrder = json.result;
			this.showDeleteOrder();
		}
	},

	showDeleteOrder : function() {
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
		}.bind(this));

		var setDeleteOrderDiv = $("setDeleteOrderDiv");
		setDeleteOrderDiv.setStyle("display", "");
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
			alert(json.message);
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
			alert(json.message);
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

	getCilents : function() {
		new Request.JSON({
			url : ctx + "/html/applicationClient/?m=getByApplicationVersion",
			data : {
				applicationVersionId : this.applicationVersionId
			},
			onSuccess : this.getCilentsCallback.bind(this)
		}).get();
	},

	getCilentsCallback : function(json) {
		if (json.success) {
			json.result.each(function(item) {
				var client = new Application.Client();
				client.showCilent(item);
			});
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

Application.Client = new Class({
	Implements : [ Events, Options ],

	modal : null,
	formId : null,
	page : null,
	formId : null,
	tempFileId : null,
	multiSelect : null,
	applicationVersionId : -1,

	initialize : function(options) {
		this.setOptions(options);
		this.page = this.options.page;
		this.applicationVersionId = this.options.applicationVersionId;
		this.tempFileId = "file" + new Date().valueOf();
		this.formId = "form" + new Date().valueOf();
	},

	uploadClient : function() {
		var template = $("clientFormTemlate");
		var html = template.get("html");

		this.modal = new LightFace({
			title : "上传客户端",
			height : 500,
			width : 800,
			content : html,
			buttons : [ {
				title : "确认",
				event : this.submit.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			}, ]
		});

		var box = this.modal.getBox();
		box.getElement("[title='client']").set("id", this.formId).erase("title");
		box.getElement("[name='applicationVersionId']").set("value", this.applicationVersionId);
		box.getElement("input[type='file']").set("id", this.tempFileId);
		box.getElement("[title='上传']").addEvent("click", function(event) {
			event.stop();
			this.upload();
		}.bind(this));

		var sysTypeElement = box.getElement("[title='sysType']");
		this.multiSelect = new JIM.UI.MultipleSelect(sysTypeElement, {
			letfTitle : "可选择",
			rightTitle : "已选中",
			buttons : {
				up : false,
				down : false,
				top : false,
				bottom : false
			},
			store : {
				remote : true,
				leftUrl : ctx + "/html/sysParams/?m=getParamsByTypeToMap&type=platform"
			}

		});

		this.modal.open();
	},

	close : function() {
		this.modal.getBox().dispose();
		this.modal.close();
	},

	submit : function() {
		var sysRequirment = this.multiSelect.getSelectedOption();
		var data = $(this.formId).toQueryString();
		data = data + "&sysRequirment=" + sysRequirment;

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
			alert(json.message);
		}
	},

	showCilent : function(client) {
		var target = $("client");

		var template = $("clientInfoTemplate").clone();

		template.getElement("[title='名称']").set("html", client.name);
		template.getElement("[title='版本']").set("html", client.version);
		template.getElement("[title='开发版本']").set("html", client.versionCode);
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
					div.inject(clientClassName, 'bottom');
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
		template.getElement("[title='系统需求']").set("html", client.sysRequirment);
		template.getElement("[title='下载地址']").set("html", ctx + "/" + client.fileUrl);
		template.getElement("[title='下载地址']").set("href", ctx + "/" + client.fileUrl);
		template.getElement("[title='文件大小']").set("html", client.size);

		template.inject(target);
	},

	/**
	 * 上传CAP文件
	 */
	upload : function() {
		var uploader = new JIM.AjaxUploadFile({
			url : ctx + "/html/commons/?m=upload",
			fileElementId : this.tempFileId,
			onSuccess : this.uploadCallback.bind(this)
		});

		uploader.upload();
	},

	uploadCallback : function(json) {
		if (json.success) {
			alert("上传成功");
			var box = this.modal.getBox();
			box.getElement("[name='tempFileAbsPath']").set("value", json.message.tempFileAbsPath);
			box.getElement("[name='tempDir']").set("value", json.message.tempDir);
		} else {
			alert("失败");
		}
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
			buttons : [ {
				title : "确认",
				event : this.submit.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			}, ]
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
			// this.page.getLoadModules(json.message.id);
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
		icon.addEvent("click", function(event) {
			event.stop();
			this.spread(id);
		}.bind(this));

	},

	spread : function(id) {
		var loadFileDivId = this.page.getLoadFileVersionDivId(id);

		$(loadFileDivId + "_loadFileDetailsAndModules").setStyle("display", "");

		var icon = $(loadFileDivId + "_icon");
		icon.set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hidden(id);
		}.bind(this));

	},

	hiddenDetails : function(id) {
		var detailsDivId = this.page.getLoadFileVersionDetailsDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", spreadIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.spreadDetails(id);
		}.bind(this));

	},

	spreadDetails : function(id) {
		var detailsDivId = this.page.getLoadFileVersionDetailsDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hiddenDetails(id);
		}.bind(this));

	},

	hiddenModules : function(id) {
		var detailsDivId = this.page.getLoadModulesDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", spreadIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.spreadModules(id);
		}.bind(this));

	},

	spreadModules : function(id) {
		var detailsDivId = this.page.getLoadModulesDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hiddenModules(id);
		}.bind(this));

	},

	/**
	 * 将以添加的加载文件显示在页面上
	 */
	showLoadFile : function(loadFile) {
		var template = $("loadFileTemplateDiv");
		var target = $("uploadedCapsInfo").getElement("tbody");

		var info = template.getElement("[id='loadFileTemplateP']").clone(true, true);// 用于显示加载文件基本信息
		var loadFileDivId = this.page.getLoadFileVersionDivId(loadFile.id);// 封装基本信息和模块的ID
		info.set("id", +"_info");

		// 图标
		var icon = info.getElement("[title='icon']");
		icon.erase('title').set("id", loadFileDivId + "_icon").set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hidden(loadFile.id);
		}.bind(this));

		// 填写加载文件信息
		info.getElement("[title='ID']").set("html", loadFile.id);
		info.getElement("[title='名称']").set("html", loadFile.name);
		info.getElement("[title='AID']").set("html", loadFile.aid);

		var loadFileDetailsAndModules = template.getElement("[id='loadFileDetailsAndModules']").clone(true, true).set("id",
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
		detailsIcon.addEvent("click", function(event) {
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
		modulesIcon.addEvent("click", function(event) {
			event.stop();
			this.hiddenModules(loadFile.id);
		}.bind(this));

		// 显示在页面上
		var loadFileDiv = new Element("div").set("id", loadFileDivId).grab(info).grab(loadFileDetailsAndModules); // 封装基本信息和模块
		loadFileDiv.inject(target);
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
			loadModule : this,
			height : 300,
			width : 500,
			content : html,
			buttons : [ {
				title : "确认",
				event : this.submit.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			}, ]
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
			alert(json.message);
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

	hidden : function(id) {
		var loadModuleDivId = this.page.getLoadModuleDivId(id);
		$(loadModuleDivId + "_loadModuleDetailsAndApplets").setStyle("display", "none");

		var icon = $(loadModuleDivId + "_icon");
		icon.set("src", spreadIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.spread(id);
		}.bind(this));
	},

	spread : function(id) {
		var loadModuleDivId = this.page.getLoadModuleDivId(id);
		$(loadModuleDivId + "_loadModuleDetailsAndApplets").setStyle("display", "");

		var icon = $(loadModuleDivId + "_icon");
		icon.set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hidden(id);
		}.bind(this));
	},

	hiddenDetails : function(id) {
		var detailsDivId = this.page.getLoadModuleDetailsDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", spreadIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.spreadDetails(id);
		}.bind(this));

	},

	spreadDetails : function(id) {
		var detailsDivId = this.page.getLoadModuleDetailsDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon1");
		icon.set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hiddenDetails(id);
		}.bind(this));

	},

	hiddenApplets : function(id) {
		var detailsDivId = this.page.getAppletsDivId(id);

		$(detailsDivId).setStyle("display", "none");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", spreadIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.spreadApplets(id);
		}.bind(this));

	},

	spreadApplets : function(id) {
		var detailsDivId = this.page.getAppletsDivId(id);

		$(detailsDivId).setStyle("display", "");

		var icon = $(detailsDivId + "_icon2");
		icon.set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hiddenApplets(id);
		}.bind(this));

	},

	showLoadModule : function(loadModule) {
		var template = $("loadModuleTemplateDiv");
		var loadFileDivId = this.page.getLoadFileVersionDivId(loadModule.loadFileId);// 用于封装加载文件基本信息和所属模块的ID
		var loadFile = $(loadFileDivId);
		var loadModulesDivId = this.page.getLoadModulesDivId(loadModule.loadFileId);// 用于显示该加载文件所属模块的ID
		var target = loadFile.getElement("[id='" + loadModulesDivId + "']");

		var loadModuleDivId = this.page.getLoadModuleDivId(loadModule.id);// 封装模块基本信息和实例的ID

		var info = template.getElement("[id='loadModuleTemplateP']").clone(true, true);
		info.set("id", loadModuleDivId + "_info");

		// 图标
		var icon = info.getElement("[title='icon']");
		icon.erase('title').set("id", loadModuleDivId + "_icon").set("src", hiddenIcon);
		icon.addEvent("click", function(event) {
			event.stop();
			this.hidden(loadModule.id);
		}.bind(this));

		// 填写数据
		info.getElement("[title='ID']").set("html", loadModule.id);
		info.getElement("[title='名称']").set("html", loadModule.name);
		info.getElement("[title='AID']").set("html", loadModule.aid);

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
		detailsIcon.addEvent("click", function(event) {
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
		appletsIcon.addEvent("click", function(event) {
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

	hidden : function(id) {
		var appletDivId = this.page.getAppletDivId(id);
		var appletDiv = $(appletDivId);

		appletDiv.getElement("[id='appletDetails']").setStyle("display", "none");

		var icon = $(appletDivId + "_icon");
		icon.set("src", spreadIcon);
		icon.addEvent("click", function(event) {
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
		icon.addEvent("click", function(event) {
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
		icon.addEvent("click", function(event) {
			event.stop();
			this.hidden(applet.id);
		}.bind(this));

		// 填写数据
		info.getElement("[title='ID']").set("html", applet.id);
		info.getElement("[title='名称']").set("html", applet.name);
		info.getElement("[title='AID']").set("html", applet.aid);

		var details = template.getElement("[id='appletDetails']").clone(true, true).set("id", "appletDetails");
		details.getElement("[title='权限']").set("html", applet.privilege);
		details.getElement("[title='安装参数']").set("html", applet.installParams);

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
			url : ctx + "/html/applet/?m=parseInstallParams",
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
			appletModal : this.modal,
			height : 300,
			width : 500,
			content : html,
			buttons : [ {
				title : "确认",
				event : this.submit.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			}, ]
		});

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='privilege']").set("id", this.formId).eliminate("privilege");
		if (this.applet.jsonPrivilege.lockCard) {
			box.getElement("[name='lockCard']").set("checked", "checked");
		}
		if (this.applet.jsonPrivilege.abandonCard) {
			box.getElement("[name='abandonCard']").set("checked", "checked");
		}
		if (this.applet.jsonPrivilege.defaultSelect) {
			box.getElement("[name='defaultSelect']").set("checked", "checked");
		}
		if (this.applet.jsonPrivilege.cvm) {
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
			loadModule : this,
			height : 300,
			width : 500,
			content : html,
			buttons : [ {
				title : "确认",
				event : this.submit.bind(this)
			}, {
				title : "取消",
				event : this.close.bind(this)
			}, ]
		});

		// 填写数据
		var box = this.modal.getBox();
		box.getElement("[title='installParams']").set("id", this.formId).eliminate("title");
		box.getElement("[name='customerParams']").set("value", this.jsonInstallParams.customerParams);
		box.getElement("[name='volatileDateSpace']").set("value", this.jsonInstallParams.volatileDateSpace);
		box.getElement("[name='nonVolatileDateSpace']").set("value", this.jsonInstallParams.nonVolatileDateSpace);

		this.modal.open();
	},

	close : function() {
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
			alert(json.message);
		}
	}

});