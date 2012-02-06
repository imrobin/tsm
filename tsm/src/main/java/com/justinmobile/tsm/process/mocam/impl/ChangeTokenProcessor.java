package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
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

@Service("notifyImsiProcessor")
public class ChangeTokenProcessor extends PublicOperationProcessor {

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = startup(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = launchSelectSd(localTransaction, securityDomainManager.getIsd(), SessionStatus.REG_SELECT_ISD);
			result.setProgress("选择安全域");
			result.setProgressPercent("25");
			break;
		case SessionStatus.REG_SELECT_ISD:
			parseSelectAppRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.REG_INIT_UPDATE);
			result.setProgress("建立安全通道");
			result.setProgressPercent("45");
			break;
		case SessionStatus.REG_INIT_UPDATE:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.REG_EXT_AUTH);
			result.setProgress("建立安全通道");
			result.setProgressPercent("65");
			break;
		case SessionStatus.REG_EXT_AUTH:
			parseExtAuthSdRsp(localTransaction);
			result = launchWriteToken(localTransaction);
			result.setProgress("写入TOKEN");
			result.setProgressPercent("85");
			break;
		case SessionStatus.REG_WRITE_TOEKN:
			parseWriteToken(localTransaction);
			result = launchNextOperation(localTransaction);
			result.setProgress("操作完成");
			result.setProgressPercent("100");
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult launchNextOperation(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		return processTrans(localTransaction);
	}

	private void parseWriteToken(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseRspWithSecurity(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_WRITE_TOKEN_ERROR, ae);
		}
	
	}

	private MocamResult launchWriteToken(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		List<ApduCommand> apdus = apduEngine.buildWriteTokenCmd(cms2acParam);

		serializeApduCmdBatch(cms2acParam, apdus, Constants.MOCAM_DATA_MAX_LENGTH);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, apdus, SessionStatus.REG_WRITE_TOEKN);
		result.setApduName(ApduName.Load);
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		if (null == card.getRegisterable() && CardInfo.REGISTERABLE_CHANGE_SIM.intValue() != card.getRegisterable().intValue()) {// 如果没有收到注册短信，抛出异常
			throw new PlatformException(PlatformErrorCode.TRANS_REG_REFUSE);
		}
		CustomerCardInfo cci = customerCardInfoManager.getByCardNo(localTransaction.getCardNo());
		if(null!=cci && !cci.getCustomer().getSysUser().getMobile().equals(localTransaction.getMobileNo())){
			throw new PlatformException(PlatformErrorCode.CARD_MOBILE_NO_NOT_MATCH);
		}
	}
}
