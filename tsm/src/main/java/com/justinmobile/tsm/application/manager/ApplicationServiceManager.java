package com.justinmobile.tsm.application.manager;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;

import com.justinmobile.core.utils.web.KeyValue;

import com.justinmobile.tsm.application.domain.ApplicationService;
import com.justinmobile.tsm.application.domain.ApplicationService.BusinessPlatformInterface;

public interface ApplicationServiceManager extends EntityManager<ApplicationService> {

	List<KeyValue> getSpName();

	List<KeyValue> getAppNameBySp(Long spId);

	List<KeyValue> getSdNameBySp(Long spId);

	public Page<ApplicationService> getAppSerForIndex(Page<ApplicationService> page, Map<String, Object> values);

	public ApplicationService getByAidAndServiceName(String aid, String serviceName);

	/**
	 * AID对应的应用或安全域是否有指定操作的授权？
	 * 
	 * @param aid
	 *            应用或安全域的AID
	 * @param businessPlatformInterface
	 *            指定的操作
	 * @return true-有授权<br/>
	 *         false-无授权
	 */
	boolean isAuthorized(String aid, BusinessPlatformInterface businessPlatformInterface);

}
