package com.justinmobile.core.utils;

import junit.framework.Assert;

import org.junit.Test;

public class LvObjectTest {

	@Test
	public void testBuild() {
		LvObject lv = new LvObject();

		{
			TlvObject c9 = new TlvObject();
			c9.add("ef", "01");

			TlvObject value = new TlvObject();
			value.add("c9", c9);

			lv.add(value);
		}

		{
			LvObject value = new LvObject();
			value.add("01");

			lv.add(value);
		}

		lv.add("01");
		lv.add(new byte[] { (byte) 0x01 });

		String value = lv.build();
		Assert.assertEquals("05C903EF010102010101010101", value);
	}

	@Test
	public void testParse() {
		LvObject lv = LvObject.parse(ConvertUtils.hexString2ByteArray("05C903EF010102010101010101"));

		Assert.assertEquals("C903EF0101", ConvertUtils.byteArray2HexString(lv.getByIndex(0)));
		Assert.assertEquals("0101", ConvertUtils.byteArray2HexString(lv.getByIndex(1)));
		Assert.assertEquals("01", ConvertUtils.byteArray2HexString(lv.getByIndex(2)));
		Assert.assertEquals("01", ConvertUtils.byteArray2HexString(lv.getByIndex(3)));

	}
}
