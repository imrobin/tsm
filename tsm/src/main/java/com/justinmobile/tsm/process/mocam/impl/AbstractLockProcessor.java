package com.justinmobile.tsm.process.mocam.impl;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

abstract public class AbstractLockProcessor extends PublicOperationProcessor {

	@Override
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
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	abstract protected void check(LocalTransaction localTransaction);

	abstract protected void changeStatus(LocalTransaction localTransaction);

	abstract protected ApduCommand launchLockCmd(LocalTransaction localTransaction);

	protected MocamResult startupLock(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		return processTrans(localTransaction);
	}

	protected void parseLockAppRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseSetStatusRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_LOCK_APP_ERROR, localTransaction, ae);
		}

		changeStatus(localTransaction);
		endTransaction(localTransaction, PlatformMessage.SUCCESS);
	}

	private MocamResult launchLockApp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand lockAppCmd = launchLockCmd(localTransaction);
		contactApduCommand(cms2acParam, lockAppCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.LOCK_APP_LOCK_CMD);
		result.setApduName(ApduName.Set_Status);
		return result;
	}

	private MocamResult launchLockloadApp(LocalTransaction localTransaction) {
		SecurityDomain sd = luanchSelectSd(localTransaction);
		MocamResult result = launchSelectSd(localTransaction, sd, SessionStatus.LOCK_APP_SELECT_SD_CMD);
		return result;
	}

	protected SecurityDomain luanchSelectSd(LocalTransaction localTransaction) {
		return securityDomainManager.getIsd();
	}

}
