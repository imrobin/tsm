package com.justinmobile.tsm.application.utils;

import com.justinmobile.tsm.application.domain.LoadFileVersion;

public class LoadFileVersionUtils {

	/**
	 * 创建对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         loadFile：LoadFileUtils.createDefult()<br/>
	 *         versionNo：1.0.0<br/>
	 */
	public static LoadFileVersion createDefult() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadFile(LoadFileUtils.createDefult());
		lfVer.setVersionNo("1.0.0");
		return lfVer;
	}
}
