var MocamDownload = MocamDownload ? MocamDownload : {};

MocamDownload.download = new Class({
	Implements : [ Options ],
	options : {

	},
	initialize : function() {
		this.setOptions(this.options);
	},
	getAllBrand : function() {
		var request = new Request({
			url : ctx + '/html/mobile/?m=getMobileBrand',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if (obj.success) {
					Array.each(a, function(item, index) {
						$('mobileBrand').options.add(new Option(item, item));
					});
				} else {
					// 没想好做些什么
				}
			}
		});
		request.post();
	},
	getHistoryVersion:function(brand,type){
		var request = new Request.JSON({
			url:ctx+'/html/applicationClient/?m=getHistoryVersion',
			async:false,
			onSuccess:function(data){
				var a = data.result;
				var android='';
				var j2me='';
				if(data.success && a.length>0){
				Array.each(a,function(item,index){
						android=android+'<span><a class="client" href="'+ctx+'/html/applicationClient/?m=getAppManagerById&id='+item.id+'">'+item.name+'-'+item.version+'</a></span><br />';
				});
				 new LightFace.MessageBox().info('<div id="left"><span><h2>Android版本</h2></span><br/>'+android+'</div>'); 
				}else{
					
				}
			}
		});
		request.post("brand="+brand+"&type="+type);
	},
	getAllTypeByBrand : function(brand) {
		if (brand == "-1") {
			$('mobileType').empty();
			$('mobileType').options.add(new Option("选择机型", "-1"));
		} else {
			// 获取该品牌的所有型号手机
			var request = new Request({
				url : ctx + '/html/mobile/?m=getTypeByBrand',
				async : false,
				onSuccess : function(result) {
					var obj = JSON.decode(result);
					var a = obj.message;
					if (obj.success) {
						$('mobileType').empty();
						$('mobileType').options.add(new Option("选择机型", "-1"));
						Array.each(a,
								function(item, index) {
									$('mobileType').options.add(new Option(
											item, item));
								});
					} else {
					}
				}
			});
			request.post('brand=' + brand);
		}
		$('deviceImage').set('src',ctx+'/images/noPhone.gif');
	},
	getPicByBrandAndType : function(brand, type) {
		if( brand == '-1' && type == '-1'){
			$('deviceImage').set('src',ctx+'/images/noPhone.gif');
		}
		else{
			var request = new Request({
			url : ctx + '/html/mobile/?m=getIdByBrandAndType&brand='
					+ encodeURIComponent(brand) + '&type='
					+ encodeURIComponent(type),
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if (obj.success) {
					$('deviceImage').set('src',
							ctx + '/html/mobile/?m=getMobilePic&id=' + a);
				} else {

				}
			}
		});
		request.post();
		//更新此链接
		$('downloadButton').set('href',ctx+'/html/applicationClient/?m=downloadAppManager&brand='
					+ encodeURIComponent(brand) + '&type='
					+ encodeURIComponent(type));
		
		var req = new Request({
			url : ctx + '/html/applicationClient/?m=checkClient&brand='
					+ encodeURIComponent(brand) + '&type='
					+ encodeURIComponent(type),
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if (obj.success) {
				    new LightFace.MessageBox().error("该手机型号的应用管理器不存在");
					$('downloadButton').set('href','#');
				} else {

				}
			}
		});
		req.post();
		}
	}
});
