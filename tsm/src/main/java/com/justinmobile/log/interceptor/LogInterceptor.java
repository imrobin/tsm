package com.justinmobile.log.interceptor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.collect.Lists;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogManager;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;

public class LogInterceptor extends HandlerInterceptorAdapter {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private List<String> ignoreControllers = new LinkedList<String>();

	private List<String> ignoreHandlers = new LinkedList<String>();

	private List<String> ignoreParameters = new LinkedList<String>();

	private List<String> noLoginActions = new LinkedList<String>();
	
	private List<String> roleNames = Lists.newArrayList();

	private OperateLogManager operateLogManager;

	private SysUserManager sysUserManager;

	private Properties handlerNamesMapping;// 资源类型映射

	public LogInterceptor() {

		ClassPathResource res = new ClassPathResource("/config/lognames.properties");
		handlerNamesMapping = new Properties();
		try {
			handlerNamesMapping.load(res.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String thisController = handler.getClass().getSimpleName();
		String thisHandler = request.getParameter("m")==null?"index":request.getParameter("m");	
		String actionName = thisController + "." + thisHandler;
		if (!isIgnore(thisController, ignoreControllers) && !isIgnore(thisHandler, ignoreHandlers)) {
			SysUser user = getUserName();
			if (user != null) {//没登陆的用户不考虑记录日志
				String roleName = user.getSysRole().getRoleName();
				if (roleNames.contains(roleName)) {//限制角色记录日志
					String thisHandleName = (String) handlerNamesMapping.get(actionName);// 获得改操作的中文名称
					if (StringUtils.isEmpty(thisHandleName)) {
						log.warn(thisHandleName + " is not config Name in spring");
						return;
						//thisHandleName = actionName; //如果在配置文件里没有的方法直接不存了
					}
					OperateLog log = new OperateLog();
					log.setTime(Calendar.getInstance());
					log.setOperateName(thisHandleName.length() > 128 ? thisHandleName.substring(0, 128) : thisHandleName);
					if (!noLoginActions.contains(actionName)) {
						log.setLoginName(user.getUserName());
					}
					String errorMsg = (String) request.getAttribute("invokeFailureMessage");
					if (StringUtils.isNotEmpty(errorMsg)) {
						log.setDescription(errorMsg.length() > 255 ? errorMsg.substring(0, 255) : errorMsg);
						log.setSuccess(1);
					} else {
						log.setSuccess(0);
					}
					try {
						operateLogManager.createLog(log, bindLogParams(request, log));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Set<OperateLogParam> bindLogParams(HttpServletRequest request, OperateLog log) throws IOException {
		Set<OperateLogParam> logParamsSet = new HashSet<OperateLogParam>();
		// 处理request中的parameter形式的参数
		Enumeration<String> paramEnum = request.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String param = paramEnum.nextElement();
			if (!isIgnore(param, ignoreParameters)) {
				String[] values = request.getParameterValues(param);
				if (values.length > 0) {
					for (String v : values) {
						OperateLogParam lp = new OperateLogParam();
						lp.setKey(param);
						lp.setValue(v);
						lp.setOperateLog(log);
						logParamsSet.add(lp);
					}
				}
			}
		}

		return logParamsSet;
	}

	private boolean isIgnore(String str, List<String> ignoreList) {
		for (String ignoreStr : ignoreList) {
			if (isLike(ignoreStr, str)) {
				return true;
			}
		}
		return false;
	}

	private boolean isLike(String srcIncludeStar, String dest) {
		if ("*".equals(srcIncludeStar)) {
			return true;
		} else if (srcIncludeStar.indexOf("*") == 0) {
			if (dest.indexOf(srcIncludeStar.substring(1, srcIncludeStar.length())) == dest.length()
					- srcIncludeStar.length() + 1) {
				return true;
			} else {
				return false;
			}
		} else if (srcIncludeStar.indexOf("*") == srcIncludeStar.length() - 1) {
			if (dest.indexOf(srcIncludeStar.substring(0, srcIncludeStar.length() - 1)) == 0) {
				return true;
			} else {
				return false;
			}
		} else if (srcIncludeStar.equals(dest)) {
			return true;
		}
		return false;
	}

	private SysUser getUserName() throws Exception {
		SysUser user = null;
		try {
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			user = sysUserManager.getUserByName(userDetails.getUsername());
		} catch (Exception e) {
			log.warn("get user error, please conform you have login!");
		}
		if (null == user) {
			log.warn("get user error, please conform you have login!");
		}
		return user;
	}

	public void setSysUserManager(SysUserManager sysUserManager) {
		this.sysUserManager = sysUserManager;
	}
	
	public void setOperateLogManager(OperateLogManager operateLogManager) {
		this.operateLogManager = operateLogManager;
	}

	public void setIgnoreControllers(List<String> ignoreControllers) {
		this.ignoreControllers = ignoreControllers;
	}

	public void setIgnoreHandlers(List<String> ignoreHandlers) {
		this.ignoreHandlers = ignoreHandlers;
	}

	public void setIgnoreParameters(List<String> ignoreParameters) {
		this.ignoreParameters = ignoreParameters;
	}


	public void setNoLoginActions(List<String> noLoginActions) {
		this.noLoginActions = noLoginActions;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

}