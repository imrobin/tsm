package com.justinmobile.tsm.system.dao.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.system.dao.MobileSectionDao;
import com.justinmobile.tsm.system.domain.MobileSection;

@Repository("mobileSectionDao")
public class MobileSectionDaoHibernate extends EntityDaoHibernate<MobileSection, Long> implements MobileSectionDao {

	public void ImportData(List<MobileSection> list) throws HibernateException {
		Session session = getSession();
		for (int i = 0; i < list.size(); i++) {
			session.save(list.get(i));
			if (i != 0 && i % 20 == 0) {
				session.flush();
				session.clear();
			}
		}
	}

	@Override
	public void removeAll(String[] ids) {
		Session session = getSession();
		for (int i = 0; i < ids.length; i++) {
			MobileSection ms = (MobileSection) getSession().get(MobileSection.class, Long.valueOf(ids[i]));
			if (ms != null) {
				getSession().delete(ms);
			}
			if (i != 0 && i % 20 == 0) {
				session.flush();
				session.clear();
			}
		}
	}
}