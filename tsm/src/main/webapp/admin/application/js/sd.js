var OptSd = OptSd ? OptSd : {};

OptSd = new Class({
	options : {},
	nowId : '',
	transConstant : '',
	aid : '',
	initialize : function() {
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
	optSd : function(sdId, opt, grid) {
		var obj = this;
		var cardNo = new JIM.CardDriver({
			ctl : cardDriver,
			operations : []
		}).readCardNo();
		if (!$chk(cardNo)) {
			new LightFace.MessageBox().info("请准备好终端");
			return;
		}
		new Request.JSON({
			url : ctx + '/html/cardinfo/?m=checkCard&cardNo=' + cardNo,
			onSuccess : function(data) {
				if (data.success) {
					new Request.JSON({
						url : ctx + '/html/securityDomain/?m=getSd&sdId=' + sdId,
						onSuccess : function(json) {
							if (json.success) {
								var sdAid = json.message.aid;
								if (opt == 'create') {
									obj.createSd(sdAid, grid);
								} else if (opt == 'del') {
									obj.delSd(sdId, sdAid, grid);
								} else if (opt == 'lock') {
									obj.lockSD(sdAid, grid);
								} else if (opt == 'unlock') {
									obj.unlockSD(sdAid, grid);
								} else if (opt == 'update') {
									obj.updateSD(sdAid, grid);
								} else {
									new LightFace.messageBox.error("不支持的操作");
								}
							}
						}.bind(this)
					}).get();
				} else {
					new LightFace.MessageBox().error(data.message);
					return;
				}
			}
		}).get();
	},
	createSd : function(aid, grid) {
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : this.transConstant.CREATE_SD
			} ],
			onSuccess : function(response) {
				this.closeConnection();
				new LightFace.MessageBox().info("操作成功");
				grid.load();
			}
		}).exec();
	},
	delSd : function(sdId, aid, grid) {
		var optsd = this;
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : optsd.transConstant.DELETE_SD
			} ],
			onSuccess : function(response){
			    this.closeConnection();
			    this.showMessage("操作成功");
			    grid.load();
			}
		}).exec();
	},
	lockSD : function(aid, grid) {
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : this.transConstant.LOCK_SD
			} ],
			onSuccess : function(response) {
				this.closeConnection();
				new LightFace.MessageBox().info("操作成功");
				grid.load();
			}
		}).exec();
	},
	updateSD : function(aid, grid) {
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : this.transConstant.UPDATE_KEY
			} ],
			onSuccess : function(response) {
				this.closeConnection();
				new LightFace.MessageBox().info("操作成功");
				grid.load();
			}
		}).exec();
	},
	unlockSD : function(aid, grid) {
		new JIM.CardDriver({
			ctl : cardDriver,
			operations : [ {
				aid : aid,
				operation : this.transConstant.UNLOCK_SD
			} ],
			onSuccess : function(response) {
				this.closeConnection();
				new LightFace.MessageBox().info("操作成功");
				grid.load();
			}
		}).exec();
	}
});