package com.justinmobile.security.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
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
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysResource;
import com.justinmobile.security.manager.SysResourceManager;

@Controller
@RequestMapping("/res/")
public class SysResourceController {
	
	@Autowired
	private SysResourceManager sysResourceManager;
	
	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SysResource> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = sysResourceManager.findPage(page, filters);
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
	public @ResponseBody JsonMessage getRes(@RequestParam Long resId) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(sysResourceManager.load(resId).toMap(null, null));
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
		SysResource res = null;
		try {
			if (isNew) {
				res = new SysResource();
			} else {
				res = sysResourceManager.load(ServletRequestUtils.getLongParameter(request, "id"));
			}
			BindingResult result = SpringMVCUtils.bindObject(request, res);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				sysResourceManager.checkResource(res);
				sysResourceManager.saveOrUpdate(res);
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
	public @ResponseBody JsonMessage remove(@RequestParam Long resId) {
		JsonMessage message = new JsonMessage();
		try {
			SysResource res = sysResourceManager.load(resId);
			if (CollectionUtils.isEmpty(res.getSysAuthorities())) {
				sysResourceManager.remove(res);
			} else {
				throw new PlatformException(PlatformErrorCode.RES_IN_USE_BY_AUTH);
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
	public @ResponseBody JsonMessage removeAuths(@RequestParam Long resId) {
		JsonMessage message = new JsonMessage();
		try {
			SysResource res = sysResourceManager.load(resId);
			Set<SysAuthority> auths = res.getSysAuthorities();
			if (CollectionUtils.isNotEmpty(auths)) {
				for (SysAuthority auth : auths) {
					auth.getSysResources().remove(res);
				}
			}
			auths.clear();
			sysResourceManager.saveOrUpdate(res);
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
