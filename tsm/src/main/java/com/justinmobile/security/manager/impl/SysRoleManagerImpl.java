package com.justinmobile.security.manager.impl;

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
import com.justinmobile.security.dao.SysRoleDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.manager.SysRoleManager;

@Service("sysRoleManager")
public class SysRoleManagerImpl extends EntityManagerImpl<SysRole, SysRoleDao> implements SysRoleManager {

	@Autowired
	private SysRoleDao roleDao;

	@Autowired
	private SysAuthorityDao authorityDao;

	@Override
	public void checkRole(SysRole role) throws PlatformException {
		try {
			SysRole existRoleName = roleDao.getRoleByName(role.getRoleName());
			if (existRoleName != null) {
				if (!existRoleName.getId().equals(role.getId())) {
					throw new PlatformException(PlatformErrorCode.ROLE_NAME_REDUPLICATE);
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
	public void removeRole(long roleId) throws PlatformException {
		try {
			SysRole role = super.load(roleId);
			if (role == null) {
				throw new PlatformException(PlatformErrorCode.ROLE_NOT_EXIST);
			}
			if (CollectionUtils.isNotEmpty(role.getSysUsers())) {
				throw new PlatformException(PlatformErrorCode.ROLE_IN_USE_BY_SOMEBODY);
			}
			super.remove(role);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void addAuths(long roleId, String auths) throws PlatformException {
		try {
			if (StringUtils.isNotBlank(auths)) {// auths为空的情况就不做任何操作
				SysRole role = super.load(roleId);
				if (role == null) {
					throw new PlatformException(PlatformErrorCode.ROLE_NOT_EXIST);
				}
				Set<SysAuthority> authorities = Sets.newHashSet();
				// 空格隔开各个id
				String[] authIds = StringUtils.split(auths);
				for (String authId : authIds) {
					authorities.add(authorityDao.load(Long.valueOf(authId)));
				}
				role.getSysAuthorities().addAll(authorities);
				super.saveOrUpdate(role);
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
	public void delAuths(long roleId, String auths) throws PlatformException {
		try {
			if (StringUtils.isNotBlank(auths)) {// auths为空的情况就不做任何操作
				SysRole role = super.load(roleId);
				if (role == null) {
					throw new PlatformException(PlatformErrorCode.ROLE_NOT_EXIST);
				}
				Set<SysAuthority> authorities = role.getSysAuthorities();
				// 空格隔开各个id
				String[] authIds = StringUtils.split(auths);
				for (String authId : authIds) {
					SysAuthority auth = authorityDao.load(Long.valueOf(authId));
					if (authorities.contains(auth)) {
						authorities.remove(auth);
					}
				}
				super.saveOrUpdate(role);
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
	public SysRole getRoleByName(String roleName) throws PlatformException {
		try {
			return roleDao.getRoleByName(roleName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
