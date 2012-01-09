package com.justinmobile.tsm.cms2ac.dto;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.binaryToLong;
import static com.justinmobile.core.utils.ByteUtils.subArray;

public class KeyCounterInfo {

	private int scp02Counter;
	private long scp80Counter;

	public KeyCounterInfo(byte[] data) {
		this.scp02Counter = binaryToInt(subArray(data, 0, 2));
		this.scp80Counter = binaryToLong(subArray(data, 2, 6));
	}

	public int getScp02Counter() {
		return scp02Counter;
	}

	public void setScp02Counter(int scp02Counter) {
		this.scp02Counter = scp02Counter;
	}

	public long getScp80Counter() {
		return scp80Counter;
	}

	public void setScp80Counter(long scp80Counter) {
		this.scp80Counter = scp80Counter;
	}

}
