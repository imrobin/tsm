package com.justinmobile.tsm.endpoint.webservice.log.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.endpoint.webservice.log.domain.MethodLog;

public interface MethodLogDao extends EntityDao<MethodLog, Long> {

	MethodLog getLog(String sessionId, String seqNum);

	void removeAll();

}
