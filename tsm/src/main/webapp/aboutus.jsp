<%@ page pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>多应用管理平台</title>
<%@ include file="/common/meta.jsp" %>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<script src="${ctx}/lib/ie6png.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/lib/mootools/mootools-core-1.3.2.js"></script>
<script type="text/javascript">
   EvPNG.fix('div, ul, img, li, a, input, p, strong, span, button');  //EvPNG.fix('包含透明PNG图片的标签'); 多个标签之间用英文逗号隔开。
   window.addEvent('domready', function(){
		$('aboutUsLink').addEvent('click', function(){
			$('aboutUsDiv').fade('in');
			$('indemnificationDiv').fade('out');
			$('termsOfServiceDiv').fade('out');
			$('aboutUsDiv').setStyle('display', '');
			$('indemnificationDiv').setStyle('display', 'none');
			$('termsOfServiceDiv').setStyle('display', 'none');
		});
		$('indemnificationLink').addEvent('click', function(){
			$('aboutUsDiv').fade('out');
			$('indemnificationDiv').fade('in');
			$('termsOfServiceDiv').fade('out');
			$('indemnificationDiv').setStyle('display', '');
			$('aboutUsDiv').setStyle('display', 'none');
			$('termsOfServiceDiv').setStyle('display', 'none');
		});
		$('termsOfServiceLink').addEvent('click', function(){
			$('aboutUsDiv').fade('out');
			$('indemnificationDiv').fade('out');
			$('termsOfServiceDiv').fade('in');
			$('termsOfServiceDiv').setStyle('display', '');
			$('aboutUsDiv').setStyle('display', 'none');
			$('indemnificationDiv').setStyle('display', 'none');
		});
		var param = getQueryValue('v');
	    if($chk(param)){
			$(param).fireEvent('click');
		}
	});
   function getQueryValue(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return "";
	}
</script>
</head>
<body>
<div id="container">
<%@ include file="/common/header.jsp" %>
<div class="curPosition">您的位置: 首页&gt;关于</div>
<div class="ad_980"><img src="${ctx}/images/ad_980.gif" /></div>
<div id="main">
<div class="div980 line_c">
<div class="title9801"></div>
<div class="aboutcon">
<div class="aboutmenu"><ul>
<li><a id="aboutUsLink" href="#">关于我们</a></li>
<li><a id="indemnificationLink" href="#">免责申明</a></li>
<li><a id="termsOfServiceLink" href="#">服务条款</a></li>
</ul></div>
<div id="aboutUsDiv" class="aboutc">
<div class="abtitle"><img src="${ctx}/images/help_ul.png" />关于我们 </div>
<p>NFC 将非接触读卡器、非接触卡和点对点（Peer-to-Peer）功能整合进一块单芯片，为消费者的生活方式开创了不计其数的全新机遇。这是一个开放接口平台，可以对无线网络进行快速、主动设置，也是虚拟连接器，服务于现有蜂窝状网络、蓝牙和无线 802.11 设备。</p>

<p>NFC可兼容索尼公司的FeliCaTM卡以及已广泛建立的非接触式智能卡架构，该架构基于ISO 14443 A，使用飞利浦的MIFARE?技术。</p>

<p>为了推动 NFC 的发展和普及，飞利浦、索尼和诺基亚创建了一个非赢利性的行业协会——NFC 论坛，促进 NFC 技术的实施和标准化，确保设备和服务之间协同合作。目前，NFC 论坛在全球拥有 70 多个成员，包括：万事达卡国际组织、松下电子工业有限公司、微软公司、摩托罗拉公司、NEC 公司、瑞萨科技公司、三星公司、德州仪器制造公司和 Visa 国际组织。</p>

<p>编者注：NFC全球最早的商用发布： * 德国，美因茨交通公司（RMV） 2006年4月19日，飞利浦、诺基亚、Vodafone公司及德国法兰克福美因茨地区的公交网络运营商美因茨交通公司（Rhein-Main Verkehrsverbund）宣布，在成功地进行为期10个月的现场试验后，近距离无线通信（NFC）技术即将投入商用。目前，Nokia 3220手机已集成了NFC技术，可以用作电子车票，还可在当地零售店和旅游景点作为折扣忠诚卡使用。哈瑙市的大约95.000位居民现在只需轻松地刷一下兼容手机，就能享受NFC式公交移动售票带来的便利。</p>
</div>
<div id="indemnificationDiv" class="aboutc">
<div class="abtitle"><img src="${ctx}/images/help_ul.png" />免责条件 </div>
<p>即法律明文规定的当事人对其不履行合同不承担违约责任的条件。我国法律规定的免责条件主要有：</p>
<p>1.不可抗力：《合同法》第117条规定，因不可抗力不能履行合同的，根据不可抗力的影响，部分或者全部免除责任，但法律另有规定的除外。当事人迟延履行后发生不可抗力的，不能免除责任。本法所称不可抗力，是指不能预见、不能避免并不能克服的客观情况。</p>
<p>2.货物本身的自然性质、货物的合理损耗：《合同法》第311条规定：承运人对运输过程中货物的毁损、灭失承担损害赔偿责任，但承运人证明货物的毁损、灭失是因不可抗力、货物本身的自然性质或者合理损耗以及托运人、收货人的过错造成的，不承担损害赔偿责任。</p>
<p>3.债权人的过错：《合同法》第311条规定、第370条规定：寄存人交付的保管物有瑕疵或者按照保管物的性质需要采取特殊保管措施的，寄存人应当将有关情况告知保管人。寄存人未告知，致使保管物受损失的，保管人不承担损害赔偿责任；保管人因此受损失的，除保管人知道或者应当知道并且未采取补救措施的以外，寄存人应当承担损害赔偿责任。</p>
<div class="abtitle"><img src="${ctx}/images/help_ul.png" />免责条款 </div>
<p>1.免责条款的概念：免责条款，就是当事人以协议排除或限制其未来责任的合同条款。其一，免责条款是合同的组成部分，是一种合同条款；其二，免责条款的提出必须是明示的，不允许以默示方式作出，也不允许法官推定免责条款的存在。</p>
<p>2.免责条款的有效与无效。</p>
<p>　　（1）基于现行法的规定确定免责条款的有效或者无效。免责条款以意思表示为要素，以排除或限制当事人的未来责任为目的，<br />
　　		因而属于一种民事行为，应受《合同法》第52条、第53条、第54条、第47条、第48条、第51条和第40条的规定调整 。</p>
<p>　　（2）基于风险分配理论确定免责条款的有效或者无效。</p>
<p>　　（3）根据过错程度确定免责条款的有效或者无效，《合同法》第40条、第53条。</p>
<p>　　（4）根据违约的轻重确定免责条款的有效或者无效 ，我国没有采用。</p>
</div>
<div id="termsOfServiceDiv" class="aboutc">
<div class="abtitle">一、服务简介 </div>
<p>“多应用平台”是中国移动通信 (以下简称“中国移动”) 推出的手机应用网上商店，服务提供商（AP）通过网上商店，以直观生动的内容展现、方便快捷的购买流程，为您提供质优价廉的手机应用服务。</p>
<p>您可以通过登陆中国移动通信网站www.10086.cn，点击“多应用平台”按钮进入“多应用平台”，浏览或订购AP 通过“多应用平台”为您提供的各项服务。</p>
<div class="abtitle">二、计费与缴费 </div>
<p>AP 通过“多应用平台”为您提供有偿服务或免费服务。有偿服务的具体价格及收费方式将标明在相应的页面上，您可以根据需要选择。您选择并使用某项服务，即视为您接受所选服务的费用标准，并同意支付。 您同意上述服务的使用费用由中国移动代 AP 收取。您可以登陆“多应用平台”网站查询相关服务费用。</p>
<div class="abtitle">三、服务条款的确认和修改 </div>
<p>AP 通过“多应用平台”提供的服务将完全按照其发布的章程、服务条款和操作规则严格执行。您在使用“多应用平台”提供的服务前，请认真阅读所有服务条款。当您使用“多应用平台”中提供的服务时，视为您已完全同意所有服务条款。
中国移动有权在必要时修改本服务条款，服务条款一旦发生变动，将会在重要页面上提示修改内容。如果您不同意所改动的内容，您可以主动取消所定制的服务。</p>
<div class="abtitle">四、服务内容的变更、中断和终止 </div>
<p>由于某些原因， AP 在提前告知您后有可能暂时或永久地终止其某些或全部服务，由此产生的任何问题由您与相关 AP 协商解决，中国移动对此不承担任何责任。</p>
<p>您可根据实际情况随时停止使用 AP 通过“多应用平台”提供的一项或多项服务。您可以取消已经定制的服务，若您取消用话费支付的服务，则您应当在一个服务计费周期内按照其在定制服务时相应页面上明示的计费及收费方式就其已经定制的服务向 AP 支付相应的服务费用。
中国移动有权判定您的行为是否符合“多应用平台”服务条款的要求，如果您违背了服务条款的规定，中国移动有权要求 AP 中断和终止对您提供服务。</p>
<div class="abtitle">五、用户隐私保护 </div>
<p>尊重用户个人隐私是“多应用平台”的一项基本政策。“多应用平台”不会在未经您授权时公开、编辑或透露您的个人资料和“多应用平台”为您保存的非公开内容，除非有法律、法规或政府有关部门要求，以及中国移动本着诚实信用原则认为披露这些资料是必要的以下情况：</p>
<p>(1) 为了维护社会公共利益的需要；</p>
<p>(2) 为了提供您所定制服务的需要；</p>
<p>(3) 为了解决 AP 与您之间关系的需要。</p>
<div class="abtitle">六、用户责任 </div>
<p>您在使用 AP 通过“多应用平台”提供的服务时必须保证：</p>
<p>(1) 遵守中国有关法律法规的规定。</p>
<p>(2) 不利用服务作非法用途。</p>
<p>(3) 不干扰服务的正常进行。</p>
<p>(4) 遵守所有与使用服务有关的网络协议、规定、程序和惯例。</p>
<p>(5) 不得以任何形式侵犯“多应用平台”的知识产权。</p>

<p>您需对您在“多应用平台”的一切行为承担法律责任。您若在“多应用平台”上散布和传播反动、色情或其他违反国家法律、法规的信息、资料，“多应用平台”的系统记录有可能作为您违反法律、法规的证据，并向有关执法部门提供。</p>
<p>您承诺不传输、传播任何非法、骚扰性、侮辱、毁谤他人、恐吓性、庸俗和淫秽的信息、资料。您也不得传输、传播任何教唆他人实施犯罪行为的信息、资料；不得传输、传播助长国内不利条件和涉及国家安全的信息、资料；不得传输、传播任何法律、法规禁止传播的其他信息、资料。</p>
<div class="abtitle">七、责任限制 </div>
<p>中国移动的“多应用平台”网站只是为您与 AP 的网络交易、网络行为和网络服务提供平台和通道。 您以 AP 发布的信息为依据而进行任何交易或者其他行为，均视为已经充分知悉并自愿承担网络交易、网上行为的风险，并应对网上交易或行为进行事前辨别和采取谨慎的预防措施，并承担因进行网上交易或行为所产生的一切法律后果。</p>
<p>您与 AP 因通过“多应用平台”进行交易或购买服务产生的任何争议或纠纷，是您与提供争议服务 AP 之间的争议或纠纷，与“多应用平台”及中国移动无关。中国移动对您与 AP 发生的交易和行为不承担任何直接、间接、附带或衍生的损失和责任。</p>
<p>对于任何第三方的行为造成您的任何损害或损失，中国移动不承担责任不论这些损害或损失来自不正当使用服务、在网上购买商品或进行同类型服务、在网上进行交易、非法使用服务或您传送的信息有所变动，或其他非中国移动原因造成的营业中断。</p>
<p>如确因中国移动的过错造成您的损害或损失，经查实，中国移动将给予您不超过中国移动代 AP 向您收取该服务项目下该次服务服务费两倍的赔偿，赔偿以手机话费形式直接存入您的手机账户。</p>
<div class="abtitle">八、担保或保证 </div>
<p>中国移动对 AP 通过“多应用平台”发布各类信息的真实性、合法性、准确性及有效性不作任何保证，亦不承担任何责任。</p>
<p>中国移动不保证您下载的任何 AP 通过“多应用平台”提供软件的合法性、准确性、安全性和完整性，也不承担您因使用这些下载软件而造成的任何形式的损失或损害。</p>
<p>您对使用 AP 通过“多应用平台”所提供的服务承担风险和责任，中国移动对此不作任何明示或暗示的担保或保证。中国移动不担保 AP 通过“多应用平台”提供的服务一定能满足您的要求，不担保 AP 不会中断和终止服务。由于您所在位置、关机等非中国移动的原因而导致您不能接收信息，中国移动将不负任何法律责任。</p>
<div class="abtitle">九、通知 </div>
<p>“多应用平台”所有发给您的通知，都可通过重要页面的公告或电子邮件、短消息或常规的信件传送。</p>
<div class="abtitle">十、法律适用 </div>
<p>本服务条款的效力和解释均适用中华人民共和国法律，您和中国移动一致同意服从中国法院的管辖。如“多应用平台”服务条款与中华人民共和国法律、法规相抵触，则这些条款将完全按法律、法规规定解释和执行，而其它条款则依旧保持对您产生法律效力和影响。</p>
</div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
</div>
</body>
</html>