package com.justinmobile.log.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.log.dao.OperateLogParamDao;
import com.justinmobile.log.domain.OperateLogParam;

@Repository("operateLogParamDao")
public class OperateLogParamDaoHibernate extends EntityDaoHibernate<OperateLogParam, Long> implements OperateLogParamDao {

}
