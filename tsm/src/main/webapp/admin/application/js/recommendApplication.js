var Recommand = Recommand ? Recommand :{};
var orderNos = "";
var selectProvince = "";
Recommand = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawUserBox();
		this.drawGrid();
	},
	drawUserBox : function() {
		this.userBox = new LightFace( {
			draggable : true,
			width : 800,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [{
				title : '保 存',
				event : function() {
					this.appForm.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
		this.userBox2 = new LightFace( {
			draggable : true,
			width : 800,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/application/?m=recommendApplication&search_EQS_showAll=yes&local=true',
			multipleSelection : false,
			buttons : [  {
				name : '查看',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.userBox2.options.title = '推荐下载应用信息';
					this.userBox2.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.userBox2.options.content = $('requistionDiv').get('html');
					this.userBox2.addEvent('open', this.openViewApp.bind(this));
					this.userBox2.open();
					this.userBox2.removeEvents('open');
				}.bind(this)
			},
			            {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.userBox.options.title = '新增推荐下载应用';
					this.userBox.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.userBox.options.content = $('requistionDiv').get('html');
					this.userBox.addEvent('open', this.openNewUser.bind(this));
					this.userBox.open();
					this.userBox.removeEvents('open');
				}.bind(this)
			}, 
			{
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的推荐下载应用');
						return;
					}
					new LightFace.MessageBox( {
						onClose : function(result) {
							if (result) {
								new Request.JSON( {
									url : ctx + '/html/application/?m=removeRecommend',
									onSuccess : function(data) {
										if (data.success) {
											new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									}.bind(this)
								}).post( {
									id : this.grid.selectIds[0]
								});
							}
						}.bind(this)
					}).confirm('确认要将该推荐删除吗？');
				}.bind(this)
			}
			],
        	columnModel : [{dataName : 'id', identity : true}
        	,{title : '应用名称', dataName : 'application_name'}
        	,{title : '应用所在地', dataName : 'application_location'}
        	,{title : '推荐排序', dataName : 'orderNo'}],
			searchButton : true,
        	searchBar : {filters : [{title : '应用名称：', name : 'search_ALIAS_applicationL_LIKES_name', type : 'text'}
        	]},
			headerText : '推荐下载应用管理',
			headerImage : ctx + '/images/user_icon_32.png'
		});
	},
	openNewUser : function() {
		isView = false;
		var selectIds = this.grid.selectIds;
		this.appForm = this.userBox.messageBox.getElement('form');
		this.appForm.set('action', ctx + '/html/application/?m=saveRecommend');
		this.addValidate();
		var optionStr = '<option value="" select>请选择... </option>';
		new Request.JSON( {
			url : ctx + '/html/application/?m=recommendAppList&reload='+Math.random()+'&local=true',
			onSuccess : function(data) {
				if (data.success) {
					orderNos = data.message;
					//alert(orderNos);
					data.result.forEach(function(result, i) {
						optionStr += '<option value="'+result.id+'">'+result.name+' 供应商:'+result.spName+' 所在地:'+result.location+'</option>';
					});
					var select = this.userBox.messageBox.getElement('select[name="application.id"]');
					select.set('html',optionStr).set('value','');
					select.addEvent('change', function(event) {
						var selectStr = select.getSelected().get('text')+'';
					//	alert(selectStr.substring(selectStr.lastIndexOf(':')+1,selectStr.length));
					//	alert(selectStr.lastIndexOf(':'));
						selectProvince = selectStr.substring(selectStr.lastIndexOf(':')+1,selectStr.length);
					}.bind(this));
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},	openViewApp : function() {
		isView = true;
		var selectIds = this.grid.selectIds;
		this.userForm = this.userBox2.messageBox.getElement('form');
		new Request.JSON( {
			url : ctx + '/html/application/?m=recommendApplication&local=true&search_EQL_id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.userBox2.messageBox.getElements('input,select');
					$each(inputs, function(input, i) {
						input.set('value', data.result[0][input.get('name')]);
					});
					this.userBox2.messageBox.getElement('select[name=\'application.id\']').set('html',"<option value='"+data.result[0]['application_id']+"'>"
							+data.result[0]['application_name']+"</option>");
					this.userBox2.messageBox.getElement('select[name=\'application.id\']').set('disabled',true);
					this.userBox2.messageBox.getElement('input[name=\'orderNo\']').set('disabled',true);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	addValidate : function() {
		new FormCheck(this.appForm, {
			submit : false,
			zIndex : this.userBox.options.zIndex,
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
			url : this.appForm.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.userBox.close();
							this.grid.load();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.appForm.toQueryString());
	}
});
