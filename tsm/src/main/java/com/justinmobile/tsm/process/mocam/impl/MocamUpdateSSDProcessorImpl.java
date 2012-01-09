package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("mocamUpdateKeyProcessor")
public class MocamUpdateSSDProcessorImpl extends PublicOperationProcessor {

	private MocamResult startupUpdate(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		return processTrans(localTransaction);
	}

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startupUpdate(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchSelectSd(localTransaction, SessionStatus.UPDATE_SD_KEY_SELECT_SD_CMD);
			setPrompt(result, "密钥更新", "32");
			break;
		case SessionStatus.UPDATE_SD_KEY_SELECT_SD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.UPDATE_SD_KEY_INITUPDATE_CMD);
			setPrompt(result, "密钥更新", "48");
			break;
		case SessionStatus.UPDATE_SD_KEY_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.UPDATE_SD_KEY_EXTAUTH_CMD);
			setPrompt(result, "密钥更新", "64");
			break;
		case SessionStatus.UPDATE_SD_KEY_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchUpdateSd(localTransaction);
			setPrompt(result, "密钥更新", "80");
			break;
		case SessionStatus.UPDATE_SD_KEY_PUT_KEY_CMD:
			result = parsePutKeyRsp(localTransaction);
			setPrompt(result, "密钥更新", "96");
			break;
		case SessionStatus.UPDATE_SD_KEY_PERSO_CMD:
			result = parsePersoRsp(localTransaction);
			break;
		case SessionStatus.COMPLETED:
			result = endSuccessProcess(localTransaction);
			break;
		default:
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
		}
		return result;
	}

	private MocamResult launchUpdateSd(LocalTransaction localTransaction) {
		MocamResult result;
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
				localTransaction.getAid());
		if (CardSecurityDomain.STATUS_KEY_UPDATED == cardSecurityDomain.getStatus()) {// 已更新密钥的直接设置状态
			result = launchPersoSd(localTransaction, SessionStatus.UPDATE_SD_KEY_PERSO_CMD, false);
		} else {
			result = launchPutSdKey(localTransaction, SessionStatus.UPDATE_SD_KEY_PUT_KEY_CMD);
		}
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String sdAid = localTransaction.getAid();

		// 校验安全域
		SecurityDomain sd = securityDomainManager.getByAid(sdAid);
		if (null == sd) {
			throw new PlatformException(PlatformErrorCode.SD_NOT_FOUND);
		} else if (SecurityDomain.STATUS_PUBLISHED != sd.getStatus().intValue()) {
			throw new PlatformException(PlatformErrorCode.SD_STATUS_ERROR);
		}

		if (SecurityDomain.MODEL_TOKEN == sd.getModel().intValue()) {
			throw new PlatformException(PlatformErrorCode.TRANS_UPDATE_KEY_ERROR_MODEL);
		}

		// 校验SP
		validateSp(sd.getSp());

		// 校验卡
		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		validateCard(card);

		// 校验卡上安全域
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, sdAid);
		if (null == cardSecurityDomain) {
			throw new PlatformException(PlatformErrorCode.CARD_SD_NOT_FOUND);
		} else if (!CardSecurityDomain.STATUS_UPDATABLE.contains(cardSecurityDomain.getStatus())) {
			throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
		}

		if ((CardSecurityDomain.STATUS_KEY_UPDATED != cardSecurityDomain.getStatus().intValue())
				&& (null != cardSecurityDomain.getCurrentKeyVersion())
				&& (cardSecurityDomain.getCurrentKeyVersion().intValue() == cardSecurityDomain.getSd().getCurrentKeyVersion().intValue())) {
			throw new PlatformException(PlatformErrorCode.APDU_UPDATEED_KEY);
		}

	}

	protected MocamResult launchSelectSd(LocalTransaction localTransaction, int sessionStatus) {
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
				localTransaction.getAid());

		Cms2acParam cms2acParam = null;
		if (CardSecurityDomain.STATUS_PERSO == cardSecurityDomain.getStatus()) {// 如果卡上安全域状态为“已个人化”，使用自己的密钥进行安全域通信
			cms2acParam = buildCms2acParam(localTransaction, securityDomainManager.getByAid(localTransaction.getAid()));
		} else {// 如果卡上安全域状态不为“已个人化”，使用主安全域的密钥进行安全域通信
			cms2acParam = buildCms2acParam(localTransaction, securityDomainManager.getIsd());
		}

		ApduCommand selectSdCmd = apduEngine.buildSelectCmd(cms2acParam, localTransaction.getAid());
		contactApduCommand(cms2acParam, selectSdCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Select);
		return result;
	}

	private MocamResult parsePutKeyRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parsePutKeyRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_UPDATE_KEY_ERROR, ae);
		}

		CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		cardSd.setCurrentKeyVersion(cardSd.getSd().getCurrentKeyVersion());
		cardSecurityDomainManager.saveOrUpdate(cardSd);

		if (CardSecurityDomain.STATUS_PERSO == cardSd.getStatus().intValue()) {// 如果卡上安全域已经是“已个人化”状态，流程结束
			localTransaction.setSessionStatus(SessionStatus.COMPLETED);
			return processTrans(localTransaction);
		} else {// 如果卡上安全域已经不是“已个人化”状态，下发set status指令
			changeCardSecurityDomainStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardSecurityDomain.STATUS_KEY_UPDATED);
			return launchPersoSd(localTransaction, SessionStatus.UPDATE_SD_KEY_PERSO_CMD, false);
		}
	}

	private MocamResult parsePersoRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseSetStatusRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_PERSO_SD_ERROR, ae);
		}
		changeCardSecurityDomainStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardSecurityDomain.STATUS_PERSO);

		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		return processTrans(localTransaction);
	}

	protected MocamResult launchPersoSd(LocalTransaction localTransaction, int sessionStatus, boolean isLoadFileSd) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand persoSdCmd = apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x0F, isLoadFileSd);
		contactApduCommand(cms2acParam, persoSdCmd);

		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Set_Status);
		return result;
	}

}
