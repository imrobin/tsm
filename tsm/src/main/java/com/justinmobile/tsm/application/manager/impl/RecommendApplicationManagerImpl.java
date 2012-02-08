package com.justinmobile.tsm.application.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.dao.RecommendApplicationDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.RecommendApplication;
import com.justinmobile.tsm.application.manager.RecommendApplicationManager;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;

@Service("recommendApplicationManager")
public class RecommendApplicationManagerImpl extends EntityManagerImpl<RecommendApplication, RecommendApplicationDao> implements
		RecommendApplicationManager {

	@Autowired
	private RecommendApplicationDao recommendApplicationDao;
	@Autowired
	private CustomerManager customerManager;
	@Autowired
	private SysUserManager userManager;
	@Autowired
	private CardInfoManager cardInfoManager;

	@Override
	public Page<RecommendApplication> findRecommendApplication(Page<RecommendApplication> page, List<PropertyFilter> filters, boolean local) {
		try {
			Customer customer = customerManager.getCustomerByUserName(SpringSecurityUtils.getCurrentUserName());
			SysUser currentUser = userManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
			return recommendApplicationDao.findRecommendApplication(page, filters, currentUser, customer, local);
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
	public void removeByApplication(Application application) {
		try {
			List<RecommendApplication> recommendApplications = recommendApplicationDao.getByApplication(application);

			for (RecommendApplication recommendApplication : recommendApplications) {
				recommendApplicationDao.remove(recommendApplication);
			}
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
	public Page<RecommendApplication> recommendAppListForMobile(Page<RecommendApplication> page, String cardNo) {
		try {
			CardInfo cardInfo = cardInfoManager.getByCardNo(cardNo);
			SysUser sysUser = userManager.getUserByMobile(cardInfo.getMobileNo());
			return recommendApplicationDao.recommendAppListForMobile(page, cardNo, sysUser);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}