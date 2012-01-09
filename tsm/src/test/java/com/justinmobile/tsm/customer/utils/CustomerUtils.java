package com.justinmobile.tsm.customer.utils;

import com.justinmobile.security.domain.SysUserUtils;
import com.justinmobile.tsm.customer.domain.Customer;

public class CustomerUtils {

	/**
	 * 创建对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         sysUser：SysUserUtils.createRandom()<br/>
	 */
	public static Customer createDefult() {
		Customer customer = new Customer();
		customer.setSysUser(SysUserUtils.createRandom());
		return customer;
	}
}
