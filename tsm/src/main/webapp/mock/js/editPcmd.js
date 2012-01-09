window.addEvent('domready', function() {
	var id = getQueryValue('id');
	if ($chk(id)) {
		var time = new Date().valueOf();
		new Request.JSON( {
			url : ctx + '/html/pcmd/?m=getPCmd',
			onSuccess : function(data) {
				if (data.success) {
					var inputs = $('pcmdForm').getElements('input[type=text],input[type=hidden],textarea');
					$each(inputs, function(input, i) {
						input.set('value', data.message[input.get('name')]);
					});
				} else {
					alert(data.message);
				}
			}.bind(this)
		}).get({cmdId : id, t : time});
	}
	$('saveButton').addEvent('click', function(e) {
		e.stop();
		var pcmdId = $('pcmdId').get('value');
		var url = '';
		if ($chk(pcmdId)) {
			url = ctx + '/html/pcmd/?m=update'
		} else {
			url = ctx + '/html/pcmd/?m=add'
		}
		var inputs = $('pcmdForm').getElements('input[type=text],textarea');
		var errorMessage = [];
		$each(inputs, function(input, i) {
			var value = input.get('value');
			if (!$chk(value)) {
				errorMessage.push('\n' + input.get('title') + '不能为空');
			}
		});
		if ($chk(errorMessage) && errorMessage.length > 0) {
			alert(errorMessage);
		} else {
			new Request.JSON( {
				url : url,
				onSuccess : function(data) {
					if (data.success) {
						alert(data.message);
						self.location = ctx + '/mock/pcmd.jsp';
					} else {
						alert(data.message);
					}
				}.bind(this)
			}).post($('pcmdForm').toQueryString());
		}
	});
	$('returnButton').addEvent('click', function() {
		self.location = ctx + '/mock/pcmd.jsp';
	});
});

function getQueryValue(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) {
		return unescape(r[2]);
	}
	return "";
}