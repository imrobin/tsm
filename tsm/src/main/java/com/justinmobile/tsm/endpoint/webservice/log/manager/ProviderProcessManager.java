package com.justinmobile.tsm.endpoint.webservice.log.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;

@Transactional
public interface ProviderProcessManager extends EntityManager<ProviderProcess> {

	ProviderProcess getBySessionIdThatNotVisited(String localSessionId);

}
