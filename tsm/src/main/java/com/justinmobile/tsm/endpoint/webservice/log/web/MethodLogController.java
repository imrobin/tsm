package com.justinmobile.tsm.endpoint.webservice.log.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.log.domain.MethodLog;
import com.justinmobile.tsm.endpoint.webservice.log.manager.MethodLogManager;

@Controller
@RequestMapping("/mLog/")
public class MethodLogController {
	
	@Autowired
	private MethodLogManager methodLogManager;
	
	@RequestMapping
	public @ResponseBody JsonResult indexApdus(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<MethodLog> page = SpringMVCUtils.getPage(request);
			Map<String, String> filters = ServletUtils.getParametersStartingWith(request, "search_");
			filters.put("EQS_targetName", "MobileWebService");
			filters.put("EQS_methodName", "execAPDU");
			page = methodLogManager.findPage(page, filters);
			result.setPage(page, "params result", "application.name customerCardInfo.mobileNo");
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
	public @ResponseBody JsonMessage getParams(@RequestParam Long logId) {
		JsonMessage message = new JsonMessage();
		try {
			MethodLog log = methodLogManager.load(logId);
			XmlType annotation = ReqExecAPDU.class.getAnnotation(XmlType.class);
			message.setMessage(log.convertParams(annotation.name(), ReqExecAPDU.class));
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
	public @ResponseBody JsonMessage getResult(@RequestParam Long logId) {
		JsonMessage message = new JsonMessage();
		try {
			MethodLog log = methodLogManager.load(logId);
			XmlType annotation = ResExecAPDU.class.getAnnotation(XmlType.class);
			message.setMessage(log.convertResult(annotation.name(), ResExecAPDU.class));
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
	public @ResponseBody JsonMessage remove() {
		JsonMessage message = new JsonMessage();
		try {
			methodLogManager.removeAll();
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
