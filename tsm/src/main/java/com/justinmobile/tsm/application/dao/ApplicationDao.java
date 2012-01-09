package com.justinmobile.tsm.application.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationType;

public interface ApplicationDao extends EntityDao<Application, Long> {

	Page<Application> advanceSearch(Page<Application> page,
			Map<String, String> paramMap);

	Page<Application> recommendAppList(Page<Application> page, SysUser currentUser);

	Page<Application> getDownloadableApps(Page<Application> page,
			String cardNo, Map<String, ?> filters);

	Page<Application> findByAppType(Page<Application> page, Long parentId);

	List<KeyValue> getSpHasApp();

	List<Application> getAppBySp(Long spId);
	
	List<Application> getAppBySd(Long sdId);
	
	List<KeyValue> getAppNameBySp(Long spId);

	List<Application> getApplistByTypeIncludeChildTypeOrderByDownloadCount(ApplicationType at);

}