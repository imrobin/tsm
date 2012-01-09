package com.justinmobile.tsm.cms2ac.manager.impl;

import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.cms2ac.dao.HsmkeyConfigDao;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;

@Service("hsmkeyConfigManager")
public class HsmkeyConfigManagerImpl extends EntityManagerImpl<HsmkeyConfig, HsmkeyConfigDao> implements HsmkeyConfigManager {

	@Autowired
	private HsmkeyConfigDao hsmkeyConfigDao;

	@Override
	public HsmkeyConfig getByKeyProfileVendor(KeyProfile keyProfile, String vendorName) {
		try {
			return hsmkeyConfigDao.getByKeyProfileVendor(keyProfile, vendorName);
		} catch (PlatformException e) {
			e.printStackTrace();
			throw e;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<HsmkeyConfig> qurySsdRelation(int pageNo, int pageSize, Map<String, String> sortMap, Map<String, Object> filterMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(HsmkeyConfig hsmKeyConfig) throws PlatformException {
		// TODO Auto-generated method stub

	}

	@Override
	public HsmkeyConfig getByKeyProfileVendor(ApplicationKeyProfile keyProfile, String vendorName) {
		try {
			return hsmkeyConfigDao.getByKeyProfileVendor(keyProfile, vendorName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}