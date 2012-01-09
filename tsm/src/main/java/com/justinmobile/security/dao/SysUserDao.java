package com.justinmobile.security.dao;

import java.util.Set;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysUser;

public interface SysUserDao extends EntityDao<SysUser, Long> {

	SysUser getUserByNameOrMobileOrEmail(String proof);
	
	SysUser getUserByUserName(String userName);
	
	SysUser getUserByEmail(String email);
	
	SysUser getUserByMobile(String mobile);
	
	void updateUseraAuths(SysRole role, Set<SysAuthority> oldRoleAuths);

}
