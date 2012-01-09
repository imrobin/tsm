<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<title>多应用管理平台</title>

<%@ include file="script.jsp" %>

<script type="text/javascript" src="${ctx}/admin/aid/js/aid.js"></script>

<script type="text/javascript">
	var ctx = '${ctx}';
	window.addEvent('domready', function() {
		//TODO aid
		new ApplicationIdentifier.list();
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

					<div id="tableDiv" class="rightcont" style="height: 450px;">
						<!-- grid -->
					</div>

				</div>
			</div>
		</div>
		<div id="footer" class="clear">
			<p class="right">Copyright©2011 Just In Mobile Corporation. All rights reserved</p>
		</div>
	</div>

	<!-- add sd-->
	<div id="addSd" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>

					<dd>
						<p class="regtextsd" id="spSelectText"><span style="color: red;">*</span>应用提供商：</p>
						<p class="left inputs">
							<select id="spSelect" name="sp_id">
								<option value="">----- 请选择 -----</option>
							</select>
						</p>
						<p class="explain left" id="spSelectExplain"></p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>归属：
						</p>
						<p class="left inputs">
							<select name="belongto">
								<option value="00">集团</option>
								<option value="01">北京</option>
								<option value="02">天津</option>
								<option value="03">河北</option>
								<option value="04">山西</option>
								<option value="05">内蒙古</option>
								<option value="06">辽宁</option>
								<option value="07">吉林</option>
								<option value="08">黑龙江</option>
								<option value="09">上海</option>
								<option value="10">江苏</option>
								<option value="11">浙江</option>
								<option value="12">安徽</option>
								<option value="13">福建</option>
								<option value="14">江西</option>
								<option value="15">山东</option>
								<option value="16">河南</option>
								<option value="17">湖北</option>
								<option value="18">湖南</option>
								<option value="19">广东</option>
								<option value="20">广西</option>
								<option value="21">海南</option>
								<option value="22">四川</option>
								<option value="23">贵州</option>
								<option value="24">云南</option>
								<option value="25">西藏</option>
								<option value="26">陕西</option>
								<option value="27">甘肃</option>
								<option value="28">青海</option>
								<option value="29">宁夏</option>
								<option value="30">新疆</option>
								<option value="31">重庆</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd"><span style="color: red;">*</span>分配数量：</p>
						<p class="left inputs">
							<input class="inputs validate['required','digit[1,100]']" maxlength="3" name="size"/>
						</p>
						<p class="explain left"></p>
					</dd>
				</dl>
			</form>
		</div>
	</div>
	<!-- add app -->
	<div id="addApp" style="display: none;">
		<div class="regcont" style="overflow-x: hidden;">
			<form method="post">
				<input name="id" type="hidden" />
				<button class="validate['submit']" style="display: none;"></button>
				<dl>

					<dd>
						<p class="regtextsd" id="spSelectText"><span style="color: red;">*</span>应用提供商：</p>
						<p class="left inputs">
							<select id="spSelect" name="sp_id">
								<option value="">----- 请选择 -----</option>
							</select>
						</p>
						<p class="explain left" id="spSelectExplain"></p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>归属：
						</p>
						<p class="left inputs">
							<select name="appType">
								<option value="00">全网</option>
								<option value="01">本地</option>
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd">
							<span style="color: red;">*</span>行业：
						</p>
						<p class="left inputs">
							<select name="industry">
								<option value="00">标准产品</option>
							 	<option value="69">其他</option>
							 	<option value="01">农业</option>
							 	<option value="02">林业</option>
							 	<option value="03">畜牧业</option>
							 	<option value="04">渔业</option>
							 	<option value="05">采矿</option>
							 	<option value="06">煤炭</option>
							 	<option value="07">石油</option>
							 	<option value="08">制造</option>
							 	<option value="09">烟草</option>
							 	<option value="10">化工</option>
							 	<option value="11">电燃水</option>
							 	<option value="12">电力</option>
							 	<option value="13">建筑</option>
							 	<option value="14">交通</option>
							 	<option value="15">铁路运输</option>
							 	<option value="16">道路运输</option>
							 	<option value="17">公交</option>
							 	<option value="18">水上运输</option>
							 	<option value="19">航空</option>
							 	<option value="20">邮政</option>
							 	<option value="21">IT</option>
							 	<option value="22">电信</option>
							 	<option value="23">批发</option>
							 	<option value="24">零售</option>
							 	<option value="25">住宿</option>
							 	<option value="26">餐饮</option>
							 	<option value="27">银行</option>
							 	<option value="28">证券</option>
							 	<option value="29">保险</option>
							 	<option value="30">房地产</option>
							 	<option value="31">租赁</option>
							 	<option value="32">商业</option>
							 	<option value="33">旅游</option>
							 	<option value="34">科研</option>
							 	<option value="35">水利</option>
							 	<option value="36">环保</option>
							 	<option value="37">市政</option>
							 	<option value="38">居民服务</option>
							 	<option value="39">教育</option>
							 	<option value="40">卫生</option>
							 	<option value="41">社保</option>
							 	<option value="42">新闻</option>
							 	<option value="43">广电</option>
							 	<option value="44">文艺</option>
							 	<option value="45">体育</option>
							 	<option value="46">娱乐</option>
							 	<option value="47">政府</option>
							 	<option value="48">共产党机关及人民政府</option>
							 	<option value="49">公安</option>
							 	<option value="50">监察</option>
							 	<option value="51">民政</option>
							 	<option value="52">司法</option>
							 	<option value="53">财政</option>
							 	<option value="54">人事</option>
							 	<option value="55">国土</option>
							 	<option value="56">海关</option>
							 	<option value="57">税务</option>
							 	<option value="58">质检</option>
							 	<option value="59">工商</option>
							 	<option value="60">统计</option>
							 	<option value="61">气象</option>
							 	<option value="62">地震</option>
							 	<option value="63">海洋</option>
							 	<option value="64">审计</option>
							 	<option value="65">烟草专卖</option>
							 	<option value="66">法院</option>
							 	<option value="67">检察院</option>
							 	<option value="68">国际组织</option>
								
							</select>
						</p>
					</dd>
					<dd>
						<p class="regtextsd"><span style="color: red;">*</span>分配数量：</p>
						<p class="left inputs">
							<input class="inputs validate['required','digit[1,100]']" maxlength="3" name="size"/>
						</p>
						<p class="explain left"></p>
					</dd>
				</dl>
			</form>
		</div>
	</div>
</body>
</html>
