package com.justinmobile.tsm.process.mocam.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.engine.ApduEngine;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("syncCardSdProcessor")
public class SyncCardSdProcessorImpl extends PublicOperationProcessor implements MocamProcessor {

	@Autowired
	private LoadFileManager loadFileManager;

	@Override
	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = launchSelectSd(localTransaction, SessionStatus.SYNC_CARD_SELECT_ISD_CMD);
			result.setProgress("建立安全通道");
			result.setProgressPercent("10");
			break;
		case SessionStatus.SYNC_CARD_SELECT_ISD_CMD:
			parseSelectSdRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.SYNC_CARD_INITUPDATE_CMD);
			result.setProgress("建立安全通道");
			result.setProgressPercent("15");
			break;
		case SessionStatus.SYNC_CARD_INITUPDATE_CMD:
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.SYNC_CARD_EXTAUTH_CMD);
			result.setProgress("建立安全通道");
			result.setProgressPercent("20");
			break;
		case SessionStatus.SYNC_CARD_EXTAUTH_CMD:
			parseExtAuthSdRsp(localTransaction);
			result = launchGetPackageStatus(localTransaction, SessionStatus.SYNC_CARD_GET_PACKAGE_STATUS);
			result.setProgress("同步卡片上安装文件");
			result.setProgressPercent("50");
			break;
		case SessionStatus.SYNC_CARD_GET_PACKAGE_STATUS:
			result = parseGetPackageStatus(localTransaction);
			result.setProgress("同步卡片上安装文件");
			if (SessionStatus.SYNC_CARD_DELETE_LOAD_FILE_CMD == localTransaction.getSessionStatus()) {
				result.setProgressPercent("65");
			} else {
				result.setProgressPercent("80");
			}
			break;
		case SessionStatus.SYNC_CARD_DELETE_LOAD_FILE_CMD:
			parseDeleteLoadFileRsp(localTransaction);
			result = launchGetAppletStatus(localTransaction, SessionStatus.SYNC_CARD_GET_APPLET_STATUS);
			result.setProgress("同步卡片上安装实例");
			result.setProgressPercent("80");
			break;
		case SessionStatus.SYNC_CARD_GET_APPLET_STATUS:
			result = parseGetAppletStatus(localTransaction);
			result.setProgress("同步卡片上安装实例");
			result.setProgressPercent("90");
			break;
		case SessionStatus.SYNC_CARD_DELETE_APPLET_CMD:
			parseDeleteLoadFileRsp(localTransaction);
			endTransaction(localTransaction, PlatformMessage.SUCCESS);
			result = MocamResult.getLastResult(localTransaction.getAid());
			break;
		default:
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
		}
		return result;
	}

	private MocamResult launchSelectSd(LocalTransaction localTransaction, int sessionStatus) {
		return launchSelectSd(localTransaction, securityDomainManager.getIsd(), sessionStatus);
	}

	private MocamResult launchGetPackageStatus(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		ApduCommand command = apduEngine.buildGetStatusPackageCmd(cms2acParam);
		contactApduCommand(cms2acParam, command);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Get_Status);
		return result;
	}

	private MocamResult launchGetAppletStatus(LocalTransaction localTransaction, int sessionStatus) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		ApduCommand command = apduEngine.buildGetStatusAppletCmd(cms2acParam);
		contactApduCommand(cms2acParam, command);
		MocamResult result = buildMocamMessage(localTransaction, cms2acParam, sessionStatus);
		result.setApduName(ApduName.Get_Status);
		return result;
	}

	@SuppressWarnings("unchecked")
	private MocamResult parseGetPackageStatus(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			Map<String, Integer> status = apduEngine.parseGetStatusPackageRsp(cms2acParam);
			String cardNo = localTransaction.getCardNo();
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			List<CardLoadFile> cardLoadFiles = cardLoadFileManager.getByCard(card);

			// 平台的卡上加载文件信息
			Set<String> dataCardLoadFileAids = Sets.newHashSet();
			if (CollectionUtils.isNotEmpty(cardLoadFiles)) {
				for (CardLoadFile cardLoadFile : cardLoadFiles) {
					String aid = cardLoadFile.getLoadFileVersion().getLoadFile().getAid();
					dataCardLoadFileAids.add(aid);
				}
			}

			// 实际的卡上加载文件信息
			Set<String> cardLoadFileAids = status.keySet();
			if (CollectionUtils.isNotEmpty(cardLoadFileAids)) {
				Set<String> bufs = Sets.newHashSet();
				for (String aid : cardLoadFileAids) {
					LoadFile loadFile = loadFileManager.getByAid(aid);
					if (loadFile == null) {
						bufs.add(aid);
					}
				}
				cardLoadFileAids.removeAll(bufs);
			}

			Collection<String> platHas = ListUtils.removeAll(dataCardLoadFileAids, cardLoadFileAids);// 平台上比卡上多的加载文件记录
			Collection<String> cardHas = ListUtils.removeAll(cardLoadFileAids, dataCardLoadFileAids);// 卡上比平台上多的加载文件记录

			if (CollectionUtils.isNotEmpty(platHas)) {// 如果平台有记录，但是卡上没有LoadFile，则删除平台上的记录
				for (String aid : platHas) {
					CardLoadFile cardLoadFile = cardLoadFileManager.getByAidAndCardNo(aid, cardNo);
					if (null != cardLoadFile) {
						cardLoadFileManager.remove(cardLoadFile);
					}
				}
			}

			cardHas.remove("D1560001010001610000000000000000");
			if (CollectionUtils.isNotEmpty(cardHas)) {// 如果卡上有LoadFile，但是平台没有记录，则下发删除LoadFile的指令
				return launchDeleteLoadFileOrApplet(localTransaction, ApduEngine.DELETE_CMD_DATA_TYPE_LOAD_FILE, cardHas,
						SessionStatus.SYNC_CARD_DELETE_LOAD_FILE_CMD);
			} else {
				return launchGetAppletStatus(localTransaction, SessionStatus.SYNC_CARD_GET_APPLET_STATUS);
			}
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.GET_PACKAGE_STATUS_ERROR, ae);
		}
	}

	@SuppressWarnings("unchecked")
	private MocamResult parseGetAppletStatus(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			Map<String, Integer> status = apduEngine.parseGetStatusAppletRsp(cms2acParam);
			String cardNo = localTransaction.getCardNo();
			CardInfo card = cardInfoManager.getByCardNo(cardNo);

			List<CardApplet> cardApplets = cardAppletManager.getByCard(card);
			List<CardSecurityDomain> cardSds = cardSecurityDomainManager.getByCard(card);

			// 平台的卡上实例/卡上安全域信息
			Set<String> dataCardAppletAndSdAids = Sets.newHashSet();
			if (CollectionUtils.isNotEmpty(cardApplets)) {
				for (CardApplet cardApplet : cardApplets) {
					String aid = cardApplet.getApplet().getAid();
					dataCardAppletAndSdAids.add(aid);
				}
			}
			if (CollectionUtils.isNotEmpty(cardSds)) {
				for (CardSecurityDomain cardSd : cardSds) {
					if (CardSecurityDomain.STATUS_UNCREATE != cardSd.getStatus().intValue()) {// 如果卡上安全状态不是“未创建”，加入到集合
						String aid = cardSd.getSd().getAid();
						dataCardAppletAndSdAids.add(aid);
					}
				}
			}

			// 实际的卡上实例/卡上安全域信息
			Set<String> cardAppletAids = status.keySet();
			if (CollectionUtils.isNotEmpty(cardAppletAids)) {
				Set<String> bufs = Sets.newHashSet();
				for (String aid : cardAppletAids) {
					List<Applet> applet = appletManager.getByAid(aid);
					SecurityDomain sd = securityDomainManager.getByAid(aid);
					if (CollectionUtils.isEmpty(applet) && null == sd) {
						bufs.add(aid);
					}
				}
				cardAppletAids.removeAll(bufs);
			}

			Collection<String> platHas = ListUtils.removeAll(dataCardAppletAndSdAids, cardAppletAids);// 平台上比卡上多的实例记录
			Collection<String> cardHas = ListUtils.removeAll(cardAppletAids, dataCardAppletAndSdAids);// 卡上比平台上多的实例记录

			platHas.remove(securityDomainManager.getIsd().getAid());
			if (CollectionUtils.isNotEmpty(platHas)) {// 如果平台有记录，但是卡上没有实例，则删除平台上的记录
				for (String aid : platHas) {
					CardApplet cardApplet = cardAppletManager.getByCardNoAndAppletAid(cardNo, aid);
					if (null != cardApplet) {
						cardAppletManager.remove(cardApplet);
					}
					CardSecurityDomain cardSd = cardSecurityDomainManager.getByCardNoAid(cardNo, aid);
					if (null != cardSd) {
						cardSecurityDomainManager.remove(cardSd);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(cardHas)) {// 如果卡上有实例，但是平台没有记录，则下发删除实例的指令
				Set<String> applets = new HashSet<String>();
				Set<String> sds = new HashSet<String>();
				ArrayList<String> deleteAids = new ArrayList<String>();

				for (String aid : cardHas) {
					List<Applet> applet = appletManager.getByAid(aid);
					SecurityDomain sd = securityDomainManager.getByAid(aid);
					if (null != applet) {
						applets.add(aid);
					} else if (null != sd) {
						sds.add(aid);
					}
				}

				deleteAids.addAll(applets);
				deleteAids.addAll(sds);
				return launchDeleteLoadFileOrApplet(localTransaction, ApduEngine.DELETE_CMD_DATA_TYPE_SD_APP, deleteAids,
						SessionStatus.SYNC_CARD_DELETE_APPLET_CMD);
			} else {
				endTransaction(localTransaction, PlatformMessage.SUCCESS);
				return MocamResult.getLastResult(localTransaction.getAid());
			}
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.GET_APPLET_STATUS_ERROR, ae);
		}
	}

	private void parseDeleteLoadFileRsp(LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		parseCms2acMoMocamMessage(localTransaction, cms2acParam);
		try {
			apduEngine.parseDeleteRsp(cms2acParam);
		} catch (ApduException ae) {
			throw new PlatformException(PlatformErrorCode.APDU_DELETE_APP_ERROR, ae);
		}
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
	}
}
