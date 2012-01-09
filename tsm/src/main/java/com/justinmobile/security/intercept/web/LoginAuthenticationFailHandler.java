package com.justinmobile.security.intercept.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysUser.USER_STATUS;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Service("failAuthenticationDispatcher")
public class LoginAuthenticationFailHandler implements AuthenticationFailureHandler {

	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private SpBaseInfoManager spBaseInfoManager;
	
	@Autowired
	private CustomerManager customerManager;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		JsonMessage message = new JsonMessage();
		message.setSuccess(Boolean.FALSE);
		Authentication auth = exception.getAuthentication();
		try {
			if (auth == null) {
				throw new PlatformException(PlatformErrorCode.USER_LOGIN_ERROR);
			}
			String userName = auth.getName();
			SysUser user = sysUserManager.getUserByName(userName);
			if (user == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_EXIST);
			}
			if (USER_STATUS.DISABLED.getValue().equals(user.getStatus())) {
				if (SpecialRoleType.SERVICE_PROVIDER.name().equals(user.getSysRole().getRoleName())) {//提供商
					SpBaseInfo sp = spBaseInfoManager.load(user.getId());
					if (sp != null) {
						if (SpBaseInfo.INBLACK == sp.getInBlack()) {
							throw new PlatformException(PlatformErrorCode.SP_BLACKLIST_ERROR, "该用户所属的");
						} else {
							throw new PlatformException(PlatformErrorCode.USER_STATUS_ERROR);
						}
					}
				} else if (SpecialRoleType.CUSTOMER.name().equals(user.getSysRole().getRoleName()) 
						|| SpecialRoleType.CUSTOMER_NOT_ACTIVE.name().equals(user.getSysRole().getRoleName())) {
					Customer c = customerManager.load(user.getId());
					if (c != null) {
						throw new PlatformException(PlatformErrorCode.USER_STATUS_ERROR);
					}
				} else {
					throw new PlatformException(PlatformErrorCode.USER_STATUS_ERROR);
				}
			}
			message.setMessage("登录失败，请重新登录");
		} catch (PlatformException e) {
			message.setMessage(e.getMessage());
		}

		ServletUtils.sendMessage(response, JsonBinder.buildNormalBinder().toJson(message));
	}

}
