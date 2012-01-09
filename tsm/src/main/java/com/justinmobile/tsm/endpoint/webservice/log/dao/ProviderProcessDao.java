package com.justinmobile.tsm.endpoint.webservice.log.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;

public interface ProviderProcessDao extends EntityDao<ProviderProcess, Long> {

	ProviderProcess getBySessionIdThatNotVisited(String sessionId);

}
