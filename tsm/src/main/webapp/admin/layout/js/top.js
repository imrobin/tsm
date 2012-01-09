var JIM = JIM ? JIM : {};
JIM.Top = new Class({

	options:{
    	
	},
	initialize: function(){        
		this.drawTopBox();
		$('accountSet').addEvent('click', function(){
			this.topBox.options.title = $('accountSet').get('html');
			this.topBox.options.content = $('accountDiv').get('html');
			this.topBox.addEvent('open', this.openAccount.bind(this));
			this.topBox.open();
			this.topBox.removeEvents('open');
		}.bind(this));
		$('passwordSet').addEvent('click', function(){
			this.topBox.options.title = $('passwordSet').get('html');
			this.topBox.options.content = $('passwordDiv').get('html');
			this.topBox.addEvent('open', this.openPassword.bind(this));
			this.topBox.open();
			this.topBox.removeEvents('open');
		}.bind(this));
	},
	drawTopBox : function() {
		this.topBox = new LightFace( {
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '保 存',
				event : function() {
					this.form.getElement('button').click();
				}.bind(this),
				color : 'blue'
			}, {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	openAccount : function() {
		this.form = this.topBox.messageBox.getElement('form');
		var time = new Date().valueOf();
		new Request.JSON({
			url : ctx + '/html/user/?m=getCurrentUser&t=' + time,
			onSuccess : function(data){
			if (data.success) {
				var inputs = this.topBox.messageBox.getElements('input');
				$each(inputs, function(input, i){
					input.set('value', data.message[input.get('name')]);
				});
			} else {
				new LightFace.MessageBox().error(data.message);
			}
		}.bind(this)
		}).get();
		this.addValidate();
	},
	openPassword : function() {
		this.form = this.topBox.messageBox.getElement('form');
		var inputs = this.topBox.messageBox.getElements('input');
		$each(inputs, function(input, i){
			input.set('value', '');
		});
		this.addValidate();
	},
	addValidate : function() {
		this.formCheck = new FormCheck(this.form, {
			submit : false,
			zIndex : this.topBox.options.zIndex,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			alerts : {number : '手机号由数字组成', length_str : '密码长度必须是 %0 - %1之间'},
			onValidateSuccess : function() {//校验通过执行load()
				this.submitForm();
			}.bind(this)
		});
	},
	submitForm : function() {
		new Request.JSON( {
			url : this.form.get('action'),
			onSuccess : function(data) {
				if (data.success) {
					new LightFace.MessageBox( {
						onClose : function() {
							this.topBox.close();
						}.bind(this)
					}).info(data.message);
				} else {
					new LightFace.MessageBox().error(data.message);
				}
			}.bind(this)
		}).post(this.form.toQueryString());
	}
});
