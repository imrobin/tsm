package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.engine.ApduEngine;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("deleteAppletProcessor")
public class DeleteAppletProcessor extends PublicOperationProcessor {

	@Override
	protected void check(LocalTransaction localTransaction) {
		// TODO Auto-generated method stub

	}

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:// 开始下载的准备工作
			result = launchSelectSd(localTransaction, securityDomainManager.getIsd(), SessionStatus.DELETE_APPLET_SELECT_CMD);
			break;
		case SessionStatus.DELETE_APPLET_SELECT_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.DELETE_APPLET_INIT_UPDATE_CMD);
			break;
		case SessionStatus.DELETE_APPLET_INIT_UPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.DELETE_APPLET_EXT_AUTH_CMD);
			break;
		case SessionStatus.DELETE_APPLET_EXT_AUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchDeleteApplet(localTransaction, SessionStatus.DELETE_APPLET_DELETE);
			break;
		case SessionStatus.DELETE_APPLET_DELETE:
			parseDeleteRsp(localTransaction);
			result = endSuccessProcess(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private void parseDeleteRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseDeleteRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_APP_ERROR, ae);
		}

		CardApplet cardApplet = cardAppletManager.getByCardNoAndAppletAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (null != cardApplet) {
			CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
			Applet applet = cardApplet.getApplet();

			CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getbySdAndCard(card, applet.getSd());
			Space aviliableSpace = cardSecurityDomain.getAviliableSpace();
			aviliableSpace = aviliableSpace.plus(aviliableSpace);
			cardSecurityDomain.setAviliableSpace(aviliableSpace);

			cardAppletManager.remove(cardApplet);
		}
	}

	private MocamResult launchDeleteApplet(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand cmd = apduEngine.buildDeleteCmd(cms2acParam, ApduEngine.DELETE_CMD_DATA_TYPE_SD_APP, false);

		contactApduCommand(cms2acParam, cmd);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Delete);

		return result;
	}
}
