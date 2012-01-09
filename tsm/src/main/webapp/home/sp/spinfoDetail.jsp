<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>应用提供商首页</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/lib/formcheck/theme/classic/formcheck.css" rel="stylesheet" type="text/css" media="screen"/>

<link href="${ctx}/lib/paging/paging.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/formcheck.js"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-more.js"></script>
<script type="text/javascript" src="${ctx}/lib/formcheck/lang/cn.js"></script>
<script src="${ctx }/lib/ie6png.js" type="text/javascript"></script>

<link href="${ctx}/lib/lightface/assets/LightFace.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/lightface/LightFace.js" type="text/javascript"></script>
<script src="${ctx}/lib/lightface/LightFace.MessageBox.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/paging/paging.js"></script>

<script type="text/javascript" src="${ctx}/home/sp/js/sp.js"></script>

<script type="text/javascript">
	EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
	
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		//is sp login
		new Request({
			async : false,
			url : '${ctx}/html/spBaseInfo/',
			onSuccess : function(responseText) {
				var object = JSON.decode(responseText);
				if(!object.success) {
					self.location = ctx + '/index.jsp';
				}
			}
		}).post('m=getCurrentSp');
		
		//load data...
		new Request.JSON({
			async : false,
			url : ctx + '/html/spBaseInfo/',
			onSuccess : function(result) {
				var sp = result.message;
				
				if(sp.hasLogo == '有') {
					new Element('img', {
						style : 'height: 90px;width: 90px;',
						src : ctx + '/html/spBaseInfo/?m=loadSpFirmLogo&id=' + sp.id
					}).inject($('firmLogo'));
				} else {
					new Element('input', {
						'class' : 'inputtext', value : '无', type : 'text', readonly : 'readonly'
					}).inject($('firmLogo'));
				}
				
				var legalPersonIdType = $$('input[name=legalPersonIdType]');
				$each(legalPersonIdType, function(e , index) {
					if(sp.legalPersonIdType == e.get('value')) {
						e.set('checked','checked');
						e.set('disabled','disabled');
					}
				});
				
				//遍历json对象
				/**/
				for(var attr in sp) {
					if(typeof(sp[attr]) == 'function') {
						sp[attr]();
					} else if(typeof(sp[attr]) == 'object' && sp[attr] != null && sp[attr] != '') {
						//nothing
					} else {
						var value = sp[attr];
						//处理外键字段
						if(attr.indexOf('_') != -1) {
							attr = attr.substring(attr.indexOf('_')+1);
						}
						var e = $(attr);
						if(attr == 'locationNo') e = $('location');
						if(e) {
							if(e.get('type') == 'radio') {
								
								e = $$('input[name='+attr+']');
								for(var i = 0; i < e.length; i++) {
									if(value == e[i].get('value')) {
										e[i].set('checked','checked');
										break;
									}
								}
							} else {
								e.set('value', value);
							}
						}
					}
				}
				
				$each($$('select'), function(e, index) {
					e.set('disabled','disabled');
				});
			}
		}).post('m=spLoad&spid=-1');
		
	});
	
</script>
</head>

<body>
<div id="container">

<%@ include file="/common/header.jsp"%>

<div class="curPosition">您的位置: 首页&gt;我的主页&gt;注册信息</div>

<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx }/images/user_icon_32.png" width="32" height="32" />应用提供商</div>
<div class="usercont">
<div class="usermenu" id="usermenu">
<%@ include file="/home/sp/menu.jsp"%>
</div>

<div class="userinput">
	<p id="userinput_t" class="userinput_t">注册信息</p>
	<div id="userinput">
	<!-- content block TODO -->
		<div>
		<dl>
			<!-- 
			<dd>
				<p class="regtext">
					企业编号:
				</p>
				<p class="left inputs">
					<input class="inputtext" type="text" readonly="readonly" id="no" name="no"/>
				</p>
			</dd>
		
			 -->
			<dd>
				<p class="regtext">企业编号:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" id="no" name="no" type="text"/>
				</p>
			</dd>
			<dd>
				<p class="regtext">RID:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" id="rid" name="rid" type="text"/>
				</p>
			</dd>
			<dd>
				<p class="regtext">企业邮件地址:</p>
				<p class="left inputs">
					<input class="inputtext" id="email" name="email" type="text" readonly="readonly"/>
				</p>
			</dd>
			<dd id="dd_05">
				<p class="regtext">企业名称:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" maxlength="50" id="name" name="name" type="text" />
				</p>
				<p class="explain left">
				</p>
			</dd>
			<dd id="dd_06">
				<p class="regtext">企业简称:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" maxlength="25" id="shortName" name="shortName" type="text" />
				</p>
				<p class="explain left">
				</p>
			</dd>
		
			<dd id="dd_07">
				<p class="regtext">工商注册编号:</p>
				<p class="left inputs">
					<input class="inputtext"readonly="readonly" maxlength="32" id="registrationNo" name="registrationNo" type="text" />
					
				</p>
				<p class="explain left">
				</p>
			</dd>
			<dd id="dd_08">
				<p class="regtext">经营许可证编号:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" maxlength="32" id="certificateNo" name="certificateNo" type="text" />
				</p>
				<p class="explain left">
				</p>
			</dd>
			<dd id="dd_09">
				<p class="regtext">
					所在地:
				</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" name="locationNo" id="location" type="text" />
				</p>
			</dd>
			<dd>
				<p class="regtext">应用提供商类型:</p>
				<p class="left inputs">
					<select name="type" id="type">
						<option value="1">全网移动</option>
						<option value="2">本地移动</option>
						<option value="3">全网应用提供商</option>
						<option value="4">本地应用提供商</option>
					</select>
				</p>
			</dd>
			<dd id="dd_10">
				<p class="regtext">企业联系地址:</p>
				<p class="left inputs">
					<input class="inputtext " readonly="readonly" maxlength="120" id="address"
										name="address" type="text" />
				</p>
			</dd>
			<dd id="dd_11">
				<p class="regtext">企业法人姓名:</p>
				<p class="left inputs">
					<input class="inputtext"readonly="readonly" maxlength="20"
										id="legalPersonName" name="legalPersonName" type="text" />
				</p>
			</dd>
			<dd id="dd_12">
				<p class="regtext">法人证件类型:</p>
				<p class="left inputs">
					<input disabled="disabled" class="inputradio" name="legalPersonIdType" type="radio" value="身份证" />身份证 
					<input disabled="disabled" class="inputradio" name="legalPersonIdType" type="radio" value="护照" />护照
				</p>
			</dd>
			<dd id="dd_13">
				<p class="regtext">法人证件号码:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" maxlength="32"
										id="legalPersonIdNo" name="legalPersonIdNo" type="text" />
				</p>
				<p class="explain left">
				</p>
			</dd>
			<dd id="dd_14">
				<p class="regtext">企业性质:</p>
				<p class="left inputs">
					<select id="firmNature" name="firmNature">
						<option value="1">国有</option>
						<option value="2">合作</option>
						<option value="3">合资</option>
						<option value="4">独资</option>
						<option value="5">集体</option>
						<option value="6">私营</option>
						<option value="7">个体工商户</option>
						<option value="8">报关</option>
						<option value="9">其他</option>
					</select>
				</p>
			</dd>
			<dd id="dd_15">
				<p class="regtext">企业规模:</p>
				<p class="left inputs">
					<select id="firmScale" name="firmScale">
						<option value="1">小型(100人以下)</option>
						<option value="2">中型(100-500人)</option>
						<option value="3">大型(500人以上)</option>
					</select>
				</p>
			</dd>
			<!-- -->
			<dd id="dd_16">
				<p class="regtext">企业LOGO:</p>
				<p class="left inputs" id="firmLogo">
				</p>
			</dd>
			<dd id="dd_17">
				<p class="regtext">业务联系人姓名:</p>
				<p class="left inputs">
					<input class="inputtext" maxlength="20" readonly="readonly"
										id="contactPersonName" name="contactPersonName" type="text" />
				</p>
				
			</dd>
			<dd id="dd_18">
				<p class="regtext">业务联系人手机号:</p>
				<p class="left inputs">
					<input class="inputtext" readonly="readonly" maxlength="11"
							id="contactPersonMobileNo" name="contactPersonMobileNo"
							type="text" />
				</p>
				<p></p>
			</dd>
			<dd id="dd_18">
				<p class="regtext"></p>
				<p class="left inputs">
					<input class="subutton" style="cursor: pointer;" type="button" value="返回" onclick="history.back(-1);"/>
				</p>
				<p></p>
			</dd>
		</dl>
	</div>
	</div>
</div>

</div>

</div>
<%@ include file="/common/footer.jsp"%>
</div>
</div>
</body>

</html>