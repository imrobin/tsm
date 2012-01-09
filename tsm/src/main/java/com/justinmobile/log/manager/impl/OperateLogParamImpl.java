package com.justinmobile.log.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.log.dao.OperateLogParamDao;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogParamManager;

@Service("operateLogParamManager")
public class OperateLogParamImpl extends EntityManagerImpl<OperateLogParam, OperateLogParamDao> implements OperateLogParamManager {

	@SuppressWarnings("unused")
	@Autowired
	private OperateLogParamDao operateLogParamDao;
}
