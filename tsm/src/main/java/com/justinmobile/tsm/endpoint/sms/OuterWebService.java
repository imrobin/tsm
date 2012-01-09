package com.justinmobile.tsm.endpoint.sms;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@WebService(targetNamespace = NameSpace.CM, name = "OuterWebService")
public interface OuterWebService {

	@WebResult(name = "status", targetNamespace = NameSpace.CM)
	@RequestWrapper(localName = "smsNotifyUser", targetNamespace = NameSpace.CM)
	@ResponseWrapper(localName = "smsNotifyUserResponse", targetNamespace = NameSpace.CM)
	public boolean smsNotifyUser(
			@WebParam(name = "mobileNo", targetNamespace = NameSpace.CM) String mobileNo,
			@WebParam(name = "message", targetNamespace = NameSpace.CM) String message
		);
	
	@WebResult(name = "status", targetNamespace = NameSpace.CM)
	@RequestWrapper(localName = "smsPushUser", targetNamespace = NameSpace.CM)
	@ResponseWrapper(localName = "smsPushUserResponse", targetNamespace = NameSpace.CM)
	public boolean smsPushUser(
			@WebParam(name = "mobileNo", targetNamespace = NameSpace.CM) String mobileNo,
			@WebParam(name = "message", targetNamespace = NameSpace.CM) String message
		);
}
