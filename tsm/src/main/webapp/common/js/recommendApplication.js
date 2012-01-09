Recommend = {};
Recommend = new Class( {

	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.loadRecommand();
	},
	loadRecommand : function() {
		var url = ctx + "/html/application/?m=recommendApplication&page_orderBy=orderNo_asc&page_pageSize=5&search_EQS_showAll=no";
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			async : false,
			onSuccess : this.onRecommandComplete.bind(this)
		}).post();
	},
	loadRecommandLocal : function() {
	//	alert(1);
		var url = ctx + "/html/application/?m=recommendApplication&page_orderBy=orderNo_asc&page_pageSize=5&search_EQS_showAll=no&local=true";
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			async : false,
			onSuccess : this.onRecommandComplete.bind(this)
		}).post();
	}
	,
	onRecommandComplete : function(result) {
		$('recomdd0').set('html','');
		$('recomdd1').set('html','');
		$('recomdd2').set('html','');
		$('recomdd3').set('html','');
		$('recomdd4').set('html','');
		result.result.forEach(function(e, index) {
			var name = DataLength(e.application_name,20);
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId="+e.application_id;
			if (!e.hasIcon){
				iconSrc =ctx+"/images/defApp.jpg";
			}
			$('recomdd' + index).set('html',"<p class='top_img'><a href='"+ctx+'/home/app/appinfo.jsp?id='+e.application_id+"'>" +
					"<img width='45' height='40'  style='border:0px' src='"+iconSrc + "' /></a></p><p class='top_text'>" +
					"<a href='"+ctx+'/home/app/appinfo.jsp?id='+e.application_id+"' title='"+e.application_name+"'>"+name+"</a>" +
							"<br/><img  style='border:0px' src='"+ctx+"/images/ls_"+
					(e.application_starNumber == null ? 0:e.application_starNumber )+".png' width='58' height='9' /><br/>"+
					"下载次数: <span class='c_r'>"+(e.application_downloadCount==undefined?'0':e.application_downloadCount)+"</span></p>");
		});
		new Request.JSON( {
			url : ctx + "/html/application/?m=isLogin",
			async : false,
			onSuccess : function(json) {
				if (json.success) {
				//	alert(1);
					$('recomlocal').setStyle('display','');
				}
			}.bind(this)
		}).post();
	}
});
function DataLength(fData,expectLength)   
{   
    var intLength=0   
    for (var i=0;i<fData.length;i++)   
    {   
        if ((fData.charCodeAt(i) < 0) || (fData.charCodeAt(i) > 255))   {
            intLength=intLength+2;   
        }else if ((fData.charCodeAt(i) > 48) && (fData.charCodeAt(i) < 58)){
        	intLength=intLength+1.3;  
        }
        else  {
            intLength=intLength+1;       
        }
        if (expectLength<intLength){
        	return fData.substring(0,i)+'...';
        }
    }
    return fData;
}