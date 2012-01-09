package com.justinmobile.core.utils;

import org.junit.Assert;
import org.junit.Test;

public class TlvObjectTest {

	@Test
	public void testBuild() {
		TlvObject c6 = new TlvObject();
		c6.add("c6", "017D");

		TlvObject c7 = new TlvObject();
		c7.add("c7", "0000");

		TlvObject c8 = new TlvObject();
		c8.add("c8", "0800");

		TlvObject ef = new TlvObject();
		ef.add("ef", c6);
		ef.add("ef", c7);
		ef.add("ef", c8);

		Assert.assertEquals("组装的HEX TLV", "EF0CC602017DC7020000C8020800".toUpperCase(), ef.build());
	}

	@Test
	public void testParse() {
		TlvObject tlvEf = TlvObject.parse("EF0CC602017DC7020000C8020800");
		byte[] bytesEf = tlvEf.getByTag("ef");
		String hexEf = ConvertUtils.byteArray2HexString(bytesEf);
		System.out.println("hexEf: " + hexEf);
		Assert.assertEquals("ef", "C602017DC7020000C8020800".toUpperCase(), hexEf);

		TlvObject tlv = TlvObject.parse(bytesEf);
		byte[] bytesC6 = tlv.getByTag("c6");
		String hexC6 = ConvertUtils.byteArray2HexString(bytesC6);
		Assert.assertEquals("c6", "017D".toUpperCase(), hexC6);

		byte[] bytesC7 = tlv.getByTag("c7");
		String hexC7 = ConvertUtils.byteArray2HexString(bytesC7);
		Assert.assertEquals("c7", "0000".toUpperCase(), hexC7);

		byte[] bytesC8 = tlv.getByTag("c8");
		String hexC8 = ConvertUtils.byteArray2HexString(bytesC8);
		Assert.assertEquals("c8", "0800".toUpperCase(), hexC8);

	}
}
