package com.justinmobile.security.manager.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.dao.SysAuthorityDao;
import com.justinmobile.security.dao.SysMenuDao;
import com.justinmobile.security.dao.SysResourceDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysResource;
import com.justinmobile.security.manager.SysAuthorityManager;

@Service("sysAuthorityManager")
public class SysAuthorityManagerImpl extends EntityManagerImpl<SysAuthority, SysAuthorityDao> implements SysAuthorityManager {

	@Autowired
	private SysAuthorityDao sysAuthorityDao;
	
	@Autowired
	private SysMenuDao sysMenuDao;
	
	@Autowired
	private SysResourceDao sysResourceDao;
	
	

	@Override
	public List<SysAuthority> getAll() throws PlatformException {
		//复写getALL，取出来的都是状态有效的
		try {
			return sysAuthorityDao.getAllEnableAuthorities();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public List<SysAuthority> getSysAuthorityByUser(String userName) throws PlatformException {
		try {
			return sysAuthorityDao.getAllAuthoritiesByUserName(userName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
	
	@Override
	public List<SysAuthority> getNotSysAuthorityByUser(String userName) throws PlatformException {
		try {
			return sysAuthorityDao.getAllNotAuthoritiesByUserName(userName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public List<SysAuthority> getSysAuthorityByResource(String resString) throws PlatformException {
		try {
			if (resString.indexOf("*") == resString.length() - 1) {
				resString = StringUtils.replace(resString, "*", "%");
			}
			return sysAuthorityDao.getSysAuthoritiesByResource(resString);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void checkAuthName(SysAuthority auth) throws PlatformException {
		try {
			SysAuthority existAuthName = sysAuthorityDao.getAuthorityByName(auth.getAuthName());
			if (existAuthName != null) {
				if (!existAuthName.getId().equals(auth.getId())) {
					throw new PlatformException(PlatformErrorCode.AUTH_NAME_REDUPLICATE);
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
	public void removeAuthority(long authId) throws PlatformException {
		try {
			SysAuthority auth = super.load(authId);
			if (CollectionUtils.isNotEmpty(auth.getSysRoles())) {
				throw new PlatformException(PlatformErrorCode.AUTH_IN_USE_BY_ROLE);
			}
			if (CollectionUtils.isNotEmpty(auth.getSysUsers())) {
				throw new PlatformException(PlatformErrorCode.AUTH_IN_USE_BY_SOMEBODY);
			}
			super.remove(auth);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void setMenus(long authId, String menus) throws PlatformException {
		try {
			if (StringUtils.isNotBlank(menus)) {
				SysAuthority auth = super.load(authId);
				if (auth == null) {
					throw new PlatformException(PlatformErrorCode.ROLE_NOT_EXIST);
				}
				Set<SysMenu> sysMenus = Sets.newHashSet();
				//空格隔开各个id
				String[] menuIds = StringUtils.split(menus);
				for (String menuId : menuIds) {
					sysMenus.add(sysMenuDao.load(Long.valueOf(menuId)));
				}
				auth.getSysMenus().clear();
				auth.getSysMenus().addAll(sysMenus);
				super.saveOrUpdate(auth);
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
	public void addResources(long authId, String reses) throws PlatformException {
		try {
			if (StringUtils.isNotBlank(reses)) {
				SysAuthority auth = super.load(authId);
				if (auth == null) {
					throw new PlatformException(PlatformErrorCode.AUTH_NOT_EXIST);
				}
				Set<SysResource> resources = Sets.newHashSet();
				//空格隔开各个id
				String[] resIds = StringUtils.split(reses);
				for (String resId : resIds) {
					resources.add(sysResourceDao.load(Long.valueOf(resId)));
				}
				auth.getSysResources().clear();
				auth.getSysResources().addAll(resources);
				super.saveOrUpdate(auth);
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
	public void delResources(long authId, String reses) throws PlatformException {
		try {
			if (StringUtils.isNotBlank(reses)) {
				SysAuthority auth = super.load(authId);
				if (auth == null) {
					throw new PlatformException(PlatformErrorCode.AUTH_NOT_EXIST);
				}
				Set<SysResource> resources = auth.getSysResources();
				//空格隔开各个id
				String[] resIds = StringUtils.split(reses);
				for (String resId : resIds) {
					SysResource res = sysResourceDao.load(Long.valueOf(resId));
					if (resources.contains(res)) {
						resources.remove(res);
					}
				}
				super.saveOrUpdate(auth);
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
	public Map<Long, String> getAuthToMap(Collection<SysAuthority> auths) throws PlatformException {
		try {
			Map<Long, String> map = new HashMap<Long, String>();
			if (CollectionUtils.isNotEmpty(auths)) {
				for (SysAuthority sysAuthority : auths) {
					StringBuilder buf = new StringBuilder();
					buf.append(sysAuthority.getAuthName());
					String desc = sysAuthority.getDescription();
					if (StringUtils.isBlank(desc)) {
						buf.append("(").append(sysAuthority.getAuthName()).append(")");
					} else {
						buf.append("(").append(desc).append(")");
					}
					map.put(sysAuthority.getId(), buf.toString());
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

}
