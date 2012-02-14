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
			$(ui.panel).load(ctx + "/admin/security/jsp/user.jsp");
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
		$tabs.tabs( "add", "#tabs-" + title, title );
		var tabs = $tabs.tabs( "widget" );
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