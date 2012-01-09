package com.justinmobile.tsm.process.mocam.impl;

import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultMessage;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("readPersoDataProcessor")
public class ReadPersoDataProcessor extends PersonalizeAppPassthonghProcessor {

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;

		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = preOperation(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}

		return result;
	}

	@Override
	protected OperationResultMessage buildOperationResultMessage(LocalTransaction localTransaction) {
		OperationResultMessage message = super.buildOperationResultMessage(localTransaction);
		message.setReslutCode(ResourceBundle.getBundle("config/systemConfig").getString("result.code.read"));
		return message;
	}

}
