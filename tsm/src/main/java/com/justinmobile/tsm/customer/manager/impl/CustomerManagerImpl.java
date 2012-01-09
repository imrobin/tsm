package com.justinmobile.tsm.customer.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.tsm.customer.dao.CustomerDao;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;

@Service("customerManager")
public class CustomerManagerImpl extends EntityManagerImpl<Customer, CustomerDao> implements CustomerManager {

	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private SysUserManager sysUserManager;

	@Override
	public Customer getCustomerByUserName(String userName) throws PlatformException {
		try {
			return getByUserNameOrEmailOrMobileNo(userName);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public void uploadIcon(String iconUrl, SysUser sysUser) throws PlatformException {
		try {
			Customer customer = customerDao.findUniqueByProperty("sysUser", sysUser);
			customer.setIconUrl(iconUrl);
			super.saveOrUpdate(customer);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void addCustomer(Customer c) throws PlatformException {
		try {

			customerDao.saveOrUpdate(c);

		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public byte[] getPcImgById(Long customerId) {
		try {
			Customer customer = customerDao.load(customerId);
			return customer.getPcIcon();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<Customer> findPageByMobileNo(Page<Customer> page, String mobileNo) {
		try {
			return customerDao.findPageByMobileNo(page, mobileNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Customer getByUserNameOrEmailOrMobileNo(String proof) {
		try {
			SysUser sysUser = sysUserManager.getUserByNameOrMobileOrEmail(proof);
			if (null == sysUser) {
				return null;
			}
			Customer customer = customerDao.load(sysUser.getId());
			return customer;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}