package com.justinmobile.tsm.system.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
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
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.system.domain.SystemParams;
import com.justinmobile.tsm.system.manager.SystemParamsManager;

@Controller
@RequestMapping("/sysParams/")
public class SystemParamsController {

	@Autowired
	private SystemParamsManager systemParamsManager;

	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SystemParams> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = systemParamsManager.findPage(page, filters);
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
	public @ResponseBody
	JsonResult getParamsByType(@RequestParam String type) {
		JsonResult result = new JsonResult();
		try {
			List<SystemParams> list = systemParamsManager.getParamsByType(type);
			result.setResult(list, null, null);
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
	public @ResponseBody
	JsonMessage getParamsByTypeToMap(@RequestParam String type) {
		JsonMessage message = new JsonMessage();
		try {
			List<SystemParams> list = systemParamsManager.getParamsByType(type);
			if (CollectionUtils.isNotEmpty(list)) {
				Map<String, String> map = new HashMap<String, String>();
				for (SystemParams p : list) {
					map.put(p.getKey(), p.getValue());
				}
				message.setMessage(map);
			} else {
				throw new PlatformException(PlatformErrorCode.SYSTEM_PARAMS_TYPE_NOT_EXIST);
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
	public @ResponseBody
	JsonMessage getParam(@RequestParam Long paramId) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(systemParamsManager.load(paramId).toMap(null, null));
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
	public @ResponseBody
	JsonMessage add(HttpServletRequest request) {
		return save(request, true);
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage update(HttpServletRequest request) {
		return save(request, false);
	}

	private JsonMessage save(HttpServletRequest request, boolean isNew) {
		JsonMessage message = new JsonMessage();
		SystemParams param = null;
		try {
			if (isNew) {
				param = new SystemParams();
			} else {
				param = systemParamsManager.load(ServletRequestUtils.getLongParameter(request, "id"));
			}
			BindingResult result = SpringMVCUtils.bindObject(request, param);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String newType = request.getParameter("newType");
				if (StringUtils.isNotBlank(newType)) {
					param.setType(newType);
				}
				if (StringUtils.isBlank(param.getType())) {
					throw new PlatformException(PlatformErrorCode.SYSTEM_PARAMS_TYPE_ERROR);
				}
				if (systemParamsManager.checkExistByTypeAndKey(param)) {
					throw new PlatformException(PlatformErrorCode.SYSTEM_PARAMS_EXIST);
				}
				systemParamsManager.saveOrUpdate(param);
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
	public @ResponseBody
	JsonMessage remove(@RequestParam Long paramId) {
		JsonMessage message = new JsonMessage();
		try {
			systemParamsManager.remove(paramId);
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
	public @ResponseBody
	JsonMessage getAllType() {
		JsonMessage message = new JsonMessage();
		try {
			List<String> types = systemParamsManager.getAllParamType();
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			if (CollectionUtils.isNotEmpty(types)) {
				for (String type : types) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("key", type);
					map.put("value", type);
					result.add(map);
				}
			}
			message.setMessage(result);
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
