SpBlackList = {};
SpBlackList = new Class({
	Implements : [Events,Options],
	options : {},
	initialize : function(options){
		this.setOptions(options);
	},
	addSpToBlack : function(ids){
		if(ids.length > 0){
			new LightFace.MessageBox({
				onClose : function(){
					if(this.result){
						var reason = this.resultMessage;
						new Request.JSON({
							url : ctx + "/html/spBlackList/?m=addBlackList",
							data :{
								"ids" : ids,
								"num" : ids.length,
								"reason" : reason
							},
							onSuccess : function(resp,xml) {
								new LightFace.MessageBox({
									onClose : function(){
										location.reload(); 
									}
								}).info(resp.message);
							}
						}).send();
					}
				}
			}).prompt('请输入理由',60,true);
		}else{
			new LightFace.MessageBox().error("请至少选择一个提供商");
		}
	},
	removeOutBlack  : function(ids){
		if(ids.length > 0){
			new LightFace.MessageBox({
				onClose : function(){
					if(this.result){
						var reason = this.resultMessage;
						new Request.JSON({
							url : ctx + "/html/spBlackList/?m=removeBlackList",
							data :{
								"ids" : ids,
								"num" : ids.length,
								"reason" : reason
							},
							onSuccess : function(resp,xml) {
								new LightFace.MessageBox({
									onClose : function(){
										location.reload(); 
									}
								}).info(resp.message);
							}
						}).send();
					}
				}
			}).prompt('请输入理由',60,true);
		}else{
			new LightFace.MessageBox().error("请至少选择一个提供商");
		}
	}
});
