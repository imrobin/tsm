package com.justinmobile.core.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

/**
 * 工具类，负责所有从一种类型转换到另一种类型的功能
 * 
 * @author JazGung
 * 
 */
public class ConvertUtils {

	/**
	 * 将byte转换为hexString
	 * 
	 * @param src
	 *            byte
	 * @return hexString byte
	 */
	public static String byte2HexString(byte src) {
		return byteArray2HexString(new byte[] { src });
	}

	/**
	 * 将byte[]转换为hexString
	 * 
	 * @param src
	 *            byte[]
	 * @return hexString，英文字符均为大写
	 */
	public static String byteArray2HexString(byte[] src) {
		return org.apache.commons.codec.binary.Hex.encodeHexString(src).toUpperCase();
	}

	/**
	 * 将hexString转换为byte
	 * 
	 * @param hex
	 *            hexString
	 * @return byte byte
	 */
	public static byte hexString2Byte(String hex) {
		HexUtils.validate(hex);

		if (2 != hex.length()) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		return hexString2ByteArray(hex)[0];
	}

	/**
	 * 将hexString转换为byte[]
	 * 
	 * @param src
	 *            hexString
	 * @return hexString
	 */
	@SuppressWarnings("deprecation")
	public static byte[] hexString2ByteArray(String data) {
		return ByteUtils.hexStringToBytes(data);
	}

	/**
	 * 将int转换为byte
	 * 
	 * @param i
	 *            int，0<=i<=255
	 * @return byte
	 */
	public static byte int2Byte(int i) {
		return int2ByteArray(i, 1)[0];
	}

	/**
	 * 将int转换为指定长度byte[]
	 * 
	 * @param i
	 *            int，0<=i<=255
	 * @return byte[]
	 */
	public static byte[] int2ByteArray(int i, int byteCount) {
		if ((byteCount < 1) || (byteCount > 4)) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		long maxValue = (long) Math.pow(2, 8 * byteCount) - 1;
		if ((i < 0) || (i > maxValue)) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		String hexString = int2HexString(i, byteCount * 2);
		return hexString2ByteArray(hexString);
	}

	/**
	 * 将int转换为长度为2的hexString
	 * 
	 * @param i
	 *            int
	 * @return hexString，长度为2
	 */

	public static String int2HexString(int i) {
		return int2HexString(i, 2);
	}

	/**
	 * 将int转换为指定长度的hexString<br />
	 * 指定的hexString的长度必须是偶数，否则抛出异常<br />
	 * 转换后的hexString长度小于指定的长度，用0补齐
	 * 
	 * @param i
	 *            int，0<=int<=16<sup>stringLength</sup>-1，即(0<=int<=2<sup>4×
	 *            stringLength</sup>-1)
	 * @param stringLength
	 *            hexString的长度
	 * @return hexString，长度为stringLength，英文字符均为大写
	 */
	public static String int2HexString(int i, int stringLength) {
		if ((stringLength < 1) || (stringLength > 8) || (0 != (stringLength % 2))) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		long maxValue = (long) Math.pow(16, stringLength) - 1;
		if ((i < 0) || (i > maxValue)) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		String hex = Integer.toHexString(i);
		if (0 != (hex.length() % 2)) {
			hex = "0" + hex;
		}

		StringBuffer sb = new StringBuffer();
		for (int loop = hex.length(); loop < stringLength; loop++) {
			sb.append('0');
		}
		return sb.append(hex).toString().toUpperCase();
	}

	/**
	 * 将long转换为指定长度的hexString<br />
	 * 指定的hexString的长度必须是偶数，否则抛出异常<br />
	 * 转换后的hexString长度小于指定的长度，用0补齐
	 * 
	 * @param l
	 *            long，0<=long<=16<sup>stringLength</sup>-1，即(0<=long<=2<sup>4×
	 *            stringLength</sup>-1)
	 * @param stringLength
	 *            hexString的长度
	 * @return hexString，长度为stringLength，英文字符均为大写
	 */
	public static String long2HexString(long l, int stringLength) {
		if ((stringLength < 1) || (stringLength > 16) || (0 != (stringLength % 2))) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		long maxValue = (long) Math.pow(16, stringLength) - 1;
		if ((l < 0) || (l > maxValue)) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		String hex = Long.toHexString(l);
		if (0 != (hex.length() % 2)) {
			hex = "0" + hex;
		}

		StringBuffer sb = new StringBuffer();
		for (int loop = hex.length(); loop < stringLength; loop++) {
			sb.append('0');
		}
		return sb.append(hex).toString().toUpperCase();
	}

	/**
	 * 将byte转换为int
	 * 
	 * @param src
	 *            byte
	 * @return int
	 */
	public static int byte2Int(byte src) {
		return hexString2Int(byte2HexString(src));
	}

	/**
	 * 将hexString转换为int
	 * 
	 * @param src
	 *            hexString
	 * @return int
	 */
	public static int hexString2Int(String src) {
		HexUtils.validate(src);

		if (8 < src.length()) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		} else if (8 == src.length()) {
			char c = src.charAt(0);
			int i = hexString2Int("0" + c);
			if (8 <= i) {
				throw new PlatformException(PlatformErrorCode.DEFAULT);
			}
		}
		return Integer.parseInt(src, 16);
	}

	/**
	 * 将byte[]转换为int
	 * 
	 * @param src
	 *            byte[]
	 * @return
	 */
	public static int byteArray2Int(byte[] src) {
		String hexString = byteArray2HexString(src);
		return hexString2Int(hexString);
	}

	/**
	 * 将file转换为byte[]
	 * 
	 * @param src
	 *            表示文件File的对象
	 * @return
	 */
	public static byte[] file2ByteArray(File src) {
		try {
			if (!src.exists()) {// TODO 临时设置为文件不存在时返回长度为0的数组
				return new byte[0];
			}

			if (!src.isFile()) {
				throw new PlatformException(PlatformErrorCode.DEFAULT);
			}

			return FileUtils.readFileToByteArray(src);
		} catch (IOException e) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}
	}

	/**
	 * 将file转换为byte[]
	 * 
	 * @param src
	 *            表示文件File的路径
	 * @return
	 */
	public static byte[] file2ByteArray(String src) {
		return file2ByteArray(new File(src));

	}
}
