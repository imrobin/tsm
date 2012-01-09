package com.justinmobile.tsm.application.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.application.manager.ApplicationVersionTestReportManager;

@Controller()
@RequestMapping("/appverTest/")
public class ApplicationVersionTestReportController {

	@Autowired
	private ApplicationVersionTestReportManager applicationVersionTestReportManager;

	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<ApplicationVersionTestReport> page = SpringMVCUtils.getPage(request);
			page = applicationVersionTestReportManager.findPage(page, filters);
			result.setPage(page, null, "cardBaseInfo.name");
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
	JsonMessage getReportByAppver(HttpServletRequest request, @RequestParam Long appVerId) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationVersionTestReport appVerTestReport = applicationVersionTestReportManager.getReportByAppver(appVerId);
			if (null == appVerTestReport) {
				message.setSuccess(false);
			} else {
				message.setMessage(appVerTestReport.toMap(null, null));
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
	JsonMessage remove(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String appVerTestReportId = request.getParameter("tfId");
			applicationVersionTestReportManager.remove(Long.valueOf(appVerTestReportId));
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
