package com.justinmobile.tsm.process.mocam.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationService.BusinessPlatformInterface;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.ApduResult;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.engine.ApduEngine;
import com.justinmobile.tsm.cms2ac.engine.TransactionHelper;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.cms2ac.message.MocamMessage;
import com.justinmobile.tsm.cms2ac.message.MocamMessageBuilder;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PersonalizationResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PersonalizationResult;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationResponse;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.domain.Personalizations;

public abstract class PublicOperationProcessor extends AbstractMocamProcessor {

	@Autowired
	protected MocamMessageBuilder mocamMessageBuilder;

	@Autowired
	protected OracleSequenceDao oracleSequenceDao;

	/**
	 * 进行操作的准备工作
	 * 
	 * @param localTransaction
	 * @return
	 */
	protected MocamResult startup(LocalTransaction localTransaction) {
		check(localTransaction);

		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);

		return processTrans(localTransaction);
	}

	// TSM->业务平台交互方法-----------开始
	/**
	 * 结果通知<br/>
	 * 通知成功，执行会话状态为SessionStatus.COMPLETED<br/>
	 * 通知失败，执行会话状态为SessionStatus.TERMINATE的操作并返回结果<br/>
	 * 
	 * @param localTransaction
	 *            通知成功后的会话状态
	 * @return
	 */
	protected MocamResult operationResult(LocalTransaction localTransaction) {
		return operationResult(localTransaction, SessionStatus.COMPLETED);
	}

	/**
	 * 结果通知<br/>
	 * 通知成功，执行会话状态为successSessionStatus<br/>
	 * 通知失败，执行会话状态为SessionStatus.TERMINATE的操作并返回结果<br/>
	 * 
	 * @param localTransaction
	 * @param successSessionStatus
	 *            通知成功后的会话状态
	 * @return
	 */
	protected MocamResult operationResult(LocalTransaction localTransaction, int successSessionStatus) {
		if (applicationServiceManager.isAuthorized(localTransaction.getAid(), BusinessPlatformInterface.RESULT_NOTIFY)) {
			OperationResultResponse response = providerCaller.operationResult(buildOperationResultMessage(localTransaction));
			if (response.isSuccess()) {
				localTransaction.setSessionStatus(successSessionStatus);
				localTransaction.setHasContinusOpt(response.hasContinueOpt());

				localTransaction.setCurrentPersonlizationIndex(localTransaction.getPersonalizations().size());
				parsePersonalizations(localTransaction, response);
			} else {
				Status status = response.getStatus();
				localTransaction.setResult(status.getStatusCode());
				localTransaction.setFailMessage(status.getStatusDescription());
				localTransaction.setSessionStatus(SessionStatus.TERMINATE);
			}
		} else {
			PlatformException e = new PlatformException(PlatformErrorCode.UNAUTHORIZED_INTERFACE,
					BusinessPlatformInterface.RESULT_NOTIFY.getValue());
			localTransaction.setResult(e.getErrorCode().getErrorCode());
			localTransaction.setFailMessage(e.getMessage());
			localTransaction.setSessionStatus(SessionStatus.TERMINATE);
		}

		return process(localTransaction);
	}

	protected OperationResultMessage buildOperationResultMessage(LocalTransaction localTransaction) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		CardPOR cardPOR = localTransaction.getCardPOR();

		OperationResultMessage message = new OperationResultMessage();
		message.setSeqNum(oracleSequenceDao.getNextSerialNoWithTime(TransactionHelper.TRANS_SEQNUM));
		message.setSessionId(localTransaction.getProviderSessionId());
		message.setSessionType(Operation.valueOf(localTransaction.getProcedureName()).getSessionType().getValue());
		message.setTimeStamp(CalendarUtils.getFormatNow());
		message.setOriginalSeqNum(null);
		message.setAid(localTransaction.getAid());
		message.setMsisdn(localTransaction.getMobileNo());
		if (null != cardPOR && !cardPOR.isSuccess()) {
			message.setReslutCode(cardPOR.getLastAPDUSW());
		} else {
			message.setReslutCode(PlatformMessage.PROVIDER_WEB_SERVICE_SUCCESS.getCode());
		}
		message.setResultMsg(null);
		message.setImsi(card.getImsi());
		message.setCardPOR(cardPOR);

		return message;
	}

	/**
	 * 业务平台预处理<br/>
	 * 预处理成功，执行会话状态为SessionStatus.PRE_OPERATION_SUCCESS的操作并返回结果<br/>
	 * 预处理失败，执行会话状态为SessionStatus.TERMINATE的操作并返回结果<br/>
	 * 
	 * @param localTransaction
	 * @return
	 */
	protected MocamResult preOperation(LocalTransaction localTransaction) {
		if (applicationServiceManager.isAuthorized(localTransaction.getAid(), BusinessPlatformInterface.PRE_OPERTION)) {
			PreOperationResponse response = transactionHelper.preOperation(localTransaction);
			if (response.isSuccess()) {
				localTransaction.setProviderSessionId(response.getProviderSessionId());
				localTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);

				parsePersonalizations(localTransaction, response);
			} else {
				Status status = response.getStatus();
				localTransaction.setResult(status.getStatusCode());
				localTransaction.setFailMessage(status.getStatusDescription());
				localTransaction.setSessionStatus(SessionStatus.TERMINATE);
			}
		} else {
			PlatformException e = new PlatformException(PlatformErrorCode.UNAUTHORIZED_INTERFACE,
					BusinessPlatformInterface.PRE_OPERTION.getValue());
			localTransaction.setResult(e.getErrorCode().getErrorCode());
			localTransaction.setFailMessage(e.getMessage());
			localTransaction.setSessionStatus(SessionStatus.TERMINATE);
		}
		return process(localTransaction);
	}

	/**
	 * 解析响应中的个人化数据列表，解析结果保存在流程中
	 * 
	 * @param localTransaction
	 *            当前流程
	 * @param response
	 */
	private void parsePersonalizations(LocalTransaction localTransaction, PersonalizationResponse response) {
		Personalizations personalizations = new Personalizations();
		if (null != response.getPersonalizations()) {// 如果响应中有个人化指令列表，处理个人化指令列表
			for (int i = 0; i < response.getPersonalizations().size(); i++) {
				personalizations.addPersonalization(response.getPersonalizations().get(i).convertEntity());
			}
		}

		if (CollectionUtils.isNotEmpty(personalizations.getPersonalizations())) {// 如果当前个人化指令列表中的个人化指令不为空，保存在流程记录中
			localTransaction.addPersonalizations(personalizations);
			localTransaction.setPersonalType(response.getPersonalType());
		}
	}

	// TSM->业务平台交互方法-----------结束

	/**
	 * 验证卡上当前安全域空间的空间是否够用<br/>
	 * 当前安全域是指最后一次通过select指令选择的安全域
	 * 
	 * @param cardSecurityDomain
	 *            卡上安全域
	 * @param needSpace
	 *            需要的空间
	 * @throws PlatformErrorCode.SD_SPACE_SCARCITY
	 *             如果卡上安全域的空间不够用
	 */
	protected void validateSpace(LocalTransaction localTransaction, Space needSpace) {
		CardSecurityDomain cardSecurityDomain = getCardSecurityDomainThatIsCurrentSelected(localTransaction);
		validateSpace(cardSecurityDomain, needSpace);
	}

	/**
	 * 验证卡上指定安全域空间的空间是否够用
	 * 
	 * @param cardSecurityDomain
	 *            卡上指定安全域
	 * @param needSpace
	 *            需要的空间
	 * @throws PlatformErrorCode.SD_SPACE_SCARCITY
	 *             如果卡上安全域的空间不够用
	 */
	protected void validateSpace(CardSecurityDomain cardSecurityDomain, Space needSpace) {
		if (!cardSecurityDomain.getAviliableSpace().isGreaterOrEqual(needSpace)) {
			throw new PlatformException(PlatformErrorCode.SD_SPACE_SCARCITY);
		}
	}

	/**
	 * 同步卡上指定安全域可用空间
	 * 
	 * @param card
	 *            卡上指定安全域
	 * @param cardSpace
	 *            卡上安全域可用空间
	 */
	private void syncSpace(CardSecurityDomain cardSecurityDomain, Space cardSpace) {
		Space platformSpace = new Space();
		platformSpace.plus(cardSecurityDomain.getAviliableSpace());
		platformSpace.plus(cardSecurityDomain.getUnknownSpace());

		if (!cardSpace.isGreaterOrEqual(platformSpace)) {// 如果卡上真实的可变空间和/或不可变空间小于平台记录的值，调整平台记录

			Space unknownSpace = new Space();
			Space aviliableSpace = new Space();

			if (platformSpace.getNvm() > cardSpace.getNvm()) {// 如果平台的不可变空间大于卡上不可变空间
				aviliableSpace.setNvm(cardSpace.getNvm());// 可用不可变空间以卡上不可变空间为准
				unknownSpace.setNvm(platformSpace.getNvm() - cardSpace.getNvm());// 将平台的不可变空间大于卡上不可变空间的差值部分设置为异常不可变空间
			} else {
				aviliableSpace.setNvm(platformSpace.getNvm());// 否则，使用平台记录的不可变空间
			}

			if (platformSpace.getRam() > cardSpace.getRam()) {// 如果平台的可变空间大于卡上可变空间
				aviliableSpace.setRam(cardSpace.getRam());// 可用可变空间以卡上可变空间为准
				unknownSpace.setRam(platformSpace.getRam() - cardSpace.getRam());
			} else {
				aviliableSpace.setRam(platformSpace.getRam());// 否则，使用平台记录的可变空间
			}

			cardSecurityDomain.setAviliableSpace(aviliableSpace);
			cardSecurityDomain.setUnknownSpace(unknownSpace);
			cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);
		}
	}

	/**
	 * 同步卡上当前安全域空间<br/>
	 * 当前安全域是指最后一次通过select指令选择的安全域
	 * 
	 * @param localTransaction
	 * @param cardSpace
	 *            卡上当前安全域可用空间
	 */
	protected void syncSpace(LocalTransaction localTransaction, Space cardSpace) {
		syncSpace(getCardSecurityDomainThatIsCurrentSelected(localTransaction), cardSpace);
	}

	/**
	 * 获取卡上当前选择的安全域<br/>
	 * 当前安全域是指最后一次通过select指令选择的安全域
	 * 
	 * @param localTransaction
	 * @return
	 */
	protected CardSecurityDomain getCardSecurityDomainThatIsCurrentSelected(LocalTransaction localTransaction) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		SecurityDomain sd = localTransaction.getLastCms2acParam().getCurrentSecurityDomain();

		return cardSecurityDomainManager.getbySdAndCard(card, sd);
	}

	protected MocamResult buildMocamMessage(LocalTransaction trans, Cms2acParam cms2acParam, int sessionStatus) {
		if (sessionStatus != SessionStatus.DUPLICATE_STATUS) {
			trans.setSessionStatus(sessionStatus);
			trans.increaseExecuteNo();
		}
		MocamResult result = new MocamResult();
		List<MocamMessage> messages = Lists.newArrayList();
		if (cms2acParam.hasNoSecurity()) {
			messages.add(mocamMessageBuilder.noSecurityMashal(trans));
		} else {
			messages.addAll(mocamMessageBuilder.mashal(trans));
		}
		if (CollectionUtils.isNotEmpty(messages)) {
			for (MocamMessage mocamMessage : messages) {
				result.getApdus().add(mocamMessage.toString());
			}
		}

		trans.setSessionStatus(sessionStatus);
		localTransactionManager.saveOrUpdate(trans);
		return result;
	}

	protected MocamResult buildMocamMessage(LocalTransaction trans, Cms2acParam cms2acParam, List<ApduCommand> commands, int sessionStatus) {
		if (sessionStatus != SessionStatus.DUPLICATE_STATUS) {
			trans.setSessionStatus(sessionStatus);
			trans.increaseExecuteNo();
		}
		MocamResult result = new MocamResult();
		List<MocamMessage> messages = Lists.newArrayList();
		if (cms2acParam.hasNoSecurity()) {
			messages.add(mocamMessageBuilder.noSecurityMashal(trans));
		} else {
			messages.addAll(mocamMessageBuilder.mashal(trans, commands));
		}
		if (CollectionUtils.isNotEmpty(messages)) {
			for (MocamMessage mocamMessage : messages) {
				result.getApdus().add(mocamMessage.toString());
			}
		}

		trans.setSessionStatus(sessionStatus);
		localTransactionManager.saveOrUpdate(trans);
		return result;
	}

	protected void setPrompt(MocamResult result, String progress, String progressPercent) {
		result.setProgress(progress);
		result.setProgressPercent(progressPercent);
	}

	protected PersonalizationResult buildPersonalizationResult(LocalTransaction trans) {
		PersonalizationResult result = new PersonalizationResult();

		result.setAppAID(trans.getAid());
		result.setMsisdn(trans.getMobileNo());
		result.setSeqNum(oracleSequenceDao.getSequence("SEQ_PERSO_RESULT"));
		result.setTimeStamp(CalendarUtils.getFormatNow());
		if (trans.isMainTransaction()) {
			result.setSessionId(trans.getLocalSessionId());
		} else {
			result.setSessionId(trans.getMainTransaction().getLocalSessionId());
		}

		return result;
	}

	protected MocamResult launchSelectSdForSynCardSpace(LocalTransaction localTransaction, SecurityDomain sd, int sessionStatus) {
		if ((SecurityDomain.MODEL_ISD == sd.getModel().intValue()) || ((SecurityDomain.UNFIXABLE_SPACE == sd.getSpaceRule().intValue()))) {
			sd = securityDomainManager.getIsd();
		}

		return launchSelectSd(localTransaction, sd, sessionStatus);
	}

	protected void validateCard(CardInfo card) {
		if (CardInfo.STATUS_DISABLE.equals(card.getStatus())) {// 如果卡状态为“不可用”，抛出异常
			throw new PlatformException(PlatformErrorCode.TRANS_CARD_DISABLE);
		}

		// 判断绑定关系
		CustomerCardInfo customerCard = customerCardInfoManager.getByCardNo(card.getCardNo());
		if (null == customerCard || CustomerCardInfo.STATUS_NORMAL != customerCard.getStatus().intValue()
				|| CustomerCardInfo.INBLACK == customerCard.getInBlack().intValue()) {
			throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
		}

		// 检查卡上主安全域
		SecurityDomain isd = securityDomainManager.getIsd();
		CardSecurityDomain cardIsd = cardSecurityDomainManager.getByCardNoAid(card.getCardNo(), isd.getAid());
		if (CardSecurityDomain.STATUS_LOCK == cardIsd.getStatus().intValue()) {// 如果卡上主安全域状态为“已锁定”，抛出异常
			throw new PlatformException(PlatformErrorCode.TRANS_CARD_LOCKED);
		}
	}

	/**
	 * 检查应用版本，同时会检查应用
	 * 
	 * @param applicationVersion
	 * @throws TRANS_APP_VER_STATUS_ILLEGAL
	 *             如果应用版本不是“已发布”状态
	 */
	protected void validate(ApplicationVersion applicationVersion) {
		if (ApplicationVersion.STATUS_PULISHED != applicationVersion.getStatus().intValue()) {
			throw new PlatformException(PlatformErrorCode.TRANS_APP_VER_STATUS_ILLEGAL);
		}

		validate(applicationVersion.getApplication());
	}

	/**
	 * 检查应用版本的手机号限定，同时会对应用版本进行检查
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param mobileNo
	 *            手机号
	 * @throws TRANS_DOWNLOAD_APP_MOBILE_NO_UNSUPPORT_APPLICATION_VERSION
	 *             如果应用版本不支持手机号
	 */
	protected void validate(ApplicationVersion applicationVersion, String mobileNo) {
		if (!applicationVersion.isMobileNoLimite(mobileNo)) {
			throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_MOBILE_NO_UNSUPPORT_APPLICATION_VERSION);
		}

		validate(applicationVersion);
	}

	/**
	 * 检查应用，同时会检查应用所属SP和安全域
	 * 
	 * @param application
	 * @throws TRANS_APP_STATUS_ILLEGAL
	 *             如果应用不是“已发布”状态
	 */
	protected void validate(Application application) {
		if (Application.STATUS_PUBLISHED != application.getStatus().intValue()) {
			throw new PlatformException(PlatformErrorCode.TRANS_APP_STATUS_ILLEGAL);
		}

		validateSd(application.getSd());
		validateSp(application.getSp());
	}

	/**
	 * 检查SP
	 * 
	 * @param sp
	 * 
	 * @throws PlatformErrorCode.SP_NOT_EXIST
	 *             sp为null
	 * @throws PlatformErrorCode.SP_UNAVALIABLE
	 *             sp为状态不为正常或者sp在黑名单
	 */
	protected void validateSp(SpBaseInfo sp) {
		if (sp == null) {
			throw new PlatformException(PlatformErrorCode.SP_NOT_EXIST);
		}

		if (SpBaseInfo.NORMAL != sp.getStatus()) {
			throw new PlatformException(PlatformErrorCode.SP_UNAVALIABLE);
		}

		if (SpBaseInfo.INBLACK == sp.getInBlack()) {
			throw new PlatformException(PlatformErrorCode.SP_UNAVALIABLE);
		}
	}

	protected MocamResult parseInitUpdateSdRsp(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseInitUpdateRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_INIT_UPDATE_ERROR, ae);
		}

		ApduCommand extAuthCmd = apduEngine.buildExtAuthCmd(cms2acParam);
		contactApduCommand(cms2acParam, extAuthCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Ext_Auth);
		return result;
	}

	protected int parseCms2acMoMocamMessage(LocalTransaction localTransaction, Cms2acParam cms2acParam) {
		// MocamProcess moMocamProcess = localTransaction.getLastMocamProcess();
		// MocamMessage mocamMessage = extractMoMocamMessage(moMocamProcess);
		//
		// int responseIndex = parseScp02ApduResponse(localTransaction,
		// cms2acParam, mocamMessage);
		return 1;
	}

	protected void parseExtAuthSdRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseExtAuthRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_EXT_AUTH_ERROR, ae);
		}
	}

	protected MocamResult launchInstallSd(LocalTransaction localTransaction, int sessionStatus, boolean isLoadFileSd) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand installSdCmd = apduEngine.buildInstallForInstallCmd(cms2acParam, true, isLoadFileSd);
		contactApduCommand(cms2acParam, installSdCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Install_For_Install);
		return result;
	}

	protected MocamResult launchPutSdKey(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand putKeyCmd = apduEngine.buildPutKeyCmd(cms2acParam, true);
		contactApduCommand(cms2acParam, putKeyCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Put_Key);
		return result;
	}

	protected void parseSelectSdRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseSelectRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.SELECT_APP_ERROR, ae);
		}
	}

	protected MocamResult launchInitUpdate(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand initUpdateCmd = apduEngine.buildInitUpdateCmd(cms2acParam);
		contactApduCommand(cms2acParam, initUpdateCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Init_Update);
		return result;
	}

	protected void parseInstallPersoRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseInstallPersoRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_EXT_AUTH_ERROR, ae);
		}

	}

	protected void parseSelectAppRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseSelectRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.SELECT_APP_ERROR, ae);
		}
	}

	protected List<ApduCommand> batchApduCommand(List<ApduCommand> loadCmdBatch, int perBatchLength, int firstBatchNo) {
		int batchLength = 0;
		int batchNo = firstBatchNo;
		int index = 1;
		for (ApduCommand apduCommand : loadCmdBatch) {
			apduCommand.setBatchNo(batchNo);
			apduCommand.setIndex(index);
			batchLength += apduCommand.getLength();
			if (batchLength > perBatchLength) {
				batchNo++;
				index = 1;
				batchLength = 0;
			} else {
				index++;
			}
		}
		return loadCmdBatch;
	}

	protected List<ApduCommand> getExeCmdBatch(List<ApduCommand> loadCmdBatch, int exeBatchNo) {
		List<ApduCommand> exeLoadCmdBatch = new ArrayList<ApduCommand>();
		for (ApduCommand apduCommand : loadCmdBatch) {
			if (apduCommand.getBatchNo() == exeBatchNo) {
				exeLoadCmdBatch.add(apduCommand);
			}
		}
		return exeLoadCmdBatch;
	}

	protected MocamResult parsePersoApduRsp(LocalTransaction localTransaction) {
		MocamResult result = null;
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		if (!isBatchCmdIndexCorrect(localTransaction)) {
			throw new PlatformException(PlatformErrorCode.PERSONALIZE_BATCH_EXE_ERROR);
		}

		List<ApduCommand> exePersoCmdBatch = nextPersoExeCmdBatch(localTransaction, cms2acParam);
		if (exePersoCmdBatch != null) {// 如果现有个人化指令没有执行完成，则继续执行
			// 后续的个人化指令已经在CMS2AC中，不需要其他处理
			result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.PERSONALIZE_APP_APDU_CMD);
			result.setApduName(ApduName.Perso_Cmd);
		} else {// 如果现有个人化指令执行完成，通知业务平台个人化结果
			if (cms2acParam != null) {
				CardPOR cardPOR = buildCardPOR(cms2acParam);// 组件卡响应对象
				localTransaction.setCardPOR(cardPOR);
			}

			result = operationResult(localTransaction, SessionStatus.DOWNLOAD_APP_PESO_APDU_RSP);

		}
		return result;
	}

	private CardPOR buildCardPOR(Cms2acParam cms2acParam) {
		CardPOR cardPOR = new CardPOR();
		ApduResult apduResult = cms2acParam.getLastApduResult();
		if (apduResult != null && StringUtils.isNotBlank(apduResult.getRawHex())) {
			cardPOR.setApduSum(Integer.toHexString(apduResult.getIndex()));
			byte[] sw = new byte[2];
			sw[0] = apduResult.getSw1();
			sw[1] = apduResult.getSw2();
			cardPOR.setLastAPDUSW(ConvertUtils.byteArray2HexString(sw));
			byte[] data = apduResult.getData();
			if (data != null) {
				cardPOR.setLastData(ConvertUtils.byteArray2HexString(data));
			}
		}
		return cardPOR;
	}

	protected boolean isBatchCmdIndexCorrect(LocalTransaction localTransaction) {
		int returnedLastApduIndex = localTransaction.getLastCms2acParam().getLastApduResult().getIndex();
		int commandIndex = localTransaction.getLastCms2acParam().getCommandIndex();

		// commandIndex记录的是指令的数目是从1开始计数；而最后一条成功的指令的索引是从0开始计数
		return returnedLastApduIndex == commandIndex;
	}

	/**
	 * 完成个人化流程
	 * 
	 * @param localTransaction
	 * @param status
	 *            完成个人化流程后事务所处状态
	 */
	protected void endPerso(LocalTransaction localTransaction, int status) {
		localTransaction.setSessionStatus(status);
		localTransaction.increaseExecuteNo();

		localTransactionManager.saveOrUpdate(localTransaction);
	}

	/**
	 * 修改卡上应用状态
	 * 
	 * @param cardNo
	 *            卡号
	 * @param appAid
	 *            应用aid
	 * @param status
	 *            状态值
	 */
	protected void changeCardApplicationStatus(String cardNo, String appAid, Integer status) {
		CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, appAid);
		cardApp.setStatus(status);
		cardApplicationManager.saveOrUpdate(cardApp);
	}

	/**
	 * 修改卡上安全域状态
	 * 
	 * @param cardNo
	 *            卡号
	 * @param appAid
	 *            安全域aid
	 * @param status
	 *            状态值
	 */
	protected void changeCardSecurityDomainStatus(String cardNo, String aid, int status) {
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, aid);
		// 将当前状态放到orginalstatus，新的状态更新status
		cardSecurityDomain.setOrginalStatus(cardSecurityDomain.getStatus());
		cardSecurityDomain.setStatus(status);
		cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);
	}

	protected void preparePerso(LocalTransaction localTransaction, int prePersoSessionStatus) {
		localTransaction.setSessionStatus(prePersoSessionStatus);
		localTransaction.increaseExecuteNo();
		localTransactionManager.saveOrUpdate(localTransaction);
	}

	protected List<ApduCommand> nextPersoExeCmdBatch(LocalTransaction localTransaction, Cms2acParam cms2acParam) {
		if (cms2acParam.getCommandBatchNo().intValue() == cms2acParam.getLastApduCommand().getBatchNo().intValue()) {
			// 如果现有个人化指令执行完成，则返回空
			return null;
		} else {
			// 否则继续执行下一个批次的个人化指令
			return continueNextBatchCmd(cms2acParam);
		}
	}

	protected List<ApduCommand> continueNextBatchCmd(Cms2acParam cms2acParam) {
		cms2acParam.increaseBatchNo();
		List<ApduCommand> exeCmdBatch = getExeCmdBatch(cms2acParam.getApduCommands(), cms2acParam.getCommandBatchNo());
		cms2acParam.setCommandIndex(exeCmdBatch.size());
		return exeCmdBatch;
	}

	protected MocamResult getAppSpace(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		buildGetDataSpaceCmd(cms2acParam);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Get_Data);
		return result;
	}

	protected Space parseGetFreeSpace(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			Space space = apduEngine.parseGetSdSpaceRsp(cms2acParam);
			return space;
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_GET_DATA_ERROR, ae);
		}
	}

	protected MocamResult launchInstallPerso(LocalTransaction localTransaction, int sessionStatus, String personalAid) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand persoCmd = apduEngine.buildInstallPersoCmd(cms2acParam, personalAid);

		contactApduCommand(cms2acParam, persoCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Install_For_Personalization);
		return result;
	}

	protected MocamResult launchSelectSd(LocalTransaction localTransaction, SecurityDomain sd, int sessionStatus) {
		CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(), sd.getAid());
		if ((null == cardSd.getCurrentKeyVersion()) || sd.getCurrentKeyVersion().intValue() != cardSd.getCurrentKeyVersion().intValue()) {

			buildSubTransaction(localTransaction, sd.getAid(), Operation.UPDATE_KEY);

			return process(localTransaction, null);
		} else {
			Cms2acParam cms2acParam = buildCms2acParam(localTransaction, sd);
			ApduCommand selectSdCmd = buildSelectCmd(cms2acParam);

			contactApduCommand(cms2acParam, selectSdCmd);

			MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
			result.setApduName(ApduName.Select);
			return result;
		}
	}

	protected ApduCommand buildSelectCmd(Cms2acParam cms2acParam) {
		return apduEngine.buildSelectCmd(cms2acParam, cms2acParam.getCurrentSecurityDomain().getAid());
	}

	public MocamMessage buildMocamMessage(String sessionId, byte[] apduCmd) {
		int code = MocamMessage.CODE_MT_APDU_COMMAND;
		MocamMessage mocamMessage = new MocamMessage(sessionId, code, apduCmd);
		return mocamMessage;
	}

	protected void contactApduCommand(Cms2acParam cms2acParam, ApduCommand apduCommand) {
		cms2acParam.increaseBatchNo();
		apduCommand.setBatchNo((int) cms2acParam.getCommandBatchNo());

		cms2acParam.setCommandIndex(1);
		apduCommand.setApduIndex(1);

		apduCommand.setCms2acParam(cms2acParam);
		cms2acParam.getApduCommands().add(apduCommand);
	}

	protected List<ApduCommand> serializeApduCmdBatch(Cms2acParam cms2acParam, List<ApduCommand> cmdBatch, int perBatchCmdBytesLength) {
		cms2acParam.increaseBatchNo();
		cmdBatch = batchApduCommand(cmdBatch, perBatchCmdBytesLength, cms2acParam.getCommandBatchNo());
		List<ApduCommand> exeCmdBatch = getExeCmdBatch(cmdBatch, cms2acParam.getCommandBatchNo());

		cms2acParam.setCommandIndex(exeCmdBatch.size());

		for (ApduCommand apduCommand : exeCmdBatch) {
			apduCommand.setCms2acParam(cms2acParam);
			cms2acParam.getApduCommands().add(apduCommand);
		}
		return exeCmdBatch;
	}

	protected Cms2acParam buildCms2acParam(LocalTransaction localTransaction, SecurityDomain securityDomain) {
		Cms2acParam cms2acParam = new Cms2acParam();
		localTransaction.addCms2acParam(cms2acParam);

		cms2acParam.setCurrentSecurityDomain(securityDomain);

		cms2acParam.setScp(Constants.CMS2AC_SCP_02);
		cms2acParam.setScp02i(Constants.CMS2AC_DEFAULT_SCP02_I);

		ResourceBundle rb = ResourceBundle.getBundle("i18n/messages_zh_CN", Locale.getDefault());
		Integer platformSecurityLevel = Integer.parseInt(rb.getString("securityLevel"));
		Integer sdSecurityLevel = cms2acParam.getCurrentSecurityDomain().getScp02SecurityLevel();
		if (platformSecurityLevel.intValue() > sdSecurityLevel.intValue()) {
			cms2acParam.setScp02SecurityLevel(platformSecurityLevel);
		} else {
			cms2acParam.setScp02SecurityLevel(sdSecurityLevel);
		}

		cms2acParam.setKic(securityDomain.getEncKey());
		cms2acParam.setKid(securityDomain.getMacKey());
		cms2acParam.setDek(securityDomain.getDekKey());

		return cms2acParam;
	}

	/**
	 * 根据安全域类型，构建get data指令<br/>
	 * 固定空间(签约空间)安全域，构建P1P2 = 2F00的get data指令<br/>
	 * 非固定空间安全域或主安全域，构建P1P2 = FF20的get data指令<br/>
	 * 指令构建完成后放在Cms2acParam对象中
	 * 
	 * @param cms2acParam
	 */
	protected void buildGetDataSpaceCmd(Cms2acParam cms2acParam) {
		SecurityDomain currentSd = cms2acParam.getCurrentSecurityDomain();
		ApduCommand getDataCmd;
		if (currentSd.isIsd()) {
			getDataCmd = apduEngine.buildGetDataCmd(cms2acParam, ApduEngine.GET_DATA_CMD_P1P2_CARD_RESOURCE);
		} else {
			if (SecurityDomain.FIXED_SPACE == currentSd.getSpaceRule()) {
				getDataCmd = apduEngine.buildGetDataCmd(cms2acParam, ApduEngine.GET_DATA_CMD_P1P2_INSTALLED_APP);
			} else {
				getDataCmd = apduEngine.buildGetDataCmd(cms2acParam, ApduEngine.GET_DATA_CMD_P1P2_CARD_RESOURCE);
			}
		}

		contactApduCommand(cms2acParam, getDataCmd);
	}

	/**
	 * 根据安全域的空间规则更新剩余空间<br/>
	 * 如果是固定空间安全域，剩余空间是卡片安全域的剩余空间<br/>
	 * 如果是引用大小空间安全域，剩余空间是卡片剩余空间
	 * 
	 * @param localTransaction
	 * @param freeVolatileSpace
	 *            剩余的可变空间
	 * @param freeNoneVolatileSpace
	 *            剩余的不可变空间
	 */
	@Deprecated
	protected void cardSpaceSync(LocalTransaction localTransaction, int freeVolatileSpace, long freeNoneVolatileSpace) {
		SecurityDomain securityDomain = localTransaction.getLastCms2acParam().getCurrentSecurityDomain();

		if (SecurityDomain.FIXED_SPACE == securityDomain.getSpaceRule()) {// 签约空间安全域
			CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
					securityDomain.getAid());

			cardSecurityDomain.setFreeNonVolatileSpace(freeNoneVolatileSpace);
			cardSecurityDomain.setFreeVolatileSpace(freeVolatileSpace);

			cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);
		} else {
			CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
			card.setAvailableNonevolatileSpace(freeNoneVolatileSpace);
			card.setAvailableVolatileSpace(freeVolatileSpace);
			cardInfoManager.saveOrUpdate(card);
		}
	}

	protected void endDeleteSd(LocalTransaction localTransaction, String sdAid) {
		endTransaction(localTransaction, PlatformMessage.SUCCESS);

		String cardNo = localTransaction.getCardNo();

		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, sdAid);
		cardSecurityDomain.setCurrentKeyVersion(null);// 将卡上当前密钥版本改为null表示没有密钥
		cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);
	}

	protected void changeSameLoadFileCardAppStatus(String cardNo, LoadFileVersion loadFileVersion, int status) {
		List<CardApplication> sameLoadFileCardApps = transactionHelper.getCardAppWithSameLoadFile(cardNo, loadFileVersion);
		for (CardApplication cardApp : sameLoadFileCardApps) {
			changeCardApplicationStatus(cardNo, cardApp.getApplicationVersion().getApplication().getAid(), status);
		}
	}

	protected MocamResult launchDeleteSd(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand deleteCmd = apduEngine.buildDeleteCmd(cms2acParam, ApduEngine.DELETE_CMD_DATA_TYPE_SD_APP, true);
		contactApduCommand(cms2acParam, deleteCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Delete);
		return result;
	}

	/**
	 * 检查安全域，同时检查安全域所属SP
	 * 
	 * @param sd
	 *            被检查的安全域
	 * @throws TRANS_SD_STATUS_ILLEGAL
	 *             如果安全域的状态不是“已发布”
	 */
	protected void validateSd(SecurityDomain sd) {
		// 检查安全域的状态
		if (SecurityDomain.STATUS_PUBLISHED != sd.getStatus()) {
			throw new PlatformException(PlatformErrorCode.TRANS_SD_STATUS_ILLEGAL);
		}
		// 检查安全域所属应用提供商
		validateSp(sd.getSp());
	}

	protected MocamResult launchDeleteLoadFileOrApplet(LocalTransaction localTransaction, int delType, Collection<String> aids,
			int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		List<ApduCommand> deleteCmds = apduEngine.buildDeleteCmd(cms2acParam, delType, aids);

		deleteCmds = serializeApduCmdBatch(cms2acParam, deleteCmds, Constants.MOCAM_DATA_MAX_LENGTH);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, deleteCmds, sessionStatus);
		result.setApduName(ApduName.Delete);
		return result;
	}
}
