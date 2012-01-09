package com.justinmobile.security.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
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
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysAuthorityManager;
import com.justinmobile.security.manager.SysRoleManager;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;

@Controller
@RequestMapping("/user/")
public class SysUserController {
	
	private static final String DEFAUT_PASSWORD = "000000";
	
	@Autowired
	private SysUserManager userManager;
	
	@Autowired
	private SysAuthorityManager authorityManager;
	
	@Autowired
	private SysRoleManager roleManager;
	
	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SysUser> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = userManager.findPage(page, filters);
			result.setPage(page, null, "sysRole.description");
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
	public @ResponseBody JsonMessage getCurrentUser() {
		JsonMessage message = new JsonMessage();
		try {
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			SysUser user = userManager.getUserByName(currentUserName);
			Map<String, Object> map = user.toMap(null, null);
			map.put("roleName", user.getSysRole().getRoleName());
			message.setMessage(map);
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
	public @ResponseBody JsonMessage getUser(@RequestParam Long userId) {
		JsonMessage message = new JsonMessage();
		try {
			SysUser user = userManager.load(userId);
			Map<String, Object> map = user.toMap("status", null, null);
			map.put("roleName", user.getSysRole().getRoleName());
			message.setMessage(map);
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
	public @ResponseBody JsonMessage add(HttpServletRequest request, @RequestParam String roleName) {
		return save(request, roleName, true);
	}

	@RequestMapping
	public @ResponseBody JsonMessage edit(HttpServletRequest request, @RequestParam String roleName) {
		return save(request, roleName, false);
	}

	@RequestMapping
	public @ResponseBody JsonMessage save(HttpServletRequest request, @RequestParam String roleName, boolean isNew) {
		JsonMessage message = new JsonMessage();
		SysUser user = null;
		try {
			if (isNew) {
				user = new SysUser();
			} else {
				user = userManager.load(ServletRequestUtils.getLongParameter(request, "id"));
			}
			if (user == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_EXIST);
			}
			BindingResult result = SpringMVCUtils.bindObject(request, user);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				userManager.checkUniqueUser(user);
				if (SpecialRoleType.SUPER_OPERATOR.name().equals(roleName)) {//超级管理员无省份
					user.setProvince(null);
				}
				if (isNew) {
					userManager.addUser(user, SpecialRoleType.valueOf(roleName));
				} else {
					userManager.updateUser(user, SpecialRoleType.valueOf(roleName));
				}
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
	public @ResponseBody JsonMessage updateSelf(HttpServletRequest request, @RequestParam String roleName) {
		JsonMessage message = new JsonMessage();
		try {
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			SysUser user = userManager.getUserByName(currentUserName);
			if (user == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			BindingResult result = SpringMVCUtils.bindObject(request, user);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				SysRole role = roleManager.getRoleByName(roleName);
				user.setSysRole(role);
				userManager.checkUniqueUser(user);
				userManager.updateUser(user, SpecialRoleType.valueOf(roleName));
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
	public @ResponseBody JsonMessage resetPassword(@RequestParam Long userId) {
		JsonMessage message = new JsonMessage();
		try {
			SysUser user = userManager.load(userId);
			user = userManager.encodeWithSalt(user, DEFAUT_PASSWORD);
			userManager.saveOrUpdate(user);
			message.setMessage(PlatformMessage.PASSWORD_RESET.getDefaultMessage(DEFAUT_PASSWORD));
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
	public @ResponseBody JsonMessage validatePassword(@RequestParam String password) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(userName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			Boolean bln = userManager.validatePassword(userName, password);
			message.setSuccess(bln);
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
	public @ResponseBody JsonMessage modifyPassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String reNewPassword) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(userName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			userManager.modifyPassword(userName, oldPassword, newPassword, reNewPassword);
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
	public @ResponseBody JsonMessage retrievePassword(@RequestParam Long userId, @RequestParam String newPassword, @RequestParam String reNewPassword) {
		JsonMessage message = new JsonMessage();
		try {
			userManager.retrievePassword(userId, newPassword, reNewPassword);
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
	public @ResponseBody JsonMessage remove(@RequestParam Long userId) {
		JsonMessage message = new JsonMessage();
		try {
			SysUser user = userManager.load(userId);
			if (user == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_EXIST);
			}
			String roleName = user.getSysRole().getRoleName();
			if (roleName.equals(SpecialRoleType.OPERATOR_AUDITOR.name()) || 
				roleName.equals(SpecialRoleType.OPERATOR_CUSTOMER_SERVICE.name())||
				roleName.equals(SpecialRoleType.SUPER_OPERATOR.name())) {
				userManager.remove(user);
			} else {
				throw new PlatformException(PlatformErrorCode.USER_NOT_DELETE_BY_ADMIN);
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
	public @ResponseBody JsonMessage selectAuths(@RequestParam Long userId, @RequestParam String authId) {
		JsonMessage message = new JsonMessage();
		try {
			SysUser user = userManager.load(userId);
			Set<SysAuthority> auths = user.getSysAuthorities();
			auths.clear();
			if (StringUtils.isNotBlank(authId)) {
				String[] authIds = StringUtils.split(authId, ",");
				if (ArrayUtils.isNotEmpty(authIds)) {
					for (String auth : authIds) {
						auths.add(authorityManager.load(Long.valueOf(auth)));
					}
				}
			}
			userManager.saveOrUpdate(user);
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
	public @ResponseBody JsonMessage getAuthsByUserName(@RequestParam Long userId) {
		JsonMessage message = new JsonMessage();
		try {
			SysUser user = userManager.load(userId);
			List<SysAuthority> auths = authorityManager.getSysAuthorityByUser(user.getUserName());
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
	
	@RequestMapping
	public @ResponseBody JsonMessage getNotAuthsByUserName(@RequestParam Long userId) {
		JsonMessage message = new JsonMessage();
		try {
			SysUser user = userManager.load(userId);
			List<SysAuthority> auths = authorityManager.getNotSysAuthorityByUser(user.getUserName());
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
