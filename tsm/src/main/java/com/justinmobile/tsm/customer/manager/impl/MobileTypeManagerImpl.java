package com.justinmobile.tsm.customer.manager.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.customer.dao.MobileTypeDao;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.MobileTypeManager;

@Service("mobileTypeManager")
public class MobileTypeManagerImpl extends
		EntityManagerImpl<MobileType, MobileTypeDao> implements
		MobileTypeManager {

	@Autowired
	private MobileTypeDao mobileTypeDao;

	@Override
	public List<String> getAllBrand() {
		List<String> list;
		try {
			list = mobileTypeDao.getMobileBrand();
			return list;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public List<String> getTypeByBrand(String brand) {

		List<String> list;
		try {
			list = mobileTypeDao.getTypeByBrand(brand);
			return list;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<MobileType> getAllMobile(final Page<MobileType> page) {
		try {
			return mobileTypeDao.getAllMobile(page);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<MobileType> getMobileByBrand(final Page<MobileType> page,
			String brand) {

		try {
			return mobileTypeDao.getMobileByBrand(page, brand);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<MobileType> getMobileByKeyword(final Page<MobileType> page,
			String keyword) {

		try {
			return mobileTypeDao.getMobileByKeyword(page, keyword);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<MobileType> getMobileByBrandAndType(
			final Page<MobileType> page, String brand, String type) {

		try {
			return mobileTypeDao.getMobileByBrandAndType(page, brand, type);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<String> getSuggestByKeyword(String keyword) {

		return mobileTypeDao.getSuggestByKeyword(keyword);
	}

	@Override
	public List<MobileType> getTypeAndValueByBrand(String brand) {
		try {
			return mobileTypeDao.getTypeAndValueByBrand(brand);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<MobileType> getMobileByKeywordForIndex(Page<MobileType> page,
			Map<String, Object> values) {
		try {
			return mobileTypeDao.getMobileByKeywordForIndex(page, values);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}