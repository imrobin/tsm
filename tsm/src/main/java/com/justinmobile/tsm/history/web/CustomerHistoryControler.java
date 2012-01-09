package com.justinmobile.tsm.history.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.history.manager.CustomerHistoryManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Controller("customerHistoryControler")
@RequestMapping("/customerHistory/")
public class CustomerHistoryControler {
	
	@Autowired
	private CustomerHistoryManager customerHistoryManager;
	
	@Autowired
	private CustomerManager customerManager;
	
	@RequestMapping
	public @ResponseBody JsonResult listCustomerCreateSDHistory(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<LocalTransaction> page = SpringMVCUtils.getPage(request);
			String userName = SpringSecurityUtils.getCurrentUserName();
			Customer customer = customerManager.getCustomerByUserName(userName);
			Map<String,Object> paramMap = new HashMap<String,Object>();
			String phoneName = request.getParameter("phoneName");
			String sdname = request.getParameter("sdname");
			paramMap.put("customer", customer);
			paramMap.put("phoneName", phoneName);
			paramMap.put("sdname", sdname);
			List<Map<String,Object>> resultList = customerHistoryManager.getCustomerCreateSDHistory(page,paramMap);
			result.setResult(resultList);
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
