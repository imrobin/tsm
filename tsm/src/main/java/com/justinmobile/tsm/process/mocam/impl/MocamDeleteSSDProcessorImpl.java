package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Service("mocamDeleteSdProcessor")
public class MocamDeleteSSDProcessorImpl extends PublicOperationProcessor implements MocamProcessor {

	@Override
	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupDelete(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = selectIsd(localTransaction);
			setPrompt(result, "删除安全域", "14");
			break;
		case SessionStatus.DELETE_SD_SELECT_ISD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.DELETE_SD_INITUPDATE_CMD);
			setPrompt(result, "删除安全域", "28");
			break;
		case SessionStatus.DELETE_SD_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.DELETE_SD_EXTAUTH_CMD);
			setPrompt(result, "删除安全域", "42");
			break;
		case SessionStatus.DELETE_SD_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchDeleteSd(localTransaction, SessionStatus.DELETE_SD_DELETE_CMD);
			setPrompt(result, "删除安全域", "56");
			break;
		case SessionStatus.DELETE_SD_DELETE_CMD:
			result = parseDeleteSdRsp(localTransaction);
			setPrompt(result, "删除安全域", "70");
			break;
		case SessionStatus.SYN_CAED_SPACE_START:
			parseSelectIsdRsp(localTransaction);
			result = getAppSpace(localTransaction, SessionStatus.SYN_CAED_SPACE_GET_DATA);
			setPrompt(result, "删除安全域", "84");
			break;
		case SessionStatus.SYN_CAED_SPACE_GET_DATA:
			Space space = parseGetFreeSpace(localTransaction);
			syncSpace(localTransaction, space);
			result = operationResult(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	@Override
	protected MocamResult operationResult(LocalTransaction localTransaction) {
		SecurityDomain securityDomain = securityDomainManager.getByAid(localTransaction.getAid());
		if (SecurityDomain.MODEL_TOKEN == securityDomain.getModel().intValue()) {// 如果是代理安全域，通知业务平台操作结果
			return super.operationResult(localTransaction);
		} else {// 结束流程
			localTransaction.setSessionStatus(SessionStatus.COMPLETED);
			return processTrans(localTransaction);
		}
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String sdAid = localTransaction.getAid();
		String cardNo = localTransaction.getCardNo();

		SecurityDomain sd = securityDomainManager.getByAid(sdAid);
		CardInfo card = cardInfoManager.getByCardNo(cardNo);

		validateCard(card);

		if (sd == null) {
			throw new PlatformException(PlatformErrorCode.SD_NOT_FOUND);
		}

		// 验证安全域是否能够删除
		cardSecurityDomainManager.checkDeletable(card, sd);

		// 验证安全域是否已安装
		CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(cardNo, sdAid);
		if (cardSd == null) {
			throw new PlatformException(PlatformErrorCode.CARD_SD_NOT_FOUND);
		}
		if (!CardSecurityDomain.STATUS_DELETEABLE.contains(cardSd.getStatus())) {// 如果卡上安全域记录不存在或者不是“可删除”状态，抛出异常
			throw new PlatformException(PlatformErrorCode.CARD_SD_STATUS_ERROR);
		}

		// 验证安全域下有没有未删除的应用实例
		List<CardApplication> cardApplications = cardApplicationManager.getByCardAndApplicationSd(card, sd);
		for (CardApplication cardApplication : cardApplications) {
			if (CardApplication.STATUS_UNDOWNLOAD.intValue() != cardApplication.getStatus().intValue()) {
				throw new PlatformException(PlatformErrorCode.CARD_SD_RELATING_OBJECT);
			}
		}

		// 验证安全域下有没有未删除的文件
		List<CardLoadFile> cardLoadFiles = cardLoadFileManager.getCardLoadFileBySd(sd.getId(), cardNo);
		if (CollectionUtils.isNotEmpty(cardLoadFiles)) {
			throw new PlatformException(PlatformErrorCode.CARD_SD_RELATING_OBJECT);
		}
	}

	private MocamResult synCardSpace(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.SYN_CAED_SPACE_START);
		SecurityDomain isd = securityDomainManager.getIsd();
		return launchSelectSd(localTransaction, isd, SessionStatus.SYN_CAED_SPACE_START);

	}

	private MocamResult startupDelete(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		// 校验通过正式执行
		return processTrans(localTransaction);
	}

	private MocamResult selectIsd(LocalTransaction localTransaction) {
		SecurityDomain sd = securityDomainManager.getIsd();
		return launchSelectSd(localTransaction, sd, SessionStatus.DELETE_SD_SELECT_ISD_CMD);
	}

	private MocamResult parseDeleteSdRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseDeleteRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_SD_ERROR);
		}

		// 修改卡上安全域状态
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
				localTransaction.getAid());
		cardSecurityDomain.setStatus(CardSecurityDomain.STATUS_UNCREATE);// 状态设为“未创建”
		cardSecurityDomain.setCurrentKeyVersion(null);// 当前密钥版本设置为null，表示没有密钥

		// 完成空间计算
		CardInfo card = cardSecurityDomain.getCard();
		SecurityDomain sd = cardSecurityDomain.getSd();
		card.setAvailableSpace(card.getAvailableSpace().plus(sd.getTotalSpace()));
		cardInfoManager.saveOrUpdate(card);

		// 做空间同步
		if (SystemConfigUtils.isCms2acRuntimeEnvironment()) {
			return synCardSpace(localTransaction);
		} else {
			return operationResult(localTransaction);
		}
	}

	private void parseSelectIsdRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseSelectRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.SELECT_APP_ERROR);
		}
	}

	@Override
	protected MocamResult preOperation(LocalTransaction localTransaction) {
		SecurityDomain securityDomain = securityDomainManager.getByAid(localTransaction.getAid());
		if (SecurityDomain.MODEL_TOKEN == securityDomain.getModel().intValue()) {
			return super.preOperation(localTransaction);
		} else {
			localTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);
			return processTrans(localTransaction);
		}
	}

}
