package com.justinmobile.tsm.endpoint.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.TsmCallProviderWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.DomainKey;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.SessionType;
import com.justinmobile.tsm.transaction.domain.PersonalizeCommand;
import com.justinmobile.tsm.transaction.domain.ProviderTransaction;
import com.justinmobile.tsm.transaction.manager.PersonalizeCommandManager;
import com.justinmobile.tsm.transaction.manager.ProviderTransactionManager;

@WebService(serviceName = "TsmCallProviderWebService", targetNamespace = NameSpace.CM, portName = "TsmCallProviderWebServiceHttpPort")
@Service("tsmCallProviderService")
@Transactional
public class TsmCallProviderWebServiceImpl implements TsmCallProviderWebService {

	protected static final Logger log = LoggerFactory.getLogger(TsmCallProviderWebServiceImpl.class);

	@Autowired
	private ProviderTransactionManager providerTransactionManager;

	@Autowired
	private OracleSequenceDao oracleSequenceDao;

	@Autowired
	private PersonalizeCommandManager personalizeCommandManager;

	@Override
	public void preOperation(Holder<String> seqNum, String sessionId, Integer sessionTypeValue, Holder<String> timeStamp, Integer commType,
			String msisdn, String seId, String imei, String appAid, Holder<Status> status, Holder<String> providerSessionId,
			Holder<String> domainAid, Holder<String> ssdDapSign, Holder<String> keyVersion, Holder<List<DomainKey>> domainKeys,
			Holder<List<Personalization>> personalizations, Holder<Integer> personalType, Holder<String> orgMsisdn) {
		log.debug("\n" + "收到预处理请求" + "\n");

		try {
			SessionType sessionType = SessionType.valueOf(sessionTypeValue);
			// 生成业务平台SessionId
			providerSessionId.value = buildTrans(appAid, sessionType.name());
			// 设置状态，默认是成功
			status.value = new Status();

			personalizations.value = preOperation(providerSessionId.value, sessionType, appAid, personalType);
			// 暂时不知道干什么用，给空值
			domainAid.value = null;
			ssdDapSign.value = null;
			keyVersion.value = null;
			domainKeys.value = Lists.newArrayList();
		} catch (PlatformException e) {
			e.printStackTrace();
			status.value.setStatusCode(e.getErrorCode().getErrorCode());
			status.value.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.value.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.value.setStatusDescription(e.getMessage());
		}
	}

	private ArrayList<Personalization> preOperation(String sessionId, SessionType sessionType, String appAid, Holder<Integer> personalType) {
		switch (sessionType) {
		case SERVICE_UNSUBSCRIBE:
		case SERVICE_UPDATE:
		case PERSO_DATA_MANAGE:
			return getPersonalizationWhenPreOperation(sessionId, appAid, personalType);
		default:
			return null;
		}
	}

	private ArrayList<Personalization> getPersonalizationWhenPreOperation(String sessionId, String appAid, Holder<Integer> personalType) {
		ProviderTransaction trans = providerTransactionManager.getBySessionId(sessionId);
		if (trans == null) {// 测试专用，正常应该在通知的时候创建
			throw new PlatformException(PlatformErrorCode.TRANS_SERVICE_FAIL);
		}

		int type = 2;

		// 设置是否还有后续
		int endFlag = getEndFlag(trans, appAid, type);
		// 设置apdu指令，如果没后续则返回null
		return getFileContent(endFlag, trans, appAid, type, personalType);
	}

	private String buildTrans(String appAid, String sessionType) {
		String sessionId = oracleSequenceDao.getNextSerialNoWithTime("PROVIDER_SESSION_SEQ");
		ProviderTransaction trans = new ProviderTransaction();
		trans.setAppAid(appAid);
		trans.setSessionId(sessionId);
		trans.setBatchIndex(0);
		trans.setProcedureName(sessionType);
		providerTransactionManager.saveOrUpdate(trans);
		return sessionId;
	}

	@Override
	public void operationResult(Holder<String> seqNum, String sessionId, Integer sessionType, Holder<String> timeStamp,
			String originalSeqNum, String msisdn, String seId, String appAid, String resultCode, String resultMsg, String imsi,
			CardPOR cardPOR, Holder<Status> status, Holder<Integer> ifContinueOpt, Holder<String> keyVersion,
			Holder<List<DomainKey>> domainKeys, Holder<List<Personalization>> personalizations, Holder<Integer> personalType) {
		try {
			ProviderTransaction trans = providerTransactionManager.getBySessionId(sessionId);
			if (trans == null) {// 测试专用，正常应该在通知的时候创建
				throw new PlatformException(PlatformErrorCode.TRANS_SERVICE_FAIL);
			}

			int type = 2;

			// 设置是否还有后续
			ifContinueOpt.value = getEndFlag(trans, appAid, type);
			// 设置apdu指令，如果没后续则返回null
			personalizations.value = getFileContent(ifContinueOpt.value, trans, appAid, type, personalType);
			// 设置状态，默认是成功
			status.value = new Status();
			// 暂时不知道干什么用，给空值
			keyVersion.value = null;
			domainKeys.value = Lists.newArrayList();
		} catch (PlatformException e) {
			e.printStackTrace();
			status.value.setStatusCode(e.getErrorCode().getErrorCode());
			status.value.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.value.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.value.setStatusDescription(e.getMessage());
		}
	}

	private ArrayList<Personalization> getFileContent(Integer ifContinueOpt, ProviderTransaction trans, String appAid, int type,
			Holder<Integer> personalType) {
		if (PersonalizeCommand.END_FLAG_NOT_LAST == ifContinueOpt) {
			return buildApduCommand(trans, appAid, type, personalType);
		} else {
			trans.setBatchIndex(null);
			trans.setOriginalType(type);
			return null;
		}
	}

	private Integer getEndFlag(ProviderTransaction trans, String appAid, int type) {
		int newBatch = trans.increaseBatchIndex();
		int maxBatch = personalizeCommandManager.getMaxBatchIndex(appAid, type);
		if (newBatch <= maxBatch) {
			return PersonalizeCommand.END_FLAG_NOT_LAST;
		} else {
			return PersonalizeCommand.END_FLAG_LAST;
		}
	}

	private ArrayList<Personalization> buildApduCommand(ProviderTransaction trans, String appAid, int type, Holder<Integer> personalType) {
		List<PersonalizeCommand> cmds = personalizeCommandManager.getByAppAidAndBatch(appAid, trans.getBatchIndex(), type);
		if (CollectionUtils.isEmpty(cmds)) {
			throw new PlatformException(PlatformErrorCode.APDU_PERSONALIZE_APP_ERROR);
		}

		ArrayList<Personalization> personalizations = new ArrayList<Personalization>();
		for (PersonalizeCommand cmd : cmds) {
			personalizations.add(cmd.convertToWebDto());
		}
		personalType.value = cmds.get(0).getPersonalType();
		return personalizations;
	}
}
