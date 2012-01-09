package com.justinmobile.tsm.process.mocam;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.rightSubArray;
import static com.justinmobile.core.utils.ByteUtils.subArray;
import static com.justinmobile.core.utils.ByteUtils.toHexString;

import java.util.ArrayList;
import java.util.List;

public class MocamMessage {

	public static final String PACKAGE_MAGIC = "12AB";

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

	public MocamMessage(byte[] fromMocam) {
		byte[] buf = subArray(fromMocam, 0, 2);
		if (!PACKAGE_MAGIC.equals(toHexString(buf))) {
			throw new IllegalArgumentException("error cardDriver message fomrat");
		}
		// SESSIONID改为29个字符
		buf = subArray(fromMocam, 2, 31);
		this.sessionId = new String(buf);

		buf = subArray(fromMocam, 31, 32);
		this.code = binaryToInt(buf);

		buf = subArray(fromMocam, 32, 34);
		this.length = binaryToInt(buf);

		this.data = rightSubArray(fromMocam, 34);
		if (this.data == null || this.data.length != this.length) {
			System.out.println(toHexString(fromMocam));
			throw new IllegalArgumentException("error CardDriver format");
		}
	}

	public byte[] toByteArray() {
		byte[] buf = new byte[0];
		// 和客户端通讯去掉读卡器协议头
		// byte[] buf = hexStringToBytes(PACKAGE_MAGIC);
		// buf = contactArray(buf, this.sessionId.getBytes());
		// buf = contactArray(buf, intToHexBytes(this.code, 1));
		// buf = contactArray(buf, intToHexBytes(this.length, 2));
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
