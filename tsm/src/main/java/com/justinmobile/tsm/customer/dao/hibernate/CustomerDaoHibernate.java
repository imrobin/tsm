package com.justinmobile.tsm.customer.dao.hibernate;

import org.springframework.stereotype.Repository;


import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.customer.dao.CustomerDao;
import com.justinmobile.tsm.customer.domain.Customer;

@Repository("customerDao")
public class CustomerDaoHibernate extends EntityDaoHibernate<Customer, Long> implements CustomerDao {

	@Override
	public Page<Customer> findPageByMobileNo(Page<Customer> page, String mobileNo) {
		String hql = "from " + Customer.class.getName() + " as customer where customer.sysUser.mobile like ?";
		return this.findPage(page, hql,'%' +  mobileNo + '%');
	}
	
}