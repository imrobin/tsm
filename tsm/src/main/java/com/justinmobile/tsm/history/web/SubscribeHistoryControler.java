package com.justinmobile.tsm.history.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.details.UserWithSalt;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.history.domain.SubscribeHistory;
import com.justinmobile.tsm.history.manager.SubscribeHistoryManager;

@Controller("subscribeHistoryControler")
@RequestMapping("/subscribehistory/")
public class SubscribeHistoryControler {
	
	@Autowired
	private SubscribeHistoryManager subscribeHistoryManager;
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private CustomerManager customerManager;
	
	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		JsonResult resultMap = new JsonResult();
		try {
			Page<Application> page = SpringMVCUtils.getPage(request);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			resultMap.setPage(pageMap);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 管理后台：订购关系查询<br/>
	 * SP自查询：订购关系查询
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			
			Page<SubscribeHistory> page = SpringMVCUtils.getPage(request);
			
			//查询参数
			//管理后台用户名称
			String nickName = ServletRequestUtils.getStringParameter(request, "customerCardInfo_customer_nickName");
			//前台门户用户名称（需要转码）
			String nickNameOfSp = ServletRequestUtils.getStringParameter(request, "nickName");
			String mobileNo = ServletRequestUtils.getStringParameter(request, "customerCardInfo_mobileNo");
			String appName  = ServletRequestUtils.getStringParameter(request, "applicationVersion_application_name");
			String subscribeDate = ServletRequestUtils.getStringParameter(request, "subscribeDate");
			String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
			String isSp = ServletRequestUtils.getStringParameter(request, "sp");
			
			Map<String, Object> queryParams = new HashMap<String, Object>();
			if(!StringUtils.isBlank(nickName)) {
				queryParams.put("customerCardInfo_customer_nickName", nickName);
			}
			if(!StringUtils.isBlank(nickNameOfSp)) {
				nickNameOfSp = new String(nickNameOfSp.getBytes("iso-8859-1"), "utf-8");
				queryParams.put("customerCardInfo_customer_nickName", nickNameOfSp);
			}
			if(!StringUtils.isBlank(mobileNo)) {
				queryParams.put("customerCardInfo_mobileNo", mobileNo);
			}
			if(!StringUtils.isBlank(appName)) {
				queryParams.put("applicationVersion_application_name", appName);
			}
			if(!StringUtils.isBlank(subscribeDate)) {
				queryParams.put("subscribeDate", subscribeDate);
			}
			if(!StringUtils.isBlank(province)) {
				queryParams.put("province", province);
			}
			String userName = SpringSecurityUtils.getCurrentUserName();
			if(!StringUtils.isBlank(isSp) && !StringUtils.isBlank(userName)) {
				SysUser user = sysUserManager.getUserByName(userName);
				queryParams.put("applicationVersion_application_sp", user.getId());
			}
			
			page = subscribeHistoryManager.findPage(page, queryParams);
			
			result.setPage(page, null, "applicationVersion.versionNo applicationVersion.application.name customerCardInfo.customer.nickName customerCardInfo.mobileNo customerCardInfo.card.cardNo");
			
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
	 * 管理后台：订购关系查询<br/>
	 * SP自查询：订购关系查询
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody JsonResult listHistoryForCustomer(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SubscribeHistory> page = SpringMVCUtils.getPage(request);
			String userName = SpringSecurityUtils.getCurrentUserName();
			Map<String,Object> paramMap = new HashMap<String,Object>();
			String phoneName = request.getParameter("phoneName");
			String appName = request.getParameter("appname");
			Customer customer = customerManager.getCustomerByUserName(userName);
			paramMap.put("customer", customer);
			paramMap.put("phoneName", phoneName);
			paramMap.put("appName", appName);
			page = subscribeHistoryManager.listHistoryForCustomer(page,paramMap);
			result.setPage(page, null, "applicationVersion.versionNo applicationVersion.application.name customerCardInfo.name");
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
