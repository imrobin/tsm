package com.justinmobile.tsm.endpoint.webservice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.webservice.ProxyServiceFactory;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.ProviderService;
import com.justinmobile.tsm.endpoint.webservice.TsmCallProviderWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.DomainKey;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationResponse;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.SessionType;
import com.justinmobile.tsm.utils.SystemConfigUtils;

/**
 * 业务平台请求客户端，用于tsm向业务平台发起
 */
@Service("providerCaller")
public class ProviderCallerImpl implements ProviderService, TsmCallProviderWebService {

	protected static final Logger log = LoggerFactory.getLogger(ProviderCallerImpl.class);

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	private TsmCallProviderWebService getService(String aid, int sessionTypeValue) {
		String url = null;
		String serviceName = null;
		SessionType sessionType = SessionType.valueOf(sessionTypeValue);
		if (SystemConfigUtils.useMockBusinessPlatform()) {// 如果启用了模拟业务平台，使用配置文件中的数据
			url = SystemConfigUtils.getMockBusinessPlatformUrl();
			serviceName = SystemConfigUtils.getMockBusinessPlatformServiceName();
		} else {// 否则，使用数据库的数据
			if (SessionType.SESSION_TYPE_APPLICATION.contains(sessionType)) {
				Application application = applicationManager.getByAid(aid);
				url = application.getBusinessPlatformUrl();
				serviceName = application.getServiceName();
			} else if (SessionType.SESSION_TYPE_SECURITY_DOMAIN.contains(sessionType)) {
				SecurityDomain securityDomain = securityDomainManager.getByAid(aid);
				url = securityDomain.getBusinessPlatformUrl();
				serviceName = securityDomain.getServiceName();
			}
		}

		log.debug("\n" + "url: " + url + "\n" + "serviceName: " + serviceName + "\n");
		Map<String, String> namespaceMap = new HashMap<String, String>();
		namespaceMap.put(NameSpace.CM, NameSpace.CM_PREFIX);
		return new ProxyServiceFactory(url, serviceName).getHttpPort(TsmCallProviderWebService.class, namespaceMap);
	}

	@Override
	public PreOperationResponse preOperation(PreOperationMessage preOperationMessage) {
		Holder<String> seqNum = new Holder<String>(preOperationMessage.getSeqNum());
		Holder<String> timeStamp = new Holder<String>(preOperationMessage.getTimeStamp());
		Holder<Status> status = new Holder<Status>();
		Holder<String> providerSessionId = new Holder<String>();

		Holder<String> domainAid = new Holder<String>();
		Holder<String> ssdDapSign = new Holder<String>();
		Holder<String> keyVersion = new Holder<String>();
		Holder<List<DomainKey>> domainKeys = new Holder<List<DomainKey>>();
		Holder<List<Personalization>> personalizations = new Holder<List<Personalization>>();
		Holder<Integer> personalType = new Holder<Integer>();
		preOperation(
		// 参数列表
				seqNum,// 1.交易序号
				preOperationMessage.getSessionId(), // 2.会话ID
				preOperationMessage.getSessionType(), // 3.会话类型
				timeStamp, // 4.时间戳
				preOperationMessage.getCommType(),// 5.承载方式
				preOperationMessage.getMsisdn(), // 6.手机号
				preOperationMessage.getSeId(),// 7.SEID
				preOperationMessage.getImei(),// 8.IMEI
				preOperationMessage.getAppAid(), // 9.AID
				status,// 10.操作结果
				providerSessionId,// 11.业务平台会话ID
				domainAid,// 12.安全域AID
				ssdDapSign,// 13.辅助安全域签名
				keyVersion,// 14.密钥版本
				domainKeys,// 15.密钥
				personalizations,// 16.个人化指令列表
				personalType,// 17.个人化类型
				null);

		PreOperationResponse response = new PreOperationResponse();
		response.setSeqNum(seqNum.value);
		response.setTimeStamp(timeStamp.value);
		response.setStatus(status.value);
		response.setProviderSessionId(providerSessionId.value);
		response.setPersonalizations(personalizations.value);
		response.setPersonalType(getPersonalType(personalType.value));

		return response;
	}

	@Override
	public void preOperation(Holder<String> seqNum, String sessionId, Integer sessionType, Holder<String> timeStamp, Integer commType,
			String msisdn, String seId, String imei, String appAid, Holder<Status> statusHolder, Holder<String> providerSessionId,
			Holder<String> domainAid, Holder<String> ssdDapSign, Holder<String> keyVersion, Holder<List<DomainKey>> domainKeys,
			Holder<List<Personalization>> personalizations, Holder<Integer> personalType, Holder<String> orgMsisdn) {
		log.debug("\n" + "发送预处理请求" + "\n");
		Status status = new Status();
		statusHolder.value = status;
		try {
			getService(appAid, sessionType).preOperation(seqNum, sessionId, sessionType, timeStamp, commType, msisdn, seId, imei, appAid,
					statusHolder, providerSessionId, domainAid, ssdDapSign, keyVersion, domainKeys, personalizations, personalType,
					orgMsisdn);
		} catch (WebServiceException e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.WEBSERVICE_ERROR.getErrorCode());
			status.setStatusDescription(PlatformErrorCode.WEBSERVICE_ERROR.getDefaultMessage());
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}
		log.debug("\n" + "预处理结果：" + statusHolder.value.getStatusCode() + "\n");
	}

	@Override
	public void operationResult(Holder<String> seqNum, String sessionId, Integer sessionType, Holder<String> timeStamp,
			String originalSeqNum, String msisdn, String seId, String appAid, String resultCode, String resultMsg, String imsi,
			CardPOR cardPOR, Holder<Status> statusHolder, Holder<Integer> ifContinueOpt, Holder<String> keyVersion,
			Holder<List<DomainKey>> domainKeys, Holder<List<Personalization>> personalizations, Holder<Integer> personalType) {
		log.debug("\n" + "发送卡操作结果请求" + "\n");
		Status status = new Status();
		statusHolder.value = status;
		try {
			getService(appAid, sessionType).operationResult(seqNum, sessionId, sessionType, timeStamp, originalSeqNum, msisdn, seId,
					appAid, resultCode, resultMsg, imsi, cardPOR, statusHolder, ifContinueOpt, keyVersion, domainKeys, personalizations,
					personalType);

		} catch (WebServiceException e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.WEBSERVICE_ERROR.getErrorCode());
			status.setStatusDescription(PlatformErrorCode.WEBSERVICE_ERROR.getDefaultMessage());
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}
		log.debug("\n" + "收到卡操作结果响应：" + statusHolder.value.getStatusCode() + "\n");
	}

	@Override
	public OperationResultResponse operationResult(OperationResultMessage message) {
		OperationResultResponse response = new OperationResultResponse();

		Holder<String> seqNum = new Holder<String>(message.getSeqNum());
		Holder<String> timeStamp = new Holder<String>(message.getTimeStamp());
		Holder<Status> status = new Holder<Status>(new Status());
		Holder<Integer> ifContinueOpt = new Holder<Integer>();
		Holder<String> keyVersion = new Holder<String>();
		Holder<List<DomainKey>> domainKeys = new Holder<List<DomainKey>>();
		Holder<List<Personalization>> personalizations = new Holder<List<Personalization>>();
		Holder<Integer> personalType = new Holder<Integer>();

		operationResult(seqNum, message.getSessionId(), message.getSessionType(), timeStamp, message.getOriginalSeqNum(),
				message.getMsisdn(), message.getMsisdn(), message.getAid(), message.getReslutCode(), message.getResultMsg(),
				message.getImsi(), message.getCardPOR(), status, ifContinueOpt, keyVersion, domainKeys, personalizations, personalType);

		response.setSeqNum(seqNum.value);
		response.setTimeStamp(timeStamp.value);
		response.setStatus(status.value);
		response.setIfContinueOpt(ifContinueOpt.value);
		response.setPersonalizations(personalizations.value);
		response.setPersonalType(getPersonalType(personalType.value));

		return response;
	}

	private PersonalType getPersonalType(Integer value) {
		if (null == value) {
			return null;
		} else if (0 == value.intValue() || 1 == value.intValue()) {
			return PersonalType.PASSTHROUGH;
		} else {
			return PersonalType.valueOf(value.intValue());
		}
	}
}
