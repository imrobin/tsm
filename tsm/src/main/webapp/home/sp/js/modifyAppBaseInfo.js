Application = {};
var applicationImgFileName = '';
var applicationImgImg = '';
var applicationImgTempDir = '';
var applicationImgTempFileAbsPath = '';
Application.BaseInfo = new Class({
	formId : "appBaseInfoForm",
	sdConstant : null,
	appConstant : null,
	sdList : new Array(5),
	options : {
		url : "",
		params : ""
	},
	initialize : function() {
		this.options.url = $(this.formId).get('action');
	},

	init : function() {
		this.getConstant();
		this.getApplicationDetails();
	},

	getApplicationDetails : function() {
		new Request.JSON({
			url : ctx + "/html/application/?m=getByCriteria",
			data : {
				search_EQL_id : applicationId
			},
			onSuccess : this.getApplicationDetailsCallback.bind(this)
		}).get();
	},

	getApplicationDetailsCallback : function(json) {
		if (json.success) {

			var application = json.result[0];

			if ($chk(application.sd_id)) {
				new Request.JSON({
					url : ctx + "/html/securityDomain/?m=index",
					data : {
						search_EQL_id : application.sd_id
					},
					onSuccess : this.getSdCallback.bind(this)
				}).get();
			}

			var form = $('appBaseInfoForm');
			form.getElement('[name="name"]').set("value", application.name);
			form.getElement('[name="form"]').getElement('option[value=' + application.formOriginal + ']').set('selected', 'selected');
			form.getElement('[name="aid"]').set("value", application.aid);
			form.getElement('[name="description"]').set("html", application.description);
			if ($chk(application.personalTypeOriginal)) {
				form.getElement('[name="personalType"]').getElement('option[value=' + application.personalTypeOriginal + ']').set(
						'selected', 'selected');
			}
			form.getElement('[name="persoCmdTransferSA"]').getElement(
					'option[value=' + application.persoCmdTransferSecureAlgorithm.value + ']').set('selected', 'selected');
			form.getElement('[name="persoCmdSensitiveDataSA"]').getElement(
					'option[value=' + application.persoCmdSensitiveDataSecureAlgorithm.value + ']').set('selected', 'selected');

			if ($chk(application.needSubscribe)) {
				form.getElement('[name="needSubscribe"]').getElement('option[value=' + application.needSubscribeOriginal + ']').set(
						'selected', 'selected');
			}

			if ($chk(application.needSubscribe)) {
				form.getElement('[name="presetChargeCondition"]').getElement(
						'option[value=' + application.presetChargeConditionOriginal + ']').set('selected', 'selected');
			}

			form.getElement('[name="businessPlatformUrl"]').set("value", application.businessPlatformUrl);
			form.getElement('[name="serviceName"]').set("value", application.serviceName);
			if ($chk(application.deleteRuleOriginal)) {
				form.getElement('[name="deleteRule"]').getElement('option[value=' + application.deleteRuleOriginal + ']').set('selected',
						'selected');
			}
			form.getElement('[name="location"]').getElement('option[value="' + application.location + '"]').set("selected", "selected");
			form.getElement('[id="pcIconImg"]').set("src", ctx + "/html/application/?m=getAppPcImg&appId=" + application.id);
			form.getElement('[id="mobileIconImg"]').set("src", ctx + "/html/application/?m=getAppMobileImg&appId=" + application.id);
			// 获取应用截图
			new Request.JSON({
				url : ctx + "/html/application/?m=getImgIdByAppId",
				data : {
					applicationId : application.id
				},
				onSuccess : function(json) {
					if (json.success && json.message != '') {
						var ids = json.message.split(",");
						for ( var i = 0; i < ids.length; i++) {
							$('applicationImgDisplay').set('style', 'width:500');
							new Element('span').set(
									'html',
									'<img name="appImgs" src="' + ctx + "/html/application/?m=getAppImg&appImgId=" + ids[i]
											+ '" width="95" height="140"/>&nbsp').inject($('applicationImgTd'));
						}
					}
				}
			}).get();
			this.getFristType(application.childType_id);

			form.getElement('[id="fristType"]').addEvent("change", function(event) {
				this.getSecondType(-1);
			}.bind(this));

			if (this.appConstant.STATUS_INIT != application.statusOriginal) {
				form.getElement('[name="name"]').set("disabled", "disabled");
				form.getElement('[name="aid"]').set("disabled", "disabled");
				form.getElement('[name="sdModel"]').set("disabled", "disabled");
				form.getElement('[name="sdId"]').set("disabled", "disabled");
				form.getElement('[name="deleteRule"]').set("disabled", "disabled");
				form.getElement('[name="personalType"]').set("disabled", "disabled");
			}

		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	getSdCallback : function(json) {
		if (json.success) {
			var sd = json.result[0];
			$('appBaseInfoForm').getElement('[name="sdModel"]').getElement('option[value="' + sd.modelOriginal + '"]').set("selected",
					"selected");
			this.confSd();
			$('appBaseInfoForm').getElement('[name="sdId"]').getElement('option[value="' + sd.id + '"]').set("selected", "selected");
		} else {
			new LightFace.MessageBox().error2('获取应用所属安全域信息失败，' + json.message);
		}
	},

	getConstant : function() {
		new Request.JSON({
			async : false,
			url : ctx + "/html/securityDomain/?m=exportConstant",
			onSuccess : function(json) {
				if (json.success) {
					this.sdConstant = json.message;
				}
			}.bind(this)
		}).get();
		this.confSd();

		new Request.JSON({
			async : false,
			url : ctx + "/html/application/?m=exportConstant",
			onSuccess : function(json) {
				if (json.success) {
					this.appConstant = json.message;
				}
			}.bind(this)
		}).get();
	},

	confSd : function() {
		var sdModel = $("sdModel").get("value");
		if (this.sdConstant.MODEL_ISD == sdModel) {
			var target = $("sdId");
			target.empty();

			var template = $("sdSelectTemplate");
			var row = template.getElement("[title='disableTemplate']").clone();
			row.erase("title").inject(target);
		} else {
			this.getSdList(sdModel);
		}
	},

	getSdList : function(sdModel) {
		if ($chk(this.sdList[sdModel])) {
			this.showSdList(this.sdList[sdModel]);
		} else {
			var data = {
				search_EQI_status : this.sdConstant.STATUS_PUBLISHED,
				search_EQI_model : sdModel,
				page_pageSize : 100000
			};
			if ((this.sdConstant.MODEL_DAP == sdModel) || (this.sdConstant.MODEL_TOKEN == sdModel)) {
				data.search_ALIAS_spL_EQL_id = spId;
			}
			new Request.JSON({
				async : false,
				url : ctx + "/html/securityDomain/?m=index",
				data : data,
				onSuccess : function(json) {
					if (json.success) {
						if (0 == json.totalCount) {
							new LightFace.MessageBox().error('没有指定模式的安全域');

							var sdOptions = $("sdModel").getElements('option');
							sdOptions.each(function(item) {
								if (this.sdConstant.MODEL_ISD == item.get("value")) {
									item.set("selected", "selected");
								} else {
									item.set("selected", "");
								}
								this.confSd();
							}.bind(this));
						} else {
							this.sdList[sdModel] = json.result;
							this.showSdList(this.sdList[sdModel]);
						}
					}
				}.bind(this)
			}).get();
		}
	},

	showSdList : function(sdList) {
		var target = $("sdId");
		target.empty();

		var template = $("sdSelectTemplate");
		sdList.each(function(item) {
			var row = template.getElement("[title='enableTemplate']").clone();
			row.set("value", item.id).set("html", item.sdName + "(" + item.aid + ")");
			row.erase("title").inject(target);
		});
	},

	submit : function() {
		if (true) {
			var form = $(this.formId);
			var params = form.toQueryString();

			new Request.JSON({
				url : ctx + "/html/application/?m=modifyApplicationBaseInfo&applicationId=" + applicationId,
				onSuccess : this.submitCallback.bind(this),
				data : params
			}).post();
		}
	},

	submitCallback : function(result) {
		if (result.success) {
			new LightFace.MessageBox().info("操作成功");
			self.location = ctx + "/home/sp/showApplicationDetails.jsp?applicationId=" + result.message;
		} else {
			new LightFace.MessageBox().error(result.message);
		}
	},

	uploadPcIcon : function() {
		var uploader = new JIM.AjaxUploadFile({
			url : ctx + "/html/commons/?m=upload",
			fileElementId : "pcIconFile",
			onSuccess : function(json) {
				this.uploadCallbak(json, "pcIcon");
			}.bind(this)
		});

		uploader.upload();
	},

	uploadMobileIcon : function() {
		var uploader = new JIM.AjaxUploadFile({
			url : ctx + "/html/commons/?m=upload",
			fileElementId : "mobileIconFile",
			onSuccess : function(json) {
				this.uploadCallbak(json, "mobileIcon");
			}.bind(this)
		});

		uploader.upload();
	},

	uploadCallbak : function(result, type) {
		if (result.success) {

			var dd = $(type + "Display");
			dd.setStyle("display", "");
			dd.getElement('[title="' + type + 'Img"]').set("src", ctx + "/" + decodeURI(result.message.tempRalFilePath));
			dd.getElement('[id="' + type + 'TempDir"]').set("value", decodeURI(result.message.tempDir));
			dd.getElement('[id="' + type + 'TempFileAbsPath"]').set("value", decodeURI(result.message.tempFileAbsPath));
		} else {
			new LightFace.MessageBox().error2("上传失败，" + result.message);
		}
	},

	getFristType : function(appType) {
		new Request.JSON({
			url : ctx + "/html/applicationType/?m=getByCriteria",
			data : {
				search_EQI_typeLevel : 1,
				search_ALIAS_applicationTypesI_NOTNULLI_id : 1,
				page_pageSize : 100000
			},
			onSuccess : function(json) {
				this.showFristType(json, appType);
			}.bind(this)
		}).get();
	},

	showFristType : function(json, appType) {
		var selectedTypeId = null;

		new Request.JSON({
			async : false,
			url : ctx + "/html/applicationType/?m=getByCriteria",
			data : {
				search_EQL_id : appType
			},
			onSuccess : function(json) {
				if (json.success) {
					selectedTypeId = json.result[0].parentType_id;
				}
			}
		}).get();

		var selector = $("fristType");
		var ids = [];
		Array.each(json.result, function(type) {
			if (ids.indexOf(type.id) == -1) {
				var option = new Element("option").set("value", type.id).set("html", type.name);

				if (selectedTypeId == type.id) {
					option.set("selected", "selected");
				}

				option.inject(selector);
			}
			ids.push(type.id);
		});

		this.getSecondType(appType);
	},

	getSecondType : function(appType) {
		new Request.JSON({
			url : ctx + "/html/applicationType/?m=getByCriteria",
			data : {
				search_ALIAS_parentTypeL_EQL_id : $("fristType").get("value")
			},
			onSuccess : function(json) {
				this.showSecondType(json, appType);
			}.bind(this)
		}).get();
	},

	showSecondType : function(json, appType) {
		var selector = $("secondType");
		selector.set("disabled", "");
		selector.empty();
		Array.each(json.result, function(type) {
			var option = new Element("option").set("value", type.id).set("html", type.name);

			if (appType == type.id) {
				option.set("selected", "selected");
			}

			option.inject(selector);
		});
	},

	uploadIconCallback : function(result, type) {
		if (result.success) {
			var dd = null;
			dd = $(type + 'Upload');
			dd.getElement('[name="' + type + 'FileName"]').set("value", decodeURI(result.message.fileName));

			dd = $(type + 'Display');
			dd.setStyle("display", "");
			dd.getElement('[id="' + type + 'Img"]').set("src", ctx + "/" + decodeURI(result.message.tempRalFilePath));
			dd.getElement('[name="' + type + 'TempDir"]').set("value", decodeURI(result.message.tempDir));
			dd.getElement('[name="' + type + 'TempFileAbsPath"]').set("value", decodeURI(result.message.tempFileAbsPath));
		} else {
			new LightFace.MessageBox().error2("上传失败，" + result.message);
		}
	},

	pcIconOversize : function() {
		new LightFace.MessageBox().error('PC版图标不能超过2MB');
	},

	mobileIconOversize : function() {
		new LightFace.MessageBox().error('手机版图标不能超过15KB');
	},
	applicationImgOversize : function() {
		new LightFace.MessageBox().error('应用截图不能超过2MB');
	},
	uploadPcIconCallback : function(file, result, responseReceived) {
		result = JSON.decode(result);
		this.uploadIconCallback(result, 'pcIcon');
	},
	uploadAppliationImageCallback : function(file, result, responseReceived) {
		result = JSON.decode(result);
		if (result.success) {
			var dd = null;
			dd = $('applicationImgUpload');
			applicationImgFileName += decodeURI(result.message.fileName) + '  ';
			dd.getElement('[name="applicationImgFileName"]').set("value", applicationImgFileName);

			dd = $('applicationImgDisplay');
			dd.setStyle("display", "");
			// new Element('img').set('src',ctx + "/" +
			// decodeURI(result.message.tempRalFilePath)).set('width','190px')
			// .set('height','280px').inject($('applicationImgTd'));
			new Element('span').set('html',
					'<img src="' + ctx + "/" + decodeURI(result.message.tempRalFilePath) + '" width="95" height="140"/>&nbsp').inject(
					$('applicationImgTd'));
			if (applicationImgTempDir != '') {
				applicationImgTempDir += ',' + decodeURI(result.message.tempDir);
			} else {
				applicationImgTempDir += decodeURI(result.message.tempDir);
			}
			dd.getElement('[name="applicationImgTempDir"]').set("value", applicationImgTempDir);

			if (applicationImgTempFileAbsPath != '') {
				applicationImgTempFileAbsPath += ',' + decodeURI(result.message.tempFileAbsPath);
			} else {
				e = $$("img[name='appImgs']");
				for ( var i = 0; i < e.length; i++) {
					// alert(1);
					e[i].destroy();
				}
				applicationImgTempFileAbsPath += decodeURI(result.message.tempFileAbsPath);
			}
			dd.getElement('[name="applicationImgTempFileAbsPath"]').set("value", applicationImgTempFileAbsPath);
		} else {
			new LightFace.MessageBox().error2("上传失败，" + result.message);
		}
	},
	uploadMobileIconCallback : function(ile, result, responseReceived) {
		result = JSON.decode(result);
		this.uploadIconCallback(result, 'mobileIcon');
	}
});