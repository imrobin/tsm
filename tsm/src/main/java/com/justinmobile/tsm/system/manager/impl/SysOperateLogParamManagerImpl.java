package com.justinmobile.tsm.system.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.system.dao.SysOperateLogParamDao;
import com.justinmobile.tsm.system.domain.SysOperateLogParam;
import com.justinmobile.tsm.system.manager.SysOperateLogParamManager;

@Service("sysOperateLogParamManager")
public class SysOperateLogParamManagerImpl extends EntityManagerImpl<SysOperateLogParam, SysOperateLogParamDao> implements SysOperateLogParamManager {

	@SuppressWarnings("unused")
	@Autowired
	private SysOperateLogParamDao sysOperateLogParamDao;}