package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardClient;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Service("mocamDownloadAppProcessor")
public class MocamDownloadAppProcessorImpl extends PublicOperationProcessor {

	private static final Logger log = LoggerFactory.getLogger(MocamDownloadAppProcessorImpl.class);

	private MocamResult startupDownload(LocalTransaction localTransaction) {
		// 校验
		check(localTransaction);
		localTransaction.setSessionStatus(SessionStatus.OPEN_RW_WAIT_OPEN_REQ);

		return processTrans(localTransaction);
	}

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:// 业务平台预处理
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:// 业务平台预处理成功，TSM鉴权
			result = startupDownload(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:// TSM鉴权成功，开始下载应用
			result = launchDownloadApp(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		// 开始下载应用
		case SessionStatus.DOWNLOAD_APP_BEGIN_LOAD:// 开始下载第一个文件或安装第一个文件的applet，下发select指令选择文件所属安全域
			result = beginLoadOrInstall(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_SELECT_APP_SD_CMD:// 校验select指令的响应，下发get-data获取安全域空间
			result = parseSelectAppSdRsp(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_SPACE_CMD: {// 校验获get-data指令的响应，校验安全域剩余空间，下发init-update开始建立安全通道
			// 解析卡上空间信息
			Space cardSpace = parseGetFreeSpace(localTransaction);

			// 同步卡上安全域空间
			syncSpace(localTransaction, cardSpace);

			// 空间校验
			LoadFileVersion currentLoadFileVersionToDownload = transactionHelper.getCurrentLoadFileVersionToDownload(localTransaction);
			CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(localTransaction.getCardNo(),
					currentLoadFileVersionToDownload.getLoadFile().getSd().getAid());
			if (!transactionHelper.isLoadFileVersionExistOnCard(cardInfoManager.getByCardNo(localTransaction.getCardNo()),
					currentLoadFileVersionToDownload)) {
				validateSpace(cardSecurityDomain, currentLoadFileVersionToDownload.getSpaceInfo());
			}

			result = launchInitUpdate(localTransaction, SessionStatus.DOWNLOAD_APP_INITUPDATE_LF_SD_CMD);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		}
		case SessionStatus.DOWNLOAD_APP_INITUPDATE_LF_SD_CMD:// 校验init-update指令响应，下发ext-auth进行外部认证
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.DOWNLOAD_APP_EXTAUTH_LOAD_CMD);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_EXTAUTH_LOAD_CMD:// 校验ext-auth指令响应，下发install-for-load指令下载文件或install-for-install指令创建实例
			parseExtAuthSdRsp(localTransaction);
			result = launchLoadOrInstall(localTransaction);// 判断文件是否需要下载
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_INSTALL_LOAD_CMD:// 校验install-for-load指令的响应,下发load指令
			parseInstallLoadRsp(localTransaction);
			result = launchLoad(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_LOAD_CMD:// 校验load指令
			result = parseLoadRsp(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_INSTALL_INSTALL_CMD:// 校验install-for-install指令的响应，下发install-for-extradition指令进行迁移或者处理下一个文件
			result = parseInstallAppRsp(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_INSTALL_EXTRADITION_CMD:// 校验install-for-extradition指令的响应，处理下一个文件
			result = parseExtraditeAppRsp(localTransaction);
			result.setProgress("正在下载");
			result.setProgressPercent(calcLoadPercent(localTransaction));
			break;
		case SessionStatus.DOWNLOAD_APP_END_LOAD:// 下载完成，通知业务业务平台下载成功
			result = launchOperationNotify(localTransaction);
			break;
		case SessionStatus.DOWNLOAD_APP_BEGIN_PERSO:// 通知业务业务平台下载成功
			result = launchOperationNotify(localTransaction);
			break;
		case SessionStatus.DOWNLOAD_APP_PERSO:// 根据是否需要个人化决定执行个人化还是跳过个人化
			result = beginPersoOrEndPerson(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult beginPersoOrEndPerson(LocalTransaction localTransaction) {
		localTransaction.setSessionStatus(SessionStatus.COMPLETED);
		if (localTransaction.hasPersonalizationsToExecute()) {
			LocalTransaction subTransaction = buildSubTransaction(localTransaction, localTransaction.getAid(),
					Operation.DOWNLOAD_PERSONALIZE_APP);
			subTransaction.setSessionStatus(SessionStatus.PRE_OPERATION_SUCCESS);

			transactionHelper.buildSubPersonalizedAppTransaction(localTransaction, subTransaction);

			return process(localTransaction);
		} else {
			return processTrans(localTransaction);
		}
	}

	private String calcLoadPercent(LocalTransaction localTransaction) {
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());
		int totalCount = applicationVersion.getApplicationLoadFiles().size();
		int currentIndex = localTransaction.getCurrentLoadFileIndex();

		Integer percent = 5 + (int) (((new Double(currentIndex - 1) / totalCount) * 0.75) * 100);

		return percent.toString();
	}

	private MocamResult launchLoadOrInstall(LocalTransaction localTransaction) {
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		LoadFileVersion loadFileVersion = transactionHelper.getDowloadOrder(localTransaction)
				.get(localTransaction.getCurrentLoadFileIndex() - 1).getLoadFileVersion();
		MocamResult result = new MocamResult();
		if (!transactionHelper.isLoadFileVersionExistOnCard(card, loadFileVersion)) {
			// 当前加载文件未下载，下发install for load
			result = launchInstallLoad(localTransaction);
		} else if (transactionHelper.hasAppletToInstallFromCurrentLoadFile(localTransaction)) {
			// 当前加载文件已下载并且有实例需要安装，下发install for install
			result = beginInstall(localTransaction);
		} else {
			// 当前加载文件已下载并且没有实例需要安装，下载下一个文件
			result = launchNextLoadOrInstall(localTransaction);
		}
		return result;
	}

	/**
	 * 完成下载流程
	 * 
	 * @param localTransaction
	 */
	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String appAid = localTransaction.getAid();
		String appVersionNo = localTransaction.getAppVersion();
		CardInfo card = cardInfoManager.getByCardNo(cardNo);

		ApplicationVersion applicationVersion = applicationVersionManager.getByAidAndVersionNo(appAid, appVersionNo);
		Application application = applicationVersion.getApplication();

		// 修改卡上应用状态
		if (application.needSubscribe()) {// 如果应用需要订购，将卡上应用状态改为“已个人化”
			changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_PERSONALIZED);
		} else {// 如果应用不需要订购，将卡上应用状态改为“可用”
			changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_AVAILABLE);

			// 添加订购记录
			subscribeHistoryManager.subscribeApplication(cardInfoManager.getByCardNo(cardNo), applicationVersion);
		}

		// 添加终端上客户端记录
		List<CardClient> cardClients = cardClientManager.getByCardAndApplication(card, applicationVersion.getApplication());
		List<ApplicationClientInfo> applicationClientInfos = clientManager.getByAidAndCardNo(appAid, cardNo);
		for (ApplicationClientInfo client : applicationClientInfos){
			CardClient cardClientTemp = null;
			for (CardClient cardClient : cardClients){
				if (cardClient.getClient().getId() == client.getId()){
					cardClientTemp = cardClient;
				}
			}
			if (null == cardClientTemp) {
				cardClientTemp = new CardClient();
			}
			cardClientTemp.setClient(client);
			cardClientTemp.setCard(card);
			cardClientManager.saveOrUpdate(cardClientTemp);
		}
		Space space = applicationVersion.getSpaceInfo();
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, appAid);

		// 修改可迁出性
		cardApplication.setMigratable(Boolean.FALSE);

		// 保存空间信息
		cardApplication.setSpaceInfo(space);

		// 应用下载次数+1
		application.increaseDownloadcount();
		applicationManager.saveOrUpdate(application);

		return super.endSuccessProcess(localTransaction);
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		String cardNo = localTransaction.getCardNo();
		String appAid = localTransaction.getAid();

		CardInfo card = cardInfoManager.getByCardNo(cardNo);
		Application app = applicationManager.getByAid(appAid);

		// 校验卡
		validateCard(card);

		// 校验应用的状态
		if (app == null) {
			throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_FOUND);
		}
		Integer appStatus = app.getStatus();
		System.out.println(appStatus);
		log.debug("\n" + "应用状态: " + appStatus + "\n");
		log.debug("\n" + "要求状态: " + Application.STATUS_PUBLISHED + "\n");
		if (!card.isTestCard() && appStatus != Application.STATUS_PUBLISHED) {
			throw new PlatformException(PlatformErrorCode.INVALID_APP_STATUS);
		}

		// 检查应用所属SP
		validateSp(app.getSp());

		ApplicationVersion applicationVersion = null;

		if (StringUtils.isNotBlank(localTransaction.getAppVersion())) {// 指定版本号，检查所指定的应用版本
			applicationVersion = applicationVersionManager.getByAidAndVersionNo(appAid, localTransaction.getAppVersion());

			if (null == applicationVersion) {// 如果指定的应用版本不存在
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_APPLICATION_VERSION_UNEXIST);
			}

			if (!card.isTestCard() && !applicationVersionManager.isSupportByCard(card, applicationVersion)) {// 如果卡不是测试卡，并且卡不支持指定的应用版本
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_CARD_UNSUPPORT_APPLICATION_VERSION);
			}
		} else if (cardApplicationManager.isPreset(card, app)) {// 如果没有指定版本号，但该应用预置，使用卡上当前应用版本
			applicationVersion = cardApplicationManager.getByCardNoAid(cardNo, appAid).getApplicationVersion();
		} else {// 其他情况，需要平台自动查找支持卡片的最新版本下载
			// 检查应用是否支持卡，如果支持，找到能支持卡的已发布最新版本
			if (!applicationManager.isSupport(card, app)) {// 如果该应用没有已发布版本支持卡，抛出异常
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_SUPPORT);
			} else {// 否则，找到能支持卡的最新已发布版本，并填写版本号
				applicationVersion = applicationVersionManager.getLastestAppVersionSupportCard(card, app, localTransaction.getMobileNo());
				if (null == applicationVersion) {// 如果匹配的应用版本不存在
					throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_NOT_SUPPORT);
				}
			}
		}
		localTransaction.setAppVersion(applicationVersion.getVersionNo());

		// 检查应用版本
		if (!cardApplicationManager.isPreset(card, app) && !card.isTestCard()) {// 测试卡不检查应用版本，预置应用不检查版本
			validate(applicationVersion, localTransaction.getMobileNo());
		}

		// 检查卡上应用版本
		CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, appAid);
		if (null != cardApp) {// 应用的某个版本已经被下载
			if (applicationVersion.equals(cardApp.getApplicationVersion())) {// 能支持卡的应用最新版本可能下载
				if (CardApplication.STATUS_AVAILABLE.intValue() == cardApp.getStatus().intValue()
						|| CardApplication.STATUS_PERSONALIZED.intValue() == cardApp.getStatus().intValue()) {
					throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_VERSION_EXIST);
				} else if (!CardApplication.STATUS_DOWNLOADABLE.contains(cardApp.getStatus())) {// 最新版本曾经下载且未完全删除，检查当前应用卡上状态是否是可下载状态
					throw new PlatformException(PlatformErrorCode.INVALID_CARD_APP_STATUS);
				}
			} else if (CardApplication.STATUS_UNDOWNLOAD.intValue() != cardApp.getStatus().intValue()) {// 如果记录存在，且状态不是“未下载”，说明应用其他版本已被下载
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_OTHER_VERSION_EXIST);
			} else {// 如果记录存在，且状态是“未下载”，说明应用其他版本曾经被下载现在已经被删除
				cardApp.setApplicationVersion(applicationVersion);// 修改卡应用信息中的应用版本
				cardApplicationManager.saveOrUpdate(cardApp);
			}
		}

		// 检查应用版本所需加载文件在卡上状态
		for (ApplicationLoadFile appLoadFile : applicationVersion.getApplicationLoadFiles()) {
			LoadFileVersion loadFileVersion = appLoadFile.getLoadFileVersion();

			if (transactionHelper.isOtherVersionOfLoadFileExistOnCard(card, loadFileVersion)) {// 卡上有某个需要的加载文件有其他版本存在
				throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_OTHER_LOAD_FILE_VERSION_EXIST);
			}
		}

		// 检查应用版本所需加载文件所属安全域
		for (ApplicationLoadFile appLoadFile : applicationVersion.getApplicationLoadFiles()) {
			LoadFile loadFile = appLoadFile.getLoadFileVersion().getLoadFile();
			SecurityDomain sd = loadFile.getSd();

			// 检查加载文件的安全域
			validateSd(sd);

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

	private MocamResult parseExtraditeAppRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseInstallRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_INSTALL_APP_ERROR, ae);
		}

		// 迁移完成，安装当前加载文件的实例或下载下一个加载文件
		return launchNextLoadOrInstall(localTransaction);
	}

	private MocamResult parseInstallAppRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseInstallRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_INSTALL_APP_ERROR, ae);
		}

		// 实例安装成功，建立安装关系
		Applet applet = appletManager.load(localTransaction.getCurrentAppletIndex());
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());

		CardApplet cardApplet = cardAppletManager.getByCardAndApplet(card, applet);
		if (null == cardApplet) {
			cardApplet = new CardApplet();
			cardApplet.setApplet(applet);
			cardApplet.setCard(card);
			cardAppletManager.saveOrUpdate(cardApplet);
		}

		// 完成空间计算
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getbySdAndCard(card, applet.getSd());
		Space aviliableSpace = cardSecurityDomain.getAviliableSpace();
		aviliableSpace = aviliableSpace.minus(applet.getSpaceInfo());
		cardSecurityDomain.setAviliableSpace(aviliableSpace);

		// 修改卡上应用状态
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_DOWNING);

		// 根据是否需要迁移决定下一操作
		if (needExtradition(localTransaction)) {// 需要迁移
			ApduCommand installForInstallCmd = apduEngine.buildInstallForExtraditeCmd(cms2acParam);

			contactApduCommand(cms2acParam, installForInstallCmd);
			MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.DOWNLOAD_APP_INSTALL_EXTRADITION_CMD);
			result.setApduName(ApduName.Install_For_Extradition);
			return result;
		} else {// 否则，安装当前加载文件的下一实例或下载下一个加载文件
			return launchNextLoadOrInstall(localTransaction);
		}
	}

	protected MocamResult launchOperationNotify(LocalTransaction localTransaction) {
		// 进入个人化流程前将订购状态改为已安装
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_INSTALLED);
		CardApplication cardApp = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		cardApp.setSpaceInfo(cardApp.getApplicationVersion().getSpaceInfo());
		cardApplicationManager.saveOrUpdate(cardApp);

		return operationResult(localTransaction, SessionStatus.DOWNLOAD_APP_PERSO);
	}

	private boolean needExtradition(LocalTransaction localTransaction) {
		SecurityDomain loadFileSd = transactionHelper.getCurrentLoadFileVersionToDownload(localTransaction).getLoadFile().getSd();
		SecurityDomain applicationSd = applicationManager.getByAid(localTransaction.getAid()).getSd();

		return !applicationSd.equals(loadFileSd);
	}

	private MocamResult parseLoadRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		if (!isBatchCmdIndexCorrect(localTransaction)) {
			throw new PlatformException(PlatformErrorCode.LOAD_BATCH_EXE_ERROR);
		}

		try {
			apduEngine.parseLoadRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_LOAD_APP_ERROR, ae);
		}

		MocamResult result = null;
		if (cms2acParam.getCommandBatchNo().intValue() == cms2acParam.getLastApduCommand().getBatchNo().intValue()) {
			// 如果当前加载文件所有LOAD指令执行完成

			List<ApplicationLoadFile> downloadOrder = transactionHelper.getDowloadOrder(localTransaction);
			LoadFileVersion loadFileVersion = downloadOrder.get(localTransaction.getCurrentLoadFileIndex() - 1).getLoadFileVersion();
			CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());

			// 添加下载完成的卡加载文件记录
			CardLoadFile cardLoadFile = cardLoadFileManager.getByAidAndCardNo(loadFileVersion.getLoadFile().getAid(), card.getCardNo());
			if (null == cardLoadFile) {
				cardLoadFile = new CardLoadFile();
				cardLoadFile.setCard(card);
				cardLoadFile.setLoadFileVersion(loadFileVersion);
				cardLoadFileManager.saveOrUpdate(cardLoadFile);
			}

			CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getbySdAndCard(card, loadFileVersion.getLoadFile().getSd());
			Space aviliableSpace = cardSecurityDomain.getAviliableSpace();
			aviliableSpace = aviliableSpace.minus(loadFileVersion.getSpaceInfo());
			cardSecurityDomain.setAviliableSpace(aviliableSpace);

			changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_DOWNING);
			result = launchNextLoadOrInstall(localTransaction);// 安装当前加载文件的实例或下载下一加载文件
		} else {
			// 否则继续执行下一个批次的LOAD指令
			cms2acParam.increaseBatchNo();
			List<ApduCommand> exeLoadCmdBatch = getExeCmdBatch(cms2acParam.getApduCommands(), cms2acParam.getCommandBatchNo());
			cms2acParam.setCommandIndex(exeLoadCmdBatch.size());

			result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.DOWNLOAD_APP_LOAD_CMD);
			result.setApduName(ApduName.Load);
		}
		return result;
	}

	/**
	 * 安装当前加载文件的实例或下载下一个加载文件
	 * 
	 * @param localTransaction
	 * @return
	 */
	private MocamResult launchNextLoadOrInstall(LocalTransaction localTransaction) {
		MocamResult result = null;
		if (transactionHelper.hasAppletToInstallFromCurrentLoadFile(localTransaction)) {
			// 如果当前安装文件有待安装的实例，安装实例
			result = beginInstall(localTransaction);
		} else {// 如果当前安装文件没有待安装的实例，继续下载下一加载文件
			result = launchNextLoad(localTransaction);
		}

		return result;
	}

	private MocamResult beginInstall(LocalTransaction localTransaction) {
		// 找到待安装的实例
		Applet applet = transactionHelper.getNextAppletToInstallFromCurrentLoadFile(localTransaction);
		localTransaction.setCurrentAppletIndex(applet.getId());

		// 下发install for install
		return launchInstallInstall(localTransaction, SessionStatus.DOWNLOAD_APP_INSTALL_INSTALL_CMD);
	}

	/**
	 * 下载下一个加载文件
	 * 
	 * @param localTransaction
	 * @return
	 */
	private MocamResult launchNextLoad(LocalTransaction localTransaction) {
		localTransaction.increaseCurrentLoadFileIndex();

		localTransaction.setSessionStatus(SessionStatus.DOWNLOAD_APP_BEGIN_LOAD);
		return processTrans(localTransaction);
	}

	/**
	 * 组建install for install
	 * 
	 * @param localTransaction
	 * @param cms2acParam
	 * @param sessionStatus
	 *            下发指令后状态
	 * @return
	 */
	private MocamResult launchInstallInstall(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand installForInstallCmd = apduEngine.buildInstallForInstallCmd(cms2acParam, false, false);

		contactApduCommand(cms2acParam, installForInstallCmd);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Install_For_Install);
		return result;
	}

	private void parseInstallLoadRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseInstallRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_INSTALL_LOAD_ERROR, ae);
		}
	}

	private MocamResult launchLoad(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		List<ApduCommand> cmdBatch = apduEngine.buildLoadCmdBatch(cms2acParam);

		serializeApduCmdBatch(cms2acParam, cmdBatch, Constants.MOCAM_DATA_MAX_LENGTH);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, cmdBatch, SessionStatus.DOWNLOAD_APP_LOAD_CMD);
		result.setApduName(ApduName.Load);
		return result;
	}

	/**
	 * 下发install for load
	 */
	private MocamResult launchInstallLoad(LocalTransaction localTransaction) {

		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();

		ApduCommand nextCmd = apduEngine.buildInstallForLoadCmd(cms2acParam);

		contactApduCommand(cms2acParam, nextCmd);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, SessionStatus.DOWNLOAD_APP_INSTALL_LOAD_CMD);
		result.setApduName(ApduName.Install_For_Load);
		return result;
	}

	private boolean isAllLoadFileDownload(LocalTransaction localTransaction) {
		List<ApplicationLoadFile> downloadOrder = transactionHelper.getDowloadOrder(localTransaction);
		return localTransaction.getCurrentLoadFileIndex() == downloadOrder.size() + 1;
	}

	private MocamResult parseSelectAppSdRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);

		try {
			apduEngine.parseSelectRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.SELECT_APP_ERROR, ae);
		}

		if (SystemConfigUtils.isCms2acRuntimeEnvironment()) {// 如果当前运行环境为cms2ac，同步当前安全域的空间信息
			return getAppSpace(localTransaction, SessionStatus.DOWNLOAD_APP_SPACE_CMD);
		} else {// 如果当前运行环境不是cms2ac，直接下发init-update
			return launchInitUpdate(localTransaction, SessionStatus.DOWNLOAD_APP_INITUPDATE_LF_SD_CMD);
		}
	}

	/**
	 * 开始下载应用
	 * 
	 * @param localTransaction
	 * @return
	 */
	private MocamResult launchDownloadApp(LocalTransaction localTransaction) {
		MocamResult result = null;
		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());

		ApplicationVersion applicationVersion = applicationVersionManager.getByAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());

		Application application = applicationVersion.getApplication();
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(card.getCardNo(), application.getAid());
		if (null == cardApplication) {// 如果卡应用记录不存在，建立记录并置为"未下载"状态
			cardApplication = new CardApplication();
			cardApplication.setCardInfo(card);
			cardApplication.setApplicationVersion(applicationVersion);
			cardApplication.setStatus(CardApplication.STATUS_UNDOWNLOAD);
		}
		cardApplicationManager.saveOrUpdate(cardApplication);

		// 根据卡应用状态判断流程
		if (CardApplication.STATUS_INSTALLED == cardApplication.getStatus().intValue()) {// 如果加载文件全部下载完成，跳过下载流程，直接进入后续流程
			localTransaction.setSessionStatus(SessionStatus.DOWNLOAD_APP_END_LOAD);
			result = processTrans(localTransaction);
		} else if (CardApplication.STATUS_PERSONALIZED == cardApplication.getStatus().intValue()) {// 如果个人化已完成，应重新个人化
			localTransaction.setSessionStatus(SessionStatus.DOWNLOAD_APP_BEGIN_PERSO);
			result = processTrans(localTransaction);
		} else {// 其他状态进入下载文件流程
			result = beginLoadOrCreateApplicationSd(localTransaction);
		}

		return result;
	}

	/**
	 * 开始下载文件或创建应用所属安全域
	 * 
	 * @param localTransaction
	 * @return
	 */
	private MocamResult beginLoadOrCreateApplicationSd(LocalTransaction localTransaction) {
		MocamResult result = null;

		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		ApplicationVersion applicationVersion = applicationVersionManager.getByAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());
		Application application = applicationVersion.getApplication();
		SecurityDomain applicationSd = application.getSd();

		if (transactionHelper.isSecurityDomainExistOnCard(card, applicationSd)) {// 如果卡上存在应用所属安全域，开始下载文件
			localTransaction.setSessionStatus(SessionStatus.DOWNLOAD_APP_BEGIN_LOAD);
			result = processTrans(localTransaction);
		} else {// 如果卡上不存在应用所属安全域
			if (transactionHelper.canSdAutoCreate(applicationSd)) {// 如果不存在应用所属安全域但允许自动创建，创建应用所属安全域
				buildSubTransaction(localTransaction, applicationSd.getAid(), null, Operation.CREATE_SD);
				result = process(localTransaction, null);
			} else {// 如果如果不存在应用所属安全域且不允许自动创建，抛出异常
				throw new PlatformException(PlatformErrorCode.CARD_APP_SD_NOT_EXIST, application.getName(), applicationSd.getSdName());
			}
		}
		return result;
	}

	/**
	 * 准备下载文件
	 * 
	 * @param localTransaction
	 * @return
	 */
	private MocamResult beginLoadOrInstall(LocalTransaction localTransaction) {
		MocamResult result = new MocamResult();
		if (isAllLoadFileDownload(localTransaction)) {// 所有文件都下载，进入后续流程
			localTransaction.setSessionStatus(SessionStatus.DOWNLOAD_APP_END_LOAD);
			result = processTrans(localTransaction);
		} else {

			CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
			LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDownload(localTransaction);
			if (!transactionHelper.isLoadFileVersionExistOnCard(card, loadFileVersion)) {// 当前加载文件未下载，下载文件或者创建所属安全域
				result = beginLoadOrCreateLoadFileSd(localTransaction);
			} else if (transactionHelper.hasAppletToInstallFromCurrentLoadFile(localTransaction)) {// 当前加载文件已下载并且有实例需要安装，安装实例
				result = launchSelectSdForLoadFile(localTransaction, SessionStatus.DOWNLOAD_APP_SELECT_APP_SD_CMD);
			} else {// 当前加载文件已下载并且没有实例需要安装，下载下一个文件
				result = launchNextLoad(localTransaction);
			}

		}

		return result;
	}

	/**
	 * 开始下载加载文件或者创建加载文件所属安全域
	 * 
	 * @param localTransaction
	 * @return
	 */
	private MocamResult beginLoadOrCreateLoadFileSd(LocalTransaction localTransaction) {
		MocamResult result = new MocamResult();

		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDownload(localTransaction);
		LoadFile loadFile = loadFileVersion.getLoadFile();
		SecurityDomain sd = loadFile.getSd();

		if (transactionHelper.isSecurityDomainExistOnCard(card, sd)) {// 如果卡上安全域存在，选择安全域开始下载文件
			result = launchSelectSdForLoadFile(localTransaction, SessionStatus.DOWNLOAD_APP_SELECT_APP_SD_CMD);
		} else {// 如果卡上安全域不存在
			if (transactionHelper.canSdAutoCreate(sd)) {// 如果卡上不存在当前加载文件所属安全域但允许自动创建，创建安全域
				buildSubTransaction(localTransaction, sd.getAid(), null, LocalTransaction.Operation.CREATE_SD);
				result = process(localTransaction, null);
			} else {// 如果卡上不存在当前加载文件所属安全域且不允许自动创建，抛出异常
				throw new PlatformException(PlatformErrorCode.CARD_LF_SD_NOT_EXIST, loadFile.getName(), loadFile.getAid());
			}
		}

		return result;
	}

	/**
	 * 为下载加载文件选择安全域
	 * 
	 * @param localTransaction
	 * @param sessionStatus
	 * @return select指令
	 */
	private MocamResult launchSelectSdForLoadFile(LocalTransaction localTransaction, int sessionStatus) {
		SecurityDomain sd = transactionHelper.getCurrentLoadFileVersionToDownload(localTransaction).getLoadFile().getSd();

		SecurityDomain targetSd = null;
		if ((SecurityDomain.MODEL_ISD == sd.getModel().intValue()) || (SecurityDomain.MODEL_COMMON == sd.getModel().intValue())) {// 如果文件所属安全域模式是主安全域或公共第三方安全域，选择主安全域下载文件
			targetSd = securityDomainManager.getIsd();
		} else {// 如果文件所属安全域模式不是主安全域或公共第三方安全域，选择所属安全域下载文件
			targetSd = sd;
		}

		return launchSelectSd(localTransaction, targetSd, sessionStatus);
	}

}
