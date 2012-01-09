package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("lockAppProcessor")
public class LockAppProcessorImpl extends AbstractLockProcessor implements MocamProcessor {

	@Override
	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupLock(localTransaction);
			break;
		case SessionStatus.LOCK_APP_LOCK_CMD:// 校验set status，通知业务平台
			parseLockAppRsp(localTransaction);
			result = operationResult(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		CardInfo card = cardInfoManager.getByCardNo(cardNo);

		// 校验卡
		validateCard(card);

		// 验证应用
		Application app = applicationManager.getByAid(localTransaction.getAid());
		if (app == null) {
			throw new PlatformException(PlatformErrorCode.TRANS_APP_AID_NOT_FOUND);
		}

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

	@Override
	protected void changeStatus(LocalTransaction localTransaction) {
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_LOCKED);
	}

	@Override
	protected ApduCommand launchLockCmd(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		return apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x87, false);
	}

}
