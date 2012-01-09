package com.justinmobile.tsm.cms2ac.dto;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.intToHexBytes;
import static com.justinmobile.core.utils.ByteUtils.rightSubArray;
import static com.justinmobile.core.utils.ByteUtils.subArray;
import static com.justinmobile.core.utils.ByteUtils.toHexString;
import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.justinmobile.core.utils.ConvertUtils;

public class TlvDto {

	private byte tag;

	private int length;

	private String value;

	public TlvDto(byte tag, int length, String value) {
		this.tag = tag;
		this.length = length;
		this.value = value;
	}

	public TlvDto(byte tag, String value) {
		this(tag, value.length() / 2, value);
	}

	public TlvDto(byte tag, byte[] value) {
		this(tag, toHexString(value));
	}

	public TlvDto() {

	}

	public byte[] toByteArray() {
		byte[] buf = new byte[0];
		buf = contactArray(buf, new byte[] { this.tag });
		buf = contactArray(buf, intToHexBytes(this.length, 1));
		buf = contactArray(buf, ConvertUtils.hexString2ByteArray(this.value));
		return buf;
	}

	@Override
	public String toString() {
		return toHexString(toByteArray());
	}

	public static int indexOfTag(byte[] sourceTlv, byte tag) {
		int index = -1;
		for (int i = 0; i < sourceTlv.length; i++) {
			if (sourceTlv[i] == tag) {
				index = i;
				break;
			}
		}
		return index;
	}

	public static byte[] findSingleTlvElement(byte[] sourceTlv, byte tag) {
		byte[] targetTlv = null;
		int index = indexOfTag(sourceTlv, tag);
		if (index == -1) {
			return null;
		}
		byte lengthByte = subArray(sourceTlv, index + 1, index + 2)[0];
		targetTlv = subArray(sourceTlv, index, index + 2 + lengthByte);
		return targetTlv;
	}

	public static List<byte[]> findMultiTlvElement(byte[] sourceTlv, byte tag) {
		List<byte[]> tlvList = new ArrayList<byte[]>();
		byte[] targetTlv = null;
		int i = 0;
		while (i < sourceTlv.length) {
			if (sourceTlv[i] == tag) {
				byte lengthByte = subArray(sourceTlv, i + 1, i + 2)[0];
				targetTlv = subArray(sourceTlv, i, i + 2 + lengthByte);
				tlvList.add(targetTlv);
				i = i + 2 + lengthByte;
			} else {
				i++;
			}
		}
		return tlvList;
	}

	public static byte[] findTlvValue(byte[] sourceTlv, byte tag) {
		byte[] searchedTlv = findSingleTlvElement(sourceTlv, tag);
		if (searchedTlv == null) {
			return null;
		}
		return rightSubArray(searchedTlv, 2);
	}

	/**
	 * 根据tag查找value，如果在data中有多个符合条件的值，返回第一个
	 * 
	 * @param data
	 *            原始数据
	 * @param tag
	 * @return 找到的value
	 */
	public static byte[] findTlvSingleValue(byte[] data, int tag) {

		return ConvertUtils.hexString2ByteArray(findTlvValue(toHexString(data).toUpperCase(), Integer.toHexString(tag).toUpperCase(), 0));

	}

	/**
	 * 根据tag查找value，如果在data中有多个符合条件的值，返回第一个
	 * 
	 * @param data
	 *            原始数据
	 * @param tag
	 * @return 找到的value
	 */
	public static byte[] findTlvSingleValue(byte[] data, String tag) {

		return ConvertUtils.hexString2ByteArray(findTlvValue(toHexString(data).toUpperCase(), tag.toUpperCase(), 0));

	}

	/**
	 * 在指定索引之后根据tag找到第一个符合value
	 * 
	 * @param data
	 *            原始数据
	 * @param tag
	 * @param beginIndex
	 *            索引值
	 * @return 找到的value
	 */
	private static String findTlvValue(String data, String tag, int beginIndex) {
		int index;
		index = data.indexOf(tag, beginIndex);// 指向tag
		index = index + tag.length();// 指向length
		int length = parseInt(data.substring(index, index + 1 * 2), 16);
		index = index + 1 * 2;// 指向value
		String result = data.substring(index, index + length * 2);
		return result;
	}

	/**
	 * 根据tag查找所有value
	 * 
	 * @param data原始数据
	 * @param tag
	 * @return 找到的value的List
	 */
	public static List<byte[]> findTlvMutilValue(byte[] data, String tag) {

		return findTlvMutilValue(toHexString(data), tag);

	}

	/**
	 * 根据tag查找所有value
	 * 
	 * @param data原始数据
	 * @param tag
	 * @return 找到的value的List
	 */
	public static List<byte[]> findTlvMutilValue(String data, String tag) {
		List<byte[]> results = new ArrayList<byte[]>();

		int index = 0;
		while (index < data.length()) {
			String value = findTlvValue(data, tag, index);
			results.add(ConvertUtils.hexString2ByteArray(value));
			index += tag.length() + 2 + value.length();
		}

		return results;
	}

	public static byte[] findTlvValue(byte[] data, int tag) {

		String dataStr = toHexString(data).toUpperCase();
		String tagStr = Integer.toHexString(tag).toUpperCase();
		return ConvertUtils.hexString2ByteArray(findTlvValue(dataStr, tagStr));

	}

	public static String findTlvValue(String data, String tag) {
		int length = 0;
		int index;
		index = data.indexOf(tag);

		index = index + tag.length();// 指向length的第一个字节

		String berStr = data.substring(index, index + 1 * 2);// 将byet[]转换为hexString后，原来的1byte现在占用2byte
		if ("81".equals(berStr)) {// 后续的2个字符代表长度
			length = Integer.parseInt(data.substring(index + 1 * 2, index + 2 * 2), 16);
			index = index + 2 * 2;// 指向value的第一个字符
		} else if ("82".equals(berStr)) {// 后续的4个字符代表长度
			length = Integer.parseInt(data.substring(index + 1 * 2, index + 3 * 2), 16);
			index = index + 3 * 2;// 指向value的第一个字符
		} else {
			length = Integer.parseInt(berStr, 16);
			index = index + 1 * 2;// 指向value的第一个字符
		}

		int begin = index;
		int end = index + length * 2;
		return data.substring(begin, end);
	}

	public static Map<Byte, byte[]> sliptTlv(byte[] data, int index) {
		Map<Byte, byte[]> result = new HashMap<Byte, byte[]>();
		while (index < data.length) {
			byte tag = subArray(data, index, index + 1)[0];
			int length = binaryToInt(subArray(data, index + 1, index + 2));
			byte[] value = subArray(data, index + 2, index + 2 + length);
			index = index + 2 + length;
			result.put(tag, value);
		}
		return result;
	}
}
