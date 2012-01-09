package com.justinmobile.core.utils;

import org.junit.Assert;
import org.junit.Test;

import com.justinmobile.tsm.application.domain.LoadParams;

public class LoadParamsTest {
	@Test
	public void testBuild() {
		LoadParams params = new LoadParams();

		params.setNonVolatileCodeSpace(0x017d);
		params.setVolatileDateSpace(0x0000);
		params.setNonVolatileDateSpace(0x0800);

		String hexParams = params.build();
		Assert.assertEquals("加载参数", "EF0CC602017DC7020000C8020800".toUpperCase(), hexParams);
	}

	@Test
	public void testParse() {
		LoadParams params = LoadParams.parse("EF0CC602017DC7020000C8020800");

		Assert.assertEquals("不可变编码空间", 0x017D, params.getNonVolatileCodeSpace());
		Assert.assertEquals("可变数据空间", 0x0000, params.getVolatileDateSpace());
		Assert.assertEquals("不可变数据空间", 0x0800, params.getNonVolatileDateSpace());
	}
}
