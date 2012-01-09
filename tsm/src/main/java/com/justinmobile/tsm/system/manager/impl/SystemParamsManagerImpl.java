package com.justinmobile.tsm.system.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.system.dao.SystemParamsDao;
import com.justinmobile.tsm.system.domain.SystemParams;
import com.justinmobile.tsm.system.manager.SystemParamsManager;

@Service("systemParamsManager")
public class SystemParamsManagerImpl extends EntityManagerImpl<SystemParams, SystemParamsDao> implements SystemParamsManager {

	@Autowired
	private SystemParamsDao systemParamsDao;

	@Override
	public List<SystemParams> getParamsByType(String type) throws PlatformException {
		try {
			return systemParamsDao.findByProperty("type", type);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<String> getAllParamType() throws PlatformException {
		try {
			return systemParamsDao.getAllParamType();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean checkExistByTypeAndKey(SystemParams param) throws PlatformException {
		try {
			return systemParamsDao.checkExistByTypeAndKey(param);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}