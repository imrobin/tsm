Search = {};
var hasChecked = new Array();
Search = new Class( {

	options : {
		url : '',
		param : {}
	},
	initialize : function(options) {
		this.loadParentType();
		this.loadSp();
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
		new Element('option', {'text' : '请选择...', 'value' : '-1'}).inject($('parentType'));
		result.result.forEach(function(e, index) {
			new Element('option', {'text' : e.name, 'value' : e.id}).inject($('parentType'));
		});
	},
	cleanData : function (){
		$('childType').set('html', '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
	},
	getChild : function(parentId) { 
		this.cleanData();
		hasChecked = new Array();
		var url =  ctx + "/html/applicationType/?m=getChild&parentId="+parentId;
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			onSuccess : this.onGetChild.bind(this)
		}).post();
	},
	onGetChild : function(result) {
		if (result.totalCount == '0'){
			new Element('label').set('html',"无类别信息").inject($('childType'));
		}
		result.result.forEach(function(e, index) {
			new Element('label').set('html',"<input class='inchebox' name='childType' id='childbox"+e.id+"' type='checkbox' value='"+e.id+"' >"+e.name + "</input>").inject($('childType'));
			$("childbox"+e.id).addEvent('click', function(){
				if ($(this).checked){
					hasChecked.push($(this).value);
				}else{
					hasChecked.remove($(this).value);
				}
			});
		});
	},
	loadSp : function() { 
		var url =  ctx + "/html/spBaseInfo/?m=index&search_EQI_status=1&search_NEI_inBlack=1";
		var login = this;
		this.request = new Request.JSON( {
			url : url,
			onSuccess : this.onloadSp.bind(this)
		}).post();
	},
	onloadSp : function(result) {
		new Element('option', {'text' : '请选择...', 'value' : ''}).inject($('sp'));
		result.result.forEach(function(e, index) {
			new Element('option', {'text' : e.name, 'value' : e.id}).inject($('sp'));
		});
	}
});
Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};