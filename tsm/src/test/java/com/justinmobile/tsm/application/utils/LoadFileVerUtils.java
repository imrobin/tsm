package com.justinmobile.tsm.application.utils;

import com.justinmobile.tsm.application.domain.LoadFileVersion;

public class LoadFileVerUtils {

	/**
	 * 创建LoadFileVersion对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         loadFile-LoadFileUtils.createDefult()
	 */
	public static LoadFileVersion createDefualt() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadFile(LoadFileUtils.createDefult());
		return lfVer;
	}
}
