package com.justinmobile.core.utils;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

public class HexUtils {

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	/**
	 * 将byte[]编码为hexString
	 * @param src byte[]
	 * @return hexString
	 */
	public static String encode(byte[] src) {
		return org.apache.commons.codec.binary.Hex.encodeHexString(src);
	}

	/**
	 * 将hexString解码为byte[]
	 * @param src hexString
	 * @return hexString
	 */
	@SuppressWarnings("deprecation")
	public static byte[] decode(String data) {
		return ByteUtils.hexStringToBytes(data);
	}

	/**
	 * 验证String是否满足十六进制的条件 如果验证失败，抛出异常
	 * 
	 * @param src
	 *            待验证的字符串
	 */
	public static void validate(String src) {
		// 待验证的字符串不能为空
		if (null == src) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		// 长度为偶数
		if (0 != (src.length() % 2)) {
			throw new PlatformException(PlatformErrorCode.HEX_STRING_ODD_LENGTH);
		}

		// 所有字符都在集合{0、1、2、3、4、5、6、7、8、9、a、b、c、d、e、f、A、B、C、D、E、F}中
		for (int i = 0; i < src.length(); i++) {
			if (!isHexChar(src.charAt(i))) {
				throw new PlatformException(PlatformErrorCode.HEX_STRING_ERROR_CHAR);
			}
		}
	}

	/**
	 * 验证字符是否满足十六进制的条件
	 * 
	 * @param hexChar
	 *            待验证的字符
	 * @return 验证结果，true表示符合十六进制的条件，false表示不符合十六进制的条件
	 */
	public static boolean isHexChar(char hexChar) {
		// 字符是否在集合{0、1、2、3、4、5、6、7、8、9、a、b、c、d、e、f、A、B、C、D、E、F}中
		hexChar = Character.toUpperCase(hexChar);
		for (char c : HEX_CHARS) {
			if (c == hexChar) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将整数转换为长度为1 byte[]的hexString
	 * 
	 * @param i
	 *            待转换的整数
	 * @return hexString，表示长度为1的byte[]
	 */

	public static String toHexString(int i) {
		return toHexString(i, 1);
	}

	/**
	 * 将整数转换为指定byte长度的hexString
	 * 
	 * @param i
	 *            待转换的整数
	 * @param byteCount
	 *            转换后的hexString所表示的byte[]长度
	 * @return hexString，表示长度为byteCount的byte[]
	 */
	public static String toHexString(int i, int byteCount) {
		if ((byteCount < 1) || (byteCount > 4)) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		int maxValue = (int) Math.pow(2, 8 * byteCount) - 1;
		if ((i < 0) || (i > maxValue)) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		String hex = Integer.toHexString(i);
		if (0 != (hex.length() % 2)) {
			hex = "0" + hex;
		}

		int targetLength = byteCount * 2;
		int paddingLength = targetLength - hex.length();
		StringBuffer sb = new StringBuffer();
		for (int loop = 0; loop < paddingLength; loop++) {
			sb.append('0');
		}
		return sb.append(hex).toString();
	}

	/**
	 * 将十六进制的字符串解码为byte
	 * @param hex hexString
	 * @return byte
	 */
	public static byte decodeToByte(String hex) {
		validate(hex);

		if (2 != hex.length()) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		return decode(hex)[0];
	}

	/**
	 * 将byte编码为hexString
	 * @param src byte
	 * @return hexString
	 */
	public static String encode(byte src) {
		return encode(new byte[] { src });
	}
}
