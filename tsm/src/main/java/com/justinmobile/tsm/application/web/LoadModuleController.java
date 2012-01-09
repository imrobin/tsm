package com.justinmobile.tsm.application.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.manager.LoadModuleManager;

@Controller("loadModuleController")
@RequestMapping("/loadModule/")
public class LoadModuleController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	LoadModuleManager loadModuleManager;

	@RequestMapping
	public @ResponseBody
	JsonMessage createNewLoadModule(HttpServletRequest request, @RequestParam Long loadFileVersionId) {
		log.debug("\n" + "LoadModuleController.creatNewLoadModule" + "\n");
		JsonMessage message = new JsonMessage();
		try {
			LoadModule loadModule = new LoadModule();
			SpringMVCUtils.bindObject(request, loadModule);

			String username = SpringSecurityUtils.getCurrentUserName();

			loadModuleManager.createNewLoadModule(loadModule, loadFileVersionId, username);

			Map<String, Object> mappedLoadModule = convertToMap(loadModule);

			message.setMessage(mappedLoadModule);
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

	private Map<String, Object> convertToMap(LoadModule loadModule) {
		Map<String, Object> mappedLoadModule = new HashMap<String, Object>();
		mappedLoadModule.put("loadFileId", loadModule.getLoadFileVersion().getId());
		mappedLoadModule.put("id", loadModule.getId());
		mappedLoadModule.put("name", loadModule.getName());
		mappedLoadModule.put("aid", loadModule.getAid());
		mappedLoadModule.put("comments", loadModule.getComments());
		return mappedLoadModule;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getByCriteria(HttpServletRequest request) {
		log.debug("\n" + "LoadModuleController.getByCriteria" + "\n");
		JsonResult result = new JsonResult();

		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<LoadModule> page = SpringMVCUtils.getPage(request);

			page = loadModuleManager.findPage(page, filters);

			List<Map<String, Object>> mappedResult = new ArrayList<Map<String, Object>>(page.getResult().size());
			for (LoadModule loadModual : page.getResult()) {
				mappedResult.add(convertToMap(loadModual));
			}

			result.setResult(mappedResult);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage removeLoadModule(@RequestParam Long loadModuleId) {
		log.debug("\n" + "LoadModuleController.removeLoadModule" + "\n");
		log.debug("\n" + "loadModuleId: " + loadModuleId + "\n");

		JsonMessage message = new JsonMessage();
		try {
			String username = SpringSecurityUtils.getCurrentUserName();

			loadModuleManager.removeLoadModule(loadModuleId, username);

			message.setMessage(loadModuleId);
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
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<LoadModule> page = SpringMVCUtils.getPage(request);

			page = loadModuleManager.findPage(page, filters);
			result.setPage(page, null, "loadFileVersion.id");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
}
