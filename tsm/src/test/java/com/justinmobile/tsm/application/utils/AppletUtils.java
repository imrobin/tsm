package com.justinmobile.tsm.application.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.tsm.application.domain.Applet;

public class AppletUtils {

	/**
	 * 
	 * 创建Applet对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         aid：32位随机数字字符
	 * 
	 */
	public static Applet createDefult() {
		Applet applet = new Applet();
		applet.setAid(RandomStringUtils.randomNumeric(32));
		return applet;
	}
}
