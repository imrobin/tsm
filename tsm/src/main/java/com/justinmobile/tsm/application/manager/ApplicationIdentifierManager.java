package com.justinmobile.tsm.application.manager;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationIdentifier;

public interface ApplicationIdentifierManager extends EntityManager<ApplicationIdentifier> {

	public void saveAid(ApplicationIdentifier aid) throws PlatformException;
	
}
