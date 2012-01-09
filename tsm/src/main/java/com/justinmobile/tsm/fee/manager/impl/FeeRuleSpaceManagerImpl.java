package com.justinmobile.tsm.fee.manager.impl;

import java.math.BigDecimal;
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
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardApplicationDao;

import com.justinmobile.tsm.fee.dao.FeeRuleSpaceDao;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;

import com.justinmobile.tsm.fee.manager.FeeRuleSpaceManager;
import com.justinmobile.tsm.history.dao.SubscribeHistoryDao;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.transaction.dao.LocalTransactionDao;

@Service("feeRuleSpaceManager")
public class FeeRuleSpaceManagerImpl extends
		EntityManagerImpl<FeeRuleSpace, FeeRuleSpaceDao> implements
		FeeRuleSpaceManager {
	@Autowired
	private FeeRuleSpaceDao frpDao;
	@Autowired
	private SecurityDomainDao sdDao;
	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private SubscribeHistoryDao shDao;
	@Autowired
	private LocalTransactionDao ltDao;
	@Autowired
	private CardApplicationDao caDao;
	@Autowired
	private SpBaseInfoDao spDao;

	public List<KeyValue> getSpNameHasSd() {
		try {
			return sdDao.getSpNameHasSd();
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
	public Long getCountByAppAndDate(Application app, String start, String end) {

		try {
			return shDao.getCountByAppAndDate(app, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<String> getCardNoCreateSD(String aid, String start, String end) {

		try {
			return ltDao.getCreateSD(aid, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public FeeRuleSpace getFrpByAidAndVersion(String aid, String version) {
		try {
			return frpDao.getFrpByAidAndVersion(aid, version);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Long getAppVerSize(ApplicationVersion appVer) {

		try {
			return caDao.getAppVerSize(appVer);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

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
	public List<Application> getAppBySd(Long sdId) {

		try {
			return appDao.getAppBySd(sdId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<String> getCardNoByAppAndDate(Application app, String start,
			String end) {

		try {
			return shDao.getCardNoByAppAndDate(app, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Long getCountByOnlyAppVerAndDate(ApplicationVersion appVer,
			String start, String end) {

		try {
			return shDao.getCountByOnlyAppVerAndDate(appVer, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<BigDecimal> getCountByMultiAppVerAndDate(Application app,
			String start, String end) {

		try {
			return shDao.getCustomerCardInfoByMultiAppVerAndDate(app, start,
					end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<FeeRuleSpace> getFrpForIndex(Page<FeeRuleSpace> page,
			Map<String, Object> values) {
		try {
			return frpDao.getFrpForIndex(page, values);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Long getAppVerIdByCustomerCardInfoAndDate(Application app,
			Long customerCardInfoId, String start, String end) {

		try {
			return shDao.getAppVerIdByCustomerCardInfoAndDate(app,
					customerCardInfoId, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public FeeRuleSpace getFrpByAid(String aid) {

		try {
			return frpDao.getFrpByAid(aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
