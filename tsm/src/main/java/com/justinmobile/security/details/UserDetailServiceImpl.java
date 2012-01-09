package com.justinmobile.security.details;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.collect.Sets;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysResource;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysAuthorityManager;
import com.justinmobile.security.manager.SysUserManager;

/**
 * 实现SpringSecurity的UserDetailsService接口
 * 获取用户信息和用户对应的权限，进行认证
 * @author peak
 *
 */
public class UserDetailServiceImpl implements UserDetailsService {
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private SysAuthorityManager sysAuthorityManager;

	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
		SysUser sysUser = sysUserManager.getUserByNameOrMobileOrEmail(userName);
		if (sysUser == null) {
			throw new UsernameNotFoundException(userName + " not found");
		}
		boolean superAdmin = SpecialRoleType.SUPER_OPERATOR.name().equals(sysUser.getSysRole().getRoleName());
		UserWithSalt userDetail = new UserWithSalt(sysUser.getUserName(), sysUser.getPassword(), sysUser.isEnable(), 
                true, true, true, obtainGrantedAuthorities(sysUser.getUserName()),sysUser.getSalt(), sysUser.getId(), sysUser.getProvince(), superAdmin);

		return userDetail;
	}
	
	/**
	 * 获得用户所有角色的权限集合.
	 */
	private Set<GrantedAuthority> obtainGrantedAuthorities(String userName) {
		Set<GrantedAuthority> authSet = Sets.newHashSet();
		List<SysAuthority> sysAuthoritys = sysAuthorityManager.getSysAuthorityByUser(userName);
		//得到所有用户下所拥有的权限
		authSet.addAll(SysAuthority.toGrantedAuthority(sysAuthoritys));
		for (SysAuthority sysAuthority : sysAuthoritys) {
			Set<SysResource> resource = sysAuthority.getSysResources();
			//得到交叉权限，比如资源为/aa/*对应的权限可以获得资源未/aa/abc.jsp对应的权限
			for (SysResource sysResource : resource) {
				String filterStr = sysResource.getFilterString();
				if (filterStr.indexOf("*") == filterStr.length() - 1) {
					List<SysAuthority> containAuths = sysAuthorityManager.getSysAuthorityByResource(filterStr);
					authSet.addAll(SysAuthority.toGrantedAuthority(containAuths));
				}
			}
		}
		return authSet;
	}
}
