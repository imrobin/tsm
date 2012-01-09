package com.justinmobile.tsm.application.manager;

import java.util.List;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.RecommendApplication;

public interface RecommendApplicationManager extends EntityManager<RecommendApplication> {

	Page<RecommendApplication> findRecommendApplication(Page<RecommendApplication> page, List<PropertyFilter> filters, boolean local);

	/**
	 * 删除指定应用的记录
	 * 
	 * @param application
	 *            指定的应用
	 */
	void removeByApplication(Application application);

	Page<RecommendApplication> recommendAppListForMobile(Page<RecommendApplication> page, String cardNo);
}