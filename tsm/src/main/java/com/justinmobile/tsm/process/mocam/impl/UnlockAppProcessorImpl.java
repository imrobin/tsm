package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("unlockAppProcessor")
public class UnlockAppProcessorImpl extends AbstractUnlockProcessor {
	
	@Override
	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = preOperation(localTransaction);
			break;
		case SessionStatus.UNLOCK_APP_UNLOCK_CMD:// 校验set status，通知业务平台
			parseUnlockAppRsp(localTransaction);
			result = operationResult(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupUnLock(localTransaction);
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

		// 验证卡
		validateCard(card);

		// 验证应用
		Application app = applicationManager.getByAid(localTransaction.getAid());
		if (app == null) {
			throw new PlatformException(PlatformErrorCode.TRANS_APP_AID_NOT_FOUND);
		}

		// 校验卡上应用
		CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, localTransaction.getAid());
		if (null != cardApp) {
			if (!CardApplication.STATUS_UNLOCKABLE.contains(cardApp.getStatus())) {// 最新版本曾经下载且未完全删除，检查当前应用卡上状态是否是可下载状态
				throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
			}
		} else {
			throw new PlatformException(PlatformErrorCode.APP_NEVER_DOWNLOADED, localTransaction);
		}

		if (!cardApp.getCardInfo().isTestCard()) {
			validate(cardApp.getApplicationVersion());
		}

		localTransaction.setAppVersion(cardApp.getApplicationVersion().getVersionNo());

	}

	@Override
	protected void changeStatus(LocalTransaction localTransaction) {
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_AVAILABLE);
	}

	@Override
	protected ApduCommand launchUnLock(LocalTransaction localTransaction) {
		return apduEngine.buildSetStatusCmd(localTransaction.getLastCms2acParam(), (byte) 0x07, false);
	}

}
