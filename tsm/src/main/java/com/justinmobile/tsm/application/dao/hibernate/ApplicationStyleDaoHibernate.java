package com.justinmobile.tsm.application.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.ApplicationStyleDao;
import com.justinmobile.tsm.application.domain.ApplicationStyle;

@Repository("applicationStyleDao")
public class ApplicationStyleDaoHibernate extends EntityDaoHibernate<ApplicationStyle, Long> implements ApplicationStyleDao {

}
