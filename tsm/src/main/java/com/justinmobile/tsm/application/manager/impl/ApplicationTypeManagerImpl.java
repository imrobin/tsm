package com.justinmobile.tsm.application.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationTypeDao;
import com.justinmobile.tsm.application.domain.ApplicationType;
import com.justinmobile.tsm.application.manager.ApplicationTypeManager;

@Service("applicationTypeManager")
public class ApplicationTypeManagerImpl extends EntityManagerImpl<ApplicationType, ApplicationTypeDao> implements
		ApplicationTypeManager {

	@Autowired
	private ApplicationTypeDao applicationTypeDao;

	@Override
	public Page<ApplicationType> getChild(Page<ApplicationType> page, Long parentId) {
		try {
			return applicationTypeDao.recentlyDownLoad(page, parentId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void checkName(ApplicationType type) throws PlatformException {
		try {
			String hql = "from " + ApplicationType.class.getName() + " as at where at.name = ? and at.typeLevel = ? ";
			List<ApplicationType> applicationTypes = applicationTypeDao.find(hql , type.getName(), type.getTypeLevel());
			if (CollectionUtils.isNotEmpty(applicationTypes)) {
				if (applicationTypes.size() > 1) {
					throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_REDUPLICATE_AT_SAME_LEVEL);
				} else {
					ApplicationType dataType = applicationTypes.get(0);
					if (!dataType.getId().equals(type.getId())) {
						throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_REDUPLICATE_AT_SAME_LEVEL);
					}
				}
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
	public void setShowIndex(String[] idArray) {
		try {
			//查询出全部父分类且清除再设置
			 List<ApplicationType> appTypeList = applicationTypeDao.findByProperty("typeLevel", ApplicationType.AppTypeLevel.ONE_LEVEL.getType());
			 for (ApplicationType at : appTypeList) {
				 at.setShowIndex(ApplicationType.NO_SHOW);
				 for (String id : idArray) {
					 if (Long.valueOf(id).longValue() == at.getId().longValue()) {
						 at.setShowIndex(ApplicationType.SHOW);
					 } 
				 }
				 applicationTypeDao.saveOrUpdate(at);
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
	public List<ApplicationType> getShowIndexTypeList() {
		try {
			 List<ApplicationType> appTypeList = applicationTypeDao.findByProperty("showIndex", ApplicationType.SHOW);
			 return appTypeList;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationType> getShowIndexTypeListOrderById() {
		try {
			 List<ApplicationType> appTypeList = applicationTypeDao.getShowIndexTypeListOrderById();
			 return appTypeList;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationType> getAllTopLevel() {
		try {
			 List<ApplicationType> appTypeList = applicationTypeDao.findByProperty("typeLevel", ApplicationType.AppTypeLevel.ONE_LEVEL.getType());
			 return appTypeList;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}