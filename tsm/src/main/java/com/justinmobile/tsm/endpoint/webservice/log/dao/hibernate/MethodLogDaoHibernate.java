package com.justinmobile.tsm.endpoint.webservice.log.dao.hibernate;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.endpoint.webservice.log.dao.MethodLogDao;
import com.justinmobile.tsm.endpoint.webservice.log.domain.MethodLog;

@Repository("methodLogDao")
public class MethodLogDaoHibernate extends EntityDaoHibernate<MethodLog, Long> implements MethodLogDao {

	@Override
	public MethodLog getLog(String sessionId, String seqNum) {
		String hql = "from " + MethodLog.class.getName() + " as log where log.sessionId = ?  and log.seqNum = ?";
		return findUniqueEntity(hql, sessionId, seqNum);
	}

	@Override
	public void removeAll() {
		Query query = createQuery("delete from " + MethodLog.class.getName());
		query.executeUpdate();
	}

}
