package com.justinmobile.security.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.security.dao.SysResourceDao;
import com.justinmobile.security.domain.SysResource;

@Repository("sysResourceDao")
public class SysResourceDaoHibernate extends EntityDaoHibernate<SysResource, Long> implements SysResourceDao {

	@Override
	public SysResource getResourceByFilterString(String filterString) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("filterString", MatchType.EQ, PropertyType.S, filterString));
		List<SysResource> list = find(filters);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else if (list.size() > 1) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		}
		return list.get(0);
	}

}
