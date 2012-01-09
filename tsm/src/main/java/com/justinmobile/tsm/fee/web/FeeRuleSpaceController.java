package com.justinmobile.tsm.fee.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;
import com.justinmobile.tsm.fee.manager.FeeRuleSpaceManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Controller("FeeRuleSpaceControler")
@RequestMapping("/feerulespace/")
public class FeeRuleSpaceController {
	private Logger logger = LoggerFactory
			.getLogger(FeeRuleSpaceController.class);

	@Autowired
	private FeeRuleSpaceManager frpManager;
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
		logger.info("");
		JsonResult result = new JsonResult();
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			Page<FeeRuleSpace> page = SpringMVCUtils.getPage(request);
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
			String orderBy = page.getOrderBy();
			if (null != orderBy && orderBy.startsWith("uiPrice")) {
				page.setOrderBy(orderBy.replace("uiPrice", "price"));
			}

			page = frpManager.getFrpForIndex(page, values);
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
			spAll = frpManager.getSpName();
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
		List<KeyValue> kvList = null;
		try {
		Long spId = ServletRequestUtils.getLongParameter(request, "spId", 0);
		Integer type = ServletRequestUtils.getIntParameter(request, "type");
			if (type == FeeRuleSpace.TYPE_APP) {
				kvList = frpManager.getAppNameBySp(spId);
			} else if (type == FeeRuleSpace.TYPE_SD ) {
				kvList = frpManager.getSdNameBySp(spId);
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
		FeeRuleSpace frp = null;
		BindingResult result = null;
		try {
			if (isNew) {
				frp = new FeeRuleSpace();
				result = SpringMVCUtils.bindObject(request, frp);
				SpBaseInfo sp = spManager.load(ServletRequestUtils
						.getLongParameter(request, "sp_id"));
				frp.setSp(sp);
				//为了前台页面验证通过，必须设置粒度值，这里清空粒度值
				if(frp.getPattern()==FeeRuleSpace.PATTERN_APP){
					frp.setGranularity(null);
				}
				String aidName = ServletRequestUtils.getStringParameter(request, "aidName");
				String[] s = aidName.split("-");
				frp.setAid(s[0]);
				frp.setAppName(aidName.substring((s[0]+"-").length(),aidName.length()));
				if (null != frpManager.getFrpByAid(frp.getAid())) {
					throw new PlatformException(
							PlatformErrorCode.FEE_RULE_SPACE_EXIST);
				}
			} else {
				frp = frpManager.load(ServletRequestUtils.getLongParameter(
						request, "id"));
				result = SpringMVCUtils.bindObject(request, frp);
				//为了前台页面验证通过，必须设置粒度值，这里清空粒度值
				if(frp.getPattern()==FeeRuleSpace.PATTERN_APP){
					frp.setGranularity(null);
				}
			}
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				frpManager.saveOrUpdate(frp);
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
	 * @Description: 获取空间计费规则
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getFeeRuleSpace(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(frpManager.load(id).toMap(null, "sp.name sp.id"));
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
			frpManager.remove(id);
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
