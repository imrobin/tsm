package com.justinmobile.tsm.customer.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.customer.domain.Customer;

@Transactional
public interface CustomerManager extends EntityManager<Customer> {
	@Transactional(readOnly = true)
	public Customer getCustomerByUserName(String userName) throws PlatformException;

	public void uploadIcon(String iconUrl, SysUser sysUser) throws PlatformException;

	public void addCustomer(Customer c) throws PlatformException;

	public byte[] getPcImgById(Long customerId);

	public Page<Customer> findPageByMobileNo(Page<Customer> page, String mobileNo);

	public Customer getByUserNameOrEmailOrMobileNo(String proof);

}