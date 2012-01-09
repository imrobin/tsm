package com.justinmobile.tsm.application.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.tsm.application.domain.LoadFile;

public class LoadFileUtils {

	/**
	 * 创建LoadFile对象用于测试
	 * 
	 * @param aid
	 *            LoadFile的AID
	 * @return 返回安全域，除以下字段外，都null<br/>
	 *         aid：随机的32位数字字符<br/>
	 *         type：LoadFile.TYPE_CMS2AC_FILE
	 */
	public static LoadFile createDefult() {
		LoadFile loadFile = new LoadFile();
		loadFile.setAid(RandomStringUtils.randomNumeric(32));
		loadFile.setType(LoadFile.TYPE_CMS2AC_FILE);
		return loadFile;
	}
}
