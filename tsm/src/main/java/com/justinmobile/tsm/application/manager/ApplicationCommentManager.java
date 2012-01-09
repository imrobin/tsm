package com.justinmobile.tsm.application.manager;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationComment;

public interface ApplicationCommentManager extends EntityManager<ApplicationComment>{
	
	int countComments(long appId) throws PlatformException;

	boolean isCommented(long appId) throws PlatformException;
	
	ApplicationComment getByAppIdAndCustomerId(long appId, long customerId) throws PlatformException;

}