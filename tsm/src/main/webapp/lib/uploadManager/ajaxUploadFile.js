var JIM = JIM ? JIM : {};

JIM.AjaxUploadFile = new Class({

	Implements : [ Events, Options ],

	options : {
		fileElementId : 'file',
		url : '',
		responseType : 'json'
	},
	initialize : function(options) {
		this.setOptions(options);
		if ($chk(options.onSuccess)) {
			this.onSuccess = options.onSuccess;
		}
		this.body = $(document.body);
		this.file = $(this.options.fileElementId);
		this.filePosition = this.file.getNext();
		//this.file.setStyle('display', 'none');
		this.iframe = this.createIframe();
		this.form = this.createForm();
	},
	createForm : function() {
		var form = new Element('form');
		var timestamp = (new Date()).valueOf();
		form.set('id', 'uploadForm' + timestamp);
		if(form.encoding) {
			form.set('encoding', 'multipart/form-data');
			form.set('enctype', 'multipart/form-data');
		} else {
        	form.set('enctype', 'multipart/form-data');
        }
		form.set('action', this.options.url);
		form.set('method', 'post');
		form.set('target', this.iframe.get('name'));
		form.grab(this.file);
		form.inject(this.filePosition, 'before');
		return form;
	},
	createIframe : function() {
		var iframe = new Element('iframe');
		var timestamp = (new Date()).valueOf();
		iframe.set('id', 'uploadIframe' + timestamp);
		iframe.set('name', 'uploadIframe' + timestamp);
		//iframe.set('src', '');
		iframe.setStyle('display', 'none');
		if (Browser.ie) {
			iframe.addEvent('load', function() {
				var result = {};
				var doc = document.frames['uploadIframe' + timestamp];
				var str = $(doc).get('html');
				alert(str);
				if(this.iframe.contentWindow) {
					result = this.iframe.contentWindow.document.body?this.iframe.contentWindow.document.body.innerHTML:null;
				} else if(io.contentDocument) {
					result = this.iframe.contentDocument.document.body?this.iframe.contentDocument.document.body.innerHTML:null;
				}
				this.dispose();
				if ($chk(result)) {
					this.fireEvent('success', eval("data = " + result));
				}
			}.bind(this));
		} else {
			iframe.addEvent('load', function(e) {
				var result = {};
				if(this.iframe.contentWindow) {
					result = this.iframe.contentWindow.document.body?this.iframe.contentWindow.document.body.innerHTML:null;
				} else if(io.contentDocument) {
					result = this.iframe.contentDocument.document.body?this.iframe.contentDocument.document.body.innerHTML:null;
				}
				this.dispose();
				this.fireEvent('success', eval("data = " + result));
			}.bind(this));
		}
		iframe.inject(this.body);
		return iframe;
	},
	upload : function() {
		this.form.submit();
	},
	onSuccess : function(result) {
		alert(result.message);
	},
	dispose : function() {
		this.file.inject(this.filePosition, 'before');
		if ($chk(this.form)) {
			this.form.dispose();
		}
		
		//this.iframe.dispose();
	}
});