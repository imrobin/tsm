package com.justinmobile.tsm.cms2ac.message;

import static com.justinmobile.core.utils.ByteUtils.contactArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("mocamMessageBuilder")
public class MocamMessageBuilder {

	private static final int SESSION_SUCCESS = 0x00;

	private static final int SESSION_FAIL = 0x01;
	
	public List<MocamMessage> mashal(LocalTransaction localTransaction) {
		List<ApduCommand> exeLoadCmdBatch = new ArrayList<ApduCommand>();
		exeLoadCmdBatch.add(localTransaction.getLastCms2acParam().getLastApduCommand());
		List<MocamMessage> mocamMessages = mashal(localTransaction, exeLoadCmdBatch);
		return mocamMessages;
	}

	public List<MocamMessage> mashal(LocalTransaction trans, List<ApduCommand> exeLoadCmdBatch) {
		List<MocamMessage> apdus = Lists.newArrayList();
		for (ApduCommand apduCommand : exeLoadCmdBatch) {
			byte[] apduBytes = apduCommand.toByteArray();
			MocamMessage mocamMessage = buildMtApduCommand(trans.getLocalSessionId(), apduBytes);
			apdus.add(mocamMessage);
		}
		return apdus;
	}

	public MocamMessage noSecurityMashal(LocalTransaction localTransaction) {
		MocamMessage mocamMessage = buildMtPPDownload(localTransaction);
		return mocamMessage;
	}

	public MocamMessage buildMtApduCommand(String sessionId, byte[] apduCmd) {
		int code = MocamMessage.CODE_MT_APDU_COMMAND;
		MocamMessage mocamMessage = new MocamMessage(sessionId, code, apduCmd);
		return mocamMessage;
	}

	public MocamMessage buildMtEndSession(String sessionId, boolean isSuccess, String failInfo) {
		int code = MocamMessage.CODE_MT_END_SESSION;
		byte[] data = null;
		if (isSuccess) {
			data = new byte[] { SESSION_SUCCESS };
		} else {
			byte[] descBytes = new byte[0];
			if (StringUtils.isNotEmpty(failInfo)) {
				try {
					descBytes = contactArray(descBytes, failInfo.getBytes("GBK"));
				} catch (UnsupportedEncodingException ue) {
					throw new IllegalStateException(ue);
				}
			}
			data = contactArray(new byte[] { SESSION_FAIL }, descBytes);
		}
		MocamMessage mocamMessage = new MocamMessage(sessionId, code, data);
		return mocamMessage;
	}

	public MocamMessage buildMtPPDownload(LocalTransaction localTransaction) {
		int code = MocamMessage.CODE_MT_PP_DOWNOAD;
		String sessionId = localTransaction.getLocalSessionId();
		ApduCommand ppDownloadCmd = localTransaction.getLastCms2acParam().getLastApduCommand();
		byte[] data = ppDownloadCmd.toByteArray();
		MocamMessage cardDriverMessage = new MocamMessage(sessionId, code, data);
		return cardDriverMessage;
	}
}
