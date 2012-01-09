package com.justinmobile.tsm.transaction.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.transaction.dao.ProviderTransactionDao;
import com.justinmobile.tsm.transaction.domain.ProviderTransaction;
import com.justinmobile.tsm.transaction.manager.ProviderTransactionManager;

@Service("providerTransaction")
public class ProviderTransactionManagerImpl extends EntityManagerImpl<ProviderTransaction, ProviderTransactionDao> implements ProviderTransactionManager {

	@Autowired
	private ProviderTransactionDao providerTransactionDao;
	
	@Override
	public ProviderTransaction getBySessionId(String sessionId) throws PlatformException {
		try {
			return providerTransactionDao.findUniqueByProperty("sessionId", sessionId);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
