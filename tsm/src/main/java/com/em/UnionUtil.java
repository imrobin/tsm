package com.em;

/*
 * author: 		guizy
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public final class UnionUtil {

	/*
	 * 16进制的字符串转换成压缩BCD码
	 */
	public static final boolean HexStr2CBCD(byte[] in, byte[] out, int len) {
		byte[] asciiCode = { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

		if (len > in.length) {
			return false;
		}

		if (len % 2 != 0) {
			return false;
		}

		byte[] temp = new byte[len];

		for (int i = 0; i < len; i++) {
			if (in[i] >= 0x30 && in[i] <= 0x39)
				temp[i] = (byte) (in[i] - 0x30);
			else if (in[i] >= 0x41 && in[i] <= 0x46)
				temp[i] = asciiCode[in[i] - 0x41];
			else if (in[i] >= 0x61 && in[i] <= 0x66)
				temp[i] = asciiCode[in[i] - 0x61];
			else
				return false;
		}

		for (int i = 0; i < len / 2; i++) {
			out[i] = (byte) (temp[2 * i] * 16 + temp[2 * i + 1]);
		}

		return true;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param len
	 * @return
	 */
	public static final boolean CBCD2HexStr(byte[] in, byte[] out, int len) {
		byte[] asciiCode = { 0x41, 0x42, 0x43, 0x44, 0x45, 0x46 };

		// System.out.println("len="+len);
		// System.out.println(new String(in));
		if (len > in.length) {
			return false;
		}

		byte[] temp = new byte[2 * len];

		for (int i = 0; i < len; i++) {
			temp[2 * i] = (byte) ((in[i] & 0xf0) / 16);
			temp[2 * i + 1] = (byte) (in[i] & 0x0f);
		}

		for (int i = 0; i < 2 * len; i++) {
			if (temp[i] <= 9 && temp[i] >= 0) {
				out[i] = (byte) (temp[i] + 0x30);
			} else {
				out[i] = asciiCode[temp[i] - 0x0a];
			}
		}

		return true;
	}

	public static final String LeftAddZero(String s, int TotalLen) {
		String sTemp = "";
		int l = s.length();
		if (l >= TotalLen)
			return s;
		else {
			int j = TotalLen - l;

			for (int i = 0; i < j; i++) {
				sTemp += "0";
			}
			sTemp += s;
		}
		return sTemp;
	}

	/*
	 * 获取环境变量
	 */
	public static final Properties getEnvVars() throws IOException {
		Process process = null;
		Properties envVars = new Properties();
		Runtime runtime = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("windows 9") > -1) {
			process = runtime.exec("command.com /c set");
		} else if (OS.indexOf("nt") > -1 || OS.indexOf("windows 2000") > -1 || OS.indexOf("windows xp") > -1) {
			process = runtime.exec("cmd.exe /c set");
		} else if (OS.indexOf("unix") > -1 || OS.indexOf("linux") > -1) {
			process = runtime.exec("/bin/env");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int idx = line.indexOf('=');
			String key = line.substring(0, idx);
			String value = line.substring(idx + 1);
			envVars.setProperty(key.toUpperCase(), value);
		}
		process.destroy();
		return envVars;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static final void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	/**
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static final String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static final byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static final byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static final byte[] BytesCopy(byte[] bytes, int start, int len) throws Exception {
		if (bytes == null || bytes.length < start + len)
			throw new Exception("BytesSub bytes为空，或长度不够！");
		byte[] subBytes = new byte[len];
		System.arraycopy(bytes, start, subBytes, 0, len);
		return subBytes;
	}

	public static final byte[] BytesCopy(byte[] bytes, int start) throws Exception {
		if (bytes == null || bytes.length < start)
			throw new Exception("BytesSub bytes为空，或长度不够！");
		byte[] subBytes = new byte[bytes.length - start];
		System.arraycopy(bytes, start, subBytes, 0, bytes.length - start);
		return subBytes;
	}

	public static final byte[] AllRightZreoTo8Multiple(byte[] bytes) throws Exception {
		if (bytes.length % 8 == 0)
			return bytes;
		int len = bytes.length + 8 - bytes.length % 8;
		byte[] newbytes = new byte[len];
		for (int i = 0; i < len; i++)
			newbytes[i] = 0;
		System.arraycopy(bytes, 0, newbytes, 0, bytes.length);
		return newbytes;
	}

	/**
	 * 二行制转字符串
	 */

	public static final String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	/**
	 * 字符串转二行制
	 */
	public static byte[] hex2byte(String hex) throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] arr = hex.toCharArray();
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			String swap = "" + arr[i++] + arr[i];
			int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = new Integer(byteint).byteValue();
		}
		return b;
	}
}
