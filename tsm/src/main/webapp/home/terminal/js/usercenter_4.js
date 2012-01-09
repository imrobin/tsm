App = {};
var hasChecked = new Array();
var cardNos = new Array();
var tab2first = true;
var tab3first = true;
var tab4first = true;
App.Index = new Class( {
	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.getConstants();
	},
	getConstants : function() {
		new Request.JSON({
			url : ctx + "/html/localtransaction/?m=exportConstant",
			onSuccess : function(json) {
				if (json.success) {
					this.transConstant = json.message;
				}
			}.bind(this)
		}).get();
	},
	loadTab1 : function() { 
		this.cleanData();
		var url =  ctx + "/html/localtransaction/?m=findDesiredOperationByCustomer&executionStatus=0&page_orderBy=id_desc";		
		var login = this;
        new JIM.UI.Paging({
            url:url,
            limit:10,
            head:{el:'nextpage1', showNumber: true, showText : false},
            onAfterLoad: this.onLoadTab1Complete.bind(this)
        }).load();		
	},		
	onLoadTab1Complete : function(result) {
		var cardApp = this;
		var html =''; 
		html += "<table width='700' border='0' cellpadding='0' cellspacing='0'><tr>" +
		"<td width='60'>选择</td>"+
		"<td width='106' align='center'>应用图标</td>" +
		"<td width='250' align='center'>应用名称</td> " +
		"<td width='106' align='center'>操作类型</td>" +
		"<td width='106' align='center'>操作终端</td>" +
		"<td width='116' align='center'>操作</td>";
	result.result.forEach(function(e, index) {
		var iconSrc = initSrc(e);
		var cardNo;
		if(e.cardNo == ""){
			cardNo = "down";
		}else{
			cardNo = e.cardNo;
		}
		html += 
		"<tr>" +
		"<td ><input type='checkbox' name='tab1' value='"+e.id+"' id='"+e.id+"' lang='" + cardNo + "'/></td>" +
		"<td width='60' align='center'>"+iconSrc+"</td>" +
		"<td width='140' align='center'>"+e.appName+"</td> " +
		"<td width='106' align='center'>"+e.procedureName+"</td>" +
		"<td width='106' align='center'>"+e.cciName+"</td>" +
		"<td width='136' align='center'><a class='buts' id='execute"+e.id+"' href='javascript:execute("+e.id+")'>执行</a>&nbsp;&nbsp;&nbsp;" +
				"<a class='buts' id='cancel"+e.id+"'  href='javascript:cancel("+e.id+")' name='cancel'>取消</a></td>" ;
	});
		if(result.result.length != 0){
			html += "<tr>" +
			"<td><input type='checkbox' name='checkbox32' value='checkbox'  id='selectAll'/></td>" +
			"<td>全选</td>" +
			"<td align='center'>&nbsp;</td><td align='center'>&nbsp;</td>" +
			"<td align='center'>&nbsp;</td>" +
			"<td align='center'><a class='buts' id='executeall' href='javascript:void(0)'>执行</a></td>" +
			"<td>&nbsp;</td>" +
			"</tr></table>";
		}else{
			html = '<div class="noinfo" id="tip1"><img src="' + ctx +'/images/no.gif" width="36" height="33" />无符合条件的结果</div>';
		}
		$('content').set('html', html);

		$('tab1Sum').set('text', '('+(result.totalCount == null ? 0:result.totalCount)+')');
		if(result.result.length != 0){
			$('selectAll').addEvent('click', function(){
				hasChecked = new Array();
				cardNos = new Array();
				var first = true;
				var lastCardNo = '';
				var flag = true;
			    if ($('selectAll').get('checked')){
			    	$$("input[name='tab1']").forEach(function(e){
			    		if(e.lang != "down"){
			    			if(!first){
				    			if( e.lang != lastCardNo){
				    					flag =  false;
				    			}
				    		}
				    		first = false;
				    		lastCardNo = e.lang;
			    		}
			    	});
			    	 if(!flag){
			    		 	$('selectAll').set('checked','');
					    	new LightFace.MessageBox().error("因为包含有不同终端的任务，不能全选");
					  }else{
						  $$("input[name='tab1']").forEach(function(e){
							  var btns = e.getParent().getParent().getElements("a[class='buts']");		
							  Array.each(btns,function(item,index){
					    			cardApp.disableAnchor(item,true);
					    		});
					    				hasChecked.push(e.id);
							    		e.set('checked',true);
						  });
				    		cardNos.push(lastCardNo);
					  }
					//	alert("hasChecked="+hasChecked);
					//	alert("cardNos="+cardNos);
			    }else{
			    	$$("input[name='tab1']").forEach(function(e){
			    		e.set('checked',false);
			    		 var btns = e.getParent().getParent().getElements("a[class='buts']");		
						  Array.each(btns,function(item,index){
				    			cardApp.disableAnchor(item,false);
				    		});
			    	});		
					hasChecked = new Array();
					cardNos = new Array();
			    }
			});
		}
    	$$("input[name='tab1']").addEvent('click', function(e){
    		var btns = $(this).getParent().getParent().getElements("a[class='buts']");
			if ($(this).checked){
				Array.each(btns,function(item,index){
	    			cardApp.disableAnchor(item,true);
	    		});
				//hasChecked.push($(this).id);
				if($(this).lang != "down"){
					if(cardNos.length == 0){
						hasChecked.push($(this).id);
						cardNos.push($(this).lang);
					}else{
						if(cardNos[0] != $(this).lang){
							new LightFace.MessageBox({
								onClose : function() {
								Array.each(btns,function(item,index){
					    			cardApp.disableAnchor(item,false);
					    		});
							}
						}).error("批量操作必须选择同一终端的任务");
							hasChecked.remove($(this).id);
							e.stop();
						}else{
							hasChecked.push($(this).id);
							//cardNos.push($(this).lang);
						}
					}
				}
			}else{
				Array.each(btns,function(item,index){
	    			cardApp.disableAnchor(item,false);
	    		});
				hasChecked.remove($(this).id);
				if (hasChecked.length == 0){
					cardNos.remove($(this).lang);
				}
			}
		//	alert("hasChecked="+hasChecked);
		//	alert("cardNos="+cardNos);
		});    	
		if(result.result.length != 0){
	    	$('executeall').addEvent('click',function(e){
	    		e.stop;
	    		//alert(hasChecked);
	    		if(hasChecked.length == 0){
	    			new LightFace.MessageBox().error("尚未选择任何任务");
	    		}else{
	    			var cardNo = cardApp.checkReady();
	
	    			var doIds = hasChecked.join(",");
	    			new LightFace.MessageBox({
	    				onClose : function() {
	    					if (this.result) {
	    						if (cardNos[0] != cardNo){
			    					new LightFace.MessageBox().error("批量执行的终端与当前终端不符");
		    						}else{
			    						if($chk(cardNo)){
			    							new Request.JSON({
			    								url : ctx + '/html/dersireOpt/?m=getBatchDoInfo&doIds=' + doIds,
			    								onSuccess : function(resp){
			    									if(resp.success){
			    										var resultList = resp.result;
			    										if(resultList.length != 0 ){
			    											if (resp.message != ''){
			    											new Request.JSON({
			    												url : ctx + '/html/dersireOpt/?m=setCCiUseCardNo&cardNo=' + cardNo + '&doIds=' + resp.message,
			    												async : false,
			    												onSuccess : function(resp){
			    													if(resp.success){
			    													}else{
			    														new LightFace.MessageBox().error(resp.message);
			    													}
			    												}
			    											}).get();
		    											}
			    											new JIM.CardDriver({
			    												ctl : cardDriver,
			    												operations : resultList,
																onSuccess : function(response) {
												    			this.closeConnection();
				    											new Request.JSON({
				    												url : ctx + '/html/dersireOpt/?m=getBatExecuteResult&doIds=' + doIds,
				    												async : false,
				    												onSuccess : function(resp2){
	    			    												if(resp2.success){
	    													    			new LightFace.MessageBox({
	    													    			    onClose : function(){
	    													    			//	win.close();
	    													    					location.reload();
	    													    			    }
	    													    			}).info(resp2.message);
	    			    												}
				    												}
				    											}).get();
															},
															onFailure : function(){
																this.closeConnection();
				    											new Request.JSON({
				    												url : ctx + '/html/dersireOpt/?m=getBatExecuteResult&doIds=' + doIds,
				    												async : false,
				    												onSuccess : function(resp2){
	    			    												if(resp2.success){
	    													    			new LightFace.MessageBox({
	    													    			    onClose : function(){
	    													    			//	win.close();
	    													    					location.reload();
	    													    			    }
	    													    			}).error(resp2.message);
	    			    												}
				    												}
				    											}).get();
															}
			    											}).exec();
		    										}else{
		    											new LightFace.MessageBox().error("任务相关信息查询失败");
		    										}
		    									}
		    								}
		    							}).get();
		    						}
		    					}
	    					}
	    				}
	    			}).confirm("您要执行批量任务吗？");
	    		}
	    	});
		}
	},
	loadTab2 : function() { 
		this.cleanData();
		var url =  ctx + "/html/localtransaction/?m=findDesiredOperationByCustomer&executionStatus=1&page_orderBy=id_desc";		
		var login = this;
        new JIM.UI.Paging({
            url:url,
            limit:10,
            head:{el:'nextpage2', showNumber: true, showText : false},
            onAfterLoad: this.onLoadTab2Complete.bind(this)
        }).load();		
	},		
	onLoadTab2Complete : function(result) {
		var html =''; 
		if (!tab2first){
			html += "<table width='700' border='0' cellpadding='0' cellspacing='0'><tr>" +
				"<td width='60' align='center'>应用/安全域 图标</td>" +
				"<td width='160' align='center'>应用/安全域 名称</td> " +
				"<td width='106' align='center'>操作类型</td>" +
				"<td width='106' align='center'>操作终端</td>" +
				"<td width='150' align='center'>执行开始时间</td>" +
				"<td width='116' align='center'>状态</td>" +
				"<td width='242'>下载客户端</td></tr>";
			result.result.forEach(function(e, index) {
				var iconSrc = initSrc(e);
				html += "<tr>" +
				"<td width='60'>"+iconSrc+"</td>" +
				"<td width='160'  align='center'>"+e.appName+"</td> " +
				"<td width='106' align='center'>"+e.procedureName+"</td>" +
				"<td width='106' align='center'>"+e.cciName+"</td>" +
				"<td width='150' align='center'>"+e.beginTime+"</td>" +
				"<td width='116' align='center'><span class='doing'>执行中</span></td>" +
				"<td width='242'>&nbsp;</td>" +
				"</tr>";
			});
			if (result.result.length == 0) {
				html = '<div class="noinfo" id="tip1"><img src="' + ctx +'/images/no.gif" width="36" height="33" />无符合条件的结果</div>';
			}
			$('content').set('html', html);
		}
		$('tab2Sum').set('text', '('+(result.totalCount== null? 0:result.totalCount)+')');
		tab2first = false; 
	},
	loadTab3 : function() { 
		this.cleanData();
		var url =  ctx + "/html/localtransaction/?m=findDesiredOperationByCustomer&executionStatus=2&result=success&page_orderBy=id_desc";		
		var login = this;
        new JIM.UI.Paging({
            url:url,
            limit:10,
            head:{el:'nextpage3', showNumber: true, showText : false},
            onAfterLoad: this.onLoadTab3Complete.bind(this)
        }).load();		
	},		
	onLoadTab3Complete : function(result) {
		var html =''; 
		if (!tab3first){
			html += "<table width='700' border='0' cellpadding='0' cellspacing='0'><tr>" +
				"<td width='70' align='center'>应用/终端 图标</td>" +
				"<td width='160' align='center'>应用名称</td> " +
				"<td width='106' align='center'>操作类型</td>" +
				"<td width='106' align='center'>操作终端</td>" +
				"<td width='150' align='center'>执行开始时间</td>" +
				"<td width='150' align='center'>执行结束时间</td>" +
				"<td width='116' align='center'>状态</td>" +
				"<td width='100'>下载客户端</td></tr>";
			result.result.forEach(function(e, index) {
				var iconSrc = initSrc(e);
				var downButtonHtml;
				if (e.procedureName != '挂失终端' && e.hasClient){
					downButtonHtml = "<a class='buts' href=\"javascript:downloadClient('"+e.aid+"')\">下载</a>";
				} else {
					downButtonHtml = "";
				}
					html += "<tr>" +
					"<td width='70'>"+iconSrc+"</td>" +
					"<td width='160' align='center'>"+e.appName+"</td> " +
					"<td width='106' align='center'>"+e.procedureName+"</td>" +
					"<td width='106' align='center'>"+e.cciName+"</td>" +
					"<td width='150' align='center'>"+e.beginTime+"</td>" +
					"<td width='150' align='center'>"+e.endTime+"</td>" +
					"<td width='116' align='center'><span class='finish'>执行成功</span></td>" +
					"<td width='100' align='center'>"+downButtonHtml+"</td></tr>";
			});
			if (result.result.length == 0) {
				html = '<div class="noinfo" id="tip1"><img src="' + ctx +'/images/no.gif" width="36" height="33" />无符合条件的结果</div>';
			}
			$('content').set('html', html);
		}
		$('tab3Sum').set('text', '('+(result.totalCount== null? 0:result.totalCount)+')');
		tab3first = false;
	},
	loadTab4 : function() { 
		this.cleanData();
		var url =  ctx + "/html/localtransaction/?m=findDesiredOperationByCustomer&executionStatus=3&result=fail&page_orderBy=id_desc";		
		var login = this;
	      new JIM.UI.Paging({
	            url:url,
	            limit:10,
	            head:{el:'nextpage4', showNumber: true, showText : false},
	            onAfterLoad: this.onLoadTab4Complete.bind(this)
	        }).load();	
	},		
	onLoadTab4Complete : function(result) {
		var html =''; 
		if (!tab4first){
			html += "<table width='700' border='0' cellpadding='0' cellspacing='0'><tr>" +
				"<td width='70' align='center'>应用/终端 图标</td>" +
				"<td width='160' align='center'>应用名称</td> " +
				"<td width='106' align='center'>操作类型</td>" +
				"<td width='106' align='center'>操作终端</td>" +
				"<td width='150' align='center'>执行开始时间</td>" +
				"<td width='150' align='center'>执行结束时间</td>" +
				"<td width='116' align='center'>失败原因</td>" +
				"<td width='100'>状态</td></tr>";
			result.result.forEach(function(e, index) {
				var iconSrc = initSrc(e);
					html += 
					"<tr>" +
					"<td width='70'>"+iconSrc+"</td>" +
					"<td width='160'  align='center'>"+e.appName+"</td> " +
					"<td width='106' align='center'>"+e.procedureName+"</td>" +
					"<td width='106' align='center'>"+e.cciName+"</td>" +
					"<td width='150' align='center'>"+e.beginTime+"</td>" +
					"<td width='150' align='center'>"+e.endTime+"</td>" +
					"<td width='116' align='center'><span class='lose'>"+e.failMessage+"</span></td>" +
					"<td width='100'><span class='lose'>执行失败</span></td></tr>";
			});
			if (result.result.length == 0) {
				html = '<div class="noinfo" id="tip1"><img src="' + ctx +'/images/no.gif" width="36" height="33" />无符合条件的结果</div>';
			}
			$('content').set('html', html);
		}
		$('tab4Sum').set('text', '('+(result.totalCount == null? 0:result.totalCount) +')');
		tab4first = false;
	},
	cleanData : function (){
		$('content').set('html', '');
		if (!tab2first && !tab3first && !tab4first)
			$('nextpage1').set('html', '');
		
		$('nextpage2').set('html', '');
		$('nextpage3').set('html', '');
		$('nextpage4').set('html', '');
		hasChecked = new Array();
	},
	cancel : function (id){
		var obj = this;
		new LightFace.MessageBox({
			onClose : function() {
				if (this.result) {
					var url =  ctx + "/html/localtransaction/?m=cancel&id="+id;		
					var login = this;
					this.request = new Request.JSON( {
						url : url,
						onSuccess : obj.onTab1Complete.bind(obj)
					}).post();
				}
			}
		}).confirm("您要取消任务吗？");
	},
	execute : function (doId){
		var cardApp = this;
		var cardNo = this.checkReady();
//		alert(cardNo);
		new LightFace.MessageBox({
			onClose : function() {
				if (this.result) {
					if($chk(cardNo)){
						new Request.JSON({
							url : ctx + '/html/dersireOpt/?m=getDoInfo&doId=' + doId,
							onSuccess : function(resp){
								if(resp.success){
									var aid = resp.message.aid;
									var opt = resp.message.procedureInt;
									//alert("opt="+opt);
									if(resp.message.cardNo != cardNo){
										new LightFace.MessageBox().error("当前终端不对应此任务");
										return;
									}
									if(opt != 3){
										new JIM.CardDriver({
											ctl : cardDriver,
											operations : [ {
												aid : aid, 
												operation : opt
											} ],
											onSuccess : function(response) {
								    			this.closeConnection();
								    			new LightFace.MessageBox({
								    			    onClose : function(){
								    			//	win.close();
								    					location.reload();
								    			    }
								    			}).info("任务执行成功");
											},
											onFailure : function(){
												this.closeConnection();
												new LightFace.MessageBox({
												onClose : function(){
													location.reload();
													}
												}).error('请到未完成任务中查看原因');
											}
										}).exec();
									}else{
										new Request.JSON({
											url : ctx + '/html/dersireOpt/?m=setCCiUseCardNo&cardNo=' + cardNo + '&doIds=' + doId,
											async : false,
											onSuccess : function(resp){
												if(resp.success){
													new JIM.CardDriver({
														ctl : cardDriver,
														showMsg : false,
														operations : [{
															aid : aid, 
															operation : opt
														}],
														onSuccess : function(response) {
										    			this.closeConnection();
										    			new LightFace.MessageBox({
										    			    onClose : function(){
										    			//	win.close();
										    					location.reload();
										    			    }
										    			}).info("任务执行成功");
													},
													onFailure : function(){
														this.closeConnection();
														new LightFace.MessageBox({
														onClose : function(){
															location.reload();
															}
														}).error('请到未完成任务中查看原因');
													}
													}).exec();
												}else{
													new LightFace.MessageBox().error(resp.message);
												}
											}
										}).get();
									}
								}
							}
						}).get();
					}
				}
			}
		}).confirm("您要执行任务吗？");
	
	},
	downloadClient : function(aid){
		obj = this;
		var liString = "无手机终端信息";
		// var liString = '';
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=index&status=1&aid=" + aid,
			async : false,
			onSuccess : function(responseText, responseXML) {
				if (responseText.success) {
					if (responseText.result.length > 0) {
						liString = "";
						var firstId = responseText.result[0].id;
						$each(responseText.result, function(item, index) {
							var phoneName = item.name == '' ? mobileType_brandChs : item.name;
							liString += '<div class="user_m_l_2" id="li' + item.id + '">' + '<p class="user_m_img">'
									+ '<img onerror="javascript:this.src=\'' + ctx + '/images/defTerim.jpg'
									+ '\' "  src="/tsm/html/mobile/?m=getMobilePic&id=' + item.mobileType_id + '" />'
									+ '</p>' + '<p class="user_m_text">' + '名称  : ' + DataLength(phoneName, 13) + '<br />'
									+ '号码  : ' + item.mobileNo + '<br />' + '状态 : ' + item.status + '(' + item.active + ')'
									+ '<br/>' + '品牌 : ' + item.mobileType_brandChs + '<br /> ' + '机型 : '
									+ DataLength(item.mobileType_type, 13) + '<br /> ';
							if (item.clientStatusStr != '') {
								liString += ('<br/><b style="color:#ff0000">' + item.clientStatusStr + '</b></p></div>');
							} else {
								if (item.clientAndroidUrl != '') {
									liString += '<a  target="_blank" class="butswide m_t_5" href="' + 
									ctx+'/html/applicationClient/?m=downloadByHref&href='+encodeURIComponent(item.clientAndroidUrl)+'">Android</a>';
								}
								if (item.clientJ2MEUrl != '') {
									liString += '&nbsp;&nbsp;<a  target="_blank" class="butswide m_t_5" href="' + 
										ctx+'/html/applicationClient/?m=downloadByHref&href='+encodeURIComponent(item.clientJ2MEUrl)+'">J2ME</a>';
								}
							}
							liString += '</p></div>';
						});
					}
				} else {
					new LightFace.MessageBox().error(responseText.message);
				}
			}
		}).send();
		
		obj.appDownBox = new LightFace({
			content : '<div class="user_m_l_1" id="terminals" style="width:600px;">' + liString + '</div>',
			title : '选择客户端',
			buttons : [ {
				title : '关闭',
				event : function() {
					this.close();
				}
			} ]
		}).open();
	},
	checkReady : function(){
		var card = this;
		var cardNo = new JIM.CardDriver({
			ctl : cardDriver,
			operations : []
		}).readCardNo();
		return cardNo;
	},
	getDoIdByAidAndOpt : function(adi,doOpt,cardNo){
		new Request.JSON({
			url : ctx + '/html/dersireOpt/?m=getDoIdByAidAndOpt',
			onSuccess : function(data){
				if(data.success){
					return data.message;
				}else{
					return 0;
				}
				
			}
		}).post({
			'aid' : adi,
			'opt' : doOpt,
			'cardNo' :cardNo
		}); 
	}
	,
	
	disableAnchor : function(obj,disable){
		if (disable) {    
		//	obj.set('class','butsdisable');
			var href = obj.getAttribute("href");        
			if (href && href != "" && href != null) {           
				obj.setAttribute('href_bak', href);        
			}        
			obj.removeAttribute('href'); 
			var onclick = obj.getAttribute("onclick");  
			if(onclick != null){ 
				obj.setAttribute('onclick_bak', onclick); 
				obj.setAttribute('onclick', "void(0);");         
			}         
			obj.onclick = null;       
			obj.style.color = "gray";        
			obj.setStyle('display','none');
		}    else {       
			obj.setStyle('display','');
				if (obj.attributes['href_bak'])  {        
					obj.setAttribute('href', obj.attributes['href_bak'].nodeValue); 
				}
				if(obj.attributes['onclick_bak']!=null){
					obj.setAttribute('onclick', obj.attributes['onclick_bak'].nodeValue);     
				}             
				obj.removeAttribute('style');       
				obj.removeAttribute('disabled');   
		}			
	},
	onTab1Complete : function (result){
		if (result.success){
			new LightFace.MessageBox(
					 {
							onClose : function(result) {
							this.loadTab1();
							tab2first = true;
							this.loadTab2();
							}.bind(this)
						}
		).info('操作成功');
		} else {
			new LightFace.MessageBox(
					 {
							onClose : function(result) {
							this.loadTab1();
							tab2first = true;
							this.loadTab2();
							}.bind(this)
						}
		).error('');
		}
	}
});


function cancel(id){
	uc.cancel(id);
}
function execute(id){
	uc.execute(id);
}
function downloadClient(aid){
	uc.downloadClient(aid);
}
Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
function initSrc(e){
	if (!e.hasIcon){
		 return "<img width='40' height='40' src='"+ctx+"/images/defApp.jpg"+"' />";
	}else if ( e.hasIcon == 'LOCK_CARD' || e.hasIcon == 'UNLOCK_CARD'){
		 return "<img width='40' height='40' src='"+ ctx + "/html/mobile/?m=getMobilePic&id=" + e.cciIconId + "' />";
	}else if (e.hasIcon == 'sd' ){
		 return "<img width='40' height='40' src='"+ctx+"/images/defsd.jpg"+"' />";
	}else{
		 return "<img width='40' height='40' src='"+ ctx + "/html/application/?m=getAppPcImg&appId="+e.application_id+"' />";
	}
}
function DataLength(fData,expectLength)   
{   
    var intLength=0;
    if (fData == undefined){
    	return "";
    }
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