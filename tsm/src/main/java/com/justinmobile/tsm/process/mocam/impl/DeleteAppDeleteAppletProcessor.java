package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
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

@Service("deleteAppDeleteAppletProcessor")
public class DeleteAppDeleteAppletProcessor extends AbstractDeleteAppProcessor {

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
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:// 选择所属安全域
			result = launchReadPesoDataOrStartDeleteApp(localTransaction);
			break;
		case SessionStatus.DELETE_APP_READ_PERSO_DATA:
			result = startDelApp(localTransaction);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_SELECT_SD_CMD:// 验证选择指令的响应，发送建立安全通道指令
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.DELETE_APP_INITUPDATE_CMD);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_INITUPDATE_CMD:// 验证建立安全通道指令的响应，发送外部认证指令
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.DELETE_APP_EXTAUTH_CMD);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_EXTAUTH_CMD: // 验证外部认证指令的响应，发送删除应用指令
			parseExtAuthSdRsp(localTransaction);
			result = launchDeleteApplet(localTransaction);// 处理当前文件的实例
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_DELETE_APPLET_CMD:// 验证删除实例指令的响应
			result = parseDeleteAppletRsp(localTransaction);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_END_DELETE_LOAD_FILE:// 验证删除实例指令的响应
			result = launchDeleteAppSdOrNext(localTransaction);
			break;
		case SessionStatus.SYN_CAED_SPACE_START:
			result = synCardSpace(localTransaction);
			result.setProgress("同步数据");
			result.setProgressPercent("90");
			break;
		case SessionStatus.SYNC_CARD_SELECT_ISD_CMD: // 已选择安全域，开始获取卡片空间
			parseSelectSdRsp(localTransaction);
			result = getAppSpace(localTransaction, SessionStatus.SYN_CAED_SPACE_GET_DATA);
			result.setProgress("同步数据");
			result.setProgressPercent("95");
			break;
		case SessionStatus.SYN_CAED_SPACE_GET_DATA:// 获取卡片空间，同步通知ota3平台
			Space cardSpace = parseGetFreeSpace(localTransaction);
			syncSpace(localTransaction, cardSpace);
			result = launchOperationNotify(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	protected MocamResult launchDeleteAppSdOrNext(LocalTransaction localTransaction) {
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		cardApplication.setSpaceInfo(cardApplication.getApplicationVersion().getLoadFileSpaceInfo());// 实例删除完成后，将应用占用空间改为文件所需共建

		return super.launchDeleteAppSdOrNext(localTransaction, SessionStatus.SYN_CAED_SPACE_START);
	}

	// TODO delete
	@Override
	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_DOWNLOADED);
		return super.endSuccessProcess(localTransaction);
	}

	private MocamResult launchDeleteApplet(LocalTransaction localTransaction) {
		MocamResult result = null;
		if (transactionHelper.hasAppletToDeleteFromCurrentLoadFile(localTransaction)) {// 如果当前加载文件还有实例需要删除
			Applet applet = transactionHelper.getNextAppletToDeleltFromCurrentLoadFile(localTransaction);
			localTransaction.setCurrentAppletIndex(applet.getId());

			Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

			ApduCommand deleteCmd = apduEngine.buildDeleteCmd(cms2acParam, ApduEngine.DELETE_CMD_DATA_TYPE_SD_APP, false);

			contactApduCommand(cms2acParam, deleteCmd);
			result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.DELETE_APP_DELETE_APPLET_CMD);
			result.setApduName(ApduName.Delete);
		} else {// 如果当前加载文件已经没有实例需要删除，进行下一操作
			result = launchDealNextLoadFile(localTransaction);
		}
		return result;
	}

	@Override
	protected MocamResult launchDealCurrentLoadFile(LocalTransaction localTransaction) {
		if (transactionHelper.hasAppletToDeleteFromCurrentLoadFile(localTransaction)) {
			Application application = applicationManager.getByAid(localTransaction.getAid());
			SecurityDomain sd = application.getSd();
			return launchSelectSd(localTransaction, sd);
		} else {
			return launchDealNextLoadFile(localTransaction);
		}
	}

	private MocamResult parseDeleteAppletRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		boolean result = false;
		try {
			result = apduEngine.parseDeleteRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_APP_ERROR, ae);
		}
		if (result) {
			// 修改卡上应用状态
			String appAid = localTransaction.getAid();
			String cardNo = localTransaction.getCardNo();
			changeCardApplicationStatus(cardNo, appAid, CardApplication.STATUS_DELETEING);

			// 删除卡上实例记录
			Applet applet = transactionHelper.getCurrentApplet(localTransaction);
			CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
			CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getbySdAndCard(card, applet.getSd());
			Space aviliableSpace = cardSecurityDomain.getAviliableSpace();
			aviliableSpace = aviliableSpace.plus(applet.getSpaceInfo());
			cardSecurityDomain.setAviliableSpace(aviliableSpace);

			CardApplet cardApplet = cardAppletManager.getByCardNoAndAppletAid(cardNo, applet.getAid());
			if (cardApplet != null) {
				cardAppletManager.remove(cardApplet);
			}

			// 删除下一实例或处理下一文件
			return launchDeleteApplet(localTransaction);
		} else {
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_APP_ERROR);
		}
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		super.check(localTransaction);

		// 校验卡上应用状态
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (cardApplication == null || !CardApplication.STATUS_APPLET_DELETEABLE.contains(cardApplication.getStatus())) {
			throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
		}
	}
}
