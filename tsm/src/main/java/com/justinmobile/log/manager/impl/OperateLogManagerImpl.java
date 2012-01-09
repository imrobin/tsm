package com.justinmobile.log.manager.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.log.dao.OperateLogDao;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogManager;

@Service("operateLogManager")
public class OperateLogManagerImpl extends EntityManagerImpl<OperateLog, OperateLogDao> implements OperateLogManager {

	@SuppressWarnings("unused")
	@Autowired
	private OperateLogDao operateLogDao;

	public void createLog(OperateLog log, Set<OperateLogParam> params) throws PlatformException {
		log.setLogParams(params);
		super.saveOrUpdate(log);
	}

}
