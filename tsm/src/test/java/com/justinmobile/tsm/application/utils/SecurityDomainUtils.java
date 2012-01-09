package com.justinmobile.tsm.application.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.tsm.application.domain.SecurityDomain;

public class SecurityDomainUtils {

	/**
	 * 创建一个SecurityDomain的对象用于测试
	 * 
	 * @return 返回安全域<br>
	 *         model：主安全域 status：已发布
	 */
	public static SecurityDomain createDefult(String aid) {
		SecurityDomain sd = new SecurityDomain();
		sd.setAid(aid);
		sd.setStatus(SecurityDomain.STATUS_PUBLISHED);
		sd.setModel(SecurityDomain.MODEL_ISD);
		return sd;
	}

	/**
	 * 创建一个SecurityDomain的对象用于测试
	 * 
	 * @return 返回安全域<br>
	 *         model：主安全域 status：已发布
	 */
	public static SecurityDomain createDefult() {
		SecurityDomain sd = new SecurityDomain();
		sd.setAid(RandomStringUtils.randomNumeric(32));
		sd.setStatus(SecurityDomain.STATUS_PUBLISHED);
		sd.setModel(SecurityDomain.MODEL_ISD);
		sd.setPrivilege(0);
		return sd;
	}
}
