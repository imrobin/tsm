var AdminCancelTerm = AdminCancelTerm ? AdminCancelTerm : {};

AdminCancelTerm = new Class({
	options : {},
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
	cancelTermianl : function(ccid,gird){
		var cardInfo =  resp.result[0];
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
		return false;
	},
	finishCancel : function(ccid) {
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
	getCCIinfo : function(ccid){
		new Request.JSON( {
			url : ctx + "/html/customerCard/?m=getCusstomerCard&ccId=" + ccid,
			onSuccess : function(resp, responseXML) {
				return resp.result[0];
			}
		}).get();
	}
});