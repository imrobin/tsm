package com.justinmobile.tsm.sp.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.sp.dao.RecommendSpDao;
import com.justinmobile.tsm.sp.domain.RecommendSp;

@Repository("recommendSpDao")
public class RecommendSpDaoHibernate extends EntityDaoHibernate<RecommendSp, Long> implements RecommendSpDao {

}