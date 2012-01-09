package com.justinmobile.tsm.application.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.tsm.application.domain.LoadModule;

public class LoadModuleUtils {
	/**
	 * 创建LoadModule对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         aid：指定的<br/>
	 */
	public static LoadModule createDefualt(String aid) {
		LoadModule lm = new LoadModule();
		lm.setAid(aid);
		return lm;
	}

	/**
	 * 创建LoadModule对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         aid：随机的32位数字字符<br/>
	 */
	public static LoadModule createDefualt() {
		LoadModule lm = new LoadModule();
		lm.setAid(RandomStringUtils.randomNumeric(32));
		return lm;
	}
}
