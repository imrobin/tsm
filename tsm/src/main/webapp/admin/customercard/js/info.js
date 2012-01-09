var QueryInfo = QueryInfo ? QueryInfo : {};

QueryInfo = new Class({
	options : {},
	initialize : function() {
	},
	createCCIGrid : function(customerid){
		var myQuery = this;
		var grid = new JIM.UI.WinGrid({
			url : ctx + '/html/customerCard/?m=list&search_ALIAS_customerL_EQL_id=' + customerid,
        	multipleSelection: false,
        	height:330,
        	width :600,
        	winButtons : [{
				title: '查看详情',
				event: function() {
					if (!$chk(this.selectIds) || this.selectIds.length == 0) {
						new LightFace.MessageBox().error('请先选择列表中的记录');
						return;
					}
					myQuery.getCardInfo(this.selectIds[0]);
				}
			},{
				title: '绑定终端',
				event: function() {
					myQuery.bindCard(customerid,grid);
				}
			},
			{
				title: '注销终端',
				event: function() {
				    if (!$chk(this.selectIds) || this.selectIds.length == 0) {
					new LightFace.MessageBox().error('请先选择列表中的记录');
					return;
				    }
				    var selid = this.selectIds[0];
				    new LightFace.MessageBox( {
						onClose : function() {
							if (this.result) {
								myQuery.cancelTerm(selid,grid);
							}
						}
					}).confirm("您确认要进行注销吗？请慎重操作");
				}
			},{
				title: '退出',
				event: function() { this.close(); }
			}],
        	drawButtons : false,
        	drawSearch : false,
        	columnModel : [{dataName : 'id', identity : true},{title : '终端名称', dataName : 'name'}, {title : '手机号', dataName : 'mobileNo'} ,{title : '状态', dataName : 'status'}],
        	searchButton : false,
        	searchBar : {filters : []},
        	headerText : '终端列表 '
		});
	},
	bindCard : function(customerid,grid){
		new Request.JSON({
	    	    url : ctx + "/html/customer/?m=getCustomer&cId=" + customerid,
	    	    onSuccess : function(data){
	    		if(data.message.activeOriginal != 1){
			    new LightFace.MessageBox().error("用户尚未激活,不能继续操作");
				return;
			};
			   var content = $('bindDiv').get('html');
				  var box = new LightFace({
					draggable : false,
					initDraw : true,
					width : 630,
					content : content,
					onClose : function() {
						var div = document.getElement('div[class=fc-tbx]');
						if ($chk(div)) {
							div.dispose();
						}
					},
					buttons : [ 
					           {
				                    	title : '检测读卡器状态',
				                    	event : function() {
				                    	new LightFace( {
								width : 300,
								title : '<img id="image" src="' + ctx + '/lib/lightface/assets/information.png" />&nbsp;提示',
								content : '请确定是否已将终端连接至读卡器，详细操作请参考<a  style="color:#4682b4; text-decoration:underline;"  href="' + ctx + '/help.jsp" target="_blank">帮助中心</a>',
								draggable : true,
								initDraw : false,
								test : false,
								buttons : [ {
									title : '确定',
									event : function() {
									    	box.test = true;
										this.close();
										try{
											var cardNo = new JIM.CardDriver( {
												ctl : cardDriver,
												operations : []
											}).readCardNo();
											
											if ($chk(cardNo)) {
											    	box.cardNo = cardNo;
												new LightFace.MessageBox().info("检测成功，可以执行操作");
											}
										} catch (e){
										    //new LightFace.MessageBox().info("无法使用读卡器，请查看帮助中心");
										}
									},
									color : 'blue'
								}, {
									title : '关 闭',
									event : function() {
										this.close();
									}
								} ]
							}).open();
				                    	},
				                    	color : 'blue'
				                    }  ,
						         
					           {
						title : '绑定',
						event : function() {
						   box.messageBox.getElement('form').getElement('button').click();
						}.bind(this),
						color : 'blue'
					}, {
						title : '关 闭',
						event : function() {
							this.close();
						}
					} ]
				});
				    var request = new Request.JSON({
					url : ctx+'/html/mobile/?m=getMobileBrand',
					onSuccess : function(data) {
						if(data.success) {
						    	box.messageBox.getElement('[id="mobileBrand"]').options.add(new Option("请选择",0));
							Array.each(data.message, function(item, index){
							    box.messageBox.getElement('[id="mobileBrand"]').options.add(new Option(item,item));	
							});
							 box.open();
						}
					}
					}).post();
				    box.messageBox.getElement('[id="mobileBrand"]').addEvent('change',function(){
					var mobileBrandvalue = this.get('value');
					var request = new Request.JSON({
						url : ctx+'/html/mobile/?m=getTypeAndValueByBrand',
						async : false,
						onSuccess : function(result) {
							if(result.success) {
							    box.messageBox.getElement('[id="mobileTypeId"]').empty();
							    box.messageBox.getElement('[id="mobileTypeId"]').options.add(new Option("请选择",0));
								Array.each(result.message, function(item, index){
								    box.messageBox.getElement('[id="mobileTypeId"]').options.add(new Option(item.type,item.id));	
								});
							}
						}
					}).post({
					    'brand' : mobileBrandvalue
					});
				    });
				   var bindForm =  box.messageBox.getElement('form');
				    new FormCheck(bindForm, {
					submit : false,
					display : {
						showErrors:0,
						indicateErrors : 1,
						scrollToFirst : false
					},
					onValidateSuccess : function() {//校验通过执行load()
					    if(box.test){
						if($chk(box.cardNo)){
						    	new Request.JSON( {
								url : this.form.get('action') + '&cardNo=' + box.cardNo + "&customerId="+ customerid,
								onSuccess : function(data) {
									if (data.success) {
										new LightFace.MessageBox( {
											onClose : function() {
											    box.close();
											    grid.load();
											}.bind(this)
										}).info(data.message);
									} else {
										new LightFace.MessageBox().error(data.message);
									}
								}.bind(this)
							}).post(bindForm.toQueryString());
						    	}else{
						    	    new LightFace.MessageBox().error("您的终端尚未准备好");
						    	}
					    }else{
						 new LightFace.MessageBox().error("操作前请先检测您的终端");
					    }
					}
				});
	    	    }
		}).get();
	},
	formatSize : function(total) {
		var totalFolat = total.toFloat();
		if (totalFolat < 1024) {
			return totalFolat + "Byte";
		} else if (totalFolat >= 1024 && totalFolat < 1048576) {
			var i = totalFolat / 1024;
			return i.toFixed(2) + "KByte";
		} else if (totalFolat >= 1048576) {
			var i = totalFolat / 1024 / 1024;
			return i.toFixed(2) + 'MByte';
		}
	},
	cancelTerm : function(ccid,grid){
		var card = this;
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=getCusstomerCard&ccId=" + ccid,
			onSuccess : function(data) {
				if(data.success){
					var cardInfo =  data.result[0];
					var status = cardInfo.card_status;
					if((cardInfo.statusOriginal != 1 && cardInfo.statusOriginal != 3) || cardInfo.inBlackOriginal == 1){
					    new LightFace.MessageBox().error("终端状态异常或者在黑名单中");
						return;
					}
					if(status == '无效'){
					    new LightFace.MessageBox().error("卡不可用，请到移动营业厅恢复卡片");
						return;
					}
					var nowCardNo = new JIM.CardDriver( {
						ctl : cardDriver,
						operations : []
					}).readCardNo();
					if (cardInfo.card_cardNo != nowCardNo) {
						new LightFace.MessageBox().error("操作终端与所选终端不符");
						return;
					}
					new Request.JSON( {
						url : ctx + "/html/customerCard/?m=checkCancelTermCardApp&ccId=" + ccid,
						onSuccess : function(resp, responseXML) {
							if (resp.success) {
								card.finishCancel(ccid); 
							} else {
								new LightFace.MessageBox().error("终端上还有未删除的应用，请先手动删除后再进行操作");
							}
						}
					}).get();
				}
			}
		}).get();
	},
	finishCancel : function(ccid,grid) {
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=finishCancel&ccId=" + ccid,
			onSuccess : function(resp, responseXML) {
				if (!$chk(resp)) {
					resp = JSON.encode(responseXML);
				}
				if (resp.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							location.reload();
						}
					}).info("注销成功");
				} else {
					new LightFace.MessageBox().error("请重试");
				}
			}
		}).get();
	},
	getCardInfo : function(ccId){
	    var cardInfo = this;
		new Request.JSON({
			url : ctx + "/html/customerCard/?m=getCardInfoByCCI&ccId=" + ccId,
			onSuccess : function(data) {
				if (data.success) {
					var box = new LightFace.Request({
						url: 'cardInfo.html',
						title : '终端详情',
						onSuccess : function(){
							var card = data.message;
							//设置卡片信息
							box.messageBox.getElement('[id="cardNo"]').set('html',card.cardInfo.cardNo);
							box.messageBox.getElement('[id="status"]').set('html',card.cardInfo.status);
							box.messageBox.getElement('[id="cardBase"]').set('html',card.cardInfo.cardBaseInfo_name);
							//设置卡片应用信息
							if(card.cardAppInfo.length>0){
								var hide = true;
								var appDiv = box.messageBox.getElement('[id="applistDiv"]');
								var tbody = appDiv.getElement('tbody');
								var html = "";
								Array.each(card.cardAppInfo,function(app,index){
								    	if(app.statusOriginal != 1){
        								    html += "<tr>";
        									html += "<td>" + app.appName + "</td>";
        									html += '<td align="center">' + app.appVer + "</td>";
        									html += '<td align="center">' + app.status + "</td>";
        									html += '<td align="center">' + cardInfo.formatSize(app.loadFileRam) + "</td>";
        									html += '<td align="center">' + cardInfo.formatSize(app.loadFileRom) +"</td>";
        									html += '<td align="center">' + cardInfo.formatSize(app.appletRam) + "</td>";
        									html += '<td align="center">' + cardInfo.formatSize(app.appletRom) + "</td>";
        									html += '<td align="center">' + app.sdName + "</td>";
        									html += "</tr>";
        									hide = false;
								    	}
								});
								tbody.set("html",html);
								if(hide){
									var appDiv = box.messageBox.getElement('[id="applistDiv"]');
									appDiv.setStyle('display','none');
								}
							}else{
								var appDiv = box.messageBox.getElement('[id="applistDiv"]');
								appDiv.setStyle('display','none');
							}
							//设置卡片安全域信息
							if(card.cardSdInfo.length>0){
								var hide = true;
								var appDiv = box.messageBox.getElement('[id="sdlistDiv"]');
								var tbody = appDiv.getElement('tbody');
								var html = "";
								Array.each(card.cardSdInfo,function(sd,index){
								    if(sd.statusOriginal != 1){
									html += "<tr>";
									html += '<td>' + sd.sdName + "</td>";
									html += '<td align="center">' + sd.appMod + "</td>";
									html += '<td align="center">' + sd.status + "</td>";
									var keyVersion = sd.sdKeyVersion;
									if(!$chk(keyVersion)){
										keyVersion = '暂无密钥';
									}
									html += '<td align="center">' + keyVersion + "</td>";
									html += '<td align="center">' + cardInfo.formatSize(sd.sdUsedRam) + "</td>";
									html += '<td align="center">' + cardInfo.formatSize(sd.sdAviliableRam) + "</td>";
									html += '<td align="center">' + cardInfo.formatSize(sd.sdUsedRom) + "</td>";
									html += '<td align="center">' + cardInfo.formatSize(sd.sdAviliableRom) + "</td>";
									html += "</tr>";
									hide = false;
								    }
								});
								tbody.set("html",html);
								if(hide){
									var sdDiv = box.messageBox.getElement('[id="sdlistDiv"]');
									sdDiv.setStyle('display','none');
								}
							}else{
								var sdDiv = box.messageBox.getElement('[id="sdlistDiv"]');
								sdDiv.setStyle('display','none');
							}
							//设置空间信息
							box.messageBox.getElement('[id="Uram"]').set("html",cardInfo.formatSize(card.cardInfo.usedSpace.ram));
							box.messageBox.getElement('[id="Unvm"]').set("html",cardInfo.formatSize(card.cardInfo.usedSpace.nvm));
							box.messageBox.getElement('[id="Eram"]').set("html",cardInfo.formatSize(card.cardInfo.existSpace.ram));
							box.messageBox.getElement('[id="Envm"]').set("html",cardInfo.formatSize(card.cardInfo.existSpace.nvm));
							box.messageBox.getElement('[id="Aram"]').set("html",cardInfo.formatSize(card.cardInfo.totalSpace.ram));
							box.messageBox.getElement('[id="Anvm"]').set("html",cardInfo.formatSize(card.cardInfo.totalSpace.nvm));
							box.open();
						},
						buttons : [
							{
								title: '退出',
								event: function() { this.close(); }
							}
						]
					});
				}
			}.bind(this)
		}).get();
	}
});