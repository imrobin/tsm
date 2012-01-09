var MobileType = MobileType ? MobileType :{};

MobileType.list = new Class({
	Implements: [Options],
	options:{
		
	},
	initialize: function(){
		this.setOptions(this.options);
		this.page = new JIM.UI.Paging({
            url:ctx+'/html/mobile/?m=getAllMobile',
            head:{el:'mobile-paging',showNumber:true},   
            limit:5,
            onAfterLoad: function(data){
            	var iconSrc ='';
    			var dis='';
            	Array.each(data.result, function(item, index){
        			if (item.icon == null) {
        				iconSrc = ctx + '/images/defTerim.jpg';
        			} else {
        				iconSrc = ctx + '/html/mobile/?m=getMobilePic&id='+ item.id;
        			}
        			dis += "<dd><img style='width: 90px;margin-left :5px;height:90px;' src='"
        					+ iconSrc
        					+ "'/>"
        					+ "<p style='width: 100px; word-wrap: break-word; overflow: hidden;'>"
        					+ item.brandChs + " " + item.type + "</p></dd>";
        		});
                $('mobile-list').set('html',dis);
            }
        }).load();
	},
	getAllBrand: function(){
		var request = new Request({
			url : ctx+'/html/mobile/?m=getMobileBrand',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if(obj.success) {
					Array.each(a, function(item, index){
					$('mobileBrand').options.add(new Option(item,item));	
					});
				}else{
					// 没想好做些什么
				}
			}
			});
		request.post();
	},
	getAllMobile: function(){
		page = new JIM.UI.Paging({
            url:ctx+'/html/mobile/?m=getAllMobile',
            head:{el:'mobile-paging',showNumber:true},   
            limit:5,
            onAfterLoad: function(data){
            	var iconSrc ='';
    			var dis='';
            	Array.each(data.result, function(item, index){
        			if (item.icon == null) {
        				iconSrc = ctx + '/images/defTerim.jpg';
        			} else {
        				iconSrc = ctx + '/html/mobile/?m=getMobilePic&id='+ item.id;
        			}
        			dis += "<dd><img style='width: 90px;margin-left :5px;height:90px;' src='"
        					+ iconSrc
        					+ "'/>"
        					+ "<p style='width: 100px; word-wrap: break-word; overflow: hidden;'>"
        					+ item.brandChs + " " + item.type + "</p></dd>";
        		});
                $('mobile-list').set('html',dis);
            }
        }).load();
	},
	getMobileByBrand:function(brand){
		if(brand=="-1"){
			this.getAllMobile();
			$('mobileType').empty();
			$('mobileType').options.add(new Option("全部型号","-1"));
		}else{
		page = new JIM.UI.Paging({
            url:ctx+'/html/mobile/?m=getMobileByBrand&brand='+encodeURIComponent(brand),
            head:{el:'mobile-paging',showNumber:true},  
            limit:5,
            onAfterLoad: function(data){
            	var iconSrc ='';
    			var dis='';
            	Array.each(data.result, function(item, index){
        			if (item.icon == null) {
        				iconSrc = ctx + '/images/defTerim.jpg';
        			} else {
        				iconSrc = ctx + '/html/mobile/?m=getMobilePic&id='+ item.id;
        			}
        			dis += "<dd><img style='width: 90px;margin-left :5px;height:90px;' src='"
        					+ iconSrc
        					+ "'/>"
        					+ "<p style='width: 100px; word-wrap: break-word; overflow: hidden;'>"
        					+ item.brandChs + " " + item.type + "</p></dd>";
        		});
                $('mobile-list').set('html',dis);
            }
        }).load();
		//获取该品牌的所有型号手机
		var request = new Request({
			url : ctx+'/html/mobile/?m=getTypeByBrand',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var a = obj.message;
				if(obj.success) {
					$('mobileType').empty();
					$('mobileType').options.add(new Option("全部型号","-1"));
					Array.each(a, function(item, index){
					$('mobileType').options.add(new Option(item,item));	
					});
				}else{
					
				}
			}
		});
		request.post('brand='+brand);
		}
	},
	getMobileByKeyword:function(keyword){
		this.page = new JIM.UI.Paging({
            url:ctx+'/html/mobile/?m=getMobileByKeyword&keyword='+encodeURIComponent(keyword),
            head:{el:'mobile-paging',showNumber:true},  
            limit:5,
            onAfterLoad: function(data){
            	var iconSrc ='';
    			var dis='';
    			if(data.totalCount=='0'){
            		$('mobile-paging').setStyle("text-align","center");
            	}
            	Array.each(data.result, function(item, index){
        			if (item.icon == null) {
        				iconSrc = ctx + '/images/defTerim.jpg';
        			} else {
        				iconSrc = ctx + '/html/mobile/?m=getMobilePic&id='+ item.id;
        			}
        			dis += "<dd><img style='width: 90px;margin-left :5px;height:90px;' src='"
        					+ iconSrc
        					+ "'/>"
        					+ "<p style='width: 100px; word-wrap: break-word; overflow: hidden;'>"
        					+ item.brandChs + " " + item.type + "</p></dd>";
        		});
                $('mobile-list').set('html',dis);
            }
        }).load();
	},
	getMobileByBrandAndType:function(brand,type){
		if(type=='-1'){
			this.getMobileByBrand(brand);
		}else{
		this.page = new JIM.UI.Paging({
            url:ctx+'/html/mobile/?m=getMobileByBrandAndType&brand='+encodeURIComponent(brand)+"&type="+encodeURIComponent(type),
            head:{el:'mobile-paging',showNumber:true},  
            limit:5,
            onAfterLoad: function(data){
            	var iconSrc ='';
    			var dis='';
            	Array.each(data.result, function(item, index){
        			if (item.icon == null) {
        				iconSrc = ctx + '/images/defTerim.jpg';
        			} else {
        				iconSrc = ctx + '/html/mobile/?m=getMobilePic&id='+ item.id;
        			}
        			dis += "<dd><img style='width: 90px;margin-left :5px;height:90px;' src='"
        					+ iconSrc
        					+ "'/>"
        					+ "<p style='width: 100px; word-wrap: break-word; overflow: hidden;'>"
        					+ item.brandChs + " " + item.type + "</p></dd>";
        		});
                $('mobile-list').set('html',dis);
            }
        }).load();
		}
	}
});
	