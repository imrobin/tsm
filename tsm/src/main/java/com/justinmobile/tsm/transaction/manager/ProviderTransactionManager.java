package com.justinmobile.tsm.transaction.manager;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.transaction.domain.ProviderTransaction;

public interface ProviderTransactionManager extends EntityManager<ProviderTransaction> {
	
	ProviderTransaction getBySessionId(String sessionId) throws PlatformException;

}
