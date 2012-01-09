package com.justinmobile.security.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.core.utils.web.treeMenu.TreeMenu;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysUser;

@Transactional
public interface SysMenuManager extends EntityManager<SysMenu> {

	void removeMenu(Long menuId) throws PlatformException;
	
	TreeMenu getTreeMenu(Long authId) throws PlatformException;
	
	@Transactional(readOnly = true)
	void checkMenu(SysMenu menu) throws PlatformException;
	
	@Transactional(readOnly = true)
	List<SysMenu> showMenus(SysUser user, Long parentId) throws PlatformException;

}
