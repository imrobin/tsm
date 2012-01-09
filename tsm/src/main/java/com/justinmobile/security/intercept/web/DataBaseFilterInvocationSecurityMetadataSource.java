package com.justinmobile.security.intercept.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.UrlMatcher;

import com.justinmobile.core.utils.hibernate.OpenSessionInMethod;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysResource;
import com.justinmobile.security.manager.SysResourceManager;

/**
 * 拦截访问的URL
 * 
 * @author peak
 * 
 */
public class DataBaseFilterInvocationSecurityMetadataSource implements InitializingBean, FilterInvocationSecurityMetadataSource {

	private final Log logger = LogFactory.getLog(getClass());

	private final String defaultParam = "m";

	private UrlMatcher urlMatcher;

	private boolean stripQueryStringFromUrls = true;
	
	@Autowired
	private OpenSessionInMethod openSessionInMethod;

	private List<String> retainParameters = new ArrayList<String>();

	/**
	 * 将需要拦截的url存到map中进行比较
	 * 
	 */
	private Map<Object, List<ConfigAttribute>> requestMap = new LinkedHashMap<Object, List<ConfigAttribute>>();

	@Autowired
	private SysResourceManager sysResourceManager;

	public void afterPropertiesSet() throws Exception {
		// 默认保留method参数进行区分
		if (CollectionUtils.isEmpty(retainParameters)) {
			this.retainParameters.add(defaultParam);
		} else {
			this.retainParameters.addAll(retainParameters);
		}
		//TODO 无法拦截到初始化的方法办法，暂时用注入的方式显示声明
		openSessionInMethod.openSession();
		initSecureUrls();
		openSessionInMethod.releaseSession();
	}

	/**
	 * 初始化数据库内所有的url资源
	 */
	public void initSecureUrls() {
		//先清空map
		this.requestMap.clear();
		List<SysResource> resources = sysResourceManager.getAll();
		List<String> resUrls = new ArrayList<String>();
		Map<String, List<ConfigAttribute>> useToMap = new HashMap<String, List<ConfigAttribute>>();
		if (CollectionUtils.isNotEmpty(resources)) {
			for (SysResource sysResource : resources) {
				//如果url没有加入到权限管理中，则不进行拦截
				if (CollectionUtils.isNotEmpty(sysResource.getSysAuthorities())) {
					String key = sysResource.getFilterString();
					resUrls.add(key);
					Set<GrantedAuthority> grantedAuthorities = SysAuthority.toGrantedAuthority(sysResource.getSysAuthorities());
					useToMap.put(key, authorityToConfig(grantedAuthorities));
				}
			}
		}
		// 倒叙，将带*号的排后面
		Collections.sort(resUrls);
		Collections.reverse(resUrls);
		if (CollectionUtils.isNotEmpty(resUrls)) {
			for (String url : resUrls) {
				if (urlMatcher.requiresLowerCaseUrl()) {
					url = url.toLowerCase();
				}
				this.requestMap.put(urlMatcher.compile(url), useToMap.get(url));
				if (logger.isDebugEnabled()) {
					logger.debug("Added URL pattern: " + url + "; attributes: " + useToMap.get(url));
				}
			}
		}
	}
	
	public void refreshSecureUrls() {
		this.requestMap.clear();
		initSecureUrls();
	}

	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if ((object == null) || !this.supports(object.getClass())) {
			throw new IllegalArgumentException("Object must be a FilterInvocation");
		}

		String url = ((FilterInvocation) object).getRequestUrl();

		return lookupAttributes(url);
	}

	public Collection<ConfigAttribute> lookupAttributes(String url) {
		// 仅保留必要的url，删除多余的url信息
		url = stripUrl(url);
		if (urlMatcher.requiresLowerCaseUrl()) {
			url = url.toLowerCase();

			if (logger.isDebugEnabled()) {
				logger.debug("Converted URL to lowercase, from: '" + url + "'; to: '" + url + "'");
			}
		}
		return matchUrl(url);
	}

	private String stripUrl(String url) {
		StringBuffer resultUrl = new StringBuffer(url);
		if (stripQueryStringFromUrls) {
			StringBuffer paramUrl = new StringBuffer();
			// Strip anything after a question mark symbol, as per SEC-161. See
			// also SEC-321
			int firstQuestionMarkIndex = resultUrl.indexOf("?");
			if (firstQuestionMarkIndex != -1) {
				resultUrl.delete(firstQuestionMarkIndex, resultUrl.length());
				if (CollectionUtils.isNotEmpty(retainParameters)) {
					String remainUrl = url.substring(firstQuestionMarkIndex + 1);
					if (StringUtils.isNotEmpty(remainUrl)) {
						String[] params = remainUrl.toString().split("&");
						if (!(params == null || params.length == 0)) {
							for (String str : params) {
								for (String param : retainParameters) {
									if (str.indexOf(param + "=") == 0) {
										paramUrl.append(str + "&");
									}
								}
							}
						}
					}
					if (paramUrl.length() > 0) {
						paramUrl.deleteCharAt(paramUrl.length() - 1);
						resultUrl.append("?" + paramUrl);
					}
				}
			}
		}
		return resultUrl.toString();
	}

	private Collection<ConfigAttribute> matchUrl(String url) {
		final boolean debug = logger.isDebugEnabled();

		for (Map.Entry<Object, List<ConfigAttribute>> entry : this.requestMap.entrySet()) {
			Object p = entry.getKey();
			boolean matched = urlMatcher.pathMatchesUrl(entry.getKey(), url);

			if (debug) {
				logger.debug("Candidate is: '" + url + "'; pattern is " + p + "; matched=" + matched);
			}

			if (matched) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 将grantedAuthorities转成ConfigAttribute
	 * 
	 * @param grantedAuthorities
	 * @return
	 */
	private List<ConfigAttribute> authorityToConfig(Set<GrantedAuthority> grantedAuthorities) {
		List<ConfigAttribute> configAttributes = new ArrayList<ConfigAttribute>();
		for (GrantedAuthority grantedAuthority : grantedAuthorities) {
			ConfigAttribute ca = new SecurityConfig(grantedAuthority.getAuthority());
			configAttributes.add(ca);
		}
		return configAttributes;
	}

	public boolean isConvertUrlToLowercaseBeforeComparison() {
		return urlMatcher.requiresLowerCaseUrl();
	}

	public void setStripQueryStringFromUrls(boolean stripQueryStringFromUrls) {
		this.stripQueryStringFromUrls = stripQueryStringFromUrls;
	}

	public void setUrlMatcher(UrlMatcher urlMatcher) {
		this.urlMatcher = urlMatcher;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

		if (MapUtils.isNotEmpty(requestMap)) {
			for (Map.Entry<Object, List<ConfigAttribute>> entry : requestMap.entrySet()) {
				allAttributes.addAll(entry.getValue());
			}
		}
		return allAttributes;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

}
