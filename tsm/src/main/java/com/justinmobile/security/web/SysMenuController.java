package com.justinmobile.security.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.core.utils.web.treeMenu.TreeMenu;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysMenuManager;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;

@Controller
@RequestMapping("/menu/")
public class SysMenuController {
	
	@Autowired
	private SysMenuManager sysMenuManager;
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SysMenu> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = sysMenuManager.findPage(page, filters);
			result.setPage(page, null, null);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonResult indexShow(HttpServletRequest request, HttpServletResponse response) {
		JsonResult result = new JsonResult();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(userName)) {
				result.setMessage(request.getContextPath() + "/html/login/?m=deniedAccess");
			} else {
				SysUser user = sysUserManager.getUserByName(userName);
				String parentId = request.getParameter("parentId");
				Long longParentId = null;
				if (StringUtils.isNotBlank(parentId)) {
					longParentId = Long.valueOf(parentId);
				}
				List<SysMenu> menus = sysMenuManager.showMenus(user, longParentId);
				result.setResult(menus, null, null);
				result.setTotalCount(menus.size());
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage getMenu(@RequestParam Long menuId) {
		JsonMessage message = new JsonMessage();
		try {
			SysMenu menu = sysMenuManager.load(menuId);
			SysMenu parent = menu.getParent();
			if (parent == null || parent.getId() == null) {
				message.setMessage(menu.toMap("menuLevel", null, null));
			} else {//如果是二级菜单，则得到顶级的标签id
				Map<String, Object> map = menu.toMap("menuLevel", null, "parent.id");
				if (parent.getParent() != null) {
					map.put("topMenu_id", parent.getParent().getId());
				}
				message.setMessage(map);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage getMenuTree(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			Long authId = null;
			if (StringUtils.isNotBlank(request.getParameter("authId"))) {
				authId = Long.valueOf(request.getParameter("authId"));
			}
			TreeMenu tree = sysMenuManager.getTreeMenu(authId);
			message.setMessage(tree);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		return save(request, true);
	}

	@RequestMapping
	public @ResponseBody JsonMessage update(HttpServletRequest request) {
		return save(request, false);
	}

	private JsonMessage save(HttpServletRequest request, boolean isNew) {
		JsonMessage message = new JsonMessage();
		SysMenu menu = null;
		try {
			if (isNew) {
				menu = new SysMenu();
			} else {
				menu = sysMenuManager.load(ServletRequestUtils.getLongParameter(request, "id"));
				int level = Integer.parseInt(request.getParameter("menuLevel"));
				if (level != menu.getMenuLevel()) {
					if (CollectionUtils.isNotEmpty(menu.getChildMenus())) {
						throw new PlatformException(PlatformErrorCode.MENU_HAS_CHILD_CHANGE_LEVEL);
					}
				}
			}
			BindingResult result = SpringMVCUtils.bindObject(request, menu);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				Long parentId = ServletRequestUtils.getLongParameter(request, "parent_id");
				if (parentId == null) {
					menu.setParent(null);
				} else {
					if (SysMenu.LEVEL_ONE == menu.getMenuLevel()) {
						menu.setParent(null);
					} else {
						SysMenu parent = sysMenuManager.load(parentId);
						menu.setParent(parent);
					}
				}
				sysMenuManager.checkMenu(menu);
				sysMenuManager.saveOrUpdate(menu);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage remove(@RequestParam Long menuId) {
		JsonMessage message = new JsonMessage();
		try {
			sysMenuManager.removeMenu(menuId);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

}
