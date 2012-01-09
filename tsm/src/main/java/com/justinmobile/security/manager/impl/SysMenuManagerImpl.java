package com.justinmobile.security.manager.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.web.treeMenu.Property;
import com.justinmobile.core.utils.web.treeMenu.State;
import com.justinmobile.core.utils.web.treeMenu.TreeMenu;
import com.justinmobile.core.utils.web.treeMenu.TreeMenu.Type;
import com.justinmobile.security.dao.SysMenuDao;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysAuthorityManager;
import com.justinmobile.security.manager.SysMenuManager;

@Service("sysMenuManager")
public class SysMenuManagerImpl extends EntityManagerImpl<SysMenu, SysMenuDao> implements SysMenuManager {

	@Autowired
	private SysMenuDao sysMenuDao;

	@Autowired
	private SysAuthorityManager sysAuthorityManager;

	@Override
	public void removeMenu(Long menuId) throws PlatformException {
		try {
			SysMenu menu = super.load(menuId);
			if (menu == null) {
				throw new PlatformException(PlatformErrorCode.MENU_NOT_EXIST);
			}
			if (CollectionUtils.isNotEmpty(menu.getSysAuthorities())) {
				throw new PlatformException(PlatformErrorCode.MENU_IN_USE_BY_AUTH);
			}
			if (CollectionUtils.isNotEmpty(menu.getChildMenus())) {
				throw new PlatformException(PlatformErrorCode.MENU_IN_USE_BY_CHILD);
			}
			sysMenuDao.remove(menu);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public TreeMenu getTreeMenu(Long authId) throws PlatformException {
		TreeMenu treeMenu = new TreeMenu();
		treeMenu.setProperty(new Property(PlatformMessage.PLATFORM_NAME.getMessage(), false));
		treeMenu.setState(new State(false, true));
		List<SysMenu> topMenus = sysMenuDao.getAllTopMenu();
		SysAuthority auth = null;
		if (authId != null) {
			auth = sysAuthorityManager.load(authId);
		}
		// 递归取所有子节点
		buildTree(treeMenu, topMenus, auth);
		return treeMenu;
	}

	private void buildTree(TreeMenu parentTree, Collection<SysMenu> childMenus, SysAuthority auth) {
		if (CollectionUtils.isNotEmpty(childMenus)) {
			for (SysMenu childMenu : childMenus) {
				Set<SysMenu> grandchildMenus = childMenu.getChildMenus();
				TreeMenu childTree = new TreeMenu();
				childTree.setProperty(new Property(childMenu.getMenuName(), true));
				if (CollectionUtils.isEmpty(grandchildMenus)) {
					childTree.setType(Type.file.name());
				}
				Set<SysAuthority> auths = childMenu.getSysAuthorities();
				childTree.setState(new State(auths.contains(auth), true));
				childTree.getData().put("id", String.valueOf(childMenu.getId()));
				parentTree.addChild(childTree);
				buildTree(childTree, grandchildMenus, auth);
			}
		}
	}

	@Override
	public void checkMenu(SysMenu menu) throws PlatformException {
		SysMenu existMenuName = sysMenuDao.getMenuByLevel(menu.getMenuName(), menu.getMenuLevel());
		if (existMenuName != null) {
			if (!existMenuName.getId().equals(menu.getId())) {
				throw new PlatformException(PlatformErrorCode.MENU_NAME_REDUPLICATE_IN_SAME_LEVEL);
			}
		}
	}

	@Override
	public List<SysMenu> showMenus(SysUser user, Long parentId) throws PlatformException {
		try {
			return sysMenuDao.showMenus(user, parentId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
