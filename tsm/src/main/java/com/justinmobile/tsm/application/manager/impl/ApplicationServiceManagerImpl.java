package com.justinmobile.tsm.application.manager.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.ApplicationServiceDao;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.ApplicationService;
import com.justinmobile.tsm.application.domain.ApplicationService.BusinessPlatformInterface;
import com.justinmobile.tsm.application.manager.ApplicationServiceManager;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;

@Service("applicationServiceManager")
public class ApplicationServiceManagerImpl extends EntityManagerImpl<ApplicationService, ApplicationServiceDao> implements
		ApplicationServiceManager {

	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private SecurityDomainDao sdDao;
	@Autowired
	private SpBaseInfoDao spDao;
	@Autowired
	private ApplicationServiceDao asDao;

	@Override
	public List<KeyValue> getSpName() {

		try {
			return spDao.getSpName();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<KeyValue> getAppNameBySp(Long spId) {
		try {
			return appDao.getAppNameBySp(spId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<KeyValue> getSdNameBySp(Long spId) {

		try {
			return sdDao.getSdNameBySp(spId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<ApplicationService> getAppSerForIndex(Page<ApplicationService> page, Map<String, Object> values) {

		try {
			return asDao.getAppSerForIndex(page, values);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public ApplicationService getByAidAndServiceName(String aid, String serviceName) {

		try {
			return asDao.getByAidAndServiceName(aid, serviceName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isAuthorized(String aid, BusinessPlatformInterface businessPlatformeInterface) {
		try {
			ApplicationService applicationService = asDao.getByAidAndServiceName(aid, businessPlatformeInterface.getValue());
			return null != applicationService;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
