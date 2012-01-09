package com.justinmobile.tsm.endpoint.webservice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.DomainKey;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@WebService(targetNamespace = NameSpace.CM)
public interface TsmCallProviderWebService {

	/**
	 * 操作结果通知
	 * 
	 * @param seqNum
	 *            1.交易序号
	 * @param sessionId
	 *            2.会话ID
	 * @param sessionType
	 *            3.会话类型
	 * @param timeStamp
	 *            4.时间戳
	 * @param originalSeqNum
	 *            5.源包交易序号
	 * @param msisdn
	 *            6.手机号
	 * @param seId
	 *            7.SEID
	 * @param appAid
	 *            8.AID
	 * @param resultCode
	 *            9.卡端操作结果返回码
	 * @param resultMsg
	 *            10.卡端操作结果返回消息
	 * @param imsi
	 *            11.IMSI
	 * @param cardPOR
	 *            12.卡响应
	 * @param status
	 *            13.操作结果
	 * @param ifContinueOpt
	 *            14.是否有后续操作
	 * @param keyVersion
	 *            15.密钥版本
	 * @param domainKeys
	 *            16.辅助安全域密钥
	 * @param personalizations
	 *            17.个人化指令列表
	 */
	@WebMethod(operationName = "OperationResultNotify")
	@WebResult(name = "OperationResultNotifyResponse", targetNamespace = NameSpace.CM)
	public void operationResult(
			// 参数列表
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,// 1.交易序号
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionId,// 2.会话ID
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) Integer sessionType,// 3.会话类型
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,// 4.时间戳
			@WebParam(name = "OriginalSeqNum", targetNamespace = NameSpace.CM) String originalSeqNum,// 5.源包交易序号
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,// 6.手机号
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,// 7.SEID
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,// 8.AID
			@WebParam(name = "ResultCode", targetNamespace = NameSpace.CM) String resultCode,// 9.卡端操作结果返回码
			@WebParam(name = "ResultMsg", targetNamespace = NameSpace.CM) String resultMsg,// 10.卡端操作结果返回消息
			@WebParam(name = "Imsi", targetNamespace = NameSpace.CM) String imsi,// 11.IMSI
			@WebParam(name = "CardPOR", targetNamespace = NameSpace.CM) CardPOR cardPOR,// 12.卡响应
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status,// 13.操作结果
			@WebParam(name = "IfContinueOpt", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Integer> ifContinueOpt,// 14.是否有后续操作
			@WebParam(name = "KeyVersion", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> keyVersion,// 15.密钥版本
			@WebParam(name = "DomainKey", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<List<DomainKey>> domainKeys,// 16.辅助安全域密钥
			@WebParam(name = "Personalization", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<List<Personalization>> personalizations,// 17.个人化指令列表
			@WebParam(name = "PersoType", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Integer> personalType// 18.个人化类型
	);

	/**
	 * 预处理
	 * 
	 * @param seqNum
	 *            1.交易序号
	 * @param sessionId
	 *            2.会话ID
	 * @param sessionType
	 *            3.会话类型
	 * @param timeStamp
	 *            4.时间戳
	 * @param commType
	 *            5.承载类型
	 * @param msisdn
	 *            6.手机号
	 * @param seId
	 *            7.SEID
	 * @param imei
	 *            8.IMEI
	 * @param appAid
	 *            9.AID
	 * @param status
	 *            10.操作结果
	 * @param providerSessionId
	 *            11.业务平台会话ID
	 * @param domainAid
	 *            12.安全域AID
	 * @param ssdDapSign
	 *            13.辅助安全域签名
	 * @param keyVersion
	 *            14.密钥版本
	 * @param domainKeys
	 *            15.密钥
	 * @param personalizations
	 *            16.个人化指令列表
	 * @param personalType
	 *            18.个人化类型
	 * @param orgMsisdn
	 *            19.原手机号码
	 */
	@WebMethod(operationName = "PreOperationsReq")
	@WebResult(name = "PreOperationsReqResponse", targetNamespace = NameSpace.CM)
	public void preOperation(
			@WebParam(name = "SeqNum", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> seqNum,// 1.交易序号
			@WebParam(name = "SessionID", targetNamespace = NameSpace.CM) String sessionId,// 2.会话ID
			@WebParam(name = "SessionType", targetNamespace = NameSpace.CM) Integer sessionType,// 3.会话类型
			@WebParam(name = "TimeStamp", mode = Mode.INOUT, targetNamespace = NameSpace.CM) Holder<String> timeStamp,// 4.时间戳
			@WebParam(name = "CommType", targetNamespace = NameSpace.CM) Integer commType,// 5.承载类型
			@WebParam(name = "Msisdn", targetNamespace = NameSpace.CM) String msisdn,// 6.手机号
			@WebParam(name = "SEID", targetNamespace = NameSpace.CM) String seId,// 7.SEID
			@WebParam(name = "IMEI", targetNamespace = NameSpace.CM) String imei,// 8.IMEI
			@WebParam(name = "AppAID", targetNamespace = NameSpace.CM) String appAid,// 9.AID
			@WebParam(name = "Status", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Status> status,// 10.操作结果
			@WebParam(name = "ProviderSessionId", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> providerSessionId,// 11.业务平台会话ID
			@WebParam(name = "DomainAID", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> domainAid,// 12.安全域AID
			@WebParam(name = "SSDDapSign", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> ssdDapSign,// 13.辅助安全域签名
			@WebParam(name = "KeyVersion", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> keyVersion,// 14.密钥版本
			@WebParam(name = "DomainKey", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<List<DomainKey>> domainKeys,// 15.密钥
			@WebParam(name = "Personalization", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<List<Personalization>> personalizations,// 16.个人化指令列表
			@WebParam(name = "PersoType", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<Integer> personalType,// 18.个人化类型
			@WebParam(name = "OrgMsisdn", mode = Mode.OUT, targetNamespace = NameSpace.CM) Holder<String> orgMsisdn// 19.原手机号码
	);
}
