
package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.dao.ApplicationServiceDao;
import com.justinmobile.tsm.application.domain.ApplicationService;


@Repository("applicationServiceDao")
public class ApplicationServiceDaoHibernate extends EntityDaoHibernate<ApplicationService, Long> implements ApplicationServiceDao{
	@Override
	public Page<ApplicationService> getAppSerForIndex(Page<ApplicationService> page,
			Map<String, Object> values) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> _values = new HashMap<String, Object>(2);
		hql.append("select appSer from ").append(ApplicationService.class.getName())
				.append(" as appSer where 1=1 ");
		if (!StringUtils.isEmpty(values.get("spName").toString())) {
			hql.append(" and  appSer.sp.name like :spName escape ' ' ");
			String spName = values.get("spName").toString()
					.replaceAll(" ", "  ").replaceAll("%", " %")
					.replaceAll("_", " _");
			spName = "%" + spName + "%";
			_values.put("spName", spName);
		}
		if (!StringUtils.isEmpty(values.get("appName").toString())){
			hql.append(" and appSer.appName like :appName escape ' ' ");
			String appName = values.get("appName").toString()
			       .replace(" ", "  ").replace("%", " %").replaceAll("_", " _");
			appName = "%" + appName + "%";
			_values.put("appName", appName);
		}
		if(!StringUtils.isEmpty(values.get("type").toString())){
			hql.append(" and appSer.type=:type");
			_values.put("type", values.get("type"));
		}
		return findPage(page, hql.toString(), _values);

	}
	
	@Override
	public ApplicationService getByAidAndServiceName(String aid,
			String serviceName) {
		StringBuilder hql = new StringBuilder();
		hql.append("select appSer from ").append(ApplicationService.class.getName())
				.append(" as appSer where appSer.aid='").append(aid).append("'")
		        .append(" and appSer.serviceName='").append(serviceName).append("'");
		return findUniqueEntity(hql.toString());
	}

}



