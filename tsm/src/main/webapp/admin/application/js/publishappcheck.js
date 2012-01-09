var Appcheck = Appcheck ? Appcheck :{};
var formCheck;
var thirdSd = "";
var box2;
var appStatus;
var applicationVersion;
Appcheck = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawUserBox();
		this.drawGrid();
		this.drawUserBox2();
		this.keyProfile = '';
		this.gridHsmkeyConfig = {};
		this.tkSelectIds = [];
		this.kekSelectIds = [];
		this.drawSpBox();
	},
	drawSpBox : function() {
		this.hsmkeyConfigBox = new LightFace({
			width : 500,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '保 存',
				event : function() {
					var ids = this.gridHsmkeyConfig.selectIds;
					if(this.keyProfile == 'tk') {
						this.tkSelectIds.empty();
					//	this.tkSelectIds = this.gridHsmkeyConfig.selectIds;
						for (var i=0;i<ids.length;i++){
							this.tkSelectIds.push(ids[i].toString());
						}
						//alert(this.tkSelectIds.toString());
						this.userBox.messageBox.getElement('input[name=hsmkeyConfigTK]').set('value', this.tkSelectIds.toString());
					} else if(this.keyProfile == 'kek') {
						this.kekSelectIds.empty();
					//	this.macSelectIds = this.gridHsmkeyConfig.selectIds;
						for (var i=0;i<ids.length;i++){
							this.kekSelectIds.push(ids[i].toString());
						}
					//	alert(this.kekSelectIds.toString());
						this.userBox.messageBox.getElement('input[name=hsmkeyConfigKEK]').set('value', this.kekSelectIds.toString());
					}
					
					this.hsmkeyConfigBox.close();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		
		});
		
		this.box = new LightFace( {
			width : 700,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '保 存',
				event : function() {
					if($chk(this.form.getElement('[id=deleteRule]'))) {
						if (deleteSelf == 0){
							if (this.form.getElement('[id=deleteRule]').get('value') == 2){
								new LightFace.MessageBox().error('安全域的删除规则必须与安装参数的配置保持一致');
								return false;
							}
						}else if (deleteSelf == 1){
							if (this.form.getElement('[id=deleteRule]').get('value') != 2){
								new LightFace.MessageBox().error('安全域的删除规则必须与安装参数的配置保持一致');
								return false;
							}
						}
					}
					this.form.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
		
		this.infoBox = new LightFace( {
			width : 700,
			height : 400,
			draggable : true,
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
	drawUserBox : function() {
		this.userBox = new LightFace( {
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
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
	},
	drawUserBox2 : function() {
		this.userBox2 = new LightFace( {
			draggable : true,
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
			url : ctx + '/html/requistion/?m=index',
			multipleSelection : false,
			buttons : [ 
			{
				name : '审核',
				icon : ctx + '/admin/images/icon_9.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.userBox.options.title = '审核信息';
					this.userBox.options.titleImage = ctx + '/admin/images/icon_9.png';
					this.userBox.options.content = $('requistionDiv').get('html');
					this.userBox.addEvent('open', this.openEditApp.bind(this));
					this.userBox.options.width = 740;
					this.userBox.options.height = 360;
					this.userBox.open();
					this.userBox.removeEvents('open');
				}.bind(this)
			},			
			{
				name : '查看应用版本',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的信息');
						return;
					}
					this.userBox2.options.title = '应用版本详情';
					this.userBox2.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.userBox2.options.content = $('appVerInfoDiv').get('html');
					this.userBox2.addEvent('open', this.openViewAppVer.bind(this));
					this.userBox2.options.width = 800;
					this.userBox2.options.height = 360;
					this.userBox2.open();
					this.userBox2.removeEvents('open');
					this.userBox2.addEvent('close', this.closeWin.bind(this));
				}.bind(this)
			}
			],
        	columnModel : [{dataName : 'id', identity : true},{title : '应用名称', dataName : 'appName',order : false}, {title : '类型', dataName : 'type'},
        	               {title : '版本号', dataName : 'versionNo',order : false},  {title : '申请理由', dataName : 'reason'}, {title : '提交时间', dataName : 'submitDate'},
      	            	  {title : '状态', dataName : 'status'}],
			searchButton : true,
        	searchBar : {filters : [{title : '应用名：', name : 'appName', type : 'text'}
        	]},
			headerText : '应用审核',
			headerImage : ctx + '/images/user_icon_32.png'
		});
	},
	openEditApp : function() {
		thirdSd = "";
		var selectIds = this.grid.selectIds;
		this.appForm = this.userBox.messageBox.getElement('form');
		var box = this.userBox.messageBox;
		var opinion = this.userBox.messageBox.getElement('input[name=opinion]');
		var typeTk = this.userBox.messageBox.getElement('input[name=typeTk]');
		var typeKek = this.userBox.messageBox.getElement('input[name=typeKek]');
		this.userBox.messageBox.getElement('form').set('action', ctx + '/html/requistion/?m=updatePublish');
		this.userBox.messageBox.getElement('select[name=statusOriginal]').addEvent('change', function(){
			if(this.get('value') == 3){
				if (appStatus == 5){
					formCheck.register(typeTk);
					formCheck.register(typeKek);
				}
				formCheck.dispose(opinion);
				if (opinion.get('value') == ''){
					opinion.set('value','同意');
				}
				opinion.set('class',"inputtext validate['%chckMaxLength']");
				formCheck.register(opinion);
				var thirdInputs = box.getElements('select[name="sdids"]');
				thirdInputs.each(function(e){
					formCheck.register(e);
				});
			}else if (this.get('value') == 4){
				if (appStatus == 5){
					formCheck.dispose(typeTk);
					formCheck.dispose(typeKek);
				}
				formCheck.dispose(opinion);
				if (opinion.get('value') == '同意'){
					opinion.set('value','');
				}
				opinion.set('class',"inputtext validate['required','%chckMaxLength']");
				formCheck.register(opinion);
				var thirdInputs = box.getElements('select[name="sdids"]');
				thirdInputs.each(function(e){
					formCheck.dispose(e);
				});
			}
		});
		new Request.JSON( {
			url : ctx + '/html/requistion/?m=index&id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					appStatus = data.result[0]['appStatus'];
					var inputs = this.userBox.messageBox.getElements('input,select');
					this.appForm = this.userBox.messageBox.getElement('form');
					var files = this.userBox.messageBox.getElement('table[name=files]');
					var box = this.userBox.messageBox;
					if (appStatus != 5){
						this.userBox.messageBox.getElement('[id=typeTktd]').set('html','');
						this.userBox.messageBox.getElement('[id=typeKektd]').set('html','');
						this.userBox.messageBox.getElement('[id=tkAlgorithmtd]').set('html','');
						this.userBox.messageBox.getElement('[id=kekAlgorithmtd]').set('html','');
						this.userBox.messageBox.getElement('[id=typeTktd]').setStyle('display','none');
						this.userBox.messageBox.getElement('[id=typeKektd]').setStyle('display','none');
						this.userBox.messageBox.getElement('[id=tkAlgorithmtd]').setStyle('display','none');
						this.userBox.messageBox.getElement('[id=kekAlgorithmtd]').setStyle('display','none');
					}else {
						this.addHsmkeyConfigTK();
						this.addHsmkeyConfigKEK();
					}
					$each(inputs, function(input, i) {
						if(input.get('name') != 'statusOriginal'){
							input.set('value', data.result[0][input.get('name')]);
						}
					});
//					new Request.JSON(
//							{
//								url : ctx
//+ "/html/commons/?m=exportEnum&enumName=com.justinmobile.tsm.cms2ac.security.scp02.EncryptorVendor&exportMethodName=export",
//								onSuccess : function(json) {
//									if (json.success) {
//										transConstant = json.message;
//										var jsonHash = new Hash(transConstant);
//										var option = new Element('option').set('value','').set('text','请选择...');
//										option.inject(box.getElement("[id='tkVendor']"));
//										option = new Element('option').set('value','').set('text','请选择...');
//										option.inject(box.getElement("[id='kekVendor']"));
//										jsonHash.each(function(value, key) {
//											option = new Element('option').set('value',value.value).set('text',value.name);
//											option.inject(box.getElement("[id='tkVendor']"));
//											option = new Element('option').set('value',value.value).set('text',value.name);
//											option.inject(box.getElement("[id='kekVendor']"));
//										});
//									}
//								}
//							}).get();
					new Request.JSON( {
						url : ctx + '/html/loadFile/?m=loadByIds&ids=' + data.result[0]['loadFileIds'],
						onSuccess : function(result) {
							if (result.success) {
								new Request.JSON({
									url : ctx + "/html/securityDomain/?m=index",
									onSuccess : function(json) {
										if (json.success) {
											json.result.forEach(function(e, index) {
												thirdSd += "<option value='"+e.id+"'>"+e.sdName+"</option>";
											});
											var html = "<tr><td align=\"center\">加载文件名</td><td  align=\"center\">" +
													"所属安全域类型</td><td  align=\"center\">所属安全域名称</td></tr>";
											result.result.forEach(function(e, index) {
												//var name=	DataLength(e.name,15);
												//alert(data.result[0]['type']);
												html += "<tr><td  align=\"left\">"+e.name+"</td><td  align=\"left\">"+e.sdModel+"</td><td  align=\"center\">";
												if ((e.sd_id != null && e.sd_id != "") || e.sdModelOriginal != '2' || data.result[0]['typeOriginal'] == '12'){
													//alert(e.sd_id);
													html += "<input name='sdids2' id='"+e.id+"' class=\"validate['%notThirdSdCheck']\" readonly='readonly' " +
															"value='"+(e.sd_sdName == undefined? '无安全域':e.sd_sdName)+"' /></td>";
												}else{
													html += "<select name='sdids' id='"+e.id+"' class=\"validate['required']\">" +
																"<option value=''>请选择...</option>"+thirdSd+"</select></td></tr>";
												}
											});
											files.set('html',html);
											this.addValidate();
										}
									}.bind(this)
								}).get({'search_EQI_status' : 2, 'search_EQI_model' : 2, 'page_pageSize' : 10000000});
							} else {
								this.addValidate();
								new LightFace.MessageBox().error("没有对应的加载文件");
							}
						}.bind(this)
					}).get();
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	closeWin : function() {
//		location.reload();
	},
	addHsmkeyConfigTK : function() {
		
		this.userBox.messageBox.getElement('[id=addHsmkeyConfigTK]').addEvent('click', function() {
			this.keyProfile = 'tk';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	addHsmkeyConfigKEK : function() {
		
		this.userBox.messageBox.getElement('[id=addHsmkeyConfigKEK]').addEvent('click', function() {
			this.keyProfile = 'kek';
			this.hsmkeyConfigBox.options.title = '配置加密机';
			this.hsmkeyConfigBox.options.titleImage = ctx + '/admin/images/icon_4.png';
			
			this.hsmkeyConfigBox.options.content = '<div name="hsmkeyConfigDiv"></div>';
			this.hsmkeyConfigBox.addEvent('open', this.openHsmkeyConfigBox.bind(this));
			this.hsmkeyConfigBox.open();
			this.hsmkeyConfigBox.removeEvents('open');
			
		}.bind(this));
	},
	openHsmkeyConfigBox : function() {
		var url = ctx + '/html/encryptor/?m=list';
		this.hsmkeyConfigBox.messageBox.setStyle('overflow-y', 'hidden');
		var tableId = this.hsmkeyConfigBox.messageBox.getElement('div[name=hsmkeyConfigDiv]');
		this.gridHsmkeyConfig = new JIM.UI.Grid(tableId, {
			url : url,
			selection : true,
			multipleSelection : true,
			columnModel : [ 
			    {dataName : 'id', identity : true}, 
			    {title : '类型',dataName : 'model', align : 'center', order : true}, 
			    {title : '厂商',dataName : 'vendor', align : 'center', order : true},
			    {title : '索引',dataName : 'index', align : 'center', order : true},
			    {title : '版本',dataName : 'version', align : 'center', order : true}
			]
			,header : false
			,
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
		var ids = new Array();
		if(this.keyProfile == 'tk') {
			for (var i=0;i<this.tkSelectIds.length;i++){
				ids.push(this.tkSelectIds[i].toString());
			}
			this.gridHsmkeyConfig.selectIds = ids;
		} else if(this.keyProfile == 'kek') {
			//this.gridHsmkeyConfig.selectIds = this.kekSelectIds;
			for (var i=0;i<this.kekSelectIds.length;i++){
				ids.push(this.kekSelectIds[i].toString());
			}
			this.gridHsmkeyConfig.selectIds = ids;
		}
		this.gridHsmkeyConfig.load();
	},
	openViewAppVer : function() {
		thirdSd = "";
		var selectIds = this.grid.selectIds;
		this.appForm = this.userBox2.messageBox.getElement('form');
		box2 = this.userBox2.messageBox;
		new Request.JSON( {
			url : ctx + '/html/requistion/?m=index&id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					data.result.forEach(function(e, index) {
						var page = new Application.Page({
							applicationVersionId : e.originalId
						});
						page.init();
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	openTestFile : function() {
		var selectIds = this.grid.selectIds;
		this.appForm = this.userBox2.messageBox.getElement('form');
		box2 = this.userBox2.messageBox;
		new Request.JSON( {
			url : ctx + '/html/requistion/?m=index&id=' + selectIds[0],
			onSuccess : function(data) {
				if (data.success) {
					data.result.forEach(function(e, index) {
						new Request.JSON({
							url : ctx + "/html/appVer/?m=index",
							data : {
								async : false,
								search_EQL_id : e.originalId
							},
							onSuccess : function(data2) {
								if (data2.success) {
									data2.result.forEach(function(e, index) {
									//	alert(e.id);
										new Request.JSON({
											url : ctx + "/html/testfile/?m=index",
											data : {
												async : false,
												search_ALIAS_appVerL_EQL_id : e.id
											},
											onSuccess : app.getTestAppFile.bind(this)
										}).get();
									});
								} else {
									new LightFace.MessageBox().error(data.message);
								}
							}.bind(this)
						}).get();
					});
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	getTestAppFile : function(json) {
		if (json.success) {
			json.result.forEach(function(e, index) {
				new Element('dd').set('html','<p class="regtextleft">'+e.originalName+
						'</p>&nbsp;&nbsp;<a class="b"  style="float : none;"  href="' + ctx + '/html/testfile/?m=downFile&tfId=' + e.id + '"><span>下载</span></a>')
				.inject(box2.getElement("[id='testFile']"));
			});
			if (json.result.length == 0){
				new Element('dd').set('html',"没有测试文件").inject(box2.getElement("[id='testFile']"));
			}
		} else {
			alert("获取测试文件失败" + json.message);
		}
	},
	addValidate : function() {
		formCheck = new FormCheck(this.appForm, {
			submit : false,
			zIndex : this.userBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			onValidateSuccess : function() {//校验通过执行load()
				var sdidStr = "";
				this.appForm.getElements('select[name=\'sdids\']').forEach(function(e, index) {
					sdidStr += (e.id + ";"+e.value+",");
				}); 
				var statusOriginal =  this.userBox.messageBox.getElement('select[name=statusOriginal]').get('value');
			//	alert(statusOriginal);
				if(statusOriginal != '4' && this.tkSelectIds.length == 0 && appStatus==5) {
					new LightFace.MessageBox().error("传输密钥未配置加密机");
				} else if(statusOriginal != '4' && this.kekSelectIds.length == 0 && appStatus==5) {
					new LightFace.MessageBox().error("敏感数据加密密钥未配置加密机");
				} else {
					this.submitForm();
				}
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
function DataLength(fData,expectLength)   
{   
    var intLength=0;   
    for (var i=0;i<fData.length;i++)   
    {   
        if ((fData.charCodeAt(i) < 0) || (fData.charCodeAt(i) > 255))   {
            intLength=intLength+2;   
        }else if ((fData.charCodeAt(i) > 48) && (fData.charCodeAt(i) < 58)){
        	intLength=intLength+1.3;  
        }
        else  {
            intLength=intLength+1;       
        }
        if (expectLength<intLength){
        	return fData.substring(0,i)+'...';
        }
    }
    return fData;
}
function notThirdSdCheck(el){ 
	if (el.value == '无安全域'){
        el.errors.push("非公共第三方安全域不存在，不能提交审核");
        return false;
	}
}

function checkHex(el) {
	if(el.value.test(/[^0-9a-fA-F]/)) {
		el.errors.push('必须是十六进制');
		return false;
	} else if(el.value.length % 2 != 0) {
		el.errors.push('必须是偶数');
		return false;
	} else {
		return true;
	}
}