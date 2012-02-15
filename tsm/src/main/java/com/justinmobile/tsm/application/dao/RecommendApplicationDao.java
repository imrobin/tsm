package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.RecommendApplication;
import com.justinmobile.tsm.customer.domain.Customer;

public interface RecommendApplicationDao extends EntityDao<RecommendApplication, Long> {

	Page<RecommendApplication> findRecommendApplication(Page<RecommendApplication> page, List<PropertyFilter> filters, SysUser currentUser,
			Customer customer, boolean local);

	/**
	 * 根据应用查找记录
	 * 
	 * @param application
	 *            应用
	 * @return 满足条件的记录
	 */
	List<RecommendApplication> getByApplication(Application application);

	Page<RecommendApplication> recommendAppListForMobile(Page<RecommendApplication> page, String cardNo, SysUser sysUser, Customer customer);
}