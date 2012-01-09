package com.justinmobile.security.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Sets;
import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.security.dao.SysUserDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysUser.USER_STATUS;

@Repository("sysUserDao")
public class SysUserDaoHibernate extends EntityDaoHibernate<SysUser, Long> implements SysUserDao {

	@Override
	public SysUser getUserByNameOrMobileOrEmail(String proof) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("status", MatchType.EQ, PropertyType.I, String.valueOf(USER_STATUS.ENABLED.getValue())));
		StringBuilder buff = new StringBuilder();
		buff.append("userName").append(PropertyFilter.OR_SEPARATOR).append("mobile").append(PropertyFilter.OR_SEPARATOR).append("email");
		filters.add(new PropertyFilter(buff.toString(), MatchType.EQ, PropertyType.S, proof));
		List<SysUser> list = find(filters);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else if (list.size() > 1) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		}
		return list.get(0);
	}

	@Override
	public SysUser getUserByEmail(String email) {
		return super.findUniqueByProperty("email", email);
	}

	@Override
	public SysUser getUserByMobile(String mobile) {
		return super.findUniqueByProperty("mobile", mobile);
	}

	@Override
	public SysUser getUserByUserName(String userName) {
		return super.findUniqueByProperty("userName", userName);
	}

	@Override
	public void updateUseraAuths(SysRole role, Set<SysAuthority> oldRoleAuths) {
		Set<SysAuthority> newRoleAuths = role.getSysAuthorities();
		Set<SysUser> users = role.getSysUsers();
		if (CollectionUtils.isNotEmpty(users)) {
			int i = 0;
			for (SysUser user : users) {
				//复制一份角色的老权限
				Set<SysAuthority> cloneRoleAuths = Sets.newHashSet();
				cloneRoleAuths.addAll(oldRoleAuths);
				//复制一份用户所有的权限
				Set<SysAuthority> cloneUserAuths = Sets.newHashSet();
				Set<SysAuthority> userAuths = user.getSysAuthorities();
				cloneUserAuths.addAll(userAuths);
				//用户删除角色之前拥有的所有权限
				userAuths.removeAll(cloneRoleAuths);
				//角色之前有，但是用户没有的权限
				cloneRoleAuths.removeAll(cloneUserAuths);
				//用户加入新的角色权限
				userAuths.addAll(newRoleAuths);
				//用户删除角色有，但是用户不该有的权限
				userAuths.removeAll(cloneRoleAuths);
				saveOrUpdate(user);
				i++;
				if (i % 20 == 0) {
					flush();
				}
			}
		}
		
	}

}
