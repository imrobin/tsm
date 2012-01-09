package com.justinmobile.security.domain;

import org.apache.commons.lang.RandomStringUtils;

public class SysUserUtils {

	/**
	 * 创建SysUser对象用于测试
	 * 
	 * @return 返回对象，除以下字段外，都为null<br/>
	 *         userName：justinmoble<br/>
	 *         email：justinmoble@justinmoble.com<br/>
	 *         mobile：02886766638<br/>
	 *         password：0000<br/>
	 *         status：SysUser.USER_STATUS.ENABLED<br/>
	 */
	public static SysUser createDefult() {
		SysUser user = new SysUser();
		user.setUserName("justinmoble");
		user.setEmail("justinmoble@justinmoble.com");
		user.setMobile("02886766638");
		user.setPassword("0000");
		user.setStatus(SysUser.USER_STATUS.ENABLED.getValue());
		return user;
	}

	/**
	 * 创建SysUser对象用于测试
	 * 
	 * @return 返回对象，除以下字段外，都为null<br/>
	 *         userName：随机值<br/>
	 *         email：帐号和域名随机值<br/>
	 *         mobile：11位随机数字字符<br/>
	 *         password：0000<br/>
	 *         status：SysUser.USER_STATUS.ENABLED<br/>
	 */
	public static SysUser createRandom() {
		SysUser user = new SysUser();
		user.setUserName(RandomStringUtils.random(5, true, false));
		user.setEmail(RandomStringUtils.random(5, true, false) + '@' + RandomStringUtils.random(5, true, false) + ".com");
		user.setMobile(RandomStringUtils.randomAlphanumeric(11));
		user.setPassword("0000");
		user.setStatus(SysUser.USER_STATUS.ENABLED.getValue());
		return user;
	}
}
