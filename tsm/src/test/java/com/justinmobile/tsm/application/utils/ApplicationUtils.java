package com.justinmobile.tsm.application.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.tsm.application.domain.Application;

public class ApplicationUtils {

	/**
	 * 创建Application对象用于测试
	 * 
	 * @param aid
	 *            应用的AID
	 * @return 返回安全域，除以下字段外，都null<br/>
	 *         aid：指定的<br/>
	 *         name：test <br/>
	 *         statistics：新创建的
	 */
	public static Application createDefult(String aid) {
		Application app = new Application();
		app.setAid(aid);
		app.setName("test app");
		app.creatNewStatistics();
		return app;
	}

	/**
	 * 创建Application对象用于测试
	 * 
	 * @param aid
	 *            应用的AID
	 * @return 返回安全域，除以下字段外，都null<br/>
	 *         aid：随机的32位数字字符<br/>
	 *         name：test <br/>
	 *         statistics：新创建的
	 */
	public static Application createDefult() {
		Application app = new Application();
		app.setAid(RandomStringUtils.randomNumeric(32));
		app.setName("测试应用 ");
		app.setBusinessPlatformUrl("http://localhost:8080/tsm/services/ProviderWebService?wsdl");
		app.setServiceName("ProviderWebService");
		app.creatNewStatistics();
		return app;
	}
}
