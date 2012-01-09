package com.justinmobile.core.utils;

import static com.justinmobile.core.utils.ConvertUtils.byte2HexString;
import static com.justinmobile.core.utils.ConvertUtils.byte2Int;
import static com.justinmobile.core.utils.ConvertUtils.byteArray2HexString;
import static com.justinmobile.core.utils.ConvertUtils.hexString2Byte;
import static com.justinmobile.core.utils.ConvertUtils.hexString2ByteArray;
import static com.justinmobile.core.utils.ConvertUtils.hexString2Int;
import static com.justinmobile.core.utils.ConvertUtils.int2HexString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.justinmobile.core.exception.PlatformException;

/**
 * <sup></sup>
 * 
 * @author JazGung
 */
public class ConvertUtilsTest {

	private final String hexStringUpperCase = "0123456789ABCDEF";
	private final String hexStringLowerCase = "0123456789abcdef";
	private final byte[] byteArray = new byte[] { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
			(byte) 0xAB, (byte) 0xCD, (byte) 0xEF };

	@Test
	public void testByte2HexString() {
		String expect = "EF";
		String actual = byte2HexString((byte) 0xEF);

		assertEquals(expect, actual.toUpperCase());
	}

	@Test
	public void testByteArray2HexString() {
		String actual = byteArray2HexString(byteArray);

		assertEquals(hexStringUpperCase, actual.toUpperCase());
	}

	@Test
	public void testHexString2byte() {
		byte expect = (byte) 0xEF;
		byte actual = hexString2Byte("EF");

		assertEquals(expect, actual);
	}

	@Test
	public void testHexString2ByteArray() {
		byte[] actualUpperCase = hexString2ByteArray(hexStringUpperCase);
		byte[] actualLowerCase = hexString2ByteArray(hexStringLowerCase);

		assertArrayEquals(byteArray, actualUpperCase);
		assertArrayEquals(byteArray, actualLowerCase);
	}

	@Test
	public void testHexString2byteException() {
		// hexString的长度小于2
		try {
			hexString2Byte("1");
			fail();
		} catch (RuntimeException e) {
		}

		// hexString的长度大于2
		try {
			hexString2Byte("123");
			fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testInt2HexStringDefualtLength() {
		int i = 0x05;

		String expect = "05";
		String actual = int2HexString(i);

		assertEquals(expect, actual);
	}

	@Test
	public void testInt2Byte() {
		int i = 0xFF;

		byte expect = (byte) i;
		byte actual = ConvertUtils.int2Byte(i);

		assertEquals(expect, actual);
	}

	@Test
	public void testInt2ByteArraySpecifiedCount() {
		int i = 0xFF;

		byte[] expect1 = new byte[] { (byte) i };
		byte[] actual1 = ConvertUtils.int2ByteArray(i, 1);
		assertArrayEquals(expect1, actual1);

		byte[] expect2 = new byte[] { (byte) 0x00, (byte) i };
		byte[] actual2 = ConvertUtils.int2ByteArray(i, 2);
		assertArrayEquals(expect2, actual2);

		byte[] expect3 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) i };
		byte[] actual3 = ConvertUtils.int2ByteArray(i, 3);
		assertArrayEquals(expect3, actual3);

		byte[] expect4 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) i };
		byte[] actual4 = ConvertUtils.int2ByteArray(i, 4);
		assertArrayEquals(expect4, actual4);
	}

	@Test
	public void testInt2HexStringException() {
		// stringLength<1
		try {
			int2HexString(0, 0);
			fail();
		} catch (RuntimeException e) {
		}

		// stringLength>8
		try {
			int2HexString(0, 10);
			fail();
		} catch (RuntimeException e) {
		}

		// stringLength%2!=0
		try {
			int2HexString(0, 5);
			fail();
		} catch (RuntimeException e) {
		}

		// i<0
		try {
			int2HexString(-1, 1);
			fail();
		} catch (RuntimeException e) {
		}

		// i>2^stringLength-1
		try {
			int2HexString(Integer.MAX_VALUE, 6);
			fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testInt2HexStringSpecifiedLength() {
		int i = 0x05;

		String expect1 = "05";
		String actual1 = int2HexString(i, 2);
		assertEquals(expect1, actual1);

		String expect2 = "0005";
		String actual2 = int2HexString(i, 4);
		assertEquals(expect2, actual2);

		String expect3 = "000005";
		String actual3 = int2HexString(i, 6);
		assertEquals(expect3, actual3);

		String expect4 = "00000005";
		String actual4 = int2HexString(i, 8);
		assertEquals(expect4, actual4);
	}

	@Test
	public void testByte2Int() {
		int expect = 0x80;
		int actual = byte2Int((byte) expect);

		assertEquals(expect, actual);
	}

	@Test
	public void testHexString2Int() {
		int expect = 0x80;
		int actual = hexString2Int("80");

		assertEquals(expect, actual);
	}

	@Test
	public void testHexString2IntExceptin() {
		List<String> datas = new ArrayList<String>();

		datas.add("1234567890");
		datas.add("87654321");

		for (String data : datas) {
			try {
				hexString2Int(data);
				fail();
			} catch (PlatformException e) {

			}
		}
	}
}
