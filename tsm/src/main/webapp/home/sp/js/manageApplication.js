Application = new Class({
	Implements : [ Events, Options ],
	modal : null,
	formId : null,
	form : null,
	applicationConstant : null,
	options : {},
	initialize : function(options) {
		this.setOptions(options);
		new Request.JSON({
			async : false,
			url : ctx + "/html/application/?m=exportConstant",
			data : {
				search_ALIAS_spL_EQL_id : spId
			},
			onSuccess : function(json) {
				if (json.success) {
					this.applicationConstant = json.message;
				}
			}.bind(this)
		}).get();
	},

	createVersion : function(value) {
		this.formId = "form" + new Date().valueOf();
		this.modal = new LightFace({
			title : "创建新版本",
			width : 480,
			content : $('createVersionDiv').get("html"),
			resetOnScroll : false,
			buttons : [ {
				title : '保 存',
				event : function() {
					this.modal.getBox().getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});

		var box = this.modal.getBox();
		box.getElement("[title='createVersionForm']").set("id", this.formId).eliminate("title");
		box.getElement("[name='applicationId']").set('value', value);

		new FormCheck(box.getElement("[title='createVersionForm']"), {
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

		this.modal.open();
	},

	submit : function() {
		new Request.JSON({
			url : ctx + "/html/appVer/?m=createVersion",
			data : $(this.formId),
			onSuccess : this.submitCallback.bind(this)
		}).post();
	},

	submitCallback : function(response) {
		if (response.success) {
			new LightFace.MessageBox().info("创建新版本成功");
			this.close();
			self.location = ctx + "/home/sp/uploadCap.jsp?applicationVersionId=" + response.message.applicationVersionId;

		} else {
			new LightFace.MessageBox().error2("创建新版本失败，" + response.message);
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

	getApplications : function() {
		$('sdnextpage').empty();
		var appName = document.getElement('input[name=appName]').get('value');
		var paging = new JIM.UI.Paging({
			url : ctx + '/html/application/?m=index&page_orderBy=aid_asc&search_ALIAS_spL_EQL_id='+spId,
			limit : 5,
			head : {el : 'sdnextpage', showNumber : true, showText : false},
			onAfterLoad: this.getApplicationsCallback.bind(this)
		});
		paging.load('search_LIKES_name=' + appName);
	},

	getApplicationsCallback : function(json) {

		if (json.success) {
			var template = $("applicationListTemplate");
			var table = template.getElement('[id="applicationTableHeader"]').clone();

			var target = table.getElement('[title="applicationList"]').erase("title");
			
			json.result.each(function(item) {
				var row = template.getElement('[id="applicationTableRow"]').clone();

				row.set("id", 'application' + item.id);
				row.getElement('[title="名称"]').set("html", item.name);
				row.getElement('[title="AID"]').set("html", item.aid);
				row.getElement('[title="状态"]').set("html", item.status);

				row.getElement('[title="查看"]').set("href", ctx + "/home/sp/showApplicationDetails.jsp?applicationId=" + item.id);

				row.getElement('[title="修改"]').addEvent('click', function(event) {
					event.stop();
					if (this.applicationConstant.STATUS_ARCHIVED == item.statusOriginal) {
						new LightFace.MessageBox().error("应用状态是已归档，不能修改");
					} else {
						self.location.href = ctx + "/home/sp/modifyAppBaseInfo.jsp?applicationId=" + item.id;
					}
				}.bind(this));
				// row.getElement('[title="创建"]').set("href", ctx +
				// "/home/sp/createAppVersion?applicationId=" + item.id);
				row.getElement('[title="创建"]').addEvent('click', function(event) {
					event.stop();
					if (this.applicationConstant.STATUS_ARCHIVED == item.statusOriginal) {
						new LightFace.MessageBox().error("应用状态是已归档，不能创建新版本");
					} else {
						this.createVersion(item.id);
					}
				}.bind(this));

				row.getElement('[title="删除"]').addEvent('click', function(event) {
					event.stop();
					if (this.applicationConstant.STATUS_INIT == item.statusOriginal) {
						this.modal = new LightFace({
							title : "确认删除",
							appletModal : this.modal,
							height : 50,
							width : 300,
							content : "确认删除应用？只有未审核的应用可以删除",
							resetOnScroll : false,
							buttons : [ {
								title : "确认",
								color : "blue",
								event : function() {
									this.close();
									this.removeApplication(item.id);
								}.bind(this)
							}, {
								title : "取消",
								event : function() {
									this.close();
								}
							} ]
						}).open();
					} else {
						new LightFace.MessageBox().error("应用状态不是初始化，不能删除");
					}

				}.bind(this));

				row.inject(target);
			}.bind(this));
			
			table.inject($("applicationList").empty());
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	},

	removeApplication : function(applicationId) {
		new Request.JSON({
			url : ctx + '/html/application/?m=remove',
			data : {
				applicationId : applicationId
			},
			onSuccess : function(json) {
				this.removeApplicationCallback(json, applicationId);
			}.bind(this)
		}).post();
	},

	removeApplicationCallback : function(json, applicationId) {
		if (json.success) {
			new LightFace.MessageBox().info('删除成功');
			this.getApplications();
		} else {
			new LightFace.MessageBox().error(json.message);
		}
	}
});