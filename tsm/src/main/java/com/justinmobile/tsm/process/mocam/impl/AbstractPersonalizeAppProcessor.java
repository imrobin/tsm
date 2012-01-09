package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.ApduResult;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.Personalizations;

public abstract class AbstractPersonalizeAppProcessor extends PublicOperationProcessor {

	@Autowired
	@Qualifier("personalizeAppProcessor")
	private MocamProcessor personalizeAppProcessor;

	protected abstract List<ApduCommand> bulildPersonalizeCmd(LocalTransaction localTransaction, String fileContent);

	protected abstract MocamResult launchSelectApp(LocalTransaction localTransaction, int sessionStatus);

	/**
	 * 重写<br/>
	 * 全部委托到PersonalizeAppProcessor.process(LocalTransaction
	 * localTransaction)，以实现个人化类型的切换
	 */
	@Override
	protected MocamResult process(LocalTransaction localTransaction) {
		return personalizeAppProcessor.process(localTransaction, null);
	}

	protected MocamResult startupPersonalize(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		return processTrans(localTransaction);
	}

	protected MocamResult beginPersoApp(LocalTransaction localTransaction) {
		MocamResult result = null;
		if (localTransaction.hasPersonalizationsToExecute()) {// 如果业务平台通知有后续个人化操作，开始下发个人化指令
			if (localTransaction.getCurrentPersonalizations().hasPersonalizationToExecute()) {// 如果当前个人化指令列表中有个人化指令，处理个人化指令
				result = launchSelectApp(localTransaction, SessionStatus.PERSONALIZE_APP_SELECT_CMD);
			} else {// 如果当前个人化指令列表中没有个人化指令，通知业务平台操作结果
				result = operationResult(localTransaction, SessionStatus.PERSONALIZE_APP_BEGIN_PERSON);
			}
		} else {// 否则结束流程
			localTransaction.setSessionStatus(SessionStatus.COMPLETED);
			result = processTrans(localTransaction);
		}
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String aid = localTransaction.getAid();

		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		Application app = applicationManager.getByAid(aid);

		// 校验卡
		validateCard(card);

		// 校验应用的状态
		if (app == null) {
			throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_FOUND);
		} else if (!card.isTestCard() && Application.STATUS_PUBLISHED != app.getStatus().intValue()) {
			throw new PlatformException(PlatformErrorCode.INVALID_APP_STATUS);
		}

		PersonalType personalType = Application.PersonalType.valueOf(app.getPersonalType());
		if (PersonalType.NOT_NECESSARY == personalType) {
			throw new PlatformException(PlatformErrorCode.TRANS_PERSO_APP_NOT_NECESSARY);
		}

		// 检查应用所属SP
		validateSp(app.getSp());

		// 校验卡上应用状态
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
		if (cardApplication == null) {
			throw new PlatformException(PlatformErrorCode.CARD_APP_NOT_FOUND);
		} else if (!CardApplication.STATUS_PESONABLE.contains(cardApplication.getStatus())) {
			throw new PlatformException(PlatformErrorCode.CARD_APP_ERROR_STATUS);
		}

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
		} else {// 如果当前实例的个人化指令执行完成，处理下一实例的个人化指令或通知业务平台个人化结果
			result = launchNextAppletOrNotifyOperationResult(localTransaction);
		}
		return result;
	}

	private MocamResult launchNextAppletOrNotifyOperationResult(LocalTransaction localTransaction) {
		Personalizations currentPersonalizations = localTransaction.getCurrentPersonalizations();
		currentPersonalizations.increaseCurrentPersonalizationIndex();

		MocamResult result;
		if (currentPersonalizations.hasPersonalizationToExecute()) {// 如果还有其他实例的个人化指令，处理下一实例的个人化指令
			localTransaction.setSessionStatus(SessionStatus.PERSONALIZE_APP_BEGIN_PERSON);
			result = processTrans(localTransaction);
		} else {// 如果没有其他实例的个人化指令，通知业务平台操作结果，成功后回到SessionStatus.PERSONALIZE_APP_BEGIN_PERSON状态
			Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
			CardPOR cardPOR = buildCardPOR(cms2acParam);
			localTransaction.setCardPOR(cardPOR);

			result = operationResult(localTransaction, SessionStatus.PERSONALIZE_APP_BEGIN_PERSON);
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

	/**
	 * 组建个人化指令
	 * 
	 * @param localTransaction
	 * @return
	 */
	protected MocamResult launchPersoApdu(LocalTransaction localTransaction) {
		MocamResult result = null;
		String fileContent = getFileContent(localTransaction);
		if (StringUtils.isNotBlank(fileContent)) {// 如果业务平台提示有后续操作并且获取了个人化数据，下发个人化数据
			List<ApduCommand> personalizeCmdBatch = bulildPersonalizeCmd(localTransaction, fileContent);

			List<ApduCommand> exePersonlizeCmdBatch = serializeApduCmdBatch(localTransaction.getLastCms2acParam(), personalizeCmdBatch,
					Constants.MOCAM_PERSO_LENGTH);
			result = buildMocamMessage(localTransaction, localTransaction.getLastCms2acParam(), exePersonlizeCmdBatch,
					SessionStatus.PERSONALIZE_APP_APDU_CMD);

			result.setApduName(ApduName.Perso_Cmd);
		} else {// 否则，结束流程
			localTransaction.setSessionStatus(SessionStatus.COMPLETED);
			result = processTrans(localTransaction);
		}
		return result;
	}

	/**
	 * 获取当前流程待下发的个人化指令模板
	 * 
	 * @param localTransaction
	 *            当前流程
	 * @return 个人化指令模板
	 */
	abstract protected String getFileContent(LocalTransaction localTransaction);

	/**
	 * 是否需要个人化？需要，返回个人化指令；否则返回下一步的指令
	 * 
	 * @param localTransaction
	 * @param nextSessionStatus
	 *            如果需要个人化，会话的状态
	 * @return
	 */
	protected MocamResult launchPersoAppOrEndPerso(LocalTransaction localTransaction, int sessionStatus) {
		MocamResult result;

		// 判断是否有新的个人化指令
		if (StringUtils.isNotEmpty(localTransaction.getFileContent())) {// 如果有，继续执行
			localTransactionManager.saveOrUpdate(localTransaction);
			localTransaction.setSessionStatus(sessionStatus);
			result = processTrans(localTransaction);
		} else {// 否则进入后续流程
			endPerso(localTransaction, SessionStatus.PERSONALIZE_APP_END_PERSO);
			result = processTrans(localTransaction);
		}

		return result;
	}

	protected MocamResult startPersonalize(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.PERSONALIZE_APP_BEGIN_PERSON);
		return processTrans(localTransaction);
	}
}
