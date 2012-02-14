/**
 * CardApplicationController
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.endpoint.manager.PushSmsManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

/**
 * @ClassName: CardApplicagionController
 * @Description:应用卡片操作类
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/cardApp/")
public class CardApplicationController {

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private CustomerCardInfoManager customerCardManager;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private CardInfoManager cardManager;
	
	@Autowired
	private PushSmsManager pushSmsManager;
	
	@Autowired
	private ApplicationVersionManager applicationVersionManager;
	

	@RequestMapping()
	public @ResponseBody
	JsonMessage getCardApplicaiton(HttpServletRequest request, @RequestParam Long caId) {
		JsonMessage message = new JsonMessage();
		try {
			CardApplication cardApp = cardApplicationManager.load(caId);
			Map<String, Object> map = cardApp.toMap(null, "cardInfo.cardNo");
			map.put("appId", cardApp.getApplicationVersion().getApplication().getId());
			map.put("appAid", cardApp.getApplicationVersion().getApplication().getAid());
			message.setMessage(map);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping()
	public @ResponseBody
	JsonMessage emigrate(@RequestParam String aid, @RequestParam long customerCardId) {
		JsonMessage message = new JsonMessage();
		try {
			Application application = applicationManager.getByAid(aid);
			CustomerCardInfo customerCard = customerCardManager.load(customerCardId);

			cardApplicationManager.emigrate(application, customerCard.getCard());
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping()
	public @ResponseBody
	JsonMessage emigrateByCardNo(@RequestParam String aid, @RequestParam String cardNo) {
		JsonMessage message = new JsonMessage();
		try {
			Application application = applicationManager.getByAid(aid);

			cardApplicationManager.emigrate(application, cardManager.getByCardNo(cardNo));
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult listByCustomer(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Map<String, Object>> page = SpringMVCUtils.getPage(request);
			int start = (page.getPageNo() - 1) * page.getPageSize();
			int end = start + page.getPageSize();
			String mobileNo = ServletRequestUtils.getStringParameter(request, "mobileNo");
			if (!StringUtils.isBlank(mobileNo)) {
				List<Map<String, Object>> list = cardApplicationManager.findByCustomer(page, mobileNo);
				page.setTotalCount(list.size());
				if (list.size() < start + end) {
					page.setResult(list.subList(start, list.size()));
				} else {
					page.setResult(list.subList(start, end));
				}
				result.setPage(page);
			}

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
	 * @Title: listCardApp
	 * @Description: 显示卡片上指定条件的应用
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult searchAppsForAdminByCustomerCardId(HttpServletRequest request, @RequestParam Long ccid) {
		JsonResult result = new JsonResult();
		try {
			Page<CardApplication> page = SpringMVCUtils.getPage(request);
			List<Map<String, Object>> resultList = cardApplicationManager.getCardApplicationsByCustomerCardId(ccid, page);
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

	/**
	 * @param request
	 * @param caId cardApplication
	 * @param ccId customerCardInfo
	 * @param opt 操作类型
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody
	JsonMessage optCardApplication(HttpServletRequest request, @RequestParam Long caId, @RequestParam Long ccId, @RequestParam int opt) {
		JsonMessage message = new JsonMessage();
		try {
			CustomerCardInfo cci = customerCardManager.load(ccId);
			CardApplication ca = cardApplicationManager.load(caId);
			Operation operation = Operation.valueOf(Operation.valueOf(opt));			
			pushSmsManager.sendPushSms(cci.getCard().getCardNo(), ca.getApplicationVersion().getApplication().getAid(), ca.getApplicationVersion().getVersionNo(), operation);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * @param request
	 * @param appId Application
	 * @param ccId customerCardinfo
	 * @return
	 */
	@RequestMapping()
	public @ResponseBody
	JsonMessage downApplication(HttpServletRequest request, @RequestParam Long appId, @RequestParam Long ccId) {
		JsonMessage message = new JsonMessage();
		try {
			String appVerId = request.getParameter("appVerId");
			String appVerNo = "";
			CustomerCardInfo cci = customerCardManager.load(ccId);
			Application application = applicationManager.load(appId);
			if (StringUtils.isNotBlank(appVerId)) {
				appVerNo = applicationVersionManager.load(Long.valueOf(appVerId)).getVersionNo();
			} else {
				appVerNo = applicationVersionManager.getLastestAppVersionSupportCard(cci.getCard(), application).getVersionNo();
			}
			if(StringUtils.isNotBlank(appVerNo)) {
				pushSmsManager.sendPushSms(cci.getCard().getCardNo(), application.getAid(), appVerNo, Operation.DOWNLOAD_APP);
			} else{
				message.setSuccess(false);
				message.setMessage("此终端不支持此应用");
			}
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

}
