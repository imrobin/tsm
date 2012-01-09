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

@Service("mocamUnlockAppProcessor")
public class MocamUnLockAppProcessorImpl extends PublicOperationProcessor implements MocamProcessor {

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startupUnLock(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchUnLockloadApp(localTransaction);
			setPrompt(result, "解锁应用", "20");
			break;
		case SessionStatus.UNLOCK_APP_SELECT_SD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.UNLOCK_APP_INITUPDATE_CMD);
			setPrompt(result, "解锁应用", "40");
			break;
		case SessionStatus.UNLOCK_APP_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.UNLOCK_APP_EXTAUTH_CMD);
			setPrompt(result, "解锁应用", "60");
			break;
		case SessionStatus.UNLOCK_APP_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchUnLockApp(localTransaction);
			setPrompt(result, "解锁应用", "80");
			break;
		case SessionStatus.UNLOCK_APP_UNLOCK_CMD:// 校验外部认证指令响应,发起install
			parseLockAppRsp(localTransaction);
			result = MocamResult.getLastResult(localTransaction.getAid());
			break;
		default:
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
		}
		return result;
	}

	private MocamResult startupUnLock(LocalTransaction localTransaction) {
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
			throw new PlatformException(PlatformErrorCode.APDU_UNLOCK_APP_ERROR, localTransaction, ae);
		}
		String cardNo = localTransaction.getCardNo();
		String aid = localTransaction.getAid();
		if (Operation.UNLOCK_APP.name().equals(localTransaction.getProcedureName())) {
			changeCardApplicationStatus(cardNo, aid, CardApplication.STATUS_AVAILABLE);
		} else if (Operation.UNLOCK_SD.name().equals(localTransaction.getProcedureName())) {
			CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, aid);
			changeCardSecurityDomainStatus(cardNo, aid, cardSecurityDomain.getOrginalStatus());
		}
		super.endTransaction(localTransaction, PlatformMessage.SUCCESS);
	}

	private MocamResult launchUnLockApp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand unLockAppCmd = null;
		if ((LocalTransaction.Operation.UNLOCK_SD == Operation.valueOf(localTransaction.getProcedureName()))
				&& (securityDomainManager.getByAid(localTransaction.getAid()).isIsd())) {
			unLockAppCmd = apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x0F, false);
		} else {
			unLockAppCmd = apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x07, false);
		}
		contactApduCommand(cms2acParam, unLockAppCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.UNLOCK_APP_UNLOCK_CMD);
		result.setApduName(ApduName.Set_Status);
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		CardInfo card = cardInfoManager.getByCardNo(cardNo);

		// 验证应用
		Application app = applicationManager.getByAid(localTransaction.getAid());
		if (app == null) {

			// 校验是否是删除安全域
			SecurityDomain sd = securityDomainManager.getByAid(localTransaction.getAid());
			if (sd == null) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_NOT_EXIST, localTransaction);
			} else {
				if (sd.isIsd()) {
					if (CardInfo.STATUS_DISABLE.equals(card.getStatus())) {// 如果卡状态为“不可用”，抛出异常
						throw new PlatformException(PlatformErrorCode.TRANS_CARD_DISABLE);
					}
				} else {
					validateCard(card);
				}

				validateSp(sd.getSp());

				CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(cardNo, sd.getAid());
				if (cardSd != null) {
					if (CardSecurityDomain.STATUS_LOCK != cardSd.getStatus()) {
						throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
					}
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
				}
			}
		} else {
			// 校验卡
			validateCard(card);
			validateSp(app.getSp());

			// 校验卡上应用
			CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, localTransaction.getAid());
			if (null != cardApp) {
				if (!CardApplication.STATUS_UNLOCKABLE.contains(cardApp.getStatus())) {// 最新版本曾经下载且未完全删除，检查当前应用卡上状态是否是可下载状态
					throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
				}
			} else {
				throw new PlatformException(PlatformErrorCode.APP_NEVER_DOWNLOADED, localTransaction);
			}

			localTransaction.setAppVersion(cardApp.getApplicationVersion().getVersionNo());

		}
	}

	private MocamResult launchUnLockloadApp(LocalTransaction localTransaction) {
		SecurityDomain sd = securityDomainManager.getIsd();
		MocamResult result = launchSelectSd(localTransaction, sd, SessionStatus.UNLOCK_APP_SELECT_SD_CMD);
		return result;
	}

}
