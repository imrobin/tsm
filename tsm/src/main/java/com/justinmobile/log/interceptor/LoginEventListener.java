package com.justinmobile.log.interceptor;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogManager;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;

public class LoginEventListener implements ApplicationListener<ApplicationEvent> {

	private OperateLogManager operateLogManager;

	private SysUserManager sysUserManager;

	public void setSysUserManager(SysUserManager sysUserManager) {
		this.sysUserManager = sysUserManager;
	}

	public void setOperateLogManager(OperateLogManager operateLogManager) {
		this.operateLogManager = operateLogManager;
	}

	public void onApplicationEvent(ApplicationEvent event) {

		String userName = "";
		OperateLog log = new OperateLog();
		if (event instanceof AuthenticationSuccessEvent) {
			AuthenticationSuccessEvent authEvent = (AuthenticationSuccessEvent) event;
			Authentication auth = authEvent.getAuthentication();
			userName = auth.getName();
			log.setSuccess(0);
		} else if (event instanceof AbstractAuthenticationFailureEvent) {
			AbstractAuthenticationFailureEvent authEvent = (AbstractAuthenticationFailureEvent) event;
			Authentication auth = authEvent.getAuthentication();
			userName = auth.getName();
			log.setSuccess(1);
			String errorMsg = getFailureMsg((AbstractAuthenticationFailureEvent) event);
			log.setDescription(errorMsg);

		} else {
			return;
		}

		SysUser user = null;
		Set<OperateLogParam> logParamsSet = new HashSet<OperateLogParam>();
		Calendar loginTime = Calendar.getInstance();
		log.setTime(loginTime);
		log.setOperateName("登录系统");
		if (log.getSuccess().equals(new Integer(0))) {// 成功的登录，记录登录用户
			user = sysUserManager.getUserByName(userName);
			user.setLatestLogin(loginTime);
			sysUserManager.saveOrUpdate(user);
			log.setLoginName(user.getUserName());
		} else {// 失败的登录，记录登录失败的用户名
			OperateLogParam failedLoginName = new OperateLogParam();
			failedLoginName.setKey("failedLoginName");
			failedLoginName.setValue(userName);
			failedLoginName.setOperateLog(log);
			logParamsSet.add(failedLoginName);
		}
		doLog(log, logParamsSet);

	}

	private void doLog(OperateLog log, Set<OperateLogParam> logedStrs) {
		try {
			operateLogManager.createLog(log, logedStrs);
		} catch (PlatformException e) {
			e.printStackTrace();
		}
	}

	private String getFailureMsg(AbstractAuthenticationFailureEvent event) {
		String msg = "登录失败";
		if (event instanceof AuthenticationFailureLockedEvent) {
			msg += "：用户已被锁定";
		} else if (event instanceof AuthenticationFailureDisabledEvent) {
			msg += "：用户已被禁止";
		} else if (event instanceof AuthenticationFailureLockedEvent) {
			msg += "：用户已被锁定";
		} else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
			msg += "：无效的用户名或密码";
		} else if (event instanceof AuthenticationFailureExpiredEvent) {
			msg += "：用户登录超时";
		} else if (event instanceof AuthenticationFailureProviderNotFoundEvent) {
			msg += "：未找到该用户注册权限";
		} else if (event instanceof AuthenticationFailureServiceExceptionEvent) {
			msg += "：认证管理器异常";
		}
		return msg;

	}
}
