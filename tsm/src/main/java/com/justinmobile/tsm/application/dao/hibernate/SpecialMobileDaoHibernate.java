package com.justinmobile.tsm.application.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.SpecialMobileDao;
import com.justinmobile.tsm.application.domain.SpecialMobile;

@Repository("specialMobileDao")
public class SpecialMobileDaoHibernate extends EntityDaoHibernate<SpecialMobile, Long> implements SpecialMobileDao {
	
}