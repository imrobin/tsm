<%@ page language="java" pageEncoding="UTF-8" %>
<script type="text/javascript">
var searchType = '${param.searchType}' || 1;
function goUrl(href){
	self.location = href;
}
window.addEvent('domready', function(){
	$('searchSelect').addEvent('mouseover', function(){
		$('searchTypeList').setStyle('display', '');
	});
	$('searchSelect').addEvent('mouseout', function(){
		$('searchTypeList').setStyle('display', 'none');
	});
	$('button').addEvent('click', function(){
		var href = '${ctx}/home/app/appindex.jsp?searchType='+searchType;
		if (searchType != 1){
			href = '${ctx}/home/sp/spindex.jsp?searchType='+searchType;
		} 
		if ($('searchName').get('value') != ''){
			var nameV = $('searchName').get('value').trim();
			$('searchName').set('value',nameV);
			//nameV = nameV.replace(/\+/g, '%2B').replace(/\"/g,'%22').replace(/\'/g, '%27').replace(/\//g,'%2F');
			href += "&searchName="+encodeURIComponent(nameV);
		}
		goUrl(href);
	});
	var options = $('searchTypeList').getElements('a');
	$each(options, function(option, i){
		option.addEvent('click', function(){
			$('searchType').set('html', option.get('html'));
			if (option.get('html') == '搜索应用'){
				searchType = 1;
			} else if (option.get('html') == '搜索提供商') {
				searchType = 2;
			}
			$('searchTypeList').setStyle('display', 'none');
		});
	});
	$('searchName').addEvent('keydown', function(event){
		//alert(event.key);
	    if (event.key == "enter") {   
	    	$('button').click();
	    }; 
	});
	if (searchType == 2){
		$('searchType').set('html', $('spSearchType').get('html'));
	}
});
</script>
<div class="left">
	<p class="search_hot"><!--热门搜索：
		<a class="b" href="#"> iphone </a> 
		<a class="b" href="#"> 电子钱包 </a>
		<a class="b" href="#"> 企业一卡通 </a>
		<a class="b" href="#"> Android </a>
		<a class="b" href="#"> java </a>
	--></p>
	<p>
		<span id="searchSelect" class="search_s"><a id="searchType" class="s_s" href="#">搜索应用</a>
			<span id="searchTypeList" class="mune_s" style="display: none; margin-top: -2px">
				<a class="m_s" style="text-align: left; padding-left: 5px;" href="#" id="appSearchType">搜索应用</a>
				<a class="m_s" style="text-align: left; padding-left: 5px;" href="#" id="spSearchType">搜索提供商</a>
			</span>
		</span>
		<span class="search_bg"> <input name="searchName" type="text"  id="searchName" value="${param.searchName}"/></span>
		<button class="search_button" id="button"></button>
		<span class="search_m"> <a href="${pageContext.request.contextPath}/home/app/advanceSearch.jsp"> 高级搜索 </a> </span>
	</p>
</div>