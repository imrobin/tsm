package com.justinmobile.tsm.endpoint.webservice.log.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.endpoint.webservice.log.dao.MethodLogDao;
import com.justinmobile.tsm.endpoint.webservice.log.domain.MethodLog;
import com.justinmobile.tsm.endpoint.webservice.log.manager.MethodLogManager;

@Service("methodLogManager")
public class MethodLogManagerImpl extends EntityManagerImpl<MethodLog, MethodLogDao> implements MethodLogManager {

	@Autowired
	private MethodLogDao methodLogDao;

	@Override
	public MethodLog getLog(String sessionId, String seqNum) throws PlatformException {
		try {
			return methodLogDao.getLog(sessionId, seqNum);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void removeAll() throws PlatformException {
		try {
			methodLogDao.removeAll();
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
