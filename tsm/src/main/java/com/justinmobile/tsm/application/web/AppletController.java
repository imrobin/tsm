package com.justinmobile.tsm.application.web;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.AppletInstallParams;
import com.justinmobile.tsm.application.manager.AppletManager;

@Controller("appletController")
@RequestMapping("/applet/")
public class AppletController {

	private static final Logger log = LoggerFactory.getLogger(AppletController.class);

	@Autowired
	AppletManager appletManager;

	@RequestMapping
	public @ResponseBody
	JsonMessage createNewApplet(HttpServletRequest request, @RequestParam Long applicationVersionId, @RequestParam Long loadModuleId) {
		log.debug("\n" + "AppletController.createNewApplet" + "\n");
		if (log.isDebugEnabled()) {
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}

		}
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();

			Applet applet = new Applet();
			BindingResult result = SpringMVCUtils.bindObject(request, applet);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				int intPrivilege = ConvertUtils.hexString2Int(request.getParameter("hexPrivilege"));
				applet.setPrivilege(intPrivilege);
				appletManager.createNewApplet(applet, applicationVersionId, loadModuleId, username);

				Map<String, Object> mappedApplet = new HashMap<String, Object>(7);
				mappedApplet.put("id", applet.getId());
				mappedApplet.put("name", applet.getName());
				mappedApplet.put("aid", applet.getAid());
				mappedApplet.put("installParams", applet.getInstallParams());
				mappedApplet.put("privilege", ConvertUtils.int2HexString(applet.getPrivilege()));
				mappedApplet.put("volatileSpace", applet.getVolatileSpace());
				mappedApplet.put("noneVolatileSpace", applet.getNoneVolatileSpace());
				mappedApplet.put("loadModuleId", applet.getLoadModule().getId());

				message.setMessage(mappedApplet);
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
	JsonResult getByCriteria(HttpServletRequest request) {
		log.debug("\n" + "AppletController.getByCriteria" + "\n");
		JsonResult result = new JsonResult();

		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<Applet> page = SpringMVCUtils.getPage(request);

			page = appletManager.findPage(page, filters);

			List<Map<String, Object>> mappedResult = new ArrayList<Map<String, Object>>(page.getResult().size());
			for (Applet applet : page.getResult()) {
				log.debug("\n" + "appletId: " + applet.getId() + "\n");
				Map<String, Object> mappedApplet = new HashMap<String, Object>(7);
				mappedApplet.put("id", applet.getId());
				mappedApplet.put("name", applet.getName());
				mappedApplet.put("aid", applet.getAid());
				mappedApplet.put("installParams", applet.getInstallParams());
				mappedApplet.put("privilege", ConvertUtils.int2HexString(applet.getPrivilege()));
				mappedApplet.put("volatileSpace", applet.getVolatileSpace());
				mappedApplet.put("noneVolatileSpace", applet.getNoneVolatileSpace());
				mappedApplet.put("loadModuleId", applet.getLoadModule().getId());
				mappedResult.add(mappedApplet);
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
	JsonResult getInstallOrder(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "AppletController.getInstallOrder" + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");
		JsonResult result = new JsonResult();
		try {
			List<Applet> applets = appletManager.getInstallOrder(applicationVersionId);

			List<Map<String, Object>> mappedResult = new ArrayList<Map<String, Object>>(applets.size());
			for (int i = 0; i < applets.size(); i++) {
				Applet applet = applets.get(i);

				log.debug("\n" + "appletId: " + applet.getId() + "\n");

				Map<String, Object> mappedEntity = new HashMap<String, Object>(3);
				mappedEntity.put("id", applet.getId());
				mappedEntity.put("name", applet.getName());
				mappedEntity.put("aid", applet.getAid());

				mappedResult.add(mappedEntity);
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
	JsonMessage setInstallOrder(@RequestParam Long appletId, @RequestParam Integer order) {
		log.debug("\n" + "appletId: " + appletId + "\n");
		log.debug("\n" + "order: " + order + "\n");

		JsonMessage message = new JsonMessage();
		try {
			String username = SpringSecurityUtils.getCurrentUserName();

			appletManager.setInstallOrder(appletId, order, username);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage buildInstallParams(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("\n" + "LoadFileController.buildInstallParams" + "\n");
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}
		}

		JsonMessage message = new JsonMessage();
		try {
			AppletInstallParams params = new AppletInstallParams();

			BindingResult result = SpringMVCUtils.bindObject(request, params);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String hexParams = params.build();

				log.debug("\n" + "hexParams: " + hexParams + "\n");

				Map<String, Object> messages = new HashMap<String, Object>(2);
				messages.put("hexInstallParams", hexParams);
				messages.put("jsonInstallParams", params);

				message.setMessage(messages);
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
	JsonMessage parseInstallParams(@RequestParam String hexInstallParams) {
		log.debug("\n" + "AppletController.parseInstallParams" + "\n");
		log.debug("\n" + "hexInstallParams: " + hexInstallParams + "\n");
		JsonMessage message = new JsonMessage();

		try {
			AppletInstallParams installParams = AppletInstallParams.parse(hexInstallParams);
			message.setMessage(installParams);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage removeApplet(@RequestParam Long appletId) {
		log.debug("\n" + "AppletController.parseInstallParams" + "\n");
		log.debug("\n" + "appletId: " + appletId + "\n");
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			appletManager.removeApplet(appletId, username);
			message.setMessage(appletId);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

}
