package com.justinmobile.tsm.cms2ac.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.tsm.cms2ac.dao.Cms2acParamDao;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.core.dao.EntityDaoHibernate;

@Repository("cms2acParamDao")
public class Cms2acParamDaoHibernate extends EntityDaoHibernate<Cms2acParam, Long> implements Cms2acParamDao {
}