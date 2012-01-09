var Terminal = Terminal ? Terminal :{};

Terminal.list = new Class({

	options:{
    	
	},
	initialize: function(){        
	},
	getAllBrand: function(){
		var obj = this;
		var request = new Request({
			url : ctx+'/html/mobile/?m=getMobileBrand',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if(obj.success) {
					$("mobileBrand").options.add(new Option("请选择",0));
					Array.each(a, function(item, index){
						$('mobileBrand').options.add(new Option(item,item));	
					});
//					if(a.length>0){
//						obj.value = a[0].id;
//					}
				}else{
					// 没想好做些什么
				}
			}
			});
		request.post();
	},
	getMobileByBrand:function(brand){
		var request = new Request({
			url : ctx+'/html/mobile/?m=getTypeAndValueByBrand',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if(obj.success) {
					$('mobileType').empty();
					Array.each(a, function(item, index){
					$('mobileType').options.add(new Option(item.type,item.id));	
					});
				}else{
					
				}
			}
		});
		request.post('brand='+brand);
	},
	getMobileByBrandAndType:function(type,id){
		this.value = type;
	}
});
	