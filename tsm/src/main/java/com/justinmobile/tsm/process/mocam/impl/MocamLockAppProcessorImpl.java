package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("mocamLockAppProcessor")
public class MocamLockAppProcessorImpl extends PublicOperationProcessor implements MocamProcessor {

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startupLock(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchLockloadApp(localTransaction);
			setPrompt(result, "建立安全通道", "20");
			break;
		case SessionStatus.LOCK_APP_SELECT_SD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.LOCK_APP_INITUPDATE_CMD);
			setPrompt(result, "建立安全通道", "40");
			break;
		case SessionStatus.LOCK_APP_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.LOCK_APP_EXTAUTH_CMD);
			setPrompt(result, "建立安全通道", "60");
			break;
		case SessionStatus.LOCK_APP_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchLockApp(localTransaction);
			setPrompt(result, "锁定中", "80");
			break;
		case SessionStatus.LOCK_APP_LOCK_CMD:// 校验外部认证指令响应,发起install
			parseLockAppRsp(localTransaction);
			result = MocamResult.getLastResult(localTransaction.getAid());
			break;
		default:
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
		}
		return result;
	}

	private MocamResult startupLock(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		return processTrans(localTransaction);
	}

	private void parseLockAppRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseSetStatusRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_LOCK_APP_ERROR, localTransaction, ae);
		}
		if (Operation.LOCK_APP.name().equals(localTransaction.getProcedureName())) {
			changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_LOCKED);
		} else if (Operation.LOCK_SD.name().equals(localTransaction.getProcedureName())) {
			changeCardSecurityDomainStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardSecurityDomain.STATUS_LOCK);
		}
		super.endTransaction(localTransaction, PlatformMessage.SUCCESS);
	}

	private MocamResult launchLockApp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand lockAppCmd = null;
		if ((LocalTransaction.Operation.LOCK_SD == Operation.valueOf(localTransaction.getProcedureName()))
				&& (securityDomainManager.getByAid(localTransaction.getAid()).isIsd())) {
			lockAppCmd = apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x7F, false);
		} else {
			lockAppCmd = apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x87, false);
		}
		contactApduCommand(cms2acParam, lockAppCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.LOCK_APP_LOCK_CMD);
		result.setApduName(ApduName.Set_Status);
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		// 校验卡
		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		if (!cardInfoManager.getByCardNo(cardNo).isTestCard()) {
			validateCard(card);
		}

		// 验证应用
		Application app = applicationManager.getByAid(localTransaction.getAid());
		if (app == null) {
			// 校验是否是删除安全域
			SecurityDomain sd = securityDomainManager.getByAid(localTransaction.getAid());
			if (sd == null) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_NOT_EXIST, localTransaction);
			} else {
				CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(cardNo, sd.getAid());
				if (cardSd != null) {
					if (!CardSecurityDomain.STATUS_LOCKABLE.contains(cardSd.getStatus())) {
						throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
					}
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
				}
			}
		} else {
			// 检验卡上应用
			CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, localTransaction.getAid());
			if (null != cardApp) {
				if (!CardApplication.STATUS_LOCKABLE.contains(cardApp.getStatus())) {// 最新版本曾经下载且未完全删除，检查当前应用卡上状态是否是可下载状态
					throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
				}
			} else {
				throw new PlatformException(PlatformErrorCode.APP_NEVER_DOWNLOADED, localTransaction);
			}

			localTransaction.setAppVersion(cardApp.getApplicationVersion().getVersionNo());
		}
	}

	private MocamResult launchLockloadApp(LocalTransaction localTransaction) {
		SecurityDomain sd = securityDomainManager.getIsd();
		MocamResult result = launchSelectSd(localTransaction, sd, SessionStatus.LOCK_APP_SELECT_SD_CMD);
		return result;
	}

}
