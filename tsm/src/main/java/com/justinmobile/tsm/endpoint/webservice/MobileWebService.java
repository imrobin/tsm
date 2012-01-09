package com.justinmobile.tsm.endpoint.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

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
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResLoginOrRegister;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResSdList;

@WebService(targetNamespace = NameSpace.CM)
public interface MobileWebService {

//	@WebMethod(operationName = "Login" )
//	@WebResult(name = "Status", targetNamespace = NameSpace.CM)
//	public Status login(
//			@WebParam(name = "userName", targetNamespace = NameSpace.CM) String userName,
//			@WebParam(name = "password", targetNamespace = NameSpace.CM) String password
//	);
	
	@WebMethod(operationName = "RequestApplicationList")
	@WebResult(name = "ApplicationListResponse", targetNamespace = NameSpace.CM)
	public ResApplicationList listApplication(
			@WebParam(name = "ApplicationListRequest", targetNamespace = NameSpace.CM) ReqApplicationList reqApplicationList
	);
	
	@WebMethod(operationName = "RequestGetInformation")
	@WebResult(name = "GetInformationResponse", targetNamespace = NameSpace.CM)
	public GetInformation getInfo(
			@WebParam(name = "GetInformationRequest", targetNamespace = NameSpace.CM) ReqGetApplicationInfo reqGetApplicationInfo
	);
	
	@WebMethod(operationName = "RequestExecAPDUs")
	@WebResult(name = "ExecAPDUsCmd", targetNamespace = NameSpace.CM)
	public ResExecAPDU execAPDU(
			@WebParam(name = "ExecAPDUsReqest", targetNamespace = NameSpace.CM) ReqExecAPDU reqExecAPDU 		
	);

	@WebMethod(operationName = "RequestPutInformation")
	@WebResult(name = "PutInformationResponse", targetNamespace = NameSpace.CM)
	public BasicResponse postAppComment(
			@WebParam(name = "PutInformationRequest", targetNamespace = NameSpace.CM) ReqAppComment reqAppComment		
	);

	@WebMethod(operationName = "RequestLoadClient")
	@WebResult(name = "LoadClientResponse", targetNamespace = NameSpace.CM)
	public LoadClientResponse loadClient(
			@WebParam(name = "LoadClientRequest", targetNamespace = NameSpace.CM) LoadClientRequest loadClientRequest		
	);
	
	@WebMethod(operationName = "RequestLoginOrRegister")
	@WebResult(name = "LoginOrRegisterResponse", targetNamespace = NameSpace.CM)
	public ResLoginOrRegister loginOrRegiseter(
			@WebParam(name = "LoginOrRegisterRequest", targetNamespace = NameSpace.CM) LoginOrRegisterRequest loginOrRegisterRequest		
	);
	
	@WebMethod(operationName = "RequestSDList")
	@WebResult(name = "SDListResponse", targetNamespace = NameSpace.CM)
	public ResSdList listSd(
			@WebParam(name = "SDListRequest", targetNamespace = NameSpace.CM) ReqSdList reqSdList		
	);

}
