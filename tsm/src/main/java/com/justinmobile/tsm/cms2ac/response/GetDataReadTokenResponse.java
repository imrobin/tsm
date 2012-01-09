package com.justinmobile.tsm.cms2ac.response;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.TlvObject;
import com.justinmobile.tsm.cms2ac.engine.ApduEngine;

public class GetDataReadTokenResponse extends GetDataResponse {

	private String dataTag = ConvertUtils.int2HexString(ApduEngine.GET_DATA_CMD_P1P2_TOKEN, 2 * 2);

	private String tokenTag = "01";

	private String imsiTag = "02";

	private byte[] token;

	private byte[] imsi;

	public byte[] getToken() {
		return token;
	}

	public void parseData() {
		TlvObject data = TlvObject.parse(getData(), 2, 1);

		TlvObject content = TlvObject.parse(data.getByTag(dataTag));
		this.token = content.getByTag(tokenTag);
		this.imsi = content.getByTag(imsiTag);
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public byte[] getImsi() {
		return imsi;
	}

	public void setImsi(byte[] imsi) {
		this.imsi = imsi;
	}
}
