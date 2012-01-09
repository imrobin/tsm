var JIM = JIM ? JIM : {};
JIM.UI = JIM.UI ? JIM.UI : {};

// 列表控件
JIM.UI.Grid = new Class( {
	/*
	 * Events beforeLoad 读取数据前执行 afterLoad 读取数据后执行
	 */
	Implements : [ Events, Options ],
	options : {
		url : '', // 后台URL
		contextPath : '',
		pagination : true,
		pageSize : 10, // 每页记录数
		multipleSelection : true,
		selection : true,
		header : true,
		headerImage : null,
		headerText : '',
		buttons : null,
		columnModel : null,
		searchBar : null,
		searchButton : false,
		width : 'auto',
		height : 'auto',
		order : true,
		mask : true,
		autoLoad : true,
		image : {
			order_up : '/lib/grid/up.png',
			order_down : '/lib/grid/down.png',
			header : '/lib/grid/icon_8.png',
			button : '/lib/grid/icon_9.png'
		}
	},

	initialize : function(container, options) {
		this.setOptions(options);
		for ( var key in this.options.image) {
			this.options.image[key] = this.options.contextPath + this.options.image[key];
		}
		this.container = $(container);
		if (!$chk(this.container)) {
			alert('请指定div');
		}
		this.selectIds = new Array();
		this.draw();
		if (this.options.autoLoad) {
			this.load();
		}
	},
	draw : function() {
		this.container.empty();

		if ($chk(this.options.width)) {
			this.container.setStyle('width', this.options.width);
		}

		if ($chk(this.options.header)) {
			this.container.addClass('rightcont');
			this.drawHearder();
		}

		if ($chk(this.options.searchBar)) {
			this.drawSearchBar();
		}

		if ($chk(this.options.buttons) && this.options.buttons.length > 0) {
			this.drawButtons();
		}

		var columnCount = $chk(this.options.columnModel) ? this.options.columnModel.length : 0;

		this.drawTable(columnCount);

	},
	drawHearder : function() {
		// 创建标题层
		this.headDiv = new Element('div').inject(this.container, 'top');
		this.headDiv.addClass('title');
		// 创建标题层图片
		var image = new Element('img');
		image.addClass('icon16');
		if ($chk(this.options.headerImage)) {
			image.set('src', this.options.headerImage);
		} else {
			image.set('src', $chk(ctx) ? ctx : '' + this.options.image.header);
		}
		image.inject(this.headDiv, 'top');
		// 创建标题层文字
		if ($chk(this.options.headerText)) {
			this.headDiv.appendText(this.options.headerText);
		} else {
			this.headDiv.appendText('管理');
		}
	},
	drawSearchBar : function() {
		this.barDiv = new Element('div').inject(this.container);
		this.barDiv.addClass('opertop');

		var bar = new Element('p').inject(this.barDiv);
		bar.addClass('search');
		this.searchForm = new Element('form').inject(bar);
		var filters = this.options.searchBar.filters;
		for ( var i = 0; i < filters.length; i++) {
			var filter = filters[i];
			if (i > 0) {
				this.searchForm.appendText(' ');
			}
			this.searchForm.appendText(filter.title);
			var input = null;
			if (filter.type == 'select') {
				input = new Element('select').inject(this.searchForm);
				if ($chk(filter.data)) {
					var isNullKey = false;
					for ( var key in filter.data) {
						if (!$chk(key)) {
							isNullKey = true;
						}
						input.options.add(new Option(filter.data[key], key));
					}
					if (isNullKey) {
						input.set('value', '');
					}
				}
				if (filter.remote) {
					input.addEvent('load', function(url, param) {
						this.options.add(new Option('全部', ''));
						if ($chk(url)) {
							new Request.JSON( {
								url : url,
								onSuccess : function(data) {
									if (data.success) {
										$each(data.message, function(message, i) {
											this.options.add(new Option(message.value, message.key));
										}.bind(this));
									} else {
										new LightFace.MessageBox().error(data.message);
									}
								}.bind(this)
							}).get(param);
						}
					});
				}
			} else {
				var input = new Element('input').inject(this.searchForm);
				input.set('type', filter.type);
			}
			if ($chk(filter.name)) {
				input.set('name', filter.name);
			}
			if ($chk(filter.id)) {
				input.set('id', filter.id);
			}
			if ($chk(filter.width)) {
				input.setStyle('width', filter.width);
			}
			if ($chk(filter.validates)) {// 校验转换
				this.searchMessage = {};
				$each(filter.validates, function(val, i) {
					input.addClass("validate['" + val.regexp + "']");
					if ($chk(val.message)) {
						this.searchMessage[val.regexp] = val.message;
					}
				}.bind(this));
			}
		}
		new FormCheck(this.searchForm, {
			submit : false,
			display : {
				showErrors : 0,
				indicateErrors : 1,
				scrollToFirst : false
			},
			alerts : this.searchMessage,
			onValidateSuccess : function() {// 校验通过执行load()
				this.cleanSelectIds();
				this.Pagination.index = 0;
				this.load();
			}.bind(this)
		});

		if (this.options.searchButton) {
			var searchBar = new Element('input', {
				type : 'submit',
				value : '',
				'title' : '查询'
			}).inject(this.searchForm);
			searchBar.addClass("validate['submit']");
			var refreshBar = new Element('button', {
				'class' : 's1',
				styles : {
					cursor : 'pointer'
				},
				'title' : '刷新'
			}).inject(this.searchForm);
			refreshBar.addEvent('click', function(e) {
				new Event(e).stop();
				var input = this.searchForm.getChildren();
				input.each(function(e, index) {
					var type = e.get('type');
					if (type == 'select-one') {
						e.getElements('option').each(function(o, i) {
							if (i == 0 || o.get('value') == '')
								o.set('selected', 'selected');
							else
								o.set('selected', '');
						});
					} else if (type == 'text') {
						e.set('value', '');
					}
				});
				// input.set('value', '');
				this.orderName = null;
				this.orderDirect = null;
				this.removeAllOrderImg();
				this.Pagination.index = 0;
				this.cleanSelectIds();
				var tip = document.getElement('div[class=fc-tbx]');
				if ($chk(tip)) {
					tip.dispose();
				}
				this.load();
			}.bind(this));
		}

	},
	removeAllOrderImg : function() {
		var ths = this.dataTable.getElement('tr').getElements('th');
		$each(ths, function(th, i) {
			var img = th.getElement('img');
			if ($chk(img)) {
				img.dispose();
			}
		});
	},
	drawButtons : function() {
		this.buttonDiv = new Element('div').inject(this.container);
		this.buttonDiv.addClass('opertop');

		var p = new Element('p').inject(this.buttonDiv);
		p.addClass('oper1');

		var bts = this.options.buttons;

		for ( var i = 0; i < bts.length; i++) {
			var bt = bts[i];
			var button = new Element('a', {
				'href' : '#',
				'class' : 'butt2'
			}).inject(p);
			if ($chk(bt.handler)) {
				button.addEvent('click', bt.handler.bind(this));
			}
			var span = new Element('span').inject(button);

			var image = new Element('img').inject(span);
			image.addClass('icon16');
			if ($chk(bt.icon)) {
				image.set('src', bt.icon);
			} else {
				image.set('src', $chk(ctx) ? ctx : '' + this.options.image.button);
			}

			span.appendText(bt.name);
		}
	},
	drawTable : function(columnCount) {
		this.tableDiv = new Element('div').inject(this.container);
		this.tableDiv.addClass('tableBox');
		this.dataTable = new Element('table', {
			'cellpadding' : 0,
			'cellspacing' : 0
		}).inject(this.tableDiv);
		var tbody = this.dataTable.getElement('tbody');
		if (!$chk(tbody)) {
			tbody = new Element('tbody').inject(this.dataTable);
		}
		var title = new Element('tr').inject(tbody);

		var thBox = new Element('th', {
			style : 'width: 30px'
		}).inject(title);
		if (this.options.multipleSelection) {
			this.checkBox = new Element('input', {
				'type' : 'checkbox'
			}).inject(thBox);
			this.checkBox.addEvent('click', function() {
				var checkBoxs = this.dataTable.getElements('input[type=checkbox][name=ids]');
				if (this.checkBox.checked) {
					$each(checkBoxs, function(checkBox, index) {
						checkBox.set('checked', true);
						this.selectIds.push(checkBox.get('value'));
						checkBox.getParent().getParent().setStyle('background-color', '#FFFACD');
					}.bind(this));
				} else {
					$each(checkBoxs, function(checkBox, index) {
						this.selectIds.empty();
						checkBox.set('checked', false);
						checkBox.getParent().getParent().setStyle('background-color', '');
					}.bind(this));
				}
			}.bind(this));
		} else {
			thBox.appendText(' ');
		}
		var grid = this;
		for ( var i = 0; i < columnCount; i++) {
			var th = null;
			if (!$chk(this.options.columnModel[i].identity)) {
				th = new Element('th', {
					'html' : this.options.columnModel[i].title
				}).inject(title);
			} else {
				continue;
			}
			if (this.options.order) {
				new Element('input', {
					'type' : 'hidden',
					'value' : this.options.columnModel[i].dataName
				}).inject(th);
				var order = true;
				order = this.options.columnModel[i].order;
				if (order === undefined) {
					order = true;
				}
				if (order) {
					th.setStyle('cursor', 'pointer');
					th.addEvent('click', function() {
						var orderName = this.getElement('input').get('value');
						if ($chk(grid.orderName)) {
							if (grid.orderName == orderName) {
								if (grid.orderDirect == 'asc') {
									grid.orderDirect = 'desc';
								} else if (grid.orderDirect = 'desc') {
									grid.orderDirect = null;
									grid.orderName = null;
								}
							} else {
								grid.removeAllOrderImg();
								grid.orderName = orderName;
								grid.orderDirect = 'asc';
							}
						} else {
							grid.orderName = orderName;
							grid.orderDirect = 'asc';
						}
						var img = this.getElement('img');

						if (!$chk(img)) {
							img = new Element('img').inject(this);
							img.addClass('icon16');
						}
						if (grid.orderDirect == 'asc') {
							img.set('src', ($chk(ctx) ? ctx : '') + grid.options.image.order_up);
						} else if (grid.orderDirect == 'desc') {
							img.set('src', ($chk(ctx) ? ctx : '') + grid.options.image.order_down);
						} else {
							img.dispose();
						}
						grid.load();
					});
				}
			}
		}

		for ( var i = 0; i < this.options.pageSize; i++) {
			var tr = new Element('tr').inject(tbody);
			var tdBox = new Element('td').inject(tr);
			tdBox.set('align', 'center');
			for ( var j = 0; j < columnCount; j++) {
				if (!$chk(this.options.columnModel[j].identity)) {
					var fieldTd = new Element('td', {
						'html' : '&nbsp;'
					}).inject(tr);
					var align = this.options.columnModel[j].align;
					if ($chk(this.options.columnModel[j].align)) {
						fieldTd.set('align', align);
					}
				}
			}
		}
	},
	drawPagination : function() {
		if (!$chk(this.pageDiv)) {
			this.pageDiv = new Element('div', {
				'class' : 'oper'
			}).inject(this.tableDiv);
			var p = new Element('p', {
				'class' : 'right'
			}).inject(this.pageDiv);
			p.setStyle('margin-top', '5px');
			this.Pagination = new JIM.UI.Paging( {
				url : this.options.url,
				head : {
					el : p,
					showNumber : true
				},
				onBeforeLoad : this.beforeLoad.bind(this),
				onAfterLoad : this.afterLoad.bind(this)
			});
		}
	},
	load : function() {
		this.drawPagination();
		var params = '';
		if ($chk(this.searchForm)) {
			params = this.searchForm.toQueryString();
		}
		if ($chk(this.orderName)) {
			params += '&' + 'page_orderBy=' + this.orderName + '_' + this.orderDirect;
		}
		this.Pagination.load(params);
	},
	getPage : function() {
		var page = {};
		page.pageNo = this.Pagination.getPageNo() + 1;// 返回的是index，所以要加1
		page.pageSize = this.Pagination.getLimit();
		page.total = this.Pagination.getTotal();
		return page;
	},
	calMaskHeight : function() {
		if (!$chk(this.maskHeight)) {
			var tempHeight = 0;
			var trs = this.dataTable.getElements('tr');
			$each(trs, function(tr, index) {
				var td = tr.getElement('th');
				if (!$chk(td)) {
					td = tr.getElement('td');
				}
				var lineHeight = td.getStyle('line-height');
				lineHeight = lineHeight.substring(0, lineHeight.length - 2);
				var padding = td.getStyle('padding');
				padding = padding.substring(0, padding.indexOf('px'));
				tempHeight += parseInt(lineHeight);
				tempHeight += parseInt(padding) * 2 + 1;
			});
			this.maskHeight = tempHeight;
		}

	},
	beforeLoad : function() {
		this.calMaskHeight();
		this.cleanCheckBox();
		this.cleanData();
		var divWidth = this.dataTable.getStyle('width');
		if (divWidth.indexOf('%') != -1) {
			divWidth = this.dataTable.getParent().getStyle('width');
		}
		divWidth = parseInt(divWidth.substring(0, divWidth.indexOf('px')));
		if (this.options.mask) {
			if ($chk(this.maskBox)) {
				this.maskBox.setStyle('opacity', 0.35);
				this.maskText.setStyle('opacity', 1);
			} else {
				this.maskBox = new Element('div', {
					styles : {
						'z-index' : 9000,
						opacity : 0.35,
						width : this.dataTable.getParent().getStyle('width'),//去父div的宽度
						height : this.maskHeight,
						'line-height' : this.maskHeight,
						position : 'absolute',
						top : this.dataTable.getSize().y - 95,
						'background-color' : '#1e1e1e'
					}
				}).inject(this.container);
				if (!$chk(this.maskText)) {
					this.maskText = '加载中，请稍后...';
				}
				var pLeft = divWidth / 2;
				var pTop = (this.dataTable.getSize().y - 95) + (this.maskHeight / 2 - 20);
				this.maskText = new Element('p', {
					html : this.maskText,
					styles : {
						position: 'absolute',
						'z-index' : 9001,
						'background':'url(' + ctx + '/lib/grid/bg_loading.gif) no-repeat scroll 0 0',
						'text-indent':'55px',
						'line-height':'50px',
						'overflow':'hidden',
						left : pLeft,
						top : pTop,
						width:'160px',
						height:'48px'
					}
				}).inject(this.container);
			}
		}
		var tds = this.container.getElements('td');
		Array.each(tds, function(td, index) {
			td.removeEvents('mouseover');
			td.removeEvents('mouseout');
			td.removeEvents('mousemove');
		});
	},
	afterLoad : function(data) {
		var trs = this.dataTable.getElements('tr');
		if (!this.options.multipleSelection) {
			this.selectIds.empty();
		}
		var cm = this.options.columnModel;
		var grid = this;
		$each(data.result, function(result, index) {
			var tds = $(trs[index + 1]).getElements('td');
			var lineNo = index + 1;
			if (tds.length > 0) {
				$each(tds, function(td, index) {

					if (index == 0) {
						td.set('html', '');
						if (grid.options.selection == true) {
							var checkBox = new Element('input', {
								'type' : 'checkbox',
								'name' : 'ids'
							}).inject(td);
							checkBox.set('value', result.id);
							$each(grid.selectIds, function(item, index) {
								if (item == result.id) {
									checkBox.set('checked', true);
								}
							});
							checkBox.addEvent('click', function() {
								if (this.checked) {
									if (grid.options.multipleSelection) {
										grid.selectIds.push(this.get('value'));
									} else {
										grid.cleanCheckBox();
										grid.selectIds.empty();
										grid.selectIds.push(this.get('value'));
									}
									this.set('checked', true);
									this.getParent().getParent().setStyle('background-color', '#FFFACD');
								} else {
									if ($chk(grid.checkBox)) {
										grid.checkBox.set('checked', false);
									}
									grid.selectIds.erase(this.get('value'));
									this.getParent().getParent().setStyle('background-color', '');
								}
							});
						} else {
							td.appendText(lineNo + grid.Pagination.getPageNo() * grid.Pagination.getLimit());
						}
					} else {
						var text = '&nbsp;';
						var flag = false;
						var omission = true;
						if(grid.options.columnModel[index].isOmission != undefined) {
							omission = grid.options.columnModel[index].isOmission;
						}
						
						if ($chk(result[cm[index].dataName])) {
							text = result[cm[index].dataName].toString();
							if(omission) {
								if (!grid.checkLength(text, 20)) {
									flag = true;
								}
								text = grid.subString(text, 20, true);
							}
						}
						
						
						$(td).set('html', text);
						if (flag) {
							$(td).addEvent('mouseover', function(event) {
								div = new Element('div', {
									id : 'tooltip',
									styles : {
										position : 'absolute',
										border : '1px solid #A5CBDB',
										background : '#F6F6F6',
										padding : '1px',
										color : '#333',
										top : event.page.y + 'px',
										left : event.page.x + 'px',
										'z-index' : '99999',
										display : 'none',
										'word-break' : 'break-all',
										'word-wrap' : 'break-word',
										'max-width' : '200px'
									},
									html : result[cm[index].dataName].toString()
								});
								div.inject(document.body, 'bottom');
								div.setStyle('display', '');
							});
							$(td).addEvent('mouseout', function(event) {
								if ($chk($('tooltip')))
									$('tooltip').dispose();
							});
							$(td).addEvent('mousemove', function(event) {
								var toolTipDiv = $('tooltip');
								if ($chk(toolTipDiv)) {
									var left = event.page.x + 10;
									if ((event.page.x + 230) > window.screen.width) {
										left = window.screen.width - 230;
									}
									toolTipDiv.setStyles( {
										"top" : (event.page.y + 20) + "px",
										"left" : (left) + "px"
									});
								}
							});
						}
					}
				});
			}
		});
		if (this.options.mask) {
			this.maskBox.setStyle('opacity', 0);
			this.maskText.setStyle('opacity', 0);
		}
	},
	cleanCheckBox : function() {
		var checkBoxs = this.dataTable.getElements('input[type=checkbox]');
		$each(checkBoxs, function(checkBox, index) {
			checkBox.set('checked', false);
			checkBox.getParent().getParent().setStyle('background-color', '');
		});
	},
	cleanData : function() {
		var trs = this.dataTable.getElements('tr');
		$each(trs, function(tr, index) {
			var tds = tr.getElements('td');
			if ($chk(tds)) {
				$each(tds, function(td, index) {
					td.set('html', '&nbsp;');
				});
			}
		});
	},
	cleanSelectIds : function() {
		this.selectIds = [];
	},
	subString : function(str, len, hasDot) {
		var newLength = 0;
		var newStr = "";
		var chineseRegex = /[^\x00-\xff]/g;
		var singleChar = "";
		var strLength = str.replace(chineseRegex, "**").length;
		for ( var i = 0; i < strLength; i++) {
			singleChar = str.charAt(i).toString();
			if (singleChar.match(chineseRegex) != null) {
				newLength += 2;
			} else {
				newLength++;
			}
			if (newLength > len) {
				break;
			}
			newStr += singleChar;
		}

		if (hasDot && strLength > len) {
			newStr += "...";
		}
		return newStr;
	},
	checkLength : function(str, len) {
		var chineseRegex = /[^\x00-\xff]/g;
		var strLength = str.replace(chineseRegex, "**").length;
		if (strLength > len) {
			return false;
		} else {
			return true;
		}
	}
});