Application = {};
var applicationImgFileName= '';
var applicationImgImg= '';
var applicationImgTempDir= '';
var applicationImgTempFileAbsPath= '';
Application.BaseInfo = new Class({
	formId : "appBaseInfoForm",
	sdConstant : null,
	sdList : new Array(5),
	options : {
		url : "",
		params : ""
	},
	initialize : function() {
		this.options.url = $(this.formId).get('action');
	},

	init : function() {
		this.getFristType();
		this.getConstant();
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
	},

	confSd : function() {
		var sdModel = $("sdModel").get("value");
		if (this.sdConstant.MODEL_ISD == sdModel) {
			var target = $("sdId");
			target.set("disabled", "disabled").empty();

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
		target.set("disabled", "").empty();

		var template = $("sdSelectTemplate");
		sdList.each(function(item) {
			var row = template.getElement("[title='enableTemplate']").clone();
			row.set("value", item.id).set("html", item.sdName + "(" + item.aid + ")");
			row.erase("title").inject(target);
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
			applicationImgFileName += decodeURI(result.message.fileName)+'  ';
			dd.getElement('[name="applicationImgFileName"]').set("value", applicationImgFileName);
			
			dd = $('applicationImgDisplay');
			dd.setStyle("display", "");
//			new Element('img').set('src',ctx + "/" + decodeURI(result.message.tempRalFilePath)).set('width','190px')
//			.set('height','280px').inject($('applicationImgTd'));
			new Element('span').set('html','<img src="'+ ctx + "/" + decodeURI(result.message.tempRalFilePath)
			+ '" width="95" height="140"/>&nbsp').inject($('applicationImgTd'));
			if (applicationImgTempDir != ''){
				applicationImgTempDir += ','+decodeURI(result.message.tempDir);
			} else {
				applicationImgTempDir += decodeURI(result.message.tempDir);
			}			
			dd.getElement('[name="applicationImgTempDir"]').set("value", applicationImgTempDir);
			
			if (applicationImgTempFileAbsPath != ''){
				applicationImgTempFileAbsPath += ','+decodeURI(result.message.tempFileAbsPath);
			} else {
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
	},
	getFristType : function() {
		new Request.JSON({// 不知道为啥会有得到重复数据的情况发生
			url : ctx + "/html/applicationType/?m=getByCriteria",
			data : {
				search_EQI_typeLevel : 1,
				search_ALIAS_applicationTypesI_NOTNULLI_id : 1,
				page_pageSize : 100000
			},
			onSuccess : this.showFristType.bind(this)
		}).get();
	},
	textCounter : function(field) {
		if (field.value.length > 80)
			field.value = field.value.substring(0, 80);
	},
	showFristType : function(json) {
		var selector = $("fristType");
		var ids = [];
		Array.each(json.result, function(type) {
			if (ids.indexOf(type.id) == -1) {
				new Element("option").set("value", type.id).set("html", type.name).inject(selector);
			}
			ids.push(type.id);
		});

		this.getSecondType();
	},

	getSecondType : function() {
		new Request.JSON({
			url : ctx + "/html/applicationType/?m=getByCriteria",
			data : {
				search_EQI_typeLevel : 2,
				search_ALIAS_parentTypeL_EQL_id : $("fristType").get("value")
			},
			onSuccess : this.showSecondType.bind(this)
		}).get();
	},

	showSecondType : function(json) {
		var selector = $("secondType");
		selector.set("disabled", "");
		selector.empty();
		Array.each(json.result, function(type) {
			new Element("option").set("value", type.id).set("html", type.name).inject(selector);
		});

	}
});