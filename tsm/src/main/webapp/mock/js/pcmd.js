window.addEvent('domready', function() {
	$('searchButton').addEvent('click', function() {
		var time = new Date().valueOf();
		var aid = $('appAidInput').get('value');
		var param = {
			appAid : $('appAidInput').get('value'),
			page_pageSize : 10000,
			page_orderBy : 'appAid_asc,batch_asc,cmdIndex_asc',
			t : time
		};
		loadCmd(param);
	});
	$('newButton').addEvent('click', function() {
		self.location = ctx + '/mock/editPcmd.jsp';
	});
});

function edit(id) {
	self.location = ctx + '/mock/editPcmd.jsp?id=' + id;
}

function remove(id) {
	var time = new Date().valueOf();
	new Request.JSON( {
		url : ctx + '/html/pcmd/?m=remove',
		onSuccess : function(data) {
			if (data.success) {
				alert(data.message);
				$('searchButton').click();
			} else {
				alert(data.message);
			}
		}.bind(this)
	}).get({cmdId : id, t : time});
}


function loadCmd(param) {
	new Request.JSON( {
		url : ctx + '/html/pcmd/?m=index',
		onSuccess : function(data) {
			if (data.success) {
				var tbody = $('pcmdTable').getElement('tbody');
				var clonetbody = tbody.clone();
				tbody.empty();
				var trs = clonetbody.getElements('tr');
				$each(trs, function(tr, i) {
					if (i < 2) {
						tr.inject(tbody);
					}
				});
				$each(data.result, function(result, i) {
					var tr = tbody.getLast('tr');
					var tds = tr.getChildren('td');
					$each(tds, function(td, j) {
						var html = result[td.title];
						if ($chk(html)) {
							if (td.get('title') == 'type') {
								if (html == 1) {
									td.set('html', '写数据');
								} else if (html == 2) {
									td.set('html', '读数据');
								} else if (html == 3) {
									td.set('html', '删数据');
								} else {
									td.set('html', '错误的类型');
								}
							}
							td.set('html', html);
						} else {
							td.set('html', '&nbsp;');
						}
						if (td.get('title') == 'option') {
							var optionButtons = td.getElements('a');
							$each(optionButtons, function(optionButton, i) {
								if (optionButton.get('name') == 'editButton') {
									optionButton.set('href', 'JavaScript:edit(' + result.id + ')');
								} else if (optionButton.get('name') == 'delButton') {
									optionButton.set('href', 'JavaScript:remove(' + result.id + ')');
								}
							});
						}
					});
					tr.clone().inject(tbody);
				});
				tbody.getLast('tr').dispose();
			} else {
				alert(data.message);
			}
		}.bind(this)
	}).post(param);
}