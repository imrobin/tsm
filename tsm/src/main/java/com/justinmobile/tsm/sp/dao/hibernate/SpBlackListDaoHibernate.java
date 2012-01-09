package com.justinmobile.tsm.sp.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.sp.dao.SpBlackListDao;
import com.justinmobile.tsm.sp.domain.SpBlackList;

@Repository("spBlackListDao")
public class SpBlackListDaoHibernate extends EntityDaoHibernate<SpBlackList, Long> implements SpBlackListDao {
}