package com.justinmobile.tsm.cms2ac.dto;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;

import java.util.Map;

public class CardSpaceInfo extends TlvDto {

	private int appCount;

	private int freeNoneVolatile;

	private int freeVolatile;

	public CardSpaceInfo(byte[] tlv) {
		int index = indexOfTag(tlv, (byte) 0x81);
		Map<Byte, byte[]> map = sliptTlv(tlv, index);
		this.appCount = binaryToInt(map.get((byte) 0x81));
		this.freeNoneVolatile = binaryToInt(map.get((byte) 0x82));
		this.freeVolatile = binaryToInt(map.get((byte) 0x83));
	}
	
	public int getAppCount() {
		return appCount;
	}

	public int getFreeVolatile() {
		return freeVolatile;
	}

	public int getFreeNoneVolatile() {
		return freeNoneVolatile;
	}

	public void setAppCount(int appCount) {
		this.appCount = appCount;
	}

	public void setFreeVolatile(int freeVolatile) {
		this.freeVolatile = freeVolatile;
	}

	public void setFreeNoneVolatile(int freeNoneVolatile) {
		this.freeNoneVolatile = freeNoneVolatile;
	}
}
