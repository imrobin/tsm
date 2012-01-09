package com.justinmobile.security.dao.hibernate;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.security.dao.SysMenuDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysUser;

@Repository("sysMenuDao")
public class SysMenuDaoHibernate extends EntityDaoHibernate<SysMenu, Long> implements SysMenuDao {

	@Override
	public List<SysMenu> getAllTopMenu() {
		String hql = "from " + SysMenu.class.getName() + " as m where m.menuLevel = ? order by m.orderNo asc";
		return find(hql, SysMenu.LEVEL_ONE);
	}

	@Override
	public SysMenu getMenuByLevel(String menuName, int level) {
		String hql = "from " + SysMenu.class.getName() + " as m where m.menuName = ? and m.menuLevel = ?";
		return findUniqueEntity(hql, menuName, level);
	}

	@Override
	public List<SysMenu> showMenus(SysUser user, Long parentId) {
		Set<SysAuthority> auths = user.getSysAuthorities();
		List<SysMenu> avMenus = Lists.newArrayList();
		StringBuilder hql = new StringBuilder("from " + SysMenu.class.getName() + " as m where 1=1");
		if (parentId == null) {
			hql.append(" and m.parent is null");
		} else {
			hql.append(" and m.parent.id = " + parentId);
		}
		hql.append(" order by m.orderNo asc");
		List<SysMenu> menus = find(hql.toString());
		if (CollectionUtils.isNotEmpty(menus)) {
			for (SysMenu menu : menus) {
				if (CollectionUtils.isEmpty(menu.getSysAuthorities())) {//没被加入到权限的菜单可以访问
					avMenus.add(menu);
				} else {
					boolean hasAuth = CollectionUtils.containsAny(menu.getSysAuthorities(), auths);
					if (hasAuth) {//至少用户的权限和菜单的权限有一个相同的
						avMenus.add(menu);
					}
				}
			}
		}
		return avMenus;
	}

}
