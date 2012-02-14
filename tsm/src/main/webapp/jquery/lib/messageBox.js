function showMessage(message) {
	var messageBox = $('<div>').appendTo($('body'));
	messageBox.attr('title', '提示');
	messageBox.css('display', 'none');
	messageBox.append(message);
	messageBox.dialog({
		resizable: false,
		modal: true,
		buttons: {
			"确定" : function() {
				$(this).dialog( "close" );
			}
		},
		open: function() {
	        var btn = $('.ui-dialog-buttonpane').find('button:contains("确定")');
	        btn.prepend('<span style="float:left; margin-top: 5px;" class="ui-icon ui-icon-circle-check"></span>');
	        btn.width(btn.width() + 20);
	        btn.css('padding-left', '10px');
	    },
	    close: function() {
	    	messageBox.remove();
	    }
	});
}

function showResult(response, postdata) {
	var message = eval('data='+response.responseText);
	if (message.success) {
		showMessage(message.message);
		return[true, ""];
	} else {
		return[false, message.message];
	}
}

function showError(message) {
	var messageBox = $('<div>').appendTo($('body'));
	messageBox.attr('title', '错误');
	messageBox.css('display', 'none');
	messageBox.append(message);
	messageBox.dialog({
		resizable: false,
		modal: true,
		buttons: {
			"确定" : function() {
				$(this).dialog( "close" );
			}
		},
		open: function() {
	        var btn = $('.ui-dialog-buttonpane').find('button:contains("确定")');
	        btn.prepend('<span style="float:left; margin-top: 5px;" class="ui-icon ui-icon-circle-check"></span>');
	        btn.width(btn.width() + 20);
	        btn.css('padding-left', '10px');
	    },
	    close: function() {
	    	messageBox.remove();
	    }
	});
}

function showConfirm(message, callback) {
	var messageBox = $('<div>').appendTo($('body'));
	messageBox.attr('title', '提示');
	messageBox.css('display', 'none');
	messageBox.append(message);
	messageBox.dialog({
		resizable: false,
		modal: true,
		buttons: {
			"是" : function() {
				callback(true);
				$(this).dialog( "close" );
			},
			"否" : function() {
				callback(false);
				$(this).dialog( "close" );
			}
		},
		open: function() {
	        var btn = $('.ui-dialog-buttonpane').find('button:contains("是")');
	        btn.prepend('<span style="float:left; margin-top: 5px;" class="ui-icon ui-icon-circle-check"></span>');
	        btn.width(btn.width() + 20);
	        btn.css('padding-left', '10px');
	        var btn2 = $('.ui-dialog-buttonpane').find('button:contains("否")');
	        btn2.prepend('<span style="float:left; margin-top: 5px;" class="ui-icon ui-icon-circle-close"></span>');
	        btn2.width(btn2.width() + 20);
	        btn2.css('padding-left', '10px');
	    },
	    close: function() {
	    	messageBox.remove();
	    }
	});
}

function showPrompt(message, callback) {
	var messageBox = $('<div>').appendTo($('body'));
	messageBox.attr('title', '提示');
	messageBox.css('display', 'none');
	messageBox.append(message);
	messageBox.append($('<br>'));
	messageBox.append($('<textarea style="width:270px;"></textarea>'));
	messageBox.dialog({
		resizable: false,
		modal: true,
		buttons: {
			"确定" : function() {
				callback(messageBox.find('textarea').val());
				$(this).dialog( "close" );
			},
			"取消" : function() {
				$(this).dialog( "close" );
			}
		},
		open: function() {
	        var btn = $('.ui-dialog-buttonpane').find('button:contains("确定")');
	        btn.prepend('<span style="float:left; margin-top: 5px;" class="ui-icon ui-icon-circle-check"></span>');
	        btn.width(btn.width() + 20);
	        btn.css('padding-left', '10px');
	        var btn2 = $('.ui-dialog-buttonpane').find('button:contains("取消")');
	        btn2.prepend('<span style="float:left; margin-top: 5px;" class="ui-icon ui-icon-circle-close"></span>');
	        btn2.width(btn2.width() + 20);
	        btn2.css('padding-left', '10px');
	    },
	    close: function() {
	    	messageBox.remove();
	    }
	});
}