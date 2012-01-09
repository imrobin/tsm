package com.justinmobile.tsm.system.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.system.dao.SysOperateLogParamDao;
import com.justinmobile.tsm.system.domain.SysOperateLogParam;

@Repository("sysOperateLogParamDao")
public class SysOperateLogParamDaoHibernate extends EntityDaoHibernate<SysOperateLogParam, Long> implements SysOperateLogParamDao {
}