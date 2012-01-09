package com.justinmobile.security.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Sets;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.manager.SysAuthorityManager;
import com.justinmobile.security.manager.SysRoleManager;
import com.justinmobile.security.manager.SysUserManager;

@Controller
@RequestMapping("/role/")
public class SysRoleController {
	
	@Autowired
	private SysRoleManager roleManager;
	
	@Autowired
	private SysAuthorityManager authorityManager;
	
	@Autowired
	private SysUserManager userManager;
	
	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SysRole> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = roleManager.findPage(page, filters);
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
	public @ResponseBody JsonMessage getRole(@RequestParam Long roleId) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(roleManager.load(roleId).toMap(null, null));
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
	public @ResponseBody JsonMessage getRoleType() {
		JsonMessage message = new JsonMessage();
		try {
			List<SysRole> roles = roleManager.getAll();
			if (CollectionUtils.isNotEmpty(roles)) {
				Map<String, String> map = new HashMap<String, String>();
				for (SysRole role : roles) {
					map.put(role.getRoleName(), role.getDescription());
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
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		return save(request, true);
	}

	@RequestMapping
	public @ResponseBody JsonMessage update(HttpServletRequest request) {
		return save(request, false);
	}

	private JsonMessage save(HttpServletRequest request, boolean isNew) {
		JsonMessage message = new JsonMessage();
		SysRole role = null;
		try {
			if (isNew) {
				role = new SysRole();
			} else {
				role = roleManager.load(ServletRequestUtils.getLongParameter(request, "id"));
			}
			BindingResult result = SpringMVCUtils.bindObject(request, role);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				roleManager.checkRole(role);
				roleManager.saveOrUpdate(role);
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
	public @ResponseBody JsonMessage remove(@RequestParam Long roleId) {
		JsonMessage message = new JsonMessage();
		try {
			roleManager.removeRole(roleId);
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
	public @ResponseBody JsonMessage selectAuths(@RequestParam Long roleId, @RequestParam String authId) {
		JsonMessage message = new JsonMessage();
		try {
			SysRole role = roleManager.load(roleId);
			Set<SysAuthority> auths = role.getSysAuthorities();
			Set<SysAuthority> cloneAuths = Sets.newHashSet();
			cloneAuths.addAll(auths);
			auths.clear();
			if (StringUtils.isNotBlank(authId)) {
				String[] authIds = StringUtils.split(authId, ",");
				if (ArrayUtils.isNotEmpty(authIds)) {
					for (String auth : authIds) {
						auths.add(authorityManager.load(Long.valueOf(auth)));
					}
				}
			}
			roleManager.saveOrUpdate(role);
			userManager.updateUseraAuths(role, cloneAuths);
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
	public @ResponseBody JsonMessage getAuthsByRole(@RequestParam Long roleId) {
		JsonMessage message = new JsonMessage();
		try {
			SysRole role = roleManager.load(roleId);
			message.setMessage(authorityManager.getAuthToMap(role.getSysAuthorities()));
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
	public @ResponseBody JsonMessage getNotAuthsByRole(@RequestParam Long roleId) {
		JsonMessage message = new JsonMessage();
		try {
			SysRole role = roleManager.load(roleId);
			List<SysAuthority> auths = authorityManager.getAll();
			auths.removeAll(role.getSysAuthorities());
			message.setMessage(authorityManager.getAuthToMap(auths));
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
