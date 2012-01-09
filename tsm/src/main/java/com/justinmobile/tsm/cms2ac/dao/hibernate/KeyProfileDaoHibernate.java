package com.justinmobile.tsm.cms2ac.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.cms2ac.dao.KeyProfileDao;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

@Repository("keyProfileDao")
public class KeyProfileDaoHibernate extends EntityDaoHibernate<KeyProfile, Long> implements KeyProfileDao {
}