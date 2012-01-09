package com.justinmobile.tsm.fee.manager.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.web.KeyLongValue;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.fee.dao.FeeRuleFunctionDao;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;
import com.justinmobile.tsm.fee.manager.FeeRuleFunctionManager;
import com.justinmobile.tsm.transaction.dao.LocalTransactionDao;

@Service("feeRuleFunctionManager")
public class FeeRuleFunctionManagerImpl extends
		EntityManagerImpl<FeeRuleFunction, FeeRuleFunctionDao> implements
		FeeRuleFunctionManager {
	@Autowired
	private FeeRuleFunctionDao frfDao;
	@Autowired
	private SecurityDomainDao sdDao;
	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private LocalTransactionDao ltDao;

	@Override
	public List<KeyValue> getSpNameHasApp() {
		try {
			return appDao.getSpHasApp();
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
	public List<SecurityDomain> getSdBySp(Long spId) {

		try {
			return sdDao.getSdBySp(spId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Application> getAppBySp(Long spId) {

		try {
			return appDao.getAppBySp(spId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<KeyLongValue> getTransByAidAndVersion(String aid,
			String version, String start, String end) {

		try {
			return ltDao.getTransByAidAndVersion(aid, version, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public FeeRuleFunction getPerFrf(Long spId) {
		try {
			return frfDao.getPerFrf(spId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<FeeRuleFunction> getFrfpForIndex(Page<FeeRuleFunction> page,
			Map<String, Object> values) {

		try {
			return frfDao.getFrfpForIndex(page, values);
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
	public FeeRuleFunction getFrfBySpAndGranularity(Long spId,
			Integer granularity) {

		try {
			return frfDao.getFrfBySpAndGranularity(spId, granularity);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

}
