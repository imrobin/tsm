package com.justinmobile.tsm.application.utils;

import com.justinmobile.tsm.application.domain.ApplicationVersion;

public class ApplicationVersionUtils {

	/**
	 * 创建默认的测试对象
	 * 
	 * @return 除了以下字段外都为null<br/>
	 *         versionNo：1.0.0
	 */
	public static ApplicationVersion createDefult() {
		ApplicationVersion version = new ApplicationVersion();
		version.setVersionNo("1.0.0");
		return version;
	}

}
