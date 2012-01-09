var JIM = JIM ? JIM : {};
JIM.CardDriver = JIM.CardDriver ? JIM.CardDriver : {};

JIM.CardDriver = new Class(
		{

			connection : false,
			appList : new Array(),
			successStutasCode : "9000",
			cardNo : '',
			commonType : null,
			modal : null,
			Implements : [ Events, Options ],

			options : {
				commonType : 2,
				url : location.protocol + "//" + location.host + ctx + "/html/mocam/?m=handler",
				ctl : null,
				operations : [],
				showMsg : true
			},

			initialize : function(options, ctl) {
				if (!$chk(options.commonType)) {
					options.commonType = this.getCommonType();
				}
				this.setOptions(options);
				this.commonType = this.options.commonType;
				this.options.operations.each(function(item) {
					var appVersion = '';
					if ($chk(item.appVersion)) {
						appVersion = item.appVersion;
					}

					var originalCardNo = '';
					if ($chk(item.originalCardNo)) {
						originalCardNo = item.originalCardNo;
					}
					this.addAppOperate(item.aid, item.operation, appVersion, originalCardNo);
				}.bind(this));

				if ($chk(options.onSuccess)) {
					this.addEvent('success', options.onSuccess);
				} else {
					this.addEvent('success', this.onSuccess.bind(this));
				}

				if ($chk(options.onFailure)) {
					this.addEvent('failure', options.onFailure);
				} else {
					this.addEvent('failure', this.onFailure.bind(this));
				}
			},

			onSuccess : function(response) {
				this.closeConnection();
				this.showMessage('操作成功');
			},

			onFailure : function(response) {
				this.closeConnection();
				this.showFailMessage(response.status.options.statusDescription);
			},

			openConnection : function() {
				if (null == this.options.ctl) {
					this.showFailMessage("读卡器控件未安装");
					throw '读卡器控件未安装';
				}

				if (this.options.ctl.GetConnectState()) {
					return true;
				} else {
					var connection = this.options.ctl.ConnectCard();

					if (!connection) {
						this.showFailMessage("读卡器连接不正确，请检查读卡器");
						throw '读卡器连接不正确，请检查读卡器';
					}
				}
			},

			closeConnection : function() {
				if (!this.options.ctl.DisconnectCard()) {
					this.showFailMessage("断开卡片失败");
					throw '断开卡片失败';
				}
			},

			readCardNo : function() {
				var cardPOR;
				this.openConnection();

				cardPOR = this.sendApdu("A0A40000027F20");
				if (!cardPOR.isSuccess()) {
					this.showFailMessage('获取卡片信息失败');
					throw '获取卡片信息失败';
				}

				cardPOR = this.sendApdu("A0A40000026f07");
				if (!cardPOR.isSuccess()) {
					this.showFailMessage('获取卡片信息失败');
					throw '获取卡片信息失败';
				}

				cardPOR = this.sendApdu("A0b0000009");
				if (!cardPOR.isSuccess()) {
					this.showFailMessage('获取卡片信息失败');
					throw '获取卡片信息失败';
				} else {
					return cardPOR.getData();
				}

				// 读SE的SEID
				// cardPOR =
				// this.sendApdu("00A4040010D1560001010001600000000100000000");
				// if (!cardPOR.isSuccess()) {
				// this.showMessage('获取卡片信息失败');
				// throw '获取卡片信息失败';
				// }
				//
				// cardPOR = this.sendApdu("80CA004400");
				// if (!cardPOR.isSuccess()) {
				// this.showMessage('获取卡片信息失败');
				// throw '获取卡片信息失败';
				// } else {
				// return cardPOR.getData().slice(10);
				// }

				this.closeConnection();
			},

			getCommonType : function() {
				return 'GPC' + '-' + Browser.name + '-' + Browser.version;
			},

			addAppOperate : function(aid, operation, appVersion, originalCardNo) {
				var operate = new JIM.CardDriver.AppOperate({
					appAid : aid,
					operation : operation,
					appVersion : appVersion,
					originalCardNo : originalCardNo
				});
				var appInfo = new JIM.CardDriver.AppInfo({
					appOperate : operate
				});
				this.appList.push(appInfo);
			},

			getXml : function(param) {
				var envelope = '';
				envelope += '<?xml version="1.0" encoding="utf-8"?>\n';
				envelope += '<simota:ExecAPDUsRequest xmlns:simota="http://www.chinamobile.com" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.chinamobile.com ../xsds/exec-apdus-resp.xsd">\n';
				var paramHash = new Hash(param);
				if (paramHash.getKeys().length > 0) {
					envelope += this.jsonToXml(param, 1, this);
				}
				envelope += '</simota:ExecAPDUsRequest>';
				return envelope;
			},

			jsonToXml : function(json, index, obj) {
				var envelope = '';
				var jsonHash = new Hash(json);
				jsonHash.each(function(value, key) {
					var space = '';
					for ( var int = 0; int < index; int++) {
						space += '  ';
					}
					envelope += space + '    <' + 'simota' + ':' + key + '>';
					if (window.Array == value.constructor && value.length > 0) {
						envelope += '\n';
						value.each(function(item) {
							envelope += obj.jsonToXml(item, index + 1, obj);
						});
						envelope += space + '    </' + 'simota' + ':' + key + '>\n';
					} else if (value == '[object Object]') {
						envelope += '\n';
						envelope += obj.jsonToXml(value, index + 1, obj);
						envelope += space + '    </' + 'simota' + ':' + key + '>\n';
					} else {
						envelope += value;
						envelope += '</' + 'simota' + ':' + key + '>\n';
					}

				});
				return envelope;
			},

			xmlToJson : function(xml) {
				// Create the return object
				var obj = {};
				if (xml.nodeType == 1) { // element
					// do attributes
					if (xml.attributes.length > 0) {
						obj["@attributes"] = {};
						for ( var j = 0; j < xml.attributes.length; j++) {
							var attribute = xml.attributes.item(j);
							obj["@attributes"][attribute.nodeName] = attribute.nodeValue;
						}
					}
				}
				// do children
				if (xml.hasChildNodes()) {
					for ( var i = 0; i < xml.childNodes.length; i++) {
						var item = xml.childNodes.item(i);
						var nodeName = item.baseName;
						if (typeof (obj[nodeName]) == "undefined") {
							if (nodeName == 'Envelope' || nodeName == 'Body') {
								return this.xmlToJson(item);
							} else {
								if (item.nodeType == 3) {
									obj['value'] = item.nodeValue;
								} else {
									obj[nodeName] = this.xmlToJson(item);
								}
							}
						} else {
							if (typeof (obj[nodeName].length) == "undefined") {
								var old = obj[nodeName];
								obj[nodeName] = [];
								obj[nodeName].push(old);
							}
							obj[nodeName].push(this.xmlToJson(item));
						}
					}
				}
				return obj;
			},

			exec : function() {
				this.fireEvent('prepare');

				this.showProgress('正在连接网络', '0');
				this.openConnection();
				this.cardNo = this.readCardNo();
				var params = new JIM.CardDriver.ReqExecAPDU({
					appList : this.appList,
					cardNo : this.cardNo,
					ueprof : this.options.commonType
				});

				this.send(params.toJson());
			},

			send : function(params) {
				var p = new Array();
				var paramsHash = new Hash(params);
				paramsHash.each(function(value, key) {
					p.push(key, value);
				});
				var request = new Request({
					url : this.options.url,
					method : 'post',
					onSuccess : function(responseText, responseXML) {
						this.execCallback(this.xmlToJson(responseXML));
					}.bind(this),
					onFailure : function() {
						this.showFailMessage('网络异常');
					}.bind(this)
				});
				request.send('xml=' + this.getXml(p[1]));

				// var webService = new JIM.Webservice({
				// url : this.options.url,
				// method : 'RequestExecAPDUs',
				// prefix : 'simota',
				// onSuccess : this.execCallback.bind(this),
				// onFailure : this.showFailMessage.bind(this)
				// });
				// webService.send(params);
			},

			execCallback : function(json) {
				var response = new JIM.CardDriver.ResExecAPDU(json);

				if (response.status.isSuccess()) {
					var apdus = response.apduList;
					var apdusLength = apdus.length;
					if (0 == apdusLength) {
						if ($chk(this.modal)) {
							this.modal.close();
						}
						this.fireEvent('success', [ response ]);
					} else {
						if ($chk(response.progress) && $chk(response.progressPercent)) {
							this.showProgress(response.progress, response.progressPercent);
						}

						var cardPOR = null;

						this.fireEvent('before', [ response ]);
						for ( var index = 0; index < apdus.length; index++) {
							var apdu = apdus[index];
							cardPOR = this.sendApdu(apdu);

							this.fireEvent('after', [ cardPOR, response ]);

							if (!cardPOR.isSuccess()) {
								break;
							}
						}

						cardPOR.setSum(index);
						cardPOR.options.lastData = cardPOR.options.lastData + cardPOR.options.lastAPDUSW;

						if (cardPOR.isDisconected()) {
							this.closeConnection();
							this.showFailMessage('卡片通信失败');
						} else {
							var params = new JIM.CardDriver.ReqExecAPDU({
								sessionID : response.sessionId,
								seqNum : response.seqNum,
								appList : this.appList,
								cardNo : this.cardNo,
								cardPOR : cardPOR,
								currentAppAid : response.currentAppAid,
								commandID : response.commandId,
								ueprof : this.options.commonType
							});
							this.send(params.toJson());
						}
					}
				} else {
					this.fireEvent('failure', [ response ]);
				}
			},

			showMessage : function(message) {
				if ($chk(this.modal)) {
					this.modal.close();
				}
				if (this.options.showMsg) {
					new LightFace.MessageBox().info(message);
				}
			},
			showFailMessage : function(message) {
				if ($chk(this.modal)) {
					this.modal.close();
				}
				if (this.options.showMsg) {
					new LightFace.MessageBox().error(message);
				}
			},
			sendApdu : function(apdu) {
				this.options.ctl.Capdu = apdu;

				var cardResponse = this.options.ctl.Capdu;

				var sw = cardResponse.slice(0, 4);// 卡响应的第1个到第4个字符为状态码
				var date = cardResponse.slice(4);// 卡响应第5个字符开始为数据

				return new JIM.CardDriver.CardPOR({
					lastAPDUSW : sw.toUpperCase(),
					lastData : date.toUpperCase(),
					lastApdu : apdu.toUpperCase()
				});
			},

			showProgress : function(progress, percent) {
				if (!$chk(this.modal)) {
					this.modal = new LightFace({
						loadModule : this,
						height : 100,
						width : 250,
						content : $('#cardDriverProgressTemplate$').get('html'),
						resetOnScroll : false
					});
				}

				this.modal.open();

				var box = this.modal.getBox();
				box.getElement('[title="progress"]').set('html', progress);
				box.getElement('[title="percent"]').set('html', percent + '%');
			}
		});

JIM.CardDriver.ResExecAPDU = new Class({
	commandId : '',
	seqNum : '',
	status : '',
	apduList : [],
	currentAppAid : '',
	timeStamp : '',
	sessionId : '',
	apduName : '',
	progress : '',
	progressPercent : '',

	initialize : function(options) {
		var response = options.ExecAPDUsCmd;

		if ($chk(response.CommandID) && $chk(response.CommandID.value)) {
			this.commandId = response.CommandID.value;
		}

		if ($chk(response.SeqNum) && $chk(response.SeqNum.value)) {
			this.seqNum = response.SeqNum.value;
		}
		if ($chk(response.ExecStatus)) {
			this.status = new JIM.CardDriver.Status({
				statusCode : response.ExecStatus.StatusCode.value,
				statusDescription : response.ExecStatus.StatusDescription.value
			});
		} else {
			this.status = new JIM.CardDriver.Status();
		}

		if ($chk(response.APDUList) && $chk(response.APDUList.APDU)) {
			var apdus = new Hash(response.APDUList.APDU);
			var apduCount = apdus.getLength();
			if (0 != apduCount) {
				if (1 == apduCount) {
					this.apduList.push(apdus.value);
				} else {
					apdus = response.APDUList.APDU;
					for ( var i = 0; i < apdus.length; i++) {
						this.apduList.push(apdus[i].value);
					}
				}
			}
		}

		if ($chk(response.CurrentAppAid) && $chk(response.CurrentAppAid.value)) {
			this.currentAppAid = response.CurrentAppAid.value;
		}

		if ($chk(response.TimeStamp) && $chk(response.TimeStamp.value)) {
			this.timeStamp = response.TimeStamp.value;
		}

		if ($chk(response.SessionID) && $chk(response.SessionID.value)) {
			this.sessionId = response.SessionID.value;
		}

		if ($chk(response.ApduName) && $chk(response.ApduName.value)) {
			this.apduName = response.ApduName.value;
		}

		if ($chk(response.Progress) && $chk(response.Progress.value)) {
			this.progress = response.Progress.value;
		}

		if ($chk(response.ProgressPercent) && $chk(response.ProgressPercent.value)) {
			this.progressPercent = response.ProgressPercent.value;
		}
	}
});

JIM.CardDriver.ReqExecAPDU = new Class({
	Implements : [ Events, Options ],

	options : {
		commandID : '',
		ueprof : 'GPC',
		timeStamp : new Date().valueOf(),
		sessionID : '',
		// seqNum : '',
		appList : new Array(),
		cardNo : '',
		currentAppAid : '',
		cardPOR : null
	},

	initialize : function(options) {
		this.setOptions(options);
	},

	toJson : function() {
		var reqExecAPDU = {};
		reqExecAPDU.ExecAPDUsReqest = {};
		reqExecAPDU.ExecAPDUsReqest.CommandID = this.options.commandID;// 1
		reqExecAPDU.ExecAPDUsReqest.UEPROF = this.options.ueprof;// 2
		reqExecAPDU.ExecAPDUsReqest.TimeStamp = this.options.timeStamp;// 3
		reqExecAPDU.ExecAPDUsReqest.SessionID = this.options.sessionID;// 4
		// reqExecAPDU.ExecAPDUsReqest.SeqNum = this.options.seqNum;// 5
		if ($chk(this.options.appList)) {// 6
			var appList = new Array(this.options.appList.length);
			var i;
			for (i = 0; i < this.options.appList.length; i++) {
				appList[i] = this.options.appList[i].toJson();
			}
			reqExecAPDU.ExecAPDUsReqest.CommandList = appList;
		} else {
			reqExecAPDU.ExecAPDUsReqest.CommandList = {};
		}
		reqExecAPDU.ExecAPDUsReqest.SEID = this.options.cardNo;// 7
		reqExecAPDU.ExecAPDUsReqest.CurrentAppAid = this.options.currentAppAid;// 8
		if ($chk(this.options.cardPOR)) {// 9
			reqExecAPDU.ExecAPDUsReqest.CardPOR = this.options.cardPOR.toJson();
		} else {
			reqExecAPDU.ExecAPDUsReqest.CardPOR = {};
		}
		return reqExecAPDU;
	}
});

JIM.CardDriver.AppInfo = new Class({
	Implements : [ Events, Options ],

	options : {
		appOperate : null
	},

	initialize : function(options) {
		this.setOptions(options);
	},

	toJson : function() {
		var appInfo = {};

		if ($chk(this.options.appOperate)) {
			appInfo.Command = this.options.appOperate.toJson();
		} else {
			appInfo.Command = {};
		}

		return appInfo;
	}
});

JIM.CardDriver.Status = new Class({
	successStatusCode : '00000000',

	Implements : [ Events, Options ],

	options : {
		statusCode : '0000',
		statusDescription : ''
	},

	initialize : function(options) {
		this.setOptions(options);
	},

	isSuccess : function() {
		return this.options.statusCode == this.successStatusCode;
	},

	toJson : function() {
		var status = {};

		status.statusCode = this.options.statusCode;
		status.statusDescription = this.options.statusDescription;

		return status;
	}
});

JIM.CardDriver.AppOperate = new Class({
	Implements : [ Events, Options ],

	options : {
		appAid : '',
		operation : 0,
		appVersion : '',
		originalCardNo : ''
	},

	initialize : function(options) {
		this.setOptions(options);
	},

	toJson : function() {
		var appOperate = {};

		appOperate.AppAID = this.options.appAid;
		appOperate.CommandID = this.options.operation;
		appOperate.AppVersion = this.options.appVersion;
		if ($chk(this.options.originalCardNo)) {
			appOperate.OriginalSEID = this.options.originalCardNo;
		} else {
			appOperate.OriginalSEID = '';
		}

		return appOperate;
	}
});

JIM.CardDriver.CardPOR = new Class({
	successStatusCode : '9000',
	lockedSuccessStatusCode : '6283',
	diconectedSatusCode : '0000',

	Implements : [ Events, Options ],

	options : {
		apduSum : 0,
		lastAPDUSW : '0000',
		lastData : '',
		lastApdu : ''
	},

	isDisconected : function() {
		var sw = this.getSw();
		var isDisconected = (this.diconectedSatusCode == sw);
		return isDisconected;
	},

	isSuccess : function() {
		var sw = this.getSw();
		var isSuccess = (this.successStatusCode == sw) || (this.lockedSuccessStatusCode == sw);
		return isSuccess;
	},

	initialize : function(options) {
		this.setOptions(options);
	},

	getData : function() {
		return this.options.lastData;
	},

	getSw : function() {
		return this.options.lastAPDUSW;
	},

	setSum : function(count) {
		this.options.apduSum = count;
	},

	toJson : function() {
		var cardPOR = {};

		cardPOR.APDUSum = this.options.apduSum;
		cardPOR.LastApduSW = this.options.lastAPDUSW;
		cardPOR.LastData = this.options.lastData;
		cardPOR.LastApdu = this.options.lastApdu;

		return cardPOR;
	}
});