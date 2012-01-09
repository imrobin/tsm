package com.justinmobile.log.manager;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;

@Transactional
public interface OperateLogManager extends EntityManager<OperateLog> {

	public void createLog(OperateLog log, Set<OperateLogParam> params) throws PlatformException;
}
