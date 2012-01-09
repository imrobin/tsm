package com.justinmobile.tsm.process.mocam.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.Personalization;
import com.justinmobile.tsm.transaction.domain.Personalizations;

@Service("personalizeAppPassthonghProcessor")
public class PersonalizeAppPassthonghProcessor extends AbstractPersonalizeAppProcessor {

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
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
		case SessionStatus.PERSONALIZE_APP_SELECT_CMD:
			parseSelectAppRsp(localTransaction);
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

	@Override
	protected MocamResult launchSelectApp(LocalTransaction localTransaction, int sessionStatus) {
		// 可以不建立cms2acParam，但是因为数据结构是指令与cms2acParam关联，因此使用应用所属安全域建立cms2acParam
		return launchSelectSd(localTransaction, securityDomainManager.getIsd(), sessionStatus);
	}

	@Override
	protected ApduCommand buildSelectCmd(Cms2acParam cms2acParam) {
		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		Application application = applicationManager.getByAid(localTransaction.getAid());
		return apduEngine.buildSelectCmd(cms2acParam, application.getAid());// 使用应用AID组装selecte指令
	}

	@Override
	protected List<ApduCommand> bulildPersonalizeCmd(LocalTransaction localTransaction, String fileContent) {
		return apduEngine.buildPersonalizeCmdBatch(localTransaction.getLastCms2acParam(), ConvertUtils.hexString2ByteArray(fileContent));
	}

	@Override
	protected String getFileContent(LocalTransaction localTransaction) {
		if (localTransaction.hasPersonalizationsToExecute()) {
			Personalizations currentPersonalizations = localTransaction.getCurrentPersonalizations();
			List<Personalization> personalizations = currentPersonalizations.getPersonalizations();
			StringBuffer sb = new StringBuffer();
			for (Personalization personalization : personalizations) {
				sb.append(personalization.getFileContent());
			}

			// 将当前个人化指令列表中下一个人化指令索引设为最大值，因为个人化方式一的个人化指令包括select指令，可以将所有实例的个人化指令一次组装完成
			currentPersonalizations.setCurrentPersonlizationIndex(personalizations.size() - 1);
			return sb.toString();
		} else {
			return null;
		}
	}
}
