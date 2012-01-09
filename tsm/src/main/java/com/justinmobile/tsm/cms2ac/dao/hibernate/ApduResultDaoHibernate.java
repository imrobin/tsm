package com.justinmobile.tsm.cms2ac.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.tsm.cms2ac.dao.ApduResultDao;
import com.justinmobile.tsm.cms2ac.domain.ApduResult;
import com.justinmobile.core.dao.EntityDaoHibernate;

@Repository("apduResultDao")
public class ApduResultDaoHibernate extends EntityDaoHibernate<ApduResult, Long> implements ApduResultDao {
}