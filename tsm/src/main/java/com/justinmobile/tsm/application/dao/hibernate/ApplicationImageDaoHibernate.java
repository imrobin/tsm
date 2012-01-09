package com.justinmobile.tsm.application.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.ApplicationImageDao;
import com.justinmobile.tsm.application.domain.ApplicationImage;

@Repository("applicationImageDao")
public class ApplicationImageDaoHibernate extends EntityDaoHibernate<ApplicationImage, Long> implements ApplicationImageDao {

	@Override
	public ApplicationImage loadById(long id) {
		return this.load(id);
	}
	
}