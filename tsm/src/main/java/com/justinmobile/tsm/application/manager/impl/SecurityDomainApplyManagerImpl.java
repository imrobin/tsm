package com.justinmobile.tsm.application.manager.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.SecurityDomainApplyDao;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;
import com.justinmobile.tsm.application.manager.SecurityDomainApplyManager;
import com.justinmobile.tsm.system.domain.Requistion;

@Service("securityDomainApplyManager")
public class SecurityDomainApplyManagerImpl extends EntityManagerImpl<SecurityDomainApply, SecurityDomainApplyDao> implements SecurityDomainApplyManager{

	@Autowired
	private SecurityDomainApplyDao securityDomainApplyDao;
	
	@Override
	public Page<SecurityDomainApply> findPage(Page<SecurityDomainApply> page, Long spId) {
		try {
			String hql = "from SecurityDomainApply a where a.sp.id = ?";
			if(!StringUtils.isBlank(page.getOrderBy())) {
				hql += " order by a." + page.getOrderBy().replaceAll("_", "\\.") + " " + page.getOrder();
			}
			page = securityDomainApplyDao.findPage(page, hql, spId);
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
	public Page<SecurityDomainApply> findPage(Page<SecurityDomainApply> page, String orderBy, Map<String, Object> params) {
		String hql = "select a from SecurityDomainApply a where 1 = ? ";
		try {
			
			if(params != null && !params.isEmpty()) {
				String name = (String)params.get("name");
				if(name != null && !StringUtils.isBlank(name)) {
					hql += " and a.sp.name like '%"+name+"%' ";
				}
				
				if(params.containsKey("province")) {
					String province = (String)params.get("province");
					hql += " and a.sp.locationNo = '" + province + "'";
				}
				
				if(params.containsKey("requistionStatus")) {
					hql += " and a.requistion.status = " + params.get("requistionStatus");
				}
				
				if(params.containsKey("sdStatus")) {
					hql += " and a.status = " + params.get("sdStatus");
				}
			}
			
			if(!StringUtils.isBlank(orderBy)) {
				String[] items = orderBy.split("_");
				if(items.length == 3) {
					orderBy = " order by a." + orderBy.replaceFirst("_", ".").replace("_", " ");
				} else if(items.length == 2) {
					orderBy = " order by a." + orderBy.replace("_", " ");
				}
				hql += orderBy;
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return securityDomainApplyDao.findPage(page, hql, Requistion.STATUS_INIT);
	}
	
	@Override
	public Page<SecurityDomainApply> findPage(Page<SecurityDomainApply> page, Integer status, String orderBy) {
		String hql = "select a from SecurityDomainApply a where a.status = ?";
		try {
			if(!StringUtils.isBlank(orderBy)) {
				String[] items = orderBy.split("_");
				if(items.length == 3) {
					orderBy = " order by a." + orderBy.replaceFirst("_", ".").replace("_", " ");
				} else if(items.length == 2) {
					orderBy = " order by a." + orderBy.replace("_", " ");
				}
				hql += orderBy;
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return securityDomainApplyDao.findPage(page, hql, status);
	}
}
