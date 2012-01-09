package com.justinmobile.tsm.application.web;

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
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationStyle;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationStyleManager;

@Controller("applicationStyleController")
@RequestMapping("/applicationStyle/")
public class ApplicationStyleController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationStyleController.class);
	
	@Autowired
	private ApplicationStyleManager applicationStyleManager;
	
	@Autowired
	private ApplicationManager applicationManager;
	
	@RequestMapping
	public @ResponseBody JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		
		try {
			
			String excludeField = null;
			String includeCascadeField = "application.name";
			Page<ApplicationStyle> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = this.applicationStyleManager.findPage(page, filters);
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
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		
		try {
			
			String styleUrl = ServletRequestUtils.getStringParameter(request, "styleUrl");
			Long applicationId = ServletRequestUtils.getLongParameter(request, "applicationId");
			
			Application application = this.applicationManager.load(applicationId);
			ApplicationStyle style = new ApplicationStyle();
			style.setApplication(application);
			style.setStyleUrl(styleUrl);
			
			this.applicationStyleManager.saveOrUpdate(style);
			
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
			Long id = ServletRequestUtils.getLongParameter(request, "id");
			ApplicationStyle origin = this.applicationStyleManager.load(id);
			
			String styleUrl = ServletRequestUtils.getStringParameter(request, "styleUrl");
			Long applicationId = ServletRequestUtils.getLongParameter(request, "applicationId");
			
			Application application = this.applicationManager.load(applicationId);
			origin.setApplication(application);
			origin.setStyleUrl(styleUrl);
			
			this.applicationStyleManager.saveOrUpdate(origin);
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
			String ignoreFormatField = null;
			String excludeField = null;
			String includeCascadeField = "application.id";
			long id = ServletRequestUtils.getLongParameter(request, "id");
			ApplicationStyle style = this.applicationStyleManager.load(id);
			result.setMessage(style.toMap(ignoreFormatField, excludeField, includeCascadeField));
			
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
			this.applicationStyleManager.remove(id);
			logger.debug("\nremove ApplicationStyle object for id : " + id);
			
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
