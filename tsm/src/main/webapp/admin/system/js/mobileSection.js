var System = System ? System :{};

System.MobileSection = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawMobileSectionBox();
		this.drawGrid();
	},
	drawMobileSectionBox : function() {
		this.mobileSectionBox = new LightFace( {
			draggable : true,
			initDraw : false,
			width : 600,
			height : 300,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			}.bind(this),
			buttons : [ {
				title : '保 存',
				event : function() {
					this.mobileSectionForm.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/mobileSection/?m=index',
			multipleSelection : true,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					var tip = document.getElement('div[class=fc-tbx]');
					if ($chk(tip)) {
						tip.dispose();
					}
					this.mobileSectionBox.options.title = '新增万号段';
					this.mobileSectionBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.mobileSectionBox.options.content = $('mobileSectionDiv').get('html');
					this.mobileSectionBox.addEvent('open', this.openNewUser.bind(this));
					this.mobileSectionBox.open();
					this.mobileSectionBox.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/mobileSection/?m=remove',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox().info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
										this.grid.load();
										this.grid.cleanSelectIds();
									}.bind(this)
								}).post( {
									msId : this.grid.selectIds.toString()
								});
							}
						}.bind(this)
					}).confirm('确认要将该万号段记录删除吗？');
				}.bind(this)
			} ],
			columnModel : [ {
				dataName : 'id',
				identity : true
			}, {
				title : '省份',
				dataName : 'province'
			}, {
				title : '城市',
				dataName : 'city'
			}, {
				title : '万号段',
				dataName : 'paragraph'
			}, {
				title : '区号',
				dataName : 'district'
			} , {
				title : '归属SCP号码',
				dataName : 'scpNumber'
			} , {
				title : 'SCP ID',
				dataName : 'scpId'
			} , {
				title : '归属SCP名称',
				dataName : 'scpName'
			} , {
				title : '彩信中心名称',
				dataName : 'mmscenterName'
			} , {
				title : '彩信中心ID',
				dataName : 'mmscenterId'
			} , {
				title : '启用局数据号',
				dataName : 'officeData'
			}],
			searchButton : true,
			searchBar : {
				filters : [ {
					title : '省份：',
					id : 'search_EQS_province',
					name : 'search_EQS_province',
					type : 'select',
					remote : true
				}, {
					title : '万号段：',
					name : 'search_LIKES_paragraph',
					type : 'text',
					'validates' : [{regexp : 'number_nonnegative', message : '请输入数字类型'}]
				} ]
			},
			headerText : '万号段管理',
			headerImage : ctx + '/images/user_icon_32.png'
		});
		this.addProvice($('search_EQS_province'));
	},
	openNewUser : function() {
		this.mobileSectionForm = this.mobileSectionBox.messageBox.getElement('form');
		this.mobileSectionForm.set('action', ctx + '/html/mobileSection/?m=add');
		this.addValidate();
		var pronvince = this.mobileSectionBox.messageBox.getElement('select[name=province]');
		this.addProvice(pronvince);
	},
	addProvice : function(pronvince) {
		var options = pronvince.options;
		options.add(new Option("---请选择---",""));
		options.add(new Option("北京","北京"));
		options.add(new Option("天津","天津"));
		options.add(new Option("河北","河北"));
		options.add(new Option("山西","山西"));
		options.add(new Option("内蒙古","内蒙古"));
		options.add(new Option("辽宁","辽宁"));
		options.add(new Option("吉林","吉林"));
		options.add(new Option("黑龙江","黑龙江"));
		options.add(new Option("上海","上海"));
		options.add(new Option("江苏","江苏"));
		options.add(new Option("浙江","浙江"));
		options.add(new Option("安徽","安徽"));
		options.add(new Option("福建","福建"));
		options.add(new Option("江西","江西"));
		options.add(new Option("山东","山东"));
		options.add(new Option("河南","河南"));
		options.add(new Option("湖北","湖北"));
		options.add(new Option("湖南","湖南"));
		options.add(new Option("广东","广东"));
		options.add(new Option("广西","广西"));
		options.add(new Option("海南","海南"));
		options.add(new Option("重庆","重庆"));
		options.add(new Option("四川","四川"));
		options.add(new Option("云南","云南"));
		options.add(new Option("贵州","贵州"));
		options.add(new Option("西藏","西藏"));
		options.add(new Option("陕西","陕西"));
		options.add(new Option("甘肃","甘肃"));
		options.add(new Option("宁夏","宁夏"));
		options.add(new Option("青海","青海"));
		options.add(new Option("新疆","新疆"));
	},
	addValidate : function() {
		this.formcheck = new FormCheck(this.mobileSectionForm, {
			submit : false,
			zIndex : this.mobileSectionBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {//校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.mobileSectionForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.mobileSectionBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.mobileSectionForm.toQueryString());
	},
	getGrid : function() {
		return this.grid;
	}
});
