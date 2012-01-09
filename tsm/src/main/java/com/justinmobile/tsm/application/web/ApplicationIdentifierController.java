package com.justinmobile.tsm.application.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.ApplicationIdentifier;
import com.justinmobile.tsm.application.manager.ApplicationIdentifierManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Controller("aidController")
@RequestMapping("/aid/")
public class ApplicationIdentifierController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationIdentifierController.class);
	
	@Autowired
	private ApplicationIdentifierManager aidManager;
	
	@RequestMapping
	public @ResponseBody JsonMessage generate(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		
		try {
			ApplicationIdentifier aid = new ApplicationIdentifier();
			int type = ServletRequestUtils.getIntParameter(request, "type");
			aid.setType(type);
			switch (type) {
			case ApplicationIdentifier.TYPE_SD  :
				aid.setBelongto(ServletRequestUtils.getIntParameter(request, "belongto"));
				break;
			case ApplicationIdentifier.TYPE_APP : 
				aid.setAppType(ServletRequestUtils.getIntParameter(request, "appType"));
				aid.setIndustry(ServletRequestUtils.getIntParameter(request, "industry"));
				break;
			default:
				break;
			}
			aid.setSize(ServletRequestUtils.getIntParameter(request, "size"));
			long spId = ServletRequestUtils.getLongParameter(request, "sp_id");
			SpBaseInfo sp = new SpBaseInfo();
			sp.setId(spId);
			aid.setSp(sp);
			logger.debug(aid.toString());
			aidManager.saveAid(aid);
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
	public @ResponseBody JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		
		try {
			
			Page<ApplicationIdentifier> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			
			page = aidManager.findPage(page, filters);
			result.setPage(page, null, "sp.name");
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
	public @ResponseBody JsonMessage zuofei(@RequestParam long id) {
		JsonMessage message = new JsonMessage();
		
		try {
			ApplicationIdentifier aid = aidManager.load(id);
			aid.setStatus(ApplicationIdentifier.STATUS_VOID);
			aidManager.saveOrUpdate(aid);
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
