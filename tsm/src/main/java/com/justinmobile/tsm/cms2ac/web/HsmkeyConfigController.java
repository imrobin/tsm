package com.justinmobile.tsm.cms2ac.web;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;

@Controller("hsmkeyConfigController")
@RequestMapping("/encryptor/")
public class HsmkeyConfigController {

	private static final Logger logger = LoggerFactory.getLogger(HsmkeyConfigController.class);
	
	@Autowired
	private HsmkeyConfigManager hsmkeyConfigManager;
	
	@RequestMapping
	public @ResponseBody JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		
		try {
			
			String excludeField = null;
			String includeCascadeField = null;
			Page<HsmkeyConfig> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = this.hsmkeyConfigManager.findPage(page, filters);
			result.setPage(page, excludeField, includeCascadeField);
			
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			logger.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonResult listAll(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		
		try {
			
			String excludeField = null;
			String includeCascadeField = null;
			
			result.setResult(this.hsmkeyConfigManager.getAll("id", true), excludeField, includeCascadeField);
			
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			logger.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		
		try {
			HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
			SpringMVCUtils.bindObject(request, hsmkeyConfig);
			hsmkeyConfig.setEffectiveDate(Calendar.getInstance());
			this.hsmkeyConfigManager.saveOrUpdate(hsmkeyConfig);
			logger.debug("add HsmkeyConfig : " + hsmkeyConfig.toString());
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			logger.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage edit(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		
		try {
			
			HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
			SpringMVCUtils.bindObject(request, hsmkeyConfig);
			this.hsmkeyConfigManager.saveOrUpdate(hsmkeyConfig);
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			logger.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage get(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		
		try {
			String ignoreFormatField = "model vendor";
			String excludeField = null;
			String includeCascadeField = null;
			long id = ServletRequestUtils.getLongParameter(request, "id");
			HsmkeyConfig hsmkeyConfig = this.hsmkeyConfigManager.load(id);
			result.setMessage(hsmkeyConfig.toMap(ignoreFormatField, excludeField, includeCascadeField));
			
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			logger.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage delete(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		
		try {
			
			long id = ServletRequestUtils.getLongParameter(request, "id");
			this.hsmkeyConfigManager.remove(id);
			logger.debug("\nremove hsmkeyConfig object for id : " + id);
			
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			logger.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return result;
	}
}
