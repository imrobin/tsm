package com.justinmobile.tsm.system.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.system.dao.SysOperateLogDao;
import com.justinmobile.tsm.system.domain.SysOperateLog;
import com.justinmobile.tsm.system.manager.SysOperateLogManager;

@Service("sysOperateLogManager")
public class SysOperateLogManagerImpl extends EntityManagerImpl<SysOperateLog, SysOperateLogDao> implements SysOperateLogManager {

	@SuppressWarnings("unused")
	@Autowired
	private SysOperateLogDao sysOperateLogDao;}