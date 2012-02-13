package com.justinmobile.tsm.endpoint.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.endpoint.dao.PushSmsDao;
import com.justinmobile.tsm.endpoint.domain.PushSms;

@Repository("pushSmsDao")
public class PushSmsDaoHibernate extends EntityDaoHibernate<PushSms, Long> implements PushSmsDao {

	@Override
	public PushSms getByPushSerial(String pushSerial) {
		String hql = "from " + PushSms.class.getName() + " where serial = ?";
		return findUniqueEntity(hql, pushSerial);
	}
}
