var JIM = JIM ? JIM : {};
JIM.UI = JIM.UI ? JIM.UI : {};

// 菜单控件
JIM.UI.MultipleSelect = new Class( {

	Implements : [ Events, Options ],

	options : {
		width : '600px',
		height : '230px',
		leftTitle : '待选择：',
		rightTitle : '已选中：',
		store : {
			remote : false,
			leftData : {},
			rightData : {},
			leftUrl : '',
			rightUrl : ''
		},
		buttons : {
			allRight : true,
			allLeft : true,
			right : true,
			left : true,
			up : true,
			down : true,
			top : true,
			bottom : true
		},
		image : {
			allRight : '/lib/multipleselect/right_d.gif',
			allLeft : '/lib/multipleselect/left_d.gif',
			right : '/lib/multipleselect/right.gif',
			left : '/lib/multipleselect/left.gif',
			up : '/lib/multipleselect/up.gif',
			down : '/lib/multipleselect/dn.gif',
			top : '/lib/multipleselect/up_d.gif',
			bottom : '/lib/multipleselect/dn_d.gif'
		}
	},

	initialize : function(container, options) {
		this.setOptions(options);
		var a = $('selectDiv');
		this.container = $(container);
		this.draw();
		this.addEvents();
		this.loadLeftData();
		this.loadRightData();
	},
	draw : function() {
		this.table = new Element('table', {
			width : this.options.width,
			border : 0,
			align : 'center',
			cellpadding : 1,
			cellspacing : 1
		}).inject(this.container);
		var tbody = this.table.getElement('tbody');
		if (!$chk(tbody)) {
			tbody = new Element('tbody').inject(this.table);
		}
		var tr = new Element('tr').inject(tbody);
		var leftSelectTd = new Element('td', {
			width : '50%',
			align : 'center'
		}).inject(tr);
		var operateTd = new Element('td', {
			width : '30px',
			align : 'center',
			styles : {'padding-top': '25px'}
		}).inject(tr);
		var rightSelectTd = new Element('td', {
			width : '50%',
			align : 'center'
		}).inject(tr);
		leftSelectTd.appendText(this.options.leftTitle);
		rightSelectTd.appendText(this.options.rightTitle);
		this.leftSelect = new Element('select', {
			styles : {
				width : '98%',
				height : this.options.height
			},
			size : 6,
			multiple : "multiple"
		}).inject(leftSelectTd);
		this.rightSelect = new Element('select', {
			styles : {
				width : '98%',
				height : this.options.height
			},
			size : 6,
			multiple : "multiple"
		}).inject(rightSelectTd);
		if (this.options.buttons.allRight) {
			this.allRightMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.allRight + ') center no-repeat'
				},
				title : '全部右移'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.right) {
			this.rightMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.right + ') center no-repeat'
				},
				title : '右移'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.left) {
			this.leftMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.left + ') center no-repeat'
				},
				title : '左移'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.allLeft) {
			this.allLeftMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.allLeft + ') center no-repeat'
				},
				title : '全部左移'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.top) {
			this.topMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.top + ') center no-repeat'
				},
				title : '移到顶部'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.up) {
			this.upMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.up + ') center no-repeat'
				},
				title : '上移'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.down) {
			this.downMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.down + ') center no-repeat'
				},
				title : '下移'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
		if (this.options.buttons.bottom) {
			this.bottomMove = new Element('button', {
				styles : {
					width : '25px',
					height : '25px',
					'background' : 'url(' + ctx + this.options.image.bottom + ') center no-repeat'
				},
				title : '移到底部'
			}).inject(operateTd);
			new Element('br').inject(operateTd);
		}
	},
	addEvents : function() {
		if ($chk(this.rightMove)) {
			this.rightMove.addEvent('click', function() {
				this.moveLeftOrRight(this.leftSelect, this.rightSelect);
			}.bind(this));
		}
		if ($chk(this.leftMove)) {
			this.leftMove.addEvent('click', function() {
				this.moveLeftOrRight(this.rightSelect, this.leftSelect);
			}.bind(this));
		}
		if ($chk(this.allRightMove)) {
			this.allRightMove.addEvent('click', function() {
				this.moveLeftOrRightAll(this.leftSelect, this.rightSelect);
			}.bind(this));
		}
		if ($chk(this.allLeftMove)) {
			this.allLeftMove.addEvent('click', function() {
				this.moveLeftOrRightAll(this.rightSelect, this.leftSelect);
			}.bind(this));
		}
		if ($chk(this.topMove)) {
			this.topMove.addEvent('click', function() {
				this.moveToTop(this.rightSelect);
			}.bind(this));
		}
		if ($chk(this.bottomMove)) {
			this.bottomMove.addEvent('click', function() {
				this.moveToBottom(this.rightSelect);
			}.bind(this));
		}
		if ($chk(this.upMove)) {
			this.upMove.addEvent('click', function() {
				this.moveUp(this.rightSelect);
			}.bind(this));
		}
		if ($chk(this.downMove)) {
			this.downMove.addEvent('click', function() {
				this.moveDown(this.rightSelect);
			}.bind(this));
		}
	},
	loadLeftData : function() {
		var time = new Date().valueOf();
		if (this.options.store.remote) {
			if ($chk(this.options.store.leftUrl)) {
				new Request.JSON( {
					url : this.options.store.leftUrl,
					onSuccess : function(data) {
						if (data.success) {
							this.options.store.leftData = data.message;
							for ( var key in this.options.store.leftData) {
								this.leftSelect.options.add(new Option(this.options.store.leftData[key], key));
							}
						} else {
							new LightFace.MessageBox().error(data.message);
						}
					}.bind(this)
				}).get({t : time});
			}
		} else {
			if ($chk(this.options.store.leftData)) {
				for ( var key in this.options.store.leftData) {
					this.leftSelect.options.add(new Option(this.options.store.leftData[key], key));
				}
			}
		}
	},
	loadRightData : function() {
		if (this.options.store.remote) {
			if ($chk(this.options.store.rightUrl)) {
				var time = new Date().valueOf();
				new Request.JSON( {
					url : this.options.store.rightUrl,
					onSuccess : function(data) {
						if (data.success) {
							this.options.store.rightData = data.message;
							for ( var key in this.options.store.rightData) {
								this.rightSelect.options.add(new Option(this.options.store.rightData[key], key));
							}
						} else {
							new LightFace.MessageBox().error(data.message);
						}
					}.bind(this)
				}).get({t : time});
			}
		} else {
			if ($chk(this.options.store.rightData)) {
				for ( var key in this.options.store.rightData) {
					this.rightSelect.options.add(new Option(this.options.store.rightData[key], key));
				}
			}
		}
	},
	reload : function() {
		this.options.store.leftData = {};
		this.options.store.rightData = {};
		for ( var i = 0; i < this.leftSelect.options.length; i++) {
			this.leftSelect.remove(i);
		}
		for ( var i = 0; i < this.rightSelect.options.length; i++) {
			this.rightSelect.remove(i);
		}
		this.loadLeftData();
		this.loadRightData();
	},
	// 获得所有已选中的value
	getSelectedOption : function() {
		this.selectValues = [];
		this.selectTexts = [];
		if (this.rightSelect && this.rightSelect.options && this.rightSelect.options.length > 0) {
			var len = this.rightSelect.options.length;
			for ( var j = 0; j < len; j++) {
				this.selectValues[j] = this.rightSelect.options[j].value;
				this.selectTexts[j] = this.rightSelect.options[j].text;
			}
		}
		return this.selectValues;
	},
	// 选中项向左移动或向右移动
	moveLeftOrRight : function(fromObj, toObj) {
		var fromObjOptions = fromObj.options;
		for ( var i = 0; i < fromObjOptions.length; i++) {
			if (fromObjOptions[i].selected) {
				toObj.appendChild(fromObjOptions[i]);
				i--;
			}
		}
	},
	// 左边全部右移动，或右边全部左移
	moveLeftOrRightAll : function(fromObj, toObj) {
		var fromObjOptions = fromObj.options;
		for ( var i = 0; i < fromObjOptions.length; i++) {
			fromObjOptions[0].selected = true;
			toObj.appendChild(fromObjOptions[i]);
			i--;
		}
	},
	// 向上移动
	moveUp : function(selectObj) {
		var theObjOptions = selectObj.options;
		for ( var i = 1; i < theObjOptions.length; i++) {
			if (theObjOptions[i].selected && !theObjOptions[i - 1].selected) {
				this.swapOptionProperties(theObjOptions[i], theObjOptions[i - 1]);
			}
		}
	},
	// 向下移动
	moveDown : function(selectObj) {
		var theObjOptions = selectObj.options;
		for ( var i = theObjOptions.length - 2; i > -1; i--) {
			if (theObjOptions[i].selected && !theObjOptions[i + 1].selected) {
				this.swapOptionProperties(theObjOptions[i], theObjOptions[i + 1]);
			}
		}
	},
	// 移动至最顶端
	moveToTop : function(selectObj) {
		var theObjOptions = selectObj.options;
		var oOption = null;
		for ( var i = 0; i < theObjOptions.length; i++) {
			if (theObjOptions[i].selected && oOption) {
				selectObj.insertBefore(theObjOptions[i], oOption);
			} else if (!oOption && !theObjOptions[i].selected) {
				oOption = theObjOptions[i];
			}
		}
	},
	// 移动至最低端
	moveToBottom : function(selectObj) {
		var theObjOptions = selectObj.options;
		var oOption = null;
		for ( var i = theObjOptions.length - 1; i > -1; i--) {
			if (theObjOptions[i].selected) {
				if (oOption) {
					oOption = selectObj.insertBefore(theObjOptions[i], oOption);
				} else
					oOption = selectObj.appendChild(theObjOptions[i]);
			}
		}
	},
	// 全部选中
	selectAllOption : function(selectObj) {
		var theObjOptions = selectObj.options;
		for ( var i = 0; i < theObjOptions.length; i++) {
			theObjOptions[0].selected = true;
		}
	},
	/* private function */
	swapOptionProperties : function(option1, option2) {
		// option1.swapNode(option2);
		var tempStr = option1.value;
		option1.value = option2.value;
		option2.value = tempStr;

		var tempValSource = option1.valSource;//
		option1.valSource = option2.valSource;//
		option2.valSource = tempValSource;//

		tempStr = option1.text;
		option1.text = option2.text;
		option2.text = tempStr;
		tempStr = option1.selected;
		option1.selected = option2.selected;
		option2.selected = tempStr;
	},
	resetAutoWidth : function(obj) {
		var tempWidth = obj.style.getExpression("width");
		if (tempWidth != null) {
			obj.style.width = "auto";
			obj.style.setExpression("width", tempWidth);
			obj.style.width = null;
		}
	}
});
