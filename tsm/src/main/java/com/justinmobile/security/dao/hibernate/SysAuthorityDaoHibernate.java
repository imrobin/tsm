package com.justinmobile.security.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.JoinType;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.security.dao.SysAuthorityDao;
import com.justinmobile.security.dao.SysUserDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysAuthority.AUTH_STATUS;
import com.justinmobile.security.domain.SysUser;

@Repository("sysAuthorityDao")
public class SysAuthorityDaoHibernate extends EntityDaoHibernate<SysAuthority, Long> implements SysAuthorityDao {
	
	@Autowired
	private SysUserDao sysUserDao;

	@Override
	public List<SysAuthority> getAllEnableAuthorities() {
		return findByProperty("status", AUTH_STATUS.ENABLED.getValue());
	}

	@Override
	public List<SysAuthority> getAllAuthoritiesByUserName(String userName) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("status", MatchType.EQ, PropertyType.I, String.valueOf(AUTH_STATUS.ENABLED.getValue())));
		filters.add(new PropertyFilter("sysUsers", JoinType.I, "userName", MatchType.EQ, PropertyType.S, userName));
		return find(filters);
	}

	@Override
	public List<SysAuthority> getAllNotAuthoritiesByUserName(String userName) {
		String hql = "select auth from " + SysAuthority.class.getName() + " as auth where auth.status = ?";
		List<SysAuthority> allAuths = find(hql, AUTH_STATUS.ENABLED.getValue());
		SysUser user = sysUserDao.getUserByUserName(userName);
		allAuths.removeAll(user.getSysAuthorities());
		return allAuths;
	}

	@Override
	public List<SysAuthority> getSysAuthoritiesByResource(String resString) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("status", MatchType.EQ, PropertyType.I, String.valueOf(AUTH_STATUS.ENABLED.getValue())));
		filters.add(new PropertyFilter("sysResources", JoinType.I, "filterString", MatchType.LIKE, PropertyType.S, resString));
		return find(filters);
	}

	@Override
	public SysAuthority getAuthorityByName(String authName) {
		return findUniqueByProperty("authName", authName);
	}

}
