package com.justinmobile.security.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysMenu;
import com.justinmobile.security.domain.SysResource;
import com.justinmobile.security.intercept.web.DataBaseFilterInvocationSecurityMetadataSource;
import com.justinmobile.security.manager.SysAuthorityManager;
import com.justinmobile.security.manager.SysMenuManager;
import com.justinmobile.security.manager.SysResourceManager;

@Controller
@RequestMapping("/auth/")
public class SysAuthorityController {
	
	@Autowired
	private SysAuthorityManager authorityManager;
	
	@Autowired
	private SysResourceManager resourceManager;
	
	@Autowired
	private SysMenuManager menuManager;
	
	@Autowired
	@Qualifier("dbSecurityMetadataSource")
	private DataBaseFilterInvocationSecurityMetadataSource dbSecurityMetadataSource;
	
	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SysAuthority> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = authorityManager.findPage(page, filters);
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
	public @ResponseBody JsonMessage getAuth(@RequestParam Long authId) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(authorityManager.load(authId).toMap("status", null, null));
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
		SysAuthority sysAuthority = null;
		try {
			if (isNew) {
				sysAuthority = new SysAuthority();
			} else {
				sysAuthority = authorityManager.load(ServletRequestUtils.getLongParameter(request, "id"));
			}
			BindingResult result = SpringMVCUtils.bindObject(request, sysAuthority);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				authorityManager.checkAuthName(sysAuthority);
				authorityManager.saveOrUpdate(sysAuthority);
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
	public @ResponseBody JsonMessage remove(@RequestParam Long authId) {
		JsonMessage message = new JsonMessage();
		try {
			authorityManager.removeAuthority(authId);
			dbSecurityMetadataSource.initSecureUrls();
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
	public @ResponseBody JsonMessage selectRes(@RequestParam Long authId, @RequestParam String resId) {
		JsonMessage message = new JsonMessage();
		try {
			SysAuthority auth = authorityManager.load(authId);
			Set<SysResource> reses = auth.getSysResources();
			reses.clear();
			if (StringUtils.isNotBlank(resId)) {
				String[] resIds = StringUtils.split(resId, ",");
				if (ArrayUtils.isNotEmpty(resIds)) {
					for (String res : resIds) {
						reses.add(resourceManager.load(Long.valueOf(res)));
					}
				}
			}
			authorityManager.saveOrUpdate(auth);
			dbSecurityMetadataSource.initSecureUrls();
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
	public @ResponseBody JsonMessage selectMenus(@RequestParam Long authId, @RequestParam String menuId) {
		JsonMessage message = new JsonMessage();
		try {
			SysAuthority auth = authorityManager.load(authId);
			Set<SysMenu> menus = auth.getSysMenus();
			menus.clear();
			if (StringUtils.isNotBlank(menuId)) {
				String[] menuIds = StringUtils.split(menuId, ",");
				if (ArrayUtils.isNotEmpty(menuIds)) {
					for (String menu : menuIds) {
						menus.add(menuManager.load(Long.valueOf(menu)));
					}
				}
			}
			authorityManager.saveOrUpdate(auth);
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
	public @ResponseBody JsonMessage getResByAuth(@RequestParam Long authId) {
		JsonMessage message = new JsonMessage();
		try {
			SysAuthority auth = authorityManager.load(authId);
			message.setMessage(resourceManager.getResToMap(auth.getSysResources()));
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
	public @ResponseBody JsonMessage getNotResByAuth(@RequestParam Long authId) {
		JsonMessage message = new JsonMessage();
		try {
			SysAuthority auth = authorityManager.load(authId);
			List<SysResource> ress = resourceManager.getAll();
			ress.removeAll(auth.getSysResources());
			message.setMessage(resourceManager.getResToMap(ress));
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
