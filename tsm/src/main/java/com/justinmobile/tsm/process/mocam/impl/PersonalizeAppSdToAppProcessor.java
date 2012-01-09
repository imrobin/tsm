package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.Personalizations;

@Service("personalizeAppSdToAppProcessor")
public class PersonalizeAppSdToAppProcessor extends AbstractPersonalizeAppProcessor {

	public MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.INIT:
			result = preOperation(localTransaction);
			break;
		case SessionStatus.PRE_OPERATION_SUCCESS:
			result = startupPersonalize(localTransaction);
			break;
		case SessionStatus.OPEN_RW_WAIT_OPEN_REQ:
			result = startPersonalize(localTransaction);
			break;
		case SessionStatus.PERSONALIZE_APP_BEGIN_PERSON:
			result = beginPersoApp(localTransaction);
			break;
		case SessionStatus.PERSONALIZE_APP_SELECT_CMD:// 选择不同方式开始下发个人化数据
			parseSelectAppRsp(localTransaction);
			result = launchInitUpdate(localTransaction, SessionStatus.PERSONALIZE_APP_INITUPDATE_APP_SD_CMD);
			break;
		case SessionStatus.PERSONALIZE_APP_INITUPDATE_APP_SD_CMD:// 验证建立安全通道指令，发送外部认证指令
			result = parseInitUpdateSdRsp(localTransaction, SessionStatus.PERSONALIZE_APP_EXTAUTH_APP_SD_CMD);
			break;
		case SessionStatus.PERSONALIZE_APP_EXTAUTH_APP_SD_CMD:// 外部认证完成，只有方式二和方式三进入本步骤，进行个人化操作
			parseExtAuthSdRsp(localTransaction);
			result = launchInstallPerso(localTransaction, SessionStatus.PERSONALIZE_APP_INSTALL_PERSO_CMD);
			break;
		case SessionStatus.PERSONALIZE_APP_INSTALL_PERSO_CMD:
			parseInstallPersoRsp(localTransaction);
			result = launchPersoApdu(localTransaction);
			break;
		case SessionStatus.PERSONALIZE_APP_APDU_CMD:
			result = parsePersoApduRsp(localTransaction);
			break;
		case SessionStatus.PERSONALIZE_APP_PESO_APDU_RSP:
			result = launchPersoApdu(localTransaction);
			break;
		case SessionStatus.PERSONALIZE_APP_END_PERSO:
			result = endSuccessProcess(localTransaction);
			break;
		case SessionStatus.COMPLETED:
			result = endSuccessProcess(localTransaction);
			break;
		default:
			result = super.processTrans(localTransaction);
		}
		return result;
	}

	private MocamResult launchInstallPerso(LocalTransaction localTransaction, int sessionStauts) {
		String appletAid = localTransaction.getCurrentPersonalizations().getCurrentPersonalization().getAid();
		return launchInstallPerso(localTransaction, sessionStauts, appletAid);
	}

	@Override
	protected MocamResult launchSelectApp(LocalTransaction localTransaction, int sessionStatus) {
		// 安全域访问应用（方式三），选择安全域，使用安全域密钥进行保护
		Application application = applicationManager.getByAid(localTransaction.getAid());
		SecurityDomain sd = application.getSd();

		return launchSelectSd(localTransaction, sd, sessionStatus);
	}

	@Override
	protected List<ApduCommand> bulildPersonalizeCmd(LocalTransaction localTransaction, String fileContent) {
		return apduEngine.buildSdToAppCmd(localTransaction.getLastCms2acParam(), fileContent, Constants.MOCAM_PERSO_LENGTH);
	}

	@Override
	protected String getFileContent(LocalTransaction localTransaction) {
		if (localTransaction.hasPersonalizationsToExecute()) {// 如果当前流程有待下发的个人化指令列表，处理个人化指令列表
			Personalizations personalizations = localTransaction.getCurrentPersonalizations();
			if (personalizations.hasPersonalizationToExecute()) {// 如果当前个人化指令列表有待下发的个人化指令，返回个人化指令
				return personalizations.getCurrentPersonalization().getFileContent();
			} else {// 如果当前个人化指令列表没有待下发的个人化指令，返回null
				return null;
			}
		} else {// 如果当前流程没有待下发的个人化指令列表，返回null
			return null;
		}
	}
}
