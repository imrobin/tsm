package com.justinmobile.tsm.endpoint.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.endpoint.dao.PushSmsDao;
import com.justinmobile.tsm.endpoint.domain.PushSms;
import com.justinmobile.tsm.endpoint.manager.PushSmsManager;

@Service("pushSmsManager")
public class PushSmsManagerImpl extends EntityManagerImpl<PushSms, PushSmsDao> implements PushSmsManager {

	@Autowired
	PushSmsDao pushSmsDao;

	@Override
	public PushSms getByPushSerial(String pushSerial) {
		try {
			return pushSmsDao.getByPushSerial(pushSerial);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
