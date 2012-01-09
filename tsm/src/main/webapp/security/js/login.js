var User = User ? User :{};
User.Login = new Class({

	options:{
    	url:'',
    	param : {}
	},
	initialize: function(options){
		var t = new Date().valueOf();
		this.options.url = $('loginForm').get('action') + '?t=' + t;
	},
	submitForm: function(simple){
		if (this.checkParams(simple)) {
			var login = this;
			this.request = new Request.JSON({
				url : this.options.url,
				onSuccess : this.onComplete.bind(this)
			}).post($('loginForm').toQueryString());
		}
	},
	onComplete: function(result) {
		if (result.success) {//成功会自动跳转到不同的页面
			self.location = result.message;
		} else {
			// 具体怎么显示错误信息，待定
			new LightFace.MessageBox().error(result.message);
			this.refreshImage();
		}
	},
	refreshImage: function() {
		var timestamp = (new Date()).valueOf();
		$('captchaImage').set('src', ctx + '/j_captcha_get?t=' + timestamp);
	},
	checkParams: function(simple) {
		if (!simple) {
			this.clearPrompt();
		}
		var result = true;
		if (!$chk($('j_username').get('value'))) {
			if (simple) {
				new LightFace.MessageBox().error('请输入用户名');
				return false;
			} else {
				$('userNamePrompt').set('html', '请输入用户名');
				result = false;
			}
		}
		if (!$chk($('j_password').get('value'))) {
			if (simple) {
				new LightFace.MessageBox().error('请输入密码');
				return false;
			} else {
				$('passwordNamePrompt').set('html', '请输入密码');
				result = false;
			}
		}
		if (!$chk($('j_captcha_response').get('value'))) {
			if (simple) {
				new LightFace.MessageBox().error('请输入验证码');
				return false;
			} else {
				$('captchaNamePrompt').set('html', '请输入验证码&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
				result = false;
			}
		}
		return result;
	},
	clearPrompt: function() {
		var prompts = $('loginInfo').getElements('p[id$=Prompt]');
		$each(prompts, function(prompt, index) {
			prompt.set('html', '');
		});
	}
});