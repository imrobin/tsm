package com.justinmobile.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多tag tlv对象<br/>
 * 支持同一个tag出现多次的tlv对象
 * 
 * @author JazGung
 * 
 */
public class MutilTagTlvObject {

	private static final Logger log = LoggerFactory.getLogger(MutilTagTlvObject.class);

	Map<String, List<ValueEntry>> content = new HashMap<String, List<ValueEntry>>();

	public class ValueEntry {
		private int index;

		private byte[] value;

		public ValueEntry(byte[] value) {
			this.value = value;
		}

		public ValueEntry(int index, byte[] value) {
			this.index = index;
			this.value = value;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public byte[] getValue() {
			return value;
		}

		public void setValue(byte[] value) {
			this.value = value;
		}
	}

	public static MutilTagTlvObject parse(byte[] src) {
		return parse(src, 1, 1);
	}

	private static String formatTag(String tag) {
		return tag.toUpperCase();
	}

	public static MutilTagTlvObject parse(String src) {
		return parse(src, 1, 1);
	}

	private static MutilTagTlvObject parse(String src, int tLength, int lLength) {
		return parse(ConvertUtils.hexString2ByteArray(src), 1, 1);
	}

	public static MutilTagTlvObject parse(byte[] src, int tLength, int lLength) {
		if (null == src) {
			throw new RuntimeException();
		}
		if (0 == tLength) {
			throw new RuntimeException();
		}
		if (0 == lLength) {
			throw new RuntimeException();
		}

		MutilTagTlvObject tlv = new MutilTagTlvObject();

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
				throw new RuntimeException();
			}
			byte[] v = ArrayUtils.subarray(src, vIndex, nextTlvIndex);
			if (log.isDebugEnabled()) {
				log.debug("\n" + "value: " + ConvertUtils.byteArray2HexString(v) + "\n");
			}
			tlv.add(formatTag(t), vIndex, v);// 获取v字段的值

			index = nextTlvIndex;
		}
		return tlv;
	}

	public void add(String tag, byte[] value) {
		List<ValueEntry> values = content.get(tag);
		if (null == values) {
			values = new ArrayList<ValueEntry>();
			content.put(tag, values);
		}
		values.add(new ValueEntry(value));
	}

	public void add(String tag, int index, byte[] value) {
		List<ValueEntry> values = content.get(tag);
		if (null == values) {
			values = new ArrayList<ValueEntry>();
			content.put(tag, values);
		}
		values.add(new ValueEntry(index, value));
	}

	/**
	 * 根据tag查找
	 * 
	 * @param tag
	 *            tag，十六进制字符串形式
	 * @return 
	 * @return tag对应的ValueEntry，List<ValueEntry>形式
	 */
	public List<ValueEntry> getByTag(String tag) {
		String formattedTag = formatTag(tag);
		List<ValueEntry> valueEntries = content.get(formattedTag);

		if (null == valueEntries) {
			valueEntries = new ArrayList<ValueEntry>();
		}
		
		return valueEntries;
	}

	/**
	 * 根据tag查找value
	 * 
	 * @param tag
	 *            tag，十六进制字符串形式
	 * @return tag对应的value，List<byte[]>形式
	 */
	public List<byte[]> getValueByTag(String tag) {
		String formattedTag = formatTag(tag);
		List<byte[]> values = new ArrayList<byte[]>();
		List<ValueEntry> valueEntries = content.get(formattedTag);
		if (null != valueEntries) {
			for (ValueEntry valueEntry : valueEntries) {
				values.add(valueEntry.getValue());
			}
		}
		return values;
	}

	/**
	 * 将MultiTagTlvObject对象组装为十六进制TLV字符串
	 * 
	 * @param lLength
	 *            TLV字符串length的字节数
	 * @return hexString
	 */
	public String build(final int lLength) {
		StringBuffer tlvBuffer = new StringBuffer();
		for (Entry<String, List<ValueEntry>> e : content.entrySet()) {// tag的集合
			List<ValueEntry> valueEntries = e.getValue();
			for (ValueEntry valueEntry : valueEntries) {// 对当前tag的每一个值
				StringBuffer sb = new StringBuffer(e.getKey());

				byte[] value = valueEntry.getValue();
				String hexLength = ConvertUtils.int2HexString(value.length, lLength * 2);
				sb.append(hexLength);

				String hexValue = ConvertUtils.byteArray2HexString(value);
				sb.append(hexValue);

				tlvBuffer.append(sb);
			}
		}

		return tlvBuffer.toString().toUpperCase();
	}

	/**
	 * 将MultiTagTlvObject对象组装为十六进制TLV字符串length的字节数，length的字节数为1
	 * 
	 * @return hexString
	 */
	public String build() {
		return build(1);
	}

}
