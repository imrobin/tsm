package com.justinmobile.security.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.security.domain.SysAuthority;

@Transactional
public interface SysAuthorityManager extends EntityManager<SysAuthority> {

	/**
	 * 根据用户名称得到用户所有的权限
	 * @param userName
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	List<SysAuthority> getSysAuthorityByUser(String userName) throws PlatformException;
	
	/**
	 * 根据用户名称得到不属于该用户的所有的权限
	 * @param userName
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	List<SysAuthority> getNotSysAuthorityByUser(String userName) throws PlatformException;
	
	@Transactional(readOnly = true)
	Map<Long, String> getAuthToMap(Collection<SysAuthority> auths) throws PlatformException;

	/**
	 * 根据资源名称，得到所有访问该资源的权限
	 * @param resString
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	List<SysAuthority> getSysAuthorityByResource(String resString) throws PlatformException;
	
	@Transactional(readOnly = true)
	void checkAuthName(SysAuthority auth) throws PlatformException;

	/**
	 * 删除一个权限，权限不能被角色和用户使用
	 * @param authId
	 * @throws PlatformException
	 */
	void removeAuthority(long authId) throws PlatformException;
	
	/**
	 * 设置菜单
	 * @param authId
	 * @param menus
	 * @throws PlatformException
	 */
	void setMenus(long authId, String menus) throws PlatformException;
	
	/**
	 * 给权限增加资源
	 * @param authId
	 * @param reses
	 * @throws PlatformException
	 */
	void addResources(long authId, String reses) throws PlatformException;
	
	/**
	 * 给权限删除资源
	 * @param authId
	 * @param reses
	 * @throws PlatformException
	 */
	void delResources(long authId, String reses) throws PlatformException;
	
}
