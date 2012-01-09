var JIM = JIM ? JIM : {};
JIM.UI = JIM.UI ? JIM.UI : {};

// 翻页控件
JIM.UI.Paging = new Class( {
	/*
	 * Events beforeLoad 读取数据前执行 afterLoad 读取数据后执行
	 */
	Implements : [ Events, Options ],
	options : {
		url : '', // 后台URL
		className : 'paging',
		total : 'totalCount', // 数据源中记录数属性
		result : 'result',
		limit : 10, // 每页记录数
		// el:控件容器,showNumber:是否显示数字按钮,showText:是否显示页码
		head : {
			el : document.body,
			showNumber : false,
			showText : true
		},
		foot : {
			el : null,
			showNumber : true,
			showText : false
		}
	},

	initialize : function(options) {
		this.setOptions(options);

		this.index = 0; // 当前页
		this.limit = this.options.limit;

		this.head = new Element('span', {
			'class' : this.options.className
		});
		this.head.injectInside($(this.options.head.el));

		this.loader = new Request.JSON( {
			url : this.options.url,
			onComplete : this.onComplete.bind(this)
		});

		this.param = {}; // load参数

		if (this.options.foot.el) {
			this.foot = this.head.clone();
			this.foot.injectInside($(this.options.foot.el));
		}
	},

	onComplete : function(data, text) {
		data = data || {};
		// if(this.index==0){
		this.total = data[this.options.total] || 0; // 总记录数
		// }
		if (data[this.options.total] <= this.limit) {
			this.index = 0;
		}
		//如果总条数大于0，但是返回的结果却为[]，则肯定是起始页的问题，修正index重新取数据
		if (this.total > 0 && data[this.options.result].length == 0) {
			this.index = this.index - 1;
			this.load(this.queryParam);
		} else {
			// if(this.total == 0){
			// this.total = data.totalCount || 0; //把返回的page里记录的总条数设进去
			// }
			this.page = Math.ceil(this.total / this.limit); // 总页数

			this.create(this.head, this.options.head);

			if (this.foot) {
				this.create(this.foot, this.options.foot);
			}
			this.fireEvent('afterLoad', [ data, text ]);
		}
	},

	create : function(panel, options) {
		panel.empty();

		if (this.index > 0) {
			var prev = new Element('a', {
				'html' : '&nbsp上一页',
				'class' : 'prev',
				'href' : 'javascript:void(null)',
				'events' : {
					'click' : this.click.bind(this, this.index - 1)
				}
			});
			panel.grab(prev);
		}

		if (options.showNumber) {
			var beginInx = this.index - 2 < 0 ? 0 : this.index - 2;
			var endIdx = this.index + 2 > this.page ? this.page : this.index + 2;

			if (beginInx > 0)
				panel.grab(this.createNumber(0));
			if (beginInx > 1)
				panel.grab(this.createNumber(1));
			if (beginInx > 2)
				panel.grab(this.createSplit());

			for ( var i = beginInx; i < endIdx; i++) {
				panel.grab(this.createNumber(i));
			}

			if (endIdx < this.page - 2)
				panel.grab(this.createSplit());
			if (endIdx < this.page - 1)
				panel.grab(this.createNumber(this.page - 2));
			if (endIdx < this.page)
				panel.grab(this.createNumber(this.page - 1));
		}

		if (this.index < this.page - 1) {
			var next = new Element('a', {
				'html' : '下一页',
				'class' : 'next',
				'href' : 'javascript:void(null)',
				'events' : {
					'click' : this.click.bind(this, this.index + 1)
				}
			});
			panel.grab(next);
		}

		if (options.showText)
			panel.grab(this.createText());
	},

	createNumber : function(i) {
		var a = new Element('a', {
			'html' : i + 1,
			'href' : 'javascript:void(null)',
			'events' : {
				'click' : this.click.bind(this, i)
			}
		});
		if (i == this.index) {
			a.addClass('clicked');
		}
		return a;
	},

	createSplit : function() {
		var split = new Element('span', {
			'html' : '...'
		});
		return split;
	},

	createText : function() {
		var text = new Element('span');
		if (this.page == 0) {
			text.set('html', '没有查询到相符的记录');
			text.setStyle('color', 'red');
		} else {
			text.set('html', '第' + (this.index + 1) + '/' + (this.page) + '页' + '&nbsp;共' + this.total + '条记录');
		}
		return text;
	},

	click : function(index) {
		this.index = index;
		this.load();
	},

	load : function(param) {
		this.fireEvent('beforeLoad');
		// this.param.start = this.index*this.limit;
		// this.param.limit = this.limit;
		this.param.page_pageNo = this.index + 1;
		this.param.page_pageSize = this.limit;
		if ($chk(param)) {
			this.queryParam = param;
		}
		var paramString = '';
		for ( var key in this.param) {
			paramString += '&';
			paramString += key;
			paramString += '=';
			paramString += this.param[key];
		}
		var timestamp = (new Date()).valueOf();
		paramString += '&t=' + timestamp;
		// if(param) this.param = $merge(this.param,param);
		this.loader.post(this.queryParam + paramString);
	},

	reload : function(param) {
		this.index = 0;
		this.load(param);
	},

	setLimit : function(limit) {
		this.limit = limit;
		this.reload();
	},
	getLimit : function() {
		return this.limit;
	} ,
	getPageNo : function() {
		return this.index;
	},
	getTotal : function() {
		return this.total;
	}
});