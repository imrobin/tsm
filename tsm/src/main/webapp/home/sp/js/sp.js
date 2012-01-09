var ServiceProvider = ServiceProvider ? ServiceProvider : {};
var firstLoad = true;
//spinfo.jsp
ServiceProvider.detailInfo = new Class({
	options : {
		url : '',
		spId : ''
	},
	initialize : function(options) {
		this.options.spId = options.spId;
		this.load();
	},
	load : function() {
		new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/',
			onSuccess : function(result) {
				var object = result;
				if(object.success) {
					var sp = object.message;
					
					var imgSrc = sp.hasLogo ? ('/html/spBaseInfo/?m=loadSpFirmLogo&id='+sp.id) : '/images/defsp.jpg';
					imgSrc = ctx + imgSrc;
					new Element('img', {src : imgSrc, styles : {width:'80px', height:'80px'}}).inject($('pimg'));

					var html = '名称：'+sp.name + '<br/>';
					html += '邮箱：'+sp.sysUser_email + '<br/>';
					html += '地址：' + sp.address + '<br/>';
					$('column_a').set('html', html);
					
					var summary = sp.spSummary == undefined ? '' : sp.spSummary;
					$('summary').appendText(summary);
				}
			}
		}).post('m=getSp&spId='+this.options.spId);
		
		new JIM.UI.Paging({
            url:ctx + '/html/spBaseInfo/?m=getAppListWithSp&spId='+this.options.spId,
            limit:5,
            head:{el:'nextpage', showNumber: true, showText : false},
            onAfterLoad: this.onComplete.bind(this)
        }).load();
	},
	onComplete : function(result) {
		this.cleanData();
		result.result.forEach(function(e, index) {
			var name = DataLength(e.name,25);
			var imgSrc;
			if (e.hasIcon){
				imgSrc = '<img  src="'+ctx+'/html/application/?m=getAppPcImg&appId='+e.id+'" style="border:0px;height: 78px; width: 78px;"/>';
			}else{
				imgSrc = '<img  src="'+ ctx + '/images/defApp.jpg" style="border:0px;height: 78px; width: 78px;"/>';
			}
			var dd = '<a href = "'+ctx+'/home/app/appinfo.jsp?id='+e.id+'" title="'+e.name+'">'+imgSrc+'<p>'+name+'</p></a>';
		//	new Element('dd', {html : dd}).inject($('cont'));
			new Element('dd').set('html',dd).inject($('cont'));
		});
	},
	cleanData : function() {
		$('cont').set('html', '');
	}
});

//spindex.jsp
ServiceProvider.index = new Class({

	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.load();
	},
	load : function() {
		var url =  ctx + "/html/spBaseInfo/?m=indexForWebSite&search_EQI_status=1&search_NEI_inBlack=1";
		if (name != null && name != ''){
			url =  ctx + "/html/spBaseInfo/?m=advanceSearch&name="+encodeURIComponent(name);
		}
        new JIM.UI.Paging({
            url:url,
            limit:3,
            head:{el:'nextpage', showNumber: true, showText : false},
            onAfterLoad: this.onComplete.bind(this)
        }).load();		
	},
	onComplete : function(result) {
		if (firstLoad){
			new Element('b',{'id':'totalapp'}).set('html','总共有'+result.totalCount+'个提供商').inject($('nextpage'),'top');
			firstLoad = false;
		}
		this.cleanData();
		
		result.result.forEach(function(e, index) {
			
			var name = '';
			var shortName = e.shortName === undefined ? '' : e.shortName;
			var imgSrc = (e.hasLogo == "有") ? ('/html/spBaseInfo/?m=loadSpFirmLogo&id='+e.id) : '/images/default_sp_logo.png';
			var spDetailUrl = ctx + '/home/sp/spinfo.jsp?id='+e.id;
			
			var dl = '<dl>';
			var dt = '<dt><img width="136" height="136" src="'+ ctx + imgSrc +'"/></dt>';
			var dd = '<dd>';
			dd += '<div class="applisttitle"><h1 class="left">企业简称：'+shortName+'</h1><span class="left"></span><span class="applisttitletj">应用数量：'+e.availableApplicationSize+'</span></div>';
			dd += '<p class="applistinfo"></p><p class="applistmible">企业名称：'+e.name+'</p><p class="applistbutton"><a href="'+spDetailUrl+'"><img  style="border:0px" src="'+ctx+'/images/button_1.gif" /></a></p>';
			dd += '</dd>';
			
			dl += dt + dd;
			dl += '</dl>';
			
			var html = dl;
			new Element('div', {'class' : 'applist'}).set('html',html).inject($('cont'));
		});
		if (result.totalCount == 0){
			var html = "<dl><dt>无符合条件的结果!</dd></dl>";
			new Element('div', {'class' : 'applist'}).set('html',html).inject($('cont'));
		}
	},
	cleanData : function (){
		$('cont').set('html', '');
	}
});

//listSpRequistion.jsp
ServiceProvider.requistion = new Class({
	options : {},
	initialize : function(options) {
		this.spId = '';
		var currentUser = new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getCurrentSp',
			onSuccess : function(result) {
				if(result.success) {
					var sp = result.message;
					this.spId = sp.id;
				} else {
					new LightFace.MessageBox({
						onClose : function() {
							self.location = ctx + '/html/login/';
						}
					}).error('用户未登录');
				}
			}.bind(this)
		});
		currentUser.post({t : new Date().getTime()});
		
		this.drawSpAddTable();
		this.drawSpModifyTable();
		
		this.dialog = new LightFace.MessageBox({
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			title : '应用提供商申请详细信息',
			//content : $('form').get('html'),
			buttons : [{
				title : '关闭', 
				event : function() {
					var div = $('container').getSiblings('div');
					this.box.dispose();
					if($chk(div)) {
						$each(div, function(e, index) {e.dispose();});
					}
					this.close();
				}
			}]
		});
		
	},
	drawSpAddTable : function() {
		var headers = ['序号','申请时间','处理时间','申请类型','审核状态','审核结果','审核意见', '操作'];
		//SP审核信息
		var tableSp = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : headers
		});
		tableSp.inject($('gridSp1'));
		
		var paging = new JIM.UI.Paging({
			url : ctx + '/html/requistion/?m=list&search_EQL_originalId='+this.spId+'&search_EQI_type=31&page_orderBy=id_desc&t='+new Date().getTime(),
			limit : 3,
			head : {el : 'sp1nextpage', showNumber : true, showText : false},
			onAfterLoad: function(result) {
				tableSp.empty();
				var whitespace = '&nbsp;';
				result.result.forEach(function(e, index) {
        			
        			var reviewDate = $chk(e.reviewDate) ? e.reviewDate : whitespace;
					var pass = $chk(e.result) ? e.result : whitespace;
					//if(pass != whitespace) pass = (e.requistion_result == 0) ? '不通过' : '通过';
					var opinion = $chk(e.opinion) ? e.opinion : whitespace;
					var operation = new Element('a', {text : '查看', href : '#', 'class' : 'b'});
					operation.addEvent('click', function() {
						this.info(e.id);
					}.bind(this));
					tableSp.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : 'center'}},
					              {content : e.submitDate, properties : {align : 'center'}},
					              {content : reviewDate, properties : {align : 'center'}},
					              {content : e.type, properties : {align : 'center'}},
					              {content : e.status, properties : {align : 'center'}},
					              {content : pass, properties : {align : 'center'}},
					              {content : opinion, properties : {align : 'center'}},
					              {content : operation, properties : {align : 'center'}}]);
        		}.bind(this));
			}.bind(this)
		});
		paging.load();
	},
	drawSpModifyTable : function() {
		var headers = ['序号','申请时间','处理时间','申请类型','审核状态','审核结果','审核意见', '操作'];
		var tableSp2 = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : headers
		});
		tableSp2.inject($('gridSp2'));
		
		var paging2 = new JIM.UI.Paging({
			url : ctx + '/html/requistion/?m=list&search_EQL_originalId='+this.spId+'&search_EQI_type=32&page_orderBy=id_desc&t='+new Date().getTime(),
			limit : 3,
			head : {el : 'sp2nextpage', showNumber : true, showText : false},
			onAfterLoad: function(result) {
				tableSp2.empty();
				var whitespace = '&nbsp;';
				result.result.forEach(function(e, index) {
        			
        			var reviewDate = $chk(e.reviewDate) ? e.reviewDate : whitespace;
					var pass = $chk(e.result) ? e.result : whitespace;
					//if(pass != whitespace) pass = (e.requistion_result == 0) ? '不通过' : '通过';
					var opinion = $chk(e.opinion) ? e.opinion : whitespace;
					var operation = new Element('a', {text : '查看', href : '#', 'class' : 'b'});
					operation.addEvent('click', function() {
						this.info(e.id);
					}.bind(this));
					tableSp2.push([{content : (paging2.getPageNo() * paging2.getLimit() + index + 1), properties : {align : 'center'}},
					              {content : e.submitDate, properties : {align : 'center'}},
					              {content : reviewDate, properties : {align : 'center'}},
					              {content : e.type, properties : {align : 'center'}},
					              {content : e.status, properties : {align : 'center'}},
					              {content : pass, properties : {align : 'center'}},
					              {content : opinion, properties : {align : 'center'}},
					              {content : operation, properties : {align : 'center'}}]);
        		}.bind(this));
			}.bind(this)
		});
		paging2.load();
	},
	info : function(id) {
		this.dialog.options.content = $('form').get('html');
		this.dialog.addEvent('open', function() {
			new Request.JSON({
				async : false,
				url : ctx + '/html/spBaseInfo/?m=getSpApply',
				onSuccess : function(data) {
					if(data.success) {
						var sp = data.message;
						var inputs = this.dialog.messageBox.getElements('input,select,radio');
						inputs.each(function(input, index) {
							if(input.get('type') == 'radio') {
								if(input.get('value') == data.message[input.get('name')]) {
									input.set('checked', 'checked');
									input.set('disabled','disabled');
								}
							} else if(input.get('type') == 'select-one') {
								input.set('value', data.message[input.get('name')]);
								input.set('disabled','disabled');
							} else if(input.get('name') == 'locationNo') {
								input.set('value', data.message[input.get('name')]);
								input.set('readonly','readonly');
							} else {
								input.set('value', data.message[input.get('name')]);
								input.set('readonly','readonly');
							}
							
							if(input.get('name') == 'type') {
								var options = input.getElements('option');
								input.getElements('option').each(function(e, index) {
									if(e.get('value') == data.message['typeOriginal']) {
										e.set('selected', 'selected');
									}
								});
							}
						});
						
						//hasLogo
						var firmLogo = this.dialog.messageBox.getElement('img[name=firmLogo]');
						var src = ctx + '/images/default_sp_logo.png';
						firmLogo.set("styles", {width: '85px',height: '85px'});
						if(sp.hasLogo) {
							src = ctx + '/html/spBaseInfo/?m=loadSpApplyFirmLogo&id=' + sp.id;
						}
						firmLogo.set('src', src);
					}
				}.bind(this)
			}).post({spId : id});
		}.bind(this));
		this.dialog.open();
		this.dialog.removeEvents('open');
		
	}
});

ServiceProvider.SubscribeHistory = new Class({
	initialize : function() {
		this.paging = {};
		this.drawGrid();
		$('submitBtn').addEvent('click', function() {
			new FormCheck($('queryform'), {
				submit : false,
				trimValue : false,
				display : {
					showErrors : 1,
					errorsLocation : 1,
					indicateErrors : 1,
					keepFocusOnError : 0,
					closeTipsButton : 1,
					removeClassErrorOnTipClosure : 1

				},
				onValidateSuccess : function() {
					$('grid').empty();
					$('nextpage').empty();
					this.grid = new HtmlTable({
						properties: {
					        border: 0,
					        cellspacing: 0,
					        style : 'width: 100%'
					    },
					    headers : ['序号','SEID','用户名称','手机号码','应用名称','应用版本','订购时间','退订时间']
					});
					this.grid.inject($('grid'));
					
					var url = ctx + '/html/subscribehistory/?m=list&sp=1&page_orderBy=subscribeDate_desc';
					
					if($chk($('nickName'))) {
						url += '&nickName=' + $('nickName').get('value');
					}
					if($chk($('customerCardInfo_mobileNo'))) {
						url += '&customerCardInfo_mobileNo=' + $('customerCardInfo_mobileNo').get('value');
					}
					if($chk($('subscribeDate'))) {
						url += '&subscribeDate=' + $('subscribeDate').get('value');
					}
					
					paging = new JIM.UI.Paging({
						url : url,
						limit : 10,
						head : {el:'nextpage', showNumber: true, showText : false},
						onAfterLoad : function(data) {
							this.grid.empty();
							
							data.result.forEach(function(row, index) {
								var whitespace = '&nbsp;';
								var name = row.customerCardInfo_customer_nickName ? row.customerCardInfo_customer_nickName : whitespace;
								var subscribeDate = row.subscribeDate ? row.subscribeDate : whitespace;
								var unsubscribeDate = row.unsubscribeDate ? row.unsubscribeDate : whitespace;
								var seid = row.customerCardInfo_card_cardNo ? row.customerCardInfo_card_cardNo : whitespace;
								this.grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
								           {content : seid, properties : {align : "center"}}, 
								           {content : name, properties : {align : "center"}}, 
								           {content : row.customerCardInfo_mobileNo, properties : {align : "center"}}, 
								           {content : row.applicationVersion_application_name, properties : {align : "center"}}, 
								           {content : row.applicationVersion_versionNo, properties : {align : "center"}}, 
								           {content : subscribeDate,    properties : {align : "center"}}, 
								           {content : unsubscribeDate, properties : {align : "center"}}]);
							}.bind(this));
						}.bind(this)
					});
					paging.load();
				}
			});
		}.bind(this));
	},
	drawGrid : function() {
		this.grid = new HtmlTable({
			properties: {
		        border: 0,
		        cellspacing: 0,
		        style : 'width: 100%'
		    },
		    headers : ['序号','SEID','用户名称','手机号码','应用名称','应用版本','订购时间','退订时间']
		});
		this.grid.inject($('grid'));
		
		var url = ctx + '/html/subscribehistory/?m=list&sp=1&page_orderBy=subscribeDate_desc';
		paging = new JIM.UI.Paging({
			url : url,
			limit : 10,
			head : {el:'nextpage', showNumber: true, showText : false},
			onAfterLoad : function(data) {
				this.grid.empty();
				
				data.result.forEach(function(row, index) {
					var whitespace = '&nbsp;';
					var name = row.customerCardInfo_customer_nickName ? row.customerCardInfo_customer_nickName : whitespace;
					var subscribeDate = row.subscribeDate ? row.subscribeDate : whitespace;
					var unsubscribeDate = row.unsubscribeDate ? row.unsubscribeDate : whitespace;
					var seid = row.customerCardInfo_card_cardNo ? row.customerCardInfo_card_cardNo : whitespace;
					this.grid.push([{content : (paging.getPageNo() * paging.getLimit() + index + 1), properties : {align : "center", width : "30px"}}, 
					           {content : seid, properties : {align : "center"}}, 
					           {content : name, properties : {align : "center"}}, 
					           {content : row.customerCardInfo_mobileNo, properties : {align : "center"}}, 
					           {content : row.applicationVersion_application_name, properties : {align : "center"}}, 
					           {content : row.applicationVersion_versionNo, properties : {align : "center"}}, 
					           {content : subscribeDate,    properties : {align : "center"}}, 
					           {content : unsubscribeDate, properties : {align : "center"}}]);
				}.bind(this));
			}.bind(this)
		});
		paging.load();
	}
});

ServiceProvider.SummaryEdit = new Class({
	initialize : function() {
		this.summary = $('textarea-1');
		this.spId = {};
//		this.summary = $('textarea-1').mooEditable({
//			toolbar : false
//		});
		
		new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/?m=getCurrentSp',
			onSuccess : function(result) {
				if(result.success) {
					var sp = result.message;
					var spSummary = sp.spSummary == undefined ? '' : sp.spSummary;
					//var content = '《实习医生格蕾》是一部以医学为主题，在美国十分受欢迎的黄金时段电视系列剧。该剧由Ellen Pompeo领衔主演，于2005年3月27日在美国广播公司首播。本剧聚焦在一群年轻人努力成为医生，而医生努力保持人性上。在高强度训练医生的同时又掺杂了大量的喜剧和性元素，揭示出在实习医生们痛苦的生活中，无论是药物还是人际关系，都不能简单地用白纸黑字来定义，真正的生活其实像灰色的阴影一样。';
					this.summary.set('value',spSummary);
					this.spId = sp.id;
				} else {
					new LightFace.MessageBox({
						onClose : function() {
							self.location = ctx + '/html/login/';
						}
					}).error('用户未登录');
				}
			}.bind(this)
		}).post();
		
		$('saveBtn').addEvent('click', function() {
			var content = this.summary.get('value');
			if(content.length > 1000) {
				new LightFace.MessageBox().error('内容超过1000字');
			} else {
				new Request.JSON({
					async : false,
					url : ctx + '/html/spBaseInfo/?m=editSpSummary',
					onSuccess : function(result) {
						if(result.success) {
							new LightFace.MessageBox({
								onClose : function() {
									self.location = ctx + '/home/sp/center.jsp';
								}
							}).info(result.message);
						} else {
							new LightFace.MessageBox().error(result.message);
						}
					}
				}).post({spId : this.spId, spSummary : this.summary.get('value')});
			}
			
		}.bind(this));
	}
});

/* ------------------------------------------------------------------------------------- */

	function checkFullName(el) {
		var orgVal = '';
		if($('nameOrg') != null) orgVal = $('nameOrg').get('value');
		return validateItem(el, 'name', '企业名称', orgVal);
	}
	
	function checkShortName(el) {
		var orgVal = '';
		if($('shortNameOrg') != null) orgVal = $('shortNameOrg').get('value');
		return validateItem(el, 'shortName', '企业简称', orgVal);
	}
	
	function checkRegistrationNo(el) {
		var orgVal = '';
		if($('registrationNoOrg') != null) orgVal = $('registrationNoOrg').get('value');
		return validateItem(el, 'registrationNo', '工商注册编号', orgVal);
	}
	
	function checkCertificateNo(el) {
		var orgVal = '';
		if($('certificateNoOrg') != null) orgVal = $('certificateNoOrg').get('value');
		return validateItem(el, 'certificateNo', '经营许可证编号', orgVal);
	}
	
	function checkLegalPersonIdNo(el) {
		var orgVal = '';
		if($('legalPersonIdNoOrg') != null) orgVal = $('legalPersonIdNoOrg').get('value');
		return validateItem(el, 'legalPersonIdNo', '法人证件号码', orgVal);
	}
	
	function checkContactPersonMobileNo(el) {
		var orgVal = '';
		if($('contactPersonMobileNoOrg') != null) orgVal = $('contactPersonMobileNoOrg').get('value');
		return validateItem(el, 'contactPersonMobileNo', '联系人手机号', orgVal);
	}
	
	function illegalCharsCheck(el) {
		if(el.value == '') return true;
		if (!el.value.test(/^[A-Za-z0-9]+$/)) {
	        el.errors.push("只能是数字或者字母");
	        return false;
	    } else {
	        return true;
	    }
	}
	
	function checkItem(el, itemName, itemType) {
		var bln = false;
		var url = ctx;
		var request = new Request({
			url : ctx + '/html/spBaseInfo/',
			async : false,
			onSuccess : function(responseText) {
				var object = JSON.decode(responseText);
				bln = object.success;
				if(!bln) {
					el.errors.push(itemName+el.value+"已经被使用");
				}
			},
			onFailure : function() {
		        el.errors.push(itemName+el.value+"已经被使用");
			}
		});
		request.post('m=checkName&type='+itemType+'&name=' + el.value);
		return bln;
	}
	
	function checkMobileInSp(el) {
		return validateItem(el, 'contactPersonMobileNo', '联系人手机号', '');
	}
	
	function validateItem(el, fieldName, fieldNameZh, orgValue) {
		//alert('newVal:'+el.value + '\norgVal:'+orgValue);
		var bln = false;
		new Request.JSON({
			async : false,
			url   : ctx + '/html/spBaseInfo/?m=validateField',
			onSuccess : function(result) {
				bln = result.message;
				if(!bln) {
					el.errors.push(fieldNameZh+el.value+"已经被使用");
				}
			}
		}).post({fieldName : fieldName, newValue : el.value, orgValue : orgValue, t : new Date().getTime()});
		return bln;
	}
	
	//前台网站页面HtmlTable用
	function addTip(id, content, width) {
		var span = null;
		if(width == undefined) width = '90px';
		span = new Element('span', {'class' : 'texthidden', style:'width:90px'});
		span.appendText(content);
		span.addEvent('mouseover', function(event) {
			div = new Element('div', {
				id : id,
				styles : {
					position:'absolute',
					border :'1px solid #A5CBDB',
				    background:'#F6F6F6',
				    padding:'1px',
				    color:'#333',
				    top : event.page.y + 'px',
				    left: event.page.x  + 'px',
				    'z-index' : '99999',
				    display:'none',
				    'word-break':'break-all',
				    'word-wrap': 'break-word',
				    'max-width': '200px'
				},
				html : content
			});
			div.inject(document.body, 'bottom');
			div.setStyle('display','');
		});
		span.addEvent('mouseout', function(event) {
			if($chk($(id))) $(id).dispose();
		});
		
		return span;
	}
	
	function applyInfo(id) {
		var tr = '';
		new Request.JSON({
			async : false,
			url : ctx + '/html/securityDomain/?m=sdApplyLoad',
			onSuccess : function(result) {
				if(result.success) {
					var sd = result.message;
					var tr  = '<tr><td align="right" width="200px">AID：</td><td align="left">'+sd.aid+'</td></tr>';
					    tr += '<tr><td align="right">名称：</td><td align="left">'+sd.sdName+'</td></tr>';
					    tr += '<tr><td align="right">安全等级：</td><td align="left">安全等级'+sd.scp02SecurityLevel+'</td></tr>';
					    var deleteRule = sd.deleteRule;
					    switch (sd.deleteRule) {
							case 0: 
								deleteRule = '自动删除';
								break;
							case 1: 
								deleteRule = '调用指令删除';
								break;
							case 2: 
								deleteRule = '不能删除';
								break;
							default:
								deleteRule = '自动删除';
								break;
						}
					    tr += '<tr><td align="right">删除规则：</td><td align="left">'+deleteRule+'</td></tr>';
					    tr += '<tr><td align="right">安全域自身的内存空间：</td><td align="left">'+($chk(sd.volatileSpace) ? sd.volatileSpace : 0)+'byte</td></tr>';
					    tr += '<tr><td align="right">安全域自身的存储空间：</td><td align="left">'+($chk(sd.noneVolatileSpace) ? sd.noneVolatileSpace : 0)+'byte</td></tr>';
					    
					    var privilege = '';
					    
					    if(sd.dap) {
						    privilege += 'DAP验证,';
					    } else if(sd.dapForce) {
						    privilege += '强制要求验证DAP,';
					    } else if(sd.token) {
						    privilege += '委托管理,';
					    } else if(sd.lockCard) {
					   	 	privilege += '锁定卡,';
					    } else if(sd.abandonCard) {
					    	privilege += '废止卡,';
					    } else if(sd.cvm) {
						    privilege += '管理卡CVM,';
					    }
					    
					    if(privilege.length == 0) privilege = '无';
					    
					    tr += '<tr><td align="right">权限：</td><td align="left">'+privilege+'</td></tr>';
					    
					    tr += '<tr><td align="right">安全域是否允许删除：</td><td align="left">'+(sd.ip.deleteSelf == 1 ? '接受' : '不接受')+'</td></tr>';
					    tr += '<tr><td align="right">安全域是否接受迁移：</td><td align="left">'+(sd.ip.transfer == 1 ? '接受' : '不接受')+'</td></tr>';
					    tr += '<tr><td align="right">是否接受主安全域发起的应用删除：</td><td align="left">'+(sd.ip.deleteApp == 1 ? '接受' : '不接受')+'</td></tr>';
					    
					    var spacePatten = $(sd.ip.managedVolatileSpace);
					    if(spacePatten) {
					    	//签约空间模式
						    tr += '<tr><td align="right">空间管理模式：</td><td align="left">签约空间模式</td></tr>';
						    tr += '<tr><td align="right">安全域管理的内存空间：</td><td align="left">'+($chk(sd.ip.managedVolatileSpace) ? sd.ip.managedVolatileSpace : 0)+'byte</td></tr>';
						    tr += '<tr><td align="right">安全域管理的存储空间：</td><td align="left">'+($chk(sd.ip.managedNoneVolatileSpace) ? sd.ip.managedNoneVolatileSpace : 0)+'byte</td></tr>';
					    } else {
					    	//应用大小管理模式
					    	tr += '<tr><td align="right">空间管理模式：</td><td align="left">应用大小管理模式</td></tr>';
					    }
					    
					    var scp = sd.ip.scp;
					    scp = 'SCP' + scp.replace(/,/, ' 0x');
					    tr += '<tr><td align="right">安全通道协议：</td><td align="left">'+scp+'</td></tr>';
					    tr += '<tr><td align="right">安全通道最大连续鉴权失败次数：</td><td align="left">'+sd.ip.maxFailCount+'</td></tr>';
					    tr += '<tr><td align="right">密钥版本号：</td><td align="left">'+sd.ip.keyVersion+'</td></tr>';
					    tr += '<tr><td align="right">安全域支持的最大对称密钥个数：</td><td align="left">'+sd.ip.maxKeyNumber+'</td></tr>';
					    
						var table = '<div class="minfo"><table border="0" cellspacing="0">'+tr+'</table></div>';
						new LightFace.MessageBox({title : sd.applyType, width : '100%'}).info(table);
					    
				}
			}
		}).post({sdid : id});
	}