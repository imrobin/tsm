var SecurityDomain = SecurityDomain ? SecurityDomain : {};
var formCheck;
SecurityDomain.Apply = new Class({
	Implements : [ Events, Options ],
	modal : null,
	formId : null,
	form : null,
	options : {},
	initialize : function(options) {
		this.setOptions(options);
		this.formId = "form" + new Date().valueOf();
		this.modal = new LightFace({
			title : "安全域参数",
			width : 480,
			content : $('installParamsDiv').get("html"),
			resetOnScroll : false,
			buttons : [ {
				title : '保 存',
				event : function() {
					this.form.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					var div = document.getElement('div[class=fc-tbx]');
					if ($chk(div)) {
						div.dispose();
					}
					var div = $('container').getSiblings('div,table');
					if ($chk(div)) {
						$each(div, function(e, index) {
							e.dispose();
						});
					}
					this.close();
				}
			} ]
		});
		this.addValidate();
	},
	addValidate : function() {
		var box = this.modal.getBox();
		this.form = box.getElement("[title='installParamsClient']");
		box.getElement("[name='maxFailCount']").set('value', 255);
		box.getElement("[name='keyVersion']").set('value', 115);
		box.getElement("[name='maxKeyNumber']").set('value', 16);
		formCheck = new FormCheck(this.form, {
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
	createInstallParams : function(installParams) {
		if (installParams != "") {
			new Request.JSON({
				url : ctx + "/html/securityDomain/?m=parseInstallParams&installParams=" + installParams,
				onSuccess : this.setParamsValues.bind(this)
			}).post();
		}
		var box = this.modal.getBox();
		box.getElement("[title='installParamsClient']").set("id", this.formId).eliminate("title");
		var token = box.getElement("[title='spacePatten']");
		var mnvp = box.getElement('[name="managedNoneVolatileSpace"]');
		var mvp = box.getElement('[name="managedVolatileSpace"]');
		token.addEvent('click', function(event) {
			if (token.get('checked')) {
				mnvp.erase('disabled');
				mvp.erase('disabled');
			} else {
				mnvp.set('disabled', 'disabled');
				mvp.set('disabled', 'disabled');
				mnvp.set('value', '');
				mvp.set('value', '');
				formCheck.removeError(mnvp);
				formCheck.removeError(mvp);
			}
		});
		this.modal.open();
	},
	setParamsValues : function(response) {
		var msg = response.message;
		var box = this.modal.getBox();
		if (response.success) {
			box.getElement("[name='transfer']").set('value', msg.transfer);
			box.getElement("[name='deleteApp']").set('value', msg.deleteApp);
			box.getElement("[name='deleteSelf']").set('value', msg.deleteSelf);
			
			box.getElement("[name='installApp']").set('value', msg.installApp);
			box.getElement("[name='downloadApp']").set('value', msg.downloadApp);
			box.getElement("[name='lockedApp']").set('value', msg.lockedApp);
			
			box.getElement("[name='scp']").set('value', msg.scp);
			box.getElement("[name='maxFailCount']").set('value', msg.maxFailCount);
			box.getElement("[name='keyVersion']").set('value', msg.keyVersion);
			box.getElement("[name='maxKeyNumber']").set('value', msg.maxKeyNumber);
			var mnvp = box.getElement("[name='managedNoneVolatileSpace']");
			var mvp = box.getElement("[name='managedVolatileSpace']");
			if (msg.managedNoneVolatileSpace != '') {
				box.getElement("[name='spacePatten']").set('checked', 'true');
				mnvp.set('value', msg.managedNoneVolatileSpace);
				mvp.set('value', msg.managedVolatileSpace);
				mnvp.erase('disabled');
				mvp.erase('disabled');
			}

		} else {
			new LightFace.MessageBox().error("安装参数解析出错，请检查参数格式");
			box.getElement("[name='transfer']").set('value', 1);
			box.getElement("[name='deleteApp']").set('value', 1);
			box.getElement("[name='deleteSelf']").set('value', 1);
			box.getElement("[name='scp']").set('value', '02,15');
			box.getElement("[name='maxFailCount']").set('value', 255);
			box.getElement("[name='keyVersion']").set('value', 115);
			box.getElement("[name='maxKeyNumber']").set('value', 16);
			box.getElement("[name='spacePatten']").set('disabled', 'disabled');
			box.getElement('[name="managedNoneVolatileSpace"]').set('disabled', 'disabled');
			box.getElement('[name="managedVolatileSpace"]').set('disabled', 'disabled');
		}
	},
	submit : function() {
		new Request.JSON({
			url : ctx + "/html/securityDomain/?m=createInstallParams",
			data : $(this.formId),
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(response) {

		if (response.success) {
			this.close();
			$('installParams').set('value', response.message);
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},
	close : function() {
		this.modal.getBox().dispose();
		this.clear();
		this.modal.close();
	},
	clear : function() {
		var div = $('container').getSiblings('div,table');
		if ($chk(div)) {
			$each(div, function(e, index) {
				e.dispose();
			});
		}
	}
});