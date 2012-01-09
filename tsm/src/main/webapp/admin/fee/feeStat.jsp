<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>计费统计</title>
<style>
.rightcont{
 background:#fff;
}
</style>
<link href="${ctx}/webhtml/admin/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/red/formcheck.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/date/datepicker/datepicker_vista.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/mootools/mootools-core-1.3.2.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/menu/menu.js"></script>
<script type="text/javascript" src="${ctx}/lib/date/datepicker/datepicker.js"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
    window.addEvent('domready', function(){
       new DatePicker('.demo_fee', {
    			pickerClass: 'datepicker_vista',
    			inputOutputFormat: 'Ym',
    			format:'Ym',
    			yearPicker: true,
    			days: ['日', '一', '二', '三', '四', '五', '六'],
    			months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    		});
    	var request = new Request.JSON({
			url : ctx+'/html/feerulespace/?m=getSpName',
			async : false,
			onSuccess : function(data) {
				$('spId').empty();
				var a = data.message;
				    $('spId').options.add(new Option('请选择应用提供商',''));
				if(data.success) {  
					Array.each(a, function(item, index){
					$('spId').options.add(new Option(item[1],item[0]));	
					});
				}else{
					
				}
			}
		});
		request.post();	
		var validater = new FormCheck('searchForm', {
				submit : false,
				display : {
					showErrors : 1,
					errorsLocation : 1,
					indicateErrors : 1,
					keepFocusOnError : 0,
					closeTipsButton : 0,
					removeClassErrorOnTipClosure:1
				},
				onValidateSuccess : function() {
					if(!$chk($('month').get('value'))){
						var now= new Date();   
						var year=now.getFullYear(); 
						var month=now.getMonth()+1;   
						if(month<10){
							month = '0'+month;
						}
						else{
							month = ''+month;
						}
						$('month').set('value',year+month);
					}
					window.open(ctx+'/html/feestat/?m=getFeeStat&spId='+$('spId').get('value')+"&date="+$('month').get('value'),'计费统计');
				}
	   });
    });
</script>
</head>

<body>
<div id="container">
<%@ include file="/admin/layout/top.jsp"%>
<div id="main">
<%@ include file="/admin/layout/menu.jsp"%>

	<div id="right">
		<div class="rightbo">
		<center><div>
				<form id="searchForm" name="searchForm" action="" method="post">
		<p >应用提供商:
		
			<select id="spId" name="spId" class="validate['required']" size="1">
			</select>
		</p>
		<p>&nbsp;</p>
		<p >月份:	
		<input id="month" class="demo_fee" name="month" maxlength="8" size="8" type="text" value=""/>
		</p>
		<p>
		<input id="search" type="button" class="validate['submit']" value="查询" />
		</p>
		</form>
		<!-- <div id="tableDiv" class="rightcont" style="height: 450px;">
		</div> -->
		</div>
		</center>
	</div>
</div>
<div id="footer" class="clear"><p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p></div>
</div>
</body>
</html>