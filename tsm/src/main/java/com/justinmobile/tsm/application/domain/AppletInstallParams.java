package com.justinmobile.tsm.application.domain;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.TlvObject;

public class AppletInstallParams {

	private String customerParams = "";

	private int volatileDateSpace = 0;

	private int nonVolatileDateSpace = 0;

	public String getCustomerParams() {
		return customerParams;
	}

	public void setCustomerParams(String customerParams) {
		this.customerParams = customerParams;
	}

	public int getVolatileDateSpace() {
		return volatileDateSpace;
	}

	public void setVolatileDateSpace(int volatileDateSpace) {
		this.volatileDateSpace = volatileDateSpace;
	}

	public int getNonVolatileDateSpace() {
		return nonVolatileDateSpace;
	}

	public void setNonVolatileDateSpace(int nonVolatileDateSpace) {
		this.nonVolatileDateSpace = nonVolatileDateSpace;
	}

	/**
	 * 组建安装参数
	 * 
	 * @return 安装参数，十六进制字符串形式
	 */
	public String build() {
		TlvObject c7 = new TlvObject();
		String hexVolatileDataSpace = ConvertUtils.int2HexString(volatileDateSpace, 2 * 2);
		c7.add("c7", hexVolatileDataSpace);

		TlvObject c8 = new TlvObject();
		String hexNonVolatileDataSpace = ConvertUtils.int2HexString(nonVolatileDateSpace, 2 * 2);
		c8.add("c8", hexNonVolatileDataSpace);

		TlvObject tlv = new TlvObject();
		if (null == customerParams) {
			tlv.add("c9", ConvertUtils.hexString2ByteArray(""));
		} else {
			tlv.add("c9", ConvertUtils.hexString2ByteArray(customerParams));
		}
		tlv.add("ef", c7);
		tlv.add("ef", c8);

		return tlv.build();
	}

	public static AppletInstallParams parse(String hexParams) {
		AppletInstallParams appletInstallParams = new AppletInstallParams();

		TlvObject tlv = TlvObject.parse(hexParams);
		byte[] bytesC9 = tlv.getByTag("c9");
		appletInstallParams.customerParams = ConvertUtils.byteArray2HexString(bytesC9);

		byte[] bytesEf = tlv.getByTag("ef");
		TlvObject tlvEf = TlvObject.parse(bytesEf);

		byte[] bytesC7 = tlvEf.getByTag("c7");
		appletInstallParams.volatileDateSpace = ConvertUtils.byteArray2Int(bytesC7);

		byte[] bytesC8 = tlvEf.getByTag("c8");
		appletInstallParams.nonVolatileDateSpace = ConvertUtils.byteArray2Int(bytesC8);

		return appletInstallParams;

	}
}
