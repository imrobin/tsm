package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("updateAppProcessor")
public class UpdateAppProcessor extends PublicOperationProcessor {

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:// 开始
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupUpdate(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:// 选择所属安全域
			result = startUpdate(localTransaction);
			break;
		case SessionStatus.UPDATE_APP_READ_DATA:
			result = launchDelete(localTransaction);
			break;
		case SessionStatus.UPDATE_APP_END_DELETE:// 选择所属安全域
			result = launchDownloadApp(localTransaction);
			break;
		case SessionStatus.UPDATE_APP_END_UPDATE:// 选择所属安全域
			result = launchOperationResult(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult startUpdate(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.UPDATE_APP_READ_DATA);
		localTransactionManager.saveOrUpdate(localTransaction);

		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		if (localTransaction.hasPersonalizationsToExecute()
				&& CardApplication.STATUS_PERSO_DATA_READABLE.contains(cardApplication.getStatus())) {// 如果有个人化指令需要执行，执行个人化指令
			LocalTransaction subTransaction = buildSubTransaction(localTransaction, localTransaction.getAid(),
					Operation.UPDATE_APP_READ_PERSO_DATA);
			subTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);

			transactionHelper.buildSubPersonalizedAppTransaction(localTransaction, subTransaction);

			return process(localTransaction);
		} else {// 否则开始删除文件
			return processTrans(localTransaction);
		}
	}

	private MocamResult launchOperationResult(LocalTransaction localTransaction) {
		return operationResult(localTransaction);
	}

	private MocamResult launchDownloadApp(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.UPDATE_APP_END_UPDATE);
		localTransactionManager.saveOrUpdate(localTransaction);

		LocalTransaction subTransaction = buildSubTransaction(localTransaction, localTransaction.getAid(),
				localTransaction.getAppVersion(), Operation.UPDATE_DOWNLOAD_APP);
		subTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);
		return process(localTransaction);
	}

	private MocamResult launchDelete(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.UPDATE_APP_END_DELETE);
		localTransactionManager.saveOrUpdate(localTransaction);

		// 到此已经对当前应用版本和目标应用版本进行的差别分析，并对localTransaction.maxOrder赋值，对比localTransaction.maxOrder及当前应用版本所使用的加载文件数，有以下情况：
		// 1.localTransaction.maxOrder<=当前应用版本所使用的加载文件数，说明当前应用版本需要删除在删除顺序中的第1个到第localTransaction.maxOrder个加载文件才能升级
		// 2.localTransaction.maxOrder>当前应用版本所使用的加载文件数，说明目标应用版本使用了当前应用版本的所有加载文件，并且每个加载文件的版本也相同，但添加的更多的加载文件，可以直接跳过更新删除直接进行更新下载
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		LocalTransaction subTransaction = buildSubTransaction(localTransaction, localTransaction.getAid(), Operation.UPDATE_DELETE_APP);
		ApplicationVersion applicationVersion = cardApplication.getApplicationVersion();
		if (localTransaction.getMaxOrder() <= applicationVersion.getApplicationLoadFiles().size()) {// 如果当前版本要删除部分或全部文件后才能升级，首先进行更新删除
			subTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		} else {// 如果当前版本要不需要删除任何文件就能升级，执行更新删除的数据清理实例及数据操作
			subTransaction.setSessionStatus(SessionStatus.UPDATE_APP_BEGIN_CLEAE_APPLET);
		}

		subTransaction.setAppVersion(localTransaction.getOriginalAppVersion());
		subTransaction.setOriginalAppVersion(localTransaction.getAppVersion());
		subTransaction.setMaxOrder(localTransaction.getMaxOrder());

		return process(localTransaction);
	}

	private MocamResult startupUpdate(LocalTransaction localTransaction) {
		check(localTransaction);

		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);
		localTransactionManager.saveOrUpdate(localTransaction);

		return processTrans(localTransaction);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String aid = localTransaction.getAid();

		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		Application app = applicationManager.getByAid(aid);

		// 校验卡
		validateCard(card);

		// 校验应用的状态
		if (app == null) {
			throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_FOUND);
		}
		Integer appStatus = app.getStatus();

		if (appStatus != Application.STATUS_PUBLISHED) {
			throw new PlatformException(PlatformErrorCode.INVALID_APP_STATUS);
		}

		// 检查应用所属SP
		validateSp(app.getSp());

		// 检查当前卡上应用版本
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
		if (cardApplication == null || !CardApplication.STATUS_UPDATABLE.contains(cardApplication.getStatus())) {
			throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
		}
		ApplicationVersion currentApplicationVersion = cardApplication.getApplicationVersion();
		localTransaction.setOriginalAppVersion(currentApplicationVersion.getVersionNo());

		// 查找升级的目标应用版本
		ApplicationVersion targetApplicationVersion = null;
		if (StringUtils.isBlank(localTransaction.getAppVersion())) {// 未指定版本号，需要平台自动查找支持卡片的最新版本下载
			// 检查应用是否支持卡，如果支持，找到能支持卡的已发布最新版本
			if (!applicationManager.isSupport(card, app)) {// 如果该应用没有已发布版本支持卡，抛出异常
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_SUPPORT);
			} else {// 否则，找到能支持卡的最新已发布版本，并填写版本号
				targetApplicationVersion = applicationVersionManager.getLastestAppVersionSupportCard(card, app,
						localTransaction.getMobileNo());

				if (null == targetApplicationVersion) {// 如果匹配的应用版本不存在
					throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_SUPPORT);
				}

				// 判断最新应用版本是否与卡上的应用版本相同
				if (targetApplicationVersion.equals(currentApplicationVersion)) {
					throw new PlatformException(PlatformErrorCode.TRANS_UPDATE_APP_LASTEST_IDENTICAL_APPLICATION_VERSION);
				}

				// 填写应用版本号
				localTransaction.setAppVersion(targetApplicationVersion.getVersionNo());
			}
		} else {// 指定版本号，检查所指定的应用版本
			targetApplicationVersion = applicationVersionManager.getByAidAndVersionNo(localTransaction.getAid(),
					localTransaction.getAppVersion());

			if (null == targetApplicationVersion) {// 如果指定的应用版本不存在
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_APPLICATION_VERSION_UNEXIST);
			}

			if (!applicationVersionManager.isSupportByCard(card, targetApplicationVersion)) {// 如果卡不支持指定的应用版本
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_CARD_UNSUPPORT_APPLICATION_VERSION);
			}

			// 判断指定应用版本是否与卡上的应用版本相同
			if (targetApplicationVersion.equals(currentApplicationVersion)) {
				throw new PlatformException(PlatformErrorCode.TRANS_UPDATE_APP_SPECIALED_IDENTICAL_APPLICATION_VERSION);
			}
		}

		// 比较目标版本和当前版本的区别
		List<ApplicationLoadFile> currentDeleteOrder = transactionHelper.getDeleteOrder(currentApplicationVersion);
		int DEFULT_MAX_DELETE_ORDER = -1;
		int maxDeleteOrder = DEFULT_MAX_DELETE_ORDER;
		for (int i = currentDeleteOrder.size() - 1; i > -1; i--) {
			LoadFileVersion currentLoadFileVersion = currentDeleteOrder.get(i).getLoadFileVersion();// 当前应用版本所使用的加载文件版本
			ApplicationLoadFile targetLoadFileVersion = applicationLoadFileManager.getByApplicationVersionAndLoadFile(
					targetApplicationVersion, currentLoadFileVersion.getLoadFile());// 目标加载文件所使用的版本
			// 在以下情况要删除当前加载文件：1.目标应用版本不再使用该加载文件；2.目标应用版本与当前应用版本所使用的加载文件版本不同
			if (null == targetLoadFileVersion || !targetLoadFileVersion.getLoadFileVersion().equals(currentLoadFileVersion)) {
				maxDeleteOrder = i + 1;// 记录要删除的加载文件版本的删除顺序值
				break;// 跳出循环
			}
		}
		if (DEFULT_MAX_DELETE_ORDER == maxDeleteOrder) {// 如果最大删除顺序值等于默认值，说明当前版本的所有文件都不用删除
			// 当前只会出现两种情况：
			// 1.目标应用版本添加了新的加载文件；
			// 2.目标应用版本和目标应用版本使用的全部加载文件版本都相同
			// 不会出现以下情况：
			// 1.当前应用版本所使用的加载文件数量多于目标版本所使用的加载文件数量；
			// 2.当前应用版本所使用的加载文件数量等于目标应用版本所使用的加载文件数量，但当前应用版本和目标应用版本所使用的加载文件不完全相同
			// 3.当前应用版本所使用的加载文件数量等于目标应用版本所使用的加载文件数量，并且当前应用版本和目标应用版本所使用加载文件完全相同，但加载文件的版本不完全相同
			// 原因是以上情况都要求先删除当前应用版本的一个或多个文件，因此最大删除顺序值肯定不等于默认值
			if (currentApplicationVersion.getApplicationLoadFiles().size() < targetApplicationVersion.getApplicationLoadFiles().size()) {// 如果当前应用版本所使用的加载文件数量小于目标版本所使用的加载文件数量，说明目标应用版本添加了新的加载文件
				maxDeleteOrder = targetApplicationVersion.getApplicationLoadFiles().size();// 将最大删除顺序值记录为目标应用版本使用的加载文件数
			} else {// 否则，当前应用版本所使用的加载文件数量等于目标版本所使用的加载文件数量，说明目标应用版本和目标应用版本使用的全部加载文件版本都相同，抛出异常
				throw new PlatformException(PlatformErrorCode.TRANS_UPDATE_APP_APP_VERSIONS_HAS_NOT_DISTINGUISH);
			}
		}
		localTransaction.setMaxOrder(maxDeleteOrder);
		log.debug("\n" + "maxDeleteOrder: " + maxDeleteOrder + "\n");

		validate(targetApplicationVersion);
		validate(targetApplicationVersion, localTransaction.getMobileNo());

		// 检查应用版本所需加载文件所属安全域
		for (ApplicationLoadFile appLoadFile : targetApplicationVersion.getApplicationLoadFiles()) {
			LoadFile loadFile = appLoadFile.getLoadFileVersion().getLoadFile();
			SecurityDomain sd = loadFile.getSd();

			// 检查加载文件的安全域
			validateSd(sd);

			// 判断该批次的卡片是否支持当前的安全域
			CardBaseSecurityDomain cardBaseSecurityDomain = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, card.getCardBaseInfo());
			if (cardBaseSecurityDomain == null) {
				throw new PlatformException(PlatformErrorCode.CARD_BASE_NOT_REF_SD, sd.getAid());
			}

			// 检查加载文件的安全域在卡上状态
			if (transactionHelper.isSdLocked(sd, card)) {
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_LOAD_FILE_SD_LOCK, loadFile.getName(), sd.getSdName());
			}
		}

		// 检查应用所属的安全域
		{
			SecurityDomain sd = app.getSd();
			// 检查加载文件的安全域
			validateSd(sd);
			// 检查加载文件的安全域在卡上状态
			if (transactionHelper.isSdLocked(sd, card)) {
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_APPLICATION_SD_LOCK, app.getName(), sd.getSdName());
			}
		}
	}
}
