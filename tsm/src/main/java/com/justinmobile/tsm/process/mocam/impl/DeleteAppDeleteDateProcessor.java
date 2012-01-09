package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("deleteAppDeleteDateProcessor")
public class DeleteAppDeleteDateProcessor extends AbstractDeleteAppProcessor {

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:// 开始
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupDelete(localTransaction);
			result.setProgress("正在删除");
			result.setProgress("25");
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchReadPesoDataOrStartDeleteApp(localTransaction);
			result.setProgress("正在删除");
			result.setProgress("60");
			break;
		case SessionStatus.DELETE_APP_READ_PERSO_DATA:
			result = endDelete(localTransaction);
			break;
		case SessionStatus.DELETE_APP_OPERATION_NOTIFY:
			result = launchOperationNotify(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult endDelete(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		return processTrans(localTransaction);
	}

	@Override
	protected MocamResult launchReadPesoDataOrStartDeleteApp(LocalTransaction localTransaction) {
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (CardApplication.STATUS_PERSO_DATA_READABLE.contains(cardApplication.getStatus())
				&& localTransaction.hasPersonalizationsToExecute()) {// 如果有个人化数据且业务平台需要读取，读取数据
			LocalTransaction subTransaction = buildSubTransaction(localTransaction, localTransaction.getAid(),
					Operation.DELETE_APP_READ_PERSO_DATA);

			transactionHelper.buildSubPersonalizedAppTransaction(localTransaction, subTransaction);

			subTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);
			localTransaction.setSessionStatus(SessionStatus.DELETE_APP_READ_PERSO_DATA);
		} else {
			localTransaction.setSessionStatus(SessionStatus.DELETE_APP_OPERATION_NOTIFY);
		}

		return process(localTransaction);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		super.check(localTransaction);

		// 校验卡上应用状态
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (cardApplication == null) {
			throw new PlatformException(PlatformErrorCode.CARD_APP_NOT_FOUND);
		} else if (!CardApplication.STATUS_PERSO_DATA_DELETABLE.contains(cardApplication.getStatus())) {
			throw new PlatformException(PlatformErrorCode.CARD_APP_ERROR_STATUS);
		}
	}

	@Override
	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_INSTALLED);
		return super.endSuccessProcess(localTransaction);
	}

	@Override
	protected MocamResult launchDealCurrentLoadFile(LocalTransaction localTransaction) {
		throw new PlatformException(PlatformErrorCode.UNCOMPLETED_METHOD);
	}

}
