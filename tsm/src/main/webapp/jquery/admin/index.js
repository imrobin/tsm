$(document).ready(function () {
	$('body').layout({ 
		applyDefaultStyles: false,
		north__closable : false,
		north__size : 50,
		north__minSize : 50,
		north__maxSize : 50,
		west__resizable : false,
		west__size : 200
	});
	$("#leftMenu").accordion({
		autoHeight: false,
		navigation: true,
		multipleMode: true,
		collapsible : true
	});
	
	var menuOpen = [];
	var $tabs = $( "#tabs").tabs({
		tabTemplate: "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
		add: function( event, ui ) {
			$tabs.tabs("select", ui.index);
		}
	});
	$( "#tabs span.ui-icon-close" ).live( "click", function() {
		var index = $( "li", $tabs ).index( $( this ).parent() );
		$tabs.tabs( "remove", index );
	});
	
	$( "#selectable" ).selectable({
		stop: function() {
			var result = $( "#select-result" ).empty();
			$( ".ui-selected", this ).each(function() {
				var index = $( "#selectable li" ).index( this );
				result.append( " #" + ( index + 1 ) );
			});
		}
	});
	$('#selectable2').selectable({
		stop: function() {
			var result = $( "#select-result" ).empty();
			$( ".ui-selected", this ).each(function() {
				var index = $( "#selectable2 li" ).index( this );
				result.append( " #" + ( index + 1 ) );
			});
		}
	});
	var $menuButtons2 = $("#selectable2").find("button").button();
	$menuButtons2.bind('click', function(){
		var tab_title = $(this).find('span').html();
		if ($tabs.find('a[href=#tabs-'+tab_title+']').html() == null) {
			addTab(tab_title);
		} else {
			$tabs.tabs("select", "#tabs-" + tab_title);
		}
	});
	var $menuButtons = $("#selectable").find("button").button();
	$menuButtons.bind('click', function(){
		var tab_title = $(this).find('span').html();
		if ($tabs.find('a[href=#tabs-'+tab_title+']').html() == null) {
			addTab(tab_title);
		} else {
			$tabs.tabs("select", "#tabs-" + tab_title);
		}
	});
	
	function addTab(title) {
		var url = ctx + '/jquery/lib/jquery-ui-1.8.17.custom/index.html';
		$tabs.tabs( "add", url, title );
	}
	
	function checkOpened(menus, menuName) {
		var index = -1;
		$.each(menus, function(i, menu){
			index = menu[manuName];
			if (index != -1) {
				return index;
			}
		});
		return index;
	}
});
