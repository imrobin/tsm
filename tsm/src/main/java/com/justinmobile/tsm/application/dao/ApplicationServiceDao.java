
package com.justinmobile.tsm.application.dao;

import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.ApplicationService;


public interface ApplicationServiceDao extends EntityDao<ApplicationService, Long> {
	public Page<ApplicationService> getAppSerForIndex(Page<ApplicationService> page,
			Map<String, Object> values);
	public ApplicationService getByAidAndServiceName(String aid,String serviceName);

}



