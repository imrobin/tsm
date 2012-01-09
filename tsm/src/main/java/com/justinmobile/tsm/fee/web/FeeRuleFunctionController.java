package com.justinmobile.tsm.fee.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
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
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;
import com.justinmobile.tsm.fee.manager.FeeRuleFunctionManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Controller("FeeRuleFunctionPerControler")
@RequestMapping("/feerulefun/")
public class FeeRuleFunctionController {
	private Logger logger = LoggerFactory
			.getLogger(FeeRuleFunctionController.class);

	@Autowired
	private FeeRuleFunctionManager frfManager;

	@Autowired
	private SpBaseInfoManager spManager;

	/**
	 * @Title: index
	 * @Description: 显示所有的空间计费规则列表
	 * @param request
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		logger.info("");
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			Page<FeeRuleFunction> page = SpringMVCUtils.getPage(request);
			String spName = ServletRequestUtils.getStringParameter(request,
					"spName", "");
			values.put("spName", spName);
			String orderBy = page.getOrderBy();
			if (null != orderBy && orderBy.startsWith("uiPrice")) {
				page.setOrderBy(orderBy.replace("uiPrice", "price"));
			}
			page = frfManager.getFrfpForIndex(page, values);
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
	 * @Title: add
	 * @Description: 增加空间计费规则
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage add(HttpServletRequest request) {
		return save(request, true);
	}

	/**
	 * @Title: edit
	 * @Description: 修改空间计费规则
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage edit(HttpServletRequest request) {
		return save(request, false);
	}

	/**
	 * @Title: save
	 * @Description: 保存手机类型
	 * @param request
	 * @param response
	 */
	private JsonMessage save(HttpServletRequest request, boolean isNew) {
		JsonMessage message = new JsonMessage();
		FeeRuleFunction frf = null;
		BindingResult result = null;
		try {
			if (isNew) {
				frf = new FeeRuleFunction();
				// 数据库设计得简单,程序就会负责些,经验呀
				result = SpringMVCUtils.bindObject(request, frf);
				SpBaseInfo sp = spManager.load(ServletRequestUtils
						.getLongParameter(request, "sp_id"));
				frf.setSp(sp);
				if(frf.getPattern()==FeeRuleFunction.PATTERN_PER){
					frf.setGranularity(null);
				}
				if (null != frfManager.getPerFrf(sp.getId())) {
					throw new PlatformException(
							PlatformErrorCode.FEE_RULE_FUNCTION_EXIST);
				}
				if (null != frfManager.getFrfBySpAndGranularity(sp.getId(),
						frf.getGranularity())) {
					throw new PlatformException(
							PlatformErrorCode.FEE_RULE_FUNCTION_EXIST);
				}
			} else {
				frf = frfManager.load(ServletRequestUtils.getLongParameter(
						request, "id"));
				result = SpringMVCUtils.bindObject(request, frf);
			}
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				frfManager.saveOrUpdate(frf);
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
	 * @Title: getFeeRuleFunction
	 * @Description: 获取功能计费规则
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getFeeRuleFunction(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(frfManager.load(id).toMap(null, "sp.name sp.id"));
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
		String type = ServletRequestUtils.getStringParameter(request, "type",
				"app");
		List<KeyValue> kvList = null;
		try {
			if (type.equalsIgnoreCase("app")) {
				kvList = frfManager.getAppNameBySp(spId);
			} else if (type.equalsIgnoreCase("sd")) {
				kvList = frfManager.getSdNameBySp(spId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bln = false;
		}
		msg.setMessage(kvList);
		msg.setSuccess(bln);
		return msg;
	}

	/**
	 * @Title: remove
	 * @Description: 删除空间计费规则
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage remove(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			frfManager.remove(id);
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
	JsonMessage getFunctionNoByType(HttpServletRequest request) {
		JsonMessage msg = new JsonMessage();
		Map<String, String> map = new HashMap<String, String>();
		List<KeyValue> kvList = new ArrayList<KeyValue>();
		try {
			String type = ServletRequestUtils.getStringParameter(request,
					"type");
			Resource r = new ClassPathResource("config/funcode.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(r);
			if (type.equalsIgnoreCase("app")) {
				map.put("DOWNLOAD_APP", props.getProperty("DOWNLOAD_APP"));
				map.put("DELETE_APP", props.getProperty("DELETE_APP"));
				map.put("PERSONALIZE_APP", props.getProperty("PERSONALIZE_APP"));
				map.put("LOCK_APP", props.getProperty("LOCK_APP"));
				map.put("UNLOCK_APP", props.getProperty("UNLOCK_APP"));
			} else if (type.equalsIgnoreCase("sd")) {
				map.put("DELETE_SD", props.getProperty("DELETE_SD"));
				map.put("CREATE_SD", props.getProperty("CREATE_SD"));
				map.put("UPDATE_KEY", props.getProperty("UPDATE_KEY"));
			}

			for (String key : map.keySet()) {
				kvList.add(new KeyValue(key, map.get(key)));
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(ex.getMessage());
		}
		msg.setMessage(kvList);
		msg.setSuccess(true);
		return msg;
	}
}
