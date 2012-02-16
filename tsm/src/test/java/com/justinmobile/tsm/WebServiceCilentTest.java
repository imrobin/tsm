package com.justinmobile.tsm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.Service;



import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.LvObject;

import com.justinmobile.core.utils.webservice.ProxyServiceFactory;

import com.justinmobile.tsm.endpoint.sms.OuterWebService;
import com.justinmobile.tsm.endpoint.sms.SmsEndpoint;
import com.justinmobile.tsm.endpoint.sms.message.MessageFormat;
import com.justinmobile.tsm.endpoint.webservice.MobileWebService;
import com.justinmobile.tsm.endpoint.webservice.ProviderCallTsmWebService;
import com.justinmobile.tsm.endpoint.webservice.SmsWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppOperate;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.BasicResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.GetInformation;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoginOrRegisterRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqAppComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqGetApplicationInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqSdList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResLoginOrRegister;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResSdList;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;

public class WebServiceCilentTest  extends BaseAbstractTest{
	@Autowired
	private SmsEndpoint smsEndpoint;

	// @Test
	public void testCilent() {
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from "
					+ "http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		}

		Service.create(wsdlUrl, new QName("http://www.chinamobile.com", "ProviderWebService"));
	}

	// @Test
	public void testCilentServiceName() {
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from "
					+ "http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		}

		Service.create(wsdlUrl, new QName("http://www.chinamobile.com", "WebService"));
	}

	// @Test
	public void testCilentUrl() {
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8080/tsm/services/WebService?wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from "
					+ "http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		}

		Service.create(wsdlUrl, new QName("http://www.chinamobile.com", "ProviderWebService"));
	}

	// @Test
	public void testCallTsmWebService() {
		Holder<String> seqNum = new Holder<String>("000000000000");
		String sessionId = "11111111111";
		Holder<String> timeStamp = new Holder<String>("20111107160000");
		Integer commType = CommType.GPC.getType();
		String msisdn = "13501298756";
		String seId = "21312312312321";
		Integer eventId = 3;
		String appAid = "213123123123213";
		Holder<Status> status = new Holder<Status>(new Status());
		// ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(ProviderCallTsmWebService.class);
		factory.setAddress("http://192.168.10.60:8083/services/ProviderCallTsmWebService");
		ProviderCallTsmWebService client = (ProviderCallTsmWebService) factory.create();
		client.businessEventNotify(seqNum, sessionId, timeStamp, commType, msisdn, appAid, seId, eventId, status);
	}

	//@Test
	public void testSms() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(SmsWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/OuterWebService?wsdl");
		OuterWebService client = (OuterWebService) factory.create();
		boolean status = client.smsNotifyUser("13880668542", "测试短信");
		System.out.println(status);
	}

	 //@Test //升级应用客户端 pass
	public void testLoadClient100201() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoadClientRequest request = new LoadClientRequest();
		request.setAppAId("D1560001018003800000000100000000");
		request.setCardNo("12000004000000100005");
		request.setCommandID("100201");
		request.setCommonType("GPC-Android2.3-1.0.0");
		request.setMobileOs("Android");
		request.setOsVersion("2.3");
		request.setSessionID("1111002054123000001");
		request.setUpgradeType("2");
		LoadClientResponse response = client.loadClient(request);
		System.out.println(response.getStatus());
	}

	// @Test
	public void testLoadClient100202() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		// factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoadClientRequest request = new LoadClientRequest();
		// request.setAppAId("D1560001018003800000000100000000");
		request.setCardNo("12000004000000100006");
		request.setCommandID("100202");
		request.setCommonType("GPC-Android2.3-1.0.0");
		request.setMobileOs("Android");
		request.setOsVersion("2.3");
		request.setSessionID("1111002054123000001");
		request.setUpgradeType("2");
		LoadClientResponse response = client.loadClient(request);
		System.out.println(response);
	}

	// @Test //卡空间信息 pass
	public void testGetInfo100004() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		// factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqGetApplicationInfo request = new ReqGetApplicationInfo();
		request.setCardNo("12000004000000100006");
		request.setCommandID("100004");
		request.setCommonType("ME-Android2.3-1.0.0");
		request.setSessionID("1111002054123000001");
		GetInformation response = client.getInfo(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test //浏览评论 pass
	public void testGetInfo100302() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		// factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqGetApplicationInfo request = new ReqGetApplicationInfo();
		request.setAppAID("D056000101800000000100001013");
		request.setCardNo("12000004000000100009");
		request.setCommandID("100302");
		request.setCommonType("GPC-Android2.3-1.0.0");
		request.setSessionID("1111002054123000001");
		GetInformation response = client.getInfo(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test //应用详情 pass
	public void testGetInfo100007() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		//factory.setAddress("http://127.0.0.1:8080/services/MobileWebService?wsdl");
		 factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqGetApplicationInfo request = new ReqGetApplicationInfo();
		request.setAppAID("A000000333010102");
		request.setCardNo("12000004000000100012");
		request.setCommandID("100007");
		request.setCommonType("ME-Android2.3-1.0");
		request.setSessionID("1111002054123000001");
		GetInformation response = client.getInfo(request);
		 System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test //客户端信息详情，有问题未解决
	public void testGetInfo100203() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		// factory.setAddress("http://127.0.0.1:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqGetApplicationInfo request = new ReqGetApplicationInfo();
		request.setAppAID("D056000101800000000100001012");
		request.setCardNo("12000004000000100006");
		request.setCommandID("100203");
		request.setCommonType("GPC-Android2.3-1.0.0");
		request.setSessionID("1111002054123000001");
		GetInformation response = client.getInfo(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

   // @Test //给应用提交评论pass
	public void testPostComment100301() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqAppComment request = new ReqAppComment();
		request.setCommandID("100401");
		AppComment comment = new AppComment();
		comment.setCommentId(23L);
		comment.setUp(1);
	    request.setComment(comment);
		request.setSessionID("1111002054123000001");
		BasicResponse response = client.postAppComment(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	@Test //获取应用列表,pass
	public void testApplicationList100005() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqApplicationList request = new ReqApplicationList();
		request.setCardNo("12000004000000100006");
		request.setCommandID("100005");
		request.setPageNumber(1);
		request.setIsDownloaded(2);
		request.setQueryCondition("like=卡");
		request.setCommonType("ME-Android2.3-1.0");
		request.setSessionID("1111002054123000001");
		client.listApplication(request);
	}

	// @Test //获取安全域列表,pass
	public void testSDList100006() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqSdList request = new ReqSdList();
		request.setCardNo("12000004000000100006");
		request.setCommandID("100006");
		request.setIsInstall(false);
		request.setCommonType("");
		request.setSessionID("1111002054123000001");
		ResSdList response = client.listSd(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	//@Test
	public void testRegAndLogin() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		//factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoginOrRegisterRequest request = new LoginOrRegisterRequest();
		request.setCardNo("12000004000000100006");
		request.setImsi("898650000000000000");
		request.setImei("310260000000000");
		request.setCommandID("100001");
		request.setChallengeNo("111111");
		request.setCommonType("ME-Android2.3-1.0");
		request.setSessionID("20111127140252000002");
	    client.loginOrRegiseter(request);
	}

	// @Test
	public void testRegAndLogin100002() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		// factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoginOrRegisterRequest request = new LoginOrRegisterRequest();
		request.setCardNo("12000004000000100008");
		request.setImsi("898650000000000000");
		request.setImei("310260000000000");
		request.setCommandID("100002");
		request.setChallengeNo("111111");
		request.setCommonType("ME-Android-1.0");
		request.setSessionID("20111127140252000002");
		ResLoginOrRegister response = client.loginOrRegiseter(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testRegAndLogin100101() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		// factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoginOrRegisterRequest request = new LoginOrRegisterRequest();
		request.setCardNo("12000004000000100008");
		request.setImsi("898650000000000000");
		request.setImei("310260000000000");
		request.setCommandID("100003");
		request.setChallengeNo("111111");
		request.setCommonType("ME-Android-1.0");
		request.setSessionID("20111127140252000002");
		ResLoginOrRegister response = client.loginOrRegiseter(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testRegAndLogin100008() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		// factory.setAddress("http://localhost:8080/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoginOrRegisterRequest request = new LoginOrRegisterRequest();
		request.setCardNo("12000004000000100006");
		request.setImsi("898650000000000000");
		request.setImei("310260000000000");
		request.setCommandID("100008");
		request.setCommonType("ME-Android-1.0");
		ResLoginOrRegister response = client.loginOrRegiseter(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testAPDU1001011() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqExecAPDU request = new ReqExecAPDU();
		request.setCardNo("08D561000000010007");
		request.setTimeStamp("1111002054123000001");
		request.setCommandID("100101");
		AppList appList = new AppList();
		AppOperate appO = new AppOperate();
		appO.setAppAid("100000000111");
		List<AppOperate> appOperateList = new ArrayList<AppOperate>();
		appOperateList.add(appO);
		appList.setAppOperate(appOperateList);
		request.setAppList(appList);
		request.setCommonType("");
		request.setSessionID("1111002054123000001");
		ResExecAPDU response = client.execAPDU(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testAPDU100101() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqExecAPDU request = new ReqExecAPDU();
		request.setCardNo("12000004000000100006");
		request.setTimeStamp("20120006152146");
		request.setCommandID("100101");

		ResExecAPDU response = client.execAPDU(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	 //@Test
	public void testSMS() {
		ProxyServiceFactory factory = new ProxyServiceFactory(
				"http://218.206.179.214:8080/tsm/services/OuterWebService?wsdl", "OuterWebService",
				"http://www.chinamobile.com");
		OuterWebService client = factory.getHttpPort(OuterWebService.class);
		client.smsNotifyUser("13880668542", "100112000004000000100009111111111111111111222222");
	}
	 //@Test
		public void testMOSMS() {
			ProxyServiceFactory factory = new ProxyServiceFactory(
					"http://218.206.179.214:8080/tsm/services/SmsWebService?wsdl", "SmsWebService",
					"http://www.chinamobile.com");
			SmsWebService client = factory.getHttpPort(SmsWebService.class);
			client.hanldeMessage("100112000004000000100009111111111111111111222222", "13880668542");
		}

	 @Test
	public void testOperate() {
		String x = "3036303530343636363636363636033132331431323030303030343030303030303130303030360C313233343132333431323334";
		System.out.println(x);
	}
	@Test
	public void testPushhSMS() {
		//smsEndpoint.pushMessage("", smsContent)
		 LvObject lvo = new LvObject();
		 ConvertUtils.long2HexStingWithNecessaryEvenLength(222L);
		 lvo.add(ByteUtils.toHexString("123".getBytes()));
		 String clientId = lvo.build();
		 LvObject lv = new LvObject();
		 lv.add(ByteUtils.toHexString("12000004000000100006".getBytes()));
		 String cardNo = lv.build();
		 LvObject lv2 = new LvObject();
		 lv2.add(ByteUtils.toHexString("123412341234".getBytes()));
		 String serial = lv2.build();
		 String port = ByteUtils.toHexString("6666".getBytes());
		 System.out.println("clientId="+clientId+" seId="+cardNo);
		 smsEndpoint.pushMessage("15867175330", MessageFormat.MSG_FORMAT_TYPE_BYTE.getValue(), port,port, clientId,cardNo,serial);
		/*ProxyServiceFactory factory = new ProxyServiceFactory(
				"http://218.206.179.214:8080/tsm/services/OuterWebService?wsdl", "OuterWebService",
				"http://www.chinamobile.com");
		OuterWebService client = factory.getHttpPort(OuterWebService.class);
		client.sendPushSms("15267468791", MessageFormat.MSG_FORMAT_TYPE_ASCII.getValue(), "9999", "9999", "22", "12000004000000100006", "12345678");
		client.sendPushSms("15867175330", MessageFormat.MSG_FORMAT_TYPE_ASCII.getValue(), "9999", "9999", "22", "12000004000000100006", "12345678");*/
	}
	//@Test
	public void testSysType(){
		String sysType = StringUtils.substringBefore(StringUtils.substringAfter("ME-Android-1.0", "-"),
		"-");
		System.out.println(sysType);
	}
	//@Test
	/*public void testMoSms(){
		//ProxyServiceFactory factory = new ProxyServiceFactory(p.getProperty("service.url"), p.getProperty("service.name"), p.getProperty("service.qname"));
		//SmsWebCaller caller = factory.getHttpPort(SmsWebCaller.class);
		//caller.hanldeMessage(msgContent, mobileNo);
		ProxyServiceFactory factory = new ProxyServiceFactory("http://218.206.179.214:8080/tsm/services/SmsWebService?wsdl", "SmsWebService", NameSpace.CM);
		SmsWebCaller caller = factory.getHttpPort(SmsWebCaller.class);
		String hexStr= "00000018800000040005F24515BF77C002F3CE7200000000";
		com.justinmobile.cmpp.webservice.Status status = caller.hanldeMessage(hexStr, "13880668542");
		System.out.println(status.getStatusDescription());
	}*/
	@Test
	public void testUUID(){
		String[] s = StringUtils.split("like=卡", "&");
		System.out.println(s.length);
	}
}
