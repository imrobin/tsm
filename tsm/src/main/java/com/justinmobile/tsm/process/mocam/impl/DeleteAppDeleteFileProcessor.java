package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.engine.ApduEngine;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("deleteAppDeleteFileProcessor")
public class DeleteAppDeleteFileProcessor extends AbstractDeleteAppProcessor {

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
			result = launchDeleteLoadFile(localTransaction);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_DELETE_LOAD_FILE_CMD:// 验证删除文件指令的响应
			result = parseDeleteLoadFileRsp(localTransaction);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_END_DELETE_LF_SD:// 完成自动删除文件所属安全域
			result = launchDealNextLoadFile(localTransaction);
			result.setProgress("正在删除");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DELETE_APP_END_DELETE_LOAD_FILE:
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
		case SessionStatus.SYN_CAED_SPACE_GET_DATA: {// 获取卡片空间，同步平台记录
			// 解析卡上空间
			Space cardSpace = parseGetFreeSpace(localTransaction);
			// 同步
			syncSpace(localTransaction, cardSpace);
			result = launchOperationNotify(localTransaction);
			break;
		}
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	protected MocamResult launchDeleteAppSdOrNext(LocalTransaction localTransaction) {
		return super.launchDeleteAppSdOrNext(localTransaction, SessionStatus.SYN_CAED_SPACE_START);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		super.check(localTransaction);

		// 校验卡上应用状态
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (cardApplication == null || !CardApplication.STATUS_FILE_DELETEABLE.contains(cardApplication.getStatus())) {
			throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
		}
	}

	private MocamResult launchDeleteLoadFile(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand deleteCmd = apduEngine.buildDeleteCmd(cms2acParam, ApduEngine.DELETE_CMD_DATA_TYPE_LOAD_FILE, false);

		contactApduCommand(cms2acParam, deleteCmd);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.DELETE_APP_DELETE_LOAD_FILE_CMD);
		result.setApduName(ApduName.Delete);
		return result;
	}

	private MocamResult parseDeleteLoadFileRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		boolean result = false;
		try {
			result = apduEngine.parseDeleteRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_APP_ERROR, ae);
		}

		if (result) {// 如果指令执行成功，修改数据库记录，然后进行下一操作
			// 修改卡上应用状态
			String appAid = localTransaction.getAid();
			String cardNo = localTransaction.getCardNo();
			changeCardApplicationStatus(cardNo, appAid, CardApplication.STATUS_DELETEING);

			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDelete(localTransaction);

			// 删除卡上加载文件记录
			CardLoadFile cardLoadFile = cardLoadFileManager.getByCardAndLoadFileVersion(card, loadFileVersion);
			if (cardLoadFile != null) {
				cardLoadFileManager.remove(cardLoadFile);
			}
			// 完成空间计算
			{
				CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getbySdAndCard(card, loadFileVersion.getSd());
				Space aviliableSpace = cardSecurityDomain.getAviliableSpace();
				aviliableSpace = aviliableSpace.plus(loadFileVersion.getSpaceInfo());
				cardSecurityDomain.setAviliableSpace(aviliableSpace);
			}

			// 删除卡上实例记录
			List<CardApplet> cardApplets = cardAppletManager.getByCardNoThatCreateLoadFileVersion(cardNo, loadFileVersion);
			for (CardApplet cardApplet : cardApplets) {
				// 完成实例的空间计算
				Applet applet = cardApplet.getApplet();
				CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getbySdAndCard(card, applet.getSd());
				Space aviliableSpace = cardSecurityDomain.getAviliableSpace();
				aviliableSpace = aviliableSpace.plus(applet.getSpaceInfo());
				cardSecurityDomain.setAviliableSpace(aviliableSpace);

				cardAppletManager.remove(cardApplet);
			}

			SecurityDomain sd = loadFileVersion.getLoadFile().getSd();
			if (transactionHelper.isSdNeedAutoDelete(card, sd)) {// 如果需要删除安全域，删除安全域
				buildSubTransaction(localTransaction, sd.getAid(), null, Operation.DELETE_SD);
				localTransaction.setSessionStatus(SessionStatus.DELETE_APP_END_DELETE_LF_SD);
				return process(localTransaction);
			} else {// 如果不需要删除安全域，执行下一删除操作
				return launchDealNextLoadFile(localTransaction);
			}
		} else {// 如果指令执行失败，抛出异常
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_APP_ERROR);
		}
	}

	@Override
	protected MocamResult launchDealCurrentLoadFile(LocalTransaction localTransaction) {
		// 查找所需要的安全域
		SecurityDomain sd = transactionHelper.getCurrentLoadFileVersionToDelete(localTransaction).getLoadFile().getSd();

		return launchSelectSd(localTransaction, sd);
	}

	@Override
	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_UNDOWNLOAD);
		return super.endSuccessProcess(localTransaction);
	}
}
