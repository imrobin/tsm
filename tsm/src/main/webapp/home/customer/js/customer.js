var Customer = Customer ? Customer : {};
Customer.resizeImage = new Class({
	Implements: [Options],
	//these are the default options
	options: {
		maxW:270,
		maxH:270       
	},					   
	initialize: function(img,z,options) {
		this.setOptions(options);
		this.z =$$(z);
		this.setImg($(img));
		this.setResize(this.z);
	},
	setImg:function(img){
		this.img = img;
	}, 
	setResize:function(z){
		var self = this;
		//执行相应的动作
		$each(z, function(el, i){
			el.addEvent('click', function(e){
				//获取原始图片大小的参数
				var initW = self.img.width, initH = self.img.height;
			//	alert(initW);
				
				//zoomOut,zoomIn
				var newW = initW, newH = 0;
				//图片比例初始化参数
				var maxW=self.options.maxW,maxH=self.options.maxH;
				//调用比例函数
				var newSize = self.scaleSize(maxW,maxH,initW,initH);
				e = new Event(e).stop();
				switch(el.rel){
					case 'zoomOut':
						newW -= 20; 
						var ratio = initH / initW;
						newH = newW * ratio;
						$("crop").width = newW;
						$("crop").height = newH;
//						alert($("crop").width+' '+$("crop").height);
						if ($("crop").getWidth() -20 < 90 || $("crop").getHeight()-20 < 90){
							$('zoomOut').setStyle('display','none');
						}
						if ($("crop").getWidth() -20 >= 90 && $("crop").getHeight() -20 >= 90){
							$('zoomOut').setStyle('display','');
						}		
						$('crop').setStyle('left',127 - $('crop').width/2);
						$('crop').setStyle('top',97 - $('crop').height/2);
						var left = 81 - ($("crop").getStyle('left')).toInt(),top = 52 - ($("crop").getStyle('top')).toInt();
						$('imgleft').set('value',left);
						$('imgtop').set('value',top);
						$('imgW').value=$("crop").getWidth();
						$('imgH').value=$("crop").getHeight();	 
						break;
					case 'zoomIn':
						newW += 20;
						var ratio = initH / initW;
						newH = newW * ratio;
						$("crop").width = newW;
						$("crop").height = newH;
//						alert($("crop").width+' '+$("crop").height);
						if ($("crop").getWidth() -20 < 90 || $("crop").getHeight()-20 < 90){
							$('zoomOut').setStyle('display','none');
						}
						if ($("crop").getWidth() -20 >= 90 && $("crop").getHeight() -20 >= 90){
							$('zoomOut').setStyle('display','');
						}		
						$('crop').setStyle('left',127 - $('crop').width/2);
						$('crop').setStyle('top',97 - $('crop').height/2);
						var left = 81 - ($("crop").getStyle('left')).toInt(),top = 52 - ($("crop").getStyle('top')).toInt();
						$('imgleft').set('value',left);
						$('imgtop').set('value',top);
						$('imgW').value=$("crop").getWidth();
						$('imgH').value=$("crop").getHeight();	 
						break;
					case 'reset':
						$("dragable-holder").removeClass("none");
						$("bigLink").removeClass("none");
						$("crop").erase('width');
						$("crop").erase('height');
						$("crop").set("src", ctx + imgSrc);
						$("crop").onload   =   function(){ 
							var width = $('crop').width.toInt();
							var height = $('crop').height.toInt();
							if (width > 260 &&height<200 ){
								$('crop').width = 260;
								$('crop').height =(260/width)*height;
							}else if (width<260 &&height>200 ){
								$('crop').height = 200;
								$('crop').width =(200/height)*width;
							}else if (width>260 &&height>200){
								if ((260/width) > (200/height)){
									$('crop').height = 200;
									$('crop').width =(200/height)*width;
								}else{
									$('crop').width = 260;
									$('crop').height =(260/width)*height;
								}
							}
							if (width<90){
								$('crop').width = 90;
								$('crop').height =(90/width)*height;
							}else if (height<90){
								$('crop').height = 90;
								$('crop').width =(90/height)*width;
							}
							if (width<90 && height<90){
								if ((90/width) < (90/height)){
									$('crop').height = 90;
									$('crop').width =(90/height)*width;
								}else{
									$('crop').width = 90;
									$('crop').height =(90/width)*height;
								}
							}
							$('crop').setStyle('left',127 - $('crop').width/2);
							$('crop').setStyle('top',97 - $('crop').height/2);
						//	alert($("crop").getWidth());
							if ($("crop").getWidth() -20 < 90 || $("crop").getHeight()-20 < 90){
								$('zoomOut').setStyle('display','none');
							}
							if ($("crop").getWidth() -20 >= 90 && $("crop").getHeight() -20 >= 90){
								$('zoomOut').setStyle('display','');
							}	
							var left = 81 - ($("crop").getStyle('left')).toInt(),top = 52 - ($("crop").getStyle('top')).toInt();
							$('imgleft').set('value',left);
							$('imgtop').set('value',top);
							$('imgW').value=$("crop").getWidth();
							$('imgH').value=$("crop").getHeight();	
						} 
						break;
					default:
						newW = initW;		            
				}	

			});
		});						
	},
	//比例函数
	scaleSize: function(maxW,maxH,currW,currH){
		var ratio = currH / currW;
		if(currW >= maxW){
			currW = maxW;
			currH = currW * ratio;
		} else if(currH >= maxH){
			currH = maxH;
			currW = currH / ratio;
		}
		return [currW, currH];
	}
});
Customer.Cal= new Class({
	options:{
	},
	initialize: function(options){
	},
	adjustDay:function(month){
		 var day = $('day').get('value');
		 $('day').empty();
			for(var i=0;i<28;i++){
				   $('day').options.add(new Option(i+1, i+1));  
			}
			switch(month){
			   case "1":
			   case "3":
			   case "5":
			   case "7":
			   case "8":
			   case "10":
			   case "12":{
				$('day').options.add(new Option(29, 29));   
				$('day').options.add(new Option(30, 30));   
			    $('day').options.add(new Option(31, 31));
			   }
			   break;
			   case "2":{
			    var nYear=$('year').get('value');
			    if(nYear%400==0 || nYear%4==0 && nYear%100!=0)$('day').options.add(new
			Option(29, 29));
			   }
			   break;
			   default:{
				   $('day').options.add(new Option(29,29));
				   $('day').options.add(new Option(30,30));
			   }
			}
			if(day==31){
				switch(month){
				   case "1":
				   case "3":
				   case "5":
				   case "7":
				   case "8":
				   case "10":
				   case "12":{
					$('day').set('value',day);   
				   }
				   break;
				   case "2":{
					    var nYear=$('year').get('value');
					    if(nYear%400==0 || nYear%4==0 && nYear%100!=0){
					    	$('day').set('value',29);
					    }else{
					    	$('day').set('value',28);
					    }
				   }    
				   break;
				   default:{
					   $('day').set('value',30);
				   }
				}
			}else if(day==29||day==30){
				switch(month){
				case "2":{
					var nYear=$('year').get('value');
				    if(nYear%400==0 || nYear%4==0 && nYear%100!=0){
				    	$('day').set('value',29);
				    }else{
				    	$('day').set('value',28);
				    }
				}
				break;
				default:{
					$('day').set('value',day);
				}
				}
			}else{
				$('day').set('value',day);
			}
	},
	getCustomerInfo: function(){
		var request = new Request({
			url : ctx+'/html/customer/?m=center',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var bln = obj.success;
				if(bln) {
					$('location').set('value',obj.message.location);
					$('nickName').set('value',obj.message.nickName);
					$('address').set('value',obj.message.address);
					$('zip').set('value',obj.message.zip);
					if(obj.message.sex==1){
						$('sex-x').set('checked','checked');
					}else if(obj.message.sex==0){
						$('sex-o').set('checked','checked');
					}
					var year = obj.message.year;
					for(var i=1930;i<=2000;i++){
						$('year').options.add(new Option(i,i));
					}
					$('year').set('value',year);
					var month = obj.message.month;
					for(var i=0;i<12;i++){
						   $('month').options.add(new Option(i+1, i+1));
				    }
					$('month').set('value',month);
					var day = obj.message.day;     
					 for(var i=0;i<28;i++){
						   $('day').options.add(new Option(i+1, i+1));  
						  }
						  if($('month').get('value')!="2"){
						   $('day').options.add(new Option(29, 29));
						   $('day').options.add(new Option(30, 30));
						  }
						  switch($('month').get('value')){
						   case "1":
						   case "3":
						   case "5":
						   case "7":
						   case "8":
						   case "10":
						   case "12":{
						    $('day').options.add(new Option(31, 31));
						   }
						   break;
						   case "2":{
						    var nYear=$('year').get('value');
						    if(nYear%400==0 || nYear%4==0 && nYear%100!=0)$('day').options.add(new
						Option(29, 29));
						   }
						  }
					   $('day').set('value',day);
				}else{
					// 没想好做些什么
				}
			}
			});
		request.post();
	},

	passwordStrength:function(ctx,password){     
		 var desc = new Array();
	        desc[0] = "弱";
	        desc[1] = "中";
	        desc[2] = "强";
		var score   = 0;            
       //if password has both lower and uppercase characters give 1 point              
		if (password.match(/[a-zA-Z]/)) score++;        //if password has at least one number give 1 point        
		if (password.match(/\d+/)) score++;        //if password has at least one special caracther give 1 point        
		if (password.match(/.[!,@,#,$,%,^,&,*,?,_,~,-,(,)]/) ) score++;        //if password bigger than 12 give another 1 point        
		if(score>0){
		$('iPwdMeter').set('html','&nbsp;&nbsp;&nbsp;'+desc[score-1]);         
		$('iPwdMeter').setStyle('width',score*72+'px');         
		$('iPwdMeter').setStyle('background-image','url('+ctx+'/images/password_meter.gif'+')');
		$('iPwdBack').setStyle('background-image','url('+ctx+'/images/password_meter_grey.gif'+')');
		}
	},
	upload : function() {

//		var uploader = new JIM.AjaxUploadFile({
//			url : ctx + '/html/image/?m=upload',
//			fileElementId : 'headlogo',
//			onSuccess : this.afterUpload
//		});
//		if(rImage) rImage==undefined;
//		uploader.upload();
	},
	afterUpload:function(result){
		if(result.success){
			$("dragable-holder").removeClass("none");
			$("bigLink").removeClass("none");
			$("crop").erase('width');
			$("crop").erase('height');
			$("crop").set("src", ctx + result.message.tempRalFilePath);
			imgSrc = ctx + result.message.tempRalFilePath;
			$("crop").onload   =   function(){ 
				var width = $('crop').width.toInt();
				var height = $('crop').height.toInt();
				if (width > 260 &&height<200 ){
					$('crop').width = 260;
					$('crop').height =(260/width)*height;
				}else if (width<260 &&height>200 ){
					$('crop').height = 200;
					$('crop').width =(200/height)*width;
				}else if (width>260 &&height>200){
					if ((260/width) > (200/height)){
						$('crop').height = 200;
						$('crop').width =(200/height)*width;
					}else{
						$('crop').width = 260;
						$('crop').height =(260/width)*height;
					}
				}
				if (width<90){
					$('crop').width = 90;
					$('crop').height =(90/width)*height;
				}else if (height<90){
					$('crop').height = 90;
					$('crop').width =(90/height)*width;
				}
				if (width<90 && height<90){
					if ((90/width) < (90/height)){
						$('crop').height = 90;
						$('crop').width =(90/height)*width;
					}else{
						$('crop').width = 90;
						$('crop').height =(90/width)*height;
					}
				}
				$('crop').setStyle('left',127 - $('crop').width/2);
				$('crop').setStyle('top',97 - $('crop').height/2);
				if ($("crop").getWidth() -20 < 90 || $("crop").getHeight()-20 < 90){
					$('zoomOut').setStyle('display','none');
				}
				if ($("crop").getWidth() -20 >= 90 && $("crop").getHeight() -20 >= 90){
					$('zoomOut').setStyle('display','');
				}	
				var left = 81 - ($("crop").getStyle('left')).toInt(),top = 52 - ($("crop").getStyle('top')).toInt();
				$('imgleft').set('value',left);
				$('imgtop').set('value',top);
				$('imgW').value=$("crop").getWidth();
				$('imgH').value=$("crop").getHeight();	
			} 
			$("filename").value = result.message.filename;
			
			if(rImage) rImage.setImg($('crop'));
			else {
				rImage = new Customer.resizeImage('crop','.bigLink a');
			}
		}else{
			new LightFace.MessageBox().error(result.message);
		}
	},
	cutImage:function(ctx){
		/*alert($('filename').get('value')+'&top=' + $('imgtop').get('value') + '&left='
				+ $('imgleft').get('value')+"&width="+$('imgW').get('value')+"&height="+$('imgH').get('value'));*/
		var request = new Request({
			url : ctx+'/html/image/',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var bln = obj.success;
				if(bln) {
					new LightFace.MessageBox( {onClose : function() {window.location.reload();}.bind(this)}).info("头像保存成功"); 
				}else{
					new LightFace.MessageBox().error2("头像保存失败");
				}
			}
			});
		request.post('m=cut&filename='+$('filename').get('value')+ '&top=' + $('imgtop').get('value') + '&left='
				+ $('imgleft').get('value')+"&imgW="+$('imgW').get('value')+"&imgH="+$('imgH').get('value'));
	},
	loadUserIcon:function(){
		var request = new Request.JSON({
			url : ctx+'/html/customer/?m=loadUserIcon',
			onSuccess : function(result) {
			if(result.success) {
				$('userlogo').set('src',ctx+result.message);
			}else{
				new LightFace.MessageBox().info(result.message);
			}
		}
		}).post();
	},
	checkMobile:function(ctx,mobile){
        var bln = false;	
     	var request = new Request({
			url : ctx+'/html/customer/',
			async : false,
			onSuccess : function(responseText) {
				var object = JSON.decode(responseText);
				bln = object.success;
				if(bln) {
					$('mobile_tip').set('html','<p class="explain left"><font color="red">该手机号码'+$('userMobile').get('value')+'已经被注册</font></p>');
					$('userMobile').set('value','');
					
				}else{
					$('mobile_tip').set('html','<p class="explain left" style="vertical-align:text-bottom"><img src="'+ctx+'/images/regf.png" width=20 height=20 style="vertical-align:text-bottom"><font color="green" size="3">该手机号码未被注册，可以使用</font></p>');
				}
			}
			});
		request.post('m=checkMobile&mobile=' + mobile);
	},
	checkEmail:function(ctx,email){
        var bln = false;	
     	var request = new Request({
			url : ctx+'/html/customer/',
			async : false,
			onSuccess : function(responseText) {
				var object = JSON.decode(responseText);
				bln = object.success;
				if(bln) {
					$('email_tip').set('html','<p class="explain left"><font color="red">该邮箱'+$('email').get('value')+'已经被注册，请输入其它邮箱帐户</font></p>');
					$('email').set('value','');
				}else{
					$('email_tip').set('html','<p class="explain left" style="vertical-align:text-bottom"><img src="'+ctx+'/images/regf.png" width=20 height=20 style="vertical-align:text-bottom"><font color="green" size="3">该邮箱未被注册，可以使用</font></p>');
				}
			}
			});
		request.post('m=checkEmail&email=' + email);
	},
	modifyCustomer:function(ctx){
		var request = new Request({
			url : ctx+'/html/customer/?m=modifyCustomer',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var bln = obj.success;
				if(bln) {
					new LightFace.MessageBox().info('修改成功');
				}
				else{
					new LightFace.MessageBox().error2('修改失败');
				}
			}
			});
		request.post($('modifyCustomerForm').toQueryString());
	},
	modifyPwd:function(ctx){
		var request = new Request({
			url : ctx+'/html/customer/?m=modifyPwd',
			async : false,
			onSuccess : function(result) {
				var obj = JSON.decode(result);
				var bln = obj.success;
				if(bln) {
					new LightFace.MessageBox().info('修改成功');
				}else{
					new LightFace.MessageBox().error(obj.message);
				}
			}
			});
		request.post($('modifyPwdForm').toQueryString());
	},
	regWaiting:function(){
        var waitingText = "正在验证注册信息，请稍候";  
        ajaxFace = new LightFace.Request(
				{
					mask : true,
					draggable : true,
					content : waitingText,
					title : '注册中',
					titleImage : ctx+'/images/bg_loading.gif'	
				}).open(); 
	        waitingInterval = setInterval(function(){},100);  
	},
	refreshImage: function() {
		var timestamp = (new Date()).valueOf();
		$('captchaImage').set('src', ctx + '/j_captcha_get?t=' + timestamp);
	},
	setProvince: function(){
	var pronvince = $('location');
	var options = pronvince.options;
	options.add(new Option("北京","北京"));
	options.add(new Option("天津","天津"));
	options.add(new Option("河北","河北"));
	options.add(new Option("山西","山西"));
	options.add(new Option("内蒙古","内蒙古"));
	options.add(new Option("辽宁","辽宁"));
	options.add(new Option("吉林","吉林"));
	options.add(new Option("黑龙江","黑龙江"));
	options.add(new Option("上海","上海"));
	options.add(new Option("江苏","江苏"));
	options.add(new Option("浙江","浙江"));
	options.add(new Option("安徽","安徽"));
	options.add(new Option("福建","福建"));
	options.add(new Option("江西","江西"));
	options.add(new Option("山东","山东"));
	options.add(new Option("河南","河南"));
	options.add(new Option("湖北","湖北"));
	options.add(new Option("湖南","湖南"));
	options.add(new Option("广东","广东"));
	options.add(new Option("广西","广西"));
	options.add(new Option("海南","海南"));
	options.add(new Option("重庆","重庆"));
	options.add(new Option("四川","四川"));
	options.add(new Option("云南","云南"));
	options.add(new Option("贵州","贵州"));
	options.add(new Option("西藏","西藏"));
	options.add(new Option("陕西","陕西"));
	options.add(new Option("甘肃","甘肃"));
	options.add(new Option("宁夏","宁夏"));
	options.add(new Option("青海","青海"));
	options.add(new Option("新疆","新疆"));
	}
});