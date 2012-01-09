package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("emigrateAppProcessor")
public class EmigrateAppProcessorImpl extends PublicOperationProcessor implements MocamProcessor {

	@Override
	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startup(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchEmigrate(localTransaction);
			break;
		case SessionStatus.COMPLETED:
			result = endSuccessProcess(localTransaction);
			break;
		default:
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
		}
		return result;
	}

	private MocamResult launchEmigrate(LocalTransaction localTransaction) {
		// 组建删除应用的子流程
		buildSubTransaction(localTransaction, localTransaction.getAid(), Operation.DELETE_APP);

		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		localTransactionManager.saveOrUpdate(localTransaction);

		// 开始执行删除应用的子流程
		return process(localTransaction);
	}

	protected MocamResult startup(LocalTransaction localTransaction) {
		check(localTransaction);

		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);

		return processTrans(localTransaction);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (cardApplication == null) {
			throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
		} else if (!cardApplication.getMigratable()) {
			throw new PlatformException(PlatformErrorCode.TRANS_MIGRATE_UNEMIGRATE);
		}

	}
}
