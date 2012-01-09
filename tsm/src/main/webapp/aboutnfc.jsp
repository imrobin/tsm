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
</script>
</head>

<body>
<div id="container">
<%@ include file="/common/header.jsp" %>
<div id="main">
<div class="div980 line_c">
<div class="title980"><img src="${ctx}/images/mobile_icon.png" width="32" height="32" />关于NFC</div>
<div class="usercont">
<div class="aboutnfc">
<h3>概述</h3>
<img class="left m_r_20" src="${ctx}/images/photo1.gif" width="303" height="227" />
<p>NFC 将非接触读卡器、非接触卡和点对点（Peer-to-Peer）功能整合进一块单芯片，为消费者的生活方式开创了不计其数的全新机遇。这是一个开放接口平台，可以对无线网络进行快速、主动设置，也是虚拟连接器，服务于现有蜂窝状网络、蓝牙和无线 802.11 设备。</p>
<p>NFC可兼容索尼公司的FeliCaTM卡以及已广泛建立的非接触式智能卡架构，该架构基于ISO 14443 A，使用飞利浦的MIFARE?技术。</p>
<p>为了推动 NFC 的发展和普及，飞利浦、索尼和诺基亚创建了一个非赢利性的行业协会——NFC 论坛，促进 NFC 技术的实施和标准化，确保设备和服务之间协同合作。目前，NFC 论坛在全球拥有 70 多个成员，包括：万事达卡国际组织、松下电子工业有限公司、微软公司、摩托罗拉公司、NEC 公司、瑞萨科技公司、三星公司、德州仪器制造公司和 Visa 国际组织。</p>
<p>编者注：NFC全球最早的商用发布： * 德国，美因茨交通公司（RMV） 2006年4月19日，飞利浦、诺基亚、Vodafone公司及德国法兰克福美因茨地区的公交网络运营商美因茨交通公司（Rhein-Main Verkehrsverbund）宣布，在成功地进行为期10个月的现场试验后，近距离无线通信（NFC）技术即将投入商用。目前，Nokia 3220手机已集成了NFC技术，可以用作电子车票，还可在当地零售店和旅游景点作为折扣忠诚卡使用。哈瑙市的大约95.000位居民现在只需轻松地刷一下兼容手机，就能享受NFC式公交移动售票带来的便利。</p>
<h3>技术优势</h3>
<img class="right m_l_20" src="${ctx}/images/photo2.gif"><p>与RFID一样，NFC信息也是通过频谱中无线频率部分的电磁感应耦合方式传递，但两者之间还是存在很大的区别。首先，NFC是一种提供轻松、安全、迅速的通信的无线连接技术，其传输范围比RFID小，RFID的传输范围可以达到几米、甚至几十米，但由于NFC采取了独特的信号衰减技术，相对于RFID来说NFC具有距离近、带宽高、能耗低等特点。 其次，NFC与现有非接触智能卡技术兼容，目前已经成为得到越来越多主要厂商支持的正式标准。再次，NFC还是一种近距离连接协议，提供各种设备间轻松、安全、迅速而自动的通信。与无线世界中的其他连接方式相比，NFC是一种近距离的私密通信方式。最后，RFID更多的被应用在生产、物流、跟踪、资产管理上，而NFC则在门禁、公交、手机支付等领域内发挥着巨大的作用。 </p>
<p>同时，NFC还优于红外和蓝牙传输方式。作为一种面向消费者的交易机制，NFC比红外更快、更可靠而且简单得多，不用向红外那样必须严格的对齐才能传输数据。与蓝牙相比，NFC面向近距离交易，适用于交换财务信息或敏感的个人信息等重要数据；蓝牙能够弥补NFC通信距离不足的缺点，适用于较长距离数据通信。因此，NFC和蓝牙互为补充，共同存在。事实上，快捷轻型的NFC协议可以用于引导两台设备之间的蓝牙配对过程，促进了蓝牙的使用。 </p>
<p>NFC手机内置NFC芯片，组成RFID模块的一部分，可以当作RFID无源标签使用———用来支付费用；也可以当作RFID读写器———用作数据交换与采集。NFC技术支持多种应用，包括移动支付与交易、对等式通信及移动中信息访问等。通过NFC手机，人们可以在任何地点、任何时间，通过任何设备，与他们希望得到的娱乐服务与交易联系在一起，从而完成付款，获取海报信息等。NFC设备可以用作非接触式智能卡、智能卡的读写器终端以及设备对设备的数据传输链路，其应用主要可分为以下四个基本类型：用于付款和购票、用于电子票证、用于智能媒体以及用于交换、传输数据。</p>
</div>

</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
</div>
</div>
</body>
</html>