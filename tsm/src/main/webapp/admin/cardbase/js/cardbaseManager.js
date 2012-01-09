/**
 * 批次管理类
 */
CardBaseManager = {};
//应用关联LOADFILE
CardBaseManager.joinLoadFile = new Class({
	Implements : [Events,Options],
	options :{
		
	},
	initialize : function(options){
		var CBM = this;
		new JIM.UI.Grid('tableDiv', {
        	url : ctx + '/html/cardbaseinfo/?m=index',
        	multipleSelection: false,
        	buttons : [{name : '关联加载文件',
        		icon : ctx + '/admin/images/join.png',
        		handler :function(){
        			if(this.selectIds!=''){
        				CBM.createLoadFileVersionGird(this.selectIds);
        			}else{
        				new LightFace.MessageBox().error("请先选择列表中的批次");
        			}
        	}},{name : '已关联文件',
        		icon : ctx + '/admin/images/list.png',
        		handler :function(){
        			if(this.selectIds!=''){
        				CBM.listGrid(this.selectIds);
        			}else{
        				new LightFace.MessageBox().error("请先选择列表中的批次");
        			}
        	}}],
        	columnModel : [{dataName : 'id', identity : true}, {title : '批次编号', dataName : 'batchNo'},{title : '批次名称', dataName : 'name'},{title : 'SE卡商', dataName : 'osImplementor'},{title : 'SE类型', dataName : 'type'},{title : '芯片类型', dataName : 'coreType'},{title : 'JAVA版本', dataName : 'javaVersion'},{title : 'cms2ac版本',dataName : 'cms2acVersion'},{title : '初始内存空间(Byte)',dataName : 'totalRamSize'},{title : '初始存储空间(Byte)',dataName : 'totalRomSize'}],
        	searchButton : true,
        	searchBar : {filters : [{title : '批次编号：', name : 'search_LIKES_batchNo', type : 'text',width :150},{title : '批次名：', name : 'search_LIKES_name', type : 'text',width :150}]},
        	headerText : '关联加载文件 '
        });
	},
	createLoadFileVersionGird : function(id){
		var CBM = this;
		new JIM.UI.WinGrid({
			url : ctx + '/html/loadFileVersion/?m=findUnLinkPage&cardBaseId=' + id[0],
        	multipleSelection: true,
        	order : false,
        	width : 800,
        	height : 330,
        	winButtons : [{
				title: '提交',
				color : 'blue',
				event: function() {
					if(this.selectIds!=''){
						CBM.submit(id,this.selectIds,this);
					}else{
						new LightFace.MessageBox().error("您还未选择加载文件版本");
					}
				;}
			},{
				title: '取消',
				event: function() { this.close();}
			}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '所属加载文件名称', dataName : 'loadFile_name'}, {title : '版本号', dataName : 'versionNo'}, {title : '创建时间', dataName : 'createDate'}, {title : 'CAP文件大小(Byte)', dataName : 'fileSize'}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '关联加载文件版本'
		});
	},
	listGrid : function(id){
		var CBM = this;
		new JIM.UI.WinGrid({
			url : ctx + '/html/cardbaseloadfile/?m=Index&search_ALIAS_cardBaseInfoL_EQL_id=' + id,
        	multipleSelection: false,
        	order : false,
        	width : 800,
        	height : 330,
        	winButtons : [{
				title: '删除',
				event: function() { 
					if(this.selectIds!=''){
						CBM.delLink(this.selectIds,this);
					}else{
						new LightFace.MessageBox().error("您还未选择加载文件版本");
					}
				}
			},{
				title: '退出',
				event: function() { this.close();}
			}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '所属加载文件名称', dataName : 'loadFileVersion_loadFile_name'}, {title : '批次名', dataName : 'cardBaseInfo_name'}, {title : '加载文件版本', dataName : 'loadFileVersion_versionNo'}, {title : '批次编号', dataName : 'cardBaseInfo_batchNo'}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '已关联文件版本信息 '
		});
	},
	delLink : function(cbld,grid){
		new LightFace.MessageBox({
			onClose : function(){
				if(this.result){
					new Request.JSON({
						url : ctx + '/html/cardbaseloadfile/?m=delLink&cbld=' + cbld[0],
						onSuccess : function(data){
							if(data.success){
								grid.load();
								new LightFace.MessageBox().info(data.message);
							}else{
								new LightFace.MessageBox().error(data.message);
							}
						}
					}).get();
				}
			}
		}).confirm("您确认要删除此关联关系吗？");
	},
	submit : function(id,ids,winGrid){
		var idsS = ids.join(",");
		new Request.JSON({
			url : ctx + '/html/cardbaseloadfile/?m=doLink',
			onSuccess : function(data){
				if(data.success){
					winGrid.close();
					new LightFace.MessageBox().info(data.message);
				}else{
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post({
			'loadfileVerIds' : idsS,
			'cardbaseId' : id
		});
	}
});


CardBaseManager.joinSecurityDomains = new Class({
	Implements : [Events,Options],
	options :{
		
	},
	initialize : function(options){
		var CBM = this;
		new JIM.UI.Grid('tableDiv', {
        	url : ctx + '/html/cardbaseinfo/?m=index',
        	multipleSelection: false,
        	buttons : [{name : '关联安全域',
        		icon : ctx + '/admin/images/list.png',
        		handler :function(){
        			if(this.selectIds!=''){
        				CBM.listGrid(this.selectIds);
        			}else{
        				new LightFace.MessageBox().error("请先选择列表中的批次");
        			}
        	}}],
        	columnModel : [{dataName : 'id', identity : true}, {title : '批次编号', dataName : 'batchNo'},{title : '批次名称', dataName : 'name'},{title : 'SE卡商', dataName : 'osImplementor'},{title : 'SE类型', dataName : 'type'},{title : '芯片类型', dataName : 'coreType'},{title : 'JAVA版本', dataName : 'javaVersion'},{title : 'cms2ac版本',dataName : 'cms2acVersion'},{title : '初始内存空间(Byte)',dataName : 'totalRamSize'},{title : '初始存储空间(Byte)',dataName : 'totalRomSize'}],
        	searchButton : true,
        	searchBar : {filters : [{title : '批次编号：', name : 'search_LIKES_batchNo', type : 'text',width :150},{title : '批次名：', name : 'search_LIKES_name', type : 'text',width :150}]},
        	headerText : '关联安全域 '
        });
	},
	createSecurityDomainGird : function(id,formWin){
		var CBM = this;
		var winGrid = new JIM.UI.WinGrid({
			url : ctx + '/html/securityDomain/?m=findUnLinkPage&cardBaseId=' + id,
        	multipleSelection: false,
        	order : false,
        	width : 850,
        	height : 330,
        	winButtons : [{
				title: '选择',
				color : 'blue',
				event: function() {
					if(this.selectIds!=''){
						var sdId = this.selectIds[0];
						new Request.JSON(
								{
									url :ctx + '/html/securityDomain/?m=getSdStatus&sdId=' + sdId,
									onSuccess : function(data) {
										if (data.success) {
											formWin.messageBox.getElement('[id="selectedSD"]').set('value',data.message.sdName);
											formWin.messageBox.getElement('[id="sdId"]').set('value',sdId);
											winGrid.close();
										} else {
											new LightFace.MessageBox().error(data.message);
										}
									},
									onError : function(
											result) {
									}
								}).post();
					}else{
						new LightFace.MessageBox().error("您还未选择安全域");
					}
				;}
			},{
				title: '取消',
				event: function() { this.close();}
			}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '安全域名称', dataName : 'sdName'}, {title : '安全域状态', dataName : 'status'},
        	               {title : '安全域AID', dataName : 'aid'}, {title : '空间删除规则', dataName : 'deleteRule'}, {title : '空间模式', dataName : 'spaceRule'}
        	               , {title : '安全域模式', dataName : 'model'}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '关联安全域 '
		});
	},
	listGrid : function(id){
		var CBM = this;
		var cbSdGrid = new JIM.UI.WinGrid({
			url : ctx + '/html/cardbasesecurity/?m=Index&search_ALIAS_cardBaseInfoL_EQL_id=' + id,
        	multipleSelection: false,
        	order : false,
        	width : 800,
        	height : 330,
        	winButtons : [
					{
						title: '新增',
						color : 'blue',
						event: function() { 
							cbSdGrid.hideFoot();
							CBM.addLink(id,this);
						}
					}  ,
				{
					title: '删除',
					color : 'blue',
					event: function() { 
						if(this.selectIds!=''){
							var cbsdId = this.selectIds[0];
							new Request.JSON(
									{
										url :ctx + '/html/cardbasesecurity/?m=checkSDisISD&cbsdId=' + cbsdId,
										onSuccess : function(data) {
											if (data.success) {
												new LightFace.MessageBox().error('不能对主安全域关联关系进行操作');
											} else {
												CBM.delLink(cbsdId,cbSdGrid);
											}
										},
										onError : function(
												result) {
										}
									}).post();
						}else{
							new LightFace.MessageBox().error("您还未选择安全域");
						}
					}
				}  ,
				{
					title: '更改',
					color : 'blue',
					event: function() { 
						if(this.selectIds!=''){
							var cbsdId = this.selectIds[0];
							var grid = this;
							new Request.JSON(
									{
										url :ctx + '/html/cardbasesecurity/?m=checkSDisISD&cbsdId=' + cbsdId,
										onSuccess : function(data) {
											if (data.success) {
												new LightFace.MessageBox().error('不能对主安全域关联关系进行操作');
											} else {
												cbSdGrid.hideFoot();
												CBM.changePreSet(cbsdId,grid);
											}
										},
										onError : function(
												result) {
										}
									}).post();
						}else{
							new LightFace.MessageBox().error("您还未选择安全域");
						}
					}
				},
        	 {
				title: '退出',
				event: function() { this.close();}
			}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '批次名称', dataName : 'cardBaseInfo_name'}, {title : '批次编号', dataName : 'cardBaseInfo_batchNo'}, {title : '安全域名', dataName : 'securityDomain_sdName'}, {title : '安全域AID', dataName : 'securityDomain_aid'},{title : '密钥版本号', dataName : 'presetKeyVersion'},{title : '是否预置', dataName : 'preset'},{title : '预置模式', dataName : 'presetMode'}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '已关联安全域信息'
		});
	},
	addLink : function(cardBaseId,listGrid){
		var CBM = this;
		var upForm = $('subForm').get('html');
		var formWin = new LightFace(
				{	
					titile: '批次关联安全域',
					content : upForm,
					mask : true,
					draggable : false,
					onClose : function() {
						var div = document
								.getElement('div[class=fc-tbx]');
						if ($chk(div)) {
							div.dispose();
						}
						listGrid.showFoot();
					},
					buttons : [
							{
								title : '保存',
								event : function() {
									formWin.messageBox
											.getElement(
													'form')
											.getElement(
													'button')
											.click();
								}.bind(this),
								color : 'blue'
							}, {
								title : '退出',
								event : function() {
									this.close();
								}
							} ]
				});
		formWin.messageBox.getElement('[id="cardId"]').set('value',cardBaseId);
		var getSdA = formWin.messageBox.getElement('[id="getSD"]');
		getSdA.addEvent('click',function(){
			CBM.createSecurityDomainGird(cardBaseId,formWin);
			return false;
		});
		var presetSel = formWin.messageBox.getElement('[id="preset"]');
		presetSel.addEvent('change',function(){
			if(this.value == 1){
				formWin.messageBox.getElement('[id="presetMode"]').set('disabled','');
				if(formWin.messageBox.getElement('[id="presetMode"]').get('value') == 4){
					formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','');
				}
			}else{
				formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','disabled');
				formWin.messageBox.getElement('[id="presetMode"]').set('disabled','disabled');
				validater.removeError(formWin.messageBox.getElement('[id="presetKeyVersion"]'));
			}
		});
		var presetMode = formWin.messageBox.getElement('[id="presetMode"]');
		presetMode.addEvent('change',function(){
			if(this.value == 4){
				formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','');
			}else{
				formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','disabled');
				validater.removeError(formWin.messageBox.getElement('[id="presetKeyVersion"]'));
			}
		});
		
		var form = formWin.messageBox.getElement('form');
		var validater = new FormCheck(
				form,
				{
					submit : false,
					trimValue : false,
					display : {
						showErrors : 1,
						errorsLocation : 1,
						indicateErrors : 1,
						keepFocusOnError : 0,
						closeTipsButton : 0,
						scrollToFirst : false,
						removeClassErrorOnTipClosure : 1
					},
					onValidateSuccess : function() {
						var sdId = formWin.messageBox.getElement('[id="sdId"]').get('value');
						if($chk(sdId)){
							new Request.JSON(
									{
										url : form
												.get('action'),
										onSuccess : function(
												result) {
											if (result.success) {
												new LightFace.MessageBox()
														.info("操作成功");
												formWin
														.close();
												listGrid.load();
											} else {
												new LightFace.MessageBox()
														.error(result.message);
											}
										},
										onError : function(
												result) {
										}
									}).post(form
									.toQueryString());
						}else{
							new LightFace.MessageBox()
							.error("请选择一个安全域");
						}
					}
				});
		formWin.open();
	},
	changePreSet : function(cbsdId,grid){
		var CBM = this;
		var upForm = $('subForm').get('html');
		var formWin = new LightFace(
				{	
					titile: '批次关联安全域',
					content : upForm,
					mask : true,
					draggable : false,
					onClose : function() {
						var div = document
								.getElement('div[class=fc-tbx]');
						if ($chk(div)) {
							div.dispose();
						}
						grid.showFoot();
					},
					buttons : [
							{
								title : '保存',
								event : function() {
									formWin.messageBox
											.getElement(
													'form')
											.getElement(
													'button')
											.click();
								}.bind(this),
								color : 'blue'
							}, {
								title : '退出',
								event : function() {
									this.close();
								}
							} ]
				});
		
		new Request.JSON(
				{
					url :ctx + '/html/cardbasesecurity/?m=getCBSD&cbsdId=' + cbsdId,
					onSuccess : function(data) {
						if (data.success) {
							var cbsd = data.message;
							formWin.messageBox.getElement('[id="selectedSD"]').set('value',cbsd.securityDomain_sdName);
							formWin.messageBox.getElement('[id="cardId"]').set('value',cbsd.cardBaseInfo_id);
							formWin.messageBox.getElement('[id="sdId"]').set('value',cbsd.securityDomain_id);
							formWin.messageBox.getElement('[id="preset"]').set('value',cbsd.presetOriginal);
							if(cbsd.presetOriginal != 0){
								formWin.messageBox.getElement('[id="presetMode"]').set('value',cbsd.presetModeOriginal);
							}
							formWin.messageBox.getElement('[id="cbSdId"]').set('value',cbsdId);
							formWin.messageBox.getElement('[id="presetKeyVersion"]').set('value',cbsd.presetKeyVersion);
							if(cbsd.presetOriginal == 1){
								formWin.messageBox.getElement('[id="presetMode"]').set('disabled','');
								if(cbsd.presetModeOriginal == 4){
									formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','');
								}
							}
							var getSdA = formWin.messageBox.getElement('[id="getSD"]');
							getSdA.addEvent('click',function(){
								CBM.createSecurityDomainGird(cbsd.cardBaseInfo_id,formWin);
								return false;
							});
						} else {
							new LightFace.MessageBox().error(data.message);
						}
						
						
					},
					onError : function(
							result) {
					}
				}).post();
		
		
		
		var presetSel = formWin.messageBox.getElement('[id="preset"]');
		presetSel.addEvent('change',function(){
			if(this.value == 1){
				formWin.messageBox.getElement('[id="presetMode"]').set('disabled','');
				if(formWin.messageBox.getElement('[id="presetMode"]').get('value') == 4){
					formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','');
				}
			}else{
				formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','disabled');
				formWin.messageBox.getElement('[id="presetMode"]').set('disabled','disabled');
				validater.removeError(formWin.messageBox.getElement('[id="presetKeyVersion"]'));
			}
		});
		var presetMode = formWin.messageBox.getElement('[id="presetMode"]');
		presetMode.addEvent('change',function(){
			if(this.value == 4){
				formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','');
			}else{
				formWin.messageBox.getElement('[id="presetKeyVersion"]').set('disabled','disabled');
				validater.removeError(formWin.messageBox.getElement('[id="presetKeyVersion"]'));
			}
		});
		
		var form = formWin.messageBox.getElement('form');
		form.set('action',ctx + '/html/cardbasesecurity/?m=changePrest');
		var validater = new FormCheck(
				form,
				{
					submit : false,
					trimValue : false,
					display : {
						showErrors : 1,
						errorsLocation : 1,
						indicateErrors : 1,
						keepFocusOnError : 0,
						closeTipsButton : 0,
						scrollToFirst : false,
						removeClassErrorOnTipClosure : 1
					},
					onValidateSuccess : function() {
						var sdId = formWin.messageBox.getElement('[id="sdId"]').get('value');
						if($chk(sdId)){
							new Request.JSON(
									{
										url : form
												.get('action'),
										onSuccess : function(
												result) {
											if (result.success) {
												new LightFace.MessageBox()
														.info("操作成功");
												formWin
														.close();
												grid.load();
											} else {
												new LightFace.MessageBox()
														.error(result.message);
											}
										},
										onError : function(
												result) {
										}
									}).post(form
									.toQueryString());
						}else{
							new LightFace.MessageBox()
							.error("请选择一个安全域");
						}
					}
				});
		formWin.open();
	},
	delLink : function(cbsdId,winGrid){
		new LightFace.MessageBox({
			onClose : function(){
				if(this.result){
					new Request.JSON({
						url : ctx + '/html/cardbasesecurity/?m=delLink&cbsdId=' + cbsdId,
						onSuccess : function(data){
							if(data.success){
								winGrid.load();
								new LightFace.MessageBox().info(data.message);
							}else{
								new LightFace.MessageBox().error(data.message);
							}
						}
					}).get();
				}
			}
		}).confirm("您确认要删除此关联关系吗？");
	},
	submit : function(id,ids,winGrid){
		var idsS = ids.join(",");
		new Request.JSON({
			url : ctx + '/html/cardbasesecurity/?m=doLink',
			onSuccess : function(data){
				if(data.success){
					winGrid.close();
					new LightFace.MessageBox().info(data.message);
				}else{
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post({
			'sdIds' : idsS,
			'cardbaseId' : id
		});
	}
});

CardBaseManager.joinAppvers = new Class({
	Implements : [Events,Options],
	options :{
		
	},
	initialize : function(options){
		var CBM = this;
		new JIM.UI.Grid('tableDiv', {
        	url : ctx + '/html/cardbaseinfo/?m=index',
        	multipleSelection: false,
        	buttons : [{name : '关联应用版本',
        		icon : ctx + '/admin/images/join.png',
        		handler :function(){
        			if(this.selectIds!=''){
        				CBM.createAppversGird(this.selectIds);
        			}else{
        				new LightFace.MessageBox().error("请先选择列表中的批次");
        			}
        	}},{name : '已关联应用',
        		icon : ctx + '/admin/images/list.png',
        		handler :function(){
        			if(this.selectIds!=''){
        				CBM.listGrid(this.selectIds);
        			}else{
        				new LightFace.MessageBox().error("请先选择列表中的批次");
        			}
        	}}],
        	searchButton : true,
        	searchBar : {filters : [{title : '批次编号：', name : 'search_LIKES_batchNo', type : 'text',width :150},{title : '批次名：', name : 'search_LIKES_name', type : 'text',width :150}]},
        	columnModel : [{dataName : 'id', identity : true}, {title : '批次编号', dataName : 'batchNo'},{title : '批次名称', dataName : 'name'},{title : 'SE卡商', dataName : 'osImplementor'},{title : 'SE类型', dataName : 'type'},{title : '芯片类型', dataName : 'coreType'},{title : 'JAVA版本', dataName : 'javaVersion'},{title : 'cms2ac版本',dataName : 'cms2acVersion'},{title : '初始内存空间(Byte)',dataName : 'totalRamSize'},{title : '初始存储空间(Byte)',dataName : 'totalRomSize'}],
        	headerText : '关联应用版本 '
        });
	},
	createAppversGird : function(cbiid){
		var CBM = this;
		new JIM.UI.WinGrid({
			url : ctx + '/html/appVer/?m=findUnLinkPage&cardBaseId=' + cbiid[0],
        	multipleSelection: true,
        	order : false,
        	width : 800,
        	height : 330,
        	winButtons : [{
				title: '提交',
				color : 'blue',
				event: function() {
					if(this.selectIds!=''){
						CBM.submit(cbiid,this.selectIds,this);
					}else{
						new LightFace.MessageBox().error("还未选择应用版本");
					}
				}
			},{
				title: '取消',
				event: function() { this.close(); }
			}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '应用名称', dataName : 'application_name'}, {title : '版本号', dataName : 'versionNo'}, {title : '状态', dataName : 'status'}, {title : '占用内存空间(Byte)', dataName : 'volatileSpace'}, {title : '占用存储空间(Byte)', dataName : 'nonVolatileSpace'},{title:"预置模式",select:true,item:[{ key :'空卡模式' ,value: 1},{key :'实例创建模式' ,value:2},{key :'个人化模式',value :3}]}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '关联应用版本 '
		});
	},
	listGrid : function(id){
		var CBM = this;
		new JIM.UI.WinGrid({
			url : ctx + '/html/cardbaseapp/?m=Index&search_ALIAS_cardBaseL_EQL_id=' + id,
        	multipleSelection: false,
        	order : false,
        	width : 600,
        	height : 330,
        	winButtons : [{
				title: '删除',
				color : 'blue',
				event: function() { 
					if(this.selectIds!=''){
						CBM.delLink(this.selectIds,this);
					}else{
						new LightFace.MessageBox().error("您还未选择应用版本");
					}
				}
			}  ,
			{
				title: '更改预置模式',
				color : 'blue',
				event: function() { 
					if(this.selectIds!=''){
						CBM.changePreSet(this.selectIds,this);
					}else{
						new LightFace.MessageBox().error("您还未选择应用版本");
					}
				}
			},
    	 {
			title: '退出',
			event: function() { this.close();}
		}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '批次名称', dataName : 'cardBase_name'}, {title : '批次号', dataName : 'cardBase_batchNo'}, {title : '应用名称', dataName : 'applicationVersion_application_name'}, {title : '应用版本', dataName : 'applicationVersion_versionNo'},{title:"预置模式",dataName:'presetMode', select:true,item:[{ key :'空卡模式' ,value: 1},{key :'实例创建模式' ,value:2},{key :'个人化模式',value :3}]}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '已关联应用版本信息'
		});
	},
	changePreSet : function(cbaId,grid){
					new LightFace.MessageBox({
						onClose : function(){
							if(this.result){
								new Request.JSON({
									url : ctx + '/html/cardbaseapp/?m=changePrest&cbaId=' + cbaId[0],
									onSuccess : function(data){
										if(data.success){
											grid.load();
											new LightFace.MessageBox().info(data.message);
										}else{
											new LightFace.MessageBox().error(data.message);
										}
									}
								}).get();
							}
						}
					}).confirm("您确定要修改预置模式吗？");
	},
	delLink : function(cbaId,winGrid){
		new LightFace.MessageBox({
			onClose : function(){
				if(this.result){
					new Request.JSON({
						url : ctx + '/html/cardbaseapp/?m=delLink&cbaId=' + cbaId[0],
						onSuccess : function(data){
							if(data.success){
								winGrid.load();
								new LightFace.MessageBox().info(data.message);
							}else{
								new LightFace.MessageBox().error(data.message);
							}
						}
					}).get();
				}
			}
		}).confirm("您确认要删除此关联关系吗？");
	},
	submit : function(id,ids,winGrid){
		var idsS = ids.join(",");
		new Request.JSON({
			url : ctx + '/html/cardbaseapp/?m=cardBaseDoLink',
			onSuccess : function(data){
				if(data.success){
					winGrid.close();
					new LightFace.MessageBox().info(data.message);
				}else{
					new LightFace.MessageBox().error(data.message);
				}
			}
		}).post({
			'appverids' : idsS,
			'cardbaseId' : id
		});
	}
});


CardBaseManager.list = new Class({
	options : {},
	initialize : function() {
		this.drawGrid();
		this.drawCardBaseBox();
	},
	drawCardBaseBox : function() {
		var cbm = this;
		this.box = new LightFace( {
			width : 800,
			draggable : true,
			initDraw : false,
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
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/cardbaseinfo/?m=index',
			multipleSelection : false,
			buttons : [ {
				name : '新增',
				icon : ctx + '/admin/images/icon_4.png',
				handler : function() {
					this.box.options.title = '新增批次';
					this.box.options.titleImage = ctx + '/admin/images/icon_4.png';
					this.box.options.content = $('cardBaseDivAdd').get('html');
					this.box.draw();
					this.box.addEvent('open', this.openNewCardBase.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}, {
				name : '修改',
				icon : ctx + '/admin/images/page_white_edit.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的批次');
						return;
					}
					this.box.options.title = '修改批次';
					this.box.options.titleImage = ctx + '/admin/images/page_white_edit.png';
					this.box.options.content = $('cardBaseDivAdd').get('html');
					this.box.draw();
					this.box.addEvent('open', this.openEditCardBase.bind(this));
					this.box.open();
					this.box.removeEvents('open');
				}.bind(this)
			}, {
				name : '删除',
				icon : ctx + '/admin/images/delete.png',
				handler : function() {
					var selectIds = this.grid.selectIds;
					if (!$chk(selectIds) || selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的批次');
						return;
					}
						new LightFace.MessageBox( {
							onClose : function(result) {
								if (result) {
									new Request.JSON( {
										url : ctx + '/html/cardbaseinfo/?m=remove',
										onSuccess : function(data) {
											if (data.success) {
												new LightFace.MessageBox( {onClose : function() {this.grid.load();}.bind(this)}).info(data.message);
											} else {
												new LightFace.MessageBox().error(data.message);
											}
										}.bind(this)
									}).post( {
										'cbiId' : this.grid.selectIds[0]
									});
								}
							}.bind(this)
						}).confirm('确认要将该记录删除吗？');
					
				}.bind(this)
			} ],
        	searchButton : true,
        	columnModel : [{dataName : 'id', identity : true}, {title : '批次编号', dataName : 'batchNo'},{title : '批次名称', dataName : 'name'},{title : 'SE卡商', dataName : 'osImplementor'},{title : 'SE类型', dataName : 'type'},{title : '芯片类型', dataName : 'coreType'},{title : 'JAVA版本', dataName : 'javaVersion'},{title : 'cms2ac版本',dataName : 'cms2acVersion'},{title : '初始内存空间(Byte)',dataName : 'totalRamSize'},{title : '初始存储空间(Byte)',dataName : 'totalRomSize'}],
			headerText : '卡批次管理',
			searchBar : {filters : [{title : '批次编号：', name : 'search_LIKES_batchNo', type : 'text',width :150},{title : '批次名：', name : 'search_LIKES_name', type : 'text',width :150}]},
			image : {
				header : ctx + '/images/user_icon_32.png'
			}
		});
	},
	openNewCardBase : function() {
		this.form = this.box.messageBox.getElement('form');
		this.form.set('action', ctx + '/html/cardbaseinfo/?m=add');
		this.addValidate();
	},
	addValidate : function() {
		var fc = new FormCheck(this.form, {
			submit : false,
			display : {
				showErrors : 1,
				errorsLocation : 1,
				indicateErrors : 1,
				keepFocusOnError : 0,
				closeTipsButton : 0,
				scrollToFirst : true,
				removeClassErrorOnTipClosure : 1
			},
			onValidateSuccess : function() {//校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
		this.fc = fc;
	},
	openEditCardBase : function() {
		var selectIds = this.grid.selectIds;
		this.form = this.box.messageBox.getElement('form');
		var myBox = this.box;
		this.form.set('action', ctx + '/html/cardbaseinfo/?m=modify');
		this.addValidate();
		new Request.JSON( {
			url : ctx + '/html/cardbaseinfo/?m=getCardBase&cbiId=' + selectIds[0],
			asyn : false,
			onSuccess : function(data) {
				if (data.success) {
					var inputs = this.box.messageBox.getElements('input,select,textarea');
					$each(inputs, function(input, i) {
						var inputName = input.get('name');
						var value = data.message[inputName];
						input.set('value', value);
					});
//					myBox.messageBox.getElement('[name=platformType]').set('value',data.message['platformTypeOriginal']);
//					myBox.messageBox.getElement('[name=osPlatform]').set('value',data.message['osPlatformOriginal']);
					myBox.messageBox.getElement('[name=type]').set('value',data.message['typeOriginal']);
					var startNo = data.message['startNo'];
					startNo = startNo.substr(0,startNo.length-1);
					var endNo = data.message['endNo'];
					endNo = endNo.substr(0,endNo.length-1);
					myBox.messageBox.getElement('[name=startNo]').set('value',startNo);
					myBox.messageBox.getElement('[name=endNo]').set('value',endNo);
					myBox.messageBox.getElement('[name=osImplementor]').set('value',data.message['osImplementorOriginal']);
					if(data.message.publishCard){
					    myBox.messageBox.getElement('[name=startNo]').set('disabled','disabled');
					    myBox.messageBox.getElement('[name=endNo]').set('disabled','disabled');
					    myBox.messageBox.getElement('[name=cardKeyVersion]').set('disabled','disabled');
					    var startInput = new Element('input',{
						'type' : 'hidden',
						'name' : 'startNo',
						'value' : startNo
					    });
					    var endInput = new Element('input',{
						'type' : 'hidden',
						'name' : 'endNo',
						'value' : endNo
					    });
					    var keyInput = new Element('input',{
							'type' : 'hidden',
							'name' : 'cardKeyVersion',
							'value' : data.message['cardKeyVersion']
						    });
					    var form = myBox.messageBox.getElement('form');
					    startInput.inject(form); endInput.inject(form);keyInput.inject(form);
					}
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).get();
	},
	addValidate : function() {
		new FormCheck(this.form, {
			submit : false,
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
	    var startv = this.form.getElement('[name=startNo]').get('value');
	    var endv = this.form.getElement('[name=endNo]').get('value');
	    var cc1 = startv.substr(6,2);
	    var cc2 = endv.substr(6,2);
	    if(cc1 != cc2){
	    	new LightFace.MessageBox().error('起始和终止SEID的卡商编号必须一样');
	    	 return false;
	    }
	    var osImplementor = this.form.getElement('[name=osImplementor]').get('value');
	    if(osImplementor != cc1 || osImplementor!=cc2){
	    	new LightFace.MessageBox().error('SEID中厂商编码须和选择的SE厂商一致');
	    	 return false;
	    }
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.box.close();
							this.grid.load();
							this.grid.selectIds = [];
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.form.toQueryString());
	}
});