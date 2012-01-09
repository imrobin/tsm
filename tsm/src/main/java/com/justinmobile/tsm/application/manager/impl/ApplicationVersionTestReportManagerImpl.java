package com.justinmobile.tsm.application.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationVersionTestReportDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionTestReportManager;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Service("onlineTestManger")
public class ApplicationVersionTestReportManagerImpl extends EntityManagerImpl<ApplicationVersionTestReport, ApplicationVersionTestReportDao> implements ApplicationVersionTestReportManager {
	
	@Autowired
	private ApplicationVersionTestReportDao ApplicationVersionOnlineTestResultDao;
	
	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Override
	public ApplicationVersionTestReport getReportByAppver(Long appVerId) {
		try {
			ApplicationVersion av = applicationVersionManager.load(appVerId);
			return ApplicationVersionOnlineTestResultDao.getReportByAppver(av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationVersionTestReport> findByAppver(ApplicationVersion av) {
		
		try {
			return ApplicationVersionOnlineTestResultDao.findByProperty("appVer", av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<ApplicationVersionTestReport> findByAppverAndTestpass(Page<ApplicationVersionTestReport> page, Long appVerId) {
		try {
			ApplicationVersion av = applicationVersionManager.load(appVerId);
			return ApplicationVersionOnlineTestResultDao.findByAppverAndTestpass(page, av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationVersionTestReport> findByAppVerAndCardBase(ApplicationVersion av, CardBaseInfo cbi) {
		try {
			return ApplicationVersionOnlineTestResultDao.findByAppVerAndCardBase(av,cbi);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	

}