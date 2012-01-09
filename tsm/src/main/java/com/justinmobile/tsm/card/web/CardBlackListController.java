/**
 * CardBlackListController.java
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import com.justinmobile.tsm.card.domain.CardBlackList;
import com.justinmobile.tsm.card.manager.CardBlackListManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;


/**
 * @ClassName: CardBlackListController
 * @Description:用户黑名单Controller类
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/cardBlackList/")
public class CardBlackListController {
		
	@Autowired
	private CardBlackListManager cardBlackListManager;
	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;
	
	/**
	* @Title: list
	* @Description: 显示所有的黑名单
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonResult list(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<CardBlackList> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = cardBlackListManager.findPage(page, filters);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			result.setPage(pageMap);
		} catch (PlatformException pe){
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
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
	public @ResponseBody JsonResult listCustomerCard(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = customerCardInfoManager.findPage(page, filters);
			result.setPage(page,null,"customer.nickName mobileType.brandChs mobileType.type card.cardNo");
		} catch (PlatformException pe){
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
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
	public @ResponseBody JsonMessage addBlackList(HttpServletRequest request){
		JsonMessage message = new JsonMessage();
		String num = request.getParameter("num");
		int numI = Integer.parseInt(num);
		String errorMessage = "出现错误,下列终端加入黑名单失败:";
		String reason = request.getParameter("reason");
		CustomerCardInfo cci  = null;
		for (int i = 0; i < numI; i++) {
			try {
				CardBlackList blackList = new CardBlackList();
				Long cciId = Long.parseLong(request.getParameter("ids[" + i + "]"));
				cci = customerCardInfoManager.load(cciId);
				blackList.setCustomerCardInfo(cci);
				blackList.setOperateDate(Calendar.getInstance());
				blackList.setReason(reason);
				blackList.setType(CardBlackList.TYPE_ADD);
				cardBlackListManager.addBlackList(blackList);
			} catch (PlatformException e){
				e.printStackTrace();
				message.setSuccess(Boolean.FALSE);
				errorMessage += cci.getMobileNo() + "    ";
			} catch (Exception e){
				e.printStackTrace();
				message.setSuccess(Boolean.FALSE);
				errorMessage += cci.getMobileNo() + "    ";
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
	public @ResponseBody JsonMessage removeBlackList(HttpServletRequest request){
		JsonMessage message = new JsonMessage();
		String num = request.getParameter("num");
		int numI = Integer.parseInt(num);
		String errorMessage = "出现错误,下列终端移除黑名单失败:";
		String reason = request.getParameter("reason");
		CustomerCardInfo cci  = null;
		for (int i = 0; i < numI; i++) {
			try {
				CardBlackList blackList = new CardBlackList();
				Long cciId = Long.parseLong(request.getParameter("ids[" + i + "]"));
				cci = customerCardInfoManager.load(cciId);
				blackList.setCustomerCardInfo(cci);
				blackList.setType(CardBlackList.TYPE_REMOVE);
				blackList.setOperateDate(Calendar.getInstance());
				blackList.setReason(reason);
				cardBlackListManager.removeBlackList(blackList);
			} catch (PlatformException e){
				e.printStackTrace();
				message.setSuccess(Boolean.FALSE);
				errorMessage += cci.getMobileNo() + "    ";
			} catch (Exception e){
				e.printStackTrace();
				message.setSuccess(Boolean.FALSE);
				errorMessage += cci.getMobileNo() + "    ";
			}
		}
		if (!message.getSuccess()) {
			message.setMessage(errorMessage);
		}
		return message;
	}
}

