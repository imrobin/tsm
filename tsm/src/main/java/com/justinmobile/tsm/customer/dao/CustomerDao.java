package com.justinmobile.tsm.customer.dao;



import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.customer.domain.Customer;
 
public interface CustomerDao extends EntityDao<Customer, Long> {

	Page<Customer> findPageByMobileNo(Page<Customer> page, String mobileNo);
}