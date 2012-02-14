<%@ page pageEncoding="utf-8"%>
<%@ include file="/jquery/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/jquery/common/meta.jsp" %>
<link rel="shortcut icon" href="${ctx}/jquery/images/favicon.ico" />
<link href="${ctx}/jquery/lib/jquery.layout.all-1.2.0/jquery.layout.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/jquery/lib/jquery.jqGrid-4.2.0/css/ui.jqgrid.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/jquery/lib/jquery.layout.all-1.2.0/jquery.layout.min.js" type="text/javascript"></script>
<script src="${ctx}/jquery/lib/accordion.js" type="text/javascript"></script>
<script src="${ctx}/jquery/lib/jquery.jqGrid-4.2.0/js/i18n/grid.locale-cn.js" type="text/javascript"></script>
<script src="${ctx}/jquery/lib/jquery.jqGrid-4.2.0/js/jquery.jqGrid.src.js" type="text/javascript"></script>
<script src="${ctx}/jquery/admin/index.js" type="text/javascript"></script>
<script type="text/javascript">
	var ctx = '${ctx}';
</script>
<style>
	#tabs li .ui-icon-close { float: left; margin: 5px 2px 0 0; cursor: pointer; }
	#add_tab { cursor: pointer; }
</style>
</head>

<body>
<div class="ui-layout-west">
<div id="leftMenu">
	<h3><a href="#section1">安全管理</a></h3>
	<div>
		<ul id="selectable">
			<li><button>用户管理</button></li>
			<li><button>权限管理</button></li>
			<li><button>资源管理</button></li>
			<li><button>菜单管理</button></li>
		</ul>
	</div>
	<h3><a href="#section2">业务管理</a></h3>
	<div>
		<ul id="selectable">
			<li><button>提供商管理</button></li>
			<li><button>应用管理</button></li>
			<li><button>安全域管理</button></li>
		</ul>
	</div>
</div>
</div>
<div class="ui-layout-center">
<div id="tabs" class="tabs-bottom" style="height: 98%;width: 98.5%;">
	<ul>
		<li><a href="#tabs-main">欢迎页面</a></li>
	</ul>
	<div id="tabs-main" style="width: 100%; margin-left: -17px;margin-top: -10px;">
		<table id="list2"></table>
		<div id="pager2"></div>
	</div>
</div>
</div>
<div class="ui-layout-north">标题栏</div>
</body>
</html>