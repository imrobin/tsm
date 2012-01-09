package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("lockCardProcessor")
public class LockCardProcessorImpl extends LockSdProcessorImpl implements MocamProcessor {

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		CardInfo card = cardInfoManager.getByCardNo(cardNo);

		// 校验卡
		CustomerCardInfo customerCard = customerCardInfoManager.getByCardThatStatusNotCanclledOrNotReplaced(card);
		if (null == customerCard || CustomerCardInfo.STATUS_LOST != customerCard.getStatus()) {
			throw new PlatformException(PlatformErrorCode.TRANS_CARD_NOT_LOST);
		}

		// 校验是安全域
		SecurityDomain isd = securityDomainManager.getIsd();
		if (isd == null) {
			throw new PlatformException(PlatformErrorCode.APPLICAION_AID_NOT_EXIST, localTransaction);
		}

		// 判断卡上安全域状态
		CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(cardNo, isd.getAid());
		if (cardSd == null || CardSecurityDomain.STATUS_PERSO != cardSd.getStatus().intValue()) {// 如果卡上安全域记录不存在或者不是“已个人化”状态，抛出异常
			throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
		}

		localTransaction.setAid(isd.getAid());
	}

	@Override
	protected ApduCommand launchLockCmd(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		return apduEngine.buildSetStatusCmd(cms2acParam, (byte) 0x7F, false);
	}
}
