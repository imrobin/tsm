package com.justinmobile.tsm.application.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationKeyProfileDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.manager.ApplicationKeyProfileManager;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.dto.KeyInfo;

@Service("applicationKeyProfileManager")
public class ApplicationKeyProfileManagerImpl extends EntityManagerImpl<ApplicationKeyProfile, ApplicationKeyProfileDao> implements
		ApplicationKeyProfileManager {

	@Autowired
	private ApplicationKeyProfileDao applicationKeyProfileDao;

	@Override
	public ApplicationKeyProfile getByApplictionAndKeyInfo(Application application, KeyInfo keyInfo) {
		try {
			return applicationKeyProfileDao.getByApplictionAndKeyInfo(application, keyInfo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}