/**
 * Copyright (c) 2005-2009 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: SpringSecurityUtils.java,v 1.2 2011/08/10 09:26:21 gaofeng Exp $
 */
package com.justinmobile.security.utils;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.justinmobile.security.details.UserWithSalt;

/**
 * SpringSecurity的工具类.
 * 
 * 注意. 本类只支持SpringSecurity 3.0.x.
 * 
 * @author peak
 */
public class SpringSecurityUtils {
	
	public static final String ANONYMOUS_USER = "anonymousUser";

	/**
	 * 取得当前用户, 返回值为SpringSecurity的User类或其子类, 如果当前用户未登录则返回null.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends UserWithSalt> T getCurrentUser() {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof User)) {
			return null;
		}
		return (T) principal;
	}

	/**
	 * 取得当前用户的登录名, 如果当前用户未登录则返回空字符串.
	 */
	public static String getCurrentUserName() {
		Authentication authentication = getAuthentication();
		String currentUserName = authentication.getName();
		if (authentication == null || authentication.getPrincipal() == null || ANONYMOUS_USER.equals(currentUserName)) {
			return "";
		}
		return currentUserName;
	}

	/**
	 * 取得当前用户登录IP, 如果当前用户未登录则返回空字符串.
	 */
	public static String getCurrentUserIp() {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return "";
		}
		Object details = authentication.getDetails();
		if (!(details instanceof WebAuthenticationDetails)) {
			return "";
		}
		WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;
		return webDetails.getRemoteAddress();
	}

	/**
	 * 判断用户是否拥有角色, 如果用户拥有参数中的任意一个角色则返回true.
	 */
	public static boolean hasAnyRole(String... roles) {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return false;
		}
		Collection<GrantedAuthority> grantedAuthorityList = authentication.getAuthorities();
		for (String role : roles) {
			for (GrantedAuthority authority : grantedAuthorityList) {
				if (role.equals(authority.getAuthority())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 将UserDetails保存到Security Context.
	 * 
	 * @param userDetails
	 *            已初始化好的用户信息.
	 * @param request
	 *            用于获取用户IP地址信息,可为Null.
	 */
	public static void saveUserDetailsToContext(UserDetails userDetails, HttpServletRequest request) {
		PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userDetails,
				userDetails.getPassword(), userDetails.getAuthorities());
		if (request != null) {
			authentication.setDetails(new WebAuthenticationDetails(request));
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 取得Authentication, 如当前SecurityContext为空时返回null.
	 */
	private static Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return null;
		}
		return context.getAuthentication();
	}
}
