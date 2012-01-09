package com.justinmobile.tsm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.Service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;

import com.justinmobile.core.utils.webservice.ProxyServiceFactory;
import com.justinmobile.tsm.endpoint.sms.OuterWebService;
import com.justinmobile.tsm.endpoint.webservice.MobileWebService;
import com.justinmobile.tsm.endpoint.webservice.ProviderCallTsmWebService;
import com.justinmobile.tsm.endpoint.webservice.SmsWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppOperate;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.BasicResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.GetInformation;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoginOrRegisterRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqAppComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqGetApplicationInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqSdList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResLoginOrRegister;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResSdList;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;

public class WebServiceCilentTest {

	// @Test
	public void testCilent() {
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from " + "http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		}

		Service.create(wsdlUrl, new QName("http://www.chinamobile.com", "ProviderWebService"));
	}

	// @Test
	public void testCilentServiceName() {
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from " + "http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		}

		Service.create(wsdlUrl, new QName("http://www.chinamobile.com", "WebService"));
	}

	// @Test
	public void testCilentUrl() {
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8080/tsm/services/WebService?wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from " + "http://localhost:8080/tsm/services/ProviderWebService?wsdl");
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

	// @Test
	public void testSms() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(SmsWebService.class);
		factory.setAddress("http://218.206.179.214:8080/tsm/services/OuterWebService?wsdl");
		SmsWebService client = (SmsWebService) factory.create();
		Status status = client.hanldeMessage("测试", "13730684365");
		System.out.println(status);
	}

	// @Test
	public void testLoadClient() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoadClientRequest request = new LoadClientRequest();
		request.setAppAId("100000000111");
		request.setCardNo("08D561000000010007");
		request.setCommandID("100004");
		request.setCommonType("");
		request.setMobileOs("Android");
		request.setOsVersion("2.3");
		request.setSessionID("1111002054123000001");
		request.setUpgradeType("2");
		LoadClientResponse response = client.loadClient(request);
		System.out.println(response);
	}

	// @Test
	public void testGetInfo() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqGetApplicationInfo request = new ReqGetApplicationInfo();
		request.setAppAID("100000000111");
		request.setCardNo("08D561000000010007");
		request.setCommandID("100007");
		request.setCommonType("");
		request.setSessionID("1111002054123000001");
		GetInformation response = client.getInfo(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testPostComment() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqAppComment request = new ReqAppComment();
		request.setAppAID("100000000111");
		request.setCardNo("08D561000000010007");
		request.setCommandID("100301");
		request.setCommonType("");
		// request.setComment("aaabbb");
		request.setSessionID("1111002054123000001");
		BasicResponse response = client.postAppComment(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testSDList() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqSdList request = new ReqSdList();
		request.setCardNo("084906206057526809");
		request.setCommandID("100006");
		request.setIsInstall(true);
		request.setCommonType("");
		request.setSessionID("1111002054123000001");
		ResSdList response = client.listSd(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	@Test
	public void testRegAndLogin() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		LoginOrRegisterRequest request = new LoginOrRegisterRequest();
		request.setCardNo("08D561000000010007");
		request.setImsi("13618087882");
		request.setCommandID("100002");
		request.setChallengeNo("111111");
		request.setCommonType("");
		request.setSessionID("1111002054123000001");
		ResLoginOrRegister response = client.loginOrRegiseter(request);
		System.out.println(response.getStatus().getStatusDescription());
	}

	// @Test
	public void testAPDU() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(MobileWebService.class);
		factory.setAddress("http://118.122.114.104:8888/services/MobileWebService?wsdl");
		MobileWebService client = (MobileWebService) factory.create();
		ReqExecAPDU request = new ReqExecAPDU();
		request.setCardNo("08D561000000010007");
		request.setTimeStamp("1111002054123000001");
		request.setCommandID("100001");
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

	@Test
	public void testSMS() {
		ProxyServiceFactory factory = new ProxyServiceFactory("http://218.206.179.214:8080/tsm/services/OuterWebService?wsdl",
				"OuterWebService", "http://www.chinamobile.com");
		OuterWebService client = factory.getHttpPort(OuterWebService.class);
		client.smsNotifyUser("15881053926", "你好哦");
	}
}
