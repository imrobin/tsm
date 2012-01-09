var ForgetPassword = ForgetPassword ? ForgetPassword : {};
ForgetPassword.Display= new Class({
	options:{
		hasClick:false
	},
	initialize: function(options){
	},
	setMobile:function(){
		$('last').addClass("current");
		$('first').removeClass("current");
		$('in_mobile').set('value','');
		$('in_email').set('tabindex',75);
		$('in_mobile').getParent().getElement('[class="msg"]').setStyle('display','none');
		$('in_mobile').getParent().getElement('[class="error"]').set('html','');
	},
	refreshImage:function(){
		var timestamp = (new Date()).valueOf();
		$('J_CheckCode').set('src', ctx + '/j_captcha_get?t=' + timestamp);
	},
	setEmail:function(){
		$('in_email').set('value','');
		$('in_email').set('tabindex','');
		$('first').addClass("current");
		$('last').removeClass("current");
		$('in_email').getParent().getElement('[class="msg"]').setStyle('display','none');
		$('in_email').getParent().getElement('[class="error"]').set('html','');
	},
	sendRequest:function(item){
		if (!this.options.hasClick){
		var box = new LightFace.MessageBox();
		box.loading('提交中，请稍候');
			 var request = new Request({
				url : $('sendPassForm').get('action'),
				onSuccess : function(response) {
					var result = JSON.decode(response);
					if(result.success) {
						if(item[1].checked){
							self.location = ctx + '/home/customer/resetRequestResult.jsp?email='+result.message;
						}else if(item[0].checked){
							self.location = ctx + '/home/customer/sendCode.jsp?mobile='+result.message;
						}
					} else {
						new LightFace.MessageBox().error(result.message);
						var timestamp = (new Date()).valueOf();
						$('J_CheckCode').set('src', ctx + '/j_captcha_get?t=' + timestamp);
					}
					box.close();
					
				},
				onError : function(response) {
					box.close();
				}
			});
			request.post($('sendPassForm').toQueryString()); 
		}
	}
});
	
	
	