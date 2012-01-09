package com.justinmobile.tsm.endpoint.sms;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@Service("outerService")
@WebService(targetNamespace = NameSpace.CM, serviceName = "OuterWebService", portName="OuterWebServiceHttpPort")
public class OuterWebServiceImpl implements OuterWebService {
	
	@Autowired
	private SmsEndpoint smsEndpoint;

	@Override
	public boolean smsNotifyUser(String mobileNo, String message) {
		return smsEndpoint.sendMessage(mobileNo, message);
	}

	@Override
	public boolean smsPushUser(String mobileNo, String message) {
		return smsEndpoint.pushMessage(mobileNo, message);
	}

}
