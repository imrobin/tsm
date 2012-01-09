LightFace.MessageBox = new Class({
	Extends: LightFace,
	options: {
		zIndex : 9002
	},
	initialize: function(options) {
		if (!$chk(options)) {
			options = {};
		}
		options.initDraw = false;
		this.parent(options);
		this.options.message = options.message;
	},
	info : function(message) {
		if (!$chk(this.options.title)) {
			this.options.title = '提示';
		}
		this.options.title = '<img id="image" src="'+ctx+'/lib/lightface/assets/information.png" />&nbsp;' + this.options.title;
		this.options.content = message;
		if (this.options.width == 'auto') {
			this.options.width = '300px';
		}
		this.options.buttons = [{
			title: '确 定',
			event: function() {this.close();},
			color: 'blue'
		}];
		this.open();
	},
	confirm : function(message) {
		if (!$chk(this.options.title)) {
			this.options.title = '提示';
		}
		this.options.title = '<img id="image" src="'+ctx+'/lib/lightface/assets/help.png" />&nbsp;' + this.options.title;
		this.options.content = message;
		if (this.options.width == 'auto') {
			this.options.width = '300px'
		}
		if (message == '您需要进行限定使用手机号设置吗？'){
			this.options.buttons = [{
				title: '需 要',
				event: function() {
					this.result = 'need';
					this.close();
				},
				color: 'blue'
			},{
				title: '不需要',
				event: function() {
					this.result = 'noNeed';
					this.close();
				},
				color: 'blue'
			},{
					title: '关闭',
					event: function() {
						this.result = 'close';
						this.close();
					}
			}];
		}else{
			this.options.buttons = [{
				title: '确 定',
				event: function() {
					this.result = true;
					this.close();
				},
				color: 'blue'
			},{
				title: '取 消',
				event: function() {this.close();}
			}];
		}
		this.open();
	},
	prompt : function(message, maxlength, required) {
		if (!$chk(this.options.title)) {
			this.options.title = '提示';
		}
		if(!$chk(maxlength)){
			maxlength = 80;
		}
		this.options.title = '<img id="image" src="'+ctx+'/lib/lightface/assets/information.png" />&nbsp;' + this.options.title;
		var timestamp = (new Date()).valueOf();
		this.options.content = message + '<br /><textarea maxlength="' + maxlength + '" id="textarea'+timestamp+'" rows="10" cols="15" style="width: 372px"></textarea>';
		if (this.options.width == 'auto') {
			this.options.width = '400px';
		}
		var box = this;
		this.options.buttons = [{
			title: '确 定',
			event: function() {
				this.result = true;
				var text = $('textarea' + timestamp);
				if (box.chckMaxLength(text)) {
					this.resultMessage = text.get('value');
					if ($chk(required) && required) {
						if ($chk(this.resultMessage) && this.resultMessage.length > 0) {
							this.close();
						} else {
							new LightFace.MessageBox().info('请输入内容');
						}
					} else {
						this.close();
					}
				}
			},
			color: 'blue'
		},{
			title: '取 消',
			event: function() {
				this.result = false;
				this.close();
			}
		}];
		this.open();
	},
	error : function(message) {
		if (!$chk(this.options.title)) {
			this.options.title = '错误';
		}
		this.options.title = '<img id="image" src="'+ctx+'/lib/lightface/assets/exclamation.png" />&nbsp;' + this.options.title;
		this.options.content = '操作失败，'+message;
		if (this.options.width == 'auto') {
			this.options.width = '300px'
		}
		this.options.buttons = [{
			title: '确 定',
			event: function() {this.close();},
			color: 'blue'
		}];
		this.open();
	},
	error2 : function(message) {
		if (!$chk(this.options.title)) {
			this.options.title = '错误';
		}
		this.options.title = '<img id="image" src="'+ctx+'/lib/lightface/assets/exclamation.png" />&nbsp;' + this.options.title;
		this.options.content = message;
		if (this.options.width == 'auto') {
			this.options.width = '300px'
		}
		this.options.buttons = [{
			title: '确 定',
			event: function() {this.close();},
			color: 'blue'
		}];
		this.open();
	},
	loading : function(content) {
		if(content && content.trim().length>0){
			this.options.content = '<img id="image" src="'+ctx+'/admin/images/ajax-loader.gif" />&nbsp;<span style="font-size: 16">'+content+'......</span>';
		}else{
		this.options.content = '<img id="image" src="'+ctx+'/admin/images/ajax-loader.gif" />&nbsp;<span style="font-size: 16">系统执行中，请稍后......</span>';
		}
		this.open();
	},
	chckMaxLength : function(el) {//中文按两个字符算
		var maxLength = el.get('maxlength')
		var length = el.value.replace(/[^\x00-\xff]/g,"00").length;
		if ($chk(maxLength)) {
			if (maxLength < length) {
				new LightFace.MessageBox().info("输入的字符最大长度为" + maxLength + "(一个汉字占两个字符)");
				return false;
			} else {
				return true;
			}
		}
	}
});