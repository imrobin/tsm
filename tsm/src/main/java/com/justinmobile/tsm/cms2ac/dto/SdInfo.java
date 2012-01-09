package com.justinmobile.tsm.cms2ac.dto;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.subArray;

public class SdInfo {

	/**
	 * 不可变空间安全域
	 */
	public static final int TYPE_UNFIXED_SPACE = 0x00;

	/**
	 * 可变空间安全域
	 */
	public static final int TYPE_FIXED_SPACE = 0x80;

	/**
	 * 可接受迁移安全域
	 */
	public static final int TYPE_EXTRODITABLE = 0x00;

	/**
	 * 不接受迁移安全域
	 */
	public static final int TYPE_UNEXTRODITABLE = 0x02;

	private int type;

	private int securityAttribute;

	private int securityChannel;

	private int volatileDataSpace;

	private int noneVolatileDataSpace;

	public SdInfo(byte[] data) {
		byte[] sdInfo = TlvDto.findTlvSingleValue(data, "82");

		this.type = binaryToInt(subArray(sdInfo, 0, 1));
		this.securityAttribute = binaryToInt(subArray(sdInfo, 1, 2));
		this.securityChannel = binaryToInt(subArray(sdInfo, 2, 3));

		if ((type & TYPE_FIXED_SPACE) != 0) {
			this.volatileDataSpace = binaryToInt(subArray(sdInfo, 3, 5));
			this.noneVolatileDataSpace = binaryToInt(subArray(sdInfo, 5, 9));
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSecurityAttribute() {
		return securityAttribute;
	}

	public void setSecurityAttribute(int securityAttribute) {
		this.securityAttribute = securityAttribute;
	}

	public int getSecurityChannel() {
		return securityChannel;
	}

	public void setSecurityChannel(int securityChannel) {
		this.securityChannel = securityChannel;
	}

	public int getVolatileDataSpace() {
		return volatileDataSpace;
	}

	public void setVolatileDataSpace(int volatileDataSpace) {
		this.volatileDataSpace = volatileDataSpace;
	}

	public int getNoneVolatileDataSpace() {
		return noneVolatileDataSpace;
	}

	public void setNoneVolatileDataSpace(int noneVolatileDataSpace) {
		this.noneVolatileDataSpace = noneVolatileDataSpace;
	}
}
