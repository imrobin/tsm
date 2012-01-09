package com.justinmobile.security.manager;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;

@Transactional
public interface SysUserManager extends EntityManager<SysUser> {
	
	/**
	 * 根据用户名，手机号码，邮箱地址来查找用户信息，保证唯一性
	 * @param proof
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	SysUser getUserByNameOrMobileOrEmail(String proof) throws PlatformException;
	
	/**
	 * 根据用户名查找用户
	 * @param userName
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	SysUser getUserByName(String userName) throws PlatformException;
	/**
	 * 根据用手机号查找用户
	 * @param mobile
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	SysUser getUserByMobile(String mobile) throws PlatformException;
	
	/**
	 * 新增用户，特殊的角色
	 * @param user
	 * @throws PlatformException
	 */
	void addUser(SysUser user, SpecialRoleType roleType) throws PlatformException;
	
	void updateUser(SysUser user, SpecialRoleType roleType) throws PlatformException;
	
	@Transactional(readOnly = true)
	void checkUniqueUser(SysUser user) throws PlatformException;

	/**
	 * 验证用户密码是否正确
	 * @param userName
	 * @param password
	 * @return true-正确；false-错误；
	 * @throws PlatformException
	 */
	Boolean validatePassword(String userName, String password) throws PlatformException;
	
	/**
	 * 变更密码
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @param reNewPassword
	 * @throws PlatformException
	 */
	void modifyPassword(String userName, String oldPassword, String newPassword, String reNewPassword) throws PlatformException;
	
	/**
	 * 
	 * @param userName
	 * @param newPassword
	 * @param reNewPassword
	 * @throws PlatformException
	 */
	void retrievePassword(long userId, String newPassword, String reNewPassword) throws PlatformException;
	
	/**
	 * 将密码加密保存到用户
	 * @param user
	 * @param password
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	SysUser encodeWithSalt(SysUser user,String password) throws PlatformException;

	void updateUseraAuths(SysRole role, Set<SysAuthority> oldRoleAuths);
}
