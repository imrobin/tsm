package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.ApplicationType;

public interface ApplicationTypeDao extends EntityDao<ApplicationType, Long> {

	Page<ApplicationType> recentlyDownLoad(Page<ApplicationType> page, Long parentId);

	List<ApplicationType> getShowIndexTypeListOrderById();
}