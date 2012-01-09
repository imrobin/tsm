var JIM = JIM ? JIM : {};

/**
 * 例子: var webservice = new JIM.Webservice({ url :
 * 'http://localhost:8080/tsm/services/MobileWebService?wsdl', method : 'Login',
 * onSuccess : function(json) {
 * alert(json.LoginResponse.Status.statusCode.value); } });
 * webservice.send({userName:'admin', password:'admin'});
 */
JIM.Webservice = new Class(
		{

			Implements : [ Events, Options ],
			options : {
				url : '', // 后台wsdl地址
				method : '',
				params : {},
				nameSpace : 'http://www.chinamobile.com',
				prefix : 'ns'
			/*
			 * onSuccess : function(json) { //do something }
			 * 
			 */
			},
			initialize : function(options) {
				this.setOptions(options);
				this.request = new Request({
					url : this.options.url,
					onSuccess : function(responseText, responseXML) {
						this.fireEvent('success', this.xmlToJson(responseXML));
					}.bind(this),
					onFailure : function() {
						this.fireEvent('failure', '网络异常');
					}.bind(this)
				});
			},

			send : function(params) {
				var theEnvelope = this.getSOAPEnvelope(this.options.method, this.options.nameSpace, params);
				this.request.post(theEnvelope);
			},

			getSOAPEnvelope : function(WebMethod, Namespace, param) {
				var envelope = '';
				envelope += '<?xml version="1.0" encoding="utf-8"?>\n';
				envelope += '<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">\n';
				envelope += '  <soap:Body>\n';
				var paramHash = new Hash(param);
				if (paramHash.getKeys().length > 0) {
					envelope += '    <' + this.options.prefix + ':' + WebMethod + ' xmlns:' + this.options.prefix + '="' + Namespace
							+ '">\n';
					envelope += this.jsonToXml(param, 1, this);
					envelope += '    </' + this.options.prefix + ':' + WebMethod + '>\n';
				} else {
					envelope += '    <' + this.options.prefix + ':' + WebMethod + ' xmlns:' + this.options.prefix + '="' + Namespace
							+ '" />\n';
				}
				envelope += '  </soap:Body>\n';
				envelope += '</soap:Envelope>';
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
					envelope += space + '    <' + obj.options.prefix + ':' + key + '>';
					if (window.Array == value.constructor && value.length > 0) {
						envelope += '\n';
						value.each(function(item) {
							envelope += obj.jsonToXml(item, index + 1, obj);
						});
						envelope += space + '    </' + obj.options.prefix + ':' + key + '>\n';
					} else if (value == '[object Object]') {
						envelope += '\n';
						envelope += obj.jsonToXml(value, index + 1, obj);
						envelope += space + '    </' + obj.options.prefix + ':' + key + '>\n';
					} else {
						envelope += value;
						envelope += '</' + obj.options.prefix + ':' + key + '>\n';
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
			}

		});