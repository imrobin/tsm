var JIM = JIM ? JIM : {};
JIM.UI = JIM.UI ? JIM.UI : {};

// 列表控件
JIM.UI.WinGrid = new Class( {
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
		header : true,
		headerImage : null,
		headerText : '',
		buttons : [],
		winButtons : [],
		columnModel : null,
		searchBar : null,
		searchButton : false,
		width : 'auto',
		height : 'auto',
		order : true,
		autoLoad : true,
		title: '',
		titleImage : '',
		draggable: false,
		initDraw : false,
		drawButtons : true,
		drawSearch : true,
		fadeDelay: 400,
		fadeDuration: 400,
		pageMode : true,
		keys: { 
			esc: function() { this.close(); } 
		},
		content: '',
		zIndex: 9001,
		pad: 100,
		mask : true,
		overlayAll: false,
		constrain: false,
		resetOnScroll: true,
		baseClass: 'lightface',
		image : {
			order_up : '/lib/grid/up.png',
			order_down : '/lib/grid/down.png',
			header : '/lib/grid/icon_8.png',
			button : '/lib/grid/icon_9.png'
		}
	},
	initialize : function(options) {
		this.setOptions(options);
		
		this.state = false;
		this.resizeOnOpen = true;
		this.ie6 = typeof document.body.style.maxHeight == "undefined";
		
		for ( var key in this.options.image) {
			this.options.image[key] = this.options.contextPath + this.options.image[key];
		}
		this.container = new Element("div");
		if (!$chk(this.container)) {
			alert('请指定div');
		}
		this.selectIds = new Array();
		this.draw();
		if (this.options.autoLoad) {
			this.load();
		}
		
		if($chk(options.onSelect)) {
			this.checkSelect = options.onSelect;
		}
	},
	draw : function() {
		this.container.empty();
		
		if ($chk(this.options.width)) {
			this.container.setStyle('width', this.options.width);
		}
		
		this.container.addClass('rightcont');
		
		if ($chk(this.options.header)) {
			this.drawHearder();
		}

		if ($chk(this.options.searchBar)) {
			this.drawSearchBar();
		}

		if ($chk(this.options.buttons) && this.options.buttons.length > 0 && this.options.drawButtons) {
			this.drawButtons();
		}

		var columnCount = $chk(this.options.columnModel) ? this.options.columnModel.length : 0;

		this.drawTable(columnCount);
		
		
	},
	drawWin : function(fast){
		//draw MaskBox
		if (this.options.mask) {
			var docBody= document.body;
			var Scroll = $(docBody).getScrollSize();
			this.maskBox = new Element('div', {
				width : Scroll.x,
				height : Scroll.y,
				styles : {
					'z-index' : this.options.zIndex - 1,
					opacity : 0,
					width : Scroll.x + 'px',
					height : Scroll.y + 'px',
					position : 'absolute',
					top : 0,
					left : 0,
					'background-color' : '#1e1e1e'
				},
				tween : {
					duration : this.options.fadeDuration / 4,
					onComplete : function() {
						if (typeof this.box == "undefined") {
							return;
						}
					}.bind(this)
				}
			}).inject(document.body, 'bottom');
		}
		// create main box
		this.box = new Element('table',{
			'class': this.options.baseClass,
			styles: {
				'z-index': this.options.zIndex,
				opacity: 0
			},
			tween: {
				duration: this.options.fadeDuration,
				onComplete: function() {
					if(this.box.getStyle('opacity') == 0) {
						this.box.setStyles({ top: -9000, left: -9000 });
					}
				}.bind(this)
			}
		}).inject(document.body,'bottom');

		//draw rows and cells;  use native JS to avoid IE7 and I6 offsetWidth and offsetHeight issues
		var verts = ['top','center','bottom'], hors = ['Left','Center','Right'], len = verts.length;
		for(var x = 0; x < len; x++) {
			var row = this.box.insertRow(x);
			for(var y = 0; y < len; y++) {
				var cssClass = verts[x] + hors[y], cell = row.insertCell(y);
				cell.className = cssClass;
				if (cssClass == 'centerCenter') {
					this.contentBox = new Element('div',{
						'class': 'lightfaceContent',
						styles: {
							width: this.options.width + 21
						}
					});
					cell.appendChild(this.contentBox);
				}
				else {
					document.id(cell).setStyle('opacity',0.4);
				}
			}
		}
		
		
		this.messageBox = this.container.setStyle('height',this.options.height).inject(this.contentBox);
		
		//button container
		this.footer = new Element('div',{
			'class': 'lightfaceFooter',
			styles: {
				display: 'block'
			},
			html : '&nbsp;'
		}).inject(this.contentBox);
		
		//draw overlay
		this.overlay = new Element('div',{
			html: '&nbsp;',
			styles: {
				opacity: 0
			},
			'class': 'lightfaceOverlay',
			tween: {
				link: 'chain',
				duration: this.options.fadeDuration,
				onComplete: function() {
					if(this.overlay.getStyle('opacity') == 0) this.box.focus();
				}.bind(this)
			}
		}).inject(this.contentBox);
		if(!this.options.overlayAll) {
			this.overlay.setStyle('top',(this.title ? this.title.getSize().y - 1: 0));
		}
		
		//create initial buttons
		this.winButtons = [];
		if(this.options.winButtons.length) {
			this.options.winButtons.each(function(button) {
				this.addButton(button.title,button.event,button.color);
			},this);
		}
		
		//focus node
		this.focusNode = this.box;
		
		if(!this.isOpen) {
			this.box[fast ? 'setStyles' : 'tween']('opacity',1);
			if (this.options.mask) {
				this.maskBox[fast ? 'setStyles' : 'tween']('opacity',0.45);
			}
			if(this.resizeOnOpen) this._resize();
			this.fireEvent('open');
			this._attachEvents();
			(function() {
				this._setFocus();
			}).bind(this).delay(this.options.fadeDuration + 10);
			this.isOpen = true;
		}
		return this;
	}
	,
	drawHearder : function() {
		
		// 创建标题层
		this.headDiv = new Element('div').inject(this.container, 'top');
		this.headDiv.addClass('title');
		// 创建标题层图片
		/*var image = new Element('img');
		image.addClass('icon16');
		if ($chk(this.options.headerImage)) {
			image.set('src', this.options.headerImage);
		} else {
			image.set('src', $chk(ctx) ? ctx : '' + this.options.image.header);
		}
		image.inject(this.headDiv, 'top');*/
		// 创建标题层文字
		if ($chk(this.options.headerText)) {
			this.headDiv.appendText(this.options.headerText);
		} else {
			this.headDiv.appendText('管理');
		}
	},
	drawSearchBar : function() {
		if(this.options.drawSearch){
			this.barDiv = new Element('div').inject(this.container);
		}else{
			this.barDiv = new Element('div').setStyle('display','none').inject(this.container);
		}
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
			} else if (filter.type == 'date') {
				var input = new Element('input').inject(this.searchForm);
				input.set('type', 'text');
				new DatePicker(input);
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
				input.set('value', '');
				this.orderName = null;
				this.orderDirect = null;
				this.removeAllOrderImg();
				this.Pagination.index = 0;
				this.cleanSelectIds();
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
		var grid = this;
		this.tableDiv = new Element('div').inject(this.container);
		this.tableDiv.addClass('tableBox');
		this.dataTable = new Element('table', {
			'cellpadding' : 0,
			'cellspacing' : 0
		}).inject(this.tableDiv);
		if(!this.options.pageMode){
			this.dataTable.setStyle('width', this.options.width - 20);
			this.tableDiv.setStyle('height', this.options.height - 35);
			this.tableDiv.setStyle('overflow', 'auto');
		}
		var tbody = this.dataTable.getElement('tbody');
		if (!$chk(tbody)) {
			tbody = new Element('tbody').inject(this.dataTable);
		}
		var title = new Element('tr').inject(tbody);

		var thBox = new Element('th', {
			styles : {'width': '30px', 'vertical-align' : 'middle'}
		}).inject(title);
		if (this.options.multipleSelection) {
			this.checkBox = new Element('input', {
				'type' : 'checkbox'
			}).inject(thBox);
			this.checkBox.addEvent('click', function(e) {
				if($chk(grid.options.maxSelection)){
					if($chk($chk(grid.result))){
						if(grid.checkBox.checked && grid.options.maxSelection < grid.result.length){
							new LightFace.MessageBox().error("全选数量超过" + grid.options.maxSelection + '个，请单独选择');
							return false;
						}
					}
				}
				var checkBoxs = this.dataTable.getElements('input[type=checkbox][name=ids]');
				if (this.checkBox.checked) {
					this.selectIds.empty();
					$each(checkBoxs, function(checkBox, index) {
						checkBox.set('checked', true);
						var temValue = checkBox.get('value');
						var tds = checkBox.getParent().getSiblings('td');
						for(i = 0;i<grid.checkTdNum.length;i++){
							var temptd = tds[grid.checkTdNum[i]-1];
							if(temptd.getElement('input').get('checked')){
								temValue += ':1';
							}else{
								temValue += ':0';
							}
						}
						for(i = 0;i<grid.selectTdNum.length;i++){
							var temptd = tds[grid.selectTdNum[i]-1];
							temValue += ':' + temptd.getElement('select').get('value');
						}
						this.selectIds.push(temValue);
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
		}
		this.checkTdNum = new Array();
		this.selectTdNum = new Array();
		var arrIndex = 0;
		var arrIndexSel = 0;;
		for ( var i = 0; i < columnCount; i++) {
			var th = null;
			if (!$chk(this.options.columnModel[i].identity)) {
				if($chk(this.options.columnModel[i].checkBox)){
					this.checkTdNum[arrIndex] = i;
					arrIndex++;
				}
				if($chk(this.options.columnModel[i].select)){
					this.selectTdNum[arrIndexSel] = i;
					arrIndexSel++;
				}
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
		if(this.options.pageMode){
			for ( var i = 0; i < this.options.pageSize; i++) {
				var tr = new Element('tr').inject(tbody);
				var tdBox = new Element('td').inject(tr);
				tdBox.set('align', 'center');
				for ( var j = 0; j < columnCount; j++) {
					if (!$chk(this.options.columnModel[j].identity)) {
						new Element('td', {
							'html' : '&nbsp;'
						}).inject(tr);
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
		if(this.options.pageMode) {
			this.drawPagination();
			var params = this.searchForm.toQueryString();
			if ($chk(this.orderName)) {
				params += '&' + 'page_orderBy=' + this.orderName + '_' + this.orderDirect;
			}
			this.Pagination.load(params);
		}else{
			var params = this.searchForm.toQueryString();
			if ($chk(this.orderName)) {
				params += '&' + 'page_orderBy=' + this.orderName + '_' + this.orderDirect;
			}
			this.singleLoad(params);
		}
	},
	singleLoad : function(params){
		this.beforeLoad();
		var columnCount = $chk(this.options.columnModel) ? this.options.columnModel.length : 0;
		new Request.JSON( {
			url : this.options.url + params,
			onComplete : function(data,text){
				if(!this.options.pageMode){
					var tbody = this.dataTable.getElement('tbody');
					for ( var i = 0; i < data.result.length; i++) {
						var tr = new Element('tr').inject(tbody);
						var tdBox = new Element('td').inject(tr);
						tdBox.set('align', 'center');
						for ( var j = 0; j < columnCount; j++) {
							if (!$chk(this.options.columnModel[j].identity)) {
								new Element('td', {
									'html' : '&nbsp;'
								}).inject(tr);
							}
						}
					}
				}
				this.afterLoad(data);
			}.bind(this)
		}).post();
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
		var tds = this.container.getElements('td');
		Array.each(tds,function(td,index){
			td.removeEvents('mouseover');
			td.removeEvents('mouseout');
			td.removeEvents('mousemove');
		});
	},
	getReuslt : function(){
		if($chk(this.result)){
			return this.result;
		}
	},
	afterLoad : function(data) {
		var trs = this.dataTable.getElements('tr');
		if (!this.options.multipleSelection) {
			this.selectIds.empty();
		}
		var cm = this.options.columnModel;
		var grid = this;
		grid.result = data.result;
		$each(data.result, function(result, index) {
			var tds = $(trs[index + 1]).getElements('td');
			if (tds.length > 0) {
				$each(tds, function(td, tdsindex) {
					if (tdsindex == 0) {	
						td.set('html' , '');
						td.setStyle('vertical-align', 'middle');
						var checkBox = new Element('input', {
							'type' : 'checkbox',
							'name' : 'ids'
						}).inject(td);
						checkBox.set('value', result.id);
						checkBox.addEvent('click', function(e) {
							if($chk(grid.options.maxSelection)){
									if(checkBox.checked && grid.options.maxSelection == grid.selectIds.length){
										new LightFace.MessageBox().error('最多只能选择' + grid.options.maxSelection + '个选项');
										return false;
									}
							}
							if($chk(grid.checkSelect)){
								var checkFlag = grid.checkSelect(checkBox,result);
								if(!checkFlag){
									e.stop();
									return false;
								}
							}
							if (this.checked) {
								var temValue = this.get('value');
								var tds = $(this).getParent().getSiblings('td');
								for(i = 0;i<grid.checkTdNum.length;i++){
									var temptd = tds[grid.checkTdNum[i]-1];
									if(temptd.getElement('input').get('checked')){
										temValue += ':1';
									}else{
										temValue += ':0';
									}
								}
								for(i = 0;i<grid.selectTdNum.length;i++){
									var temptd = tds[grid.selectTdNum[i]-1];
									temValue += ':' + temptd.getElement('select').get('value');
								}
								if (grid.options.multipleSelection) {
									grid.selectIds.push(temValue);
								} else {
									grid.cleanCheckBox();
									grid.selectIds.empty();
									grid.selectIds.push(temValue);
								}
								this.set('checked', true);
								this.getParent().getParent().setStyle('background-color', '#FFFACD');
							} else {
								var inputThis = this;
								if ($chk(grid.checkBox)) {
									grid.checkBox.set('checked', false);
								}
								Array.each(grid.selectIds,function(ids,index){
									var thisId = inputThis.get('value');
									var idArray = ids.split(":");
									if(idArray[0] == thisId){
										grid.selectIds.erase(ids);
									}
								});
								grid.selectIds.erase(temValue);
								this.getParent().getParent().setStyle('background-color', '');
							}
						});
					} else {
						var isCheckBox = false;
						var isSelectBox = false;
						for(i = 0;i<grid.checkTdNum.length;i++){
							if(tdsindex == grid.checkTdNum[i]){
								isCheckBox = true;
								break;
							}
						}
						for(i = 0;i<grid.selectTdNum.length;i++){
							if(tdsindex == grid.selectTdNum[i]){
								isSelectBox = true;
								break;
							}
						}
						if(isCheckBox){
							$(td).set('html', ' <input type="checkbox"></input>');
							var gridCheck = $(td).getElement('input');
							gridCheck.addEvent('click', function(){
								var chkInput = this;
								Array.each(grid.selectIds,function(ids,index){
									var firstInput = $(chkInput).getParent().getParent().getFirst().getFirst();
									var thisId = firstInput.get('value');
									var idArray = ids.split(":");
									if(idArray[0] == thisId){
										grid.selectIds.erase(ids);
										firstInput.fireEvent('click');
									}
								});
							});
						}else if(isSelectBox){
							var text = '';
							var flag = false;
							if ($chk(result[cm[tdsindex].dataName])) {
								text = result[cm[tdsindex].dataName].toString();
							}
							var items = cm[tdsindex].item;
							var selectHtml = "<select>";
							items.each(function(item,index){
								selectHtml += '<option value="' + item.value + '">' + item.key + '</option>';
							});
							selectHtml += '</select>';
							$(td).set('html', selectHtml);
							var select = $(td).getElement('select');
							select.addEvent('change', function(){
								var select = this;
								Array.each(grid.selectIds,function(ids,index){
									var firstInput = $(select).getParent().getParent().getFirst().getFirst();
									var thisId = firstInput.get('value');
									var idArray = ids.split(":");
									if(idArray[0] == thisId){
										grid.selectIds.erase(ids);
										firstInput.fireEvent('click');
									}
								});
							});
							if($chk(result[cm[tdsindex].dataName])){
								select.set('value',text);
							}
						}else{
							var text = '&nbsp;';
							var flag = false;
							if ($chk(result[cm[tdsindex].dataName])) {
								text = result[cm[tdsindex].dataName].toString();
								if(!grid.checkLength(text,20)){
									flag = true;
								}
								text = grid.subString(text, 20, true);
							}
							$(td).set('html', text);
							if(flag){
								$(td).addEvent('mouseover',function(event){
									   var event = new Event(event); 
									   div = new Element('div',{
										 id : 'tooltip',
										 styles : {
											position:'absolute',
											border :'1px solid #A5CBDB',
										    background:'#F6F6F6',
										    padding:'1px',
										    color:'#333',
										    top : event.page.y + 'px',
										    left: event.page.x + 'px',
										    'z-index' : '99999',
										    display:'none',
										    'word-break':'break-all',
										    'word-wrap': 'break-word',
										    'max-width': '200px'
										},
										html : result[cm[tdsindex].dataName].toString()
									});
									div.inject(document.body, 'bottom');
									div.setStyle('display','');
								});
								$(td).addEvent('mouseout',function(event){
									$('tooltip').dispose();
								});
								$(td).addEvent('mousemove',function(event){
									var event = new Event(event); 
									var toolTipDiv =  $('tooltip');
									if($chk(toolTipDiv)){
										var left = event.page.x + 10;
										toolTipDiv.setStyles({
											 "top": (event.page.y + 20) + "px",
								             "left": left  + "px"
										 });
									}
								});
							}
						}
					}
				});
				if($chk(grid.options.callBack)) {
					if(result[grid.options.callBack.name] == grid.options.callBack.value) {
						grid.selectIds.push(result.id + '');
					}
				}
				$each(grid.selectIds,function(item,index){
					var oldid = item.split(":");
					var flag = false;
					for(var i=0;i<oldid.length;i++){
						if(i==0){
							if(oldid[i] == result.id){
								flag = true;
								tds[0].getElement('input').set('checked', true);
							}
						}else{
							if(flag){
								var mixArray = new Array();
								mixArray.append(grid.selectTdNum);
								mixArray.append(grid.checkTdNum);
								mixArray.sort(function(int1,int2){
									var iNum1 = parseInt(int1);//强制转换成int 型;
								    var iNum2 = parseInt(int2);
								    if(iNum1 < iNum2){
								        return -1;
								    }else if(iNum1 > iNum2){
								        return 1;
								    }else{
								        return 0;
								    }
								});
								if(grid.checkTdNum.contains(mixArray[i-1])){
									var temptd = tds[grid.mixArray[i-1]];
									var tempinput = temptd.getElement('input');
									if(oldid[i] == '1'){
										tempinput.set('checked', true);
									}else{
										tempinput.set('checked', false);
									}
								}
								if(grid.selectTdNum.contains(mixArray[i-1])){
									var select = tds[grid.selectTdNum[i-1]];
									var tempSelect = select.getElement('select');
									tempSelect.set('value',oldid[i]);
								}
							}
						}
					}
				});
			}
		});
		if(!this.isOpen){
			this.drawWin();
		}
	},
	cleanCheckBox : function() {
		var checkBoxs = this.dataTable.getElements('input[type=checkbox]');
		$each(checkBoxs, function(checkBox, index) {
			if(!$chk(checkBox.getParent().getPrevious())){
				checkBox.set('checked', false);
				checkBox.getParent().getParent().setStyle('background-color', '');
			}
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
	addButton: function(title,clickEvent,color) {
		this.footer.setStyle('display','block');
		var focusClass = 'lightfacefocus' + color;
		var label = new Element('label',{
			'class': color ? 'lightface' + color : '',
			events: {
				mousedown: function() {
					if(color) {
						label.addClass(focusClass);
						var ev = function() {
							label.removeClass(focusClass);
							document.id(document.body).removeEvent('mouseup',ev);
						};
						document.id(document.body).addEvent('mouseup',ev);
					}
				}
			}
		});
		this.winButtons[title] = (new Element('input',{
			type: 'button',
			value: title,
			events: {
				click: (clickEvent || this.close).bind(this)
			}
		}).inject(label));
		label.inject(this.footer);
		return this;
	},
	showButton: function(title) {
		if(this.winButtons[title]) this.winButtons[title].removeClass('hiddenButton');
		return this.winButtons[title];
	},
	hideButton: function(title) {
		if(this.winButtons[title]) this.winButtons[title].addClass('hiddenButton');
		return this.winButtons[title];
	},
	hideFoot: function() {
		if(this.footer) this.footer.setStyle('display','none');
		return this.footer;
	},
	showFoot: function() {
		if(this.footer) this.footer.setStyle('display','');
		return this.footer;
	},
	// Open and close box
	close: function(fast) {
		if(this.isOpen) {
			this.box[fast ? 'setStyles' : 'tween']('opacity',0);
			if (this.options.mask) {
				this.maskBox[fast ? 'setStyles' : 'tween']('opacity',0);
			}
			this.fireEvent('close', [$chk(this.result), this.resultMessage]);
			this._detachEvents();
			this.isOpen = false;
		}
		return this;
	},
	_setFocus: function() {
		this.focusNode.setAttribute('tabIndex',0);
		this.focusNode.focus();
	},
	
	// Show and hide overlay
	fade: function(fade,delay) {
		this._ie6Size();
		(function() {
			this.overlay.setStyle('opacity',fade || 1);
		}.bind(this)).delay(delay || 0);
		this.fireEvent('fade');
		return this;
	},
	unfade: function(delay) {
		(function() {
			this.overlay.fade(0);
		}.bind(this)).delay(delay || this.options.fadeDelay);
		this.fireEvent('unfade');
		return this;
	},
	_ie6Size: function() {
		if(this.ie6) {
			var size = this.contentBox.getSize();
			var titleHeight = (this.options.overlayAll || !this.title) ? 0 : this.title.getSize().y;
			this.overlay.setStyles({
				height: size.y - titleHeight,
				width: size.x
			});
		}
	},
	
	
	// Attaches events when opened
	_attachEvents: function() {
		this.keyEvent = function(e){
			if(this.options.keys[e.key]) this.options.keys[e.key].call(this);
		}.bind(this);
		this.focusNode.addEvent('keyup',this.keyEvent);
		
		this.resizeEvent = this.options.constrain ? function(e) { 
			this._resize(); 
		}.bind(this) : function() { 
			this._position(); 
		}.bind(this);
		window.addEvent('resize',this.resizeEvent);
		
		if(this.options.resetOnScroll) {
			this.scrollEvent = function() {
				this._position();
			}.bind(this);
			window.addEvent('scroll',this.scrollEvent);
		}
		
		return this;
	},
	
	// Detaches events upon close
	_detachEvents: function() {
		this.focusNode.removeEvent('keyup',this.keyEvent);
		window.removeEvent('resize',this.resizeEvent);
		if(this.scrollEvent) window.removeEvent('scroll',this.scrollEvent);
		return this;
	},
	
	// Repositions the box
	_position: function() {
		var windowSize = window.getSize(), 
			scrollSize = window.getScroll(), 
			boxSize = this.box.getSize();
		this.box.setStyles({
			left: scrollSize.x + ((windowSize.x - boxSize.x) / 2),
			top: scrollSize.y + ((windowSize.y - boxSize.y) / 2)
		});
		if (this.options.mask) {
			this.maskBox.setStyles( {
				width : this.maskBox.get('width').toInt() + scrollSize.x.toInt(),
				height : this.maskBox.get('height').toInt() + scrollSize.y.toInt()
			});
		}
		this._ie6Size();
		return this;
	},
	
	// Resizes the box, then positions it
	_resize: function() {
		var height = this.options.height;
		if(height == 'auto') {
			//get the height of the content box
			var max = window.getSize().y - this.options.pad;
			if(this.contentBox.getSize().y > max) height = max;
		}
		this.messageBox.setStyle('height',height);
		this._position();
	},
	
	// Expose message box
	toElement: function () {
		return this.messageBox;
	},
	
	// Expose entire modal box
	getBox: function() {
		return this.box;
	},
	
	// Cleanup
	destroy: function() {
		this._detachEvents();
		this.buttons.each(function(button) {
			button.removeEvents('click');
		});
		this.box.dispose();
		delete this.box;
		if (this.options.mask) {
			this.maskBox.dispose();
			delete this.maskBox;
		}
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
	checkLength : function(str,len){
		var chineseRegex = /[^\x00-\xff]/g;
		var strLength = str.replace(chineseRegex, "**").length;
		if(strLength > len){
			return false;
		}else{
			return true;
		}
	}
});