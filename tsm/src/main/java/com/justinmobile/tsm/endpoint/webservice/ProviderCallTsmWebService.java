package com.justinmobile.tsm.endpoint.webservice;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;

import com.justinmobile.tsm.endpoint.webservice.dto.DomainKey;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@WebService(targetNamespace = NameSpace.CM)
public interface ProviderCallTsmWebService {
	
	@WebMethod(operationName = "ApplicationAPDUReq")
	@WebResult(name = "ApplicationAPDUReqResponse", targetNamespace = NameSpace.CM)
	void applicationAPDU(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,// 交易序号
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionId,// 会话ID
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,// 时间戳
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,// 手机号
			@WebParam(name = "CmdType", targetNamespace = NameSpace.CM) Integer cmdType,
			@WebParam(name = "Endflag", targetNamespace = NameSpace.CM) Integer endflag,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,// SEID
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,// AID
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "FileContent", targetNamespace = NameSpace.CM) String fileContent,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status);

	@WebMethod(operationName = "DownloadApplicationReq")
	@WebResult(name = "DownloadApplicationReqResponse", targetNamespace = NameSpace.CM)
	void downloadApplication(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionId,
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,
			@WebParam(name = "DomainAID", targetNamespace = NameSpace.CM) String domainAid,
			@WebParam(name = "SSDDapSign", targetNamespace = NameSpace.CM) String ssdDapSign,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status);

	@WebMethod(operationName = "DeleteApplicationReq")
	@WebResult(name = "DeleteApplicationReqResponse", targetNamespace = NameSpace.CM)
	public void deleteApplication(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionID,
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status
	);
	
	@WebMethod(operationName = "CreateSSDReq")
	@WebResult(name = "CreateSSDReqResponse", targetNamespace = NameSpace.CM)
	public void createSSD(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionID,
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status
	);
	
	@WebMethod(operationName = "DeleteSSDReq")
	@WebResult(name = "DeleteSSDReqResponse", targetNamespace = NameSpace.CM)
	public void deleteSSD(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionID,
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status
	);
	
	@WebMethod(operationName = "UpdateDomainKeyReq")
	@WebResult(name = "UpdateDomainKeyReqResponse", targetNamespace = NameSpace.CM)
	public void updateDomainKey(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionID,
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "DomainAID", targetNamespace = NameSpace.CM) String domainAid,
			@WebParam(name = "KeyVersion", targetNamespace = NameSpace.CM) String keyVersion,
			@WebParam(name = "DomainKey", targetNamespace = NameSpace.CM) List<DomainKey> domainKey,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status
	);
	
	@WebMethod(operationName = "LockApplicationReq")
	@WebResult(name = "LockApplicationReqResponse", targetNamespace = NameSpace.CM)
	public void lockApplication(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionID,
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) String sessionType,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,
			@WebParam(name = "LockFlag", targetNamespace = NameSpace.CM) Integer lockFlag,
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status
	);
	
	@WebMethod(operationName = "EventNotifyReq")
	@WebResult(name = "EventNotifyReqResponse", targetNamespace = NameSpace.CM)
	void businessEventNotify(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,// 交易序号
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionId,// 会话ID
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,// 时间戳
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,// 承载方式
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,// 手机号
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,// 应用AID
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,// SEID
			@WebParam(name = "EventID", targetNamespace = NameSpace.CM) Integer eventId,// 事件ID；1-退网；2-退订；
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status// 状态
	);

	@WebMethod(operationName = "AcquireTokenReq")
	@WebResult(name = "AcquireTokenReqResponse", targetNamespace = NameSpace.CM)
	public void acquireToken(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionID,
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,// 应用AID
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,
			@WebParam(name = "DomainAID", targetNamespace = NameSpace.CM) String domainAid,
			@WebParam(name = "HashValue", targetNamespace = NameSpace.CM) String hashValue, 
			@WebParam(name = "Token", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> Token, 
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> Status
	);
}
