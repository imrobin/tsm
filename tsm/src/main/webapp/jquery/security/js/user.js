$(document).ready(function () {
	var grid = $("#userGrid");
	var tableHeight = $('body').css('height');
	tableHeight = tableHeight.substr(0, tableHeight.length - 2);
	grid.jqGrid({ 
		url:ctx +'/html/user/?m=index',
		editurl : ctx + '/html/user/?',
		datatype: "json",
		mtype : 'GET',
		jsonReader : {
			root : "result",
			total : 'totalPage',
			records : 'totalCount',
			page : 'pageNo',
			repeatitems : false
	    },
	    prmNames : {page : 'page_pageNo', rows : 'page_pageSize', sort : 'page_orderBy', oper : 'm', deloper : 'remove', search : null, order : null},
		colNames:['编号', '用户名', '角色名', '角色', '密码', '真实姓名', '手机号', '邮箱', '状态'], 
		colModel:[ 
		   {name:'id',index:'id',hidden : true, editable:true},
		   {name:'userName', editable:true,index:'search_LIKES_userName',sortIndex:'userName',editrules:{required:true},formoptions:{elmsuffix:'<font color="red">*</font>'}}, 
		   {name:'sysRole_description', editable:false,index:'search_ALIAS_sysRoleL_LIKES_description'}, 
		   {name:'sysRole_roleName', editable:true, hidden:true,index:'sysRole_roleName', edittype:'select', editrules:{edithidden:true}, editoptions:{value:'SUPER_OPERATOR:超级管理员;OPERATOR_CUSTOMER_SERVICE:客服操作员'}}, 
		   {name:'password',index:'password',editable:true, hidden: true, edittype:'password', viewable :false, editrules:{edithidden:true,required:true}, formoptions:{elmsuffix:'<font color="red">*</font>'}},
		   {name:'realName', editable:true,index:'search_LIKES_realName'}, 
		   {name:'mobile', editable:true,index:'search_LIKES_mobile', editrules:{required:false, custom:true, custom_func:checkMobileNo}}, 
		   {name:'email', editable:true,index:'search_LIKES_email',editrules:{required:false,email:true}}, 
		   {name:'status', editable:true,search:true, stype:'select', edittype:'select',editoptions:{value:'1:有效;0:无效'}, searchoptions:{value:':全部;1:有效;0:无效'}, index:'search_EQI_status'} 
		],
		height : tableHeight - 215,
		autowidth : true,
		multiselect : false,
		toppager:true,
		cloneToTop:true,
		rowNum:15,
		rowList:[15,30,50,100], 
		rownumbers: true,
		pager: '#userGridPage', 
		sortname: 'id', 
		viewrecords: true, 
		sortorder: "desc", 
		caption:"用户列表"
	});
	grid.jqGrid('navGrid','#userGridPage',{
		add:true, addtext:'新增',
		edit:true, edittext:'修改', 
		del:true, deltext:'删除', 
		view:true, viewtext:'查看',
		refresh:true, refreshtext:'刷新',
		search:false,
		cloneToTop:true},
	{//编辑
		closeAfterEdit:true,
		viewPagerButtons:false,
		afterSubmit : showResult
	},
	{//新增
		closeAfterAdd:true,
		afterSubmit : showResult
	},
	{//删除
		afterSubmit : showResult
	}
	);
	grid.jqGrid('filterToolbar');
	var trow = $('tr.ui-search-toolbar');
	trow.css('display', 'none');
	grid.jqGrid('navButtonAdd' , '#' + grid[0].id + '_toppager' , {
        caption : "查询" ,  
        title : "查询" ,  
        buttonicon : 'ui-icon-search' ,  
        onClickButton : function () {
        	grid[0].toggleToolbar();
        	if (trow.css('display') == 'none') {
        		grid.setGridHeight(tableHeight - 215);
			} else {
				grid.setGridHeight(tableHeight - 238);
			}
        }  
    });
	
	$('div .ui-pg-div').css('cursor', 'pointer');
	//删除多余内容
    $("#" + grid[0].id + "_toppager_center").empty();
    $("#" + grid[0].id + "_toppager_right").empty();
    $("#" + grid[0].id + "_left").empty();

});