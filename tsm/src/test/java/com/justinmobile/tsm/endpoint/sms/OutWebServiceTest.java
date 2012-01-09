package com.justinmobile.tsm.endpoint.sms;

import org.junit.Test;

import com.justinmobile.core.utils.webservice.ProxyServiceFactory;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;

public class OutWebServiceTest {

	@Test
	public void testSmsNotifyUser() {
		ProxyServiceFactory factory = new ProxyServiceFactory("http://218.206.179.214:8080/tsm/services/OuterWebService?wsdl",
				"OuterWebService", NameSpace.CM);
		OuterWebService service = factory.getHttpPort(OuterWebService.class);
		service.smsNotifyUser("13910767907", "结行短信网关测试");
	}

}
