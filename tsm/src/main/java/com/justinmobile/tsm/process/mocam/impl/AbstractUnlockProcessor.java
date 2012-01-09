package com.justinmobile.tsm.process.mocam.impl;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

public abstract class AbstractUnlockProcessor extends PublicOperationProcessor implements MocamProcessor {

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startupUnLock(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchUnLockloadApp(localTransaction);
			setPrompt(result, "建立安全通道", "20");
			break;
		case SessionStatus.UNLOCK_APP_SELECT_SD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.UNLOCK_APP_INITUPDATE_CMD);
			setPrompt(result, "建立安全通道", "40");
			break;
		case SessionStatus.UNLOCK_APP_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.UNLOCK_APP_EXTAUTH_CMD);
			setPrompt(result, "建立安全通道", "60");
			break;
		case SessionStatus.UNLOCK_APP_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchUnLockApp(localTransaction);
			setPrompt(result, "解锁中", "80");
			break;
		case SessionStatus.UNLOCK_APP_UNLOCK_CMD:// 校验外部认证指令响应,发起install
			parseUnlockAppRsp(localTransaction);
			result = MocamResult.getLastResult(localTransaction.getAid());
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	abstract protected void check(LocalTransaction localTransaction);

	abstract protected ApduCommand launchUnLock(LocalTransaction localTransaction);

	abstract protected void changeStatus(LocalTransaction localTransaction);

	protected MocamResult startupUnLock(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		return processTrans(localTransaction);
	}

	protected void parseUnlockAppRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseSetStatusRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_UNLOCK_APP_ERROR, localTransaction, ae);
		}

		changeStatus(localTransaction);
		endTransaction(localTransaction, PlatformMessage.SUCCESS);
	}

	private MocamResult launchUnLockApp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand unLockAppCmd = launchUnLock(localTransaction);
		contactApduCommand(cms2acParam, unLockAppCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.UNLOCK_APP_UNLOCK_CMD);
		result.setApduName(ApduName.Set_Status);
		return result;
	}

	private MocamResult launchUnLockloadApp(LocalTransaction localTransaction) {
		SecurityDomain sd = luanchSelectSd(localTransaction);
		MocamResult result = launchSelectSd(localTransaction, sd, SessionStatus.UNLOCK_APP_SELECT_SD_CMD);
		return result;
	}

	protected SecurityDomain luanchSelectSd(LocalTransaction localTransaction) {
		return securityDomainManager.getIsd();
	}

}
