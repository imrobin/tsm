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
		var url = ctx + "/html/spBaseInfo/?m=recommendSp&page_orderBy=orderNo_asc&page_pageSize=5" +
				"&search_ALIAS_spL_EQI_status=1&search_ALIAS_spL_NEI_inBlack=1";
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			onSuccess : this.onRecommandComplete.bind(this)
		}).post();
	}
	,
	onRecommandComplete : function(result) {
		result.result.forEach(function(e, index) {
			var name = DataLength(e.sp_name,20);
			var imgSrc = (e.hasLogo) ? ('/html/spBaseInfo/?m=loadSpFirmLogo&id='+e.sp_id) : '/images/defsp.jpg';
			var spDetailUrl = ctx + '/home/sp/spinfo.jsp?id='+e.sp_id;

			$('recomdd' + index).set('html',"<p class='top_img'><a href='"+spDetailUrl+"'>" +
					"<img width='45' height='40'  style='border:0px' src='"+ctx+imgSrc + "' title='"+e.sp_name+"' /></a></p><p class='top_text'>" +
					"<a href='"+spDetailUrl+"'>"+name+"</a><br/>"+
					"应用数量: <span class='c_r'>"+(e.availableApplicationSize==undefined?'0':e.availableApplicationSize)+"</span>");
		});
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