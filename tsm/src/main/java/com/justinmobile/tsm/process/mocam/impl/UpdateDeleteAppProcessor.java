package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Service("updateDeleteAppProcessor")
public class UpdateDeleteAppProcessor extends DeleteAppDeleteFileProcessor {

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.UPDATE_APP_BEGIN_CLEAE_APPLET:
			result = launchClearApplet(localTransaction);
			result.setProgress("清除实例");
			result.setProgressPercent("85");
			break;
		case SessionStatus.OPERATE_NOTIFY:
			localTransaction.setSessionStatus(SessionStatus.COMPLETED);// 更新删除完成后不需要通知业务平台
			result = processTrans(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
		// 升级时删除应用的检查忽略应用的删除规则
		try {
			super.check(localTransaction);
		} catch (PlatformException e) {
			if (PlatformErrorCode.APP_CAN_NOT_DELETE != e.getErrorCode()) {// 忽略父类方法中的一些错误
				throw e;
			}
		}

		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(localTransaction.getCardNo(), localTransaction.getAid());
		localTransaction.setAppVersion(cardApplication.getApplicationVersion().getVersionNo());
		localTransactionManager.saveOrUpdate(localTransaction);
	}

	@Override
	protected MocamResult launchDeleteAppSdOrNext(LocalTransaction localTransaction, int sessionStatus) {// 更新时不删除应用所属安全域
		localTransaction.setSessionStatus(SessionStatus.SYN_CAED_SPACE_START);
		return processTrans(localTransaction);
	}

	@Override
	protected boolean isAllLoadFileDelete(LocalTransaction localTransaction) {
		return localTransaction.getCurrentLoadFileIndex() == localTransaction.getMaxOrder() + 1;
	}

	@Override
	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		changeCardApplicationStatus(localTransaction.getCardNo(), localTransaction.getAid(), CardApplication.STATUS_UNDOWNLOAD);

		CardInfo card = cardInfoManager.getByCardNo(localTransaction.getCardNo());
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());

		// 无条件修改订购记录
		subscribeHistoryManager.unsubscribeApplication(card, applicationVersion);
		return super.endSuccessProcess(localTransaction);
	}

	@Override
	protected MocamResult launchDeleteAppSdOrNext(LocalTransaction localTransaction) {
		MocamResult result = super.launchDeleteAppSdOrNext(localTransaction, SessionStatus.DUPLICATE_STATUS);// 状态迁移不由父类实现决定
		localTransaction.setSessionStatus(SessionStatus.UPDATE_APP_BEGIN_CLEAE_APPLET);// 强制将状态改为“清理实例”
		return result;
	}

	private MocamResult launchClearApplet(LocalTransaction localTransaction) {
		// 执行到此，卡上原有应用版本已经部分或全部删除，新应用版本还未下载
		// 获取卡上原有应用版本
		ApplicationVersion originalApplicationVersion = applicationVersionManager.getByAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());
		// 获取升级目标应用版本
		ApplicationVersion targetApplicationVersion = applicationVersionManager.getByAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getOriginalAppVersion());

		// 卡上剩余加载文件是在原有应用版本的删除顺序中从第localTransaction.maxOrder=1开始的加载文件
		List<ApplicationLoadFile> originalDeleteOrder = transactionHelper.getDeleteOrder(originalApplicationVersion);
		for (int i = 0; i < originalDeleteOrder.size(); i++) {// 对于卡上剩余的加载文件版本，检查是否有实例
			LoadFileVersion loadFileVersion = originalDeleteOrder.get(i).getLoadFileVersion();

			// 获取从当前加载文件版本创建的、属于原有应用版本的卡上实例记录
			List<CardApplet> cardApplets = cardAppletManager.getByCardNoAndApplicationVersionThatCreateLoadFileVersion(
					localTransaction.getCardNo(), originalApplicationVersion, loadFileVersion);

			for (CardApplet cardApplet : cardApplets) {// 对于每一个卡上应用记录，查看在目标应用版本中是否有相同AID且来自于同一模块的实例
				// 在目标应用版本中查找与当前实例AID相同且由同一个模块创建的实例
				Applet applet = appletManager.getByAidAndLoadModuleAndApplicationVersion(cardApplet.getApplet().getAid(), cardApplet
						.getApplet().getLoadModule(), targetApplicationVersion);
				if (null == applet) {// 如果在目标应用版本中没有与当前实例AID相同且由同一个模块创建的实例，删除当前卡上实例
					buildSubTransaction(localTransaction, cardApplet.getApplet().getAid(), Operation.DELETE_APPLET);
				} else {// 如果在目标应用版本中有与当前实例AID相同且由同一个模块创建的实例，修改当前卡上实例
					cardApplet.setApplet(applet);
					cardAppletManager.saveOrUpdate(cardApplet);
				}
			}
		}

		localTransaction.setSessionStatus(SessionStatus.SYN_CAED_SPACE_START);
		return process(localTransaction);
	}
}
