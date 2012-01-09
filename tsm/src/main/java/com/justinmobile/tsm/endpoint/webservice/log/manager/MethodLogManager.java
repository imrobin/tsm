package com.justinmobile.tsm.endpoint.webservice.log.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.endpoint.webservice.log.domain.MethodLog;

@Transactional
public interface MethodLogManager extends EntityManager<MethodLog> {
	
	@Transactional(readOnly = true)
	MethodLog getLog(String sessionId, String seqNum) throws PlatformException;

	void removeAll() throws PlatformException;

}
