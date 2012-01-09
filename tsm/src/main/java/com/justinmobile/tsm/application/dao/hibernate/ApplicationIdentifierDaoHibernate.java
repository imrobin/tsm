package com.justinmobile.tsm.application.dao.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.ApplicationIdentifierDao;
import com.justinmobile.tsm.application.domain.ApplicationIdentifier;

@Repository("aidDao")
public class ApplicationIdentifierDaoHibernate extends EntityDaoHibernate<ApplicationIdentifier, Long> implements ApplicationIdentifierDao {

	@Override
	public void saveApplicationIdentifiers(List<ApplicationIdentifier> list) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		
		for(int index = 0; index < list.size(); index++) {
			ApplicationIdentifier aid = list.get(index);
			session.saveOrUpdate(aid);
//			if(index % 20 == 0) {
//				session.flush();
//				session.clear();
//			}
		}
		session.flush();
		session.clear();
		tx.commit();
		session.close();
	}
}
