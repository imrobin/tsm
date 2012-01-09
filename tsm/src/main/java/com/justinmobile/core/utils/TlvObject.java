package com.justinmobile.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TlvObject {

	private static final Logger log = LoggerFactory.getLogger(TlvObject.class);

	private Map<String, byte[]> content = new HashMap<String, byte[]>();

	private static String formatTag(String tag) {
		return tag.toUpperCase();
	}

	public static TlvObject parse(byte[] src) {
		return parse(src, 1, 1);
	}

	public static TlvObject parse(byte[] src, final int tLength, final int lLength) {
		if (null == src) {
			// TODO 异常不明确
			throw new RuntimeException();
		}
		if (0 == tLength) {
			// TODO 异常不明确
			throw new RuntimeException();
		}
		if (0 == lLength) {
			// TODO 异常不明确
			throw new RuntimeException();
		}

		TlvObject tlv = new TlvObject();

		int index = 0;

		log.debug("\n" + "开始解析" + "\n");
		log.debug("\n" + "length: " + src.length + "\n");
		while ((index + 1) < src.length) {
			int tIndex = index;
			int lIndex = index + tLength;
			int vIndex = lIndex + lLength;

			log.debug("\n" + "tIndex: " + tIndex + "\n");
			log.debug("\n" + "lIndex: " + lIndex + "\n");
			log.debug("\n" + "vIndex: " + vIndex + "\n");

			// 保证源数据中有完整的t和l字段
			if (vIndex > src.length) {
				// TODO 异常不明确
				throw new RuntimeException();
			}
			String t = ConvertUtils.byteArray2HexString(ArrayUtils.subarray(src, tIndex, lIndex));// 获取t字段的值
			int vLength = ConvertUtils.byteArray2Int(ArrayUtils.subarray(src, lIndex, vIndex));// 获取l字段的值

			int nextTlvIndex = vIndex + vLength;

			log.debug("\n" + "tag: " + t + "\n");
			log.debug("\n" + "lenght: " + vLength + "\n");
			log.debug("\n" + "nextTlvIndex: " + nextTlvIndex + "\n");
			// 保证源数据中有完整的v字段
			if (nextTlvIndex > src.length) {
				// TODO 异常不明确
				throw new RuntimeException();
			}
			byte[] v = ArrayUtils.subarray(src, vIndex, nextTlvIndex);
			if (log.isDebugEnabled()) {
				log.debug("\n" + "value: " + ConvertUtils.byteArray2HexString(v) + "\n");
			}
			tlv.content.put(formatTag(t), v);// 获取v字段的值

			index = nextTlvIndex;
		}
		return tlv;
	}

	public static TlvObject parse(String tlv) {
		return parse(ConvertUtils.hexString2ByteArray(tlv));
	}

	/**
	 * 根据tag查找value
	 * 
	 * @param tag
	 *            tag，十六进制字符串形式
	 * @return tag对应的value，byte[]形式
	 */
	public byte[] getByTag(String tag) {
		String formattedTag = formatTag(tag);
		byte[] value = content.get(formattedTag);
		if (null == value) {
			return new byte[] {};
		} else {
			return value;
		}
	}

	/**
	 * 为指定tag添加值<br/>
	 * 如果在添加前，指定的tag已经存在，则新添加的部分在原有部分之后
	 * 
	 * @param formattedTag
	 *            指定的tag，十六进制字符串形式
	 * @param value
	 *            value，byte[]的形式
	 */
	public void add(String tag, byte[] value) {
		if (StringUtils.isBlank(tag)) {
			// TODO 异常不明确
			throw new RuntimeException();
		}

		if (null == value) {
			// TODO 异常不明确
			throw new RuntimeException();
		}

		String formattedTag = formatTag(tag);
		byte[] exsitValue = content.get(formattedTag);
		if (null == exsitValue) {
			content.put(formattedTag, value);
		} else {
			content.put(formattedTag, ArrayUtils.addAll(exsitValue, value));
		}
	}

	/**
	 * 为指定tag添加值<br/>
	 * 如果在添加前，指定的tag已经存在，则新添加的部分在原有部分之后
	 * 
	 * @param tag
	 *            指定的tag，十六进制字符串形式
	 * @param value
	 *            value，十六进制字符串形式
	 */
	public void add(String tag, String value) {
		add(tag, ConvertUtils.hexString2ByteArray(value));
	}

	/**
	 * 为指定tag添加值<br/>
	 * 如果在添加前，指定的tag已经存在，则新添加的部分在原有部分之后
	 * 
	 * @param tag
	 *            指定的tag，十六进制字符串形式
	 * @param value
	 *            value，TlvObject对象形式
	 */
	public void add(String tag, TlvObject value) {
		add(tag, value.build());
	}

	/**
	 * 将TlvObject对象组装为十六进制TLV字符串
	 * 
	 * @param lLength
	 *            TLV字符串length的字节数
	 * @return hexString
	 */
	public String build(final int lLength) {
		StringBuffer tlvBuffer = new StringBuffer();
		for (Entry<String, byte[]> e : content.entrySet()) {
			StringBuffer sb = new StringBuffer(e.getKey());

			byte[] value = e.getValue();

			String hexLength = ConvertUtils.int2HexString(value.length, lLength * 2);
			sb.append(hexLength);

			String hexValue = ConvertUtils.byteArray2HexString(value);
			sb.append(hexValue);

			tlvBuffer.append(sb);
		}

		return tlvBuffer.toString().toUpperCase();
	}

	/**
	 * 将TlvObject对象组装为十六进制TLV字符串length的字节数，length的字节数为1
	 * 
	 * @return hexString
	 */
	public String build() {
		return build(1);
	}

}
