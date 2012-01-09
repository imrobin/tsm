package com.justinmobile.tsm.application.domain;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.TlvObject;

public class LoadParams {

	private int nonVolatileCodeSpace = 0;

	private int volatileDateSpace = 0;

	private int nonVolatileDateSpace = 0;

	public int getNonVolatileCodeSpace() {
		return nonVolatileCodeSpace;
	}

	public void setNonVolatileCodeSpace(int nonVolatileCodeSpace) {
		this.nonVolatileCodeSpace = nonVolatileCodeSpace;
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
	 * 组建加载参数
	 * 
	 * @return 加载参数，十六进制字符串形式
	 */
	public String build() {
		TlvObject c6 = new TlvObject();
		String hexNonVolatileCodeSpace = ConvertUtils.int2HexString(nonVolatileCodeSpace, 2 * 2);
		c6.add("c6", hexNonVolatileCodeSpace);

		TlvObject c7 = new TlvObject();
		String hexVolatileDataSpace = ConvertUtils.int2HexString(volatileDateSpace, 2 * 2);
		c7.add("c7", hexVolatileDataSpace);

		TlvObject c8 = new TlvObject();
		String hexNonVolatileDataSpace = ConvertUtils.int2HexString(nonVolatileDateSpace, 2 * 2);
		c8.add("c8", hexNonVolatileDataSpace);

		TlvObject ef = new TlvObject();
		ef.add("ef", c6);
		ef.add("ef", c7);
		ef.add("ef", c8);

		return ef.build();
	}

	public static LoadParams parse(String hexParams) {
		LoadParams loadParams = new LoadParams();

		TlvObject tlvEf = TlvObject.parse(hexParams);
		byte[] bytesEf = tlvEf.getByTag("ef");

		TlvObject tlv = TlvObject.parse(bytesEf);
		byte[] bytesC6 = tlv.getByTag("c6");
		loadParams.nonVolatileCodeSpace = ConvertUtils.byteArray2Int(bytesC6);

		byte[] bytesC7 = tlv.getByTag("c7");
		loadParams.volatileDateSpace = ConvertUtils.byteArray2Int(bytesC7);

		byte[] bytesC8 = tlv.getByTag("c8");
		loadParams.nonVolatileDateSpace = ConvertUtils.byteArray2Int(bytesC8);

		return loadParams;
	}
}
