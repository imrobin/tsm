package com.justinmobile.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LvObject {
	private static final Logger log = LoggerFactory.getLogger(LvObject.class);

	private List<byte[]> values = new ArrayList<byte[]>();

	public String build() {
		return build(1);
	}

	public String build(int lLength) {
		StringBuilder sb = new StringBuilder();
		for (byte[] value : values) {
			int intLength = value.length;

			String hexLength = ConvertUtils.int2HexString(intLength, lLength * 2);
			String hexValue = ConvertUtils.byteArray2HexString(value);

			log.debug("\n" + "length: " + hexLength + ",value: " + hexValue + "\n");

			sb.append(hexLength).append(hexValue);
		}
		return sb.toString();
	}

	public static LvObject parse(byte[] src) {
		return parse(src, 1);
	}

	public static LvObject parse(byte[] src, final int lLength) {
		LvObject lv = new LvObject();

		if (null == src) {
			// TODO 异常不明确
			throw new RuntimeException();
		}
		if (0 == lLength) {
			// TODO 异常不明确
			throw new RuntimeException();
		}

		int index = 0;

		log.debug("\n" + "开始解析" + "\n");
		log.debug("\n" + "length: " + src.length + "\n");
		while ((index + 1) < src.length) {
			int lIndex = index;
			int vIndex = index + lLength;

			log.debug("\n" + "lIndex: " + lIndex + "\n");
			log.debug("\n" + "vIndex: " + vIndex + "\n");

			int vLength = ConvertUtils.byteArray2Int(ArrayUtils.subarray(src, lIndex, vIndex));// 获取l字段的值

			int nextLvIndex = vIndex + vLength;// 保证源数据中有完整的v字段
			if (nextLvIndex > src.length) {
				// TODO 异常不明确
				throw new RuntimeException();
			}

			byte[] v = ArrayUtils.subarray(src, vIndex, nextLvIndex);
			if (log.isDebugEnabled()) {
				log.debug("\n" + "value: " + ConvertUtils.byteArray2HexString(v) + "\n");
			}

			lv.values.add(v);

			index = nextLvIndex;
		}

		return lv;
	}

	public byte[] getByIndex(int index) {
		byte[] value = null;

		if (0 <= index && index <= values.size()) {
			value = values.get(index);
		}

		if (null == value) {
			value = new byte[] {};
		}

		return value;
	}

	public void add(String value) {
		add(ConvertUtils.hexString2ByteArray(value));
	}

	public void add(byte[] value) {
		values.add(value);
	}

	public void add(LvObject value) {
		add(value.build());
	}

	public void add(TlvObject value) {
		add(value.build());
	}
}
