package com.justinmobile.security.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysUser;

public interface SysMenuDao extends EntityDao<SysMenu, Long> {
	
	List<SysMenu> getAllTopMenu();
	
	SysMenu getMenuByLevel(String menuName, int level);

	List<SysMenu> showMenus(SysUser user, Long parentId);

}
