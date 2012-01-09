package com.justinmobile.log.interceptor;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogManager;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;

/**
 * 对用户退出系统做日志
 * 
 */
public class LogoutLogger implements LogoutHandler {

	private OperateLogManager operateLogManager;

	private SysUserManager sysUserManager;

	public void setOperateLogManager(OperateLogManager operateLogManager) {
		this.operateLogManager = operateLogManager;
	}

	public void setSysUserManager(SysUserManager sysUserManager) {
		this.sysUserManager = sysUserManager;
	}

	/**
	 * @param request
	 *            not used (can be <code>null</code>)
	 * @param response
	 *            not used (can be <code>null</code>)
	 * @param authentication
	 *            not used (can be <code>null</code>)
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {

		OperateLog log = new OperateLog();
		Set<OperateLogParam> logParamsSet = new HashSet<OperateLogParam>();
		log.setOperateName("退出系统");
		log.setSuccess(0);
		log.setTime(Calendar.getInstance());
		SysUser user = null;
		try {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userName = userDetails.getUsername();
			user = sysUserManager.getUserByName(userName);
		} catch (Exception e) {
			log.setDescription("获取用户信息失败");
			return;
		}
		if (user != null) {
			log.setLoginName(user.getUserName());
		}
		try {
			operateLogManager.createLog(log, logParamsSet);
		} catch (PlatformException e) {
			e.printStackTrace();
		}

	}

}
