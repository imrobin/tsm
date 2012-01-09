Top = {};
Top = new Class( {

	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.loadTop();
	},
	loadTop : function() { 
		var url = ctx + "/html/application/?m=advanceSearch&page_pageSize=5";
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			onSuccess : this.onTopComplete.bind(this)
		}).post();
	},
	onTopComplete : function(result) {
		result.result.forEach(function(e, index) {
			var name = DataLength(e.name,20);
			var iconSrc = ctx + "/html/application/?m=getAppPcImg&appId="+e.id;
			if (!e.hasIcon){
				iconSrc =ctx+"/images/defApp.jpg";
			}
			$('topdd' + index).set('html',"<p class='top_img'><a  href='"+ctx+'/home/app/appinfo.jsp?id='+e.id+"'>" +
					'<img  border="0" width="38" height="38"  src="' + iconSrc + '"/></a></p><p class="top_text">' +
				"<a  title='"+e.name+"'  href='"+ctx+'/home/app/appinfo.jsp?id='+e.id+"'>"+name+"</a><br/>" +
				"<img  style='border:0px' src='"+ctx+"/images/ls_"+e.avgCount+".png' width='58' height='9' /><br/>"+
			  "下载次数: <span class='c_r'>"+(e.downloadCount==undefined?'0':e.downloadCount)+"</span></p>");
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