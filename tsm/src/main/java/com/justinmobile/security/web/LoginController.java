package com.justinmobile.security.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;

@Controller
@RequestMapping("/login/")
public class LoginController {
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@RequestMapping
	public String index(HttpServletRequest request) {
		return "/security/login.jsp";
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage fail(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		message.setSuccess(Boolean.FALSE);
		message.setMessage(PlatformErrorCode.USER_LOGIN_ERROR.getDefaultMessage());
		return message;
	}
	
	@RequestMapping
	public String deniedAccess() {
		return "/security/login.jsp";
	}
	
	@RequestMapping
	public String myCenter() {
		String url = "/security/login.jsp";
		String userName = SpringSecurityUtils.getCurrentUserName();
		if (StringUtils.isNotBlank(userName)) {
			SysUser user = sysUserManager.getUserByName(userName);
			if (user != null) {
				SysRole role = user.getSysRole();
				if (SpecialRoleType.CUSTOMER.name().equals(role.getRoleName())) {
					url = "/home/customer/customerCenter.jsp";
				} else if (SpecialRoleType.SERVICE_PROVIDER.name().equals(role.getRoleName())) {
					url = "/home/sp/center.jsp";
				} else if (SpecialRoleType.CUSTOMER_NOT_ACTIVE.name().equals(role.getRoleName())) {
					url = role.getLoginSuccessForward();
				} else {
					url = "/admin/index.jsp";
				}
			}
		}
		return url;
	}
	
}
