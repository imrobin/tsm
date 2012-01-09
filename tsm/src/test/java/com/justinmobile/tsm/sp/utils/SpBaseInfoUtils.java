package com.justinmobile.tsm.sp.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.security.domain.SysUserUtils;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

public class SpBaseInfoUtils {

	/**
	 * 创建SpBaseInfo的对象用于测试<br/>
	 * 
	 * @return SpBaseInfo的对象，除以下字段外都为null<br/>
	 *         no：4位随机数字字符<br/>
	 *         name：随机值<br/>
	 *         sysUser：SysUserUtils.createRandom( )
	 *         status：SpBaseInfo.STATUS_AVALIABLE
	 */
	public static SpBaseInfo createDefult() {
		SpBaseInfo sp = new SpBaseInfo();

		sp.setSysUser(SysUserUtils.createDefult());
		sp.setNo(RandomStringUtils.randomAlphanumeric(4));
		sp.setName(RandomStringUtils.random(5, true, false));
		sp.setStatus(SpBaseInfo.STATUS_AVALIABLE);

		return createRandom();
	}

	/**
	 * 创建SpBaseInfo的随机对象用于测试<br/>
	 * 
	 * @return SpBaseInfo的对象，除以下字段外都为null<br/>
	 *         no：4位随机数字字符<br/>
	 *         name：随机值<br/>
	 *         sysUser：SysUserUtils.createRandom()
	 *         status：SpBaseInfo.STATUS_AVALIABLE
	 */
	public static SpBaseInfo createRandom() {
		SpBaseInfo sp = new SpBaseInfo();

		sp.setSysUser(SysUserUtils.createRandom());
		sp.setNo(RandomStringUtils.randomAlphanumeric(4));
		sp.setName(RandomStringUtils.random(5, true, false));
		sp.setStatus(SpBaseInfo.STATUS_AVALIABLE);

		return sp;
	}

	public static SpBaseInfo getNewInstance() {
		SpBaseInfo sp = new SpBaseInfo();

		sp.setAddress("ADDRESS_" + RandomStringUtils.random(5, true, false));
		sp.setName("NAME_" + RandomStringUtils.random(5, true, false));
		sp.setNo("NO_" + RandomStringUtils.random(5, true, false));

		return sp;
	}
}
