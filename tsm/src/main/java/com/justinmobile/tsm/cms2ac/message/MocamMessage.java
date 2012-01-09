package com.justinmobile.tsm.cms2ac.message;

import static com.justinmobile.core.utils.ByteUtils.*;

import java.util.ArrayList;
import java.util.List;

public class MocamMessage {

	public static final int CODE_MO_LAUNCH_SESSION = 0x01;

	public static final int CODE_MT_APDU_COMMAND = 0x02;

	public static final int CODE_MO_APDU_RESULT = 0x03;

	public static final int CODE_MT_END_SESSION = 0x04;

	public static final int CODE_MT_PP_DOWNOAD = 0x05;

	private String sessionId;

	private int code;

	private int length;

	private byte[] data;

	private List<byte[]> hexApduCommands = new ArrayList<byte[]>();

	public MocamMessage(String sessionId, int code, byte[] data) {
		this.sessionId = sessionId;
		this.code = code;
		this.length = data.length;
		this.data = data;
	}

	public byte[] toByteArray() {
		byte[] buf = new byte[0];
		buf = contactArray(buf, this.data);
		return buf;
	}

	public String getSessionId() {
		return sessionId;
	}

	public int getCode() {
		return code;
	}

	public int getLength() {
		return length;
	}

	public byte[] getData() {
		return data;
	}

	public List<byte[]> getHexApduCommands() {
		return hexApduCommands;
	}

	public void setHexApduCommands(List<byte[]> hexApduCommands) {
		this.hexApduCommands = hexApduCommands;
	}

	@Override
	public String toString() {
		byte[] byteArray = toByteArray();
		if (byteArray == null || byteArray.length == 0) {
			return "";
		} else {
			return toHexString(byteArray);
		}
	}
}
