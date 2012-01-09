package com.justinmobile.tsm.endpoint.sms;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace = "http://www.justinmobile.com", serviceName = "SmsWebService")
public interface SmsWsClient {
	
	@WebResult(name="success")
	boolean sendMessage(
			@WebParam(name="mobileNo")String mobileNo, 
			@WebParam(name="message")String message
	);
	
	@WebResult(name="success")
	boolean sendApdu(
			@WebParam(name="mobileNo")String mobileNo, 
			@WebParam(name="tpUdhi")Integer tpUdhi, 
			@WebParam(name="tpPid")Integer tpPid, 
			@WebParam(name="messageFormat")Integer messageFormat, 
			@WebParam(name="apdu")String apdu
	);
}
