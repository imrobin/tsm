package com.justinmobile.core.utils;

import org.junit.Assert;
import org.junit.Test;

import com.justinmobile.tsm.application.domain.AppletInstallParams;

public class AppletInstallParamsTest {

	@Test
	public void testBuile1() {
		String expert = "C9081234567890ABCDEFEF08C7020100C8020010";

		AppletInstallParams installParams = new AppletInstallParams();
		installParams.setCustomerParams("1234567890ABCDEF");
		installParams.setVolatileDateSpace(256);
		installParams.setNonVolatileDateSpace(16);

		String actual = "";
		try {
			actual = installParams.build();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("安装参数", expert, actual);
	}

	@Test
	public void testBuile2() {
		String expert = "C900EF08C7020100C8020010";

		AppletInstallParams installParams = new AppletInstallParams();
		installParams.setCustomerParams(null);
		installParams.setVolatileDateSpace(256);
		installParams.setNonVolatileDateSpace(16);

		String actual = "";
		try {
			actual = installParams.build();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("安装参数", expert, actual);
	}

	@Test
	public void testParse() {
		String hexParam = "C9081234567890ABCDEFEF08C7020100C8020010";

		AppletInstallParams actual = null;
		try {
			actual = AppletInstallParams.parse(hexParam);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("定制参数", "1234567890ABCDEF", actual.getCustomerParams());
		Assert.assertEquals("可变空间", 0x0100, actual.getVolatileDateSpace());
		Assert.assertEquals("不可变空间", 0x0010, actual.getNonVolatileDateSpace());
	}
}
