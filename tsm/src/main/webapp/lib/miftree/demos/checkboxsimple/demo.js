window.addEvent('domready', function() {
	var tree = new Mif.Tree( {
		container : $('tree_container'),
		forest : true,
		initialize : function() {
			this.initCheckbox('simple');
			new Mif.Tree.KeyNav(this);
		},
		types : {
			folder : {
				openIcon : 'mif-tree-open-icon',
				closeIcon : 'mif-tree-close-icon'
			},
			file : {
				openIcon : 'mif-tree-file-open-icon',
				closeIcon : 'mif-tree-file-close-icon'
			}
		},
		dfltType : 'folder',
		height : 18,
		
		onClickCheck : function(node) {
			checkChildren(node.getChildren(), 'checked');
			checkParent(node, 'checked');
		},
		onClickUnCheck : function(node) {
			checkChildren(node.getChildren(), 'unchecked');
			checkParent(node, 'unchecked');
		}
	});
	
	function checkChildren(childrenNodes, checked) {
		if ($chk(childrenNodes) && childrenNodes.length > 0) {
			$each(childrenNodes, function(childNode, i){
				childNode.switch(checked);
				checkChildren(childNode.getChildren(), checked);
			});
		}
	}
	
	function checkParent(node, checked) {
		var parentNode = node.getParent();
		if ($chk(parentNode)) {
			var brothers = parentNode.getChildren();
			var brotherIsCheck = false;
			$each(brothers, function(brother, i){
				if (brother.state.checked == 'checked' && brother != node) {
					brotherIsCheck = true;
				}
			});
			if (!brotherIsCheck) {
				parentNode.switch(checked);
				checkParent(parentNode, checked);
			}
		}
	}

	// tree.initSortable();
	var json = [];
	new Request.JSON( {
		url : ctx + '/html/menu/?m=getMenuTree&authId=107',
		onComplete : function(data){
			json.push(data.message);
			tree.load( {
				json : json
			});
			tree2.load( {
				json : json
			});
		}
	}).get();
});