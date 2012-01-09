package com.justinmobile.tsm.history.manager.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.history.dao.SubscribeHistoryDao;
import com.justinmobile.tsm.history.domain.SubscribeHistory;
import com.justinmobile.tsm.history.manager.SubscribeHistoryManager;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Service("subscribeHistoryManager")
public class SubscribeHistoryManagerImpl extends EntityManagerImpl<SubscribeHistory, SubscribeHistoryDao> implements
		SubscribeHistoryManager {

	@Autowired
	private SubscribeHistoryDao subscribeHistoryDao;

	@Autowired
	private SpBaseInfoManager spManager;

	@Autowired
	private SecurityDomainManager sdManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	public SpBaseInfoManager getSpManager() {
		return spManager;
	}

	public void setSpManager(SpBaseInfoManager spManager) {
		this.spManager = spManager;
	}

	public SecurityDomainManager getSdManager() {
		return sdManager;
	}

	public void setSdManager(SecurityDomainManager sdManager) {
		this.sdManager = sdManager;
	}

	@Override
	public Page<SubscribeHistory> recentlyDownLoad(Page<SubscribeHistory> page, Long appId, boolean isRecently) {
		try {
			return subscribeHistoryDao.recentlyDownLoad(page, appId, isRecently);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean hasSubscribed(Page<SubscribeHistory> pageSub, Long appId, boolean b) {
		pageSub = this.recentlyDownLoad(pageSub, appId, b);
		for (SubscribeHistory ac : pageSub.getResult()) {
			SysUser sysUser = ac.getCustomerCardInfo().getCustomer().getSysUser();
			if (sysUser.getUserName().equals(SpringSecurityUtils.getCurrentUserName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public SubscribeHistory getLastSubscribeHistoryByCustomerCardAndApplicationVersion(CustomerCardInfo customerCard,
			ApplicationVersion applicationVersion) {
		List<SubscribeHistory> subscribeHistories = subscribeHistoryDao.getByCustomerCardAndApplicationVersionOrderBySubscribeDateDesc(
				customerCard, applicationVersion);

		if (0 == subscribeHistories.size()) {
			return null;
		} else {
			return subscribeHistories.get(0);
		}

	}

	@Override
	public Page<SubscribeHistory> findPage(Page<SubscribeHistory> page, Map<String, Object> queryParams) throws PlatformException {
		try {
			page = subscribeHistoryDao.findPageByMultiQueryParams(page, queryParams);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return page;
	}

	@Override
	public void subscribeApplication(CardInfo card, ApplicationVersion applicationVersion) {
		try {
			CustomerCardInfo customerCard = customerCardInfoManager.getByCardNo(card.getCardNo());
			SubscribeHistory subscribeHistory = new SubscribeHistory();
			subscribeHistory.setCustomerCardInfo(customerCard);
			subscribeHistory.setApplicationVersion(applicationVersion);
			subscribeHistory.setSubscribeDate(Calendar.getInstance());
			subscribeHistoryDao.saveOrUpdate(subscribeHistory);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void unsubscribeApplication(CardInfo card, ApplicationVersion applicationVersion) {
		try {
			CustomerCardInfo customerCard = customerCardInfoManager.getByCardNo(card.getCardNo());
			SubscribeHistory subscribeHistory = getLastSubscribeHistoryByCustomerCardAndApplicationVersion(customerCard, applicationVersion);
			if (null != subscribeHistory) {
				subscribeHistory.setUnsubscribeDate(Calendar.getInstance());
				subscribeHistoryDao.saveOrUpdate(subscribeHistory);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<SubscribeHistory> listHistoryForCustomer(Page<SubscribeHistory> page, Map<String, Object> paramMap) {
		try {
			return page = subscribeHistoryDao.listHistoryForCustomer(page, paramMap);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}