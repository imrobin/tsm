package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("deleteAppProcessor")
public class DeleteAppProcessor extends PublicOperationProcessor {

	@Autowired
	@Qualifier("deleteAppDeleteFileProcessor")
	private MocamProcessor deleteAppDeleteFileProcessor;

	@Autowired
	@Qualifier("deleteAppDeleteAppletProcessor")
	private MocamProcessor deleteAppDeleteAppletProcessor;

	@Autowired
	@Qualifier("deleteAppDeleteDateProcessor")
	private MocamProcessor deleteAppDeleteDateProcessor;

	@Override
	public MocamResult process(LocalTransaction localTransaction, ReqExecAPDU reqExecAPDU) {
		String cardNo = localTransaction.getCardNo();
		String aid = localTransaction.getAid();
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
		if (cardApplication == null) {
			throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
		}

		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		Application application = applicationManager.getByAid(localTransaction.getAid());
		int deleteRule = transactionHelper.getDeleteRule(card, application);

		MocamResult result = null;

		if (Application.DELETE_RULE_CAN_NOT == deleteRule) {
			result = deleteAppDeleteDateProcessor.process(localTransaction, reqExecAPDU);
		} else if (Application.DELETE_RULE_DELETE_DATA_ONLY == deleteRule) {
			result = deleteAppDeleteAppletProcessor.process(localTransaction, reqExecAPDU);
		} else {
			result = deleteAppDeleteFileProcessor.process(localTransaction, reqExecAPDU);
		}

		return result;
	}

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
		throw new PlatformException(PlatformErrorCode.UNCOMPLETED_METHOD);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		throw new PlatformException(PlatformErrorCode.UNCOMPLETED_METHOD);
	}

}
