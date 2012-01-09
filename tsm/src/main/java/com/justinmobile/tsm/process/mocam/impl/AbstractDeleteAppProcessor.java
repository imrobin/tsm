package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardClient;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.utils.SystemConfigUtils;

public abstract class AbstractDeleteAppProcessor extends PublicOperationProcessor {

	protected MocamResult launchReadPesoDataOrStartDeleteApp(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.DELETE_APP_READ_PERSO_DATA);

		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (CardApplication.STATUS_PERSO_DATA_READABLE.contains(cardApplication.getStatus())
				&& localTransaction.hasPersonalizationsToExecute()) {// 如果有个人化数据且业务平台需要读取，读取数据
			LocalTransaction subTransaction = buildSubTransaction(localTransaction, localTransaction.getAid(),
					Operation.DELETE_APP_READ_PERSO_DATA);

			transactionHelper.buildSubPersonalizedAppTransaction(localTransaction, subTransaction);

			subTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);
		}

		return process(localTransaction);
	}

	protected MocamResult startupDelete(LocalTransaction localTransaction) {
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);
		return processTrans(localTransaction);
	}

	protected boolean isAllLoadFileDelete(LocalTransaction localTransaction) {
		List<ApplicationLoadFile> deleteOrder = transactionHelper.getDeleteOrder(localTransaction);
		return localTransaction.getCurrentLoadFileIndex() == deleteOrder.size() + 1;
	}

	protected MocamResult startDelApp(LocalTransaction localTransaction) {
		MocamResult result = new MocamResult();
		if (isAllLoadFileDelete(localTransaction)) {// 所有文件都删除，进入后续流程
			localTransaction.setSessionStatus(SessionStatus.DELETE_APP_END_DELETE_LOAD_FILE);
			result = processTrans(localTransaction);
		} else {
			CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
			LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDelete(localTransaction);
			if (transactionHelper.isLoadFileVersionExistOnCard(card, loadFileVersion)) {// 当前加载文件在卡上
				return launchDealCurrentLoadFile(localTransaction);
			} else {// 当前加载文件没在卡上，删除下一个文件
				return launchDealNextLoadFile(localTransaction);
			}
		}
		return result;
	}

	abstract protected MocamResult launchDealCurrentLoadFile(LocalTransaction localTransaction);

	protected MocamResult launchSelectSd(LocalTransaction localTransaction, SecurityDomain sd) {
		if ((SecurityDomain.MODEL_ISD == sd.getModel().intValue()) || (SecurityDomain.MODEL_COMMON == sd.getModel())) {
			sd = securityDomainManager.getIsd();
		}

		return launchSelectSd(localTransaction, sd, SessionStatus.DELETE_APP_SELECT_SD_CMD);
	}

	protected String calcLoadPercent(LocalTransaction localTransaction) {
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());
		int totalCount = applicationVersion.getApplicationLoadFiles().size();
		int currentIndex = localTransaction.getCurrentLoadFileIndex();

		Integer percent = 5 + (int) (((new Double(currentIndex - 1) / totalCount) * 0.85) * 100);

		return percent.toString();
	}

	protected MocamResult launchDealNextLoadFile(LocalTransaction localTransaction) {
		// 当前处理的加载文件索引值+1
		localTransaction.increaseCurrentLoadFileIndex();
		localTransactionManager.saveOrUpdate(localTransaction);

		// 处理当前的加载文件
		return startDelApp(localTransaction);
	}

	protected MocamResult launchDeleteAppSdOrNext(LocalTransaction localTransaction, int sessionStatus) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		Application application = applicationManager.getByAid(localTransaction.getAid());
		SecurityDomain sd = application.getSd();
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());

		MocamResult result;

		Integer currentStatus = cardApplication.getStatus();// 保存当前卡上应用状态
		cardApplication.setStatus(CardApplication.STATUS_UNDOWNLOAD);// 将卡上应用状态设置为“未下载”，防止干扰判断应用所属安全域是否自动删除

		if (transactionHelper.isSdNeedAutoDelete(card, sd)) {// 如果需要删除应用所属安全域，执行删除安全域子流程
			buildSubTransaction(localTransaction, sd.getAid(), null, Operation.DELETE_SD);
			localTransaction.setSessionStatus(sessionStatus);
			result = process(localTransaction);
			cardApplication.setStatus(currentStatus);// 恢复卡上应用状态
		} else {// 否则同步卡空间
			cardApplication.setStatus(currentStatus);// 如果当前运行环境为cms2ac，同步安全域的空间信息
			if (SystemConfigUtils.isCms2acRuntimeEnvironment()) {//
				localTransaction.setSessionStatus(SessionStatus.SYN_CAED_SPACE_START);
				result = processTrans(localTransaction);
			} else {// 如果当前运行环境不是cms2ac，通知业务平台操作结果
				result = launchOperationNotify(localTransaction);
			}
		}

		return result;
	}

	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		ApplicationVersion applicationVersion = applicationVersionManager.getByAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());

		if (null == applicationVersion.getApplication().getNeedSubscribe() || !applicationVersion.getApplication().getNeedSubscribe()) {// 如果应用不需要单独订购/退订，则删除使用即为退订，添加退订记录
			subscribeHistoryManager.unsubscribeApplication(card, applicationVersion);
		}

		// 删除终端上客户端记录
		List<CardClient> cardClients = cardClientManager.getByCardAndApplication(card, applicationVersion.getApplication());
		if (null != cardClients) {
			for (CardClient cardClient : cardClients){
				cardClientManager.remove(cardClient);
			}
		}

		return super.endSuccessProcess(localTransaction);
	}

	protected MocamResult launchOperationNotify(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.OPERATE_NOTIFY);
		return processTrans(localTransaction);
	}

	protected MocamResult synCardSpace(LocalTransaction localTransaction) {
		Application application = applicationManager.getByAid(localTransaction.getAid());
		SecurityDomain sd = application.getSd();
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(), application.getSd()
				.getAid());

		if (CardSecurityDomain.STATUS_PERSO == cardSecurityDomain.getStatus().intValue()) {// 如果应用所属安全域没有被自动删除，同步应用所属安全域空间
			return launchSelectSdForSynCardSpace(localTransaction, sd, SessionStatus.SYNC_CARD_SELECT_ISD_CMD);
		} else {
			localTransaction.setSessionStatus(SessionStatus.OPERATE_NOTIFY);
			return processTrans(localTransaction);
		}
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String aid = localTransaction.getAid();
		Application app = applicationManager.getByAid(aid);

		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		validateCard(card);

		// 校验应用的状态
		if (app == null) {
			throw new PlatformException(PlatformErrorCode.APPLICATION_AID_NOT_EXIST);
		}

		// 校验卡上应用状态
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
		if (cardApplication == null) {
			throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
		}
		if (cardApplication.getApplicationVersion().getApplication().needSubscribe()
				&& CardApplication.STATUS_AVAILABLE.intValue() == cardApplication.getStatus().intValue()) {// 如果应用需要退订，但卡上应用状态为“可用”，抛出异常
			throw new PlatformException(PlatformErrorCode.TRANS_DELETE_APP_UNSUBSCRIBE);
		}

		// 校验完成，设置卡上应用的版本号
		localTransaction.setAppVersion(cardApplication.getApplicationVersion().getVersionNo());
	}
}
