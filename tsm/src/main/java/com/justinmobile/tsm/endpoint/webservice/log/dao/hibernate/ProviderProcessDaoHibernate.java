package com.justinmobile.tsm.endpoint.webservice.log.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;
import com.justinmobile.tsm.endpoint.webservice.log.dao.ProviderProcessDao;

@Repository("providerProcessDao")
public class ProviderProcessDaoHibernate extends EntityDaoHibernate<ProviderProcess, Long> implements ProviderProcessDao {

	@Override
	public ProviderProcess getBySessionIdThatNotVisited(String sessionId) {
		String hql = "from " + ProviderProcess.class.getName() + " as pa where pa.sessionId = :sessionId and pa.visited = :visited";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("sessionId", sessionId);
		values.put("visited", Boolean.FALSE);

		return findUnique(hql, values);
	}
}
