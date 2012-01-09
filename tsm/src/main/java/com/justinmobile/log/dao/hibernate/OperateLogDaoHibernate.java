package com.justinmobile.log.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.log.dao.OperateLogDao;
import com.justinmobile.log.domain.OperateLog;

@Repository("operateLogDao")
public class OperateLogDaoHibernate extends EntityDaoHibernate<OperateLog, Long> implements OperateLogDao {

}
