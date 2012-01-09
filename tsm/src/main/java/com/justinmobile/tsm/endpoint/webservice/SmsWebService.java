package com.justinmobile.tsm.endpoint.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@WebService(targetNamespace = NameSpace.CM)
public interface SmsWebService {

	@WebMethod(operationName = "HandleMessage")
	@WebResult(name = "Status", targetNamespace = NameSpace.CM)
	public Status hanldeMessage(
	// 参数列表
			@WebParam(name = "Content", targetNamespace = NameSpace.CM) String content// 短信内容
			, @WebParam(name = "MSISDN", targetNamespace = NameSpace.CM) String mobileNo)// 手机号
	;
}
