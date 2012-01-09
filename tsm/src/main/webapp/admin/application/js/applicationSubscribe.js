/**
 * 用户订购关系查询
 */
var ApplicationSubscribe = ApplicationSubscribe ? ApplicationSubscribe : {};

//用户卡应用查询
ApplicationSubscribe.list = new Class({
	Implements : [Events,Options],
	options :{},
	headerText : '用户订购关系查询',
	
	initialize : function(options){
		this.drawGrid();
	},
	drawBox : function() {
		this.infoBox = new LightFace( {
			width : 700,
			height : 400,
			draggable : true,
			initDraw : false,
			onClose : function() {
				var div = document.getElement('div[class=fc-tbx]');
				if ($chk(div)) {
					div.dispose();
				}
			},
			buttons : [ {
				title : '关 闭',
				event : function() {
					this.close();
				}
			} ]
		});
	},
	drawGrid : function() {
		this.grid = new JIM.UI.Grid('tableDiv', {
			url : ctx + '/html/cardApp/?m=listByCustomer',
        	multipleSelection: false,
        	columnModel : [
        	    {dataName : 'id', identity : true},
        	    {title : '手机号码', dataName : 'cci_mobileNo'},
        	    {title : '终端名称', dataName : 'cci_name'},
        	    {title : '终端状态', dataName : 'cci_status'},
        	    {title : '应用名称', dataName : 'ca_applicationVersion_application_name'}, 
        	    {title : '应用版本', dataName : 'ca_applicationVersion_versionNo'}, 
        	    {title : '应用状态', dataName : 'ca_status'},
        	    {title : '占用存储空间大小(byte)', dataName: 'ca_usedNonVolatileSpace'},
        	    {title : '占用内存空间小大(byte)', dataName: 'ca_usedVolatileSpace'},
        	    {title : '是否可迁移', dataName: 'ca_migratable'}
        	    
        	],
        	searchButton : true,
        	searchBar : {
        		filters : [
        		    {title : '手机号码：', name : 'mobileNo', type : 'text',width :100}
        		]
        	},
        	headerText : this.headerText,
        	headerImage : ctx + '/admin/images/text_padding_left.png'
        });
	}
});