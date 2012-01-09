package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Service("mocamCreateSdProcessor")
public class MocamCreateSSDProcessorImpl extends PublicOperationProcessor implements MocamProcessor {

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupCreateSsd(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchCreateSd(localTransaction);
			setPrompt(result, "创建安全域", "12");
			break;
		case SessionStatus.CREATE_SD_SELECT_ISD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchSyncSpaceOrInstallSsd(localTransaction);
			setPrompt(result, "创建安全域", "25");
			break;
		case SessionStatus.CREATE_SD_SPACE_CMD:
			Space space = parseGetFreeSpace(localTransaction);
			// 同步卡空间
			syncSpace(localTransaction, space);
			// 验证空间
			validateSpace(localTransaction, securityDomainManager.getByAid(localTransaction.getAid()).getTotalSpace());
			result = launchInitUpdate(localTransaction, SessionStatus.CREATE_SD_INITUPDATE_CMD);
			setPrompt(result, "创建安全域", "37");
			break;
		case SessionStatus.CREATE_SD_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.CREATE_SD_EXTAUTH_CMD);
			setPrompt(result, "创建安全域", "50");
			break;
		case SessionStatus.CREATE_SD_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchInstallSd(localTransaction, SessionStatus.CREATE_SD_INSTALL_CMD, false);
			setPrompt(result, "创建安全域", "62");
			break;
		case SessionStatus.CREATE_SD_INSTALL_CMD:
			parseInstallSdRsp(localTransaction);
			result = beginUpdateKey(localTransaction);
			setPrompt(result, "创建安全域", "75");
			break;
		case SessionStatus.CREATE_SD_END_INSTALL:
			result = endSuccessProcess(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult launchSyncSpaceOrInstallSsd(LocalTransaction localTransaction) {
		MocamResult result;

		if (SystemConfigUtils.isCms2acRuntimeEnvironment()) {// 如果当前运行环境为cms2ac，同步主安全域安全域的空间信息
			result = getAppSpace(localTransaction, SessionStatus.CREATE_SD_SPACE_CMD);
		} else {// 如果当前运行环境不是cms2ac，下发init-update开始创建安全域
			result = launchInitUpdate(localTransaction, SessionStatus.CREATE_SD_INITUPDATE_CMD);
		}

		return result;
	}

	private MocamResult startupCreateSsd(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);

		return processTrans(localTransaction);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String aid = localTransaction.getAid();
		String cardNo = localTransaction.getCardNo();

		SecurityDomain sd = securityDomainManager.getByAid(aid);
		if (null == sd) {
			throw new PlatformException(PlatformErrorCode.TRANS_CREATE_SSD_SD_UNEXSIT);
		}

		CardInfo card = cardInfoManager.getByCardNo(cardNo);

		// 检查安全域
		validateSd(sd);

		// 检查卡
		validateCard(card);

		// 判断该批次的卡片是否支持当前的安全域
		CardBaseSecurityDomain cardBaseSecurityDomain = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, card.getCardBaseInfo());
		if (cardBaseSecurityDomain == null) {
			throw new PlatformException(PlatformErrorCode.CARD_BASE_NOT_REF_SD, aid);
		}

		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, aid);
		if (cardSecurityDomain == null) {// 如果没有卡上安全域记录，创建记录
			cardSecurityDomain = new CardSecurityDomain();
			cardSecurityDomain.setCard(card);
			cardSecurityDomain.setSd(sd);
			if (sd.getSpaceRule().intValue() == SecurityDomain.FIXED_SPACE) {
				int freeVolatileSpace = sd.getManagedVolatileSpace();
				long freeNoneVolatileSpace = sd.getManagedNoneVolatileSpace();
				cardSecurityDomain.setFreeVolatileSpace(freeVolatileSpace);
				cardSecurityDomain.setFreeNonVolatileSpace(freeNoneVolatileSpace);
			}

			cardSecurityDomain.setScp02SecurityLevel(sd.getScp02SecurityLevel());
			cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);
		}

		// 校验卡上安全域
		if (!CardSecurityDomain.STATUS_CREATEABLE.contains(cardSecurityDomain.getStatus())) {
			throw new PlatformException(PlatformErrorCode.SD_EXISTED);
		}
	}

	private void parseInstallSdRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseInstallRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_INSTALL_SD_ERROR, ae);
		}

		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
				localTransaction.getAid());
		SecurityDomain sd = cardSecurityDomain.getSd();
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());

		// 修改卡上安全域状态
		cardSecurityDomain.setStatus(CardSecurityDomain.STATUS_CREATED);

		// 如果是签约空间安全域，填写卡上安全域的空用空间
		if (sd.isSpaceFixed()) {
			cardSecurityDomain.setAviliableSpace(sd.getTotalManagedSpace());
			cardSecurityDomain.setUnknownSpace(new Space());
		}

		cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);

		// 完成空间计算
		card.setAvailableSpace(card.getAvailableSpace().minus(sd.getTotalSpace()));
		cardInfoManager.saveOrUpdate(card);
	}

	private MocamResult beginUpdateKey(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		localTransactionManager.saveOrUpdate(localTransaction);

		SecurityDomain securityDomain = securityDomainManager.getByAid(localTransaction.getAid());
		if (SecurityDomain.MODEL_TOKEN == securityDomain.getModel().intValue()) {// 如果是代理安全域，通知业务平台操作结果
			return operationResult(localTransaction);
		} else {// 否则，执行密钥更新
			buildSubTransaction(localTransaction, localTransaction.getAid(), null, LocalTransaction.Operation.UPDATE_KEY);
			return process(localTransaction);
		}
	}

	private MocamResult launchCreateSd(LocalTransaction localTransaction) {
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
				localTransaction.getAid());

		MocamResult result = null;
		if (CardSecurityDomain.STATUS_UNCREATE == cardSecurityDomain.getStatus()) {// 如果卡上安全域状态是“未创建”，创建安全域
			result = launchSelectSd(localTransaction, securityDomainManager.getIsd(), SessionStatus.CREATE_SD_SELECT_ISD_CMD);
		} else if (CardSecurityDomain.STATUS_CREATED == cardSecurityDomain.getStatus()) {// 如果卡上安全域状态是“已创建”，进入更新密钥子流程
			result = beginUpdateKey(localTransaction);
		} else if (CardSecurityDomain.STATUS_KEY_UPDATED == cardSecurityDomain.getStatus()) {// 如果卡上安全域状态是“密钥已更新”，进入更新密钥子流程
			result = beginUpdateKey(localTransaction);
		} else {// 如果是已创建的安全域，直接跳过安装的过程
			localTransaction.setSessionStatus(SessionStatus.COMPLETED);
			localTransactionManager.saveOrUpdate(localTransaction);
			result = processTrans(localTransaction);
		}

		return result;
	}

	@Override
	protected MocamResult preOperation(LocalTransaction localTransaction) {
		SecurityDomain securityDomain = securityDomainManager.getByAid(localTransaction.getAid());
		if (SecurityDomain.MODEL_TOKEN == securityDomain.getModel().intValue()) {
			return super.preOperation(localTransaction);
		} else {
			localTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);
			return processTrans(localTransaction);
		}
	}

}
