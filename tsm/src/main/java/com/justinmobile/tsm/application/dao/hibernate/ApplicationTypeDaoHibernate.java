package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.dao.ApplicationTypeDao;
import com.justinmobile.tsm.application.domain.ApplicationType;

@Repository("applicationTypeDao")
public class ApplicationTypeDaoHibernate extends EntityDaoHibernate<ApplicationType, Long> implements ApplicationTypeDao {

	@Override
	public Page<ApplicationType> recentlyDownLoad(Page<ApplicationType> page, Long parentId) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("parentId", parentId);
		String hsql = "select g from " + ApplicationType.class.getName()
				+ " as g " + " where g.typeLevel=2 and parentType.id = :parentId";
		return findPage(page, hsql, values);
	}

	@Override
	public List<ApplicationType> getShowIndexTypeListOrderById() {
		String hql = "from " + ApplicationType.class.getName() + " as at where at.showIndex = ? order by at.id ";
		return this.find(hql, ApplicationType.SHOW);
	}
}