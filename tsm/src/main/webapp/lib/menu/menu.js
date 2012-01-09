var JIM = JIM ? JIM : {};
JIM.UI = JIM.UI ? JIM.UI : {};

// 菜单控件
JIM.UI.TopMenu = new Class( {

	Implements : [ Events, Options ],

	options : {
		url : '' // 后台URL
	},

	initialize : function(container, options) {
		this.setOptions(options);
		this.container = $(container);
		this.loader = new Request.JSON( {
			url : this.options.url,
			onSuccess : this.onSuccess.bind(this)
		});
		this.params = {};
	},
	draw : function(id, name, url) {
		var menu = new Element('a')
		if ($chk(this.container.getLast('a'))) {
			menu.inject(this.container.getLast('a'), 'after');
		} else {
			menu.inject(this.container, 'top');
		}
		menu.set('href', url);
		if (getQueryValue('menu') == id) {
			menu.addClass('butt1');
			this.selectedMenu = menu;
		} else {
			menu.addClass('butt');
		}
		var menuText = new Element('span').inject(menu);
		menuText.set('html', name);
		new Element('input', {
			'type' : 'hidden',
			'value' : id
		}).inject(menu);
		var topMenu = this;
		menu.addEvent('click', function(e) {
			var url = topMenu.getMenuId(this);
			this.set('href', url);
		});
	},
	getMenuId : function(menu) {
		var url = menu.get('href');
		var menuId = menu.getElement('input[type=hidden]').get('value')
		if (url.indexOf("?") == -1 || url.indexOf('=') == -1) {
			url += '?menu=' + menuId;
		} else {
			url += '&menu=' + menuId;
		}
		return ctx + url;
	},
	load : function() {
		this.params.parentId = '';
		this.loader.get(this.params);
	},
	onSuccess : function(data) {
		if (data.success) {
			if (data.totalCount == null) {
				self.location = data.message;
			}
			$each(data.result, function(result, i) {
				this.draw(result.id, result.menuName, result.url);
			}.bind(this));
			if (!$chk(this.selectedMenu)) {
				var firstMenu = this.container.getElement('a');
				firstMenu.addClass('butt1');
				self.location = this.getMenuId(firstMenu);
			}
		}
	}
});

JIM.UI.TreeMenu = new Class( {

	Implements : [ Events, Options ],

	options : {
		url : '', // 后台URL
		image : {
			menu_open : '/lib/menu/icon_6.png',
			menu_close : '/lib/menu/icon_7.png'
		}
	},

	initialize : function(container, options) {
		this.setOptions(options);
		this.container = $(container);
		this.loader = new Request.JSON( {
			url : this.options.url,
			onSuccess : this.onSuccess.bind(this)
		});
		this.params = {};
	},
	draw : function(id, name, url) {
		var div = new Element('div').inject(this.container);
		div.addClass('leftcontt');
		var img = new Element('img', {'src' : $chk(ctx) ? ctx + this.options.image.menu_close : this.options.image.menu_close}).inject(div);
		img.addClass('icon16');
		var span = new Element('span', {'html' : name}).inject(div);
		span.setStyle('cursor', 'pointer');
		new Element('input', {'name' : 'divId', 'type' : 'hidden', 'value' : id}).inject(div);
		new Element('input', {'name' : 'isFirst', 'type' : 'hidden', 'value' : true}).inject(div);
		new Element('input', {'name' : 'isOpen', 'type' : 'hidden', 'value' : false}).inject(div);
		var params = {};
		params.parentId = div.getElement('input[name=divId]').get('value');
		this.loadChildMenu(div, params);
		var menu = this;
		span.addEvent('click', function() {
			var isFirst = this.getElement('input[name=isFirst]').get('value');
			var isOpen = this.getElement('input[name=isOpen]').get('value');
			if (isFirst == 'true') {
				var params = {};
				params.search_ALIAS_parentI_EQL_id = this.getElement('input[name=divId]').get('value');
				params.page_orderBy = 'orderNo_asc';
				params.page_pageSize = 1000000;
				menu.loadChildMenu(div, params);
			} else {
				if (isOpen == 'true') {
					this.getNext('dl').setStyle('display', 'none');
					this.getElement('img').set('src', ($chk(ctx) ? ctx : '') + menu.options.image.menu_close);
					this.getElement('input[name=isOpen]').set('value', false);
				} else {
					this.getNext('dl').setStyle('display', '');
					this.getElement('img').set('src', ($chk(ctx) ? ctx : '') + menu.options.image.menu_open);
					this.getElement('input[name=isOpen]').set('value', true);
				}
			}
		}.bind(div));
	},
	load : function() {
		if ($chk(getQueryValue('menu'))) {
			this.params.parentId = getQueryValue('menu');
			this.params.t = new Date().valueOf();
			this.loader.get(this.params);
		};
	},
	loadChildMenu : function(parent, params) {
		params.t = new Date().valueOf();
		new Request.JSON( {
			url : this.options.url,
			onSuccess : function(data) {
				if (data.success) {
					if (data.totalCount > 0) {
						var dl = new Element('dl').inject(parent, 'after');
						$each(data.result, function(result, i) {
							var dd = new Element('dd').inject(dl);
							var menuUrl = '';
							if (result.url.indexOf("?") == -1 || result.url.indexOf('=') == -1) {
								menuUrl = result.url + '?menu=' + getQueryValue("menu");
							} else {
								menuUrl += result.url + '&menu=' + getQueryValue("menu");
							}
							new Element('a', {'href' : ctx + menuUrl, 'html' : result.menuName}).inject(dd);
						});
						parent.getElement('input[name=isFirst]').set('value', false);
						parent.getElement('input[name=isOpen]').set('value', true);
						parent.getElement('img').set('src', ($chk(ctx) ? ctx : '') + this.options.image.menu_open);
					} else {
						parent.getElement('input[name=isFirst]').set('value', true);
						parent.getElement('input[name=isOpen]').set('value', false);
						parent.getElement('img').set('src', ($chk(ctx) ? ctx : '') + this.options.image.menu_open);
					}
				}
			}.bind(this)
		}).get(params);
	},
	onSuccess : function(data) {
		if (data.success) {
			$each(data.result, function(result, i) {
				this.draw(result.id, result.menuName, result.url);
			}.bind(this));
		}
	}
});

function getQueryValue(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) {
		return unescape(r[2]);
	}
	return "";
}