


package com.justinmobile.tsm.application.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.ApplicationService;
import com.justinmobile.tsm.application.domain.ApplicationService.BusinessPlatformInterface;
import com.justinmobile.tsm.application.manager.ApplicationServiceManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;


@Controller("pplicationServiceController")
@RequestMapping("/applicationService/")
public class ApplicationServiceController {
	@Autowired
	private ApplicationServiceManager asManager;
	@Autowired
	private SpBaseInfoManager spManager;
	
	/**
	 * @Title getSp
	 * @Description显示所有的SP列表
	 * @param request
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getSpName(HttpServletRequest request) {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		List<KeyValue> spAll = new ArrayList<KeyValue>();
		try {
			spAll = asManager.getSpName();
		} catch (Exception ex) {
			ex.printStackTrace();
			bln = false;
		}
		msg.setMessage(spAll);
		msg.setSuccess(bln);
		return msg;
	}
	/**
	 * @Title getNameBySp
	 * @Description 获取该SP下所有的SD或者APP名称
	 * @param request
	 * 
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getNameBySp(HttpServletRequest request) {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		Long spId = ServletRequestUtils.getLongParameter(request, "spId", 0);
		Integer type = ServletRequestUtils.getIntParameter(request, "type",
				1);
		List<KeyValue> kvList = null;
		try {
			if (type==ApplicationService.TYPE_APP) {
				kvList = asManager.getAppNameBySp(spId);
			} else if (type==ApplicationService.TYPE_SD) {
				kvList = asManager.getSdNameBySp(spId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bln = false;
		}
		msg.setMessage(kvList);
		msg.setSuccess(bln);
		return msg;
		
	}
	@RequestMapping
	public @ResponseBody
	JsonMessage getFunctionNoByType(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		List<KeyValue> kvList = new ArrayList<KeyValue>();
		for (BusinessPlatformInterface operation : ApplicationService.BusinessPlatformInterface.values()) {
				kvList.add(new KeyValue(operation.name(),operation.getValue()));
		}
		kvList.remove(kvList.size()-1);
		message.setMessage(kvList);
		return message;
	}
	/**
	 * @Title: save
	 * @Description: 增加业务接口
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ApplicationService as = null;
		BindingResult result = null;
		try {
				as = new ApplicationService();
				// 数据库设计得简单,程序就会负责些,经验呀
				result = SpringMVCUtils.bindObject(request, as);
				SpBaseInfo sp = spManager.load(ServletRequestUtils
						.getLongParameter(request, "sp_id"));
				as.setSp(sp);
				String aidName = ServletRequestUtils.getStringParameter(request, "aidName");
				String[] s = aidName.split("-");
				as.setAid(s[0]);
				as.setAppName(aidName.substring((s[0]+"-").length(),aidName.length()));
				if (null != asManager.getByAidAndServiceName(as.getAid(), as.getServiceName())) {
					throw new PlatformException(
							PlatformErrorCode.APPLICATION_SERVICE_ALREAY_EXIST);
				}
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				asManager.saveOrUpdate(as);
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
	/**
	 * @Title: index
	 * @Description: 显示所有的业务接口
	 * @param request
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			Page<ApplicationService> page = SpringMVCUtils.getPage(request);
			String spName = ServletRequestUtils.getStringParameter(request,
					"spName", "");
			String appName = ServletRequestUtils.getStringParameter(request, "appName","");
			String type = ServletRequestUtils.getStringParameter(request, "type");
			values.put("spName", spName);
			values.put("appName", appName);
			if(!StringUtils.isEmpty(type)){
				values.put("type", new Integer(type));
			}else{
				values.put("type", type);
			}
			page = asManager.getAppSerForIndex(page, values);
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
	/**
	 * @Title: remove
	 * @Description: 删除业务接口
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage remove(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			asManager.remove(id);
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



