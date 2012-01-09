App = {};
var fathertype = 'litypeall';
var firstLoad=true;
App.Index = new Class( {

	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.loadApplication();
		this.loadParentType();
	},
	loadApplication : function() {
		$('nextpage').set('html', '');
		this.loadImg = new Element('img', {
			'src' : ctx + '/admin/images/ajax-loader.gif',
			'class' : 'icon16',
			width : 16, 
			height : 16
		}).inject($('cont'));
		$('cont').appendText('数据加载中...');
		var types = "&childs="+childs;
		if (father != null && father != ''){
			types = "&father="+father;
		}
		if (fathertype != null && fathertype != '' && fathertype != 'litypeall'){
			types = "&father="+fathertype;
		}
		//	name = name.replace(/\+/g, '%2B').replace(/\"/g,'%22').replace(/\'/g, '%27').replace(/\//g,'%2F');
			url =  ctx + "/html/application/?m=advanceSearch&search_ALIAS_spL_NEI_inBlack=1&search_EQI_status=1&" +
					"&name="+encodeURIComponent(name)+types+'&sp='+sp+'&star='+star;
		//	alert(url);
        new JIM.UI.Paging({
            url:url,
            limit:2,
            head:{el:'nextpage', showNumber: true, showText : false},
            onAfterLoad: this.onComplete.bind(this)
        }).load();		
	},
	onComplete : function(result) {
		if (firstLoad){
			new Element('b',{'id':'totalapp'}).set('html','总共有'+result.totalCount+'个应用').inject($('nextpage'),'top');
			firstLoad = false;
		}
		this.cleanData();
		this.loadImg.dispose();
	//	$('nextpage').set('text','总共有'+result.totalCount+'个应用');
		result.result.forEach(function(e, index) {
			var des = e.description;
			if (e.description != null && e.description.length >80){
				des = e.description.substring(0,80)+'...';
			}
			var name = DataLength(e.name,30);
			var href = ctx+'/home/app/appinfo.jsp?id='+e.id;
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId="+e.id;
			if (!e.hasIcon){
				iconSrc =ctx+"/images/defApp.jpg";
			}
			var html = "<dl><dt><a href='"+href+"'><img width='92' height='92'   style='border:0px' src='"+iconSrc+"' id='img"+e.id+"'/></a>" +
					"</dt><dd><div class='applisttitle'><a href='"+href+"'><h1 class='left' id='name0'>"+name+"</h1></a><span class='left'>" +
					"<img src='"+ctx+"/images/s_"+e.avgCount+".png'  style='border:0px' width='96' height='16' /></span><span class='applisttitletj'>" +
							"下载次数：<b id='down0'>" +(e.downloadCount==undefined?'0':e.downloadCount)+
					"</b>  </span></div><p class='applistinfo' id='info0'>"+(des==null?'':des)+"</p>" +
					"<p>提供商: " + e.spName +"</p><p>所在地: " + e.location +"</p><p class='applistbutton'><a href='"+href+"' id='href0'><img style='border:0px' src='"+ctx+
					"/images/button_1.gif' />" +
					"</a></p></dd></dl>";
			var appdiv = new Element('div', {'class' : 'applist'}).set('html',html).inject($('cont'));
		});
		if (result.totalCount == 0){
			var html = "<dl><dt>无符合条件的结果!</dd></dl>";
			new Element('div', {'class' : 'applist'}).set('html',html).inject($('cont'));
		}
	}, 
	loadParentType : function() { 
		var url =  ctx + "/html/applicationType/?m=getByCriteria&search_EQI_typeLevel=1";
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			onSuccess : this.onParentComplete.bind(this)
		}).post();
	},
	onParentComplete : function(result) {
		var all = new Element('li', {'style': "cursor:pointer" , 'text' : '所有应用','id':'litypeall'});
		all.inject($('type'));
		result.result.forEach(function(e, index) {
			var father = new Element('li', {'style': "cursor:pointer" , 'text' : maxText(e.name,8),'id':e.id ,'title' : e.name});
			father.inject($('type'));
		});
		var typeBar = 1;
		if (((result.result.length +1)%7) != 0){
			typeBar = parseInt((result.result.length +1)/7) + 1;
		}
		$('apptitle').setStyle('height',typeBar*50);
		$('type').getChildren('li').forEach(function(e, index2) {
			if (fathertype == e.get('id')){
				e.set('class','b');
			}else{
				e.set('class','s');
			}
			if (fathertype == 'litypeall'){
				$('litypeall').set('class','b');
			}
			
			e.addEvent('click',function(){
				fathertype = e.id;
				index.cleanData();
				firstLoad = true;
				index.loadApplication();
				$('type').getChildren('li').forEach(function(e2, index3) {
					if (fathertype == e2.get('id')){
						e2.set('class','b');
					}else{
						e2.set('class','s');
					}
					if (fathertype == 'litypeall'){
						$('litypeall').set('class','b');
					}
				});
			});
		});
	},
	cleanData : function (){
		$('cont').set('html', '');
	}
});


function maxText(str, len) {
	var newLength = 0;
	var newStr = "";
	var hasDot = true;
	var chineseRegex = /[^\x00-\xff]/g;
	var singleChar = "";
	if(!$chk(str)){str=''};
	var strLength = str.replace(chineseRegex, "**").length;
	for ( var i = 0; i < strLength; i++) {
		singleChar = str.charAt(i).toString();
		newLength += 2;
		if (newLength > len) {
			break;
		}
		newStr += singleChar;
	}

	if (hasDot && strLength > len) {
		newStr += "...";
	}
	return newStr;
}
