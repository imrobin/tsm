package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("immigrateAppProcessor")
public class ImmigrateAppProcessorImpl extends MocamDownloadAppProcessorImpl implements MocamProcessor {

	@Override
	protected void check(LocalTransaction localTransaction) {
		CardApplication originalCardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getOriginalCardNo(),
				localTransaction.getAid());
		if (null == originalCardApplication || null == originalCardApplication.getMigratable() || !originalCardApplication.getMigratable()) {
			throw new PlatformException(PlatformErrorCode.TRANS_MIGRATE_UNEMIGRATE);
		}

		super.check(localTransaction);
	}

	@Override
	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		CardApplication originalCardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getOriginalCardNo(),
				localTransaction.getAid());
		originalCardApplication.setMigratable(Boolean.FALSE);
		cardApplicationManager.saveOrUpdate(originalCardApplication);
		return super.endSuccessProcess(localTransaction);
	}

}
