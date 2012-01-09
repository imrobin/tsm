package com.justinmobile.security.manager;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.security.domain.SysRole;

@Transactional(propagation = Propagation.REQUIRED)
public interface SysRoleManager extends EntityManager<SysRole> {
	
	@Transactional(readOnly = true)
	SysRole getRoleByName(String roleName) throws PlatformException;

	@Transactional(readOnly = true)
	void checkRole(SysRole role) throws PlatformException;
	
	/**
	 * 删除一个角色，被用户使用的角色无法删除
	 * @param roleId
	 * @throws PlatformException
	 */
	void removeRole(long roleId) throws PlatformException;
	
	/**
	 * 给角色增加权限
	 * @param roleId
	 * @param auths 权限id，用空格隔开
	 * @throws PlatformException
	 */
	void addAuths(long roleId, String auths) throws PlatformException;
	
	/**
	 * 给角色减少权限
	 * @param roleId
	 * @param auths 权限id，用空格隔开
	 * @throws PlatformException
	 */
	void delAuths(long roleId, String auths) throws PlatformException;

}
