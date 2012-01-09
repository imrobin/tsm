/*需要引入以下几个文件
 * <link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
 * <link href="${ctx}/css/city.css" rel="stylesheet" type="text/css" />
 * <script type="text/javascript" src="${ctx}/lib/lightface/LightFace.js"></script>
 * <script type="text/javascript" src="${ctx}/lib/lightface/LightFace.Request.js"></script>
 * 如何使用:
 * $(element.id).addEvent('click',function(){
 *				 new CityPicker().showCity($(element.id));
 *			});
 */
var CityPicker = new Class(
		{
			Implements : [ Events, Options ],
			options : {},
			initialize : function(options) {
			},
			showCity : function(cp) {
				var beforeVal = $(cp).get('value');
				ajaxFace = new LightFace.Request(
						{
							mask : true,
							draggable : true,
							height : 120,
							width : 440,
							buttons : [ {
								title : '确认',
								color : 'blue',
								event : function() {
								/*if (typeof(formCheck) != "undefined"){
									formCheck.manageError($(cp), 'blur');
								}*/
								this.destroy();
							}
							}, {
								title : '取消',
								event : function() {
									$(cp).set('value', beforeVal);
									/*if (typeof(formCheck) != "undefined"){
										formCheck.manageError($(cp), 'blur');
									}*/
									this.destroy();
								}
							} ],
							/*content : '<div id="showBox"><div id="provinceBox"><dl id="province"><dd><a class="c1" href="#">北京</a></dd><dd> <a class="c1" href="#">天津</a> </dd><dd><a class="c1" href="#">上海</a> </dd><dd><a class="c1" href="#">重庆</a> </dd><dd><a class="c1" href="#">黑龙江</a> </dd><dd><a class="c1" href="#">吉林</a></dd><dd> <a class="c1" href="#">辽宁</a> </dd><dd><a class="c1" href="#">山东</a> </dd><dd><a class="c1" href="#">山西</a> </dd><dd><a class="c1" href="#">陕西</a> </dd><dd><a class="c1" href="#">河北</a> </dd><dd><a class="c1" href="#">河南</a> </dd><dd><a class="c1" href="#">湖北</a> </dd><dd><a class="c1" href="#">内蒙古</a></dd><dd> <a class="c1" href="#">海南</a> </dd><dd><a class="c1" href="#">江苏</a></dd><dd> <a class="c1" href="#">江西</a> </dd><dd><a class="c1" href="#">广东</a> </dd><dd><a class="c1" href="#">广西</a> </dd><dd><a class="c1" href="#">云南</a> </dd><dd><a class="c1" href="#">贵州</a> </dd><dd><a class="c1" href="#">四川</a> </dd><dd><a class="c1" href="#">湖南</a> </dd><dd><a class="c1" href="#">宁夏</a> </dd><dd><a class="c1" href="#">甘肃</a> </dd><dd><a class="c1" href="#">青海</a> </dd><dd><a class="c1" href="#">西藏</a> </dd><dd><a class="c1" href="#">新疆</a> </dd><dd><a class="c1" href="#">安徽</a> </dd><dd><a class="c1" href="#">浙江</a> </dd><dd><a class="c1" href="#">福建</a></dd></dl></div><div id="cityBox"><dl id="city"></dl></div></div>',*/
							content : '<div id="showBox"><div id="provinceBox"><dl id="province"><dd><a class="c1" href="#">北京</a></dd><dd> <a class="c1" href="#">天津</a> </dd><dd><a class="c1" href="#">上海</a> </dd><dd><a class="c1" href="#">重庆</a> </dd><dd><a class="c1" href="#">黑龙江</a> </dd><dd><a class="c1" href="#">吉林</a></dd><dd> <a class="c1" href="#">辽宁</a> </dd><dd><a class="c1" href="#">山东</a> </dd><dd><a class="c1" href="#">山西</a> </dd><dd><a class="c1" href="#">陕西</a> </dd><dd><a class="c1" href="#">河北</a> </dd><dd><a class="c1" href="#">河南</a> </dd><dd><a class="c1" href="#">湖北</a> </dd><dd><a class="c1" href="#">内蒙古</a></dd><dd> <a class="c1" href="#">海南</a> </dd><dd><a class="c1" href="#">江苏</a></dd><dd> <a class="c1" href="#">江西</a> </dd><dd><a class="c1" href="#">广东</a> </dd><dd><a class="c1" href="#">广西</a> </dd><dd><a class="c1" href="#">云南</a> </dd><dd><a class="c1" href="#">贵州</a> </dd><dd><a class="c1" href="#">四川</a> </dd><dd><a class="c1" href="#">湖南</a> </dd><dd><a class="c1" href="#">宁夏</a> </dd><dd><a class="c1" href="#">甘肃</a> </dd><dd><a class="c1" href="#">青海</a> </dd><dd><a class="c1" href="#">西藏</a> </dd><dd><a class="c1" href="#">新疆</a> </dd><dd><a class="c1" href="#">安徽</a> </dd><dd><a class="c1" href="#">浙江</a> </dd><dd><a class="c1" href="#">福建</a></dd></dl></div></div>',
							title : '选择所在地'
						}).open();
				$$('#province a').each(function(el) {
					var elVal = el.get('html');
					el.addEvent('click', function(event) {
						event.stop();
						$(cp).set('value',elVal);
						/*var request = new Request({
							url : ctx+'/html/image/',
							async : false,
							onSuccess : function(responseText) {
								var object = JSON.decode(responseText);
								$('city').set('html', '');
								object.message.each(function(item) {
									var dd = new Element("dd");
									var myElement = new Element('a', {
										'href' : '#',
										'html' : item,
										'class' : 'c2',
										'events' : {
											'click' : function(event) {
												event.stop();
												$(cp).set('value', '');
												$(cp).set('value', elVal + this.get('html'));
											}
										}
									}).inject(dd);
									dd.inject($('city'));
								});
							}*/
						});
						//request.post('m=cityChoose&province=' + el.get('html'));
						//$('city').set('value', el.get('html'));
					});
			}
		});