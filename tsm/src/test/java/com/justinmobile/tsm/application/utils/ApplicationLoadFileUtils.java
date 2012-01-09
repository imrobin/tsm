package com.justinmobile.tsm.application.utils;

import com.justinmobile.tsm.application.domain.ApplicationLoadFile;

public class ApplicationLoadFileUtils {

	/**
	 * 创建默认的测试对象
	 * 
	 * @return 除了以下字段外都为null<br/>
	 * 
	 */
	public static ApplicationLoadFile createDefult() {
		ApplicationLoadFile appLf = new ApplicationLoadFile();
		return appLf;
	}
}
