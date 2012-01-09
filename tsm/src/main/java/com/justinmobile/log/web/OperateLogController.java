package com.justinmobile.log.web;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.log.domain.OperateLog;
import com.justinmobile.log.domain.OperateLogParam;
import com.justinmobile.log.manager.OperateLogManager;
import com.justinmobile.log.manager.OperateLogParamManager;

@Controller("operateLogController")
@RequestMapping("/log/")
public class OperateLogController {

	@Autowired
	private OperateLogManager operateLogManager;
	
	@Autowired
	private OperateLogParamManager operateLogParamManager;

	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<OperateLog> page = SpringMVCUtils.getPage(request);
			if (page.getOrderBy() == null){
				page.setOrderBy("time");
				page.setOrder("desc");
			}
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = operateLogManager.findPage(page, filters);
			List<OperateLog> operateLogs = page.getResult();
			ClassPathResource res = new ClassPathResource("/config/lognames.properties");
			Properties handlerNamesMapping = new Properties();
			handlerNamesMapping.load(res.getInputStream());
			for (OperateLog op:operateLogs){
				String thisHandleName = (String) handlerNamesMapping.get(op.getOperateName());// 获得改操作的中文名称
				if (!StringUtils.isEmpty(thisHandleName)) {
					op.setOperateName(thisHandleName);
				}
			}
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
	public @ResponseBody JsonResult indexParam(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<OperateLogParam> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = operateLogParamManager.findPage(page, filters);
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

}
