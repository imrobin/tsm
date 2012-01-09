package com.justinmobile.security.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.security.dao.SysRoleDao;
import com.justinmobile.security.domain.SysRole;

@Repository("sysRoleDao")
public class SysRoleDaoHibernate extends EntityDaoHibernate<SysRole, Long> implements SysRoleDao {

	@Override
	public SysRole getRoleByName(String roleName) {
		return findUniqueByProperty("roleName", roleName);
	}
	
}
