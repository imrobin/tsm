package com.justinmobile.security.manager.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.dao.SysResourceDao;
import com.justinmobile.security.domain.SysResource;
import com.justinmobile.security.manager.SysResourceManager;

@Service("sysResourceManager")
public class SysResourceManagerImpl extends EntityManagerImpl<SysResource, SysResourceDao> implements SysResourceManager {
	
	@Autowired
	private SysResourceDao sysResourceDao;

	@Override
	public SysResource getResourceByFilterString(String filterString) throws PlatformException {
		try {
			return sysResourceDao.getResourceByFilterString(filterString);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Map<Long, String> getResToMap(Collection<SysResource> sysResources) throws PlatformException {
		try {
			Map<Long, String> map = new HashMap<Long, String>();
			if (CollectionUtils.isNotEmpty(sysResources)) {
				for (SysResource sysResource : sysResources) {
					map.put(sysResource.getId(), sysResource.getResName() + "(" + sysResource.getFilterString() + ")");
				}
			}
			return map;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void checkResource(SysResource res) throws PlatformException {
		SysResource existResName = sysResourceDao.findUniqueByProperty("resName", res.getResName());
		if (existResName != null) {
			if (!existResName.getId().equals(res.getId())) {
				throw new PlatformException(PlatformErrorCode.RES_NAME_REDUPLICATE);
			}
		}
		SysResource existResFilter = getResourceByFilterString(res.getFilterString());
		if (existResFilter != null) {
			if (!existResFilter.getId().equals(res.getId())) {
				throw new PlatformException(PlatformErrorCode.RES_FILTER_REDUPLICATE);
			}
		}
	}

}
