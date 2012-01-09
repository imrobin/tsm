package com.justinmobile.tsm.cms2ac.dto;

import static com.justinmobile.core.utils.ByteUtils.toHexString;
import static com.justinmobile.tsm.cms2ac.dto.TlvDto.findTlvSingleValue;

public class CardUniqueIdentifier {
	private String iccid;
	private String imsg;

	public CardUniqueIdentifier(byte[] data) {
		iccid = toHexString(findTlvSingleValue(data, "91"));
		imsg = toHexString(findTlvSingleValue(data, "92"));
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getImsg() {
		return imsg;
	}

	public void setImsg(String imsg) {
		this.imsg = imsg;
	}

}
