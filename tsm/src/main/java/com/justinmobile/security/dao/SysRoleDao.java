package com.justinmobile.security.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.security.domain.SysRole;

public interface SysRoleDao extends EntityDao<SysRole, Long> {
	
	SysRole getRoleByName(String roleName);

}
