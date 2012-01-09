/**
 * CardBlackListController.java
 * 
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.sp.web;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.domain.SpBlackList;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.sp.manager.SpBlackListManager;

/**
 * @ClassName: CardBlackListController
 * @Description:SP黑民党Controller类
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/spBlackList/")
public class SpBlackListController {

	@Autowired
	private SpBlackListManager	spBlackListManager;
	@Autowired
	private SpBaseInfoManager	spBaseInfoManager;

	/**
	 * @Title: list
	 * @Description: 显示所有的黑名单
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody
	JsonResult listBlack(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBlackList> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = spBlackListManager.findPage(page, filters);
			result.setPage(page, null, null);
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * @Title: list
	 * @Description: 显示所有的黑名单
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody
	JsonResult listSp(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBaseInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = spBaseInfoManager.findPage(page, filters);
			result.setPage(page, "firmLogo", "sysUser.email");
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * @Title: add
	 * @Description: 添加一个用户到黑名单(暂时为表单填写所需字段)
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody
	JsonMessage addBlackList(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		String num = request.getParameter("num");
		int numI = Integer.parseInt(num);
		String errorMessage = "出现错误,下列商户加入黑名单失败:";
		String reason = request.getParameter("reason");
		SpBaseInfo sp = null;
		for (int i = 0; i < numI; i++) {
			try {
				SpBlackList blackList = new SpBlackList();
				Long spId = Long.parseLong(request.getParameter("ids[" + i + "]"));
				sp = spBaseInfoManager.load(spId);
				blackList.setSp(sp);
				blackList.setOperateDate(Calendar.getInstance());
				blackList.setReason(reason);
				blackList.setType(SpBlackList.TYPE_ADD);
				spBlackListManager.addBlackList(blackList);
			} catch (PlatformException e) {
				message.setSuccess(Boolean.FALSE);
				errorMessage += sp.getName() + "    ";
			} catch (Exception e) {
				message.setSuccess(Boolean.FALSE);
				errorMessage += sp.getName() + "    ";
			}
		}
		if (!message.getSuccess()) {
			message.setMessage(errorMessage);
		}
		return message;
	}

	/**
	 * @Title: remove
	 * @Description: 移除一个用户到黑名单(暂时为表单填写所需字段)
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody
	JsonMessage removeBlackList(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		String num = request.getParameter("num");
		int numI = Integer.parseInt(num);
		String errorMessage = "出现错误,下列商户移出黑名单失败:";
		String reason = request.getParameter("reason");
		SpBaseInfo sp = null;
		for (int i = 0; i < numI; i++) {
			try {
				SpBlackList blackList = new SpBlackList();
				Long spId = Long.parseLong(request.getParameter("ids[" + i + "]"));
				sp = spBaseInfoManager.load(spId);
				blackList.setOperateDate(Calendar.getInstance());
				blackList.setReason(reason);
				blackList.setSp(sp);
				blackList.setType(SpBlackList.TYPE_REMOVE);
				spBlackListManager.removeBlackList(blackList);
			} catch (PlatformException e) {
				message.setSuccess(Boolean.FALSE);
				errorMessage += sp.getName() + "    ";
			} catch (Exception e) {
				message.setSuccess(Boolean.FALSE);
				errorMessage += sp.getName() + "    ";
			}
		}
		if (!message.getSuccess()) {
			message.setMessage(errorMessage);
		}
		return message;
	}
}
