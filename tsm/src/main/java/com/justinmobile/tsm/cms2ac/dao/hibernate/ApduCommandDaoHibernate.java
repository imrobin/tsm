package com.justinmobile.tsm.cms2ac.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.tsm.cms2ac.dao.ApduCommandDao;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.core.dao.EntityDaoHibernate;

@Repository("apduCommandDao")
public class ApduCommandDaoHibernate extends EntityDaoHibernate<ApduCommand, Long> implements ApduCommandDao {
}