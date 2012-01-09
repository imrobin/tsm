package com.justinmobile.security.intercept.web;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogManager;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.manager.SysUserManager;

@Service("authenticationDispatcher")
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private SysUserManager sysUserManager;
	@Autowired
	private OperateLogManager operateLogManager;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String userName = authentication.getName();
		SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(userName);
		user.setLatestLogin(Calendar.getInstance());
		sysUserManager.saveOrUpdate(user);
		SysRole role = user.getSysRole();
		JsonMessage message = new JsonMessage();
		if (role == null) {//没角色的用户
			message.setSuccess(Boolean.FALSE);
			message.setMessage(PlatformErrorCode.UNKNOWN_ERROR.getDefaultMessage());
		} else {
			message.setMessage(request.getContextPath() + role.getLoginSuccessForward());
		}
		if (request.getParameter("from") != null && !request.getParameter("from").equals("")) {
			System.out.println(request.getParameter("from"));
			message.setMessage(request.getContextPath()+request.getParameter("from"));
		}
		//后台用户登入写operateLog
		if (role.getRoleName().equals(SpecialRoleType.OPERATOR_AUDITOR.toString()) || 
				role.getRoleName().equals(SpecialRoleType.OPERATOR_CUSTOMER_SERVICE.toString()) || 
				role.getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())){
			OperateLog log = new OperateLog();
			log.setTime(Calendar.getInstance());
			log.setOperateName("后台用户登录");
			log.setLoginName(user.getUserName());
			log.setSuccess(0);
			operateLogManager.createLog(log, new HashSet<OperateLogParam>());
		}
		//end
		ServletUtils.sendMessage(response, JsonBinder.buildNormalBinder().toJson(message));
	}

}
