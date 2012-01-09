package com.justinmobile.security.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.security.domain.SysResource;

public interface SysResourceDao extends EntityDao<SysResource, Long> {

	SysResource getResourceByFilterString(String filterString);

}
