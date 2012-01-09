package com.justinmobile.tsm.system.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.tsm.system.dao.SystemParamsDao;
import com.justinmobile.tsm.system.domain.SystemParams;

@Repository("systemParamsDao")
public class SystemParamsDaoHibernate extends EntityDaoHibernate<SystemParams, Long> implements SystemParamsDao {

	@Override
	public List<String> getAllParamType() {
		String hsql = "select distinct sp.type from " + SystemParams.class.getName() + " as sp";
		return find(hsql);
	}

	@Override
	public boolean checkExistByTypeAndKey(SystemParams param) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("type", MatchType.EQ, PropertyType.S, param.getType()));
		filters.add(new PropertyFilter("key", MatchType.EQ, PropertyType.S, param.getKey()));
		List<SystemParams> params = find(filters);
		if (CollectionUtils.isEmpty(params)) {
			return false;
		} else {
			if (params.size() <= 1) {
				Long id = params.get(0).getId();
				if (id.equals(param.getId())) {
					return false;
				}
			}
		}
		return true;
	}
}