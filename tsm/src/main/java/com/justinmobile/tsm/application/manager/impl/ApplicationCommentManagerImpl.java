package com.justinmobile.tsm.application.manager.impl;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.dao.ApplicationCommentDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationComment;
import com.justinmobile.tsm.application.domain.GradeStatistics;
import com.justinmobile.tsm.application.manager.ApplicationCommentManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.GradeStatisticsManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;

@Service("applicationCommentManager")
public class ApplicationCommentManagerImpl extends EntityManagerImpl<ApplicationComment, ApplicationCommentDao> implements ApplicationCommentManager {

	@Autowired
	private ApplicationCommentDao applicationCommentDao;
	
	@Autowired
	private ApplicationManager applicationManager;
	
	@Autowired
	private CustomerManager customerManager;
	
	@Autowired
	private GradeStatisticsManager gradeStatisticsManager;

	@Override
	public int countComments(long appId) throws PlatformException {
		try {
			Integer countComments = applicationCommentDao.countComments(appId);
			if (countComments == null) {
				countComments = 0;
			}
			return countComments;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
	
	@Override
	public void saveOrUpdate(ApplicationComment ac) {
		GradeStatistics gs = null;
		Application application = applicationManager.load(ac.getApplication().getId());
		if (ac.getId() == null) {
			ac.setOldGrade(-1); // 如果没有 commentId表明是新comment没有oldstar
		}
		ac.setApplication(application);
		ac.setCommentTime(Calendar.getInstance());
		String currentUserName = SpringSecurityUtils.getCurrentUserName();
		if (StringUtils.isBlank(currentUserName)) {
			if (ac.getCustomer() == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_EXIST);
			}
		} else {
			Customer customer = customerManager.getCustomerByUserName(currentUserName);
			ac.setCustomer(customer);
		}
		if (ac.getGrade() == null) { // =null表明有权限但是打的0分
			ac.setGrade(0);
		}
		if (ac.getGrade() == -1){ // =-1表明没有打星的权限
			ac.setGrade(null);
		}

		super.saveOrUpdate(ac);
		// 修改 GRADE_STATISTICS表, 打分人数加1, 修改评论的话要把以前的星减下来
		if (application.getStatistics() == null) {
			gs = new GradeStatistics();
			gs.setApplication(application);
		} else {
			gs = application.getStatistics();
		}
		if (ac.getGrade() != null){
			gs.countGrade(ac.getGrade(), true);
		}
		if ( ac.getOldGrade() != null){
			gs.countGrade(ac.getOldGrade(), false);
		}
		gradeStatisticsManager.saveOrUpdate(gs);
		application.setStatistics(gs);
		application.setStarNumber(gs == null ? 0 : gs.getAvgNumber());
		applicationManager.saveOrUpdate(application);
	}

	@Override
	public boolean isCommented(long appId) {
		try {
			Customer customer = customerManager.getCustomerByUserName(SpringSecurityUtils.getCurrentUserName());
			if (customer == null){
				return true;
			}
			Integer countComments = applicationCommentDao.isCommented(appId, customer.getId());
			if (countComments == null){
				countComments = 0;
			}
			return countComments != 0;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public ApplicationComment getByAppIdAndCustomerId(long appId, long customerId) throws PlatformException {
		try {
			return applicationCommentDao.getByAppIdAndCustomerId(appId, customerId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}