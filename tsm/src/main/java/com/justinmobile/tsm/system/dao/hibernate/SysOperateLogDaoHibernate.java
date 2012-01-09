package com.justinmobile.tsm.system.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.system.dao.SysOperateLogDao;
import com.justinmobile.tsm.system.domain.SysOperateLog;

@Repository("sysOperateLogDao")
public class SysOperateLogDaoHibernate extends EntityDaoHibernate<SysOperateLog, Long> implements SysOperateLogDao {
}