package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("replaceMobileNoProcessor")
public class ReplaceMobileNoProcessor extends PublicOperationProcessor {

	protected MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startupChange(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchChange(localTransaction);
			break;
		case SessionStatus.REPLACE_MOBILE_NO_NOTIFIED:
			result = launchSelect(localTransaction);
			break;
		case SessionStatus.REPLACE_MOBILE_NO_SELECT:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.REPLACE_MOBILE_NO_INIT_UPDATE);
			break;
		case SessionStatus.REPLACE_MOBILE_NO_INIT_UPDATE:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.REPLACE_MOBILE_NO_EXT_AUTH);
			break;
		case SessionStatus.REPLACE_MOBILE_NO_EXT_AUTH:
			parseExtAuthSdRsp(localTransaction);
			result = launchStoreMobileNo(localTransaction);
			break;
		case SessionStatus.REPLACE_MOBILE_NO_STORE_MOBILE_NO:
			parseStoreMobileNo(localTransaction);
			result = launchChangeBind(localTransaction);
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult launchChangeBind(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String mobileNo = localTransaction.getMobileNo();
		customerCardInfoManager.finashCancel(customerCardInfoManager.getByCardNo(cardNo).getId());
		customerCardInfoManager.bindCardAsActivedAndCreatCustomerIfNeed(mobileNo, cardNo);

		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		return processTrans(localTransaction);
	}

	private void parseStoreMobileNo(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseRspWithSecurity(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_STORE_MOBILE_NO_ERROR, ae);
		}
	}

	private MocamResult launchStoreMobileNo(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		List<ApduCommand> apdus = apduEngine.buildStoreMobileNo(cms2acParam);

		serializeApduCmdBatch(cms2acParam, apdus, Constants.MOCAM_DATA_MAX_LENGTH);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, apdus, SessionStatus.REPLACE_MOBILE_NO_STORE_MOBILE_NO);
		result.setApduName(ApduName.Load);
		return result;
	}

	private MocamResult launchSelect(LocalTransaction localTransaction) {
		return launchSelectSd(localTransaction, securityDomainManager.getIsd(), SessionStatus.REPLACE_MOBILE_NO_SELECT);
	}

	private MocamResult launchChange(LocalTransaction localTransaction) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		List<CardApplication> cardApplications = cardApplicationManager.getByCardAndStatus(card, CardApplication.STATUS_AVAILABLE);

		for (CardApplication cardApplication : cardApplications) {
			LocalTransaction subTransaction = buildSubTransaction(localTransaction, cardApplication.getApplicationVersion()
					.getApplication().getAid(), Operation.NOTIFY_REPLACE_MOBILE_NO);
			subTransaction.setMobileNo(localTransaction.getMobileNo());
		}

		localTransaction.setSessionStatus(SessionStatus.REPLACE_MOBILE_NO_NOTIFIED);
		return process(localTransaction);
	}

	private MocamResult startupChange(LocalTransaction localTransaction) {
		check(localTransaction);

		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		return processTrans(localTransaction);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();

		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		// 校验卡
		validateCard(card);

		CustomerCardInfo customerCard = customerCardInfoManager.getByCardNo(cardNo);
		String newMobileNo = localTransaction.getMobileNo();
		if (StringUtils.isBlank(newMobileNo)) {
			throw new PlatformException(PlatformErrorCode.TRANS_REPLACE_INVALID_MOBILE_NO);
		} else if (newMobileNo.equals(customerCard.getMobileNo())) {
			throw new PlatformException(PlatformErrorCode.TRANS_REPLACE_IDENTICAL_MOBILE_NO);
		}
	}
}
