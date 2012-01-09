package com.justinmobile.security.manager.impl;

import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.dao.SysRoleDao;
import com.justinmobile.security.dao.SysUserDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;

@Service("sysUserManager")
@Transactional
public class SysUserManagerImpl extends EntityManagerImpl<SysUser, SysUserDao> implements SysUserManager {

	@Autowired
	private SysUserDao sysUserDao;
	
	@Autowired
	private SysRoleDao sysRoleDao;

	@Override
	public SysUser getUserByNameOrMobileOrEmail(String proof) throws PlatformException {
		try {
			return sysUserDao.getUserByNameOrMobileOrEmail(proof);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public SysUser getUserByName(String userName) throws PlatformException {
		try {
			if (StringUtils.isBlank(userName)) {
				return null;
			}
			return sysUserDao.getUserByUserName(userName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public void addUser(SysUser user, SpecialRoleType roleType) throws PlatformException {
		try {
			user = encodeWithSalt(user, user.getPassword());
			if (roleType != null) {//指定特殊的用户角色
				SysRole role = sysRoleDao.getRoleByName(roleType.name());
				if (role == null) {
					throw new PlatformException(PlatformErrorCode.ROLE_NOT_EXIST);
				}
				role.getSysUsers().add(user);
				user.setSysRole(role);
				user.getSysAuthorities().addAll(role.getSysAuthorities());
			}
			super.saveOrUpdate(user);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public void updateUser(SysUser user, SpecialRoleType roleType) throws PlatformException {
		try {
			if (roleType != null) {
				String roleName = user.getSysRole().getRoleName();
				if (!roleName.equals(roleType.name())) {//如果角色反生变更
					//删除所有原角色对应的权限
					user.getSysAuthorities().removeAll(user.getSysRole().getSysAuthorities());
					SysRole role = sysRoleDao.getRoleByName(roleType.name());
					if (role == null) {
						throw new PlatformException(PlatformErrorCode.ROLE_NOT_EXIST);
					}
					//指定新的角色，添加新的权限
					role.getSysUsers().add(user);
					user.setSysRole(role);
					user.getSysAuthorities().addAll(role.getSysAuthorities());
				}
			}
			super.saveOrUpdate(user);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
	
	@Transactional(readOnly = true)
	public void checkUniqueUser(SysUser user) throws PlatformException {
		SysUser existUserName = sysUserDao.getUserByUserName(user.getUserName());
		if (existUserName != null) {
			if (!existUserName.getId().equals(user.getId())) {
				throw new PlatformException(PlatformErrorCode.USER_NAME_REDUPLICATE);
			}
		}
		SysUser existUserMobile = sysUserDao.getUserByMobile(user.getMobile());
		if (existUserMobile != null) {
			if (!existUserMobile.getId().equals(user.getId())) {
				throw new PlatformException(PlatformErrorCode.USER_MOBILE_REDUPLICATE);
			}
		}
		SysUser existUserEmail = sysUserDao.getUserByEmail(user.getEmail());
		if (existUserEmail != null) {
			if (!existUserEmail.getId().equals(user.getId())) {
				throw new PlatformException(PlatformErrorCode.USER_EMAIL_REDUPLICATE);
			}
		}
	}
	
	@Override
	public void modifyPassword(String userName, String oldPassword, String newPassword, String reNewPassword) throws PlatformException {
		try {
			SysUser user = getUserByName(userName);
			if (StringUtils.equals(encode(oldPassword, user.getSalt()), user.getPassword())) {
				if (StringUtils.equals(newPassword, reNewPassword)) {
					user = encodeWithSalt(user, newPassword);
					super.saveOrUpdate(user);
				} else {
					throw new PlatformException(PlatformErrorCode.USER_PASSWORD_TWICE_INCONFORMITY);
				}
			} else {
				throw new PlatformException(PlatformErrorCode.USER_PASSWORD_ERROR);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 将密码加密保存到用户
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public SysUser encodeWithSalt(SysUser user, String password) {
		// 新的密码种子
		int random = Math.abs(new Random().nextInt());
		String salt = String.valueOf(random % 1000);
		user.setSalt(salt);
		user.setPassword(encode(password, salt));
		return user;
	}

	private String encode(String originalText, String salt) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		// false 表示：生成32位的Hex版, 这也是encodeHashAsBase64的, Acegi 默认配置; true
		// 表示：生成24位的Base64版
		md5.setEncodeHashAsBase64(false);
		return md5.encodePassword(originalText, salt);
	}

	@Override
	public void retrievePassword(long userId, String newPassword, String reNewPassword) throws PlatformException {
		try {
			SysUser user = super.load(userId);
			if (StringUtils.equals(newPassword, reNewPassword)) {
				user = encodeWithSalt(user, newPassword);
				super.saveOrUpdate(user);
			} else {
				throw new PlatformException(PlatformErrorCode.USER_PASSWORD_ERROR);
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
	public void updateUseraAuths(SysRole role, Set<SysAuthority> oldRoleAuths) {
		try {
			sysUserDao.updateUseraAuths(role, oldRoleAuths);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Boolean validatePassword(String userName, String password) throws PlatformException {
		Boolean bln = false;
		
		try {
			SysUser user = sysUserDao.getUserByUserName(userName);
			String oldPassword = user.getPassword();
			String currentPwd = this.encode(password, user.getSalt());
			bln = currentPwd.equalsIgnoreCase(oldPassword);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}
    
	@Override
	public SysUser getUserByMobile(String mobile) throws PlatformException {
		try {
			if (StringUtils.isBlank(mobile)) {
				return null;
			}
			return sysUserDao.getUserByMobile(mobile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
