package com.justinmobile.tsm.application.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;

public interface ApplicationVersionDao extends EntityDao<ApplicationVersion, Long> {

	ApplicationVersion getAidAndVersionNo(String aid, String versionNo);

	long hasArchiveRequest(Long appVerId);
	
	List<KeyValue> getAppVerBySp(Long spId);

	Page<ApplicationVersion> findPageByMultParams(Page<ApplicationVersion> page, Map<String, Object> queryParams);
	
	int getUndownloadUserAmountByApplicationVersionWithCardInfo(ApplicationVersion applicationVersion);
	
	int getUndownloadUserAmountByApplicationVersionWithCardSecurityDomain(ApplicationVersion applicationVersion);

	Page<ApplicationVersion> getDownTestFileAppver(Page<ApplicationVersion> page, String appName);

	/**
	 * 根据应用来获取应用版本,同时要求是已发布能使用的
	 * @param app 属于的应用
	 * @return
	 */
	List<ApplicationVersion> getByAppIdWithPublish(Application app);
}